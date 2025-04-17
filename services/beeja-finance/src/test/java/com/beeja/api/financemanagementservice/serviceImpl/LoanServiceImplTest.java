package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import com.beeja.api.financemanagementservice.repository.LoanRepository;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.beeja.api.financemanagementservice.Utils.Constants.GET_ALL_LOANS;
import static com.beeja.api.financemanagementservice.enums.LoanType.PERSONAL_LOAN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  public void testGetAllLoansSuccess() throws Exception {
    // Arrange
    List<Loan> expectedLoans = Arrays.asList(new Loan(), new Loan());
    when(loanRepository.findAllByOrganizationId("tac")).thenReturn(expectedLoans);

    // Act
    List<Loan> actualLoans = loanService.getAllLoans();

    // Assert
    assertEquals(expectedLoans, actualLoans);
  }

  @Test
  public void testGetAllLoansNoLoans() throws Exception {
    List<Loan> emptyLoans = Collections.emptyList();
    when(loanRepository.findAllByOrganizationId("tac")).thenReturn(emptyLoans);
    List<Loan> actualLoans = loanService.getAllLoans();
    assertTrue(actualLoans.isEmpty());
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

  @Test
  void testSubmitLoanRequestSuccess() throws Exception {
    // Arrange
    loanRequest.setEmiStartDate(new Date());
    OrganizationPattern orgPattern = new OrganizationPattern();
    orgPattern.setPrefix("LN-");

    when(accountClient.getActivePatternByType("LOAN_ID_PATTERN"))
            .thenReturn(ResponseEntity.ok(orgPattern));
    when(loanRepository.countByOrganizationId("tac")).thenReturn(5L);

    Loan savedLoan = new Loan();
    savedLoan.setLoanNumber("LN-6");
    when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

    ReflectionTestUtils.setField(loanService, "accountClient", accountClient);

    // Act
    Loan result = loanService.submitLoanRequest(loanRequest);

    // Assert
    assertEquals("LN-6", result.getLoanNumber());
    verify(loanRepository).save(any(Loan.class));
  }
  @Test
  void testUploadBulkPaySlipsWithValidZip() throws Exception {
    // Arrange
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    zos.putNextEntry(new ZipEntry("test1.pdf"));
    zos.write("dummycontent".getBytes());
    zos.closeEntry();
    zos.close();

    MultipartFile zipFile = new MockMultipartFile("file", "test.zip", "application/zip", baos.toByteArray());
    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);

    // Act + Assert
    assertDoesNotThrow(() -> loanService.uploadBulkPaySlips(request, "Bearer token"));
  }


}
