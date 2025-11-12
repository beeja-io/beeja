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

interface Props {
  options: { title: string; svg: React.ReactNode }[];
  currentCycle: any;
  fetchCycles: () => void;
  onSuccess?: (message: string) => void;
  onError?: (message: string) => void;
  isOpen: boolean;
  onToggle: () => void;
  setCycles: React.Dispatch<React.SetStateAction<any[]>>;
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
}) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  const navigate = useNavigate();
  const { t } = useTranslation();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const handleDiscardModalToggle = () => {
    setShowDeleteModal(false);
  };

  const handleOptionClick = async (title: string) => {
    if (title === 'Edit') {
      navigate(`/performance/create-evaluation-form/${currentCycle.id}`);
    }
    if (title === 'Delete') {
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

  return (
    <>
      <ActionContainer ref={dropdownRef}>
        <ActionMenu className="action-align" onClick={onToggle}>
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((op, i) => (
              <ActionMenuOption
                key={i}
                onClick={() => handleOptionClick(op.title)}
              >
                {op.svg} {op.title}
              </ActionMenuOption>
            ))}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {showDeleteModal && (
        <CenterModal
          handleModalLeftButtonClick={handleDiscardModalToggle}
          handleModalClose={handleDiscardModalToggle}
          handleModalSubmit={handleConfirmDelete}
          modalHeading={t('Delete Review Cycle?')}
          modalContent={t('Are you sure you want to delete this review cycle?')}
          modalType="discardModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
          modalLeftButtonText={t('No')}
          modalRightButtonText={isDeleting ? t('Deleting...') : t('Delete')}
        />
      )}
    </>
  );
};

export default EvaluationListAction;
