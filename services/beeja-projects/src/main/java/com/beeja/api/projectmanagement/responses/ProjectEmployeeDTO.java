package com.beeja.api.projectmanagement.responses;

import com.beeja.api.projectmanagement.model.dto.EmployeeNameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEmployeeDTO {
    private List<EmployeeNameDTO> managers;
    private List<EmployeeNameDTO> resources;
}

