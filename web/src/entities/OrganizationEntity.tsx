export interface IOrganization {
  id: string;
  name: string;
  email: string;
  subscriptionId?: string;
  location?: string;
  emailDomain?: string;
  contactMail: string;
  website: string;
  preferences: IPreferences;
  address?: Address;
  filingAddress?: string;
  accounts?: Accounts;
  logoFileId?: string;
  loanLimit: ILoanLimits;
  dateFormat: string;
  currencyType: string; 
  bankDetails?: {
    accountName?: string;
    bankName?: string;
    accountNumber?: string;
    ifscNumber?: string;
  };
}
export interface IPreferences {
  dateFormat: string;
  timeZone: string;
  fontName: string;
  fontSize: number;
  theme: string;
  currencyType: string;
}

export interface Address {
  addressOne: string;
  addressTwo: string;
  state: string;
  city: string;
  pinCode: number;
  country: string;
}

export interface Accounts {
  id: string;
  pfNumber: string;
  tanNumber: string;
  panNumber: string;
  esiNumber: string;
  linNumber: string;
  gstNumber: string;
   taxNumber: string;  
   bankDetails: string;
}

export interface ILoanLimits {
  monitorLoan: number;
  isMonitorLoanEnabled: boolean;
  personalLoan: number;
  isPersonalLoanEnabled: boolean;
  salaryMultiplier: number;
  isSalaryMultiplierEnabled: boolean;
}
