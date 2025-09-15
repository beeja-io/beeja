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
} from '../../styles/DropDownMenu.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import { TickMark } from '../../styles/DocumentTabStyles.style';
import { TickmarkIcon } from '../../svgs/DocumentTabSvgs.svg';
import { CloseButtonSVG } from '../../svgs/profilePictureSvgs.svg';

type DropdownMenuProps = {
  label?: string;
  name?: string;
  id?: string;
  className?: string | null;
  options: { label: string; value: string | null }[];
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
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState<string | null>(value);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [touched, setTouched] = useState(false);

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
    setSelected(value ?? null);
  }, [value]);

  useEffect(() => {
    setSelected(value ?? null);

    if (required && onValidationChange) {
      const isValid = !!(value ?? null);
      onValidationChange(isValid);
    }
  }, [value, required, onValidationChange]);

  const handleSelect = (item: { label: string; value: string | null }) => {
    setTouched(true);
    setSelected(item.value);
    onChange?.(item.value);
    setIsOpen(false);
    if (required && onValidationChange) {
      const isValid = !!item.value;
      onValidationChange(isValid);
    }
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  };
  const sortedOptions = [...options].sort((a, b) => {
    if (!a.value) return -1;
    if (!b.value) return 1;
    return String(a.label).localeCompare(String(b.label));
  });

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
        >
          {options.find((o) => o.value === selected)?.label || label}
          <ArrowDownSVG />
        </ToggleButtonStyle>

        {isOpen && !disabled && (
          <DropdownListStyle className={`${listClassName || ''}`}>
            {sortedOptions.map((item, index) => {
              const isSelected = selected === item.value;
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
            })}
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
        <div style={{ color: 'red', marginTop: '4px', fontSize: '12px' }}>
          This field is required
        </div>
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
  style,
  onKeyDown,
  onChange,
  disabled = false,
  required,
  listClassName = '',
}) => {
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
    setSelected(value ?? null);
  }, [value]);

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
          {options.find((o) => o.value === selected)?.label || label}
          <ArrowDownSVG />
        </ToggleButtonStyle>

        {isOpen && !disabled && (
          <DropdownListStyle className={`${listClassName || ''}`}>
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

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
        setTouched(true);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (option: { label: string; value: string }) => {
    setTouched(true);
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
    setTouched(true);
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
          <ToggleButtonStyle onClick={() => setOpen(!open)}>
            <span>
              {value.length > 0
                ? value.map((opt) => opt.label).join(', ')
                : placeholder}
            </span>
            <ArrowDownSVG />
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
              <div
                style={{ padding: '10px', textAlign: 'center', color: '#888' }}
              >
                No results found
              </div>
            )}
          </DropdownListStyle>
        )}
      </ContainerStyleMulti>
      {required && touched && value.length === 0 && (
        <div style={{ color: 'red', marginTop: '4px', fontSize: '12px' }}>
          This field is required
        </div>
      )}
    </div>
  );
};
