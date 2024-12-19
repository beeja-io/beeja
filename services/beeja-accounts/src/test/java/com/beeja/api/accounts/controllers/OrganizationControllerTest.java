package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.OrganizationExceptions;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrganizationControllerTest {

  @Mock private OrganizationService organizationService;
  @Mock private OrganizationRepository organizationRepository;
  @Mock private EmployeeFeignClient employeeFeignClient;

  @InjectMocks private OrganizationController organizationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  Organization organization1 =
      new Organization(
          "org1",
          "OrganizationName1",
          "org1@example.com",
          "sub123",
          "example.com",
          "contact1@example.com",
          "https://www.example.com",
          null,
          null,
          null,
          null,
          null,
          null);

  Organization organization2 =
      new Organization(
          "org2",
          "OrganizationName2",
          "org2@example.com",
          "sub123",
          "example.com",
          "contact2@example.com",
          "https://www.example.com",
          null,
          null,
          null,
          null,
          null,
          null);

  @Test
  void testGetAllOrganizations() throws Exception {
    // Arrange
    List<Organization> org = new ArrayList<>(Arrays.asList(organization1, organization2));
    when(organizationService.getAllOrganizations()).thenReturn(org);

    // Act
    ResponseEntity<?> responseEntity = organizationController.getAllOrganizations();

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }

  @Test
  public void testGetAllOrganizations_InternalServerError() throws Exception {
    // Arrange
    when(organizationService
            .getAllOrganizations()) // Assuming you meant organizationService instead of
        // organizationController
        .thenThrow(new RuntimeException("Internal server error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          organizationController.getAllOrganizations();
        });
  }

  @Test
  void testGetAllEmployeesByOrganizationId_Success() throws Exception {
    // Arrange
    String organizationId = "org123";
    List<User> users = Arrays.asList(new User(), new User());
    when(organizationService.getAllUsersByOrganizationId(organizationId)).thenReturn(users);

    // Act
    ResponseEntity<?> responseEntity =
        organizationController.getAllEmployeesByOrganizationId(organizationId);

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }

  @Test
  public void testGetAllEmployeesByOrganizationId_NotFound() throws Exception {
    // Arrange
    String organizationId = "org123";
    when(organizationService.getAllUsersByOrganizationId(organizationId))
        .thenThrow(new OrganizationExceptions("Organization not found"));

    // Act & Assert
    assertThrows(
        OrganizationExceptions.class,
        () -> {
          organizationController.getAllEmployeesByOrganizationId(organizationId);
        });
  }

  @Test
  public void testGetAllEmployeesByOrganizationId_InternalServerError() throws Exception {
    // Arrange
    String organizationId = "org123";
    when(organizationService.getAllUsersByOrganizationId(organizationId))
        .thenThrow(new RuntimeException("Internal server error"));

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          organizationController.getAllEmployeesByOrganizationId(organizationId);
        });
  }

  @Test
  void testCreateOrganization_Success() throws Exception {
    // Arrange
    Organization organization = new Organization();
    BindingResult bindingResult = mock(BindingResult.class);

    when(bindingResult.hasErrors()).thenReturn(false);
    when(organizationService.createOrganization(organization)).thenReturn(organization);

    // Act
    ResponseEntity<?> responseEntity =
        organizationController.createOrganization(organization, bindingResult);

    // Assert
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }

  @Test
  void testCreateOrganization_errormessages() throws Exception {
    // Arrange
    Organization organization = new Organization();
    BindingResult bindingResult = mock(BindingResult.class);

    // Simulate that there are errors
    when(bindingResult.hasErrors()).thenReturn(true);

    // Create a mock ObjectError
    ObjectError error = new ObjectError("organization", "Invalid organization data");

    // Mock the behavior of bindingResult to return the mock error
    when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));

    // Act & Assert
    Exception exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              organizationController.createOrganization(organization, bindingResult);
            });

    // Assert the exception message
    assertEquals("[" + "Invalid organization data" + "]", exception.getMessage());
  }

  @Test
  public void testCreateOrganization_conflict() throws Exception {
    // Arrange
    Organization organization = new Organization();
    BindingResult bindingResult = mock(BindingResult.class);

    // Mock the behavior of the organizationService to throw an exception
    when(organizationService.createOrganization(organization))
        .thenThrow(new BadRequestException("Conflict error"));

    // Act & Assert
    Exception exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              organizationController.createOrganization(organization, bindingResult);
            });

    // Assert the exception message
    assertEquals("Conflict error", exception.getMessage());
  }

  @Test
  public void testCreateOrganization_Badrequest() throws Exception {
    // Arrange
    Organization organization =
        new Organization(); // Create an organization object, you might need to populate it as per
    // your validation
    BindingResult bindingResult = mock(BindingResult.class);

    // Mock the behavior of the BindingResult to indicate that there are validation errors
    when(bindingResult.hasErrors()).thenReturn(true);

    // Mock the error messages to be returned by the binding result
    ObjectError error =
        new ObjectError("organization", "Invalid organization data"); // Example error message
    when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));

    // Act
    Exception exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              organizationController.createOrganization(organization, bindingResult);
            });

    // Assert
    assertEquals(
        "[" + error.getDefaultMessage() + "]",
        exception.getMessage()); // Assert that the message matches expected
  }

  @Test
  public void testGetOrganizationById() throws Exception {
    // Arrange
    String organizationId = "ABCD";

    // Mock the repository to return the organization when called with organizationId
    Mockito.when(organizationService.getOrganizationById(anyString()))
        .thenReturn(new OrganizationResponse());

    // Act
    ResponseEntity<OrganizationResponse> responseEntity =
        organizationController.getOrganizationById(organizationId);

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        new OrganizationResponse(),
        responseEntity.getBody()); // Check against the actual Organization object
  }

  @Test
  void testDeleteOrganizationById() throws Exception {
    // Arrange
    String organizationId = "org123";

    when(organizationService.deleteOrganizationById(organizationId))
        .thenReturn(Optional.ofNullable(organization1));

    // Act
    ResponseEntity<Organization> responseEntity =
        organizationController.deleteOrganizatiobById(organizationId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }
}
