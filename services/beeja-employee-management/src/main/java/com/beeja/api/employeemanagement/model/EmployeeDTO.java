package com.beeja.api.employeemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    @Id
    private String id;
    private String beejaAccountId;
    private String employeeId;
    private String employmentType;
    private String organizationId;
    private Address address;
    private PersonalInformation personalInformation;
}
