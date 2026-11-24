/*
 * Azure Ad Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.cyber_security.iam.azureAD.impl.connectors;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extensions.cyber_security.iam.azureAD.AzureADAttributes;
import app.krista.extensions.cyber_security.iam.azureAD.impl.stores.RefreshTokenStore;
import app.krista.extensions.cyber_security.iam.azureAD.impl.util.Constants;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import app.krista.model.field.NamedValuedField;
import com.google.gson.JsonObject;
import com.microsoft.aad.msal4j.*;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserRequestBuilder;
import okhttp3.Request;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GraphServiceClientProvider {
    public static final String REQUIRED_SCOPE = Constants.REQUIRED_SCOPE;
    public static final String SCOPE_SEPARATOR = " ";
    public static final String AUTHORITY = "https://login.microsoftonline.com/";
    private static final Logger LOG = LoggerFactory.getLogger(GraphServiceClientProvider.class);
    private final RefreshTokenStore refreshTokenStore;
    private final AzureADAttributes azureADAttributes;
    private final RequestContext requestContext;
    private final AuthorizationContext authorizationContext;
    private final String authContextId;


    @Inject
    public GraphServiceClientProvider(RefreshTokenStore refreshTokenStore, AzureADAttributes azureADAttributes, RequestContext requestContext, AuthorizationContext authorizationContext) {
        this(refreshTokenStore, azureADAttributes, requestContext, authorizationContext, null);
    }

    public GraphServiceClientProvider(RefreshTokenStore refreshTokenStore, AzureADAttributes azureADAttributes, RequestContext requestContext, AuthorizationContext authorizationContext, String authContextId) {
        this.refreshTokenStore = refreshTokenStore;
        this.azureADAttributes = azureADAttributes;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.authContextId = authContextId;
    }

    public AzureADAttributes getAzureADAttributes() {
        return azureADAttributes;
    }

    public GraphServiceClient<Request> getGraphServiceClient() {
        return getGraphServiceClient(!requestContext.invokeAsUser());
    }

    public GraphServiceClient<Request> getGraphServiceClientForAdmin() {
        return getGraphServiceClient(true);
    }

    private GraphServiceClient getGraphServiceClient(boolean useEmail) {
        try {
            String userId = getUserId(useEmail);
            String refreshToken = refreshTokenStore.get(userId);
            if (refreshToken == null) {
                throw createMustAuthorizationException(userId, false);
            }
            return getGraphServiceClient(userId, refreshToken);
        } catch (RuntimeException cause) {
            System.err.println("Failed to get graph client " + cause.getClass().getName());
            cause.printStackTrace();
            throw cause;
        }
    }

    private GraphServiceClient getGraphServiceClient(String userId, String refreshToken) {
        try {
            String[] scopes = REQUIRED_SCOPE.split(SCOPE_SEPARATOR);
            Set<String> scopeSet = Arrays.stream(scopes).collect(Collectors.toSet());
            RefreshTokenParameters refreshTokenParameters = RefreshTokenParameters.builder(scopeSet, refreshToken).build();
            IClientCredential clientCredential = ClientCredentialFactory.createFromSecret(azureADAttributes.getClientSecret());
            String authority = AUTHORITY + azureADAttributes.getTenantId();
            ConfidentialClientApplication confidentialClientApplication = ConfidentialClientApplication.builder
                    (azureADAttributes.getClientId(), clientCredential).authority(authority).build();
            IAuthenticationResult authenticationResult = confidentialClientApplication.acquireToken(refreshTokenParameters).get();
            final String cachedTokenContent = confidentialClientApplication.tokenCache().serialize();
            updateRefreshToken(userId, Constants.GSON.fromJson(cachedTokenContent, JsonObject.class));
            return GraphServiceClient.builder()
                    .authenticationProvider(new GraphServiceClientAuthenticationProvider(authenticationResult.accessToken()))
                    .buildClient();
        } catch (ClientException | ExecutionException | MalformedURLException cause) {
            throw createMustAuthorizationException(userId, true);
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw createMustAuthorizationException(userId, true);
        }
    }

    private void updateRefreshToken(String userId, JsonObject jsonTokenCache) {
        JsonObject refreshToken = jsonTokenCache.get("RefreshToken").getAsJsonObject();
        String firstKey = refreshToken.keySet().iterator().next();
        JsonObject tokenData = refreshToken.getAsJsonObject(firstKey);
        String secret = tokenData.get("secret").getAsString();
        refreshTokenStore.put(userId, secret);
        LOG.info("Refresh token updated");
    }

    public UserRequestBuilder getUserRequestBuilder() {
        return requestContext.invokeAsUser()
                ? getGraphServiceClient().me()
                : getGraphServiceClient().users(azureADAttributes.getEmail());
    }

    private MustAuthorizeException createMustAuthorizationException(String userId, boolean reAuthentication) {
        System.err.println(reAuthentication ? "Got error for Authentication. So sending for re authentication" : "Got error for Authentication. So sending for authentication");
        List<NamedValuedField> details = new ArrayList<>();
        NamedValuedField userIdField = new NamedValuedField("userId", "Text", userId, new HashMap<>(), new HashMap<>());
        details.add(userIdField);
        if (authContextId != null) {
            NamedValuedField contextIdField = new NamedValuedField("authContextId", "Text", authContextId, new HashMap<>(), new HashMap<>());
            details.add(contextIdField);
        }
        return new MustAuthorizeException(reAuthentication ? "Your authentication session has expired. Please click 'Authorize' to re-authenticate with Azure AD and continue." : "Authentication required. Please click 'Authorize' to connect to Azure AD, then click 'Validate Attributes' before saving your configuration.", details);
    }

    private String getUserId(boolean calledFromValidateAttributes) {
        String userId = calledFromValidateAttributes || !requestContext.invokeAsUser()
                ? azureADAttributes.getEmail()
                : authorizationContext.getAuthorizedAccount().getAccountId();
        if (userId == null) {
            throw new IllegalStateException("Unable to determine user account for authentication. Please ensure the extension is properly configured with a valid administrator email address.");
        }
        return userId;
    }

    public static class GraphServiceClientAuthenticationProvider implements IAuthenticationProvider {

        private final String accessToken;

        public GraphServiceClientAuthenticationProvider(String accessToken) {
            if (accessToken == null || accessToken.isEmpty() || accessToken.isBlank()) {
                throw new IllegalArgumentException("Invalid access token provided. The access token cannot be null, empty, or blank. Please re-authenticate to obtain a valid token.");
            }
            this.accessToken = accessToken;
        }

        @NotNull
        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL requestUrl) {
            return CompletableFuture.completedFuture(accessToken);
        }
    }

}
