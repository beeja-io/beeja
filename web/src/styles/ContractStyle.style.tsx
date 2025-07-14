import { styled } from "styled-components";
import StatusDropdown from "./ProjectStatusStyle.style";
import { ClientInfo } from "./ClientStyles.style";

export const RowWrapper = styled.div`
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 8px;
`;

export const ColumnItem = styled.span`
  font-weight: 500;
`;

export const HorizontalLine = styled.hr`
  width: 100%;
  border: none;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  margin: 10px 0;
`;

export const IconItem = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;

  svg {
    width: 16px;
    height: 16px;
    fill: #333;
  }
`;
