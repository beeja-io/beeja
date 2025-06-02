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
    font-style: normal;
    font-weight: 550;
    span {
      transform: rotate(90deg);
      display: inline-block;
      margin-right: 5px;
      cursor: pointer;
    }
    padding-bottom: 20px;
  }

  .TimeSheet_Container {
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
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

  &.search .commandF {
    margin-right: 10px;
    height: 40px;
    width: 40px;
    background-color: ${(props) => props.theme.colors.blackColors.white};
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 5px;
    box-shadow:
      0px -1px 1px 0px rgba(0, 0, 0, 0.04) inset,
      0px -1px 2px 0px rgba(0, 0, 0, 0.05) inset;
    svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
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

export const TimesheetRow = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid
    ${(props) => props.theme.colors.grayColors.grayscale300};
  align-items: center;
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

export const FormContainer = styled.div`
  padding: 10px;
  border-radius: 5px;

  .Form_Headings {
    display: flex;
    justify-content: space-between;
    background: #f6fbff;
    padding: 10px;
    font-weight: bold;
    border-radius: 5px;
  }
  .Form_Row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 0;
    border-bottom: 1px solid #ddd;
  }
  input,
  textarea {
    flex: 1;
    margin: 0 5px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 8px;
  }
  input {
    min-width: 120px;
    max-width: 150px;
  }
  textarea {
    min-width: 180px;
    max-width: 250px;
  }
`;
