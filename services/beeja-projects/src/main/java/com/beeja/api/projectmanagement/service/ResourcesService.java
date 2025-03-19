package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Resource;

import java.util.List;
import java.util.Map;

public interface ResourcesService {
    List<Resource> getOrCreateResources(List<Resource> employees);
   Map<String, String> getEmployeeNamesByIds(List<String> employeeIds);
    List<Resource> updateResourceAllocations(List<Resource> resources, Map<String, String> employeeNamesMap);
}
