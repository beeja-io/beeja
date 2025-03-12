package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.clients.FileClient;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.enums.CurrencyType;
import com.beeja.api.accounts.enums.DateFormats;
import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.enums.FontNames;
import com.beeja.api.accounts.enums.Theme;
import com.beeja.api.accounts.enums.TimeZones;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Accounts;
import com.beeja.api.accounts.model.Organization.Address;
import com.beeja.api.accounts.model.Organization.LoanLimit;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Preferences;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.repository.FeatureToggleRepository;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.repository.OrganizationRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.FileRequest;
import com.beeja.api.accounts.response.FileDownloadResultMetaData;
import com.beeja.api.accounts.response.FileResponse;
import com.beeja.api.accounts.response.OrganizationResponse;
import com.beeja.api.accounts.service.OrganizationService;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

  @Autowired OrganizationRepository organizationRepository;

  @Autowired UserRepository userRepository;

  @Autowired RolesRepository rolesRepository;

  @Autowired EmployeeFeignClient employeeFeignClient;

  @Autowired FileClient fileClient;

  @Autowired FeatureToggleRepository featureToggleRepository;

  @Autowired OrgDefaultsRepository orgDefaultsRepository;

  @Autowired OrgDefaultsGenerationImpl orgDefaultsGenerationImpl;

  @Autowired OrgDefaultsGenerationExistingImpl orgDefaultsGenerationExistingImpl;

  @Override
  public List<User> getAllUsersByOrganizationId(String organizationId) throws Exception {
    Optional<Organization> organization;
    try {
      organization = organizationRepository.findById(organizationId);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID));
    }
    if (organization.isPresent()) {
      try {
        return userRepository.findByOrganizationsId(organizationId);
      } catch (Exception e) {
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.DB_ERROR,
                ErrorCode.UNABLE_TO_FETCH_DETAILS,
                Constants.ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID));
      }
    } else {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.ORGANIZATION_NOT_FOUND,
              Constants.ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID));
    }
  }

  @Override
  public OrganizationResponse getOrganizationById(String id) throws Exception {
    try {
      if (UserContext.getLoggedInUserPermissions()
          .contains(PermissionConstants.READ_ALL_ORGANIZATIONS)) {
        return organizationRepository.findByOrganizationId(id);
      }
      return organizationRepository.findByOrganizationId(
          UserContext.getLoggedInUserOrganization().getId());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID));
    }
  }

  @Override
  public Organization updateOrganization(String organizationId, String fields, MultipartFile file)
      throws Exception {
    Optional<Organization> orgOptional;
    if (UserContext.getLoggedInUserPermissions()
        .contains(PermissionConstants.UPDATE_ALL_ORGANIZATIONS)) {
      orgOptional = organizationRepository.findById(organizationId);
    } else {
      orgOptional =
          organizationRepository.findById(UserContext.getLoggedInUserOrganization().getId());
    }

    if (orgOptional.isEmpty()) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.ORGANIZATION_NOT_FOUND,
              Constants.ERROR_NO_ORGANIZATION_FOUND_WITH_PROVIDED_ID));
    }

    Organization organization = orgOptional.get();
    if (fields != null) {
      ObjectMapper objectMapper = new ObjectMapper();

      try {
        Map<String, Object> fieldsMap =
            objectMapper.readValue(
                fields,
                new TypeReference<Map<String, Object>>() {
                  // Added to pass checkstyle
                });

        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
          String key = entry.getKey();
          Object value = entry.getValue();

          if (value == null) {
            continue;
          }

          try {
            Field field = Organization.class.getDeclaredField(key);
            field.setAccessible(true);
            if (key.equals("preferences")) {
              Map<String, Object> preferencesMap = (Map<String, Object>) value;
              Preferences preferences;
              if (organization.getPreferences() == null) {
                preferences = new Preferences();
              } else {
                preferences = organization.getPreferences();
              }
              for (Map.Entry<String, Object> prefEntry : preferencesMap.entrySet()) {
                String prefKey = prefEntry.getKey();
                Object prefValue = prefEntry.getValue();
                if (prefValue != null) {
                  if (prefKey.equals("theme")) {
                    Theme theme = Theme.valueOf((String) prefValue);
                    preferences.setTheme(theme);
                  } else if (prefKey.equals("dateFormat")) {
                    DateFormats dateFormats = DateFormats.valueOf((String) prefValue);
                    preferences.setDateFormat(dateFormats);
                  } else if (prefKey.equals("timeZone")) {
                    TimeZones timeZone = TimeZones.valueOf((String) prefValue);
                    preferences.setTimeZone(timeZone);
                  } else if (prefKey.equals("fontName")) {
                    FontNames fontName = FontNames.valueOf((String) prefValue);
                    preferences.setFontName(fontName);
                  } else if (prefKey.equals("currencyType")) {
                    CurrencyType currencyType = CurrencyType.valueOf((String) prefValue);
                    preferences.setCurrencyType(currencyType);
                  } else {
                    Field prefField = Preferences.class.getDeclaredField(prefKey);
                    prefField.setAccessible(true);
                    prefField.set(preferences, prefValue);
                  }
                }
              }
              field.set(organization, preferences);
            } else if (key.equals("address")) {
              Map<String, Object> addressMap = (Map<String, Object>) value;
              Address address;
              if (organization.getAddress() == null) {
                address = new Address();
              } else {
                address = organization.getAddress();
              }
              for (Map.Entry<String, Object> addressEntry : addressMap.entrySet()) {
                String addressKey = addressEntry.getKey();
                Object addressValue = addressEntry.getValue();
                if (addressValue != null) {
                  Field addressField = Address.class.getDeclaredField(addressKey);
                  addressField.setAccessible(true);
                  addressField.set(address, addressValue);
                }
              }
              field.set(organization, address);
            } else if (key.equals("accounts")) {
              Map<String, Object> accountsMap = (Map<String, Object>) value;
              Accounts accounts;
              if (organization.getAccounts() == null) {
                accounts = new Accounts();
              } else {
                accounts = organization.getAccounts();
              }
              for (Map.Entry<String, Object> accountsEntry : accountsMap.entrySet()) {
                String accountsKey = accountsEntry.getKey();
                Object accountsValue = accountsEntry.getValue();
                if (accountsValue != null) {
                  Field accountsField = Accounts.class.getDeclaredField(accountsKey);
                  accountsField.setAccessible(true);
                  accountsField.set(accounts, accountsValue);
                }
              }
              field.set(organization, accounts);
            } else if (key.equals("loanLimit")) {
              Map<String, Object> loanLimitMap = (Map<String, Object>) value;
              LoanLimit loanLimit;
              if (organization.getLoanLimit() == null) {
                loanLimit = new LoanLimit();
              } else {
                loanLimit = organization.getLoanLimit();
              }
              for (Map.Entry<String, Object> loanLimitEntry : loanLimitMap.entrySet()) {
                String loanLimitKey = loanLimitEntry.getKey();
                Object loanLimitValue = loanLimitEntry.getValue();
                if (loanLimitValue != null) {
                  Field loanLimitField = LoanLimit.class.getDeclaredField(loanLimitKey);
                  loanLimitField.setAccessible(true);
                  loanLimitField.set(loanLimit, loanLimitValue);
                }
              }
              field.set(organization, loanLimit);
            } else {
              field.set(organization, value);
            }
          } catch (NoSuchFieldException e) {
            throw new BadRequestException(
                BuildErrorMessage.buildErrorMessage(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.FIELD_VALIDATION_MISSING,
                    Constants.FIELD_NOT_EXIST_ORGANIZATION_ENTITY + key));
          } catch (IllegalAccessException e) {
            throw new BadRequestException(
                BuildErrorMessage.buildErrorMessage(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.FIELD_VALIDATION_MISSING,
                    Constants.ERROR_UPDATING_FIELD + key));
          }
        }
      } catch (IOException e) {
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR,
                ErrorCode.FIELD_VALIDATION_MISSING,
                Constants.ERROR_PARSING_JSON));
      }
    }

    if (file != null) {
      if (organization.getLogoFileId() == null) {
        try {
          FileRequest fileRequest = new FileRequest();
          fileRequest.setFile(file);
          fileRequest.setEntityType("orgLogo");
          fileRequest.setEntityId(UserContext.getLoggedInUserOrganization().getId());
          ResponseEntity<?> fileResponse = fileClient.uploadFile(fileRequest);
          LinkedHashMap<String, Object> responseBody =
              (LinkedHashMap<String, Object>) fileResponse.getBody();

          ObjectMapper objectMapper = new ObjectMapper();
          FileResponse savedFile = objectMapper.convertValue(responseBody, FileResponse.class);
          organization.setLogoFileId(savedFile.getId());
        } catch (Exception e) {
          throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.API_ERROR,
                  ErrorCode.RESOURCE_CREATING_ERROR,
                  Constants.ERROR_IN_UPDATING_ORG_LOGO));
        }
      } else {
        try {
          FileRequest fileRequest = new FileRequest();
          fileRequest.setFile(file);
          fileClient.updateFile(organization.getLogoFileId(), fileRequest);
        } catch (Exception e) {
          throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.API_ERROR,
                  ErrorCode.RESOURCE_CREATING_ERROR,
                  Constants.ERROR_IN_UPDATING_ORG_LOGO));
        }
      }
    }

    try {
      return organizationRepository.save(organization);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_UPDATING_ORGANIZATION));
    }
  }

  @Override
  public ByteArrayResource downloadOrganizationFile() throws Exception {
    try {
      if (UserContext.getLoggedInUserOrganization().getLogoFileId() == null) {
        throw new ResourceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.FILE_NOT_FOUND,
                Constants.ORGANIZATION_LOGO_NOT_FOUND));
      }
      ResponseEntity<byte[]> fileResponse =
          fileClient.downloadFile(UserContext.getLoggedInUserOrganization().getLogoFileId());
      byte[] fileData = fileResponse.getBody();
      FileDownloadResultMetaData finalMetaData = getMetaData(fileResponse);

      return new ByteArrayResource(Objects.requireNonNull(fileData)) {
        @Override
        public String getFilename() {
          return finalMetaData.getFileName() != null ? finalMetaData.getFileName() : "beeja_file";
        }
      };
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR,
              Constants.ERROR_IN_FETCHING_ORGANIZATION_LOGO));
    }
  }

  private static FileDownloadResultMetaData getMetaData(ResponseEntity<byte[]> fileResponse) {
    HttpHeaders headers = fileResponse.getHeaders();
    String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
    String createdBy = headers.getFirst("createdby");
    String organizationId = headers.getFirst("organizationid");
    String entityId = headers.getFirst("entityId");
    String filename = null;

    if (contentDisposition != null && !contentDisposition.isEmpty()) {
      int startIndex = contentDisposition.indexOf("filename=\"") + 10;
      int endIndex = contentDisposition.lastIndexOf("\"");
      if (endIndex != -1) {
        filename = contentDisposition.substring(startIndex, endIndex);
      }
    }

    return new FileDownloadResultMetaData(filename, createdBy, entityId, organizationId);
  }

  @Override
  public OrgDefaults updateOrganizationValues(OrgDefaults orgDefaults) throws Exception {
    OrgDefaults orgDefaults1 =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), orgDefaults.getKey());
    if (orgDefaults1 != null) {
      orgDefaults1.setValues(orgDefaults.getValues());
      try {
        return orgDefaultsRepository.save(orgDefaults1);
      } catch (Exception e) {
        log.error(Constants.ERROR_IN_UPDATING_ORGANIZATION + "{}", e.getMessage());
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.DB_ERROR,
                ErrorCode.CANNOT_SAVE_CHANGES,
                Constants.ERROR_IN_UPDATING_ORGANIZATION));
      }
    } else {
      OrgDefaults newOrgDefaults = new OrgDefaults();
      newOrgDefaults.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
      newOrgDefaults.setKey(orgDefaults.getKey());
      newOrgDefaults.setValues(orgDefaults.getValues());
      try {
        return orgDefaultsRepository.save(newOrgDefaults);
      } catch (Exception e) {
        log.error(Constants.ERROR_IN_CREATE_ORGANIZATION + "{}", e.getMessage());
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.DB_ERROR,
                ErrorCode.CANNOT_SAVE_CHANGES,
                Constants.ERROR_IN_CREATE_ORGANIZATION));
      }
    }
  }

  @Override
  public OrgDefaults getOrganizationValuesByKey(String key) throws Exception {
    try {
      return orgDefaultsRepository.findByOrganizationIdAndKey(
          UserContext.getLoggedInUserOrganization().getId(), key);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_UPDATING_ORGANIZATION));
    }
  }

  @Override
  public List<OrgDefaults> getOrganizationValues(List<String> keys) throws Exception {
    try {
      return orgDefaultsRepository.findByOrganizationIdAndKeyIn(UserContext.getLoggedInUserOrganization().getId(), keys);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_UPDATING_ORGANIZATION));
    }
  }

  @Override
  @Async
  public void generateOrganizationDefaults() throws Exception {
    orgDefaultsGenerationExistingImpl.generateExistingValuesOfExpenseType();
    CompletableFuture<Void> jobTitlesFuture = CompletableFuture.runAsync(() -> {
      log.info(Constants.GENERATING_DEFAULT_VALUES,  UserContext.getLoggedInUserOrganization().getId());
      orgDefaultsGenerationImpl.generateJobTitles();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "jobTitles", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
      return result;
    });

    CompletableFuture<Void> departmentsFuture = CompletableFuture.runAsync(() -> {
      orgDefaultsGenerationImpl.generateOrganizationDepartments();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "departments", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
      return result;
    });

    CompletableFuture<Void> employmentTypesFuture = CompletableFuture.runAsync(() -> {
      orgDefaultsGenerationImpl.generateEmploymentTypes();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "employmentTypes", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
      return result;
    });

    CompletableFuture<Void> expenseCategoriesFuture = CompletableFuture.runAsync(() -> {
      orgDefaultsGenerationImpl.generateExpenseCategories();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "expenseCategories", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
      return result;
    });

    CompletableFuture<Void> expenseTypesFuture = CompletableFuture.runAsync(() -> {
      orgDefaultsGenerationImpl.generateExpenseTypes();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "expenseTypes", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
        return result;
    });

    CompletableFuture<Void> paymentModesFuture = CompletableFuture.runAsync(() -> {
      orgDefaultsGenerationImpl.generatePaymentModes();
    }).handle((result, ex) -> {
      if (ex != null) {
        log.error(Constants.ERROR_GENERATING_DEFAULT_VALUES, "paymentModes", UserContext.getLoggedInUserOrganization().getId(), ex);
      }
      return result;
    });
    CompletableFuture<Void> allFutures = CompletableFuture.allOf(jobTitlesFuture,
            departmentsFuture,
            employmentTypesFuture,
            expenseCategoriesFuture,
            expenseTypesFuture,
            paymentModesFuture);
    allFutures.join();
    log.info("All defaults generated");
  }
}
