package com.beeja.api.projectmanagement.config;

import static com.beeja.api.projectmanagement.utils.Constants.NO_REQUIRED_PERMISSIONS;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.exceptions.CustomAccessDeniedException;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect that enforces permission-based access control on methods
 * annotated with {@link HasPermission}.
 */
@Aspect
@Component
public class PermissionAspect {

  /**
   * Checks if the logged-in user has at least one of the required permissions
   * before proceeding with the method execution.
   * @param hasPermission the {@link HasPermission} annotation containing required permissions
   * @throws CustomAccessDeniedException if the user lacks all required permissions
   */
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

  /**
   * Retrieves the permissions of the currently logged-in user from {@link UserContext}.
   * @return a list of permission strings assigned to the user
   */
  private List<String> getUserPermissions() {
    Set<String> loggedInUserPermissions = UserContext.getLoggedInUserPermissions();
    return new ArrayList<>(loggedInUserPermissions);
  }
}
