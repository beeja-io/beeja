package com.beeja.api.expense.controller;

import com.beeja.api.expense.controllers.ExpenseController;
import com.beeja.api.expense.exceptions.ExpenseNotFound;
import com.beeja.api.expense.exceptions.OrganizationMismatchException;
import com.beeja.api.expense.modal.Expense;
import com.beeja.api.expense.repository.ExpenseRepository;
import com.beeja.api.expense.requests.CreateExpense;
import com.beeja.api.expense.requests.ExpenseUpdateRequest;
import com.beeja.api.expense.service.ExpenseService;
import com.beeja.api.expense.utils.Constants;
import com.beeja.api.expense.utils.UserContext;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseControllerTest {

  @Mock private ExpenseRepository expenseRepository;

  @InjectMocks private ExpenseController expenseController;
  @Autowired MockMvc mockMvc;

  @Mock ExpenseService expenseService;

  @Mock private UserContext userContext;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  @Test
  void testCreateExpenseSuccess() throws Exception {
    CreateExpense createExpense = new CreateExpense();
    Expense newExpense = new Expense();
    when(expenseService.createExpense(any(CreateExpense.class))).thenReturn(newExpense);
    ResponseEntity<?> responseEntity = expenseController.createExpense(createExpense);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @Test
  void testUpdateExpenseSuccess() throws Exception {
    ExpenseUpdateRequest updatedExpense = new ExpenseUpdateRequest();
    Expense expense = new Expense();
    when(expenseService.updateExpense("123", updatedExpense)).thenReturn(expense);
    ResponseEntity<?> responseEntity = expenseController.updateExpense("123", updatedExpense);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void testUpdateExpenseInternalServerError() throws Exception {
    ExpenseUpdateRequest updatedExpense = new ExpenseUpdateRequest();
    when(expenseService.updateExpense("123", updatedExpense))
            .thenThrow(new Exception("Some internal error"));

    Exception thrown = assertThrows(Exception.class, () -> {
      expenseController.updateExpense("123", updatedExpense);
    });
    assertEquals("Some internal error", thrown.getMessage());
  }

  @Test
  void testUpdateExpenseForbidden() throws Exception {
    ExpenseUpdateRequest updatedExpense = new ExpenseUpdateRequest();

    when(expenseService.updateExpense("123", updatedExpense))
            .thenThrow(new OrganizationMismatchException("Organization mismatch detected"));

    OrganizationMismatchException thrownException = assertThrows(OrganizationMismatchException.class, () -> {
      expenseController.updateExpense("123", updatedExpense);
    });
     assertEquals("Organization mismatch detected", thrownException.getMessage());
  }

  @Test
  void testFilterExpensesSuccess() throws Exception {
    Expense expense = new Expense();
    expense.setType("Purchase");
    expense.setCategory("Fitness");
    expense.setOrganizationId("tac");
    List<String> expenseType = Collections.singletonList("Purchase");
    List<String> expenseCategory = Collections.singletonList("Fitness");
    List<String> department = null;
    String filterBasedOn = "expenseDate";
    List<String> modeOfPayment = null;
    Date startDate = null;
    Date endDate = null;
    int pageNumber = 1;
    int pageSize = 10;
    String sortBy = null;
    boolean ascending = true;
    boolean  booleanParamFromControllerFalse = false;
    List<Expense> filteredExpenses = Collections.singletonList(expense);
    when(expenseService.getFilteredExpenses(
            any(Date.class),
            any(Date.class),
            isNull(),
            eq("expenseDate"),
            isNull(),
            eq(expenseType),
            eq(expenseCategory),
            eq("tac"),
            eq(1),
            eq(10),
            isNull(),
            eq(booleanParamFromControllerFalse),
            eq(true)))
            .thenReturn(filteredExpenses);
    ResponseEntity<?> responseEntity =
            expenseController.filterExpenses(
                    startDate,
                    endDate,
                    department,
                    filterBasedOn,
                    modeOfPayment,
                    expenseType,
                    expenseCategory,
                    pageNumber,
                    pageSize,
                    sortBy,
                    booleanParamFromControllerFalse,
                    ascending);
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
  }

  @Test
  void testFilterExpensesGeneralException() throws Exception {
    Expense expense = new Expense();
    expense.setType("Purchase");
    expense.setCategory("Fitness");
    expense.setOrganizationId("tac");
    List<String> expenseType = Collections.singletonList("Purchase");
    List<String> expenseCategory = Collections.singletonList("Fitness");
    List<String> department = null;
    String filterBasedOn = "expenseDate";
    List<String> modeOfPayment = null;
    Date startDate = null;
    Date endDate = null;
    int pageNumber = 1;
    int pageSize = 10;
    String sortBy = null;
    boolean ascending = true;
    boolean  booleanParamFromControllerFalse = false;
    List<Expense> filteredExpenses = Collections.singletonList(expense);

    when(expenseService.getFilteredExpenses(
            any(Date.class),
            any(Date.class),
            isNull(),
            eq(filterBasedOn),
            isNull(),
            eq(expenseType),
            eq(expenseCategory),
            eq("tac"),
            eq(pageNumber),
            eq(pageSize),
            isNull(),
            eq( booleanParamFromControllerFalse),
            eq(ascending)))
            .thenThrow(new Exception("Simulated internal error"));

    Exception thrownException = assertThrows(Exception.class, () -> {
      expenseController.filterExpenses(
              startDate,
              endDate,
              department,
              filterBasedOn,
              modeOfPayment,
              expenseType,
              expenseCategory,
              pageNumber,
              pageSize,
              sortBy,
              booleanParamFromControllerFalse,
              ascending);
    });
    assertEquals("Simulated internal error", thrownException.getMessage());
  }

  @Test
  public void testDeleteExpenseSuccess() throws Exception {
    String expenseId = "1";
    Expense mockExpense = new Expense();
    when(expenseService.deleteExpense(expenseId)).thenReturn(mockExpense);
    ResponseEntity<?> responseEntity = expenseController.deleteExpense(expenseId);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void testDeleteExpenseExpenseNotFound() throws Exception {
    String expenseId = "1";
    when(expenseService.deleteExpense(expenseId))
            .thenThrow(new ExpenseNotFound("Expense not found with ID: " + expenseId));

    ExpenseNotFound thrown = assertThrows(ExpenseNotFound.class, () -> {
      expenseController.deleteExpense(expenseId);
    });
    assertEquals("Expense not found with ID: 1", thrown.getMessage());
  }

  @Test
  public void testDeleteExpenseInternalServerError() throws Exception {
    String expenseId = "1";
    when(expenseService.deleteExpense(expenseId))
            .thenThrow(new RuntimeException("Internal Server Error"));

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      expenseController.deleteExpense(expenseId);
    });
    assertEquals("Internal Server Error", thrown.getMessage());
  }

  @Test
  void testGetExpenseStatusSuccess() throws Exception {
    String expenseId = "1";
    Expense mockExpense = new Expense();
    mockExpense.setStatus("Approved");
    when(expenseService.getExpenseById(expenseId)).thenReturn(mockExpense);
    ResponseEntity<String> responseEntity =
            (ResponseEntity<String>) expenseController.getExpenseStatus(expenseId);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertTrue(
            responseEntity
                    .getBody()
                    .contains("Approved")); // Check if the response body contains the expected status
  }

  @Test
  void testSettleExpenseSuccess() throws Exception {
    String expenseId = "123";
    Expense mockExpense = new Expense();
    mockExpense.setStatus("settled");
    when(expenseService.settleExpense(expenseId)).thenReturn(mockExpense);
    ResponseEntity<?> responseEntity = expenseController.settleExpense(expenseId);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(Constants.EXPENSE_STATUS + "settled", responseEntity.getBody());
    verify(expenseService).settleExpense(expenseId);
  }
}