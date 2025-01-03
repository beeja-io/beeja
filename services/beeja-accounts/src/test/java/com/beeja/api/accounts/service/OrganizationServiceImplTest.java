package com.beeja.api.accounts.service;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.OrganizationExceptions;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.serviceImpl.OrganizationServiceImpl;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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
