import styled from 'styled-components';

export const BulkPayslipContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1100px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 16px;
  padding: 20px 50px;

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
