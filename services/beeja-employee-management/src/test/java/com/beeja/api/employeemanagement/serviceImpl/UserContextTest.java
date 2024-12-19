package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.utils.UserContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

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
