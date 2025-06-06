package com.beeja.api.financemanagementservice.controllers;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import com.beeja.api.financemanagementservice.response.InventoryResponseDTO;
import com.beeja.api.financemanagementservice.service.InventoryService;
import com.beeja.api.financemanagementservice.serviceImpl.LoanServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class InventoryControllerTest {

  @InjectMocks InventoryController inventoryController;

  @Autowired MockMvc mockMvc;

  @Mock private BindingResult bindingResult;

  @Mock private InventoryService inventoryService;

  @Mock private LoanServiceImpl loanServiceImpl;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  @Test
  public void testAddDevice_Success() throws Exception {
    DeviceDetails deviceDetails = new DeviceDetails();
    Inventory inventory = new Inventory();
    when(bindingResult.hasErrors()).thenReturn(false);
    when(inventoryService.addDevice(any(DeviceDetails.class))).thenReturn(inventory);
    ResponseEntity<Inventory> response =
        inventoryController.addDevice(deviceDetails, bindingResult);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(inventory, response.getBody());
    verify(inventoryService, times(1)).addDevice(deviceDetails); // Verify service call
  }

  @Test
  public void testAddDeviceWith_BadRequestException() throws Exception {
    DeviceDetails deviceDetails = new DeviceDetails();
    List<ObjectError> errors =
        Collections.singletonList(new ObjectError("field", "must not be null"));
    when(bindingResult.hasErrors()).thenReturn(true);
    when(bindingResult.getAllErrors()).thenReturn(errors);
    BadRequestException exception =
        assertThrows(
            BadRequestException.class,
            () -> {
              inventoryController.addDevice(deviceDetails, bindingResult);
            });
    assertTrue(exception.getMessage().contains("VALIDATION_ERROR"));
    assertTrue(exception.getMessage().contains("must not be null"));
    verify(inventoryService, never()).addDevice(any());
  }

  @Test
  void deleteExistingDeviceDetails_ValidDeviceId_ShouldReturnOkResponse() throws Exception {
    String deviceId = "TAC-0014";
    Inventory deletedInventory = new Inventory();
    when(inventoryService.deleteExistingDeviceDetails(deviceId))
        .thenReturn(new ResponseEntity<>(deletedInventory, HttpStatus.OK));
    ResponseEntity<Inventory> response = inventoryController.deleteExistingDeviceDetails(deviceId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(deletedInventory, response.getBody());
    verify(inventoryService, times(1)).deleteExistingDeviceDetails(deviceId); // Verify service call
  }

  @Test
  void deleteExistingDeviceDetails_InvalidDeviceId_ShouldThrowException() throws Exception {
    String invalidDeviceId = "TAC-0067";
    when(inventoryService.deleteExistingDeviceDetails(invalidDeviceId))
        .thenThrow(new RuntimeException("Device not found"));
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              inventoryController.deleteExistingDeviceDetails(invalidDeviceId);
            });
    assertEquals("Device not found", exception.getMessage());
    verify(inventoryService, times(1))
        .deleteExistingDeviceDetails(invalidDeviceId); // Verify service call
  }

  @Test
  public void testDeleteExistingDeviceDetails_Exception() throws Exception {
    String deviceId = "TAC-0014";
    when(inventoryService.deleteExistingDeviceDetails(deviceId))
        .thenThrow(new RuntimeException("Service error"));
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              inventoryController.deleteExistingDeviceDetails(deviceId);
            });
    assertEquals("Service error", exception.getMessage());
    verify(inventoryService, times(1)).deleteExistingDeviceDetails(deviceId); // Verify service call
  }

  @Test
  void testUpdateDeviceDetails_Success() throws Exception {
    String deviceId = "TAC-0004";
    DeviceDetails deviceDetails = new DeviceDetails();
    Inventory updatedInventory = new Inventory();
    when(inventoryService.updateDeviceDetails(deviceDetails, deviceId))
        .thenReturn(updatedInventory);
    ResponseEntity<Inventory> response =
        inventoryController.updateDeviceDetails(deviceDetails, deviceId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedInventory, response.getBody());
    verify(inventoryService, times(1))
        .updateDeviceDetails(deviceDetails, deviceId); // Verify service call
  }

  @Test
  public void testFilterInventory_Success() throws Exception {
    Inventory inventory = new Inventory();
    inventory.setDevice(Device.MOBILE);
    inventory.setProvider("Google");
    inventory.setAvailability(Availability.NO);
    inventory.setOs("NA");

    List<Inventory> filteredDevices = Collections.singletonList(inventory);
    when(inventoryService.filterInventory(
            anyInt(),
            anyInt(),
            any(Device.class),
            anyString(),
            any(Availability.class),
            anyString(),
            anyString(),
            anyString()))
        .thenReturn(filteredDevices);
    when(inventoryService.getTotalInventorySize(
            any(), anyString(), any(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(1L);
    ResponseEntity<InventoryResponseDTO> responseEntity =
        inventoryController.filterInventory(
            1, 10, Device.MOBILE, "Google", Availability.NO, "NA", "NA", "Mobile");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());

    InventoryResponseDTO responseBody = responseEntity.getBody();
    assertNotNull(responseBody);
    assertEquals(1L, responseBody.getMetadata().get("totalSize"));
    assertEquals(1, responseBody.getInventory().size());
    Inventory returnedInventory = responseBody.getInventory().get(0);
    assertEquals(Device.MOBILE, returnedInventory.getDevice());
    assertEquals("Google", returnedInventory.getProvider());
    assertEquals(Availability.NO, returnedInventory.getAvailability());
    assertEquals("NA", returnedInventory.getOs());
  }
}
