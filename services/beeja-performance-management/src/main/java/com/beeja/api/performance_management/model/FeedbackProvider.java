package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feedback_providers")
public class FeedbackProvider {
    @Id
    private String id;
    private String organizationId;
    @Indexed
    private String employeeId;
    @Indexed
    private String cycleId;
    private String questionnaireId;
    private List<AssignedReviewer> assignedReviewers;
    private ProviderStatus providerStatus;
}
