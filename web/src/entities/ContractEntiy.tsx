enum ContractStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  ON_HOLD = 'ON_HOLD',
  COMPLETED = 'COMPLETED',
}

type Resources = {
  value: string;
  label: string;
  availability?: number;
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
  Resources: Resources[];
  billingType?: string;
  billingCurrency?: string;
  contractValue?: string;
  description?: string;
  contractType?: string;
  startDate: string;
  endDate: string;
  projectManagers: string[];
}
