import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/DocumentTabStyles.style';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import { DeviceDetails } from '../../entities/InventoryEntity';
import { deleteInventory } from '../../service/axiosInstance';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import CenterModal from './CenterModal.component';
import CenterModalMain from './CenterModalMain.component';
import ToastMessage from './ToastMessage.component';
import EditInventoryForm from '../directComponents/EditInventory.component';
import { useUser } from '../../context/UserContext';
import { INVENTORY_MODULE } from '../../constants/PermissionConstants';
import { hasPermission } from '../../utils/permissionCheck';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import { disableBodyScroll, enableBodyScroll } from '../../constants/Utility';

interface ActionProps {
  options: {
    key: 'EDIT' | 'DELETE';
    title: string;
    svg: React.ReactNode;
  }[];
  currentDevice: DeviceDetails;
  handleSuccessMessage: () => void;
  handleDeleteInventory: () => void;
  updateInventoryList: () => void;
  deviceTypes: OrganizationValues;
  inventoryProviders: OrganizationValues;
}

export const InventoryListAction: React.FC<ActionProps> = ({
  options,
  currentDevice,
  handleSuccessMessage,
  handleDeleteInventory,
  updateInventoryList,
  deviceTypes,
  inventoryProviders,
}) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<
    'EDIT' | 'DELETE' | null
  >(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const handleOpenEditModal = () => {
    setIsEditModalOpen(!isEditModalOpen);
  };
  const [isDeletedToastMessage, setIsDeleteToastMessage] = useState(false);
  const handleIsDeleteToastMessage = () => {
    setIsDeleteToastMessage(!isDeletedToastMessage);
  };

  const handleDeleteModal = () => {
    setConfirmDeleteModal(!confirmDeleteModal);
  };

  const openDropdown = () => {
    setIsOpen(!isOpen);
  };
  const handleOptionClick = (key: 'EDIT' | 'DELETE') => {
    setSelectedOption(key);

    if (key === 'EDIT') {
      setIsEditModalOpen(true);
    }
    if (key === 'DELETE') {
      setConfirmDeleteModal(true);
    }

    setIsOpen(false);
  };
  const deleteSelectedDevice = async () => {
    try {
      setIsResponseLoading(true);
      await deleteInventory(currentDevice.id);
      handleDeleteInventory();
      setIsDeleteToastMessage(true);
      setIsResponseLoading(false);
      setConfirmDeleteModal(false);
    } catch (error) {
      setIsResponseLoading(false);
    }
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown-container')) {
      setIsOpen(false);
    }
  };

  document.addEventListener('click', handleClickOutside);

  const handleDocumentClick = (e: MouseEvent) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };
  window.addEventListener('click', handleDocumentClick);

  const hasActionPermission =
    user &&
    (hasPermission(user, INVENTORY_MODULE.DELETE_DEVICE) ||
      hasPermission(user, INVENTORY_MODULE.UPDATE_DEVICE));
  useKeyPress(27, () => {
    setConfirmDeleteModal(false);
  });

  useEffect(() => {
    if (isEditModalOpen) {
      disableBodyScroll();
    } else {
      enableBodyScroll();
    }
  }, [isEditModalOpen]);

  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu
          className="action-align"
          onClick={() => {
            if (hasActionPermission) {
              openDropdown();
            }
          }}
        >
          <div
            style={{
              opacity: !hasActionPermission ? 0.3 : 1,
              cursor: !hasActionPermission ? 'not-allowed' : 'pointer',
            }}
          >
            <ActionIcon />
          </div>
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((option) => {
              const hasDeletePermission =
                option.key === 'DELETE' &&
                user &&
                hasPermission(user, INVENTORY_MODULE.DELETE_DEVICE);

              const hasEditPermission =
                option.key === 'EDIT' &&
                user &&
                hasPermission(user, INVENTORY_MODULE.UPDATE_DEVICE);

              if (!hasDeletePermission && !hasEditPermission) {
                return null;
              }

              return (
                <ActionMenuOption
                  key={option.key}
                  className={selectedOption === option.key ? 'selected' : ''}
                  onClick={() => handleOptionClick(option.key)}
                >
                  {option.svg}
                  {option.title}
                </ActionMenuOption>
              );
            })}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {confirmDeleteModal && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalLeftButtonClick={handleDeleteModal}
            handleModalClose={handleDeleteModal}
            handleModalSubmit={deleteSelectedDevice}
            modalHeading={t('DELETE')}
            modalContent={`Are you sure want to Delete the Inventory of ${currentDevice.deviceNumber}`}
          />
        </span>
      )}
      {isResponseLoading && <SpinAnimation />}
      {isDeletedToastMessage && (
        <ToastMessage
          messageType="success"
          messageHeading="Inventory Deleted"
          messageBody="Inventory Deleted Successfully"
          handleClose={handleIsDeleteToastMessage}
        />
      )}
      {isEditModalOpen && (
        <span style={{ cursor: 'default' }}>
          <CenterModalMain
            heading="Edit Inventory"
            modalClose={handleOpenEditModal}
            actualContentContainer={
              <EditInventoryForm
                handleClose={() => setIsEditModalOpen(false)}
                initialFormData={currentDevice}
                handleSuccessMessage={handleSuccessMessage}
                updateInventoryList={updateInventoryList}
                deviceTypes={deviceTypes}
                inventoryProviders={inventoryProviders}
              />
            }
          />
        </span>
      )}
    </>
  );
};

export default InventoryListAction;
