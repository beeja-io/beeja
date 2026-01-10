import styled, { keyframes } from 'styled-components';

const slideInFromRight = keyframes`
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
`;

export const ToastMainContainer = styled.div`
  position: fixed;
  top: 70px;
  right: 30px;
  width: 417px;
  height: fit-content;
  min-height: 90px;
  flex-shrink: 0;
  border-radius: 12px;
  background: ${(props) => props.theme.colors.backgroundColors.secondary};
  color: ${(props) => props.theme.colors.blackColors.black1};
  box-shadow: 5px 5px 50px 0px rgba(26, 32, 44, 0.2);
  display: flex;
  align-items: center;
  padding: 10px 30px;
  justify-content: space-between;
  animation: ${slideInFromRight} 0.5s ease-in-out;
  z-index: 99999;

  span.toastStatusIcon {
    width: 30px;
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    &.success {
      background-color: #34a853;
      color: #34a853;
    }
    &.error {
      background: #ff0606;
      color: red;
    }
    &.warn {
      background-color: #fbbc05;
      color: #fbbc05;
    }
  }

  span.toastMessageContent {
    display: flex;
    flex-direction: column;
    width: 69%;
  }

  span.toastMessageHead {
    font-family: Nunito;
    font-size: 16px;
    font-style: normal;
    font-weight: 700;
    line-height: 160%; /* 25.6px */
    letter-spacing: 0.2px;
    background-color: ${(props) =>
      props.theme.colors.backgroundColors.seconday};
    &.success {
      color: #34a853;
    }
    &.error {
      color: red;
    }
  }
  span.toastMessageBody {
    font-size: 14px;
  }
  span.toastCloseIcon {
    width: 19px;
    height: 19px;
    flex-shrink: 0;
    cursor: pointer;
    align-self: flex-start;
    background-color: red;
    display: flex;
    justify-content: center;
    align-items: center;

    border-radius: 100px;
    background: #fff;
    box-shadow: 0px 20px 40px 0px rgba(0, 0, 0, 0.4);

    span.icon {
      position: relative;
      top: -1.5px;
    }
  }
`;
