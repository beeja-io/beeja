package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.SubscriptionName;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.subscriptions.Permissions;
import com.beeja.api.accounts.repository.PermissionRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.utils.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
@Service
@Slf4j
public class SuperAdminPermissionUpdater {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @PostConstruct
    public void initPermissionsUpdate() {
        updatePermissions();
    }

    @Async
    public void updatePermissions() {
        log.info(Constants.UPDATING_PERMISSIONS_FOR_ALL_SUPER_ADMINS);

        Permissions dbPermissions = permissionRepository.findByName(SubscriptionName.ALL_PERMISSIONS);
        if (dbPermissions == null) {
            log.warn(Constants.CREATING_NEW_PERMISSIONS_ENTRY, SubscriptionName.ALL_PERMISSIONS);
            dbPermissions = new Permissions();
            dbPermissions.setName(SubscriptionName.ALL_PERMISSIONS);
            dbPermissions.setPermissions(new HashSet<>());
        }

        Set<String> jsonPermissions = getAllPermissions();

        if (jsonPermissions.isEmpty()) {
            log.warn("No permissions loaded from JSON. Skipping update.");
            return;
        }

        boolean isDbUpdated = dbPermissions.getPermissions().addAll(jsonPermissions);
        if (isDbUpdated) {
            permissionRepository.save(dbPermissions);
            log.info(Constants.SUCCESSFULLY_UPDATED_PERMISSIONS_ENTRY, SubscriptionName.ALL_PERMISSIONS);
        }

        List<Role> superAdminRoles = rolesRepository.findAllByName("Super Admin");
        Permissions finalDbPermissions = dbPermissions;
        for (Role role : superAdminRoles) {
            if (role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }

            boolean isRoleUpdated = role.getPermissions().addAll(finalDbPermissions.getPermissions());
            if (isRoleUpdated) {
                rolesRepository.save(role);
                log.info("Updated Super Admin role with new permissions: {}", role.getName());
            }
        }

        log.info(Constants.PERMISSIONS_UPDATED_SUCCESSFULLY);
    }

    public Set<String> getAllPermissions() {
        log.info(Constants.GETTING_ALL_PERMISSIONS_FROM_JSON);
        Set<String> permissionsSet = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ClassPathResource resource = new ClassPathResource("permissions/AllPermissions.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            rootNode.fields().forEachRemaining(entry -> {
                JsonNode arrayNode = entry.getValue();
                if (arrayNode.isArray()) {
                    for (JsonNode permission : arrayNode) {
                        permissionsSet.add(permission.asText());
                    }
                }
            });

            log.info("Total unique permissions loaded: {}", permissionsSet.size());

        } catch (IOException e) {
            log.error(Constants.ERROR_READING_PERMISSIONS_JSON, e);
            return Collections.emptySet();
        }

        return permissionsSet;
    }

}
