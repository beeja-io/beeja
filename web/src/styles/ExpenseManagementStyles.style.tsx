import styled from "styled-components";

export const ExpenseManagementMainContainer = styled.section`
  padding: 0 40px;
  padding-bottom: 20px;
`;

export const ExpenseHeadingSection = styled.section`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;

  .heading {
    font-size: 24px;
    font-style: normal;
    font-weight: 550;

    span {
      transform: rotate(90deg);
      display: inline-block;
      margin-right: 5px;
      cursor: pointer;
    }
  }
  .separator {
    transform: none !important;
    font-weight: 400;
    margin: 0 24px;
    color: rgba(160, 174, 192, 1);
  }
  .nav_AddClient {
    transform: none !important;
    font-family: Nunito;
    font-weight: 700;
    font-size: 24px;
    line-height: 130%;
    letter-spacing: 0px;
    color: #005792;
    margin-left: 10px;
  }
`;

export const ExpenseAddFormMainContainer = styled.form`
  width: 100%;
  min-height: 80vh;
  max-height: 50svh;
  overflow-y: scroll;
  padding-right: 10px;

  @media screen and (max-width: 1150px) {
    width: 70vw;
  }

  .formInputs {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 40px;
  }

  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
  }

  .submitButton {
    background-color: #005792;
    color: #fff;
    border: none;

    display: flex;
    width: 162px;
    height: 56px;
    padding: 18px 24px;
    justify-content: center;
    align-items: center;
    gap: 8px;
    border-radius: 10px;
    outline: none;
    cursor: pointer;
  }
`;

export const FileUploadField = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 15px;
  height: 54px;
  flex-shrink: 0;
  border-radius: 10px;
  border: 1px dashed #e9eaec;
  label {
    display: flex;
    width: 197px;
    height: 24px;
    flex-direction: column;
    flex-shrink: 0;
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%; /* 22.4px */
  }
`;

export const FormFileSelected = styled.div`
  height: 31px;
  margin: 3px;
  display: flex;
  padding: 6px 12.5px 6px 12px;
  justify-content: center;
  align-items: center;
  gap: 22px;
  border-radius: 10px;
  border: 1px solid #e9eaec;
  background: #f8f8f8;
  span {
    color: #111827;
    font-family: Nunito;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
  }
`;
