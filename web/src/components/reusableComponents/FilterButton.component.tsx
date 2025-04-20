import React, { useRef, useState } from "react";
/* eslint-disable */
import {
  FilterDropdown,
  DropdownContainer,
  DropdownContent,
  DropdownOption,
  TickMark,
} from "../../styles/DocumentTabStyles.style";
import { TickmarkIcon } from "../../svgs/DocumentTabSvgs.svg";

interface FilterButtonProps {
  options: string[];
  // onOptionSelect: (selectedOption: string) => void;
  name: string;
  icon?: React.ReactNode;
}

export const FilterButton: React.FC<FilterButtonProps> = ({
  options,
  name,
  icon,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
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

  const handleDocumentClick = (e: any) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };

  window.addEventListener("click", handleDocumentClick);

  // if(isOpen){
  //   document.body.style.overflow = 'hidden';
  // }
  // else{
  //   document.body.style.overflow = 'unset';
  // }

  return (
    <DropdownContainer className="dropdown-container" ref={dropdownRef}>
      <FilterDropdown onClick={openDropdown}>
        {icon}
        {name}
      </FilterDropdown>
      {isOpen && (
        <DropdownContent /*appendTo={document.body}*/>
          {options.map((option, index) => (
            <DropdownOption
              key={index}
              className={selectedOption === option ? "selected" : ""}
              onClick={() => handleOptionClick(option)}
            >
              {option}
              {selectedOption === option && (
                <TickMark>
                  <TickmarkIcon />
                </TickMark>
              )}
            </DropdownOption>
          ))}
        </DropdownContent>
      )}
    </DropdownContainer>
  );
};
