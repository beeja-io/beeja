import { useState, useEffect } from 'react';
import AddRoleComponent from '../components/directComponents/AddRoles.component';
import UserRoleList from './UserRolesList.screen';
import { getAllRolesInOrganization } from '../service/axiosInstance';
import { Button } from '../styles/CommonStyles.style';
import { BorderDivLine } from '../styles/MyProfile.style';
import {
  HeaderWrapper,
  ProfileHeading,
} from '../styles/OrganizationSettingsStyles.style';
import { IRole } from '../entities/RoleEntity';
import { useUser } from '../context/UserContext';
import { hasPermission } from '../utils/permissionCheck';
import { PERMISSION_MODULE } from '../constants/PermissionConstants';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useTranslation } from 'react-i18next';

interface Role {
  id: string;
  name: string;
  description: string;
  permissions: string[];
}
const UserRolesPermissionsComponent = () => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [showRoleForm, setShowRoleForm] = useState(false);
  const [roles, setRoles] = useState<IRole[]>([]);
  const [editingRole, setEditingRole] = useState<IRole | null>(null);
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      setIsLoaded(true);
      const response = await getAllRolesInOrganization();
      const rolesData: IRole[] = response.data.map((role: Role) => ({
        id: role.id,
        name: role.name,
        description: role.description,
        permissions: role.permissions,
      }));
      setRoles(rolesData);
      setIsLoaded(false);
    } catch {
      setIsLoaded(false);
    }
  };

  const handleAddRoleClick = () => {
    setEditingRole(null);
    setShowRoleForm(!showRoleForm);
  };

  const handleEditRole = (role: IRole) => {
    setEditingRole(role);
    setShowRoleForm(true);
  };

  const handleCloseForm = () => {
    setShowRoleForm(false);
    setEditingRole(null);
  };

  return (
    <>
      <HeaderWrapper>
        <ProfileHeading>{t('USER_ROLES_AND_PERMISSIONS')}</ProfileHeading>
        {showRoleForm ? (
          <Button
            width="112px"
            height="40px"
            className="submit btnMobile"
            onClick={handleCloseForm}
          >
            {t('CANCEL')}
          </Button>
        ) : (
          user &&
          hasPermission(
            user,
            PERMISSION_MODULE.CREATE_ROLES_OF_ORGANIZATIONS
          ) && (
            <Button
              width="142px"
              height="40px"
              className="submit btnMobile"
              onClick={handleAddRoleClick}
            >
              <AddNewPlusSVG /> {t('ADD_ROLE')}
            </Button>
          )
        )}
      </HeaderWrapper>
      {showRoleForm ? (
        <AddRoleComponent
          initialPermissions={editingRole ? editingRole.permissions : []}
          editingRole={editingRole}
          onClose={handleCloseForm}
          onSubmit={() => {
            fetchRoles();
            handleCloseForm();
          }}
        />
      ) : (
        <>
          <BorderDivLine width="100%" />
          <UserRoleList
            roles={roles}
            onEditRole={handleEditRole}
            onDeleteRole={fetchRoles}
            isLoaded={isLoaded}
          />
        </>
      )}
    </>
  );
};

export default UserRolesPermissionsComponent;
