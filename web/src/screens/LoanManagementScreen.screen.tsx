import { useNavigate } from 'react-router-dom';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useEffect, useCallback, useState } from 'react';
import LoanApplicationScreen from './LoanApplicationScreen.screen';
import LoanListView from '../components/reusableComponents/LoanListView.compoment';
import useKeyPress from '../service/keyboardShortcuts/onKeyPress';
import { useUser } from '../context/UserContext';
import { hasPermission } from '../utils/permissionCheck';
import { LOAN_MODULE } from '../constants/PermissionConstants';
import { useTranslation } from 'react-i18next';
import Pagination from '../components/directComponents/Pagination.component';
import { getAllLoans } from '../service/axiosInstance';

const LoanManagementScreen = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { t } = useTranslation();

  const [isApplyLoanScreen, setIsApplyLoanScreen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalApplicants, setTotalApplicants] = useState(0);
  const [loanList, setLoanList] = useState([]);
  const [loading, setLoading] = useState(false);

  const goToPreviousPage = () => {
    navigate(-1);
  };

  const handleIsApplyLoanScreen = () => {
    setIsApplyLoanScreen(!isApplyLoanScreen);
  };

  useKeyPress(78, () => {
    user &&
      hasPermission(user, LOAN_MODULE.CREATE_LOAN) &&
      setIsApplyLoanScreen(true);
  });

  const fetchLoanData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllLoans();
      const loans = res.data.loans || [];
      const total = loans.length;

      const startIndex = (currentPage - 1) * pageSize;
      const paginatedLoans = loans
        .slice(startIndex, startIndex + pageSize)
        .reverse();

      setLoanList(paginatedLoans);
      setTotalApplicants(total);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching loan data:', error);
      setLoading(false);
    }
  }, [currentPage, pageSize]);

  useEffect(() => {
    fetchLoanData();
  }, [fetchLoanData]);

  return (
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
        <>
          <LoanListView
            handleIsApplyLoanScreen={handleIsApplyLoanScreen}
            currentPage={currentPage}
            totalApplicants={totalApplicants}
            pageSize={pageSize}
            setCurrentPage={setCurrentPage}
            setPageSize={setPageSize}
            setTotalApplicants={setTotalApplicants}
            loanList={loanList}
            loading={loading}
          />
          <Pagination
            currentPage={currentPage}
            totalPages={Math.ceil(totalApplicants / pageSize)}
            handlePageChange={setCurrentPage}
            totalItems={totalApplicants}
            handleItemsPerPage={setPageSize}
            itemsPerPage={pageSize}
          />
        </>
      )}
    </ExpenseManagementMainContainer>
  );
};

export default LoanManagementScreen;
