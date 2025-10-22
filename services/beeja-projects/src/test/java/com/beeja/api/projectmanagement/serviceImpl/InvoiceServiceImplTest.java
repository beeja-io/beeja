package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
import com.beeja.api.projectmanagement.repository.InvoiceRepository;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.InvoiceStatus;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.InvoicePeriod;
import com.beeja.api.projectmanagement.model.PaymentDetails;
import com.beeja.api.projectmanagement.model.Task;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.InvoiceRepository;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import com.beeja.api.projectmanagement.utils.AmountToWordsUtil;
import com.beeja.api.projectmanagement.utils.InvoicePDFGen;
import com.beeja.api.projectmanagement.utils.PdfGenerationUtil;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    private static Map<String, Object> orgMap;
    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void init() {
        orgMap = new HashMap<>();
        orgMap.put("id", "org123");
        orgMap.put("name", "Tech.at.core");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
    }

    @AfterEach
    void tearDown() {
        userContextMock.close();
    }

    @Test
    void testGenerateInvoiceIdentifiers_shouldGenerateCorrectInvoiceAndRemittance() {
        // Arrange
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String expectedPrefix = "INV-" + yearMonth + "-";
        when(invoiceRepository.countByInvoiceIdRegex("^" + expectedPrefix)).thenReturn(0L);


        InvoiceIdentifiersResponse response = invoiceService.generateInvoiceIdentifiers("contract123");


        String expectedInvoiceId = expectedPrefix + "01";
        String expectedRemittance = "TEC" + expectedInvoiceId;

        assertNotNull(response);
        assertEquals(expectedInvoiceId, response.getInvoiceId());
        assertEquals(expectedRemittance, response.getRemittanceReferenceNumber());
    }
}


import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceImplTest {


  @Mock private InvoiceRepository invoiceRepository;
  @Mock private ContractRepository contractRepository;
  @Mock private PdfGenerationUtil pdfGenerationUtil;
  @Mock private InvoicePDFGen invoicePDFGen;
  @Mock private ClientRepository clientRepository;
  @Mock private FileClient fileClient;
  @Mock private AccountClient accountClient;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private InvoiceServiceImpl invoiceService;

  private Contract mockContract;
  private InvoiceRequest mockInvoiceRequest;
  private Invoice mockInvoice;
  private Client mockClient;
  private Map<String, Object> mockOrganizationMap;
  private LinkedHashMap<String, Object> mockFileClientResponse;

  @BeforeEach
  void setUp() {
    mockOrganizationMap = new LinkedHashMap<>();
    mockOrganizationMap.put("id", "org123");
    mockOrganizationMap.put("name", "TestOrg");
    Map<String, Object> bankDetails = new LinkedHashMap<>();
    bankDetails.put("accountName", "Org Acc");
    bankDetails.put("bankName", "Org Bank");
    bankDetails.put("accountNumber", "1234567890");
    bankDetails.put("ifscNumber", "ORGB000123");
    mockOrganizationMap.put("bankDetails", bankDetails);

    mockContract = new Contract();
    mockContract.setContractId("contract123");
    mockContract.setOrganizationId("org123");

    mockClient = new Client();
    mockClient.setClientId("client123");
    mockClient.setOrganizationId("org123");

    mockInvoiceRequest = new InvoiceRequest();
    mockInvoiceRequest.setContractId("contract123");
    mockInvoiceRequest.setBillingDate(new Date());
    mockInvoiceRequest.setDueDate(new Date());
    mockInvoiceRequest.setAmount(100.0);
    mockInvoiceRequest.setCurrency("USD");
    mockInvoiceRequest.setNotes(List.of("Test note 1"));
    mockInvoiceRequest.setTasks(List.of(new Task()));
    mockInvoiceRequest.setClientId("client123");
    mockInvoiceRequest.setProjectId("project123");
    mockInvoiceRequest.setVat(20);
    mockInvoiceRequest.setDaysLeftForPayment("30");
    mockInvoiceRequest.setInvoicePeriod(new InvoicePeriod());

    mockInvoice = new Invoice();
    mockInvoice.setId("invoiceDbId123");
    mockInvoice.setInvoiceId("INV0001");
    mockInvoice.setContractId("contract123");
    mockInvoice.setOrganizationId("org123");
    mockInvoice.setAmount(100.0);
    mockInvoice.setCurrency("USD");
    mockInvoice.setDueDate(new Date());
    mockInvoice.setStatus(InvoiceStatus.PENDING);
    mockInvoice.setBillingDate(new Date());
    mockInvoice.setRemittanceRef("TESINV0001");
    mockInvoice.setClientId("client123");
    mockInvoice.setProjectId("project123");
    mockInvoice.setNotes(List.of("Test note 1"));
    mockInvoice.setTasks(List.of(new Task()));
    mockInvoice.setVat(20);
    mockInvoice.setDaysLeftForPayment("30");
    mockInvoice.setInvoicePeriod(new InvoicePeriod());
    mockInvoice.setTaxId("ORGTAX123");
    mockInvoice.setPaymentDetails(
        new PaymentDetails("Org Acc", "Org Bank", "1234567890", "ORGB000123"));
    mockInvoice.setAmountInWords("ONE HUNDRED dollars");

    mockFileClientResponse = new LinkedHashMap<>();
    mockFileClientResponse.put("id", "fileId123");
    mockFileClientResponse.put("name", "invoice_INV0001.pdf");
    mockFileClientResponse.put("fileType", "application/pdf");
    mockFileClientResponse.put("entityId", "INV0001");
    mockFileClientResponse.put("entityType", "project");
  }

  @Test
  void generateInvoiceForContract_Success() throws Exception {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class);
        MockedStatic<UUID> mockedUUID = mockStatic(UUID.class);
        MockedStatic<AmountToWordsUtil> mockedAmountToWordsUtil =
            mockStatic(AmountToWordsUtil.class)) {

      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);
      mockedUserContext
          .when(
              () ->
                  UserContext.setLoggedInUser(
                      anyString(), anyString(), anyString(), anyMap(), anySet(), anyString()))
          .thenAnswer(invocation -> null);

      UUID mockUuid = mock(UUID.class);
      when(mockUuid.toString()).thenReturn("0123456789abcdef");
      mockedUUID.when(UUID::randomUUID).thenReturn(mockUuid);

      mockedAmountToWordsUtil
          .when(() -> AmountToWordsUtil.convertToWords(anyDouble()))
          .thenReturn("ONE HUNDRED");

      when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockContract);
      when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockClient);
      when(invoiceRepository.save(any(Invoice.class))).thenReturn(mockInvoice);

      Map<String, Object> orgAccountDetails = new LinkedHashMap<>();
      orgAccountDetails.put("taxId", "ORGTAX123");
      Map<String, Object> accountResponse = new LinkedHashMap<>();
      accountResponse.put("accounts", orgAccountDetails);
      ResponseEntity<Object> orgResponse = new ResponseEntity<>(accountResponse, HttpStatus.OK);
      when(accountClient.getOrganizationById(anyString())).thenReturn(orgResponse);
      when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(accountResponse);

      when(invoicePDFGen.generatePDF(any(Contract.class), any(Invoice.class), any(Client.class)))
          .thenReturn("dummy pdf bytes".getBytes());

      when(fileClient.uploadFile(any(FileUploadRequest.class)))
          .thenReturn(new ResponseEntity<>(mockFileClientResponse, HttpStatus.OK));
      when(objectMapper.convertValue(any(LinkedHashMap.class), eq(File.class)))
          .thenReturn(new File());

      Invoice result = invoiceService.generateInvoiceForContract("contract123", mockInvoiceRequest);

      assertNotNull(result);
      assertEquals("invoiceDbId123", result.getId());
      assertEquals("INV0001", result.getInvoiceId());
      assertEquals("TESINV0001", result.getRemittanceRef());
      assertEquals("ORGTAX123", result.getTaxId());
      assertEquals("fileId123", result.getInvoiceFileId());
      assertEquals(InvoiceStatus.PENDING, result.getStatus());
      assertEquals("ONE HUNDRED dollars", result.getAmountInWords());
      assertNotNull(result.getPaymentDetails());
      assertEquals("Org Acc", result.getPaymentDetails().getAccountName());

      verify(contractRepository, times(1))
          .findByContractIdAndOrganizationId("contract123", "org123");
      verify(clientRepository, times(1)).findByClientIdAndOrganizationId("client123", "org123");
      verify(invoiceRepository, times(2)).save(any(Invoice.class));
      verify(accountClient, times(1)).getOrganizationById("org123");
      verify(invoicePDFGen, times(1))
          .generatePDF(any(Contract.class), any(Invoice.class), any(Client.class));
      verify(fileClient, times(1)).uploadFile(any(FileUploadRequest.class));
    }
  }

  @Test
  void generateInvoiceForContract_ContractNotFound() {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(null);

      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () ->
                  invoiceService.generateInvoiceForContract(
                      "nonExistentContract", mockInvoiceRequest));

      assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorResponse().getCode());
      assertEquals("Contract not found for provided ID", exception.getErrorResponse().getMessage());
      verify(invoiceRepository, never()).save(any(Invoice.class));
    }
  }

  @Test
  void generateInvoiceForContract_PdfGenerationOrUploadFailed() throws Exception {

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class);
        MockedStatic<UUID> mockedUUID = mockStatic(UUID.class);
        MockedStatic<AmountToWordsUtil> mockedAmountToWordsUtil =
            mockStatic(AmountToWordsUtil.class)) {

      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      UUID mockUuid = mock(UUID.class);
      when(mockUuid.toString()).thenReturn("0123456789abcdef");
      mockedUUID.when(UUID::randomUUID).thenReturn(mockUuid);

      mockedAmountToWordsUtil
          .when(() -> AmountToWordsUtil.convertToWords(anyDouble()))
          .thenReturn("ONE HUNDRED");

      when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockContract);
      when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockClient);
      when(invoiceRepository.save(any(Invoice.class))).thenReturn(mockInvoice);

      Map<String, Object> orgAccountDetails = new LinkedHashMap<>();
      orgAccountDetails.put("taxId", "ORGTAX123");
      Map<String, Object> accountResponse = new LinkedHashMap<>();
      accountResponse.put("accounts", orgAccountDetails);
      ResponseEntity<Object> orgResponse = new ResponseEntity<>(accountResponse, HttpStatus.OK);
      when(accountClient.getOrganizationById(anyString())).thenReturn(orgResponse);
      when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(accountResponse);

      when(invoicePDFGen.generatePDF(any(Contract.class), any(Invoice.class), any(Client.class)))
          .thenThrow(new RuntimeException("PDF generation error"));

      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> invoiceService.generateInvoiceForContract("contract123", mockInvoiceRequest));

      assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorResponse().getCode());
      assertEquals("Failed to upload invoice PDF", exception.getErrorResponse().getMessage());
      verify(invoiceRepository, times(1)).save(any(Invoice.class));
      verify(fileClient, never()).uploadFile(any(FileUploadRequest.class));
    }
  }

  @Test
  void getInvoiceById_Success() {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      when(invoiceRepository.findByInvoiceIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockInvoice);

      Invoice result = invoiceService.getInvoiceById("invoiceId123");

      assertNotNull(result);
      assertEquals("invoiceDbId123", result.getId());
      verify(invoiceRepository, times(1))
          .findByInvoiceIdAndOrganizationId("invoiceId123", "org123");
    }
  }

  @Test
  void getInvoiceById_NotFound() {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      when(invoiceRepository.findByInvoiceIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(null);

      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> invoiceService.getInvoiceById("nonExistentInvoiceId"));

      assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorResponse().getCode());
      assertEquals("Invoice not found", exception.getErrorResponse().getMessage());
    }
  }

  @Test
  void getInvoicesByContractId_Success() {
    List<Invoice> invoices = List.of(mockInvoice);
    when(invoiceRepository.findByContractId(anyString())).thenReturn(invoices);

    List<Invoice> result = invoiceService.getInvoicesByContractId("contract123");

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals("invoiceDbId123", result.get(0).getId());
    verify(invoiceRepository, times(1)).findByContractId("contract123");
  }

  @Test
  void getInvoicesByContractId_NoInvoicesFound() {
    when(invoiceRepository.findByContractId(anyString())).thenReturn(Collections.emptyList());

    List<Invoice> result = invoiceService.getInvoicesByContractId("contract123");

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(invoiceRepository, times(1)).findByContractId("contract123");
  }

  @Test
  void markInvoiceAsPaid_Success() {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      when(invoiceRepository.findByInvoiceIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(mockInvoice);
      when(invoiceRepository.save(any(Invoice.class))).thenReturn(mockInvoice);

      Invoice result = invoiceService.markInvoiceAsPaid("invoiceId123");

      assertNotNull(result);
      assertEquals(InvoiceStatus.PAID, result.getStatus());
      verify(invoiceRepository, times(1))
          .findByInvoiceIdAndOrganizationId("invoiceId123", "org123");
      verify(invoiceRepository, times(1)).save(mockInvoice);
    }
  }

  @Test
  void markInvoiceAsPaid_InvoiceNotFound() {
    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext
          .when(UserContext::getLoggedInUserOrganization)
          .thenReturn(mockOrganizationMap);

      when(invoiceRepository.findByInvoiceIdAndOrganizationId(anyString(), anyString()))
          .thenReturn(null);

      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> invoiceService.markInvoiceAsPaid("nonExistentInvoiceId"));

      assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorResponse().getCode());
      assertEquals("Invoice not found", exception.getErrorResponse().getMessage());
      verify(invoiceRepository, never()).save(any(Invoice.class));
    }
  }
}

