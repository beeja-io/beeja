package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.FeedbackResponse;
import com.beeja.api.performance_management.model.dto.CycleWithResponsesDTO;
import com.beeja.api.performance_management.model.dto.EmployeeGroupedResponsesDTO;
import com.beeja.api.performance_management.model.dto.SubmitFeedbackRequest;
import com.beeja.api.performance_management.response.MyFeedbackFormResponse;

import java.util.List;

/**
 * Service interface for managing feedback responses within the performance management system.
 * <p>
 * This service provides operations to submit, retrieve, and group feedback responses
 * across employees, evaluation cycles, and feedback forms.
 */
public interface FeedbackResponseService {

    /**
     * Submits a feedback response based on the provided request data.
     *
     * @param req the {@link SubmitFeedbackRequest} object containing feedback details
     * @return the saved {@link FeedbackResponse} entity
     */
    FeedbackResponse submitFeedback(SubmitFeedbackRequest req);

    /**
     * Retrieves all feedback responses associated with a given feedback form.
     *
     * @param formId the unique identifier of the feedback form
     * @return a list of {@link FeedbackResponse} objects for the specified form
     */
    List<FeedbackResponse> getByFormId(String formId);

    /**
     * Retrieves all feedback responses submitted for a specific employee
     * during a particular evaluation cycle.
     *
     * @param employeeId the unique identifier of the employee
     * @param cycleId    the unique identifier of the evaluation cycle
     * @return a list of {@link FeedbackResponse} objects matching the given employee and cycle
     */
    List<FeedbackResponse> getByEmployeeAndCycle(String employeeId, String cycleId);

    /**
     * Retrieves all feedback responses submitted for a specific employee across all cycles.
     *
     * @param employeeId the unique identifier of the employee
     * @return a list of {@link FeedbackResponse} objects associated with the employee
     */
    List<FeedbackResponse> getByEmployee(String employeeId);

    /**
     * Retrieves all feedback responses associated with a specific evaluation cycle,
     * including additional cycle details.
     *
     * @param cycleId the unique identifier of the evaluation cycle
     * @return a {@link CycleWithResponsesDTO} containing cycle information and responses
     */
    CycleWithResponsesDTO getResponsesForCycle(String cycleId);

    /**
     * Retrieves feedback responses for a specific employee, grouped by feedback cycle.
     *
     * @param employeeId the unique identifier of the employee
     * @return an {@link EmployeeGroupedResponsesDTO} containing grouped feedback responses
     *         and associated cycle information
     */
    EmployeeGroupedResponsesDTO getGroupedResponsesWithCycleByEmployee(String employeeId);

    /**
     * Retrieves all feedback forms assigned to the currently logged-in user,
     * including metadata such as due dates and status.
     *
     * @return a list of {@link MyFeedbackFormResponse} objects representing the user’s feedback forms
     */
    List<MyFeedbackFormResponse> getMyFeedbackForms();

    /**
     * Retrieves feedback responses submitted by the currently logged-in user for a specific cycle,
     * grouped by relevant categories or evaluation criteria.
     *
     * @param cycleId the unique identifier of the evaluation cycle
     * @return an {@link EmployeeGroupedResponsesDTO} containing the user’s responses for that cycle
     */
    EmployeeGroupedResponsesDTO getMyResponsesByCycle(String cycleId);
}
