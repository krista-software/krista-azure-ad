# Delete Account

## Overview

Permanently deletes a user account from Azure Active Directory using either the user ID or email address, and returns the deletion status.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| ID or User Principal Name | Text | Yes | User identifier (GUID) or email address | "john.smith@company.com" or "a1b2c3d4-e5f6-7890-abcd-ef1234567890" |

### Parameter Details

#### ID or User Principal Name
- **Purpose**: Identifies the user account to delete
- **Format Options**:
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
  - **Email Address**: User principal name format (e.g., "user@domain.com")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate and delete the specified user account

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Deleted | Switch | Boolean value indicating successful deletion (true/false) |

### Output Details

#### Deleted
- **Type**: Boolean (Switch)
- **Values**: 
  - `true`: User account was successfully deleted
  - `false`: User account deletion failed
- **Purpose**: Confirms the deletion operation status

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| ID/Email is empty or null | "User identifier cannot be empty" | Provide a valid user ID or email address |
| Invalid GUID format | "Invalid user ID format" | Ensure GUID follows correct format |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |
| User already deleted | "User has already been deleted" | No action needed - user is already removed |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null identifier parameter
- Invalid GUID or email format
- Malformed input data

**Resolution**: Validate input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or user state issues
**Common Scenarios**:
- User account does not exist
- User account is already deleted
- User has dependencies that prevent deletion
- Insufficient permissions for user deletion

**Resolution**: Verify user exists and check for dependencies

### System Errors (SYSTEM_ERROR)
**Cause**: Azure AD service issues or network problems
**Common Scenarios**:
- Azure AD service unavailable
- Network connectivity issues
- Rate limiting from Microsoft Graph API
- Temporary service disruptions

**Resolution**: Retry the operation or check Azure AD service status

### Authorization Errors
**Cause**: Insufficient permissions or authentication issues
**Common Scenarios**:
- Missing User.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent
- Conditional access policy blocking operation

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Delete User by Email

**Input**:
```
ID or User Principal Name: "john.smith@company.com"
```

**Output**:
```json
{
  "Deleted": true
}
```

**Result**: User account successfully deleted from Azure AD

### Example 2: Delete User by ID

**Input**:
```
ID or User Principal Name: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

**Output**:
```json
{
  "Deleted": true
}
```

**Result**: User account successfully deleted using GUID identifier

### Example 3: Failed Deletion - User Not Found

**Input**:
```
ID or User Principal Name: "nonexistent@company.com"
```

**Output**:
```json
{
  "Deleted": false
}
```

**Result**: Deletion failed because user does not exist

### Example 4: Retry Scenario

**Input**:
```
ID or User Principal Name: "alice.johnson@company.com"
```

**Initial Result**: System error due to temporary Azure AD service issue
**Retry Result**: Successful deletion after automatic retry
**Final Output**: `{"Deleted": true}`

## Business Rules

1. **Permanent Deletion**: User accounts are permanently deleted and cannot be recovered
2. **Soft Delete Period**: Azure AD may retain deleted users for 30 days in a soft-deleted state
3. **License Recovery**: Licenses assigned to deleted users are automatically recovered
4. **Group Membership**: User is automatically removed from all groups upon deletion
5. **Data Retention**: User data may be retained according to organizational policies
6. **Audit Trail**: Deletion events are logged in Azure AD audit logs

## Limitations

1. **Permanent Action**: Deleted users cannot be restored through this extension
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Dependencies**: Some users may have dependencies that prevent deletion
4. **Admin Accounts**: Global administrators may have additional protection
5. **Tenant Restrictions**: Subject to tenant-level deletion policies

## Best Practices

### 1. Pre-Deletion Verification
- Verify the user account exists before attempting deletion
- Confirm the correct user is being targeted
- Check for any critical dependencies or data

### 2. Data Backup
- Export important user data before deletion if required
- Document user's group memberships for potential restoration
- Save user profile information for compliance purposes

### 3. Communication
- Notify relevant stakeholders before deleting user accounts
- Inform IT teams about license recovery
- Update documentation and access lists

### 4. Security Considerations
- Verify deletion requests are authorized
- Log all deletion operations for audit purposes
- Monitor for unauthorized deletion attempts

## Common Use Cases

### 1. Employee Termination
```
Scenario: Employee leaves the company
Action: Delete user account to revoke all access
Result: Account removed, licenses recovered, access revoked
```

### 2. Account Cleanup
```
Scenario: Remove unused or test accounts
Action: Delete obsolete user accounts
Result: Cleaner directory and recovered licenses
```

### 3. Security Response
```
Scenario: Compromised account needs immediate removal
Action: Delete account to prevent further access
Result: Security threat mitigated through account removal
```

## Related Catalog Requests

- [Create Account](/pages/CreateAccount.md) - Create new user accounts
- [Update Account](/pages/UpdateAccount.md) - Modify existing user accounts
- [Get User Details](/pages/GetUserDetails.md) - Retrieve user information before deletion
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Alternative to deletion

## Technical Implementation

### Helper Class
- **Class**: DeleteAccountHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and user existence verification
- **Service**: Delegates to AccountImpl.deleteUser() method

### Telemetry Metrics
- **USER_DELETION_INITIATED**: Tracks when user deletion process starts
- **USER_DELETION_COMPLETED**: Tracks successful user deletions
- **USER_DELETION_FAILED**: Tracks failed user deletion attempts
- **USER_NOT_FOUND**: Tracks attempts to delete non-existent users

## Troubleshooting

### User Not Found Error
**Cause**: The specified identifier doesn't match any existing user
**Solution**:
1. Verify the user ID or email address is correct
2. Check if the user has already been deleted
3. Ensure you're searching in the correct tenant

### Deletion Fails with Permission Error
**Cause**: Insufficient permissions to delete user accounts
**Solution**:
1. Verify User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies blocking the operation

### Deletion Returns False
**Cause**: Various reasons can cause deletion to fail
**Solution**:
1. Check Azure AD audit logs for specific error details
2. Verify the user account is in a deletable state
3. Ensure no dependencies are preventing deletion

### Rate Limiting Issues
**Cause**: Too many deletion requests in a short time period
**Solution**:
1. Implement proper rate limiting in your application
2. Use batch operations when possible
3. Spread deletion operations over time

## Security Considerations

### 1. Authorization
- Verify deletion requests are properly authorized
- Implement approval workflows for sensitive accounts
- Log all deletion attempts for security auditing

### 2. Data Protection
- Ensure compliance with data retention policies
- Consider data export requirements before deletion
- Verify backup procedures are in place

### 3. Access Control
- Limit deletion permissions to authorized personnel
- Monitor for unusual deletion patterns
- Implement safeguards against accidental deletions

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Delete User API](https://docs.microsoft.com/en-us/graph/api/user-delete) - Official API documentation
- [Azure AD User Management](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/add-users-azure-active-directory) - User management best practices
