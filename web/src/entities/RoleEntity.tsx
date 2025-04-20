import { IOrganization } from './OrganizationEntity';

export interface IRole {
  id: string;
  name: string;
  description: string;
  permissions: string[];
  organization: IOrganization;
}
