package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.requests.CreateReviewFormRequest;
import com.beeja.api.performance_management.service.ReviewFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/reviewForm")
public class ReviewFormController {

    @Autowired
    ReviewFormService reviewFormService;

    @PostMapping("{reviewCycleId}")
    public void createReviewForm(@PathVariable String reviewCycleId, @RequestBody CreateReviewFormRequest request){
        reviewFormService.createReviewForm( reviewCycleId,request);
    }

   @PutMapping("{reviewFormId}")
    public void updateCreatedReviewForm(@PathVariable String reviewFormId, @RequestBody CreateReviewFormRequest request){
        reviewFormService.updateReviewForm(reviewFormId,request);
   }

}
