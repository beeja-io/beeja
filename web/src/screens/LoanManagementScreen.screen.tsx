import { useNavigate } from 'react-router-dom';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useState } from 'react';
import LoanApplicationScreen from './LoanApplicationScreen.screen';
import LoanListView from '../components/reusableComponents/LoanListView.compoment';
import useKeyPress from '../service/keyboardShortcuts/onKeyPress';
import { useUser } from '../context/UserContext';
import { hasPermission } from '../utils/permissionCheck';
import { LOAN_MODULE } from '../constants/PermissionConstants';
import { useTranslation } from 'react-i18next';

const LoanManagementScreen = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { t } = useTranslation();
  const goToPreviousPage = () => {
    navigate(-1);
  };

  const [isApplyLoanScreen, setIsApplyLoanScreen] = useState(false);
  const handleIsApplyLoanScreen = () => {
    setIsApplyLoanScreen(!isApplyLoanScreen);
  };

  useKeyPress(78, () => {
    user &&
      hasPermission(user, LOAN_MODULE.CREATE_LOAN) &&
      setIsApplyLoanScreen(true);
  });
  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('DEDUCTIONS_AND_LOANS')}
          </span>
        </ExpenseHeadingSection>
        {isApplyLoanScreen ? (
          <LoanApplicationScreen
            handleIsApplyLoanScreen={handleIsApplyLoanScreen}
          />
        ) : (
          <LoanListView handleIsApplyLoanScreen={handleIsApplyLoanScreen} />
        )}
      </ExpenseManagementMainContainer>
    </>
  );
};

export default LoanManagementScreen;
