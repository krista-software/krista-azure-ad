# Extension Configuration

## Overview

The Azure AD Extension requires proper configuration to establish a secure connection with your Azure Active Directory tenant. This configuration involves setting up OAuth 2.0 credentials, configuring authentication modes, and establishing the necessary permissions for the extension to perform user and group management operations.

## Configuration Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| Client ID | Text | Yes | OAuth 2.0 client identifier from your Azure AD app registration | "12345678-1234-1234-1234-123456789abc" |
| Client Secret | Text (Secured) | Yes | OAuth 2.0 client secret for authentication | "abcdef123456..." |
| Tenant ID | Text (Secured) | Yes | Azure AD tenant identifier | "87654321-4321-4321-4321-cba987654321" |
| Email | Text | Yes | Administrator email for service account authentication | "admin@yourcompany.com" |

### Parameter Details

#### Client ID
- **Purpose**: Identifies your Azure AD application to Microsoft's authentication servers
- **Format**: GUID (Globally Unique Identifier)
- **Location**: Found in Azure portal under App registrations > Your app > Overview
- **Security**: Not sensitive, but should be kept confidential

#### Client Secret
- **Purpose**: Authenticates your application to Azure AD
- **Format**: Base64-encoded string
- **Location**: Created in Azure portal under App registrations > Your app > Certificates & secrets
- **Security**: ⚠️ **Highly sensitive** - treat like a password
- **Expiration**: Client secrets have expiration dates - monitor and rotate regularly

#### Tenant ID
- **Purpose**: Identifies your specific Azure AD tenant
- **Format**: GUID (Globally Unique Identifier)
- **Location**: Found in Azure portal under Azure Active Directory > Properties
- **Alternative Names**: Directory ID

#### Email
- **Purpose**: Administrator account for service-level authentication
- **Format**: Valid email address
- **Requirements**: Must have appropriate permissions in Azure AD
- **Usage**: Used for refresh token association and service account operations

## Step-by-Step Setup

### 1. Prepare Azure AD Application
Before configuring the extension, ensure you have:
- Created an Azure AD app registration
- Configured appropriate API permissions
- Generated a client secret
- Set up redirect URIs

> **📝 Note**: If you haven't created your Azure AD application yet, follow the [Creating Azure App](/pages/CreatingAzureApp.md) guide first.

### 2. Access Extension Configuration
1. Navigate to your Krista environment
2. Go to Extensions management
3. Locate the Azure AD Extension
4. Click on **Configure** or **Setup**

### 3. Enter Configuration Parameters
1. **Client ID**: Copy from Azure portal > App registrations > Your app > Overview
2. **Client Secret**: Copy the secret value (not the ID) from Certificates & secrets
3. **Tenant ID**: Copy from Azure portal > Azure Active Directory > Properties
4. **Email**: Enter the administrator email address

### 4. Configure Redirect URI
1. In the extension Details tab, copy the **Extension Base URL**
2. Append `/rest/azuread/callback` to create the full redirect URI
3. Add this URI to your Azure AD app registration under Authentication > Redirect URIs

**Example Redirect URI Format**:
```
https://your-krista-instance.com/extensions/azure-ad-extension-id/rest/azuread/callback
```

### 5. Validate Configuration
1. Click **Validate** in the extension setup
2. Complete the OAuth authorization flow
3. Verify successful authentication

### 6. Test Connection
1. Click **Test Connection** to verify the setup
2. Confirm that the extension can communicate with Azure AD
3. Check for any error messages or warnings

## Authentication Type Selection

### Service Account Authentication
- **When to Use**: For automated, system-level operations
- **Configuration**: Uses the administrator email specified in configuration
- **Token Management**: Refresh tokens are managed automatically
- **Permissions**: Requires application-level permissions in Azure AD

### User Account Authentication
- **When to Use**: For user-specific operations requiring individual consent
- **Configuration**: Each user authenticates with their own credentials
- **Token Management**: Individual user tokens are managed separately
- **Permissions**: Uses delegated permissions on behalf of the user

> **💡 Tip**: For most enterprise scenarios, service account authentication is recommended for consistency and reliability.

## Allow Retry Flag

The extension includes a retry mechanism for handling transient failures:

- **Purpose**: Automatically retries failed operations due to temporary issues
- **Scope**: Applies to network timeouts, rate limiting, and temporary service unavailability
- **Configuration**: Enabled by default for most catalog requests
- **Behavior**: Uses exponential backoff strategy with maximum retry attempts

## Security Considerations

### 1. Credential Protection
- Store client secrets securely using Krista's secured field functionality
- Rotate client secrets regularly (recommended: every 6-12 months)
- Monitor secret expiration dates and renew before expiry
- Use Azure Key Vault for additional secret management if required

### 2. Principle of Least Privilege
- Grant only the minimum required permissions to your Azure AD application
- Regularly review and audit application permissions
- Remove unused or excessive permissions
- Use application permissions instead of delegated permissions where possible

### 3. Network Security
- Ensure HTTPS is used for all communications
- Verify redirect URIs are properly configured and secured
- Monitor for unauthorized access attempts
- Implement IP restrictions if required by your organization

### 4. Token Management
- Tokens are automatically managed by the extension
- Access tokens expire after 1 hour and are automatically refreshed
- Refresh tokens have longer lifespans but may expire due to various conditions
- Monitor authentication logs for unusual activity

## Troubleshooting

### Common Configuration Issues

#### Invalid Client ID or Secret
**Symptoms**: Authentication failures, "invalid_client" errors
**Resolution**:
1. Verify Client ID matches exactly from Azure portal
2. Ensure Client Secret is current and not expired
3. Check that the secret value (not ID) is used
4. Regenerate client secret if necessary

#### Incorrect Tenant ID
**Symptoms**: "tenant not found" or "unauthorized_client" errors
**Resolution**:
1. Verify Tenant ID from Azure AD Properties
2. Ensure the application is registered in the correct tenant
3. Check for typos in the Tenant ID

#### Redirect URI Mismatch
**Symptoms**: "redirect_uri_mismatch" errors during authentication
**Resolution**:
1. Verify the redirect URI in Azure AD app registration
2. Ensure the URI includes the correct extension base URL
3. Check for trailing slashes or case sensitivity issues

#### Permission Issues
**Symptoms**: "insufficient_privileges" or "access_denied" errors
**Resolution**:
1. Review API permissions in Azure AD app registration
2. Ensure admin consent has been granted
3. Verify the administrator email has appropriate privileges

### Advanced Troubleshooting

#### Token Refresh Failures
**Symptoms**: Periodic authentication failures after initial setup
**Resolution**:
1. Check refresh token expiration policies
2. Verify continuous usage requirements
3. Re-authenticate if refresh token has expired

#### Rate Limiting
**Symptoms**: "too_many_requests" errors
**Resolution**:
1. Implement proper retry logic (handled automatically)
2. Review usage patterns and optimize request frequency
3. Consider request batching for bulk operations

## Best Practices

### 1. Configuration Management
- Document all configuration parameters and their sources
- Maintain a configuration backup for disaster recovery
- Use environment-specific configurations for development/testing
- Implement configuration change approval processes

### 2. Monitoring and Maintenance
- Set up alerts for authentication failures
- Monitor client secret expiration dates
- Regularly review extension logs for errors or warnings
- Perform periodic connection tests

### 3. Security Hygiene
- Follow your organization's security policies for credential management
- Implement regular security reviews of the extension configuration
- Keep the extension updated to the latest version
- Monitor Microsoft security advisories for Azure AD

## See Also

- [Authentication](/pages/authentication.md) - Detailed authentication flow documentation
- [Creating Azure App](/pages/CreatingAzureApp.md) - Azure AD application setup guide
- [Microsoft Graph Permissions Reference](https://docs.microsoft.com/en-us/graph/permissions-reference) - Official permissions documentation
