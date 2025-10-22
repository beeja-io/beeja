interface Task {
  taskName: string;
  description: string;
  amount: number;
}

interface InvoicePeriod {
  startDate: string;
  endDate: string;
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
  invoiceId: string | null;
  vat: number;
  daysLeftForPayment: string;
  invoicePeriod: InvoicePeriod;
  currency: string;
  taxId: string | null;
}
