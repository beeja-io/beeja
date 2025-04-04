package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.enums.SubscriptionName;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.subscriptions.Permissions;
import com.beeja.api.accounts.repository.PermissionRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
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

        Permissions dbPermissions = permissionRepository.findByName(SubscriptionName.ALL_PERMISSIONS);

        Set<String> jsonPermissions = getAllPermissions();

        List<Role> superAdminPermissions = rolesRepository.findAllByName("Super Admin");

//        TODO: Update logic to consider Subscription Plans
            if(!dbPermissions.getPermissions().containsAll(jsonPermissions))
            {
                dbPermissions.getPermissions().addAll(jsonPermissions);
                permissionRepository.save(dbPermissions);
            }
            superAdminPermissions.forEach(role -> {
                Set<String> saPermissions = role.getPermissions();
                if(!saPermissions.containsAll(dbPermissions.getPermissions())){
                    saPermissions.addAll(dbPermissions.getPermissions());
                    rolesRepository.save(role);
                }
            });
    }
    public Set<String> getAllPermissions() {
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
                return Collections.emptySet();
            }
            return permissionsSet;
    }
}

