package com.beeja.api.expense.serviceImpl;

import static com.beeja.api.expense.utils.Constants.ERROR_SAVING_EXPENSE;
import static com.beeja.api.expense.utils.Constants.ERROR_SAVING_FILE_IN_FILE_SERVICE;
import static com.beeja.api.expense.utils.Constants.EXPENSE_NOT_FOUND;
import static com.beeja.api.expense.utils.Constants.FILE_COUNT_ERROR;
import static com.beeja.api.expense.utils.Constants.INVALID_FILE_FORMATS;
import static com.beeja.api.expense.utils.Constants.SERVICE_DOWN_ERROR;

import com.beeja.api.expense.client.FileClient;
import com.beeja.api.expense.config.properties.AllowedContentTypes;
import com.beeja.api.expense.enums.ErrorCode;
import com.beeja.api.expense.enums.ErrorType;
import com.beeja.api.expense.exceptions.ExpenseAlreadySettledException;
import com.beeja.api.expense.exceptions.ExpenseNotFound;
import com.beeja.api.expense.exceptions.OrganizationMismatchException;
import com.beeja.api.expense.exceptions.handleInternalServerException;
import com.beeja.api.expense.modal.Expense;
import com.beeja.api.expense.modal.File;
import com.beeja.api.expense.repository.ExpenseRepository;
import com.beeja.api.expense.requests.CreateExpense;
import com.beeja.api.expense.requests.ExpenseUpdateRequest;
import com.beeja.api.expense.requests.FileRequest;
import com.beeja.api.expense.response.ExpenseDefaultValues;
import com.beeja.api.expense.response.ExpenseValues;
import com.beeja.api.expense.service.ExpenseService;
import com.beeja.api.expense.utils.BuildErrorMessage;
import com.beeja.api.expense.utils.Constants;
import com.beeja.api.expense.utils.UserContext;
import com.beeja.api.expense.utils.helpers.FileExtensionHelpers;
import com.beeja.api.expense.utils.methods.ServiceMethods;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
  @Autowired ExpenseRepository expenseRepository;

  @Autowired FileClient fileClient;

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private AllowedContentTypes allowedContentTypes;

  @Override
  public Expense deleteExpense(String expenseId) throws Exception {
    try {
      String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();
      Optional<Expense> expense =
          expenseRepository.findByOrganizationIdAndId(organizationId, expenseId);
      if (expense.isPresent()) {
        if (expense.get().getFileId() != null) {
          for (String fileId : expense.get().getFileId()) {
            fileClient.deleteFile(fileId);
          }
        }
        expenseRepository.deleteById(expenseId);
        return expense.get();
      } else {
        log.error(EXPENSE_NOT_FOUND + expenseId);
        throw new ExpenseNotFound(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND,
                ErrorCode.EXPENSE_NOT_FOUND,
                EXPENSE_NOT_FOUND + expenseId));
      }
    } catch (ExpenseNotFound e) {
      throw e;
    } catch (Exception e) {
      log.error(SERVICE_DOWN_ERROR, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.API_ERROR, ErrorCode.SERVER_ERROR, SERVICE_DOWN_ERROR));
    }
  }

  @Override
  public Expense updateExpense(String expenseId, ExpenseUpdateRequest updatedExpense)
      throws Exception {
    try {
      Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);

      int finalFileCount = 0;
      // Checking if request has files & more than 3
      // Checking the extensions of files
      if (updatedExpense.getNewFiles() != null && updatedExpense.getDeleteFileId() != null) {
        finalFileCount =
            Math.abs(
                updatedExpense.getDeleteFileId().size()
                    + updatedExpense.getNewFiles().size()
                    - optionalExpense.get().getFileId().size());
        for (MultipartFile file : updatedExpense.getNewFiles()) {
          if (!FileExtensionHelpers.isValidContentType(
              file.getContentType(), allowedContentTypes.getAllowedTypes())) {
            log.error(INVALID_FILE_FORMATS);
            throw new Exception(
                BuildErrorMessage.buildErrorMessage(
                    ErrorType.VALIDATION_ERROR,
                    ErrorCode.INVALID_FILE_FORMAT,
                    INVALID_FILE_FORMATS));
          }
        }
      } else if (updatedExpense.getNewFiles() != null
          && optionalExpense.isPresent()
          && optionalExpense.get().getFileId() != null) {
        finalFileCount =
            Math.abs(
                updatedExpense.getNewFiles().size() + optionalExpense.get().getFileId().size());
      }
      if (finalFileCount > 3) {
        log.error(FILE_COUNT_ERROR + 3);
        throw new Exception(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.VALIDATION_ERROR, ErrorCode.FILE_COUNT_EXCEED, FILE_COUNT_ERROR + 3));
      }

      String loggedInUserOrganizationId =
          (String) UserContext.getLoggedInUserOrganization().get("id");
      if (optionalExpense.isPresent()
          && loggedInUserOrganizationId.equals(optionalExpense.get().getOrganizationId())) {

        Expense existingExpense = optionalExpense.get();
        String[] nullProperties = ServiceMethods.getNullPropertyNames(updatedExpense);

        BeanUtils.copyProperties(updatedExpense, existingExpense, nullProperties);
        List<String> fileIds = optionalExpense.get().getFileId();

        if (fileIds == null) {
          fileIds = new ArrayList<>();
        }
        List<String> deleteFiles = updatedExpense.getDeleteFileId();
        if (deleteFiles != null) {
          for (String fileId : deleteFiles) {
            if (fileIds.contains(fileId)
                && fileClient.deleteFile(fileId).getStatusCode().is2xxSuccessful()) {
              fileIds.remove(fileId);
              Optional<File> fileToRemove =
                  existingExpense.getFiles().stream()
                      .filter(file -> fileId.equals(file.getId()))
                      .findFirst();
              fileToRemove.ifPresent(existingExpense.getFiles()::remove);
            }
          }
        }
        List<MultipartFile> addedFiles = updatedExpense.getNewFiles();
        if (addedFiles != null) {
          for (MultipartFile file : addedFiles) {
            FileRequest fileUploadRequest = new FileRequest();
            fileUploadRequest.setFile(file);
            fileUploadRequest.setEntityType(Constants.EXPENSE_ENTITY_TYPE);
            ResponseEntity<?> fileResponse = fileClient.uploadFile(fileUploadRequest);
            if (fileResponse.getStatusCode().is2xxSuccessful()) {
              Map<String, Object> responseBody = (Map<String, Object>) fileResponse.getBody();
              fileIds.add((String) responseBody.get("id"));
              String fileId = responseBody.get("id").toString();
              String fileName = responseBody.get("name").toString();
              File newFile = new File(fileId, fileName);
              List<File> files = existingExpense.getFiles();
              if (files == null) {
                files = new ArrayList<>();
              }
              files.add(newFile);
              existingExpense.setFiles(files);
              fileIds.add(fileId);
            }
          }
        }

        if ("No Payment Settled".equals(updatedExpense.getPaymentSettled())) {
          existingExpense.setPaymentSettled(null);
        }

        if (updatedExpense.getExpenseDate() != null) {
          existingExpense.setExpenseDate(convertToUTCDate(updatedExpense.getExpenseDate()));
        }

        if (updatedExpense.getPaymentSettled() != null
            && !"No Payment Settled".equals(updatedExpense.getPaymentSettled())) {
          existingExpense.setPaymentSettled(convertToUTCDate(updatedExpense.getPaymentSettled()));
        }

        if (updatedExpense.getRequestedDate() != null) {
          existingExpense.setRequestedDate(convertToUTCDate(updatedExpense.getRequestedDate()));
        }
        existingExpense.setModifiedBy(UserContext.getLoggedInUserEmail());
        existingExpense.setId(expenseId);
        existingExpense.setFileId(fileIds);
        return expenseRepository.save(existingExpense);
      } else {
        log.error(Constants.EXPENSE_NOT_FOUND_ORGANIZATION + expenseId);
        throw new OrganizationMismatchException(
            BuildErrorMessage.buildErrorMessage(
                ErrorType.RESOURCE_NOT_FOUND,
                ErrorCode.EXPENSE_NOT_FOUND,
                Constants.EXPENSE_NOT_FOUND_ORGANIZATION + expenseId));
      }
    } catch (Exception e) {
      log.error(Constants.ERROR_UPDATING_EXPENSE + e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INTERNAL_SERVER_ERROR,
              ErrorCode.EXPENSE_UPDATE_FAILED,
              Constants.ERROR_UPDATING_EXPENSE));
    }
  }

  @Override
  public Expense createExpense(CreateExpense createExpense) throws Exception {

    /*
     * NOTE:
     * Currently the number of files is limited to 3
     * */
    if (createExpense.getFiles() != null && createExpense.getFiles().size() > 3) {
      log.error(FILE_COUNT_ERROR + 3);
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.VALIDATION_ERROR, ErrorCode.FILE_COUNT_EXCEED, FILE_COUNT_ERROR + 3));
    }

    //        Checking the extensions of files
    if (createExpense.getFiles() != null) {
      for (MultipartFile file : createExpense.getFiles()) {
        if (!FileExtensionHelpers.isValidContentType(
            file.getContentType(), allowedContentTypes.getAllowedTypes())) {
          log.error(INVALID_FILE_FORMATS);
          throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.VALIDATION_ERROR, ErrorCode.INVALID_FILE_FORMAT, INVALID_FILE_FORMATS));
        }
      }
    }

    Expense newExpense = new Expense();
    newExpense.setAmount(createExpense.getAmount());
    newExpense.setCurrencyCode(createExpense.getCurrencyCode());
    newExpense.setType(createExpense.getType());
    newExpense.setCategory(createExpense.getCategory());
    newExpense.setModeOfPayment(createExpense.getModeOfPayment());
    newExpense.setMerchant(createExpense.getMerchant());
    newExpense.setClaimed(createExpense.isClaimed());
    newExpense.setPaymentMadeBy(createExpense.getPaymentMadeBy());
    newExpense.setDescription(createExpense.getDescription());
    newExpense.setDepartment(createExpense.getDepartment());

    if ("null".equals(newExpense.getStatus())) {
      newExpense.setStatus("Pending");
    }

    if (createExpense.getExpenseDate() != null) {
      newExpense.setExpenseDate(convertToUTCDate(createExpense.getExpenseDate()));
    }

    if (createExpense.getPaymentSettled() != null) {
      newExpense.setPaymentSettled(convertToUTCDate(createExpense.getPaymentSettled()));
    }

    if (createExpense.getRequestedDate() != null) {
      newExpense.setRequestedDate(convertToUTCDate(createExpense.getRequestedDate()));
    }

    if (createExpense.getFiles() != null) {
      for (MultipartFile multipartFile : createExpense.getFiles()) {
        FileRequest file = new FileRequest();
        file.setFile(multipartFile);
        try {
          ResponseEntity<?> response = fileClient.uploadFile(file);
          Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
          String fileId = responseBody.get("id").toString();
          String fileName = responseBody.get("name").toString();
          if (newExpense.getFileId() == null) {
            newExpense.setFileId(new ArrayList<>());
          }
          if (newExpense.getFiles() == null) {
            newExpense.setFiles(new ArrayList<>());
          }
          newExpense.getFileId().add(fileId);
          newExpense.getFiles().add(new File(fileId, fileName));
        } catch (Exception e) {
          //              Deleting existing files if expense is failed to create
          if (newExpense.getFileId() != null) {
            for (String fileId : newExpense.getFileId()) {
              fileClient.deleteFile(fileId);
            }
          }
          log.error(ERROR_SAVING_FILE_IN_FILE_SERVICE, e.getMessage());
          throw new Exception(
              BuildErrorMessage.buildErrorMessage(
                  ErrorType.INTERNAL_SERVER_ERROR,
                  ErrorCode.ERROR_SAVING_FILE,
                  ERROR_SAVING_FILE_IN_FILE_SERVICE));
        }
      }
    }
    try {
      return expenseRepository.save(newExpense);
    } catch (Exception e) {
      log.error(ERROR_SAVING_EXPENSE, e.getMessage());
      throw new Exception(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INTERNAL_SERVER_ERROR,
              ErrorCode.EXPENSE_SAVED_FAILED,
              ERROR_SAVING_EXPENSE));
    }
  }

  @Override
  public Expense getExpenseById(String expenseId) throws Exception {
    return expenseRepository
        .findById(expenseId)
        .orElseThrow(
            () -> {
              String errorMessage = EXPENSE_NOT_FOUND + expenseId;
              log.error(errorMessage);
              throw new ExpenseNotFound(
                  BuildErrorMessage.buildErrorMessage(
                      ErrorType.RESOURCE_NOT_FOUND, ErrorCode.EXPENSE_NOT_FOUND, errorMessage));
            });
  }

  @Override
  public Expense settleExpense(String expenseId) throws Exception {
    Expense expense = getExpenseById(expenseId);
    if (Constants.STATUS_SETTLED.equals(expense.getStatus())) {
      log.error(Constants.SETTLED);
      throw new ExpenseAlreadySettledException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.BAD_REQUEST, ErrorCode.EXPENSE_ALREADY_SETTLED, Constants.SETTLED));
    }
    expense.setStatus(Constants.STATUS_SETTLED);
    return expenseRepository.save(expense);
  }

  @Override
  public List<Expense> getFilteredExpenses(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      String organizationId,
      int pageNumber,
      int pageSize,
      String sortBy,
      Boolean settlementStatus,
      boolean ascending) {
    if (pageNumber <= 0 || pageSize <= 0) {
      log.error(Constants.INVALID_PAGINATION_PARAM);
      throw new IllegalArgumentException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.BAD_REQUEST,
              ErrorCode.INVALID_PAGINATION_PARAMS,
              Constants.INVALID_PAGINATION_PARAM));
    }

    try {
      Query query = new Query();

      if (startDate != null && endDate != null) {
        query.addCriteria(Criteria.where(filterBasedOn).gte(startDate).lte(endDate));
      } else if (endDate != null) {
        query.addCriteria(Criteria.where(filterBasedOn).lte(endDate));
      } else if (startDate != null) {
        query.addCriteria(Criteria.where(filterBasedOn).gte(startDate));
      }

      if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
        query.addCriteria(Criteria.where("modeOfPayment").in(modeOfPayment));
      }

      if (expenseType != null && !expenseType.isEmpty()) {
        query.addCriteria(Criteria.where("type").in(expenseType));
      }

      if (expenseCategory != null && !expenseCategory.isEmpty()) {
        query.addCriteria(Criteria.where("category").in(expenseCategory));
      }

      if (organizationId != null) {
        query.addCriteria(Criteria.where("organizationId").is(organizationId));
      }

      if (department != null && !department.isEmpty()) {
        query.addCriteria(Criteria.where("department").in(department));
      }

      if (settlementStatus != null) {
        if (settlementStatus) {
          query.addCriteria(Criteria.where("paymentSettled").ne(null));
        } else {
          query.addCriteria(Criteria.where("paymentSettled").is(null));
        }
      }

      int skip = (pageNumber - 1) * pageSize;
      query.skip(skip).limit(pageSize);

      if (sortBy != null && !sortBy.isEmpty()) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, sortBy));
      }

      return mongoTemplate.find(query, Expense.class);

    } catch (Exception e) {
      log.error(Constants.ERROR_FILTERING_EXPENSE);
      throw new handleInternalServerException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INTERNAL_SERVER_ERROR,
              ErrorCode.EXPENSE_FILTERING_FAILED,
              Constants.ERROR_FILTERING_EXPENSE));
    }
  }

  @Override
  public Double getFilteredTotalAmount(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      Boolean settlementStatus,
      String organizationId) {

    AggregationOperation matchOperation =
        Aggregation.match(
            getCriteria(
                startDate,
                endDate,
                department,
                filterBasedOn,
                modeOfPayment,
                expenseType,
                expenseCategory,
                settlementStatus,
                organizationId));
    GroupOperation groupOperation = Aggregation.group().sum("amount").as("totalAmount");

    Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

    Map result =
        mongoTemplate.aggregate(aggregation, "expenses", Map.class).getUniqueMappedResult();

    return result != null ? ((Number) result.get("totalAmount")).doubleValue() : 0.0;
  }

  @Override
  public Long getTotalExpensesSize(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      Boolean settlementStatus,
      String organizationId) {
    Query query = new Query();
    if (startDate != null && endDate != null) {
      query.addCriteria(Criteria.where(filterBasedOn).gte(startDate).lte(endDate));
    } else if (endDate != null) {
      query.addCriteria(Criteria.where(filterBasedOn).lte(endDate));
    } else if (startDate != null) {
      query.addCriteria(Criteria.where(filterBasedOn).gte(startDate));
    }

    if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
      query.addCriteria(Criteria.where("modeOfPayment").in(modeOfPayment));
    }

    if (expenseType != null && !expenseType.isEmpty()) {
      query.addCriteria(Criteria.where("type").in(expenseType));
    }

    if (expenseCategory != null && !expenseCategory.isEmpty()) {
      query.addCriteria(Criteria.where("category").in(expenseCategory));
    }

    if (organizationId != null) {
      query.addCriteria(Criteria.where("organizationId").is(organizationId));
    }

    if (department != null && !department.isEmpty()) {
      query.addCriteria(Criteria.where("department").in(department));
    }
    if (settlementStatus != null && settlementStatus.equals(true)) {
      query.addCriteria(Criteria.where("paymentSettled").ne(null));
    } else if (settlementStatus != null && settlementStatus.equals(false)) {
      query.addCriteria(Criteria.where("paymentSettled").is(null));
    }

    return mongoTemplate.count(query, Expense.class);
  }

  @Override
  public ExpenseValues getExpenseDefaultValues(String organizationId) {
    List<ExpenseDefaultValues> expenseDefaultValues =
        expenseRepository.findDistinctTypeByOrganizationId(organizationId);

    if (expenseDefaultValues.isEmpty()) {
      log.error(Constants.ERROR_FETCH_EXPENSE_DEFAULTS + organizationId);
      throw new handleInternalServerException(
          BuildErrorMessage.buildErrorMessage(
              ErrorType.INTERNAL_SERVER_ERROR,
              ErrorCode.EXPENSE_DEFAULT_FETCH_FAILED,
              Constants.ERROR_FETCH_EXPENSE_DEFAULTS + organizationId));
    }

    ExpenseValues expenseValues = new ExpenseValues();
    Set<String> categories =
        expenseDefaultValues.stream()
            .map(ExpenseDefaultValues::getCategory)
            .collect(Collectors.toSet());
    Set<String> types =
        expenseDefaultValues.stream()
            .map(ExpenseDefaultValues::getType)
            .collect(Collectors.toSet());
    Set<String> modeOfPayments =
        expenseDefaultValues.stream()
            .map(ExpenseDefaultValues::getModeOfPayment)
            .collect(Collectors.toSet());

    expenseValues.setExpenseCategories(categories);
    expenseValues.setExpenseTypes(types);
    expenseValues.setExpenseModesOfPayment(modeOfPayments);

    return expenseValues;
  }

  private Criteria getCriteria(
      Date startDate,
      Date endDate,
      List<String> department,
      String filterBasedOn,
      List<String> modeOfPayment,
      List<String> expenseType,
      List<String> expenseCategory,
      Boolean settlementStatus,
      String organizationId) {
    Criteria criteria = new Criteria();

    if (startDate != null && endDate != null) {
      criteria.andOperator(Criteria.where(filterBasedOn).gte(startDate).lte(endDate));
    } else if (endDate != null) {
      criteria.and(filterBasedOn).lte(endDate);
    } else if (startDate != null) {
      criteria.and(filterBasedOn).gte(startDate);
    }

    if (modeOfPayment != null && !modeOfPayment.isEmpty()) {
      criteria.and("modeOfPayment").in(modeOfPayment);
    }

    if (expenseType != null && !expenseType.isEmpty()) {
      criteria.and("type").in(expenseType);
    }

    if (expenseCategory != null && !expenseCategory.isEmpty()) {
      criteria.and("category").in(expenseCategory);
    }

    if (organizationId != null) {
      criteria.and("organizationId").is(organizationId);
    }

    if (department != null && !department.isEmpty()) {
      criteria.and("department").in(department);
    }

    if (settlementStatus != null && settlementStatus.equals(true)) {
      criteria.and("paymentSettled").ne(null);
    } else if (settlementStatus != null && settlementStatus.equals(false)) {
      criteria.and("paymentSettled").is(null);
    }
    return criteria;
  }

  private static Date convertToUTCDate(String inputDateString) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat.parse(inputDateString);
  }
}
