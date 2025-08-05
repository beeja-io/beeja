package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.enums.ProjectStatus;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Project;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.responses.ContractResponseDTO;
import com.beeja.api.projectmanagement.responses.ContractResponsesDTO;
import com.beeja.api.projectmanagement.service.ContractService;

import java.util.HashMap;
import java.util.List;

import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing contracts within the project management system. Provides endpoints
 * for creating, retrieving, and updating contracts.
 */
@RestController
@RequestMapping("/v1/contracts")
public class ContractsController {

  @Autowired private ContractService contractService;

  /**
   * Creates a new contract based on the provided contract request.
   *
   * @param request the contract request containing contract details
   * @return a {@link ResponseEntity} containing the created contract and HTTP status {@code 201
   *     Created}
   */
  @PostMapping
  @HasPermission(PermissionConstants.CREATE_CONTRACT)
  public ResponseEntity<Contract> createContract(@RequestBody ContractRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContract(request));
  }

  /**
   * Retrieves a contract by its unique identifier.
   *
   * @param contractId the unique identifier of the contract
   * @return a {@link ResponseEntity} containing the contract and HTTP status {@code 200 OK}
   */
  @GetMapping("/{contractId}")
  @HasPermission(PermissionConstants.GET_CONTRACT)
  public ResponseEntity<Contract> getContractById(@PathVariable String contractId) {
    return ResponseEntity.ok(contractService.getContractById(contractId));
  }

  /**
   * Retrieves all contracts associated with a specific project.
   *
   * @param projectId the unique identifier of the project
   * @return a {@link ResponseEntity} containing the list of contracts and HTTP status {@code 200
   *     OK}
   */
  @GetMapping("/project/{projectId}")
  @HasPermission(PermissionConstants.GET_CONTRACT)
  public ResponseEntity<List<Contract>> getContractsByProject(@PathVariable String projectId) {
    return ResponseEntity.ok(contractService.getContractsByProjectId(projectId));
  }

  /**
   * Updates an existing contract identified by its unique identifier.
   *
   * @param contractId the unique identifier of the contract to update
   * @param request the contract request containing updated contract details
   * @return a {@link ResponseEntity} containing the updated contract and HTTP status {@code 200 OK}
   */
  @PutMapping("/{contractId}")
  @HasPermission(PermissionConstants.UPDATE_CONTRACT)
  public ResponseEntity<Contract> updateContract(
      @PathVariable String contractId, @RequestBody ContractRequest request) {
    return ResponseEntity.ok(contractService.updateContract(contractId, request));
  }

  @GetMapping
  @HasPermission(PermissionConstants.GET_CONTRACT)
  public ResponseEntity<ContractResponseDTO> getAllContracts( @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                     @RequestParam(required = false) String projectId,
                                                                     @RequestParam(required = false) ProjectStatus status) {
    String organizationId = UserContext.getLoggedInUserOrganization()
            .get(Constants.ID).toString();
    HashMap<String, Object> metadata = new HashMap<>();
    metadata.put(
            "totalSize",
            contractService.getTotalContractSize(organizationId,projectId, status)
    );

    List<ContractResponsesDTO> contracts = contractService.getAllContracts(organizationId,pageNumber, pageSize, projectId, status);
    ContractResponseDTO response = new ContractResponseDTO();
    response.setMetadata(metadata);
    response.setContracts(contracts);
    return ResponseEntity.ok(response);
  }
  @PatchMapping("/{contractId}/status")
  @HasPermission(PermissionConstants.UPDATE_CONTRACT)
  public ResponseEntity<Contract> changeContractStatus(
          @PathVariable String contractId,
          @RequestBody ProjectStatus status) {

    Contract updatedContract = contractService.changeContractStatus(contractId, status);
    return ResponseEntity.ok(updatedContract);
  }

}
