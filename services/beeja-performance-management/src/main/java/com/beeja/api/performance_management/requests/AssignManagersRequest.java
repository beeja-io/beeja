package com.beeja.api.performance_management.requests;

import lombok.Data;

import java.util.List;

@Data
public class AssignManagersRequest {
    private List<String> managers;
}
