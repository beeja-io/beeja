export interface ProjectStatusOption {
  value: string;
  label: string;
  color: string;
  bgColor: string;
}

export const ProjectStatus: ProjectStatusOption[] = [
  {
    value: 'NOT_STARTED',
    label: 'Not Started',
    color: '#A0AEC0',
    bgColor: '#A0AEC01A',
  },
  {
    value: 'IN_PROGRESS',
    label: 'In Progress',
    color: '#FF9900',
    bgColor: '#FF99001A',
  },
  {
    value: 'COMPLETED',
    label: 'Completed',
    color: '#34A853',
    bgColor: '#34A8531A',
  },
  {
    value: 'CANCELLED',
    label: 'Cancelled',
    color: '#E03137',
    bgColor: '#E031371A',
  },
  { value: 'ACTIVE', label: 'Active', color: '#2563EB', bgColor: '#DBEAFE' },
  {
    value: 'INACTIVE',
    label: 'Inactive',
    color: '#6B7280',
    bgColor: '#E5E7EB',
  },
  {
    value: 'ON_HOLD',
    label: 'On Hold',
    color: '#005792',
    bgColor: '#0057921A',
  },
];
