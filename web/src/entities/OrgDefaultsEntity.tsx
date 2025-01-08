export interface OrgDefaults {
  organizationId: string;
  key: string;
  values: OrgValues[];
}

export interface OrgValues {
  value: string;
  description: string;
}
