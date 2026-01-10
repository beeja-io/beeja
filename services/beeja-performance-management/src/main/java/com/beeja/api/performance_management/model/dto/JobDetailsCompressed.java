package com.beeja.api.performance_management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailsCompressed {
    private String designation;
    private String employementType;
    private String department;
}
