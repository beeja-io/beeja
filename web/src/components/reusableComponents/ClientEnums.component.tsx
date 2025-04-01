interface ClientOptions {
    clientType: string[];
    industry: string[];
    taxCategory: string[];
  }
  /* eslint-disable no-unused-vars */
  export enum ClientType {
    INTERNAL = 'INTERNAL',
    INDIVIDUAL = 'INDIVIDUAL',
    COMPANY = 'COMPANY',
    ORGANIZATION = 'ORGANIZATION',
  }
  export enum Industry {
    HRMS = 'HRMS',
    SOCIALMEDIA = 'SOCIALMEDIA',
    ECOMMERCE = ' ECOMMERCE',
    ITSERVICES = 'ITSERVICES',
  }
  export enum TaxCategory {
    VAT = ' VAT',
    GST = 'GST',
    SALES_TAX = 'SALES_TAX',
    EXCISE_TAX = 'EXCISE_TAX',
    
  }
  /* eslint-enable no-unused-vars */
  export const clientOptions: ClientOptions = {
    clientType: [
     ClientType.INTERNAL,
     ClientType.INDIVIDUAL,
     ClientType.COMPANY,
     ClientType.ORGANIZATION
    ],
    industry: [
        Industry.HRMS,
        Industry.SOCIALMEDIA,
        Industry.ECOMMERCE,
        Industry.ITSERVICES
    ],
    taxCategory: [
        TaxCategory.VAT,
        TaxCategory.GST,
        TaxCategory.EXCISE_TAX,
        TaxCategory.SALES_TAX
    ]
  };
  