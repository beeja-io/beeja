package com.beeja.api.employeemanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import com.beeja.api.employeemanagement.utils.UserContext;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class UserContextTest {

  @Test
  public void testMockStaticMethod() {
    try (MockedStatic<UserContext> mockedStatic = mockStatic(UserContext.class)) {
      mockedStatic
          .when(UserContext::getLoggedInUserPermissions)
          .thenReturn(Set.of("READ_ALL_EMPLOYEE_DOCUMENT"));
      Set<String> permissions = UserContext.getLoggedInUserPermissions();
      assertTrue(permissions.contains("READ_ALL_EMPLOYEE_DOCUMENT"));
    }
  }
}
