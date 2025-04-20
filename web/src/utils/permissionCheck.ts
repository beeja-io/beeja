import { LoggedInUserEntity } from '../entities/LoggedInUserEntity';

export const hasPermission = (user: LoggedInUserEntity, permission: string) => {
  return user.roles.some((role) =>
    role.permissions.some((rolePermission) => rolePermission === permission)
  );
};
