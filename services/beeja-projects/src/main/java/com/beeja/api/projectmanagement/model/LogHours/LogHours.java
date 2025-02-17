package com.beeja.api.projectmanagement.model.LogHours;

import com.beeja.api.projectmanagement.enums.LogHourEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogHours {
    @NotBlank
    private String projectId;
    @NotBlank
    private String contractId;
    private String description;
    @NotBlank
    @Pattern(regexp = "^(0[0-8]):([0-5][0-9])$", message = "Log hours must be in the format of HH:MM (e.g., 50:30).")
    private String loghour;
    @NotNull
    private Date date;

    public void validateLogHour() {
        if (!LogHourEnum.isValid(this.loghour)) {
            String allowedValues = LogHourEnum.getAllowedValues();
            throw new IllegalArgumentException("Invalid log hour. Allowed values: [" + allowedValues + "]");
        }
    }
}
