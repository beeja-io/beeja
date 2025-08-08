export interface Project {
  projectId: string;
  name: string;
  description?: string;
  startDate?: string; // or Date if you parse it
  endDate?: string; // or Date
  status?: string;
  clientId: string;
}
