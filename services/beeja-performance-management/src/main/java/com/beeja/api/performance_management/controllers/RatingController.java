package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.FinalRating;
import com.beeja.api.performance_management.model.dto.ComputeRatingRequest;
import com.beeja.api.performance_management.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing employee ratings.
 */
@RestController
@RequestMapping("/v1/api/ratings")
@Validated
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /** Computes the final rating for an employee in a specific cycle. */
    @PostMapping
    public ResponseEntity<FinalRating> computeRating(@Valid @RequestBody ComputeRatingRequest req) {
        return ResponseEntity.ok(
                ratingService.computeRating(req.getEmployeeId(), req.getCycleId(), req.getComputedBy())
        );
    }

    /** Retrieves all ratings for an employee in a specific cycle. */
    @GetMapping("/employee/{employeeId}/cycle/{cycleId}")
    public ResponseEntity<List<FinalRating>> getRatings(@PathVariable String employeeId, @PathVariable String cycleId) {
        return ResponseEntity.ok(ratingService.getRatings(employeeId, cycleId));
    }

    /** Publishes a specific final rating by its ID. */
    @PatchMapping("/{id}/publish")
    public ResponseEntity<FinalRating> publish(@PathVariable String id) {
        return ResponseEntity.ok(ratingService.publishRating(id));
    }
}
