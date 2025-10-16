export enum ReviewType {
  ANNUAL = 'ANNUAL',
  QUARTERLY = 'QUARTERLY',
  HALFYEARLY = 'HALFYEARLY',
}

export const ReviewTypeLabels: Record<ReviewType, string> = {
  [ReviewType.ANNUAL]: 'Annual',
  [ReviewType.QUARTERLY]: 'Quarterly',
  [ReviewType.HALFYEARLY]: 'Half-Yearly',
};

export enum Department {
  DESIGN = 'DESIGN',
  DEVOPS = 'DEVOPS',
  ENGINEERING = 'ENGINEERING',
  MARKETING = 'MARKETING',
  ALL = 'ALL',
}

export const DepartmentLabels: Record<Department, string> = {
  [Department.DESIGN]: 'Design',
  [Department.DEVOPS]: 'Devops',
  [Department.ENGINEERING]: 'Engineering',
  [Department.MARKETING]: 'Marketing',
  [Department.ALL]: 'All',
};
