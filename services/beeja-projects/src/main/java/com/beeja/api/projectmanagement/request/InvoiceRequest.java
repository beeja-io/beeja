package com.beeja.api.projectmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
}
