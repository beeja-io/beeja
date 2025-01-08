// styles/EmployeeIDCreate.style.ts
import styled from 'styled-components';

export const FormContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 25px;
  border-radius: 12px;
  margin: auto;
`;

export const FormGroup = styled.div`
  display: flex;
  align-items: center;
  gap: 20px;

  label {
    width: 20%;
    font-size: 1rem;
    font-weight: 600;
    white-space: nowrap;
  }

  input[type='text'],
  input[type='number'] {
    width: 470px;
    height: 54px;
    border-radius: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    padding-left: 15px;
    outline: none;
    background-color: transparent;
    color: inherit;
  }

  .radio-group {
    display: flex;
    gap: 60px;

    label {
      display: flex;
      align-items: center;
      font-size: 1rem;
    }

    input[type='radio'] {
      margin-right: 5px;
    }
  }
  .custom-text-style {
    font-family: 'Nunito', sans-serif;
    font-size: 10px;
    font-weight: 400;
    line-height: 12.8px;
    text-align: left;
    text-underline-position: from-font;
    text-decoration-skip-ink: none;
    color: #818181; /* or #FFFFFF if needed */
  }
`;

interface ToggleSwitchContainerProps {
  isChecked: boolean;
}

export const ToggleSwitchContainer = styled.div<ToggleSwitchContainerProps>`
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1rem;
  cursor: pointer;
  font-weight: 600;
  margin-top: 15px;

  .toggle-switch-container {
    display: flex;
    justify-content: flex-end;
    width: 20%;
  }

  .toggle-switch {
    position: relative;
    display: inline-block;
    width: 44px;
    height: 26px;
    background: ${({ isChecked }: { isChecked: boolean }) =>
      isChecked ? '#004080' : '#ccc'};
    border-radius: 1000px;
    transition: background-color 0.3s ease;
    padding: 2px;
  }

  .toggle-switch input {
    opacity: 0;
    width: 0;
    height: 0;
  }

  .toggle-switch svg {
    transform: ${({ isChecked }: { isChecked: boolean }) =>
      isChecked ? 'translateX(18px)' : 'translateX(0)'};
    transition: transform 0.3s ease;
  }
`;

export const ToggleInfoContainer = styled.div`
  display: flex;
  flex-direction: row; /* Place the SVG and text in a row */
  align-items: center;
  gap: 8px;
  /* margin-top: 2px; Space between toggle and info */
  margin-top: -20px;
  padding-left: 135px; /* Align the info text and SVG with the toggle button */
  /* padding-bottom: 50px; */
`;

export const ToggleInfoText = styled.span`
  font-family: 'Nunito', sans-serif;
  font-size: 10px;
  font-weight: 400;
  line-height: 12.8px;
  text-align: left;
  color: #818181;
`;

export const ButtonGroup = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;

  button {
    padding: 10px 25px;
    font-size: 1rem;
    font-weight: 600;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &.reset-btn {
      border: 1px solid #ccc;
      width: 145px;
      height: 56px;
    }

    &.submit-btn {
      background-color: #004080;
      color: white;
      width: 145px;
      height: 56px;

      &:hover {
        background-color: #002d5a;
      }
    }
  }
`;
