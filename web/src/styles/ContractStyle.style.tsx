import { styled } from 'styled-components';

export const RowWrapper = styled.div`
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 8px;
  max-width: 994px;
  width: 100%;
  gap: 8px;

  &.stepTwo {
    gap: 40px;
  }
`;

export const ListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  margin-bottom: 8px;
  max-width: 994px;
  width: 100%;
  gap: 8px;
`;

export const AddContractButtons = styled.div`
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
    padding-left: 15px;
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

export const ColumnItem = styled.span`
  font-weight: 500;
`;

export const HorizontalLine = styled.hr`
  width: 100%;
  border: none;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  margin: 10px 0;
`;

export const IconItem = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;

  svg {
    width: 16px;
    height: 16px;
    fill: #333;
  }
`;

export const ContractTitleHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const FormInputsContainer = styled.div`
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 40px;
  width: 100%;
  flex-wrap: wrap;
`;

export const ColumnWrapper = styled.div`
  flex: 1 1 0;
  max-width: 491px;
  width: 100%;
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
    color: ${(props) => props.theme.colors.blackColors.black7};
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
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  max-width: 75%;
  width: 50%;
  margin: 20px 0 30px 20px;
  padding-top: 15px;

  @media screen and (max-width: 1200px) {
    max-width: 30%;
    margin: 10px 0;
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
    margin: 20px;
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

export const LogoContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  flex: 1;
`;

export const LogoUploadContainer = styled.div`
  box-sizing: border-box;
  min-height: 54px;
  border-radius: 10px;
  border: 1px dashed #d5d5d5;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  position: relative;
  margin-bottom: 16px;
  flex: 1;
  max-width: 491px;
  cursor: pointer;
  padding-left: 15px;
`;

export const FormResourceContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
  flex-wrap: wrap;
`;

export const ContractInfo = styled.div`
  display: flex;
  flex-direction: column;
  width: auto;
  height: auto;
  background: ${(props) => props.theme.colors.blackColors.white6};
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;
