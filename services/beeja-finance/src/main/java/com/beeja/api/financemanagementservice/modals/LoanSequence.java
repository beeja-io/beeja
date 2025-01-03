package com.beeja.api.financemanagementservice.modals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loan_sequences")
public class LoanSequence {
  @Id
  private String id;
  private long seq;
}
