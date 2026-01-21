# Azure Active Directory Extension

## Overview

The Azure Active Directory (Azure AD) Extension enables seamless integration with Microsoft's cloud-based identity and access management service. This extension allows you to manage user accounts, groups, and authentication within your Azure AD tenant directly from Krista.

Azure AD is Microsoft's enterprise identity service that provides single sign-on, multifactor authentication, and conditional access to guard against cybersecurity attacks. With this extension, you can automate user lifecycle management, group assignments, and access control operations.

## Key Features

✅ **User Account Management**
- Create, update, and delete user accounts
- Activate and deactivate accounts
- Reset user passwords with temporary credentials

✅ **Group Management**
- Add users to groups
- Remove users from groups
- Retrieve list of available groups

✅ **User Information Retrieval**
- Get detailed user information by ID or email
- Search and filter user accounts
- Access user profile data and group memberships

✅ **Secure Authentication**
- OAuth 2.0 authentication flow
- Service account and user account authentication modes
- Automatic token refresh and management

✅ **Enterprise Integration**
- Support for custom user fields (Job Title, Office Location, etc.)
- Retry mechanism for reliable operations
- Comprehensive error handling and validation

## Quick Start Guide

### 1. Prerequisites
- Azure AD tenant with administrative privileges
- Registered Azure AD application with appropriate permissions
- Client ID, Client Secret, and Tenant ID from your Azure application

### 2. Initial Setup
1. **[Create Azure AD Application](pages/CreatingAzureApp.md)** - Set up your Azure AD app registration
2. **[Configure Extension](pages/ExtensionConfiguration.md)** - Configure the extension with your Azure credentials
3. **[Set Up Authentication](pages/authentication.md)** - Configure OAuth 2.0 authentication

### 3. Start Using
Once configured, you can immediately begin using the catalog requests to manage your Azure AD environment.

## Documentation Structure

### 📋 Getting Started
- **[Extension Configuration](pages/ExtensionConfiguration.md)** - Complete setup and configuration guide
- **[Authentication](pages/authentication.md)** - OAuth 2.0 flows, scopes, and token management
- **[Creating Azure App](pages/CreatingAzureApp.md)** - Step-by-step Azure AD application setup

### 👥 Account Management
- **[Create Account](pages/CreateAccount.md)** - Create new user accounts with custom fields
- **[Update Account](pages/UpdateAccount.md)** - Modify existing user account information
- **[Delete Account](pages/DeleteAccount.md)** - Remove user accounts from Azure AD
- **[Activate or Deactivate Account](pages/ActivateOrDeactivateAccount.md)** - Enable/disable user accounts
- **[Reset Password](pages/ResetPassword.md)** - Generate temporary passwords for users
- **[Get User Details](pages/GetUserDetails.md)** - Retrieve detailed user information

### 🔍 Group Operations
- **[Add Account to Group](pages/AddAccountToGroup.md)** - Assign users to groups
- **[Remove Account from Group](pages/RemoveAccountFromGroup.md)** - Remove users from groups
- **[Get List of Groups](pages/GetListOfGroups.md)** - Retrieve available groups

### 📊 Entity Operations
- **[Account Search](pages/AccountSearch.md)** - Search and filter user accounts

### 🛠️ Support & Troubleshooting
- **[Troubleshooting Guide](pages/TroubleshootingGuide.md)** - Comprehensive error resolution guide

## Support & Resources

- **Extension Version**: 2.0.5
- **Krista Service APIs Java**: 1.0.102
- **Global Catalog Version**: 1.0.53
- **Appliance Release Version**: 3.4.2

### Additional Resources
- **[Release Notes](pages/release-notes.md)** - Version history and updates
- **[Microsoft Graph API Documentation](https://docs.microsoft.com/en-us/graph/)** - Official Azure AD API reference
- **[Azure AD Documentation](https://docs.microsoft.com/en-us/azure/active-directory/)** - Microsoft's Azure AD documentation

---

> **💡 Tip**: Start with the [Extension Configuration](pages/ExtensionConfiguration.md) guide to set up your connection, then explore the catalog requests based on your specific use cases.
