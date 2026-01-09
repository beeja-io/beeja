import React, { useEffect, useRef, useState } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
/* eslint-disable */
import {
  ActionContainer,
  ActionMenuContent,
  ActionMenuOption,
  ActionMenu,
} from '../../styles/ExpenseListStyles.style';
import { useTranslation } from 'react-i18next';
import { deleteExpense } from '../../service/axiosInstance';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import ToastMessage from './ToastMessage.component';
import { Expense } from '../../entities/ExpenseEntity';
import CenterModalMain from './CenterModalMain.component';
import AddExpenseForm from '../directComponents/AddExpenseForm.component';
import CenterModal from './CenterModal.component';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import { disableBodyScroll, enableBodyScroll } from '../../constants/Utility';

interface ActionProps {
  options: {
    key: 'EDIT' | 'DELETE';
    title: string; // translated text
    svg: React.ReactNode;
  }[];
  fetchExpenses: () => void;
  currentExpense: Expense;
  expenseCategories: OrganizationValues;
  expenseDepartments: OrganizationValues;
  expenseTypes: OrganizationValues;
  expensePaymentModes: OrganizationValues;
  // onOptionSelect: (selectedOption: string) => void;
}

export const ExpenseAction: React.FC<ActionProps> = ({
  options,
  fetchExpenses,
  currentExpense,
  expenseCategories,
  expenseDepartments,
  expensePaymentModes,
  expenseTypes,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const { t } = useTranslation();
  const [selectedOption, setSelectedOption] = useState<
    'EDIT' | 'DELETE' | null
  >(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const handleIsEditModalOpen = () => {
    setIsEditModalOpen(!isEditModalOpen);
  };

  const [isDeletedToastMessage, setIsDeleteToastMessage] = useState(false);
  const handleIsDeleteToastMessage = () => {
    setIsDeleteToastMessage(!isDeletedToastMessage);
  };

  const handleDeleteModal = () => {
    setConfirmDeleteModal(!confirmDeleteModal);
  };

  const deleteSelectedExpense = async (fileId: string) => {
    try {
      setIsResponseLoading(true);
      await deleteExpense(fileId);
      handleIsDeleteToastMessage();
      fetchExpenses();
    } catch (error) {
      setIsResponseLoading(false);
      console.error('Error deleting expense:', error);
    }
  };
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(!showSuccessMessage);
  };
  const handleOptionClick = (action: 'EDIT' | 'DELETE') => {
    setSelectedOption(action);

    if (action === 'DELETE') {
      handleDeleteModal();
    }

    if (action === 'EDIT') {
      handleIsEditModalOpen();
    }

    setIsOpen(false);
    setSelectedOption(null);
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown-container')) {
      setIsOpen(false);
    }
  };

  document.addEventListener('click', handleClickOutside);

  const handleDocumentClick = (e: any) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };

  window.addEventListener('click', handleDocumentClick);
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
        <ActionMenu onClick={openDropdown} className="action-align">
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((option) => (
              <ActionMenuOption
                key={option.key}
                className={selectedOption === option.key ? 'selected' : ''}
                onClick={() => handleOptionClick(option.key)}
              >
                {option.svg}
                {option.title}
              </ActionMenuOption>
            ))}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {confirmDeleteModal && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalLeftButtonClick={handleDeleteModal}
            handleModalClose={handleDeleteModal}
            handleModalSubmit={() => deleteSelectedExpense(currentExpense.id)}
            modalHeading={t('DELETE')}
            modalContent={t('CONFIRM_DELETE_EXPENSE', {
              amount: currentExpense.amount,
            })}
          />
        </span>
      )}
      {isResponseLoading && <SpinAnimation />}
      {isDeletedToastMessage && (
        <ToastMessage
          messageType="success"
          messageHeading="Expense Deleted"
          messageBody="Expense Deleted Succesfully"
          handleClose={handleIsDeleteToastMessage}
        />
      )}
      {isEditModalOpen && (
        <span style={{ cursor: 'default' }}>
          <CenterModalMain
            heading="Edit Expense"
            modalClose={handleIsEditModalOpen}
            actualContentContainer={
              <AddExpenseForm
                handleClose={handleIsEditModalOpen}
                handleLoadExpenses={fetchExpenses}
                handleShowSuccessMessage={handleShowSuccessMessage}
                mode="edit"
                expense={currentExpense}
                expenseCategories={expenseCategories}
                expenseTypes={expenseTypes}
                expenseDepartments={expenseDepartments}
                expensePaymentModes={expensePaymentModes}
              />
            }
          />
        </span>
      )}
    </>
  );
};
