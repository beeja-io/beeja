import {
  ClientType,
  Industry,
  TaxCategory,
} from '../components/reusableComponents/ClientEnums.component';

export interface ClientDetails {
  clientId: string;
  clientName: string;
  clientType: ClientType;
  email: string;
  industry: Industry;

  contact: string;
  description: string;
  usePrimaryAddress: boolean;
  logo: string | File;
  logoId?: string;
  taxDetails: {
    taxCategory: TaxCategory;
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
  clientId: string;
  organizationId: string;
  email: string;
  industry: string;
  contact: string;
  description: string;
  logoId: string;
  status: string;
  taxDetails: {
    taxCategory: string;
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
  organizationId: string;
  id: string;
}
