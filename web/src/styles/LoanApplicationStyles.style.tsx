import styled from 'styled-components';
export const LoanApplicationFormContainer = styled.form`
  .formArea {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 40px;
  }
`;
export const ChildDiv1 = styled.div`
  width: calc(50% - 20px);
  select {
    width: 100%;
    max-width: 100%;
  }
`;

export const ChildDiv2 = styled.div`
  width: calc(50% - 20px);
  select {
    width: 100%;
    max-width: 100%;
  }
`;
export const PayrollMainContainer = styled.section`
  padding: 20px 30px;
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  width: 100%;
  border-radius: 16px;

  section {
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
  }
`;
export const HeaderSection = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: calc(100%); /* Adjust width as needed */
`;
export const Hr = styled.hr`
  margin-bottom: 20px;
  height: 2px;

  background: #f1f2f4;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
`;

export const CheckboxLabel = styled.div`
  display: flex;
  font-size: 14px;
  width: fit-content;
`;

export const Checkbox = styled.input`
  margin-right: 10px;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  accent-color: #005792;
  background-color: ${(props) => (props.checked ? '#005792' : 'transparent')};

  &.small {
    width: 16px;
    height: 16px;
  }

  &:disabled {
    outline: none;
  }
`;
export const ButtonGroup = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
  margin-right: 10px;

  button {
    margin-left: 10px;
    cursor: pointer;
    font-weight: 800;
  }
  @media screen and (max-width: 830px) {
    justify-content: space-between;
    flex-wrap: wrap;
    button {
      margin-right: 0;
      margin-bottom: 10px;
      width: calc(50% - 5px);
    }
  }
`;

export const TermsHeading = styled.h5`
  margin-left: 0;
  text-align: left;
  margin-bottom: 15px;
  font-size: 14px;
`;

export const TermsLinks = styled.div`
  display: flex;
  justify-content: flex-start;
  margin-bottom: 10px;

  a {
    margin-right: 24px;
    color: #005792;
    text-decoration: none;
    font-size: 14px;
    font-weight: 700;
  }
`;
export const StatusIndicator = styled.div<{ status: string }>`
  width: 84px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-weight: 700;
  padding: 4px 16px;
  font-size: 10px;
  color: ${({ status }) => {
    switch (status) {
      case 'WAITING':
        return '#CE9900';
      case 'REJECTED':
        return '#EB4335';
      case 'APPROVED':
        return '#005792';
      default:
        return 'black';
    }
  }};
  background-color: ${({ status }) => {
    switch (status) {
      case 'WAITING':
        return '#FFF4D8';
      case 'REJECTED':
        return '#F1C6C6';
      case 'APPROVED':
        return '#A8CFEA';
      default:
        return 'rgba(0, 0, 0, 0.1)';
    }
  }};
  margin: auto;
`;
