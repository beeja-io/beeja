enum ContractStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  ON_HOLD = 'ON_HOLD',
  COMPLETED = 'COMPLETED',
}

export enum ContractType {
  FIXED_PRICE = 'FIXED_PRICE',
  HYBRID = 'HYBRID',
  MILESTONE_BASED = 'MILESTONE_BASED',
  RETAINER = 'RETAINER',
  OTHER = 'OTHER',
}

export enum ContractBillingType {
  BILLABLE = 'BILLABLE',
  NON_BILLABLE = 'NON_BILLABLE',
  PARTIALLY_BILLABLE = 'PARTIALLY_BILLABLE',
}

export type RawProjectResource = {
  employeeId: string;
  name: string;
  contractName: string | null;
  allocationPercentage: number;
};

export interface ContractDetails {
  attachmentIds: string[];
  customContractType: string;
  projectId: string;
  contractId: string;
  contractTitle: string;
  projectName: string;
  clientName: string;
  status: ContractStatus;
  projectManagerIds: string[];
  projectManagerNames: string[];
  billingType?: ContractBillingType;
  billingCurrency?: string;
  contractValue?: string;
  description?: string;
  contractType?: ContractType;
  startDate: string;
  endDate: string;
  projectManagers: string[];
  rawProjectResources: RawProjectResource[];
}
