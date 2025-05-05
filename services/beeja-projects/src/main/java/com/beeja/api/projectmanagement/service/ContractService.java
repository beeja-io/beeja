package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.request.ContractRequest;

import java.util.List;

public interface ContractService {
    Contract createContract(ContractRequest request);
    Contract getContractById(String contractId);
    List<Contract> getContractsByProjectId(String projectId);
    Contract updateContract(String contractId, ContractRequest request);
}
