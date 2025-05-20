package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.exceptions.UnAuthorisedException;
import com.beeja.api.projectmanagement.model.dto.File;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.FileDownloadResultMetaData;
import com.beeja.api.projectmanagement.responses.FileResponse;
import com.beeja.api.projectmanagement.service.FileService;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Objects;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired FileClient fileClient;

    @Override
    public FileResponse listOfFileByEntityId(String entityId, int page, int size) {
        if (UserContext.getLoggedInUserPermissions()
                .contains(PermissionConstants.READ_ALL_DOCUMENTS)){
            ResponseEntity<FileResponse> responseEntity;
            try{
                responseEntity = fileClient.getAllFilesByEntityId(entityId, page, size);
            } catch (Exception e) {
                log.error(
                        Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "Entity Id: {}, error: {}",
                        entityId,
                        e.getMessage());
                throw new FeignClientException(
                        Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE);
            }
            FileResponse files = responseEntity.getBody();
            return files;
        } else {
            throw new UnAuthorisedException(
                    Constants.UNAUTHORISED_TO_READ_OTHERS_DOCUMENTS);
        }
    }

    @Override
    public File uploadFile(FileUploadRequest fileUploadRequest) throws Exception {
        if(UserContext.getLoggedInUserPermissions()
                .contains(PermissionConstants.CREATE_ALL_DOCUMENT)){
            ResponseEntity<?> fileResponse;
            try{
                fileResponse = fileClient.uploadFile(fileUploadRequest);
            } catch (Exception e) {
                log.error(
                        Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE + ", error: {}", e.getMessage());
                throw new FeignClientException(
                        Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE);
            }

            LinkedHashMap<String, Object> responseBody =
                    (LinkedHashMap<String, Object>) fileResponse.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(responseBody, File.class);
        } else {
            throw new UnAuthorisedException(
                    Constants.UNAUTHORISED_TO_CREATE_DOCUMENTS);
        }
    }

    @Override
    public File deleteFile(String fileId) throws Exception {
        ResponseEntity<?> response;
        try {
            response = fileClient.getFileById(fileId);
        }
        catch (Exception e){
            log.error(
                    Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE + "File Id : {}, error :  {}.",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(
                    Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE);
        }

        LinkedHashMap<String, Object> responseBody =
                (LinkedHashMap<String, Object>) response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        File file = objectMapper.convertValue(responseBody, File.class);
        if(!Objects.equals(file.getEntityType(), "client")) {
            throw new UnAuthorisedException(
                    Constants.UNAUTHORISED_ACCESS);
        }
        if(!UserContext.getLoggedInUserPermissions()
                .contains(PermissionConstants.DELETE_ALL_DOCUMENT)
            && !Objects.equals(file.getCreatedBy(), UserContext.getLoggedInEmployeeId())){
            throw new UnAuthorisedException(
                    Constants.UNAUTHORISED_ACCESS);
        }

        ResponseEntity<?> deletedFile;
        try{
            deletedFile = fileClient.deleteFile(fileId);
        } catch (Exception e) {
            throw new FeignClientException(
                    Constants.ERROR_IN_DELETING_FILE_FROM_FILE_SERVICE);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            LinkedHashMap<String, Object> responseBodyDeleted =
                    (LinkedHashMap<String, Object>) deletedFile.getBody();
            return mapper.convertValue(responseBodyDeleted, File.class);
        } catch (Exception e){
            throw new Exception(
                    Constants.SOMETHING_WENT_WRONG);
        }
    }

    @Override
    public File updateFile(String fileId, FileUploadRequest fileUploadRequest) throws Exception {
        File file;
        try {
            ResponseEntity<Object> response = fileClient.getFileById(fileId);
            if (response == null || response.getBody() == null){
                log.error(Constants.NULL_RESPONSE_FROM_FILE_CLIENT + fileId);
                throw new FeignClientException(
                        Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE);
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
                    Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE);

        }

        if(!UserContext.getLoggedInUserPermissions()
                .contains(PermissionConstants.UPDATE_DOCUMENT)){
            throw new UnAuthorisedException(
                    Constants.UNAUTHORISED_ACCESS);
        }

        try {
            ResponseEntity<Object> response = fileClient.updateFile(fileId, fileUploadRequest);
            if(response == null || response.getBody() == null){
                log.error(Constants.NULL_RESPONSE_FROM_FILE_CLIENT + fileId);
                throw  new FeignClientException(
                        Constants.ERROR_IN_UPDATING_FILE_FROM_FILE_SERVICE);
            }

            LinkedHashMap<String, Object> responseBody =
                    (LinkedHashMap<String, Object>) response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(responseBody, File.class);
        } catch (Exception e) {
            log.error(
                    Constants.ERROR_IN_UPDATING_FILE_FROM_FILE_SERVICE + "FileId : {}, error :  {}.",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(
                    Constants.ERROR_IN_UPDATING_FILE_FROM_FILE_SERVICE);
        }
    }

    @Override
    public ByteArrayResource downloadFile(String fileId) throws Exception{

        String fileFormat;
        try {
            ResponseEntity<?> response = fileClient.getFileById(fileId);
            LinkedHashMap<String,Object> responseBody = (LinkedHashMap<String, Object>) response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            File file = objectMapper.convertValue(responseBody, File.class);
            fileFormat = file.getFileFormat();
            if(!Objects.equals(file.getEntityType(),"client")){
                log.error(Constants.UNAUTHORISED_ACCESS);
                throw new UnAuthorisedException(
                        Constants.UNAUTHORISED_ACCESS
                );
            }
        } catch (Exception e) {
            log.error(Constants.ERROR_IN_FETCHING_FILE_FROM_FILE_SERVICE
                            + " file Id: {}, error: {}",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(
                    Constants.FILE_NOT_FOUND + fileId);
        }

        try {
            ResponseEntity<byte[]> fileResponse = fileClient.downloadFile(fileId);

            byte[] fileData = fileResponse.getBody();
            FileDownloadResultMetaData fileDownloadResultMetaData = getMetaData(fileResponse,fileFormat);

            return  new ByteArrayResource(Objects.requireNonNull(fileData)){
                @Override
                public String getFilename(){
                    return fileDownloadResultMetaData.getFileName() != null
                            ? fileDownloadResultMetaData.getFileName()
                            : "project_Beeja";
                }
            };
        } catch (Exception e){
            log.error(
                    Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE + "File Id : {}, error: {}",
                    fileId,
                    e.getMessage());
            throw new FeignClientException(Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE);
        }
    }

    private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse,String fileFormat) {
        HttpHeaders headers = fileResponse.getHeaders();
        String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String createdBy = headers.getFirst("createdby");
        String organizationId = headers.getFirst("organizationid");
        String entityId = headers.getFirst("entityId");
        String filename = headers.getFirst("filename");

        if ((filename == null || filename.isBlank()) && contentDisposition != null && !contentDisposition.isEmpty()) {
            int startIndex = contentDisposition.indexOf("filename=\"") + 10;
            int endIndex = contentDisposition.lastIndexOf("\"");
            if (endIndex != -1) {
                filename = contentDisposition.substring(startIndex, endIndex);
            }
        }
        return new FileDownloadResultMetaData(filename+"."+fileFormat, createdBy, entityId, organizationId);
    }
}
