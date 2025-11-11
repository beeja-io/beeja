package com.beeja.api.performance_management.model.dto;

import lombok.Data;

@Data
public class OverallRatingRequestDTO {
    private Double rating;
    private String comments;
}
