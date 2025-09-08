import styled from 'styled-components';

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

  h3 {
    font-size: 24px;
    font-style: normal;
    font-weight: 550;
  }

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
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    width: 100%;
    height: 50%;
    background-color: rgba(0, 0, 0, 0.5);
    border-bottom-left-radius: 50%;
    border-bottom-right-radius: 50%;
    background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='24' height='25' viewBox='0 0 24 25' fill='none'><path fill-rule='evenodd' clip-rule='evenodd' d='M15.2705 3.52661C16.4373 3.37742 17.6177 3.85453 18.8171 4.98308L18.8185 4.98439C20.0219 6.12248 20.5698 7.27697 20.4876 8.45285C20.4081 9.5891 19.7494 10.5508 18.9983 11.3444M18.9983 11.3444L10.7924 20.0301C10.5611 20.282 10.2496 20.4953 9.95427 20.6532C9.65517 20.8131 9.30921 20.95 8.98442 21.0077L8.97943 21.0086L5.76058 21.5584C4.98001 21.693 4.23151 21.4979 3.69831 20.9923C3.16586 20.4873 2.93057 19.751 3.01773 18.9665L3.01799 18.9642L3.38959 15.7102C3.43275 15.3865 3.55297 15.0362 3.69468 14.7323C3.83588 14.4295 4.02853 14.1085 4.25651 13.8658L4.25797 13.8642L12.468 5.17425C13.2195 4.38037 14.1431 3.67079 15.2705 3.52661M13.5578 6.20491L5.34978 14.8928C5.26781 14.9803 5.15521 15.1495 5.05412 15.3663C4.95486 15.5791 4.89524 15.7761 4.87737 15.9026L4.50856 19.1321L4.50845 19.1332C4.46594 19.5181 4.58557 19.7664 4.73048 19.9039C4.87478 20.0407 5.12628 20.1456 5.50572 20.0802L5.50687 20.08L8.72398 19.5305C8.84925 19.5079 9.04227 19.4399 9.24703 19.3304C9.45461 19.2194 9.61076 19.0997 9.68893 19.014L9.69789 19.0042L17.908 10.3142C18.5766 9.6079 18.9482 8.96425 18.9912 8.34826C19.0315 7.7718 18.7945 7.02654 17.7886 6.075C16.7883 5.13401 16.0339 4.94121 15.4608 5.0145C14.8484 5.09281 14.2261 5.49903 13.5578 6.20491Z' fill='%23A0AEC0'></path><path fill-rule='evenodd' clip-rule='evenodd' d='M11.5277 6.39825C11.937 6.33448 12.3204 6.61457 12.3842 7.02385C12.7613 9.44447 14.7259 11.2971 17.1683 11.5431C17.5804 11.5846 17.8809 11.9523 17.8394 12.3645C17.7979 12.7766 17.4301 13.077 17.018 13.0355C13.9004 12.7215 11.385 10.3541 10.9021 7.25476C10.8383 6.84548 11.1184 6.46201 11.5277 6.39825Z' fill='%23A0AEC0'></path></svg>");
    background-repeat: no-repeat;
    background-position: center;
    background-size: 30%;
  }
`;
