import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import {
  ActionContainer,
  ActionMenuContent,
} from '../../styles/ExpenseListStyles.style';
import {
  ActionMenuIcon,
  ActionMenuOptions,
} from '../../styles/AssignFeedbackReceiversProvidersStyle.style';

interface FeedbackProviderActionProps {
  options: { title: string; svg: React.ReactNode }[];
  currentEmployee: any;
  handleAssign: (employee: any) => void;
  onSuccess?: (msg: string) => void;
  onError?: (msg: string) => void;
}

const FeedbackProviderAction: React.FC<FeedbackProviderActionProps> = ({
  options,
  currentEmployee,
  handleAssign,
  onError,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

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

  const handleOptionClick = async (title: string) => {
    try {
      if (
        title === 'Assign Feedback Providers' ||
        title === 'Reassign Feedback Providers'
      ) {
        handleAssign(currentEmployee);
      }

      if (title === 'View More Details') {
        navigate('/performance/view-more-details', {
          state: {
            employeeId: currentEmployee.employeeId,
            cycleId: currentEmployee.cycleId,
            receiverName: currentEmployee.fullName,
            fromReceiversList: true,
            fromReceiversListDirect: true,
          },
        });
      }

      setIsOpen(false);
    } catch (error: any) {
      onError?.(error?.response?.data?.message || 'Action failed!');
    }
  };

  return (
    <ActionContainer ref={dropdownRef}>
      <ActionMenuIcon
        className="action-align-feedback"
        onClick={toggleDropdown}
      >
        <ActionIcon />
      </ActionMenuIcon>

      {isOpen && (
        <ActionMenuContent>
          {options.map((option, index) => (
            <ActionMenuOptions
              key={index}
              onClick={() => handleOptionClick(option.title)}
            >
              {option.svg}
              <span>{option.title}</span>
            </ActionMenuOptions>
          ))}
        </ActionMenuContent>
      )}
    </ActionContainer>
  );
};

export default FeedbackProviderAction;
