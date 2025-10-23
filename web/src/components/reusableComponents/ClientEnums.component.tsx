interface ClientOptions {
  clientType: string[];
  industry: string[];
  taxCategory: string[];
}
/* eslint-disable no-unused-vars */
export enum ClientType {
  INTERNAL = 'Internal',
  INDIVIDUAL = 'Individual',
  CORPORATE = 'Corporate',
  STARTUP = 'Start up',
  CONSULTING = 'Consulting',
  OTHER = 'OTHER',
}
export enum Industry {
  SOCIALMEDIA = 'Social Media',
  ITSERVICES = 'IT services',
  CONSULTING = 'Consulting',
  EDUCATION = 'Education',
  FINANCE = 'Finance',
  HEALTHCARE = 'Health Care',
  MANUFACTURING = 'Manufacturing',
  REAL_ESTATE = 'Real Estate',
  RETAIL = 'Retail',
  OTHER = 'OTHER',
}
export enum TaxCategory {
  VAT = 'VAT',
  GST = 'GST',
  ABN = 'ABN',
  OTHER = 'OTHER',
}
/* eslint-enable no-unused-vars */
export const clientOptions: ClientOptions = {
  clientType: [
    ClientType.INTERNAL,
    ClientType.INDIVIDUAL,
    ClientType.CORPORATE,
    ClientType.STARTUP,
    ClientType.CONSULTING,
    ClientType.OTHER,
  ],
  industry: [
    Industry.SOCIALMEDIA,
    Industry.ITSERVICES,
    Industry.CONSULTING,
    Industry.EDUCATION,
    Industry.FINANCE,
    Industry.HEALTHCARE,
    Industry.MANUFACTURING,
    Industry.RETAIL,
    Industry.REAL_ESTATE,
    Industry.OTHER,
  ],
  taxCategory: [
    TaxCategory.VAT,
    TaxCategory.GST,
    TaxCategory.ABN,
    TaxCategory.OTHER,
  ],
};
