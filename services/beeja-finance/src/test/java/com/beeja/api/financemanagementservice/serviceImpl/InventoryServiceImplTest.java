package com.beeja.api.financemanagementservice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.exceptions.DuplicateDataException;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import com.beeja.api.financemanagementservice.repository.InventoryRepository;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {
        @Mock private InventoryRepository inventoryRepository;
        @Mock private MongoTemplate mongoTemplate;
        @Mock private AccountClient accountClient;

        private InventoryServiceImpl inventoryService;
        private DeviceDetails deviceDetails;
        private MongoOperations mongoOperations;

        @BeforeEach
        void setUp() {
            inventoryService =
                    new InventoryServiceImpl(mongoOperations, mongoTemplate, inventoryRepository, accountClient);
            deviceDetails = new DeviceDetails();
            deviceDetails.setDevice(String.valueOf(Device.MOBILE));
            deviceDetails.setProvider("Google");
            deviceDetails.setAvailability(Availability.NO);
            deviceDetails.setOs("Android");
            deviceDetails.setProductId("P001");

            Map<String, Object> organizationMap = Collections.singletonMap("id", "tac");
            UserContext.setLoggedInUserOrganization(organizationMap);
            mongoTemplate.remove(new Query());
            Inventory inventory = new Inventory();
            inventory.setDevice("Mobile");
            inventory.setProvider("Google");
            inventory.setAvailability(Availability.NO);
            inventory.setOs("NA");
        }

        @Test
        void testAddDevice_Success() throws Exception {
            when(inventoryRepository.findByProductId("P001")).thenReturn(Optional.empty());

            OrganizationPattern mockPattern = new OrganizationPattern();
            mockPattern.setPrefix("DEV");
            mockPattern.setInitialSequence(0); // Starting sequence for the test
            mockPattern.setPatternLength(5);

            ResponseEntity<OrganizationPattern> mockResponseEntity = ResponseEntity.ok(mockPattern);

            when(accountClient.getActivePatternByType(anyString())).thenReturn(mockResponseEntity);

            Inventory savedInventory = new Inventory();
            savedInventory.setProductId("P001");
            savedInventory.setDeviceNumber("DEV-00101-XYZ");

            when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

            Inventory result = inventoryService.addDevice(deviceDetails);

            assertNotNull(result);
            assertEquals("P001", result.getProductId());
            assertEquals("DEV-00101-XYZ", result.getDeviceNumber());

            verify(inventoryRepository, times(1)).save(any(Inventory.class));
            verify(accountClient, times(1)).getActivePatternByType(anyString());
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
            inventory.setDevice(String.valueOf(Device.MOBILE));
            inventory.setProvider("Google");
            inventory.setAvailability(Availability.NO);
            inventory.setOs("Android");
            inventory.setRam("NA");
            List<Inventory> expectedInventories = new ArrayList<>();
            expectedInventories.add(inventory);

            Query query = new Query();
            query.addCriteria(Criteria.where("device").regex("Mobile", "i"));
            query.addCriteria(Criteria.where("provider").regex("Google", "i"));
            query.addCriteria(Criteria.where("availability").is("NO"));
            query.addCriteria(Criteria.where("os").regex("NA", "i"));
            query.addCriteria(Criteria.where("RAM").regex("NA", "i"));
            query.skip(0).limit(10);

            when(mongoTemplate.find(any(Query.class), eq(Inventory.class))).thenReturn(expectedInventories);
            List<Inventory> result =
                    inventoryService.filterInventory(
                            1, 10, Device.valueOf("Mobile"), "Google", Availability.NO, "NA", "NA", "NA");
            verify(mongoTemplate).find(any(Query.class), eq(Inventory.class));
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedInventories.get(0), result.get(0));
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
            inventory.setDevice("Mobile");
            inventory.setProvider("Google");
            inventory.setAvailability(Availability.NO);
            inventory.setOs("NA");
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

            long size = inventoryService.getTotalInventorySize(null, null, null, null, null, null,null);

            assertEquals(0, size);
        }

        @Test
        void testGetTotalInventorySize_withAllFilters() {
            String device = "Mobile";
            Availability availability = Availability.YES;
            String os = "Android";
            String organizationId = "org123";
            String provider = "Amazon";
            String RAM = "12GB";
            Query query = new Query();
            query.addCriteria(Criteria.where("device").is(device));
            query.addCriteria(Criteria.where("availability").is(availability));
            query.addCriteria(Criteria.where("os").is(os));
            query.addCriteria(Criteria.where("RAM").is(RAM));
            query.addCriteria(Criteria.where("organizationId").is(organizationId));
            query.addCriteria(Criteria.where("provider").is(provider));
            when(mongoTemplate.count(query, Inventory.class)).thenReturn(40L);
            Long result =
                    inventoryService.getTotalInventorySize(
                            Device.valueOf(device), provider, availability, os, RAM, organizationId, "");
            assertEquals(40L, result);
            verify(mongoTemplate).count(query, Inventory.class);
        }
    }
