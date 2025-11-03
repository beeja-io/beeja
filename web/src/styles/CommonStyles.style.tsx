import { Link } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import styled from 'styled-components';

export const ProfileIcon = styled.img<{ width?: string; height?: string }>`
  width: ${(props) => (props.width ? props.width : '32px')};
  height: ${(props) => (props.height ? props.height : '32px')};
  border-radius: 50%;
`;

export const StyledNavLink = styled(NavLink)`
  color: ${(props) => props.theme.colors.blackColors.black1};
  text-decoration: none;
  &.active {
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
`;

export const Svg = styled.svg<{ fillColor?: string }>`
  fill: ${(props) =>
    props.fillColor ? props.fillColor : props.theme.colors.brandColors.primary};
`;

export const DynamicSpaceMainContainer = styled.section``;

export const Button = styled.button<{
  width?: string;
  padding?: string;
  height?: string;
  backgroundColor?: string;
  border?: string;
  color?: string;
  fontSize?: string;
}>`
  display: flex;
  width: ${(props) => (props.width ? props.width : '162px')};
  height: ${(props) => (props.height ? props.height : '56px')};
  padding: ${(props) => (props.padding ? props.padding : '18px 24px')};
  justify-content: center;
  align-items: center;
  gap: 8px;
  border-radius: 10px;
  border: ${(props) =>
    props.border
      ? props.border
      : `1px solid ${props.theme.colors.blackColors.black4}`};
  background-color: ${(props) =>
    props.backgroundColor
      ? props.backgroundColor
      : props.theme.colors.blackColors.white6};
  color: ${(props) =>
    props.color ? props.color : props.theme.colors.blackColors.black1};
  outline: none;
  cursor: pointer;
  font-size: ${(props) => (props.fontSize ? props.fontSize : '12px')};
  white-space: nowrap;

  &.submit {
    background-color: #005792;
    color: #fff;
    border: none;
  }

  &.LoginButton {
    font-size: 16px;
    font-weight: 400;
    transition: 0.25s ease-in;
    border: 1px solid #005792;
    margin-top: 40px;
    gap: 30px;
    &:hover {
      background: #005792;
      color: white;
      box-shadow: 0px 10px 30px 0px rgba(11, 161, 235, 0.4);
      border: none;
    }
  }
  &.shadow {
    box-shadow: 0px 10px 30px 0px rgba(0, 87, 146, 0.2);
  }
  &.buttonstyle {
    font-size: 15px;
    padding: 5px 10px;
    height: 50px;
    width: auto; /* Adjust width automatically to content */
  }
  &.btnMobile {
    &:first-child {
      background-color: ${(props) =>
        props.theme.colors.backgroundColors.primary};
      color: ${(props) => props.theme.colors.blackColors.black1};
      border: 1px solid ${(props) => props.theme.colors.blackColors.black5};
    }
    @media screen and (max-width: 600px) {
      width: 140px;
      height: 48px;
    }
  }

  &.formValidated {
    background-color: #005792;
    border: 1px solid #005792;
    color: white;
  }

  &.noFormValidated {
    background-color: #d1d5da;
    border: 1px solid #d1d5da;
    color: white;
    cursor: not-allowed;
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
  &:disabled {
    cursor: not-allowed;
    opacity: 1; 
  }

  &.cancel:disabled {
    background-color: #ffffff;
    color: #d9d9d9;
    border: 1px solid #d9d9d9;
  }

  &.submit:disabled {
    background-color: #d9d9d9;
    color: #ffffff;
    border: 1px solid #ffffff;
  
  @keyframes button-loading-spinner {
    from {
      transform: rotate(0turn);
    }

    to {
      transform: rotate(1turn);
    }
  }
`;

export const StyledLink = styled(Link)`
  color: ${(props) => props.theme.colors.blackColors.black1};
  text-decoration: ${(props) => props.theme.colors.blackColors.black1};
`;

export const Select = styled.select`
  background-color: #f1f2f4;
`;

export const HighlightedBoxes = styled.div`
  width: 100%;
  padding: 15px 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: center;
  gap: 20px;

  div {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    background-color: ${(props) => props.theme.colors.blackColors.white6};
    border-radius: 10px;
    box-shadow: 1px 1px 10px 1px #88888811;
    min-width: 200px;
    width: fit-content;
    height: 88px;
    padding: 20px;
    gap: 5px;
    cursor: default;

    h4 {
      font-weight: bold;
      font-size: 16px;
    }

    span {
      font-weight: medium;
      font-size: 14px;
      color: ${(props) => props.theme.colors.grayColors.gray7};
    }
  }
`;
