package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.enums.ErrorCode;
import com.beeja.api.performance_management.enums.ErrorType;
import com.beeja.api.performance_management.enums.ProviderStatus;
import com.beeja.api.performance_management.exceptions.BadRequestException;
import com.beeja.api.performance_management.exceptions.DuplicateDataException;
import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.model.dto.AssignedReviewer;
import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import com.beeja.api.performance_management.repository.FeedbackProviderRepository;
import com.beeja.api.performance_management.repository.FeedbackReceiverRepository;
import com.beeja.api.performance_management.request.ReceiverRequest;
import com.beeja.api.performance_management.response.ReceiverResponse;
import com.beeja.api.performance_management.service.FeedbackReceiversService;
import com.beeja.api.performance_management.utils.Constants;
import com.beeja.api.performance_management.utils.ErrorUtils;
import com.beeja.api.performance_management.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FeedbackReceiversServiceImpl implements FeedbackReceiversService {

    @Autowired
    FeedbackReceiverRepository feedbackReceiverRepository;

    @Autowired
    FeedbackProviderRepository feedbackProviderRepository;

    @Override
    public List<FeedbackReceivers> addFeedbackReceivers(ReceiverRequest receiverRequest) {

        if (receiverRequest == null) {
            log.error(Constants.FEEDBACK_RECEIVERS_NULL);
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.FEEDBACK_REQUEST_NULL)
            );
        }

        Set<String> seenEmployeeIds = new HashSet<>();
        for (ReceiverDetails r : receiverRequest.getReceiverDetails()) {
            String empId = r.getEmployeeId();
            if (!StringUtils.hasText(empId)) {
                throw new BadRequestException(
                        ErrorUtils.formatError(
                                ErrorType.VALIDATION_ERROR,
                                ErrorCode.NUll_VALUE,
                                Constants.EMPLOYEE_ID_NOT_EMPTY)
                );
            }
            empId = empId.trim().toLowerCase();
            if (!seenEmployeeIds.add(empId)) {
                throw new DuplicateDataException(
                        ErrorUtils.formatError(
                                ErrorType.VALIDATION_ERROR,
                                ErrorCode.DUPLICATE_RESOURCE,
                                Constants.DUPLICATE_EMPLOYEE_ID + empId)
                );
            }
        }
        String cycleId = receiverRequest.getCycleId();
        String questionnaireId = receiverRequest.getQuestionnaireId();
        List<ReceiverDetails> receiverRequests = receiverRequest.getReceiverDetails();

        if (!StringUtils.hasText(cycleId) || !StringUtils.hasText(questionnaireId)) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            "Cycle ID and Questionnaire ID must not be empty")
            );
        }

        if (receiverRequests == null || receiverRequests.isEmpty()) {
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.RECEIVER_LIST_CANNOT_BE_EMPTY)
            );
        }

        String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

        List<FeedbackReceivers> existingReceivers =
                feedbackReceiverRepository.findByOrganizationIdAndCycleIdAndQuestionnaireId(
                        organizationId, cycleId, questionnaireId);

        Map<String, FeedbackReceivers> existingMap = existingReceivers.stream()
                .collect(Collectors.toMap(FeedbackReceivers::getEmployeeId, r -> r));

        List<FeedbackReceivers> toSave = receiverRequests.stream()
                .peek(r -> {
                    if (!StringUtils.hasText(r.getEmployeeId()) ||
                            !StringUtils.hasText(r.getFullName()) ||
                            !StringUtils.hasText(r.getDepartment())) {
                        throw new BadRequestException(
                                ErrorUtils.formatError(
                                        ErrorType.VALIDATION_ERROR,
                                        ErrorCode.NUll_VALUE,
                                        Constants.RECEIVER_DETAIL_NOT_EMPTY)
                        );
                    }

                    if (existingMap.containsKey(r.getEmployeeId())) {
                        throw new DuplicateDataException(
                                ErrorUtils.formatError(
                                        ErrorType.VALIDATION_ERROR,
                                        ErrorCode.DUPLICATE_RESOURCE,
                                        Constants.DUPLICATE_EMPLOYEE_ID + r.getEmployeeId()
                        ));
                    }
                })
                .map(r -> FeedbackReceivers.builder()
                        .organizationId(organizationId)
                        .cycleId(cycleId)
                        .questionnaireId(questionnaireId)
                        .employeeId(r.getEmployeeId().trim())
                        .fullName(r.getFullName().trim())
                        .department(r.getDepartment().trim())
                        .email(StringUtils.hasText(r.getEmail()) ? r.getEmail().trim() : null)
                        .build()
                )
                .toList();


        return feedbackReceiverRepository.saveAll(toSave);
    }

    @Override
    public List<FeedbackReceivers> updateFeedbackReceivers(String cycleId, ReceiverRequest receiverRequest) {

        if (receiverRequest == null) {
            log.error(Constants.FEEDBACK_RECEIVERS_NULL);
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.FEEDBACK_RECEIVERS_NULL)
            );
        }

        String questionnaireId = receiverRequest.getQuestionnaireId();
        List<ReceiverDetails> receiverDetails = receiverRequest.getReceiverDetails();

        if (!StringUtils.hasText(cycleId) || !StringUtils.hasText(questionnaireId)) {
            log.error(Constants.CYCLE_ID_QUESTIONNAIRE_ID_EMPTY, cycleId, questionnaireId);
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.CYCLE_ID_QUESTIONNAIRE_ID_EMPTY)
            );
        }

        if (receiverDetails == null || receiverDetails.isEmpty()) {
            log.error(Constants.RECEIVER_CYCLE_QUESTIONNAIRE_EMPTY, cycleId, questionnaireId);
            throw new BadRequestException(
                    ErrorUtils.formatError(
                            ErrorType.VALIDATION_ERROR,
                            ErrorCode.NUll_VALUE,
                            Constants.FEEDBACK_RECEIVERS_NULL)
            );
        }

        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        Map<String, FeedbackReceivers> existingMap = feedbackReceiverRepository
                .findByOrganizationIdAndCycleIdAndQuestionnaireId(organizationId, cycleId, questionnaireId)
                .stream()
                .collect(Collectors.toMap(FeedbackReceivers::getEmployeeId, fr -> fr));

        Set<String> incomingIds = new HashSet<>();
        List<FeedbackReceivers> toSave = new ArrayList<>();

        for (ReceiverDetails r : receiverDetails) {
            String empId = r.getEmployeeId() != null ? r.getEmployeeId().trim() : null;

            if (!StringUtils.hasText(empId)) {
                log.warn(Constants.EMPTY_EMPLOYEE_ID);
                continue;
            }

            incomingIds.add(empId);

            if (!StringUtils.hasText(r.getFullName()) || !StringUtils.hasText(r.getDepartment())) {
                log.error(Constants.RECEIVER_DETAIL_NOT_EMPTY);
                throw new BadRequestException(
                        ErrorUtils.formatError(
                                ErrorType.VALIDATION_ERROR,
                                ErrorCode.NUll_VALUE,
                                Constants.RECEIVER_DETAIL_NOT_EMPTY)
                );
            }

            FeedbackReceivers receiver = existingMap.get(empId);

            if (receiver == null) {
                receiver = new FeedbackReceivers();
                receiver.setOrganizationId(organizationId);
                receiver.setCycleId(cycleId);
                receiver.setQuestionnaireId(questionnaireId);
                receiver.setEmployeeId(empId);
            }

            receiver.setFullName(r.getFullName().trim());
            receiver.setDepartment(r.getDepartment().trim());
            receiver.setEmail(StringUtils.hasText(r.getEmail()) ? r.getEmail().trim() : null);

            toSave.add(receiver);
        }

        List<FeedbackReceivers> toDelete = existingMap.values().stream()
                .filter(fr -> !incomingIds.contains(fr.getEmployeeId()))
                .toList();

        if (!toDelete.isEmpty()) {
            feedbackReceiverRepository.deleteAll(toDelete);
            log.info(Constants.REMOVE_RECEIVER, toDelete.size());
        }

        return feedbackReceiverRepository.saveAll(toSave);
    }

    @Override
    public ReceiverResponse getFeedbackReceiversList(String cycleId, String questionnaireId) {
        String organizationId = UserContext.getLoggedInUserOrganization().get(Constants.ID).toString();
        List<FeedbackReceivers> receivers = feedbackReceiverRepository
                .findByOrganizationIdAndCycleIdAndQuestionnaireId(organizationId, cycleId, questionnaireId);

        if (receivers == null || receivers.isEmpty()) {
            log.warn(Constants.NO_RECEIVER_FOUND,
                    organizationId, cycleId, questionnaireId);

            return ReceiverResponse.builder()
                    .cycleId(cycleId)
                    .questionnaireId(questionnaireId)
                    .receivers(Collections.emptyList())
                    .build();
        }

        List<ReceiverDetails> receiverDetailsList = new ArrayList<>();

        for (FeedbackReceivers receiver : receivers) {
            Optional<FeedbackProvider> feedbackProviderOpt =
                    feedbackProviderRepository.findByOrganizationIdAndEmployeeIdAndCycleId(
                            organizationId,
                            receiver.getEmployeeId(),
                            cycleId
                    );

            ProviderStatus providerStatus;

            if (feedbackProviderOpt.isEmpty()) {
                providerStatus = ProviderStatus.NOT_ASSIGNED;

            } else {
                FeedbackProvider feedbackProvider = feedbackProviderOpt.get();
                List<AssignedReviewer> reviewers = feedbackProvider.getAssignedReviewers();
                if (reviewers == null || reviewers.isEmpty()) {
                    log.warn(Constants.NOT_ASSIGNED, receiver.getEmployeeId(), feedbackProvider.getId());
                    providerStatus = ProviderStatus.NOT_ASSIGNED;

                } else {
                    boolean allCompleted = reviewers.stream()
                            .allMatch(r -> r.getStatus() == ProviderStatus.COMPLETED);
                    boolean anyInProgress = reviewers.stream()
                            .anyMatch(r -> r.getStatus() == ProviderStatus.IN_PROGRESS);

                    if (allCompleted) {
                        providerStatus = ProviderStatus.COMPLETED;
                    } else if (anyInProgress) {
                        providerStatus = ProviderStatus.IN_PROGRESS;
                    } else {
                        providerStatus = ProviderStatus.NOT_ASSIGNED;
                    }
                }
            }
            ReceiverDetails details = ReceiverDetails.builder()
                    .employeeId(receiver.getEmployeeId())
                    .fullName(receiver.getFullName())
                    .department(receiver.getDepartment())
                    .email(receiver.getEmail())
                    .providerStatus(providerStatus)
                    .build();

            receiverDetailsList.add(details);
        }

        return ReceiverResponse.builder()
                .cycleId(cycleId)
                .questionnaireId(questionnaireId)
                .receivers(receiverDetailsList)
                .build();
    }
}
