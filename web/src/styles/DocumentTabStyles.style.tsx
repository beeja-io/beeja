import styled from 'styled-components';

// export const DocumentMainContainer = styled.section``;

export const DocumentContainer = styled.section`
  padding-top: 24px;
  position: relative;
`;

export const DocumentHeadSection = styled.div`
  display: flex;
  position: relative;
  margin-left: 10px;
  justify-content: space-between;
  align-items: center;

  .document_heading {
    flex-direction: column;
  }

  div {
    display: flex;

    &:last-child {
      gap: 10px;
    }
    p {
      color: ${(props) => props.theme.colors.blackColors.black7};
      font-family: Nunito;
      font-size: 24px;
      font-style: normal;
      font-weight: 700;
      line-height: 130%;
    }
    span {
      color: ${(props) => props.theme.colors.grayColors.gray7};
      font-family: Nunito;
      font-size: 14px;
      margin-top: 8px;
      font-style: normal;
      font-weight: 500;
      line-height: 160%;
    }
  }
`;

export const EmployeeListFilterSection = styled.section`
  display: flex;
  padding: 24px 0;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  flex: 1 0 0;
  border-radius: 16px;
  height: fit-content;
`;

export const SearchEmployee = styled.input`
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  flex-grow: 2;
`;

export const TableBodyRow = styled.tr`
  height: 56px;
  border-bottom: 1px solid ${(props) => props.theme.colors.blackColors.white2};

  &:hover {
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
    cursor: pointer;
  }

  .truncate_filename {
    text-overflow: ellipsis;
    overflow: hidden;
    display: block;
    width: 150px;
    white-space: nowrap;
    padding: 20px 0px;
  }
  td {
    padding: 0 10px;
    font-size: 12px;
    vertical-align: middle;
  }
`;
export const TableListContainer = styled.section`
  margin-top: 60px;
  display: relative;
  &.overFlowScroll {
    overflow: scroll;
  }
  
  &.bgTable {
    background-color: white;
  }
`;
export const TableList = styled.table`
  border: 0;
  margin-top: 30px;
  width: 100%;
  border-collapse: collapse;
  /*  z-index: -1; */

  thead {
    background-color: ${(props) => props.theme.colors.blackColors.white3};
    color: ${(props) => props.theme.colors.grayColors.gray7};
    font-size: 16px;
    font-style: normal;
    font-weight: 700;
    line-height: 160%;
    letter-spacing: 0.2px;
    height: 56px;

    tr th {
      padding: 0 10px;
      font-size: 12px;
    }
  }
`;
export const UploadButton = styled.button`
  display: flex;
  height: 56px;
  padding: 21px 24px;
  justify-content: center;
  align-items: center;
  gap: 8px;
  //   font-size: 16px;
  align-self: stretch;
  border-radius: 10px;
  background: #005792;
  cursor: pointer;
  color: var(--others-white, #fff);
  border: none;
`;
export const TableHead = styled.thead`
  .statusHeader {
    text-align: center;
  }
  tr {
    &.documentsTableTow {
      text-align: left;
      border-radius: 10px;
    }
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

export const StyledForm = styled.form`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

export const InputLabelContainer = styled.div<{ Width?: string }>`
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;

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

export const ValidationText = styled.span`
  color: #e03137;
  font-size: 12px;
  font-weight: 400;
  line-height: 160%;
  display: flex;
  align-items: center;
  margin-top: 4px;

  &.star {
    margin-top: 0px;
    font-size: 15px;
    font-weight: 400;
    display: inline-block;
  }
`;

// interface MenuProps{
//   appendTo: HTMLElement;
// }

export const FilterDropdown = styled.button`
  display: flex;
  font-size: 14px;
  padding: 13px 19px 13px 20px;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border-radius: 8px;
  border: 1px solid #005792;
  background: var(--primary-white, #fff);
  cursor: pointer;
  gap: 30px;
  z-index: 1;
`;

export const DropdownContainer = styled.div`
  position: relative;
  // display: inline-block;
`;
export const DropdownContent = styled.div`
  display: flex;
  flex-direction: column;
  position: absolute;
  max-height: 50svh;
  z-index: 99999;
  overflow: auto;
  padding: 16px;
  // top: 20px;
  // left: 0;
  background-color: ${(props) => props.theme.colors.blackColors.white};
  box-shadow: 0 8px 16px rgba(12, 175, 96, 0.2);
  margin-top: 60px;

  &::-webkit-scrollbar {
    display: none;
  }
`;
/* FIXME */
export const DropdownOption = styled.div`
  display: flex;
  position: relative;
  width: 200px;
  text-decoration: none;
  color: #111827; //not in theme but in figma
  cursor: pointer;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  letter-spacing: 0.2px;
  padding: 16px 0px;
  align-items: center;
  justify-content: flex-start;

  &:hover {
    background: ${(props) => props.theme.colors.grayColors.gray6};
    border-radius: 10px;
  }
  &.selected {
    font-weight: 700;
    border-radius: 10px;
    padding: 16px;
    background: ${(props) => props.theme.colors.grayColors.gray6};
  }
`;

export const TickMark = styled.span`
  width: 24px;
  height: 24px;
  margin-left: auto;
`;

//Action Styling

export const ActionContainer = styled.div`
  position: relative;
  // display: inline-block;
  width: 24px;
`;
export const ActionMenuContent = styled.div`
  display: flex;
  flex-direction: column;
  position: absolute;
  z-index: 99999;
  // max-height: 50vh;
  overflow: visible;
  // overflow-y: auto;
  padding: 16px;
  top: 0;
  right: 100%;
  transform: translateY(-70%);
  background-color: ${(props) => props.theme.colors.blackColors.white};
  box-shadow: 0 8px 16px rgba(12, 175, 96, 0.2);
`;

export const ActionMenuOption = styled.div`
  display: flex;
  position: relative;
  // width: 200px;
  text-decoration: none;
  color: ${(props) => props.theme.colors.blackColors.black1};
  cursor: pointer;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  letter-spacing: 0.2px;
  padding: 15px 11px 15px 18px;
  align-items: center;
  gap: 20px;

  &:hover {
    background: ${(props) => props.theme.colors.grayColors.gray6};
    border-radius: 10px;
  }
  &.selected {
    font-weight: 700;
    border-radius: 10px;
    padding: 16px;
    background: ${(props) => props.theme.colors.grayColors.gray6};
  }
`;

export const ActionMenu = styled.div``;

export const FileUploadForm = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;

  .infoText {
    color: ${(props) => props.theme.colors.grayColors.gray7};
    font-size: 12px;
  }
`;

export const FileUploadField = styled.div`
  display: flex;
  flex-direction: row;
  padding: 16px 20px;
  border: 2px dashed ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 10px;
  cursor: pointer;
  &.bulkpayslipFile {
    width: 100%;
    padding: 0;
    height: 53px;

    .blackTextInInput {
      color: ${(props) => props.theme.colors.blackColors.black1};
    }
  }

  .textInInput {
    color: #a0aec0;
  }

  .file-info-text{
    font-size: 12px;
  }

  &.expenseReceiptUpload {
    width: 100%;
    padding: 0;
    height: 53px;

    label {
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;

      div {
        display: flex;
        gap: 10px;
        align-items: center;
        font-size: 12px;

        div {
          color: #a0aec0;

          span {
            color: ${(props) => props.theme.colors.blackColors.black1};
          }
        }
      }
    }
  }

  label {
    color: ${(props) => props.theme.colors.blackColors.black7};
    padding: 7px;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-self: flex-start;
    font-size: 9px;
    font-weight: 600;
    align-items: center;
    gap: 10px;
  }

  .bulkPayslipsLable {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;

    div {
      display: flex;
      gap: 5px;
      align-items: center;
      font-size: 14px;
    }
  }
`;

export const FormFileSelected = styled.div`
  display: flex;
  align-items: center;
  padding: 6px 12.5px 6px 12px;
  align-self: flex-start;
  margin-top: 15px;
  gap: 22px;
  border-radius: 10px;
  border: 1px solid #e9eaec;
  background: ${(props) => props.theme.colors.blackColors.white6};
  max-width: max-content;

  .fileDetails {
    display: flex;
    flex-direction: column;

    span:last-child {
      font-size: 10px;
    }
  }

  .fileName {
    font-size: 12px;
  }

  .closeMark {
    cursor: pointer;
    margin-left: 10px;
  }
`;

export const NoDocsContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  margin: 68px 0px 130px 0px;

  .heading {
    color: 68px 0px 130px 0px;
    text-align: center;
    font-family: Nunito;
    font-size: 32px;
    font-style: normal;
    font-weight: 700;
    line-height: 125%;
    margin-top: 10px;
  }

  .description {
    color: ${(props) => props.theme.colors.blackColors.black7};
    text-align: center;
    font-family: Nunito;
    font-size: 18px;
    font-style: normal;
    font-weight: 400;
    line-height: 150%;
  }
`;
