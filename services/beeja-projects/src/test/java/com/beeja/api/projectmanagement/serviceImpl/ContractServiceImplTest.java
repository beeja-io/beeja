package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @InjectMocks
    private ContractServiceImpl contractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ProjectRepository projectRepository;

    private static Map<String, Object> orgMap;

    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void init() {
        orgMap = new HashMap<>();
        orgMap.put("id", "org123");
    }

    @BeforeEach
    void setUp() {
        userContextMock = mockStatic(UserContext.class);
        userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
    }

    @AfterEach
    void closeStaticMock() {
        userContextMock.close();
    }

    @Test
    void testCreateContract_success() {
        ContractRequest request = new ContractRequest();
        request.setProjectId("project123");
        request.setClientId("client123");
        request.setContractTitle("Contract Title");
        request.setDescription("Contract Description");
        request.setContractValue(5000.0);
        request.setStartDate(new Date());
        request.setEndDate(new Date());
        request.setSignedBy("John Doe");

        Project project = new Project();
        project.setProjectId("project123");
        project.setClientId("client123");
        project.setOrganizationId("org123");

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(
                anyString(), anyString(), anyString())).thenReturn(project);
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract result = contractService.createContract(request);

        assertNotNull(result);
        assertEquals("Contract Title", result.getContractTitle());
        assertEquals("Contract Description", result.getDescription());
    }

    @Test
    void testCreateContract_projectNotFound() {
        ContractRequest request = new ContractRequest();
        request.setProjectId("project123");
        request.setClientId("client123");

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(
                anyString(), anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.createContract(request));

        assertTrue(exception.getMessage().contains("Project not found with given projectId"));
    }

    @Test
    void testGetContractById_success() {
        Contract contract = new Contract();
        contract.setContractId("contract123");

        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(contract);

        Contract result = contractService.getContractById("contract123");

        assertNotNull(result);
        assertEquals("contract123", result.getContractId());
    }

    @Test
    void testGetContractById_notFound() {
        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.getContractById("contract123"));

        assertTrue(exception.getMessage().contains("Contract not found with given contractId"));
    }

    @Test
    void testGetContractsByProjectId_success() {
        List<Contract> contracts = Arrays.asList(new Contract(), new Contract());

        when(contractRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(contracts);

        List<Contract> result = contractService.getContractsByProjectId("project123");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateContract_success() {
        ContractRequest request = new ContractRequest();
        request.setContractTitle("Updated Contract Title");

        Contract existingContract = new Contract();
        existingContract.setContractId("contract123");

        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(existingContract);
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contract result = contractService.updateContract("contract123", request);

        assertNotNull(result);
        assertEquals("Updated Contract Title", result.getContractTitle());
    }

    @Test
    void testUpdateContract_notFound() {
        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.updateContract("contract123", new ContractRequest()));

        assertTrue(exception.getMessage().contains("Contract not found with given contractId"));
    }

    @Test
    void testCreateContract_dbError() {
        ContractRequest request = new ContractRequest();
        request.setProjectId("project123");
        request.setClientId("client123");

        Project project = new Project();
        project.setProjectId("project123");
        project.setClientId("client123");
        project.setOrganizationId("org123");

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(
                anyString(), anyString(), anyString())).thenReturn(project);
        when(contractRepository.save(any(Contract.class))).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.createContract(request));

        assertTrue(exception.getMessage().contains("Failed to save contract"));
    }

    @Test
    void testUpdateContract_dbError() {
        ContractRequest request = new ContractRequest();
        request.setContractTitle("Updated Contract Title");

        Contract existingContract = new Contract();
        existingContract.setContractId("contract123");

        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(existingContract);
        when(contractRepository.save(any(Contract.class))).thenThrow(new RuntimeException("DB error"));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> contractService.updateContract("contract123", request));

        assertTrue(exception.getMessage().contains("Failed to update contract"));
    }
}
