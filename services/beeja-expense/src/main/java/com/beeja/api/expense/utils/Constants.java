package com.beeja.api.expense.utils;

public class Constants {

  //    PERMISSIONS
  public static final String CREATE_EXPENSE = "CEX";
  public static final String READ_EXPENSE = "REX";
  public static final String UPDATE_EXPENSE = "UEX";
  public static final String DELETE_EXPENSE = "DEX";

  //    File Entity Types;
  public static final String EXPENSE_ENTITY_TYPE = "expense";

  //    Errors
  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  public static final String NO_REQUIRED_PERMISSIONS =
      "You have no required permissions to do this operation";
  public static final String FILE_COUNT_ERROR =
      "At least one receipt is required & Files cannot be more than ";
  public static final String ERROR_SAVING_FILE_IN_FILE_SERVICE =
      "Error Occcured while saving the file";
  public static final String ERROR_SAVING_EXPENSE =
      "Error happened while saving expense in beeja cloud";
  public static final String INVALID_FILE_FORMATS =
      "Error Occurred, We just accepting files with extensions .png, .jpeg, .pdf ";

  //    VALIDATION MESSAGES

  public static final String EXPENSE_NOT_FOUND = "Expense Not Found ";
  public static final String ERROR_RETRIEVING_EXPENSE = "Error In Retrieving Expense ";
  public static final String UNAUTHORISED_ACCESS_ERROR = "You have no permission to access";
  public static final String SERVICE_DOWN_ERROR = "Something went wrong in our system";
  public static final String UNAUTHORISED_ACCESS = "NO REQUIRED PERMISSIONS - CODE: EXP_SER";
  public static final String SETTLED = "Expense is already settled";
  public static final String STATUS_SETTLED = "Settled";
  public static final String EXPENSE_STATUS = "Expense Status";
}
