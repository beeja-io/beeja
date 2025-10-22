package com.beeja.api.accounts.service;
import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.clients.ExpenseClient;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.response.EmployeeValuesDTO;
import com.beeja.api.accounts.response.ExpenseValuesDTO;
import com.beeja.api.accounts.serviceImpl.OrgDefaultsGenerationExistingImpl;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrgDefaultsGenerationExistingImplTest {

    @InjectMocks
    private OrgDefaultsGenerationExistingImpl orgDefaultsGenerationExisting;

    @Mock
    private ExpenseClient expenseClient;

    @Mock
    private EmployeeFeignClient employeeFeignClient;

    @Mock
    private OrgDefaultsRepository orgDefaultsRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create dummy organization object
        Organization org = new Organization();
        org.setId("org123");

        // Set the organization in the UserContext
        UserContext.setLoggedInUserOrganization(org);

        // Set access token
        UserContext.setAccessToken("fakeToken");
    }


    @Test
    public void testGenerateExistingValuesOfExpenseType() {
        // Arrange
        ExpenseValuesDTO dto = new ExpenseValuesDTO();
        dto.setExpenseTypes(Set.of("Travel", "Food"));

        when(expenseClient.getExpenseValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "expenseTypes"))
                .thenReturn(null);

        // Act
        orgDefaultsGenerationExisting.generateExistingValuesOfExpenseType();

        // Assert
        verify(orgDefaultsRepository, times(1)).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExistingValuesOfExpenseCategories() {
        // Arrange
        ExpenseValuesDTO dto = new ExpenseValuesDTO();
        dto.setExpenseCategories(Set.of("Training", "Lodging"));

        when(expenseClient.getExpenseValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "expenseCategories")).thenReturn(null);

        // Act
        orgDefaultsGenerationExisting.generateExistingValuesOfExpenseCategories();

        // Assert
        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExistingPaymentModes() {
        // Arrange
        ExpenseValuesDTO dto = new ExpenseValuesDTO();
        dto.setExpenseModesOfPayment(Set.of("Cash", "Credit"));

        when(expenseClient.getExpenseValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "paymentModes")).thenReturn(null);

        // Act
        orgDefaultsGenerationExisting.generateExistingPaymentModes();

        // Assert
        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExistingEmployeeTypes() {
        // Arrange
        EmployeeValuesDTO dto = new EmployeeValuesDTO();
        dto.setEmploymentTypes(Set.of("Full-time", "Contract"));

        when(employeeFeignClient.getEmployeeValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "employeeTypes")).thenReturn(null);

        // Act
        orgDefaultsGenerationExisting.generateExistingEmployeeTypes();

        // Assert
        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExistingEmployeeDepartments() {
        // Arrange
        EmployeeValuesDTO dto = new EmployeeValuesDTO();
        dto.setDepartments(Set.of("HR", "Engineering"));

        when(employeeFeignClient.getEmployeeValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "employeeDepartments")).thenReturn(null);

        // Act
        orgDefaultsGenerationExisting.generateExistingEmployeeDepartments();

        // Assert
        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExistingDesignations() {
        EmployeeValuesDTO dto = new EmployeeValuesDTO();
        dto.setDesignations(Set.of("Developer", "Manager"));

        when(employeeFeignClient.getEmployeeValues(anyString())).thenReturn(dto);
        when(orgDefaultsRepository.findByOrganizationIdAndKey("org123", "jobTitles")).thenReturn(null);

        orgDefaultsGenerationExisting.generateExistingDesignations();

        verify(orgDefaultsRepository).save(any(OrgDefaults.class));
    }


    @Test
    public void testGenerateExpenseType_ExceptionHandling() {
        when(expenseClient.getExpenseValues(anyString())).thenThrow(new RuntimeException("Something went wrong"));

        orgDefaultsGenerationExisting.generateExistingValuesOfExpenseType();

        // Should log error but not crash
        verify(orgDefaultsRepository, never()).save(any());
    }
}

