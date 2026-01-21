# Reset Password

## Overview

Generates a new temporary password for a user account in Azure Active Directory. The user must change this password on their next sign-in.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry with exponential backoff for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Id | Text | Yes | User identifier (email address or GUID) | "john.smith@company.com" or "a1b2c3d4-e5f6-7890-abcd-ef1234567890" |

### Parameter Details

#### Id
- **Purpose**: Identifies the user account for password reset
- **Format Options**:
  - **Email Address**: User principal name (e.g., "user@domain.com")
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate the user account for password reset

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Password | Text | Newly generated temporary password |

### Output Details

#### Password
- **Type**: Text (secure string)
- **Purpose**: Temporary password for user authentication
- **Characteristics**:
  - Randomly generated secure password
  - Meets Azure AD password complexity requirements
  - Must be changed on first use
- **Security**: Should be communicated securely to the user

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Id is empty or null | "User identifier cannot be empty" | Provide a valid user email or ID |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| Invalid GUID format | "Invalid user ID format" | Ensure GUID follows correct format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |
| User account disabled | "Cannot reset password for disabled account" | Activate the account first |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null user identifier
- Invalid email or GUID format
- Malformed input data

**Resolution**: Validate input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or user state issues
**Common Scenarios**:
- User account does not exist
- User account is disabled or in invalid state
- Password policy violations
- Insufficient permissions for password reset

**Resolution**: Verify user exists and is in valid state for password reset

### System Errors (SYSTEM_ERROR)
**Cause**: Azure AD service issues or network problems
**Common Scenarios**:
- Azure AD service unavailable
- Network connectivity issues
- Rate limiting from Microsoft Graph API
- Temporary service disruptions

**Resolution**: Automatic retry with exponential backoff (up to 3 attempts)

### Authorization Errors
**Cause**: Insufficient permissions or authentication issues
**Common Scenarios**:
- Missing User.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent
- Conditional access policy restrictions

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Reset Password by Email

**Input**:
```
Id: "john.smith@company.com"
```

**Output**:
```json
{
  "Password": "TempPass123!@#"
}
```

**Result**: New temporary password generated for the user

### Example 2: Reset Password by User ID

**Input**:
```
Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

**Output**:
```json
{
  "Password": "SecureTemp456$%^"
}
```

**Result**: Temporary password generated using GUID identifier

### Example 3: Retry Scenario

**Input**:
```
Id: "alice.johnson@company.com"
```

**Process**:
1. **First Attempt**: Fails due to temporary Azure AD service issue
2. **Wait**: 3 seconds (exponential backoff)
3. **Second Attempt**: Fails again due to continued service issue
4. **Wait**: 6 seconds (exponential backoff)
5. **Third Attempt**: Succeeds

**Output**:
```json
{
  "Password": "RetryPass789&*("
}
```

**Result**: Password successfully reset after automatic retry

## Business Rules

1. **Force Password Change**: Users must change the temporary password on next sign-in
2. **Password Complexity**: Generated passwords meet Azure AD complexity requirements
3. **Immediate Effect**: Password reset takes effect immediately
4. **Token Revocation**: Existing access tokens may be invalidated
5. **Audit Logging**: Password reset events are logged in Azure AD audit logs
6. **Account Status**: Account must be enabled for password reset to succeed

## Limitations

1. **Account State**: Cannot reset passwords for disabled accounts
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Password Policy**: Generated passwords must comply with tenant password policy
4. **Global Administrators**: Additional restrictions may apply to admin accounts
5. **Federated Users**: May not work for users from federated domains

## Best Practices

### 1. Secure Communication
- Communicate temporary passwords through secure channels
- Use encrypted email or secure messaging systems
- Avoid sending passwords via unencrypted methods

### 2. Password Management
- Ensure users change temporary passwords immediately
- Monitor for successful password changes
- Set appropriate password expiration policies

### 3. Security Monitoring
- Log all password reset operations
- Monitor for unusual password reset patterns
- Implement approval workflows for sensitive accounts

### 4. User Experience
- Provide clear instructions for password change process
- Offer self-service password reset options when possible
- Ensure users understand password requirements

## Common Use Cases

### 1. Forgotten Password
```
Scenario: User forgets their password and cannot sign in
Action: Reset password and provide temporary credentials
Result: User can sign in and set new password
```

### 2. Security Incident Response
```
Scenario: Suspected account compromise
Action: Reset password to invalidate existing credentials
Result: Unauthorized access prevented, user gets new credentials
```

### 3. New Employee Setup
```
Scenario: New employee needs initial password
Action: Reset password to provide secure temporary credentials
Result: Employee can sign in and set personal password
```

### 4. Bulk Password Reset
```
Scenario: Security policy requires password reset for multiple users
Action: Reset passwords for affected users with retry support
Result: All users receive new temporary passwords
```

## Related Catalog Requests

- [Create Account](/pages/CreateAccount.md) - Create accounts with initial passwords
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Ensure account is active
- [Get User Details](/pages/GetUserDetails.md) - Verify user information before reset
- [Update Account](/pages/UpdateAccount.md) - Update user information

## Technical Implementation

### Helper Class
- **Class**: ResetPasswordHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and user existence verification
- **Service**: Delegates to AccountImpl.resetPassword() method

### Retry Mechanism
- **Maximum Attempts**: 3
- **Backoff Strategy**: Exponential (3s, 6s, 12s)
- **Retry Conditions**: ClientException with specific error codes
- **Failure Handling**: Returns error after all retry attempts exhausted

### Telemetry Metrics
- **PASSWORD_RESET_INITIATED**: Tracks when password reset starts
- **PASSWORD_RESET_COMPLETED**: Tracks successful password resets
- **PASSWORD_RESET_FAILED**: Tracks failed password reset attempts
- **PASSWORD_RESET_RETRIED**: Tracks retry attempts

## Troubleshooting

### User Not Found Error
**Cause**: The specified identifier doesn't match any existing user
**Solution**:
1. Verify the user email or ID is correct
2. Check if the user exists in Azure AD
3. Ensure you're searching in the correct tenant

### Password Reset Fails for Disabled Account
**Cause**: User account is disabled in Azure AD
**Solution**:
1. Activate the user account first
2. Then attempt password reset
3. Consider if account should remain disabled

### Permission Denied Error
**Cause**: Insufficient permissions to reset passwords
**Solution**:
1. Verify User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### Retry Attempts Exhausted
**Cause**: Persistent Azure AD service issues
**Solution**:
1. Check Azure AD service health status
2. Wait for service recovery and try again
3. Contact Microsoft support if issues persist

### Generated Password Doesn't Meet Requirements
**Cause**: Tenant has custom password policy requirements
**Solution**:
1. Review tenant password policy settings
2. Ensure password generation meets all requirements
3. Contact Azure AD administrator for policy clarification

## Security Considerations

### 1. Password Security
- Generated passwords use cryptographically secure random generation
- Passwords meet or exceed Azure AD complexity requirements
- Temporary passwords have limited validity period

### 2. Access Control
- Limit password reset permissions to authorized personnel
- Implement approval workflows for sensitive accounts
- Monitor for unauthorized password reset attempts

### 3. Audit and Compliance
- All password reset operations are logged
- Maintain audit trails for compliance reporting
- Monitor password reset patterns for security analysis

### 4. Communication Security
- Use secure channels for password distribution
- Implement secure password delivery mechanisms
- Ensure passwords are not logged or cached

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Reset Password API](https://docs.microsoft.com/en-us/graph/api/user-update) - Official API documentation
- [Azure AD Password Policies](https://docs.microsoft.com/en-us/azure/active-directory/authentication/concept-password-ban-bad) - Password policy documentation
