package com.beeja.api.projectmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDropdownDto {
    private String id;
    private String contractId;
    private String contractTitle;
}