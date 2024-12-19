package com.beeja.api.financemanagementservice.modals.payrollsettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalTax extends StatutoryComponent {

  private Double taxAmount;

  private String state;

  private Double restrictedPfWage;
}
