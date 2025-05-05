package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.exceptions.DuplicateDataException;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.repository.InventoryRepository;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

  @InjectMocks
  private InventoryServiceImpl inventoryService;

  @Mock
  private InventoryRepository inventoryRepository;

  @Mock
  private MongoTemplate mongoTemplate;

  private DeviceDetails deviceDetails;

  @BeforeEach
  void setUp() {
    deviceDetails = new DeviceDetails();
    deviceDetails.setDevice(Device.MOBILE);
    deviceDetails.setProvider("Google");
    deviceDetails.setAvailability(Availability.NO);
    deviceDetails.setOs("Android");
    deviceDetails.setProductId("P001");

    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
  }

  @Test
  void testAddDevice_Success() throws Exception {
    when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.empty());

    Inventory savedInventory = new Inventory();
    savedInventory.setProductId("P001");

    when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

    Inventory result = inventoryService.addDevice(deviceDetails);

    assertNotNull(result);
    assertEquals("P001", result.getProductId());
    verify(inventoryRepository).save(any(Inventory.class));
  }

  @Test
  void testAddDevice_DuplicateProductId() {
    when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.of(new Inventory()));

    assertThrows(DuplicateDataException.class, () -> inventoryService.addDevice(deviceDetails));
    verify(inventoryRepository, never()).save(any(Inventory.class));
  }

  @Test
  void testFilterInventory_Success() {
    Inventory inventory = new Inventory();
    inventory.setDevice(Device.MOBILE);
    inventory.setProvider("Google");
    inventory.setAvailability(Availability.NO);
    inventory.setOs("Android");

    List<Inventory> expectedList = List.of(inventory);

    when(mongoTemplate.find(any(Query.class), eq(Inventory.class))).thenReturn(expectedList);

    List<Inventory> result = inventoryService.filterInventory(1, 10, Device.MOBILE, "Google", Availability.NO, "Android", "Android");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedList.get(0), result.get(0));
  }

  @Test
  void testUpdateDeviceDetails_Success() throws Exception {
    String deviceId = "TAC-0001";
    String newProductId = "P002";

    DeviceDetails update = new DeviceDetails();
    update.setProductId(newProductId);

    Inventory existingInventory = new Inventory();
    existingInventory.setId(deviceId);
    existingInventory.setProductId("OLD");

    when(inventoryRepository.findByProductId(newProductId)).thenReturn(Optional.empty());
    when(inventoryRepository.findByIdAndOrganizationId(eq(deviceId), anyString())).thenReturn(Optional.of(existingInventory));
    when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

    Inventory result = inventoryService.updateDeviceDetails(update, deviceId);

    assertNotNull(result);
    assertEquals(newProductId, result.getProductId());
  }

  @Test
  void testUpdateDeviceDetails_DuplicateProductId() {
    DeviceDetails update = new DeviceDetails();
    update.setProductId("P001");

    when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.of(new Inventory()));

    assertThrows(DuplicateDataException.class, () -> inventoryService.updateDeviceDetails(update, "1"));
  }

  @Test
  void testDeleteExistingDeviceDetails_Success() throws Exception {
    Inventory inventory = new Inventory();
    inventory.setId("1");

    when(inventoryRepository.findByIdAndOrganizationId("1", "tac")).thenReturn(Optional.of(inventory));

    ResponseEntity<Inventory> response = inventoryService.deleteExistingDeviceDetails("1");

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("1", response.getBody().getId());
    verify(inventoryRepository).delete(inventory);
  }

  @Test
  void testGetAllDevicesByOrganizationId() {
    List<Inventory> list = Arrays.asList(new Inventory(), new Inventory());

    when(inventoryRepository.findByOrganizationId("tac")).thenReturn(list);

    List<Inventory> result = inventoryService.getAllDevicesByOrganizationId("tac");

    assertEquals(2, result.size());
  }

  @Test
  void testGetTotalInventorySize_NoFilters() {
    when(mongoTemplate.count(any(Query.class), eq(Inventory.class))).thenReturn(0L);

    long size = inventoryService.getTotalInventorySize(null, null, null, null, null, null);

    assertEquals(0, size);
  }

  @Test
  void testGetTotalInventorySize_withAllFilters() {
    when(mongoTemplate.count(any(Query.class), eq(Inventory.class))).thenReturn(40L);

    long size = inventoryService.getTotalInventorySize(
        Device.MOBILE, "Amazon", Availability.YES, "Android", "org123", "");

    assertEquals(40L, size);
  }
}
