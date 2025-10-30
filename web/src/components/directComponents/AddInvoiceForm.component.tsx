import { useContext, useEffect, useMemo, useRef, useState } from 'react';
import {
  AddRowContainer,
  DatePicker,
  InvoiceAddFormMainContainer,
  InvoiceAddFormSubContainer,
  InvoiceAddressContainer,
  InvoiceButton,
  InvoiceButtonContainer,
  InvoiceCalculationContainer,
  InvoiceDetails,
  InvoicePaymentContainer,
  InvoiceRemittance,
  TableBodyRow,
  TableBodyRows,
  TableHead,
  TableHeadLabel,
  TableRow,
  TableShowing,
  TableList,
  Tablelist,
  TextInput,
  ValidationText,
  Alignmenting,
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
import {
  createInvoice,
  downloadContractFile,
} from '../../service/axiosInstance';
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
import {
  BillingCurrency,
  BillingCurrencyLabels,
} from '../reusableComponents/ContractEnums.component.tsx';
import SpinAnimation from '../loaders/SprinAnimation.loader.tsx';
import { t } from 'i18next';

export const AddInvoiceForm = (props: AddInvoiceFormProps) => {

  const generateDueRemarks = (endDate: Date | undefined): string => {
    return t(
      `Please transfer the due amount to the following bank account with in next 7 days.`
    );
  };

  const getBillingCurrency = (value?: string): BillingCurrency => {
    if (
      value &&
      Object.values(BillingCurrency).includes(value as BillingCurrency)
    ) {
      return value as BillingCurrency;
    }
    return BillingCurrency.EURO;
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

  const getCurrencySymbol = (currency: BillingCurrency): string => {
    const label = BillingCurrencyLabels[currency];
    const match = label.match(/\((.*)\)/);
    return match ? match[1] : '';
  };
  const [formData, setFormData] = useState<FormDataProps>({
    RemittanceNo: props.remittanceReferenceNumber || '',
    InvoiceNo: props.invoiceId || '',
    tax: "",
    taxId: organizationDetails?.taxId || '',
    organization: organizationDetails?.name || '',
    organizationId: props.organizationId || '',
    projectId: props.projectId || '',
    fromDate: props.startDate ? new Date(props.startDate) : new Date(),
    toDate: props.endDate ? new Date(props.endDate) : new Date(),
    contractName: props.contractTitle || '',
    contractId: props.contractId,
    clientName: props.clientName || '',
    clientId: props.clientId || '',
    status: props.status || '',
    currencyType: getBillingCurrency(props.billingCurrency),

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
      props.endDate ? new Date(props.endDate) : undefined
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
      contractName: props.contractTitle || '',
      currencyType: getBillingCurrency(props.billingCurrency),

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
        props.endDate ? new Date(props.endDate) : undefined
      ),
      taxId: organizationDetails?.taxId || prevData.taxId,
      organization: organizationDetails?.name || '',
      paymentDetails: {
        accountName: organizationDetails?.bankDetails?.accountName || '-',
        bankName: organizationDetails?.bankDetails?.bankName || '-',
        accountNumber: organizationDetails?.bankDetails?.accountNumber || '-',
        ifscNumber: organizationDetails?.bankDetails?.ifscNumber || '-',
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
    props.contractTitle,
    props.status,
    props.billingCurrency,
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
  const [isCityEditModeOn, setIsCityEditModeOn] = useState(false);
  const handleIsCityEditModeOn = () => setIsCityEditModeOn(!isCityEditModeOn);
  const [cityValue, setCityValue] = useState(
    organizationDetails?.address?.city || ''
  );
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
        contract: t('* Task name should not be empty'),
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
        price: t('* price should not be empty'),
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
  const gstAmount = (subTotal * Number(formData.tax)) / 100;
  const Total = subTotal + gstAmount;
  const [confirmDeleteChanges, setConfirmDeleteChanges] = useState(false);
  const handleConfirmDeleteChanges = (
    e: React.MouseEvent<HTMLButtonElement>
  ) => {
    e.preventDefault();
    setConfirmDeleteChanges(true);
  };
  const [confirmSaveChanges, setConfirmSaveChanges] = useState(false);
  const [isInvoiceSaved, setIsInvoiceSaved] = useState(false);
  const [invoiceFileId, setInvoiceFileId] = useState<string | null>(null);
  const [invoiceData, setInvoiceData] = useState<any>(null);
  const [isDownloading, setIsDownloading] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleSaveButtonClick = (e: { preventDefault: () => void }) => {
    e.preventDefault();

    if (data.length === 0) {
      toast.error(
        t(
          'Please add at least one Task and Price before generating the invoice.'
        )
      );
      return;
    }

    if (
      !formData.contractId ||
      !formData.clientId ||
      !formData.fromDate ||
      !formData.toDate
    ) {
      return;
    }
    setConfirmSaveChanges(true);
  };

  const handleConfirmSaveChanges = async () => {
    setIsLoading(true);
    setConfirmSaveChanges(false);
    try {
      const invoiceRequest: InvoiceRequest = {
        contractId: formData.contractId,
        billingDate: formData.fromDate.toISOString(),
        dueDate: formData.toDate.toISOString(),
        amount: Total,
        currency: formData.currencyType,
        notes: [formData.remarksNote || '', formData.dueRemarks || ''].filter(
          Boolean
        ),
        tasks: data.map((item) => ({
          taskName: item.contract,
          description: item.description,
          amount: Number(item.price),
        })),
        clientId: formData.clientId,
        projectId: formData.projectId || null,
        remittanceRef: formData.RemittanceNo || null,
        invoiceId: formData.InvoiceNo || null,
        taxId: formData.taxId || null,
        vat: Number(formData.tax),
        daysLeftForPayment: formData.dueRemarks.match(/\d+/)?.[0] || '30',
        invoicePeriod: {
          startDate: formData.fromDate.toISOString(),
          endDate: formData.toDate.toISOString(),
        },
      };

      const response = await createInvoice(invoiceRequest);
      setInvoiceData(response.data);
      setInvoiceFileId(response.data.invoiceFileId);

      toast.success(t('Invoice created successfully!'));
      setIsInvoiceSaved(true);
    } catch (error: any) {
      toast.error(
        error?.response?.data?.message ||
        'Failed to create invoice. Please try again.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleDownloadInvoiceFile = async () => {
    if (!invoiceFileId || !invoiceData || isDownloading) {
      toast.error(t('Invoice file not available for download.'));
      return;
    }
    setIsDownloading(true);
    try {
      const response = await downloadContractFile(invoiceFileId);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute(
        'download',
        `${invoiceData.invoiceId} - ${formData.contractName}.pdf`
      );
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      toast.success(t('Invoice downloaded successfully'));
    } catch (error) {
      toast.error(t('Failed to download invoice. Please try again.'));
    } finally {
      setIsDownloading(false);
    }
  };

  const handleSendInvoice = async () => {
    if (!invoiceFileId || !invoiceData) {
      toast.error(t('Invoice file not available to send.'));
      return;
    }

    try {
      const subject = encodeURIComponent(
        `Invoice ${invoiceData.invoiceId} - ${formData.contractName}`
      );
      const body = encodeURIComponent(
        `Dear ${formData.clientName},\n\nPlease find attached the invoice ${invoiceData.invoiceId}.\n\nBest Regards,\n${organizationDetails?.name}`
      );

      window.location.href = `mailto:?subject=${subject}&body=${body}`;

      toast.success(t('Email client opened. Please attach the PDF manually.'));
    } catch (error) {
      toast.error(t('Failed to open email client.'));
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
          <InvoiceButton
            variant="send"
            disabled={!isInvoiceSaved}
            onClick={handleSendInvoice}
            title={
              !isInvoiceSaved
                ? t('Please generate an invoice Below to Send Invoice')
                : ''
            }
          >
            <MessageIconSVG />
            {t('Send Invoice')}
          </InvoiceButton>

          <InvoiceButton
            variant="download"
            disabled={!isInvoiceSaved || isDownloading}
            onClick={handleDownloadInvoiceFile}
            title={
              !isInvoiceSaved
                ? t('Please generate an invoice Below to download')
                : isDownloading
                  ? t('Downloading in progress...')
                  : ''
            }
          >
            <DownloadSVG />
            {isDownloading ? t('Downloading...') : t('Download')}
          </InvoiceButton>
        </InvoiceButtonContainer>
        <InvoiceAddFormSubContainer onSubmit={handleConfirmSaveChanges}>
          <InvoiceAddressContainer>
            <div className="adjusting">
              <span className="applyStyle1">
                {t('Billing From')}
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
                {t('Billing To')}
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
              <label>{t('Remittance Ref :')} </label>
              {!isRemittanceRefEditModeOn ? (
                <>
                  <span className="applyMargin"> {formData.RemittanceNo}</span>
                  <span onClick={handleIsRemittanceEditModeOn} style={{ cursor: 'pointer' }}>
                    <EditWhitePenSVG />
                  </span>
                </>
              ) : (
                <TextInput
                  type="text"
                  name="RemittanceNo"
                  value={formData.RemittanceNo}
                  onChange={handleChange}
                  onBlur={handleIsRemittanceEditModeOn}
                  required
                />
              )}
            </div>
            <div className="spanElement">
              <label>{t('TaxID :')}</label>
              {!isTaxIdEditModeOn ? (
                <>
                  <span className="applyMargin">
                    {' '}
                    {organizationDetails?.taxId}{' '}
                  </span>
                  <span onClick={handleIsTaxIdEditModeOn} style={{ cursor: 'pointer' }}>
                    <EditWhitePenSVG />
                  </span>
                </>
              ) : (
                <TextInput
                  type="text"
                  name="TaxId"
                  value={formData.taxId}
                  onChange={handleTaxIdChange}
                  onBlur={handleIsTaxIdEditModeOn}
                  required
                />
              )}
            </div>
          </InvoiceRemittance>
          <InvoiceDetails>
            <div className="spanElement fontSize">
              <label>{t('Invoice :')}</label>
              {!isInvoiceEditModeOn ? (
                <>
                  <span className="applyMargin fontColor">
                    {formData.InvoiceNo}{' '}
                  </span>
                  <span onClick={handleIsInvoiceEditModeOn} style={{ cursor: 'pointer' }}>
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
                  onBlur={handleIsInvoiceEditModeOn}
                  required
                />
              )}
            </div>
            <span className="applyMargin1 ">
              {formData.contractId} - {formData.contractName}
            </span>
            <div className="sub-invoicedetails">
              <div className="dateSet">
                <span>{t('Invoice Period')}</span>&nbsp;
                <div ref={calendarFromRef}>
                  <DatePicker onClick={handleFromCalendarClick}>
                    <span className="dateName">
                      <span className="calenderIcon" style={{ cursor: 'pointer' }}>
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
                        title="From Date"
                        minDate={props.startDate ? new Date(props.startDate) : undefined}
                        maxDate={
                          toDate
                            ? new Date(
                              Math.min(
                                toDate.getTime(),
                                props.endDate ? new Date(props.endDate).getTime() : toDate.getTime()
                              )
                            )
                            : props.endDate
                              ? new Date(props.endDate)
                              : undefined
                        }
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
                        handleCalenderChange={function (): void { }}
                      />
                    </div>
                  )}
                </div>
                <span> {t('To')} </span>&nbsp;
                <div ref={calendarToRef}>
                  <DatePicker onClick={handleToCalendarClick}>
                    <span className="dateName">
                      <span className="calenderIcon" style={{ cursor: 'pointer' }}>
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
                        title="To Date"
                        minDate={
                          fromDate
                            ? new Date(
                              Math.max(
                                fromDate.getTime(),
                                props.startDate ? new Date(props.startDate).getTime() : fromDate.getTime()
                              )
                            )
                            : props.startDate
                              ? new Date(props.startDate)
                              : undefined
                        }
                        maxDate={props.endDate ? new Date(props.endDate) : undefined}
                        handleDateInput={(selectedDate) => {
                          if (selectedDate instanceof Date) {
                            handleDateInput(selectedDate, false);
                            setFormData((prevState) => ({
                              ...prevState,
                              toDate: selectedDate,
                              dueRemarks: generateDueRemarks(selectedDate),
                            }));
                          }
                        }}
                        selectedDate={toDate ? toDate : new Date()}
                        handleCalenderChange={() => { }}
                      />
                    </div>
                  )}
                </div>
              </div>
              <div>
                {!isCityEditModeOn ? (
                  <>
                    <span className="remarks applyMargin">
                      {cityValue}, {formatCurrentDate(currentDate)}
                    </span>
                    <span onClick={handleIsCityEditModeOn} style={{ cursor: 'pointer' }}>
                      <EditWhitePenSVG />
                    </span>
                  </>
                ) : (
                  <TextInput
                    type="text"
                    value={cityValue}
                    onChange={(e) => setCityValue(e.target.value)}
                    onBlur={handleIsCityEditModeOn}
                  />
                )}
              </div>
            </div>
          </InvoiceDetails>
          <TableShowing>
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Sno')}</th>
                  <th className='spacesno'>
                    <TableHeadLabel>
                      {t('Task')}
                      <ValidationText>*</ValidationText>
                    </TableHeadLabel>
                  </th>
                  <th className='spacetask'>{t('Description')}</th>
                  <th>
                    <TableHeadLabel>
                      {'Price in ' + getCurrencySymbol(formData.currencyType)}
                      <ValidationText>*</ValidationText>
                    </TableHeadLabel>
                  </th>
                  <th></th>
                </tr>
              </TableHead>

              {data.map((project, index) => (
                <TableBodyRow key={index}>
                  <td>{index + 1}</td>
                  <td>{project.contract}</td>
                  <td>{project.description}</td>
                  <td>{project.price}</td>
                  <td onClick={(e) => handleDeleteList(e, index)} className='deletebox' style={{cursor:'pointer'}}>
                    <DeleteIconSVG />
                  </td>
                </TableBodyRow>
              ))}
            </TableList>
            <AddRowContainer>
              {!isAddRowsEditModeOn ? (
                <span
                  className="addRows"
                  style={{ cursor: 'pointer' }}
                  onClick={handleIsAddRowsEditModeOn}
                >
                  <Plus /> {t('Add Row')}
                </span>
              ) : (
                <div className="rowsAlign">
                  <div className="rowItem r1">
                    <input name="serialNo" value={data.length + 1} readOnly />
                  </div>
                  <div className="rowItem r2">
                    <input name="contract" onChange={handleChangeRows} />
                    {errors.contract && (
                      <span className="errorSpan">{errors.contract}</span>
                    )}
                  </div>
                  <div className="rowItem r3">
                    <input name="description" onChange={handleChangeRows} />
                  </div>
                  <div className="rowItem r4">
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
                    <button onClick={() => setIsAddRowsEditModeOn(false)} className="cross-icon">
                      <CrossMarkSVG />
                    </button>
                  </div>
                </div>
              )}
            </AddRowContainer>
          </TableShowing>
          <InvoiceCalculationContainer>
            <Tablelist>
              <div className="borderCollapse">
                <TableRow>
                  <td className='sidehead'>{t('Sub Total')}</td>
                  <td className='sidehead2'>{`(${getCurrencySymbol(formData.currencyType)}) ${subTotal}`}</td>
                </TableRow>
                <TableRow>
                  <td className='sidehead'>{t('Tax')}{' (%)'}</td>
                  {!taxFlag ? (
                    <td>
                      {`(${getCurrencySymbol(formData.currencyType)}) ${formData.tax}`}
                      <span onClick={handleTaxClick} style={{ cursor: 'pointer' }}>
                        {' '}
                        <EditWhitePenSVG />{' '}
                      </span>
                    </td>
                  ) : (
                    <td>
                      ({`${getCurrencySymbol(formData.currencyType)}`})
                      <TextInput
                        name="tax"
                        value={Number(formData.tax)}
                        onChange={handleChange}
                        onBlur={handleTaxClick}
                        required
                      />
                    </td>
                  )}
                </TableRow>
                <TableRow>
                  <td className='sidehead'>{t('Total')}</td>
                  <td>
                    ({getCurrencySymbol(formData.currencyType)}) {Total}
                  </td>
                </TableRow>
              </div>
            </Tablelist>

            <Tablelist>
              <TableRow>
                <td>
                  {t('Amount In Words')}({formData.currencyType}):
                </td>
                <td className='style1'>
                  {CapitalizeWords(toWords(Total))} {t('Only/-')}
                </td>
              </TableRow>
            </Tablelist>
          </InvoiceCalculationContainer>
          <Alignmenting>
          <div>
            <span className="remarks">{t('NOTE: ')}</span>
            {isDueDaysEditModeOn ? (
              <TextInput
                className="remarksNote"
                name="dueRemarks"
                value={formData.dueRemarks}
                onChange={handleChange}
                onBlur={handleIsDueDaysEditModeOn}
                required
              />
            ) : (
              <span className="remarksNote">
                {formData.dueRemarks}{' '}
                <span onClick={handleIsDueDaysEditModeOn} style={{ cursor: 'pointer' }}>
                  {' '}
                  <EditWhitePenSVG />
                </span>
              </span>
            )}
          </div>
          </Alignmenting>
          <InvoicePaymentContainer>
            <span className="PayDet">{t('Payment Details')}</span>
            <div>
              <Tablelist>
                <TableBodyRows>
                  <td>{t('Name :')}</td>
                  <td>{organizationDetails?.bankDetails?.accountName}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td> {t('Bank Name :')}</td>
                  <td>{organizationDetails?.bankDetails?.bankName}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td>{t('Account Number :')}</td>
                  <td>{organizationDetails?.bankDetails?.accountNumber}</td>
                </TableBodyRows>
                <TableBodyRows>
                  <td>{t('IFSC :')}</td>
                  <td>{organizationDetails?.bankDetails?.ifscNumber}</td>
                </TableBodyRows>
              </Tablelist>
            </div>
          </InvoicePaymentContainer>
          <Alignmenting>
          <div>
            <span className="remarks">{t('Remarks')}</span>
            {!isRemarksNoteEditModeOn ? (
              <span className="remarksNote">
                <span>"</span> {formData.remarksNote} <span>"</span>
                <span onClick={handleIsRemarksNoteEditModeOn} style={{ cursor: 'pointer' }}>
                  <EditWhitePenSVG />
                </span>
              </span>
            ) : (
              <TextInput
                className="remarksNote"
                name="remarksNote"
                value={formData.remarksNote}
                onChange={handleChange}
                onBlur={handleIsRemarksNoteEditModeOn}
                required
              />
            )}
            <div className="spanSign">
              <span className="remarksNote"> {t('Best Regards,')} </span>
              <span className="remarks"> {organizationDetails?.name} </span>
            </div>
          </div>
          </Alignmenting>
          <div className="formButtons">
            <Button type="button" onClick={handleConfirmDeleteChanges}>
              {t('CANCEL')}
            </Button>
            <InvoiceButton
              className="submit"
              type="submit"
              onClick={handleSaveButtonClick}
              disabled={isInvoiceSaved}
              title={
                isInvoiceSaved
                  ? t('Invoice already generated. Please download it above.')
                  : ''
              }
            >
              {t('Generate')}
            </InvoiceButton>
          </div>
        </InvoiceAddFormSubContainer>
      </InvoiceAddFormMainContainer>
      {confirmDeleteChanges && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalClose={() => setConfirmDeleteChanges(false)}
            handleModalLeftButtonClick={() => setConfirmDeleteChanges(false)}
            handleModalSubmit={() => props.handleClose()}
            modalHeading="Invoice Changes"
            modalContent={'Are you sure you want to discard changes?'}
          />
        </span>
      )}
      {confirmSaveChanges && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalClose={() => setConfirmSaveChanges(false)}
            handleModalLeftButtonClick={() => setConfirmSaveChanges(false)}
            handleModalSubmit={handleConfirmSaveChanges}
            modalHeading="Save Changes"
            modalContent={'Are you sure you want to save changes?'}
          />
        </span>
      )}
      {isLoading && (
        <div className="loader-overlay">
          <SpinAnimation />
        </div>
      )}
    </>
  );
};
