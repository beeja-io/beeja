import { EMPLOYEE_MODULE } from '../constants/PermissionConstants';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import { LoggedInUserEntity } from '../entities/LoggedInUserEntity';

// FIXME Delete this file
export const isAuthorized = (
  user: LoggedInUserEntity,
  employee: EmployeeEntity
) => {
  return (
    employee.account.employeeId === user?.employeeId ||
    user.roles.some((role) =>
      role.permissions.some(
        (permission) =>
          permission === EMPLOYEE_MODULE.UPDATE_ROLES_AND_PERMISSIONS
      )
    )
  );
};
