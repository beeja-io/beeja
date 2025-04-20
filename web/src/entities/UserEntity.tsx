import { Role } from "./LoggedInUserEntity";
import { IOrganization } from "./OrganizationEntity";

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  roles: Role[];
  employeeId: string;
  employmentType: string;
  organizations: IOrganization;
  createdBy: string;
  modifiedBy: string | null;
  createdAt: string;
  modifiedAt: string;
  active: boolean;
}
