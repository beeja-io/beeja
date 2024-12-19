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
};
const LoanListView = (props: LoanListViewProps) => {
  const { user } = useUser();
  const [loading, setLoading] = useState(false);

  const { loanList, updateLoanList } = useContext(ApplicationContext);

  const fetchLoans = async () => {
    try {
      /* 
        If user is super admin or account manager, then user will see all loans
        or user will see only his loans
      */
      if (user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS)) {
        const res = await getAllLoans();
        updateLoanList(res.data.reverse());
      } else {
        if (user && user.employeeId) {
          const res = await getAllLoans(user.employeeId);
          updateLoanList(res.data.reverse());
        }
      }
    } catch (error) {
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (loanList === null || loanList === undefined) {
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
  const formatLoanType = (loanType: string): string =>
    loanType
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
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
              <h4>List of Loans</h4>
            </span>
          ) : (
            <span>
              <h4>My Loans</h4>
            </span>
          )}
          {user && hasPermission(user, LOAN_MODULE.CREATE_LOAN) && (
            <Button
              className="submit shadow"
              width="135px"
              height="40px"
              onClick={props.handleIsApplyLoanScreen}
            >
              Request Loan
            </Button>
          )}
        </section>
        <TableListContainer style={{ marginTop: 0 }}>
          {loanList && loanList.length === 0 ? (
            <ZeroEntriesFound
              heading="There's no Loan history found"
              message="You have never involved in any previous loan requests"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th>Loan Number</th>
                  <th>Loan Type</th>
                  {user && hasPermission(user, LOAN_MODULE.GET_ALL_LOANS) ? (
                    <th>Employee ID</th>
                  ) : (
                    ''
                  )}

                  <th>Requested Date</th>
                  <th>Loan Amount</th>
                  <th className="statusHeader">Status</th>
                  {user && hasPermission(user, LOAN_MODULE.STATUS_CHANGE) ? (
                    <th>Action</th>
                  ) : (
                    ''
                  )}
                </tr>
              </TableHead>
              <tbody>
                {loanList &&
                  loanList.map((loan, index) => (
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
                        {formatLoanType(loan.loanType)}
                      </td>
                      {user &&
                      hasPermission(user, LOAN_MODULE.GET_ALL_LOANS) ? (
                        <td
                          onClick={() => {
                            handleLoanToBePreviewed(loan);
                            handleIsLoanPreviewModalOpen();
                          }}
                        >
                          {loan.employeeId}
                        </td>
                      ) : (
                        ''
                      )}

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

                        {formatDate(loan.createdAt)}
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
