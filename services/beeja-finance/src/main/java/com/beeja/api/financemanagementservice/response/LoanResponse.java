package com.beeja.api.financemanagementservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    Long totalRecords;
    int pageNumber;
    int pageSize;
    List<LoanDTO> loansList;
}
