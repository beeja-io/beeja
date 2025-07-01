package com.beeja.api.employeemanagement.utils;

public class ExtractEmpNumUtil {
  public static int extractEmpNumber(String empId) {

    String empNumber = empId.replaceAll("\\D", "");

    if (empNumber.isEmpty()) {
      throw new NumberFormatException(Constants.NO_NUMERIC_FOUND + empId);
    }

    return Integer.parseInt(empNumber);
  }
}
