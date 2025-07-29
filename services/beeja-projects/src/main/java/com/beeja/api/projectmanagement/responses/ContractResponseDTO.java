package com.beeja.api.projectmanagement.responses;

import com.beeja.api.projectmanagement.model.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {
    private Map<String, Object> metadata;
    private List<ContractResponsesDTO> contracts;

}
