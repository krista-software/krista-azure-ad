# Remove Account from Group

## Overview

Removes a user account from an Azure Active Directory group using the user's email/ID and the group's object ID, returning the removal status.

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
- **Purpose**: Identifies the user account to remove from the group
- **Format Options**:
  - **Email Address**: User principal name (e.g., "user@domain.com")
  - **User ID**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing user in Azure AD
- **Usage**: Used to locate the user account for group membership removal

#### Group Object Id
- **Purpose**: Identifies the target group for membership removal
- **Format**: GUID format (e.g., "12345678-1234-1234-1234-123456789abc")
- **Validation**: Must match an existing group in Azure AD
- **Usage**: Used to locate the target group for removing the user

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Removed | Switch | Boolean value indicating successful removal (true/false) |

### Output Details

#### Removed
- **Type**: Boolean (Switch)
- **Values**:
  - `true`: User was successfully removed from the group
  - `false`: User removal failed or user was not a member
- **Purpose**: Confirms the removal operation status

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| User Mail/Id is empty | "User identifier cannot be empty" | Provide a valid user email or ID |
| Group Object Id is empty | "Group Object Id cannot be empty" | Provide a valid group GUID |
| Invalid email format | "Invalid email format" | Ensure email follows user@domain.com format |
| Invalid GUID format | "Invalid GUID format" | Ensure GUIDs follow correct format |
| User not found | "User not found: {identifier}" | Verify the user exists in Azure AD |
| Group not found | "Group not found: {groupId}" | Verify the group exists in Azure AD |
| User not in group | "User is not a member of this group" | Verify current group membership |

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
- User is not a member of the group
- Insufficient permissions for group modification
- Group is a dynamic group (cannot manually remove members)

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

### Example 1: Remove User from Group

**Input**:
```
User Mail or Id: "john.smith@company.com"
Group Object Id: "0e831f0a-b85f-4971-80b5-34f2324b2598"
```

**Output**:
```json
{
  "Removed": true
}
```

**Result**: User successfully removed from the group

### Example 2: Remove User by ID from Group

**Input**:
```
User Mail or Id: "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
Group Object Id: "1f942e1b-c96f-5082-91c6-45g3435c3609"
```

**Output**:
```json
{
  "Removed": true
}
```

**Result**: User removed from group using GUID identifier

### Example 3: User Not in Group

**Input**:
```
User Mail or Id: "bob.wilson@company.com"
Group Object Id: "0e831f0a-b85f-4971-80b5-34f2324b2598"
```

**Output**:
```json
{
  "Removed": false
}
```

**Result**: Removal failed because user was not a group member

### Example 4: Group Not Found

**Input**:
```
User Mail or Id: "alice.johnson@company.com"
Group Object Id: "nonexistent-group-id"
```

**Output**:
```json
{
  "Removed": false
}
```

**Result**: Removal failed because group does not exist

## Business Rules

1. **Membership Verification**: User must be a current member to be removed
2. **Immediate Effect**: Group membership removal takes effect immediately
3. **Permission Revocation**: Users lose group permissions upon removal
4. **Audit Logging**: Group membership changes are logged in Azure AD audit logs
5. **Group Types**: Works with security groups and distribution groups
6. **Dynamic Groups**: Cannot manually remove members from dynamic groups

## Limitations

1. **Dynamic Groups**: Cannot remove members from dynamic groups
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Group Ownership**: Cannot remove group owners through this operation
4. **Nested Groups**: Does not handle nested group membership removal
5. **Tenant Boundaries**: Can only remove users from groups within the same tenant

## Best Practices

### 1. Pre-Removal Verification
- Verify user is currently a member of the group
- Check if removal will impact user's access to critical resources
- Confirm the correct group is being targeted

### 2. Access Impact Assessment
- Understand what permissions will be revoked
- Notify users of access changes when appropriate
- Document access changes for compliance purposes

### 3. Error Handling
- Handle "not a member" scenarios gracefully
- Implement proper error handling for non-existent groups
- Use retry logic for transient failures

### 4. Security Considerations
- Verify authorization before removing users from groups
- Log all group membership changes for audit purposes
- Monitor for unauthorized group removals

## Common Use Cases

### 1. Role Change
```
Scenario: Employee changes roles and no longer needs specific access
Action: Remove user from role-specific group
Result: User loses access to role-specific resources
```

### 2. Project Completion
```
Scenario: Project ends and team members no longer need project access
Action: Remove users from project-specific group
Result: Users lose access to project resources
```

### 3. Department Transfer
```
Scenario: Employee transfers to different department
Action: Remove user from old department group
Result: User loses access to previous department resources
```

### 4. Security Response
```
Scenario: User account compromised, need to revoke specific access
Action: Remove user from sensitive groups
Result: Reduced access scope while maintaining basic functionality
```

## Related Catalog Requests

- [Add Account to Group](/pages/AddAccountToGroup.md) - Add users to groups
- [Get List of Groups](/pages/GetListOfGroups.md) - Retrieve available groups
- [Get User Details](/pages/GetUserDetails.md) - Check current user group memberships
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Alternative access control

## Technical Implementation

### Helper Class
- **Class**: RemoveAccountFromGroupHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and membership verification
- **Service**: Delegates to AccountImpl.removeUserFromGroup() method

### Telemetry Metrics
- **GROUP_MEMBERSHIP_REMOVE_INITIATED**: Tracks when group removal starts
- **GROUP_MEMBERSHIP_REMOVE_COMPLETED**: Tracks successful group removals
- **GROUP_MEMBERSHIP_REMOVE_FAILED**: Tracks failed group removal attempts
- **USER_NOT_IN_GROUP**: Tracks attempts to remove users not in group

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

### User Not in Group
**Cause**: User is not currently a member of the specified group
**Solution**:
1. Verify current group memberships using Get User Details
2. Check if user was already removed
3. Confirm the correct group ID is being used

### Permission Denied Error
**Cause**: Insufficient permissions to modify group membership
**Solution**:
1. Verify Group.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### Cannot Remove from Dynamic Group
**Cause**: Attempting to remove user from a dynamic group
**Solution**:
1. Identify if the group is dynamic
2. Modify group membership rules instead
3. Contact Azure AD administrator for dynamic group changes

## Security Considerations

### 1. Access Control
- Verify authorization before removing users from groups
- Implement approval workflows for sensitive group removals
- Monitor for unauthorized group membership changes

### 2. Impact Assessment
- Understand the access implications of group removal
- Ensure users retain necessary access for their roles
- Document access changes for compliance and audit

### 3. Audit and Compliance
- Log all group membership removals with user context
- Maintain audit trails for compliance reporting
- Review group membership changes regularly

### 4. Emergency Procedures
- Establish procedures for emergency access revocation
- Ensure capability for rapid group membership changes
- Document escalation procedures for critical access issues

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Remove Group Member API](https://docs.microsoft.com/en-us/graph/api/group-delete-members) - Official API documentation
- [Azure AD Group Management](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/active-directory-groups-create-azure-portal) - Group management best practices
