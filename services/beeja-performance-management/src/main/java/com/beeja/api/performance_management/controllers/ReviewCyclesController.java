package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.requests.AssignManagersRequest;
import com.beeja.api.performance_management.requests.ReviewCycleRequest;
import com.beeja.api.performance_management.response.ReviewCycleResponse;
import com.beeja.api.performance_management.service.ReviewCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/reviewCycles")
public class ReviewCyclesController {

    @Autowired
    ReviewCycleService reviewCycleService;

    @PostMapping
    public String createReviewCycle(@RequestBody ReviewCycleRequest request){
        return reviewCycleService.createReviewCycle(request);
    }

    @PostMapping("{reviewCycleId}/assign-managers")
    public void assignMangers(@PathVariable String reviewCycleId, @RequestBody AssignManagersRequest managers){
        reviewCycleService.assignManagers(reviewCycleId,managers);
    }

    @GetMapping("{reviewCycleId}")
    public ResponseEntity<ReviewCycleResponse> getReviewCycleById(@PathVariable String reviewCycleId){
        return new ResponseEntity(reviewCycleService.getReviewCycleById(reviewCycleId), HttpStatus.OK);
    }

    @GetMapping
    public List<ReviewCycle> getAllReviewCycles(){
        return  reviewCycleService.getAllRCs();
    }

    @PatchMapping("{reviewCycleId}")
    public ResponseEntity<ReviewCycle> updateReviewCycle(@PathVariable String reviewCycleId, @RequestBody ReviewCycleRequest request){
        return ResponseEntity.ok(reviewCycleService.updateReviewCycle(reviewCycleId,request));
    }

}
