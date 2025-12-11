import styled from 'styled-components';

export const StyledDiv = styled.div`
  margin-bottom: 12px;
`;

export const FeedbackCard = styled.div`
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  max-height: 624px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;

  /* allow content to scroll */
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
`;

export const ExpenseHeadingFeedback = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const Section = styled.div`
  margin-top: 16px;

  .search-container {
    position: relative;
  }

  .search-input {
    width: 100%;
    padding: 10px 40px 10px 12px;
    border: 1px solid #e5e7eb;
    border-radius: 6px;
    font-size: 14px;
    color: #333;
    outline: none;
    background-color: #f8f8f8;
    // background: ${(props) => props.theme.colors.grayColors.gray5};
  }

  .search-icon {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    width: 18px;
    height: 18px;
    fill: #9ca3af;
    cursor: pointer;
  }

  .scrollable-list {
    margin-top: 8px;
    max-height: 200px;
    overflow-y: auto;
    border-radius: 6px;
    padding-right: 4px;
    background-color: transparent;

    &::-webkit-scrollbar {
      width: 6px;
    }
    &::-webkit-scrollbar-thumb {
      background: #c7c7c7;
      border-radius: 4px;
    }
    &::-webkit-scrollbar-thumb:hover {
      background: #999;
    }
  }

  &.selected-section {
    .scrollable-list {
      background-color: ${(props) =>
        props.theme.colors.backgroundColors.primary};
      padding: 6px 4px;
    }

    .employee-row {
      border-bottom: 1px solid #dcdcdc;
    }
  }

  .employee-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 12px;
    border: none;
    cursor: pointer;

    &.disabled {
      cursor:
        url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" fill="none" stroke="red" stroke-width="2"/><line x1="6" y1="18" x2="18" y2="6" stroke="red" stroke-width="2"/></svg>')
          8 8,
        not-allowed;
      background: ${(props) => props.theme.colors.grayColors.gray5};
      opacity: 0.9;
    }

    .employee-details {
      .employee-name {
        font-size: 12px;
        font-weight: 500;
        color: ${(props) => props.theme.colors.blackColors.black1};
        margin: 0;
      }

      .employee-role {
        font-size: 10px;
        color: #6b7280;
        margin: 0;
      }
    }

    .remove-icon {
      font-size: 18px;
      color: #9ca3af;
      cursor: pointer;
      padding: 4px;

      &:hover {
        color: #ef4444;
      }
    }
  }
`;

export const TableHead = styled.thead`
  background-color: #f9fafb;
  text-align: left;

  th {
    font-size: 14px;
    font-weight: 600;
    color: #687588;
    padding: 12px 5px;

    &:nth-child(2) {
      // text-align: center;
    }
  }

  tr.table-header {
    padding: 12px 5px;
  }
  th.status-container {
    padding-right: 50px;
    text-align: center;
  }
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
    padding: 10px 10px;
    font-size: 12px;
    vertical-align: middle;
  }
  td.status-row {
    display: flex;
    justify-content: center;
    text-align: center;
  }
`;

export const TableCellStatus = styled.td`
  text-align: center;
  position: absolute;

  .status-container {
    position: relative;
    margin-left: -20px;
    display: inline-flex;
    margin-top: -10px;
    font-size: 14px;

    font-weight: 500;
  }
`;

export const ProfileCell = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;

  .profile-img {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
  }

  .name {
    font-size: 12px;
    font-weight: 500;
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  .email {
    font-size: 10px;
    font-weight: 400;
    color: ${(props) => props.theme.colors.grayColors.gray7};
  }
`;

export const ExpenseHeadingRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .heading {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;

    svg {
      cursor: pointer;
      transform: rotate(90deg);
    }
  }
`;

export const ExpenseTitleProviders = styled.p`
  font-size: 16px;
  font-weight: 700;
  color: ${(props) => props.theme.colors.blackColors.black1};
  display: flex;
  flex-direction: column;
  gap: 4px;

  .sub-text {
    font-size: 14px;
    font-weight: 400;
    color: ${(props) => props.theme.colors.grayColors.gray7};
    margin-top: 4px;
  }
`;
export const AddFeedbackButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-left: auto;
  margin-top: -75px;
`;
export const FeedbackHeaderRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
`;

export const HeadingRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .heading {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;

    svg {
      cursor: pointer;
      transform: rotate(90deg);
    }
  }
`;

export const SectionTitle = styled.h3`
  font-size: 14px;
  font-weight: 500;
  color: ${(props) => props.theme.colors.blackColors.black1};
  margin-bottom: 8px;
`;

export const Divider = styled.hr`
  margin: 16px 0;
  border: none;
  // border-top: 1px solid #e5e7eb;
`;

export const FooterContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 20px;
`;

export const ButtonGroup = styled.div`
  display: flex;
  gap: 12px;
`;

export const Button = styled.button`
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid transparent;
  font-weight: 500;
  transition: all 0.2s ease;
`;
export const SearchInput = styled.input`
  padding-left: 30px;
  height: 36px;
  border-radius: 4px;
  border: 1px solid #ccc;
  outline: none;
  font-size: 14px;
  width: 220px;
  color: #333;
`;

export const SearchInputWrapper = styled.div`
  position: relative;
  transform: translateY(4px);
  display: flex;
  align-items: center;

  svg {
    position: absolute;
    left: 190px;
    top: 50%;
    transform: translateY(-50%);
    width: 16px;
    height: 16px;
    color: #888;
  }
`;
export const ActionMenuIcon = styled.div`
  &.action-align-feedback {
    margin-top: -10px;
  }
`;

export const ActionMenuOptions = styled.div<{ isDisabled?: boolean }>`
  display: flex;
  position: relative;
  // width: 200px;
  text-decoration: none;
  white-space: nowrap;
  color: ${(props) => props.theme.colors.blackColors.black1};
  cursor: ${({ isDisabled }) => (isDisabled ? 'not-allowed' : 'pointer')};
  opacity: ${({ isDisabled }) => (isDisabled ? 0.5 : 1)};
  pointer-events: ${({ isDisabled }) => (isDisabled ? 'none' : 'auto')};
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  letter-spacing: 0.2px;
  padding: 15px 50px 15px 18px;
  align-items: center;
  gap: 20px;
  background: transparent;

  > svg path {
    fill: none;
    stroke: ${(props) => props.theme.colors.blackColors.black1};
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
  svg {
    flex-shrink: 0;
  }

  span {
    display: inline-block;
    line-height: 1.2;
    white-space: nowrap;
  }
  &.disabled-action {
    opacity: 0.5;
    cursor: not-allowed !important;
    pointer-events: none !important;
  }
`;

export const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
`;

export const ModalContainer = styled.div`
  background: #fff;
  border-radius: 10px;
  width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  position: relative;
`;

export const ExpenseHeadingSection = styled.section`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;

  .heading {
    font-size: 24px;
    font-style: normal;
    font-weight: 550;

    span {
      transform: none;
      margin-right: 5px;
      display: inline-block;
    }

    .back-arrow {
      transform: rotate(90deg);
      cursor: pointer;
    }
    .form-name {
      color: #005792;
      font-weight: 700;
      font-size: 24px;
      margin-left: 4px;
    }
  }

  .separator-form {
    transform: none !important;
    margin: 0 15px;
    vertical-align: middle;
    margin-top: 5px;
  }

  .nav_AddClient {
    transform: none !important;
    font-family: Nunito;
    font-weight: 700;
    font-size: 24px;
    line-height: 130%;
    letter-spacing: 0px;
    color: #005792;
    margin-left: 2px;
  }
`;
