import { BillingCurrency } from '../components/reusableComponents/ContractEnums.component';

interface BillingAddress {
  street?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
}
interface Address {
  addressOne?: string;
  addressTwo?: string;
  city?: string;
  state?: string;
  pinCode?: number;
  country?: string;
}
interface BankDetails {
  accountName: string;
  bankName: string;
  accountNumber: string;
  ifscNumber: string;
}

export interface AddInvoiceFormProps {
  handleClose: () => void;
  invoiceId?: string;
  remittanceReferenceNumber?: string;
  contractId: string;
  contractTitle?: string;
  startDate?: string;
  endDate?: string;
  primaryAddress?: Address;
  billingAddress?: BillingAddress;
  clientName?: string;
  dueDays?: number;
  organizationId?: string;
  projectId?: string;
  bankDetails?: BankDetails;
  status?: string;
  clientId?: string;
  billingCurrency?: string;
  billingType?: string;
}

export interface FormDataProps {
  RemittanceNo: string;
  InvoiceNo: string;
  tax: String;
  taxId: string;
  fromDate: Date;
  toDate: Date;
  dueRemarks: string;
  remarksNote: string;
  contractName: string;
  contractId: string;
  primaryAddress?: Address;
  billingAddress?: BillingAddress;
  clientName: string;
  organization: string;
  organizationId: string;
  projectId: string;
  bankDetails: BankDetails;
  status: string;
  clientId: string;
  currencyType: BillingCurrency;
}

export interface RowProps {
  contract: string;
  description: string;
  price: string;
}
