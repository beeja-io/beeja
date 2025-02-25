package com.beeja.api.projectmanagement.controllers;


import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.exceptions.MethodArgumentNotValidException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

import static com.beeja.api.projectmanagement.constants.PermissionConstants.*;

@RestController
@Slf4j
@RequestMapping("v1/clients")
public class ClientController {

    @Autowired private ClientService clientService;

    @PostMapping
   @HasPermission(CREATE_CLIENT)
    public ResponseEntity<Client> addClient(@Valid @RequestBody ClientRequest clientRequest, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(bindingResult);
        }
        return ResponseEntity.ok(clientService.addClient(clientRequest));
    }

    @PatchMapping("/{id}")
    @HasPermission(UPDATE_CLIENT)
    public ResponseEntity<Client> updateClientPartially(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        Client updatedClient = clientService.updateClientPartially(id, updates);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/{id}")
    @HasPermission(GET_CLIENT)
    public ResponseEntity<Client> getClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    @HasPermission(GET_CLIENT)
    public ResponseEntity<List<ClientDTO>> getClients() {
        List<ClientDTO> clients = clientService.getClients();

        if (clients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(clients);
    }

}

