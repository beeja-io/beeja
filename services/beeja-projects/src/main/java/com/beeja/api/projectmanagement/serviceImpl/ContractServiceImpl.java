package com.beeja.api.projectmanagement.serviceImpl;


import com.beeja.api.projectmanagement.client.EmployeeClient;
import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.*;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.repository.ProjectRepository;
import com.beeja.api.projectmanagement.repository.ResourceRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.responses.EmployeeDetailsResponse;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.service.ResourcesService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.*;


@Service
@Slf4j
public class ContractServiceImpl implements ContractService {


    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ResourcesService resourceService;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private EmployeeClient employeeClient;


@Transactional
public Contract addContract(ContractRequest contractRequest) {
    validateContractRequest(contractRequest);

    String contractId = generateNextContractId();

    Project project = projectRepository.findById(contractRequest.getProject())
            .orElseThrow(() -> new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.format(Constants.RESOURCE_NOT_FOUND, "Project", "ID", contractRequest.getProject())
                    )
            ));

    Client client = clientRepository.findById(contractRequest.getClient())
            .orElseThrow(() -> new ResourceNotFoundException(
                    BuildErrorMessage.buildErrorMessage(
                            ErrorType.NOT_FOUND,
                            ErrorCode.RESOURCE_NOT_FOUND,
                            Constants.format(Constants.RESOURCE_NOT_FOUND, "Client", "ID", contractRequest.getClient())
                    )
            ));

    String attachmentId = uploadFile(contractRequest.getAttachment(), contractId);

    List<EmployeeDetailsResponse> employeeDetails;
    try {
        employeeDetails = employeeClient.getEmployeeDetails();
        if (employeeDetails == null || employeeDetails.isEmpty()) {
            throw new IllegalArgumentException("Error fetching employee details. No employees found.");
        }
    } catch (Exception e) {
        throw new IllegalArgumentException("Error fetching employee details. Please try again later.", e);
    }

    if (contractRequest.getResources() != null && !contractRequest.getResources().isEmpty()) {
        contractRequest.setResources(resourceService.getOrCreateResources(contractRequest.getResources()));
    }


    if (contractRequest.getProjectManagers() != null && !contractRequest.getProjectManagers().isEmpty()) {
        contractRequest.setProjectManagers(resourceService.getOrCreateResources(contractRequest.getProjectManagers()));
    }

    Contract contract = new Contract();
    contract.setContractId(contractId);
    contract.setContractName(contractRequest.getContractName());
    contract.setContractType(contractRequest.getContractType());
    contract.setStartDate(contractRequest.getStartDate());
    contract.setEndDate(contractRequest.getEndDate());
    contract.setBillingType(contractRequest.getBillingType());
    contract.setBillingCurrency(contractRequest.getBillingCurrency());
    contract.setBudget(contractRequest.getBudget());
    contract.setDescription(contractRequest.getDescription());
    contract.setProject(project);
    contract.setClient(client);
    contract.setProjectManagers(contractRequest.getProjectManagers());
    contract.setResources(contractRequest.getResources());
    contract.setAttachmentId(attachmentId);

    Contract savedContract = contractRepository.save(contract);

    return savedContract;
}


  String uploadFile(MultipartFile attachment, String contractId) {
        if (attachment == null || attachment.isEmpty()) {
            return null;
        }
            FileUploadRequest fileUpload = new FileUploadRequest();
        fileUpload.setEntityId(contractId);
            fileUpload.setFile(attachment);
            ResponseEntity<?> fileResponse;
            try {
                fileResponse = fileClient.uploadFile(fileUpload);
            } catch (Exception e) {
                log.error(
                        Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE + ", error: {}", e.getMessage());
                throw new FeignClientException(Constants.ERROR_IN_UPLOADING_FILE_TO_FILE_SERVICE);
            }
            LinkedHashMap<String, Object> responseBody =
                    (LinkedHashMap<String, Object>) fileResponse.getBody();
            if (responseBody != null) {
                 return responseBody.get("id").toString();
            }


        return null;
    }

    private void validateContractRequest(ContractRequest contractRequest) {
        if (contractRequest.getStartDate() != null && contractRequest.getEndDate() != null &&
                contractRequest.getStartDate().isAfter(contractRequest.getEndDate())) {
            ErrorResponse errorResponse = new ErrorResponse(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_DATE,
                    "Start date cannot be after end date",
                    "/contract/validate"
            );
            throw new ValidationException(errorResponse);
        }
    }
    String generateNextContractId() {
        String prefix = "C-";
        Contract lastContract = contractRepository.findTopByOrderByContractIdDesc();

        if (lastContract == null || lastContract.getContractId() == null || !lastContract.getContractId().startsWith(prefix)) {
            return prefix + "001";
        }

        String lastId = lastContract.getContractId();
        int lastNumber = Integer.parseInt(lastId.substring(2));
        int nextNumber = lastNumber + 1;

        return String.format(prefix + "%03d", nextNumber);
    }


    @Transactional
    public Contract updateContract(String contractId, ContractRequest updatedContract) {
        Contract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        new ErrorResponse(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CONTRACT_NOT_FOUND,
                                "Contract not found with ID: " + contractId,
                                "v1/contracts/update"
                        )));

        if (updatedContract.getContractName() != null) {
            String contractName = updatedContract.getContractName().trim();
            if (contractName.isEmpty()) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Contract name cannot be empty.",
                        "v1/contracts/update"
                ));
            }
            existingContract.setContractName(contractName);
        }

        if (updatedContract.getContractType() != null) {
            existingContract.setContractType(updatedContract.getContractType());
        }

        if (updatedContract.getStartDate() != null) {
            LocalDate startDate = updatedContract.getStartDate();
            if (startDate.isBefore(LocalDate.now())) {
                throw new ValidationException(new ErrorResponse(
                        ErrorType.VALIDATION_ERROR,
                        ErrorCode.FIELD_VALIDATION_ERROR,
                        "Start date cannot be in the past.",
                        "v1/contracts/update"
                ));
            }
            existingContract.setStartDate(startDate);
        }

        if (updatedContract.getEndDate() != null) {
            existingContract.setEndDate(updatedContract.getEndDate());
        }

        if (updatedContract.getBillingType() != null) {
            existingContract.setBillingType(updatedContract.getBillingType());
        }

        if (updatedContract.getBillingCurrency() != null) {
            existingContract.setBillingCurrency(updatedContract.getBillingCurrency());
        }

        if (updatedContract.getBudget() != null) {
            existingContract.setBudget(updatedContract.getBudget());
        }

        if (updatedContract.getDescription() != null) {
            existingContract.setDescription(updatedContract.getDescription());
        }

        if (updatedContract.getProject() != null) {
            Project project = projectRepository.findById(updatedContract.getProject())
                    .orElseThrow(() -> new ResourceNotFoundException(new ErrorResponse(
                            ErrorType.NOT_FOUND,
                            ErrorCode.PROJECT_NOT_FOUND,
                            "Project not found with ID: " + updatedContract.getProject(),
                            "v1/contracts/update"
                    )));
            existingContract.setProject(project);
        }

        if (updatedContract.getClient() != null) {
            Client client = clientRepository.findById(updatedContract.getClient())
                    .orElseThrow(() -> new ResourceNotFoundException(new ErrorResponse(
                            ErrorType.NOT_FOUND,
                            ErrorCode.CLIENT_NOT_FOUND,
                            "Client not found with ID: " + updatedContract.getClient(),
                            "v1/contracts/update"
                    )));
            existingContract.setClient(client);
        }

        if (updatedContract.getProjectManagers() != null) {
            List<Resource> projectManagers = resourceService.getOrCreateResources(updatedContract.getProjectManagers());
            existingContract.setProjectManagers(projectManagers);
        }

        if (updatedContract.getResources() != null) {
            List<Resource> resources = resourceService.getOrCreateResources(updatedContract.getResources());
            existingContract.setResources(resources);
        }

        if (updatedContract.getAttachment() != null && !updatedContract.getAttachment().isEmpty()) {
            String attachmentId = uploadFile(updatedContract.getAttachment(), contractId);
            existingContract.setAttachmentId(attachmentId);
        }
        return contractRepository.save(existingContract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contract> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();

        if (contracts.isEmpty()) {
            throw new ContractNotFoundException("No contracts available.");
        }

        return contracts;
    }

    @Override
    @Transactional(readOnly = true)
    public Contract getContractById(String id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException("Contract not found with ID: " + id));
    }




}









