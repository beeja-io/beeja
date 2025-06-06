package com.beeja.api.financemanagementservice.modals;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.enums.LoanType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class Loan {

  @Id private String id;

  @NotBlank(message = Constants.EMPID_MUST_NOT_NULL)
  private String employeeId;

  @NotBlank(message = Constants.ORG_ID_NOT_NULL)
  private String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

  private String purpose;
  private String loanNumber;

  @Min(value = 1, message = Constants.AMT_GREATER_THAN_ZERO)
  private Double amount;

  @Min(value = 1, message = Constants.EMI_TENURE_MUST_GREATER_THAN_ZERO)
  private Integer emiTenure;

  @Min(value = 1, message = Constants.MONTHLY_EMI_MUST_GREATER_THAN_ZERO)
  private Double monthlyEMI;

  private Date emiStartDate;

  @NotNull(message = Constants.LOAN_STATUS_MUST_NOT_BE_NULL)
  private LoanStatus status;

  @NotNull(message = Constants.LOAN_TYPE_CANNOT_BE_NULL)
  private LoanType loanType;

  @NotNull private Date requestedDate;

  private Boolean termsAccepted;
  private String rejectionReason;

  @CreatedDate private Date createdAt;

  private String createdBy;

  @LastModifiedDate private Date modifiedAt;

  private String modifiedBy;
}
