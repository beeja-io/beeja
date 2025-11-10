import { useEffect, useState, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import {
  ExpenseHeading,
  StyledDiv,
  TableBodyRow,
  // TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { getFeedbackProviderDetails } from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import {
  ExpenseTitleProviders,
  Section,
  TableHead,
  TableCellStatus,
} from '../styles/AssignFeedbackReceiversProvidersStyle.style';

type FeedbackProvider = {
  role?: string;
  id: string;
  name: string;
  designation?: string;
  status: string;
  profileImage?: string;
};

const ViewMoreDetails = () => {
  const [providers, setProviders] = useState<FeedbackProvider[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();

  const { employeeId, cycleId, receiverName } = (location.state || {}) as {
    employeeId?: string;
    cycleId?: string;
    receiverName?: string;
  };

  const fetchFeedbackDetails = useCallback(async () => {
    if (!employeeId || !cycleId) {
      throw new Error(
        `Missing employeeId or cycleId — skipping fetch (employeeId=${employeeId}, cycleId=${cycleId})`
      );
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await getFeedbackProviderDetails(employeeId, cycleId);
      const data = Array.isArray(response?.data)
        ? response.data
        : response?.data?.assignedReviewers || [];

      setProviders(
        data.map((item: any) => ({
          id: item.reviewerId || item.id,
          name: item.reviewerName || item.name || '-',
          designation: item.role || '',
          status: item.status || 'IN_PROGRESS',
          profileImage: item.profileImage || '',
        }))
      );
    } catch (err) {
      setError('Unable to fetch feedback details');
      toast.error('Unable to fetch feedback details');
    } finally {
      setIsLoading(false);
    }
  }, [employeeId, cycleId]);

  useEffect(() => {
    fetchFeedbackDetails();
  }, [fetchFeedbackDetails]);

  const goToPreviousPage = () => navigate(-1);

  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span onClick={goToPreviousPage}>
            <ArrowDownSVG />
          </span>
          {t('Assign_Feedback_Receivers_Providers')}
          <span className="separator"> {'>'} </span>
          <span className="nav_AddClient">{t('View_More_Details')}</span>
        </span>
      </ExpenseHeadingSection>

      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitleProviders>
            {t('Feedback Providers')} {receiverName ? `of ${receiverName}` : ''}
            <p className="sub-text">
              The following are the feedback contributors
              {receiverName ? ` of ${receiverName.toLowerCase()}.` : '.'}
            </p>
          </ExpenseTitleProviders>
        </ExpenseHeading>
        <Section>
          <TableListContainer>
            {error ? (
              <ZeroEntriesFound heading={error} />
            ) : !isLoading && providers.length === 0 ? (
              <ZeroEntriesFound heading="No Feedback Providers Found" />
            ) : (
              <TableList>
                <TableHead>
                  <tr>
                    <th>{t('Name')}</th>
                    <th>{t('Status')}</th>
                  </tr>
                </TableHead>
                <tbody>
                  {isLoading
                    ? [...Array(5).keys()].map((index) => (
                        <TableBodyRow key={index}>
                          <td colSpan={2}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        </TableBodyRow>
                      ))
                    : providers.map((provider, index) => (
                        <TableBodyRow key={index}>
                          <td>
                            <div className="profile-cell">
                              <div>
                                <div className="name">{provider.name}</div>
                              </div>
                            </div>
                          </td>
                          <td>
                            <TableCellStatus>
                              <div className="status-container">
                                <span className="status-label"></span>
                                <StatusDropdown
                                  value={provider.status}
                                  disabled
                                />
                              </div>
                            </TableCellStatus>
                          </td>
                        </TableBodyRow>
                      ))}
                </tbody>
              </TableList>
            )}
          </TableListContainer>
        </Section>
      </StyledDiv>
    </ExpenseManagementMainContainer>
  );
};

export default ViewMoreDetails;
