package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.service.FileService;
import com.beeja.api.employeemanagement.service.ProfilePicService;
import com.beeja.api.employeemanagement.utils.Constants;
import com.beeja.api.employeemanagement.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class ProfilePicServiceImpl implements ProfilePicService {

  @Autowired FileService fileService;

  @Override
  public File saveProfilePicture(FileUploadRequest fileUploadRequest) throws Exception {

    if (fileUploadRequest.getFileType() == null) {
      fileUploadRequest.setFileType("ProfilePicture");
    }
    if (fileUploadRequest.getEntityType() == null) {
      fileUploadRequest.setEntityType("employee");
    }
    if (fileUploadRequest.getEntityId() == null) {
      fileUploadRequest.setEntityId(UserContext.getLoggedInEmployeeId());
    }
    try {
      return fileService.uploadFile(fileUploadRequest);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE);
    }
  }

  @Override
  public File updateProfilePicture(String fileId, FileUploadRequest fileUploadRequest)
      throws Exception {

    try {
      return fileService.updateFile(fileId, fileUploadRequest);
    } catch (Exception e) {
      throw new Exception(Constants.ERROR_IN_UPDATING_FILE_IN_FILE_SERVICE);
    }
  }

  @Override
  public ByteArrayResource getProfilePicById(String fileId) throws Exception {
    try {
      return fileService.downloadFile(fileId);
    } catch (Exception e) {
      throw new Exception(Constants.ERROR_IN_DOWNLOADING_FILE_FROM_FILE_SERVICE);
    }
  }
}
