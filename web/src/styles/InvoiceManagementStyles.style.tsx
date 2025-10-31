import styled from 'styled-components';
import { Button } from './CommonStyles.style';
export const InvoiceManagementHeading = styled.div`
  margin: 20px;
  font-weight: bold;
  font-size: 20px;
  .highlight {
    color: #005792;
  }
`;
export const InvoiceManagementMainContainer = styled.section`
  padding: 0 40px;
  padding-bottom: 20px;
  display: flex;
  gap: 10px;
`;
export const InvoiceAddFormSubContainer = styled.form`
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  margin: 10px 0px;
  padding: 24px;
  .remarks {
    font-size: 12px;
    fort-weight: bold;
  }
  .remarksNote {
    font-size: 12px;
    color: var(--greyscale-600, #687588);
    margin: 0px 5px;
  }
  .heading {
    font-size: 14px;
  }
`;
export const InvoiceInnerBigContainer = styled.div`
  .Project_Heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    span {
      display: block;
    }
  }
  .projectName {
    font-size: 20px;
    font-weight: bold;
    color: #005792;
  }
  .projectDetails {
    padding: 10px 0;
    border-bottom: 1px solid
      ${(props) => props.theme.colors.grayColors.grayscale300};
    display: flex;
    div {
      display: flex;
      align-items: center;
      gap: 5px;
      margin-right: 10px;
    }
  }
  .button_element {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 15px 32px;
    border: none;
    border-radius: 10px;
    background-color: #005792;
    color: #ffffff;
    font-size: 14px;
    font-weight: 600;
    font-family: 'Nunito', sans-serif;
    box-shadow: 0px 4px 8px rgba(0, 87, 146, 0.25);
    cursor: pointer;
    transition: all 0.2s ease;

    &:active {
      background-color: #00466e;
      box-shadow: 0px 3px 6px rgba(0, 87, 146, 0.25);
      transform: translateY(0);
    }

    &:disabled {
      background-color: #a5c8de;
      cursor: not-allowed;
      box-shadow: none;
    }
  }
`;
export const InvoiceInnersmallContainer = styled.div`
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  padding: 20px;
  margin: 10px;
  width: 30%;
  .clientDetails {
    display: flex;
    margin: 20px 0px;
  }
  .align {
    display: flex;
    flex-direction: column;
    margin: 0px 20px;
  }
  .text {
    font-size: 13px;
    color: #005792;
  }
  > div:last-child {
    display: flex;
    justify-content: flex-start;
    gap: 20px;
    margin-top: 10px;
  }
  > div:last-child > span {
    display: flex;
    align-items: flex-start;
    gap: 6px;
    font-size: 15px;
  }
  > div:last-child > span > svg {
    transform: translateY(-1px);
  }
`;

export const InvoiceAddFormMainContainer = styled.div`
  width: 70vw;
  @media screen and (max-width: 1150px) {
    width: 70vw;
  }
  .addRows {
    margin: 0px 10px 10px 10px;
    font-size: 13px;
    color: #005792;
  }
  .formButtons {
    display: flex;
    justify-content: center;
    gap: 40px;
  }
  .submitButton {
    background-color: #005792;
    color: #fff;
    border: none;
    display: flex;
    width: 162px;
    height: 56px;
    padding: 18px 24px;
    justify-content: center;
    align-items: center;
    gap: 8px;
    border-radius: 10px;
    outline: none;
    cursor: pointer;
  }
  .spanSign {
    display: flex;
    flex-direction: column;
    margin: 10px 0px;
  }
  .spanSign span {
    margin: 5px 0px;
  }
`;

export const InvoiceRemittance = styled.div`
  display: flex;
  justify-content: space-between;
  border-radius: 6px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  padding: 10px;
  margin: 20px 10px;
  label {
    color: var(--greyscale-600, #687588);
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
  }
  .length {
    width: 33%;
  }
  .spanElement {
    display: block;
  }
  .applyMargin {
    margin: 0px 5px;
    font-size: 14px;
  }
`;

export const InvoiceAddressContainer = styled.div`
width:100%;
display:flex;
justify-content:space-between;
margin:10px;
.adjusting{
 display:flex;
 flex-direction:column;
 overflow-wrap:break-word;
 width:30%
}
.applyStyle1{
 color: #005792;
 font-size:13px;
}
.applyStyle2{
 color: ${(props) => props.theme.colors.blackColors.black1};
 font-size:15px;
 margin-left:10px;
}
.textFont{
    color: var(--greyscale-600, #687588);
    font-family: Nunito;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
    margin:10px 0px;
  }
.arrowAdjust{
 display:flex;
 align-items:center;
 
}
}
`;
export const AddRowContainer = styled.div`
margin-top: 10px;
margin-bottom: 10px;
  .rowsAlign {
    display: flex;
    margin-top: 10px;
    flex-wrap: wrap;  
  }
.alignButton {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;

  button {
    border: none;
    background: transparent;  
    padding: 0;             
    cursor: pointer;          
    display: flex;           
    align-items: center;
    justify-content: center;
  }
  button:last-child svg path {
    fill: ${(props) => props.theme.colors.blackColors.black1};
  }

}
  .rowItem {
    display: flex;
    flex-direction: column;
    label {
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      color: ${(props) => props.theme.colors.grayColors.gray7};
      font-size: 12px;
      font-style: nounito;
      font-weight: 700;
      letter-spacing: 0.2px;
    }
    input {
      padding: 5px;
      border-radius: 6px;
      border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
      color: ${(props) => props.theme.colors.blackColors.black1};
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      outline: none;
      transition: none;

      &:focus,
      &:active,
      &:valid,
      &:invalid {
        background-color: ${(props) => props.theme.colors.blackColors.white3};
      }

      &:-webkit-autofill,
      &:-webkit-autofill:hover,
      &:-webkit-autofill:focus {
        -webkit-box-shadow: 0 0 0 1000px
          ${(props) => props.theme.colors.blackColors.white3} inset !important;
        -webkit-text-fill-color: ${(props) =>
          props.theme.colors.blackColors.black1} !important;
        transition: background-color 9999s ease-in-out 0s;
      }

      &:focus {
        border-color: ${(props) => props.theme.colors.brandColors.primary};
      }
    }

    input[name="serialNo"] {
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      cursor: not-allowed;
      width: 50px;
    }
    input[name="contract"] {
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      width: 150px;
    }
    input[name="description"] {
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      width: 500px;
    }
    input[name="price"] {
      background-color: ${(props) => props.theme.colors.blackColors.white3};
      width: 110px;
    }
  }
  .r1{
    margin-right: 37px;
    margin-left: 5px;
  }
  .r2{
    margin-right: 50px;
  }
  .r3{
    margin-right: 98px;
  }
  .r4{
    margin-right: 40px;
  }
  .errorSpan {
    font-size: 12px;
    color: ${(props) => props.theme.colors.stateColors.error};
    margin: 5px 0px;
  }

  @media screen and (max-width: 768px) {
    .rowsAlign {
      flex-direction: column;
      align-items: stretch;
  }

  @media screen and (max-width: 1024px) {
    .rowItem input[name='description'] {
      width: 350px;
    }
    .rowItem input[name='contract'] {
      width: 120px;
    }
  }

  @media screen and (max-width: 768px) {
    .rowItem input {
      width: 100% !important;
    }
    .r1,
    .r2,
    .r3,
    .r4 {
      margin-right: 0;
      margin-left: 0;
    }
  }

  @media screen and (max-width: 480px) {
    .alignButton {
      flex-direction: column;
      gap: 8px;
    }
    .rowsAlign {
      flex-direction: column;
      align-items: stretch;
    }
    .rowItem label {
      font-size: 11px;
    }
    .rowItem input {
      font-size: 12px;
      padding: 4px;
    }
  }
`;
export const ValidationText = styled.span`
  color: #e03137;
  font-size: 12px;
  font-weight: 400;
  line-height: 160%;
  display: flex;
  align-items: center;
  margin-top: 4px;
  &.star {
    margin-top: 0px;
    font-size: 15px;
    font-weight: 400;
    display: inline-block;
  }
`;
export const InvoiceButtonContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin: 20px 0px;
`;
export const InvoiceButton = styled(Button)<{
  disabled?: boolean;
  variant?: 'send' | 'download';
}>`
  width: 150px;
  height: 55px;
  padding: 12px 12px;
  font-weight: bold;

  ${({ disabled, variant }) =>
    disabled &&
    `
      background-color: ${variant === 'download' ? '#ecececff' : '#e8e8e8ff'};
      color: #a0a0a0;
      cursor: not-allowed;
      border: 1px solid #ccc;
  `}
  &.submit {
    background-color: ${({ disabled }) => (disabled ? '#e0e0e0' : undefined)};
    color: ${({ disabled }) => (disabled ? '#a0a0a0' : undefined)};
    cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
  }
`;
export const TableShowing = styled.div`
  margin: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 6px;
`;
export const InvoiceDetails = styled.div`
  border-radius: 6px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  display: flex;
  flex-direction: column;
  padding: 15px;
  margin: 10px;
  .dateSet {
    display: flex;
  }
  .applyMargin {
    margin: 0px 5px;
  }
  .applyMargin1 {
    margin: 10px 0px;
    font-size: 15px;
  }
  .sub-invoicedetails {
    display: flex;
    justify-content: space-between;
  }
  .fontSize {
    font-size: 15px;
    font-weight: bold;
  }
  .fontColor {
    color: #005792;
  }
  .filterCalender {
    position: absolute;
    display: flex;
    border-radius: 16px;
    background: ${(props) => props.theme.colors.backgroundColors.primary};
    box-shadow: 0px 5px 40px 0px rgba(0, 0, 0, 0.1);
  }
`;
export const TextInput = styled.input`
  outline: none;
  border: none;
  border-radius: 5px;
  background-color: #f1f2f4;
  padding: 4px 3px;
  color: #000;
  width: 160px;
  margin: 0px 10px;
`;

export const TableHead = styled.thead`
  background-color: ${(props) => props.theme.colors.blackColors.white3};
  color: ${(props) => props.theme.colors.grayColors.gray7};
  font-size: 16px;
  font-style: normal;
  font-weight: 700;
  line-height: 160%;
  letter-spacing: 0.2px;
  height: 56px;

  tr {
    text-align: left;
    border-radius: 10px;

    th {
      font-size: 12px;
    }
  }
  .spacesno {
    width: 200px;
  }
  .spacetask {
    width: 600px;
  }
`;

export const TableHeadLabel = styled.label`
  display: inline-flex;
  align-items: center;
  gap: 4px;
`;

export const TableBodyRow = styled.tr`
  height: 45px;
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};
  &:hover {
    border-top: 2px solid #005792;
    border-color: #005792;
  }
  td {
    padding: 0 10px;
    font-size: 12px;
    vertical-align: middle;
  }
  svg path {
    fill: ${(props) => props.theme.colors.blackColors.black1};
  }
`;
export const DatePicker = styled.div`
  .dateName {
    display: flex;
    span svg path {
      fill: ${(props) => props.theme.colors.grayColors.gray9};
    }
  }
  .dateChild {
    font-family: Nunito;
    font-size: 12px;
    font-weight: 500;
    line-height: 20px;
    letter-spacing: 0px;
    padding-right: 10px;
    text-align: left;
    color: ${(props) => props.theme.colors.blackColors.black1};
  }
`;
export const InvoiceCalculationContainer = styled.div`
display:flex;
flex-direction:column;
align-items:flex-end;
gap:10px;
margin:20px 0px;
}
`;
export const TableRow = styled.tr`
  height: 35px;
  border-bottom: 1px solid #55565735;
  color: #687588;
  td {
    font-size: 12px;
    vertical-align: middle;
  }
  .style1 {
    color: ${(props) => props.theme.colors.blackColors.black1};
    font-weight: 600;
    padding-left: 2px;
  }
  .sidehead {
    font-size: 14px;
    font-weight: 700;
    padding-right: 80px;
    padding-left: 15px;
  }
  .sidehead2 {
    padding-right: 50px;
  }
`;
export const Tablelist = styled.table`
  .borderCollapse {
    border-collapse: collapse;
    margin: 10px;
  }
`;
export const TableList = styled.table`
  border: 0;
  width: 100%;
  border-collapse: collapse;
  /*  z-index: -1; */

  thead {
    background-color: ${(props) => props.theme.colors.blackColors.white3};
    color: ${(props) => props.theme.colors.grayColors.gray7};
    font-size: 16px;
    font-style: normal;
    font-weight: 700;
    line-height: 160%;
    letter-spacing: 0.2px;
    height: 56px;

    tr th {
      padding: 0 10px;
      font-size: 12px;
    }
  }
`;
export const TableBodyRows = styled.tr`
  height: 30px;
  td {
    padding: 0 10px;
    font-size: 12px;
  }
`;
export const InvoicePaymentContainer = styled.div`
  border-radius: 6px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  padding: 10px;
  margin: 20px 10px;
  .PayDet {
    font-size: 12px;
    color: #005792;
    padding: 5px 10px;
  }
`;
export const InvoiceItem = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0px 8px;
  gap: 12px;

  width: 717px;
  height: 64px;

  background: #ffffff;
  border-bottom: 1px solid #f1f2f4;
  border-radius: 7px;

  flex: none;
  order: 0;
  align-self: stretch;
  flex-grow: 0;

  cursor: pointer;

  &:hover {
    background-color: #f9f9f9; /* optional */
  }
`;

export const InvoiceTextContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

export const InvoiceId = styled.span`
  width: 158px;
  height: 22px;

  font-family: 'Nunito', sans-serif;
  font-style: normal;
  font-weight: 500;
  font-size: 14px;
  line-height: 22px;
  letter-spacing: 0.3px;

  color: #121212;

  flex: none;
  order: 0;
  flex-grow: 0;
`;

export const ContractName = styled.span`
  font-family: 'Nunito', sans-serif;
  font-size: 12px;
  line-height: 18px;
  color: #6b6b6b; /* lighter color for secondary text */
`;

export const PdfCell = styled.td`
  text-align: center;
`;

export const PdfWrapper = styled.span`
  display: inline-flex;
  align-items: center;
  gap: 6px;
`;
export const InvId = styled.span`
  font-weight: 500;
  font-size: 11px;
`;
export const Alignmenting = styled.div`
  margin-left: 10px;
`;
