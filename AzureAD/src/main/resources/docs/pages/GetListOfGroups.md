# Get List of Groups

## Overview

Retrieves a comprehensive list of all groups available in the Azure Active Directory tenant, including group names and unique identifiers.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

This request requires no input parameters.

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Group List | MultiField | Array of group objects containing group information |

### Output Details

#### Group List
The Group List is an array of group objects, where each group contains:
- **Group Mail Id**: Group's email address (if available)
- **Group Id**: Unique identifier (GUID) for the group

**Example Structure**:
```json
[
  {
    "Group Mail Id": "developers@company.com",
    "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
  },
  {
    "Group Mail Id": "managers@company.com",
    "Group Id": "1f942e1b-c96f-5082-91c6-45g3435c3609"
  },
  {
    "Group Mail Id": "hr-team@company.com",
    "Group Id": "2g053f2c-d07g-6193-02d7-56h4546d4710"
  }
]
```

## Validation Rules

No input validation is required as this request takes no parameters.

## Error Handling

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
- Missing Group.Read.All or Group.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent
- Conditional access policy restrictions

**Resolution**: Verify application permissions and re-authenticate if necessary

### Logic Errors (LOGIC_ERROR)
**Cause**: Tenant configuration or access issues
**Common Scenarios**:
- No groups exist in the tenant
- Groups are hidden due to security policies
- Insufficient permissions to read group information

**Resolution**: Verify tenant configuration and permissions

## Usage Examples

### Example 1: Retrieve All Groups

**Input**: No parameters required

**Output**:
```json
{
  "Group List": [
    {
      "Group Mail Id": "all-employees@company.com",
      "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
    },
    {
      "Group Mail Id": "developers@company.com",
      "Group Id": "1f942e1b-c96f-5082-91c6-45g3435c3609"
    },
    {
      "Group Mail Id": "managers@company.com",
      "Group Id": "2g053f2c-d07g-6193-02d7-56h4546d4710"
    },
    {
      "Group Mail Id": "hr-team@company.com",
      "Group Id": "3h164g3d-e18h-7204-13e8-67i5657e5821"
    },
    {
      "Group Mail Id": "finance@company.com",
      "Group Id": "4i275h4e-f29i-8315-24f9-78j6768f6932"
    }
  ]
}
```

**Result**: Complete list of all groups in the tenant

### Example 2: Empty Tenant

**Input**: No parameters required

**Output**:
```json
{
  "Group List": []
}
```

**Result**: Empty array returned when no groups exist

### Example 3: Groups Without Email Addresses

**Input**: No parameters required

**Output**:
```json
{
  "Group List": [
    {
      "Group Mail Id": "security-group-1",
      "Group Id": "0e831f0a-b85f-4971-80b5-34f2324b2598"
    },
    {
      "Group Mail Id": "project-team-alpha",
      "Group Id": "1f942e1b-c96f-5082-91c6-45g3435c3609"
    }
  ]
}
```

**Result**: Groups without email addresses show display names in Group Mail Id field

## Business Rules

1. **Complete Enumeration**: Returns all groups visible to the application
2. **Real-time Data**: Information is retrieved in real-time from Azure AD
3. **Permission-based Visibility**: Only groups accessible with current permissions are returned
4. **Group Types**: Includes security groups, distribution groups, and Office 365 groups
5. **Sorting**: Groups are returned in the order provided by Azure AD (typically alphabetical)
6. **Pagination**: Large result sets may be paginated by Azure AD

## Limitations

1. **Permission Scope**: Groups returned depend on application permissions
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Large Tenants**: Very large tenants may experience slower response times
4. **Group Visibility**: Some groups may be hidden based on security policies
5. **Deleted Groups**: Recently deleted groups are not included in results

## Best Practices

### 1. Caching Strategy
- Cache group list results to reduce API calls
- Refresh cached data periodically (recommended: every 15-30 minutes)
- Implement cache invalidation for real-time scenarios

### 2. Performance Optimization
- Use cached results for dropdown lists and selection interfaces
- Implement pagination for large result sets if needed
- Monitor API usage to stay within rate limits

### 3. Error Handling
- Handle empty result sets gracefully
- Implement proper error handling for permission issues
- Use retry logic for transient failures

### 4. User Experience
- Display group names in user-friendly format
- Provide search/filter functionality for large group lists
- Sort groups alphabetically for better usability

## Common Use Cases

### 1. Group Selection Interface
```
Scenario: User interface needs to display available groups for selection
Action: Retrieve group list to populate dropdown or selection list
Result: Users can select from all available groups
```

### 2. Group Management Dashboard
```
Scenario: Administrator needs overview of all groups in tenant
Action: Get complete group list for management interface
Result: Comprehensive view of organizational groups
```

### 3. Access Control Configuration
```
Scenario: Setting up access controls that reference specific groups
Action: Retrieve group list to identify target groups
Result: Accurate group IDs for access control configuration
```

### 4. Audit and Compliance Reporting
```
Scenario: Generate reports on group structure and membership
Action: Get group list as foundation for detailed analysis
Result: Complete inventory of groups for compliance reporting
```

## Related Catalog Requests

- [Add Account to Group](/pages/AddAccountToGroup.md) - Add users to specific groups
- [Remove Account from Group](/pages/RemoveAccountFromGroup.md) - Remove users from groups
- [Get User Details](/pages/GetUserDetails.md) - Check user's current group memberships
- [Account Search](/pages/AccountSearch.md) - Search for users within specific groups

## Technical Implementation

### Helper Class
- **Class**: GetListOfGroupsHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: No input validation required
- **Service**: Delegates to AccountImpl.getAllGroups() method

### Telemetry Metrics
- **GROUP_LIST_REQUESTED**: Tracks when group list retrieval starts
- **GROUP_LIST_RETRIEVED**: Tracks successful group list retrieval
- **GROUP_LIST_FAILED**: Tracks failed group list retrieval attempts
- **GROUP_COUNT_RETURNED**: Tracks number of groups returned

## Troubleshooting

### Empty Group List Returned
**Cause**: No groups exist or insufficient permissions
**Solution**:
1. Verify groups exist in Azure AD portal
2. Check application permissions
3. Ensure admin consent has been granted

### Permission Denied Error
**Cause**: Insufficient permissions to read group information
**Solution**:
1. Verify Group.Read.All or Group.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### Incomplete Group List
**Cause**: Some groups may be hidden due to permissions or policies
**Solution**:
1. Review application permissions
2. Check for security policies that hide groups
3. Verify admin consent covers all required scopes

### Rate Limiting Issues
**Cause**: Too many requests in a short time period
**Solution**:
1. Implement proper caching to reduce API calls
2. Use exponential backoff for retries
3. Monitor API usage patterns

### Slow Response Times
**Cause**: Large number of groups in tenant
**Solution**:
1. Implement caching strategy
2. Consider pagination for very large result sets
3. Use asynchronous processing for better user experience

## Security Considerations

### 1. Data Privacy
- Ensure group information is handled according to privacy policies
- Implement appropriate access controls for group data
- Log access to group information for audit purposes

### 2. Permission Management
- Use principle of least privilege for group access
- Regularly review and audit group read permissions
- Monitor for unauthorized access to group information

### 3. Information Disclosure
- Be aware that group names may contain sensitive information
- Implement appropriate filtering for sensitive groups
- Consider data classification for group information

## Performance Considerations

### 1. Caching
- Implement intelligent caching to reduce API calls
- Use appropriate cache expiration times
- Consider cache warming strategies for better performance

### 2. API Usage
- Monitor API rate limits and usage patterns
- Implement efficient retry mechanisms
- Use batch operations when possible

### 3. User Experience
- Provide loading indicators for group list retrieval
- Implement search and filter functionality
- Consider pagination for large group lists

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph List Groups API](https://docs.microsoft.com/en-us/graph/api/group-list) - Official API documentation
- [Azure AD Group Types](https://docs.microsoft.com/en-us/azure/active-directory/fundamentals/active-directory-groups-create-azure-portal) - Understanding different group types
