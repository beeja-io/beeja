import { useState } from 'react';
import { BulkPayslipContainer } from '../styles/BulkPayslipStyles.style';
import { Button } from '../styles/CommonStyles.style';
import {
  FileUploadField,
  FormFileSelected,
  InputLabelContainer,
  ValidationText,
} from '../styles/DocumentTabStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { getCurrentThreeMonths } from '../utils/dateFormatter';
import { FormFileCloseIcon, FormFileIcon } from '../svgs/DocumentTabSvgs.svg';
import { uploadBulkPayslip } from '../service/axiosInstance';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useNavigate } from 'react-router-dom';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { UploadReceiptIcon } from '../svgs/ExpenseListSvgs.svg';
import { formatFileSize } from '../utils/fileSizeFormatter';
import { months } from '../utils/monthsConstants';
import axios, { AxiosError } from 'axios';
import { useTranslation } from 'react-i18next';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';

const BulkPayslip = () => {
  const [selectedFile, setSelectedFile] = useState<File>();

  const { t } = useTranslation();

  const [monthOfPayslips, setMonthOfPayslips] = useState(
    new Date().toLocaleString('default', { month: 'long' })
  );

  const [yearOfPayslips, setYearOfPayslips] = useState(
    new Date().getFullYear().toString()
  );

  const [toastSuccessMessage, setToastSuccessMessage] = useState('');
  const [toastErrorMessage, setToastErrorMessage] = useState('');
  const [isResponseLoading, setIsResponseLoading] = useState(false);

  const handleIsResponseLoading = (bool: boolean) => {
    setIsResponseLoading(bool);
  };

  const handleToastSuccessMessage = (message: string) => {
    setToastSuccessMessage(message);
  };
  const handleToastErrorMessage = (message: string) => {
    setToastErrorMessage(message);
  };

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();
    event.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
      const droppedFiles = Array.from(event.dataTransfer.files);
      setSelectedFile(droppedFiles[0]);
    }
  };

  const removeFile = () => {
    setSelectedFile(undefined);
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      const selectedFiles = Array.from(event.target.files);
      setSelectedFile(selectedFiles[0]);
    }
  };

  const handleMonthChange = (month: string) => {
    setMonthOfPayslips(month);
    if (month !== months[0] && month !== months[1]) {
      setYearOfPayslips(new Date().getFullYear().toString());
    }
  };
  const handleYearChange = (year: string) => {
    setYearOfPayslips(year);
  };

  const handleUploadBulkPayslipSubmit = async () => {
    if (selectedFile == null || selectedFile == undefined) {
      handleToastErrorMessage('ZIP_IS_REQUIRED');
      return;
    }
    const formData = new FormData();
    if (selectedFile) {
      formData.append('zipFile', selectedFile);
    }
    formData.append('month', monthOfPayslips);
    formData.append('year', yearOfPayslips);
    setIsResponseLoading(true);
    try {
      await uploadBulkPayslip(formData);
      handleToastSuccessMessage('SUCCESSFULLY_UPLOADED');
      removeFile();
      setMonthOfPayslips(
        new Date().toLocaleString('default', { month: 'long' })
      );
      setYearOfPayslips(new Date().getFullYear().toString());
      handleIsResponseLoading(false);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.code === 'ERR_NETWORK') {
          handleToastErrorMessage('NETWORK_ISSUE');
        } else if (axiosError.response?.status === 503) {
          handleToastErrorMessage('SERVICE_UNAVAILABLE');
        } else {
          handleToastErrorMessage('UPLOAD_UNSUCCESSFUL');
        }
      } else {
        handleToastErrorMessage('UPLOAD_UNSUCCESSFUL');
      }
    } finally {
      handleIsResponseLoading(false);
    }
  };

  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };

  const getYearOptions = () => {
    const currentYearNum = new Date().getFullYear();
    const previousYearNum = currentYearNum - 1;

    const selectedMonthName = monthOfPayslips;

    const needsPreviousYear =
      selectedMonthName === months[0] || selectedMonthName === months[1];

    let yearsToShow: number[] = [currentYearNum];

    if (needsPreviousYear) {
      yearsToShow.unshift(previousYearNum);
    }

    return yearsToShow.map((year) => ({
      label: year.toString(),
      value: year.toString(),
    }));
  };

  const yearDropdownOptions = getYearOptions();

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('UPLOAD_BULK_PAYSLIPS')}
          </span>
        </ExpenseHeadingSection>
        <BulkPayslipContainer>
          <section className="topFields">
            <InputLabelContainer Width="400px">
              <label>
                {t('MONTH')} <ValidationText className="star">*</ValidationText>
              </label>
              <DropdownMenu
                label="Select Month"
                name="month"
                id="month"
                className="largeContainerBulk"
                onChange={(e) => handleMonthChange(e ?? '')}
                sortOptions={false}
                options={getCurrentThreeMonths.map((mon, index) => ({
                  label: mon,
                  value: (index + 1).toString(),
                }))}
              />
            </InputLabelContainer>
            <InputLabelContainer Width="400px">
              <label>
                {t('YEAR')} <ValidationText className="star">*</ValidationText>
              </label>
              <DropdownMenu
                label="Select Year"
                name="year"
                id="year"
                className="largeContainerBulk"
                onChange={(e) => handleYearChange(e ?? '')}
                options={yearDropdownOptions}
              />
            </InputLabelContainer>
          </section>

          <InputLabelContainer
            style={{ width: '100%', alignSelf: 'flex-start' }}
          >
            <div>
              <label>
                {t('ATTACHMENT')}{' '}
                <ValidationText className="star">*</ValidationText>
              </label>{' '}
            </div>
            <FileUploadField
              className="bulkpayslipFile"
              onDragOver={handleDragOver}
              onDrop={handleDrop}
            >
              <label htmlFor="fileInput" className="bulkPayslipsLable">
                <div>
                  <div className="textInInput">
                    {t('DRAG_AND_DROP_OR')}
                    <span className="blackTextInInput"> {t('BROWSE')} </span>
                  </div>
                  <UploadReceiptIcon />
                </div>
              </label>
              <input
                type="file"
                accept="application/zip"
                id="fileInput"
                style={{ display: 'none' }}
                required
                onChange={handleFileChange}
                onKeyPress={(event) => {
                  if (event.key === 'Enter') {
                    event.preventDefault();
                  }
                }}
              />
            </FileUploadField>
            {selectedFile && (
              <div>
                <FormFileSelected>
                  <FormFileIcon />
                  <span className="fileDetails">
                    <span>{selectedFile.name}</span>
                    <span>{formatFileSize(selectedFile.size)}</span>
                  </span>
                  <span
                    style={{
                      cursor: 'pointer',
                    }}
                    onClick={() => removeFile()}
                  >
                    <FormFileCloseIcon />
                  </span>
                </FormFileSelected>
              </div>
            )}
            <span className="grayText">{t('FILE_FORMAT_ZIP')}</span>
          </InputLabelContainer>

          <section className="buttonsArea">
            <Button
              width="200px"
              className="submit"
              onClick={handleUploadBulkPayslipSubmit}
            >
              {t('UPLOAD')}
            </Button>
          </section>
        </BulkPayslipContainer>
      </ExpenseManagementMainContainer>
      {toastSuccessMessage.length > 0 && (
        <ToastMessage
          messageBody="PAYSLIPS_WILL_BE_DISTRIBUTED_SHORTLY"
          messageHeading={toastSuccessMessage}
          messageType="success"
          handleClose={() => handleToastSuccessMessage('')}
        />
      )}
      {toastErrorMessage.length > 0 && (
        <ToastMessage
          messageBody="THE_BULK_PAYSLIP_HAS_NOT_UPLOADED"
          messageHeading={toastErrorMessage}
          messageType="error"
          handleClose={() => handleToastErrorMessage('')}
        />
      )}
      {isResponseLoading && <SpinAnimation />}
    </>
  );
};

export default BulkPayslip;
