package com.beeja.api.accounts.model.Organization;

import com.beeja.api.accounts.enums.CurrencyType;
import com.beeja.api.accounts.enums.DateFormats;
import com.beeja.api.accounts.enums.FontNames;
import com.beeja.api.accounts.enums.Theme;
import com.beeja.api.accounts.enums.TimeZones;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Preferences {
  private DateFormats dateFormat = DateFormats.DD_MM_YYYY;
  private TimeZones timeZone = TimeZones.COORDINATED_UNIVERSAL_TIME;
  private FontNames fontName = FontNames.NUNITO;
  private int fontSize = 12;
  private Theme theme = Theme.LIGHT;
  private CurrencyType currencyType = CurrencyType.INDIAN_RUPEE;
}
