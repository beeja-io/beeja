package com.beeja.api.accounts.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeValuesDTO {
    private Set<String> employmentTypes;
    private Set<String> designations;
    private Set<String> departments;
}
