import { styled } from 'styled-components';

export const CalendarContainer = styled.div`
  width: Fixed (320px);
  height: Hug (397px);
  top: 119px;
  padding: 24px;
  border-radius: 16px;
  gap: 40px;
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  box-shadow: 0px 5px 40px 0px
    ${(props) => props.theme.colors.blackColors.black6};
`;

export const CalendarHeader = styled.h2`
  width: 272px;
  height: 28px;
  font-family: Nunito;
  font-size: 20px;
  font-weight: 700;
  line-height: 28px;
  letter-spacing: 0px;
  text-align: left;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const HeaderLine = styled.div`
  width: 272px;
  height: 1px;
  background: ${(props) => props.theme.colors.blackColors.white2};
  margin: 5px;
`;
export const MonthsContainer = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  bottom: -15px;
`;

export const MonthContainer = styled.div`
  width: Fixed (272px);
  height: Hug (304px);
  gap: 10px;
`;

export const MonthHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  padding: 5px;
`;

export const MonthName = styled.h2`
  width: 196px;
  height: 24px;
  font-family: Nunito;
  font-size: 16px;
  font-weight: 600;
  line-height: 24px;
  letter-spacing: 0.30000001192092896px;
  text-align: center;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const WeekdaysContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
`;

export const Weekday = styled.div`
  text-align: center;
  gap: 8px;
  color: #a0aec0;
  font-size: 14px;
  font-weight: 400;
  line-height: 160%;
`;

export const CalendarGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
`;
