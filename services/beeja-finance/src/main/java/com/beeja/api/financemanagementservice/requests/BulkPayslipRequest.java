package com.beeja.api.financemanagementservice.requests;

import static com.beeja.api.financemanagementservice.Utils.Constants.PAYSLIP_ENTITY_TYPE;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BulkPayslipRequest {

  private MultipartFile zipFile;
  private String month;
  private String year;
  private String entityType = PAYSLIP_ENTITY_TYPE;
}
