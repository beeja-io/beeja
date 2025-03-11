package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Resource;

import java.util.List;

public interface ResourcesService {
    List<Resource> getOrCreateResources(List<Resource> employees);

    Resource allocateResource(String employeeId, double allocation);
}
