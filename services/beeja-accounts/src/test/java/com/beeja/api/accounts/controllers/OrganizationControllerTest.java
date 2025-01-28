package com.beeja.api.accounts.controllers;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.exceptions.OrganizationExceptions;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class OrganizationControllerTest {

  @Mock private OrganizationService organizationService;
  @Mock private OrganizationRepository organizationRepository;
  @Mock private EmployeeFeignClient employeeFeignClient;

  @InjectMocks private OrganizationController organizationController;

  @Autowired private MockMvc mockMvc;

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

  @Test
  void testUpdateOrganization_Success() throws Exception {
    String organizationId = "123";
    String fields = "{\"name\":\"Updated Org Name\"}";
    MultipartFile mockFile = new MockMultipartFile("file", "logo.png", "image/png", new byte[] {});
    Organization updatedOrganization = new Organization();
    updatedOrganization.setId(organizationId);
    updatedOrganization.setName("Updated Org Name");

    Mockito.when(organizationService.updateOrganization(organizationId, fields, mockFile))
            .thenReturn(updatedOrganization);
    ResponseEntity<?> response =
            organizationController.updateOrganization(organizationId, fields, mockFile);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedOrganization, response.getBody());
    Mockito.verify(organizationService, Mockito.times(1))
            .updateOrganization(organizationId, fields, mockFile);
  }

  @Test
  void testUpdateOrganization_NullFieldsAndFile() throws Exception {
    String organizationId = "123";
    String fields = null;
    MultipartFile file = null;
    Organization updatedOrganization = new Organization();
    updatedOrganization.setId(organizationId);
    updatedOrganization.setName("Default Org Name");

    Mockito.when(organizationService.updateOrganization(organizationId, fields, file))
            .thenReturn(updatedOrganization);
    ResponseEntity<?> response =
            organizationController.updateOrganization(organizationId, fields, file);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedOrganization, response.getBody());
    Mockito.verify(organizationService, Mockito.times(1))
            .updateOrganization(organizationId, fields, file);
  }

//  @Test
//  void testDownloadOrganizationFile_Success() throws Exception {
//    ByteArrayResource resource = mock(ByteArrayResource.class);
//    when(resource.getFilename()).thenReturn("logo.png");
//    when(organizationService.downloadOrganizationFile()).thenReturn(resource);
//    mockMvc
//            .perform(get("/v1/organizations/logo"))
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
//            .andExpect(
//                    header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"logo.png\""));
//  }



  @Test
  void testUpdateOrganizationValues_Success() throws Exception {
    OrgDefaults orgDefaults = new OrgDefaults();
    orgDefaults.setOrganizationId("org1");
    orgDefaults.setKey("settingKey");
    orgDefaults.setValues(Set.of(new OrgValues("value1", "value2")));

    when(organizationService.updateOrganizationValues(orgDefaults)).thenReturn(orgDefaults);
    ResponseEntity<OrgDefaults> response =
            organizationController.updateOrganizationValues(orgDefaults);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(orgDefaults, response.getBody());
  }

  @Test
  void testGetOrganizationValuesByKey_Success() throws Exception {
    String key = "settingKey";
    OrgDefaults orgDefaults = new OrgDefaults();
    orgDefaults.setKey(key);
    orgDefaults.setValues(Set.of(new OrgValues("value1", "value2")));

    when(organizationService.getOrganizationValuesByKey(key)).thenReturn(orgDefaults);
    ResponseEntity<OrgDefaults> response = organizationController.getOrganizationValuesByKey(key);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(orgDefaults, response.getBody());
  }

  @Test
  void testGetOrganizationValuesByKey_NotFound() throws Exception {
    String key = "invalidKey";
    when(organizationService.getOrganizationValuesByKey(key))
            .thenThrow(new RuntimeException("Key not found"));
    Exception exception =
            assertThrows(
                    RuntimeException.class, () -> organizationController.getOrganizationValuesByKey(key));
    assertEquals("Key not found", exception.getMessage());
  }

}
