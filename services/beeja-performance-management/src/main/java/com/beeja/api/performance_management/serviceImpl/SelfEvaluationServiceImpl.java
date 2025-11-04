package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.model.SelfEvaluation;
import com.beeja.api.performance_management.repository.SelfEvaluationRepository;
import com.beeja.api.performance_management.service.SelfEvaluationService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Service implementation for handling self-evaluations.
 * Provides functionality to submit and retrieve self-evaluations for employees.
 */
@Service
public class SelfEvaluationServiceImpl implements SelfEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(SelfEvaluationServiceImpl.class);

    private final SelfEvaluationRepository selfRepo;

    public SelfEvaluationServiceImpl(SelfEvaluationRepository selfRepo) {
        this.selfRepo = selfRepo;
    }

    /**
     * Retrieves the organization ID from the logged-in user's context.
     *
     * @return Organization ID as a string
     * @throws IllegalStateException if the organization ID is not found
     */
    private String getOrgId() {
        var org = UserContext.getLoggedInUserOrganization();
        if (org == null || org.get("id") == null) {
            log.error(Constants.ORG_ID_NOT_FOUND_IN_CONTEXT);
            throw new IllegalStateException("Organization ID not found in UserContext");
        }
        return org.get("id").toString();
    }

    /**
     * Submits a self-evaluation for an employee.
     *
     * @param self SelfEvaluation object containing employee responses
     * @return Saved SelfEvaluation object
     * @throws ResponseStatusException if the employee has already submitted a self-evaluation
     * @throws IllegalArgumentException if employee ID or responses are missing
     */
    @Override
    public SelfEvaluation submitSelfEvaluation(SelfEvaluation self) {
        if (self == null || self.getEmployeeId() == null || self.getEmployeeId().isBlank()) {
            log.warn(Constants.INVALID_SELF_EVALUATION_MISSING_EMPLOYEE_ID);
            throw new IllegalArgumentException("Employee ID is required for self-evaluation submission.");
        }

        if (self.getResponses() == null || self.getResponses().isEmpty()) {
            log.warn(Constants.INVALID_SELF_EVALUATION_NO_RESPONSES, self.getEmployeeId());
            throw new IllegalArgumentException("Self-evaluation responses cannot be empty.");
        }

        String orgId = getOrgId();

        boolean alreadySubmitted = selfRepo.existsByEmployeeIdAndOrganizationIdAndSubmittedTrue(self.getEmployeeId(), orgId);
        if (alreadySubmitted) {
            log.warn(Constants.SELF_EVALUATION_ALREADY_SUBMITTED, self.getEmployeeId());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Self-evaluation already submitted by this employee."
            );
        }

        self.setOrganizationId(orgId);
        self.setSubmittedAt(Instant.now());
        self.setSubmitted(true);

        SelfEvaluation saved = selfRepo.save(self);
        log.info(Constants.SELF_EVALUATION_SUBMITTED_SUCCESSFULLY, self.getEmployeeId());
        return saved;
    }

    /**
     * Retrieves all self-evaluations submitted by a specific employee.
     *
     * @param employeeId Employee ID
     * @return List of SelfEvaluation objects
     * @throws IllegalArgumentException if employee ID is null or empty
     */
    @Override
    public List<SelfEvaluation> getByEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            log.warn(Constants.NULL_OR_EMPTY_EMPLOYEE_ID_WHILE_FETCHING_SELF_EVAL);
            throw new IllegalArgumentException("Employee ID is required.");
        }

        List<SelfEvaluation> evaluations = selfRepo.findByEmployeeIdAndOrganizationId(employeeId, getOrgId());
        log.info(Constants.FETCHED_SELF_EVALUATIONS_FOR_EMPLOYEE, evaluations.size(), employeeId);
        return evaluations;
    }
}
