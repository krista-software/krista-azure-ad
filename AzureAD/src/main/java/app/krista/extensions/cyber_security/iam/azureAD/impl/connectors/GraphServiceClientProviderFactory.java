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

import app.krista.extensions.cyber_security.iam.azureAD.AzureADAttributes;
import app.krista.extensions.cyber_security.iam.azureAD.impl.stores.AzureADAttributesStore;
import app.krista.extensions.cyber_security.iam.azureAD.impl.stores.RefreshTokenStore;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public final class GraphServiceClientProviderFactory {

    private final RefreshTokenStore refreshTokenStore;
    private final AzureADAttributesStore azureADAttributesStore;
    private final RequestContext requestContext;
    private final AuthorizationContext authorizationContext;
    private final GraphServiceClientProvider defaultClientProvider;

    @Inject
    public GraphServiceClientProviderFactory(RefreshTokenStore refreshTokenStore, AzureADAttributesStore azureADAttributesStore, AzureADAttributes azureADAttributes, RequestContext requestContext, AuthorizationContext authorizationContext) {
        this(refreshTokenStore, azureADAttributesStore, requestContext, authorizationContext,
                new GraphServiceClientProvider(refreshTokenStore, azureADAttributes, requestContext, authorizationContext, null));
    }

    public GraphServiceClientProviderFactory(RefreshTokenStore refreshTokenStore, AzureADAttributesStore azureADAttributesStore, RequestContext requestContext, AuthorizationContext authorizationContext, GraphServiceClientProvider defaultClientProvider) {
        this.refreshTokenStore = refreshTokenStore;
        this.azureADAttributesStore = azureADAttributesStore;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.defaultClientProvider = defaultClientProvider;
    }

    public GraphServiceClientProvider create(AzureADAttributes azureADAttributes) {
        String authContextId = azureADAttributesStore.save(azureADAttributes);
        return new GraphServiceClientProvider(refreshTokenStore, azureADAttributes, requestContext, authorizationContext, authContextId);
    }

    public GraphServiceClientProvider create() {
        return defaultClientProvider;
    }
}
