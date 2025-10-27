package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.model.FeedbackProvider;
import com.beeja.api.performance_management.request.FeedbackProviderRequest;
import com.beeja.api.performance_management.response.FeedbackProviderDetails;

import java.util.List;

public interface FeedbackProvidersService {

    List<FeedbackProvider> assignFeedbackProvider(String employeeId, FeedbackProviderRequest requestDto);

    List<FeedbackProvider> updateFeedbackProviders(FeedbackProviderRequest request, String employeeId);

    FeedbackProviderDetails getFeedbackFormDetails(String employeeId, String cycleId, String providerStatus);
}

