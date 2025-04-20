import styled from 'styled-components';

export const SideModalMainContainer = styled.section`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* background: rgba(255, 255, 255, 0.8); */
  background: #00000080;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
`;

export const SideModalInnerContainer = styled.section<{ isModalOpen: boolean }>`
  width: 420px;
  height: 100vh;
  position: absolute;
  right: 0;
  top: 0;
  border-left: #005792.3 solid #005792;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  padding: 50px 30px;
  display: flex;
  justify-content: center;
  box-shadow: 0 2px 4px #005792;
`;

export const SideModalCloseButton = styled.div`
  height: 50px;
  width: 50px;
  border-radius: 50%;
  background-color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 50%;
  left: -70px;
  cursor: pointer;
`;
