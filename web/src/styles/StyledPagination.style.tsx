// src/components/PaginationStyles.js
import styled from "styled-components";

export const PaginationContainer = styled.nav`
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
`;

export const PaginationList = styled.ul`
  display: flex;
  list-style-type: none;
  padding: 0;
`;

export const PaginationItem = styled.li`
  margin: 0 5px;

  &.disabled button {
    color: ${({ theme }) => theme.colors.grayColors.gray7};
    cursor: not-allowed;
  }

  &.active button {
    background-color: ${({ theme }) => theme.colors.blackColors.white2};
  }
`;

export const PaginationButton = styled.button`
  border-radius: 8px;
  padding: 5px 10px;
  cursor: pointer;
  color: ${({ theme }) => theme.colors.blackColors.black1};
  background-color: ${({ theme }) => theme.colors.blackColors.white6};
  border: none;
  &.arrowIcon {
    border: 1px solid ${({ theme }) => theme.colors.blackColors.white2};
  }
`;

export const PaginationActionArea = styled.div`
  color: ${({ theme }) => theme.colors.grayColors.gray7};
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 0 10px;
  font-size: 12px;

  select {
    border: 1px solid ${({ theme }) => theme.colors.blackColors.white2};
    background-color: ${({ theme }) => theme.colors.blackColors.white2};
    color: ${({ theme }) => theme.colors.grayColors.gray7};
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
    width: fit-content;
    height: 30px;
    outline: none;
  }
`;
