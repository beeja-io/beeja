package com.beeja.api.projectmanagement.model;

import lombok.Data;

import java.util.Date;

@Data
public class InvoicePeriod {
    private Date startDate;
    private Date endDate;
}
