import React, { useState, useEffect, useRef } from 'react';
import {
  ContainerStyle,
  ContainerStyleOrg,
  ContainerStyleMulti,
  ToggleButtonStyle,
  DropdownListStyle,
  DropdownItemStyle,
  CheckIconStyle,
  ClearButton,
  SearchField,
  CloseButtonStyle,
  CustomInputContainer,
  CustomInputField,
  AddButton,
  ErrorText,
  NoResults,
  ArrowIconWrapper,
  EmployeeCountDropdownList,
} from '../../styles/DropDownMenu.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import { TickMark } from '../../styles/DocumentTabStyles.style';
import { TickmarkIcon } from '../../svgs/DocumentTabSvgs.svg';
import { CloseButtonSVG } from '../../svgs/profilePictureSvgs.svg';
import { t } from 'i18next';

type DropdownMenuProps = {
  label?: string;
  name?: string;
  id?: string;
  className?: string | null;
  options: { label: string; value: string | null; disabled?: boolean }[];
  value?: string | null;
  onChange?: (value: string | null) => void;
  disabled?: boolean;
  style?: React.CSSProperties;
  onKeyDown?: (event: React.KeyboardEvent<HTMLDivElement>) => void;
  required?: boolean;
  isMulti?: boolean;
  selected?: string;
  onValidationChange?: (isValid: boolean) => void;
  listClassName?: string;
  sortOptions?: boolean;
  onCustomValue?: (value: string) => void;
  width?: string;
  justify?: 'center' | 'space-between' | 'flex-start' | 'flex-end';
};

const DropdownMenu: React.FC<DropdownMenuProps> = ({
  label = 'Select an option',
  name = '',
  id = '',
  className = '',
  options,
  value = null,
  style,
  onKeyDown,
  onChange,
  disabled = false,
  required = false,
  onValidationChange,
  listClassName = '',
  sortOptions = true,
  onCustomValue,
  justify = 'space-between',
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState<string | null>(value);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [touched, setTouched] = useState(false);
  const [customValue, setCustomValue] = useState('');
  const [isOtherSelected, setIsOtherSelected] = useState(false);
  const [localOptions, setLocalOptions] = useState(options);
  const customInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOtherSelected && customInputRef.current) {
      customInputRef.current.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
      });
      customInputRef.current.focus();
    }
  }, [isOtherSelected]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node)
      ) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    setLocalOptions(options);
  }, [options]);

  useEffect(() => {
    const selectedValue = selected;
    const incomingHasSelected =
      selectedValue == null
        ? false
        : options.some((o) => o.value === selectedValue);

    if (selectedValue && !incomingHasSelected) {
      const withoutOther = options.filter(
        (opt) => opt.label.toLowerCase() !== 'other'
      );
      const hasOther = options.some(
        (opt) => opt.label.toLowerCase() === 'other'
      );

      const newLocal = [
        ...withoutOther,
        { label: selectedValue, value: selectedValue },
        ...(hasOther ? [{ label: 'Other', value: 'OTHER' }] : []),
      ];

      setLocalOptions(newLocal);
    } else {
      setLocalOptions(options);
    }
  }, [options, selected]);

  useEffect(() => {
    setSelected(value ?? null);

    if (required && onValidationChange) {
      const isValid = !!(value ?? null);
      onValidationChange(isValid);
    }
  }, [value, required, onValidationChange]);

  const handleSelect = (item: { label: string; value: string | null }) => {
    if (!item || !item.label) return;

    if (item.label.toLowerCase() === 'other') {
      setIsOtherSelected(true);
      setCustomValue('');
      setIsOpen(true);
      return;
    }
    setSelected(item.value);
    onChange?.(item.value);
    setIsOtherSelected(false);
    setIsOpen(false);
    setTouched(true);

    if (required && onValidationChange) {
      const isValid = !!item.value;
      onValidationChange(isValid);
    }
  };

  const handleAddCustomValue = () => {
    const trimmedValue = customValue?.trim();
    if (!trimmedValue) return;

    const alreadyExists = localOptions.some(
      (opt) => opt.value !== null && String(opt.value) === trimmedValue
    );

    let updatedOptions;
    if (alreadyExists) {
      updatedOptions = [...localOptions];
    } else {
      const withoutOther = localOptions.filter(
        (opt) => opt.label.toLowerCase() !== 'other'
      );
      updatedOptions = [
        ...withoutOther,
        { label: trimmedValue, value: trimmedValue },
        { label: 'Other', value: 'OTHER' },
      ];
    }
    setLocalOptions(updatedOptions);
    setSelected(trimmedValue);
    if (typeof onCustomValue === 'function') {
      onCustomValue(trimmedValue);
    }

    setIsOtherSelected(false);
    setIsOpen(false);
    setCustomValue('');
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  };
  const sortedOptions = (() => {
    if (!sortOptions) {
      return localOptions;
    }

    const tempOptions = [...localOptions];
    const otherIndex = tempOptions.findIndex(
      (opt) =>
        typeof opt.value === 'string' && opt.value.toUpperCase() === 'OTHER'
    );

    const otherOption = otherIndex !== -1 ? tempOptions[otherIndex] : null;
    const optionsWithoutOther =
      otherIndex !== -1
        ? tempOptions.filter((_, index) => index !== otherIndex)
        : tempOptions;

    optionsWithoutOther.sort((a, b) => {
      if (a.value === null || a.value === '') return -1;
      if (b.value === null || b.value === '') return 1;

      const aNum = Number(a.label);
      const bNum = Number(b.label);
      if (!isNaN(aNum) && !isNaN(bNum)) {
        return aNum - bNum;
      }
      return String(a.label).localeCompare(String(b.label), undefined, {
        sensitivity: 'base',
      });
    });

    if (otherOption) {
      optionsWithoutOther.push(otherOption);
    }

    return optionsWithoutOther;
  })();

  return (
    <div>
      <ContainerStyle
        ref={dropdownRef}
        onKeyDown={onKeyDown || handleKeyPress}
        hasValue={!!selected}
        className={`${className || ''} ${required && touched && !selected ? 'error-border' : ''}`}
        style={style}
      >
        <ToggleButtonStyle
          onClick={() => !disabled && setIsOpen(!isOpen)}
          disabled={disabled}
          justify={justify}
        >
          {localOptions.find((o) => o.value === selected)?.label ||
            selected ||
            t(label)}
          <ArrowIconWrapper isOpen={isOpen}>
            <ArrowDownSVG />
          </ArrowIconWrapper>
        </ToggleButtonStyle>

        {isOpen && !disabled && (
          <DropdownListStyle className={`${listClassName || ''}`}>
            {sortedOptions.map((item, index) => {
              const isSelected = selected === item.value;
              return (
                <DropdownItemStyle
                  key={item.value ?? index}
                  selected={isSelected}
                  disabled={item.disabled}
                  onClick={() => !item.disabled && handleSelect(item)}
                >
                  <span>{item.label}</span>
                  <CheckIconStyle selected={isSelected}>
                    <TickMark>
                      <TickmarkIcon />
                    </TickMark>
                  </CheckIconStyle>
                </DropdownItemStyle>
              );
            })}
            {isOtherSelected && (
              <CustomInputContainer>
                <CustomInputField
                  ref={customInputRef}
                  type="text"
                  value={customValue}
                  onChange={(e) => setCustomValue(e.target.value)}
                  placeholder={t('Enter specific type')}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleAddCustomValue();
                    }
                  }}
                />
                <AddButton
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleAddCustomValue();
                  }}
                >
                  <span>+</span> {t('Add')}
                </AddButton>
              </CustomInputContainer>
            )}
          </DropdownListStyle>
        )}

        {!disabled && name ? (
          <input
            type="hidden"
            name={name}
            id={id}
            value={selected ?? ''}
            required={required}
          />
        ) : null}
      </ContainerStyle>
      {required && touched && !selected && (
        <ErrorText>{t('This field is required')}</ErrorText>
      )}
    </div>
  );
};

export default DropdownMenu;

export const DropdownOrg: React.FC<DropdownMenuProps> = ({
  label = 'Select an option',
  name = '',
  id = '',
  className = '',
  options,
  value = null,
  selected: selectedProp,
  style,
  onKeyDown,
  onChange,
  disabled = false,
  required,
  listClassName = '',
}) => {
  const initialValue = selectedProp ?? value ?? null;
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState<string | null>(value ?? null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node)
      ) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    setSelected(initialValue);
  }, [initialValue]);

  const handleSelect = (item: { label: string; value: string | null }) => {
    setSelected(item.value);
    onChange?.(item.value);
    setIsOpen(false);
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  };

  return (
    <div>
      <ContainerStyleOrg
        ref={dropdownRef}
        onKeyDown={onKeyDown || handleKeyPress}
        className={className || ''}
        hasValue={!!selected}
        style={style}
      >
        <ToggleButtonStyle
          onClick={() => !disabled && setIsOpen(!isOpen)}
          disabled={disabled}
        >
          {options.find((o) => o.value === selected)?.label || t(label)}
          <ArrowIconWrapper isOpen={isOpen}>
            <ArrowDownSVG />
          </ArrowIconWrapper>
        </ToggleButtonStyle>

        {isOpen && !disabled && (
          <EmployeeCountDropdownList className={`${listClassName || ''}`}>
            {options.map((item, index) => {
              const isSelected = selected === item.value;
              return (
                <DropdownItemStyle
                  key={item.value ?? index}
                  selected={isSelected}
                  onClick={() => handleSelect(item)}
                >
                  <span>{item.label}</span>
                </DropdownItemStyle>
              );
            })}
          </EmployeeCountDropdownList>
        )}

        {!disabled && name ? (
          <input
            type="hidden"
            name={name}
            id={id}
            value={selected ?? ''}
            required={required}
          />
        ) : null}
      </ContainerStyleOrg>
    </div>
  );
};

interface MultiSelectDropdownProps {
  options: { label: string; value: string }[];
  value: { label: string; value: string }[];
  placeholder?: string;
  searchable?: boolean;
  className?: string | null;
  required?: boolean;
  onChange: (values: { label: string; value: string }[]) => void;
}

export const MultiSelectDropdown: React.FC<MultiSelectDropdownProps> = ({
  options,
  value,
  placeholder = 'Select Client',
  onChange,
  className = '',
  searchable = false,
  required = false,
}) => {
  const [open, setOpen] = useState(false);
  const [touched, setTouched] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const dropdownRef = useRef<HTMLDivElement>(null);

  const markTouched = () => {
    if (!touched) setTouched(true);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (option: { label: string; value: string }) => {
    markTouched();
    if (value.some((v) => v.value === option.value)) {
      onChange(value.filter((v) => v.value !== option.value));
    } else {
      onChange([...value, option]);
    }
  };

  const filteredOptions = searchable
    ? options.filter((item) =>
        item.label.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : options;
  const clearAll = () => {
    markTouched();
    onChange([]);
  };

  return (
    <div>
      <ContainerStyleMulti
        ref={dropdownRef}
        hasValue={value.length > 0}
        className={`${className || ''} ${
          required && touched && value.length === 0 ? 'error-border' : ''
        }
        `}
      >
        <ClearButton onClick={(e) => e.stopPropagation()}>
          {value.length > 0 && (
            <button className="clear" onClick={clearAll}>
              <CloseButtonStyle>
                <CloseButtonSVG />
              </CloseButtonStyle>
            </button>
          )}
          <ToggleButtonStyle
            onClick={() => {
              markTouched();
              setOpen(!open);
            }}
          >
            <span>
              {value.length > 0
                ? value.map((opt) => opt.label).join(', ')
                : placeholder}
            </span>
            <ArrowIconWrapper isOpen={open}>
              <ArrowDownSVG />
            </ArrowIconWrapper>
          </ToggleButtonStyle>
        </ClearButton>

        {open && (
          <DropdownListStyle>
            {searchable && (
              <div>
                <SearchField
                  type="text"
                  placeholder="Search..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            )}

            {filteredOptions.length > 0 ? (
              filteredOptions.map((item, index) => {
                const isSelected = value.some((v) => v.value === item.value);
                return (
                  <DropdownItemStyle
                    key={item.value ?? index}
                    selected={isSelected}
                    onClick={() => handleSelect(item)}
                  >
                    <span>{item.label}</span>
                    <CheckIconStyle selected={isSelected}>
                      <TickMark>
                        <TickmarkIcon />
                      </TickMark>
                    </CheckIconStyle>
                  </DropdownItemStyle>
                );
              })
            ) : (
              <NoResults>{t('No results found')}</NoResults>
            )}
          </DropdownListStyle>
        )}
      </ContainerStyleMulti>
      {required && touched && value.length === 0 && (
        <ErrorText>{t('This field is required')}</ErrorText>
      )}
    </div>
  );
};
