package com.beeja.api.projectmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.serviceImpl.ContractServiceImpl;
import com.beeja.api.projectmanagement.serviceImpl.ProjectServiceImpl;
import com.beeja.api.projectmanagement.responses.ClientResourcesDTO;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

class ContractServiceTest {

    @InjectMocks
    private ContractServiceImpl contractService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ProjectServiceImpl projectServiceImpl;

    private MockedStatic<com.beeja.api.projectmanagement.utils.UserContext> userContextStatic;

    private Contract contract;
    private Project project;
    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userContextStatic = Mockito.mockStatic(com.beeja.api.projectmanagement.utils.UserContext.class);
        Map<String, Object> mockOrg = new HashMap<>();
        mockOrg.put("id", "org123");
        userContextStatic.when(com.beeja.api.projectmanagement.utils.UserContext::getLoggedInUserOrganization)
                .thenReturn(mockOrg);

        project = new Project();
        project.setProjectId("proj1");
        project.setName("Test Project");
        project.setOrganizationId("org123");
        project.setProjectManagers(List.of("emp1"));
        project.setProjectResources(List.of("emp2"));

        client = new Client();
        client.setClientId("client1");
        client.setClientName("Test Client");

        contract = new Contract();
        contract.setContractId("c1");
        contract.setProjectId("proj1");
        contract.setClientId("client1");
        contract.setOrganizationId("org123");
        contract.setProjectManagers(List.of("emp1"));
        contract.setRawProjectResources(List.of("emp2"));
    }


    @AfterEach
    void tearDown() {
        userContextStatic.close();
    }

    @Test
    void testCreateContract_success() {
        ContractRequest request = new ContractRequest();
        request.setProjectId("proj1");
        request.setClientId("client1");
        request.setProjectManagers(List.of("emp1"));
        request.setProjectResources(List.of(new ResourceAllocation("emp2", 100.0)));

        when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
                .thenReturn(project);
        when(projectServiceImpl.validateAndFetchEmployees(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);

        Contract result = contractService.createContract(request);

        assertNotNull(result);
        assertEquals("c1", result.getContractId());
        assertEquals("proj1", result.getProjectId());
    }

    @Test
    void testGetContractById_found() {
        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(contract);
        when(accountClient.getEmployeeNamesById(anyList()))
                .thenReturn(List.of(new EmployeeNameDTO("emp2", "Jane Doe", true)));

        Contract result = contractService.getContractById("c1");

        assertNotNull(result);
        assertEquals("c1", result.getContractId());
        // Check enriched resources
        assertEquals(1, result.getRawProjectResources().size());
    }

    @Test
    void testGetContractsByProjectId() {
        when(contractRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(List.of(contract));

        List<Contract> result = contractService.getContractsByProjectId("proj1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).getContractId());
    }

    @Test
    void testUpdateContract_success() {
        ContractRequest request = new ContractRequest();
        request.setProjectId("proj1");
        request.setProjectManagers(List.of("emp1"));
        request.setProjectResources(List.of(new ResourceAllocation("emp2", 100.0)));

        when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(contract);
        when(projectServiceImpl.validateAndFetchEmployees(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountClient.getEmployeeNamesById(anyList()))
                .thenReturn(List.of(new EmployeeNameDTO("emp2", "Jane Doe", true)));

        Contract updated = contractService.updateContract("c1", request);

        assertNotNull(updated);
        assertEquals("c1", updated.getContractId());
    }

    @Test
    void testGetAllContractsInOrganization() {
        when(mongoTemplate.find(any(), eq(Contract.class))).thenReturn(List.of(contract));

        List<Contract> result = contractService.getAllContractsInOrganization("org123", 1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetClientResources_success() {
        when(contractRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
                .thenReturn(List.of(contract));
        when(projectServiceImpl.fetchEmployees(anyList(), anyString()))
                .thenReturn(List.of(new EmployeeNameDTO("emp2", "Jane Doe", true)));

        List<ClientResourcesDTO> result = contractService.getClientResources("client1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane Doe", result.get(0).getEmployeeName());
    }

}
