package com.beeja.api.projectmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.serviceImpl.ClientServiceImpl;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class ClientServiceTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    private static MockedStatic<UserContext> userContextMock;

    @BeforeAll
    static void initStaticMock() {
        userContextMock = org.mockito.Mockito.mockStatic(UserContext.class);
        Map<String, Object> orgMap = new HashMap<>();
        orgMap.put("id", "org123");
        orgMap.put("name", "TestOrg");
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
        when(clientRepository.countByOrganizationId(anyString())).thenReturn(0L);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.addClientToOrganization(request);

        assertNotNull(result);
        assertEquals("Test Client", result.getClientName());
        assertEquals("org123", result.getOrganizationId());
        assertTrue(result.getClientId().startsWith("TES")); // from organization name substring
    }

    @Test
    void testAddClientToOrganization_clientAlreadyExists() {
        ClientRequest request = new ClientRequest();
        request.setEmail("existing@example.com");

        Client existingClient = new Client();
        existingClient.setEmail("existing@example.com");

        when(clientRepository.findByEmailAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);

        assertThrows(ResourceAlreadyFoundException.class,
                () -> clientService.addClientToOrganization(request));
    }

    @Test
    void testUpdateClientOfOrganization_success() throws Exception {
        ClientRequest request = new ClientRequest();
        request.setClientName("Updated Name");

        Client existingClient = new Client();
        existingClient.setClientId("client123");

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.updateClientOfOrganization(request, "client123");

        assertNotNull(result);
        assertEquals("Updated Name", result.getClientName());
    }

    @Test
    void testUpdateClientOfOrganization_clientNotFound() {
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> clientService.updateClientOfOrganization(new ClientRequest(), "client123"));
    }

    @Test
    void testGetClientById_success() throws Exception {
        Client client = new Client();
        client.setClientId("client123");

        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(client);

        Client result = clientService.getClientById("client123");

        assertNotNull(result);
        assertEquals("client123", result.getClientId());
    }

    @Test
    void testGetClientById_clientNotFound() {
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClientById("client123"));
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

    @Test
    void testGetAllClientsOfOrganizationWithPagination() {
        Client client = new Client();
        Page<Client> page = new PageImpl<>(List.of(client));
        when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString(), any(Pageable.class))).thenReturn(page);

        Page<Client> result = clientService.getAllClientsOfOrganization("org123", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
