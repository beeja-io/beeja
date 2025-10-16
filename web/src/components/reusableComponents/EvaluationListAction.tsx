import React, { useState, useRef } from 'react';
import { ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/ExpenseListStyles.style';
import { useNavigate } from 'react-router-dom';
import { deletePerformanceCycle } from '../../service/axiosInstance';

interface Props {
  options: { title: string; svg: React.ReactNode }[];
  currentCycle: any;
  fetchCycles: () => void;
  onSuccess?: (message: string) => void;
  onError?: (message: string) => void;
}

const EvaluationListAction: React.FC<Props> = ({
  options,
  currentCycle,
  fetchCycles,
  onSuccess,
  onError,
}) => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const navigate = useNavigate();

  const handleOptionClick = async (title: string) => {
    if (title === 'Edit') {
      navigate(`/performance/create-evaluation-form/${currentCycle.id}`);
    }
    if (title === 'Delete') {
      try {
        await deletePerformanceCycle(currentCycle.id);
        onSuccess?.('Review cycle deleted successfully!');
        if (fetchCycles) fetchCycles();
      } catch (error: any) {
        onError?.(
          error.response?.data?.message || 'Failed to delete review cycle'
        );
      }
    }
    setOpen(false);
  };

  return (
    <ActionContainer ref={dropdownRef}>
      <ActionMenu className="action-align" onClick={() => setOpen(!open)}>
        <ActionIcon />
      </ActionMenu>

      {open && (
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
  );
};

export default EvaluationListAction;
