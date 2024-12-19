package com.beeja.api.filemanagement.utils.helpers;

import java.util.Arrays;

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
}
