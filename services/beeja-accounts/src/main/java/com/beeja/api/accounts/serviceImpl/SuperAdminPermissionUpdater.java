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
    RolesRepository rolesRepository;

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
            permissionRepository.save(dbPermissions);
            log.info(Constants.SUCCESSFULLY_CREATED_NEW_PERMISSION_ENTRY, SubscriptionName.ALL_PERMISSIONS);
        }
        Set<String> jsonPermissions = getAllPermissions();

        List<Role> superAdminPermissions = rolesRepository.findAllByName("Super Admin");

//        TODO: Update logic to consider Subscription Plans
            if(!dbPermissions.getPermissions().containsAll(jsonPermissions))
            {
                log.info(Constants.ADDING_NEW_PERMISSIONS_TO_DB);
                dbPermissions.getPermissions().addAll(jsonPermissions);
                permissionRepository.save(dbPermissions);
            }
            log.info(Constants.ADDING_NEW_PERMISSIONS_TO_SUPER_ADMINS);
        Permissions finalDbPermissions = dbPermissions;
        superAdminPermissions.forEach(role -> {
                Set<String> saPermissions = role.getPermissions();
                if(!saPermissions.containsAll(finalDbPermissions.getPermissions())){
                    saPermissions.addAll(finalDbPermissions.getPermissions());
                    rolesRepository.save(role);
                }
            });
            log.info(Constants.PERMISSIONS_UPDATED_SUCCESSFULLY);
    }
    public Set<String> getAllPermissions() {
        log.info(Constants.GETTING_ALL_PERMISSIONS_FROM_JSON);
            Set<String> permissionsSet = new HashSet<>();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ClassPathResource resource = new ClassPathResource("permissions/AllPermissions.json");
                JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
                for (JsonNode arrayNode : rootNode) {
                    if (arrayNode.isArray()) {
                        arrayNode.forEach(permission -> permissionsSet.add(permission.asText()));
                    }
                }
            } catch (IOException e) {
                log.error(Constants.ERROR_READING_PERMISSIONS_JSON, e);
                return Collections.emptySet();
            }
            return permissionsSet;
    }
}

