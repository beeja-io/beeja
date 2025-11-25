import styled, { css } from 'styled-components';

export const NavBarContainer = styled.section`
  border-right: 1px solid
    ${(props) => props.theme.colors.grayColors.grayscale300};
  color: ${(props) => props.theme.colors.blackColors.black1};
  &.sidebar {
    max-height: 100vh;
    height: 100vh;
    width: 78px;
    padding: 6px 14px;
    z-index: 2;
    background-color: ${(props) => props.theme.colors.backgroundColors.primary};
    transition: all 0.5s ease;
    position: fixed;
    top: 0;
    left: 0;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    overflow-y: auto;
    scrollbar-width: thin;

    /* Hide the scrollbar */
    &::-webkit-scrollbar {
      width: 0;
    }
  }
  &.sidebar.open {
    width: 250px;
  }
  @media screen and (max-width: 500px) {
    &.sidebar {
      display: none;
    }
  }

  &.sidebar i {
    min-width: 50px;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  &.sidebar li .tooltip {
    position: absolute;
    top: -20px;
    left: calc(100% + 15px);
    /* z-index: 3; */
    background-color: ${(props) => props.theme.colors.blackColors.white};
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.3);
    padding: 6px 14px;
    border-radius: 5px;
    white-space: nowrap;
    opacity: 0;
    pointer-events: none;
  }

  &.sidebar li:hover .tooltip {
    opacity: 1;
    pointer-events: auto;
    transition: all 0.4s ease;
    top: 50%;
    transform: translateY(-50%);
    background-color: ${(props) => props.theme.colors.blackColors.white};
    color: ${(props) => props.theme.colors.blackColors.black1};
  }

  &.sidebar.open li .tooltip {
    display: none;
  }

  &.sidebar li a {
    display: flex;
    height: 100%;
    width: 100%;
    align-items: center;
    text-decoration: none;
    background-color: transparent;
    position: relative;
    transition: all 0.5s ease;
    z-index: 1;
    cursor: pointer;
  }

  &.sidebar li a::after {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    transform: scaleX(0);
    border-radius: 5px;
    transition: transform 0.3s ease-in-out;
    transform-origin: left;
    z-index: -2;
  }

  &.sidebar li a:hover::after {
    transform: scaleX(1);
    color: ${(props) => props.theme.colors.blackColors.white};
  }

  &.sidebar li a .link_name {
    color: ${(props) => props.theme.colors.blackColors.black};
    white-space: nowrap;
    pointer-events: auto;
    transition: all 0.4s ease;
    pointer-events: none;
    opacity: 0;
  }

  &.sidebar li a:hover .link_name,
  .sidebar li a:hover i {
    transition: all 0.5s ease;
    color: ${(props) => props.theme.colors.blackColors.black};
  }

  &.sidebar.open li a .link_name {
    opacity: 1;
    pointer-events: auto;
  }

  &.sidebar li i {
    height: 40px;
    line-height: 35px;
    border-radius: 5px;
  }
  .nav_Link.active_nav_link {
    color: ${(props) => props.theme.colors.brandColors.primary};
  }
  .nav_Link {
    color: ${(props) => props.theme.colors.blackColors.black1};
    // font-size: 14px;
    font-size: var(--font-size-primary);
  }
  /* Width for right side container/space */
  &.sidebar.open ~ .home-section {
    left: 250px;
    width: calc(100% - 250px);

    @media screen and (max-width: 500px) {
      width: 100%;
      left: 0;
    }
  }
  &.sidebar ~ .home-section .topNavHeader {
    width: calc(100% - 78px);
    transition: all 0.5s ease 0s;
  }
  &.sidebar.open ~ .home-section .topNavHeader {
    width: calc(100% - 250px);
    transition: all 0.5s ease 0s;
  }

  &.sidebar li a:hover::after,
  .dropdown-link.selected {
    transform: scaleX(1);
    color: ${(props) =>
      props.theme.colors.blackColors.black}; // Change this to the desired color
  }
  .dropdown-item {
    white-space: pre-line;
  }

  @media screen and (max-width: 800px) {
    // &.sidebar {
    //   display: none;
    // }
  }
`;

export const NavHeader = styled.div<{ isOpen: boolean }>`
  &.logo_details {
    height: 60px;
    display: flex;
    align-items: center;
    position: relative;
    padding-top: 30px;
  }

  &.logo_details .icon {
    opacity: 0;
    transition: all 0.5s ease;
  }

  &.logo_details .logo_name {
    color: ${(props) => props.theme.colors.blackColors.black1};
    font-weight: 600;
    opacity: ${(props) => (props.isOpen ? 1 : 0)};
    transition: all 0.5s ease;
    font-family: 'Rubik';

    .logo_name_blue {
      color: ${(props) => props.theme.colors.brandColors.primary};
    }
  }

  &.logo_details .btn {
    position: absolute;
    top: 50%;
    right: 0;
    transform: translateY(-50%);
    font-size: 23px;
    text-align: center;
    cursor: pointer;
    transition: all 0.5s ease;
    padding-top: 25px;
  }
`;

export const DashBoardButton = styled.span`
  border-radius: 10px;
  background-color: #005792;
  box-shadow: 2px 7px 8px 0px rgba(0, 87, 146, 0.2);
  width: 100%;
  height: 50px;
  color: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  cursor: pointer;
  margin-bottom: 25px;
`;

export const TopNavBar = styled.div`
  height: 96px;
  background: ${(props) => props.theme.colors.backgroundColors.primary};
  color: ${(props) => props.theme.colors.blackColors.black1};
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  margin: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  position: fixed;
  top: 0;
  z-index: 100;

  .menuArea {
    display: none;
  }

  .mainTopNav {
    display: flex;
    align-items: center;
    gap: 25px;
    flex-wrap: wrap;
  }
  .topNavLinks {
    display: flex;
    gap: 15px;
  }
  @media screen and (max-width: 1000px) {
    .topNavLinks {
      display: none;
    }
  }
  @media screen and (max-width: 740px) {
    .mainTopNav {
      display: none;
    }
    height: 65px;
  }
  @media screen and (max-width: 500px) {
    .menuArea {
      display: block;
    }
    height: 65px;
  }
`;

export const SearchBox = styled.div`
  display: flex;
  width: 315px;
  padding: 8px 8px 8px 16px;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  height: 50px;
  flex-shrink: 0;
  align-self: stretch;
  border-radius: 10px;
  margin-bottom: 5px;
  background: ${(props) => props.theme.colors.grayColors.gray6};

  &.search .span {
    display: flex;
    align-items: center;
    gap: 15px;
    svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
  }

  &.search .commandF {
    margin-right: 10px;
    height: 40px;
    width: 40px;
    background-color: ${(props) => props.theme.colors.blackColors.white};
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 5px;
    box-shadow:
      0px -1px 1px 0px rgba(0, 0, 0, 0.04) inset,
      0px -1px 2px 0px rgba(0, 0, 0, 0.05) inset;
    svg path {
      fill: ${(props) => props.theme.colors.blackColors.black1};
    }
  }
`;

export const SearchInput = styled.input`
  color: ${(props) => props.theme.colors.blackColors.black1};
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: 160%;
  outline: none;
  border: none;
  background-color: transparent;
`;

export const LeftNavList = styled.ul`
  margin-top: 20px;
  height: 100%;

  li {
    position: relative;
    margin: 8px 0;
    list-style: none;
  }

  &.bottomLinks {
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
  }

  .dropdown-item {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .dropdown-container {
    margin-left: 62px;
    white-space: nowrap;
    position: relative;
  }
  .dropdown-container:after {
    content: '';
    position: absolute;
    left: -20px;
    top: 0;
    bottom: 28px;
    width: 2px;
    background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 36px;
  }
  .dropdown-link .dropdown-item {
    margin-bottom: -10px;
    align-items: center;
    justify-content: center;
  }
  &.sidebar li a:hover::after,
  .dropdown-link.selected {
    transform: scaleX(1);
    color: ${(props) => props.theme.colors.brandColors.primary};
  }

  .dropdown-link {
    display: flex;
    height: 50px;
    padding: 18px 24px 18px 10px;
    align-items: start;
    border-radius: 10px;
    margin-right: 10px;
    transition: background 0.3s ease;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
    margin-left: 2px;
  }

  .dropdown-link::before {
    content: '';
    position: absolute;
    left: -22px;
    top: 50%;
    transform: translateY(-100%);
    width: 20px;
    height: 8px;
    border-left: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-bottom: 2px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 0 0 0 100px;
  }
  .dropdown-link.selected {
    background: rgba(216, 215, 215, 0.4);
  }

  .dropdown-link:hover {
    background: rgba(216, 215, 215, 0.4);
  }
  .dropdown-item.selected,
  .dropdown-link.selected:hover {
    background: rgba(216, 215, 215, 0.4);
  }
  .dropdown-link.selected a .link_name {
    color: ${(props) => props.theme.colors.brandColors.primary};
  }
  .dropdown-link.selected a i {
    color: ${(props) => props.theme.colors.brandColors.primary};
  }

  .dropdown-link.selected {
    background: rgba(216, 215, 215, 0.4);
  }

  .dropdown-link.selected:hover {
    background: rgba(216, 215, 215, 0.4);
  }

  .dropdown-link.selected .dropdown-item span {
    color: ${(props) => props.theme.colors.blackColors.black};
  }
`;

export const RightSection = styled.div`
  position: relative;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.secondary};
  min-height: 100vh;
  margin-top: 90px;
  padding-top: 5px;
  left: 78px;
  width: calc(100% - 78px);
  transition: all 0.5s ease;

  @media screen and (max-width: 500px) {
    width: 100%;
    left: 0;
  }
`;

export const DynamicSpace = styled.section`
  /* display: inline-block; */
  color: ${(props) => props.theme.colors.blackColors.black1};
  font-size: 20px;
  font-weight: 500;
  margin: 18px;
  padding: 20px;
  margin-bottom: 0;
  background-color: ${(props) => props.theme.colors.blackColors.white};
  border-radius: 16px;
`;

export const TopNavRightIcons = styled.span`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;

  span {
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .language select {
    border: none;
    outline: none;
    color: ${(props) => props.theme.colors.blackColors.black1};
    background-color: ${(props) => props.theme.colors.blackColors.white6};
  }
`;
export const ThemeContainer = styled.div`
  width: 216px;
  height: 59px;
  display: flex;
  padding-top: 16px;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
`;

export const CommonButtonStyles = css`
  width: 100px;
  height: 31px;
  display: flex;
  padding: 6px 16px;
  align-items: center;
  gap: 8px;
  border-radius: 100px;
  border: none;
`;

export const ThemeToggler = styled.div`
  ${CommonButtonStyles};
  width: 204px;
  height: 43px;
  justify-content: center;
  align-items: center;
  gap: 4px;
  align-self: stretch;
  background: ${(props) => props.theme.colors.grayColors.gray8};
  position: relative;

  @keyframes wiggle {
    0%,
    100% {
      transform: rotate(-5deg);
    }
    50% {
      transform: rotate(5deg);
    }
  }

  .beta {
    background-color: #88caf1;
    position: absolute;
    font-size: 10px;
    top: -5px;
    right: 0;
    padding: 4px;
    border-radius: 6px;
    color: black;
    animation: wiggle 0.3s ease-in-out 4;
  }
`;

export const LightTheme = styled.button`
  ${CommonButtonStyles};
  flex: 1 0 0;
  font-size: 12px;
  font-weight: 700;
  line-height: 160%;
  letter-spacing: 0.2px;
  color: ${(props) => props.theme.colors.grayColors.gray9};
  background: ${(props) => props.theme.colors.backgroundColors.secondary};
  box-shadow: 0px 5px 10px 0px rgba(0, 0, 0, 0.1);
`;

export const DarkTheme = styled.button`
  ${CommonButtonStyles};
  color: ${(props) => props.theme.colors.grayColors.gray9};
  background: ${(props) => props.theme.colors.grayColors.grayscale300};
  justify-content: center;
  align-items: center;
  gap: 8px;
`;
