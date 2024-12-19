package com.beeja.api.expense.utils.helpers;

import java.util.Arrays;

public class FileExtensionHelpers {
  //    Used to check contentType of uploaded file
  public static boolean isValidContentType(String fileContentType, String[] allowedContentTypes) {
    return Arrays.asList(allowedContentTypes).contains(fileContentType);
  }
}
