package com.beeja.api.accounts.serviceImpl;

import static com.beeja.api.accounts.utils.SecretsGenerator.hashWithBcrypt;

import com.beeja.api.accounts.clients.EmployeeFeignClient;
import com.beeja.api.accounts.constants.PermissionConstants;
import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.CustomAccessDenied;
import com.beeja.api.accounts.exceptions.ResourceAlreadyFoundException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.exceptions.UserNotFoundException;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.beeja.api.accounts.model.User;
import com.beeja.api.accounts.model.dto.EmployeeNameDTO;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.repository.OrganizationPatternsRepository;
import com.beeja.api.accounts.repository.RolesRepository;
import com.beeja.api.accounts.repository.UserRepository;
import com.beeja.api.accounts.requests.AddEmployeeRequest;
import com.beeja.api.accounts.requests.ChangeEmailAndPasswordRequest;
import com.beeja.api.accounts.requests.UpdateUserRequest;
import com.beeja.api.accounts.requests.UpdateUserRoleRequest;
import com.beeja.api.accounts.response.CreatedUserResponse;
import com.beeja.api.accounts.response.EmployeeCount;
import com.beeja.api.accounts.service.EmployeeService;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.SecretsGenerator;
import com.beeja.api.accounts.utils.UserContext;
import com.beeja.api.accounts.utils.methods.ServiceMethods;
import feign.FeignException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired UserRepository userRepository;

  @Autowired private EmployeeFeignClient employeeFeignClient;

  @Autowired RolesRepository rolesRepository;

  @Autowired MongoTemplate mongoTemplate;

  @Autowired OrgDefaultsRepository orgDefaultsRepository;

  @Autowired OrganizationPatternsRepository patternsRepository;

  private final PasswordEncoder passwordEncoder;

  public EmployeeServiceImpl(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public CreatedUserResponse createEmployee(AddEmployeeRequest addEmployeeRequest)
      throws Exception {
    User user = new User();
    user.setFirstName(addEmployeeRequest.getFirstName());
    user.setLastName(addEmployeeRequest.getLastName());
    user.setEmail(addEmployeeRequest.getEmail());
    user.setEmploymentType(addEmployeeRequest.getEmploymentType());

    String userEmail = user.getEmail();
    if (userRepository.findByEmailAndOrganizations(
            userEmail, UserContext.getLoggedInUserOrganization())
        != null) {
      throw new ResourceAlreadyFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_EXISTS_ERROR,
              ErrorCode.EMPLOYEE_ALREADY_FOUND,
              Constants.USER_ALREADY_FOUND + userEmail));
    }

    //    Checking for Employment Type
    OrgDefaults orgDefaults =
        orgDefaultsRepository.findByOrganizationIdAndKey(
            UserContext.getLoggedInUserOrganization().getId(), "employeeTypes");

    if (orgDefaults == null
        || orgDefaults.getValues() == null
        || orgDefaults.getValues().isEmpty()) {
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.INVALID_EMPLOYMENT_TYPE_CODE,
              Constants.NO_EMPLOYEE_TYPES_DEFINED));
    }

    boolean isEmploymentTypeValid =
        orgDefaults.getValues().stream()
            .anyMatch(
                value ->
                    value.getValue() != null
                        && value.getValue().equalsIgnoreCase(user.getEmploymentType()));

    if (!isEmploymentTypeValid) {
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.VALIDATION_ERROR,
              ErrorCode.INVALID_EMPLOYMENT_TYPE_CODE,
              Constants.INVALID_EMPLOYMENT_TYPE + user.getEmploymentType()));
    }

    //    OrganizationPattern organizationPattern =
    //        patternsRepository.findByOrganizationIdAndPatternTypeAndActive(
    //            UserContext.getLoggedInUserOrganization().getId(),
    //            String.valueOf(PatternType.EMPLOYEE_ID_PATTERN),
    //            true);

    //    long userCount =
    //
    // userRepository.countByOrganizationId(UserContext.getLoggedInUserOrganization().getId());
    //    int sequenceNumber = (int) (userCount + 1);
    //
    //    int numberLength =
    //        organizationPattern.getPatternLength() - organizationPattern.getPrefix().length();
    //    String formattedSequence = String.format("%0" + numberLength + "d", sequenceNumber);
    //
    //    String autoGeneratedEmployeeId = organizationPattern.getPrefix().toUpperCase() +
    // formattedSequence;
    user.setEmployeeId(addEmployeeRequest.getEmployeeId().toUpperCase());
    Set<Role> userRoles = new HashSet<>();

    try {
      List<Role> roles =
          rolesRepository.findAllByOrganizationIdAndIsDefaultRole(
              UserContext.getLoggedInUserOrganization().getId(), true);
      if (roles != null) {
        userRoles.addAll(roles);
      }
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.ERROR_ASSIGNING_ROLE,
              Constants.ERROR_IN_ASSIGNING_ROLE));
    }
    user.setRoles(userRoles);
    user.setCreatedBy(UserContext.getLoggedInUserEmail());
    user.setOrganizations(UserContext.getLoggedInUserOrganization());

    String password = SecretsGenerator.generateSecret();
    String hashedPassword = hashWithBcrypt(password);
    user.setPassword(hashedPassword);

    User createdUser;
    try {
      createdUser = userRepository.save(user);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR, ErrorCode.RESOURCE_CREATING_ERROR, Constants.USER_CREATE_ERROR));
    }
    try {
      Map<String, Object> newEmployee = new HashMap<>();
      newEmployee.put("employeeId", createdUser.getEmployeeId());
      newEmployee.put("email", createdUser.getEmail());
      newEmployee.put("organizations", createdUser.getOrganizations());
      newEmployee.put("department", addEmployeeRequest.getDepartment());
      employeeFeignClient.createEmployee(newEmployee);
    } catch (FeignException.FeignClientException e) {
      userRepository.delete(createdUser);
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.RESOURCE_CREATING_ERROR,
              Constants.EMPLOYEE_FEIGN_CLIENT_ERROR));
    }
    CreatedUserResponse createdUserResponse = new CreatedUserResponse();
    createdUserResponse.setPassword(password);
    createdUserResponse.setUser(createdUser);
    return createdUserResponse;
  }

  @Override
  public void changeEmployeeStatus(String employeeId) throws Exception {
    employeeId = employeeId.toUpperCase();
    User optionalUser =
        userRepository.findByEmployeeIdAndOrganizations(
            employeeId, UserContext.getLoggedInUserOrganization());
    if (optionalUser == null) {
      throw new ResourceAlreadyFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.USER_NOT_FOUND,
              Constants.USER_NOT_FOUND + employeeId));
    }

    if (Objects.equals(optionalUser.getEmployeeId(), UserContext.getLoggedInEmployeeId())) {
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.CANNOT_CHANGE_SELF_STATUS,
              Constants.CANT_INACTIVE_SELF));
    }

    optionalUser.setActive(!optionalUser.isActive());
    try {
      userRepository.save(optionalUser);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR, ErrorCode.CANNOT_SAVE_CHANGES, Constants.USER_UPDATE_ERROR));
    }
  }

  @Override
  public List<EmployeeNameDTO> getEmployeeNamesByIds(List<String> employeeIds) {
    return userRepository.findEmployeeNamesByEmployeeIdInAndOrganizations_Id(
        employeeIds, UserContext.getLoggedInUserOrganization().getId());
  }

  @Override
  public List<User> getAllEmployees() throws Exception {
    try {
      if (UserContext.getLoggedInUserPermissions()
          .contains(PermissionConstants.GET_ALL_EMPLOYEES)) {
        return userRepository.findByOrganizationsId(
            UserContext.getLoggedInUserOrganization().getId());
      } else {
        return userRepository.findByOrganizationsAndIsActive(
            UserContext.getLoggedInUserOrganization(), true);
      }
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_RETRIEVING_USER));
    }
  }

  @Override
  public User getEmployeeByEmail(String email, Organization organization) throws Exception {
    User user;
    try {
      user = userRepository.findByEmailAndOrganizations(email, organization);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.UNABLE_TO_FETCH_DETAILS_FROM_DATABASE));
    }
    if (user == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.USER_NOT_FOUND,
              Constants.USER_NOT_FOUND + email));
    }
    return user;
  }

  @Override
  public User getEmployeeByEmployeeId(String employeeId, Organization organization)
      throws Exception {
    User user;
    try {
      user = userRepository.findByEmployeeIdAndOrganizations(employeeId, organization);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_RETRIEVING_USER + e.getMessage()));
    }

    if (user == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.USER_NOT_FOUND,
              Constants.USER_NOT_FOUND));
    }
    return user;
  }

  @Override
  public User updateEmployeeRolesDyEmployeeId(String empId, UpdateUserRoleRequest updateRequest)
      throws Exception {
    User user;
    try {
      user =
          userRepository.findByEmployeeIdAndOrganizations(
              empId, UserContext.getLoggedInUserOrganization());
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_RETRIEVING_USER));
    }

    if (user == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.USER_NOT_FOUND,
              Constants.USER_NOT_FOUND + empId));
    }

    if (Objects.equals(user.getEmployeeId(), UserContext.getLoggedInEmployeeId())) {
      throw new BadRequestException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.AUTHORIZATION_ERROR,
              ErrorCode.CANNOT_CHANGE_SELF_ROLES,
              Constants.CANT_UPDATE_ROLES_SELF));
    }
    Set<Role> updatedRoles = new HashSet<>();
    for (String role : updateRequest.getRoles()) {
      Role roleToBeAddedToEmployee =
          rolesRepository.findByNameAndOrganizationId(
              role, UserContext.getLoggedInUserOrganization().getId());
      if (roleToBeAddedToEmployee != null) {
        updatedRoles.add(roleToBeAddedToEmployee);
      } else {
        throw new ResourceNotFoundException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND_ERROR,
                ErrorCode.ROLE_NOT_FOUND,
                Constants.ROLE_NOT_FOUND + role));
      }
    }
    user.setRoles(updatedRoles);
    try {
      return userRepository.save(user);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.CANNOT_SAVE_CHANGES,
              Constants.ERROR_IN_ASSIGNING_ROLE));
    }
  }

  @Override
  public User updateEmployeeByEmployeeId(String employeeId, UpdateUserRequest updatedUser) {
    User existingUser =
        userRepository.findByEmployeeIdAndOrganizations(
            employeeId, UserContext.getLoggedInUserOrganization());

    if (existingUser == null) {
      throw new ResourceNotFoundException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.RESOURCE_NOT_FOUND_ERROR,
              ErrorCode.USER_NOT_FOUND,
              Constants.USER_NOT_FOUND + employeeId));
    }

    String[] nullProperties = ServiceMethods.getNullPropertyNames(updatedUser);
    BeanUtils.copyProperties(updatedUser, existingUser, nullProperties);
    existingUser.setModifiedAt(new Date());
    existingUser.setModifiedBy(UserContext.getLoggedInUserEmail());
    return userRepository.save(existingUser);
  }

  @Override
  public List<User> getUsersByPermissionAndOrganization(String permission) throws Exception {
    try {
      Query roleQuery = new Query();
      roleQuery.addCriteria(
          Criteria.where("permissions")
              .in(permission.toUpperCase())
              .and("organizationId")
              .is(UserContext.getLoggedInUserOrganization().getId()));
      roleQuery.fields().include("permissions");
      List<Role> roles = mongoTemplate.find(roleQuery, Role.class);
      Set<User> userSet = new HashSet<>();

      for (Role role : roles) {
        Query userQuery = new Query();
        userQuery.addCriteria(
            Criteria.where("roles")
                .in(role.getId())
                .and("isActive")
                .is(true)
                .and("organizations")
                .is(UserContext.getLoggedInUserOrganization()));
        userQuery
            .fields()
            .include("firstName")
            .include("lastName")
            .include("employeeId")
            .include("email");
        userSet.addAll(mongoTemplate.find(userQuery, User.class));
      }
      return new ArrayList<>(userSet);
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_RETRIEVING_USER));
    }
  }

  @Override
  public EmployeeCount getEmployeeCountByOrganization() throws Exception {
    try {
      Long totalCount = 0L;
      if (UserContext.getLoggedInUserPermissions()
          .contains(PermissionConstants.GET_ALL_EMPLOYEES)) {
        totalCount = userRepository.countByOrganizations(UserContext.getLoggedInUserOrganization());
      }
      Long activeEmployeeCount =
          userRepository.countByOrganizationsAndIsActive(
              UserContext.getLoggedInUserOrganization(), true);
      EmployeeCount employeeCount = new EmployeeCount();
      employeeCount.setTotalCount(totalCount);
      employeeCount.setActiveCount(activeEmployeeCount);
      if (UserContext.getLoggedInUserPermissions()
          .contains(PermissionConstants.GET_ALL_EMPLOYEES)) {
        employeeCount.setInactiveCount(totalCount - activeEmployeeCount);
      }
      return employeeCount;
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_FETCHING_EMPLOYEE_COUNT));
    }
  }

  @Override
  public boolean isEmployeeHasPermission(String employeeId, String permission) throws Exception {
    try {
      User user =
          userRepository.findByEmployeeIdAndOrganizations(
              employeeId, UserContext.getLoggedInUserOrganization());
      if (user == null) {
        throw new UserNotFoundException(Constants.USER_NOT_FOUND + employeeId);
      }
      for (Role role : user.getRoles()) {
        if (role.getPermissions().contains(permission)) {
          return true;
        }
      }
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_IN_CHECKING_PERMISSION));
    }
    return false;
  }

  @Override
  public List<User> getUsersByEmployeeIds(List<String> employeeIds) throws Exception {
    try {
      if (UserContext.getLoggedInUserPermissions().contains(PermissionConstants.READ_EMPLOYEE)) {
        String organizationId = UserContext.getLoggedInUserOrganization().getId();
        return userRepository.findByEmployeeIdInAndOrganizations_Id(employeeIds, organizationId);
      } else {
        throw new CustomAccessDenied(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.AUTHORIZATION_ERROR,
                ErrorCode.PERMISSION_MISSING,
                Constants.NO_REQUIRED_PERMISSIONS));
      }
    } catch (Exception e) {
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.DB_ERROR,
              ErrorCode.UNABLE_TO_FETCH_DETAILS,
              Constants.ERROR_RETRIEVING_USER));
    }
  }

  @Override
  public String changeEmailAndPassword(
      ChangeEmailAndPasswordRequest changeEmailAndPasswordRequest) {
    User user =
        userRepository.findByEmailAndOrganizations(
            UserContext.getLoggedInUserEmail(), UserContext.getLoggedInUserOrganization());
    if (changeEmailAndPasswordRequest.getCurrentPassword() != null
        && changeEmailAndPasswordRequest.getNewPassword() != null
        && changeEmailAndPasswordRequest.getConfirmPassword() != null) {
      if (!passwordEncoder.matches(
          changeEmailAndPasswordRequest.getCurrentPassword(), user.getPassword())) {
        throw new BadRequestException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR,
                ErrorCode.BAD_REQUEST,
                Constants.CURRENT_PASSWORD_IS_INCORRECT));
      }

      if (!changeEmailAndPasswordRequest
          .getNewPassword()
          .equals(changeEmailAndPasswordRequest.getConfirmPassword())) {
        throw new BadRequestException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR,
                ErrorCode.BAD_REQUEST,
                Constants.NEW_PASSWORD_MUST_MATCH_CONFIRMATION_PASSWORD));
      }
      String hashedNewPassword =
          passwordEncoder.encode(changeEmailAndPasswordRequest.getNewPassword());
      user.setPassword(hashedNewPassword);
    }
    if (changeEmailAndPasswordRequest.getNewEmail() != null) {
      user.setEmail(changeEmailAndPasswordRequest.getNewEmail());
    }
    userRepository.save(user);
    return Constants.UPDATED;
  }
}
