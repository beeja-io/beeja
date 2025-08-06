import { useContext, useEffect, useMemo, useRef, useState } from 'react';
import { minDateOfFromCalendar } from '../../constants/Constants';
import {
  AddRowContainer,
  DatePicker,
  InvoiceAddFormMainContainer,
  InvoiceAddFormSubContainer,
  InvoiceAddressContainer,
  InvoiceButtonContainer,
  InvoiceCalculationContainer,
  InvoiceDetails,
  InvoicePaymentContainer,
  InvoiceRemittance,
  TableBodyRow,
  TableBodyRows,
  TableHead,
  TableList,
  TableRow,
  Tablelist,
  TextInput,
  ValidationText,
} from '../../styles/InvoiceManagementStyles.style.tsx';
import { formatDate } from '../../utils/dateFormatter';
import Calendar from '../reusableComponents/Calendar.component';
import { toWords } from 'number-to-words';
import { ApplicationContext } from '../../context/ApplicationContext';
import {
  AddInvoiceFormProps,
  FormDataProps,
  RowProps,
} from '../../entities/InvoiceGenerationEntity';
import { Address } from '../../entities/OrganizationEntity';
import { createInvoice } from '../../service/axiosInstance';
import { Button } from '../../styles/CommonStyles.style';
import {
  CheckBoxOnSVG,
  CrossMarkSVG,
  MessageIconSVG,
} from '../../svgs/CommonSvgs.svs';
import {
  CalenderSVG,
  DeleteIconSVG,
  DownloadSVG,
  EditWhitePenSVG,
  Plus,
  RightArrowSVG,
} from '../../svgs/InvoiceSvgs.svg';
import CenterModal from '../reusableComponents/CenterModal.component';
import { toast } from 'sonner';

export const AddInvoiceForm = (props: AddInvoiceFormProps) => {
  const generateDueRemarks = (
    endDate: Date | undefined,
    dueDays: number | undefined
  ): string => {
    const genericMessage =
      'Please Transfer the due amount to the following bank.';
    const currentDate = new Date();
    if (!endDate || isNaN(endDate.getTime())) {
      return genericMessage;
    }
    if (dueDays === undefined) {
      if (endDate < currentDate) {
        return 'The contract has ended.';
      }
      return genericMessage;
    }
    const dueDate = new Date(endDate);
    dueDate.setDate(endDate.getDate() + dueDays);
    if (dueDate < currentDate) {
      return 'The contract has ended.';
    }
    return `Please Transfer the due amount to the following bank within ${dueDays} days`;
  };
  const { organizationDetails } = useContext(ApplicationContext);
  const [data, setData] = useState<RowProps[]>([]);
  const getDefaultAddress = (): Address => ({
    addressOne: '',
    addressTwo: '',
    city: '',
    state: '',
    country: '',
    pinCode: 0,
  });

  const getCurrencySymbol = (currencyType?: string): string => {
    const currencySymbols: Record<string, string> = {
      DOLLER: '$',
      INDIAN_RUPEE: '₹',
      EURO: '€',
    };
    return currencyType && currencySymbols[currencyType]
      ? currencySymbols[currencyType]
      : '';
  };
  const [formData, setFormData] = useState<FormDataProps>({
    RemittanceNo: props.remittanceReferenceNumber || '',
    InvoiceNo: props.invoiceId || '',
    tax: 18,
    taxId: organizationDetails?.taxId || '',
    organization: organizationDetails?.name || '',
    organizationId: props.organizationId || '',
    projectId: props.projectId || '',
    fromDate: props.startDate
      ? new Date(props.startDate)
      : new Date(2024, 2, 4),
    toDate: props.endDate ? new Date(props.endDate) : new Date(2025, 2, 4),
    contractName: props.contractTitle || '',
    contractId: props.contractId,
    clientName: props.clientName || '',
    clientId: props.clientId || '',
    status: props.status || '',
    currencyType: organizationDetails?.currencyType || '',
    primaryAddress: organizationDetails?.address
      ? {
          addressOne: organizationDetails.address.addressOne || '',
          addressTwo: organizationDetails.address.addressTwo,
          city: organizationDetails.address.city || '',
          state: organizationDetails.address.state || '',
          country: organizationDetails.address.country || '',
          pinCode: organizationDetails.address.pinCode || 0,
        }
      : getDefaultAddress(),
    billingAddress: props.billingAddress || {
      street: '',
      city: '',
      state: '',
      postalCode: '',
      country: '',
    },
    bankDetails: {
      accountName: organizationDetails?.bankDetails?.accountName || '',
      bankName: organizationDetails?.bankDetails?.bankName || '',
      accountNumber: organizationDetails?.bankDetails?.accountNumber || '',
      ifscNumber: organizationDetails?.bankDetails?.ifscNumber || '',
    },
    dueRemarks: generateDueRemarks(
      props.endDate ? new Date(props.endDate) : undefined,
      props.dueDays
    ),
    remarksNote: 'Thank you so much for the great opportunity as always',
  });
  const [errors, setErrors] = useState({
    contract: '',
    description: '',
    price: '',
  });
  useEffect(() => {
    setFormData((prevData) => ({
      ...prevData,
      RemittanceNo: props.remittanceReferenceNumber || '',
      InvoiceNo: props.invoiceId || '',
      fromDate: props.startDate ? new Date(props.startDate) : prevData.fromDate,
      toDate: props.endDate ? new Date(props.endDate) : prevData.toDate,
      status: props.status || '',
      clientId: props.clientId || '',
      currencyType: organizationDetails?.currencyType || '',
      primaryAddress: organizationDetails?.address
        ? {
            addressOne: organizationDetails.address.addressOne || '',
            addressTwo: organizationDetails.address.addressTwo,
            city: organizationDetails.address.city || '',
            state: organizationDetails.address.state || '',
            country: organizationDetails.address.country || '',
            pinCode: organizationDetails.address.pinCode || 0,
          }
        : getDefaultAddress(),
      billingAddress: props.billingAddress || {
        street: '',
        city: '',
        state: '',
        postalCode: '',
        country: '',
      },
      clientName: props.clientName || '',
      dueRemarks: generateDueRemarks(
        props.endDate ? new Date(props.endDate) : undefined,
        props.dueDays
      ),
      taxId: organizationDetails?.taxId || prevData.taxId,
      organization: organizationDetails?.name || '',
      paymentDetails: {
        accountName:
          organizationDetails?.bankDetails?.accountName ||
          'TECHATCORE PRIVATE LIMITED',
        bankName: organizationDetails?.bankDetails?.bankName || 'ICICI',
        accountNumber:
          organizationDetails?.bankDetails?.accountNumber ||
          'DE63 1001 1001 2628 1900 12',
        ifscNumber:
          organizationDetails?.bankDetails?.ifscNumber || 'NTSBDEB1XXX',
      },
    }));
  }, [
    props.invoiceId,
    props.remittanceReferenceNumber,
    props.startDate,
    props.endDate,
    props.billingAddress,
    props.clientName,
    props.dueDays,
    props.clientId,
    props.status,
    organizationDetails?.taxId,
    organizationDetails?.name,
    organizationDetails?.address?.addressOne,
    organizationDetails?.address?.addressTwo,
    organizationDetails?.address?.city,
    organizationDetails?.address?.state,
    organizationDetails?.address?.pinCode,
    organizationDetails?.address?.country,
    organizationDetails?.bankDetails?.accountName,
    organizationDetails?.bankDetails?.bankName,
    organizationDetails?.bankDetails?.accountNumber,
    organizationDetails?.bankDetails?.ifscNumber,
    organizationDetails?.currencyType,
  ]);

  const [isRemittanceRefEditModeOn, setIsRemittanceRefEditModeOn] =
    useState(false);
  const handleIsRemittanceEditModeOn = () => {
    setIsRemittanceRefEditModeOn(!isRemittanceRefEditModeOn);
  };
  const [isTaxIdEditModeOn, setIsTaxIdEditModeOn] = useState(false);
  const handleIsTaxIdEditModeOn = () => {
    setIsTaxIdEditModeOn(!isTaxIdEditModeOn);
  };
  const [isInvoiceEditModeOn, setIsInvoiceEditModeOn] = useState(false);
  const handleIsInvoiceEditModeOn = () => {
    setIsInvoiceEditModeOn(!isInvoiceEditModeOn);
  };
  const [isAddRowsEditModeOn, setIsAddRowsEditModeOn] = useState(false);
  const handleIsAddRowsEditModeOn = () => {
    setIsAddRowsEditModeOn(!isAddRowsEditModeOn);
    setErrors({ contract: '', description: '', price: '' });
  };
  const [isDueDaysEditModeOn, setIsDueDaysEditModeOn] = useState(false);
  const handleIsDueDaysEditModeOn = () => {
    setIsDueDaysEditModeOn(!isDueDaysEditModeOn);
  };
  const [isRemarksNoteEditModeOn, setIsRemarksNoteEditModeOn] = useState(false);
  const handleIsRemarksNoteEditModeOn = () => {
    setIsRemarksNoteEditModeOn(!isRemarksNoteEditModeOn);
  };
  const [showFromCalendar, setShowFromCalendar] = useState(false);
  const handleFromCalendarClick = () => {
    setShowFromCalendar(!showFromCalendar);
  };
  const [showToCalendar, setShowToCalendar] = useState(false);
  const handleToCalendarClick = () => {
    setShowToCalendar(!showToCalendar);
  };
  const [taxFlag, setTaxFlag] = useState(false);
  const handleTaxClick = () => {
    setTaxFlag(!taxFlag);
  };
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
  const [fromDate, setFromDate] = useState<Date | null>(
    props.startDate ? new Date(props.startDate) : null
  );
  const [toDate, setToDate] = useState<Date | null>(
    props.endDate ? new Date(props.endDate) : null
  );
  const currentDate = useMemo(() => new Date(), []);
  useEffect(() => {
    if (props.startDate) {
      setFromDate(new Date(props.startDate));
    }
    if (props.endDate) {
      setToDate(new Date(props.endDate));
    }
  }, [props.startDate, props.endDate]);
  const handleDateInput = (selectedDate: Date | null, isFrom: boolean) => {
    if (isFrom) {
      setFromDate(selectedDate);
      setShowFromCalendar(false);
    } else {
      setToDate(selectedDate);
      setShowToCalendar(false);
    }
  };
  const handleTaxIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = event.target;
    setFormData((prevState) => ({
      ...prevState,
      taxId: value,
    }));
  };
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: name === 'tax' ? Number(value) || 0 : value,
    }));
  };
  const [rowData, setRowData] = useState<RowProps>({
    contract: '',
    description: '',
    price: '',
  });
  const handleChangeRows = (event: React.ChangeEvent<HTMLInputElement>) => {
    setErrors({ contract: '', description: '', price: '' });
    const { name, value } = event.target;
    setRowData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };
  const handleRowChanges = () => {
    if (!rowData.contract.trim()) {
      setErrors((prevState) => ({
        ...prevState,
        contract: '* Contract name should not be empty',
        description: '',
        price: '',
      }));
      return;
    }
    if (!rowData.price.trim()) {
      setErrors((prevState) => ({
        ...prevState,
        contract: '',
        description: '',
        price: '* price should not be empty',
      }));
      return;
    }
    setData([...data, rowData]);
    setIsAddRowsEditModeOn(false);
    setRowData({ contract: '', description: '', price: '' });
    setErrors((prevState) => ({
      ...prevState,
      contract: '',
      description: '',
      price: '',
    }));
  };
  const handleDeleteList = (
    _event: React.MouseEvent<HTMLTableDataCellElement, MouseEvent>,
    id: number
  ) => {
    setData([...data.slice(0, id), ...data.slice(id + 1)]);
  };
  const subTotal = data.reduce((sum, item) => sum + Number(item.price), 0);
  const Total = subTotal + formData.tax;
  const [confirmDeleteChanges, setConfirmDeleteChanges] = useState(false);
  const handleConfirmDeleteChanges = (
    e: React.MouseEvent<HTMLButtonElement>
  ) => {
    e.preventDefault();
    setConfirmDeleteChanges(true);
  };

  const [confirmSaveChanges, setConfirmSaveChanges] = useState(false);

  const handleConfirmSaveChanges = async (e: {
    preventDefault: () => void;
  }) => {
    e.preventDefault();
    try {
      if (
        !formData.contractId ||
        !formData.clientId ||
        !Total ||
        !formData.fromDate ||
        !formData.toDate
      ) {
        return;
      }
      const invoiceRequest: InvoiceRequest = {
        contractId: formData.contractId,
        billingDate: formData.fromDate.toISOString(),
        dueDate: formData.toDate.toISOString(),
        amount: Total,
        currencyType: formData.currencyType,
        notes: [formData.remarksNote || '', formData.dueRemarks || ''].filter(
          Boolean
        ),
        tasks: data.map((item) => ({
          name: item.contract,
          description: item.description,
          amount: Number(item.price),
        })),
        clientId: formData.clientId,
        projectId: formData.projectId || null,
        remittanceRef: formData.RemittanceNo || null,
        vat: formData.tax || 18,
        daysLeftForPayment: formData.dueRemarks.match(/\d+/)?.[0] || '30',
        invoicePeriod: {
          from: formData.fromDate.toISOString(),
          to: formData.toDate.toISOString(),
        },
      };

      await createInvoice(invoiceRequest);

      setConfirmSaveChanges(true);
    } catch (error: any) {
      toast.error(
        error?.response?.data?.message ||
          'Failed to create invoice. Please try again.'
      );
    }
  };

  const CapitalizeWords = (str: string) => {
    return str
      .split(' ')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };
  const formatCurrentDate = (date: Date) => {
    const options: Intl.DateTimeFormatOptions = {
      month: 'long',
      day: 'numeric',
      year: 'numeric',
    };
    const formatted = new Intl.DateTimeFormat('en-US', options).format(date);
    return `${formatted}`;
  };
  return (
    <>
      <InvoiceAddFormMainContainer>
        <InvoiceButtonContainer>
          <Button
            width="150px"
            height="50px"
            padding="12px 12px"
            style={{ fontWeight: 'bold' }}
          >
            <MessageIconSVG />
            Send Invoice
          </Button>
          <Button
            width="150px"
            height="50px"
            padding="12px 12px"
            style={{ fontWeight: 'bold' }}
          >
            <DownloadSVG />
            Download
          </Button>
        </InvoiceButtonContainer>
        <InvoiceAddFormSubContainer onSubmit={handleConfirmSaveChanges}>
          <InvoiceAddressContainer>
            <div className="adjusting">
              <span className="applyStyle1">
                Billing From
                <span className="applyStyle2">{organizationDetails?.name}</span>
              </span>
              <span className="textFont">
                {organizationDetails?.address?.addressOne}
                <br />
                {organizationDetails?.address?.city}-
                {organizationDetails?.address?.pinCode}
                <br />
                {organizationDetails?.address?.state},
                {organizationDetails?.address?.country}.
              </span>
            </div>
            <div className="arrowAdjust">
              <RightArrowSVG />
            </div>
            <div className="adjusting">
              <span className="applyStyle1">
                Billing To
                <span className="applyStyle2">{formData.clientName}</span>
              </span>
              <span className="textFont">
                {formData.billingAddress?.street}
                <br />
                {formData.billingAddress?.city}-
                {formData.billingAddress?.postalCode}
                <br />
                {formData.billingAddress?.state},
                {formData.billingAddress?.country}.
              </span>
            </div>
          </InvoiceAddressContainer>
          <InvoiceRemittance>
            <div className="spanElement length">
              <label>Remittance Ref</label>
              {!isRemittanceRefEditModeOn ? (
                <>
                  <span className="applyMargin"> {formData.RemittanceNo}</span>
                  <span onClick={handleIsRemittanceEditModeOn}>
                    <EditWhitePenSVG />
                  </span>
                </>
              ) : (
                <TextInput
                  type="text"
                  name="RemittanceNo"
                  value={formData.RemittanceNo}
                  onChange={handleChange}
                  required
                />
              )}
            </div>
            <div className="spanElement">
              <label>TaxID</label>
              {!isTaxIdEditModeOn ? (
                <>
                  <span className="applyMargin">
                    {' '}
                    {organizationDetails?.taxId}{' '}
                  </span>
                  <span onClick={handleIsTaxIdEditModeOn}>
                    <EditWhitePenSVG />
                  </span>
                </>
              ) : (
                <TextInput
                  type="text"
                  name="TaxID"
                  value={formData.taxId}
                  onChange={handleTaxIdChange}
                  required
                />
              )}
            </div>
          </InvoiceRemittance>
          <InvoiceDetails>
            <div className="spanElement fontSize">
              <label>Invoice</label>
              {!isInvoiceEditModeOn ? (
                <>
                  <span className="applyMargin fontColor">
                    {formData.InvoiceNo}{' '}
                  </span>
                  <span onClick={handleIsInvoiceEditModeOn}>
                    <EditWhitePenSVG />
                  </span>
                </>
              ) : (
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
            <span className="applyMargin1 ">
              {formData.contractId}- {formData.contractName}
            </span>
            <div className="dateSet">
              <span style={{ fontSize: '13px' }}>Invoice Period</span>&nbsp;
              <div ref={calendarFromRef} style={{ position: 'relative' }}>
                <DatePicker onClick={handleFromCalendarClick}>
                  <span className="dateName">
                    <span className="calenderIcon">
                      <CalenderSVG />
                    </span>
                    &nbsp;
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
                        if (selectedDate instanceof Date) {
                          handleDateInput(selectedDate, true);
                          setFormData((prevState) => ({
                            ...prevState,
                            fromDate: selectedDate,
                          }));
                        }
                      }}
                      selectedDate={fromDate ? fromDate : new Date()}
                      handleCalenderChange={function (): void {}}
                    />
                  </div>
                )}
              </div>
              <span style={{ fontSize: '12px' }}> To </span>&nbsp;
              <div ref={calendarToRef} style={{ position: 'relative' }}>
                <DatePicker onClick={handleToCalendarClick}>
                  <span className="dateName">
                    <span className="calenderIcon">
                      <CalenderSVG />
                    </span>
                    &nbsp;
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
                      handleDateInput={(selectedDate) => {
                        if (selectedDate instanceof Date) {
                          handleDateInput(selectedDate, false);
                          setFormData((prevState) => ({
                            ...prevState,
                            toDate: selectedDate,
                          }));
                        }
                      }}
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
                {!isRemarksNoteEditModeOn ? (
                  <span className="remarksNote">
                    <span>"</span> {formData.remarksNote} <span>"</span>
                    <span
                      onClick={handleIsRemarksNoteEditModeOn}
                      style={{ marginLeft: '5px' }}
                    >
                      <EditWhitePenSVG />
                    </span>
                  </span>
                ) : (
                  <TextInput
                    className="remarksNote"
                    style={{ width: '70%' }}
                    name="remarksNote"
                    value={formData.remarksNote}
                    onChange={handleChange}
                    required
                  />
                )}
              </div>
              <div>
                <span className="remarks applyMargin">
                  {organizationDetails?.address?.state},{' '}
                  {formatCurrentDate(currentDate)}
                </span>
                <span>
                  <EditWhitePenSVG />
                </span>
              </div>
            </div>
          </InvoiceDetails>
          <div>
            <TableList>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th>Sno</th>
                  <th>
                    <label
                      style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '4px',
                      }}
                    >
                      <ValidationText>*</ValidationText> Task
                    </label>
                  </th>
                  <th>Description</th>
                  <th>
                    <label
                      style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '4px',
                      }}
                    >
                      <ValidationText>*</ValidationText>{' '}
                      {'Price In ' +
                        getCurrencySymbol(organizationDetails?.currencyType)}
                    </label>
                  </th>
                </tr>
              </TableHead>
              {data.map((project, index) => (
                <TableBodyRow key={index}>
                  <td>{index + 1}</td>
                  <td>{project.contract}</td>
                  <td>{project.description}</td>
                  <td>{project.price}</td>
                  <td onClick={(e) => handleDeleteList(e, index)}>
                    <DeleteIconSVG />
                  </td>
                </TableBodyRow>
              ))}
            </TableList>
          </div>
          <AddRowContainer>
            {!isAddRowsEditModeOn ? (
              <span
                className="addRows"
                style={{ cursor: 'pointer' }}
                onClick={handleIsAddRowsEditModeOn}
              >
                <Plus /> Add Row
              </span>
            ) : (
              <div className="rowsAlign">
                <div className="rowItem">
                  <input
                    name="serialNo"
                    value={data.length + 1}
                    readOnly
                    style={{
                      backgroundColor: '#f0f0f0',
                      cursor: 'not-allowed',
                      width: '60px',
                    }}
                  />
                </div>
                <div className="rowItem">
                  <input name="contract" onChange={handleChangeRows} />
                  {errors.contract && (
                    <span className="errorSpan">{errors.contract}</span>
                  )}
                </div>
                <div className="rowItem">
                  <input name="description" onChange={handleChangeRows} />
                </div>
                <div className="rowItem">
                  <input
                    name="price"
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
                  {errors.price && (
                    <span className="errorSpan">{errors.price}</span>
                  )}
                </div>
                <div className="alignButton">
                  <button type="button" onClick={handleRowChanges}>
                    {' '}
                    <CheckBoxOnSVG />{' '}
                  </button>
                  <button onClick={() => setIsAddRowsEditModeOn(false)}>
                    <CrossMarkSVG />
                  </button>
                </div>
              </div>
            )}
          </AddRowContainer>
          <InvoiceCalculationContainer>
            <Tablelist>
              <div className="borderCollapse">
                <TableRow>
                  <td style={{ paddingRight: '20px' }}>Sub Total</td>
                  <td>{`${getCurrencySymbol(organizationDetails?.currencyType)} ${subTotal}`}</td>
                </TableRow>
                <TableRow>
                  <td>Tax</td>
                  {!taxFlag ? (
                    <td>
                      {`${getCurrencySymbol(organizationDetails?.currencyType)} ${formData.tax}`}
                      <span onClick={handleTaxClick}>
                        {' '}
                        <EditWhitePenSVG />{' '}
                      </span>
                    </td>
                  ) : (
                    <td>
                      ($)
                      <TextInput
                        style={{ width: '40px' }}
                        name="tax"
                        value={formData.tax}
                        onChange={handleChange}
                        required
                      />
                    </td>
                  )}
                </TableRow>
                <TableRow>
                  <td>Total</td>
                  <td>
                    ({getCurrencySymbol(organizationDetails?.currencyType)}){' '}
                    {Total}
                  </td>
                </TableRow>
              </div>
            </Tablelist>

            <Tablelist>
              <TableRow>
                <td>Amount In Words({organizationDetails?.currencyType}):</td>
                <td style={{ fontWeight: 'bold' }}>
                  {CapitalizeWords(toWords(Total))} Only/-
                </td>
              </TableRow>
            </Tablelist>
          </InvoiceCalculationContainer>
          <div style={{ margin: '0px 10px' }}>
            <span className="remarks">NOTE:</span>
            {isDueDaysEditModeOn ? (
              <TextInput
                className="remarksNote"
                style={{ width: '30%' }}
                value={formData.dueRemarks}
                required
              />
            ) : (
              <span className="remarksNote">
                {formData.dueRemarks}{' '}
                <span onClick={handleIsDueDaysEditModeOn}>
                  {' '}
                  <EditWhitePenSVG />
                </span>
              </span>
            )}
          </div>
          <InvoicePaymentContainer>
            <span className="PayDet">Payment Details</span>
            <div>
              <Tablelist>
                <TableBodyRows>
                  <td>Name :</td>
                  <td>{organizationDetails?.bankDetails?.accountName}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td> Bank Name :</td>
                  <td>{organizationDetails?.bankDetails?.bankName}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td>Account Number :</td>
                  <td>{organizationDetails?.bankDetails?.accountNumber}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td>IFSC :</td>
                  <td>{organizationDetails?.bankDetails?.ifscNumber}</td>
                </TableBodyRows>
              </Tablelist>
            </div>
          </InvoicePaymentContainer>
          <div style={{ margin: '0px 10px' }}>
            <span className="remarks">Remarks</span>
            {!isRemarksNoteEditModeOn ? (
              <span className="remarksNote">
                <span>"</span> {formData.remarksNote} <span>"</span>
                <span
                  onClick={handleIsRemarksNoteEditModeOn}
                  style={{ marginLeft: '5px' }}
                >
                  <EditWhitePenSVG />
                </span>
              </span>
            ) : (
              <TextInput
                className="remarksNote"
                style={{ width: '30%' }}
                name="remarksNote"
                value={formData.remarksNote}
                onChange={handleChange}
                required
              />
            )}
            <div className="spanSign">
              <span className="remarksNote"> Best Regards </span>
              <span className="remarks"> {organizationDetails?.name} </span>
            </div>
          </div>
          <div className="formButtons">
            <Button type="button" onClick={handleConfirmDeleteChanges}>
              CANCEL
            </Button>
            <Button className="submit" type="submit">
              SAVE
            </Button>
          </div>
        </InvoiceAddFormSubContainer>
      </InvoiceAddFormMainContainer>
      {confirmDeleteChanges && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalClose={() => setConfirmDeleteChanges(false)}
            handleModalSubmit={() => props.handleClose()}
            modalHeading="Invoice Changes"
            modalContent={'Are you sure to discard changes'}
          />
        </span>
      )}
      {confirmSaveChanges && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalClose={() => setConfirmSaveChanges(false)}
            handleModalSubmit={() => props.handleClose()}
            modalHeading="Save Changes"
            modalContent={'Are you sure to save changes'}
          />
        </span>
      )}
    </>
  );
};
