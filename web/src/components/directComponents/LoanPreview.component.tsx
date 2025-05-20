import { useTranslation } from 'react-i18next';
import {
  LOAN_TYPE,
  LOAN_TYPE_BE,
  LoanStatus,
  LoanStatusBE,
} from '../../constants/LoanStatus';
import { LOAN_MODULE } from '../../constants/PermissionConstants';
import { useUser } from '../../context/UserContext';
import { Loan } from '../../entities/LoanEntity';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import {
  InputLabelContainer,
  TextInput,
} from '../../styles/DocumentTabStyles.style';
import { LoanPreviewModal } from '../../styles/LoanPreviewStyles.style';
import { formatDate, monthsDiff } from '../../utils/dateFormatter';
import { hasPermission } from '../../utils/permissionCheck';

type LoanPreviewProps = {
  loan: Loan;
  handleClose: () => void;
};
const LoanPreview = (props: LoanPreviewProps) => {
  const { user } = useUser();

  useKeyPress(27, () => {
    props.handleClose();
  });
  const { t } = useTranslation();

  const safeFormatDate = (dateString: string | null | undefined) => {
    if (!dateString) return '-';
    try {
      return formatDate(dateString);
    } catch {
      return '-';
    }
  };
  return (
    <LoanPreviewModal>
      <div>
        <InputLabelContainer>
          <label>{t('LOAN_NUMBER')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={props.loan.loanNumber}
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('LOAN_TYPE')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={
              props.loan.loanType === LOAN_TYPE_BE.MONITOR_LOAN
                ? LOAN_TYPE.MONITOR_LOAN
                : LOAN_TYPE.PERSONAL_LOAN
            }
            disabled={true}
          />
        </InputLabelContainer>
        {user && hasPermission(user, LOAN_MODULE.UPDATE_LOAN) && (
          <InputLabelContainer>
            <label>{t('EMPLOYEE_ID')} </label>
            <TextInput
              className={'disabledBgWhite'}
              value={props.loan.employeeId}
              disabled={true}
            />
          </InputLabelContainer>
        )}
        <InputLabelContainer>
          <label>{t('REQUESTED_DATE')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={safeFormatDate(props.loan.createdAt?.toString())}
            disabled={true}
          />
        </InputLabelContainer>

        <InputLabelContainer>
          <label>{t('LOAN_AMOUNT')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={`₹ ${props.loan.amount}`}
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('LOAN_PURPOSE')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={props.loan.purpose}
            disabled={true}
          />
        </InputLabelContainer>

        <InputLabelContainer>
          <label>{t('EMI_TENURE_MONTHS')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={props.loan.emiTenure}
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('EMI_AMOUNT')}</label>
          <TextInput
            className={'disabledBgWhite'}
            value={`₹ ${props.loan.monthlyEMI}`}
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('LOAN_STATUS')} </label>
          <TextInput
            className={'disabledBgWhite'}
            value={
              props.loan.status === LoanStatusBE.WAITING
                ? LoanStatus.WAITING
                : props.loan.status === LoanStatusBE.REJECTED
                  ? LoanStatus.REJECTED
                  : LoanStatus.APPROVED
            }
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('REMAINING_EMIS')}</label>
          <TextInput
            className={'disabledBgWhite'}
            value={
              props.loan.status === LoanStatusBE.APPROVED
                ? props.loan.emiTenure -
                  monthsDiff(new Date(props.loan.modifiedAt), new Date())
                : '-'
            }
            disabled={true}
          />
        </InputLabelContainer>
        <InputLabelContainer>
          <label>{t('COMMENTS')} </label>
          <textarea
            className={'disabledBgWhite'}
            value={props.loan.rejectionReason ? props.loan.rejectionReason : ''}
            disabled={true}
          />
        </InputLabelContainer>
      </div>
    </LoanPreviewModal>
  );
};

export default LoanPreview;
