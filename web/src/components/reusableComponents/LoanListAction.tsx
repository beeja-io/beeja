import { useRef, useState } from 'react';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/DocumentTabStyles.style';
import { ActionIcon } from '../../svgs/DocumentTabSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import { Loan } from '../../entities/LoanEntity';
import CenterModalReject from './CenterModalReject';
import { statusChange } from '../../service/axiosInstance';
import ToastMessage from './ToastMessage.component';
interface ActionProps {
  options: {
    title: string;
  }[];
  fetchLoans: () => void;
  currentLoan: Loan;
}
export const LoanAction: React.FC<ActionProps> = ({
  options,
  fetchLoans,
  currentLoan,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmRejectModal, setConfirmRejectModal] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [approvedMessage, setApprovedMessage] = useState(false);
  const [rejectedMessage, setRejectMessage] = useState(false);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };
  const handleRejectModal = () => {
    setConfirmRejectModal(!confirmRejectModal);
  };
  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    if (option == 'Approve') {
      handleStatusApprove(currentLoan.id);
    }
    if (option == 'Reject') {
      handleRejectModal();
    }
    setIsOpen(false);
  };

  const handleStatusApprove = async (id: string) => {
    try {
      setIsResponseLoading(true);
      await statusChange(id, 'APPROVE');
      fetchLoans();
      setIsResponseLoading(false);
      handleApproveMessage();
    } catch {
      setIsResponseLoading(false);
    }
  };
  const handleModalSubmit = (reason: string) => {
    handleRejectLoan(currentLoan.id, reason);
  };
  const handleRejectLoan = async (id: string, message: string) => {
    try {
      handleRejectModal();
      setIsResponseLoading(true);
      await statusChange(id, 'REJECT', message);

      fetchLoans();
      setIsResponseLoading(false);
      handleRejectMessage();
    } catch {
      setIsResponseLoading(false);
    }
  };
  const handleApproveMessage = () => {
    setTimeout(() => {
      setApprovedMessage(true);
    }, 3000);
    setTimeout(() => {
      setApprovedMessage(false);
    }, 5000);
  };
  const handleRejectMessage = () => {
    setTimeout(() => {
      setRejectMessage(true);
    }, 3000);
    setTimeout(() => {
      setRejectMessage(false);
    }, 5000);
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
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu
          onClick={() => {
            if (
              currentLoan.status !== 'APPROVED' &&
              currentLoan.status !== 'REJECTED'
            ) {
              openDropdown();
            }
          }}
        >
          <div
            style={{
              opacity:
                currentLoan.status === 'APPROVED' ||
                currentLoan.status === 'REJECTED'
                  ? 0.3
                  : 1,
              cursor:
                currentLoan.status === 'APPROVED' ||
                currentLoan.status === 'REJECTED'
                  ? 'not-allowed'
                  : 'pointer',
            }}
          >
            <ActionIcon />
          </div>
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map((option, index) => (
              <ActionMenuOption
                key={index}
                className={selectedOption === option.title ? 'selected' : ''}
                onClick={() => handleOptionClick(option.title)}
              >
                {option.title}
              </ActionMenuOption>
            ))}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {isResponseLoading && <SpinAnimation />}
      {confirmRejectModal && (
        <span style={{ cursor: 'default' }}>
          <CenterModalReject
            handleModalClose={handleRejectModal}
            handleModalSubmit={handleModalSubmit}
            modalFieldText="Reason for Rejection"
            modalPlaceHolder="Type your Comment here"
            modalLeftButtonText="Cancel"
            modalRightButtonText="Done"
          />
        </span>
      )}
      {approvedMessage && (
        <ToastMessage
          messageType="success"
          messageBody="Approved loan successfully"
          messageHeading="Loan Approval Successful"
          handleClose={() => handleApproveMessage()}
        />
      )}
      {rejectedMessage && (
        <ToastMessage
          messageType="success"
          messageBody="Rejected loan successfully"
          messageHeading="Loan Rejection Successful"
          handleClose={() => handleRejectMessage()}
        />
      )}
    </>
  );
};
export default LoanAction;
