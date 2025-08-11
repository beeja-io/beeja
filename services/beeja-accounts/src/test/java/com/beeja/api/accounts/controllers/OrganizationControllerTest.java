package com.beeja.api.accounts.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.exceptions.OrganizationExceptions;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.service.OrganizationService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
