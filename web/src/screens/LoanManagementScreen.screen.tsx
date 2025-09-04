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
import { getAllLoans, getLoans } from '../service/axiosInstance';

const LoanManagementScreen = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { t } = useTranslation();

  const [isApplyLoanScreen, setIsApplyLoanScreen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [totalApplicants, setTotalApplicants] = useState(0);
  const [loansList, setLoansList] = useState([]);
  const [loading, setLoading] = useState(false);

  const goToPreviousPage = () => {
    if (isApplyLoanScreen) {
      setIsApplyLoanScreen(false);
    } else {
      navigate(-1);
    }
  };

  const handleIsApplyLoanScreen = () => {
    setIsApplyLoanScreen(!isApplyLoanScreen);
  };

  useKeyPress(78, () => {
    user &&
      hasPermission(user, LOAN_MODULE.CREATE_LOAN) &&
      setIsApplyLoanScreen(true);
  });

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);
  };

  const fetchLoanData = useCallback(async () => {
    try {
      setLoading(true);
      const queryString = `?pageNumber=${currentPage}&pageSize=${itemsPerPage}`;
      let res;
      if (user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS)) {
        res = await getAllLoans(queryString);
        const loans = res.data.loansList || [];
        setLoansList(loans);
        setTotalItems(Number(res.data.totalRecords || loans.length));
        setTotalApplicants(res.data.totalSize || loans.length);
      } else if (user && hasPermission(user, LOAN_MODULE.READ_LOAN)) {
        res = await getLoans(user.employeeId);
        const loans = res.data || [];
        setLoansList(loans);
        setTotalItems(loans.length);
        setTotalApplicants(loans.length);
      } else {
        setLoansList([]);
      }
    } catch (error) {
      throw new Error('Error fetching loan data:' + error);
    } finally {
      setLoading(false);
    }
  }, [currentPage, itemsPerPage, user]);

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
            setCurrentPage={setCurrentPage}
            setTotalApplicants={setTotalApplicants}
            loansList={loansList}
            loading={loading}
            fetchLoans={fetchLoanData}
          />
          {loansList.length > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={Math.ceil(totalItems / itemsPerPage)}
              handlePageChange={handlePageChange}
              itemsPerPage={itemsPerPage}
              handleItemsPerPage={handlePageSizeChange}
              totalItems={totalItems}
            />
          )}
        </>
      )}
    </ExpenseManagementMainContainer>
  );
};

export default LoanManagementScreen;
