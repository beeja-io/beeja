import styled from 'styled-components';

export const TimesheetContainer = styled.div`
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  background: ${(props) => props.theme.colors.grayColors.gray6};
  padding: 24px;
  display: flex;
  flex-direction: column;
  margin: 24px 0;

  .heading {
    font-size: 24px;
    font-weight: 550;
    padding-bottom: 20px;
    span {
      transform: rotate(90deg);
      display: inline-block;
      margin-right: 5px;
      cursor: pointer;
    }
  }

  .TimesheetSubContainer {
    padding: 21px;
    border-radius: 12px;
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
    margin-top: 16px;
  }
  .TimeSheet_Container {
    padding-left: 42px;
  }

  .TimeSheet_Heading {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    padding-top: 15px;
  }
  .TimeSheetTitle {
    padding: 12px 8px 2px 0px;
    color: #005792;
    font-size: 14px;
    font-weight: 700;
    text-align: center;
    align-item: flex-end;
  }
  .underline {
    border-bottom: 2px solid #005792;
  }
  .Filter_Container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .Export {
    padding: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 8px;
  }
`;
export const SearchBox = styled.div`
  display: flex;
  fontstyle: Nunito;
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
    align-items: center;
    gap: 15px;
    svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
  }
  &.search {
    margin-right: 10px;
    height: 40px;
    width: 40px;
    background-color: ${(props) => props.theme.colors.blackColors.white};
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 5px;
  }
`;
export const Filters = styled.div`
  display: flex;
  gap: 10px;
  margin-top: 20px;
  margin-bottom: 20px;
`;

export const SearchInput = styled.input`
  padding: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 8px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
`;

export const Dropdown = styled.select`
  padding: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 8px;
`;

export const ViewToggle = styled.div`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  button {
    padding: 10px 20px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    background-color: ${(props) => props.theme.colors.grayColors.gray8};
    &.active {
      background-color: #005792;
      color: #fff;
    }
  }
`;

export const MonthHoursContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0;
`;

export const MonthBox = styled.div`
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  padding: 10px 16px;
  border-radius: 8px;
  background: transparent;
  color: ${(props) => props.theme.colors.blackColors.black7};

  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0.2px;
  vertical-align: middle;

  span {
    color: #005792;
    ffont-family: Nunito;
    font-weight: 700;
    font-style: Bold;
    font-size: 14px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0.2px;
    vertical-align: middle;
  }
`;

export const HoursBox = styled(MonthBox)`
  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0.2px;
  vertical-align: middle;
  color: ${(props) => props.theme.colors.blackColors.black7};
`;

export const DaysContainer = styled.div`
  display: flex;
  flex-direction: column;
  border-top: 1px solid ${(props) => props.theme.colors.grayColors.grayscale200};
`;

export const SingleRowContainer = styled.div<{
  disabled: boolean;
  isWeekend: boolean;
  isSelected: boolean;
  isFuture: boolean;
}>`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #ddd;
  cursor: ${({ disabled, isFuture }) =>
    disabled || isFuture ? 'not-allowed' : 'pointer'};

  background: ${({ isSelected, isWeekend }) =>
    isSelected ? 'rgba(52, 168, 83, 0.12)' : isWeekend ? '#FFF4F4' : '#F8F8F8'};
  color: ${({ isWeekend }) => (isWeekend ? '#E0313799' : '#111827')}
  opacity: ${({ disabled }) => (disabled ? 0.5 : 1)};
  pointer-events: ${({ disabled }) => (disabled ? 'none' : 'auto')};
`;

export const WeekdayRow = styled.div`
  font-size: 14px;
`;
export const LoggedHours = styled.div`
  font-size: 14px;
  font-weight: bold;
  color: ${(props) => props.theme.colors.grayColors.gray7};
  min-width: 120px;
  text-align: right;
  white-space: nowrap;
  span {
    font-family: Nunito;
    font-weight: 700;
    font-style: Bold;
    font-size: 13px;
    leading-trim: NONE;
    line-height: 100%;
    letter-spacing: 0%;
    text-align: right;
    vertical-align: middle;
    color: #111827;
    margin-left: 4px;
  }
`;

export const DayText = styled.div<{
  isWeekend: boolean;
  isFuture: boolean;
}>`
  font-family: Nunito;
  font-weight: 500;
  font-size: 13px;
  line-height: 100%;

  color: ${({ isFuture, isWeekend }) =>
    isFuture ? '#999999' : isWeekend ? '#E03137' : '#111827'};
`;

export const TimesheetRow = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid
    ${(props) => props.theme.colors.grayColors.grayscale300};
  align-items: center;
  background: #f8f9fa;
  padding: 12px 16px;
  cursor: pointer;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
`;

export const NavigationButtons = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  button {
    padding: 10px 20px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
    border: 1px solid #f1f2f4;
  }
`;

export const RotateArrow = styled.span<{ isExpanded: boolean }>`
  display: inline-block;
  transform: ${({ isExpanded }) =>
    isExpanded ? 'rotate(180deg)' : 'rotate(0deg)'};
  transition: transform 0.1s ease-in-out;
  opacity: 0.8;
  padding: 15px;
  font-weight: bold;
`;

export const DailyLogContainer = styled.div`
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  width: 100%;
  position: relative;
  table {
    width: 100%;
    border-collapse: collapse;
  }
`;
export const ButtonWrapper = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 10px;
`;
export const ButtonGroup = styled.div`
  display: flex;
  justify-content: center;
  gap: 10px;
`;

export const StyledTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  text-align: left;
  table-layout: fixed;
  th,
  td {
    padding: 12px;
    border-bottom: 1px solid #ddd;
    text-align: center;
    vertical-align: middle;
    white-space: nowrap;
  }
  td.no-padding {
    padding: 0;
  }
  th {
    font-weight: bold;
    background: ${(props) => props.theme.colors.grayColors.gray6};
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
  .projectWidth {
    width: 20%;
  }
  .contractWidth {
    width: 20%;
  }
  .logHoursWidth {
    width: 10%;
  }
  .descriptionWidth {
    width: 40%;
  }
  .actionWidth {
    width: 10%;
  }
  tbody tr {
    width: 100%;
    background: ${(props) => props.theme.colors.grayColors.gray6};
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
  tbody td:nth-child(1),
  tbody td:nth-child(2) {
    white-space: normal;
    word-break: break-word;
  }
  .Action {
    position: relative;
    z-index: 10;
    .dropdown-container {
      margin: 0 auto;
    }
  }
  .no-entries {
    text-align: center;
    padding: 20px;
  }
`;

export const WeekContainer = styled.div`
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  margin-bottom: 10px;
  transition: all 0.3s ease-in-out;
  overflow: hidden;
  &:hover {
    box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
  }
`;
export const CloseButton = styled.button`
  border: 1px solid ${(props) => props.theme.colors.blackColors.black1};
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 0%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  width: 100px;
  height: 40px;
  angle: 0 deg;
  opacity: 1;
  border-radius: 10px;
  border-width: 1px;
  background: transparent;
  cursor: pointer;
`;

export const WeekSubContainer = styled.div<{ isActive: boolean }>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;

  background-color: ${({ isActive }) => (isActive ? '#F1F2F7' : 'transparent')};
`;

export const WeekBodyContainer = styled.div`
  display: flex;
  flex-direction: column;
  background-color: #f7f8fc;
`;

export const TotalWeekHoursContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 2px;
`;

export const WeekTitle = styled.span`
  padding-left: 10px;
  font-family: Nunito;
  font-weight: 400;
  font-style: Regular;
  font-size: 13px;
  leading-trim: NONE;
  line-height: 100%;
  letter-spacing: 0%;
  text-align: center;
  vertical-align: middle;
  color: ${(props) => props.theme.colors.grayColors.gray7};
  span {
    font-family: Nunito;
    font-weight: 700;
    font-style: Bold;
    font-size: 13px;
    leading-trim: NONE;
    line-height: 100%;
    letter-spacing: 0%;
    text-align: center;
    vertical-align: middle;
    color: #111827;
  }
`;

export const WeeklyLogs = styled.p`
  font-size: 13px;
  color: ${(props) => props.theme.colors.grayColors.gray7};
  margin-top: 4px;
  span {
    font-family: Nunito;
    font-weight: 700;
    font-style: Bold;
    font-size: 13px;
    leading-trim: NONE;
    line-height: 100%;
    letter-spacing: 0%;
    text-align: right;
    vertical-align: middle;
    color: #111827;
  }
`;

export const AddButton = styled.button`
  background: #555;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 50%;
  font-size: 18px;
  cursor: pointer;
  position: relative;
  bottom: 0;
  left: 0;
  &:hover {
    background: #0056b3;
  }
`;
export const EditButton = styled.button`
background: transparent;
border: none;
cursor: pointer;
padding: 6px;
display: flex;
align - items: center;
  &:hover {
  transform: scale(1.1);
}
`;

export const SaveButton = styled.button`
  width: 100px;
  height: 40px;
  background: #005792;
  color: #ffffff;
  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 150%;
  letter-spacing: 0.3px;
  text-align: center;
  border-radius: 10px;
  border: none;
  cursor: pointer;

  &:hover {
    background-color: #004494;
  }
`;

export const Weekday = styled.div`
padding: 10px 16px;
cursor: pointer;
display: flex;
align - items: center;
justify - content: space - between;
border - bottom: 1px solid #ddd;
`;
export const LogEntry = styled.div`
padding: 8px;
margin - top: 5px;
background: #f1f1f1;
border - radius: 4px;
`;

export const FormContainer = styled.div`
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  font-family: 'Poppins', sans-serif;
  .FormRow {
    display: flex;
    align-items: flex-start;
    background: #fafafa;
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 10px;
    gap: 15px;
  }
  .ButtonRow {
    display: flex;
    justify-content: center;
    margin-top: 12px;
  }
  select,
  input,
  textarea {
    flex: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
    min-width: 140px;
    background: #fff;
    outline: none;
  }
  textarea {
    overflow-y: auto;
    resize: none;
    max-height: 60px;
  }
  input {
    flex-grow: 2;
    min-width: 200px;
  }
  .EditWhitePenSVG {
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 5px;
  }
  .hoursSelect {
    flex: 0 0 auto;
    width: 150px;
    min-width: 120px;
  }
  textarea {
    flex-grow: 2;
  }
`;
export const SelectInput = styled.select`
    flex: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
    width:220px;
    min-width: 140px;
    background: #fff;
    outline: none;
  }
`;

export const Input = styled.input`
    flex: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
    width:220px;
    min-width: 140px;
    background: #fff;
    outline: none;
  }
`;
export const PaginationContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 20px;
`;

export const PaginationButton = styled.button`
  background: transparent;
  color: ${(props) => props.theme.colors.blackColors.black7};
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;

  &:hover {
    background: ${(props) => props.theme.colors.grayColors.grayscale100};
    border-color: ${(props) => props.theme.colors.grayColors.grayscale300};
  }
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  .leftArrow {
    transform: rotate(90deg);
  }
  .rightArrow {
    transform: rotate(-90deg);
  }
`;

export const DescriptionText = styled.div`
  max-width: 500px;
  white-space: normal;
  word-wrap: break-word;
  text-align: left;
  display: inline-block;
`;
