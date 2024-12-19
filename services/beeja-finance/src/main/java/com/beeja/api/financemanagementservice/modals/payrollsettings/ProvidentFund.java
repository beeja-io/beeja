package com.beeja.api.financemanagementservice.modals.payrollsettings;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvidentFund extends StatutoryComponent {

  @NotNull private String epfNumber;

  private Double restrictedPfWage;

  private Double employeeContributionPercentage;

  private Double employerContributionPercentage;
}
