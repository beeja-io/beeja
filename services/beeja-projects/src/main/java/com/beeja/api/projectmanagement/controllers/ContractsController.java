package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/contracts")
public class ContractsController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody ContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContract(request));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<Contract> getContractById(@PathVariable String contractId) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Contract>> getContractsByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(contractService.getContractsByProjectId(projectId));
    }

    @PutMapping("/{contractId}")
    public ResponseEntity<Contract> updateContract(@PathVariable String contractId,
                                                   @RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.updateContract(contractId, request));
    }
}
