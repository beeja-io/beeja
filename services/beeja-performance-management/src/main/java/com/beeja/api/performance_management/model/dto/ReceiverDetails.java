package com.beeja.api.performance_management.model.dto;

import com.beeja.api.performance_management.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverDetails{
    private String employeeId;
    private String fullName;
    private String department;
    private String email;
    private ProviderStatus providerStatus;
}
