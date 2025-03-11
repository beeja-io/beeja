package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.request.ContractRequest;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ContractService {
    Contract addContract(ContractRequest contractRequest);

    Contract getContractById(String id);

    List<Contract> getAllContracts();

    Contract updateContract(String id, ContractRequest updatedContract);

}
