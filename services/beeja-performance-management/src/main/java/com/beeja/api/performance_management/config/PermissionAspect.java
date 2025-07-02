package com.beeja.api.performance_management.config;

import static com.beeja.api.performance_management.utils.Constants.NO_REQUIRED_PERMISSIONS;

import com.beeja.api.performance_management.annotations.HasPermission;
import com.beeja.api.performance_management.exceptions.CustomAccessDeniedException;
import com.beeja.api.performance_management.utils.UserContext;
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
  public void checkPermission(HasPermission hasPermission) throws CustomAccessDeniedException {
    String[] requiredPermissions = hasPermission.value();
    List<String> userPermissions = getUserPermissions();

    boolean hasRequiredPermission =
        Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);
    if (!hasRequiredPermission) {
      throw new CustomAccessDeniedException(NO_REQUIRED_PERMISSIONS);
    }
  }

  private List<String> getUserPermissions() {
    Set<String> loggedInUserPermissions = UserContext.getLoggedInUserPermissions();
    return new ArrayList<>(loggedInUserPermissions);
  }
}
