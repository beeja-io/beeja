package com.beeja.api.projectmanagement.controller;

import com.beeja.api.projectmanagement.client.AccountClient;
import static org.mockito.Mockito.never;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.beeja.api.projectmanagement.controllers.ContractController;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;
import java.util.List;


public class ContractControllerTest {

    @InjectMocks
    private ContractController contractController;

    @MockBean
    private AccountClient accountClient;

    @Mock
    private ContractService contractService;

    @Mock
    private MockMvc mockMvc;

    @Mock
    private ContractRequest contractRequest;

    private ObjectMapper objectMapper = new ObjectMapper();;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(contractController).build();
    }

    @Test
    public void testAddContract_Success() throws Exception {
        String contractName = "New Contract";
        String contractType = "Service";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        String project = "project123";
        String client = "client456";
        MockMultipartFile attachment = new MockMultipartFile("attachment", "test.txt", "text/plain", "Test File Content".getBytes());

        ContractRequest contractRequest = new ContractRequest();
        contractRequest.setContractName(contractName);
        contractRequest.setContractType(contractType);
        contractRequest.setStartDate(startDate);
        contractRequest.setProject(project);
        contractRequest.setClient(client);
        contractRequest.setAttachment(attachment);

        Contract createdContract = new Contract();
        createdContract.setId("1");
        createdContract.setContractName(contractName);
        createdContract.setContractType(contractType);

        when(contractService.addContract(any(ContractRequest.class))).thenReturn(createdContract);

        mockMvc.perform(multipart("/v1/contracts")
                        .file(attachment)
                        .param("contractName", contractName)
                        .param("contractType", contractType)
                        .param("startDate", startDate.toString())
                        .param("project", project)
                        .param("client", client))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.contractName").value(contractName))
                .andExpect(jsonPath("$.contractType").value(contractType));

        verify(contractService, times(1)).addContract(any(ContractRequest.class));
    }

    @Test
    public void testAddContract_InvalidRequest() throws Exception {
        MockMultipartFile attachment = new MockMultipartFile("attachment", "test.txt", "text/plain", "Test File Content".getBytes());

        mockMvc.perform(multipart("/v1/contracts")
                        .file(attachment)
                        .param("contractName", "")  // Missing contract name
                        .param("contractType", "Service")
                        .param("startDate", "2025-01-01")
                        .param("project", "project123")
                        .param("client", "client456"))
                .andExpect(status().isBadRequest());

        verify(contractService, never()).addContract(any(ContractRequest.class));
    }

    @Test
    public void testUpdateContract_Success() throws Exception {
        String contractId = "1";

        ContractRequest updateRequest = new ContractRequest();
        updateRequest.setContractName("Updated Contract");
        updateRequest.setContractType("Service");
        updateRequest.setBillingType("Fixed");
        updateRequest.setBillingCurrency("USD");
        updateRequest.setBudget(5000.0);
        updateRequest.setDescription("Updated description");
        updateRequest.setProject("Project1");
        updateRequest.setClient("Client1");


        Contract updatedContract = new Contract();
        updatedContract.setId(contractId);
        updatedContract.setContractName("Updated Contract");
        updatedContract.setContractType("Service");
        updatedContract.setBillingType("Fixed");
        updatedContract.setBillingCurrency("USD");
        updatedContract.setBudget(5000.0);
        updatedContract.setDescription("Updated description");

        when(contractService.updateContract(eq(contractId), any(ContractRequest.class))).thenReturn(updatedContract);

        mockMvc.perform(patch("/v1/contracts/" + contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contractId))
                .andExpect(jsonPath("$.contractName").value("Updated Contract"))
                .andExpect(jsonPath("$.contractType").value("Service"));

        verify(contractService, times(1)).updateContract(eq(contractId), any(ContractRequest.class));
    }


    @Test
    public void testUpdateContract_InvalidRequest() throws Exception {
        String contractId = "1";
        ContractRequest invalidRequest = new ContractRequest();  // Missing required fields

        mockMvc.perform(patch("/v1/contracts/" + contractId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

    }


    @Test
    public void testGetAllContracts_Success() throws Exception {
        Contract contract1 = new Contract();
        contract1.setId("1");
        contract1.setContractName("Contract One");
        contract1.setContractType("Service");
        contract1.setStartDate(LocalDate.of(2025, 1, 1));

        Contract contract2 = new Contract();
        contract2.setId("2");
        contract2.setContractName("Contract Two");
        contract2.setContractType("Product");
        contract2.setStartDate(LocalDate.of(2025, 2, 1));

        List<Contract> contracts = List.of(contract1, contract2);

        when(contractService.getAllContracts()).thenReturn(contracts);

        mockMvc.perform(get("/v1/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].contractName").value("Contract One"))
                .andExpect(jsonPath("$[0].contractType").value("Service"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].contractName").value("Contract Two"))
                .andExpect(jsonPath("$[1].contractType").value("Product"));

        verify(contractService, times(1)).getAllContracts();
    }

    @Test
    public void testGetAllContracts_EmptyList() throws Exception {
        when(contractService.getAllContracts()).thenReturn(List.of());

        mockMvc.perform(get("/v1/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contractService, times(1)).getAllContracts();
    }


    @Test
    public void testGetContractById_Success() throws Exception {
        String contractId = "1";
        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setContractName("Test Contract");
        contract.setContractType("Service");
        contract.setStartDate(LocalDate.of(2025, 1, 1));

        when(contractService.getContractById(contractId)).thenReturn(contract);

        mockMvc.perform(get("/v1/contracts/" + contractId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contractId))
                .andExpect(jsonPath("$.contractName").value("Test Contract"))
                .andExpect(jsonPath("$.contractType").value("Service"));

        verify(contractService, times(1)).getContractById(contractId);
    }
}
