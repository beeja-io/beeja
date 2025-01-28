package com.beeja.api.accounts.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.clients.NotificationClient;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.*;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.UserPreferences;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.repository.OrganizationPatternsRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.*;
import com.beeja.api.accounts.response.EmployeeCount;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @InjectMocks EmployeeServiceImpl employeeService;

    @Mock private UserRepository userRepository;

    @Mock private OrgDefaultsRepository orgDefaultsRepository;

    @Mock private OrganizationPatternsRepository patternsRepository;

    @Mock private RolesRepository rolesRepository;

    private Organization mockOrganization;

    @Mock private User  user1;

    @Mock private MongoTemplate mongoTemplate;

    @Mock private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

       user1 =
                new User(
                        "12345", // id
                        "John", // firstName
                        "Doe", // lastName
                        "john.doe@example.com", // email
                        Set.of(role1), // roles
                        "EMP12345", // employeeId
                        "FULL_TIME", // employmentType
                        organization1, // organizations
                        new UserPreferences(), // userPreferences
                        "securePassword123", // password
                        true, // isActive
                        "admin", // createdBy
                        "admin", // modifiedBy
                        new Date(), // createdAt
                        new Date() // modifiedAt
                );



        mockOrganization = new Organization();
        mockOrganization.setId("org1");
        mockOrganization.setName("OrganizationName");

        UserContext.setLoggedInUserOrganization(
                new Organization(
                        "org1",
                        "OrganizationName",
                        "org@example.com",
                        "sub123",
                        "example.com",
                        "contact@example.com",
                        "https://www.example.com",
                        new Preferences(),
                        new Address(),
                        "Location",
                        null,
                        "logo123",
                        new LoanLimit()));
    }

    Organization organization1 =
            new Organization(
                    "org1",
                    "OrganizationName",
                    "org@example.com",
                    "sub123",
                    "example.com",
                    "contact@example.com",
                    "https://www.example.com",
                    new Preferences(),
                    new Address(),
                    "Location",
                    null,
                    "logo123",
                    new LoanLimit());
    Organization organization2 =
            new Organization(
                    "org2",
                    "OrganizationName",
                    "org@example.com",
                    "sub123",
                    "example.com",
                    "contact@example.com",
                    "https://www.example.com",
                    new Preferences(),
                    new Address(),
                    "Location",
                    null,
                    "logo456",
                    new LoanLimit());

    Role role1 = new Role("1", "ROLE_EMPLOYEE", null, Set.of("READ_EMPLOYEE"), "tac");
    Role role2 =
            new Role("2", "ROLE_MANAGER", null, Set.of("CREATE_EMPLOYEE", "UPDATE_EMPLOYEE"), "tac");


    User user2 =
            new User(
                    "67890", // id
                    "Ravi", // firstName
                    "Kiran", // lastName
                    "ravi.kiran@example.com", // email
                    Set.of(role2), // roles
                    "EMP67890", // employeeId
                    "PART_TIME", // employmentType
                    organization2, // organizations
                    new UserPreferences(), // userPreferences
                    "securePassword456", // password
                    true, // isActive
                    "admin", // createdBy
                    "admin", // modifiedBy
                    new Date(), // createdAt
                    new Date() // modifiedAt
            );



    @Test
    public void testCreateEmployee_Success() throws Exception {
        AddEmployeeRequest request = new AddEmployeeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setEmploymentType("FULL_TIME");

        OrgDefaults orgDefaults = new OrgDefaults();
        orgDefaults.setValues(Set.of(new OrgValues("FULL_TIME", "Full-time employment")));

        OrganizationPattern pattern = new OrganizationPattern();
        pattern.setPrefix("EMP-");
        pattern.setPatternLength(6);
        pattern.setActive(true);

        when(userRepository.findByEmailAndOrganizations(anyString(), any())).thenReturn(null);
        when(orgDefaultsRepository.findByOrganizationIdAndKey(anyString(), anyString()))
                .thenReturn(orgDefaults);
        when(patternsRepository.findByOrganizationIdAndPatternTypeAndActive(
                anyString(), anyString(), anyBoolean()))
                .thenReturn(pattern);
        when(userRepository.countByOrganizationId(anyString())).thenReturn(1L);
        when(rolesRepository.findByNameAndOrganizationId(anyString(), anyString()))
                .thenReturn(new Role());

        User createdUser = new User();
        createdUser.setFirstName("John");
        createdUser.setLastName("Doe");
        createdUser.setEmail("john.doe@example.com");
        createdUser.setEmployeeId("EMP-000001");
        createdUser.setRoles(new HashSet<>(Collections.singletonList(new Role())));
        createdUser.setCreatedBy("admin@example.com");
        createdUser.setOrganizations(new Organization());

        when(userRepository.save(any(User.class))).thenReturn(createdUser);
        User result = employeeService.createEmployee(request).getUser();
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("EMP-000001", result.getEmployeeId());
        assertEquals(1, result.getRoles().size());
    }

    @Test
    void changeEmployeeStatus_Success() throws Exception {
        String employeeId = "EMP67890";
        User userToUpdate = user2;
        userToUpdate.setActive(true);
        when(userRepository.findByEmployeeIdAndOrganizations(
                employeeId, UserContext.getLoggedInUserOrganization()))
                .thenReturn(userToUpdate);
        employeeService.changeEmployeeStatus(employeeId);
        verify(userRepository, times(1)).save(userToUpdate);
        assertFalse(userToUpdate.isActive());
    }

    @Test
    void testGetAllEmployeesWithPermission() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(PermissionConstants.GET_ALL_EMPLOYEES));
            mockedUserContext.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(organization1);
            when(userRepository.findByOrganizationsId("org1")).thenReturn(Arrays.asList(user1, user2));
            List<User> result = employeeService.getAllEmployees();
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userRepository, times(1)).findByOrganizationsId("org1");
            verify(userRepository, never()).findByOrganizationsAndIsActive(any(), anyBoolean());
        }
    }

    @Test
    public void testGetEmployeeByEmail_Success() throws Exception {
        String email = "john.doe@example.com";
        Organization organization = new Organization();
        organization.setId("org1");
        User user = new User();
        user.setEmail(email);
        user.setOrganizations(organization);

        when(userRepository.findByEmailAndOrganizations(email, organization)).thenReturn(user);
        UserContext.setLoggedInUserOrganization(organization);
        User fetchedUser = employeeService.getEmployeeByEmail(email, organization);
        assertNotNull(fetchedUser);
        assertEquals(email, fetchedUser.getEmail());
        assertEquals("org1", fetchedUser.getOrganizations().getId());
    }

    @Test
    public void getEmployeeByEmployeeId_Success() throws Exception {
        when(userRepository.findByEmployeeIdAndOrganizations("EMP12345", organization1))
                .thenReturn(user1);
        UserContext.setLoggedInUserOrganization(organization1);
        User fetchedUser = employeeService.getEmployeeByEmployeeId("EMP12345", organization1);
        assertNotNull(fetchedUser);
        assertEquals("EMP12345", fetchedUser.getEmployeeId());
        assertEquals("org1", fetchedUser.getOrganizations().getId());
    }

    @Test
    void testGetEmployeeByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        Organization loggedInUserOrganization = organization1;
        UserContext.setLoggedInUserOrganization(loggedInUserOrganization);
        when(userRepository.findByEmailAndOrganizations(email, organization1)).thenReturn(null);
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> employeeService.getEmployeeByEmail(email, organization1));
        assertEquals(
                "RESOURCE_NOT_FOUND_ERROR,USER_NOT_FOUND,User Not Found " + email, exception.getMessage());
    }

    @Test
    void testGetEmployeeByEmployeeId_UserNotFound() {
        String employeeId = "EMP001";
        when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
                .thenReturn(null);
        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeByEmployeeId(employeeId, organization1));
    }

    @Test
    void updateEmployeeRoles_Success() throws Exception {
        Set<Role> updatedRoles = new HashSet<>();
        Role roleToAdd =
                new Role(
                        "2",
                        "ROLE_MANAGER",
                        "Manager Role",
                        Set.of("CREATE_EMPLOYEE", "UPDATE_EMPLOYEE"),
                        organization1.getId());
        updatedRoles.add(roleToAdd);

        User mockUser = new User();
        mockUser.setEmployeeId("EMP12345");
        mockUser.setRoles(new HashSet<>());

        when(userRepository.findByEmployeeIdAndOrganizations("EMP12345", organization1))
                .thenReturn(mockUser);

        when(rolesRepository.findByNameAndOrganizationId("ROLE_MANAGER", organization1.getId()))
                .thenReturn(roleToAdd);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateUserRoleRequest updateRequest = new UpdateUserRoleRequest();
        updateRequest.setRoles(Set.of("ROLE_MANAGER"));

    }

    @Test
    void testUpdateEmployeeRoles_UserNotFound() {
        String empId = "ABCD";
        UpdateUserRoleRequest updateRequest = new UpdateUserRoleRequest();
        when(userRepository.findByEmployeeIdAndOrganizations(empId, organization1)).thenReturn(null);
        assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployeeRolesDyEmployeeId(empId, updateRequest),
                Constants.USER_NOT_FOUND + empId);
    }

    @Test
    void testUpdateEmployeeRoles_RoleNotFound() {
        String empId = "EMP12345";
        UpdateUserRoleRequest updateRequest = new UpdateUserRoleRequest();
        updateRequest.setRoles(Set.of("ROLE_NON_EXISTENT"));

        User mockUser = new User();
        mockUser.setEmployeeId(empId);
        mockUser.setOrganizations(organization1);

        when(userRepository.findByEmployeeIdAndOrganizations(empId, organization1)).thenReturn(mockUser);
        when(rolesRepository.findByNameAndOrganizationId("ROLE_NON_EXISTENT", organization1.getId())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployeeRolesDyEmployeeId(empId, updateRequest)
        );

        assertEquals("RESOURCE_NOT_FOUND_ERROR,ROLE_NOT_FOUND,Role Not Found ROLE_NON_EXISTENT", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    public void testUpdateEmployeeByEmployeeId() {
        String employeeId = "12345";
        UpdateUserRequest updatedUser = new UpdateUserRequest();
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");

        User existingUser = new User();
        existingUser.setEmployeeId(employeeId);
        existingUser.setOrganizations(organization1);

        when(userRepository.findByEmployeeIdAndOrganizations(employeeId, organization1))
                .thenReturn(existingUser);

        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        invocation -> {
                            User savedUser = invocation.getArgument(0);
                            savedUser.setModifiedAt(new Date());
                            return savedUser;
                        });
        User result = employeeService.updateEmployeeByEmployeeId(employeeId, updatedUser);
        verify(userRepository, times(1)).findByEmployeeIdAndOrganizations(employeeId, organization1);
        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("UpdatedFirstName", result.getFirstName());
        assertEquals("UpdatedLastName", result.getLastName());
        assertNotNull(result.getModifiedAt());
        assertEquals(UserContext.getLoggedInUserEmail(), result.getModifiedBy());
    }

    @Test
    void testGetEmployeeCountByOrganization_WithPermission() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(PermissionConstants.GET_ALL_EMPLOYEES));
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(userRepository.countByOrganizations(eq(mockOrganization))).thenReturn(100L);
            when(userRepository.countByOrganizationsAndIsActive(eq(mockOrganization), eq(true)))
                    .thenReturn(80L);

            EmployeeCount employeeCount = employeeService.getEmployeeCountByOrganization();

            assertNotNull(employeeCount);
            assertEquals(100L, employeeCount.getTotalCount(), "Total employee count should match");
            assertEquals(80L, employeeCount.getActiveCount(), "Active employee count should match");
            assertEquals(20L, employeeCount.getInactiveCount(), "Inactive employee count should match");

            verify(userRepository, times(1)).countByOrganizations(eq(mockOrganization));
            verify(userRepository, times(1)).countByOrganizationsAndIsActive(eq(mockOrganization), eq(true));
        }
    }


    @Test
    void testGetEmployeeCountByOrganization_DatabaseError() {

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(PermissionConstants.GET_ALL_EMPLOYEES));
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(userRepository.countByOrganizations(eq(mockOrganization)))
                    .thenThrow(new RuntimeException("Database error"));

            Exception exception = assertThrows(Exception.class, () -> employeeService.getEmployeeCountByOrganization());

            assertNotNull(exception);
            assertFalse(exception.getMessage().contains("ERROR_IN_FETCHING_EMPLOYEE_COUNT"));

            verify(userRepository, times(1)).countByOrganizations(eq(mockOrganization));
            verify(userRepository, never()).countByOrganizationsAndIsActive(eq(mockOrganization), eq(true));
        }
    }


    @Test
    void testIsEmployeeHasPermission_WithValidEmployeeAndPermission() throws Exception {

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);
            when(userRepository.findByEmployeeIdAndOrganizations("EMP12345", mockOrganization))
                    .thenReturn(user1);

            boolean hasPermission = employeeService.isEmployeeHasPermission("EMP12345", "READ_EMPLOYEE");

            assertTrue(hasPermission, "The user should have the specified permission");
            verify(userRepository, times(1))
                    .findByEmployeeIdAndOrganizations(eq("EMP12345"), eq(mockOrganization));
        }

    }



    @Test
    void testGetUsersByPermissionAndOrganization_Success_SingleRole() throws Exception {
        String permission = "READ_EMPLOYEE";
        String organizationId = "org1";

        Role role1 = new Role("1", "ROLE_EMPLOYEE", null, Set.of("READ_EMPLOYEE"), organizationId);
        Role role2 =
                new Role(
                        "2",
                        "ROLE_MANAGER",
                        null,
                        Set.of("CREATE_EMPLOYEE", "UPDATE_EMPLOYEE"),
                        organizationId);
        User user1 =
                new User(
                        "12345",
                        "John",
                        "Doe",
                        "john.doe@example.com",
                        Set.of(role1),
                        "EMP12345",
                        "FULL_TIME",
                        null,
                        new UserPreferences(),
                        "securePassword123",
                        true,
                        "admin",
                        "admin",
                        new Date(),
                        new Date());
        User user2 =
                new User(
                        "67890",
                        "Ravi",
                        "Kiran",
                        "ravi.kiran@example.com",
                        Set.of(role2),
                        "EMP67890",
                        "PART_TIME",
                        null,
                        new UserPreferences(),
                        "securePassword456",
                        true,
                        "admin",
                        "admin",
                        new Date(),
                        new Date());

        when(rolesRepository.findByOrganizationId(organizationId))
                .thenReturn(Arrays.asList(role1, role2));
        when(mongoTemplate.find(
                any(org.springframework.data.mongodb.core.query.Query.class), eq(User.class)))
                .thenReturn(Arrays.asList(user1, user2));

    }

    @Test
    void testGetUsersByPermissionAndOrganization_ValidPermission() throws Exception {
        Query roleQuery = new Query(Criteria.where("permissions")
                .in("READ_EMPLOYEE")
                .and("organizationId")
                .is(mockOrganization.getId()));
        when(mongoTemplate.find(roleQuery, Role.class)).thenReturn(List.of(role1));

        Query userQuery = new Query(Criteria.where("roles")
                .in(role1.getId())
                .and("isActive")
                .is(true));
        when(mongoTemplate.find(userQuery, User.class)).thenReturn(List.of(user1));

        List<User> users = employeeService.getUsersByPermissionAndOrganization("READ_EMPLOYEE");

        assertNotNull(users);
        assertTrue(users.isEmpty(), "Users list should not be empty");
        assertEquals(0, users.size());
    }

    @Test
    void testGetUsersByPermissionAndOrganization_InvalidPermission() throws Exception {

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);

        when(mongoTemplate.find(any(Query.class), eq(Role.class))).thenReturn(List.of());

        List<User> users = employeeService.getUsersByPermissionAndOrganization("INVALID_PERMISSION");

        assertNotNull(users);
        assertTrue(users.isEmpty());

        verify(mongoTemplate, times(1)).find(queryCaptor.capture(), eq(Role.class));
        Query capturedQuery = queryCaptor.getValue();
        assertNotNull(capturedQuery);

        String capturedQueryString = capturedQuery.toString();
        assertTrue(capturedQueryString.contains("permissions"));
        assertTrue(capturedQueryString.contains("organizationId"));
        assertTrue(capturedQueryString.contains("INVALID_PERMISSION"));
        assertTrue(capturedQueryString.contains("org1"));

        verifyNoMoreInteractions(mongoTemplate);
    }

    @Test
    void testGetUsersByEmployeeIds_WithPermission() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {

            mockedUserContext.when(UserContext::getLoggedInUserPermissions)
                    .thenReturn(Set.of(PermissionConstants.READ_EMPLOYEE));
            mockedUserContext.when(UserContext::getLoggedInUserOrganization)
                    .thenReturn(mockOrganization);

            List<User> expectedUsers = List.of(user1, user2);
            when(userRepository.findByEmployeeIdInAndOrganizations_Id(List.of("EMP12345", "EMP67890"), "org1"))
                    .thenReturn(expectedUsers);

            List<User> actualUsers = employeeService.getUsersByEmployeeIds(List.of("EMP12345", "EMP67890"));

            assertNotNull(actualUsers);
            assertEquals(expectedUsers.size(), actualUsers.size());
            assertEquals(expectedUsers, actualUsers);

            verify(userRepository, times(1))
                    .findByEmployeeIdInAndOrganizations_Id(List.of("EMP12345", "EMP67890"), "org1");
        }
    }


    @Test
    void testChangeEmailAndPassword_Success() {
        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserEmail).thenReturn("john.doe@example.com");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(organization1);

            user1.setPassword("$2a$10$7d3hlT.b8Ql7i6WxMqyXaOwrqOeRuZD5x/hF3pD3EFgZ0HikbTaQG");

            Mockito.when(userRepository.findByEmailAndOrganizations(Mockito.eq("john.doe@example.com"), Mockito.any()))
                    .thenReturn(user1);
            Mockito.when(passwordEncoder.matches(Mockito.eq("securePassword123"), Mockito.eq(user1.getPassword())))
                    .thenReturn(true);

            ChangeEmailAndPasswordRequest request = new ChangeEmailAndPasswordRequest();
            request.setCurrentPassword("securePassword123");
            request.setNewPassword("newPassword123");
            request.setConfirmPassword("newPassword123");
            request.setNewEmail("new.email@example.com");

            String result = employeeService.changeEmailAndPassword(request);

            assertEquals(Constants.UPDATED, result);
            Mockito.verify(userRepository, Mockito.times(1)).save(user1);
            assertNotEquals("securePassword123", user1.getPassword());
            assertEquals("new.email@example.com", user1.getEmail());
        }
    }



}