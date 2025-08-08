interface Task {
  name: string;
  description: string;
  amount: number;
}

interface InvoicePeriod {
  from: string;
  to: string;
}

interface InvoiceRequest {
  contractId: string;
  billingDate: string;
  dueDate: string;
  amount: number;
  notes: string[];
  tasks: Task[];
  clientId: string;
  projectId: string | null;
  remittanceRef: string | null;
  vat: number;
  daysLeftForPayment: string;
  invoicePeriod: InvoicePeriod;
  currencyType: string;
}
