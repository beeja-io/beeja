package com.beeja.api.financemanagementservice.modals.payrollsettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateInsurance extends StatutoryComponent {

  private String esiNumber;

  private Double employeeContributionPercentage;

  private Double employerContributionPercentage;

  private Double restrictedPfWage;
}
