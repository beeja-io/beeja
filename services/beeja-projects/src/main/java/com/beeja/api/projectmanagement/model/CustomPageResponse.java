package com.beeja.api.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageResponse<Timesheet> {
    private List<Timesheet> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
