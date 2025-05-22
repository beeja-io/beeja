package com.beeja.api.performance.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.beeja.api.performance.annotations.HasPermission;
import com.beeja.api.performance.exceptions.CustomAccessDeniedException;
import com.beeja.api.performance.utils.UserContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.beeja.api.performance.utils.Constants.NO_REQUIRED_PERMISSIONS;

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
