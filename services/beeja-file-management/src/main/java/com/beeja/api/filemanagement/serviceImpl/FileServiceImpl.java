package com.beeja.api.filemanagement.serviceImpl;

import com.beeja.api.filemanagement.config.properties.AllowedContentTypes;
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
import com.beeja.api.filemanagement.utils.Constants;
import com.beeja.api.filemanagement.utils.UserContext;
import com.beeja.api.filemanagement.utils.helpers.FileExtensionHelpers;
import com.mongodb.MongoWriteException;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired private AllowedContentTypes allowedContentTypes;
    @Autowired private FileStorageService fileStorage;
    @Autowired private FileRepository fileRepository;

    @Override
    public File uploadFile(FileUploadRequest file) throws Exception {
        File savedFile = null;
        try {
            if (!FileExtensionHelpers.isValidContentType(
                    file.getFile().getContentType(), allowedContentTypes.getAllowedTypes())) {
                throw new FileTypeMismatchException("Constants.INVALID_FILE_FORMATS");
            }

            String fileName = (file.getName() != null) ? file.getName() : file.getFile().getOriginalFilename();

            if (file.getEntityType() == null) {
                file.setEntityType(Constants.EMPLOYEE_ENTITY_TYPE);
            }

            File fileEntity = new File();
            fileEntity.setFileSize("SizeConverter.formatFileSize(file.getFile().getSize())");
            fileEntity.setName(fileName);
            fileEntity.setEntityId(file.getEntityId());
            fileEntity.setDescription(file.getDescription());
            fileEntity.setFileFormat(FileExtensionHelpers.getExtension(file.getFile().getOriginalFilename()));
            fileEntity.setEntityType(file.getEntityType());
            fileEntity.setFileType(file.getFileType() != null ? file.getFileType() : "General");

            savedFile = fileRepository.save(fileEntity);

            fileStorage.uploadFile(file.getFile(), savedFile);

            return savedFile;
        } catch (MongoWriteException e) {
            throw new MongoFileUploadException(Constants.MONGO_UPLOAD_FAILED);
        } catch (IOException | IllegalStateException e) {
            if (savedFile != null) fileRepository.delete(savedFile);
            throw new FileAccessException("Constants.FILE_UPLOAD_FAILED");
        } catch (FileTypeMismatchException e) {
            throw new FileTypeMismatchException(e.getMessage());
        } catch (Exception e) {
            log.error("Error while uploading file: ", e.getMessage());
            throw new Exception(Constants.SERVICE_DOWN_ERROR);
        }
    }

    @Override
    public File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception {
        File file = fileRepository.findByOrganizationIdAndId(UserContext.getLoggedInUserOrganization().get("id").toString(), fileId);
        fileStorage.updateFile(file, fileUploadRequest.getFile());
        return file;
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
        File newFile;
        if (file != null) {
            newFile = updateFile(file.getId(), fileUploadRequest);
        } else {
            newFile = uploadFile(fileUploadRequest);
        }
        return newFile;
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
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID));
        return new FileDownloadResult(new ByteArrayResource(fileStorage.downloadFile(file)),
                file.getCreatedBy(),
                file.getEntityId(),
                file.getOrganizationId());
    }

    @Override
    public File deleteFile(String id) throws Exception {
        File fileToBeDeleted = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(Constants.NO_FILE_FOUND_WITH_GIVEN_ID));

        fileStorage.deleteFile(fileToBeDeleted);
        fileRepository.delete(fileToBeDeleted);
        return fileToBeDeleted;
    }
}
