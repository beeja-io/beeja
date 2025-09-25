package tac.beeja.recruitmentapi.config;

import static tac.beeja.recruitmentapi.utils.Constants.NO_REQUIRED_PERMISSIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import tac.beeja.recruitmentapi.annotations.HasPermission;
import tac.beeja.recruitmentapi.exceptions.CustomAccessDeniedException;
import tac.beeja.recruitmentapi.utils.UserContext;

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
