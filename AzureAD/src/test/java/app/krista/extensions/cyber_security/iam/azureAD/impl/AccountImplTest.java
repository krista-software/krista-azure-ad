/*
 * Azure Ad Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.cyber_security.iam.azureAD.impl;

import app.krista.extensions.cyber_security.iam.azureAD.catalog.entities.Account;
import app.krista.extensions.cyber_security.iam.azureAD.impl.connectors.GraphServiceClientProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountImpl service class.
 * Tests core public methods with happy path, edge cases, and error scenarios.
 *
 * Note: This test focuses on business logic validation and mocks all external dependencies
 * including Microsoft Graph API clients and Krista KSDK framework components.
 */
public class AccountImplTest {

    @Mock
    private GraphServiceClientProvider mockProvider;

    @Mock
    private GraphServiceClient mockGraphServiceClient;

    @Mock
    private UserCollectionRequestBuilder mockUsersRequestBuilder;

    @Mock
    private UserCollectionRequest mockUserCollectionRequest;

    @Mock
    private UserRequestBuilder mockUserRequestBuilder;

    @Mock
    private UserRequest mockUserRequest;

    @Mock
    private GroupCollectionRequestBuilder mockGroupsRequestBuilder;

    @Mock
    private GroupCollectionRequest mockGroupCollectionRequest;

    @Mock
    private DirectoryObjectCollectionWithReferencesPage mockDirectoryObjectCollectionWithReferencesPage;

    @Mock
    private DirectoryObjectCollectionWithReferencesRequestBuilder mockMemberOfRequestBuilder;



    @Mock
    private UserCollectionPage mockUserCollectionPage;

    @Mock
    private DirectoryObjectCollectionReferenceRequest mockDirectoryObjectCollectionReferenceRequest;

    private AccountImpl accountImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountImpl = new AccountImpl(mockProvider);

        // Setup common mock behavior
        when(mockProvider.getGraphServiceClient()).thenReturn(mockGraphServiceClient);
        when(mockGraphServiceClient.users()).thenReturn(mockUsersRequestBuilder);
        when(mockGraphServiceClient.groups()).thenReturn(mockGroupsRequestBuilder);
    }

    // Helper method to create test maps for extra fields
    private Map<String, Object> createExtraField(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("value", value);
        return map;
    }

    // ========== User Creation Tests ==========

    @Test
    public void testCreateUserSuccessfully() {
        // Arrange
        String name = "John Doe";
        String email = "john.doe@example.com";
        String mobileNumber = "+1234567890";
        java.util.List<Map<String, Object>> extraFields = Arrays.asList(
            createExtraField("Job Title", "Software Engineer"),
            createExtraField("Given Name", "John")
        );

        User createdUser = new User();
        createdUser.id = "user-123";
        createdUser.displayName = name;
        createdUser.mail = email;

        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.post(any(User.class))).thenReturn(createdUser);

        // Act
        Map<String, Object> result = accountImpl.createUser(name, email, mobileNumber, extraFields);

        // Assert
        assertNotNull(result);
        assertEquals("user-123", result.get("User Id"));
        assertNotNull(result.get("Default Password"));
        assertEquals(12, ((String) result.get("Default Password")).length());

        verify(mockUserCollectionRequest).post(argThat(user ->
            user.displayName.equals(name) &&
            user.mail.equals(email) &&
            user.userPrincipalName.equals(email) &&
            user.mobilePhone.equals(mobileNumber) &&
            user.accountEnabled == true &&
            user.mailNickname.equals("John") &&
            user.passwordProfile != null &&
            user.passwordProfile.forceChangePasswordNextSignIn == true &&
            user.jobTitle.equals("Software Engineer") &&
            user.givenName.equals("John")
        ));
    }

    @Test
    public void testCreateUserWithMinimalFields() {
        // Arrange
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        String mobileNumber = "+9876543210";

        User createdUser = new User();
        createdUser.id = "user-456";

        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.post(any(User.class))).thenReturn(createdUser);

        // Act
        Map<String, Object> result = accountImpl.createUser(name, email, mobileNumber, null);

        // Assert
        assertNotNull(result);
        assertEquals("user-456", result.get("User Id"));
        assertNotNull(result.get("Default Password"));

        verify(mockUserCollectionRequest).post(argThat(user ->
            user.displayName.equals(name) &&
            user.mail.equals(email) &&
            user.mobilePhone.equals(mobileNumber) &&
            user.mailNickname.equals("Jane")
        ));
    }

    @Test
    public void testCreateUserHandlesSingleWordName() {
        // Arrange
        String name = "Madonna";
        String email = "madonna@example.com";
        String mobileNumber = "+1111111111";

        User createdUser = new User();
        createdUser.id = "user-789";

        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.post(any(User.class))).thenReturn(createdUser);

        // Act
        Map<String, Object> result = accountImpl.createUser(name, email, mobileNumber, null);

        // Assert
        assertNotNull(result);
        verify(mockUserCollectionRequest).post(argThat(user ->
            user.mailNickname.equals("Madonna")
        ));
    }

    @Test
    public void testCreateUserThrowsExceptionWhenCreationFails() {
        // Arrange
        String name = "Test User";
        String email = "test@example.com";
        String mobileNumber = "+1234567890";

        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.post(any(User.class))).thenThrow(new ClientException("User already exists", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.createUser(name, email, mobileNumber, null);
        });
    }

    @Test
    public void testCreateUserProcessesAllSupportedExtraFields() {
        // Arrange
        String name = "Test User";
        String email = "test@example.com";
        String mobileNumber = "+1234567890";
        java.util.List<Map<String, Object>> extraFields = Arrays.asList(
            createExtraField("Given Name", "TestGiven"),
            createExtraField("Job Title", "TestJob"),
            createExtraField("mail", "test.mail@example.com"),
            createExtraField("Office Location", "TestOffice"),
            createExtraField("Surname", "TestSurname"),
            createExtraField("Unknown Field", "ShouldBeIgnored")
        );

        User createdUser = new User();
        createdUser.id = "user-extra";

        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.post(any(User.class))).thenReturn(createdUser);

        // Act
        accountImpl.createUser(name, email, mobileNumber, extraFields);

        // Assert
        verify(mockUserCollectionRequest).post(argThat(user ->
            user.givenName.equals("TestGiven") &&
            user.jobTitle.equals("TestJob") &&
            user.mail.equals("test.mail@example.com") &&
            user.officeLocation.equals("TestOffice") &&
            user.surname.equals("TestSurname")
        ));
    }

    // ========== Password Profile Tests ==========

    @Test
    public void testGetPasswordProfile() {
        // Act
        PasswordProfile profile = accountImpl.getPasswordProfile();

        // Assert
        assertNotNull(profile);
        assertNotNull(profile.password);
        assertTrue(profile.forceChangePasswordNextSignIn);
        assertEquals(12, profile.password.length());

        // Verify password format: 1 uppercase + 7 digits + 4 alphanumeric
        char firstChar = profile.password.charAt(0);
        assertTrue(Character.isUpperCase(firstChar));

        for (int i = 1; i <= 7; i++) {
            assertTrue(Character.isDigit(profile.password.charAt(i)));
        }
    }

    @Test
    public void testGetPasswordProfileGeneratesDifferentPasswords() {
        // Act
        PasswordProfile profile1 = accountImpl.getPasswordProfile();
        PasswordProfile profile2 = accountImpl.getPasswordProfile();

        // Assert
        assertNotEquals(profile1.password, profile2.password);
    }

    // ========== User Deletion Tests ==========

    @Test
    public void testDeleteUserSuccessfully() {
        // Arrange
        String email = "user@example.com";

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.delete()).thenReturn(null);

        // Act
        Boolean result = accountImpl.deleteUser(email);

        // Assert
        assertTrue(result);
        verify(mockUserRequest).delete();
    }

    @Test
    public void testDeleteUserThrowsExceptionWhenDeletionFails() {
        // Arrange
        String email = "nonexistent@example.com";

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.delete()).thenThrow(new ClientException("User not found", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.deleteUser(email);
        });
    }

    // ========== Account Activation Tests ==========

    @Test
    public void testActivateAccountSuccessfully() {
        // Arrange
        String userMailOrID = "user@example.com";
        Boolean deactivate = false;

        when(mockGraphServiceClient.users(userMailOrID)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenReturn(null);

        // Act
        Boolean result = accountImpl.activateOrDeactivateAccount(userMailOrID, deactivate);

        // Assert
        assertTrue(result);
        verify(mockUserRequest).patch(argThat(user -> user.accountEnabled == true));
    }

    @Test
    public void testDeactivateAccountSuccessfully() {
        // Arrange
        String userMailOrID = "user@example.com";
        Boolean deactivate = true;

        when(mockGraphServiceClient.users(userMailOrID)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenReturn(null);

        // Act
        Boolean result = accountImpl.activateOrDeactivateAccount(userMailOrID, deactivate);

        // Assert
        assertFalse(result);
        verify(mockUserRequest).patch(argThat(user -> user.accountEnabled == false));
    }

    @Test
    public void testActivateAccountThrowsExceptionWhenActivationFails() {
        // Arrange
        String userMailOrID = "user@example.com";
        Boolean deactivate = false;

        when(mockGraphServiceClient.users(userMailOrID)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenThrow(new ClientException("User not found", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.activateOrDeactivateAccount(userMailOrID, deactivate);
        });
    }

    // ========== User Retrieval Tests ==========

    @Test
    public void testGetUserSuccessfully() {
        // Arrange
        String userId = "user-123";
        User user = new User();
        user.id = userId;
        user.displayName = "Test User";
        user.mail = "test@example.com";
        user.mobilePhone = "+1234567890";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.get()).thenReturn(user);

        // Act
        Account result = accountImpl.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id);
        assertEquals("Test User", result.name);
        assertEquals("test@example.com", result.email);
        assertEquals("+1234567890", result.mobileNumber);
    }

    @Test
    public void testGetUserThrowsExceptionWhenUserNotFound() {
        // Arrange
        String userId = "nonexistent-user";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.get()).thenThrow(new ClientException("User not found", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.getUser(userId);
        });
    }

    // ========== Group Operations Tests ==========

    @Test
    public void testGetAllGroupsSuccessfully() {
        // Arrange
        Group group1 = new Group();
        group1.id = "group-1";
        group1.mail = "group1@example.com";

        Group group2 = new Group();
        group2.id = "group-2";
        group2.mail = "group2@example.com";

        Group group3 = new Group();
        group3.id = "group-3";
        group3.mail = null; // Should be excluded

        GroupCollectionPage groupPage = mock(GroupCollectionPage.class);
        when(groupPage.getCurrentPage()).thenReturn(Arrays.asList(group1, group2, group3));
        when(groupPage.getNextPage()).thenReturn(null);

        when(mockGroupsRequestBuilder.buildRequest()).thenReturn(mockGroupCollectionRequest);
        when(mockGroupCollectionRequest.get()).thenReturn(groupPage);

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getAllGroups();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // group3 should be excluded due to null mail

        Map<String, String> firstGroup = result.get(0);
        assertEquals("group1@example.com", firstGroup.get("Group Mail Id"));
        assertEquals("group-1", firstGroup.get("Group Id"));

        Map<String, String> secondGroup = result.get(1);
        assertEquals("group2@example.com", secondGroup.get("Group Mail Id"));
        assertEquals("group-2", secondGroup.get("Group Id"));
    }

    @Test
    public void testGetAllGroupsReturnsEmptyListWhenExceptionOccurs() {
        // Arrange
        when(mockGroupsRequestBuilder.buildRequest()).thenReturn(mockGroupCollectionRequest);
        when(mockGroupCollectionRequest.get()).thenThrow(new RuntimeException("Network error"));

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getAllGroups();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ========== User Update Tests ==========

    @Test
    public void testUpdateUserSuccessfully() {
        // Arrange
        String name = "Updated Name";
        String mobileNumber = "+9999999999";
        String email = "user@example.com";
        java.util.List<Map<String, Object>> extraFields = Arrays.asList(
            createExtraField("Job Title", "Senior Engineer")
        );

        User updatedUser = new User();
        updatedUser.id = "user-123";
        updatedUser.displayName = name;
        updatedUser.mobilePhone = mobileNumber;
        updatedUser.mail = email;

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenReturn(null);
        when(mockUserRequest.get()).thenReturn(updatedUser);

        // Act
        Account result = accountImpl.updateUser(name, mobileNumber, email, extraFields);

        // Assert
        assertNotNull(result);
        assertEquals("user-123", result.id);
        verify(mockUserRequest).patch(argThat(user ->
            user.displayName.equals(name) &&
            user.mobilePhone.equals(mobileNumber) &&
            user.jobTitle.equals("Senior Engineer")
        ));
    }

    @Test
    public void testUpdateUserWithBlankFieldsIgnoresThem() {
        // Arrange
        String name = "";
        String mobileNumber = "";
        String email = "user@example.com";

        User updatedUser = new User();
        updatedUser.id = "user-123";
        updatedUser.mail = email;

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenReturn(null);
        when(mockUserRequest.get()).thenReturn(updatedUser);

        // Act
        Account result = accountImpl.updateUser(name, mobileNumber, email, null);

        // Assert
        assertNotNull(result);
        verify(mockUserRequest).patch(argThat(user ->
            user.displayName == null &&
            user.mobilePhone == null
        ));
    }

    @Test
    public void testUpdateUserThrowsExceptionWhenUserNotFound() {
        // Arrange
        String name = "Test User";
        String mobileNumber = "+1234567890";
        String email = "nonexistent@example.com";

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenThrow(new ClientException("User not found", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.updateUser(name, mobileNumber, email, null);
        });
    }

    @Test
    public void testUpdateUserWithExtraFields() {
        // Arrange
        String name = "Test User";
        String mobileNumber = "+1234567890";
        String email = "user@example.com";
        java.util.List<Map<String, Object>> extraFields = Arrays.asList(
            createExtraField("Given Name", "UpdatedGiven"),
            createExtraField("Surname", "UpdatedSurname"),
            createExtraField("Office Location", "New Office")
        );

        User updatedUser = new User();
        updatedUser.id = "user-123";
        updatedUser.mail = email;

        when(mockGraphServiceClient.users(email)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.patch(any(User.class))).thenReturn(null);
        when(mockUserRequest.get()).thenReturn(updatedUser);

        // Act
        accountImpl.updateUser(name, mobileNumber, email, extraFields);

        // Assert
        verify(mockUserRequest).patch(argThat(user ->
            user.givenName.equals("UpdatedGiven") &&
            user.surname.equals("UpdatedSurname") &&
            user.officeLocation.equals("New Office")
        ));
    }

    // ========== Get User For Groups Tests ==========
    // Note: addAccountToGroup, removeAccountFromGroup, and resetPassword methods
    // require complex Microsoft Graph SDK mocking that is difficult to test in unit tests.
    // These are better suited for integration tests with actual Azure AD instances.

    @Test
    public void testGetUserForGroupsSuccessfully() {
        // Arrange
        String userId = "user@example.com";

        User user = new User();
        user.id = "user-id-123";
        user.mail = userId;
        user.displayName = "Test User";

        Group group1 = new Group();
        group1.id = "group-1";
        group1.mail = "group1@example.com";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.get()).thenReturn(user);

        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(Arrays.asList(group1));

        // Act
        Map<String, Object> result = accountImpl.getUserForGroups(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.get("User Mail Id"));
        assertEquals("Test User", result.get("User Name"));
        assertEquals("user-id-123", result.get("User Id"));

        @SuppressWarnings("unchecked")
        java.util.List<Map<String, String>> groupList = (java.util.List<Map<String, String>>) result.get("Group List");
        assertNotNull(groupList);
        assertEquals(1, groupList.size());
        assertEquals("group1@example.com", groupList.get(0).get("Group Mail Id"));
    }

    @Test
    public void testGetUserForGroupsWithNoGroups() {
        // Arrange
        String userId = "user@example.com";

        User user = new User();
        user.id = "user-id-123";
        user.mail = userId;
        user.displayName = "Test User";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.get()).thenReturn(user);

        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(new ArrayList<>());

        // Act
        Map<String, Object> result = accountImpl.getUserForGroups(userId);

        // Assert
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, String>> groupList = (java.util.List<Map<String, String>>) result.get("Group List");
        assertNotNull(groupList);
        assertTrue(groupList.isEmpty());
    }

    @Test
    public void testGetUserForGroupsThrowsExceptionWhenUserNotFound() {
        // Arrange
        String userId = "nonexistent@example.com";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.buildRequest()).thenReturn(mockUserRequest);
        when(mockUserRequest.get()).thenThrow(new ClientException("User not found", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.getUserForGroups(userId);
        });
    }


    // ========== Get User Groups Tests ==========

    @Test
    public void testGetUserGroupsSuccessfully() {
        // Arrange
        String userId = "user@example.com";

        Group group1 = new Group();
        group1.id = "group-1";
        group1.mail = "group1@example.com";

        Group group2 = new Group();
        group2.id = "group-2";
        group2.mail = "group2@example.com";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(Arrays.asList(group1, group2));

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getUserGroups(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("group1@example.com", result.get(0).get("Group Mail Id"));
        assertEquals("group-1", result.get(0).get("Group Id"));
        assertEquals("group2@example.com", result.get(1).get("Group Mail Id"));
        assertEquals("group-2", result.get(1).get("Group Id"));
    }

    @Test
    public void testGetUserGroupsWithNullMail() {
        // Arrange
        String userId = "user@example.com";

        Group group1 = new Group();
        group1.id = "group-1";
        group1.mail = null;

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(Arrays.asList(group1));

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getUserGroups(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NA", result.get(0).get("Group Mail Id"));
        assertEquals("group-1", result.get(0).get("Group Id"));
    }

    @Test
    public void testGetUserGroupsReturnsEmptyListWhenNoGroups() {
        // Arrange
        String userId = "user@example.com";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(new ArrayList<>());

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getUserGroups(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUserGroupsReturnsEmptyListOnException() {
        // Arrange
        String userId = "user@example.com";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenThrow(new RuntimeException("Network error"));

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getUserGroups(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUserGroupsFiltersNonGroupDirectoryObjects() {
        // Arrange
        String userId = "user@example.com";

        Group group1 = new Group();
        group1.id = "group-1";
        group1.mail = "group1@example.com";

        DirectoryObject nonGroupObject = new DirectoryObject();
        nonGroupObject.id = "other-object";

        when(mockGraphServiceClient.users(userId)).thenReturn(mockUserRequestBuilder);
        when(mockUserRequestBuilder.memberOf()).thenReturn(mockMemberOfRequestBuilder);
        when(mockMemberOfRequestBuilder.buildRequest()).thenReturn(mockDirectoryObjectCollectionReferenceRequest);
        when(mockDirectoryObjectCollectionReferenceRequest.get()).thenReturn(mockDirectoryObjectCollectionWithReferencesPage);
        when(mockDirectoryObjectCollectionWithReferencesPage.getCurrentPage()).thenReturn(Arrays.asList(group1, nonGroupObject));

        // Act
        java.util.List<Map<String, String>> result = accountImpl.getUserGroups(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Only the group should be included
        assertEquals("group1@example.com", result.get(0).get("Group Mail Id"));
    }

    // ========== Get User Collection Page Tests ==========

    @Test
    public void testGetUserCollectionPageSuccessfully() {
        // Arrange
        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.get()).thenReturn(mockUserCollectionPage);

        // Act
        UserCollectionPage result = accountImpl.getUserCollectionPage();

        // Assert
        assertNotNull(result);
        assertEquals(mockUserCollectionPage, result);
        verify(mockUserCollectionRequest).get();
    }

    @Test
    public void testGetUserCollectionPageThrowsExceptionOnError() {
        // Arrange
        when(mockUsersRequestBuilder.buildRequest()).thenReturn(mockUserCollectionRequest);
        when(mockUserCollectionRequest.get()).thenThrow(new ClientException("Network error", null));

        // Act & Assert
        assertThrows(ClientException.class, () -> {
            accountImpl.getUserCollectionPage();
        });
    }
}
