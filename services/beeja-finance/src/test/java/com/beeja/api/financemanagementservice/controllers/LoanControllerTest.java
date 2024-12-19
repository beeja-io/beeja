package com.beeja.api.financemanagementservice.controllers;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.config.filters.AuthorizationFilter;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.repository.LoanRepository;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import com.beeja.api.financemanagementservice.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

  @Mock private LoanService loanService;

  @InjectMocks private LoanController loanController;

  @Mock private LoanRepository loanRepository;

  @Autowired MockMvc mockMvc;

  @InjectMocks BulkPayslipController bulkPayslipController;

  @Autowired private AuthorizationFilter authorizationFilter;

  @Mock private BindingResult bindingResult;

  private SubmitLoanRequest loanRequest;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();

    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  public void createUserContext() {
    Map<String, Object> loggedInUserOrganization = new HashMap<>();
    loggedInUserOrganization.put("id", "testId");
    UserContext.setLoggedInUserOrganization(loggedInUserOrganization);
    Set<String> loggedInUserPermissions = new HashSet<>();
    UserContext.setLoggedInUserPermissions(loggedInUserPermissions);
  }

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testSubmitLoanRequestSuccess() throws Exception {
    // Arrange
    SubmitLoanRequest loanRequest = new SubmitLoanRequest();
    Loan loan = new Loan();
    when(bindingResult.hasErrors()).thenReturn(false);
    when(loanService.submitLoanRequest(loanRequest)).thenReturn(loan);

    // Act
    ResponseEntity<?> responseEntity = loanController.submitLoanRequest(loanRequest, bindingResult);

    // Assert
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(loan, responseEntity.getBody());
    verify(loanService, times(1)).submitLoanRequest(loanRequest);
  }

  @Test
  public void testSubmitLoanRequestWithException() throws Exception {
    // Setup: mock the validation errors
    when(bindingResult.hasErrors()).thenReturn(true);
    when(bindingResult.getAllErrors())
        .thenReturn(
            Arrays.asList(
                new ObjectError("loanAmount", "Loan amount is required"),
                new ObjectError("loanTerm", "Loan term must be specified")));

    // Act & Assert
    Exception exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              loanController.submitLoanRequest(
                  loanRequest, bindingResult); // Use loanRequest properly
            });

    // Assert: check that the exception message contains the validation errors
    assertTrue(exception.getMessage().contains("Loan amount is required"));
    assertTrue(exception.getMessage().contains("Loan term must be specified"));
  }

  @Test
  public void testSubmitLoanRequestWithBadRequestError() throws Exception {
    when(bindingResult.hasErrors()).thenReturn(true);
    when(bindingResult.getAllErrors())
        .thenReturn(
            Arrays.asList(
                new ObjectError("loanAmount", "Loan amount is required"),
                new ObjectError("loanTerm", "Loan term must be specified")));
    try {
      loanController.submitLoanRequest(loanRequest, bindingResult);
      fail("Expected BadRequestException to be thrown");
    } catch (BadRequestException ex) {
      String errorMessage = ex.getMessage();
      assertTrue(errorMessage.contains("Loan amount is required"));
      assertTrue(errorMessage.contains("Loan term must be specified"));
      assertTrue(errorMessage.contains("VALIDATION_ERROR"));
    }
    verify(loanService, never()).submitLoanRequest(loanRequest);
  }

  @Test
  public void testGetAllLoans_Success() throws Exception {
    createUserContext();
    // Arrange
    List<Loan> expectedLoans = Arrays.asList(new Loan(), new Loan()); // Mocked list of loans
    when(loanService.getAllLoans()).thenReturn(expectedLoans);
    // Act
    ResponseEntity<?> responseEntity = loanController.getAllLoans();
    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedLoans, responseEntity.getBody());
  }

  @Test
  public void testGetAllLoans_Exception() throws Exception {
    when(loanService.getAllLoans()).thenThrow(new RuntimeException("Error fetching loans"));
    try {
      loanController.getAllLoans();
      fail("Expected RuntimeException to be thrown");
    } catch (RuntimeException ex) {
      assertEquals("Error fetching loans", ex.getMessage());
    }
    verify(loanService, times(1)).getAllLoans();
  }

  @Test
  public void testGetLoansByEmployeeId_Success() throws Exception {
    createUserContext();
    String employeeId = "EMP001";
    List<Loan> expectedLoans = Arrays.asList(new Loan(), new Loan());
    when(loanService.getAllLoansByEmployeeId(employeeId)).thenReturn(expectedLoans);
    ResponseEntity<?> responseEntity = loanController.getLoansByEmployeeId(employeeId);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedLoans, responseEntity.getBody());
  }

  @Test
  public void testGetLoansByEmployeeId_Exception() throws Exception {
    createUserContext();
    String employeeId = "EMP001";
    when(loanService.getAllLoansByEmployeeId(employeeId))
        .thenThrow(new RuntimeException("Error fetching loans for employee ID: " + employeeId));
    try {
      loanController.getLoansByEmployeeId(employeeId);
      fail("Expected RuntimeException to be thrown");
    } catch (RuntimeException ex) {
      assertEquals("Error fetching loans for employee ID: " + employeeId, ex.getMessage());
    }
    verify(loanService, times(1)).getAllLoansByEmployeeId(employeeId);
  }

  @Test
  public void testUploadBulkPaySlipsSuccess() throws Exception {
    String authorizationHeader = "Bearer token";
    BulkPayslipRequest bulkPayslipRequest = new BulkPayslipRequest();
    doNothing().when(loanService).uploadBulkPaySlips(bulkPayslipRequest, authorizationHeader);
    bulkPayslipController.uploadBulkPaySlips(authorizationHeader, bulkPayslipRequest);
    verify(loanService, times(1)).uploadBulkPaySlips(bulkPayslipRequest, authorizationHeader);
  }

  @Test
  public void testChangeLoanStatus_Success() {
    String loanId = "LOAN001";
    String status = "APPROVED";
    String message = "Loan approved successfully";
    doNothing().when(loanService).changeLoanStatus(loanId, status, message);
    loanController.changeLoanStatus(loanId, status, message);
    verify(loanService, times(1)).changeLoanStatus(loanId, status, message);
  }

  @Test
  public void testChangeLoanStatus_Exception() {
    String loanId = "LOAN001";
    String status = "APPROVED";
    String message = "Loan approved successfully";
    doThrow(new RuntimeException("Error changing loan status"))
        .when(loanService)
        .changeLoanStatus(loanId, status, message);
    Exception exception =
        assertThrows(
            RuntimeException.class, () -> loanController.changeLoanStatus(loanId, status, message));

    assertEquals("Error changing loan status", exception.getMessage());
    verify(loanService, times(1)).changeLoanStatus(loanId, status, message);
  }
}
