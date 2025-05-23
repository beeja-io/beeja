package com.beeja.api.financemanagementservice.response;

import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private String id;
    private String employeeName;
    private String employeeId;
    private String organizationId;
    private String purpose;
    private String loanNumber;
    private Double amount;
    private Integer emiTenure;
    private Double monthlyEMI;
    private Date emiStartDate;
    private Date createdAt;
    private LoanStatus status;
    private LoanType loanType;
}
