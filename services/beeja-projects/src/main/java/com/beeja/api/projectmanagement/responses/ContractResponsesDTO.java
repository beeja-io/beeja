package com.beeja.api.projectmanagement.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponsesDTO {
    private String contractId;
    private String projectId;
    private String contractTitle;
    private String projectName;
    private String clientName;
    private String status;
    private List<String> projectManagerIds;
    private List<String> projectManagerNames;

}
