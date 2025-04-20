import styled from "styled-components";

export const InterviewerArea = styled.section`
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;

  .assignedInterviewers {
    width: 50%;
    table {
      border-collapse: collapse;
      width: 100%;
      tr {
        border: 1px solid #000;
        td {
          padding: 0.5rem;
        }
      }
    }
  }
`;
