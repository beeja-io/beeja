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
import com.beeja.api.projectmanagement.utils.UserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceServiceImpl resourceService;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private EmployeeClient employeeClient;
    @Override
    public Contract addContract(ContractRequest contractRequest) {
        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
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

        ObjectMapper objectMapper = new ObjectMapper();
        List<Resource> projectManagers = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();

        try {
            if (contractRequest.getProjectManagers() != null && !contractRequest.getProjectManagers().isEmpty()) {
                projectManagers = objectMapper.readValue(contractRequest.getProjectManagers(),
                        new TypeReference<List<Resource>>() {});
            }

            if (contractRequest.getResources() != null && !contractRequest.getResources().isEmpty()) {
                resources = objectMapper.readValue(contractRequest.getResources(),
                        new TypeReference<List<Resource>>() {});
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON format for Project Managers or Resources.");
        }

        if (!resources.isEmpty() || !projectManagers.isEmpty()) {
            List<String> employeeIds = Stream.concat(
                    resources.stream().map(Resource::getEmployeeId),
                    projectManagers.stream().map(Resource::getEmployeeId)
            ).distinct().collect(Collectors.toList());

            if (!employeeIds.isEmpty()) {
                Map<String, String> employeeNamesMap = resourceService.getEmployeeNamesByIds(employeeIds);

                if (!resources.isEmpty()) {
                    resources = resourceService.updateResourceAllocations(resources, employeeNamesMap);
                }

                if (!projectManagers.isEmpty()) {
                    List<Resource> savedProjectManagers = resourceService.getOrCreateResources(projectManagers);
                    savedProjectManagers.forEach(manager -> manager.setFirstName(employeeNamesMap.get(manager.getEmployeeId())));
                    projectManagers = savedProjectManagers;
                }
            }
        }

        Contract contract = new Contract();
        contract.setContractId(contractId);
        contract.setContractName(contractRequest.getContractName());
        contract.setContractType(contractRequest.getContractType());
        contract.setStartDate(contractRequest.getStartDate());
        contract.setOrganizationId(organizationId);
        contract.setEndDate(contractRequest.getEndDate());
        contract.setBillingType(contractRequest.getBillingType());
        contract.setBillingCurrency(contractRequest.getBillingCurrency());
        contract.setBudget(contractRequest.getBudget());
        contract.setDescription(contractRequest.getDescription());
        contract.setProject(project);
        contract.setClient(client);
        contract.setProjectManagers(projectManagers);
        contract.setResources(resources);
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

    @Override
    public Contract updateContract(String contractId, ContractRequest updatedContract) {
        Contract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        new ErrorResponse(
                                ErrorType.NOT_FOUND,
                                ErrorCode.CONTRACT_NOT_FOUND,
                                "Contract not found with ID: " + contractId,
                                "v1/contracts/update"
                        )));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

        if (updatedContract.getAttachment() != null && !updatedContract.getAttachment().isEmpty()) {
            String attachmentId = uploadFile(updatedContract.getAttachment(), contractId);
            existingContract.setAttachmentId(attachmentId);
        }

        try {
            if (updatedContract.getResources() != null && !updatedContract.getResources().isEmpty()) {
                List<Resource> savedResources = resourceService.getOrCreateResources(
                        objectMapper.readValue(updatedContract.getResources(), new TypeReference<List<Resource>>() {})
                );
                existingContract.setResources(savedResources);
            }

            if (updatedContract.getProjectManagers() != null && !updatedContract.getProjectManagers().isEmpty()) {
                List<Resource> savedManagers = resourceService.getOrCreateResources(
                        objectMapper.readValue(updatedContract.getProjectManagers(), new TypeReference<List<Resource>>() {})
                );
                existingContract.setProjectManagers(savedManagers);
            }
        } catch (JsonProcessingException e) {
            throw new ValidationException(new ErrorResponse(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.FIELD_VALIDATION_ERROR,
                    "Invalid JSON format for resources or project managers.",
                    "v1/contracts/update"
            ));
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

    @Override
    public List<String> getAttachments(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BuildErrorMessage.buildErrorMessage(
                                ErrorType.NOT_FOUND,
                                ErrorCode.RESOURCE_NOT_FOUND,
                                Constants.format(Constants.RESOURCE_NOT_FOUND, "Contract", "ID", id)
                        )
                ));

        String attachmentId = contract.getAttachmentId();

        if (attachmentId == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(attachmentId);
    }


}









