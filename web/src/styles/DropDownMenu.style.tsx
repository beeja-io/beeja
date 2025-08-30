import styled from 'styled-components';

export const ContainerStyle = styled.div<{
  Width?: string;
  hasValue?: boolean;
}>`
  position: relative;
  overflow: visible;
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 0.75rem;
  padding: 1rem;
  transition: all 0.2s ease-in-out;
  width: ${(props) => props.Width ?? '100%'};
  background-color: ${(props) => props.theme.colors.blackColors.white6};
  color: ${({ hasValue, theme }) =>
    hasValue
      ? theme.colors.blackColors.black1
      : theme.colors.grayColors.gray11};
  &.smallContainerDed {
    height: 45px;
  }
  &.largeContainerFil {
    width: 200px;
  }

  &.largeContainerExp {
    width: 490px;
  }

  &.largeContainerBulk {
    width: 400px;
  }

  &.largeContainerRes {
    width: 550px;
  }
  &.cursor-disabled {
    cursor: not-allowed !important;
    pointer-events: none;
  }
  &.error-border {
    border: 1px solid red !important;
  }
  &:hover {
    // Optional hover effect
    // box-shadow: 0 0 8px 0 ${(props) =>
      props.theme.colors.brandColors.primary};
  }
`;
export const ContainerStyleOrg = styled.div<{
  Width?: string;
  hasValue?: boolean;
}>`
  position: relative;
  overflow: visible;
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 0.75rem;
  padding: 0.5rem;
  transition: all 0.2s ease-in-out;
  width: ${(props) => props.Width ?? '100%'};
  background-color: ${(props) => props.theme.colors.blackColors.white6};
  color: ${({ hasValue, theme }) =>
    hasValue
      ? theme.colors.blackColors.black1
      : theme.colors.grayColors.gray11};

  &.largeContainerFil {
    width: 200px;
  }

  &.largeContainerExp {
    width: 490px;
  }

  &.largeContainerBulk {
    width: 400px;
  }

  &.largeContainerRes {
    width: 550px;
  }
  &.cursor-disabled {
    cursor: not-allowed !important;
    pointer-events: none;
  }
  &.error-border {
    border: 1px solid red !important;
  }
  &:hover {
    // Optional hover effect
    // box-shadow: 0 0 8px 0 ${(props) =>
      props.theme.colors.brandColors.primary};
  }
`;
export const ContainerStyleMulti = styled.div<{
  Width?: string;
  hasValue?: boolean;
}>`
  position: relative;
  overflow: visible;
  font-size: 0.875rem;
  font-weight: 500;
  height: 54px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 0.75rem;
  padding: 1rem;
  transition: all 0.2s ease-in-out;
  width: ${(props) => props.Width ?? '100%'};
  background-color: ${(props) => props.theme.colors.blackColors.white6};
  color: ${({ hasValue, theme }) =>
    hasValue
      ? theme.colors.blackColors.black1
      : theme.colors.grayColors.gray11};
  &.largeContainerFil {
    width: 200px;
  }

  &.largeContainerExp {
    width: 490px;
  }

  &.largeContainerBulk {
    width: 400px;
  }

  &.largeContainerRes {
    width: 550px;
  }

  &.cursor-disabled {
    cursor: not-allowed !important;
    pointer-events: none;
  }
  &.error-border {
    border: 1px solid red !important;
  }
  &:hover {
    // Optional hover effect
    // box-shadow: 0 0 8px 0 ${(props) =>
      props.theme.colors.brandColors.primary};
  }
`;
export const ToggleButtonStyle = styled.div<{ disabled?: boolean }>`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
  margin-left: +7px;
  margin-top: +5px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
`;

export const DropdownListStyle = styled.ul`
  position: absolute;
  margin-top: 0.5rem;
  width: 100%;
  top: 100%;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 0.5rem;
  margin-left: -12px;
  box-shadow: 5px 5px 50px 0 rgba(26, 32, 44, 0.06);
  overflow-y: auto;
  z-index: 1000;
`;
export const DropdownItemStyle = styled.li<{ selected: boolean }>`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  cursor: pointer;
  z-index: 999;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.blackColors.white6};
  font-weight: ${(props) => (props.selected ? 600 : 'normal')};

  &:hover {
    background-color: ${(props) => props.theme.colors.grayColors.gray6};
    color: ${(props) => props.theme.colors.blackColors.black7};
  }
`;

export const CheckIconStyle = styled.span<{ selected: boolean }>`
  opacity: ${(props) => (props.selected ? 1 : 0)};
  transition: opacity 0.2s ease-in-out;

  ${DropdownItemStyle}:hover & {
    opacity: 1;
  }
`;

export const ClearButton = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  .clear,
  .arrow {
    background: transparent;
    border: 0;
    cursor: pointer;
    color: ${(props) => props.theme.colors.grayColors.gray3};
    font-size: 20px;
  }
`;

export const SearchField = styled.input`
  width: 100%;
  padding: 6px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: ${(props) => props.theme.colors.primary || '#007bff'};
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.2);
  }
`;

export const CloseButtonStyle = styled.button`
  position: absolute;
  top: 7px;
  right: 30px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: black;
  width: 15px;
  height: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-right: 2px solid ${(props) => props.theme.colors.grayColors.gray4};
  height: 40px;
`;
