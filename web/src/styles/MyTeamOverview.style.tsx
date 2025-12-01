import styled from 'styled-components';
import { TableBodyRow } from '../styles/EmployeeListStyles.style';

export const Title = styled.h2`
  font-size: 24px;
  font-weight: 700;
  margin-left: 10px;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const TitleSection = styled.div`
  display: flex;
  align-items: center;
  gap: 3px;
  margin: 10px;
  margin-top: 20px;
  color: ${(props) => props.theme.colors.blackColors.black1};

  .arrow {
    display: inline-block;
    transform: rotate(90deg);
    cursor: pointer;
    transition: transform 0.2s ease;

    svg {
      width: 22px;
      height: 22px;
    }

    &:hover {
      transform: rotate(90deg) scale(1.1);
    }
  }
`;

export const StatusCell = styled.td<{
  completed: boolean;
  noProviders?: boolean;
}>`
  background-color: ${(p) =>
    p.noProviders
      ? p.theme.colors.grayColors.gray8
      : p.completed
        ? '#34A8531A'
        : '#FF99001A'};

  color: ${(p) =>
    p.noProviders ? '#AAAAAA' : p.completed ? '#34A853' : '#FF9900'};

  height: 40px;
  max-width: 200px;
  border-radius: 8px;
  font-weight: 600;
  text-align: center;
  vertical-align: middle;
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const RatingCenter = styled.td`
  position: relative;

  span {
    position: absolute;
    left: 45px;
  }
`;

export const TableMain = styled(TableBodyRow)<{ disabled?: boolean }>`
  cursor: ${(p) => (p.disabled ? 'not-allowed' : 'pointer')};

  &:hover {
    cursor: ${(p) => (p.disabled ? 'not-allowed' : 'pointer')};
  }
`;
