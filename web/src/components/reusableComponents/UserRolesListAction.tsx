import React, { useState, useRef, useEffect } from 'react';
import { EditIcon, DeleteIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  ActionContainer,
  ActionMenuContent,
  ActionMenuOption,
  ActionMenu,
} from '../../styles/ExpenseListStyles.style';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import CenterModal from './CenterModal.component';
import { IRole } from '../../entities/RoleEntity';
import { PERMISSION_MODULE } from '../../constants/PermissionConstants';
import { hasPermission } from '../../utils/permissionCheck';
import { useUser } from '../../context/UserContext';
import { useTranslation } from 'react-i18next';

interface UserRoleListActionProps {
  onEdit: () => void;
  onDelete: () => void;
  isLoading: boolean;
  role: IRole;
}

const UserRoleListAction: React.FC<UserRoleListActionProps> = ({
  onEdit,
  onDelete,
  isLoading,
  role,
}) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [deleteConfirmModal, setDeleteConfirmModal] = useState(false);

  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown-container')) {
      setIsOpen(false);
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleDocumentClick = (e: any) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };
  window.addEventListener('click', handleDocumentClick);

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu
          onClick={openDropdown}
          className="rolesAction"
          style={{
            cursor: role.name === 'Super Admin' ? 'not-allowed' : 'pointer',
          }}
        >
          <ActionIcon />
        </ActionMenu>
        {isOpen && role.name != 'Super Admin' && (
          <ActionMenuContent>
            {user &&
              hasPermission(
                user,
                PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS
              ) && (
                <ActionMenuOption onClick={onEdit}>
                  <EditIcon />
                  {t('EDIT')}
                </ActionMenuOption>
              )}
            {user &&
              hasPermission(
                user,
                PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS
              ) && (
                <ActionMenuOption
                  onClick={() => {
                    setDeleteConfirmModal(true);
                    setIsOpen(false);
                  }}
                >
                  <DeleteIcon />
                  {t('DELETE')}
                </ActionMenuOption>
              )}
          </ActionMenuContent>
        )}
        {deleteConfirmModal && (
          <>
            <span style={{ cursor: 'default' }}>
              <CenterModal
                handleModalClose={() => setDeleteConfirmModal(false)}
                handleModalSubmit={onDelete}
                modalHeading="DELETE"
                modalContent={'ARE_YOU_SURE_WANT_DELETE_ROLE'}
                isResponseLoading={isLoading}
              />
            </span>
          </>
        )}
      </ActionContainer>
    </>
  );
};

export default UserRoleListAction;
