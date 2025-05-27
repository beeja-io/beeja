package com.beeja.api.financemanagementservice.modals.clients.finance;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeNameDTO {
    private String employeeId;
    private String fullName;

    // Constructor, Getters, Setters
}