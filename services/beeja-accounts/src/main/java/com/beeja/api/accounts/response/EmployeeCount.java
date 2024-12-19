package com.beeja.api.accounts.response;

import lombok.Data;

@Data
public class EmployeeCount {
  private Long totalCount = 0L;
  private Long activeCount = 0L;
  private Long inactiveCount = 0L;
}
