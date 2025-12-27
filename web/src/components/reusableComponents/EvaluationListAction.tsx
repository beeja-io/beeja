import React, { useEffect, useRef, useState } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/ExpenseListStyles.style';
import { useNavigate } from 'react-router-dom';
import { deletePerformanceCycle } from '../../service/axiosInstance';
import CenterModal from './CenterModal.component';
import { useTranslation } from 'react-i18next';
import { useUser } from '../../context/UserContext';
import { hasPermission } from '../../utils/permissionCheck';
import { PERFORMANCE_MODULE } from '../../constants/PermissionConstants';

interface Props {
  options: {
    key: string;
    title: string;
    svg: React.ReactNode;
    className: string;
  }[];
  currentCycle: any;
  fetchCycles: () => void;
  onSuccess?: (message: string) => void;
  onError?: (message: string) => void;
  isOpen: boolean;
  onToggle: () => void;
  setCycles: React.Dispatch<React.SetStateAction<any[]>>;
  disabled?: boolean;
}

const EvaluationListAction: React.FC<Props> = ({
  options,
  currentCycle,
  fetchCycles,
  onSuccess,
  onError,
  isOpen,
  onToggle,
  setCycles,
  disabled,
}) => {
  const dropdownRef = useRef<HTMLDivElement>(null);
  const { user } = useUser();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const handleDiscardModalToggle = () => {
    setShowDeleteModal(false);
  };

  const handleOptionClick = async (key: string) => {
    if (key === 'EDIT') {
      navigate(`/performance/create-evaluation-form/${currentCycle.id}`);
    }
    if (key === 'DELETE') {
      setShowDeleteModal(true);
    }
    onToggle();
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        isOpen &&
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        onToggle();
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, [isOpen, onToggle]);

  const handleConfirmDelete = async () => {
    setIsDeleting(true);
    try {
      await deletePerformanceCycle(currentCycle.id);
      setCycles((prev: any) =>
        prev.filter((cycle: any) => cycle.id !== currentCycle.id)
      );
      fetchCycles();
      onSuccess?.('Review cycle has been deleted successfully!');
    } catch (error: any) {
      onError?.(
        error.response?.data?.message || 'Failed to delete review cycle'
      );
    } finally {
      setIsDeleting(false);
      setShowDeleteModal(false);
    }
  };

  const canEdit =
    user && hasPermission(user, PERFORMANCE_MODULE.UPDATE_REVIEW_CYCLE);
  const canDelete =
    user && hasPermission(user, PERFORMANCE_MODULE.DELETE_REVIEW_CYCLE);

  return (
    <>
      <ActionContainer ref={dropdownRef}>
        <ActionMenu className="action-align" onClick={onToggle}>
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((op, i) => {
              const isDisabled =
                (op.key === 'EDIT' && (!canEdit || disabled)) ||
                (op.key === 'DELETE' && !canDelete);

              return (
                <ActionMenuOption
                  key={i}
                  className={isDisabled ? 'edit-disabled' : ''}
                  onClick={() => !isDisabled && handleOptionClick(op.key)}
                  title={isDisabled ? t('NO_PERMISSION_ACTION') : ''}
                >
                  <div className={op.className}>{op.svg}</div> {op.title}
                </ActionMenuOption>
              );
            })}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {showDeleteModal && (
        <CenterModal
          handleModalLeftButtonClick={handleDiscardModalToggle}
          handleModalClose={handleDiscardModalToggle}
          handleModalSubmit={handleConfirmDelete}
          modalHeading={t('DELETE_REVIEW_CYCLE')}
          modalContent={t('CONFIRM_DELETE_REVIEW_CYCLE')}
          modalType="discardModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
          modalLeftButtonText={t('No')}
          modalRightButtonText={isDeleting ? t('DELETING') : t('DELETE')}
        />
      )}
    </>
  );
};

export default EvaluationListAction;
