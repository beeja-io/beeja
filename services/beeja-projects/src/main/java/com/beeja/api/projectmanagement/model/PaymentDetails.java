package com.beeja.api.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
    String accountName;
    String bankName;
    String accountNumber;
    String ifscNumber;
}
