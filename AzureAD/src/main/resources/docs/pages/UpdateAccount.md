# Update Account

## Overview

Updates an existing user account in Azure Active Directory with new information and returns the updated account entity with all current details.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Name | Text | Yes | Updated display name for the user account | "John A. Smith" |
| Email | Text | Yes | Current email address (used to identify the user) | "john.smith@company.com" |
| Mobile Number | Text | Yes | Updated mobile phone number | "+1-555-123-9999" |
| Extra Fields | List of Multi-field | No | Additional user profile fields to update | [{"key": "Job Title", "value": "Senior Manager"}] |

### Parameter Details

#### Name
- **Purpose**: Updates the display name for the user account
- **Format**: Full name (first and last name recommended)
- **Validation**: Must not be empty or contain only whitespace
- **Usage**: Updates the displayName property in Azure AD

#### Email
- **Purpose**: Identifies the user account to update (not changed by this operation)
- **Format**: Valid email address format (user@domain.com)
- **Validation**: Must match an existing user's userPrincipalName
- **Usage**: Used to locate the user account for updating

#### Mobile Number
- **Purpose**: Updates the contact phone number for the user
- **Format**: Phone number in international format recommended
- **Validation**: Must be a valid phone number format
- **Usage**: Updates the mobilePhone property in Azure AD

#### Extra Fields
Supported extra fields for updates include:
- **Job Title**: User's job title or position
- **Given Name**: First name of the user
- **Office Location**: Physical office location
- **Surname**: Last name of the user

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Updated Entity | Account Entity | Complete user account information after update |

### Output Details

#### Updated Entity (Account)
The Account entity contains:
- **User Id**: Unique identifier (GUID) for the user
- **Display Name**: Updated display name
- **Email**: User principal name (email address)
- **Mobile Phone**: Updated mobile phone number
- **Job Title**: Updated job title (if provided)
- **Given Name**: Updated first name (if provided)
- **Surname**: Updated last name (if provided)
- **Office Location**: Updated office location (if provided)
- **Account Enabled**: Account status (active/inactive)

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Name is empty or null | "Name cannot be empty" | Provide a valid display name |
| Email is invalid format | "Invalid email format" | Ensure email follows user@domain.com format |
| User not found | "Unable to update user account. Please verify that the email address or user ID is correct and the user exists in Azure AD" | Verify the email address exists in Azure AD |
| Mobile number is invalid | "Invalid mobile number format" | Provide a valid phone number |
| Extra field key is invalid | "Invalid extra field key: {key}" | Use only supported extra field keys |

## Error Handling

### Input Errors (INPUT_ERROR)
**Cause**: Invalid or missing required parameters
**Common Scenarios**:
- Empty or null name parameter
- Invalid email format
- Missing required fields

**Resolution**: Validate all input parameters before submission

### Logic Errors (LOGIC_ERROR)
**Cause**: Business logic violations or user not found
**Common Scenarios**:
- User account does not exist
- User account is in an invalid state for updates
- Insufficient permissions for user modification

**Resolution**: Verify user exists and check permissions

### System Errors (SYSTEM_ERROR)
**Cause**: Azure AD service issues or network problems
**Common Scenarios**:
- Azure AD service unavailable
- Network connectivity issues
- Rate limiting from Microsoft Graph API

**Resolution**: Retry the operation or check Azure AD service status

### Authorization Errors
**Cause**: Insufficient permissions or authentication issues
**Common Scenarios**:
- Missing User.ReadWrite.All permission
- Expired authentication tokens
- Application not granted admin consent

**Resolution**: Verify application permissions and re-authenticate if necessary

## Usage Examples

### Example 1: Basic Profile Update

**Input**:
```
Name: "John Alexander Smith"
Email: "john.smith@company.com"
Mobile Number: "+1-555-123-4567"
Extra Fields: []
```

**Output**:
```json
{
  "Updated Entity": {
    "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "Display Name": "John Alexander Smith",
    "Email": "john.smith@company.com",
    "Mobile Phone": "+1-555-123-4567",
    "Account Enabled": true
  }
}
```

**Result**: User's display name and mobile number updated successfully

### Example 2: Update with Extra Fields

**Input**:
```
Name: "Alice Johnson"
Email: "alice.johnson@company.com"
Mobile Number: "+1-555-987-6543"
Extra Fields: [
  {"key": "Job Title", "value": "Senior Developer"},
  {"key": "Office Location", "value": "Seattle Office"},
  {"key": "Given Name", "value": "Alice"},
  {"key": "Surname", "value": "Johnson"}
]
```

**Output**:
```json
{
  "Updated Entity": {
    "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
    "Display Name": "Alice Johnson",
    "Email": "alice.johnson@company.com",
    "Mobile Phone": "+1-555-987-6543",
    "Job Title": "Senior Developer",
    "Office Location": "Seattle Office",
    "Given Name": "Alice",
    "Surname": "Johnson",
    "Account Enabled": true
  }
}
```

**Result**: User profile updated with new job title and office location

### Example 3: Retry Scenario

**Input**:
```
Name: "Bob Wilson"
Email: "bob.wilson@company.com"
Mobile Number: "+1-555-456-7890"
Extra Fields: [{"key": "Job Title", "value": "Team Lead"}]
```

**Initial Result**: System error due to temporary Azure AD service issue
**Retry Result**: Successful update after automatic retry
**Final Output**: User account updated with new information

## Business Rules

1. **User Identification**: Users are identified by their email address (userPrincipalName)
2. **Partial Updates**: Only provided fields are updated; other fields remain unchanged
3. **Email Immutability**: The email address itself cannot be changed through this operation
4. **Account Status**: Account enabled/disabled status is not modified by this operation
5. **Validation Requirements**: All updated fields must meet Azure AD validation requirements
6. **Audit Trail**: All updates are logged in Azure AD audit logs

## Limitations

1. **Email Changes**: Cannot change the user's email address (userPrincipalName)
2. **Rate Limiting**: Microsoft Graph API rate limits apply
3. **Field Restrictions**: Some Azure AD fields cannot be updated via this operation
4. **Permission Requirements**: Requires User.ReadWrite.All application permission
5. **Tenant Boundaries**: Can only update users within the same Azure AD tenant

## Best Practices

### 1. Data Validation
- Validate all input data before submission
- Ensure phone numbers are in a consistent format
- Verify extra field values meet business requirements

### 2. Error Handling
- Implement proper error handling for user not found scenarios
- Use retry logic for transient failures
- Log update events for audit purposes

### 3. Partial Updates
- Only update fields that have actually changed
- Preserve existing data when possible
- Use extra fields to maintain complete user profiles

### 4. Security Considerations
- Verify user permissions before allowing updates
- Log all update operations for security auditing
- Validate that updates don't violate security policies

## Common Use Cases

### 1. Employee Information Update
```
Scenario: Employee changes job title or office location
Action: Update account with new profile information
Result: User profile reflects current employment details
```

### 2. Contact Information Update
```
Scenario: Employee provides new mobile phone number
Action: Update mobile number in user profile
Result: Updated contact information for MFA and notifications
```

### 3. Bulk Profile Updates
```
Scenario: Organization restructure requires job title updates
Action: Update multiple users with new job titles
Result: All affected users have updated profile information
```

## Related Catalog Requests

- [Create Account](/pages/CreateAccount.md) - Create new user accounts
- [Get User Details](/pages/GetUserDetails.md) - Retrieve current user information
- [Delete Account](/pages/DeleteAccount.md) - Remove user accounts
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Enable/disable accounts

## Technical Implementation

### Helper Class
- **Class**: UpdateAccountHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and user existence verification
- **Service**: Delegates to AccountImpl.updateUser() method

### Telemetry Metrics
- **USER_UPDATE_INITIATED**: Tracks when user update process starts
- **USER_UPDATE_COMPLETED**: Tracks successful user updates
- **USER_UPDATE_FAILED**: Tracks failed user update attempts
- **EXTRA_FIELDS_UPDATED**: Tracks usage of extra fields in updates

## Troubleshooting

### User Not Found Error
**Cause**: The specified email address doesn't match any existing user
**Solution**:
1. Verify the email address is correct
2. Check if the user exists in Azure AD
3. Ensure you're searching in the correct tenant

### Update Fails with Permission Error
**Cause**: Insufficient permissions to modify user accounts
**Solution**:
1. Verify User.ReadWrite.All permission is granted
2. Ensure admin consent has been provided
3. Check that the application has the necessary privileges

### Extra Fields Not Updated
**Cause**: Invalid extra field keys or unsupported field types
**Solution**:
1. Verify supported extra field keys
2. Check field value formats and requirements
3. Ensure field names match exactly (case-sensitive)

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph Update User API](https://docs.microsoft.com/en-us/graph/api/user-update) - Official API documentation
