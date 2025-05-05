package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.BuildErrorMessage;
import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.client.AccountClient;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.enums.ErrorCode;
import com.beeja.api.financemanagementservice.enums.ErrorType;
import com.beeja.api.financemanagementservice.exceptions.DuplicateDataException;
import com.beeja.api.financemanagementservice.exceptions.ResourceNotFoundException;
import com.beeja.api.financemanagementservice.modals.Inventory;
import com.beeja.api.financemanagementservice.modals.clients.finance.OrganizationPattern;
import com.beeja.api.financemanagementservice.repository.InventoryRepository;
import com.beeja.api.financemanagementservice.requests.DeviceDetails;
import com.beeja.api.financemanagementservice.service.InventoryService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final MongoTemplate mongoTemplate;
  private final MongoOperations mongoOperations;

  @Autowired
  AccountClient accountClient;

  @Autowired
  public InventoryServiceImpl(
      InventoryRepository inventoryRepository,
      MongoTemplate mongoTemplate,
      MongoOperations mongoOperations) {
    this.inventoryRepository = inventoryRepository;
    this.mongoTemplate = mongoTemplate;
    this.mongoOperations = mongoOperations;
  }

  @Override
  public Inventory addDevice(DeviceDetails deviceDetails) throws Exception {
    Optional<Inventory> existingDevice =
        inventoryRepository.findByProductId(deviceDetails.getProductId());
    if (existingDevice.isPresent()) {
      throw new DuplicateDataException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_EXISTS_ERROR,
              ErrorCode.RESOURCE_IN_USE,
              Constants.PRODUCT_ID_ALREADY_EXISTS + deviceDetails.getProductId()));
    }

    Inventory device = new Inventory();
    device.setDevice(deviceDetails.getDevice());
    device.setProvider(deviceDetails.getProvider());
    device.setModel(deviceDetails.getModel());
    device.setType(deviceDetails.getType());
    device.setOs(deviceDetails.getOs());
    device.setSpecifications(deviceDetails.getSpecifications());
    device.setRAM(deviceDetails.getRAM());
    device.setAvailability(deviceDetails.getAvailability());
    device.setProductId(deviceDetails.getProductId());
    device.setPrice(deviceDetails.getPrice());
    device.setDateOfPurchase(deviceDetails.getDateOfPurchase());
    device.setComments(deviceDetails.getComments());
    device.setAccessoryType(deviceDetails.getAccessoryType());
    device.setDeviceNumber(generateDeviceId());
    device.setCreatedBy(UserContext.getLoggedInUserEmail());
    device.setOrganizationId(UserContext.getLoggedInUserOrganization().get("id").toString());
    device.setCreatedAt(new java.util.Date());
    try {
      return inventoryRepository.save(device);
    } catch (DuplicateKeyException e) {
      throw new DuplicateDataException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_EXISTS_ERROR, ErrorCode.RESOURCE_IN_USE, e.getMessage()));
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_SAVING_DEVICE_DETAILS));
    }
  }

  private String generateDeviceId() {
    OrganizationPattern devicePattern =
        accountClient.getActivePatternByType("DEVICE_ID_PATTERN").getBody();
    String prefix = devicePattern.getPrefix();
    int dIDLength = devicePattern.getPatternLength();
    List<Inventory> devices = inventoryRepository.findLastAddedDeviceByPrefix("^" + prefix);
    int newSequence =
        devices.stream()
            .findFirst()
            .map(device -> {
              String lastDeviceNumber = device.getDeviceNumber();
              String lastSequenceStr = lastDeviceNumber.substring(prefix.length());
              if (lastSequenceStr.isEmpty() || !lastSequenceStr.matches("\\d+")) {
                return devicePattern.getInitialSequence();
              }
              return Integer.parseInt(lastSequenceStr) + 1;
            })
            .orElse(devicePattern.getInitialSequence());
    int numberLength = dIDLength - prefix.length();
    String formattedSeq = String.format("%0" + numberLength + "d", newSequence);
    return prefix.toUpperCase() + formattedSeq;
  }

  @Override
  public List<Inventory> filterInventory(
      int pageNumber,
      int pageSize,
      Device device,
      String provider,
      Availability availability,
      String os,
      String RAM,
      String searchTerm) {
    try {
      Query query = new Query();
      if (device != null) {
        query.addCriteria(Criteria.where("device").is(device));
      }
      if (StringUtils.hasText(provider)) {
        query.addCriteria(Criteria.where("provider").is(provider));
      }
      if (availability != null) {
        query.addCriteria(Criteria.where("availability").is(availability));
      }
      if (StringUtils.hasText(os)) {
        query.addCriteria(Criteria.where("os").is(os));
      }
      if (StringUtils.hasText(RAM)) {
        query.addCriteria(Criteria.where("RAM").is(RAM));
      }
      if (StringUtils.hasText(searchTerm)) {
        query.addCriteria(
            Criteria.where("deviceNumber").regex(".*" + Pattern.quote(searchTerm) + ".*", "i"));
      }

      int skip = (pageNumber - 1) * pageSize;
      query.skip(skip).limit(pageSize);
      query.with(Sort.by(Sort.Direction.DESC, "created_at"));
      return mongoTemplate.find(query, Inventory.class);
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_FETCHING_DEVICE_DETAILS));
    }
  }

  @Override
  public Long getTotalInventorySize(
      Device device,
      String provider,
      Availability availability,
      String os,
      String RAM,
      String organizationId,
      String searchTerm) {
    Query query = new Query();
    if (device != null) {
      query.addCriteria(Criteria.where("device").is(device));
    }
    if (availability != null) {
      query.addCriteria(Criteria.where("availability").is(availability));
    }
    if (StringUtils.hasText(os)) {
      query.addCriteria(Criteria.where("os").is(os));
    }
    if (StringUtils.hasText(RAM)) {
      query.addCriteria(Criteria.where("RAM").is(RAM));
    }
    if (organizationId != null) {
      query.addCriteria(Criteria.where("organizationId").is(organizationId));
    }
    if (StringUtils.hasText(provider)) {
      query.addCriteria(Criteria.where("provider").is(provider));
    }
    if (StringUtils.hasText(searchTerm)) {
      query.addCriteria(
          Criteria.where("deviceNumber").regex(".*" + Pattern.quote(searchTerm) + ".*", "i"));
    }
    return mongoTemplate.count(query, Inventory.class);
  }

  public List<Inventory> getAllDevicesByOrganizationId(String organizationId) {
    try {
      return inventoryRepository.findByOrganizationId(
          UserContext.getLoggedInUserOrganization().get("id").toString());
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_FETCHING_DEVICE_DETAILS));
    }
  }

  @Override
  public ResponseEntity<Inventory> deleteExistingDeviceDetails(String id) throws Exception {
    try {
      String loggedInUserOrganizationId =
          (String) UserContext.getLoggedInUserOrganization().get("id");
      Optional<Inventory> optionalDevice =
          inventoryRepository.findByIdAndOrganizationId(id, loggedInUserOrganizationId);
      if (optionalDevice.isPresent()) {
        Inventory inventory = optionalDevice.get();
        inventoryRepository.delete(inventory);
        return ResponseEntity.ok().body(inventory);
      } else {
        throw new ResourceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.RESOURCE_NOT_FOUND,
                Constants.DEVICE_NOT_FOUND + " with ID " + id));
      }
    } catch (Exception e) {
      throw new RuntimeException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_DELETE_SELF_ORGANIZATION,
              Constants.ERROR_DELETING_DEVICE_DETAILS));
    }
  }

  @Override
  public Inventory updateDeviceDetails(DeviceDetails updatedDeviceDetails, String deviceId)
      throws Exception {
    try {
      if (updatedDeviceDetails.getProductId() != null) {
        Optional<Inventory> productIdCheck =
            inventoryRepository.findByProductId(updatedDeviceDetails.getProductId());
        if (productIdCheck.isPresent()) {
          throw new DuplicateDataException(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.CONFLICT_ERROR,
                  ErrorCode.RESOURCE_EXISTS_ERROR,
                  Constants.PRODUCT_ID_ALREADY_EXISTS + updatedDeviceDetails.getProductId()));
        }
      }

      String loggedInUserOrganizationId =
          (String) UserContext.getLoggedInUserOrganization().get("id").toString();
      Optional<Inventory> optionalDeviceDetails =
          inventoryRepository.findByIdAndOrganizationId(deviceId, loggedInUserOrganizationId);
      if (optionalDeviceDetails.isPresent()) {
        Inventory existingDevice = optionalDeviceDetails.get();
        String actualProductId = existingDevice.getProductId();

        for (Field field : updatedDeviceDetails.getClass().getDeclaredFields()) {
          field.setAccessible(true);
          Object value = field.get(updatedDeviceDetails);
          if (value != null) {
            Field existingField = existingDevice.getClass().getDeclaredField(field.getName());
            existingField.setAccessible(true);
            existingField.set(existingDevice, value);
          }
        }

        if (!StringUtils.hasText(updatedDeviceDetails.getProductId())) {
          existingDevice.setProductId(actualProductId);
        }

        return inventoryRepository.save(existingDevice);
      } else {
        throw new ResourceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.RESOURCE_NOT_FOUND,
                Constants.DEVICE_NOT_FOUND + " with ID " + deviceId));
      }
    } catch (DuplicateDataException e) {
      throw new DuplicateDataException(e.getMessage());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_UPDATING_DEVICE_DETAILS));
    }
  }
}
