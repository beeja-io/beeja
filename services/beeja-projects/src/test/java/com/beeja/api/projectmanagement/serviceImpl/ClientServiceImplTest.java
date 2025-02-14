package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.ProjectManagementApplication;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = ProjectManagementApplication.class)
@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {
    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private Client validClient;
    private Client existingClient;
    private Map<String, Object> mockUserContext;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        validClient = new Client();
        validClient.setId("1234567890abcdef");
        validClient.setClientName("John Doe");
        validClient.setEmail("john.doe@example.com");
        validClient.setClientId("C123");
        validClient.setPrimaryAddress(new Address("123 Main St", "Springfield", "IL", "62701", "USA"));
        validClient.setUsePrimaryAsBillingAddress(true);
        existingClient = new Client();
        existingClient.setEmail("john.doe@example.com");

        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "Org123");

    }
    @Test
    public void testAddClient_success() {

        when(clientRepository.findByEmail(validClient.getEmail())).thenReturn(null);
        when(clientRepository.save(any(Client.class))).thenReturn(validClient);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");

            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            Client result = clientService.addClient(validClient);

            assertNotNull(result);
            assertEquals(validClient.getClientName(), result.getClientName());
            assertEquals(validClient.getEmail(), result.getEmail());
            verify(clientRepository).save(any(Client.class));
        }
    }
    @Test
    public void testAddClient_alreadyExists_throwsException() {
        when(clientRepository.findByEmail(validClient.getEmail())).thenReturn(validClient);

        ResourceAlreadyFoundException thrown = assertThrows(
                ResourceAlreadyFoundException.class,
                () -> clientService.addClient(validClient)
        );


        verify(clientRepository, never()).save(any(Client.class)); // Ensure save() is never called
    }

    @Test
    public void testUpdateClientPartially_Success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("clientName", "Jane Doe");
        updates.put("email", "jane.doe@example.com");
        updates.put("industry", "Finance");
        updates.put("description", "Updated description");
        when(clientRepository.findById("C123")).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        Client updatedClient = clientService.updateClientPartially("C123", updates);
        assertNotNull(updatedClient);
        assertEquals("Jane Doe", updatedClient.getClientName());
        assertEquals("jane.doe@example.com", updatedClient.getEmail());
        assertEquals("Finance", updatedClient.getIndustry());
        assertEquals("Updated description", updatedClient.getDescription());

        verify(clientRepository, times(1)).save(existingClient);
    }

    @Test
    public void testUpdateClientPartially_ClientNotFound() {

        when(clientRepository.findById("C999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clientService.updateClientPartially("C999", new HashMap<>());
        });

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testUpdateClientPartially_InvalidField() {

        Map<String, Object> updates = new HashMap<>();
        updates.put("invalidField", "someValue");

        when(clientRepository.findById("C123")).thenReturn(Optional.of(existingClient));

        assertThrows(ValidationException.class, () -> {
            clientService.updateClientPartially("C123", updates);
        });

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    public void testGetSortedClients_Success() {
        // Given
        String organizationId = "Org123";
        Map<String, Object> mockUserContext = new HashMap<>();
        mockUserContext.put("id", organizationId);

        Client client1 = new Client();
        client1.setClientId("C1");
        client1.setClientName("Client A");
        client1.setClientType(ClientType.INTERNAL);
        client1.setOrganizationId(organizationId);
        client1.setCreatedAt(LocalDateTime.now().minusDays(1).atZone(ZoneOffset.UTC).toInstant());

        Client client2 = new Client();
        client2.setClientId("C2");
        client2.setClientName("Client B");
        client2.setClientType(ClientType.INDIVIDUAL);
        client2.setOrganizationId(organizationId);
        client2.setCreatedAt(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());

        List<Client> clientList = Arrays.asList(client2, client1); // Ensure sorted order (latest first)

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId)).thenReturn(clientList);

            // When
            List<ClientDTO> result = clientService.getSortedClients();

            // Then
            verify(clientRepository, times(1)).findAllByOrganizationIdOrderByCreatedAtDesc(organizationId);

            // Ensure correct size
            assertEquals(2, result.size());

            // Validate first client (latest created)
            assertEquals("C2", result.get(0).getClientId());
            assertEquals("Client B", result.get(0).getClientName());
            assertEquals(ClientType.INDIVIDUAL, result.get(0).getClientType());
            assertEquals(organizationId, result.get(0).getOrganizationId());

            // Validate second client
            assertEquals("C1", result.get(1).getClientId());
            assertEquals("Client A", result.get(1).getClientName());
            assertEquals(ClientType.INTERNAL, result.get(1).getClientType());
            assertEquals(organizationId, result.get(1).getOrganizationId());
        }
    }



    @Test
    public void testGetClientById_Success() {

        when(clientRepository.findById("1234567890abcdef")).thenReturn(Optional.of(validClient));

        Client result = clientService.getClientById("1234567890abcdef");

        assertNotNull(result);
        assertEquals("1234567890abcdef", result.getId());
        assertEquals("John Doe", result.getClientName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(clientRepository, times(1)).findById("1234567890abcdef");
    }

}
