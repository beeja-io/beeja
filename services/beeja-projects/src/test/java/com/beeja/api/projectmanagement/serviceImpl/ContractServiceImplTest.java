package com.beeja.api.projectmanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.client.AccountClient;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.exceptions.FeignClientException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import com.beeja.api.projectmanagement.model.dto.ResourceAllocation;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {


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
  private com.beeja.api.projectmanagement.serviceImpl.ProjectServiceImpl projectServiceImpl;

  private static Map<String, Object> orgMap;
  private static MockedStatic<UserContext> userContextMock;

  @BeforeAll
  static void init() {
    orgMap = new HashMap<>();
    orgMap.put("id", "org123");
  }

  @BeforeEach
  void setUp() {
    userContextMock = Mockito.mockStatic(UserContext.class);
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
  }

  @AfterEach
  void closeMock() {
    userContextMock.close();
  }

  @Test
  void testCreateContract_success() {
    ContractRequest request = new ContractRequest();
    request.setProjectId("project123");
    request.setClientId("client123");
    request.setContractTitle("Contract Title");
    request.setDescription("Contract Description");

    Project project = new Project();
    project.setProjectId("project123");
    project.setClientId("client123");
    project.setOrganizationId("org123");

    when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
            .thenReturn(project);
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

    when(projectRepository.findByProjectIdAndClientIdAndOrganizationId(anyString(), anyString(), anyString()))
            .thenReturn(null);

    assertThrows(ResourceNotFoundException.class, () -> contractService.createContract(request));
  }

  @Test
  void testGetContractById_success_withResources() {
    Contract contract = new Contract();
    contract.setContractId("C1");
    ResourceAllocation ra = new ResourceAllocation();
    ra.setEmployeeId("emp1");
    ra.setAllocationPercentage(100.0);
    contract.setRawProjectResources(List.of(ra));

    EmployeeNameDTO empDTO = new EmployeeNameDTO();
    empDTO.setEmployeeId("emp1");
    empDTO.setFullName("John Doe");
    empDTO.setActive(true);

    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(contract);
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(empDTO));

    Contract result = contractService.getContractById("C1");

    assertNotNull(result);
    assertEquals(1, result.getRawProjectResources().size());
  }

  @Test
  void testGetContractById_notFound() {
    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contractService.getContractById("C1"));
  }

  @Test
  void testGetContractById_feignException() {
    Contract contract = new Contract();
    contract.setContractId("C1");
    ResourceAllocation ra = new ResourceAllocation();
    ra.setEmployeeId("emp1");
    contract.setRawProjectResources(List.of(ra));

    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(contract);
    when(accountClient.getEmployeeNamesById(anyList())).thenThrow(new FeignClientException("Error"));

    assertThrows(FeignClientException.class, () -> contractService.getContractById("C1"));
  }

  @Test
  void testGetContractsByProjectId_success() {
    when(contractRepository.findByProjectIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(Arrays.asList(new Contract(), new Contract()));
    var result = contractService.getContractsByProjectId("P1");
    assertEquals(2, result.size());
  }

  @Test
  void testUpdateContract_notFound() {
    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contractService.updateContract("C1", new ContractRequest()));
  }

  @Test
  void testUpdateContract_dbError() {
    Contract existing = new Contract();
    existing.setContractId("C1");
    ContractRequest req = new ContractRequest();
    req.setContractTitle("Updated");

    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(existing);
    when(contractRepository.save(any())).thenThrow(new RuntimeException("DB error"));

    assertThrows(ResourceNotFoundException.class, () -> contractService.updateContract("C1", req));
  }

  @Test
  void testGetAllContractsInOrganization() {
    Contract c = new Contract();
    c.setContractId("C1");
    c.setStatus(ProjectStatus.ACTIVE);

    when(mongoTemplate.find(any(Query.class), eq(Contract.class))).thenReturn(List.of(c));
    var list = contractService.getAllContractsInOrganization("org123", 1, 10, null, ProjectStatus.ACTIVE);
    assertEquals(1, list.size());
  }

  @Test
  void testGetTotalContractSize() {
    when(mongoTemplate.count(any(Query.class), eq(Contract.class))).thenReturn(5L);
    assertEquals(5L, contractService.getTotalContractSize("org123", null, ProjectStatus.ACTIVE));
  }

  @Test
  void testChangeContractStatus_success() {
    Contract c = new Contract();
    c.setContractId("C1");
    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(c);
    when(contractRepository.save(any())).thenReturn(c);

    var updated = contractService.changeContractStatus("C1", ProjectStatus.COMPLETED);
    assertEquals(ProjectStatus.COMPLETED, updated.getStatus());
  }

  @Test
  void testChangeContractStatus_notFound() {
    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contractService.changeContractStatus("C1", ProjectStatus.COMPLETED));
  }

  @Test
  void testChangeContractStatus_dbError() {
    Contract c = new Contract();
    c.setContractId("C1");
    when(contractRepository.findByContractIdAndOrganizationId(anyString(), anyString())).thenReturn(c);
    when(contractRepository.save(any())).thenThrow(new RuntimeException("DB error"));
    assertThrows(ResourceNotFoundException.class, () -> contractService.changeContractStatus("C1", ProjectStatus.COMPLETED));
  }

  @Test
  void testGetContractsByClientId_success() {
    Contract c = new Contract();
    c.setContractId("C1");
    c.setProjectId("P1");
    c.setClientId("C1");
    c.setProjectManagers(List.of("emp1"));

    when(contractRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(List.of(c));
    when(projectRepository.findByProjectId(anyString(), anyString())).thenReturn(Optional.of(new Project() {{
      setName("Proj1");
    }}));
    when(accountClient.getEmployeeNamesById(anyList())).thenReturn(List.of(new EmployeeNameDTO() {{
      setEmployeeId("emp1");
      setFullName("John Doe");
    }}));
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString()))
            .thenReturn(null);

    var result = contractService.getContractsByClientId("C1");
    assertEquals(1, result.size());
    assertEquals("Proj1", result.get(0).getProjectName());
  }

  @Test
  void testGetClientResources_success() throws FeignClientException {
    Contract c = new Contract();
    c.setClientId("C1");
    c.setRawProjectResources(List.of(new ResourceAllocation() {{
      setEmployeeId("emp1");
      setAllocationPercentage(50.0);
    }}));

    when(contractRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(List.of(c));
    when(projectServiceImpl.fetchEmployees(anyList(), anyString())).thenReturn(List.of(new EmployeeNameDTO() {{
      setEmployeeId("emp1");
      setFullName("John Doe");
    }}));

    var result = contractService.getClientResources("C1");
    assertEquals(1, result.size());
    assertEquals("John Doe", result.get(0).getEmployeeName());
  }
}
