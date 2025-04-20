import React, { useState, useEffect } from "react";
import { deleteRole } from "../service/axiosInstance";
import { IRole } from "../entities/RoleEntity";
import { Table } from "../styles/TableStyles.style";
import UserRoleListAction from "../components/reusableComponents/UserRolesListAction";
import { BorderDivLine } from "../styles/MyProfile.style";
import AddRoleComponent from "../components/directComponents/AddRoles.component";
import { toast } from "sonner";
import { AxiosError } from "axios";
import { hasPermission } from "../utils/permissionCheck";
import { PERMISSION_MODULE } from "../constants/PermissionConstants";
import { useUser } from "../context/UserContext";
import { useTranslation } from "react-i18next";

interface UserRoleListProps {
  roles: IRole[];
  onEditRole: (role: IRole) => void;
  onDeleteRole: () => Promise<void>;
  isLoaded: boolean;
}

const UserRoleList: React.FC<UserRoleListProps> = ({
  roles: propRoles,
  onEditRole,
  onDeleteRole,
  isLoaded,
}) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [roles, setRoles] = useState<IRole[]>(propRoles);
  const [showAddRole, setShowAddRole] = useState(false);
  const [editingRole, setEditingRole] = useState<IRole | null>(null);

  useEffect(() => {
    setRoles(propRoles);
  }, [propRoles]);

  const handleEditRole = (role: IRole) => {
    setEditingRole(role);
    setShowAddRole(true);
    onEditRole(role);
  };

  const [isDeleteResponseLoading, setIsDeleteResponseLoading] = useState(false);

  const handleDeleteRoleLocal = async (roleId: string) => {
    try {
      setIsDeleteResponseLoading(true);
      await deleteRole(roleId);
      onDeleteRole();
      setRoles((prevRoles) => prevRoles.filter((role) => role.id !== roleId));
      setIsDeleteResponseLoading(false);
      toast.success("Role deleted successfully");
    } catch (error) {
      if (error instanceof AxiosError) {
        if (
          error.response?.data.startsWith(
            "Error Occurred in Deleting Role because it is in use and assigned",
          )
        ) {
          toast.error(
            "Role is in use and assigned to users. Cannot delete role",
          );
          return;
        }
      }
      toast.error("Failed to delete role");
      setIsDeleteResponseLoading(false);
    } finally {
      setIsDeleteResponseLoading(false);
    }
  };

  return (
    <>
      {showAddRole && (
        <AddRoleComponent
          editingRole={editingRole}
          initialPermissions={[]}
          onClose={() => setShowAddRole(false)}
          onSubmit={() => setShowAddRole(false)}
        />
      )}
      {!showAddRole && (
        <Table style={{ height: "fit-content" }}>
          <thead className="rolesAndPermissions">
            <tr>
              <th className="first-column">{t("ROLE_NAME")}</th>
              <th className="second-column">{t("DESCRIPTION")}</th>
              {user &&
                (hasPermission(
                  user,
                  PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS,
                ) ||
                  hasPermission(
                    user,
                    PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS,
                  )) && <th className="third-column">{t("ACTIONS")}</th>}
            </tr>
          </thead>
          <tbody className="rolesAndPermissions">
            {!isLoaded ? (
              <>
                {roles.map((role) => (
                  <tr key={role.id} className="loaded">
                    <td className="first-column">{role.name}</td>
                    <td className="second-column">{role.description}</td>
                    {user &&
                      (hasPermission(
                        user,
                        PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS,
                      ) ||
                        hasPermission(
                          user,
                          PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS,
                        )) && (
                        <td className="third-column">
                          <UserRoleListAction
                            onEdit={() => handleEditRole(role)}
                            onDelete={() => handleDeleteRoleLocal(role.id)}
                            isLoading={isDeleteResponseLoading}
                            role={role}
                          />
                        </td>
                      )}
                    <BorderDivLine width="100%" />
                  </tr>
                ))}
              </>
            ) : (
              <>
                {[...Array(6).keys()].map((rowIndex) => (
                  <tr key={rowIndex}>
                    {[
                      ...Array(
                        user &&
                          (hasPermission(
                            user,
                            PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS,
                          ) ||
                            hasPermission(
                              user,
                              PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS,
                            ))
                          ? 3
                          : 2,
                      ).keys(),
                    ].map((cellIndex) => (
                      <td key={cellIndex}>
                        <div className="skeleton skeleton-text">&nbsp;</div>
                      </td>
                    ))}
                  </tr>
                ))}
              </>
            )}
          </tbody>
        </Table>
      )}
    </>
  );
};

export default UserRoleList;
