package com.beeja.api.financemanagementservice.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
  Long totalRecords;
  int pageNumber;
  int pageSize;
  List<LoanDTO> loansList;
}
