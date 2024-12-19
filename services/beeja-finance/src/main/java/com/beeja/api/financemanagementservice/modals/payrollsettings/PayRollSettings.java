package com.beeja.api.financemanagementservice.modals.payrollsettings;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payroll_settings")
public class PayRollSettings {

  @Id private String id;

  @NotBlank(message = "organizationId must not be null")
  private String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

  private List<SalaryComponent> salaryComponents;

  private List<StatutoryComponent> statutoryComponents;
}
