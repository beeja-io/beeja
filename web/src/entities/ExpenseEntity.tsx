export interface Expense {
  id: string;
  department: string;
  category: string;
  type: string;
  amount: number;
  currencyCode: string;
  modeOfPayment: string;
  merchant: string;
  claimed: boolean;
  paymentMadeBy: string;
  description: string;
  organizationId: string;
  createdBy: string;
  createdByEmployeeId: string;
  fileId: string;
  fileType: string;
  fileFormat: string;
  name: string;
  files: File[];
  expenseDate: Date;
  paymentDate: Date;
  paymentSettled: Date;
  requestedDate: Date;
  createdAt: Date;
  modifiedAt: Date;
  modifiedBy: string;
}

interface File {
  id: string;
  name: string;
}
