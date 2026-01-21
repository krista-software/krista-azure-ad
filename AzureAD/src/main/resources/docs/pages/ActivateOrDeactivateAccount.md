# Activate or Deactivate Account

## Overview

Enables or disables a user account in Azure Active Directory, controlling the user's ability to sign in and access resources.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Deactivate | Switch | Yes | Boolean flag to deactivate (true) or activate (false) the account | true |
| User Mail or ID | Text | Yes | User identifier (email address or GUID) | "john.smith@company.com" |

### Parameter Details

#### Deactivate
- **Purpose**: Controls whether to deactivate or activate the user account
- **Values**:
  - `true`: Deactivate the account (disable sign-in)
  - `false`: Activate the account (enable sign-in)
- **Default**: No default value - must be explicitly specified
- **Usage**: Determines the final account enabled state

#### User Mail or ID
- **Purpose**: Identifies the user account to modify
- **Format Options**:
  - **Email Address**: User principal name (e.g., "user@domain.com")
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate the user account for status modification

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Account Status | Switch | Boolean value indicating the final account status |
| Message | Text | Descriptive message about the operation result |

### Output Details

#### Account Status
- **Type**: Boolean (Switch)
- **Values**:
  - `true`: Account is active (enabled)
  - `false`: Account is inactive (disabled)
- **Purpose**: Confirms the final account status after the operation

#### Message
- **Type**: Text
- **Purpose**: Provides human-readable confirmation of the operation
- **Examples**:
  - "User account has been successfully activated"
  - "User account has been successfully deactivated"
  - "Account status unchanged - already in requested state"

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| User Mail/ID is empty | "User identifier cannot be empty" | Provide a valid user email or ID |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| Invalid GUID format | "Invalid user ID format" | Ensure GUID follows correct format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |
| Deactivate parameter missing | "Deactivate parameter is required" | Specify true or false for deactivate parameter |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null user identifier
- Missing deactivate parameter
- Invalid email or GUID format

**Resolution**: Validate all input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or user state issues
**Common Scenarios**:
- User account does not exist
- User account is already in the requested state
- Insufficient permissions for account modification
- Account has restrictions preventing status changes

**Resolution**: Verify user exists and check permissions

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
- Conditional access policy restrictions

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Deactivate User Account

**Input**:
```
Deactivate: true
User Mail or ID: "john.smith@company.com"
```

**Output**:
```json
{
  "Account Status": false,
  "Message": "User account has been successfully deactivated"
}
```

**Result**: User account disabled, preventing sign-in

### Example 2: Activate User Account

**Input**:
```
Deactivate: false
User Mail or ID: "alice.johnson@company.com"
```

**Output**:
```json
{
  "Account Status": true,
  "Message": "User account has been successfully activated"
}
```

**Result**: User account enabled, allowing sign-in

### Example 3: Using User ID

**Input**:
```
Deactivate: true
User Mail or ID: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

**Output**:
```json
{
  "Account Status": false,
  "Message": "User account has been successfully deactivated"
}
```

**Result**: User account identified by GUID is deactivated

### Example 4: Account Already in Requested State

**Input**:
```
Deactivate: true
User Mail or ID: "bob.wilson@company.com"
```

**Output**:
```json
{
  "Account Status": false,
  "Message": "Account status unchanged - already deactivated"
}
```

**Result**: No change made as account was already deactivated

## Business Rules

1. **Immediate Effect**: Account status changes take effect immediately
2. **Sign-in Impact**: Deactivated users cannot sign in to any Azure AD-connected services
3. **Token Revocation**: Existing access tokens may be revoked when account is deactivated
4. **License Retention**: Licenses remain assigned to deactivated accounts
5. **Group Membership**: Group memberships are retained when account is deactivated
6. **Data Preservation**: User data and mailbox are preserved during deactivation

## Limitations

1. **Global Administrators**: Some restrictions may apply to Global Administrator accounts
2. **Service Accounts**: Critical service accounts may have additional protections
3. **Conditional Access**: Conditional access policies may override account status
4. **Rate Limiting**: Microsoft Graph API rate limits apply
5. **Propagation Time**: Changes may take time to propagate across all services

## Best Practices

### 1. Account Lifecycle Management
- Use deactivation instead of deletion for temporary access removal
- Document reasons for account status changes
- Implement approval workflows for sensitive accounts

### 2. Security Considerations
- Deactivate accounts immediately upon employee termination
- Monitor for unauthorized account activations
- Log all account status changes for audit purposes

### 3. Communication
- Notify users before deactivating their accounts (when appropriate)
- Inform IT teams about account status changes
- Update access documentation and procedures

### 4. Operational Efficiency
- Use batch operations for multiple account changes
- Implement automated workflows for common scenarios
- Monitor account status for compliance reporting

## Common Use Cases

### 1. Employee Termination
```
Scenario: Employee leaves the company
Action: Deactivate account to immediately revoke access
Result: User cannot sign in but data is preserved
```

### 2. Temporary Leave
```
Scenario: Employee on extended leave
Action: Deactivate account during absence, reactivate upon return
Result: Secure account during absence, easy restoration
```

### 3. Security Incident Response
```
Scenario: Suspected account compromise
Action: Immediately deactivate account to prevent further access
Result: Security threat contained while investigation proceeds
```

### 4. Account Recovery
```
Scenario: Previously deactivated account needs restoration
Action: Activate account to restore user access
Result: User regains access to all previously available resources
```

## Related Catalog Requests

- [Create Account](/pages/CreateAccount.md) - Create new user accounts
- [Delete Account](/pages/DeleteAccount.md) - Permanently remove user accounts
- [Get User Details](/pages/GetUserDetails.md) - Check current account status
- [Reset Password](/pages/ResetPassword.md) - Reset passwords for activated accounts

## Technical Implementation

### Helper Class
- **Class**: ActivateDeactivateAccountHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and user existence verification
- **Service**: Delegates to AccountImpl.activateOrDeactivateAccount() method

### Telemetry Metrics
- **ACCOUNT_ACTIVATION_INITIATED**: Tracks when account activation starts
- **ACCOUNT_DEACTIVATION_INITIATED**: Tracks when account deactivation starts
- **ACCOUNT_STATUS_CHANGED**: Tracks successful status changes
- **ACCOUNT_STATUS_UNCHANGED**: Tracks when no change was needed
- **ACCOUNT_STATUS_FAILED**: Tracks failed status change attempts

## Troubleshooting

### User Not Found Error
**Cause**: The specified identifier doesn't match any existing user
**Solution**:
1. Verify the user email or ID is correct
2. Check if the user exists in Azure AD
3. Ensure you're searching in the correct tenant

### Permission Denied Error
**Cause**: Insufficient permissions to modify user account status
**Solution**:
1. Verify User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### Account Status Not Changed
**Cause**: Account may already be in the requested state
**Solution**:
1. Check current account status before making changes
2. Verify the deactivate parameter value
3. Review the operation message for details

### Changes Not Taking Effect
**Cause**: Propagation delays or caching issues
**Solution**:
1. Allow time for changes to propagate across services
2. Clear browser caches and retry sign-in
3. Check Azure AD sign-in logs for confirmation

## Security Considerations

### 1. Access Control
- Limit account status modification permissions to authorized personnel
- Implement approval workflows for sensitive account changes
- Monitor for unauthorized status modifications

### 2. Audit and Compliance
- Log all account status changes with timestamps and user context
- Maintain audit trails for compliance reporting
- Review account status changes regularly

### 3. Emergency Procedures
- Establish procedures for emergency account deactivation
- Ensure 24/7 capability for security incident response
- Document escalation procedures for critical accounts

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Update User API](https://docs.microsoft.com/en-us/graph/api/user-update) - Official API documentation
- [Azure AD Account Management](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/add-users-azure-active-directory) - Account management best practices
