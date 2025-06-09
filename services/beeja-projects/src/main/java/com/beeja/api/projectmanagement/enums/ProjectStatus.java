package com.beeja.api.projectmanagement.enums;

public enum ProjectStatus {
  ACTIVE,
  INACTIVE,
  COMPLETED,
  ON_HOLD,
  NOT_STARTED,
  IN_PROGRESS,
  CANCELLED;

  public static ProjectStatus fromString(String status) {
    for (ProjectStatus projectStatus : ProjectStatus.values()) {
      if (projectStatus.name().equalsIgnoreCase(status)) {
        return projectStatus;
      }
    }
    throw new IllegalArgumentException(
        "No enum constant " + ProjectStatus.class.getCanonicalName() + "." + status);
  }
}
