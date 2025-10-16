import React, { useState, useRef, useEffect } from 'react';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/DocumentTabStyles.style';

interface FeedbackProviderActionProps {
  options: { title: string; svg: React.ReactNode }[];
  currentEmployee: any;
  handleAssign: (employee: any) => void;
  handleMoreInfo: (employee: any) => void;
}

const FeedbackProviderAction: React.FC<FeedbackProviderActionProps> = ({
  options,
  currentEmployee,
  handleAssign,
  handleMoreInfo,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const toggleDropdown = () => setIsOpen((prev) => !prev);

  const handleClickOutside = (event: MouseEvent) => {
    if (!dropdownRef.current?.contains(event.target as Node)) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  const handleOptionClick = (optionTitle: string) => {
    if (optionTitle === 'Assign') {
      handleAssign(currentEmployee);
    }
    if (optionTitle === 'More Info') {
      handleMoreInfo(currentEmployee);
    }
    setIsOpen(false);
  };

  return (
    <ActionContainer ref={dropdownRef}>
      <ActionMenu onClick={toggleDropdown}>
        <ActionIcon />
      </ActionMenu>

      {isOpen && (
        <ActionMenuContent>
          {options.map((option, index) => (
            <ActionMenuOption
              key={index}
              onClick={() => handleOptionClick(option.title)}
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

export default FeedbackProviderAction;
