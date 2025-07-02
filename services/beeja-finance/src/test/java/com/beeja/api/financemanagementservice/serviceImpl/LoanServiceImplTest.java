package com.beeja.api.financemanagementservice.serviceImpl;

import static com.beeja.api.financemanagementservice.Utils.Constants.GET_ALL_LOANS;
import static com.beeja.api.financemanagementservice.enums.LoanType.PERSONAL_LOAN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.repository.LoanRepository;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

  @Mock private LoanRepository loanRepository;

  @InjectMocks private LoanServiceImpl loanService;

  @Autowired MockMvc mockMvc;

  private UserContext userContext;

  private LoanServiceImpl loanServiceImpl;

  private AccountClient accountClient;

  @InjectMocks private String organizationId;
  private String employeeId;
  private SubmitLoanRequest loanRequest;
  private Loan loan;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(loanService).build();
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
    UserContext.setLoggedInEmployeeId("employee456");
    ReflectionTestUtils.setField(loanService, "loanRepository", loanRepository);
    accountClient = mock(AccountClient.class);
    loanRequest = new SubmitLoanRequest();
    loanRequest.setLoanType(PERSONAL_LOAN);
    loanRequest.setAmount(10000.0);
    loanRequest.setMonthlyEMI(500.0);
    loanRequest.setPurpose("Personal purpose");
    loanRequest.setEmiTenure(12);
  }

  private Loan createMockLoan() {
    Loan mockLoan = new Loan();
    mockLoan.setLoanNumber("123456");
    mockLoan.setOrganizationId("organization123");
    mockLoan.setEmployeeId("employee789");
    mockLoan.setStatus(LoanStatus.WAITING);
    return mockLoan;
  }

  @Test
  void testChangeLoanStatusApprove() {
    String loanId = "123456";
    String status = "APPROVE";
    String message = "Approved successfully";
    Loan mockLoan = createMockLoan();
    when(loanRepository.findByIdAndOrganizationId(any(), any())).thenReturn(mockLoan);
    loanService.changeLoanStatus(loanId, status, message);
    assertEquals(LoanStatus.APPROVED, mockLoan.getStatus());
    verify(loanRepository, times(1)).save(mockLoan);
  }

  @Test
  void testChangeLoanStatusReject() {
    String loanId = "123456";
    String status = "REJECT";
    String message = "message";

    Loan mockLoan = createMockLoan();
    when(loanRepository.findByIdAndOrganizationId(any(), any())).thenReturn(mockLoan);

    assertDoesNotThrow(() -> loanService.changeLoanStatus(loanId, status, message));

    assertEquals(LoanStatus.REJECTED, mockLoan.getStatus());
  }

  @Test
  void testChangeLoanStatusWaiting() {
    String loanId = "123456";
    String status = "WAITING";
    String message = "message";

    Loan mockLoan = createMockLoan();
    when(loanRepository.findByIdAndOrganizationId(any(), any())).thenReturn(mockLoan);

    assertDoesNotThrow(() -> loanService.changeLoanStatus(loanId, status, message));

    assertEquals(LoanStatus.WAITING, mockLoan.getStatus());
  }

  @Test
  void testChangeLoanStatusLoanNotFound() {
    when(loanRepository.findByIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

    assertThrows(
        ResourceNotFoundException.class,
        () -> loanService.changeLoanStatus("invalidId", "APPROVE", null));
  }

  @Test
  public void testGetAllLoansByEmployeeIdWithoutPermission() throws Exception {
    // Arrange
    String employeeId = "testEmployeeId";
    String loggedInEmployeeId = "loggedInEmployeeId";
    List<Loan> expectedLoans = Arrays.asList(new Loan(), new Loan());
    when(loanRepository.findAllByEmployeeIdAndOrganizationId(loggedInEmployeeId, "tac"))
        .thenReturn(expectedLoans);

    Set<String> permissions = Collections.emptySet();
    UserContext.setLoggedInUserPermissions(permissions);
    UserContext.setLoggedInEmployeeId(loggedInEmployeeId);

    // Act
    List<Loan> actualLoans = loanService.getAllLoansByEmployeeId(employeeId);

    // Assert
    assertEquals(expectedLoans, actualLoans);
  }

  @Test
  public void testGetAllLoansByEmployeeIdWithPermission() throws Exception {
    // Arrange
    String employeeId = "testEmployeeId";
    List<Loan> expectedLoans = Arrays.asList(new Loan(), new Loan());
    when(loanRepository.findAllByEmployeeIdAndOrganizationId(employeeId, "tac"))
        .thenReturn(expectedLoans);

    Set<String> permissions = Set.of(GET_ALL_LOANS);
    UserContext.setLoggedInUserPermissions(permissions);

    // Act
    List<Loan> actualLoans = loanService.getAllLoansByEmployeeId(employeeId);

    // Assert
    assertEquals(expectedLoans, actualLoans);
  }
}
