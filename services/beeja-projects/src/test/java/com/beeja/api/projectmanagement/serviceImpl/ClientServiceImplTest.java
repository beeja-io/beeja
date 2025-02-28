package com.beeja.api.projectmanagement.serviceImpl;

import com.beeja.api.projectmanagement.ProjectManagementApplication;
import com.beeja.api.projectmanagement.enums.ClientType;
import com.beeja.api.projectmanagement.enums.Industry;
import com.beeja.api.projectmanagement.enums.TaxCategory;
import com.beeja.api.projectmanagement.exceptions.DatabaseException;
import com.beeja.api.projectmanagement.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.exceptions.ValidationException;
import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.TaxDetails;
import com.beeja.api.projectmanagement.model.dto.ClientDTO;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private ClientRequest validClient;
    private Client existingClient;
    private Map<String, Object> mockUserContext;


    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        validClient = new ClientRequest();
        validClient.setEmail("test@example.com");
        validClient.setClientName("John Doe");
        validClient.setClientType(ClientType.valueOf("INTERNAL"));
        validClient.setContact("1234567890");
        validClient.setUsePrimaryAsBillingAddress(true);
        validClient.setIndustry(Industry.valueOf("ECOMMERCE"));
        validClient.setDescription("Test Client");
        validClient.setLogo("logo.png");

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

        existingClient = new Client();
        existingClient.setId("client123");
        existingClient.setClientName("Old Name");
        existingClient.setClientType(ClientType.INDIVIDUAL);
        existingClient.setEmail("old@example.com");
        existingClient.setContact("1234567890");

        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "org123");

    }

    /**
     * ✅ Test case: Successfully adding a new client.
     */
    @Test
    public void testAddClient_Success() {
        String clientId = "123";
        String clientName = "John Doe";

        Client client = new Client();
        client.setId(clientId);
        client.setClientName(clientName);
        client.setOrganizationId("Org123");

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", "Org123");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            when(clientRepository.findByEmailAndOrganizationId(validClient.getEmail(), "Org123"))
                    .thenReturn(null);

            when(clientRepository.save(any(Client.class))).thenReturn(client);
            Client result = clientService.addClient(validClient);
            assertNotNull(result);
            assertEquals(clientId, result.getId());
            assertEquals(clientName, result.getClientName());
            assertEquals("Org123", result.getOrganizationId());

            verify(clientRepository, times(1)).save(any(Client.class));
        }
    }

    @Test
    public void testAddClient_ClientAlreadyExists() {
        String organizationId = "Org123";
        Client existingClient = new Client();
        existingClient.setEmail(validClient.getEmail());

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findByEmailAndOrganizationId(validClient.getEmail(), organizationId))
                    .thenReturn(existingClient);
            assertThrows(ResourceAlreadyFoundException.class, () -> clientService.addClient(validClient));
            verify(clientRepository, never()).save(any(Client.class));
        }
    }

    @Test
    public void testAddClient_PrimaryAddressAsBilling() {
        validClient.setUsePrimaryAsBillingAddress(true);
        String organizationId = "Org123";

        Client savedClient = new Client();
        savedClient.setClientName(validClient.getClientName());
        savedClient.setPrimaryAddress(validClient.getPrimaryAddress());
        savedClient.setBillingAddress(validClient.getPrimaryAddress());
        savedClient.setOrganizationId(organizationId);

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findByEmailAndOrganizationId(validClient.getEmail(), organizationId))
                    .thenReturn(null);

            when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
            Client result = clientService.addClient(validClient);
            assertNotNull(result);
            assertEquals(validClient.getPrimaryAddress(), result.getBillingAddress());
            verify(clientRepository, times(1)).save(any(Client.class));
        }
    }

    @Test
    public void testAddClient_MissingUserContext() {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(null);
            assertThrows(NullPointerException.class, () -> clientService.addClient(validClient));

            verify(clientRepository, never()).save(any(Client.class));
        }
    }

    @Test
    public void testAddClient_DatabaseException() {
        String organizationId = "Org123";

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockUserContext = new HashMap<>();
            mockUserContext.put("id", organizationId);
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);

            when(clientRepository.findByEmailAndOrganizationId(validClient.getEmail(), organizationId))
                    .thenReturn(null);
            when(clientRepository.save(any(Client.class))).thenThrow(new MongoException("DB Error"));
            assertThrows(DatabaseException.class, () -> clientService.addClient(validClient));
            verify(clientRepository, times(1)).save(any(Client.class));
        }
    }

    @Test
    void testUpdateClientPartially_Success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("clientName", "New Name");

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

            Client updatedClient = clientService.updateClientPartially("client123", updates);
            assertNotNull(updatedClient);
            assertEquals("New Name", updatedClient.getClientName());
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
    void testUpdateClientPartially_UpdateClientType() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("clientType", "INDIVIDUAL");

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

            Client updatedClient = clientService.updateClientPartially("client123", updates);
            assertEquals(ClientType.INDIVIDUAL, updatedClient.getClientType());
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
            verify(clientRepository, never()).save(any(Client.class));
        }
    }


    @Test
    void testUpdateClientPartially_DatabaseException() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("clientName", "New Name");

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenThrow(new MongoException("DB error"));

            assertThrows(DatabaseException.class, () ->
                    clientService.updateClientPartially("client123", updates)
            );
        }
    }

    @Test
    void testUpdateClientPartially_UpdateNullField() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("clientName", null);

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);
            Client updatedClient = clientService.updateClientPartially("client123", updates);
        }
    }

    @Test
    void testUpdateClientPartially_UpdateTaxDetails() {
        Map<String, Object> updates = new HashMap<>();
        Map<String, Object> taxDetailsUpdate = new HashMap<>();
        taxDetailsUpdate.put("taxCategory", "VAT");
        taxDetailsUpdate.put("taxNumber", "VAT12345");
        updates.put("taxDetails", taxDetailsUpdate);

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

            Client updatedClient = clientService.updateClientPartially("client123", updates);
            assertNotNull(updatedClient.getTaxDetails());
            assertEquals("VAT", updatedClient.getTaxDetails().getTaxCategory().name());
        }
    }

    @Test
    void testUpdateClientPartially_UpdateTaxDetailsPartially() {
        Map<String, Object> taxDetailsUpdate = new HashMap<>();
        taxDetailsUpdate.put("taxCategory", "VAT");
        Map<String, Object> updates = new HashMap<>();
        updates.put("taxDetails", taxDetailsUpdate);

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);
            Client updatedClient = clientService.updateClientPartially("client123", updates);

              assertNotNull(updatedClient);
        }
    }

    @Test
    void testUpdateClientPartially_UpdateAddressPartially() {
        Map<String, Object> addressUpdate = new HashMap<>();
        addressUpdate.put("street", "456 New St");
        Map<String, Object> updates = new HashMap<>();
        updates.put("primaryAddress", addressUpdate);

        try (var mockedStatic = mockStatic(UserContext.class)) {
            mockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "org123"));
            when(clientRepository.findByIdAndOrganizationId("client123", "org123")).thenReturn(existingClient);
            when(clientRepository.save(any(Client.class))).thenReturn(existingClient);
            Client updatedClient = clientService.updateClientPartially("client123", updates);

            assertNotNull(updatedClient);
            assertEquals("456 New St", updatedClient.getPrimaryAddress().getStreet());
        }
    }

    @Test
    void testGetClients_Success() {
        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "org123");

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            ClientDTO mockClientDTO = new ClientDTO(
                    existingClient.getId(),
                    existingClient.getClientName(),
                    existingClient.getClientType(),
                    "org123"
            );
            List<ClientDTO> mockClients = Collections.singletonList(mockClientDTO);
            when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString()))
                    .thenReturn(mockClients);
            List<ClientDTO> clients = clientService.getClients();
            assertNotNull(clients);
            assertEquals(1, clients.size());
            assertEquals(existingClient.getClientName(), clients.get(0).getClientName());
        }
    }

    @Test
    void testGetClients_EmptyList() {
        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "org123");

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString()))
                    .thenReturn(Collections.emptyList());
            List<ClientDTO> clients = clientService.getClients();
            assertNotNull(clients);
            assertTrue(clients.isEmpty());
        }
    }


    @Test
    void testGetClients_ExceptionHandling() {
        mockUserContext = new HashMap<>();
        mockUserContext.put("id", "org123");

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString()))
                    .thenThrow(new RuntimeException("Database error"));
            assertThrows(RuntimeException.class, () -> clientService.getClients());
        }
    }

    @Test
    void testGenerateClientId_Success() {
        when(clientRepository.countByOrganizationId("org123")).thenReturn(5L);
        when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(null);
        String clientId = clientService.generateClientId("JohnDoe", "org123");
        assertNotNull(clientId);
        assertEquals("JD006", clientId);
    }

    @Test
    void shouldReturnClientWhenFound() {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(mockUserContext);
            when(clientRepository.findByIdAndOrganizationId("client123", "org123"))
                    .thenReturn(existingClient);
            Client result = clientService.getClientById("client123");
            assertNotNull(result);
            assertEquals("client123", result.getId());
            assertEquals("Old Name", result.getClientName());
            verify(clientRepository, times(1)).findByIdAndOrganizationId("client123", "org123");
        }
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUserContextIsNull() {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(null);
            assertThrows(NullPointerException.class, () -> {
                clientService.getClientById("client123");
            });

            verifyNoInteractions(clientRepository);
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenOrganizationIdMismatch() {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(Map.of("id", "wrongOrgId"));
            when(clientRepository.findByIdAndOrganizationId("client123", "wrongOrgId")).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> {
                clientService.getClientById("client123");
            });
        }
    }


    @Test
    void testConvertValueSafely_ValidInput_ShouldReturnConvertedObject() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InvocationTargetException {
        Map<String, Object> validInput = new HashMap<>();
        validInput.put("clientId", "C123");
        validInput.put("clientName", "John Doe");
        validInput.put("clientType", "INTERNAL");
        validInput.put("organizationId", "Org123");


        Method method = clientService.getClass().getDeclaredMethod("convertValueSafely", Object.class, Class.class, String.class);
        method.setAccessible(true);
        ClientDTO result = (ClientDTO) method.invoke(clientService, validInput, ClientDTO.class, "client");
        assertNotNull(result);
        assertEquals("C123", result.getClientId());
        assertEquals("John Doe", result.getClientName());
        assertEquals("Org123", result.getOrganizationId());
    }

    @Test
    void testConvertValueSafely_InvalidInput_NotAMap_ShouldThrowValidationException() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String invalidInput = "Invalid String";

        Method method = clientService.getClass().getDeclaredMethod("convertValueSafely", Object.class, Class.class, String.class);
        method.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                method.invoke(clientService, invalidInput, ClientDTO.class, "client")
        );
        assertTrue(exception.getCause() instanceof ValidationException);
        ValidationException validationException = (ValidationException) exception.getCause();
        assertFalse(validationException.getMessage().contains("INVALID_JSON_STRUCTURE"));
    }




}





