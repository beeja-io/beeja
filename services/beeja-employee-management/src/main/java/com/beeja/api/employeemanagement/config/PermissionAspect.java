package com.beeja.api.employeemanagement.config;

import static com.beeja.api.employeemanagement.utils.Constants.UNAUTHORISED_ACCESS;

import com.beeja.api.employeemanagement.annotations.HasPermission;
import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
import com.beeja.api.employeemanagement.exceptions.UnAuthorisedException;
import com.beeja.api.employeemanagement.utils.BuildErrorMessage;
import com.beeja.api.employeemanagement.utils.UserContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

  @Before("@annotation(hasPermission)")
  public void checkPermission(HasPermission hasPermission) {
    String[] requiredPermissions = hasPermission.value();
    List<String> userPermissions = getUserPermissions();

    boolean hasRequiredPermission =
        Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);

    if (!hasRequiredPermission) {
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR, ErrorCode.PERMISSION_MISSING, UNAUTHORISED_ACCESS));
    }
  }

  private List<String> getUserPermissions() {
    Set<String> loggedInUserPermissions = UserContext.getLoggedInUserPermissions();
    return new ArrayList<>(loggedInUserPermissions);
  }
}
