import styled from 'styled-components';

export const BulkPayslipContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1100px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 16px;
  padding: 20px 50px;

  &.secondItem {
    margin-top: 20px;
  }

  .topFields {
    display: flex;
    width: 100%;
    gap: 128px;
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .buttonsArea {
    display: flex;
    gap: 30px;
  }

  &.addNewApplicant {
    form {
      display: flex;
      flex-direction: column;
      width: 100%;
      justify-content: center;
      button {
        align-self: center;
        margin-top: 20px;
      }

      div {
        display: flex;
        gap: 20px;
        flex-wrap: wrap;
        justify-content: space-between;
      }

      .selectoption {
        width: 400px;
      }

      input {
        font-size: 16px;
      }
    }
  }

  @media screen and (max-width: 1387px) {
    .topFields {
      gap: 20px;
    }
  }
  @media screen and (max-width: 1245px) {
    .topFields {
      gap: 0px;
    }
  }
`;
