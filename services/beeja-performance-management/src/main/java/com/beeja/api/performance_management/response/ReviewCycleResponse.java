package com.beeja.api.performance_management.response;

import com.beeja.api.performance_management.enums.ReviewCycleStatus;
import com.beeja.api.performance_management.enums.ReviewType;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewCycleResponse {
    private String name;
    private ReviewType reviewType;
    private Date startDate;
    private Date endDate;
    private ReviewCycleStatus status;
}
