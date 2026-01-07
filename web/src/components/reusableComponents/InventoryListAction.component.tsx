import React, { useEffect, useRef, useState } from 'react';
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
import EditInventoryForm from '../directComponents/EditInventory.component';
import { useUser } from '../../context/UserContext';
import { INVENTORY_MODULE } from '../../constants/PermissionConstants';
import { hasPermission } from '../../utils/permissionCheck';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import { disableBodyScroll, enableBodyScroll } from '../../constants/Utility';

interface ActionProps {
  options: {
    title: string;
    svg: React.ReactNode;
  }[];
  currentDevice: DeviceDetails;
  handleSuccessMessage: () => void;
  handleDeleteSuccess: () => void;
  updateInventoryList: () => void;
  deviceTypes: OrganizationValues;
  inventoryProviders: OrganizationValues;
}

export const InventoryListAction: React.FC<ActionProps> = ({
  options,
  currentDevice,
  handleSuccessMessage,
  handleDeleteSuccess,
  updateInventoryList,
  deviceTypes,
  inventoryProviders,
}) => {
  const { user } = useUser();
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const handleOpenEditModal = () => {
    setIsEditModalOpen(!isEditModalOpen);
  };

  const handleDeleteModal = () => {
    setConfirmDeleteModal(!confirmDeleteModal);
  };

  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    if (option === 'Edit') {
      setIsEditModalOpen(true);
    } else if (option === 'Delete') {
      setConfirmDeleteModal(true);
    }
    setIsOpen(false);
    setSelectedOption(null);
  };
  const deleteSelectedDevice = async () => {
    try {
      setIsResponseLoading(true);
      await deleteInventory(currentDevice.id);

      handleDeleteSuccess(); // ✅ notify parent ONLY

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
            {options.map((option, index) => {
              const hasDeletePermission =
                option.title === 'Delete' &&
                user &&
                hasPermission(user, INVENTORY_MODULE.DELETE_DEVICE);
              const hasEditPermission =
                option.title === 'Edit' &&
                user &&
                hasPermission(user, INVENTORY_MODULE.UPDATE_DEVICE);

              if (!hasDeletePermission && !hasEditPermission) {
                return null;
              }

              return (
                <ActionMenuOption
                  key={index}
                  className={selectedOption === option.title ? 'selected' : ''}
                  onClick={() => handleOptionClick(option.title)}
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
            modalHeading="Delete"
            modalContent={`Are you sure want to Delete the Inventory of ${currentDevice.deviceNumber}`}
          />
        </span>
      )}
      {isResponseLoading && <SpinAnimation />}
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
