import { styled } from 'styled-components';

export const FilterSection = styled.div`
  margin-top: 25px;
  display: flex;
  gap: 16px;
  flex-wrap: wrap;

  .selectoption {
    outline: none;
    border-radius: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    display: flex;
    padding: 16px 50px 16px 20px;
    height: max-content;
    min-width: 17%;
    color: ${(props) => props.theme.colors.blackColors.black1};
    appearance: none;
    background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1em;
    background-color: ${(props) => props.theme.colors.blackColors.white6};
  }

  .largeSelectOption {
    min-width: 200px;
  }

  /* Multi-select container styles */
  .multi-select-container {
    position: relative;
    min-width: 200px;
  }

  .multi-select-header {
    cursor: pointer;
    padding: 16px 20px;
    border-radius: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    background-color: ${(props) => props.theme.colors.blackColors.white6};
    display: flex;
    justify-content: space-between;
    position: relative;
    padding-right: 40px;
    align-items: center;
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  .dropdown-arrow {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    width: 16px;
    height: 16px;
    transition: transform 0.2s ease;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23222' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: center;

    /* Hide the Unicode arrow */
    color: transparent;
    font-size: 0;
  }

  .multi-select-options {
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    right: 0;
    z-index: 1000;
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 0 0 10px 10px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    max-height: 300px;
    overflow-y: auto;
  }
  body:not(.multi-select-container):active ~ .multi-select-options {
    display: none;
  }
  .multi-select-option {
    display: flex;
    align-items: center;
    padding: 10px 20px;
    cursor: pointer;
    color: ${(props) => props.theme.colors.blackColors.black1};
    &:hover {
      background-color: rgb(24, 127, 237);
      color: ${(props) => props.theme.colors.blackColors.white};
    }
  }

  .multi-select-option input {
    margin-right: 10px;
  }
`;
export const DatePicker = styled.div`
  width: fit-content;
  height: fit-content;
  padding: 13px 20px 10px 20px;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};

  .dateName {
    display: flex;
    justify-content: space-around;
    align-items: center;
    span svg path {
      fill: ${(props) => props.theme.colors.grayColors.gray9};
    }
  }

  .dateChild {
    width: fit-content;
    height: fit-content;
    font-family: Nunito;
    font-size: 14px;
    font-weight: 500;
    line-height: 22px;
    letter-spacing: 0px;
    padding-right: 10px;
    text-align: left;
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
`;
export const DisplayFilters = styled.div`
  font-size: 14px;
  margin: 24px 0px;
  font-weight: 600;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  .filterText {
    border-radius: 4px;
    padding: 6px;
    margin-right: 10px;
    color: #333;
    font-size: 14px;
  }

  .clearFilters {
    font-size: 12px;
    color: #005792;
    text-decoration: underline;
    cursor: pointer;
    font-weight: 600;
    text-decoration-line: underline;
  }
  .noFilters {
    font-size: 12px;
    color: #687588;
    font-weight: 600;
    margin-left: 10px;
  }
`;
export const FilterStyle = styled.div`
  border-radius: 4px;
  background: #fafafa;
  padding: 6px;
  margin-right: 10px;
  color: #333;
  font-size: 14px;
`;

export const TableBodyRow = styled.tr`
  height: 56px;
  border-bottom: 1px solid ${(props) => props.theme.colors.blackColors.white2};

  &.disabled-row {
    font-family: Nunito;
    font-weight: 400;
    font-style: Regular;
    font-size: 12px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    vertical-align: middle;
    color: ${(props) => props.theme.colors.blackColors.black4};
    background-color: ${(props) => props.theme.colors.blackColors.white2};
  }

  &.disabled-row:hover {
    background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
  }

  &:hover {
    background-color: ${(props) => props.theme.colors.grayColors.gray8};
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
`;
export const TableHead = styled.thead`
  text-align: left;
`;
export const TableList = styled.table`
  border: 0;
  margin-top: 30px;
  width: 100%;
  border-collapse: collapse;
  z-index: -1;
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

export const ActionContainer = styled.div`
  position: absolute;
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
  // box-shadow: 0 8px 16px rgba(12, 175, 96, 0.2);
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
  padding: 15px 50px 15px 18px;
  align-items: center;
  gap: 20px;
  background: transparent;

  .edit-icon svg path {
    fill: ${(props) => props.theme.colors.blackColors.black1} !important;
  }

  .delete-icon svg path {
    stroke: ${(props) => props.theme.colors.blackColors.black1} !important;
  }

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
  &.edit-disabled {
    pointer-events: auto !important;
    cursor: not-allowed !important;
    background: #f8f8f8 !important;
    color: #aaaaaa;

    * {
      cursor: not-allowed !important;
    }
    .edit-icon svg path {
      fill: #aaaaaa !important;
    }
  }
`;
export const StyledDiv = styled.div`
  padding: 24px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 16px;
`;
export const ExpenseHeading = styled.div`
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid
    ${(props) => props.theme.colors.grayColors.grayscale300};
`;

export const ExpenseTitle = styled.p`
  padding: 12px 8px;
  color: #005792;
  font-size: 14px;
  font-weight: 700;
  border-bottom: 2px solid #005792;
  text-align: center;
`;

export const TotalAmount = styled.span`
  color: #005792;
  padding: 12px 8px;
  border-radius: 6px;
  border: 2px solid #005792;
  font-size: 14px;
  font-weight: 700;
`;

export const ActionMenu = styled.div`
  &.action-align {
    margin-top: -12px;
  }
`;

export const ExpenseFilterArea = styled.section`
  .filterValues {
    border-radius: 4px;
    background: ${(props) => props.theme.colors.backgroundColors.secondary};
    padding: 6px;
    margin: 0 10px;
    color: #687588;
    font-size: 13px;
    font-weight: 500;
  }
  .filterClearBtn {
    margin-left: 5px;
    cursor: pointer;
    vertical-align: middle;
  }
`;

export const ExpenseListSection = styled.section`
  .mainDiv {
    padding: 24px;
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
    border-radius: 16px;
  }

  .Expense_Heading {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
  }
  .noFilters {
    font-size: 12px;
    color: #687588;
    font-weight: 600;
    line-height: 12px;
    margin-left: 10px;
    width: fit-content;
    height: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
  }
  .noMargin {
    margin-left: 0px;
  }

  .right {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .expenseListTitle {
    padding: 12px 8px;
    color: #005792;
    font-size: 14px;
    font-weight: 700;
    text-align: center;
    margin-bottom: 10px;
  }
  .underline {
    border-bottom: 2px solid #005792;
  }
  .amount {
    border-radius: 6px;
  }
  .blackText {
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
  .blackText {
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  .filterCalender {
    position: absolute;
    display: flex;
    border-radius: 16px;
    background: ${(props) => props.theme.colors.backgroundColors.primary};
    box-shadow: 0px 5px 40px 0px rgba(0, 0, 0, 0.1);
    z-index: 9999; // Add this to ensure it appears above other elements
    top: 100%; // Position it right below the date picker
    left: 0;

    -webkit-user-select: none;
    pointer-events: all;

    @supports (-webkit-touch-callout: none) {
      pointer-events: all !important;
      z-index: 99999;
    }
  }
`;
