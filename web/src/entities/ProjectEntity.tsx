export type ProjectStatus =
  | 'NOT_STARTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'ON_HOLD'
  | 'CANCELLED';

export interface ProjectEntity {
  projectManagerNames: any;
  projectStatus: string;
  projectManagers: any[];
  resources?: string[];
  projectId: string;
  id?: string;
  name: string;
  description?: string;
  status: ProjectStatus;
  startDate: string;
  endDate?: string;
  clientId: string;
  clientName?: string;
  organizationId?: string;
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
