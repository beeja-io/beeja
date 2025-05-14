package com.beeja.api.expense.serviceImpl;

import com.beeja.api.expense.client.FileClient;
import com.beeja.api.expense.enums.ErrorCode;
import com.beeja.api.expense.enums.ErrorType;
import com.beeja.api.expense.exceptions.FeignClientException;
import com.beeja.api.expense.exceptions.UnAuthorisedException;
import com.beeja.api.expense.response.FileDownloadResultMetaData;
import com.beeja.api.expense.response.FileResponse;
import com.beeja.api.expense.service.ReceiptService;
import com.beeja.api.expense.utils.BuildErrorMessage;
import com.beeja.api.expense.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Objects;

@Service
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

  @Autowired FileClient fileClient;

  @Override
  public ByteArrayResource downloadFile(String fileId) throws Exception {

    /*Checking File Type
     * If file tye is not expense, then throwing an error
     */
    try {
      ResponseEntity<?> response = fileClient.getFileById(fileId);
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) response.getBody();

      ObjectMapper objectMapper = new ObjectMapper();
      FileResponse file = objectMapper.convertValue(responseBody, FileResponse.class);
      if (!Objects.equals(file.getEntityType(), "expense")) {
        log.error(Constants.UNAUTHORISED_ACCESS);
        throw new UnAuthorisedException(
                BuildErrorMessage.buildErrorMessage(
                        ErrorType.AUTHORIZATION_ERROR,
                        ErrorCode.PERMISSION_MISSING,
                        Constants.UNAUTHORISED_ACCESS));
      }
    } catch (Exception e) {
      log.error(Constants.FILE_SERVICE_FETCH_FAILED);
      throw new FeignClientException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.FEIGN_CLIENT_ERROR,
                      ErrorCode.FILE_SERVICE_COMMUNICATION_FAILED,
                      Constants.FILE_SERVICE_FETCH_FAILED));
    }

    try {
      ResponseEntity<byte[]> fileResponse = fileClient.downloadFile(fileId);
      byte[] fileData = fileResponse.getBody();
      FileDownloadResultMetaData finalMetaData = getMetaData(fileResponse);

      return new ByteArrayResource(Objects.requireNonNull(fileData)) {
        @Override
        public String getFilename() {
          return finalMetaData.getFileName() != null
              ? finalMetaData.getFileName()
              : "expense_Beeja";
        }
      };
    } catch (Exception e) {
      log.error(Constants.FILE_DOWNLOAD_FAILED);
      throw new FeignClientException(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.FEIGN_CLIENT_ERROR,
                      ErrorCode.FILE_DOWNLOAD_FAILED,
                      Constants.FILE_DOWNLOAD_FAILED));
    }
  }

  private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse) {
    HttpHeaders headers = fileResponse.getHeaders();
    String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
    String createdBy = headers.getFirst("createdby");
    String organizationId = headers.getFirst("organizationid");
    String entityId = headers.getFirst("entityId");
    String filename = null;

    if (contentDisposition != null && !contentDisposition.isEmpty()) {
      int startIndex = contentDisposition.indexOf("filename=\"") + 10;
      int endIndex = contentDisposition.lastIndexOf("\"");
      if (endIndex != -1) {
        filename = contentDisposition.substring(startIndex, endIndex);
      }
    }

    return new FileDownloadResultMetaData(filename, createdBy, entityId, organizationId);
  }
}
