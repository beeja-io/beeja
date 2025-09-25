package com.beeja.api.projectmanagement.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.responses.ContractResponseDTO;
import com.beeja.api.projectmanagement.responses.ContractResponsesDTO;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ContractsControllerTest {

  @Mock private ContractService contractService;

  @InjectMocks private ContractsController contractsController;

  private static MockedStatic<UserContext> userContextMock;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    Map<String, Object> orgMap = new HashMap<>();
    orgMap.put(Constants.ID, "org123");
    userContextMock = org.mockito.Mockito.mockStatic(UserContext.class);
    userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
  }

  @AfterEach
  public void tearDown() {
    userContextMock.close();
  }

  @Test
  public void testCreateContract() {
    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setProjectId("project123");
    contractRequest.setClientId("client123");
    contractRequest.setContractTitle("Test Contract");
    contractRequest.setContractValue(1000.0);
    contractRequest.setStartDate(new Date());

    Contract createdContract = new Contract();
    createdContract.setId("contract123");
    createdContract.setProjectId("project123");
    createdContract.setClientId("client123");
    createdContract.setContractTitle("Test Contract");
    createdContract.setContractValue(1000.0);

    when(contractService.createContract(contractRequest)).thenReturn(createdContract);

    ResponseEntity<Contract> responseEntity = contractsController.createContract(contractRequest);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(createdContract, responseEntity.getBody());
    verify(contractService, times(1)).createContract(contractRequest);
  }

  @Test
  public void testGetContractById() {
    String contractId = "contract123";
    Contract contract = new Contract();
    contract.setId(contractId);
    contract.setContractTitle("Test Contract");
    contract.setContractValue(1000.0);

    when(contractService.getContractById(contractId)).thenReturn(contract);

    ResponseEntity<Contract> responseEntity = contractsController.getContractById(contractId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(contract, responseEntity.getBody());
    verify(contractService, times(1)).getContractById(contractId);
  }

  @Test
  public void testGetContractsByProject() {
    String projectId = "project123";
    List<Contract> contracts = new ArrayList<>();
    Contract contract1 = new Contract();
    contract1.setId("contract1");
    contract1.setContractTitle("Contract 1");
    contract1.setProjectId(projectId);
    contracts.add(contract1);

    Contract contract2 = new Contract();
    contract2.setId("contract2");
    contract2.setContractTitle("Contract 2");
    contract2.setProjectId(projectId);
    contracts.add(contract2);

    when(contractService.getContractsByProjectId(projectId)).thenReturn(contracts);

    ResponseEntity<List<Contract>> responseEntity =
            contractsController.getContractsByProject(projectId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(contracts, responseEntity.getBody());
    verify(contractService, times(1)).getContractsByProjectId(projectId);
  }

  @Test
  public void testUpdateContract() {
    String contractId = "contract123";
    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContractTitle("Updated Contract Title");
    contractRequest.setContractValue(2000.0);
    contractRequest.setProjectId("project123");
    contractRequest.setClientId("client123");

    Contract updatedContract = new Contract();
    updatedContract.setId(contractId);
    updatedContract.setContractTitle("Updated Contract Title");
    updatedContract.setContractValue(2000.0);
    updatedContract.setProjectId("project123");
    updatedContract.setClientId("client123");

    when(contractService.updateContract(contractId, contractRequest)).thenReturn(updatedContract);

    ResponseEntity<Contract> responseEntity =
            contractsController.updateContract(contractId, contractRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(updatedContract, responseEntity.getBody());
    verify(contractService, times(1)).updateContract(contractId, contractRequest);
  }

  @Test
  public void testGetAllContracts_withMetadataAndContracts() {
    String orgId = "org123";
    String projectId = "project123";
    int pageNumber = 1;
    int pageSize = 10;
    ProjectStatus status = ProjectStatus.ACTIVE;

    ContractResponsesDTO dto = ContractResponsesDTO.builder()
            .contractId("c1")
            .contractTitle("Sample Contract")
            .projectId(projectId)
            .status(status.name())
            .build();

    when(contractService.getTotalContractSize(orgId, projectId, status)).thenReturn(1L);
    when(contractService.getAllContracts(orgId, pageNumber, pageSize, projectId, status))
            .thenReturn(List.of(dto));

    ResponseEntity<ContractResponseDTO> response = contractsController.getAllContracts(
            pageNumber, pageSize, projectId, status);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getMetadata().get("totalSize"));
    assertEquals(1, response.getBody().getContracts().size());
    assertEquals("c1", response.getBody().getContracts().get(0).getContractId());

    verify(contractService).getTotalContractSize(orgId, projectId, status);
    verify(contractService).getAllContracts(orgId, pageNumber, pageSize, projectId, status);
  }
}
