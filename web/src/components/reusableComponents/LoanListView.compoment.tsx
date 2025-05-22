import { useTranslation } from 'react-i18next';
import {
  PayrollMainContainer,
  StatusIndicator,
} from '../../styles/LoanApplicationStyles.style';
import ZeroEntriesFound from './ZeroEntriesFound.compoment';
import { Button } from '../../styles/CommonStyles.style';
import {
  TableListContainer,
  TableList,
  TableHead,
  TableBodyRow,
} from '../../styles/DocumentTabStyles.style';
import { getAllLoans } from '../../service/axiosInstance';
import { useContext, useEffect, useState } from 'react';
import { CalenderIcon } from '../../svgs/DocumentTabSvgs.svg';
import { LoanAction } from './LoanListAction';
import { useUser } from '../../context/UserContext';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import { ApplicationContext } from '../../context/ApplicationContext';
import CenterModalMain from './CenterModalMain.component';
import LoanPreview from '../directComponents/LoanPreview.component';
import { Loan } from '../../entities/LoanEntity';
import { LOAN_MODULE } from '../../constants/PermissionConstants';
import { hasPermission } from '../../utils/permissionCheck';
type LoanListViewProps = {
  handleIsApplyLoanScreen: () => void;
  currentPage: number;
  totalApplicants: number;
  // pageSize: number;
  setCurrentPage: (page: number) => void;
  // setPageSize: (size: number) => void;
  setTotalApplicants: (total: number) => void;
  loansList: any[];
  loading: boolean;
};
const LoanListView = (props: LoanListViewProps) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);

  const { loansList, updateLoansList } = useContext(ApplicationContext);

  const fetchLoans = async () => {
    try {
      /* 
        If user is super admin or account manager, then user will see all loans
        or user will see only his loans
      */
      if (user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS)) {
        const res = await getAllLoans();

        if (res?.data) {
          const sortedLoans = res.data.loansList.sort(
            (firstLoan: Loan, secondLoan: Loan) =>
              new Date(secondLoan.createdAt).getTime() -
              new Date(firstLoan.createdAt).getTime()
          );
          updateLoansList(sortedLoans);
        }
      } else {
        if (user && user.employeeId) {
          const res = await getAllLoans(user.employeeId);
          const sortedLoans = res.data.loansList.sort(
            (a: Loan, b: Loan) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          );

          updateLoansList(sortedLoans);
        }
      }
    } catch {
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (loansList === null || loansList === undefined) {
      setLoading(true);
    }
    fetchLoans();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  const Actions = [{ title: 'Approve' }, { title: 'Reject' }];
  const formatDate = (dateString: string | number | Date) =>
    new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    }).format(new Date(dateString));
  const formatLoanType = (loanType: any): string => {
  try {
    if (typeof loanType !== 'string' || !loanType) {
      return 'Unknown Loan Type';
    }
    return loanType
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
  } catch {
    return 'Unknown Loan Type';
  }
};

  const formatStatus = (status: string) => {
    return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
  };

  const [isLoanPreviewModalOpen, setIsLoanPreviewModalOpen] = useState(false);
  const [loanToBePreviewed, setIsLoanToBePreviewed] = useState<Loan>();
  const handleIsLoanPreviewModalOpen = () => {
    setIsLoanPreviewModalOpen(!isLoanPreviewModalOpen);
  };
  const handleLoanToBePreviewed = (loan: Loan) => {
    setIsLoanToBePreviewed(loan);
  };
  return (
    <>
      <PayrollMainContainer>
        <section>
          {user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS) ? (
            <span>
              <h4>{t('LIST_OF_LOANS')}</h4>
            </span>
          ) : (
            <span>
              <h4>{t('MY_LOANS')}</h4>
            </span>
          )}
          {user && hasPermission(user, LOAN_MODULE.CREATE_LOAN) && (
            <Button
              className="submit shadow"
              width="135px"
              height="40px"
              onClick={props.handleIsApplyLoanScreen}
            >
              {t('REQUESTED_LOAN')}
            </Button>
          )}
        </section>
        <TableListContainer style={{ marginTop: 0 }}>
          {loansList && loansList.length === 0 ? (
            <ZeroEntriesFound
              heading="There's no Loan history found"
              message="You have never involved in any previous loan requests"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th>{t('LOAN_NUMBER')}</th>
                  <th>{t('EMPLOYEE_NAME')}</th>
                  <th>{t('LOAN_TYPE')}</th>
                  <th>{t('REQUESTED_DATE')}</th>
                  <th>{t('LOAN_AMOUNT')}</th>
                  <th className="statusHeader">{t('STATUS')}</th>
                  {user && hasPermission(user, LOAN_MODULE.STATUS_CHANGE) ? (
                    <th>{t('ACTION')}</th>
                  ) : (
                    ''
                  )}
                </tr>
              </TableHead>
              <tbody>
                {loansList &&
                  loansList.map((loan: any, index: any) => (
                    <TableBodyRow key={index}>
                      <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        {loan.loanNumber}
                      </td>
                     <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        
                        <div>
                          {loan.employeeName || 'Unknown'}
                          {user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS) && (
                            <div style={{ color: '#666', fontSize: '0.8em' }}>
                              {loan.employeeId}
                            </div>
                          )}
                        </div>
                      </td>
                      <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        {formatLoanType(loan.loanType)}
                      </td>
                      <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        <span
                          style={{
                            verticalAlign: 'middle',
                            marginRight: '6px',
                          }}
                        >
                          <CalenderIcon />
                        </span>

                        {loan.createdAt != null
                          ? formatDate(loan.createdAt)
                          : '-'}
                      </td>
                      <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        {loan.amount === 0 ? '-' : loan.amount + ' INR'}
                      </td>
                      <td
                        onClick={() => {
                          handleLoanToBePreviewed(loan);
                          handleIsLoanPreviewModalOpen();
                        }}
                      >
                        <StatusIndicator status={loan.status}>
                          {formatStatus(loan.status)}
                        </StatusIndicator>
                      </td>
                      {user &&
                      hasPermission(user, LOAN_MODULE.STATUS_CHANGE) ? (
                        <td>
                          <LoanAction
                            options={Actions}
                            currentLoan={loan}
                            fetchLoans={fetchLoans}
                          />
                        </td>
                      ) : (
                        ''
                      )}
                    </TableBodyRow>
                  ))}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </PayrollMainContainer>
      {loading && <SpinAnimation />}
      {loanToBePreviewed && isLoanPreviewModalOpen && (
        <CenterModalMain
          heading="Loan Preview"
          modalClose={handleIsLoanPreviewModalOpen}
          actualContentContainer={
            <LoanPreview
              handleClose={handleIsLoanPreviewModalOpen}
              loan={loanToBePreviewed}
            />
          }
        />
      )}
    </>
  );
};

export default LoanListView;
