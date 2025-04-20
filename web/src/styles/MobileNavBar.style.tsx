import styled from "styled-components";

export const MobileNavMainContainer = styled.nav`
  &.div {
    position: relative;
  }
  @media screen and (max-width: 600px) {
    width: 50%;
    height: 100vh;
    position: fixed;
    z-index: 99999;
    top: 0;
    left: 0;
    justify-content: center;
    align-items: center;

    .hamburger {
      position: absolute;
      right: 0;
      width: 50px;
      height: 50px;
      background-color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
    }

    ul {
      margin-top: 20px;
      height: 100%;
    }

    ul li {
      position: relative;
      margin: 8px 0;
      list-style: none;
    }
    ul li a {
      display: flex;
      height: 100%;
      width: 100%;
      padding: 5px 0;
      align-items: center;
      text-decoration: none;
      background-color: #fff;
      position: relative;
      transition: all 0.5s ease;
      z-index: 1;
      cursor: pointer;
    }

    i {
      margin: 5px 10px;
    }
  }
`;
