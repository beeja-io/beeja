package com.beeja.api.projectmanagement.request;

import com.beeja.api.projectmanagement.model.InvoicePeriod;
import com.beeja.api.projectmanagement.model.Task;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
  private String contractId;
  private Date billingDate;
  private Date dueDate;
  private Double amount;
  private String currency;
  private List<String> notes;
  private List<Task> tasks;
  private String clientId;
  private String projectId;
  private String remittanceRef;
  private int vat;
  private String daysLeftForPayment;
  private InvoicePeriod invoicePeriod;
}
