package com.beeja.api.performance_management.response;

import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiverResponse {
    private String cycleId;
    private String questionnaireId;
    private List<ReceiverDetails> receivers;
}

