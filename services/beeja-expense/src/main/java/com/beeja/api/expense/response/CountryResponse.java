package com.beeja.api.expense.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
  @Id private String id;

  private String name;

  private List<String> expenseTypes = new ArrayList<>();

  private List<String> category = new ArrayList<>();

  private List<String> modeOfPayment = new ArrayList<>();
}
