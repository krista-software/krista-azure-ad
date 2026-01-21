# Create Account

## Overview

Creates a new user account in Azure Active Directory with the specified details and returns the newly created account information including the user ID and default password.

## Request Details

- **Area**: Account Manager
- **Type**: CHANGE_SYSTEM
- **Retry Support**: ✅ Yes - Supports automatic retry for transient failures

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Name | Text | Yes | Display name for the new user account | "John Smith" |
| Email | Text | Yes | Email address that will serve as the user principal name | "john.smith@company.com" |
| Mobile Number | Text | Yes | Mobile phone number for the user | "+1-555-123-4567" |
| Extra Fields | List of Multi-field | No | Additional user profile fields as key-value pairs | [{"key": "Job Title", "value": "Manager"}] |

### Parameter Details

#### Name
- **Purpose**: Sets the display name for the user account
- **Format**: Full name (first and last name recommended)
- **Validation**: Must not be empty or contain only whitespace
- **Usage**: Used for display purposes in Azure AD and Office 365 applications

#### Email
- **Purpose**: Primary email address and user principal name for the account
- **Format**: Valid email address format (user@domain.com)
- **Validation**: Must be unique within the Azure AD tenant
- **Usage**: Used for sign-in and as the primary email address

#### Mobile Number
- **Purpose**: Contact phone number for the user
- **Format**: Phone number in international format recommended
- **Validation**: Must be a valid phone number format
- **Usage**: Used for multi-factor authentication and contact purposes

#### Extra Fields
Supported extra fields include:
- **Job Title**: User's job title or position
- **Given Name**: First name of the user
- **Office Location**: Physical office location
- **Surname**: Last name of the user

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| User Id | Text | Unique identifier (GUID) for the newly created user |
| Default Password | Text | Temporary password for initial login |

### Output Details

#### User Id
- **Format**: GUID (e.g., "12345678-1234-1234-1234-123456789abc")
- **Purpose**: Unique identifier for the user account in Azure AD
- **Usage**: Used for subsequent operations on the user account

#### Default Password
- **Format**: Randomly generated secure password
- **Purpose**: Temporary password for initial user login
- **Security**: User must change password on first login
- **Expiration**: Password expires after first use

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Name is empty or null | "Name cannot be empty" | Provide a valid display name |
| Email is invalid format | "Invalid email format" | Ensure email follows user@domain.com format |
| Email already exists | "Unable to create user account. Please verify that the email address is valid and not already in use" | Use a different email address or check for existing users |
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
**Cause**: Business logic violations or conflicts
**Common Scenarios**:
- Email address already exists in Azure AD
- Email domain not verified in tenant
- Invalid tenant configuration
- Insufficient permissions for user creation

**Resolution**: Check for existing users, verify domain configuration, and confirm permissions

**Improved Error Message**: "Unable to create user account. Please verify that the email address is valid and not already in use. Check that your Azure AD application has the required permissions (User.ReadWrite.All)."

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

### Example 1: Basic User Creation

**Input**:
```
Name: "Alice Johnson"
Email: "alice.johnson@company.com"
Mobile Number: "+1-555-987-6543"
Extra Fields: []
```

**Output**:
```json
{
  "User Id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "Default Password": "TempPass123!"
}
```

**Result**: New user account created with basic information

### Example 2: User Creation with Extra Fields

**Input**:
```
Name: "Bob Wilson"
Email: "bob.wilson@company.com"
Mobile Number: "+1-555-456-7890"
Extra Fields: [
  {"key": "Job Title", "value": "Senior Developer"},
  {"key": "Office Location", "value": "New York Office"},
  {"key": "Given Name", "value": "Bob"},
  {"key": "Surname", "value": "Wilson"}
]
```

**Output**:
```json
{
  "User Id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
  "Default Password": "SecureTemp456#"
}
```

**Result**: User account created with additional profile information

### Example 3: Retry Scenario

**Input**:
```
Name: "Carol Davis"
Email: "carol.davis@company.com"
Mobile Number: "+1-555-321-0987"
Extra Fields: []
```

**Initial Result**: System error due to temporary Azure AD service issue
**Retry Result**: Successful creation after automatic retry
**Final Output**: User account created successfully

## Business Rules

1. **Unique Email Requirement**: Each user must have a unique email address within the Azure AD tenant
2. **Password Policy**: Generated passwords must meet Azure AD password complexity requirements
3. **Force Password Change**: All new users must change their password on first login
4. **Account Activation**: New accounts are created in an active state by default
5. **Mail Nickname Generation**: Mail nickname is automatically generated from the display name
6. **License Assignment**: No licenses are automatically assigned - must be done separately

## Limitations

1. **Tenant Limits**: Subject to Azure AD tenant user limits
2. **Rate Limiting**: Microsoft Graph API rate limits apply (approximately 3000 requests per 5 minutes)
3. **Email Domain**: Email domain must be verified in the Azure AD tenant
4. **Character Restrictions**: Display names cannot contain certain special characters
5. **Password Complexity**: Generated passwords must meet tenant password policy requirements

## Best Practices

### 1. Email Address Management
- Use consistent email naming conventions
- Verify email domains are configured in Azure AD
- Check for existing users before creation

### 2. Extra Fields Usage
- Use extra fields to populate user profiles completely
- Maintain consistency in job titles and office locations
- Validate extra field values before submission

### 3. Error Handling
- Implement proper error handling for duplicate email scenarios
- Use retry logic for transient failures
- Log creation events for audit purposes

### 4. Security Considerations
- Securely communicate temporary passwords to users
- Ensure users change passwords on first login
- Monitor for successful account activations

## Common Use Cases

### 1. Employee Onboarding
```
Scenario: New employee joins the company
Action: Create account with full profile information
Result: Ready-to-use account with temporary password
```

### 2. Bulk User Creation
```
Scenario: Multiple users need accounts created
Action: Create accounts individually with retry support
Result: All accounts created with unique credentials
```

### 3. Service Account Creation
```
Scenario: Application needs a service account
Action: Create account with minimal profile information
Result: Service account ready for application use
```

## Related Catalog Requests

- [Update Account](/pages/UpdateAccount.md) - Modify existing user account information
- [Get User Details](/pages/GetUserDetails.md) - Retrieve user account information
- [Reset Password](/pages/ResetPassword.md) - Generate new temporary password
- [Activate or Deactivate Account](/pages/ActivateOrDeactivateAccount.md) - Enable/disable user accounts

## Technical Implementation

### Helper Class
- **Class**: CreateAccountHelper
- **Package**: app.krista.extensions.cyber_security.iam.azureAD.catalog.helper
- **Validation**: Input parameter validation and business rule enforcement
- **Service**: Delegates to AccountImpl.createUser() method

### Telemetry Metrics
- **USER_CREATION_INITIATED**: Tracks when user creation process starts
- **USER_CREATION_COMPLETED**: Tracks successful user creation
- **USER_CREATION_FAILED**: Tracks failed user creation attempts
- **EXTRA_FIELDS_PROCESSED**: Tracks usage of extra fields

## Troubleshooting

### User Creation Fails with "Email Already Exists"
**Cause**: Another user account already uses the specified email address
**Solution**: 
1. Check existing users in Azure AD
2. Use a different email address
3. Consider updating the existing user instead

### Generated Password Doesn't Meet Requirements
**Cause**: Tenant password policy is more restrictive than default generation
**Solution**:
1. Review tenant password policy settings
2. Ensure password generation meets all requirements
3. Contact Azure AD administrator if policy needs adjustment

### Extra Fields Not Applied
**Cause**: Invalid extra field keys or values
**Solution**:
1. Verify supported extra field keys
2. Check for typos in field names
3. Ensure values meet field-specific requirements

## See Also

- [Extension Configuration](/pages/ExtensionConfiguration.md) - Setup and configuration
- [Authentication](/pages/authentication.md) - Authentication requirements
- [Troubleshooting Guide](/pages/TroubleshootingGuide.md) - Error resolution and troubleshooting
- [Microsoft Graph User API](https://docs.microsoft.com/en-us/graph/api/user-post-users) - Official API documentation
