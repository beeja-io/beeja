import styled from 'styled-components';

export const FormContainer = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  border-radius: 10px;

  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
    width: 100%;
  }
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  max-width: 75%;
  width: 100%;
  margin: 20px 0 30px 20px;
  padding-top: 15px;

  @media screen and (max-width: 1200px) {
    max-width: 30%;
    margin: 10px 0;
  }
`;
export const StepWrapper = styled.div`
  display: flex;
  align-items: center;
`;

export const StepLabel = styled.div.withConfig({
  shouldForwardProp: (prop) => !['isActive', 'isCompleted'].includes(prop),
})<{
  isActive: boolean;
  isCompleted: boolean;
}>`
  display: flex;
  align-items: center;
  text-align: center;
  font-family: 'Nunito';
  padding: 10px;
  font-weight: ${(props) =>
    props.isActive || props.isCompleted ? 'bold' : 'normal'};
  color: ${(props) =>
    props.isActive || props.isCompleted ? '#005792' : '#9FAEC0'};

  .circle {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background-color: ${({ isActive, isCompleted }) =>
      isActive || isCompleted ? '#005792' : 'transparent'};
    border: 2px solid
      ${({ isActive, isCompleted }) =>
        isActive || isCompleted ? '#005792' : '#9FAEC0'};
    color: ${({ isActive, isCompleted }) =>
      isActive || isCompleted ? '#FFFFFF' : '#B0B0B0'};
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 8px;
    font-size: 14px;
  }
  .labelHead {
    font-family: 'Nunito';
    font-size: 18px;
    font-weight: 600;
    white-space: nowrap;
  }
`;
export const Line = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 8px;
`;

export const FormInputsContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  column-gap: 45px;
  row-gap: 15px;
  width: 100%;
  flex-wrap: wrap;
`;

export const SummaryAddressContainer = styled.div`
  flex-wrap: wrap;
  display: flex;
  color: ${(props) => props.theme.colors.blackColors.black5};

  justify-content: space-around;
  gap: 20px;
`;

export const SummaryAddressSubContainer = styled.div`
  width: 100%;
  background-color: rgba(248, 249, 251, 1);
  display: flex;
  justify-content: start;
`;

export const FormResourceContainer = styled.div`
  display: flex;
  margin-left: 190px;
  gap: 10px;
  height: 86px;
  width: 1160px;
`;

export const InputLabelContainer = styled.div<{ Width?: string }>`
  flex: 1;
  margin: 20px 0px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 491px;
  width: 100%;

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
  &.logoEditContainer {
    position: relative;
  }
  .selectoption {
    outline: none;
    border-radius: 10px;
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
    background: ${(props) => props.theme.colors.blackColors.white6};
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
      max-width: 400px;
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

export const TextInput = styled.input`
  outline: none;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 20px;
  align-self: stretch;
  width: 100%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};

  &.largeInput {
    max-width: 491px;
  }

  &.disabledBgWhite {
    background-color: white;
  }
  &.grayText {
    color: #8b8b8b;
  }
  &:-webkit-autofill {
    box-shadow: 0 0 0 1000px
      ${(props) => props.theme.colors.backgroundColors.primary} inset;
    -webkit-text-fill-color: ${(props) =>
      props.theme.colors.blackColors.black1};
  }
`;

export const HeadingContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-family: Nunito;
  font-weight: 500;
  font-style: Medium;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  height: 26px;

  margin: 8px;
`;

export const InputLabelLogoContainer = styled.div<{ Width?: string }>`
  flex-wrap: wrap;
  display: flex;
  align-items: center;
  justify-content: center;

  label {
    font-size: 14px;
  }
  input {
    cursor: text;
  }
`;

export const LogoPreview = styled.div`
  width: 62px;
  border-radius: 50%;
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-right: 16px;

  background-color: rgba(248, 249, 251, 1);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
`;
export const LogoNameWrapper = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 5px;
  flex-shrink: 0;
`;
export const LogoContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  flex: 1 1 2;

  width: 100%;
`;

export const LogoLabel = styled.div`
  font-family: Nunito;
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  max-width: 1024px;
  width: 100%;
  &.editLabel {
    position: absolute;
    top: 100%;
    left: 0;
    margin-top: 4px;
    z-index: 10;
  }
`;

export const LogoUploadContainer = styled.div`
  box-sizing: border-box;
  border: 1px dashed #d5d5d5;
  height: 100%;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  max-width: 1024px;
  width: 100%;
  cursor: pointer;
  &.edit-height {
    height: 50.74px;
  }
  &.add_height {
    height: 54px;
  }
`;

export const FileName = styled.span`
  font-size: 14px;
  color: #333;
`;

export const RemoveButton = styled.button`
  background: transparent;
  border: none;
  font-size: 16px;
  color: #f00;
  cursor: pointer;
  line-height: 1;
  padding: 0;
`;
export const SubHeadingDivTwo = styled.div`
  font-family: 'Nunito';
  font-size: 14px;
  font-weight: 600;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: ${(props) => props.theme.colors.grayColors.gray12};
  width: 90px;
`;
export const UploadText = styled.span`
  font-family: 'Nunito', sans-serif;
  font-weight: 400;
  font-size: 14px;
  color: #a0aec0;
  padding-left: 18px;
`;

export const BrowseText = styled.span`
  font-weight: 700;
  text-decoration: underline;
  color: #005792;
  padding-left: 6px;
`;

export const FormButtons = styled.div`
  display: flex;
  gap: 10px;
`;
export const AddFormMainContainer = styled.form`
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
 
  }

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
    margin: 30px;
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

export const AddClientButtons = styled.div`
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

export const FormInputs = styled.div`
  display: flex;
  flex-wrap: nowrap;
  gap: 20px;
  margin-bottom: 100px;
  height: 254px;
  margin-left: 80px;
  margin-bottom: 20px;
  &.tax-container {
    gap: 30px;

    @media screen and (max-width: 1050px) {
      flex-direction: column;
      margin-left: 0;
      height: auto;
    }
  }
`;

export const HeadingDiv = styled.div`
  font-family: 'Nunito';
  width: 850px;
  margin-left: 80px;
  font-weight: 700;
  font-style: Bold;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const CheckBoxOuterContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
  width: 850px;
  margin-left: 80px;
`;

export const StyledCheckbox = styled.input`
  width: 16px;
  height: 16px;
  color: rgba(0, 87, 146, 1);
  cursor: pointer;
  border-width: 1px;
  border: 1px solid #005792;
`;

export const LabelText = styled.div`
  align-items: center;
  height: 100%;
  width: 100%;
  font-family: 'Nunito';
`;

export const SubHeadingDiv = styled.div`
  font-family: 'Nunito';
  font-size: 14px;
  font-weight: 600;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: ${(props) => props.theme.colors.grayColors.gray11};
  width: 150px;
`;

export const SectionHeader = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
`;
export const EditIconWrapper = styled.div`
  cursor: pointer;
  display: flex;
  align-items: center;
`;

export const InfoRow = styled.div`
  width: 750px;
  padding-top: 10px;
  padding-left: 10px;
  display: flex;
`;

export const DotWrap = styled.div`
  margin: 0 4px 0 6px;
  display: flex;
  align-items: center;
  gap: 4px;
`;
export const InfoText = styled.div`
  font-family: 'Nunito';
  padding-right: 30px;
  font-size: 14px;
`;

export const InfoGroup = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 4px;
`;

export const InfoBlock = styled.div`
  padding-top: 20px;
  padding-left: 10px;
  padding-bottom: 20px;
  &.address {
    display: flex;
    width: 50%;
  }
`;

export const PrimaryContainer = styled.div`
  flex: 1 1 48%;
  background-color: ${(props) => props.theme.colors.grayColors.gray12};
  color: ${(props) => props.theme.colors.blackColors.black5};
  gap: 10px;
  h2 {
    padding: 10px 0 10px 20px;
  }
`;
export const AddressBlock = styled.div`
  padding: 10px 0 10px 18px;
  margin-top: 5px;
  display: flex;
  color: ${(props) => props.theme.colors.blackColors.black5};
`;

export const AddressMainContainer = styled.form`
  width: 1028;
  height: 482;
  angle: 0 deg;
  opacity: 1;
  padding-top: 24px;
  padding-right: 64px;
  padding-bottom: 24px;
  padding-left: 64px;
  gap: 16px;
  border-radius: 10px;

  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
    margin: 20px;
  }
`;

export const ExpenseAddFormMainContainer = styled.form`
  background: ${(props) => props.theme.colors.blackColors.white6};
  angle: 0 deg;
  opacity: 1;
  padding-top: 24px;
  padding-right: 64px;
  padding-bottom: 24px;
  padding-left: 64px;
  border-radius: 10px;
  border-width: 1px;
  &.formBackground {
    margin: 20px;
    border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  }
  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
    margin: 20px;
    border: 1px solid #f1f2f4;
    color: red;
  }
`;

export const SummarySubContainer = styled.div`
  display: flex;
  justify-content: center;
  flex-direction: column;
`;

export const BasicOrganizationDetailsContainer = styled.div`
  padding-bottom: 9px;
  padding-top: 5px;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  background: ${(props) => props.theme.colors.grayColors.gray12};
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray10};
  color: ${(props) => props.theme.colors.blackColors.black5};
`;

export const ClientInfo = styled.div`
  display: flex;
  flex-direction: column;
  width: auto;
  height: 215px;
  background: ${(props) => props.theme.colors.blackColors.white6};
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;
export const ClientInfoRowItem = styled.div`
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;
export const DotWrapper = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin: 0 6px;

  svg {
    width: 6px;
    height: 6px;
  }
`;

export const ClientInfoSection = styled.section`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
  row-gap: 12px;
  align-items: center;
`;

export const TableContainer = styled.div`
  width: 700px;
  background-color: #ffffff;
  border-radius: 8px;
  margin-top: 10px;
  padding: 10px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;

export const LeftSection = styled.div`
  flex: 3;
  border-radius: 10px;
  height: 539px;
`;

export const RightSection = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 522px;
  background: ${(props) => props.theme.colors.blackColors.white6};
  width: 315px;

  angle: 0 deg;
  opacity: 1;
  padding-top: 36px;
  padding-right: 24px;
  padding-bottom: 36px;
  padding-left: 24px;
  gap: 32px;
  border-radius: 16px;
`;

export const Container = styled.div`
  display: flex;
  width: 100%;
  gap: 20px;
  padding: 20px;
`;

export const ClientInfoDiv = styled.div`
  font-family: Nunito;
  font-size: 14px;
  font-weight: 500;
  line-height: 22.4px;
  text-align: left;
  text-underline-position: from-font;
  text-decoration-skip-ink: none;
  color: #818181;
`;

export const ClientTitle = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 24px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  width: 40rem;
  height: 42px;
  color: #005792;
`;

export const ProjectInfo = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  line-height: 160%;
  letter-spacing: 0.2px;
  color: #005792;
  padding-left: 10px;
  padding-right: 10px;
`;

export const RightSectionDiv = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  line-height: 160%;
  letter-spacing: 0.2px;
  height: 128px;
  gap: 16px;
  padding: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray10};
  color: ${(props) => props.theme.colors.blackColors.black7};
  align-items: flex-start;
  justify-content: flex-start;
  word-wrap: break-word;
`;
export const RowLine = styled.div`
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray10};
`;
export const AddressDiv = styled.div`
  font-family: Nunito;
  font-weight: 400;
  font-size: 15px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  color: ${(props) => props.theme.colors.grayColors.gray11};
  display: block;
  white-space: normal;
  word-wrap: break-word;
  max-width: 100%;
`;

export const nav_AddClient = styled.span`
  transform: rotate(90deg);
`;

export const CountBadge = styled.span`
  background-color: #f1f2f4;
  color: #333;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  margin-left: 6px;
`;
export const TaxDetailsWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.7rem;
  margin-top: 0.5rem;
`;

export const TaxItem = styled.div`
  display: flex;
  gap: 0.5rem;
  align-items: center;
`;
export const TaxLabel = styled.div`
  color: ${(props) => props.theme.colors.grayColors.gray11};
`;

export const TaxValue = styled.div`
  font-weight: 600;
  color: ${(props) => props.theme.colors.grayColors.gray11};
`;

export const DateIconWrapper = styled.span`
  display: inline-flex;
  align-items: center;
  gap: 6px;
`;

export const ColumnWrapper = styled.div`
  flex: 1 1 0;
  max-width: 491px;
  width: 100%;
`;

export const ContractInfo = styled.div`
  display: flex;
  flex-direction: column;
  width: auto;
  height: 289px;
  background: ${(props) => props.theme.colors.blackColors.white6};
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;
