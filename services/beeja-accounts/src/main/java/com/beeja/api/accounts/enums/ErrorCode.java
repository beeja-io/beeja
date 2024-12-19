package com.beeja.api.accounts.enums;

public enum ErrorCode {
  SERVER_ERROR,
  UNABLE_TO_FETCH_DETAILS,
  MISSING_ACCESS_TOKEN,
  PERMISSION_MISSING,
  CANNOT_CHANGE_SELF_STATUS,
  CANNOT_CHANGE_SELF_ROLES,
  CANNOT_DELETE_SELF_ORGANIZATION,
  FIELD_VALIDATION_MISSING,
  FILE_NOT_FOUND,
  USER_NOT_FOUND,
  ROLE_NOT_FOUND,
  FEATURES_ARE_NOT_FOUND,
  ORGANIZATION_NOT_FOUND,
  EMPLOYEE_ALREADY_FOUND,
  ERROR_ASSIGNING_ROLE,
  RESOURCE_CREATING_ERROR,
  RESOURCE_DELETING_ERROR,
  ROLE_ALREADY_FOUND,
  ORGANIZATION_ALREADY_FOUND,
  RESOURCE_IN_USE,
  FILE_SIZE_LIMIT_EXCEEDED,
  ORGANIZATION_MEMORY_LIMIT_EXCEEDED,
  CANNOT_SAVE_CHANGES,
  RESOURCE_NOT_FOUND_ERROR,
  INVALID_EMPLOYMENT_TYPE_CODE,
  BAD_REQUEST,
  UNKNOWN_ERROR
}
