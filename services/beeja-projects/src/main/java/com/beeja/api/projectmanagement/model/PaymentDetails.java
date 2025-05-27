package com.beeja.api.projectmanagement.model;

import lombok.Data;

@Data
public class PaymentDetails {
    String accountName;
    String bankName;
    String accountNumber;
    String ifscNumber;
}
