# Get User Details by ID or Email

## Overview

Retrieves comprehensive user account information from Azure Active Directory using either the user ID or email address.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Id | Text | Yes | User identifier (email address or GUID) | "john.smith@company.com" or "a1b2c3d4-e5f6-7890-abcd-ef1234567890" |

### Parameter Details

#### Id
- **Purpose**: Identifies the user account to retrieve
- **Format Options**:
  - **Email Address**: User principal name (e.g., "user@domain.com")
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate and retrieve the specified user account

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Result | Account Entity | Complete user account information |

### Output Details

#### Result (Account Entity)
The Account entity contains comprehensive user information:

- **User Id**: Unique identifier (GUID) for the user
- **Display Name**: Full display name of the user
- **Email**: User principal name (primary email address)
- **Mobile Phone**: Mobile phone number
- **Job Title**: User's job title or position
- **Given Name**: First name of the user
- **Surname**: Last name of the user
- **Office Location**: Physical office location
- **Account Enabled**: Boolean indicating if account is active
- **Mail Nickname**: Email alias/nickname
- **Department**: Department or organizational unit
- **Manager**: Manager information (if available)
- **Group Memberships**: List of groups the user belongs to

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Id is empty or null | "User identifier cannot be empty" | Provide a valid user email or ID |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| Invalid GUID format | "Invalid user ID format" | Ensure GUID follows correct format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null user identifier
- Invalid email or GUID format
- Malformed input data

**Resolution**: Validate input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or user access issues
**Common Scenarios**:
- User account does not exist
- User account is in an inaccessible state
- Insufficient permissions to read user details
- User is in a different tenant

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
- Missing User.Read.All or User.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent
- Conditional access policy restrictions

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Get User by Email

**Input**:
```
Id: "john.smith@company.com"
```

**Output**:
```json
{
  "Result": {
    "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Display Name": "John Smith",
    "Email": "john.smith@company.com",
    "Mobile Phone": "+1-555-123-4567",
    "Job Title": "Senior Developer",
    "Given Name": "John",
    "Surname": "Smith",
    "Office Location": "Seattle Office",
    "Account Enabled": true,
    "Mail Nickname": "john.smith",
    "Department": "Engineering"
  }
}
```

**Result**: Complete user profile information retrieved

### Example 2: Get User by ID

**Input**:
```
Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
```

**Output**:
```json
{
  "Result": {
    "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Display Name": "Alice Johnson",
    "Email": "alice.johnson@company.com",
    "Mobile Phone": "+1-555-987-6543",
    "Job Title": "Product Manager",
    "Given Name": "Alice",
    "Surname": "Johnson",
    "Office Location": "New York Office",
    "Account Enabled": true,
    "Mail Nickname": "alice.johnson",
    "Department": "Product"
  }
}
```

**Result**: User information retrieved using GUID identifier

### Example 3: User Not Found

**Input**:
```
Id: "nonexistent@company.com"
```

**Output**:
```json
{
  "Result": {}
}
```

**Result**: Empty Account entity returned when user doesn't exist

### Example 4: Disabled User Account

**Input**:
```
Id: "disabled.user@company.com"
```

**Output**:
```json
{
  "Result": {
    "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
    "Display Name": "Disabled User",
    "Email": "disabled.user@company.com",
    "Mobile Phone": "+1-555-000-0000",
    "Job Title": "Former Employee",
    "Account Enabled": false,
    "Mail Nickname": "disabled.user"
  }
}
```

**Result**: User information retrieved including disabled status

## Business Rules

1. **Data Completeness**: Returns all available user profile information
2. **Privacy Compliance**: Only returns data accessible with current permissions
3. **Real-time Data**: Information is retrieved in real-time from Azure AD
4. **Account Status**: Includes current account enabled/disabled status
5. **Group Information**: May include group membership details based on permissions
6. **Audit Logging**: Read operations are logged in Azure AD audit logs

## Limitations

1. **Permission Scope**: Data returned depends on application permissions
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Tenant Boundaries**: Can only retrieve users from the same Azure AD tenant
4. **Sensitive Data**: Some sensitive fields may require additional permissions
5. **Deleted Users**: Cannot retrieve information for permanently deleted users

## Best Practices

### 1. Data Usage
- Use retrieved information responsibly and in compliance with privacy policies
- Cache user data appropriately to reduce API calls
- Refresh cached data periodically to ensure accuracy

### 2. Error Handling
- Handle user not found scenarios gracefully
- Implement proper error handling for permission issues
- Use retry logic for transient failures

### 3. Performance Optimization
- Batch multiple user lookups when possible
- Use appropriate caching strategies
- Monitor API usage to stay within rate limits

### 4. Security Considerations
- Ensure user data is handled securely
- Log access to user information for audit purposes
- Implement appropriate access controls

## Common Use Cases

### 1. User Profile Display
```
Scenario: Application needs to display user profile information
Action: Retrieve user details for profile page
Result: Complete user information displayed to authorized users
```

### 2. Account Verification
```
Scenario: Verify user account exists before performing operations
Action: Get user details to confirm account existence
Result: Confirmation of user account status and basic information
```

### 3. Contact Information Lookup
```
Scenario: Need to contact a user for business purposes
Action: Retrieve user contact information
Result: Current phone number and email address obtained
```

### 4. Account Status Check
```
Scenario: Verify if user account is active before granting access
Action: Get user details to check account enabled status
Result: Account status confirmed for access control decisions
```

## Related Catalog Requests

- [Create Account](/pages/CreateAccount.md) - Create new user accounts
- [Update Account](/pages/UpdateAccount.md) - Modify user account information
- [Delete Account](/pages/DeleteAccount.md) - Remove user accounts
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Change account status
- [Account Search](/pages/AccountSearch.md) - Search for multiple users

## Technical Implementation

### Helper Class
- **Class**: GetUserDetailsHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and user existence verification
- **Service**: Delegates to AccountImpl.getUser() method

### Telemetry Metrics
- **USER_DETAILS_REQUESTED**: Tracks when user details retrieval starts
- **USER_DETAILS_RETRIEVED**: Tracks successful user details retrieval
- **USER_DETAILS_FAILED**: Tracks failed user details retrieval attempts
- **USER_NOT_FOUND**: Tracks attempts to retrieve non-existent users

## Troubleshooting

### User Not Found
**Cause**: The specified identifier doesn't match any existing user
**Solution**:
1. Verify the user email or ID is correct
2. Check if the user exists in Azure AD
3. Ensure you're searching in the correct tenant

### Empty Result Returned
**Cause**: User may not exist or insufficient permissions
**Solution**:
1. Verify user exists in Azure AD portal
2. Check application permissions
3. Ensure admin consent has been granted

### Incomplete User Information
**Cause**: Insufficient permissions to read all user properties
**Solution**:
1. Review application permissions in Azure AD
2. Ensure appropriate read permissions are granted
3. Request additional permissions if needed

### Permission Denied Error
**Cause**: Insufficient permissions to read user information
**Solution**:
1. Verify User.Read.All or User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

## Security Considerations

### 1. Data Privacy
- Ensure compliance with data protection regulations
- Implement appropriate access controls for user data
- Log access to user information for audit purposes

### 2. Permission Management
- Use principle of least privilege for data access
- Regularly review and audit data access permissions
- Monitor for unauthorized access attempts

### 3. Data Handling
- Handle user data securely in transit and at rest
- Implement appropriate data retention policies
- Ensure secure disposal of user data when no longer needed

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Get User API](https://docs.microsoft.com/en-us/graph/api/user-get) - Official API documentation
- [Azure AD User Properties](https://docs.microsoft.com/en-us/graph/api/resources/user) - User object reference
