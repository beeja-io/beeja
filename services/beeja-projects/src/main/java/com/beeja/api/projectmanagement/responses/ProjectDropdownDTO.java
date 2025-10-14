package com.beeja.api.projectmanagement.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectDropdownDTO {
    private String projectId;
    private String name;
    private String clientId;
}
