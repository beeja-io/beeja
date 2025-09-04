import styled from 'styled-components';
import StatusDropdown from './ProjectStatusStyle.style';

export const Select = styled.select`
  padding: 0.6rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
  background-color: #fff;

  &:focus {
    border-color: #3182ce;
    outline: none;
  }
`;

export const ClientInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const LogoPreview = styled.div`
  img {
    width: 48px;
    height: 48px;
    object-fit: cover;
    border-radius: 50%;
  }
`;

export const InfoText = styled.div`
  line-height: 1.4;
  display: flex;
  flex-direction: column;

  .id {
    font-size: 12px;
    color: #6b7280;

    span {
      color: #007bff;
      font-weight: 500;
    }
  }

  .name {
    font-size: 14px;
    font-weight: 600;
    color: ${(props) => props.theme.colors.blackColors.black7};

    font-family: Nunito;
    font-weight: 600;
    font-style: SemiBold;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    vertical-align: middle;
  }

  .industry {
    font-size: 12px;
    color: #65676d !important;
  }
`;

export const RightSectionDiv = styled.div`
  width: 315;
  height: 218;
  angle: 0 deg;
  opacity: 1;
  gap: 32px;
  border-width: 1px;
  padding-top: 28px;
  padding-right: 24px;
  padding-bottom: 28px;
  padding-left: 24px;
  border-top-left-radius: 16px;
  border-bottom-left-radius: 16px;
  background-color: #ffffff;
`;

export const IconWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  div {
    display: flex;
    align-items: center;
    gap: 8px;
    word-break: break-all;
    white-space: normal;
    max-width: 100%;
  }

  svg {
    width: 19px;
    height: 16px;
  }
`;
export const Button = styled.button<{
  width?: string;
  padding?: string;
  height?: string;
  backgroundColor?: string;
  border?: string;
  color?: string;
  fontSize?: string;
}>`
  display: flex;
  width: ${(props) => (props.width ? props.width : '162px')};
  height: ${(props) => (props.height ? props.height : '56px')};
  padding: ${(props) => (props.padding ? props.padding : '18px 24px')};
  justify-content: center;
  align-items: center;
  gap: 8px;
  border-radius: 10px;
  border: ${(props) =>
    props.border
      ? props.border
      : `1px solid ${props.theme.colors.blackColors.black4}`};
  background-color: ${(props) =>
    props.backgroundColor
      ? props.backgroundColor
      : props.theme.colors.blackColors.white6};
  color: ${(props) =>
    props.color ? props.color : props.theme.colors.blackColors.black1};
  outline: none;
  cursor: pointer;
  font-size: ${(props) => (props.fontSize ? props.fontSize : '12px')};
  white-space: nowrap;

  &.submit {
    background-color: #005792;
    color: #fff;
    border: none;
    font-family: Nunito;
    font-weight: 350;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 150%;
    letter-spacing: 0.3px;
    text-align: center;
    width: 180px;
  }
  &.cancel {
    font-family: Nunito;
    font-weight: 600;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 150%;
    letter-spacing: 0.3px;
    text-align: center;
    width: 180px;
  }
`;

export const FormContainer = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  padding: 24px 32px;
  border-radius: 10px;
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.08);
  width: 100%;
  margin: 10px 10px;
`;

export const SectionTitle = styled.h2`
  font-family: Nunito;
  font-weight: 600;
  font-style: SemiBold;
  font-size: 18px;
  leading-trim: NONE;
  line-height: 150%;
  letter-spacing: 0px;
  margin-bottom: 25px;
  height: 27px;
  angle: 0 deg;
  opacity: 1;
  left: 60.09px;
  color: #005792;
  padding-left: 20px;
  gap: 32px;
`;

export const FormField = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Label = styled.label`
  font-size: 14px;
  font-weight: 600;
  height: 24px;
  color: ${(props) => props.theme.colors.blackColors.black5};
`;

export const RequiredAsterisk = styled.span`
  color: red;
  margin-left: 4px;
`;

export const Input = styled.input`
  width: 433px;
  height: 54px;
  padding: 10px 14px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

export const SelectWrapper = styled.div`
  width: 433px;
  border-radius: 8px;

  .react-select__control {
    min-height: 54px;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    box-shadow: none;
  }
`;

export const RightSection = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 250px;
  background: ${(props) => props.theme.colors.blackColors.white6};
  width: 100%;
  max-width: 390px;

  angle: 0 deg;
  opacity: 1;
  padding-top: 36px;
  padding-right: 24px;
  padding-bottom: 36px;
  padding-left: 24px;
  gap: 32px;
  border-radius: 16px;
`;

export const TextArea = styled.textarea`
  max-width: 491px;
  height: 54px;
  padding: 10px 14px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

export const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 32px;
  gap: 16px;
`;
export const CancelButton = styled.button`
  padding: 10px 24px;
  font-size: 14px;
  border: 1px solid #c5c5c5;
  background-color: white;
  border-radius: 8px;
  color: #333;
  cursor: pointer;
  &:hover {
    background-color: #f4f4f4;
  }
`;

export const SubmitButton = styled.button`
  padding: 10px 24px;
  font-size: 14px;
  background-color: #004b8d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  &:hover {
    background-color: #003866;
  }
`;

export const TextInput = styled.input`
  width: 100%;
  max-width: 491px;
  height: 54px;
  padding: 10px 14px;
  padding-right: 40px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};

  &.editText {
    max-width: 1022px;
    width: 100%;
  }

  &:disabled,
  &.disabled {
    background-color: ${(props) => props.theme.colors.grayColors.gray6};
    cursor: not-allowed;
    color: ${(props) => props.theme.colors.grayColors.gray11};
  }
  &:focus {
    border-color: #007bff;
    outline: none;
  }
  .selectoption.largeSelectOption {
    width: 100%;
    max-width: 491px;
    box-sizing: border-box;
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

export const ClientTitleWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const InfoRow = styled.div`
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;

  svg {
    vertical-align: middle;
  }
`;

export const StyledStatusDropdown = styled(StatusDropdown)`
  max-width: 150px;
`;

export const InputLabelContainer = styled.div<{ Width?: string }>`
  flex: 1;
  max-width: 491px;
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;

  &.editContainer {
    max-width: 1022px;
    width: 100%;
  }

  label {
    font-family: Nunito;
    font-weight: 600;
    font-style: SemiBold;
    font-size: 14px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
  }
  input {
    cursor: text;
  }
  .selectoption {
    outline: none;
    border-radius: 8px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    color: ${(props) => props.theme.colors.blackColors.black1};
    display: flex;
    padding: 16px 20px;
    gap: 10px;
    align-items: flex-start;
    justify-content: space;
    align-self: stretch;
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
    box-sizing: border-box;
    height: 54px;
    background: ${(props) => props.theme.colors.blackColors.white6};
  }

  .react-select__control {
    min-height: 54px !important;
    border-radius: 8px;
    background: ${(props) => props.theme.colors.blackColors.white6};
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  .react-select__menu {
    background: ${(props) => props.theme.colors.blackColors.white6};
    border-radius: 8px;
    z-index: 1000;
  }

  .react-select__option {
    background: ${(props) => props.theme.colors.blackColors.white6} !important;
    color: ${(props) => props.theme.colors.blackColors.black1} !important;
    padding: 10px;
    cursor: pointer;
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
    cursor: not-allowed;
  }
  textarea:disabled {
    background-color: ${(props) => props.theme.colors.blackColors.white6};

    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  }
`;

export const FormInputsContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  width: 100%;
  flex-wrap: wrap;

  @media screen and (max-width: 1050px) {
    flex-direction: column;
    gap: 24px;
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
    margin: 35px;
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

export const ColumnWrapper = styled.div`
  flex: 1 1 0;
  max-width: 491px;
  width: 100%;
`;

export const SectionContainer = styled.div`
  width: 100%;
  margin-bottom: 15px;

  h2 {
    font-size: 18px;
    font-weight: 600;
    color: #005792;
    margin-bottom: 14px;
    max-width: 390px;

    height: 27.1px;
    display: flex;
    justify-content: center;
    align-items: center;
    @media screen and (max-width: 1490px) {
      justify-content: flex-start;
    }
  }
`;
