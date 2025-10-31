package com.beeja.api.performance_management.request;

import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverRequest {
    private String cycleId;
    private String questionnaireId;
    private List<ReceiverDetails> receiverDetails;
}
