export interface UpdateExpenseRequest {
  department: string;
  category: string;
  type: string;
  amount: string;
  currencyCode: string;
  claimed: boolean;
  deleteFileId: string[];
  newFiles: File[];
  modeOfPayment: string;
  paymentMadeBy: string;

  merchant: string;
  description: string;
  expenseDate: string;
  requestedDate: string;
  paymentSettled: string;
  paymentDate: string | null;
}
