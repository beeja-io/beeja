import { IOrganization } from "./OrganizationEntity";

export type LoggedInUserEntity = {
  firstName: string;
  email: string;
  lastName: string;
  active: boolean;
  employeeId: string;
  roles: Role[];
  organizations: IOrganization;
};

export interface Role {
  id: string;
  name: string;
  description: string;
  permissions: string[];
}
