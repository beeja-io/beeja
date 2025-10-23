interface ClientOptions {
  clientType: string[];
  industry: string[];
  taxCategory: string[];
}
/* eslint-disable no-unused-vars */
export enum ClientType {
  INTERNAL = 'INTERNAL',
  INDIVIDUAL = 'INDIVIDUAL',
  CORPORATE = 'CORPORATE',
  STARTUP = 'STARTUP',
  CONSULTING = 'CONSULTING',
  OTHER = 'OTHER',
}
export enum Industry {
  SOCIALMEDIA = 'SOCIALMEDIA',
  ITSERVICES = 'ITSERVICES',
  CONSULTING = 'CONSULTING',
  EDUCATION = 'EDUCATION',
  FINANCE = 'FINANCE',
  HEALTHCARE = 'HEALTHCARE',
  MANUFACTURING = 'MANUFACTURING',
  REAL_ESTATE = 'REAL_ESTATE',
  RETAIL = 'RETAIL',
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
