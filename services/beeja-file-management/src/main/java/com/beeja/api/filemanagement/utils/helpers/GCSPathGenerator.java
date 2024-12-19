package com.beeja.api.filemanagement.utils.helpers;

import com.beeja.api.filemanagement.utils.UserContext;
import com.beeja.api.filemanagement.model.File;

import java.util.Objects;

public class GCSPathGenerator {
  public static String generateGCSPath(File file) {
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
