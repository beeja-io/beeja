import styled from 'styled-components';

export const MainErrorScreenContainer = styled.section`
  height: 100svh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  &.serviceUnavailable {
    flex-direction: column;
  }
`;

export const ErrorScreenContentArea = styled.section`
  display: flex;
  align-items: center;
  flex-direction: column;
  height: fit-content;
  gap: 15px;

  font-size: 200px;
  font-weight: 300;
  letter-spacing: 0.3px;

  .errorCode span {
    color: #005792;
  }

  .errorHeading {
    font-size: 50px;
    font-weight: 500;
  }

  .errorDescription {
    font-size: 24px;
    font-style: normal;
    font-weight: 500;
    line-height: 130%;
  }
`;

export const ServiceUnavailableText = styled.section`
  color: #005792;
  font-size: 50px;
  font-weight: 700;
  line-height: 130%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 10px;
  text-align: center;
  .description {
    color: #000;
    font-family: Nunito;
    font-size: 24px;
    font-style: normal;
    font-weight: 500;
    line-height: 130%;
  }

  .submit {
    margin-top: 50px;
  }

  @media screen and (max-width: 500px) {
    font-size: 30px;
    .description {
      font-size: 14px;
      width: 60%;
    }
  }
`;
