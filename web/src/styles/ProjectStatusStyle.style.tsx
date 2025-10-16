import React, { useState } from 'react';
import styled from 'styled-components';
import { ProjectStatus } from '.././utils/projectStatus';
import { StatusIcon } from '../svgs/ClientManagmentSvgs.svg';

const DropdownWrapper = styled.div`
  position: relative;
  display: flex;
  align-items: center;
`;

const Selected = styled.div<{ color: string; bgColor: string }>`
  padding: 4px 12px;
  background-color: ${({ bgColor }) => bgColor};
  color: ${({ color }) => color};
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  cursor: pointer;
  min-width: 120px;
`;

const Dot = styled.span<{ color: string }>`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: ${({ color }) => color};
`;

const DropdownArrow = styled.div`
  cursor: pointer;
  margin-left: 8px;
  display: flex;
  align-items: center;
`;

const DropdownList = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 4px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  width: max-content;
`;

const DropdownItem = styled.div<{ color: string; bgColor: string }>`
  padding: 6px 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  cursor: pointer;
  background-color: ${({ bgColor }) => bgColor};
  color: ${({ color }) => color};
  border-radius: 6px;
  margin: 4px;

  &:hover {
    opacity: 0.7;
  }
`;

interface Props {
  value: string;
  onChange: (newValue: string) => void;
  disabled?: boolean;
}

const StatusDropdown: React.FC<Props> = ({ value, onChange, disabled }) => {
  const [open, setOpen] = useState(false);

  const selected = ProjectStatus.find((s) => s.value === value);
  const toggleDropdown = () => {
    if (!disabled) {
      setOpen(!open);
    }
  };

  return (
    <DropdownWrapper>
      <Selected
        onClick={() => {
          if (!disabled) {
            setOpen(!open);
          }
        }}
        color={selected?.color || '#000'}
        bgColor={selected?.bgColor || '#fff'}
        style={{
          cursor: disabled ? 'not-allowed' : 'pointer',
          opacity: disabled ? 0.6 : 1,
        }}
      >
        <Dot color={selected?.color || '#000'} />
        <span>{selected?.label || 'Select'}</span>
      </Selected>

      {!disabled && (
        <DropdownArrow onClick={toggleDropdown}>
          <StatusIcon open={open} />
        </DropdownArrow>
      )}

      {open && !disabled && (
        <DropdownList>
          {ProjectStatus.map((status) => (
            <DropdownItem
              key={status.value}
              color={status.color}
              bgColor={status.bgColor}
              onClick={() => {
                if (!disabled) {
                  onChange(status.value);
                  setOpen(false);
                }
              }}
            >
              <Dot color={status.color} />
              {status.label}
            </DropdownItem>
          ))}
        </DropdownList>
      )}
    </DropdownWrapper>
  );
};

export default StatusDropdown;
