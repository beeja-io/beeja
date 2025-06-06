package com.beeja.api.accounts.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.serviceImpl.OrganizationServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class OrganizationServiceImplTest {

  @Mock private OrganizationRepository organizationRepository;

  @Mock private UserRepository userRepository;

  @Mock private RolesRepository roleRepository;

  @Mock private EmployeeFeignClient employeeFeignClient;

  @Mock private FeatureToggleRepository featureToggleRepository;

  @InjectMocks private OrganizationServiceImpl organizationService;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetAllUsersByOrganizationId() throws Exception {
    // Arrange
    String organizationId = "1";

    // Mock the organization repository to return empty (organization not found)
    when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

    // Act and Assert
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              organizationService.getAllUsersByOrganizationId(organizationId);
            });

    // Verify the exception message
    String expectedMessage =
        "RESOURCE_NOT_FOUND_ERROR,ORGANIZATION_NOT_FOUND,No Organization Found with provided Id";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}
