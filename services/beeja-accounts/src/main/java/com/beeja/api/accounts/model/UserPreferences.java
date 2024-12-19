package com.beeja.api.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
  private String theme;
  private String language;
  private String font;
}
