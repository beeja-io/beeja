import React, { useRef, useState } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
/* eslint-disable */
import {
  ActionContainer,
  ActionMenuContent,
  ActionMenuOption,
  ActionMenu,
} from '../../styles/ExpenseListStyles.style';
import { deleteExpense } from '../../service/axiosInstance';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import ToastMessage from './ToastMessage.component';
import { Expense } from '../../entities/ExpenseEntity';
import CenterModalMain from './CenterModalMain.component';
import AddExpenseForm from '../directComponents/AddExpenseForm.component';
import CenterModal from './CenterModal.component';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import { useTranslation } from 'react-i18next';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';

interface ActionProps {
  options: {
    title: string;
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
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const { t } = useTranslation();

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

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    if (option == 'Delete') {
      handleDeleteModal();
    }
    if (option == 'Edit') {
      handleIsEditModalOpen();
    }
    setIsOpen(false);
    // onOptionSelect(option);
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
  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu onClick={openDropdown}>
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((option, index) => (
              <ActionMenuOption
                key={index}
                className={selectedOption === option.title ? 'selected' : ''}
                onClick={() => handleOptionClick(option.title)}
              >
                {option.svg}
                {t(option.title.toUpperCase())}
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
            modalContent={`${t('DELETE_EXPENSE_CONFIRMATION')} ${currentExpense.amount}`}
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
            heading={t('EDIT_EXPENSE')}
            modalClose={handleIsEditModalOpen}
            actualContentContainer={
              <AddExpenseForm
                handleClose={handleIsEditModalOpen}
                handleLoadExpenses={fetchExpenses}
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
