package com.beeja.api.filemanagement.utils.helpers;

import com.beeja.api.filemanagement.model.File;
import com.beeja.api.filemanagement.utils.UserContext;

import java.util.Arrays;
import java.util.Objects;

public class FileExtensionHelpers {
  //    Used to check contentType of uploaded file
  public static boolean isValidContentType(String fileContentType, String[] allowedContentTypes) {
    return Arrays.asList(allowedContentTypes).contains(fileContentType);
  }

  //    Used to extract extension from original file name while uploading file
  public static String getExtension(String fileName) {
    String[] parts = fileName.split("\\.");
    return parts.length > 1 ? parts[1].toLowerCase() : "";
  }

    public static class FilePathGenerator {
      public static String generateFilePath(File file) {
        if (Objects.equals(file.getEntityType(), "expense")) {
          return "organizations/"
              + UserContext.getLoggedInUserOrganization().get("id")
              + "/"
              + file.getEntityType()
              + "/"
              + file.getId();
        }
        return "organizations/"
            + UserContext.getLoggedInUserOrganization().get("id")
            + "/"
            + file.getEntityType()
            + "/"
            + file.getEntityId()
            + "/"
            + file.getFileType()
            + "/"
            + file.getId().toString();
      }
    }
}
