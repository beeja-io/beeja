import styled from 'styled-components';

export const ZeroEntriesFoundMainContainer = styled.section`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40px;

  flex-direction: column;
  gap: 30px;

  .contentArea {
    display: flex;
    flex-direction: column;
    text-align: center;
  }
  .content {
    font-size: 18px;
    font-style: normal;
    font-weight: 400;
    line-height: 150%; /* 27px */
  }

  .heading {
    color: ${(props) => props.theme.colors.blackColors.black4};
    text-align: center;
    font-family: Nunito;
    font-size: 30px;
    font-style: normal;
    font-weight: 550;
    line-height: 125%; /* 40px */
  }
`;
