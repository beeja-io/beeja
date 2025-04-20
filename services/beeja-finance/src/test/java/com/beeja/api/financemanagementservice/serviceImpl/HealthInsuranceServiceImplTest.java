package com.beeja.api.financemanagementservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.InstalmentType;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceCreationException;
import com.beeja.api.financemanagementservice.exceptions.HealthInsuranceNotFoundException;
import com.beeja.api.financemanagementservice.modals.HealthInsurance;
import com.beeja.api.financemanagementservice.repository.HealthInsuranceRepository;
import com.beeja.api.financemanagementservice.requests.HealthInsuranceRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class HealthInsuranceServiceImplTest {

  @Mock private HealthInsuranceRepository healthInsuranceRepository;

  @Autowired MockMvc mockMvc;

  @InjectMocks private HealthInsuranceServiceImpl healthInsuranceService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(healthInsuranceService).build();
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  @Test
  public void testSaveHealthInsurance_Success() throws Exception {
    // Arrange
    HealthInsuranceRequest request = new HealthInsuranceRequest();
    request.setEmployeeId("E123");
    request.setGrossPremium("1000.0");
    request.setInstalmentType(InstalmentType.MONTHLY);
    request.setInstalmentAmount(100.0);
    UserContext.setLoggedInUserOrganization(Map.of("id", "tac"));
    UserContext.setLoggedInUserEmail("test@example.com");
    when(healthInsuranceRepository.findByEmployeeId("E123")).thenReturn(Optional.empty());
    when(healthInsuranceRepository.save(any(HealthInsurance.class)))
        .thenReturn(new HealthInsurance());

    // Act
    HealthInsurance result = healthInsuranceService.saveHealthInsurance(request);

    // Assert
    assertNotNull(result);
    verify(healthInsuranceRepository, times(1)).save(any(HealthInsurance.class));
  }

  @Test
  public void testSaveHealthInsurance_Existing() {
    // Arrange
    HealthInsuranceRequest request = new HealthInsuranceRequest();
    request.setEmployeeId("E123");
    UserContext.setLoggedInUserOrganization(Map.of("id", "tac"));
    when(healthInsuranceRepository.findByEmployeeId("E123"))
        .thenReturn(Optional.of(new HealthInsurance()));

    // Act & Assert
    assertThrows(
        HealthInsuranceCreationException.class,
        () -> {
          healthInsuranceService.saveHealthInsurance(request);
        });
  }

  @Test
  public void testSaveHealthInsurance_Exception() {
    // Arrange
    HealthInsuranceRequest request = new HealthInsuranceRequest();
    request.setEmployeeId("E123");
    UserContext.setLoggedInUserOrganization(Map.of("id", "tac"));
    when(healthInsuranceRepository.findByEmployeeId("E123")).thenReturn(Optional.empty());
    when(healthInsuranceRepository.save(any(HealthInsurance.class)))
        .thenThrow(new DuplicateKeyException("Duplicate key"));

    // Act & Assert
    assertThrows(
        Exception.class,
        () -> {
          healthInsuranceService.saveHealthInsurance(request);
        });
  }

  @Test
  public void testUpdateHealthInsurance_Success() throws Exception {
    // Arrange
    HealthInsuranceRequest request = new HealthInsuranceRequest();
    request.setGrossPremium("1000.0");
    request.setInstalmentType(InstalmentType.MONTHLY);
    request.setInstalmentAmount(100.0);
    String employeeId = "E123";
    UserContext.setLoggedInUserOrganization(Map.of("id", "org1"));
    HealthInsurance existingInsurance = new HealthInsurance();
    when(healthInsuranceRepository.findByEmployeeIdAndOrganizationId(employeeId, "org1"))
        .thenReturn(Optional.of(existingInsurance));
    UserContext.setLoggedInUserEmail("test@example.com");
    when(healthInsuranceRepository.save(any(HealthInsurance.class))).thenReturn(existingInsurance);

    // Act
    HealthInsurance result = healthInsuranceService.updateHealthInsurance(request, employeeId);

    // Assert
    assertNotNull(result);
    assertEquals("1000.0", result.getGrossPremium());
    verify(healthInsuranceRepository, times(1)).save(any(HealthInsurance.class));
  }

  @Test
  public void testUpdateHealthInsurance_NotFound() {
    // Arrange
    HealthInsuranceRequest request = new HealthInsuranceRequest();
    String employeeId = "E123";
    UserContext.setLoggedInUserOrganization(Map.of("id", "org1"));
    when(healthInsuranceRepository.findByEmployeeIdAndOrganizationId(employeeId, "org1"))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        HealthInsuranceNotFoundException.class,
        () -> {
          healthInsuranceService.updateHealthInsurance(request, employeeId);
        });
  }

  @Test
  public void testDeleteByEmployeeIdAndOrganizationId_Success() throws Exception {
    // Arrange
    String employeeId = "E123";
    String organizationId = "org1";
    UserContext.setLoggedInUserOrganization(Map.of("id", organizationId));
    HealthInsurance existingInsurance = new HealthInsurance();
    when(healthInsuranceRepository.deleteByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(existingInsurance);

    // Act
    HealthInsurance result = healthInsuranceService.deleteByEmployeeIdAndOrganizationId(employeeId);

    // Assert
    assertNotNull(result);
    verify(healthInsuranceRepository, times(1))
        .deleteByEmployeeIdAndOrganizationId(employeeId, organizationId);
  }

  @Test
  public void testDeleteByEmployeeIdAndOrganizationId_NotFound() {
    // Arrange
    String employeeId = "E123";
    String organizationId = "org1";
    UserContext.setLoggedInUserOrganization(Map.of("id", organizationId));
    when(healthInsuranceRepository.deleteByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(null);

    // Act & Assert
    assertThrows(
        HealthInsuranceNotFoundException.class,
        () -> {
          healthInsuranceService.deleteByEmployeeIdAndOrganizationId(employeeId);
        });
  }

  @Test
  public void testFindHealthInsuranceByEmployeeId_Success() throws Exception {
    // Arrange
    String employeeId = "E123";
    String organizationId = "org1";
    UserContext.setLoggedInUserOrganization(Map.of("id", organizationId));
    HealthInsurance existingInsurance = new HealthInsurance();
    when(healthInsuranceRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(Optional.of(existingInsurance));

    // Act
    HealthInsurance result = healthInsuranceService.findHealthInsuranceByEmployeeId(employeeId);

    // Assert
    assertNotNull(result);
    verify(healthInsuranceRepository, times(1))
        .findByEmployeeIdAndOrganizationId(employeeId, organizationId);
  }

  @Test
  public void testFindHealthInsuranceByEmployeeId_NotFound() {
    // Arrange
    String employeeId = "E123";
    String organizationId = "org1";
    UserContext.setLoggedInUserOrganization(Map.of("id", organizationId));
    when(healthInsuranceRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        HealthInsuranceNotFoundException.class,
        () -> {
          healthInsuranceService.findHealthInsuranceByEmployeeId(employeeId);
        });
  }
}
