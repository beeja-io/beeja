import {
  PayrollMainContainer,
  LoanApplicationFormContainer,
  ButtonGroup,
  TermsHeading,
  TermsLinks,
  CheckboxLabel,
  Checkbox,
  HeaderSection,
  Hr,
  ChildDiv1,
  ChildDiv2,
} from '../styles/LoanApplicationStyles.style';

import React, { useEffect, useState } from 'react';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { postLoan } from '../service/axiosInstance';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { Button, HighlightedBoxes } from '../styles/CommonStyles.style';
import { RedcircleExclamatory } from '../svgs/CommonSvgs.svs';
import CenterModal from '../components/reusableComponents/CenterModal.component';
import {
  MonitorLoanTermsAndConditions,
  PersonalLoanTermsAndConditions,
} from '../constants/Constants';
import {
  InputLabelContainer,
  ValidationText,
  TextInput,
} from '../styles/DocumentTabStyles.style';
import { toast } from 'sonner';
import axios, { AxiosError } from 'axios';
import { useUser } from '../context/UserContext';
import useKeyPress from '../service/keyboardShortcuts/onKeyPress';
import { hasPermission } from '../utils/permissionCheck';
import { LOAN_MODULE } from '../constants/PermissionConstants';
import useKeyCtrl from '../service/keyboardShortcuts/onKeySave';
import { useTranslation } from 'react-i18next';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';

interface LoanApplicationData {
  loanType: string;
  amount: string;
  loanPurpose: string;
  monthlyEMI: string;
  emiStartDate: string;
  emiTenure: string;
}
type LoanApplicationScreenProps = {
  handleIsApplyLoanScreen: () => void;
};
const LoanApplicationScreen = (props: LoanApplicationScreenProps) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [amount, setAmount] = useState<string>('');
  const [emiTenure, setEmiTenure] = useState<string>('');
  const [loanType, setLoanType] = useState<string>('');
  const [loanPurpose, setLoanPurpose] = useState<string>('');
  const [monthlyEMI, setMonthlyEMI] = useState<string>('');
  const [emiStartDate, setEmiStartDate] = useState<string>('');
  const [isChecked, setIsChecked] = useState<boolean>(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isFormComplete, setIsFormComplete] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [amountExceedModal, setAmountExceedModal] = useState(false);
  const [isEmiTenureEnabled, setIsEmiTenureEnabled] = useState<boolean>(false);

  useEffect(() => {
    if (
      amount &&
      emiTenure &&
      loanType &&
      loanPurpose &&
      emiStartDate &&
      isChecked
    ) {
      setIsFormComplete(true);
    } else {
      setIsFormComplete(false);
    }
  }, [amount, emiTenure, loanType, loanPurpose, emiStartDate, isChecked]);

  const handleAmount = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    if (/^[0-9]*$/.test(value)) {
      setAmount(value);
    }
  };

  const handleLoanTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const newLoanType = e.target.value;
    setLoanType(newLoanType);
    setIsEmiTenureEnabled(newLoanType !== '');
    setEmiTenure('');
    switch (newLoanType) {
      case 'MONITOR_LOAN':
        setEmiTenure('');
        break;
      case 'PERSONAL_LOAN':
        setEmiTenure('');
        break;
      case 'ADVANCE_SALARY':
        setEmiTenure('');
        break;
      default:
        setEmiTenure('');
        break;
    }
  };
  const handleLoanPurposeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newLoanPurpose = e.target.value;
    setLoanPurpose(newLoanPurpose);
  };

  const handleEmiStartMonthChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const newEmiStartMonth = e.target.value;
    setEmiStartDate(newEmiStartMonth);
  };

  const renderEmiTenureOptions = () => {
    switch (loanType) {
      case 'MONITOR_LOAN':
        return Array.from({ length: 6 }, (_, i) => i + 1).map((month) => (
          <option key={month} value={month}>
            {month}
          </option>
        ));
      case 'PERSONAL_LOAN':
        return Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
          <option key={month} value={month}>
            {month}
          </option>
        ));
      case 'ADVANCE_SALARY':
        return Array.from({ length: 1 }, (_, i) => i + 1).map((month) => (
          <option key={month} value={month}>
            {month}
          </option>
        ));
      default:
        return <option value="">{t('SELECT_EMI_TENURE')}</option>;
    }
  };
  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setIsChecked(e.target.checked);
  };
  useEffect(() => {
    if (amount && emiTenure) {
      const loanAmount = parseFloat(amount.replace(/,/g, ''));
      const emiTenureMonths = parseInt(emiTenure);
      const monthlyEMIValue = (loanAmount / emiTenureMonths).toFixed(2);
      setMonthlyEMI(monthlyEMIValue);
    } else {
      setMonthlyEMI('');
    }
  }, [amount, emiTenure]);

  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
      props.handleIsApplyLoanScreen();
    }, 3000);
  };
  const handleSuccess = async () => {
    setIsSubmitting(true);
    const data: LoanApplicationData = {
      loanType,
      amount,
      loanPurpose,
      monthlyEMI,
      emiStartDate,
      emiTenure,
    };

    const currentDate = new Date();
    const nextMonthDate = new Date(currentDate);
    nextMonthDate.setMonth(nextMonthDate.getMonth() + 1);

    toast.promise(
      postLoan({
        loanType: data.loanType,
        amount: parseFloat(data.amount),
        purpose: data.loanPurpose,
        monthlyEMI: parseFloat(data.monthlyEMI),
        emiStartDate:
          data.emiStartDate === 'CURRENT_MONTH'
            ? currentDate.toISOString()
            : nextMonthDate.toISOString(),
        emiTenure: parseInt(data.emiTenure),
      }),
      {
        loading: 'Applying new loan...',
        closeButton: true,
        success: () => {
          setIsSubmitting(false);
          props.handleIsApplyLoanScreen();
          return 'Your loan request has been reached our team.';
        },
        error: (error) => {
          setIsSubmitting(false);
          if (axios.isAxiosError(error)) {
            const axiosError = error as AxiosError;
            if (axiosError.code === 'ERR_NETWORK') {
              return 'Network Error, Please check connection';
            }
            if (axiosError.code === 'ECONNABORTED') {
              return 'Request timeout, Please try again';
            }
          }
          return 'Request Unsuccessful, Please try again';
        },
      }
    );
  };

  const handleModalClose = () => {
    setAmountExceedModal(false);
  };
  const handleModalSubmit = () => {
    setAmountExceedModal(false);
    handleSuccess();
  };
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (
      (loanType === 'MONITOR_LOAN' &&
        user &&
        parseFloat(amount) > user.organizations.loanLimit.monitorLoan) ||
      (loanType === 'PERSONAL_LOAN' &&
        user &&
        parseFloat(amount) > user.organizations.loanLimit.personalLoan)
    ) {
      setAmountExceedModal(true);
      setIsSubmitting(false);
      return;
    }
    handleSuccess();
  };

  useKeyPress(27, () => {
    user &&
      hasPermission(user, LOAN_MODULE.CREATE_LOAN) &&
      props.handleIsApplyLoanScreen();
  });
  useKeyCtrl('s', () =>
    handleSubmit(event as unknown as React.FormEvent<HTMLFormElement>)
  );

  return (
    <>
      <PayrollMainContainer>
        <LoanApplicationFormContainer onSubmit={handleSubmit}>
          <HeaderSection>
            <h4>{t('LOAN_APPLICATION')}</h4>
            <ButtonGroup>
              <Button
                onClick={props.handleIsApplyLoanScreen}
                width="112px"
                height="40px"
              >
                {t('CANCEL')}
              </Button>
              <Button
                width="112px"
                height="40px"
                disabled={!isFormComplete}
                className={`${
                  isFormComplete ? 'formValidated' : 'noFormValidated'
                }`}
              >
                {t('SUBMIT')}
              </Button>
            </ButtonGroup>
          </HeaderSection>
          <Hr />
          <TermsHeading>{t('TERMS_AND_CONDITIONS')}</TermsHeading>
          <TermsLinks>
            <a target="_blank" href={MonitorLoanTermsAndConditions}>
              {t('MONITOR_LOAN_TERNMS')}
            </a>
            <a target="_blank" href={PersonalLoanTermsAndConditions}>
              {t('PERSONAL_LOAN_TERMS')}
            </a>
          </TermsLinks>
          <HighlightedBoxes>
            <div>
              <h4>{t('MAXIMUM_MONITOR_LOAN_AMOUNT')}</h4>
              <span>{user && user.organizations.loanLimit.monitorLoan}</span>
            </div>
            <div>
              <h4>{t('MAXIMUM_PERSONAL_LOAN_AMOUNT')}</h4>
              <span>{user && user.organizations.loanLimit.personalLoan}</span>
            </div>
          </HighlightedBoxes>
          <Hr />
          <div className="formArea">
            <ChildDiv1>
              <InputLabelContainer>
                <label>
                  {t('LOAN_TYPE')}{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('SELECT_LOAN_TYPE')}
                  name="loanType"
                  className="largeContainerExp"
                  id="loanType"
                  value={loanType}
                  onChange={(val) => {
                    handleLoanTypeChange({
                      target: {
                        name: 'loanType',
                        value: val,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>);
                  }}
                  options={[
                    { label: t('SELECT_LOAN_TYPE'), value: '' },
                    { label: t('MONITOR_LOAN'), value: 'MONITOR_LOAN' },
                    { label: t('PERSONAL_LOAN'), value: 'PERSONAL_LOAN' },
                  ]}
                />
              </InputLabelContainer>
              <InputLabelContainer>
                <label>
                  {t('LOAN_AMOUNT')}{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  className="largeInput"
                  type="text"
                  name="loanAmount"
                  id="loanAmount"
                  value={amount}
                  onChange={handleAmount}
                  onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    } else {
                      const isNumeric = /^\d+$/.test(e.key);
                      if (!isNumeric) {
                        e.preventDefault();
                      }
                    }
                  }}
                  placeholder={t('ENTER_AMOUNT')}
                />
              </InputLabelContainer>
              <InputLabelContainer>
                <label>{t('MONTHLY_EMI')}</label>
                <TextInput
                  className="largeInput"
                  type="text"
                  placeholder="₹ 0"
                  name="monthyEmi"
                  value={'₹ ' + monthlyEMI}
                  disabled
                  onKeyPress={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                    }
                  }}
                />
              </InputLabelContainer>
            </ChildDiv1>
            <ChildDiv2>
              <InputLabelContainer>
                <label>
                  {t('LOAN_PURPOSE')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  className="largeInput"
                  type="text"
                  placeholder={t('ENTER_YOUR_TEXT_HERE')}
                  name="loanPurpose"
                  value={loanPurpose}
                  onChange={handleLoanPurposeChange}
                  onKeyPress={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                    }
                  }}
                />
              </InputLabelContainer>
              <InputLabelContainer>
                <label>
                  {t('EMI_TENURE_MONTHS')}{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('SELECT_EMI_TENURE')}
                  name="emiTenure"
                  className={`largeContainerExp ${!isEmiTenureEnabled ? 'cursor-disabled' : ''}`}
                  id="emiTenure"
                  value={emiTenure}
                  disabled={!isEmiTenureEnabled}
                  onChange={(val) => {
                    setEmiTenure(val ?? '');
                  }}
                  options={[
                    { label: t('SELECT_EMI_TENURE'), value: '' },
                    ...React.Children.toArray(renderEmiTenureOptions()).map(
                      (option: any) => ({
                        label: option.props.children,
                        value: option.props.value,
                      })
                    ),
                  ]}
                />
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('EMI_START_FROM')}{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('SELECT_MONTH')}
                  name="emiStartDate"
                  id="emiStartDate"
                  className="largeContainerExp"
                  onChange={(val) => {
                    const syntheticEvent = {
                      target: {
                        name: 'emiStartDate',
                        value: val,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>;
                    handleEmiStartMonthChange(syntheticEvent);
                  }}
                  options={[
                    { label: t('SELECT_MONTH'), value: '' },
                    { label: t('CURRENT_MONTH'), value: 'CURRENT_MONTH' },
                    { label: t('NEXT_MONTH'), value: 'NEXT_MONTH' },
                  ]}
                />
              </InputLabelContainer>
            </ChildDiv2>
          </div>

          <CheckboxLabel>
            <Checkbox
              id="checkbox"
              type="checkbox"
              checked={isChecked}
              onChange={handleCheckboxChange}
            />
            <span>{t('I_HAVE_AGGREE_TERMS_AND_CONDITIONS')}</span>
          </CheckboxLabel>
        </LoanApplicationFormContainer>
        {showSuccessMessage && (
          <ToastMessage
            messageType="success"
            messageBody="YOUR_LOAN_REQUEST_REACHED_TO_OUR_TEAM"
            messageHeading="REQUEST_SUCCESSFUL"
            handleClose={() => handleShowSuccessMessage()}
          />
        )}
        {isSubmitting && <SpinAnimation />}
        {errorMessage && (
          <ToastMessage
            messageType="error"
            messageBody={errorMessage}
            messageHeading="REQUEST_UNSUCCESSFUL"
            handleClose={() => setErrorMessage(null)}
          />
        )}
      </PayrollMainContainer>
      {amountExceedModal && (
        <CenterModal
          handleModalClose={handleModalClose}
          handleModalSubmit={handleModalSubmit}
          modalHeading="MAXIMUM_LOAN_LIMIT_REACHED"
          modalContent=""
          modalLeftButtonClass="changeAmountButton"
          modalRightButtonClass="continueAnywayButton"
          modalLeftButtonText="CHANGE_AMOUNT"
          modalRightButtonText="CONTINUE_ANYWAY"
          modalSVG={<RedcircleExclamatory />}
          modalWidth="550px"
        />
      )}
    </>
  );
};

export default LoanApplicationScreen;
