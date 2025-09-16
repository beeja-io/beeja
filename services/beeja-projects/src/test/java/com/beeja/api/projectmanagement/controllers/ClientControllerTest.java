package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import com.beeja.api.projectmanagement.utils.Constants;
import com.beeja.api.projectmanagement.utils.UserContext;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {

  @Mock
  private ClientService clientService;

  private ClientController clientController;

  private static MockedStatic<UserContext> mockedUserContext;

  @BeforeAll
  static void initStaticMock() {
    mockedUserContext = Mockito.mockStatic(UserContext.class);
  }

  @AfterAll
  static void closeStaticMock() {
    mockedUserContext.close();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    clientController = new ClientController();
    clientController.clientService = clientService;

    // Mock UserContext to always return an organization map
    Map<String, Object> orgMap = new HashMap<>();
    orgMap.put(Constants.ID, "org123");
    mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(orgMap);
  }

  @AfterEach
  void tearDown() {
    mockedUserContext.reset(); // reset for next test
  }

  @Test
  void addClientToOrganization_Success() throws Exception {
    ClientRequest request = new ClientRequest();
    Client client = new Client();
    client.setClientId("1");
    client.setClientName("Client A");

    when(clientService.addClientToOrganization(request)).thenReturn(client);

    ResponseEntity<Client> response = clientController.addClientToOrganization(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(client, response.getBody());
    verify(clientService, times(1)).addClientToOrganization(request);
  }

  @Test
  void updateClientOfOrganization_Success() throws Exception {
    ClientRequest request = new ClientRequest();
    Client client = new Client();
    client.setClientId("1");
    client.setClientName("Updated Client");

    when(clientService.updateClientOfOrganization(request, "1")).thenReturn(client);

    ResponseEntity<Client> response = clientController.updateClientOfOrganization(request, "1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(client, response.getBody());
    verify(clientService, times(1)).updateClientOfOrganization(request, "1");
  }

  @Test
  void getClientById_Success() throws Exception {
    Client client = new Client();
    client.setClientId("1");
    client.setClientName("Client A");

    when(clientService.getClientById("1")).thenReturn(client);

    ResponseEntity<Client> response = clientController.getClientById("1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(client, response.getBody());
    verify(clientService, times(1)).getClientById("1");
  }

  @Test
  void getAllClientsOfOrganization_ClientsExist_ReturnsListOfClients() {
    List<Client> clients = new ArrayList<>();
    Client client1 = new Client();
    client1.setClientId("1");
    client1.setClientName("Client A");
    Client client2 = new Client();
    client2.setClientId("2");
    client2.setClientName("Client B");
    clients.add(client1);
    clients.add(client2);


    int pageNumber = 1;
    int pageSize = 10;
    Page<Client> pageClients = new PageImpl<>(clients, PageRequest.of(pageNumber - 1, pageSize), 25L);

    when(clientService.getAllClientsOfOrganization("org123", pageNumber - 1, pageSize))
            .thenReturn(pageClients);

    ResponseEntity<Map<String, Object>> response = clientController.getAllClientsOfOrganization(pageNumber, pageSize);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    Map<String, Object> responseBody = response.getBody();
    List<Client> clientList = (List<Client>) responseBody.get("data");
    Map<String, Object> metadata = (Map<String, Object>) responseBody.get("metadata");
    Long totalRecords = (Long) metadata.get("totalRecords");

    assertEquals(2, clientList.size());
    assertEquals("Client A", clientList.get(0).getClientName());
    assertEquals("Client B", clientList.get(1).getClientName());
    assertEquals(25L, totalRecords);

    verify(clientService, times(1)).getAllClientsOfOrganization("org123", pageNumber - 1, pageSize);
  }

  @Test
  void getAllClientsOfOrganization_NoClientsExist_ReturnsEmptyList() {
    Page<Client> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
    when(clientService.getAllClientsOfOrganization("org123", 0, 10)).thenReturn(emptyPage);

    ResponseEntity<Map<String, Object>> response = clientController.getAllClientsOfOrganization(1, 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    List<Client> clients = (List<Client>) response.getBody().get("data");
    Map<String, Object> metadata = (Map<String, Object>) response.getBody().get("metadata");

    assertTrue(clients.isEmpty());
    assertEquals(0L, metadata.get("totalRecords"));
    verify(clientService, times(1)).getAllClientsOfOrganization("org123", 0, 10);
  }

  @Test
  void getAllClientsOfOrganization_ServiceThrowsException_ReturnsInternalServerError() {
    when(clientService.getAllClientsOfOrganization("org123", 0, 10))
            .thenThrow(new RuntimeException("Failed to retrieve clients"));

    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> clientController.getAllClientsOfOrganization(1, 10));

    assertEquals("Failed to retrieve clients", exception.getMessage());
    verify(clientService, times(1)).getAllClientsOfOrganization("org123", 0, 10);
  }

  @Test
  void getAllClientsOfOrganization_InvalidPageParams_ReturnsBadRequest() {
    ResponseEntity<Map<String, Object>> response = clientController.getAllClientsOfOrganization(0, -5);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Page number and page size must be positive integers.",
            response.getBody().get("error"));
    verifyNoInteractions(clientService);
  }
}
