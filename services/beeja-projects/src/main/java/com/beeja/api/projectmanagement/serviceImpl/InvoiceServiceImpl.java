package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.enums.InvoiceStatus;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.PaymentDetails;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
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
import com.beeja.api.projectmanagement.utils.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




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
      String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();
    Contract contract =
        contractRepository.findByContractIdAndOrganizationId(
            contractId, orgId);

    if (contract == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.NOT_FOUND,
              ErrorCode.RESOURCE_NOT_FOUND,
              "Contract not found for provided ID"));
    }

      if (request.getInvoiceId() != null && !request.getInvoiceId().isBlank()) {
          boolean invoiceExists = invoiceRepository.existsByInvoiceIdAndOrganizationId(request.getInvoiceId(), orgId);
          if (invoiceExists) {
              throw new ResourceAlreadyFoundException(
                      BuildErrorMessage.buildErrorMessage(
                              ErrorType.VALIDATION_ERROR,
                              ErrorCode.DUPLICATE_ENTRY,
                              Constants.INVOICE_ID_ALREADY_EXISTS));
          }
      }

      if (request.getRemittanceRef() != null && !request.getRemittanceRef().isBlank()) {
          boolean remittanceExists = invoiceRepository.existsByRemittanceRefAndOrganizationId(request.getRemittanceRef(), orgId);
          if (remittanceExists) {
              throw new ResourceAlreadyFoundException(
                      BuildErrorMessage.buildErrorMessage(
                              ErrorType.VALIDATION_ERROR,
                              ErrorCode.DUPLICATE_ENTRY,
                              Constants.REMITTANCE_REF_ALREADY_EXISTS));
          }
      }

    Invoice invoice = new Invoice();
    invoice.setInvoiceId(request.getInvoiceId());
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
    invoice.setRemittanceRef(request.getRemittanceRef());
    invoice.setCreatedByName(UserContext.getLoggedInUserName());


    // setting TAX ID of a ORG
    ResponseEntity<Object> orgResponse = accountClient.getOrganizationById(orgId);


   Map<String, Object> responseMap = objectMapper.convertValue(orgResponse.getBody(), new TypeReference<Map<String, Object>>() {});
    Map<String, Object> accountsMap = (Map<String, Object>) responseMap.get("accounts");
    String taxId = accountsMap.get("taxNumber").toString();
        if (request.getTaxId() != null && !request.getTaxId().isBlank() && !request.getTaxId().equals(taxId)) {
            invoice.setTaxId(request.getTaxId());
            log.info(Constants.CUSTOMER_TAX_ID, request.getTaxId());
        } else {
            invoice.setTaxId(taxId);
            log.info(Constants.ORG_TAX_ID, taxId);
        }

    PaymentDetails invoicePaymentDetails = new PaymentDetails();
    Map<String, Object> orgBankDetails = (Map<String, Object>) UserContext.getLoggedInUserOrganization().get("bankDetails");
    invoicePaymentDetails.setAccountName((String) orgBankDetails.get("accountName"));
    invoicePaymentDetails.setBankName((String) orgBankDetails.get("bankName"));
    invoicePaymentDetails.setAccountNumber((String) orgBankDetails.get("accountNumber"));
    invoicePaymentDetails.setIfscNumber((String) orgBankDetails.get("ifscNumber"));
    invoice.setPaymentDetails(invoicePaymentDetails);

    invoice = invoiceRepository.save(invoice);


        Client client = clientRepository.findByClientIdAndOrganizationId(invoice.getClientId(), orgId);
    try {

      byte[] invoicePdfBytes = invoicePDFGen.generatePDF(contract, invoice, client, request.getPrimaryAddress(),
              request.getBillingAddress());
      MultipartFile multipartFile = new InMemoryMultipartFile("invoice.pdf", invoicePdfBytes);

      FileUploadRequest fileUpload = new FileUploadRequest();
      fileUpload.setFile(multipartFile);
      fileUpload.setName("invoice_" + invoice.getInvoiceId());
      fileUpload.setFileType("application/pdf");
      fileUpload.setEntityId(invoice.getInvoiceId());
      fileUpload.setEntityType(Constants.ENTITY_TYPE_INVOICE);
      fileUpload.setDescription("Invoice PDF for contract " + contractId);

      ResponseEntity<Object> uploadedFile = fileClient.uploadFile(fileUpload);
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) uploadedFile.getBody();

      objectMapper.convertValue(responseBody, File.class);

      invoice.setInvoiceFileId(responseBody.get("id").toString());

      invoice = invoiceRepository.save(invoice);
    } catch (Exception e) {
      log.error(Constants.INVOICE_PDF_FAILED, e.getMessage());
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR, ErrorCode.VALIDATION_ERROR, Constants.INVOICE_PDF_FAILED));
    }
    log.info(Constants.INVOICE_PDF_SUCCESS);
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
              ErrorType.DB_ERROR, ErrorCode.RESOURCE_NOT_FOUND, Constants.INVOICE_NOT_FOUND));
    }
      return invoice;
  }
    @Override
    public InvoiceIdentifiersResponse generateInvoiceIdentifiers(String contractId) {
        try {
            String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
            Contract contract = contractRepository.findByContractIdAndOrganizationId(contractId, organizationId);
            if (contract == null) {
                throw new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.CONTRACT_NOT_FOUND
                        )
                );
            }

            if (contract.getEndDate() != null && contract.getEndDate().before(new Date())) {
                log.warn(Constants.CONTRACT_ENDED, contractId, contract.getEndDate());
                throw new IllegalStateException(Constants.CONTRACT_ENDED);
            }
            LocalDate now = LocalDate.now();
            String yearMonth = now.format(DateTimeFormatter.ofPattern(Constants.YEAR_MONTH));
            String prefix = Constants.INVOICE_PREFIX + yearMonth + "-";
            Map<String, Object> org = UserContext.getLoggedInUserOrganization();
            if (organizationId == null || org.get(Constants.NAME) == null) {
                log.error(Constants.ORGANIZATION_MISSING);
                throw new IllegalStateException(Constants.ORGANIZATION_MISSING);
            }

            String orgName = org.get(Constants.NAME).toString();
            String orgPrefix = orgName.length() >= 3 ? orgName.substring(0, 3).toUpperCase() : orgName.toUpperCase();
            long count = invoiceRepository.countByOrganizationIdAndInvoiceIdStartingWith(organizationId, prefix);
            String serialNumber = String.format("%02d", count + 1);
            String invoiceId = prefix + serialNumber;
            String remittanceReferenceNumber = orgPrefix + "-" + invoiceId;

            log.info(Constants.GEN_INVOICE_ID_REMITTANCE, invoiceId, remittanceReferenceNumber);

            return new InvoiceIdentifiersResponse(invoiceId, remittanceReferenceNumber);

        } catch (Exception e) {
            log.error(Constants.GEN_INVOICE_ID_FAILED, contractId);
            throw new RuntimeException(Constants.GEN_INVOICE_ID_FAILED + contractId);
        }
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
