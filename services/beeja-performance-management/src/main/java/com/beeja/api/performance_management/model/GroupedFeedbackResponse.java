package com.beeja.api.performance_management.model;

import com.beeja.api.performance_management.model.dto.QRDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroupedFeedbackResponse {
    private List<QRDTO> questions;

    public GroupedFeedbackResponse(List<QRDTO> questions) {
        this.questions = questions;
    }

}
