package com.beeja.api.accounts.enums;

import lombok.Getter;

@Getter
public enum DateFormats {
  DD_MM_YYYY("dd-MM-yyyy"),
  DD_SLASH_MM_SLASH_YYYY("dd/MM/yyyy"),
  DD_SPACE_MMMM_SPACE_YYYY("dd MMMM yyyy"),
  MMMM_DD_YYYY("MMMM dd yyyy"),
  MM_SLASH_DD_SLASH_YYYY("MM/dd/yyyy"),
  DD_DOT_MM_DOT_YYYY("dd.MM.yyyy");

  private final String format;

  DateFormats(String format) {
    this.format = format;
  }
}
