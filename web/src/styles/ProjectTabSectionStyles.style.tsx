import styled from 'styled-components';

export const Container = styled.div`
  margin-top: 30px;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  background: ${(props) => props.theme.colors.blackColors.white6};
  min-height: 312px;
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
    ${(props) => (props.active ? '#005792' : 'transparent')};
  color: ${(props) =>
    props.active
      ? '#005792'
      : '${(props) => props.theme.colors.blackColors.white6}'};
  font-weight: ${(props) => (props.active ? '600' : '500')};
  &:hover {
    color: '#005792';
  }
`;

export const TabContent = styled.div`
  overflow-x: auto;
  overflow: visible;
  position: relative;
  z-index: auto;
  margin-top: 18px;
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
    background: ${(props) => props.theme.colors.grayColors.gray6};
    color: ${(props) => props.theme.colors.blackColors.black7};
    font-weight: 600;
  }
  td {
    color: ${(props) => props.theme.colors.blackColors.black7};
  }

  tbody tr:hover {
    background-color: ${(props) =>
      props.theme.colors.backgroundColors.blueShade};
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

export const AttachmentList = styled.div`
  margin-top: 1rem;
`;

export const AttachmentItem = styled.div`
  overflow: visible !important;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: ${(props) => props.theme.colors.blackColors.white6};
  padding: 10px 15px;
  margin-bottom: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(196, 196, 196, 0.1);
`;

export const AttachmentInfo = styled.div`
  display: flex;
  align-items: center;

  svg {
    margin-right: 8px;
  }
`;

export const FileDetails = styled.div`
  display: flex;
  flex-direction: column;
`;

export const FileName = styled.div`
  font-weight: 500;
`;

export const FileMeta = styled.div`
  font-size: 12px;
  color: #666;
`;

export const MenuWrapper = styled.div`
  position: relative;
`;

export const NoAttachments = styled.div`
  text-align: center;
  padding: 1rem;
  color: #777;
`;

export const LoaderContainer = styled.div`
  width: 100%;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const NoDataContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 600;
  font-size: 14px;
  line-height: 160%;
  letter-spacing: 0.2px;
  color: #818181;
  text-align: center;
`;
