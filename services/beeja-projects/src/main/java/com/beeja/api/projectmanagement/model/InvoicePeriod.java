package com.beeja.api.projectmanagement.model;

import java.util.Date;
import lombok.Data;

@Data
public class InvoicePeriod {
  private Date startDate;
  private Date endDate;
}
