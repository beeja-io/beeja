package com.beeja.api.projectmanagement.controllers;


import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.exceptions.MethodArgumentNotValidException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


import static com.beeja.api.projectmanagement.constants.PermissionConstants.CREATE_CLIENT;

@RestController
@RequestMapping("v1/clients")
public class ClientController {

    @Autowired private ClientService clientService;

    @PostMapping
   @HasPermission(CREATE_CLIENT)
    public ResponseEntity<?> addClient(@Valid @RequestBody Client client, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(bindingResult);
        }
        return ResponseEntity.ok(clientService.addClient(client));
    }

    @PatchMapping("/update/{clientId}")
    public ResponseEntity<Client> updateClientPartially(
            @PathVariable String clientId,
            @RequestBody Map<String, Object> updates) {
        Client updatedClient = clientService.updateClientPartially(clientId, updates);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable String id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getClients() {
        List<ClientDTO> clients = clientService.getSortedClients();

        if (clients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(clients);
    }

}

