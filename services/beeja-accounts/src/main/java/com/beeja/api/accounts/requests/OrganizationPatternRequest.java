package com.beeja.api.accounts.requests;

import com.beeja.api.accounts.enums.PatternType;
import com.beeja.api.accounts.utils.Constants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizationPatternRequest {
  @NotNull(message = Constants.PATTERN_MUST_NOT_NULL)
  private PatternType patternType;

  @Min(value = 1, message = Constants.PATTERN_LENGTH_MUST_BE_GREATER_THAN_ONE)
  private int patternLength;

  @NotBlank(message = Constants.PREFIX_MUST_NOT_NULL)
  @Size(max = 10, message = Constants.PREFIX_MUST_NOT_EXCEED_TEN_CHARS)
  private String prefix;

  @Min(value = 0, message = Constants.INITIAL_SEQUENCE_MUST_GREATER_THAN_ZERO)
  private int initialSequence;

  @NotNull(message = Constants.STATUS_IS_REQUIRED)
  private boolean active;
}
