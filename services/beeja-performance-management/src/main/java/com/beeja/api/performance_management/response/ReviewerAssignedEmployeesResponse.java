package com.beeja.api.performance_management.response;

import com.beeja.api.performance_management.model.dto.AssignedEmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewerAssignedEmployeesResponse {
    private String reviewerId;
    private String reviewerName;
    private List<AssignedEmployeeDTO> assignedEmployees;
}
