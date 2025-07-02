package com.beeja.api.financemanagementservice.controllers;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceCreationException;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceNotFoundException;
import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import com.beeja.api.financemanagementservice.requests.HealthInsuranceRequest;
import com.beeja.api.financemanagementservice.service.HealthInsuranceService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

class HealthInsuranceControllerTest {
  @InjectMocks HealthInsuranceController healthInsuranceController;

  @Autowired MockMvc mockMvc;
  @Mock HealthInsuranceService healthInsuranceService;

  @Mock UserContext userContext;

  @Mock private BindingResult bindingResult;

  private HealthInsuranceRequest healthInsuranceRequest;

  private HealthInsurance healthInsuranceResponse;

  private HealthInsurance healthInsurance;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(healthInsuranceController).build();
    createUserContext();
    healthInsuranceRequest = new HealthInsuranceRequest();
    healthInsuranceResponse = new HealthInsurance();
  }

  public void createUserContext() {
    Map<String, Object> loggedInUserOrganization = new HashMap<>();
    loggedInUserOrganization.put("id", "testId");
    UserContext.setLoggedInUserOrganization(loggedInUserOrganization);
    Set<String> loggedInUserPermissions = new HashSet<>();
    UserContext.setLoggedInUserPermissions(loggedInUserPermissions);
  }

  @Test
  void testGetHealthInsuranceByEmployeeIdSuccess() throws Exception {
    createUserContext();
    HealthInsurance healthInsurance = new HealthInsurance();
    when(healthInsuranceService.findHealthInsuranceByEmployeeId("123")).thenReturn(healthInsurance);
    ResponseEntity<?> responseEntity =
        healthInsuranceController.getHealthInsuranceByEmployeeId("123");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testSubmitHealthInsuranceSuccess() throws Exception {
    // Arrange
    HealthInsuranceRequest healthInsuranceRequest = new HealthInsuranceRequest();
    UserContext.setLoggedInUserOrganization(Collections.singletonMap("id", "value"));
    //    when(UserContext.getLoggedInUserOrganization()).thenReturn(Collections.singletonMap("id",
    // "value"));

    HealthInsurance healthInsurance = new HealthInsurance();
    when(bindingResult.hasErrors()).thenReturn(false);
    when(healthInsuranceService.saveHealthInsurance(any())).thenReturn(healthInsurance);
    // Act
    ResponseEntity<?> responseEntity =
        healthInsuranceController.submitHealthInsurance(healthInsuranceRequest, bindingResult);

    // Assert
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(healthInsurance, responseEntity.getBody());
  }

  @Test
  public void testSubmitHealthInsuranceCreationException() throws Exception {
    HealthInsuranceRequest healthInsuranceRequest = new HealthInsuranceRequest();
    when(bindingResult.hasErrors()).thenReturn(false);
    doThrow(new HealthInsuranceCreationException("Creation failed"))
        .when(healthInsuranceService)
        .saveHealthInsurance(any());
    Exception exception =
        assertThrows(
            HealthInsuranceCreationException.class,
            () -> {
              healthInsuranceController.submitHealthInsurance(
                  healthInsuranceRequest, bindingResult);
            });
    assertEquals("Creation failed", exception.getMessage());
    verify(healthInsuranceService).saveHealthInsurance(any());
  }

  @Test
  public void testSubmitHealthInsuranceGeneralException() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(false);
    when(healthInsuranceService.saveHealthInsurance(healthInsuranceRequest))
        .thenReturn(healthInsuranceResponse);
    ResponseEntity<HealthInsurance> responseEntity =
        healthInsuranceController.submitHealthInsurance(healthInsuranceRequest, bindingResult);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(healthInsuranceResponse, responseEntity.getBody());
  }

  @Test
  public void testGetHealthInsuranceByEmployeeId_Success() throws Exception {
    String employeeID = "TEST";
    when(healthInsuranceService.findHealthInsuranceByEmployeeId(employeeID))
        .thenReturn(healthInsuranceResponse);
    ResponseEntity<HealthInsurance> responseEntity =
        healthInsuranceController.getHealthInsuranceByEmployeeId(employeeID);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(healthInsuranceResponse, responseEntity.getBody());
  }

  @Test
  public void testGetHealthInsuranceByEmployeeId_NotFound() throws Exception {
    String employeeID = "213";
    when(healthInsuranceService.findHealthInsuranceByEmployeeId(employeeID))
        .thenThrow(new NoSuchElementException("Health insurance not found"));
    NoSuchElementException exception =
        assertThrows(
            NoSuchElementException.class,
            () -> {
              healthInsuranceController.getHealthInsuranceByEmployeeId(employeeID);
            });
    assertEquals("Health insurance not found", exception.getMessage());
  }

  @Test
  public void testDeleteHealthInsurance_Success() throws Exception {
    String employeeID = "TEST";
    HealthInsurance expectedHealthInsurance = new HealthInsurance();
    expectedHealthInsurance.setId("1");
    expectedHealthInsurance.setEmployeeId(employeeID);
    when(healthInsuranceService.deleteByEmployeeIdAndOrganizationId(employeeID))
        .thenReturn(expectedHealthInsurance);
    ResponseEntity<HealthInsurance> response =
        healthInsuranceController.deleteHealthInsurance(employeeID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedHealthInsurance, response.getBody());
  }

  @Test
  public void testDeleteHealthInsurance_NotFound() throws Exception {
    String employeeID = "123";
    when(healthInsuranceService.deleteByEmployeeIdAndOrganizationId(employeeID))
        .thenThrow(new HealthInsuranceNotFoundException("Health insurance not found"));
    HealthInsuranceNotFoundException exception =
        assertThrows(
            HealthInsuranceNotFoundException.class,
            () -> {
              healthInsuranceController.deleteHealthInsurance(employeeID);
            });
    assertEquals("Health insurance not found", exception.getMessage());
  }

  @Test
  public void testUpdateHealthInsurance_Success() throws Exception {
    String employeeID = "TEST";
    when(bindingResult.hasErrors()).thenReturn(false);
    when(healthInsuranceService.updateHealthInsurance(healthInsuranceRequest, employeeID))
        .thenReturn(healthInsurance);
    ResponseEntity<HealthInsurance> response =
        healthInsuranceController.updateHealthInsurance(
            healthInsuranceRequest, employeeID, bindingResult);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(healthInsurance, response.getBody());
  }
}
