# Account Search

## Overview

Searches for user accounts in Azure Active Directory based on various criteria including name, email, and mobile number, returning a list of matching accounts.

## Request Details

- **Area**: Entity Operations
- **Type**: QUERY_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

This request supports dynamic search parameters. No specific input parameters are required, but search criteria can be provided through the search interface.

### Search Criteria

The search functionality supports the following search fields:
- **Name**: Search by display name or parts of the name
- **Email**: Search by email address or email domain
- **Mobile Number**: Search by mobile phone number
- **User ID**: Search by unique identifier (GUID)

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Account | List<Account> | Array of Account entities matching the search criteria |

### Output Details

#### Account (List<Account>)
Each Account entity in the list contains:
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

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Search criteria too broad | "Search criteria too broad, please refine" | Provide more specific search terms |
| Invalid search format | "Invalid search format for {field}" | Ensure search terms match expected format |
| Search term too short | "Search term must be at least 3 characters" | Provide longer search terms |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid search parameters or criteria
**Common Scenarios**:
- Search terms that are too short or too broad
- Invalid format for specific search fields
- Malformed search queries

**Resolution**: Refine search criteria and ensure proper formatting

### Logic Errors (LOGIC_ERROR)
**Cause**: Search execution issues or access problems
**Common Scenarios**:
- No results found for search criteria
- Search timeout due to complex queries
- Insufficient permissions to search users

**Resolution**: Modify search criteria or check permissions

### System Errors (SYSTEM_ERROR)
**Cause**: Azure AD service issues or network problems
**Common Scenarios**:
- Azure AD service unavailable
- Network connectivity issues
- Rate limiting from Microsoft Graph API
- Search service disruptions

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

### Example 1: Search by Name

**Search Criteria**: "John"

**Output**:
```json
{
  "Account": [
    {
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
    },
    {
      "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
      "Display Name": "John Doe",
      "Email": "john.doe@company.com",
      "Mobile Phone": "+1-555-987-6543",
      "Job Title": "Product Manager",
      "Given Name": "John",
      "Surname": "Doe",
      "Office Location": "New York Office",
      "Account Enabled": true,
      "Mail Nickname": "john.doe",
      "Department": "Product"
    }
  ]
}
```

**Result**: All users with "John" in their display name

### Example 2: Search by Email Domain

**Search Criteria**: "@company.com"

**Output**:
```json
{
  "Account": [
    {
      "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "Display Name": "Alice Johnson",
      "Email": "alice.johnson@company.com",
      "Mobile Phone": "+1-555-111-2222",
      "Job Title": "Manager",
      "Account Enabled": true
    },
    {
      "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
      "Display Name": "Bob Wilson",
      "Email": "bob.wilson@company.com",
      "Mobile Phone": "+1-555-333-4444",
      "Job Title": "Developer",
      "Account Enabled": true
    }
  ]
}
```

**Result**: All users with company.com email domain

### Example 3: Search by Mobile Number

**Search Criteria**: "555-123"

**Output**:
```json
{
  "Account": [
    {
      "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "Display Name": "John Smith",
      "Email": "john.smith@company.com",
      "Mobile Phone": "+1-555-123-4567",
      "Job Title": "Senior Developer",
      "Account Enabled": true
    }
  ]
}
```

**Result**: Users with mobile numbers containing "555-123"

### Example 4: No Results Found

**Search Criteria**: "NonexistentUser"

**Output**:
```json
{
  "Account": []
}
```

**Result**: Empty array when no matching users are found

## Business Rules

1. **Partial Matching**: Search supports partial matches for name and email fields
2. **Case Insensitive**: All searches are case-insensitive
3. **Active and Inactive**: Returns both active and inactive accounts
4. **Permission-based Results**: Only returns users visible with current permissions
5. **Result Limits**: Large result sets may be limited to prevent performance issues
6. **Real-time Search**: Results reflect current state of Azure AD

## Limitations

1. **Search Scope**: Limited to users within the same Azure AD tenant
2. **Rate Limiting**: Microsoft Graph API rate limits apply to search operations
3. **Result Size**: Large result sets may be truncated or paginated
4. **Search Complexity**: Complex search queries may have performance implications
5. **Permission Filtering**: Results filtered based on application permissions

## Best Practices

### 1. Search Optimization
- Use specific search terms to reduce result sets
- Implement client-side filtering for better user experience
- Cache frequently searched results when appropriate

### 2. User Experience
- Provide search suggestions and auto-complete functionality
- Display search results in a user-friendly format
- Implement pagination for large result sets

### 3. Performance Considerations
- Limit search result size to improve performance
- Use debouncing for real-time search interfaces
- Monitor search patterns to optimize common queries

### 4. Error Handling
- Handle empty result sets gracefully
- Provide helpful error messages for invalid searches
- Implement retry logic for transient failures

## Common Use Cases

### 1. User Lookup
```
Scenario: Administrator needs to find a specific user account
Action: Search by name or email to locate the user
Result: User account information displayed for further action
```

### 2. Contact Directory
```
Scenario: Employee needs to find colleague contact information
Action: Search by name to find contact details
Result: Contact information retrieved for communication
```

### 3. Account Verification
```
Scenario: Verify if a user account exists before creating new account
Action: Search by email to check for existing account
Result: Confirmation of account existence or availability
```

### 4. Bulk Operations Preparation
```
Scenario: Identify users for bulk operations (group assignment, etc.)
Action: Search by department or role criteria
Result: List of users for subsequent bulk operations
```

## Related Catalog Requests

- [Get User Details](/pages/GetUserDetails.md) - Get detailed information for specific users
- [Create Account](/pages/CreateAccount.md) - Create new accounts for users not found
- [Update Account](/pages/UpdateAccount.md) - Modify information for found users
- [Add Account to Group](/pages/AddAccountToGroup.md) - Add found users to groups

## Technical Implementation

### Helper Class
- **Class**: AccountSearchHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.stores
- **Validation**: Search criteria validation and formatting
- **Service**: Delegates to AccountStore.search() method

### Search Implementation
- **Search Engine**: Uses Microsoft Graph API search capabilities
- **Filtering**: Client-side filtering for complex criteria
- **Pagination**: Supports pagination for large result sets
- **Caching**: Implements intelligent caching for performance

### Telemetry Metrics
- **ACCOUNT_SEARCH_INITIATED**: Tracks when search operations start
- **ACCOUNT_SEARCH_COMPLETED**: Tracks successful search operations
- **ACCOUNT_SEARCH_FAILED**: Tracks failed search attempts
- **SEARCH_RESULTS_COUNT**: Tracks number of results returned

## Troubleshooting

### No Results Returned
**Cause**: Search criteria doesn't match any users or insufficient permissions
**Solution**:
1. Verify search criteria are correct
2. Try broader search terms
3. Check application permissions

### Search Too Slow
**Cause**: Search criteria too broad or complex
**Solution**:
1. Use more specific search terms
2. Implement client-side filtering
3. Consider pagination for large result sets

### Permission Denied Error
**Cause**: Insufficient permissions to search users
**Solution**:
1. Verify User.Read.All or User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check for conditional access policies

### Incomplete Results
**Cause**: Result set truncated due to size limits
**Solution**:
1. Use more specific search criteria
2. Implement pagination to retrieve all results
3. Consider multiple targeted searches

## Security Considerations

### 1. Data Privacy
- Ensure search results comply with privacy policies
- Implement appropriate access controls for user data
- Log search operations for audit purposes

### 2. Information Disclosure
- Be aware of what user information is exposed through search
- Implement filtering for sensitive user data
- Consider data classification requirements

### 3. Access Control
- Verify users have appropriate permissions to search
- Monitor search patterns for unusual activity
- Implement rate limiting to prevent abuse

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Search Users API](https://docs.microsoft.com/en-us/graph/api/user-list) - Official API documentation
- [Azure AD User Properties](https://docs.microsoft.com/en-us/graph/api/resources/user) - User object reference
