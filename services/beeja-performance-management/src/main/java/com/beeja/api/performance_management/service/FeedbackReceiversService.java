package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.FeedbackReceivers;
import com.beeja.api.performance_management.request.ReceiverRequest;
import com.beeja.api.performance_management.response.ReceiverResponse;

import java.util.List;

public interface FeedbackReceiversService {

    List<FeedbackReceivers> addFeedbackReceivers(ReceiverRequest receiverRequest);

    List<FeedbackReceivers> updateFeedbackReceivers(String cycleId, ReceiverRequest receiverRequest);

    ReceiverResponse getFeedbackReceiversList(String cycleId, String questionnaireId);
}
