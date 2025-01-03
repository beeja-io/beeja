package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.exceptions.DuplicateProductIdException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

  @InjectMocks InventoryServiceImpl inventoryService;

  @Mock InventoryRepository inventoryRepository;

  @Mock private MongoTemplate mongoTemplate;

  private MockMvc mockMvc;

  private DeviceDetails deviceDetails;

  private static final String DEVICE_SEQUENCE = "DEVICE_SEQUENCE";
  private static final String EXPECTED_PREFIX = "TAC-";

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(inventoryService).build();
    Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
    UserContext.setLoggedInUserOrganization(organizationMap);
    mongoTemplate.remove(new Query());
    Inventory inventory = new Inventory();
    inventory.setDevice(Device.MOBILE);
    inventory.setProvider("Google");
    inventory.setAvailability(Availability.NO);
    inventory.setOs("NA");
  }

  @Test
  public void testAddDevice_Success() throws Exception {
    DeviceDetails deviceDetails = new DeviceDetails();
    when(inventoryRepository.findByProductId(deviceDetails.getProductId()))
        .thenReturn(Optional.empty());
    Inventory savedInventory = new Inventory();
    savedInventory.setProductId(deviceDetails.getProductId());
    when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);
    Inventory result = inventoryService.addDevice(deviceDetails);
    assertNotNull(result);
    assertEquals(deviceDetails.getProductId(), result.getProductId());
    verify(inventoryRepository, times(1)).save(any(Inventory.class));
  }

  @Test
  public void testAddDevice_DuplicateProductId() {
    DeviceDetails deviceDetails = new DeviceDetails();
    deviceDetails.setProductId("P001");
    when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.of(new Inventory()));
    assertThrows(
        DuplicateProductIdException.class, () -> inventoryService.addDevice(deviceDetails));
  }

  @Test
  public void testFilterInventory_Success() throws Exception {
    Inventory inventory = new Inventory();
    inventory.setDevice(Device.MOBILE);
    inventory.setProvider("Google");
    inventory.setAvailability(Availability.NO);
    inventory.setOs("NA");
    List<Inventory> expectedInventories = new ArrayList<>();
    expectedInventories.add(inventory);

    Query query = new Query();
    query.addCriteria(Criteria.where("device").regex("Mobile", "i"));
    query.addCriteria(Criteria.where("provider").regex("Google", "i"));
    query.addCriteria(Criteria.where("availability").is("NO"));
    query.addCriteria(Criteria.where("os").regex("NA", "i"));
    query.skip(0).limit(10);

    when(mongoTemplate.find(any(Query.class), eq(Inventory.class))).thenReturn(expectedInventories);
    List<Inventory> result =
        inventoryService.filterInventory(
            1, 10, Device.MOBILE, "Google", Availability.NO, "NA", "NA");
    verify(mongoTemplate).find(any(Query.class), eq(Inventory.class));
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedInventories.get(0), result.get(0));
  }

  @Test
  public void testUpdateDeviceDetails_Success() throws Exception {
    String deviceId = "TAC-0001";
    String uniqueProductId = "unique-product-id";
    DeviceDetails updatedDeviceDetails = new DeviceDetails();
    updatedDeviceDetails.setProductId(uniqueProductId);
    Inventory existingInventory = new Inventory();
    existingInventory.setId(deviceId);
    existingInventory.setProductId("old-product-id");
    when(inventoryRepository.findByProductId(uniqueProductId)).thenReturn(Optional.empty());
    when(inventoryRepository.findByIdAndOrganizationId(eq(deviceId), anyString()))
        .thenReturn(Optional.of(existingInventory));
    when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);
    Inventory result = inventoryService.updateDeviceDetails(updatedDeviceDetails, deviceId);
    assertNotNull(result);
    assertEquals(uniqueProductId, result.getProductId());
    verify(inventoryRepository, times(1)).findByProductId(uniqueProductId);
    verify(inventoryRepository, times(1)).findByIdAndOrganizationId(eq(deviceId), anyString());
    verify(inventoryRepository, times(1)).save(any(Inventory.class)); // Verify save operation
  }

  @Test
  public void testUpdateDeviceDetails_DuplicateProductId() {
    DeviceDetails updatedDeviceDetails = new DeviceDetails();
    updatedDeviceDetails.setProductId("P001");
    when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.of(new Inventory()));
    assertThrows(
        DuplicateProductIdException.class,
        () -> inventoryService.updateDeviceDetails(updatedDeviceDetails, "1"));
  }

  @Test
  public void testDeleteExistingDeviceDetails_Success() throws Exception {
    Inventory inventory = new Inventory();
    inventory.setDevice(Device.MOBILE);
    inventory.setProvider("Google");
    inventory.setAvailability(Availability.NO);
    inventory.setOs("NA");
    inventory.setId("1");
    when(inventoryRepository.findByIdAndOrganizationId("1", "tac"))
        .thenReturn(Optional.of(inventory));
    ResponseEntity<Inventory> response = inventoryService.deleteExistingDeviceDetails("1");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("1", response.getBody().getId());
    verify(inventoryRepository, times(1)).delete(inventory);
  }

  @Test
  public void testGetAllDevicesByOrganizationId() {
    // Arrange
    String organizationId = "tac";
    List<Inventory> expectedDevices = Arrays.asList(new Inventory(), new Inventory());
    when(inventoryRepository.findByOrganizationId(organizationId)).thenReturn(expectedDevices);

    // Act
    List<Inventory> actualDevices = inventoryService.getAllDevicesByOrganizationId(organizationId);

    // Assert
    assertEquals(expectedDevices, actualDevices);
    verify(inventoryRepository, times(1)).findByOrganizationId(organizationId);
  }

  @Test
  void testGetTotalInventorySize_NoFilters() {
    long totalSize = inventoryService.getTotalInventorySize(null, null, null, null, null, null);
    assertEquals(0, totalSize, "Total inventory size should be zero when no filters are applied.");
  }

  @Test
  void testGetTotalInventorySize_withAllFilters() {
    Device device = Device.MOBILE;
    Availability availability = Availability.YES;
    String os = "Android";
    String organizationId = "org123";
    String provider = "Amazon";
    Query query = new Query();
    query.addCriteria(Criteria.where("device").is(device));
    query.addCriteria(Criteria.where("availability").is(availability));
    query.addCriteria(Criteria.where("os").is(os));
    query.addCriteria(Criteria.where("organizationId").is(organizationId));
    query.addCriteria(Criteria.where("provider").is(provider));
    when(mongoTemplate.count(query, Inventory.class)).thenReturn(40L);
    Long result =
        inventoryService.getTotalInventorySize(
            device, provider, availability, os, organizationId, "");
    assertEquals(40L, result);
    verify(mongoTemplate).count(query, Inventory.class);
  }
}
