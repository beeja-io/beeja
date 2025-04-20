import styled, { keyframes } from "styled-components";

const rippleAnimation = keyframes`
  0% {
    transform: scale(0.2);
    opacity: 1;
  }
  100% {
    transform: scale(2);
    opacity: 0;
  }
`;

const RippleLoaderWrapper = styled.div<{ height?: string }>`
  display: flex;
  justify-content: center;
  align-items: center;
  height: ${(props) => (props.height ? props.height : "100vh")};
  position: relative;
  background-color: ${(props) => props.theme.colors.blackColors.white};
`;

const Ripple = styled.div`
  width: 40px;
  height: 40px;
  border: 5px solid ${(props) => props.theme.colors.brandColors.primary};
  border-radius: 50%;
  animation: ${rippleAnimation} 1.5s linear infinite;
  position: absolute;
`;

type PulseLoaderProps = {
  height?: string;
};
const PulseLoader = (props: PulseLoaderProps) => {
  return (
    <RippleLoaderWrapper height={props.height ? props.height : undefined}>
      <Ripple />
    </RippleLoaderWrapper>
  );
};

export default PulseLoader;
