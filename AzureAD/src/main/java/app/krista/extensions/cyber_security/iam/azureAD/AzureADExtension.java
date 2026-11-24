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

package app.krista.extensions.cyber_security.iam.azureAD;

import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.*;
import app.krista.extensions.cyber_security.iam.azureAD.impl.connectors.GraphServiceClientProviderFactory;
import app.krista.extensions.cyber_security.iam.azureAD.impl.stores.AzureADAttributesStore;

import javax.inject.Inject;
import java.util.Map;

@Java(version = Java.Version.JAVA_21)
@Field.Text(value = AzureADAttributes.CLIENT_ID)
@Field.Text(value = AzureADAttributes.CLIENT_SECRET, isSecured = true)
@Field.Text(value = AzureADAttributes.TENANT_ID, isSecured = true)
@Field.Text(value = AzureADAttributes.EMAIL)
@Extension(version = "2.0.6",name="AzureAD")
@StaticResource(path = "docs", file = "docs")
public final
class AzureADExtension {


    private final Invoker invoker;
    private final AzureADAttributes azureADAttributes;
    private final GraphServiceClientProviderFactory clientProviderFactory;
    private final AzureADRequestAuthenticator requestAuthenticator;

    @Inject
    public AzureADExtension(Invoker invoker, AzureADAttributes azureADAttributes, GraphServiceClientProviderFactory clientProviderFactory, AzureADAttributesStore azureADAttributesStore) {
        this(invoker, azureADAttributes,
                new AzureADRequestAuthenticator(azureADAttributesStore, azureADAttributes), clientProviderFactory);
    }

    public AzureADExtension(Invoker invoker, AzureADAttributes azureADAttributes, AzureADRequestAuthenticator requestAuthenticator, GraphServiceClientProviderFactory clientProviderFactory) {
        this.invoker = invoker;
        this.azureADAttributes = azureADAttributes;
        this.requestAuthenticator = requestAuthenticator;
        this.clientProviderFactory = clientProviderFactory;
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of("Documentation", "static/docs");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateConnection(Map<String, Object> connectionAttributes) {
        AzureADAttributes localAzureADAttributes = AzureADAttributes.create(invoker, connectionAttributes);
        validateAndTestConnection(localAzureADAttributes);
    }

    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
        validateAndTestConnection(azureADAttributes);
    }

    /**
     * @param azureADAttributes This function creates a connection with GraphServiceClient to validate the given credentials.
     */
    private void validateAndTestConnection(AzureADAttributes azureADAttributes) {
        if (azureADAttributes.getEmail() != null && !azureADAttributes.getEmail().isBlank()) {
            clientProviderFactory.create(azureADAttributes)
                    .getGraphServiceClientForAdmin()
                    .me().buildRequest().get();
        }
    }

    @InvokerRequest(InvokerRequest.Type.INVOKER_UPDATED)
    public void attributesUpdated(Map<String, Object> oldAttributes, Map<String, Object> newAttributes) {
        azureADAttributes.update(newAttributes);
    }
}

