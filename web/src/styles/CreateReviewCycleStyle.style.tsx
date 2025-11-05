import { styled } from 'styled-components';

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
  .noBorder {
    border: none;
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
    margin: 25px 30px;
    border-radius: 10px;
    padding-top: 15px;
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

  &.answer-container {
    padding: 20px 10px 20px 10px;
    width: 98%;
    margin: 15px auto;
    background: transparent;
  }
  &.description-container {
    margin-bottom: 30px;
    background: transparent;
  }
`;

export const Container = styled.div`
  margin-left: 68px;
  margin-right: 68px;
`;

export const HeaderRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
`;

export const Label = styled.div`
  font-family: Inter;
  font-weight: 500;
  font-style: Medium;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 100%;
  letter-spacing: 0%;
  color: ${(props) => props.theme.colors.blackColors.black7};
`;

export const InputContainer = styled.div`
  width: 98%;
  border-bottom: 1px solid #e9eaec;
  padding-bottom: 5px;
`;

export const StyledInput = styled.input`
  width: 100%;
  border: none !important;
  outline: none;
  font-size: 14px;
  background: transparent !important;
  resize: none;
  color: ${(props) => props.theme.colors.brandColors.primary};

  &::placeholder {
    color: #999;
  }
`;

export const StyledTextArea = styled.textarea`
  width: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;
  resize: none;
  box-sizing: border-box;
  overflow: hidden;
  color: ${(props) => props.theme.colors.brandColors.primary};
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  font-family: Nunito;
  font-weight: 600;
  font-style: SemiBold;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  &.answer-color {
    color: ${(props) => props.theme.colors.grayColors.gray7};
  }
  &::placeholder {
    color: #999;
    position: absolute;
    bottom: 1px;
  }
`;

export const DescriptionButton = styled.button`
  display: flex;
  align-items: baseline;
  color: #005792;
  background: none;
  border: none;
  padding: 10px 0px 10px 25px;
  cursor: pointer;

  gap: 8px;

  font-family: Nunito;
  font-weight: 500;
  font-style: Medium;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 1.4;
  letter-spacing: 0.3px;

  .plus-box {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    background: #005792;
    color: #fff;
    font-size: 1rem;
    font-weight: bold;
    border-radius: 4px;
  }
  &:hover {
    text-decoration: underline;
  }
`;

export const QuestionBlock = styled.div`
  border-radius: 12px;
  padding: 1.2rem 0px;
  background-color: transparent;

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
    overflow: hidden;
    background: #ffff;
    border-bottom: 1px solid #a0aec0;
  }

  & > .question-input {
    border: none;
    border-bottom: 1px solid #a0aec0;
    padding: 0.6rem 0;
    margin-bottom: 1.5rem;
    width: 98%;
    margin: 15px auto;
    display: block;
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
    padding: 0 25px;
    background: transparent;

    height: 65px;
    border-radius: 10px;

    .add-btn {
      height: 45px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 15px;
      background-color: #005792;
      color: #fff;
      border: none;
      cursor: pointer;
      transition: background 0.2s ease;
      padding: 15px;

      &:hover {
        background: #005795;
      }
    }

    .actions {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      margin-left: auto;
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
  .date-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .error-text {
    color: red;
    font-size: 12px;
    margin-top: 4px;
  }
`;

export const DateRangeContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 1rem 0;

  .to-label {
    font-size: 14px;
    font-weight: 500;
    color: ${(props) => props.theme.colors.grayColors.gray7};
  }
  .date-wrapper {
    display: flex;
    flex-direction: column;
  }

  .error-text {
    color: red;
    font-size: 12px;
    margin-top: 4px;
  }
`;

export const DateField = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: ${(props) => props.theme.colors.grayColors.gray9};
  position: relative;

  > svg path {
    fill: ${(props) => props.theme.colors.grayColors.gray9};
  }

  &:hover svg path {
    fill: ${(props) => props.theme.colors.blackColors.black1};
  }
  span {
    font-size: 14px;
    color: ${(props) => props.theme.colors.grayColors.gray9};
  }

  .calendarSpace {
    position: absolute;
    top: 120%;
    left: 0;
    z-index: 10;
    background: #fff;
    border: 1px solid #eee;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
`;

export const TitleInput = styled.input`
  border: none;
  outline: none;
  background: transparent;
  width: 100%;
  max-width: 600px;

  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 24px;
  leading-trim: NONE;
  line-height: 150%;
  letter-spacing: 0.3px;
  text-align: center;
  color: ${(props) => props.theme.colors.brandColors.primary};
`;

export const DropdownRow = styled.div`
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 1rem;

  select {
    min-width: 200px;
    padding: 0.5rem 0.75rem;
    border: 1px solid #ddd;
    border-radius: 6px;
    background: #fff;
    font-size: 14px;
    cursor: pointer;

    &:focus {
      border-color: #007bff;
      outline: none;
    }
  }
  .dropdown-wrapper {
    display: flex;
    flex-direction: column;
  }

  .error-text {
    color: red;
    font-size: 12px;
    margin-top: 4px;
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
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  border: 1px solid var(--Outline-stroke, #e9eaec);
  margin: 40px 4% 0 4%;
  padding: 25px 5% 10px 5%;
  width: 100%;

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

  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 24px;
  leading-trim: NONE;
  line-height: 150%;
  letter-spacing: 0.3px;
  text-align: center;
  color: ${(props) => props.theme.colors.brandColors.primary};
  span {
    transform: rotate(90deg);
    display: inline-block;
    margin-right: 5px;
    cursor: pointer;
  }
`;

export const DateRow = styled.div`
  font-size: 14px;
  color: #6b7280;
  margin: 6px 0;
  svg path {
    fill: ${(props) => props.theme.colors.grayColors.gray9};
  }
`;

export const DateText = styled.span`
  display: inline-flex;
  align-items: center;
  gap: 6px;

  svg path {
    fill: ${(props) => props.theme.colors.grayColors.gray9};
  }
  span {
    margin: 0 8px;
  }
`;

export const Subtitle = styled.h3`
  font-family: Nunito;
  font-weight: 600;
  font-style: SemiBold;
  font-size: 20px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0.17px;
  text-align: center;
  color: ${(props) => props.theme.colors.blackColors.black7};
`;

export const DescriptionBox = styled.div`
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 12px;
  margin: 10px 0px 25px 0px;
  font-weight: 400;
  font-style: Regular;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  color: ${(props) => props.theme.colors.blackColors.black7};
  &.preview-mode {
    background: transparent;
  }
`;

export const Questions = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const QuestionText = styled.p`
  font-weight: 500;
  font-size: 15px;
  margin-bottom: 8px;
  color: ${(props) => props.theme.colors.brandColors.primary};
`;

export const SearchBox = styled.div`
  display: flex;
  width: 315px;
  padding: 8px 8px 8px 16px;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  height: 50px;
  flex-shrink: 0;
  align-self: stretch;
  border-radius: 10px;
  margin-bottom: 5px;
  background: ${(props) => props.theme.colors.grayColors.gray6};

  &.search .span {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 10px;
    gap: 15px;
    width: 310px;
    svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
  }
`;

export const QuestionDescription = styled.p`
  margin: 4px 0 10px 0;
  font-size: 0.9rem;
  color: #717171;

  font-family: Nunito;
  font-weight: 400;
  font-style: Regular;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
`;

export const RequiredMark = styled.span`
  color: #dc2626;
  margin-left: 4px;
`;

export const ActionMenu = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  height: 100%;
  padding: 8px;
  svg {
    display: block;
  }
`;
