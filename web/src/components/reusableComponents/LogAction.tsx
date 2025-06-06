import { useRef, useState, useEffect } from 'react';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/DocumentTabStyles.style';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import CenterModal from './CenterModal.component'; 

interface LogActionProps {
  onEdit: () => void;
  onDelete: () => void;
}

const LogAction: React.FC<LogActionProps> = ({ onEdit, onDelete }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [confirmModalType, setConfirmModalType] = useState<'delete' | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleDocumentClick = (e: MouseEvent) => {
      if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    window.addEventListener('click', handleDocumentClick);
    return () => window.removeEventListener('click', handleDocumentClick);
  }, [isOpen]);

  const handleOptionClick = (action: 'edit' | 'delete') => {
    setIsOpen(false);
    if (action === 'edit') {
      onEdit();
    } else {
      setConfirmModalType('delete');
    }
  };

  const handleConfirmAction = () => {
    if (confirmModalType === 'delete') {
      onDelete();
    }
    setConfirmModalType(null);
  };

  const handleModalClose = () => {
    setConfirmModalType(null);
  };

  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu onClick={() => setIsOpen((prev) => !prev)}>
          <ActionIcon />
        </ActionMenu>

        {isOpen && (
          <ActionMenuContent>
            <ActionMenuOption onClick={() => handleOptionClick('edit')}>
              Edit
            </ActionMenuOption>
            <ActionMenuOption onClick={() => handleOptionClick('delete')}>
              Delete
            </ActionMenuOption>
          </ActionMenuContent>
        )}
      </ActionContainer>

      {confirmModalType === 'delete' && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalLeftButtonClick={handleModalClose}
            handleModalClose={handleModalClose}
            handleModalSubmit={handleConfirmAction}
            modalHeading="Delete Log"
            modalContent="Are you sure you want to delete this log entry?"
          />
        </span>
      )}
    </>
  );
};

export default LogAction;
