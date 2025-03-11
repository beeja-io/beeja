package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Resource;
import com.beeja.api.projectmanagement.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v1/resources")
public class ResourceController {
    @Autowired
    private ResourcesService resourceService;


    @PostMapping
    public ResponseEntity<List<Resource>> addOrGetResources(@RequestBody List<Resource> employees) {
        List<Resource> resources = resourceService.getOrCreateResources(employees);
        return ResponseEntity.ok(resources);
    }


    @PostMapping("/allocate")
    public Resource allocateResource(@RequestParam String employeeId, @RequestParam double allocation) {
        return resourceService.allocateResource(employeeId, allocation);
    }





}
