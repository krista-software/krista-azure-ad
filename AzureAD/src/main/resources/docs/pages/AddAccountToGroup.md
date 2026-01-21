# Add Account to Group

## Overview

Adds a user account to an Azure Active Directory group using the user's email/ID and the group's object ID, returning updated account information with group memberships.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| User Mail or Id | Text | Yes | User identifier (email address or GUID) | "john.smith@company.com" |
| Group Object Id | Text | Yes | Unique identifier (GUID) of the target group | "0e831f0a-b85f-4971-80b5-34f2324b2598" |

### Parameter Details

#### User Mail or Id
- **Purpose**: Identifies the user account to add to the group
- **Format Options**:
  - **Email Address**: User principal name (e.g., "user@domain.com")
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate the user account for group membership addition

#### Group Object Id
- **Purpose**: Identifies the target group for membership addition
- **Format**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing group in Azure AD
- **Usage**: Used to locate the target group for adding the user

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Account Info | Complex Object | User account information with updated group memberships |

### Output Details

#### Account Info
The Account Info object contains:
- **User Mail Id**: User's email address
- **User Name**: Display name of the user
- **User Id**: Unique identifier (GUID) for the user
- **Group List**: Array of group objects containing:
  - **Group Mail Id**: Group's email address (if available)
  - **Group Id**: Group's unique identifier (GUID)

**Example Structure**:
```json
{
  "User Mail Id": "john.smith@company.com",
  "User Name": "John Smith",
  "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "Group List": [
    {
      "Group Mail Id": "developers@company.com",
      "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
    }
  ]
}
```

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| User Mail/Id is empty | "User identifier cannot be empty" | Provide a valid user email or ID |
| Group Object Id is empty | "Group Object Id cannot be empty" | Provide a valid group GUID |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| Invalid GUID format | "Invalid GUID format" | Ensure GUIDs follow correct format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |
| Group not found | "Group not found: {groupId}" | Verify the group exists in Azure AD |
| User already in group | "User is already a member of this group" | No action needed - membership already exists |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null user identifier
- Empty or null group object ID
- Invalid email or GUID format

**Resolution**: Validate all input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or membership issues
**Common Scenarios**:
- User account does not exist
- Group does not exist
- User is already a member of the group
- Group membership limits exceeded
- Insufficient permissions for group modification

**Resolution**: Verify user and group exist, check current memberships

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
- Missing Group.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent
- Conditional access policy restrictions

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Add User to Group

**Input**:
```
User Mail or Id: "john.smith@company.com"
Group Object Id: "0e831f0a-b85f-4971-80b5-34f2324b2598"
```

**Output**:
```json
{
  "Account Info": {
    "User Mail Id": "john.smith@company.com",
    "User Name": "John Smith",
    "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Group List": [
      {
        "Group Mail Id": "developers@company.com",
        "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
      }
    ]
  }
}
```

**Result**: User successfully added to the group

### Example 2: Add User by ID to Group

**Input**:
```
User Mail or Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
Group Object Id: "1f942e1b-c96f-5082-91c6-45g3435c3609"
```

**Output**:
```json
{
  "Account Info": {
    "User Mail Id": "alice.johnson@company.com",
    "User Name": "Alice Johnson",
    "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Group List": [
      {
        "Group Mail Id": "managers@company.com",
        "Group Id": "1f942e1b-c96f-5082-91c6-45g3435c3609"
      },
      {
        "Group Mail Id": "developers@company.com",
        "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
      }
    ]
  }
}
```

**Result**: User added to additional group, showing all group memberships

### Example 3: User Already in Group

**Input**:
```
User Mail or Id: "bob.wilson@company.com"
Group Object Id: "0e831f0a-b85f-4971-80b5-34f2324b2598"
```

**Output**:
```json
{
  "Account Info": {
    "User Mail Id": "bob.wilson@company.com",
    "User Name": "Bob Wilson",
    "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
    "Group List": [
      {
        "Group Mail Id": "developers@company.com",
        "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
      }
    ]
  }
}
```

**Result**: No change made as user was already a group member

## Business Rules

1. **Membership Uniqueness**: Users can only be added to a group once
2. **Immediate Effect**: Group membership changes take effect immediately
3. **Permission Inheritance**: Users inherit group permissions upon addition
4. **Audit Logging**: Group membership changes are logged in Azure AD audit logs
5. **Group Types**: Works with security groups and distribution groups
6. **Nested Groups**: Does not automatically handle nested group memberships

## Limitations

1. **Group Membership Limits**: Azure AD has limits on group membership size
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Group Types**: Some group types may have restrictions on membership
4. **Dynamic Groups**: Cannot manually add members to dynamic groups
5. **Tenant Boundaries**: Can only add users to groups within the same tenant

## Best Practices

### 1. Group Management
- Verify group exists before attempting to add members
- Check current group memberships to avoid duplicate additions
- Use group object IDs rather than display names for reliability

### 2. User Verification
- Confirm user account exists and is active
- Validate user permissions before group addition
- Consider user's role and responsibilities when adding to groups

### 3. Error Handling
- Handle "already a member" scenarios gracefully
- Implement proper error handling for non-existent groups
- Use retry logic for transient failures

### 4. Security Considerations
- Verify authorization before adding users to sensitive groups
- Log all group membership changes for audit purposes
- Monitor for unauthorized group additions

## Common Use Cases

### 1. Role-Based Access Control
```
Scenario: Employee gets promoted and needs access to management resources
Action: Add user to "Managers" group
Result: User gains access to management-specific resources
```

### 2. Project Team Assignment
```
Scenario: Developer joins a new project team
Action: Add user to project-specific group
Result: User gains access to project resources and collaboration tools
```

### 3. Department Transfer
```
Scenario: Employee transfers to a different department
Action: Add user to new department group
Result: User gains access to department-specific resources
```

### 4. Bulk Group Assignment
```
Scenario: Multiple users need to be added to the same group
Action: Add each user to the group with retry support
Result: All users become group members with proper error handling
```

## Related Catalog Requests

- [Remove Account from Group](/pages/RemoveAccountFromGroup.md) - Remove users from groups
- [Get List of Groups](/pages/GetListOfGroups.md) - Retrieve available groups
- [Get User Details](/pages/GetUserDetails.md) - Check current user group memberships
- [Create Account](/pages/CreateAccount.md) - Create accounts that may need group assignments

## Technical Implementation

### Helper Class
- **Class**: AddAccountToGroupHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and existence verification
- **Service**: Delegates to AccountImpl.addUserToGroup() method

### Telemetry Metrics
- **GROUP_MEMBERSHIP_ADD_INITIATED**: Tracks when group addition starts
- **GROUP_MEMBERSHIP_ADD_COMPLETED**: Tracks successful group additions
- **GROUP_MEMBERSHIP_ADD_FAILED**: Tracks failed group addition attempts
- **USER_ALREADY_IN_GROUP**: Tracks attempts to add users already in group

## Troubleshooting

### User Not Found Error
**Cause**: The specified user identifier doesn't match any existing user
**Solution**:
1. Verify the user email or ID is correct
2. Check if the user exists in Azure AD
3. Ensure you're searching in the correct tenant

### Group Not Found Error
**Cause**: The specified group object ID doesn't match any existing group
**Solution**:
1. Verify the group object ID is correct
2. Check if the group exists in Azure AD
3. Use the Get List of Groups request to find valid group IDs

### Permission Denied Error
**Cause**: Insufficient permissions to modify group membership
**Solution**:
1. Verify Group.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### User Already in Group
**Cause**: User is already a member of the specified group
**Solution**:
1. This is not an error - no action needed
2. Check current group memberships if verification is needed
3. Consider if different group was intended

## Security Considerations

### 1. Access Control
- Verify authorization before adding users to groups
- Implement approval workflows for sensitive group additions
- Monitor for unauthorized group membership changes

### 2. Audit and Compliance
- Log all group membership additions with user context
- Maintain audit trails for compliance reporting
- Review group membership changes regularly

### 3. Group Security
- Understand the permissions granted by group membership
- Ensure users should have access to group resources
- Monitor for privilege escalation through group membership

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Add Group Member API](https://docs.microsoft.com/en-us/graph/api/group-post-members) - Official API documentation
- [Azure AD Group Management](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/active-directory-groups-create-azure-portal) - Group management best practices
