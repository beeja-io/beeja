package com.beeja.api.employeemanagement.response;

import com.beeja.api.employeemanagement.model.JobDetails;
import lombok.Data;

@Data
public class GetLimitedEmployee {
  private String id;
  private String employeeId;
  private JobDetails jobDetails;
  private String profilePictureId;
}
