package com.beeja.api.financemanagementservice.requests;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.enums.Availability;
import com.beeja.api.financemanagementservice.enums.Device;
import com.beeja.api.financemanagementservice.enums.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
public class DeviceDetails {

  private String deviceNumber;

  @NotNull private Device device;

  @NotNull private String provider;

  @NotNull private String model;

  @NotNull private Type type;

  private String os;

  private String specifications;

  private String ram;

  @NotNull private Availability availability;

  @NotNull private String productId;

  @NotNull
  @Min(value = 1, message = Constants.VALUE_MUST_GREATER_THAN_ZERO)
  private Double price;

  @NotNull private Date dateOfPurchase;

  private String comments;

  private String accessoryType;
}
