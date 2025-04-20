import styled from "styled-components";

export const EmployeeListContainer = styled.section`
  .profilePicArea {
    display: flex;
    align-items: center;
    gap: 10px;
    padding-top: 10px;
  }

  .nameAndMail {
    display: flex;
    flex-direction: column;
  }

  .employeeMail {
    font-size: 12px;
    color: #a0aec0;
  }
`;

export const EmployeeListHeadSection = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;

  div {
    display: flex;

    &:first-child {
      flex-direction: column;
    }
    &:last-child {
      gap: 15px;
    }
    span {
      color: ${(props) => props.theme.colors.grayColors.gray7};
      font-family: Nunito;
      font-size: 14px;
      font-style: normal;
      font-weight: 500;
      line-height: 160%;
    }
  }
`;

export const EmployeeListFilterSection = styled.section`
  display: flex;
  padding: 24px 0;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  flex: 1 0 0;
  border-radius: 16px;
  height: fit-content;
`;

export const SearchEmployee = styled.input`
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  flex-grow: 2;
`;

export const TableBodyRow = styled.tr`
  height: 56px;
  border-bottom: 1px solid ${(props) => props.theme.colors.blackColors.white2};

  &:hover {
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
    cursor: pointer;
  }

  td {
    padding: 0 10px;
  }
`;

export const Monogram = styled.span`
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 0.2px;
  font-size: 14px;
  font-weight: 700;
  border-radius: 50%;
  position: relative;
  overflow: hidden;

  &.quickDetails {
    height: 100px;
    width: 100px;
    font-size: 35px;
  }
  &.modal-monogram {
    width: 250px;
    height: 250px;
    font-size: 80px;
  }

  &.R,
  &.I,
  &.V,
  &.B {
    background-color: #ceefdf;
    color: #27a376;
  }

  &.J,
  &.O,
  &.W {
    background-color: #d6cdea;
    color: #9a78e3;
  }

  &.A,
  &.E,
  &.K,
  &.P,
  &.X {
    background-color: #cdf5f6;
    color: #27a375d1;
  }

  &.F,
  &.L,
  &.T,
  &.Q,
  &.Y {
    background-color: #9c9be1;
    color: #ffffff;
  }

  &.C,
  &.G,
  &.M,
  &.S,
  &.X {
    background-color: ${(props) =>
      props.theme.colors.backgroundColors.blueShade};
    color: #276f96ac;
  }

  &.D,
  &.H,
  &.N,
  &.U {
    background-color: #cbe4f9;
    color: #58a5e5;
  }

  &.unique-monogram--hover-enabled:hover::after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 50%;
    background-color: rgba(0, 0, 0, 0.5);
    border-bottom-left-radius: 50%;
    border-bottom-right-radius: 50%;
  }
`;
