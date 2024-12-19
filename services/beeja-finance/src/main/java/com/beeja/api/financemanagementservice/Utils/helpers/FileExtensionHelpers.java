package com.beeja.api.financemanagementservice.Utils.helpers;

public class FileExtensionHelpers {

  //    Used to extract extension from original file name while uploading file
  public static String getExtension(String fileName) {
    String[] parts = fileName.split("\\.");
    return parts.length > 1 ? parts[1].toLowerCase() : "";
  }
}
