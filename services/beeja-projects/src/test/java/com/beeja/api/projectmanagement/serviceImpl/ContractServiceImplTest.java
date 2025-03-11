package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.exceptions.ContractNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ResourceServiceImpl resourceService;

    @Mock
    private EmployeeClient employeeClient;

    @Mock
    private FileClient fileClient;

    @Mock
    private MultipartFile mockFile;

    private ContractRequest contractRequest;
    private Project mockProject;
    private Client mockClient;
    private Contract existingContract;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        contractRequest = new ContractRequest();
        contractRequest.setContractName("Test Contract");
        contractRequest.setProject("projectId");
        contractRequest.setClient("clientId");
        contractRequest.setStartDate(LocalDate.now());
        contractRequest.setEndDate(LocalDate.now().plusDays(10));
//        contractRequest.setAttachment((MultipartFile) mock(File.class));
        MultipartFile mockFile = mock(MultipartFile.class);
        contractRequest.setAttachment(mockFile);

        mockProject = new Project();
        mockProject.setId("projectId");

        mockClient = new Client();
        mockClient.setId("clientId");

        existingContract = new Contract();
        existingContract.setContractId("C001");
        existingContract.setContractName("Old Contract Name");
        existingContract.setStartDate(LocalDate.now().plusDays(1));
        existingContract.setContractType("Old Type");
        existingContract.setEndDate(LocalDate.now().plusDays(10));
        existingContract.setBillingType("Old Billing");
        existingContract.setBillingCurrency("USD");
        existingContract.setBudget(1000.0);
        existingContract.setDescription("Old Description");

    }

    @Test
    void testAddContract_SuccessfulCreation() {
        when(projectRepository.findById("projectId")).thenReturn(Optional.of(mockProject));
        when(clientRepository.findById("clientId")).thenReturn(Optional.of(mockClient));
        when(contractRepository.findTopByOrderByContractIdDesc()).thenReturn(null);
        when(fileClient.uploadFile(any())).thenReturn(ResponseEntity.ok(new LinkedHashMap<>(Map.of("id", "fileId"))));
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeClient.getEmployeeDetails()).thenReturn(Collections.singletonList(new EmployeeDetailsResponse()));

        Contract savedContract = contractService.addContract(contractRequest);

        assertNotNull(savedContract);
        assertEquals("C-001", savedContract.getContractId());
        assertEquals("Test Contract", savedContract.getContractName());
        assertEquals("fileId", savedContract.getAttachmentId());

        verify(projectRepository).findById("projectId");
        verify(clientRepository).findById("clientId");
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    void testAddContract_ProjectNotFound() {
        when(projectRepository.findById("projectId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                contractService.addContract(contractRequest));

        assertTrue(exception.getMessage().contains("Project"));
    }

    @Test
    void testAddContract_ClientNotFound() {
        when(projectRepository.findById("projectId")).thenReturn(Optional.of(mockProject));
        when(clientRepository.findById("clientId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                contractService.addContract(contractRequest));

        assertTrue(exception.getMessage().contains("Client"));
    }

    @Test
    void testAddContract_FileUploadFailure() {
        when(projectRepository.findById("projectId")).thenReturn(Optional.of(mockProject));
        when(clientRepository.findById("clientId")).thenReturn(Optional.of(mockClient));
        when(fileClient.uploadFile(any())).thenThrow(new RuntimeException("File upload failed"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                contractService.addContract(contractRequest));

        assertFalse(exception.getMessage().contains("File upload failed"));
    }

    @Test
    void testGenerateNextContractId_FirstContract() {
        // No previous contract in the database
        when(contractRepository.findTopByOrderByContractIdDesc()).thenReturn(null);

        String contractId = contractService.generateNextContractId();

        assertEquals("C-001", contractId);
        verify(contractRepository, times(1)).findTopByOrderByContractIdDesc();
    }

    @Test
    void testUploadFile_SuccessfulUpload() throws IOException {
        // Mocking MultipartFile
        MultipartFile attachment = mock(MultipartFile.class);
        when(attachment.isEmpty()).thenReturn(false);

        // Mocking FileClient response
        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("id", "fileId123");

        ResponseEntity<?> responseEntity = ResponseEntity.ok(responseMap);
        when(fileClient.uploadFile(any(FileUploadRequest.class))).thenReturn((ResponseEntity<Object>) responseEntity);

        // Call the method
        String result = contractService.uploadFile(attachment, "contractId123");

        // Verify the result
        assertEquals("fileId123", result);
        verify(fileClient, times(1)).uploadFile(any(FileUploadRequest.class));
    }



    @Test
    void testAddContract_EmployeeClientFailure() {
        when(projectRepository.findById("projectId")).thenReturn(Optional.of(mockProject));
        when(clientRepository.findById("clientId")).thenReturn(Optional.of(mockClient));
        when(fileClient.uploadFile(any())).thenReturn(ResponseEntity.ok(Map.of("id", "fileId")));
        when(employeeClient.getEmployeeDetails()).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                contractService.addContract(contractRequest));
    }


    @Test
    void testSuccessfulUpdate() {
        ContractRequest request = new ContractRequest();
        request.setContractName("Updated Contract");
        request.setStartDate(LocalDate.now().plusDays(2));

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);  // Mock the save operation

        Contract updatedContract = contractService.updateContract("C001", request);

        assertNotNull(updatedContract);  // Check if the updated contract is not null
        assertEquals("Updated Contract", updatedContract.getContractName());
        assertEquals(LocalDate.now().plusDays(2), updatedContract.getStartDate());
        verify(contractRepository).save(existingContract);
    }

    @Test
    void  testSuccessfulUpdate_ContractNameAndStartDate() {
        ContractRequest request = new ContractRequest();
        request.setContractName("Updated Contract");
        request.setStartDate(LocalDate.now().plusDays(2));

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);

        Contract updatedContract = contractService.updateContract("C001", request);

        assertNotNull(updatedContract);
        assertEquals("Updated Contract", updatedContract.getContractName());
        assertEquals(LocalDate.now().plusDays(2), updatedContract.getStartDate());
        verify(contractRepository).save(existingContract);
    }

    @Test
    void testUpdateContract_WithEmptyContractName() {
        ContractRequest request = new ContractRequest();
        request.setContractName("  ");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));

        assertThrows(ValidationException.class, () ->
                contractService.updateContract("C001", request)
        );

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    void testUpdateContract_WithPastStartDate() {
        ContractRequest request = new ContractRequest();
        request.setStartDate(LocalDate.now().minusDays(1));

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));

        assertThrows(ValidationException.class, () ->
                contractService.updateContract("C001", request)
        );

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    void testUpdateContract_WithEndDateAndDescription() {
        ContractRequest request = new ContractRequest();
        request.setEndDate(LocalDate.now().plusMonths(1));
        request.setDescription("Updated Description");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);

        Contract updatedContract = contractService.updateContract("C001", request);

        assertEquals(LocalDate.now().plusMonths(1), updatedContract.getEndDate());
        assertEquals("Updated Description", updatedContract.getDescription());
        verify(contractRepository).save(existingContract);
    }

    @Test
    void testUpdateContract_WithBillingTypeAndCurrency() {
        ContractRequest request = new ContractRequest();
        request.setBillingType("Hourly");
        request.setBillingCurrency("USD");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);

        Contract updatedContract = contractService.updateContract("C001", request);

        assertEquals("Hourly", updatedContract.getBillingType());
        assertEquals("USD", updatedContract.getBillingCurrency());
        verify(contractRepository).save(existingContract);
    }

    @Test
    void testUpdateContract_WithProject() {
        Project project = new Project();
        project.setId("P001");

        ContractRequest request = new ContractRequest();
        request.setProject("P001");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(projectRepository.findById("P001")).thenReturn(Optional.of(project));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);

        Contract updatedContract = contractService.updateContract("C001", request);

        assertEquals(project, updatedContract.getProject());
        verify(contractRepository).save(existingContract);
    }

    @Test
    void testUpdateContract_WithNonExistentProject() {
        ContractRequest request = new ContractRequest();
        request.setProject("P002");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(projectRepository.findById("P002")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                contractService.updateContract("C001", request)
        );

        verify(contractRepository, never()).save(any(Contract.class));
    }

    @Test
    void testUpdateContract_WithClient() {
        Client client = new Client();
        client.setId("CL001");

        ContractRequest request = new ContractRequest();
        request.setClient("CL001");

        when(contractRepository.findById("C001")).thenReturn(Optional.of(existingContract));
        when(clientRepository.findById("CL001")).thenReturn(Optional.of(client));
        when(contractRepository.save(existingContract)).thenReturn(existingContract);

        Contract updatedContract = contractService.updateContract("C001", request);

        assertEquals(client, updatedContract.getClient());
        verify(contractRepository).save(existingContract);
    }


    @Test
    public void testGetAllContracts_Success() {
        Contract contract1 = new Contract();
        contract1.setId("1");
        contract1.setContractName("Contract 1");

        Contract contract2 = new Contract();
        contract2.setId("2");
        contract2.setContractName("Contract 2");

        when(contractRepository.findAll()).thenReturn(Arrays.asList(contract1, contract2));
        List<Contract> result = contractService.getAllContracts();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Contract 1", result.get(0).getContractName());
        assertEquals("Contract 2", result.get(1).getContractName());

        verify(contractRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllContracts_NoContractsFound() {
        when(contractRepository.findAll()).thenReturn(Collections.emptyList());
        ContractNotFoundException exception = assertThrows(ContractNotFoundException.class, () -> {
            contractService.getAllContracts();
        });

        assertEquals("No contracts available.", exception.getMessage());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    public void testGetContractById_Success() {
        String contractId = "1";

        Contract mockContract = new Contract();
        mockContract.setId(contractId);
        mockContract.setContractName("Sample Contract");

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(mockContract));

        Contract result = contractService.getContractById(contractId);

        assertNotNull(result);
        assertEquals(contractId, result.getId());
        assertEquals("Sample Contract", result.getContractName());

        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    public void testGetContractById_NotFound() {
        String contractId = "invalidId";

        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ContractNotFoundException.class,
                () -> contractService.getContractById(contractId));

        assertEquals("Contract not found with ID: " + contractId, exception.getMessage());
        verify(contractRepository, times(1)).findById(contractId);
    }

}