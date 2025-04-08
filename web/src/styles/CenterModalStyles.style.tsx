import styled from 'styled-components';

export const CenterModalContainer = styled.section`
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
  overflow-y: scroll !important;
  // &.scrollable {
  //   overflow-y: scroll;
  // }
  &::-webkit-scrollbar {
    width: 10px;
    background-color: transperant;
  }
  &::-webkit-scrollbar {
    display: none;
  }

  &.loanRejectModal {
    display: flex;
    align-items: center;
    justify-content: center;
  }
`;

export const CenterModelMainContainer = styled.section<{
  Width?: string;
  isExpanded?: boolean;
}>`
  display: inline-flex;
  // height: 415px;
  width: ${(props) => (props.Width ? props.Width : '500px')};
  padding: 35px 45px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 26px;
  flex-shrink: 0;
  position: relative;

  border-radius: 16px;
  background: ${(props) => props.theme.colors.backgroundColors.primary};

  ${(props) =>
    props.isExpanded &&
    `
      width: 720px;
      height: 576px;
    `}

  &.loginModal {
    height: 350px;
    width: 500px;

    @media screen and (max-width: 500px) {
      height: 200px;
      width: 80%;
    }
  }

  .iconArea {
    height: fit-content;
    max-height: 100px;
  }

  .modalHeading {
    color: ${(props) => props.theme.colors.blackColors.black1};
    text-align: center;
    font-family: Nunito;
    font-size: 28px;
    font-style: normal;
    font-weight: 700;
    //line-height: 125%; /* 40px */
  }
  .modalContent {
    color: ${(props) => props.theme.colors.blackColors.black1};
    text-align: center;
    font-size: 22px;
    font-style: normal;
    font-weight: 500;
    //line-height: 125%; /* 35px */
    font-family:Nunito;
    margin-top:10px;
  }

  @media screen and (max-width: 500px) {
    .modalHeading {
      font-size: 24px;
    }
    .modalContent {
      font-size: 20px;
    }
    .iconArea {
      display: none;
    }
  }
  &.mediumPopUp {
    min-width: 700px;
  }
  .controlButtonArea {
    display: flex;
    gap: 30px;

    button {
      display: flex;
      width: 180px;
      height: 56px;
      padding: 16px 0px;
      justify-content: center;
      align-items: center;
      border-radius: 10px;
      background: #005792;
      color: var(--Others-White, var(--Color, #fff));
      text-align: center;
      font-family: Nunito;
      font-size: 16px;
      font-style: normal;
      font-weight: 500;
      line-height: 150%;
      letter-spacing: 0.3px;
      outline: none;
      border: none;
      cursor: pointer;

      &:disabled {
        cursor: progress;
      }

      &.closeButton {
        background-color: ${(props) =>
          props.theme.colors.backgroundColors.primary};
        color: ${(props) => props.theme.colors.blackColors.black7};
        border: 2px solid ${(props) => props.theme.colors.blackColors.black1};
      }
      &.mobileBtn {
        @media screen and (max-width: 500px) {
          width: 70px;
          padding: 0 30px;
        }
      }
      &.loading::after {
        content: '';
        width: 16px;
        height: 16px;

        margin: auto;
        border: 4px solid transparent;
        border-top-color: #ffffff;
        border-radius: 50%;
        animation: button-loading-spinner 1s ease infinite;
      }

      @keyframes button-loading-spinner {
        from {
          transform: rotate(0turn);
        }

        to {
          transform: rotate(1turn);
        }
      }
    }
  }
  .compactButtonContainer {
    gap: 24px;
    padding: 5px 0 40px button {
      width: 180px;
      height: 56px;
      padding: 12px 0;
    }
  }
  .modalRejectHeading {
    width: 410px;
    height: 40px;
    font-family: Nunito;
    font-size: 32px;
    font-weight: 600;
    line-height: 40px;
    letter-spacing: 0px;
    text-align: center;
  }
  .modalFieldText {
    height: 32px;
    top: 123px;
    left: 47px;
    font-family: Nunito;
    font-weight: 500;
    font-size: 20px;
    line-height: 32px;
    margin-top: 15px;
    display: flex;
    align-items: center;
  }
  .modalTextArea {
    width: 405px;
    height: 102px;
    top: 22px;
    padding: 15px, 206px, 65px, 13px;
    border-radius: 10px;
    border: 1px;
    gap: 10px;
    border: 1px solid #e9eaec;
    margin-top: 10px;
    outline: None;
    padding: 10px;
    resize: none;
  }
  .modalTextArea::-webkit-scrollbar {
    width: 10px;
  }

  .modalTextArea::-webkit-scrollbar-thumb {
    background-color: #005792;
    border-radius: 4px;
  }

  &.loanRejectModalInner {
    width: 500px;
    height: fit-content;

    .controlButtonArea {
      margin: 10px auto 20px auto;
    }
  }
  .passwordArea {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .passwordBox {
    background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
    padding: 5px 15px;
    border-radius: 10px;
  }
  .icon {
    display: flex;
    justify-content: center;
    align-items: center;

    :hover {
      cursor: pointer;
    }
  }
`;
export const EditProfileText = styled.div`
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 16px;
  font-weight: bold;
  z-index: 1;
`;
export const CloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  z-index: 1;
  color: black;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  background-color: #fff;
`;
export const DashedBox = styled.div`
  border: 2px dashed #ccc;
  width: 100%;
  height: 250px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  margin: 30px auto 0;
`;

export const DashedBoxContent = styled.div`
  display: flex;
  align-items: center;
  flex-direction: column;
`;

export const DropText = styled.span`
  margin-top: 8px;
  font-size: 14px;
`;
export const MonogramArea = styled.div`
  margin-bottom: 40px;
  padding-top: 30px;
`;
export const CenterModalTypeTwoInnerContainer = styled.section`
  display: flex;
  width: fit-content;
  height: fit-content;
  min-width: 400px;
  padding: 35px 45px;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 26px;
  flex-shrink: 0;
  margin-bottom: 30px;
  // overflow: scroll;

  border-radius: 16px;
  background: ${(props) => props.theme.colors.backgroundColors.primary};

  position: absolute;
  top: 10px;

  .headingSection {
    align-self: flex-start;
    font-size: 24px;
    font-style: normal;
    font-weight: 500;
    line-height: 130%;
    width: 100%;
    display: flex;
    justify-content: space-between;

    .heading {
      width: 227px;
      color: ${(props) => props.theme.colors.blackColors.black7};
      font-family: Nunito;
      font-weight: 700;
      display: flex;
      flex-direction: column;
      flex-wrap: wrap;
    }
    .subHeading {
      color: ${(props) => props.theme.colors.blackColors.black7};
      width: 173px;
      font-family: Nunito;
      font-size: 16px;
      font-style: normal;
      font-weight: 400;
      line-height: 160%; /* 25.6px */
    }
  }

  .modalSVG {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;

    .closeIcon {
      width: 40px;
      height: 40px;
      margin-top: -20px;
      margin-right: 40px;
      cursor: pointer;
    }
  }

  .react-pdf__Page__canvas {
    width: 100% !important;
    height: 730px !important;
  }

  .pageButton {
    width: 16px;
    height: 16px;
    border-radius: 5px;
    border: 1px solid #f1f2f4;
    background-color: #ffffff;
    &:hover {
      background: #f0efef;
      color: #ffffff;
    }
  }

  .pages {
    margin-left: 5px;
    font-size: 12px;
    font-family: Nunito;
    color: #687588;
    font-weight: 500;
  }

  .downloadButton {
    height: 40px;
    display: inline-flex;
    width: 40px;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    background-color: #fff;
    cursor: pointer;
    box-shadow: 0 8px 16px #00589241;
  }

  .downloadSVG {
    display: flex;
    color: #005792;
    text-align: center;
    font-family: Nunito;
    font-size: 16px;
    font-style: normal;
    font-weight: 700;
    line-height: 150%;
    letter-spacing: 0.3px;
    gap: 5px;
    justify-content: center;
    align-items: center;
  }
  .dashedBox {
    border: 2px dashed black;
    padding: 40px;
    text-align: center;
    margin: 20px 0;
    width: 400px;
    height: 400px;
    background-color: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .dashedBoxContent {
    font-size: 16px;
    color: #999;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 400px;
  }

  .upload-icon {
    font-size: 32px;
    margin-bottom: 10px;
  }

  .modal-header {
    position: relative;
    width: 100%;
    height: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
  }

  .edit-profile-text {
    font-size: 16px;
    color: #000;
  }

  .close-button {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: black;
  }

  .close-button:hover {
    color: #f00;
  }

  .controlButtonArea {
    display: flex;
    gap: 30px;
    justify-content: center;

    button {
      width: 180px;
      height: 56px;
      padding: 16px 0px;
    }
  }

  .modal-monogram .controlButtonArea,
  .dashedBox .controlButtonArea,
  .imageSelected .controlButtonArea {
    gap: 15px;
  }

  .modal-monogram .controlButtonArea button,
  .dashedBox .controlButtonArea button,
  .imageSelected .controlButtonArea button {
    width: 150px;
    height: 50px;
    padding: 12px 0;
  }

  // &.scrollable {
  //   margin-top: 100px;

  //   @media screen and (max-width: 1150px) {
  //     margin-top: 560px;
  //   }
  //   @media screen and (max-width: 1330px) {
  //     margin-top: 60px;
  //   }
  //   @media screen and (height<760px) {
  //     /* margin-top: 410px; */
  //     position: absolute;
  //     top: 10px;
  //   }
  // }
`;
export const CenterModalTypeExpenseInnerContainer = styled.div`
  width: 40%; /* Adjust modal width */
  max-width: 700px; /* Set a maximum width for responsiveness */
  //background: #ffffff;
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  text-align: center; /* Center content inside the modal */
  position: relative; /* For positioning elements like the close button */

  .headingSection {
    margin-bottom: 20px; /* Add space below the heading */
  }

  .heading {
    font-size: 1.8rem; /* Larger font size for the heading */
    font-weight: 700; /* Bold heading */
    //color: #333; /* Darker text color */
    margin: 0;
  }

  .buttonSection {
    margin-top: 25px; /* Add space between content and buttons */
  }
`;
