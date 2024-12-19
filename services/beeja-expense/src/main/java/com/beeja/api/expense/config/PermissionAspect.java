package com.beeja.api.expense.config;

import com.beeja.api.expense.annotations.HasPermission;
import com.beeja.api.expense.annotations.RequireAllPermissions;
import com.beeja.api.expense.exceptions.CustomAccessDeniedException;
import com.beeja.api.expense.utils.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.beeja.api.expense.utils.Constants.NO_REQUIRED_PERMISSIONS;

@Aspect
@Component
public class PermissionAspect {

  @Before("@annotation(hasPermission)")
  public void checkPermission(HasPermission hasPermission) throws CustomAccessDeniedException {
    String[] requiredPermissions = hasPermission.value();
    List<String> userPermissions = getUserPermissions();

    boolean hasRequiredPermission =
        Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);

    if (!hasRequiredPermission) {
      throw new CustomAccessDeniedException(NO_REQUIRED_PERMISSIONS);
    }
  }

  @Before("@annotation(requireAllPermissions)")
  public void checkAllPermissions(RequireAllPermissions requireAllPermissions)
      throws CustomAccessDeniedException {
    HasPermission[] requiredPermissions = requireAllPermissions.value();
    List<String> userPermissions = getUserPermissions();

    for (HasPermission permissionAnnotation : requiredPermissions) {
      String[] permissions = permissionAnnotation.value();

      boolean hasRequiredPermissions =
          Arrays.stream(permissions).allMatch(userPermissions::contains);

      if (!hasRequiredPermissions) {
        throw new CustomAccessDeniedException(NO_REQUIRED_PERMISSIONS);
      }
    }
  }

  private List<String> getUserPermissions() {
    Set<String> loggedInUserPermissions = UserContext.getLoggedInUserPermissions();
    return new ArrayList<>(loggedInUserPermissions);
  }
}
