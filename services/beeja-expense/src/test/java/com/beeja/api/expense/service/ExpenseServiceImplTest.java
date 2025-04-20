package com.beeja.api.expense.service;

import com.beeja.api.expense.client.AccountClient;
import com.beeja.api.expense.client.FileClient;
import com.beeja.api.expense.exceptions.ExpenseNotFound;
import com.beeja.api.expense.modal.Expense;
import com.beeja.api.expense.repository.ExpenseRepository;
import com.beeja.api.expense.requests.CreateExpense;
import com.beeja.api.expense.requests.ExpenseUpdateRequest;
import com.beeja.api.expense.response.CountryResponse;
import com.beeja.api.expense.serviceImpl.ExpenseServiceImpl;
import com.beeja.api.expense.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceImplTest {

  @Mock private ExpenseRepository expenseRepository;

  @Mock private MongoTemplate mongoTemplate;

  @Mock private AccountClient accountClient;

  @Mock private FileClient fileClient;
  @InjectMocks private ExpenseServiceImpl expenseService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  @Test
  public void testCreateExpense_SavesExpenseDetails() throws Exception {
    CreateExpense createExpense = new CreateExpense();
    createExpense.setAmount(100);
    createExpense.setCurrencyCode("USD");
    createExpense.setType("Travel");
    createExpense.setCategory("Transport");
    createExpense.setModeOfPayment("Credit Card");
    createExpense.setMerchant("Uber");
    createExpense.setClaimed(true);
    createExpense.setPaymentMadeBy("Employee");
    createExpense.setDescription("Business trip to client");
    createExpense.setDepartment("Sales");
    Expense mockExpense = new Expense();
    when(expenseRepository.save(any())).thenReturn(mockExpense);
    when(accountClient.getCountryByOrganizationId(anyString()))
        .thenReturn(ResponseEntity.ok("USA"));
    CountryResponse mockCountryResponse = new CountryResponse();
    mockCountryResponse.setExpenseTypes(Arrays.asList("Travel", "Food"));
    mockCountryResponse.setModeOfPayment(Arrays.asList("Credit Card", "Cash"));
    mockCountryResponse.setCategory(Arrays.asList("Transport", "Accommodation"));
    when(accountClient.getExpenseTypesByCountryName(anyString()))
        .thenReturn(ResponseEntity.ok(mockCountryResponse));
    Expense savedExpense = expenseService.createExpense(createExpense);
    assertNotNull(savedExpense);
    verify(expenseRepository, times(1)).save(any());

  }

  @Test
  void testUpdateExpensesSuccess() throws Exception {
    UserContext.setLoggedInUserPermissions(Set.of("READ_EXPENSE"));
    Expense expense = new Expense();
    Optional<Expense> optionalExpense = Optional.of(expense);
    optionalExpense.get().setOrganizationId("tac");
    when(expenseRepository.findById(anyString())).thenReturn(optionalExpense);
    ExpenseUpdateRequest updatedExpense = new ExpenseUpdateRequest();
    optionalExpense.get().setOrganizationId("tac");
    String loggedInUserOrganizationId =
        (String) UserContext.getLoggedInUserOrganization().get("id");
    when(expenseRepository.save(expense)).thenReturn(expense);
    Expense expense1 = expenseService.updateExpense("123", updatedExpense);
    assertEquals(expense, expense1);
  }

  @Test
  void testUpdateExpensesGeneralException() {
    UserContext.setLoggedInUserPermissions(Set.of("READ_EXPENSE"));
    Expense expense = new Expense();
    ExpenseUpdateRequest expenseUpdateRequest = new ExpenseUpdateRequest();
    when(expenseRepository.save(expense)).thenThrow(new RuntimeException("Some internal error"));
    assertThrows(Exception.class, () -> expenseService.updateExpense("123", expenseUpdateRequest));
  }

  @Test
  void testGetFilteredExpensesSuccess() {
    Query query = new Query();
    query.addCriteria(Criteria.where("modeOfPayment").is("UPI"));
    query.addCriteria(Criteria.where("type").is("Purchase"));
    query.addCriteria(Criteria.where("category").is("Office Supplies - Lap"));
    query.addCriteria(Criteria.where("organizationId").is("tac"));
    Expense expense = new Expense();
    expense.setModeOfPayment("UPI");
    expense.setType("Purchase");
    expense.setCategory("Office Supplies - Lap");
    expense.setOrganizationId("tac");
    List<Expense> filteredExpenses = Collections.singletonList(expense);
    when(mongoTemplate.find(query, Expense.class)).thenReturn(filteredExpenses);
    assertEquals(filteredExpenses.get(0), expense);
  }

  @Test
  public void testDeleteFile_Success() throws Exception {
    String expenseId = "1";
    UserContext.setLoggedInUserEmail("tac");

    Expense mockExpense = new Expense();
    mockExpense.setFileId(Arrays.asList("abc", "cdf"));
    UserContext.setLoggedInUserPermissions(Set.of("DELETE_EXPENSE"));

    when(expenseRepository.findByOrganizationIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(mockExpense));
    Expense result = expenseService.deleteExpense(expenseId);
    assertEquals(mockExpense, result);
  }

  @Test
  void testDeleteExpenseExpenseNotFound() {
    String expenseId = "1";
    when(expenseRepository.findByOrganizationIdAndId(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(ExpenseNotFound.class, () -> expenseService.deleteExpense(expenseId));
  }

  @Test
  void testDeleteExpenseException() throws Exception {
    String expenseId = "1234";
    String organizationId = "abcde";
    UserContext.setLoggedInUserOrganization(Collections.singletonMap("id", organizationId));
    Expense expense = new Expense();
    expense.setId(expenseId);
    expense.setFileId(Arrays.asList("fileId1", "fileId2"));
    when(expenseRepository.findByOrganizationIdAndId(organizationId, expenseId))
        .thenReturn(Optional.of(expense));
    expenseService.deleteExpense(expenseId);
    verify(expenseRepository).deleteById(expenseId);
    verify(fileClient, times(2)).deleteFile(anyString());
  }

  @Test
  public void testGetExpenseById_Success() throws Exception {
    String expenseId = "12345";
    Expense mockExpense = new Expense();
    mockExpense.setId(expenseId);
    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(mockExpense));
    Expense result = expenseService.getExpenseById(expenseId);
    assertNotNull(result);
    assertEquals(mockExpense, result);
    verify(expenseRepository).findById(expenseId);
  }

  @Test
  public void testSettleExpense_Success() throws Exception {
    String expenseId = "12345";
    Expense mockExpense = new Expense();
    mockExpense.setId(expenseId);
    mockExpense.setStatus("PENDING");
    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(mockExpense));
    when(expenseRepository.save(mockExpense)).thenReturn(mockExpense);
    Expense settledExpense = expenseService.settleExpense(expenseId);
    assertNotNull(settledExpense);
    assertEquals("Settled", settledExpense.getStatus());
    verify(expenseRepository).findById(expenseId);
    verify(expenseRepository).save(mockExpense);
  }

  @Test
  void testGetFilteredTotalAmount_Success() {
    Date startDate = new GregorianCalendar(2023, GregorianCalendar.JANUARY, 1).getTime();
    Date endDate = new GregorianCalendar(2023, GregorianCalendar.JANUARY, 31).getTime();
    String department = "HR";
    String filterBasedOn = "department";
    String modeOfPayment = "Credit Card";
    String expenseType = "Office Supplies";
    String expenseCategory = "Stationery";
    Boolean settlementStatus=true;
    String organizationId = "Org123";
    Map<String, Object> mockResult = Collections.singletonMap("totalAmount", 5000.0);
    AggregationResults<Map> mockAggregationResults = mock(AggregationResults.class);
    when(mockAggregationResults.getMappedResults())
        .thenReturn(Collections.singletonList(mockResult));
    when(mongoTemplate.aggregate(any(Aggregation.class), eq("expenses"), eq(Map.class)))
        .thenReturn(mockAggregationResults);
    Double totalAmount =
        expenseService.getFilteredTotalAmount(
            startDate,
            endDate,
            department,
            filterBasedOn,
            modeOfPayment,
            expenseType,
            expenseCategory,
            settlementStatus,
            organizationId);
    assertEquals(0.0, totalAmount);
  }

  @Test
  void testGetTotalExpensesSize_Success() {
    Date startDate = new GregorianCalendar(2023, Calendar.JANUARY, 1).getTime();
    Date endDate = new GregorianCalendar(2023, Calendar.JANUARY, 31).getTime();
    String department = "HR";
    String filterBasedOn = "date";
    String modeOfPayment = "Credit Card";
    String expenseType = "Office Supplies";
    String expenseCategory = "Stationery";
    Boolean settlementStatus=true;
    String organizationId = "Org123";
    long expectedCount = 10;
    when(mongoTemplate.count(any(Query.class), eq(Expense.class))).thenReturn(expectedCount);
    Long actualCount =
        expenseService.getTotalExpensesSize(
            startDate,
            endDate,
            department,
            filterBasedOn,
            modeOfPayment,
            expenseType,
            expenseCategory,
            settlementStatus,
            organizationId);
    assertEquals(expectedCount, actualCount);
    verify(mongoTemplate).count(any(Query.class), eq(Expense.class));
  }

  @Test
  void testGetFilteredExpenses() throws Exception {
    when(mongoTemplate.find(any(Query.class), eq(Expense.class)))
        .thenReturn(List.of(new Expense()));
    boolean ascending = true;
    List<Expense> result =
        expenseService.getFilteredExpenses(
            new Date(),
            new Date(),
            "Finance",
            "createdDate",
            "Credit Card",
            "Office",
            "Stationery",
            "org123",
            1,
            10,
            "createdDate",
            true, ascending);
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(mongoTemplate).find(any(Query.class), eq(Expense.class));
  }
}
