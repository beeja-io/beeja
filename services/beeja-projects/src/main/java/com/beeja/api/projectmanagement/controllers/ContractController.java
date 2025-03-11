package com.beeja.api.projectmanagement.controllers;


import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Contract;

import com.beeja.api.projectmanagement.repository.ContractRepository;
import com.beeja.api.projectmanagement.request.ContractRequest;
import com.beeja.api.projectmanagement.service.ContractService;
import com.beeja.api.projectmanagement.utils.BuildErrorMessage;
import com.beeja.api.projectmanagement.utils.Constants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Contract> addContract(@Valid @ModelAttribute ContractRequest contractRequest) {
        return ResponseEntity.ok(contractService.addContract(contractRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable String id, @RequestBody ContractRequest updateRequest) {
        Contract updatedContract = contractService.updateContract(id, updateRequest);
        return new ResponseEntity<>(updatedContract, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable String id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<String>> getAttachments(@PathVariable String id) {
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
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(Collections.singletonList(attachmentId));
    }




}
