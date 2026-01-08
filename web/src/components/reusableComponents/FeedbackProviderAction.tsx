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
import { useUser } from '../../context/UserContext';
import { hasPermission } from '../../utils/permissionCheck';
import { PERFORMANCE_MODULE } from '../../constants/PermissionConstants';
import { t } from 'i18next';

type ActionKey = 'ASSIGN' | 'REASSIGN' | 'VIEW';

interface FeedbackProviderActionProps {
  options: {
    key: ActionKey;
    title: string;
    svg: React.ReactNode;
    disabled: boolean;
  }[];
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
  const { user } = useUser();

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

  const handleOptionClick = async (key: ActionKey) => {
    try {
      if (key === 'ASSIGN' || key === 'REASSIGN') {
        handleAssign(currentEmployee);
      }

      if (key === 'VIEW') {
        navigate('/performance/view-more-details', {
          state: {
            employeeId: currentEmployee.employeeId,
            cycleId: currentEmployee.cycleId,
            receiverName: currentEmployee.fullName,
            fromReceiversList: true,
            fromReceiversListDirect: true,
            formName: currentEmployee.formName,
          },
        });
      }

      setIsOpen(false);
    } catch (error: any) {
      onError?.(error?.response?.data?.message || 'Action failed!');
    }
  };

  const getIsDisabled = (key: ActionKey): boolean => {
    if (!user) return true;

    switch (key) {
      case 'ASSIGN':
        return !hasPermission(user, PERFORMANCE_MODULE.ASSIGN_PROVIDER);

      case 'REASSIGN':
        return !hasPermission(user, PERFORMANCE_MODULE.UPDATE_PROVIDER);

      case 'VIEW':
        return !hasPermission(user, PERFORMANCE_MODULE.READ_PROVIDER);

      default:
        return false;
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
          {options.map((option, index) => {
            const isDisabled = getIsDisabled(option.key);
            const isActionDisabled = isDisabled || option.disabled;

            return (
              <ActionMenuOptions
                key={index}
                className={isActionDisabled ? 'disabled-action' : ''}
                onClick={() => {
                  if (!isActionDisabled) {
                    handleOptionClick(option.key);
                  }
                }}
                title={isActionDisabled ? t('No_Permission') : ''}
              >
                {option.svg}
                <span>{option.title}</span>
              </ActionMenuOptions>
            );
          })}
        </ActionMenuContent>
      )}
    </ActionContainer>
  );
};

export default FeedbackProviderAction;
