package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.dto.CycleWithResponsesDTO;
import com.beeja.api.performance_management.model.dto.EmployeeGroupedResponsesDTO;
import com.beeja.api.performance_management.model.dto.SubmitFeedbackRequest;

import java.util.List;

/**
 * Service interface for managing feedback responses.
 */
public interface FeedbackResponseService {

    /** Submits a feedback response. */
    FeedbackResponse submitFeedback(SubmitFeedbackRequest req);

    /** Retrieves feedback responses by form ID. */
    List<FeedbackResponse> getByFormId(String formId);

    /** Retrieves feedback responses by employee ID and cycle ID. */
    List<FeedbackResponse> getByEmployeeAndCycle(String employeeId, String cycleId);

    /** Retrieves all feedback responses for a specific employee. */
    List<FeedbackResponse> getByEmployee(String employeeId);

    /** Retrieves all feedback responses for a specific evaluation cycle. */
    CycleWithResponsesDTO getResponsesForCycle(String cycleId);

    /** Retrieves grouped feedback responses for an employee along with cycle details. */
    EmployeeGroupedResponsesDTO getGroupedResponsesWithCycleByEmployee(String employeeId);
}
