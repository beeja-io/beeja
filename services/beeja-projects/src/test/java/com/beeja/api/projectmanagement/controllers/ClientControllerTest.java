package com.beeja.api.projectmanagement.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.request.ClientRequest;
import com.beeja.api.projectmanagement.service.ClientService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ClientControllerTest {

  @InjectMocks private ClientController clientController;

  @Mock private ClientService clientService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addClientToOrganization_ValidRequest_ReturnsCreatedClient() throws Exception {
    ClientRequest clientRequest = new ClientRequest();
    clientRequest.setClientName("Test Client");
    MultipartFile mockFile =
        new MockMultipartFile("logo", "logo.png", "image/png", "some image".getBytes());
    clientRequest.setLogo(mockFile);

    Client createdClient = new Client();
    createdClient.setClientId("123");
    createdClient.setClientName("Test Client");
    when(clientService.addClientToOrganization(clientRequest)).thenReturn(createdClient);

    ResponseEntity<Client> response = clientController.addClientToOrganization(clientRequest);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("123", response.getBody().getClientId());
    assertEquals("Test Client", response.getBody().getClientName());
    verify(clientService, times(1)).addClientToOrganization(clientRequest);
  }

  @Test
  void addClientToOrganization_ServiceThrowsException_ReturnsInternalServerError()
      throws Exception {
    ClientRequest clientRequest = new ClientRequest();
    MultipartFile mockFile =
        new MockMultipartFile("logo", "logo.png", "image/png", "some image".getBytes());
    clientRequest.setLogo(mockFile);
    when(clientService.addClientToOrganization(clientRequest))
        .thenThrow(new RuntimeException("Failed to add client"));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              clientController.addClientToOrganization(clientRequest);
            });

    assertEquals("Failed to add client", exception.getMessage());
    verify(clientService, times(1)).addClientToOrganization(clientRequest);
  }

  @Test
  void updateClientOfOrganization_ValidRequest_ReturnsUpdatedClient() throws Exception {
    String clientId = "456";
    ClientRequest clientRequest = new ClientRequest();
    clientRequest.setClientName("Updated Client");
    Client updatedClient = new Client();
    updatedClient.setClientId(clientId);
    updatedClient.setClientName("Updated Client");
    when(clientService.updateClientOfOrganization(clientRequest, clientId))
        .thenReturn(updatedClient);

    ResponseEntity<Client> response =
        clientController.updateClientOfOrganization(clientRequest, clientId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(clientId, response.getBody().getClientId());
    assertEquals("Updated Client", response.getBody().getClientName());
    verify(clientService, times(1)).updateClientOfOrganization(clientRequest, clientId);
  }

  @Test
  void updateClientOfOrganization_ServiceThrowsException_ReturnsInternalServerError()
      throws Exception {
    String clientId = "456";
    ClientRequest clientRequest = new ClientRequest();
    when(clientService.updateClientOfOrganization(clientRequest, clientId))
        .thenThrow(new RuntimeException("Failed to update client"));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              clientController.updateClientOfOrganization(clientRequest, clientId);
            });

    assertEquals("Failed to update client", exception.getMessage());
    verify(clientService, times(1)).updateClientOfOrganization(clientRequest, clientId);
  }

  @Test
  void getClientById_ExistingClientId_ReturnsClient() throws Exception {
    String clientId = "789";
    Client client = new Client();
    client.setClientId(clientId);
    client.setClientName("Existing Client");
    when(clientService.getClientById(clientId)).thenReturn(client);

    ResponseEntity<Client> response = clientController.getClientById(clientId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(clientId, response.getBody().getClientId());
    assertEquals("Existing Client", response.getBody().getClientName());
    verify(clientService, times(1)).getClientById(clientId);
  }

  @Test
  void getClientById_NonExistingClientId_ReturnsNotFound() throws Exception {
    String clientId = "nonExistent";
    when(clientService.getClientById(clientId))
        .thenThrow(new NoSuchElementException("Client not found"));

    NoSuchElementException exception =
        assertThrows(
            NoSuchElementException.class,
            () -> {
              clientController.getClientById(clientId);
            });

    assertEquals("Client not found", exception.getMessage());
    verify(clientService, times(1)).getClientById(clientId);
  }

  @Test
  void getClientById_ServiceThrowsGenericException_ReturnsInternalServerError() throws Exception {
    String clientId = "errorId";
    when(clientService.getClientById(clientId)).thenThrow(new RuntimeException("Database error"));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              clientController.getClientById(clientId);
            });

    assertEquals("Database error", exception.getMessage());
    verify(clientService, times(1)).getClientById(clientId);
  }

  @Test
  void getAllClientsOfOrganization_ClientsExist_ReturnsListOfClients() throws Exception {
    List<Client> clients = new ArrayList<>();

    Client client1 = new Client();
    client1.setClientId("1");
    client1.setClientName("Client A");

    Client client2 = new Client();
    client2.setClientId("2");
    client2.setClientName("Client B");

    clients.add(client1);
    clients.add(client2);

    when(clientService.getAllClientsOfOrganization()).thenReturn(clients);

    ResponseEntity<List<Client>> response = clientController.getAllClientsOfOrganization();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("Client A", response.getBody().get(0).getClientName());
    assertEquals("Client B", response.getBody().get(1).getClientName());
    verify(clientService, times(1)).getAllClientsOfOrganization();
  }

  @Test
  void getAllClientsOfOrganization_NoClientsExist_ReturnsEmptyList() throws Exception {
    List<Client> clients = Arrays.asList();
    when(clientService.getAllClientsOfOrganization()).thenReturn(clients);

    ResponseEntity<List<Client>> response = clientController.getAllClientsOfOrganization();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size());
    verify(clientService, times(1)).getAllClientsOfOrganization();
  }

  @Test
  void getAllClientsOfOrganization_ServiceThrowsException_ReturnsInternalServerError()
      throws Exception {
    when(clientService.getAllClientsOfOrganization())
        .thenThrow(new RuntimeException("Failed to retrieve clients"));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              clientController.getAllClientsOfOrganization();
            });

    assertEquals("Failed to retrieve clients", exception.getMessage());
    verify(clientService, times(1)).getAllClientsOfOrganization();
  }
}
