import styled from 'styled-components';

export const InvoiceManagementHeading = styled.div`
  margin:20px;
  font-weight:bold;
  font-size:20px;

  .highlight{
  color: #005792;
  }
`
  

export const InvoiceManagementMainContainer = styled.section`
  padding: 0 40px;
  padding-bottom: 20px;
  display:flex;
  gap:10px;
`;

export const InvoiceAddFormSubContainer = styled.form`
border-radius: 16px;
border: 1px solid  ${(props) => props.theme.colors.grayColors.grayscale300};
margin: 10px 0px;
padding:24px;

.remarks{
  font-size:12px;
}

.remarksNote{
  font-size:12px;
  color: var(--greyscale-600, #687588);;
  margin:0px 5px;
}

.heading{
 font-size:14px;
}
`

export const InvoiceInnerBigContainer = styled.div`
border-radius: 8px;
border: 1px solid  ${(props) => props.theme.colors.grayColors.grayscale300};
padding: 20px; 
margin:10px 0px;
width:70%;

.Project_Heading{
 display:flex;
 justify-content:space-between;
 align-items:center;
 padding:10px 0; 


 span{
display:block;
}
}

.projectName{
   font-size:20px;
   font-weight:bold;
   color: #005792;
}

.projectDetails{
padding:10px 0;
border-bottom:1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
display:flex;


 div{
   display:flex;
   align-items:center;
   gap:5px;
   margin-right:10px;
 }
}

.button_element{
border-radius: 10px;
  background-color: #005792;
  box-shadow: 2px 7px 8px 0px rgba(0, 87, 146, 0.2);
  padding:10px;
  color: #fff;
  cursor: pointer;
  margin:20px 10px 0px 0px;
}
`;

export const InvoiceInnersmallContainer = styled.div`
border-radius: 16px;
border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
padding: 20px;
margin:10px;
width:30%;

.clientDetails{
 display:flex;
 margin:20px 0px;
};

.align{
display:flex;
flex-direction:column;
margin:0px 20px;
};

.text{
font-size:13px;
  color: #005792;}
`;

export const InvoiceAddFormMainContainer = styled.div`
  width:70vw;
  @media screen and (max-width: 1150px) {
    width: 70vw;
  }

  .addRows{
  margin:0px 10px 10px 10px;
  font-size:13px;
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

  .spanSign{
   display:flex;
   flex-direction:column;
   margin:10px 0px;
  }

  .spanSign span{
  margin:10px 0px;
  }
`;

export const InvoiceRemittance = styled.div`
display:flex;
justify-content:space-between;
border-radius: 6px;
border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
padding: 10px;
margin:20px 10px; 

label{
  color: var(--greyscale-600, #687588);
    font-family: Nunito;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: 160%;
}

.length{
width:33%;
}

.spanElement{
display:block;
}

.applyMargin{
 margin:0px 5px;
 font-size:14px;
}
`

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
 color: #000;
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
`

export const AddRowContainer = styled.div`
.rowsAlign{
display:flex;
justify-content:space-between;
margin:10px;
padding:10px;
}

.alignButton{
  display:flex;
  justify-content:center;
  align-items:center;
  gap:10px;

  button{
    border:none;
  }
}

.rowItem{
display:flex;
flex-direction:column;

 label{
    background-color: ${(props) => props.theme.colors.blackColors.white3};
    color: ${(props) => props.theme.colors.grayColors.gray7};
    font-size: 12px;
    font-style:nounito;
    font-weight: 700;
    letter-spacing: 0.2px;
 }
  
  input{
    padding:5px;
    border-radius: 6px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  }
}

.errorSpan{
  font-size:12px;
  color:red;
  margin:5px 0px;
}
`
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
display:flex;
justify-content:flex-end;
gap:10px;
margin:20px 0px;
`
export const InvoiceDetails = styled.div`
border-radius: 6px;
border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
display:flex;
flex-direction:column;
padding:15px;
margin:10px;

.dateSet{
display:flex;
}

.applyMargin{
margin:0px 5px;
}

.applyMargin1{
margin:10px 0px;
font-size:15px;
}

.sub-invoicedetails{
display:flex;
justify-content:space-between;
margin-top:10px;
}

.fontSize{
font-Size: 15px;
font-weight:bold;
}

.fontColor{
 color: #005792;
}

.filterCalender {
    position: absolute;
    display: flex;
    border-radius: 16px;
    background: ${(props) => props.theme.colors.backgroundColors.primary};
    box-shadow: 0px 5px 40px 0px rgba(0, 0, 0, 0.1);
}
`


export const TextInput = styled.input`
    outline: none;
    border: none;
    border-radius: 5px;
    background-color: #f1f2f4;
    padding: 4px 3px;
    color: #000; 
    width:160px;
    margin:0px 10px;

`
export const TableHead = styled.thead``;
export const TableList = styled.table`
  border:0;
  margin:10px;
  width:98%;
  border-collapse:collapse;

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

export const TableBodyRow = styled.tr`
  height: 45px;
  border: 1px solid ${(props) => props.theme.colors.blackColors.white2};

  &:hover {
    border-top:2px solid #005792;
    border-color: #005792;
  }

  td {
    padding: 0 10px;
    font-size: 12px;
    vertical-align: middle;
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
`

export const TableRow = styled.tr`
  height: 35px;
  border-bottom: 1px solid  #005792;
    font-family: Nunito;

  td {
    font-size: 12px;
    vertical-align: middle;
  }
`;

export const Tablelist = styled.table`
.borderCollapse{
border-collapse:collapse;
margin:10px;
}
`;

export const TableBodyRows = styled.tr`
height: 30px;

  td {
    padding: 0 10px;
    font-size: 12px;
    vertical-align: middle;
  }
`
export const InvoicePaymentContainer = styled.div`
border-radius: 6px;
border: 1px solid  ${(props) => props.theme.colors.grayColors.grayscale300};
padding: 10px; 
margin:20px 10px;

.PayDet{
 font-size:12px;
 color:#005792;
 padding:5px 10px;
}
`

