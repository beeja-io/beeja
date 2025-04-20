interface ISelectOptions {
  [key: string]: string;
}

export const DATE_FORMATS: ISelectOptions = {
  DD_SLASH_MM_SLASH_YYYY: "DD/MM/YYYY",
  DD_MM_YYYY: "DD-MM-YYYY",
  DD_SPACE_MMMM_SPACE_YYYY: "DD MM YYYY",
  MMMM_DD_YYYY: "MM DD YYYY",
  MM_SLASH_DD_SLASH_YYYY: "MM/DD/YYYY",
  DD_DOT_MM_DOT_YYYY: "DD.MM.YYYY",
};

export const TIME_ZONES: ISelectOptions = {
  COORDINATED_UNIVERSAL_TIME: "Coordinated Universal Time (UTC)",
  INDIAN_STANDARD_TIME: "Indian Standard Time (IST)",
  CENTRAL_EUROPEAN_TIME: "Central European Time (CET)",
  EASTERN_STANDARD_TIME: "Eastern Standard Time (EST)",
};

export const CURRENCY_TYPES: ISelectOptions = {
  INDIAN_RUPEE: "Indian Rupee (INR)",
  EURO: "Euro (EUR)",
  DOLLAR: "Dollar (USD)",
};
