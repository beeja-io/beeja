package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.InvoiceStatus;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.PaymentDetails;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.InvoiceRepository;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import com.beeja.api.projectmanagement.service.InvoiceService;
import com.beeja.api.projectmanagement.utils.AmountToWordsUtil;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.InMemoryMultipartFile;
import com.beeja.api.projectmanagement.utils.InvoicePDFGen;
import com.beeja.api.projectmanagement.utils.PdfGenerationUtil;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired private InvoiceRepository invoiceRepository;

  @Autowired private ContractRepository contractRepository;

  @Autowired PdfGenerationUtil pdfGenerationUtil;

  @Autowired InvoicePDFGen invoicePDFGen;

  @Autowired ClientRepository clientRepository;

  @Autowired FileClient fileClient;

  @Autowired AccountClient accountClient;

  @Autowired private ObjectMapper objectMapper;

  @Override
  public Invoice generateInvoiceForContract(String contractId, InvoiceRequest request) {
    Contract contract =
        contractRepository.findByContractIdAndOrganizationId(
            contractId, UserContext.getLoggedInUserOrganization().get("id").toString());

    if (contract == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.NOT_FOUND,
              ErrorCode.RESOURCE_NOT_FOUND,
              "Contract not found for provided ID"));
    }

    Invoice invoice = new Invoice();
    invoice.setInvoiceId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    invoice.setContractId(contractId);
    invoice.setOrganizationId(contract.getOrganizationId());
    invoice.setAmount(request.getAmount());
    invoice.setDueDate(request.getDueDate());
    invoice.setStatus(InvoiceStatus.PENDING);
    invoice.setBillingDate(request.getBillingDate());
    invoice.setCurrency(request.getCurrency());
    invoice.setAmountInWords(AmountToWordsUtil.convertToWords(request.getAmount()) + " dollars");
    invoice.setClientId(request.getClientId());
    invoice.setProjectId(request.getProjectId());
    invoice.setNotes(request.getNotes());
    invoice.setTasks(request.getTasks());
    invoice.setVat(request.getVat());
    invoice.setDaysLeftForPayment(request.getDaysLeftForPayment());
    invoice.setInvoicePeriod(request.getInvoicePeriod());

    // taking Remittance Ref is ORG Name Prefix(3 letters) + invoiceId
    String orgName = UserContext.getLoggedInUserOrganization().get("name").toString();
    String prefix =
        orgName.length() >= 3 ? orgName.substring(0, 3).toUpperCase() : orgName.toUpperCase();
    String remittanceRef = prefix + invoice.getInvoiceId();
    invoice.setRemittanceRef(remittanceRef);

    // setting TAX ID of a ORG
    ResponseEntity<Object> orgResponse =
        accountClient.getOrganizationById(
            UserContext.getLoggedInUserOrganization().get("id").toString());
    Map<String, Object> responseMap =
        objectMapper.convertValue(
            orgResponse.getBody(), new TypeReference<Map<String, Object>>() { });
    Map<String, Object> accountsMap = (Map<String, Object>) responseMap.get("accounts");
    String taxId = accountsMap.get("taxId").toString();

    invoice.setTaxId(taxId);

    PaymentDetails invoicePaymentDetails = new PaymentDetails();
    Map<String, Object> orgBankDetails =
        (Map<String, Object>) UserContext.getLoggedInUserOrganization().get("bankDetails");
    invoicePaymentDetails.setAccountName((String) orgBankDetails.get("accountName"));
    invoicePaymentDetails.setBankName((String) orgBankDetails.get("bankName"));
    invoicePaymentDetails.setAccountNumber((String) orgBankDetails.get("accountNumber"));
    invoicePaymentDetails.setIfscNumber((String) orgBankDetails.get("ifscNumber"));

    invoice.setPaymentDetails(invoicePaymentDetails);

    invoice = invoiceRepository.save(invoice);

    Client client =
        clientRepository.findByClientIdAndOrganizationId(
            invoice.getClientId(),
            UserContext.getLoggedInUserOrganization().get("id").toString()); // for Client Details

    try {

      // byte[] pdfBytes = pdfGenerationUtil.generateInvoicePdf(invoice, contract);
      byte[] invoicepdfBytes = invoicePDFGen.generatePDF(contract, invoice, client);
      MultipartFile multipartFile = new InMemoryMultipartFile("invoice.pdf", invoicepdfBytes);

      FileUploadRequest fileUpload = new FileUploadRequest();
      fileUpload.setFile(multipartFile);
      fileUpload.setName("invoice_" + invoice.getInvoiceId());
      fileUpload.setFileType("application/pdf");
      fileUpload.setEntityId(invoice.getInvoiceId());
      fileUpload.setEntityType("project");
      fileUpload.setDescription("Invoice PDF for contract " + contractId);

      ResponseEntity<Object> uploadedFile = fileClient.uploadFile(fileUpload);
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) uploadedFile.getBody();

      objectMapper.convertValue(responseBody, File.class);

      invoice.setInvoiceFileId(responseBody.get("id").toString());

      invoice = invoiceRepository.save(invoice);
    } catch (Exception e) {
      log.error("Invoice PDF generation or upload failed: {}", e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR, ErrorCode.VALIDATION_ERROR, "Failed to upload invoice PDF"));
    }

    return invoice;
  }

  @Override
  public Invoice getInvoiceById(String invoiceId) {
    Invoice invoice =
        invoiceRepository.findByInvoiceIdAndOrganizationId(
            invoiceId, UserContext.getLoggedInUserOrganization().get("id").toString());
    if (invoice == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, "Invoice not found"));
    }
    return invoice;
  }

  @Override
  public List<Invoice> getInvoicesByContractId(String contractId) {
    return invoiceRepository.findByContractId(contractId);
  }

  @Override
  public Invoice markInvoiceAsPaid(String invoiceId) {
    Invoice invoice = getInvoiceById(invoiceId);
    invoice.setStatus(InvoiceStatus.PAID);
    return invoiceRepository.save(invoice);
  }
}
