export interface CreateExpenseRequest {
  department: string;
  category: string;
  type: string;
  amount: string;
  currencyCode: string;
  modeOfPayment: string;
  merchant: string;
  isClaimed: boolean;
  paymentMadeBy: string;
  description: string;
  files: File[];
  expenseDate: string;
  paymentDate: string | null;
  requestedDate: string;
}
