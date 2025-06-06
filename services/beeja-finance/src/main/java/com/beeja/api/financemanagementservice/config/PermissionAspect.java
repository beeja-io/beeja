package com.beeja.api.financemanagementservice.config;

import static com.beeja.api.financemanagementservice.Utils.Constants.NO_REQUIRED_PERMISSIONS;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.exceptions.CustomAccessDeniedException;
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
