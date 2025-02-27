package com.beeja.api.projectmanagement.controller;

import com.beeja.api.projectmanagement.controllers.ClientController;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.enums.TaxCategory;
import com.beeja.api.projectmanagement.exceptions.MethodArgumentNotValidException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.TaxDetails;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;



public class ClientControllerTest {
    @InjectMocks
    private ClientController clientController;

    @Mock
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    private Map<String, Object> updateFields;

    @Mock
    private BindingResult bindingResult;

    private Client validClient;
    private String validClientId;
    private String invalidClientId;
    private List<ClientDTO> clientList;

    private ClientRequest validClientRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);


        validClient = new Client();
        validClient.setClientName("John Doe");
        validClient.setClientType(ClientType.INTERNAL);
        validClient.setClientId("C123");
        validClient.setEmail("john@example.com");
        validClient.setContact("+1234567890");
        validClient.setIndustry(Industry.ECOMMERCE);
        validClient.setDescription("A premium IT client");
        validClient.setLogo("logo.png");

        validClientId = "C123";
        invalidClientId = "C999";

        TaxDetails validTaxDetails = new TaxDetails();
        validTaxDetails.setTaxCategory(TaxCategory.VAT);
        validTaxDetails.setTaxNumber("VAT12345");
        validClient.setTaxDetails(validTaxDetails);

        Address validAddress = new Address();
        validAddress.setStreet("123 Main St");
        validAddress.setCity("Springfield");
        validAddress.setState("IL");
        validAddress.setPostalCode("62701");
        validAddress.setCountry("USA");

        validClient.setPrimaryAddress(validAddress);
        validClient.setBillingAddress(validAddress);

        validClientRequest = new ClientRequest();
        validClientRequest.setClientName("John Doe");
        validClientRequest.setClientType(ClientType.INTERNAL);
        validClientRequest.setEmail("john@example.com");
        validClientRequest.setContact("+1234567890");
        validClientRequest.setIndustry(Industry.ECOMMERCE);
        validClientRequest.setDescription("A premium IT client");
        validClientRequest.setLogo("logo.png");
        validClientRequest.setTaxDetails(validTaxDetails);
        validClientRequest.setPrimaryAddress(validAddress);

        // Initialize list with a ClientDTO
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setClientId("C123");
        clientDTO.setClientName("John Doe");
        clientDTO.setClientType(ClientType.INTERNAL);
        clientDTO.setOrganizationId("Org123");

        clientList = new ArrayList<>();
        clientList.add(clientDTO);

        when(clientService.getClients()).thenReturn(clientList);

    }



    @Test
    public void testAddClient_validClient() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(clientService.addClient(validClientRequest)).thenReturn(validClient);

        ResponseEntity<Client> response = clientController.addClient(validClientRequest, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validClient, response.getBody());
        verify(clientService, times(1)).addClient(validClientRequest);
    }

    @Test
    public void testAddClient_invalidClient() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("client", "name", "Name is required")));
        assertThrows(MethodArgumentNotValidException.class, () -> {
            clientController.addClient(validClientRequest, bindingResult);
        });
    }

    @Test
    public void testUpdateClientPartially_validClient() {

        when(clientService.updateClientPartially("C123", updateFields)).thenReturn(validClient);

        ResponseEntity<Client> response = clientController.updateClientPartially("C123", updateFields);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validClient, response.getBody());
        verify(clientService, times(1)).updateClientPartially("C123", updateFields);
    }

    @Test
    public void testUpdateClientPartially_invalidClientId() {
        when(clientService.updateClientPartially("C999", updateFields)).thenReturn(null);

        ResponseEntity<Client> response = clientController.updateClientPartially("C999", updateFields);

        assertNull(response.getBody());
        verify(clientService, times(1)).updateClientPartially("C999", updateFields);
    }

    @Test
    public void testGetClient_validClient() {

        when(clientService.getClientById(validClientId)).thenReturn(validClient);

        ResponseEntity<Client> response = clientController.getClient(validClientId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validClient, response.getBody());
        verify(clientService, times(1)).getClientById(validClientId);
    }

    @Test
    public void testGetClient_invalidClientId() {

        when(clientService.getClientById(invalidClientId)).thenReturn(null);

        ResponseEntity<Client> response = clientController.getClient(invalidClientId);

        assertNull(response.getBody());
        verify(clientService, times(1)).getClientById(invalidClientId);
    }

    @Test
    public void testGetClients_ReturnsClientList() {
        when(clientService.getClients()).thenReturn(clientList);
        ResponseEntity<List<ClientDTO>> response = clientController.getClients();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals("C123", response.getBody().get(0).getClientId());
        assertEquals("John Doe", response.getBody().get(0).getClientName());
        assertEquals("INTERNAL", response.getBody().get(0).getClientType().toString());
        assertEquals("Org123", response.getBody().get(0).getOrganizationId());

        verify(clientService, times(1)).getClients();
    }


}







