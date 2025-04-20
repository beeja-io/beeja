import { styled } from "styled-components";

export const ArrowButton = styled.span<{ isDisabled: boolean }>`
  background: none;
  border: none;
  cursor: ${({ isDisabled }) => (isDisabled ? "not-allowed" : "pointer")};
  color: ${({ isDisabled }) => (isDisabled ? "#A0AEC0" : "#000")};
  opacity: ${({ isDisabled }) => (isDisabled ? "0.4" : "1")};
  font-size: 16px;
  display: flex;
`;

export const DayCell = styled.div<{
  isDisabled: boolean;
  isSelected?: boolean;
}>`
  text-align: center;
  padding: 8px;
  cursor: ${(props) => (props.isDisabled ? "not-allowed" : "pointer")};
  color: ${(props) =>
    props.isDisabled ? "#A0AEC0" : props.theme.colors.blackColors.black1};

  &:hover {
    border-radius: 10px;
    background: #005792;
    color: white;
  }
  ${(props) =>
    props.isSelected &&
    `
    border-radius: 10px;
    background: #005792;
    color: white;
  `}
`;
