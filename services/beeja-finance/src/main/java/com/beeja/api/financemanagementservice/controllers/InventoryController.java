package com.beeja.api.financemanagementservice.controllers;

import static com.beeja.api.financemanagementservice.Utils.Constants.CREATE_DEVICES;
import static com.beeja.api.financemanagementservice.Utils.Constants.DELETE_DEVICES;
import static com.beeja.api.financemanagementservice.Utils.Constants.READ_DEVICES;
import static com.beeja.api.financemanagementservice.Utils.Constants.UPDATE_DEVICES;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.annotations.HasPermission;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.exceptions.BadRequestException;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import com.beeja.api.financemanagementservice.response.InventoryResponseDTO;
import com.beeja.api.financemanagementservice.service.InventoryService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/inventory")
public class InventoryController {

  @Autowired InventoryService inventoryService;

  @PostMapping
  @HasPermission(CREATE_DEVICES)
  public ResponseEntity<Inventory> addDevice(
      @Valid @RequestBody DeviceDetails deviceDetails, BindingResult bindingResult)
      throws Exception {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages =
          bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .collect(Collectors.toList());
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.VALIDATION_ERROR,
              ErrorCode.FIELD_VALIDATION_MISSING,
              errorMessages.toString()));
    }
    Inventory newDevice = inventoryService.addDevice(deviceDetails);
    return new ResponseEntity<>(newDevice, HttpStatus.CREATED);
  }

  @GetMapping
  @HasPermission(READ_DEVICES)
  public ResponseEntity<InventoryResponseDTO> filterInventory(
      @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
      @RequestParam(required = false) Device device,
      @RequestParam(required = false) String provider,
      @RequestParam(required = false) Availability availability,
      @RequestParam(required = false) String os,
      @RequestParam(required = false) String RAM,
      @RequestParam(required = false) String searchTerm)
      throws Exception {
    HashMap<String, Object> metadata = new HashMap<>();
    metadata.put(
        "totalSize",
        inventoryService.getTotalInventorySize(
            device,
            provider,
            availability,
            os,
            RAM,
            UserContext.getLoggedInUserOrganization().get("id").toString(),
            searchTerm));

    List<Inventory> devices =
        inventoryService.filterInventory(
            pageNumber, pageSize, device, provider, availability, os, RAM, searchTerm);
    InventoryResponseDTO response = new InventoryResponseDTO();
    response.setMetadata(metadata);
    response.setInventory(devices);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/{deviceId}")
  @HasPermission(DELETE_DEVICES)
  public ResponseEntity<Inventory> deleteExistingDeviceDetails(@PathVariable String deviceId)
      throws Exception {
    return inventoryService.deleteExistingDeviceDetails(deviceId);
  }

  @PutMapping("/{deviceId}")
  @HasPermission(UPDATE_DEVICES)
  public ResponseEntity<Inventory> updateDeviceDetails(
      @RequestBody DeviceDetails deviceDetails, @PathVariable String deviceId) throws Exception {
    Inventory updatedDeviceDetails = inventoryService.updateDeviceDetails(deviceDetails, deviceId);
    return ResponseEntity.ok().body(updatedDeviceDetails);
  }
}
