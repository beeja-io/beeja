enum ContractStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  ON_HOLD = 'ON_HOLD',
  COMPLETED = 'COMPLETED',
}

export type RawProjectResource = {
  employeeId: string;
  name: string;
  contractName: string | null;
  allocationPercentage: number;
};

export interface ContractDetails {
  projectId: string;
  contractId: string;
  contractTitle: string;
  projectName: string;
  clientName: string;
  status: ContractStatus;
  projectManagerIds: string[];
  projectManagerNames: string[];
  billingType?: string;
  billingCurrency?: string;
  contractValue?: string;
  description?: string;
  contractType?: string;
  startDate: string;
  endDate: string;
  projectManagers: string[];
  rawProjectResources: RawProjectResource[];
}
