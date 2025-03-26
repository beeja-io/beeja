package com.beeja.api.accounts.enums;


public enum OrganizationDefaults {
  DEPARTMENTS("departments"),
  JOB_TITLES("jobTitles"),
  EMPLOYMENT_TYPE("employmentTypes"),
  EXPENSE_CATEGORY("expenseCategories"),
  EXPENSE_TYPE("expenseTypes"),
  PAYMENT_MODE("paymentMode");

  private final String value;

  OrganizationDefaults(String value) {
    this.value=value;
  }
  public String getValue() {
    return value;
  }




}
