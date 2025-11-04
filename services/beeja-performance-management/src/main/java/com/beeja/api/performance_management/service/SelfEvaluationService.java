package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.SelfEvaluation;
import java.util.List;

/**
 * Service interface for submitting and retrieving self-evaluations.
 */
public interface SelfEvaluationService {

    /** Submits a self-evaluation for an employee. */
    SelfEvaluation submitSelfEvaluation(SelfEvaluation self);

    /** Retrieves all self-evaluations submitted by an employee. */
    List<SelfEvaluation> getByEmployee(String employeeId);
}
