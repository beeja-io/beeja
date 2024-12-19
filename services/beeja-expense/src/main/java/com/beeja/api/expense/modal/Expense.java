package com.beeja.api.expense.modal;

import com.beeja.api.expense.utils.UserContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "expenses")
public class Expense {
  @Id private String id;
  private String category;
  private String type;
  private float amount;
  private String currencyCode;
  private String modeOfPayment;
  private String merchant;
  private boolean isClaimed;
  private String paymentMadeBy;
  private String description;
  private String organizationId = (String) UserContext.getLoggedInUserOrganization().get("id");
  private String department;
  private String createdBy = UserContext.getLoggedInUserEmail();
  private String createdByEmployeeId = UserContext.getLoggedInEmployeeId();
  private List<String> fileId;
  private List<File> files;
  private Date expenseDate;
  private Date requestedDate;
  private String status = "Pending";
  private Date paymentSettled;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;

  private String modifiedBy;
}
