import styled from "styled-components";

export const LoginContainer = styled.section`
  min-width: 100vw;
  min-height: 100svh;
  display: flex;
  align-items: center;
  justify-content: center;

  .backgroundSVG {
    position: fixed;
    top: 0;
    left: 0;
    width: 100px;
    z-index: -1;
  }
`;

export const BrandText = styled.span`
  color: #000;
  font-family: "Rubik", sans-serif;
  font-size: 60.734px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;

  span {
    color: #005792;
    font-family: "Rubik", sans-serif;
  }
`;

export const LoginInnerContainer = styled.section`
  height: 300px;
  width: fit-content;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;

  .loginErrorMessage {
    margin-top: 20px;
    color: red;
  }

  div.subText {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  @media screen and (max-width: 800px) {
    div.subText {
      width: 89vw;
    }
    button.LoginButton {
      width: 80vw;
    }
  }
`;

export const TextSpace = styled.span`
  display: flex;
  flex-direction: column;
  gap: 15px 15px;
  align-items: center;
  justify-content: center;
`;
