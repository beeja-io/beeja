import { useState,useRef,useEffect,useMemo } from "react";

import { minDateOfFromCalendar } from "../../constants/Constants";

import { InvoiceAddFormMainContainer,
         InvoiceAddFormSubContainer,
         InvoicePaymentContainer,
         InvoiceRemittance,
         InvoiceAddressContainer,
         InvoiceButtonContainer,
         InvoiceDetails,
         TableList,
         TableHead ,
         TableBodyRow,
         TextInput,
         InvoiceCalculationContainer,
         TableRow,
         Tablelist,
         TableBodyRows,
         DatePicker,
         AddRowContainer,
         ValidationText
        } from "../../styles/InvoiceManagementStyles.style";


import Calendar from "../reusableComponents/Calendar.component";
import { formatDate } from "../../utils/dateFormatter";

import {toWords} from 'number-to-words';

import { Button } from '../../styles/CommonStyles.style';
import {CheckBoxOnSVG, CrossMarkSVG, MessageIconSVG } from '../../svgs/CommonSvgs.svs';
import { RightArrowSVG , EditWhitePenSVG , DeleteIconSVG , Plus , DownloadSVG,CalenderSVG} from "../../svgs/InvoiceSvgs.svg";
import CenterModal from "../reusableComponents/CenterModal.component";

type AddInvoiceFormProps ={
    handleClose:() => void ;
}

type formDataProps = {
    RemittanceNo:number,
    InvoiceNo:number,
    TaxID:number,
    fromDate:Date,
    toDate:Date,
    vat:number,
    dueRemarks:string,
    remarksNote:string
}

type rowProps = {
    contract:string,
    description:string,
    price:string;

}

export const AddInvoiceForm = (props:AddInvoiceFormProps ) => {

    const [data,setData] = useState([{contract:"zeies website",description:"Enter the details projects",price:"500"},
                                     {contract:"morris website",description:"Enter the details projects",price:"1500"},
                                     {contract:"zeies website",description:"Enter the details projects",price:"500"}
                                    ])

    const [formData,setFormData] = useState<formDataProps>({RemittanceNo:23478,InvoiceNo:67849,TaxID:642873,
                                                            fromDate:new Date(2024,2,4),toDate:new Date(2025,2,4),vat:75,
                                                            dueRemarks:"Please Transfer the due amount to the following bank within 14 days",
                                                            remarksNote:"Thank you so much for the great opportunity as always"});

    const [errors,setErrors] = useState({ contract : '' , description : '', price : '' });

    const [isRemittanceRefEditModeOn , setIsRemittanceRefEditModeOn] = useState(false);
    
    const handleIsRemittanceEditModeOn = () => {
        setIsRemittanceRefEditModeOn(!isRemittanceRefEditModeOn);
    }

    const [isInvoiceNoEditModeOn , setIsInvoiceNoEditModeOn] = useState(false);
    
    const handleIsInvoiceNoEditModeOn = () => {
        setIsInvoiceNoEditModeOn(!isInvoiceNoEditModeOn);
    }

    const [isTaxIdEditModeOn , setIsTaxIdEditModeOn] = useState(false);
    
    const handleIsTaxIdEditModeOn = () => {
        setIsTaxIdEditModeOn(!isTaxIdEditModeOn);
    }

    const [isInvoiceEditModeOn , setIsInvoiceEditModeOn] = useState(false);

    const handleIsInvoiceEditModeOn = () => {
        setIsInvoiceEditModeOn(!isInvoiceEditModeOn);
    }

    const [isAddRowsEditModeOn , setIsAddRowsEditModeOn] = useState(false);
    
    const handleIsAddRowsEditModeOn = () => {
        setIsAddRowsEditModeOn(!isAddRowsEditModeOn);
        setErrors({ contract : '' , description : '', price : '' });
    }

    const [isDueDaysEditModeOn , setIsDueDaysEditModeOn] = useState(false);
    
    const handleIsDueDaysEditModeOn = () => {
        setIsDueDaysEditModeOn(!isDueDaysEditModeOn)
    }

    const [isRemarksNoteEditModeOn , setIsRemarksNoteEditModeOn] = useState(false);
    
    const handleIsRemarksNoteEditModeOn = () => {
        setIsRemarksNoteEditModeOn(!isRemarksNoteEditModeOn)
    }

    const [showFromCalendar, setShowFromCalendar] = useState(false);

    const handleFromCalendarClick = () => {
        setShowFromCalendar(!showFromCalendar)
    }

    const [showToCalendar, setShowToCalendar] = useState(false);
    
    const handleToCalendarClick = () => {
        setShowToCalendar(!showToCalendar)
    }

    const [vatFlag , setVatFlag] = useState(false);

    const handleVatClick = () => {
        setVatFlag(!vatFlag);
    }
    const calendarFromRef = useRef<HTMLDivElement>(null);
    const calendarToRef = useRef<HTMLDivElement>(null);

    const handleClickOutside = (event: MouseEvent) => {
        if (
          calendarFromRef.current &&
          !calendarFromRef.current.contains(event.target as Node)
        ) {
          setShowFromCalendar(false);
        }
        if (
          calendarToRef.current &&
          !calendarToRef.current.contains(event.target as Node)
        ) {
          setShowToCalendar(false);
        }
      };
    
      useEffect(() => {
        document.addEventListener('click', handleClickOutside);
    
        return () => {
          document.removeEventListener('click', handleClickOutside);
        };
      }, []);

    const [fromDate, setFromDate] = useState<Date | null>();
    
    const [toDate, setToDate] = useState<Date | null>();

    const [maxToDate, setMaxToDate] = useState<Date | null>(new Date());

    const currentDate = useMemo(() => new Date(), []);
    
    useEffect(() => {
        if (fromDate && fromDate < currentDate) {
            setMaxToDate(currentDate);
        }
    }, [fromDate, currentDate]);
    
    const handleDateInput = (selectedDate: Date | null, isFrom: boolean) => {
        if (isFrom) {
          setFromDate(selectedDate);
          setShowFromCalendar(false);
        } else {
          setToDate(selectedDate);
          setShowToCalendar(false);
        }
    };


    const handleChange = (event:React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setFormData((prevState) => ({
            ...prevState,
            [name]: name === "vat" ? Number(value) || 0 :value,
          }));
    }

    const [rowData ,setRowData] = useState<rowProps>({contract : '' , description : '', price : ''});

    const handleChangeRows = (event:React.ChangeEvent<HTMLInputElement>) => {
        setErrors({ contract : '' , description : '', price : '' });
        const { name, value } = event.target;
        setRowData((prevState) => ({
            ...prevState,
            [name]: value,
          }));
    }

    const handleRowChanges = () => {
        if (!rowData.contract.trim()){
            setErrors((prevState) => ({...prevState,contract:"* Contract name should not be empty",description:"",price:""}));
            return;
        }
        if (!rowData.description.trim()){
            setErrors((prevState) => ({...prevState,contract:"",description:"* description should not be empty",price:""}));
            return;
        }
        if(!rowData.price.trim()){
            setErrors((prevState) => ({...prevState,contract:"",description:"",price:"* price should not be empty"}));
            return;
        }
          setData([...data, rowData]);
          setIsAddRowsEditModeOn(false);
          setRowData({contract:"",description:"",price:""});
          setErrors((prevState) => ({...prevState,contract:"",description:"",price:""}));
    }

    const handleDeleteList = (_event: React.MouseEvent<HTMLTableDataCellElement, MouseEvent> ,id : number) => {
        setData([...data.slice(0,id),...data.slice(id+1)]);
    }

    const subTotal = data.reduce((sum,item) => sum+ Number(item.price) , 0);

    const Total = subTotal + formData.vat ;

    const [confirmDeleteChanges,setConfirmDeleteChanges] = useState(false);

    const handleConfirmDeleteChanges = (e:React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        setConfirmDeleteChanges(true);
    }

    const [confirmSaveChanges,setConfirmSaveChanges] = useState(false);

    const handleConfirmSaveChanges = (e: { preventDefault: () => void; }) => {
        e.preventDefault();
        setConfirmSaveChanges(true);
        console.log(formData);
    }

    const CapitalizeWords = (str : string) => {
        return str
               .split(' ')
               .map((word) => word.charAt(0).toUpperCase()+word.slice(1))
               .join(' ')
    }

    return (
        <>
        <InvoiceAddFormMainContainer>
            <InvoiceButtonContainer>
                <Button width="150px" height="50px" padding='12px 12px' style={{ fontWeight: 'bold' }}>
                    <MessageIconSVG />
                    Send Invoice
                </Button>
                <Button width="150px" height="50px" padding="12px 12px" style={{ fontWeight: 'bold' }}>
                    <DownloadSVG />
                    Download
                </Button>
            </InvoiceButtonContainer>
            <InvoiceAddFormSubContainer onSubmit={handleConfirmSaveChanges}>
                <InvoiceAddressContainer>
                    <div className="adjusting">
                        <span className="applyStyle1">
                            Billing From 
                            <span className="applyStyle2">
                                TECHATCORE Gmbh
                            </span>
                        </span>
                        <span className="textFont">
                            Jayakeshava enclave , kakatiya hills , madhapur , telangana ,10119,india
                        </span>
                    </div>
                    <div className="arrowAdjust">
                        <RightArrowSVG />
                    </div>
                    <div className="adjusting">
                        <span className="applyStyle1">
                            Billing To
                            <span className="applyStyle2">
                                TECHATCORE Gmbh
                            </span>
                        </span>
                        <span className="textFont">
                            Jayakeshava enclave , kakatiya hills , madhapur , telangana ,10119,india
                        </span>
                    </div>
                </InvoiceAddressContainer>
                <InvoiceRemittance>
                    <div className="spanElement length">
                        <label>Remittance Ref</label>
                        {!isRemittanceRefEditModeOn ?(
                            <>
                                <span className="applyMargin"> {formData.RemittanceNo}</span>
                                <span onClick={handleIsRemittanceEditModeOn} ><EditWhitePenSVG /></span>
                            </>
                        ):(
                            <TextInput
                                type="text"
                                name="RemittanceNo"
                                value={formData.RemittanceNo}
                                onChange={handleChange}
                                required
                            />
                        )}
                    </div>

                    <div className="spanElement length">
                        <label>Invoice No</label>
                        {!isInvoiceNoEditModeOn ?(
                            <>
                            <span className="applyMargin">{formData.InvoiceNo} </span>
                            <span onClick={handleIsInvoiceNoEditModeOn} ><EditWhitePenSVG /></span>
                            </>
                        ):(
                            <TextInput 
                                type="text"
                                name="InvoiceNo"
                                value={formData.InvoiceNo}
                                onChange={handleChange}
                                required
                            />
                        )}
                    </div>
                    
                    <div className="spanElement">
                        <label>TaxID</label>
                        {!isTaxIdEditModeOn ?(
                            <>
                            <span className="applyMargin"> {formData.TaxID} </span>
                            <span onClick={handleIsTaxIdEditModeOn} ><EditWhitePenSVG /></span>
                            </>
                        ):(
                            <TextInput 
                                type="text"
                                name="TaxID"
                                value={formData.TaxID}
                                onChange={handleChange}
                                required
                            />
                        )}
                    </div>
                </InvoiceRemittance>
                <InvoiceDetails>
                    <div className="spanElement fontSize">
                        <label>Invoice</label>
                        {!isInvoiceEditModeOn ?(
                            <>
                                <span className="applyMargin fontColor">{formData.InvoiceNo} </span>
                                <span onClick={handleIsInvoiceEditModeOn} ><EditWhitePenSVG /></span>
                            </>
                        ):(
                            <TextInput
                                className="fontSize fontColor"
                                type="text"
                                name="InvoiceNo"
                                value={formData.InvoiceNo}
                                onChange={handleChange}
                                required
                            />
                        )}
                    </div>
                    <span className="applyMargin1 ">2024P0N100010 - Central Monitering System</span>
                    <div className="dateSet">
                        <span style={{fontSize:"13px"}}>Invoice Period</span>&nbsp;
                        <div ref={calendarFromRef} style={{ position: 'relative' }}>
                            <DatePicker onClick={handleFromCalendarClick}>
                                <span className="dateName">
                                    <span className="calenderIcon">
                                        <CalenderSVG />
                                    </span>&nbsp;
                                    <span className="dateChild">
                                        {fromDate
                                            ? `${formatDate(fromDate.toString())}`
                                            : `${formatDate(formData.fromDate.toString())}`}
                                    </span>
                                </span>
                            </DatePicker>
                    
                            {showFromCalendar && (
                                <div className="filterCalender">
                                    <Calendar
                                      title="FROM_DATE"
                                      minDate={minDateOfFromCalendar}
                                      maxDate={currentDate}
                                      handleDateInput={(selectedDate) => {
                                        if(selectedDate instanceof Date){
                                        handleDateInput(selectedDate,true)
                                        setFormData((prevState) => ({
                                            ...prevState,
                                            fromDate: selectedDate,
                                          }));
                                      }}
                                    }
                                      selectedDate={fromDate ? fromDate : new Date()}
                                      handleCalenderChange={function (): void {
                                        throw new Error('Function not implemented.');
                                      }}
                                    />
                                </div>
                            )}
                        </div>
                        <span style={{fontSize:"12px"}}> To </span>&nbsp;
                        <div ref={calendarToRef} style={{ position: 'relative' }}>
                            <DatePicker onClick={handleToCalendarClick} >
                                <span className="dateName">
                                    <span className="calenderIcon">
                                       <CalenderSVG/>
                                    </span>&nbsp;
                                    <span className="dateChild">
                                        {toDate
                                            ? `${formatDate(toDate.toString())}`
                                            : `${formatDate(formData.toDate.toString())}`}
                                    </span>
                                </span>
                            </DatePicker>
                            {showToCalendar && (
                                <div className="filterCalender">
                                    <Calendar
                                      title="TO_DATE"
                                      minDate={fromDate}
                                      maxDate={maxToDate}
                                      handleDateInput={(selectedDate) => {
                                        if(selectedDate instanceof Date){
                                        handleDateInput(selectedDate,false)
                                        setFormData((prevState) => ({
                                            ...prevState,
                                            toDate: selectedDate,
                                          }));
                                      }}
                                    }
                                      selectedDate={toDate ? toDate : new Date()}
                                      handleCalenderChange={() => {}}
                                    />
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="sub-invoicedetails">
                        <div>
                            <span className="remarks">Remarks</span>
                            {!isRemarksNoteEditModeOn ?
                                (<span className="remarksNote"><span>"</span> {formData.remarksNote} <span>"</span>
                                <span onClick={handleIsRemarksNoteEditModeOn} style={{marginLeft:"5px"}}>
                                <EditWhitePenSVG />
                                </span>
                            </span>
                            ) : (
                                <TextInput className="remarksNote" style={{width:"70%"}} name="remarksNote" value={formData.remarksNote} onChange={handleChange} required />
                            )}
                        </div>
                        <div>
                            <span className="remarks applyMargin">Berlin,September 10 2024</span>
                            <span><EditWhitePenSVG /></span>
                        </div>
                    </div>
                </InvoiceDetails>
                <div>
                    <TableList>
                        <TableHead>
                            <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                                <th>Sno</th>
                                <th>Contract</th>
                                <th>Description</th>
                                <th>{`Price In {$} `}</th>
                            </tr>
                        </TableHead>
                        {data.map((project,index) => (
                            <TableBodyRow key={index}>
                                <td>{index+1}</td>
                                <td>{project.contract}</td>
                                <td>{project.description}</td>
                                <td>{project.price}</td>
                                <td onClick={(e) => handleDeleteList(e,index)}><DeleteIconSVG /></td>
                            </TableBodyRow>
                            ))   
                        }                                  
                    </TableList>
                </div>
                <AddRowContainer>
                {!isAddRowsEditModeOn ?
                    (
                      <span className="addRows" style={{cursor: "pointer"}} onClick={handleIsAddRowsEditModeOn}>
                        <Plus /> Add Row
                      </span>
                    ) : (
                        <div className="rowsAlign">
                            <div className="rowItem">
                                <label>Contract<ValidationText className="star">*</ValidationText></label>
                                <input name="contract"  onChange={handleChangeRows} />
                                {errors.contract && <span className="errorSpan">{errors.contract}</span>}
                            </div>
                            <div className="rowItem">
                                <label>Description<ValidationText className="star">*</ValidationText></label>
                                <input name="description" onChange={handleChangeRows} />
                                {errors.description && <span className="errorSpan">{errors.description}</span>}
                            </div>
                            <div className="rowItem">
                                <label>Price<ValidationText className="star">*</ValidationText></label>
                                <input name="price" 
                                       onChange={handleChangeRows} 
                                       onKeyDown={(event) => {
                                        const allowedCharacters = /^[0-9]+$/;
                                        if (
                                            !allowedCharacters.test(event.key) &&
                                            event.key !== 'ArrowLeft' &&
                                            event.key !== 'ArrowRight' &&
                                            event.key !== 'Backspace'
                                        ) {
                                            event.preventDefault();
                                        }
                                        if (event.key === 'e') {
                                            event.preventDefault();
                                        }
                                        }}
                                 />
                                {errors.price && <span className="errorSpan">{errors.price}</span>}
                            </div>
                            <div className="alignButton">
                                <button type="button" onClick={handleRowChanges}> <CheckBoxOnSVG /> </button>
                                <button onClick={() => setIsAddRowsEditModeOn(false)}><CrossMarkSVG /></button>
                            </div>
                        </div>
                    )}
                </AddRowContainer>
                <InvoiceCalculationContainer>
                    <Tablelist>
                        <div className="borderCollapse">
                            <TableRow>
                                <td style={{paddingRight:"20px"}}>Sub Total</td>
                                <td>($){subTotal}</td>             
                            </TableRow>
                            <TableRow>
                                <td>Vat</td>
                                {!vatFlag ?
                                <td>($){formData.vat} <span onClick={handleVatClick}> <EditWhitePenSVG /> </span></td> :
                                <td>($)<TextInput style={{width:"40px"}} name="vat" value={formData.vat} onChange={handleChange} required /></td>   
                                }        
                            </TableRow>
                            <TableRow>
                                <td>Total</td>
                                <td>($){Total}</td>             
                            </TableRow>  
                        </div>       
                    </Tablelist>

                    <Tablelist>
                        <TableRow>
                            <td>Amount In Words(Dollars):</td>
                            <td style={{fontWeight:"bold"}}>{CapitalizeWords(toWords(Total))} Only/-</td>             
                        </TableRow>       
                    </Tablelist>
                </InvoiceCalculationContainer>
                <div style={{margin:"0px 10px"}}>
                    <span className="remarks">NOTE:</span>
                    {isDueDaysEditModeOn ? (<TextInput  className="remarksNote" style={{width:"30%"}} value={formData.dueRemarks} required/>
                       ) : (
                       <span className="remarksNote">{formData.dueRemarks} <span onClick={handleIsDueDaysEditModeOn}> <EditWhitePenSVG /></span></span>
                    )}
                </div>
                <InvoicePaymentContainer>
                    <span className="PayDet">Payment Details</span>
                    <div>
                        <Tablelist>
                            <TableBodyRows>
                                <td>Name</td>
                                <td>TECHATCORE PRIVATE LIMITED</td>
                            </TableBodyRows> 
                            <TableBodyRows>
                                <td> Bank Name</td>
                                <td>ICICI</td>
                            </TableBodyRows>
                            <TableBodyRows>
                                <td>Account Number</td>
                                <td>DE63 1001 1001 2628 1900 12</td>
                            </TableBodyRows>
                            <TableBodyRows>
                                <td>IFSC</td>
                                <td>NTSBDEB1XXX</td>
                            </TableBodyRows>                    
                        </Tablelist>
                    </div>
                </InvoicePaymentContainer>
                <div style={{margin:"0px 10px"}}>
                    <span className="remarks">
                        Remarks 
                    </span>
                    {!isRemarksNoteEditModeOn ?
                        (<span className="remarksNote"><span>"</span> {formData.remarksNote} <span>"</span>
                            <span onClick={handleIsRemarksNoteEditModeOn} style={{marginLeft:"5px"}}>
                               <EditWhitePenSVG />
                            </span>
                         </span>
                        ) : (
                            <TextInput className="remarksNote" style={{width:"30%"}} name="remarksNote" value={formData.remarksNote} onChange={handleChange} required />
                    )}
                    <div className="spanSign">
                        <span className="remarksNote"> Best Regards </span>
                        <span className="remarks"> Tech.at.core </span>
                    </div>
                </div>
                <div className="formButtons">
                    <Button type="button" onClick={handleConfirmDeleteChanges}>CANCEL</Button>
                    <Button className="submit" type="submit">SAVE</Button>
                </div>
            </InvoiceAddFormSubContainer>
        </InvoiceAddFormMainContainer>
            {confirmDeleteChanges && (
                <span style={{ cursor: 'default' }}>
                <CenterModal
                    handleModalClose={() => setConfirmDeleteChanges(false)}
                    handleModalSubmit={() => props.handleClose()}
                    modalHeading="Invoice Changes"
                    modalContent={`Are you sure to discard changes`}
                />
                </span>
            )}
            {confirmSaveChanges && (
                <span style={{ cursor: 'default' }}>
                <CenterModal
                    handleModalClose={() => setConfirmSaveChanges(false)}
                    handleModalSubmit={() => props.handleClose()}
                    modalHeading="Save Changes"
                    modalContent={`Are you sure to save changes`}
                />
                </span>
            )}
        </>
    )
}