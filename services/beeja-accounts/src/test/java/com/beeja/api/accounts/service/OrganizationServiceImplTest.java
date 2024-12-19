package com.beeja.api.accounts.service;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.OrganizationExceptions;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.serviceImpl.OrganizationServiceImpl;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class OrganizationServiceImplTest {

  @Mock private OrganizationRepository organizationRepository;

  @Mock private UserRepository userRepository;

  @Mock private RolesRepository roleRepository;

  @Mock private EmployeeFeignClient employeeFeignClient;

  @Mock private FeatureToggleRepository featureToggleRepository;

  @InjectMocks private OrganizationServiceImpl organizationService;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetAllOrganizations() {
    // Arrange
    when(organizationRepository.findAll()).thenThrow(new RuntimeException("DB_ERROR"));

    // Act and Assert
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              organizationService.getAllOrganizations();
            });

    // Verify the exception message
    String expectedMessage =
        "DB_ERROR,UNABLE_TO_FETCH_DETAILS,Error Encountered in Fetching Organizations";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testCreateOrganization() throws Exception {
    // Arrange
    Role role1 = new Role("1", "ROLE_SUPER_ADMIN", null, Set.of("READ_EMPLOYEE"), null);

    Organization organization = new Organization();
    organization.setEmail("test@gmail.com");
    organization.setEmailDomain("abcd@gmail.com");

    when(organizationRepository.findByEmailDomain(anyString())).thenReturn(null);
    when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

    User user = new User();
    user.setRoles(Set.of(role1));
    when(roleRepository.findByName(anyString())).thenReturn(role1);

    when(userRepository.save(any(User.class))).thenReturn(user);

    doNothing().when(employeeFeignClient).createEmployee(any(User.class));

    // Act and Assert
    try {
      organizationService.createOrganization(organization);
    } catch (Exception e) {
      String expectedMessage = "API_ERROR,RESOURCE_CREATING_ERROR,Error in Creating Organization";
      String actualMessage = e.getMessage();

      assertTrue(actualMessage.contains(expectedMessage));
    }
  }

  @Test
  void testCreate_error() throws Exception {
    // Arrange
    Organization organization = new Organization();
    organization.setId("1");
    organization.setName("tac");
    organization.setEmail("abcd@gmail.com");

    OrganizationResponse organizationResponse = new OrganizationResponse();
    organizationResponse.setId("1");
    organizationResponse.setName("tac");
    organizationResponse.setEmail("abcd@gmail.com");
    organizationResponse.setEmailDomain("gmail.com");

    when(organizationRepository.findByEmailDomain(anyString())).thenReturn(organizationResponse);
    // Act & Assert
    assertThrows(Exception.class, () -> organizationService.createOrganization(organization));
  }

  @Test
  void testCreate_error_role_super_admin() {

    // Arrange
    Organization organization = new Organization();
    organization.setEmail("test@gmail.com");
    organization.setEmailDomain("abcd@gmail.com");

    Mockito.when(organizationRepository.findByEmailDomain(Mockito.anyString())).thenReturn(null);
    Mockito.when(organizationRepository.save(Mockito.any(Organization.class)))
        .thenReturn(organization);

    User user = new User();
    when(roleRepository.findByName(anyString())).thenReturn(null);

    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
    // Act & Assert
    assertThrows(Exception.class, () -> organizationService.createOrganization(organization));
  }

  @Test
  public void testGetAllUsersByOrganizationId() throws Exception {
    // Arrange
    String organizationId = "1";

    // Mock the organization repository to return empty (organization not found)
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

    // Act and Assert
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              organizationService.getAllUsersByOrganizationId(organizationId);
            });

    // Verify the exception message
    String expectedMessage =
        "DB_ERROR,UNABLE_TO_FETCH_DETAILS,No Organization Found with provided Id";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void testDeleteOwnOrganization_shouldThrowBadRequestException() throws Exception {
    // Arrange
    String organizationId = "org123"; // The organization ID the user is trying to delete
    Organization organization = new Organization();
    organization.setId(organizationId);
    organization.setName("My Own Organization");

    // Mock static method for logged-in user's organization
    mockStatic(UserContext.class);
    when(UserContext.getLoggedInUserOrganization()).thenReturn(organization);

    when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));

    // Act & Assert
    BadRequestException exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              organizationService.deleteOrganizationById(organizationId);
            });

    // Assert the exception message
    assertEquals(
        "AUTHORIZATION_ERROR,CANNOT_DELETE_SELF_ORGANIZATION,Cannot delete your own organization",
        exception.getMessage());
  }

  @Test
  public void testDeleteOrganizationById_ExceptionThrown() {
    // Arrange
    String organizationId = "123";
    Mockito.when(organizationRepository.findById(organizationId))
        .thenThrow(new OrganizationExceptions("Test exception"));
    NullPointerException exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              organizationService.deleteOrganizationById(organizationId);
            });
  }
}
