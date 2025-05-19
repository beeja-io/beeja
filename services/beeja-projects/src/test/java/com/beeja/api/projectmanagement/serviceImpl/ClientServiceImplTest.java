package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void initStaticMock() {
        userContextMock = mockStatic(UserContext.class);
        Map<String, Object> orgMap = new HashMap<>();
        orgMap.put("id", "org123");
        userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
    }

    @AfterAll
    static void closeStaticMock() {
        userContextMock.close();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddClientToOrganization_success() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setEmail("test@example.com");
        request.setClientName("Test Client");

        when(clientRepository.findByEmailAndOrganizationId(anyString(), anyString())).thenReturn(null);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.addClientToOrganization(request);

        assertNotNull(result);
        assertEquals("Test Client", result.getClientName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("org123", result.getOrganizationId());
    }

    @Test
    void testAddClientToOrganization_clientAlreadyExists() {
        ClientRequest request = new ClientRequest();
        request.setEmail("existing@example.com");

        Client existingClient = new Client();
        existingClient.setEmail("existing@example.com");
        when(clientRepository.findByEmailAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);

        ResourceAlreadyFoundException exception = assertThrows(ResourceAlreadyFoundException.class,
                () -> clientService.addClientToOrganization(request));

        assertTrue(exception.getMessage().toLowerCase().contains("client found with provided email"));
    }

    @Test
    void testAddClientToOrganization_saveThrowsException() {
        ClientRequest request = new ClientRequest();
        request.setEmail("test@example.com");
        request.setClientName("Test Client");

        when(clientRepository.findByEmailAndOrganizationId(anyString(), anyString())).thenReturn(null);
        when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(Exception.class,
                () -> clientService.addClientToOrganization(request));

        assertEquals("Error while saving new client to database", exception.getMessage());
    }

    @Test
    void testUpdateClientOfOrganization_success() throws Exception {
        String clientId = "client123";
        ClientRequest request = new ClientRequest();
        request.setClientName("Updated Client");

        Client existingClient = new Client();
        existingClient.setClientId(clientId);

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.updateClientOfOrganization(request, clientId);

        assertNotNull(result);
        assertEquals("Updated Client", result.getClientName());
    }

    @Test
    void testUpdateClientOfOrganization_clientNotFound() {
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clientService.updateClientOfOrganization(new ClientRequest(), "client123"));

        assertTrue(exception.getMessage().toLowerCase().contains("client not found"));
    }

    @Test
    void testUpdateClientOfOrganization_saveThrowsException() {
        String clientId = "client123";
        ClientRequest request = new ClientRequest();
        request.setClientName("Updated Client");

        Client existingClient = new Client();
        existingClient.setClientId(clientId);

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);
        when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(Exception.class,
                () -> clientService.updateClientOfOrganization(request, clientId));

        assertEquals("Error while updating client in database", exception.getMessage());
    }

    @Test
    void testGetClientById_success() throws Exception {
        String clientId = "client123";
        Client client = new Client();
        client.setClientId(clientId);

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);

        Client result = clientService.getClientById(clientId);

        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
    }

    @Test
    void testGetClientById_clientNotFound() {
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClientById("client123"));

        assertTrue(exception.getMessage().toLowerCase().contains("client not found"));
    }

    @Test
    void testGetClientById_repoThrowsException() {
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(Exception.class,
                () -> clientService.getClientById("client123"));

        assertEquals("Error while fetching client from database", exception.getMessage());
    }

    @Test
    void testGetAllClientsOfOrganization_returnsClients() {
        List<Client> clients = List.of(new Client(), new Client());

        when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString())).thenReturn(clients);

        List<Client> result = clientService.getAllClientsOfOrganization();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllClientsOfOrganization_returnsEmptyList() {
        when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString())).thenReturn(Collections.emptyList());

        List<Client> result = clientService.getAllClientsOfOrganization();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
