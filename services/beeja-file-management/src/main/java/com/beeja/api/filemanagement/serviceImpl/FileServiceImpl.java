package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.exceptions.FileAccessException;
import com.mongodb.MongoWriteException;
import com.beeja.api.filemanagement.config.properties.AllowedContentTypes;
import com.beeja.api.filemanagement.exceptions.FileNotFoundException;
import com.beeja.api.filemanagement.exceptions.FileTypeMismatchException;
import com.beeja.api.filemanagement.exceptions.MongoFileUploadException;
import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.repository.FileRepository;
import com.beeja.api.filemanagement.requests.FileUploadRequest;
import com.beeja.api.filemanagement.response.FileDownloadResult;
import com.beeja.api.filemanagement.response.FileResponse;
import com.beeja.api.filemanagement.service.FileService;
import com.beeja.api.filemanagement.service.FileStorage;
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import com.beeja.api.filemanagement.utils.helpers.FileExtensionHelpers;
import com.beeja.api.filemanagement.utils.helpers.SizeConverter;
import com.beeja.api.filemanagement.utils.methods.ServiceMethods;
import org.springframework.beans.BeanUtils;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class FileServiceImpl implements FileService {


  @Autowired private MongoTemplate mongoTemplate;

  @Autowired AllowedContentTypes allowedContentTypes;


  @Autowired FileStorage fileStorage;

  @Autowired FileRepository fileRepository;

  @Override
  public File uploadFile(FileUploadRequest file) throws Exception {

    File savedFile = null;
    try {

      if (!FileExtensionHelpers.isValidContentType(
          file.getFile().getContentType(), allowedContentTypes.getAllowedTypes())) {
        throw new FileTypeMismatchException(
            Constants.INVALID_FILE_FORMATS + " " + Constants.SUPPORTED_FILE_TYPES);
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
          FileExtensionHelpers.getExtension(
              Objects.requireNonNull(file.getFile().getOriginalFilename())));
      fileEntity.setEntityType(file.getEntityType());

      if (file.getFileType() != null) {
        fileEntity.setFileType(file.getFileType());
      } else {
        fileEntity.setFileType("General");
      }

      savedFile = fileRepository.save(fileEntity);

      fileStorage.uploadFile(file.getFile(), savedFile);
      return savedFile;
    } catch (MongoWriteException e) {
      throw new MongoFileUploadException(Constants.MONGO_UPLOAD_FAILED);
    } catch (IOException | IllegalStateException e) {
      assert savedFile != null;
      fileRepository.delete(savedFile);
      throw new FileAccessException(Constants.GCS_UPLOAD_FAILED);
    } catch (FileTypeMismatchException e) {
      throw new FileTypeMismatchException(e.getMessage());
    } catch (Exception e) {
      throw new Exception(Constants.SERVICE_DOWN_ERROR);
    }
  }

  @Override
  public File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception {
    File fileToUpdate =
        fileRepository.findByOrganizationIdAndId(
            UserContext.getLoggedInUserOrganization().get("id").toString(), fileId);
    File spareFile = fileToUpdate;

    if (fileToUpdate == null) {
      throw new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID);
    }


    if (fileUploadRequest.getFile() != null) {
      // Deleting existing file
      try {
       fileStorage.deleteFile(spareFile);
      } catch (FileAccessException e) {
        throw new FileAccessException(Constants.GCS_FILE_DELETE_ERROR);
      }

    }

    // Update file properties
    String[] nullProperties = ServiceMethods.getNullPropertyNames(fileUploadRequest);
    BeanUtils.copyProperties(fileUploadRequest, fileToUpdate, nullProperties);
    File fileUpdated = fileRepository.save(fileToUpdate);
    if(fileUploadRequest.getFile() != null){
      try {

        fileStorage.uploadFile(fileUploadRequest.getFile(), fileUpdated);
      } catch (Exception e) {
        e.printStackTrace();
        throw new Exception("Error while uploading file: " + e.getMessage());
      }
    }

    // If no new file uploaded, update path based on new fields
//    if (fileUploadRequest.getFile() == null && blob != null) {
//      try {
//        String newPath = generateGCSPath(fileUpdated);
//        BlobId newBlobId = BlobId.of(gcsProperties.getBucket().getName(), newPath);
//        BlobInfo newBlobInfo =
//            BlobInfo.newBuilder(newBlobId).setContentType(blob.getContentType()).build();
//
//        // Copy the content of the existing blob to the new blob
//        byte[] blobContent = blob.getContent();
//        storage.create(newBlobInfo, blobContent);
//        // Delete the existing blob
//        blob.delete();
//      } catch (Exception e) {
//        fileRepository.save(spareFile);
//        throw new Exception(Constants.FAILED_TO_UPDATE_BLOB + e.getMessage());
//      }
//    }

    return fileUpdated;
  }

  @Override
  public File getFileById(String fileId) throws FileNotFoundException {
    Optional<File> file = fileRepository.findById(fileId);
    return file.orElseThrow(() -> new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID));
  }

  @Override
  public File uploadOrUpdateFile(FileUploadRequest fileUploadRequest) throws Exception {
    File file =
        fileRepository.findByEntityIdAndFileTypeAndOrganizationId(
            fileUploadRequest.getEntityId(),
            fileUploadRequest.getFileType(),
            (String) UserContext.getLoggedInUserOrganization().get("id"));
    File profilePic;
    if (file != null) {
      profilePic = updateFile(file.getId(), fileUploadRequest);
    } else {
      profilePic = uploadFile(fileUploadRequest);
    }
    return profilePic;
  }

  @Override
  public FileResponse listofFileByEntityId(String entityId, int page, int size) throws Exception {
    try {
      MatchOperation matchStage =
          Aggregation.match(
              Criteria.where("entityId")
                  .is(entityId)
                  .and("organizationId")
                  .is(UserContext.getLoggedInUserOrganization().get("id").toString()));
      SkipOperation skipStage =
          Aggregation.skip((long) (page - 1) * size); // Skip documents for pagination
      LimitOperation limitStage = Aggregation.limit(size); // Limit to the specified size
      Aggregation aggregation = Aggregation.newAggregation(matchStage, skipStage, limitStage);

      Query query = new Query();
      query.addCriteria(Criteria.where("entityId").is(entityId));
      query.addCriteria(
          Criteria.where("organizationId")
              .is(UserContext.getLoggedInUserOrganization().get("id").toString()));
      List<File> documents =
          mongoTemplate.aggregate(aggregation, File.class, File.class).getMappedResults();
      HashMap<String, Object> metadata = new HashMap<>();
      metadata.put("totalSize", mongoTemplate.count(query, File.class));
      FileResponse response = new FileResponse();
      response.setMetadata(metadata);
      response.setFiles(documents);
      return response;
    } catch (Exception e) {
      throw new Exception(Constants.SERVICE_DOWN_ERROR + e.getMessage());
    }
  }

  @Override
  public FileDownloadResult downloadFile(String fileId) throws Exception {
    try {
      File file =
          fileRepository.findByOrganizationIdAndId(
              UserContext.getLoggedInUserOrganization().get("id").toString(), fileId);
      String path;
      if (file != null) {
//        path = generateGCSPath(file);
//        Blob blob = storage.get(gcsProperties.getBucket().getName(), path);
//
//        ByteArrayResource resource =
//            new ByteArrayResource(blob.getContent()) {
//              @Override
//              public String getFilename() {
//                return file.getId() + "." + file.getFileFormat();
//              }
//            };

        return new FileDownloadResult(new ByteArrayResource(fileStorage.downloadFile(file)), file.getCreatedBy(), file.getEntityId(), file.getOrganizationId());
      } else {
        throw new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID);
      }
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  public File deleteFile(String id) throws Exception {
    //        TODO - Update service for client entityType & client ID
    try {
      File fileToBeDeleted =
          fileRepository.findByOrganizationIdAndId(
              UserContext.getLoggedInUserOrganization().get("id").toString(), id);
      String path;
      if (fileToBeDeleted != null) {

        try {
          fileRepository.delete(fileToBeDeleted);
          return fileToBeDeleted;
        } catch (Exception e) {
          throw new Exception(Constants.MONGO_FILE_DELETE_ERROR);
        }
      } else {
        throw new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID);
      }

    } catch (FileAccessException e) {
      throw new FileAccessException(e.getMessage());
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException(e.getMessage());
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
}
