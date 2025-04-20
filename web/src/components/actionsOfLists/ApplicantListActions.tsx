import { useRef, useState } from "react";
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from "../../styles/DocumentTabStyles.style";
import { ActionIcon } from "../../svgs/DocumentTabSvgs.svg";
import ToastMessage from "../reusableComponents/ToastMessage.component";
import { IApplicant } from "../../entities/ApplicantEntity";
import { useNavigate } from "react-router-dom";

interface ApplicantListActionsProps {
  applicant: IApplicant;
  options: {
    title: string;
    svg: React.ReactNode;
  }[];
  // onOptionSelect: (selectedOption: string) => void;
}

const ApplicantListActions = (props: ApplicantListActionsProps) => {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const [isDeletedToastMessage, setIsDeleteToastMessage] = useState(false);
  const handleIsDeleteToastMessage = () => {
    setIsDeleteToastMessage(!isDeletedToastMessage);
  };

  const handleDeleteModal = () => {
    setConfirmDeleteModal(!confirmDeleteModal);
  };

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    if (option == "Delete") {
      handleDeleteModal();
    }
    if (option == "Edit") {
      navigate(`/recruitment/hiring-management/${props.applicant.id}`);
    }
    setIsOpen(false);
    // onOptionSelect(option);
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (!target.closest(".dropdown-container")) {
      setIsOpen(false);
    }
  };

  document.addEventListener("click", handleClickOutside);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleDocumentClick = (e: any) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };

  window.addEventListener("click", handleDocumentClick);

  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu onClick={openDropdown}>
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {props.options.map((option, index) => (
              <ActionMenuOption
                key={index}
                className={selectedOption === option.title ? "selected" : ""}
                onClick={() => handleOptionClick(option.title)}
              >
                {option.svg}
                {option.title}
              </ActionMenuOption>
            ))}
          </ActionMenuContent>
        )}
      </ActionContainer>

      {isDeletedToastMessage && (
        <ToastMessage
          messageType="success"
          messageHeading="Expense Deleted"
          messageBody="Expense Deleted Succesfully"
          handleClose={handleIsDeleteToastMessage}
        />
      )}
    </>
  );
};

export default ApplicantListActions;
