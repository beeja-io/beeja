package com.beeja.api.financemanagementservice.Utils.bulkpayslipsUtil;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.client.FileClient;
import com.beeja.api.financemanagementservice.client.NotificationClient;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import com.beeja.api.financemanagementservice.requests.FileUploadRequest;
import com.beeja.api.financemanagementservice.requests.PayslipEmailRequest;
import com.beeja.api.financemanagementservice.response.PDFResponse;
import feign.FeignException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class PaySlipProcessor {

  public static void processPayslips(
      List<MultipartFile> pdfFiles,
      BulkPayslipRequest bulkPayslipRequest,
      FileClient fileClient,
      AccountClient accountClient,
      NotificationClient notificationClient,
      String asyncAccessToken) {

    List<Map<String, Object>> successList = new ArrayList<>();

    try {
      List<PDFResponse> pdfResponses = TextExtractor.extractTextsFromPDFs(pdfFiles);

      for (PDFResponse pdfResponse : pdfResponses) {
        Map<String, Object> response =
            processPayslip(
                pdfResponse, bulkPayslipRequest, fileClient, accountClient, asyncAccessToken);

        if (response != null) {
          successList.add(response);
        }
      }

      notifyEmployees(successList, notificationClient, bulkPayslipRequest);
    } catch (Exception e) {
      log.error(Constants.ERROR_OCCORRED_DURING_BULK_PAY_SLIPS_UPLOAD, e);
    }
  }

  private static Map<String, Object> processPayslip(
      PDFResponse pdfResponse,
      BulkPayslipRequest bulkPayslipRequest,
      FileClient fileClient,
      AccountClient accountClient,
      String asyncAccessToken) {
    try {
      FileUploadRequest fileUploadRequest = new FileUploadRequest();
      fileUploadRequest.setEntityId(pdfResponse.getEntityId());
      fileUploadRequest.setName(
          Constants.PAYSLIP_ + bulkPayslipRequest.getMonth() + "_" + bulkPayslipRequest.getYear());
      fileUploadRequest.setFileType(Constants.PAYSLIP_ENTITY_TYPE);
      fileUploadRequest.setEntityType(Constants.PAYSLIP_ENTITY_TYPE);
      fileUploadRequest.setFile(pdfResponse.getPdfFile());
      fileClient.uploadFile(fileUploadRequest, asyncAccessToken);

      log.info(Constants.SUCCESSFULLY_UPLOADED + pdfResponse.getEntityId());
      ResponseEntity<?> result =
          accountClient.getUserByEmployeeId(pdfResponse.getEntityId(), asyncAccessToken);
      if (result.getStatusCode().is2xxSuccessful()) {
        Map<String, Object> accountClientResponseBody = (Map<String, Object>) result.getBody();
        if (accountClientResponseBody != null) return accountClientResponseBody;
      } else {
        log.error(Constants.USER_NOT_FOUND + pdfResponse.getEntityId());
      }
    } catch (FeignException.NotFound fe) {
      log.error(Constants.FEIGN_CLIENT_ERROR_NOT_FOUND + pdfResponse.getEntityId());
    } catch (FeignException fe) {
      log.error(
          Constants.FEIGN_CLIENT_ERROR
              + fe.getMessage()
              + Constants.FOR_EMPID
              + pdfResponse.getEntityId());
    } catch (Exception e) {
      log.error(Constants.ERROR_OCCORRED_DURING_BULK_PAY_SLIPS_UPLOAD, e);
    }
    return null;
  }

  private static void notifyEmployees(
      List<Map<String, Object>> successList,
      NotificationClient notificationClient,
      BulkPayslipRequest bulkPayslipRequest) {

    for (Map<String, Object> pdfResponse : successList) {
      sendSuccessEmail(pdfResponse, notificationClient, bulkPayslipRequest);
    }
  }

  private static void sendSuccessEmail(
      Map<String, Object> pdfResponse,
      NotificationClient notificationClient,
      BulkPayslipRequest bulkPayslipRequest) {
    try {
      Map<String, Object> organizations = (Map<String, Object>) pdfResponse.get("organizations");
      String organizationName = (String) organizations.get("name");
      PayslipEmailRequest payslipEmailRequest = new PayslipEmailRequest();
      payslipEmailRequest.setEmployeeName((String) pdfResponse.get("firstName"));
      payslipEmailRequest.setEmployeeId((String) pdfResponse.get("employeeId"));
      payslipEmailRequest.setOrganizationName(organizationName);
      payslipEmailRequest.setToMail("email");
      payslipEmailRequest.setYear(bulkPayslipRequest.getYear());
      payslipEmailRequest.setMonth(bulkPayslipRequest.getMonth());

      notificationClient.sendEmail(payslipEmailRequest, "Bearer " + UserContext.getAccessToken());

    } catch (Exception e) {
      log.error("Failed to send success email to " + pdfResponse.get("email"), e);
    }
  }
}
