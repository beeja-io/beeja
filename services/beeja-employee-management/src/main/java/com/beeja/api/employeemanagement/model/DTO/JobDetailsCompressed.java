package com.beeja.api.employeemanagement.model.DTO;

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
