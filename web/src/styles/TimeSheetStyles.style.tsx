import styled from 'styled-components';

export const TimesheetContainer = styled.div`
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  background-color: ${(props) => props.theme.colors.backgroundColors.secondary};
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
    .TimeSheet_Container{
   padding-left: 42px;
   }     
  
  .TimeSheet_Heading {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    padding-top: 15px 
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
  .Filter_Container{
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .Export{
    padding: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 8px;
    }
`;
export const SearchBox = styled.div`
  display: flex;
  fontstyle : Nunito;
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
  background: #fff;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  
  span {
    color: #0056b3;
    font-weight: 600;
  }
`;

export const HoursBox = styled(MonthBox)``;

export const DaysContainer = styled.div`
  display: flex;
  flex-direction: column;
  border-top: 1px solid ${(props) => props.theme.colors.grayColors.grayscale200};
`;

export const SingleRowContainer = styled.div`
  width: 100%;
   display: flex;
  align-items: center;
  justify-content: space-between;  
  padding: 12px 16px;  
  border-bottom: 1px solid #ddd;  
`;

export const WeekdayRow = styled.div`
  font-size: 14px;  
`;
export const LoggedHours = styled.div`
  ffont-size: 14px;
  font-weight: bold;
  color: #555;
  min-width: 120px;
  text-align: right;
  }
`;

export const DayText = styled.div`
  font-size: 14px;
  color: #333;
`;

export const TimesheetRow = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
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
     border: 1px solid #F1F2F4
    }
    `;

export const RotateArrow = styled.span<{ isExpanded: boolean }>`
  display: inline-block;
  transform: ${({ isExpanded }) => (isExpanded ? "rotate(180deg)" : "rotate(0deg)")};
  transition: transform 0.1s ease-in-out;
  opacity: 0.8;
  padding: 15px;
  font-weight: bold;
`;

export const DailyLogContainer = styled.div`
  padding: 10px 15px;
  background: #fff;
  width : 100%;
  overflow-x: auto;
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
  margin-right: 500px; /* Or adjust/remove as needed */
`;

export const StyledTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  background: white;
  text-align: left;
  table-layout: fixed; 
  th, td {
    padding: 12px;
    border-bottom: 1px solid #ddd;
    text-align: center;
    vertical-align: middle;
    white-space: nowrap;
  }
  th {
    font-weight: bold;
    background: #f9f9f9;
  }
  tbody tr {
    width: 100%;
  }
  .Action {
    display: flex;
    justify-content: center;
    align-items: center;
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
  background-color: #a6a6a6;
  color: white;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;

  &:hover {
    background-color: #8c8c8c;
  }
`;

export const WeekSubContainer = styled.div<{ isActive: boolean }>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  background-color: ${({ isActive }) => (isActive ? 'rgba(0, 87, 146, 0.06);' : 'transparent')};
`;

export const TotalWeekHoursContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 2px;
`;

export const WeekTitle = styled.span`
  padding-left: 10px;
  font-weight: 600;
  font-size: 14px;
  color: #333;
`;

export const WeeklyLogs = styled.p`
  font-size: 13px;
  color: #666;
  margin-top: 4px;
`;

export const AddButton = styled.button`
 background: #555;
  color: white;;
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
  background-color: #0056b3;
  color: white;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;

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
  background-color: #f8fafd;
  padding: 20px;
  border-radius: 8px;
  font-family: "Poppins", sans-serif;
  .Form_Row {
    display: flex;
    align-items: center;
    background: #ffffff;
    padding: 12px;
    border-radius: 8px;
    margin-bottom: 10px;
    gap: 15px;
  }
  select, input {
    flex: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 14px;
    min-width: 140px;
    background: #fff;
    outline: none;
  }
  input {
    flex-grow: 2;
    min-width: 200px;
  }
  button {
    background-color: #0056b3;
    color: white;
    font-weight: 600;
    padding: 10px 20px;
    border-radius: 6px;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 15px auto;
    transition: background 0.3s;
  }
  button:hover {
    background-color: #004494;
  }
  .EditWhitePenSVG {
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 5px;
  }
`;
export const SelectInput=styled.select`
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
`

export const Input=styled.input`
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
`
export const PaginationContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 20px;
`;

export const PaginationButton = styled.button`
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  
  &:hover {
    background: #f9f9f9;
    border-color: #ccc;
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