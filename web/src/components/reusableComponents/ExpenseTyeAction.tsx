import React, { useRef, useState } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  ActionContainer,
  ActionMenuContent,
  ActionMenuOption,
  ActionMenu,
} from '../../styles/ExpenseListStyles.style';
import { OrgValues } from '../../entities/OrgDefaultsEntity';

interface ActionOption {
  key: 'EDIT' | 'DELETE';
  title: string; // translated text
  svg: React.ReactNode;
}

interface ExpenseActionProps {
  options: ActionOption[];
  fetchExpenses: () => void;
  currentExpense: { orgValues: OrgValues; index: number };
  onActionClick: (
    action: 'EDIT' | 'DELETE',
    expense: { orgValues: OrgValues; index: number }
  ) => void;
}

export const ExpenseTypeAction: React.FC<ExpenseActionProps> = ({
  options,
  currentExpense,
  onActionClick,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<
    'EDIT' | 'DELETE' | null
  >(null);

  const dropdownRef = useRef<HTMLDivElement>(null);

  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleActionClick = (action: 'EDIT' | 'DELETE') => {
    setSelectedOption(action);
    onActionClick(action, currentExpense);
    setSelectedOption(null);
    setIsOpen(false);
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

  return (
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
              onClick={() => handleActionClick(option.key)}
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
