package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.Loan;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import com.beeja.api.financemanagementservice.repository.LoanRepository;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.requests.PdfMultipartFile;
import com.beeja.api.financemanagementservice.requests.SubmitLoanRequest;
import com.beeja.api.financemanagementservice.service.LoanService;
import com.beeja.api.financemanagementservice.requests.FileUploadRequest;
import com.beeja.api.financemanagementservice.client.FileClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.beeja.api.financemanagementservice.Utils.Constants.GET_ALL_LOANS;
import static com.beeja.api.financemanagementservice.Utils.Constants.PDF;
import static com.beeja.api.financemanagementservice.Utils.Constants.zipFile;

/**
 * Implementation of the LoanService interface providing operations for managing loan requests and
 * loan details.
 */
@Service
@Slf4j
public class LoanServiceImpl implements LoanService {
  private final MongoOperations mongoOperations;

  @Autowired
  public LoanServiceImpl(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Autowired LoanRepository loanRepository;

  @Autowired
  AccountClient accountClient;

  @Autowired FileClient fileClient;

  /**
   * Changes the status of a loan based on the provided loan ID.
   *
   * @param loanId The ID of the loan to update.
   * @param status The new status of the loan ("APPROVE", "REJECT", or others).
   * @param message Optional message or reason for status change.
   * @throws LoanNotFound If the loan with the specified ID is not found.
   */
  @Override
  public void changeLoanStatus(String loanId, String status, String message) {
    status = status.toUpperCase();

    Loan optionalLoan =
        loanRepository.findByIdAndOrganizationId(
            loanId, UserContext.getLoggedInUserOrganization().get("id").toString());

    if (optionalLoan != null) {
      if (status.equals("APPROVE")) {
        optionalLoan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(optionalLoan);
      } else if (status.equals("REJECT")) {
        optionalLoan.setStatus(LoanStatus.REJECTED);
        optionalLoan.setRejectionReason(message);
        loanRepository.save(optionalLoan);
      } else {
        optionalLoan.setStatus(LoanStatus.WAITING);
        loanRepository.save(optionalLoan);
      }
    } else {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.LOAN_NOT_FOUND,
              Constants.LOAN_NOT_FOUND + " with loanNumber " + loanId));
    }
  }

  /**
   * Submits a new loan request.
   *
   * @param loanRequest The request object containing loan details.
   * @return The saved Loan entity.
   * @throws Exception If an error occurs during loan submission.
   */
  @Override
  public Loan submitLoanRequest(SubmitLoanRequest loanRequest) throws Exception {
    Loan loan = new Loan();
    loan.setEmployeeId(UserContext.getLoggedInEmployeeId());
    loan.setLoanType(loanRequest.getLoanType());
    loan.setAmount(loanRequest.getAmount());
    loan.setMonthlyEMI(loanRequest.getMonthlyEMI());
    loan.setPurpose(loanRequest.getPurpose());
    loan.setEmiTenure(loanRequest.getEmiTenure());
    loan.setEmiStartDate(loanRequest.getEmiStartDate());
    try {
      OrganizationPattern organizationPattern = null;
      try {
        organizationPattern = accountClient.getActivePatternByType("LOAN_ID_PATTERN").getBody();
      } catch (Exception e) {
        log.warn("Failed to retrieve organization pattern, proceeding without prefix.");
      }

      long existingLoanCount = loanRepository.countByOrganizationId(
              UserContext.getLoggedInUserOrganization().get("id").toString()
      );

      long newLoanNumber = existingLoanCount + 1;
      String finalLoanNumber = (organizationPattern != null && organizationPattern.getPrefix() != null)
              ? organizationPattern.getPrefix() + newLoanNumber
              : String.valueOf(newLoanNumber);

      loan.setLoanNumber(finalLoanNumber);
    } catch (FeignException e) {
      log.error("Failed to generate loan number: {}", e.getMessage());
      throw new RuntimeException("Failed to generate loan number", e);
    } catch (Exception e) {
      log.error("Error occurred: {}", e.getMessage());
      throw new RuntimeException(e.getMessage(), e);
    }

    loan.setCreatedAt(Date.from(Instant.now()));
    try {
      loan.setStatus(LoanStatus.WAITING);
      return loanRepository.save(loan);
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.SERVER_ERROR, Constants.ERROR_SUBMITTING_LOAN_REQUEST));
    }
  }

  /**
   * Retrieves all loans associated with the logged-in user's organization.
   *
   * @return List of Loan entities.
   * @throws Exception If an error occurs while retrieving loans.
   */
  @Override
  public List<Loan> getAllLoans() throws Exception {
    try {
      return loanRepository.findAllByOrganizationId(
          UserContext.getLoggedInUserOrganization().get("id").toString());
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.SERVICE_ERROR, ErrorCode.SERVER_ERROR, Constants.SERVICE_DOWN_ERROR));
    }
  }

  /**
   * Retrieves all loans associated with a specific employee ID within the logged-in user's
   * organization.
   *
   * @param employeeId The ID of the employee whose loans are to be retrieved.
   * @return List of Loan entities.
   * @throws Exception If an error occurs while retrieving loans.
   */
  @Override
  public List<Loan> getAllLoansByEmployeeId(String employeeId) throws Exception {
    try {
      if (UserContext.getLoggedInUserPermissions().contains(GET_ALL_LOANS)) {
        return loanRepository.findAllByEmployeeIdAndOrganizationId(
            employeeId, UserContext.getLoggedInUserOrganization().get("id").toString());
      }
      return loanRepository.findAllByEmployeeIdAndOrganizationId(
          UserContext.getLoggedInEmployeeId(),
          UserContext.getLoggedInUserOrganization().get("id").toString());
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.SERVICE_ERROR, ErrorCode.SERVER_ERROR, Constants.SERVICE_DOWN_ERROR));
    }
  }

  /**
   * Uploads bulk payslips in a zip file asynchronously.
   *
   * @param bulkPayslipRequest  The request object containing bulk payslip details.
   * @param authorizationHeader Authorization header for API calls.
   * @throws Exception If an error occurs during bulk payslip upload.
   */
  @Override
  public void uploadBulkPaySlips(BulkPayslipRequest bulkPayslipRequest, String authorizationHeader) {
    MultipartFile zipFile = bulkPayslipRequest.getZipFile();
    List<String> successList = new ArrayList<>();
    List<String> failureList = new ArrayList<>();

    try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {

      for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
        String fileName = entry.getName();

        if (fileName == null || fileName.trim().isEmpty()) {
          failureList.add("Unknown file with missing name.");
          continue;
        }

        if (!fileName.toLowerCase().endsWith(".pdf")) {
          failureList.add(fileName + " - Unsupported file type.");
          continue;
        }

        try {
          byte[] pdfBytes = zipInputStream.readAllBytes();
          String employeeId = extractEmployeeIdFromPdf(pdfBytes);

          if (employeeId == null || employeeId.isEmpty()) {
            failureList.add(fileName + " - Employee ID not found.");
            continue;
          }

          String finalFileName = employeeId + "_" + bulkPayslipRequest.getMonth() + "_" + bulkPayslipRequest.getYear() + ".pdf";

          MultipartFile payslipFile = new PdfMultipartFile(finalFileName, finalFileName, pdfBytes);

          FileUploadRequest fileUploadRequest = new FileUploadRequest();
          fileUploadRequest.setFile(payslipFile);
          fileUploadRequest.setName(finalFileName);
          fileUploadRequest.setFileType("pdf");
          fileUploadRequest.setEntityType("employee");
          fileUploadRequest.setEntityId(employeeId);
          fileUploadRequest.setDescription("Payslip for employee " + employeeId);

          ResponseEntity<?> response = fileClient.uploadFile(fileUploadRequest, authorizationHeader);

          successList.add(finalFileName + " - uploaded successfully.");
        } catch (Exception e) {
          failureList.add(fileName + " - Upload failed: " + e.getMessage());
          log.warn("Failed to upload {}: {}", fileName, e.getMessage(), e);
        }
      }
    } catch (IOException e) {
      failureList.add("ZIP file could not be processed: " + e.getMessage());
      log.warn("Failed to process zip file: {}", e.getMessage(), e);
    }

    log.info("Payslip upload complete. Success: {}, Failures: {}", successList.size(), failureList.size());
    successList.forEach(msg -> log.info("SUCCESS: " + msg));
    failureList.forEach(msg -> log.warn("FAILURE: " + msg));
  }


  private String extractEmployeeIdFromPdf(byte[] pdfBytes) throws IOException {
    try (PDDocument document = PDDocument.load(pdfBytes)) {
      PDFTextStripper stripper = new PDFTextStripper();
      String text = stripper.getText(document);

      Pattern pattern = Pattern.compile("Employee ID\\s*[:\\-]?\\s*([A-Z]+\\d+)", Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        return matcher.group(1);
      }
    }
    throw new RuntimeException("Employee ID not found in PDF");
  }
}
