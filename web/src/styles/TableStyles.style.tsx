import styled from "styled-components";

export const TableContainer = styled.section`
  overflow: scroll;
  &::-webkit-scrollbar {
    width: 0;
  }
`;
export const Table = styled.table`
  border: 0;
  width: 100%;
  border-collapse: collapse;
  overflow-x: auto; /* Enable horizontal scrolling */
  thead {
    background-color: ${(props) => props.theme.colors.blackColors.white3};
    color: ${(props) => props.theme.colors.grayColors.gray7};
    font-size: 14px;
    font-style: normal;
    font-weight: 700;
    line-height: 160%;
    letter-spacing: 0.2px;
    height: 56px;

    tr th {
      padding: 0 10px;
    }
  }
  .rolesAndPermissions {
    tr {
      height: 56px;
      text-align: left;

      &.loaded:hover {
        background-color: ${(props) => props.theme.colors.grayColors.gray10};
        cursor: default;
      }
    }
    .first-column {
      padding-left: 20px;
    }
    .second-column {
      text-align: left;
    }

    .rolesAction {
      padding-left: 20px;
      cursor: pointer;
    }
  }
  .table-row {
    text-align: left;
    border-radius: 10px;
  }

  .th-type {
    width: 250px;
  }

  .th-description {
    width: 250px;
  }

  .th-action {
    width: 140px;
  }
`;

export const TableHead = styled.thead`
  white-space: nowrap;
`;
