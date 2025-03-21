package com.beeja.api.employeemanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeValues {
    private Set<String> employmentTypes;
    private Set<String> designations;
    private Set<String> departments;
}
