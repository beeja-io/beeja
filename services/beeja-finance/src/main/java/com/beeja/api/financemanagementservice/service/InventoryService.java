package com.beeja.api.financemanagementservice.service;

import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface InventoryService {
  Inventory addDevice(DeviceDetails deviceDetails) throws Exception;

  List<Inventory> filterInventory(
      int pageNumber,
      int pageSize,
      String device,
      String provider,
      Availability availability,
      String os,
      String ram,
      String searchTerm)
      throws Exception;

  ResponseEntity<Inventory> deleteExistingDeviceDetails(String id) throws Exception;

  Inventory updateDeviceDetails(DeviceDetails deviceDetails, String deviceId) throws Exception;

  List<Inventory> getAllDevicesByOrganizationId(String organizationId);

  Long getTotalInventorySize(
      String device,
      String provider,
      Availability availability,
      String os,
      String ram,
      String organizationId,
      String searchTerm);
}
