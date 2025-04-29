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

import { useEffect, useState } from 'react';
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
      case t('MONITOR_LOAN'):
        setEmiTenure('');
        break;
      case t('PERSONAL_LOAN'):
        setEmiTenure('');
        break;
      case t('ADVANCE_SALARY'):
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
      case t('MONITOR_LOAN'):
        return Array.from({ length: 6 }, (_, i) => i + 1).map((month) => (
          <option key={month} value={month}>
            {month}
          </option>
        ));
      case t('PERSONAL_LOAN'):
        return Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
          <option key={month} value={month}>
            {month}
          </option>
        ));
      case t('ADVANCE_SALARY'):
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
          data.emiStartDate === t('CURRENT_MONTH')
            ? currentDate.toISOString()
            : nextMonthDate.toISOString(),
        emiTenure: parseInt(data.emiTenure),
      }),
      {
        loading: t('APPLYING_NEW_LOAN'),
        closeButton: true,
        success: () => {
          setIsSubmitting(false);
          props.handleIsApplyLoanScreen();
          return t('LOAN_REQUEST_RECEIVED');
        },
        error: (error) => {
          setIsSubmitting(false);
          if (axios.isAxiosError(error)) {
            const axiosError = error as AxiosError;
            if (axiosError.code === 'ERR_NETWORK') {
              return t('NETWORK_ERROR_CHECK_CONNECTION');
            }
            if (axiosError.code === 'ECONNABORTED') {
              return t('REQUEST_TIMEOUT_TRY_AGAIN');
            }
          }
          return t('REQUEST_UNSUCCESSFUL_TRY_AGAIN');
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
      (loanType === t('MONITOR_LOAN') &&
        user &&
        parseFloat(amount) > user.organizations.loanLimit.monitorLoan) ||
      (loanType === t('PERSONAL_LOAN') &&
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
                <select
                  className="selectoption largeSelectOption"
                  name="loanType"
                  id="loanType"
                  value={loanType}
                  onChange={handleLoanTypeChange}
                  onKeyPress={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                    }
                  }}
                >
                  <option value="">{t('SELECT_LOAN_TYPE')}</option>
                  <option value="MONITOR_LOAN">{t('MONITOR_LOAN')}</option>
                  <option value="PERSONAL_LOAN">{t('PERSONAL_LOAN')}</option>
                </select>
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
                <select
                  className="selectoption largeSelectOption"
                  name="emiTenure"
                  id="emiTenure"
                  value={emiTenure}
                  onChange={(e) => setEmiTenure(e.target.value)}
                  disabled={!isEmiTenureEnabled}
                  onKeyPress={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                    }
                  }}
                >
                  <option value="">{t('SELECT_EMI_TENURE')}</option>
                  {renderEmiTenureOptions()}
                </select>
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('EMI_START_FROM')}{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <select
                  className="selectoption largeSelectOption"
                  name="emiStartDate"
                  id="emiStartDate"
                  onChange={handleEmiStartMonthChange}
                  onKeyPress={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                    }
                  }}
                >
                  <option value="">{t('SELECT_MONTH')}</option>
                  <option value="CURRENT_MONTH">{t('CURRENT_MONTH')}</option>
                  <option value="NEXT_MONTH">{t('NEXT_MONTH')}</option>
                </select>
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
            messageBody={t('YOUR_LOAN_REQUEST_REACHED_TO_OUR_TEAM')}
            messageHeading={t('REQUEST_SUCCESSFUL')}
            handleClose={() => handleShowSuccessMessage()}
          />
        )}
        {isSubmitting && <SpinAnimation />}
        {errorMessage && (
          <ToastMessage
            messageType="error"
            messageBody={errorMessage}
            messageHeading={t('REQUEST_UNSUCCESSFUL')}
            handleClose={() => setErrorMessage(null)}
          />
        )}
      </PayrollMainContainer>
      {amountExceedModal && (
        <CenterModal
          handleModalClose={handleModalClose}
          handleModalSubmit={handleModalSubmit}
          modalHeading={t('MAXIMUM_LOAN_LIMIT_REACHED')}
          modalContent=""
          modalLeftButtonClass="changeAmountButton"
          modalRightButtonClass="continueAnywayButton"
          modalLeftButtonText={t('CHANGE_AMOUNT')}
          modalRightButtonText={t('CONTINUE_ANYWAY')}
          modalSVG={<RedcircleExclamatory />}
          modalWidth="550px"
        />
      )}
    </>
  );
};

export default LoanApplicationScreen;
