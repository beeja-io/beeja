package com.beeja.api.employeemanagement.service;

import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import org.springframework.core.io.ByteArrayResource;

public interface ProfilePicService {

  File saveProfilePicture(FileUploadRequest file) throws Exception;

  File updateProfilePicture(String fileId, FileUploadRequest fileUploadRequest) throws Exception;

  ByteArrayResource getProfilePicById(String fileId) throws Exception;
}
