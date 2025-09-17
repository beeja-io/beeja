import { styled } from 'styled-components';
export const RowWrapper = styled.div`
  flex: 1 1 0;
  margin-left: 20px;
  margin-right: 40px;
  gap: 40px;
  display: flex;
  justify-content: center;
`;

export const FormContainer = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  border-radius: 10px;
  font-family: Nunito;

  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
    width: 100%;
  }
`;

export const FormInputsContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;

  flex-wrap: wrap;
  &.stepOne {
    width: 100%;
  }
  &.row-container {
    flex-direction: row;
    width: 100%;
    gap: 15px;
  }
  &.form-header {
    display: flex;
  }
  &.stepTwoContainer {
    border: 1px solid var(--Outline-stroke, #e9eaec);
    margin: 5px 30px;
    border-radius: 10px;
    padding-top: 15px;
  }
`;

export const AddFormMainContainer = styled.form`
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;

  .formInputs {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 40px;
  }

  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
    width: 100%;
    margin: 120px 40px 60px 30px;
  }

  .submitButton {
    background-color: #005792;
    color: #fff;
    border: none;

    display: flex;
    width: 162px;
    height: 56px;
    padding: 18px 24px;
    justify-content: center;
    align-items: center;
    gap: 8px;
    border-radius: 10px;
    outline: none;
    cursor: pointer;
  }
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  max-width: 75%;
  width: 50%;
  margin: 20px 0 20px 10px;
  padding-top: 15px;

  @media screen and (max-width: 1200px) {
    max-width: 30%;
    margin: 10px 0;
  }
`;
export const DateInputWrapper = styled.div`
  position: relative;
  max-width: 491px;
  width: 100%;
  height: 54px;

  .iconArea {
    position: absolute;
    top: 50%;
    right: 14px;
    transform: translateY(-50%);
    pointer-events: all;
    cursor: pointer;
    display: flex;
    align-items: center;
  }

  .calendarSpace {
    position: absolute;
    top: 60px;
    z-index: 10;
  }
`;
export const TextInput = styled.input`
  outline: none;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
  height: 54px;
  width: 100%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};

  &.largeInput {
    width: 491px;
  }

  &.disabledBgWhite {
    background-color: white;
  }
  &.grayText {
    color: #8b8b8b;
  }
`;

export const InputLabelContainer = styled.div<{ Width?: string }>`
  flex: 1;
  max-width: 491px;
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;

  label {
    font-family: Nunito;
    font-weight: 600;
    font-size: 14px;
    line-height: 160%;
    letter-spacing: 0px;
    color: ${(props) => props.theme.colors.blackColors.black5};
  }
  input {
    cursor: text;
  }
  .selectoption {
    outline: none;
    border-radius: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    display: flex;
    padding: 16px 20px;
    gap: 10px;
    align-items: flex-start;
    justify-content: space-between;
    align-self: stretch;
    min-height: 54px;
    width: ${(props) => (props.Width ? props.Width : '100%')};
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1em;
  }
  .tax-container {
    margin-right: 140px;
  }
  .selectoption.largeSelectOption {
    max-width: 491px;
    width: 100%;
    box-sizing: border-box;
  }

  .react-select__control {
    min-height: 54px !important;
    max-width: 491px;
    width: 100%;
    border-radius: 8px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  }

  span.calendarField {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  span.calendarField .iconArea {
    position: absolute;
    right: 20px;
  }
  div.calendarSpace {
    justify-self: flex-start;
    align-self: flex-start;
    position: absolute;
    left: 0;
    top: 55px;
    z-index: 1;
  }

  &.fileInputSelected {
    .selectedFilesMain {
      display: flex;
      flex-direction: row;
      flex-wrap: wrap;
      max-width: 491px;
      column-gap: 10px;
      font-size: 12px;
    }

    .redPointer {
      cursor: pointer;
    }
  }

  .fileFormatText {
    color: #687588;
    font-size: 12px;
  }

  .grayText {
    color: #687588;
    font-size: 12px;
  }
  select:disabled {
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  }
  input:disabled {
    background-color: ${(props) => props.theme.colors.blackColors.white6};
  }
  textarea:disabled {
    background-color: ${(props) => props.theme.colors.blackColors.white6};

    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  }
`;

export const FormLabelContainer = styled.div`
  width: 100%;
  padding: 20px 0px 10px 10px;
  border: 1px solid #e9eaec;
  border-radius: 10px;
  background: #fff;
  margin: 15px 0;
  gap: 10px;
`;

export const Container = styled.div`
  margin-left: 68px;
  margin-right: 68px;
`;
export const Label = styled.div`
  font-family: Nunito, sans-serif;
  font-weight: 600;
  font-size: 14px;
  line-height: 160%;
  color: #333;
  margin-bottom: 10px;
`;

export const InputContainer = styled.div`
  width: 100%;
  border-bottom: 1px solid #e9eaec;
  padding-bottom: 5px;

  &:focus-within {
    border-bottom: 2px solid #007bff;
  }
`;

export const StyledInput = styled.input`
  width: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;

  &::placeholder {
    color: #999;
  }
`;

export const QuestionBlock = styled.div`
  border-radius: 12px;
  padding: 1.2rem 0px;
  margin-bottom: 1rem;
  background: #fff;

  label {
    font-weight: 600;
    font-size: 13px;
    color: #444;
    margin-bottom: 4px;
  }

  input {
    width: 100%;
    font-size: 0.95rem;
    outline: none;
  }

  .question-input {
    border: none;
    border-bottom: 1px solid #a0aec0;
    padding: 0.6rem 0;
    margin-bottom: 1.5rem;
    background: transparent;

    height: 55px;
    angle: 0 deg;
    opacity: 1;
    border-bottom-width: 1px;
    font-family: Nunito;
    font-weight: 400;
    font-style: Regular;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    vertical-align: middle;

    &:focus {
      border-bottom: 1px solid #005792;
    }
  }

  .answer-input {
    height: 55px;
    margin-bottom: 1rem;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: #fafafa;

    &:focus {
      border-color: #005792;
      background: #fff;
    }
  }
  .required-toggle {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .question-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 15px;
    border-top: 1px solid #f0f0f0;
    background: #e2ecf399;
    height: 65px;
    border-radius: 10px;

    .add-btn {
      width: 96px;
      height: 45px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 5px;
      background: #005792;
      color: #fff;
      cursor: pointer;
      transition: background 0.2s ease;

      &:hover {
        background: #005795;
      }
    }

    .actions {
      display: flex;
      align-items: center;
      gap: 1rem;

      button {
        background: transparent;
        border: none;
        font-size: 1.2rem;
        cursor: pointer;

        &:hover {
          opacity: 0.7;
        }
      }

      label {
        display: flex;
        align-items: center;
        gap: 0.4rem;
        font-size: 0.85rem;
        color: #333;

        input[type='checkbox'] {
          accent-color: #005792;
          cursor: pointer;
        }
      }
    }
  }
`;

export const FormHeader = styled.div`
  text-align: center;
  margin-bottom: 2rem;

  h2 {
    font-family: Nunito;
    font-weight: 700;
    font-style: Bold;
    font-size: 24px;
    leading-trim: NONE;
    line-height: 150%;
    letter-spacing: 0.3px;
    text-align: center;
    color: #111827;
  }

  p {
    display: flex;
    align-items: center;
    justify-content: center;

    font-family: Nunito;
    font-weight: 400;
    font-style: Regular;
    font-size: 14px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    vertical-align: middle;
    gap: 10px;
    color: #687588;
    margin: 10px;
    svg {
      width: 14.5px;
      height: 16.25px;
    }
  }
  span {
    margin: 0px;
  }

  h3 {
    font-size: 1.2rem;
    font-weight: 500;
    color: #333;
    margin-top: 0.5rem;
  }
`;

export const FooterContainer = styled.div`
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 20px 0;
  min-width: 520px;
  .leftAlign {
    position: absolute;
    left: 0;
    display: flex;
    color: ${(props) => props.theme.colors.brandColors.primary};
    font-weight: 700;
    font-size: 16px;
    cursor: pointer;
  }
  .centerAlign {
    display: flex;
    gap: 12px;
  }
`;

export const ButtonGroup = styled.div`
  display: flex;
  gap: 12px;
  margin-bottom: 15px;
`;

export const PreviewWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  background: #00000080;
`;

export const PreviewCard = styled.div`
  background: white;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid var(--Outline-stroke, #e9eaec);
  max-width: 1246px;
  width: 100%;
  padding: 30px;
  max-height: 100vh;
  overflow-y: auto;
`;

export const Header = styled.div`
  text-align: center;
  margin-bottom: 20px;
`;

export const Title = styled.h2`
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #222;
`;

export const DateRow = styled.div`
  font-size: 14px;
  color: #6b7280;
  margin: 6px 0;
`;

export const DateText = styled.span`
  display: inline-flex;
  align-items: center;
  gap: 6px;
`;

export const Subtitle = styled.h3`
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #374151;
`;

export const DescriptionBox = styled.div`
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #374151;
`;

export const Questions = styled.div`
  display: flex;
  flex-direction: column;
  gap: 20px;
`;

export const QuestionText = styled.p`
  font-weight: 500;
  font-size: 15px;
  margin-bottom: 8px;
  color: #1d4ed8;
`;

export const RequiredMark = styled.span`
  color: #dc2626;
  margin-left: 4px;
`;

export const AnswerPlaceholder = styled.div`
  border: 1px solid #d1d5db;
  border-radius: 6px;
  min-height: 60px;
  padding: 10px;
  font-size: 14px;
  color: #9ca3af;
  background: #fff;
`;
