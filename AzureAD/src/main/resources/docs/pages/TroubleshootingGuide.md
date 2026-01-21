# Troubleshooting Guide

## Overview

This comprehensive troubleshooting guide helps you resolve common issues with the Azure AD Extension. Error messages have been designed to be user-friendly and actionable, providing clear guidance on how to resolve problems.

## Common Error Categories

### 🔐 Authentication and Authorization Errors

#### "Authentication required. Please click 'Authorize' to connect to Azure AD"
**Cause**: Initial authentication has not been completed
**Resolution**:
1. Click the **Authorize** button in the extension configuration
2. Complete the OAuth 2.0 authentication flow
3. Click **Validate Attributes** to confirm the connection
4. Save your configuration

#### "Your authentication session has expired. Please click 'Authorize' to re-authenticate"
**Cause**: Refresh token has expired or been revoked
**Resolution**:
1. Click the **Authorize** button to start re-authentication
2. Complete the OAuth 2.0 flow again
3. Validate the new connection
4. Resume your operations

**Common Causes of Token Expiration**:
- User password changed in Azure AD
- User account disabled or deleted
- Conditional access policy changes
- Extended period of inactivity (90+ days for verified apps)

#### "Unable to determine user account for authentication"
**Cause**: Extension configuration is missing administrator email
**Resolution**:
1. Go to extension configuration
2. Verify the **Email** field contains a valid administrator email
3. Ensure the email corresponds to a user with appropriate Azure AD permissions
4. Save and validate the configuration

### 👤 User Management Errors

#### "Unable to create user account. Please verify that the email address is valid and not already in use"
**Cause**: Email validation failed or user already exists
**Resolution**:
1. **Check email format**: Ensure email follows user@domain.com format
2. **Verify domain**: Confirm the domain is verified in your Azure AD tenant
3. **Check for existing user**: Search Azure AD to see if user already exists
4. **Review permissions**: Ensure your app has User.ReadWrite.All permission
5. **Check tenant limits**: Verify you haven't exceeded user creation limits

**Additional Checks**:
- Email domain must be verified in Azure AD
- User principal name must be unique within the tenant
- Check for soft-deleted users that may conflict

#### "Unable to update user account. Please verify that the email address or user ID is correct"
**Cause**: User not found or insufficient permissions
**Resolution**:
1. **Verify user exists**: Check that the user exists in Azure AD
2. **Check identifier format**: Ensure email or GUID is correctly formatted
3. **Confirm permissions**: Verify User.ReadWrite.All permission is granted
4. **Test with different identifier**: Try using email if using ID, or vice versa

#### "Unable to delete user account. Note: This action cannot be undone"
**Cause**: User not found, insufficient permissions, or deletion restrictions
**Resolution**:
1. **Confirm user exists**: Verify the user is present in Azure AD
2. **Check permissions**: Ensure your app can delete users
3. **Review dependencies**: Some users may have dependencies preventing deletion
4. **Consider deactivation**: Use account deactivation instead of deletion for temporary access removal

**Important Notes**:
- Deleted users cannot be recovered through this extension
- Some admin accounts may have additional deletion protections
- Consider data backup before deletion

#### "Unable to change account status. Please verify that the email address or user ID is correct"
**Cause**: User not found or permission issues
**Resolution**:
1. **Verify user identifier**: Check email address or user ID is correct
2. **Confirm user exists**: Ensure user is present in Azure AD
3. **Check permissions**: Verify User.ReadWrite.All permission
4. **Review account state**: Some accounts may have restrictions on status changes

### 🔑 Password Management Errors

#### "Unable to reset password. Please verify that the user email or ID is correct, the user exists in Azure AD, and the account is enabled"
**Cause**: User not found, account disabled, or permission issues
**Resolution**:
1. **Verify user exists**: Confirm user is present in Azure AD
2. **Check account status**: Ensure account is enabled (not disabled)
3. **Confirm permissions**: Verify User.ReadWrite.All permission
4. **Review password policy**: Ensure generated passwords meet tenant requirements

**Password Reset Requirements**:
- User account must be enabled
- User must not be a guest user (in some configurations)
- Application must have appropriate permissions
- Tenant password policy must allow programmatic resets

### 👥 Group Management Errors

#### "Unable to add user to group. Please verify that the user email/ID and group object ID are correct"
**Cause**: User or group not found, already a member, or permission issues
**Resolution**:
1. **Verify user exists**: Confirm user is present in Azure AD
2. **Check group exists**: Verify group object ID is correct
3. **Check current membership**: User may already be a member
4. **Confirm permissions**: Ensure Group.ReadWrite.All permission
5. **Review group type**: Some group types may restrict membership changes

**Group Membership Notes**:
- Dynamic groups cannot have members added manually
- Some groups may have membership restrictions
- Nested group memberships are not automatically handled

#### "Unable to remove user from group. Please verify that the user email/ID and group object ID are correct"
**Cause**: User not in group, group/user not found, or permission issues
**Resolution**:
1. **Verify current membership**: Check if user is actually in the group
2. **Confirm identifiers**: Verify user and group identifiers are correct
3. **Check permissions**: Ensure Group.ReadWrite.All permission
4. **Review group type**: Cannot remove from dynamic groups

### 🔍 Search and Retrieval Errors

#### "Unable to retrieve user information. The user ID or email was not found in Azure AD"
**Cause**: User does not exist or identifier is incorrect
**Resolution**:
1. **Check spelling**: Verify email address or user ID is correctly spelled
2. **Confirm user exists**: Search Azure AD portal to confirm user presence
3. **Try alternative identifier**: Use email if using ID, or vice versa
4. **Check tenant**: Ensure you're searching in the correct Azure AD tenant

#### "Unable to retrieve user list from Azure AD. Please check your network connection"
**Cause**: Network issues, permission problems, or service unavailability
**Resolution**:
1. **Check network**: Verify internet connectivity
2. **Confirm permissions**: Ensure User.Read.All or User.ReadWrite.All permission
3. **Check service status**: Verify Azure AD service health
4. **Review rate limits**: May be hitting API rate limits

## Permission Requirements Reference

### Required Application Permissions

| Operation | Required Permission | Description |
|-----------|-------------------|-------------|
| Create User | User.ReadWrite.All | Create new user accounts |
| Update User | User.ReadWrite.All | Modify existing user accounts |
| Delete User | User.ReadWrite.All | Remove user accounts |
| Activate/Deactivate User | User.ReadWrite.All | Change account enabled status |
| Reset Password | User.ReadWrite.All | Generate new passwords |
| Read User Details | User.Read.All or User.ReadWrite.All | Retrieve user information |
| Add to Group | Group.ReadWrite.All | Add users to groups |
| Remove from Group | Group.ReadWrite.All | Remove users from groups |
| List Groups | Group.Read.All or Group.ReadWrite.All | Retrieve group information |
| Search Users | User.Read.All or User.ReadWrite.All | Search user directory |

### Permission Verification Steps

1. **Check Azure AD App Registration**:
   - Go to Azure portal > App registrations > Your app
   - Click **API permissions**
   - Verify required permissions are listed

2. **Confirm Admin Consent**:
   - Check that permissions show "Granted for [Organization]"
   - If not granted, click "Grant admin consent"

3. **Validate Permission Scope**:
   - Ensure permissions are "Application" type for service account mode
   - Use "Delegated" permissions only for user-specific operations

## Network and Connectivity Issues

### Connection Timeouts
**Symptoms**: Operations fail with timeout errors
**Resolution**:
1. Check network connectivity to Azure endpoints
2. Verify firewall settings allow HTTPS traffic
3. Consider proxy configuration if applicable
4. Test connection from Azure portal

### Rate Limiting
**Symptoms**: "Too many requests" errors
**Resolution**:
1. Implement proper retry logic with exponential backoff
2. Reduce frequency of API calls
3. Use batch operations where possible
4. Monitor API usage patterns

## Configuration Issues

### Invalid Client Credentials
**Symptoms**: Authentication failures with "invalid_client" errors
**Resolution**:
1. Verify Client ID matches Azure AD app registration
2. Ensure Client Secret is current and not expired
3. Check for typos in credentials
4. Regenerate client secret if necessary

### Redirect URI Mismatch
**Symptoms**: "redirect_uri_mismatch" during authentication
**Resolution**:
1. Verify redirect URI in Azure AD app registration
2. Ensure exact match including protocol and path
3. Check for trailing slashes or case differences
4. Update registration if extension URL changed

## Best Practices for Error Prevention

### 1. Proactive Monitoring
- Set up alerts for authentication failures
- Monitor client secret expiration dates
- Track API usage to avoid rate limits
- Regular health checks of the extension

### 2. Proper Error Handling
- Implement retry logic for transient failures
- Log errors with sufficient detail for troubleshooting
- Provide user-friendly error messages
- Graceful degradation when possible

### 3. Regular Maintenance
- Keep extension updated to latest version
- Rotate client secrets before expiration
- Review and audit permissions regularly
- Test functionality after Azure AD changes

## Getting Additional Help

### 1. Check Extension Logs
- Review Krista diagnostics for detailed error information
- Look for correlation IDs in Azure AD sign-in logs
- Check Azure AD audit logs for operation details

### 2. Azure AD Resources
- [Azure AD Service Health](https://status.azure.com/)
- [Microsoft Graph API Documentation](https://docs.microsoft.com/en-us/graph/)
- [Azure AD Troubleshooting Guide](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/)

### 3. Extension Documentation
- [Extension Configuration](/pages/ExtensionConfiguration.md)
- [Authentication Guide](/pages/authentication.md)
- [Creating Azure App](/pages/CreatingAzureApp.md)

---

> **💡 Pro Tip**: When reporting issues, include the specific error message, operation being performed, and any relevant correlation IDs from Azure AD logs for faster resolution.
