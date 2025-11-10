package com.beeja.api.performance_management.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class    MyFeedbackFormResponse {
    private String cycleId;
    private String cycleName;
    private String type;
    private String status;
    private String startDate;
    private String endDate;
}
