import React from "react";
import styled, { keyframes } from "styled-components";
import SpinnerImage from "./../../images/loader_v1.png";

const rotate = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

const LoaderContainer = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
`;

const LoaderContent = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
`;

const Spinner = styled.img`
  width: 50px;
  height: 50px;
  animation: ${rotate} 1s infinite linear;
`;

const SpinAnimation: React.FC = () => {
  return (
    <LoaderContainer>
      <LoaderContent>
        <Spinner src={SpinnerImage} alt="spinner" />
      </LoaderContent>
    </LoaderContainer>
  );
};

export default SpinAnimation;
