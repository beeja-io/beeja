import React, { useRef, useState } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
/* eslint-disable */
import {
  ActionContainer,
  ActionMenuContent,
  ActionMenuOption,
  ActionMenu,
} from '../../styles/ExpenseListStyles.style';
import { OrgValues } from '../../entities/OrgDefaultsEntity';

interface ActionOption {
  title: string;
  svg: React.ReactNode;
}

interface ExpenseActionProps {
  options: ActionOption[];
  fetchExpenses: () => void;
  currentExpense: { orgValues: OrgValues; index: number };
  onActionClick: (
    action: string,
    expense: { orgValues: OrgValues; index: number }
  ) => void; // Callback to handle action
}

export const ExpenseTypeAction: React.FC<ExpenseActionProps> = ({
  options,
  currentExpense,
  onActionClick,
}) => {
  const handleActionClick = (action: string) => {
    setSelectedOption(action);
    onActionClick(action, currentExpense);
    setIsOpen(false);
  };
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
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

  return (
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
              onClick={() => handleActionClick(option.title)}
            >
              {option.svg}
              {option.title}
            </ActionMenuOption>
          ))}
        </ActionMenuContent>
      )}
    </ActionContainer>
  );
};
