export type ProjectStatus =
  | 'NOT_STARTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED';

export interface ProjectEntity {
  projectStatus: string;
  projectId: string;
  name: string;
  description: string;
  status: ProjectStatus;
  clientId: string;
  clientName: string;
  clientLogId?: string;
  projectManagerIds: string[];
  projectManagerNames: string[];
  projectResourceIds: string[];
  projectResourceNames: string[];
  startDate: string;
  clientIndustries: string;
  clientContact: string;
  clientEmail: string;
  endDate: string | null;
  billingCurrency: string | null;

  projectManagers: {
    employeeId: string;
    name: string;
    contractName: string | null;
  }[];

  contracts: {
    contractId: string;
    name: string;
    status: ProjectStatus;
    startDate: string;
    projectManagers: {
      employeeId: string;
      name: string;
      contractName: string;
    }[];
  }[];

  resources: {
    employeeId: string;
    name: string;
    contractName: string;
    allocationPercentage: number;
  }[];

  logoId?: string;
  industry?: string;
}

export interface Employee {
  employeeId: string;
  firstName: string;
  lastName: string;
  availabilityPercentage?: number;
}

export interface ProjectApiResponse {
  metadata: {
    pageNumber: number;
    totalSize: number;
    pageSize: number;
  };
  projects: ProjectEntity[];
}
