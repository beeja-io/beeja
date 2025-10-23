import {
  ClientType,
  Industry,
  TaxCategory,
} from '../components/reusableComponents/ClientEnums.component';

export interface ClientDetails {
  clientId: string;
  clientName: string;
  clientType: ClientType;
  customClientType: string;
  email: string;
  industry: Industry;
  customIndustry: string;

  contact: string;
  description: string;
  usePrimaryAddress: boolean;
  logo: string | File;
  logoId?: string;
  taxDetails: {
    taxCategory: TaxCategory;
    customTaxCategory: string;
    taxNumber: string;
  };
  primaryAddress: {
    street: string;
    city: string;

    state: string;

    postalCode: string;
    country: string;
  };
  billingAddress: {
    street: string;
    city: string;

    state: string;

    postalCode: string;
    country: string;
  };
}

export interface ClientResponse {
  id: string;
  clientName: string;
  clientType: string;
  customClientType: string;
  clientId: string;
  organizationId: string;
  email: string;
  industry: string;
  customIndustry: string;
  contact: string;
  description: string;
  logoId: string;
  status: string;
  taxDetails: {
    taxCategory: string;
    customTaxCategory: string;
    taxNumber: string;
  };
  primaryAddress: {
    street: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
  };
  billingAddress: {
    street: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
  };
  createdAt: string;
  usePrimaryAsBillingAddress: boolean;
}

export interface Client {
  clientId: string;
  clientName: string;
  clientType: string;
  customClientType: string;
  organizationId: string;
  id: string;
}
