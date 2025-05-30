package com.beeja.api.accounts.model.Organization;

import lombok.Data;

@Data
public class BankDetails {
    String accountName;
    String bankName;
    String accountNumber;
    String ifscNumber;
}
