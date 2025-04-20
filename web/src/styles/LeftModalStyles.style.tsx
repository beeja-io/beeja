import styled from "styled-components";

export const LeftSideModalContainer = styled.section`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #00000080;
  z-index: 999;
`;

export const LeftSideModalInnerContainer = styled.section<{
  isModalOpen: boolean;
}>`
  width: 60%;
  height: 100vh;
  position: absolute;
  left: 0;
  top: 0;
  border-left: #005792.3 solid #005792;
  background-color: white;
  padding: 0 30px;
  justify-content: center;
  box-shadow: 0 2px 4px #005792;
`;
