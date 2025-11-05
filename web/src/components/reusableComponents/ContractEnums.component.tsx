export enum ContractType {
  FIXED_PRICE = 'FIXED_PRICE',
  HYBRID = 'HYBRID',
  MILESTONE_BASED = 'MILESTONE_BASED',
  RETAINER = 'RETAINER',
  OTHER = 'OTHER',
}

export const ContractTypeLabels: Record<ContractType, string> = {
  [ContractType.FIXED_PRICE]: 'Fixed Price',
  [ContractType.HYBRID]: 'Hybrid',
  [ContractType.MILESTONE_BASED]: 'Milestone Based',
  [ContractType.RETAINER]: 'Retainer',
  [ContractType.OTHER]: 'Other',
};

export enum ContractBillingType {
  BILLABLE = 'BILLABLE',
  NON_BILLABLE = 'NON_BILLABLE',
  PARTIALLY_BILLABLE = 'PARTIALLY_BILLABLE',
}

export const ContractBillingTypeLabels: Record<ContractBillingType, string> = {
  [ContractBillingType.BILLABLE]: 'Billable',
  [ContractBillingType.NON_BILLABLE]: 'Non-Billable',
  [ContractBillingType.PARTIALLY_BILLABLE]: 'Partially Billable',
};

export enum BillingCurrency {
  DOLLER = 'DOLLER',
  EURO = 'EURO',
  INR = 'INR',
}

export const BillingCurrencyLabels: Record<BillingCurrency, string> = {
  [BillingCurrency.DOLLER]: 'DOLLER ($)',
  [BillingCurrency.EURO]: 'EURO (€)',
  [BillingCurrency.INR]: 'INR (₹)',
};
