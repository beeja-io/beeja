import styled from 'styled-components';

export const MyProfileMainContainer = styled.section`
  width: 100%;
  height: 100%;
  padding: 30px;
  @media screen and (max-width: 740px) {
    padding: 15px;
  }
`;
interface ImagePreviewContainerProps {
  isDragging: boolean;
}
export const ImagePreviewContainer = styled.div<ImagePreviewContainerProps>`
  position: relative;
  width: 250px;
  height: 250px;
  overflow: hidden;
  border-radius: 50%;
  border: 2px solid #000;
  margin: auto;
  cursor: ${(props) => (props.isDragging ? 'grabbing' : 'grab')};
`;

interface ImagePreviewProps {
  offsetX: number;
  offsetY: number;
  scale: number;
}

export const ImagePreview = styled.img<ImagePreviewProps>`
  position: absolute;
  width: auto;
  height: 150%;
  object-fit: cover;
  top: 50%;
  left: 50%;
  transform: ${(props) =>
    `translate(${props.offsetX}px, ${props.offsetY}px) translate(-50%, -50%) scale(${props.scale})`};
  transform-origin: center;
`;

export const ZoomSliderContainer = styled.div`
  margin-top: 20px;
  text-align: center;
  display: flex;
  align-items: center;
`;

export const ZoomSlider = styled.input`
  width: 400px;
  color: #005792;
  height: 6px;
`;

export const SliderIconContainer = styled.div`
  flex: 0 0 30px;
  margin-right: 10px;
  margin-left: 10px;
`;

export const MyProfileInnerContainer = styled.section``;

export const MyProfileHeadingSection = styled.h3`
  padding-bottom: 24px;
  span {
    transform: rotate(90deg);
    display: inline-block;
    margin-right: 5px;
    cursor: pointer;
  }
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const MyProfileQuickDetailsMainContainer = styled.span`
  width: 30%;
  min-width: 350px;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: fit-content;
  position: sticky;
  top: 100px;

  @media screen and (max-width: 1280px) {
    position: relative;
    top: 10px;
  }
  @media screen and (max-width: 1080px) {
    position: relative;
  }
  @media screen and (max-width: 570px) {
    min-width: 100%;
  }
`;
export const MyProfileInformationContainer = styled.section`
  width: 100%;
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
  .actualMainInfo {
    width: 60%;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    height: 400px;
  }
  .quick-details-tabs-skeleton {
    background-color: ${(props) => props.theme.colors.blackColors.white};
    padding: 20px;
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    height: fit-content;

    span:last-child {
      width: 10%;
    }
  }
`;

export const MyProfileQuickDetails = styled.div`
  width: 100%;
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  height: fit-content;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 10px 10px;
  border-radius: 20px;
  white-space: nowrap;
  text-overflow: ellipsis;

  /* border: 1px solid ${(props) => props.theme.colors.blackColors.black1}; */

  img {
    height: 100px;
    width: 100px;
    background-color: white;
    border-radius: 50%;
    border: 2px solid black;
  }

  .statusChangeButton {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }

  .arrowIcon {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
`;

export const MyProfileTabsMainContainer = styled.section`
  width: 60%;
  min-width: 500px;
  border-radius: 20px;
  padding: 20px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  height: fit-content;

  .animationGif {
    position: absolute;
    top: -180px;
    left: 65px;
    overflow: hidden;
    animation: fadeInOut 8s forwards;
    /* background-color: #d6373720; */
  }
  .imageArea {
    display: flex;
    flex-wrap: wrap;
  }
  .firework {
    position: absolute;
    top: 0;
  }
  .eventText {
    font-size: 160px;
    font-weight: 300;
    position: relative;
    top: 40vh;

    animation:
      neon 1.5s infinite alternate,
      fadeInOut 6s forwards;
  }

  @media screen and (max-width: 570px) {
    width: 100%;
    min-width: 300px;
  }

  @keyframes fadeInOut {
    0% {
      opacity: 0;
    }
    10% {
      opacity: 1;
    }
    90% {
      opacity: 1;
    }
    100% {
      opacity: 0;
    }
  }
`;

export const BorderDivLine = styled.hr<{ width?: string }>`
  display: flex;
  width: ${(props) => (props.width ? props.width : '252px')};
  height: 1px;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  border: 1.5px solid ${(props) => props.theme.colors.blackColors.white2};
`;

export const QuickInfoContactContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 10px 0;
  align-items: flex-start;
  width: 70%;
  overflow: hidden;

  div {
    display: flex;
    padding: 5px 0;
    span:first-child {
      width: 15%;
    }
    span:last-child {
      width: 250px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  div.contactInfo {
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
`;

export const QuickInfoDepartmentContainer = styled.div`
  display: flex;
  align-items: center;
  width: 70%;
  margin: 5px 0;
  justify-content: space-between;
  div {
    display: flex;
    flex-direction: column;
  }

  div span:first-child {
    color: var(--greyscale-600, #687588);
    font-family: Nunito;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%; /* 19.2px */
  }

  div span:last-child {
    color: ${(props) => props.theme.colors.blackColors.black1};
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 600;
    line-height: 160%; /* 22.4px */
  }
`;

export const QuickInfoActionButtions = styled.div`
  display: flex;
  width: 80%;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;

  span {
    width: fit-content;
    display: flex;
    flex-direction: column;
    justify-content: center;
    /* position: relative; */
  }

  .statusChangeButtons {
    position: relative;
    z-index: 1;
  }
`;
export const MyProfileTabsDiv = styled.div`
  /* FIXME */
  overflow: hidden;
  text-align: center;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: Nunito;
  font-size: 14px;
  font-style: normal;
  font-weight: 700;
  line-height: 160%; /* 22.4px */
  letter-spacing: 0.2px;
  padding: 5px 0;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  ul {
    display: flex;
    gap: 30px;
    border-bottom: 1px solid ${(props) => props.theme.colors.blackColors.white2};
    overflow-x: scroll;

    scrollbar-width: none;
    -ms-overflow-style: none;
  }

  ul::-webkit-scrollbar {
    display: none;
  }

  ul li {
    display: inline-block;
    cursor: pointer;
    padding: 0 10px;

    &.active {
      color: #005792;
      border-bottom: 2px solid #005792;
    }
  }
`;

export const UnderLineEmp = styled.div`
  height: 50px;
  font-size: 18px;
  display: flex;
  border-bottom: 1px solid #f1f2f4;
  margin-bottom: 20px;
  border-bottom: 1px solid
    ${(props) => props.theme.colors.grayColors.grayscale300};
`;

export const TabContentMainContainer = styled.section`
  width: 100%;
  height: fit-content;
  margin-top: 15px;
  padding: 10px;
  border-radius: 16px;
  border: 2px solid ${(props) => props.theme.colors.blackColors.white2};
  color: ${(props) => props.theme.colors.blackColors.black1};
  background: ${(props) => props.theme.colors.backgroundColors.primary};

  .checkBoxArea {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }

  &.rolesPermissions {
    .scrollableTable {
      overflow-x: scroll;
    }
    table {
      table-layout: fixed;
      width: fit-content;
      border-collapse: collapse;
      tr {
        height: 50px;
        th {
          width: 160px;
          font-weight: 400;
          padding: 0 10px;
          text-align: left;
        }
        td {
          width: 100px;

          &.checkBoxes {
            text-align: center;
          }
        }
      }
    }
  }
`;

export const TabContentMainContainerHeading = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 10px 0;

  h4 {
    font-size: 18px;
    font-weight: 500;
  }
`;

export const TabContentInnerContainer = styled.div`
  display: flex;
  width: 100%;
  justify-content: flex-start;
  gap: 0px 120px;
  flex-wrap: wrap;

  div {
    display: flex;

    /* &:nth-child(2) {
      margin-left: 30px;
    } */
  }
`;

export const TabContentEditArea = styled.span`
  span {
    display: flex;
    gap: 15px;
    height: 25px;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    span:last-child svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
  }
`;

export const TabContentTable = styled.table``;

export const TabContentTableTd = styled.td`
  padding: 4px 8px 14px 0;

  &:first-child {
    width: 90px;
    color: var(--greyscale-600, #687588);
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
    text-overflow: ellipsis;
    overflow: visible;
    white-space: nowrap;
  }

  &:last-child {
    color: ${(props) => props.theme.colors.blackColors.black1};
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
    text-overflow: ellipsis;
    overflow: visible;
    white-space: nowrap;
    padding: 3px 8px 10px 0;
  }
  .validation-error {
    color: #ff0000;
    margin-left: 10px;
    overflow-wrap: break-word;
    white-space: normal;
  }
`;

// Quick Details Styles:

export const UserName = styled.div`
  /* FIXME - Replace with Commons Here */
  color: ${(props) => props.theme.colors.blackColors.black1};
  text-align: center;
  font-family: Nunito;
  font-size: 20px;
  font-style: normal;
  font-weight: 700;
  line-height: 140%; /* 28px */

  margin: 10px 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
`;

export const RolesDiv = styled.div`
  display: flex;
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;

  /* FIXME - With commons */
  color: var(--greyscale-600, #687588);
  text-align: center;

  /* Body/medium/regular */
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: 160%; /* 22.4px */
`;

export const MyProfileButton = styled.button`
  display: flex;
  padding: 4px 16px;
  justify-content: center;
  align-items: center;
  gap: 4px;
  flex: 1 0 0;
  border-radius: 8px;
  background: #e7f7ef;
  outline: none;
  border: none;
  margin: 14px 0;
  cursor: pointer;

  &.inactiveButton {
    background: #ff00002f;
  }
`;

// DropDown for AddRole
export const DropDownContainer = styled.span`
  position: absolute;
  top: 40px;
  z-index: 2;
  height: fit-content;
  padding: 10px;
  display: flex;
  justify-content: center;
  align-items: center;

  border-radius: 12px;
  background: var(--others-white, #fff);

  box-shadow: 5px 5px 50px 0px rgba(26, 32, 44, 0.06);

  ul {
  }

  ul li {
    list-style-type: none;
    padding: 5px;
    cursor: pointer;
    text-align: center;
    border-bottom: 1px solid #e3e3e3;
    color: var(--greyscale-600, #687588);
  }
`;

// inputs
export const InlineInput = styled.input`
  outline: none;
  border: none;
  border-radius: 5px;
  background-color: ${(props) => props.theme.colors.blackColors.white2};
  padding: 4px 3px;
  width: 160px;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const CalendarInputContainer = styled.div`
  position: relative;
  display: flex;
  // justify-content : flex-start;
  // align-items : center;
`;
export const StyledCalendarIconDark = styled.div`
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30px;
  height: 30px;
  right: 50px;
`;

export const CalendarContainer = styled.div`
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0rem 5rem;
`;
