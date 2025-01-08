export interface OrganizationValuesInternal {
  value: string;
  description: string;
}

export interface OrganizationValues {
  id: string;
  organizationId: string;
  key: string;
  values: OrganizationValuesInternal[];
}
