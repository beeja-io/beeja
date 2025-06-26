package com.beeja.api.performance_management.service;

import com.beeja.api.performance_management.requests.CreateReviewFormRequest;

public interface ReviewFormService {
    void createReviewForm(String reviewFormId, CreateReviewFormRequest request);

    void updateReviewForm(String reviewFormId, CreateReviewFormRequest request);
}
