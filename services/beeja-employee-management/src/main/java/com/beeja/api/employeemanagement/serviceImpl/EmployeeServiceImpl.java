package com.beeja.api.employeemanagement.serviceImpl;

import com.beeja.api.employeemanagement.response.EmployeeDefaultValues;
import com.beeja.api.employeemanagement.response.EmployeeValues;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.beeja.api.employeemanagement.client.AccountClient;
import com.beeja.api.employeemanagement.constants.PermissionConstants;
import com.beeja.api.employeemanagement.enums.ErrorCode;
import com.beeja.api.employeemanagement.enums.ErrorType;
import com.beeja.api.employeemanagement.exceptions.BadRequestException;
import com.beeja.api.employeemanagement.exceptions.ResourceAlreadyFound;
import com.beeja.api.employeemanagement.exceptions.ResourceNotFound;
import com.beeja.api.employeemanagement.exceptions.UnAuthorisedException;
import com.beeja.api.employeemanagement.model.Address;
import com.beeja.api.employeemanagement.model.BankDetails;
import com.beeja.api.employeemanagement.model.Contact;
import com.beeja.api.employeemanagement.model.Employee;
import com.beeja.api.employeemanagement.model.File;
import com.beeja.api.employeemanagement.model.JobDetails;
import com.beeja.api.employeemanagement.model.KYCDetails;
import com.beeja.api.employeemanagement.model.NomineeDetails;
import com.beeja.api.employeemanagement.model.PFDetails;
import com.beeja.api.employeemanagement.model.PersonalInformation;
import com.beeja.api.employeemanagement.repository.EmployeeRepository;
import com.beeja.api.employeemanagement.requests.EmployeeOrgRequest;
import com.beeja.api.employeemanagement.requests.EmployeeUpdateRequest;
import com.beeja.api.employeemanagement.requests.FileUploadRequest;
import com.beeja.api.employeemanagement.requests.UpdateKYCRequest;
import com.beeja.api.employeemanagement.response.EmployeeResponse;
import com.beeja.api.employeemanagement.response.GetLimitedEmployee;
import com.beeja.api.employeemanagement.service.EmployeeService;
import com.beeja.api.employeemanagement.service.FileService;
import com.beeja.api.employeemanagement.utils.BuildErrorMessage;
import com.beeja.api.employeemanagement.utils.Constants;
import com.beeja.api.employeemanagement.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.io.Files.getFileExtension;
import static com.beeja.api.employeemanagement.constants.PermissionConstants.CREATE_EMPLOYEE;
import static com.beeja.api.employeemanagement.constants.PermissionConstants.READ_COMPLETE_EMPLOYEE_DETAILS;
import static com.beeja.api.employeemanagement.constants.PermissionConstants.UPDATE_ALL_EMPLOYEES;
import static com.beeja.api.employeemanagement.utils.Constants.EMAIL_ALREADY_REGISTERED;
import static com.beeja.api.employeemanagement.utils.Constants.EMPLOYEE_NOT_FOUND;
import static com.beeja.api.employeemanagement.utils.Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE;
import static com.beeja.api.employeemanagement.utils.Constants.INVALID_PROFILE_PIC_FORMATS;
import static com.beeja.api.employeemanagement.utils.Constants.SUCCESSFULLY_UPDATED_PROFILE_PHOTO;
import static com.beeja.api.employeemanagement.utils.Constants.UNAUTHORISED_ACCESS;
import static com.beeja.api.employeemanagement.utils.Constants.UNAUTHORISED_TO_UPDATE_PROFILE_PIC;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired EmployeeRepository employeeRepository;

  @Autowired AccountClient accountClient;

  @Autowired MongoTemplate mongoTemplate;

  @Autowired FileService fileService;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository, AccountClient accountClient) {
    this.employeeRepository = employeeRepository;
    this.accountClient = accountClient;
  }

  @Override
  public Employee createEmployee(Map<String, Object> employee) throws Exception {
    Employee emp = new Employee();
    emp.setBeejaAccountId(((String) employee.get("id")));
    emp.setEmployeeId(((String) employee.get("employeeId")));
    Object organizationsObject = employee.get("organizations");
    if (organizationsObject instanceof Map) {
      Map<String, Object> organizationsMap = (Map<String, Object>) organizationsObject;
      emp.setOrganizationId((String) organizationsMap.get("id"));
    }
    try {
      return employeeRepository.save(emp);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_SAVING_DETAILS));
    }
  }

  @Override
  public Map<String, Object> getEmployeeByEmployeeId(String employeeId) throws Exception {
    Set<String> loggedInUserPermissions = UserContext.getLoggedInUserPermissions();
    boolean haveAccessToCompleteData =
        (Objects.equals(employeeId, UserContext.getLoggedInEmployeeId()))
            || loggedInUserPermissions.contains(READ_COMPLETE_EMPLOYEE_DETAILS);
    String organizationId = UserContext.getLoggedInUserOrganization().getId();
    Employee employee =
        haveAccessToCompleteData
            ? employeeRepository.findByEmployeeIdAndOrganizationId(employeeId, organizationId)
            : employeeRepository.getLimitedDataFindByEmployeeId(employeeId, organizationId);
    if (employee != null) {
      ResponseEntity<?> accountResponse;
      try {
        accountResponse = accountClient.getUserByEmployeeId(employeeId.toUpperCase());
      } catch (Exception e) {
        log.error(
            ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE
                + " Organization Id: {} , EmployeeID: {} {}",
            UserContext.getLoggedInUserOrganization().getId(),
            employeeId,
            e.getMessage());
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.SERVER_ERROR,
                Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE));
      }

      if (accountResponse.getStatusCode().is2xxSuccessful()) {
        Map<String, Object> combinedData = new HashMap<>();
        combinedData.put("employee", employee);
        combinedData.put("account", accountResponse.getBody());

        return combinedData;
      } else {
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.API_ERROR,
                ErrorCode.SERVER_ERROR,
                Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE));
      }

    } else {
      throw new ResourceNotFound(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR, ErrorCode.USER_NOT_FOUND, EMPLOYEE_NOT_FOUND));
    }
  }

  @Override
  public Employee updateEmployee(String id, EmployeeUpdateRequest updatedEmployee)
      throws Exception {

    // TODO - Update this method for org.level
    if (updatedEmployee.getEmail() != null) {
      Boolean userIsPresentInAccount =
          accountClient.isUserPresentWithMail(updatedEmployee.getEmail());
      if (userIsPresentInAccount) {
        throw new ResourceAlreadyFound(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_EXISTS_ERROR,
                ErrorCode.RESOURCE_CREATING_ERROR,
                EMAIL_ALREADY_REGISTERED));
      }
    }

    Optional<Employee> existingEmployeeOptional =
        Optional.ofNullable(
            employeeRepository.findByEmployeeIdAndOrganizationId(
                id, UserContext.getLoggedInUserOrganization().getId()));

    ResponseEntity<?> accountsResponse = null;
    try {
      accountsResponse = accountClient.getUserByEmployeeId(id);
    } catch (Exception e) {
      log.error(
          ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE + " Organization Id: {} , EmployeeID: {} {}",
          UserContext.getLoggedInUserOrganization().getId(),
          id,
          e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR,
              Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE));
    }

    if (existingEmployeeOptional.isPresent()
        && accountsResponse.getStatusCode().is2xxSuccessful()) {
      Map<String, Object> accountDetails = (Map<String, Object>) accountsResponse.getBody();
      String accountOrganizationId =
          ((Map<String, String>) accountDetails.get("organizations")).get("id");

      Employee existingEmployee = existingEmployeeOptional.get();
      updatedEmployee.setId(existingEmployee.getId());
      updatedEmployee.setEmployeeId(existingEmployee.getEmployeeId());
      updatedEmployee.setBeejaAccountId(existingEmployee.getBeejaAccountId());

      if (UserContext.getLoggedInUserPermissions().contains(UPDATE_ALL_EMPLOYEES)) {
        existingEmployee.setPosition(updatedEmployee.getPosition());

        // Updating embedded objects like address, jobDetails etc;
        updateAddress(existingEmployee, updatedEmployee.getAddress());
        updatePersonalInformation(existingEmployee, updatedEmployee.getPersonalInformation());
        updateJobDetails(existingEmployee, updatedEmployee.getJobDetails());
        updateContact(existingEmployee, updatedEmployee.getContact());
        updatePfDetails(existingEmployee, updatedEmployee.getPfDetails());

        if (updatedEmployee.getEmail() != null
            || updatedEmployee.getFirstName() != null
            || updatedEmployee.getLastName() != null) {
          accountClient.updateUser(updatedEmployee.getEmployeeId(), updatedEmployee);
        }

        return employeeRepository.save(existingEmployee);
      } else if (UserContext.getLoggedInEmployeeId().equals(id)) {
        updateContact(existingEmployee, updatedEmployee.getContact());
        return employeeRepository.save(existingEmployee);
      } else {
        throw new UnAuthorisedException(UNAUTHORISED_ACCESS);
      }
    } else {
      throw new ResourceNotFound(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR, ErrorCode.USER_NOT_FOUND, EMPLOYEE_NOT_FOUND));
    }
  }

  @Override
  public List<GetLimitedEmployee> getLimitedDataOfEmployees(
      String department,
      String designation,
      String employmentType,
      int pageNumber,
      int pageSize,
      String status) {
    String organizationId = UserContext.getLoggedInUserOrganization().getId();

    Criteria criteria = Criteria.where("organizationId").is(organizationId);
    if (department != null && !department.isEmpty()) {
      criteria.and("jobDetails.department").is(department);
    }
    if (designation != null && !designation.isEmpty()) {
      criteria.and("jobDetails.designation").is(designation);
    }
    if (employmentType != null && !employmentType.isEmpty()) {
      criteria.and("jobDetails.employementType").is(employmentType);
    }

    List<Employee> employees = mongoTemplate.find(Query.query(criteria), Employee.class);
    List<String> employeeIds =
        employees.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
    ResponseEntity<?> accountResponse =
        accountClient.getUsersByEmployeeIds(new EmployeeOrgRequest(employeeIds));

    // Check if accountResponse is successful and contains body data
    if (accountResponse != null && accountResponse.getStatusCode().is2xxSuccessful()) {
      List<Map<String, Object>> accountDataList =
          (List<Map<String, Object>>) accountResponse.getBody();
      boolean isActive = "active".equalsIgnoreCase(status);

      // Filter account data based on the active status
      List<Map<String, Object>> filteredAccountData =
          accountDataList.stream()
              .filter(accountData -> Boolean.TRUE.equals(accountData.get("active")) == isActive)
              .collect(Collectors.toList());

      // Add criteria based on the filtered account data
      List<String> matchedEmployeeIds =
          filteredAccountData.stream()
              .map(accountData -> (String) accountData.get("employeeId"))
              .collect(Collectors.toList());
      criteria.and("employeeId").in(matchedEmployeeIds);
      if (pageNumber < 1) {
        throw new IllegalArgumentException(Constants.PAGE_NUMBER_INVALID);
      }
      if (pageSize < 1) {
        throw new IllegalArgumentException(Constants.PAGE_SIZE_INVALID);
      }
      if (pageSize > 100) {
        throw new IllegalArgumentException(Constants.PAGE_SIZE_EXCEEDS_LIMIT);
      }
      Aggregation aggregation =
          Aggregation.newAggregation(
              Aggregation.match(criteria),
              Aggregation.project("id", "employeeId", "jobDetails"),
              Aggregation.skip((pageNumber - 1) * pageSize),
              Aggregation.limit(pageSize));
      AggregationResults<GetLimitedEmployee> results =
          mongoTemplate.aggregate(aggregation, "employees", GetLimitedEmployee.class);
      return results.getMappedResults();
    }
    return Collections.emptyList();
  }

  public EmployeeResponse getCombinedLimitedDataOfEmployees(
          String department,
          String designation,
          String employmentType,
          int pageNumber,
          int pageSize,
          String status) throws Exception {


    String loggedInUserRole = UserContext.getLoggedInUserDTO().getRoles().toString();
    boolean validUser = UserContext.getLoggedInUserPermissions().contains(CREATE_EMPLOYEE);

    if (!validUser) {
      status = "active";
    }

    List<GetLimitedEmployee> employeesWithLimitedData =
            getLimitedDataOfEmployees(department, designation, employmentType, pageNumber, pageSize, status);

    if (employeesWithLimitedData == null || employeesWithLimitedData.isEmpty()) {
      EmployeeResponse emptyResponse = new EmployeeResponse();
      emptyResponse.setEmployeeList(Collections.emptyList());
      emptyResponse.setTotalSize(0L);
      return emptyResponse;
    }

    List<String> employeeIds = employeesWithLimitedData.stream()
            .map(GetLimitedEmployee::getEmployeeId)
            .collect(Collectors.toList());

    ResponseEntity<?> accountResponse;
    try {
      accountResponse = accountClient.getUsersByEmployeeIds(new EmployeeOrgRequest(employeeIds));
    } catch (Exception e) {
      log.error("Error fetching data from Account Service: {} ", e.getMessage());
      throw new Exception(BuildErrorMessage.buildErrorMessage(ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR, Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE));
    }

    if (accountResponse.getStatusCode().is2xxSuccessful()) {
      List<Map<String, Object>> combinedDataList = new ArrayList<>();
      List<Map<String, Object>> accountDataList = (List<Map<String, Object>>) accountResponse.getBody();
      boolean isActiveStatus = "active".equalsIgnoreCase(status);

      for (GetLimitedEmployee employee : employeesWithLimitedData) {
        Optional<Map<String, Object>> accountDataOptional =
                accountDataList.stream()
                        .filter(accountData -> employee.getEmployeeId().equals(accountData.get("employeeId")))
                        .findFirst();

        if (accountDataOptional.isPresent()) {
          boolean accountIsActive = Boolean.TRUE.equals(accountDataOptional.get().get("active"));
          if (validUser || accountIsActive) {
            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("employee", employee);
            combinedData.put("account", accountDataOptional.get());
            combinedDataList.add(combinedData);
          }
        }
      }

      Long totalCount = getFilteredEmployeeCount(department, designation, employmentType, status);
      EmployeeResponse response = new EmployeeResponse();
      response.setEmployeeList(combinedDataList);
      response.setTotalSize(totalCount);
      return response;
    } else {
      throw new Exception(BuildErrorMessage.buildErrorMessage(ErrorType.API_ERROR,
              ErrorCode.SERVER_ERROR, Constants.ERROR_IN_FETCHING_DATA_FROM_ACCOUNT_SERVICE));
    }
  }

  private Long getFilteredEmployeeCount(
      String department, String designation, String employementType, String status) {

    Query query = new Query();
    if (department != null && !department.isEmpty()) {
      query.addCriteria(Criteria.where("jobDetails.department").is(department));
    }
    if (designation != null && !designation.isEmpty()) {
      query.addCriteria(Criteria.where("jobDetails.designation").is(designation));
    }
    if (employementType != null && !employementType.isEmpty()) {
      query.addCriteria(Criteria.where("jobDetails.employementType").is(employementType));
    }
    List<Employee> employees = mongoTemplate.find(query, Employee.class);

    List<String> employeeIds =
        employees.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
    ResponseEntity<?> accountResponse =
        accountClient.getUsersByEmployeeIds(new EmployeeOrgRequest(employeeIds));
    if (accountResponse != null && accountResponse.getStatusCode().is2xxSuccessful()) {
      List<Map<String, Object>> accountDataList =
          (List<Map<String, Object>>) accountResponse.getBody();
      boolean isActive = "active".equalsIgnoreCase(status);
      long count = 0;
      for (Employee employee : employees) {
        Optional<Map<String, Object>> accountDataOptional =
            accountDataList.stream()
                .filter(
                    accountData ->
                        employee.getEmployeeId().equals(accountData.get("employeeId"))
                            && Boolean.TRUE.equals(accountData.get("active")) == isActive)
                .findFirst();
        if (accountDataOptional.isPresent()) {
          count++;
        }
      }
      return count;
    }
    return 0L;
  }

  public void updateJobDetails(Employee existingEmployee, JobDetails updatedJobDetails) {
    if (updatedJobDetails != null) {
      JobDetails existingJobDetails = existingEmployee.getJobDetails();
      if (existingJobDetails == null) {
        existingJobDetails = new JobDetails();
        existingEmployee.setJobDetails(existingJobDetails);
      }

      if (updatedJobDetails.getDesignation() != null) {
        existingJobDetails.setDesignation(updatedJobDetails.getDesignation());
      }
      if (updatedJobDetails.getEmployementType() != null) {
        existingJobDetails.setEmployementType(updatedJobDetails.getEmployementType());
      }
      if (updatedJobDetails.getDepartment() != null) {
        existingJobDetails.setDepartment(updatedJobDetails.getDepartment());
      }
      if (updatedJobDetails.getJoiningDate() != null) {
        existingJobDetails.setJoiningDate(updatedJobDetails.getJoiningDate());
      }
      if (updatedJobDetails.getResignationDate() != null) {
        existingJobDetails.setResignationDate(updatedJobDetails.getResignationDate());
      }
    }
  }

  private void updateContact(Employee existingEmployee, Contact updatedContact) {
    if (updatedContact != null) {
      Contact existingContact = existingEmployee.getContact();
      if (existingContact == null) {
        existingContact = new Contact();
        existingEmployee.setContact(existingContact);
      }

      if (updatedContact.getAlternativeEmail() != null) {
        existingContact.setAlternativeEmail(updatedContact.getAlternativeEmail());
      }
      if (UserContext.getLoggedInUserPermissions().contains(CREATE_EMPLOYEE)) {
        if (updatedContact.getPhone() != null) {
          existingContact.setPhone(updatedContact.getPhone());
        }
      }
      if (updatedContact.getAlternativePhone() != null) {
        existingContact.setAlternativePhone(updatedContact.getAlternativePhone());
      }
    }
  }

  private void updatePfDetails(Employee existingEmployee, PFDetails updatedPfDetails) {
    if (updatedPfDetails != null) {
      PFDetails existingPfDetails = existingEmployee.getPfDetails();
      if (existingPfDetails == null) {
        existingPfDetails = new PFDetails();
        existingEmployee.setPfDetails(existingPfDetails);
      }

      if (updatedPfDetails.getPFNumber() != null) {
        existingPfDetails.setPFNumber(updatedPfDetails.getPFNumber());
      }
      if (updatedPfDetails.getUAN() != null) {
        existingPfDetails.setUAN(updatedPfDetails.getUAN());
      }
      if (updatedPfDetails.getJoiningData() != null) {
        existingPfDetails.setJoiningData(updatedPfDetails.getJoiningData());
      }
      if (updatedPfDetails.getAccountNumber() != null) {
        existingPfDetails.setAccountNumber(updatedPfDetails.getAccountNumber());
      }
      if (updatedPfDetails.getState() != null) {
        existingPfDetails.setState(updatedPfDetails.getState());
      }
      if (updatedPfDetails.getLocation() != null) {
        existingPfDetails.setLocation(updatedPfDetails.getLocation());
      }
    }
  }

  private void updateNomineeDetails(
      PersonalInformation existingPersonalInfo, NomineeDetails updatedNomineeDetails) {
    if (updatedNomineeDetails != null) {
      NomineeDetails existingNomineeDetails = existingPersonalInfo.getNomineeDetails();
      if (existingNomineeDetails == null) {
        existingNomineeDetails = new NomineeDetails();
        existingPersonalInfo.setNomineeDetails(existingNomineeDetails);
      }

      if (updatedNomineeDetails.getName() != null) {
        existingNomineeDetails.setName(updatedNomineeDetails.getName());
      }
      if (updatedNomineeDetails.getEmail() != null) {
        existingNomineeDetails.setEmail(updatedNomineeDetails.getEmail());
      }
      if (updatedNomineeDetails.getPhone() != null) {
        existingNomineeDetails.setPhone(updatedNomineeDetails.getPhone());
      }
      if (updatedNomineeDetails.getRelationType() != null) {
        existingNomineeDetails.setRelationType(updatedNomineeDetails.getRelationType());
      }
      if (updatedNomineeDetails.getAadharNumber() != null) {
        existingNomineeDetails.setAadharNumber(updatedNomineeDetails.getAadharNumber());
      }
    }
  }

  private void updatePersonalInformation(
      Employee existingEmployee, PersonalInformation updatedPersonalInfo) {
    if (updatedPersonalInfo != null) {
      PersonalInformation existingPersonalInfo = existingEmployee.getPersonalInformation();
      if (existingPersonalInfo == null) {
        existingPersonalInfo = new PersonalInformation();
        existingEmployee.setPersonalInformation(existingPersonalInfo);
      }

      if (updatedPersonalInfo.getNationality() != null) {
        existingPersonalInfo.setNationality(updatedPersonalInfo.getNationality());
      }
      if (updatedPersonalInfo.getDateOfBirth() != null) {
        existingPersonalInfo.setDateOfBirth(updatedPersonalInfo.getDateOfBirth());
      }
      if (updatedPersonalInfo.getGender() != null) {
        existingPersonalInfo.setGender(updatedPersonalInfo.getGender());
      }
      if (updatedPersonalInfo.getMaritalStatus() != null) {
        existingPersonalInfo.setMaritalStatus(updatedPersonalInfo.getMaritalStatus());
      }
      updateNomineeDetails(existingPersonalInfo, updatedPersonalInfo.getNomineeDetails());
    }
  }

  private void updateAddress(Employee existingEmployee, Address updatedAddress) {
    if (updatedAddress != null) {
      Address existingAddress = existingEmployee.getAddress();
      if (existingAddress == null) {
        existingAddress = new Address();
        existingEmployee.setAddress(existingAddress);
      }

      if (updatedAddress.getHouseNumber() != null) {
        existingAddress.setHouseNumber(updatedAddress.getHouseNumber());
      }
      if (updatedAddress.getLandMark() != null) {
        existingAddress.setLandMark(updatedAddress.getLandMark());
      }
      if (updatedAddress.getVillage() != null) {
        existingAddress.setVillage(updatedAddress.getVillage());
      }
      if (updatedAddress.getCity() != null) {
        existingAddress.setCity(updatedAddress.getCity());
      }
      if (updatedAddress.getState() != null) {
        existingAddress.setState(updatedAddress.getState());
      }
      if (updatedAddress.getCountry() != null) {
        existingAddress.setCountry(updatedAddress.getCountry());
      }
      if (updatedAddress.getPinCode() != null) {
        existingAddress.setPinCode(updatedAddress.getPinCode());
      }
    }
  }

  @Override
  public Employee updateKYCRequest(String id, UpdateKYCRequest updateKYCRequest) throws Exception {
    Employee employee =
        employeeRepository.findByEmployeeIdAndOrganizationId(
            id, UserContext.getLoggedInUserOrganization().getId());
    if (employee == null) {
      throw new ResourceNotFound(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR, ErrorCode.USER_NOT_FOUND, EMPLOYEE_NOT_FOUND));
    }
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = objectMapper.writeValueAsString(updateKYCRequest);
    Map<String, Object> fieldsMap =
        objectMapper.readValue(
            jsonString,
            new TypeReference<Map<String, Object>>() {
              // checkstyle
            });
    for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value == null) {
        continue;
      }
      try {
        Field field = Employee.class.getDeclaredField(key);
        field.setAccessible(true);
        if (key.equals("kycDetails")) {
          Map<String, Object> kycDetailsMap = (Map<String, Object>) value;
          KYCDetails kycDetails;
          if (employee.getKycDetails() == null) {
            kycDetails = new KYCDetails();
          } else {
            kycDetails = employee.getKycDetails();
          }
          for (Map.Entry<String, Object> kycEntry : kycDetailsMap.entrySet()) {
            String kycDetailsKey = kycEntry.getKey();
            Object kycDetailsValue = kycEntry.getValue();
            if (kycDetailsValue != null) {
              Field kycFiled = KYCDetails.class.getDeclaredField(kycDetailsKey);
              kycFiled.setAccessible(true);
              kycFiled.set(kycDetails, kycDetailsValue);
            }
          }
          field.set(employee, kycDetails);
        } else if (key.equals("bankDetails")) {
          Map<String, Object> bankDetailsMap = (Map<String, Object>) value;
          BankDetails bankDetails;
          if (employee.getBankDetails() == null) {
            bankDetails = new BankDetails();
          } else {
            bankDetails = employee.getBankDetails();
          }
          for (Map.Entry<String, Object> bankEntry : bankDetailsMap.entrySet()) {
            String bankDetailsKey = bankEntry.getKey();
            Object bankDetailsValue = bankEntry.getValue();
            if (bankDetailsValue != null) {
              Field bankFiled = BankDetails.class.getDeclaredField(bankDetailsKey);
              bankFiled.setAccessible(true);
              bankFiled.set(bankDetails, bankDetailsValue);
            }
          }
          field.set(employee, bankDetails);
        }

      } catch (NoSuchFieldException | IllegalAccessException e) {
        log.error(e.getMessage());
        throw new BadRequestException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR,
                ErrorCode.FIELD_VALIDATION_MISSING,
                Constants.IMPROPER_PAYLOAD));
      }
    }
    try {
      return employeeRepository.save(employee);
    } catch (DuplicateKeyException e) {
      String errorMessage = extractDuplicateKeyError(e);
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.CANNOT_SAVE_CHANGES, errorMessage));
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_SAVING_DETAILS));
    }
  }

  @Override
  public Employee uploadOrUpdateProfilePic(MultipartFile file, String employeeId) throws Exception {
    log.info(
        "Entering into profile pic upload. Logged In Employee: {}, Employee ID who's photo is updated: {},"
            + " Organization Id: {}",
        UserContext.getLoggedInEmployeeId(),
        employeeId,
        UserContext.getLoggedInUserOrganization().getId());
    if (!(UserContext.getLoggedInUserPermissions()
                .contains(PermissionConstants.UPDATE_PROFILE_PIC_SELF)
            && UserContext.getLoggedInEmployeeId().equals(employeeId))
        && !(UserContext.getLoggedInUserPermissions()
            .contains(PermissionConstants.UPDATE_PROFILE_PIC_ALL))) {
      log.info(
          UNAUTHORISED_TO_UPDATE_PROFILE_PIC
              + " Logged In Employee: {}, Employee ID who's photo is updated: {}, Organization Id: {}",
          UserContext.getLoggedInEmployeeId(),
          employeeId,
          UserContext.getLoggedInUserOrganization().getId());
      throw new UnAuthorisedException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR, ErrorCode.PERMISSION_MISSING, UNAUTHORISED_ACCESS));
    }
    String filename = file.getOriginalFilename();
    if (file.isEmpty() || filename == null || filename.isEmpty()) {
      throw new ResourceNotFound(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.FILE_NOT_FOUND,
              INVALID_PROFILE_PIC_FORMATS));
    } else {
      String fileExtension = getFileExtension(filename).toLowerCase();
      List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");

      if (!allowedExtensions.contains(fileExtension)) {
        throw new BadRequestException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR,
                ErrorCode.FIELD_VALIDATION_MISSING,
                INVALID_PROFILE_PIC_FORMATS));
      }
    }

    Employee employee =
        employeeRepository.findByEmployeeIdAndOrganizationId(
            employeeId, UserContext.getLoggedInUserOrganization().getId());
    if (employee == null) {
      throw new ResourceNotFound(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR, ErrorCode.USER_NOT_FOUND, EMPLOYEE_NOT_FOUND));
    }
    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(file);
    fileUploadRequest.setEntityId(employeeId);
    fileUploadRequest.setFileType("ProfilePicture");
    fileUploadRequest.setName(filename);
    File response = fileService.uploadOrUpdateFile(fileUploadRequest);
    employee.setProfilePictureId(response.getId());

    try {
      employeeRepository.save(employee);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.SOMETHING_WENT_WRONG,
              Constants.ERROR_IN_SAVING_DETAILS));
    }
    log.info(
        SUCCESSFULLY_UPDATED_PROFILE_PHOTO
            + " Logged In Employee: {}, Employee ID who's photo is updated: {}, Organization Id: {}",
        UserContext.getLoggedInEmployeeId(),
        employeeId,
        UserContext.getLoggedInUserOrganization().getId());
    return employee;
  }

  @Override
  public EmployeeValues getEmployeeValues() throws Exception {
    EmployeeValues employeeValues = new EmployeeValues();
    List<EmployeeDefaultValues> employeeDefaultValues = employeeRepository.findDistinctTypeByOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    Set<String> departments = employeeDefaultValues.stream().map(EmployeeDefaultValues::getDepartment).collect(Collectors.toSet());
    Set<String> designations = employeeDefaultValues.stream().map(EmployeeDefaultValues::getDesignation).collect(Collectors.toSet());
    Set<String> employmentTypes = employeeDefaultValues.stream().map(EmployeeDefaultValues::getEmploymentType).collect(Collectors.toSet());

    employeeValues.setDepartments(departments);
    employeeValues.setDesignations(designations);
    employeeValues.setEmploymentTypes(employmentTypes);
    return employeeValues;
  }

  private String extractDuplicateKeyError(DuplicateKeyException e) {
    if (e.getMessage().contains("duplicate key error")) {
      if (e.getMessage().contains("kycDetails.aadhaarNumber")) {
        return "Duplicate Aadhaar number";
      } else if (e.getMessage().contains("kycDetails.panNumber")) {
        return "Duplicate PAN number";
      } else if (e.getMessage().contains("kycDetails.passportNumber")) {
        return "Duplicate Passport number";
      }
    }
    return "Duplicate entry found.";
  }
}
