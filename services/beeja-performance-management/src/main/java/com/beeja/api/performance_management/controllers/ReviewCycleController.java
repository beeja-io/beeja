package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.responses.ReviewCycleDto;
import com.beeja.api.performance_management.service.EnhancedPerformanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/performance/v1/review-cycles")
@CrossOrigin(origins = "*")
public class ReviewCycleController {

    @Autowired
    private EnhancedPerformanceService performanceService;

    @PostMapping
    public ResponseEntity<ReviewCycleDto> createReviewCycle(@Valid @RequestBody ReviewCycle reviewCycle) {
        ReviewCycle created = performanceService.createReviewCycle(reviewCycle);
        return ResponseEntity.ok(new ReviewCycleDto(created));
    }

    @GetMapping
    public ResponseEntity<List<ReviewCycleDto>> list() {
        List<ReviewCycleDto> dtos = performanceService.getAllReviewCycles()
                .stream()
                .map(ReviewCycleDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{cycleId}")
    public ResponseEntity<ReviewCycleDto> getReviewCycleById(@PathVariable String cycleId) {
        return performanceService.getReviewCycleById(cycleId)
                .map(rc -> ResponseEntity.ok(new ReviewCycleDto(rc)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{cycleId}")
    public ResponseEntity<ReviewCycleDto> updateReviewCycleById(@PathVariable String cycleId,
                                                                @RequestBody ReviewCycle reviewCycle) {
        ReviewCycle updated = performanceService.updateReviewCycle(cycleId, reviewCycle);
        return ResponseEntity.ok(new ReviewCycleDto(updated));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ReviewCycleDto>> getAllReviewCyclesbyManager(@PathVariable String managerId) {
        List<ReviewCycleDto> dtos = performanceService.getReviewCyclesByManager(managerId)
                .stream()
                .map(ReviewCycleDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}
