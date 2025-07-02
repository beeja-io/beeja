package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.client.FileClient;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
import com.beeja.api.employeemanagement.exceptions.FeignClientException;
import com.beeja.api.employeemanagement.exceptions.UnAuthorisedException;
import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.response.FileDownloadResultMetaData;
import com.beeja.api.employeemanagement.response.FileResponse;
import com.beeja.api.employeemanagement.service.FileService;
import com.beeja.api.employeemanagement.utils.BuildErrorMessage;
import com.beeja.api.employeemanagement.utils.Constants;
import com.beeja.api.employeemanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

  @Autowired FileClient fileClient;

  @Autowired private MongoTemplate mongoTemplate;

  @Override
  public FileResponse listOfFileByEntityId(String entityId, int page, int size) throws Exception {
    if (UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.READ_ALL_EMPLOYEE_DOCUMENT)
        || (Objects.equals(entityId, UserContext.getLoggedInEmployeeId()))) {
      ResponseEntity<FileResponse> responseEntity;
      try {
        responseEntity =
            (ResponseEntity<FileResponse>) fileClient.getAllFilesByEntityId(entityId, page, size);
      } catch (Exception e) {
        log.error(
            Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "Entity Id: {}, error: {}",
            entityId,
            e.getMessage());
        throw new FeignClientException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.UNABLE_TO_FETCH_DETAILS,
                Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE));
      }
      FileResponse files = responseEntity.getBody();
      return files;
    } else {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS));
    }
  }

  @Override
  public ByteArrayResource downloadFile(String fileId) throws Exception {
    //    Checking Permissions
    ResponseEntity<?> response;
    try {
      response = fileClient.getFileById(fileId);
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "File Id: {}, error: {}",
          fileId,
          e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE));
    }
    LinkedHashMap<String, Object> responseBody = (LinkedHashMap<String, Object>) response.getBody();

    ObjectMapper objectMapper = new ObjectMapper();
    File file = objectMapper.convertValue(responseBody, File.class);
    if (!Objects.equals(file.getEntityType(), "employee")) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_ACCESS));
    }
    if (!UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.READ_ALL_EMPLOYEE_DOCUMENT)
        && !Objects.equals(file.getEntityId(), UserContext.getLoggedInEmployeeId())) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_ACCESS));
    }
    ResponseEntity<byte[]> fileResponse;
    try {
      fileResponse = fileClient.downloadFile(fileId);
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE + "File Id : {}, error: {}",
          fileId,
          e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE));
    }
    try {
      byte[] fileData = fileResponse.getBody();
      FileDownloadResultMetaData finalMetaData = getMetaData(fileResponse);

      return new ByteArrayResource(Objects.requireNonNull(fileData)) {

        @Override
        public String getFilename() {
          return finalMetaData.getFileName() != null ? finalMetaData.getFileName() : "beeja_file";
        }
      };
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_CONVERTING_FILE_TO_DOWNLOADABLE_FILE + "File Id : {}, error : {}",
          fileId,
          e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.SOMETHING_WENT_WRONG,
              Constants.ERROR_IN_CONVERTING_FILE_TO_DOWNLOADABLE_FILE));
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

  @Override
  public File deleteFile(String fileId) throws Exception {
    //    Checking Permissions
    ResponseEntity<?> response;
    try {
      response = fileClient.getFileById(fileId);
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "File Id : {}, error :  {}.",
          fileId,
          e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE));
    }
    LinkedHashMap<String, Object> responseBody = (LinkedHashMap<String, Object>) response.getBody();

    ObjectMapper objectMapper = new ObjectMapper();
    File file = objectMapper.convertValue(responseBody, File.class);
    if (!Objects.equals(file.getEntityType(), "employee")) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_ACCESS));
    }
    if (!UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.DELETE_ALL_EMPLOYEE_DOCUMENT)
        && !Objects.equals(file.getCreatedBy(), UserContext.getLoggedInEmployeeId())) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_ACCESS));
    }

    ResponseEntity<?> deletedFile;
    try {
      deletedFile = fileClient.deleteFile(fileId);
    } catch (Exception e) {
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE));
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      LinkedHashMap<String, Object> responseBodyDownload =
          (LinkedHashMap<String, Object>) deletedFile.getBody();
      return mapper.convertValue(responseBodyDownload, File.class);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR, ErrorCode.SOMETHING_WENT_WRONG, Constants.SOMETHING_WENT_WRONG));
    }
  }

  @Override
  public File uploadFile(FileUploadRequest fileUpload) throws Exception {
    if (Objects.equals(fileUpload.getEntityId(), UserContext.getLoggedInEmployeeId())
        || UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.CREATE_ALL_EMPLOYEE_DOCUMENT)) {
      ResponseEntity<?> fileResponse;
      try {
        fileResponse = fileClient.uploadFile(fileUpload);
      } catch (Exception e) {
        log.error(
            Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE + ", error: {}", e.getMessage());
        throw new FeignClientException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.SERVER_ERROR,
                Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE));
      }
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) fileResponse.getBody();

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.convertValue(responseBody, File.class);
    } else {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_TO_WRITE_OTHERS_DOCUMENTS));
    }
  }

  @Override
  public File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception {

    //    Checking Permissions
    File file;
    try {
      ResponseEntity<Object> response = fileClient.getFileById(fileId);
      if (response == null || response.getBody() == null) {
        log.error("Received null response from file service for fileId: {}", fileId);
        throw new FeignClientException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.UNABLE_TO_FETCH_DETAILS,
                "Error in Fetching Data from File Service"));
      }
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) response.getBody();

      ObjectMapper objectMapper = new ObjectMapper();
      file = objectMapper.convertValue(responseBody, File.class);
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "FileId : {}, error :  {}.",
          fileId,
          e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE));
    }
    if (!UserContext.getLoggedInUserPermissions().contains(PermissionConstants.UPLOAD_FILE)
        && !UserContext.getLoggedInEmployeeId().equals(file.getEntityId())) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.PERMISSION_MISSING,
              Constants.UNAUTHORISED_ACCESS));
    }

    //    Actual Request to Update File
    try {
      ResponseEntity<Object> response = fileClient.updateFile(fileId, fileUploadRequest);
      if (response == null || response.getBody() == null) {
        log.error("Received null response from file service for fileId: {}", fileId);
        throw new FeignClientException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.SERVER_ERROR,
                "Error in Updating File in File Service"));
      }
      LinkedHashMap<String, Object> responseBody =
          (LinkedHashMap<String, Object>) response.getBody();
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.convertValue(responseBody, File.class);
    } catch (Exception e) {
      log.error(
          Constants.ERROR_IN_UPDATING_FILE_IN_FILE_SERVICE + "FileId : {}, error :  {}.",
          fileId,
          e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR,
              Constants.ERROR_IN_UPDATING_FILE_IN_FILE_SERVICE));
    }
  }

  @Override
  public File uploadOrUpdateFile(FileUploadRequest fileUpload) {
    ResponseEntity<?> fileResponse;
    try {
      fileResponse = fileClient.uploadOrUpdateFile(fileUpload);
    } catch (Exception e) {
      log.error(Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE + ", error: {}", e.getMessage());
      throw new FeignClientException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR,
              Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE));
    }
    LinkedHashMap<String, Object> responseBody =
        (LinkedHashMap<String, Object>) fileResponse.getBody();

    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(responseBody, File.class);
  }
}
