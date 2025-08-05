import styled from 'styled-components';

export const Container = styled.div`
  margin-top: 30px;
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
`;

export const Tabs = styled.div`
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 16px;
`;

export const Tab = styled.div<{ active: boolean }>`
  flex: 1;
  text-align: center;
  padding: 10px 16px;
  cursor: pointer;
  border-bottom: 2px solid
    ${(props) => (props.active ? '#007bff' : 'transparent')};
  color: ${(props) => (props.active ? '#007bff' : '#555')};
  font-weight: ${(props) => (props.active ? '600' : '500')};
  &:hover {
    color: #007bff;
  }
`;

export const TabContent = styled.div`
  overflow-x: auto;
`;

export const ProjectsTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  opacity: 0.8;

  th,
  td {
    padding: 12px;
    border-bottom: 1px solid #eee;
    text-align: left;
    word-wrap: break-word;
    overflow-wrap: break-word;
  }

  th {
    background-color: #f9f9f9;
    font-weight: 600;
  }

  tbody tr:nth-child(even) {
    background-color: #f7f9fa;
  }

  tbody tr:hover {
    background-color: #eef2f6;
  }
`;

export const Status = styled.span`
  background-color: #ffecb3;
  color: #f0ad4e;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
`;

export const AvatarGroup = styled.div`
  display: flex;
  gap: -8px;
`;

export const Avatar = styled.img`
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.1);
`;
