import styled from 'styled-components';

export const FormContainer = styled.div`
  background-color: white;
  border-radius: 10px;
  border: 1px solid #f1f2f4;
  max-width: 100%;
  overflow: hidden;
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  width: 100%;
  padding: 10px 0;
  margin-bottom: 20px;
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
    props.isActive || props.isCompleted ? '#005792' : '#888'};

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
  justify-content: space-evenly;
  gap: 40px;x
  flex-wrap: wrap;
  width: 100%;
  padding: 0 20px;
  margin: 0 auto;

  @media screen and (max-width: 1150px) {
    flex-direction: column;
    gap: 20px;
  }
`;

export const InputLabelContainer = styled.div<{ Width?: string }>`
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: ${(props) => props.Width || 'clamp(200px, 50%, 400px)'};

  @media screen and (max-width: 1150px) {
    width: 100%;
  }

  label {
    font-size: 14px;
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
    justify-content: space;
    align-self: stretch;
    width: ${(props) => (props.Width ? props.Width : '100%')};
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1em;
    background-color: ${(props) => props.theme.colors.blackColors.white6};
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  .largeSelectOption {
    width: 491px;
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
      display: 'flex';
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
export const InputLabelLogoContainer = styled.div<{ Width?: string }>`
  margin: 20px 0;
  display: flex;
  flex-direction: row;

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
  gap: 16px;
`;
export const LogoContainer = styled.div`
  display: flex;
  flex-direction: column;
  margin-left: 50px;
`;

export const LogoLabel = styled.div`
  font-family: Nunito;
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  width: 900px;
  margin-left: 150px;
  gap: 10px;
`;

export const LogoUploadContainer = styled.div`
  box-sizing: border-box;
  height: 54px;
  border: 1px dashed #d5d5d5;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 16px;
  width: 920px;
  margin-left: 150px;
  cursor: pointer;
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
  color: #818181;
  width: 90px;
`;
export const UploadText = styled.span`
  font-family: 'Nunito', sans-serif;
  font-weight: 400;
  font-size: 14px;
  color: #a0aec0;
  padding-left: 10px;
`;

export const BrowseText = styled.span`
  font-weight: 700;
  text-decoration: underline;
  color: #005792;
`;

export const FormButtons = styled.div`
  display: flex;
  gap: 10px;
`;
export const AddFormMainContainer = styled.form`
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  @media screen and (max-width: 1150px) {
    width: 100%;
    padding: 0 16px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  width: 100%;
  margin: 80px;

  .leftAlign {
    position: absolute;
    display: flex;
    justify-content: flex-start;
    color: #005792;
    font-weight: 700;
    font-size: 16px;
  }
  .centerAlign {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    justify-content: center;
    gap: 12px;
    padding-right: 60px;
  }
`;

export const FormInputs = styled.div`
  display: flex;
  gap: 20px;
  margin-bottom: 100px;
  width: 850px;
  margin-left: 80px;
  margin-bottom: 20px;
`;
export const HeadingDiv = styled.div`
  font-family: 'Nunito';
  width: 850px;
  margin-left: 80px;
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
  align-items: center;
  display: flex;
`;

export const LabelText = styled.div`
  align-items: center;
  height: 100%;
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
  color: #818181;
  width: 90px;
`;

export const ActionButton = styled.div`
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: #f0f0f0;
  }
`;

export const ButtonGroup = styled.div`
  display: flex;
  gap: 20px;
`;
export const HeadingContainer = styled.div`
  width: 850px;
  margin-left: 80px;
  font-family: 'Nunito';
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const InfoRow = styled.div`
  width: 750px;
  padding-top: 10px;
  padding-left: 10px;
  display: flex;
`;

export const InfoText = styled.div`
  font-family: 'Nunito';
  padding-left: 60px;
`;

export const InfoBlock = styled.div`
  width: 400px;
  padding-top: 20px;
  padding-left: 10px;
  padding-bottom: 20px;
`;
export const AddressBlock = styled.div`
  width: 400px;
  padding: 10px 0 10px 10px;
  display: flex;
`;
export const BasicOrganizationDetailsContainer = styled.div`
  margin-bottom: 5px;
  margin-top: 5px;
  width: 850px;
  margin-left: 80px;
  background-color: rgba(248, 249, 251, 1);
`;

export const ClientInfo = styled.div`
  display: flex;
  flex-direction: column;
  width: 750px;
  height: 200px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;

export const TableContainer = styled.div`
  maxWidth: 1200px
  background-color: #ffffff;
  border-radius: 8px;
  margin-top: 10px;
  padding: 10px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
`;

export const LeftSection = styled.div`
  flex: 3;
  background-color: white;
  border-radius: 10px;
  padding: 20px;
`;

export const RightSection = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  width: 70px;
`;

export const ClientTitle = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 26px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  width: 330px;
  height: 42px;
  color: #005792;
  margin-bottom: 20px;
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
  width: 320px;
  height: 128px;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f1f2f4;
  align-items: flex-start;
  justify-content: flex-start;
  word-wrap: break-word;
`;

export const AddressDiv = styled.div`
  font-family: Nunito;
  font-weight: 400;
  font-size: 15px;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  color: #818181;
  display: flex;
  flex-direction: column;
  padding-top: 20px;
`;

export const nav_AddClient = styled.span`
  transform: rotate(90deg);
`;
