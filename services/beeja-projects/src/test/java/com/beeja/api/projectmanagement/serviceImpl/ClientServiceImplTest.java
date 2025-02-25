package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.ProjectManagementApplication;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.exceptions.DatabaseException;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
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
import org.springframework.dao.DataAccessException;
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

    private ClientRequest validClient;
    private Client existingClient;
    private Map<String, Object> mockUserContext;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        validClient = new ClientRequest();
//        validClient.setId("1234567890abcdef");
        validClient.setClientName("John Doe");
        validClient.setEmail("john.doe@example.com");
//        validClient.setClientId("C123");
        validClient.setPrimaryAddress(new Address("123 Main St", "Springfield", "IL", "62701", "USA"));
        validClient.setUsePrimaryAsBillingAddress(true);
        existingClient = new Client();
        existingClient = new Client();
        existingClient.setId("1234567890abcdef");
        existingClient.setClientName("John Doe");
        existingClient.setEmail("john.doe@example.com");
        existingClient.setIndustry(Industry.ITSERVICES);

        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "Org123");

    }
    @Test
    public void testAddClient_success() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");

            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            lenient().when(clientRepository.findByEmailAndOrganizationId(validClient.getEmail(), "Org123"))
                    .thenReturn(null);

            when(clientRepository.save(any(Client.class))).thenReturn(validClient);
            Client result = clientService.addClient(validClient);
//
            assertEquals(validClient.getClientName(), result.getClientName());
            assertEquals(validClient.getEmail(), result.getEmail());
            verify(clientRepository).save(any(Client.class));
        }
    }

    @Test
    public void testAddClient_alreadyExists_throwsException() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            String organizationId = (String) mockUserContext.get("id");

            when(clientRepository.findByEmailAndOrganizationId(organizationId, validClient.getEmail()))
                    .thenReturn(validClient);

            ResourceAlreadyFoundException thrown = assertThrows(
                    ResourceAlreadyFoundException.class,
                    () -> clientService.addClient(validClient)
            );

            verify(clientRepository, never()).save(any(Client.class));
        }
    }


    @Test
    public void testUpdateClientPartially_ClientNotFound() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {

            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findByIdAndOrganizationId("C999", "Org123")).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                clientService.updateClientPartially("C999", new HashMap<>());
            });

            verify(clientRepository, never()).save(any(Client.class));
        }
    }



    @Test
    public void testUpdateClientPartially_Success() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            Map<String, Object> updates = new HashMap<>();
            updates.put("clientName", "Jane Doe");
            updates.put("email", "jane.doe@example.com");
            updates.put("industry", Industry.ITSERVICES);
            updates.put("description", "Updated description");

            lenient().when(clientRepository.findByIdAndOrganizationId("1234567890abcdef", "Org123"))
                    .thenReturn((existingClient));
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

            Client updatedClient = clientService.updateClientPartially("1234567890abcdef", updates);

            assertEquals("Jane Doe", updatedClient.getClientName());
            assertEquals("jane.doe@example.com", updatedClient.getEmail());
            assertEquals("Updated description", updatedClient.getDescription());
            verify(clientRepository, times(1)).save(existingClient);
        }
    }

    @Test
    public void testUpdateClientPartially_InvalidField() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findByIdAndOrganizationId(eq("1234567890abcdef"), anyString()))
                    .thenReturn((existingClient));
            Map<String, Object> updates = new HashMap<>();
            updates.put("invalidField", "someValue");
            ValidationException exception = assertThrows(ValidationException.class, () -> {
                clientService.updateClientPartially("1234567890abcdef", updates);
            });

            verify(clientRepository, never()).save(any(Client.class));
        }
    }
    @Test
    public void testUpdateClientPartially_InvalidEnumValue() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            Map<String, Object> updates = new HashMap<>();
            updates.put("clientType", "INVALID_TYPE");

            when(clientRepository.findByIdAndOrganizationId(eq("1234567890abcdef"), anyString()))
                    .thenReturn((existingClient));

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                clientService.updateClientPartially("1234567890abcdef", updates);
            });
//            assertTrue(exception.getMessage().contains("Invalid value 'INVALID_TYPE' for field 'clientType'"));
            verify(clientRepository, never()).save(any(Client.class));
        }
    }


    @Test
    public void testUpdateClientPartially_DatabaseSaveFailure() {
        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            userContextMock.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            Map<String, Object> updates = new HashMap<>();
            updates.put("clientName", "Updated Name");

            when(clientRepository.findByIdAndOrganizationId(eq("1234567890abcdef"), anyString()))
                    .thenReturn((existingClient));

            when(clientRepository.save(any(Client.class)))
                    .thenThrow(new DataAccessException("Database error") {
                    });

            DatabaseException exception = assertThrows(DatabaseException.class, () -> {
                clientService.updateClientPartially("1234567890abcdef", updates);
            });


            verify(clientRepository, times(1)).save(existingClient);
        }
    }

    @Test
    public void testGetSortedClients_Success() {

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

            List<ClientDTO> result = clientService.getClients();

            verify(clientRepository, times(1)).findAllByOrganizationIdOrderByCreatedAtDesc(organizationId);

            assertEquals(2, result.size());

            assertEquals("C2", result.get(0).getClientId());
            assertEquals("Client B", result.get(0).getClientName());
            assertEquals(ClientType.INDIVIDUAL, result.get(0).getClientType());
            assertEquals(organizationId, result.get(0).getOrganizationId());

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
