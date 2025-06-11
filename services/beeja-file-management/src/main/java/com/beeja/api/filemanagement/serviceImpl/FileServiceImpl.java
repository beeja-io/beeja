package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.config.properties.AllowedContentTypes;
import com.beeja.api.filemanagement.enums.ErrorCode;
import com.beeja.api.filemanagement.enums.ErrorType;
import com.beeja.api.filemanagement.exceptions.FileAccessException;
import com.beeja.api.filemanagement.exceptions.FileTypeMismatchException;
import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.repository.FileRepository;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileService;
import com.beeja.api.filemanagement.service.FileStorageService;
import com.beeja.api.filemanagement.utils.BuildErrorMessage;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import com.beeja.api.filemanagement.utils.helpers.FileExtensionHelpers;
import com.beeja.api.filemanagement.utils.helpers.SizeConverter;
import com.mongodb.MongoWriteException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private AllowedContentTypes allowedContentTypes;
  @Autowired private FileStorageService fileStorage;
  @Autowired private FileRepository fileRepository;
  @Autowired private FileStorageService fileStorageService;

  @Override
  public File uploadFile(FileUploadRequest file) throws Exception {
    File savedFile = null;
    try {
      if (!FileExtensionHelpers.isValidContentType(
          file.getFile().getContentType(), allowedContentTypes.getAllowedTypes())) {
        log.error(Constants.INVALID_FILE_FORMATS + file.getFile().getContentType());
        throw new FileTypeMismatchException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.INVALID_REQUEST,
                ErrorCode.INVALID_FILE_FORMATS,
                Constants.INVALID_FILE_FORMATS));
      }

      String fileName =
          (file.getName() != null) ? file.getName() : file.getFile().getOriginalFilename();

      if (file.getEntityType() == null) {
        file.setEntityType(Constants.EMPLOYEE_ENTITY_TYPE);
      }

      File fileEntity = new File();
      fileEntity.setFileSize(SizeConverter.formatFileSize(file.getFile().getSize()));
      fileEntity.setName(fileName);
      fileEntity.setEntityId(file.getEntityId());
      fileEntity.setDescription(file.getDescription());
      fileEntity.setFileFormat(
          FileExtensionHelpers.getExtension(file.getFile().getOriginalFilename()));
      fileEntity.setEntityType(file.getEntityType());
      fileEntity.setFileType(file.getFileType() != null ? file.getFileType() : "General");

      savedFile = fileRepository.save(fileEntity);

      try {
        fileStorage.uploadFile(file.getFile(), savedFile);
      } catch (Exception e) {
        log.error("Error occurred while uploading file: {}", e.getMessage());
      }

      return savedFile;
    } catch (MongoWriteException e) {
      log.error(Constants.MONGO_UPLOAD_FAILED);
      throw new MongoFileUploadException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.MONGO_UPLOAD_FAILED, Constants.MONGO_UPLOAD_FAILED));
    } catch (IllegalStateException e) {
      if (savedFile != null) fileRepository.delete(savedFile);
      log.error(Constants.FILE_UPLOAD_FAILED);
      throw new FileAccessException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.IO_ERROR, ErrorCode.FILE_UPLOAD_FAILED, Constants.FILE_UPLOAD_FAILED));
    } catch (FileTypeMismatchException e) {
      log.error(Constants.INVALID_FILE_FORMATS + file.getFile().getContentType());
      throw new FileTypeMismatchException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INVALID_REQUEST,
              ErrorCode.INVALID_FILE_FORMATS,
              Constants.INVALID_FILE_FORMATS + file.getFile().getContentType()));
    } catch (Exception e) {
      log.error(Constants.SERVICE_DOWN_ERROR, e.getMessage());
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.SERVICE_ERROR, ErrorCode.SERVICE_DOWN, Constants.SERVICE_DOWN_ERROR));
    }
  }

  @Override
  public File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception {
    try {
      String orgId = UserContext.getLoggedInUserOrganization().get("id").toString();
      File file = fileRepository.findByOrganizationIdAndId(orgId, fileId);

      if (file == null) {
        log.error(Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId);
        throw new FileNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.INVALID_REQUEST,
                ErrorCode.FILE_NOT_FOUND,
                Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId));
      }
      fileStorage.updateFile(file, fileUploadRequest.getFile());
      return file;

    } catch (IOException | IllegalStateException e) {
      log.error(Constants.FILE_UPDATE_FAILED + fileId, e.getMessage());
      throw new FileAccessException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.IO_ERROR,
              ErrorCode.FILE_UPDATE_FAILED,
              Constants.FILE_UPDATE_FAILED + fileId));
    } catch (Exception e) {
      log.error(Constants.SERVICE_DOWN_ERROR, e.getMessage());
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.SERVICE_ERROR, ErrorCode.SERVICE_DOWN, Constants.SERVICE_DOWN_ERROR));
    }
  }

  @Override
  public File getFileById(String fileId) throws FileNotFoundException {
    Optional<File> file = fileRepository.findById(fileId);
    return file.orElseThrow(
        () -> {
          log.error(Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId);
          return new FileNotFoundException(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.INVALID_REQUEST,
                  ErrorCode.FILE_NOT_FOUND,
                  Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId));
        });
  }

  @Override
  public File uploadOrUpdateFile(FileUploadRequest fileUploadRequest) throws Exception {
    try {
      File file =
          fileRepository.findByEntityIdAndFileTypeAndOrganizationId(
              fileUploadRequest.getEntityId(),
              fileUploadRequest.getFileType(),
              (String) UserContext.getLoggedInUserOrganization().get("id"));
      File newFile;
      if (file != null) {
        newFile = updateFile(file.getId(), fileUploadRequest);
      } else {
        newFile = uploadFile(fileUploadRequest);
      }
      return newFile;

    } catch (FileTypeMismatchException
        | MongoFileUploadException
        | FileAccessException
        | FileNotFoundException e) {
      log.error(Constants.ERROR_UPLOAD_UPDATE, e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error(Constants.SERVICE_DOWN_ERROR, e.getMessage());
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.SERVICE_ERROR, ErrorCode.SERVICE_DOWN, Constants.SERVICE_DOWN_ERROR));
    }
  }

  @Override
  public FileResponse listofFileByEntityId(String entityId, int page, int size) throws Exception {
    try {
      MatchOperation matchStage =
              Aggregation.match(
                      Criteria.where("entityId")
                              .is(entityId)
                              .and("organizationId")
                              .is(UserContext.getLoggedInUserOrganization().get("id").toString())
                              .and("fileType")
                              .not().regex("^profilepicture$", "i"));
      SkipOperation skipStage =
              Aggregation.skip((long) (page - 1) * size);
      LimitOperation limitStage = Aggregation.limit(size); // Limit to the specified size
      Aggregation aggregation = Aggregation.newAggregation(matchStage, skipStage, limitStage);

      Query query = new Query();
      query.addCriteria(Criteria.where("entityId").is(entityId));
      query.addCriteria(
              Criteria.where("organizationId")
                      .is(UserContext.getLoggedInUserOrganization().get("id").toString()));
      query.addCriteria(
              Criteria.where("fileType").not().regex("^profilepicture$", "i"));

      List<File> documents =
              mongoTemplate.aggregate(aggregation, File.class, File.class).getMappedResults();
      HashMap<String, Object> metadata = new HashMap<>();
      metadata.put("totalSize", mongoTemplate.count(query, File.class));
      FileResponse response = new FileResponse();
      response.setMetadata(metadata);
      response.setFiles(documents);
      return response;
    } catch (Exception e) {
      log.error(Constants.SERVICE_DOWN_ERROR, e.getMessage());
      throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                      ErrorType.SERVICE_ERROR, ErrorCode.SERVICE_DOWN, Constants.SERVICE_DOWN_ERROR));
    }
  }

  @Override
  public FileDownloadResult downloadFile(String fileId) throws Exception {
    File file =
        fileRepository
            .findById(fileId)
            .orElseThrow(
                () -> {
                  log.error(Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId);
                  return new FileNotFoundException(
                      BuildErrorMessage.buildErrorMessage(
                          ErrorType.INVALID_REQUEST,
                          ErrorCode.FILE_NOT_FOUND,
                          Constants.NO_FILE_FOUND_WITH_GIVEN_ID + fileId));
                });
    return new FileDownloadResult(
        new ByteArrayResource(fileStorage.downloadFile(file)),
        file.getCreatedBy(),
        file.getEntityId(),
        file.getOrganizationId(),
        file.getName());
  }

  @Override
  public File deleteFile(String id) throws Exception {
    File fileToBeDeleted =
        fileRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.error(Constants.NO_FILE_FOUND_WITH_GIVEN_ID + id);
                  return new FileNotFoundException(
                      BuildErrorMessage.buildErrorMessage(
                          ErrorType.INVALID_REQUEST,
                          ErrorCode.FILE_NOT_FOUND,
                          Constants.NO_FILE_FOUND_WITH_GIVEN_ID + id));
                });
    fileStorage.deleteFile(fileToBeDeleted);
    fileRepository.delete(fileToBeDeleted);
    return fileToBeDeleted;
  }
}
