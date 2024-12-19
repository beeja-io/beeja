export interface Loan {
  id: string;
  amount: number;
  createdAt: Date;
  createdBy: string | null;
  emiStartDate: Date;
  emiTenure: number;
  employeeId: string;
  loanNumber: string;
  loanType: string;
  modifiedAt: Date;
  modifiedBy: string | null;
  monthlyEMI: number;
  organizationId: string;
  purpose: string;
  rejectionReason: string | null;
  requestedDate: Date | null;
  status: string;
  termsAccepted: boolean | null;
}
