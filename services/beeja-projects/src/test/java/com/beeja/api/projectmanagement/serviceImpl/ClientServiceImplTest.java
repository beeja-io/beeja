package com.beeja.api.projectmanagement.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.client.FileClient;
import com.beeja.api.projectmanagement.config.LogoValidator;
import com.beeja.api.projectmanagement.exceptions.*;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.repository.ClientRepository;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.request.FileUploadRequest;
import com.beeja.api.projectmanagement.utils.UserContext;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

class ClientServiceImplTest {


  @InjectMocks
  private ClientServiceImpl clientService;

  @Mock private ClientRepository clientRepository;
  @Mock private LogoValidator logoValidator;
  @Mock private FileClient fileClient;

  private static MockedStatic<UserContext> userContextMock;

  @BeforeAll
  static void initStaticMock() {
    userContextMock = mockStatic(UserContext.class);
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
    assertEquals("test@example.com", result.getEmail());
    assertEquals("org123", result.getOrganizationId());
    assertTrue(result.getClientId().startsWith("TES"));
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
  void testAddClientToOrganization_saveThrowsException() {
    ClientRequest request = new ClientRequest();
    request.setEmail("test@example.com");
    request.setClientName("Test Client");

    when(clientRepository.findByEmailAndOrganizationId(anyString(), anyString())).thenReturn(null);
    when(clientRepository.countByOrganizationId(anyString())).thenReturn(1L);
    when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("DB error"));

    Exception exception = assertThrows(Exception.class, () -> clientService.addClientToOrganization(request));
    assertEquals("Error while saving new client to database", exception.getMessage());
  }

  @Test
  void testAddClientToOrganization_invalidLogoType_throwsValidationException() {
    ClientRequest request = new ClientRequest();
    request.setEmail("test@example.com");
    request.setClientName("ClientX");

    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getContentType()).thenReturn("application/zip");
    when(mockFile.isEmpty()).thenReturn(false);
    request.setLogo(mockFile);

    when(logoValidator.getAllowedTypes()).thenReturn(List.of("image/png", "image/jpeg"));

    assertThrows(ValidationException.class, () -> clientService.addClientToOrganization(request));
  }

  @Test
  void testAddClientToOrganization_logoUploadFails_throwsFeignClientException() {
    ClientRequest request = new ClientRequest();
    request.setEmail("test@example.com");
    request.setClientName("ClientY");

    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getContentType()).thenReturn("image/png");
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getName()).thenReturn("logo.png");
    request.setLogo(mockFile);

    when(logoValidator.getAllowedTypes()).thenReturn(List.of("image/png"));
    when(fileClient.uploadFile(any(FileUploadRequest.class))).thenThrow(new RuntimeException("Upload error"));

    assertThrows(FeignClientException.class, () -> clientService.addClientToOrganization(request));
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

    assertThrows(ResourceNotFoundException.class,
            () -> clientService.updateClientOfOrganization(new ClientRequest(), "client123"));
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
  void testUpdateClientOfOrganization_invalidLogoType_throwsValidationException() {
    Client existingClient = new Client();
    existingClient.setClientId("c1");

    ClientRequest request = new ClientRequest();
    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getContentType()).thenReturn("application/zip");
    when(mockFile.isEmpty()).thenReturn(false);
    request.setLogo(mockFile);

    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);
    when(logoValidator.getAllowedTypes()).thenReturn(List.of("image/png"));

    assertThrows(ValidationException.class, () -> clientService.updateClientOfOrganization(request, "c1"));
  }

  @Test
  void testUpdateClientOfOrganization_logoUploadFails_throwsFeignClientException() {
    Client existingClient = new Client();
    existingClient.setClientId("c1");

    ClientRequest request = new ClientRequest();
    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getContentType()).thenReturn("image/png");
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getName()).thenReturn("logo.png");
    request.setLogo(mockFile);

    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenReturn(existingClient);
    when(logoValidator.getAllowedTypes()).thenReturn(List.of("image/png"));
    when(fileClient.uploadFile(any(FileUploadRequest.class))).thenThrow(new RuntimeException("upload error"));

    assertThrows(FeignClientException.class, () -> clientService.updateClientOfOrganization(request, "c1"));
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

    assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById("client123"));
  }

  @Test
  void testGetClientById_repoThrowsException() {
    when(clientRepository.findByClientIdAndOrganizationId(anyString(), anyString())).thenThrow(new RuntimeException("DB error"));

    Exception exception = assertThrows(Exception.class, () -> clientService.getClientById("client123"));
    assertEquals("Error while fetching client from database", exception.getMessage());
  }

  // ==================== GET ALL CLIENTS ====================
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
  void testGetAllClientsOfOrganization_withPagination() {
    List<Client> clients = List.of(new Client(), new Client());
    Page<Client> mockPage = new PageImpl<>(clients);

    when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString(), any(Pageable.class)))
            .thenReturn(mockPage);

    Page<Client> result = clientService.getAllClientsOfOrganization("org123", 0, 10);

    assertEquals(2, result.getContent().size());
  }

  @Test
  void testGetAllClientsOfOrganization_withInvalidPageSize() {
    Page<Client> mockPage = new PageImpl<>(List.of(new Client()));

    when(clientRepository.findAllByOrganizationIdOrderByCreatedAtDesc(anyString(), any(Pageable.class)))
            .thenReturn(mockPage);

    Page<Client> result = clientService.getAllClientsOfOrganization("org123", 0, 0);
    assertEquals(1, result.getContent().size());

    result = clientService.getAllClientsOfOrganization("org123", 0, 200);
    assertEquals(1, result.getContent().size());
  }
}
