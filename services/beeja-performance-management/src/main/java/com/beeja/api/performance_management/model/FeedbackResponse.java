package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.enums.FormStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feedback_responses")
public class FeedbackResponse {

    @Id
    private String id;

    private String organizationId;

    @Indexed
    private String formId;

    private String employeeId;

    @Indexed
    private String cycleId;

    @Indexed
    private String reviewerId;

    private String reviewerRole;

    private List<QuestionAnswer> responses;

    private Instant submittedAt;

    private FormStatus status;
}
