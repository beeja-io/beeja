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
    bgColor: '#EDF2F7',
  },
  {
    value: 'IN_PROGRESS',
    label: 'In Progress',
    color: '#ED8936',
    bgColor: '#FEF3C7',
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
    color: '#E53E3E',
    bgColor: '#FEE2E2',
  },
  { value: 'ACTIVE', label: 'Active', color: '#2563EB', bgColor: '#DBEAFE' },
  {
    value: 'INACTIVE',
    label: 'Inactive',
    color: '#6B7280',
    bgColor: '#E5E7EB',
  },
  { value: 'ON_HOLD', label: 'On Hold', color: '#F59E0B', bgColor: '#FEF9C3' },
];
