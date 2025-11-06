import styled from "styled-components";

export const Container = styled.div`
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  margin: 20px;
`;

export const Title = styled.h2`
  font-size: 20px;
  font-weight: 700;
  color: #111827;
`;

export const Filters = styled.div`
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
`;

export const SearchInput = styled.input`
  padding: 8px 10px;       
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 220px;            
  outline: none;

  &:focus {
    border-color: #007bff;
  }
`;


export const Select = styled.select`
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  min-width: 180px; 
  width: 220px;       
  outline: none;
`;

export const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
`;

export const Th = styled.th`
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #687588;
  background-color: #FAFAFA;
  padding: 20px 12px;

  &:first-child {
    border-top-left-radius: 8px;
  }

  &:last-child {
    border-top-right-radius: 8px;
  }
`;

export const Td = styled.td`
  font-size: 14px;
  color: #111827;
  padding: 12px;
  font-weight: 400;
  border-bottom: 1px solid #f1f1f1;
`;

export const EmployeeCell = styled.div`
  display: flex;
  align-items: center;
  gap: 10px;
`;

export const Avatar = styled.img`
  width: 36px;
  height: 36px;
  border-radius: 50%;
`;

export const EmpName = styled.p`
  font-weight: 500;
  margin: 0;
`;

export const EmpEmail = styled.p`
  font-size: 12px;
  color: #888;
  margin: 0;
`;

export const StatusBadge = styled.span<{ status: string }>`
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  background-color: ${(props) =>
    props.status === "Completed" ? "#e9f9ee" : "#fff3e0"};
  color: ${(props) =>
    props.status === "Completed" ? "#34a853" : "#fb8c00"};
`;

export const TitleSection = styled.div`
  display: flex;
  align-items: center;
  gap: 3px;
  margin: 20px;
  color: #111827;

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