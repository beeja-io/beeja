package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "FeedbackReceivers")
public class FeedbackReceivers {
    @Id
    private String id;

    private String organizationId;
    private String cycleId;
    private String questionnaireId;
    private String employeeId;
    private String fullName;
    private String email;
    private String department;
}
