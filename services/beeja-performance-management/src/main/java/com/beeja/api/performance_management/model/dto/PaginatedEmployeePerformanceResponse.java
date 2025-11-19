package com.beeja.api.performance_management.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedEmployeePerformanceResponse {

    private long totalRecords;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private List<EmployeePerformanceDTO> data;
}
