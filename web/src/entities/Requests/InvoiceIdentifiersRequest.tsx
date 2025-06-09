export interface GenerateInvoiceIdentifiersRequest {
  contractId?: string;
}

export interface InvoiceIdentifiers {
  invoiceId: string;
  remittanceReferenceNumber: string;
}
