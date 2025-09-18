package com.beeja.api.performance_management.responses;

import com.beeja.api.performance_management.model.ReviewCycle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCycleDto {
    private String id;
    private String cycleId;
    private String name;
    private String description;
    private String reviewType;
    private String reviewFormId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private List<String> managerIds;
    private List<String> employeeIds;
    private String organizationId;
    private String createdBy;

    public ReviewCycleDto(ReviewCycle cycle) {
        this.id = cycle.getId();
        this.cycleId = cycle.getCycleId();
        this.name = cycle.getName();
        this.description = cycle.getDescription();
        this.reviewType = String.valueOf(cycle.getReviewType());
        this.reviewFormId = cycle.getReviewFormId();
        this.startDate = cycle.getStartDate();
        this.endDate = cycle.getEndDate();
        this.status = cycle.getStatus();
        this.managerIds = cycle.getManagerIds();
        this.employeeIds = cycle.getEmployeeIds();
        this.organizationId = cycle.getOrganizationId();
        this.createdBy = cycle.getCreatedBy();
    }
}
