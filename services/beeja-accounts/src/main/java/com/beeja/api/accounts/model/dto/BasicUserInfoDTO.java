package com.beeja.api.accounts.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserInfoDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String email;
}
