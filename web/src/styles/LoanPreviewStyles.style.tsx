import styled from "styled-components";

export const LoanPreviewModal = styled.section`
  max-width: 900px;
  div {
    display: flex;
    flex-wrap: wrap;
    gap: 0 30px;
    justify-content: space-between;
  }

  input {
    width: 391px;
    height: 54px;
    color: #8b8b8b;
  }
  textarea {
    width: 391px;
    min-height: 54px;
    color: #8b8b8b;
    background-color: white;
    outline: none;
    border-radius: 10px;
    padding: 10px 20px;
    border: 1px solid #e9eaec;
    resize: none;
  }
`;
