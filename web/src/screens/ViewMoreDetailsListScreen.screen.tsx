import { useEffect, useState, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';
import { ExpenseManagementMainContainer } from '../styles/ExpenseManagementStyles.style';
import {
  ExpenseHeading,
  StyledDiv,
  TableBodyRow,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import { ArrowDownSVG, LeftArrowSVG } from '../svgs/CommonSvgs.svs';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { getFeedbackProviderDetails } from '../service/axiosInstance';
import FeedbackStatusDropdown from '../styles/FeedbackStatusStyle.style';
import {
  ExpenseTitleProviders,
  ExpenseHeadingSection,
  Section,
  TableHead,
  TableCellStatus,
} from '../styles/AssignFeedbackReceiversProvidersStyle.style';
import { FeedbackStatus } from '.././utils/feedbackStatus';

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

  const { employeeId, cycleId, receiverName, formName } = (location.state ||
    {}) as {
    employeeId?: string;
    cycleId?: string;
    receiverName?: string;
    formName?: string;
  };

  const fetchFeedbackDetails = useCallback(async () => {
    if (!employeeId || !cycleId) {
      throw new Error('Missing employeeId or cycleId');
    }

    setIsLoading(true);
    setError('');

    try {
      const response = await getFeedbackProviderDetails(employeeId, cycleId);
      const data = Array.isArray(response?.data)
        ? response.data
        : response?.data?.assignedReviewers || [];

      const mappedProviders = data.map((item: any) => ({
        id: item.reviewerId || item.id,
        name: item.reviewerName || item.name || '-',
        designation: item.role || '',
        status: item.providerStatus || 'IN_PROGRESS',
        profileImage: item.profileImage || '',
      }));

      setProviders(mappedProviders);

      sessionStorage.setItem(
        `previousProviders_${employeeId}_${cycleId}`,
        JSON.stringify(mappedProviders)
      );
    } catch (err) {
      setError('Unable to fetch feedback details');
      toast.error('Unable to fetch feedback details');
      throw new Error(
        `Error fetching feedback details: ${
          err instanceof Error ? err.message : String(err)
        }`
      );
    } finally {
      setIsLoading(false);
    }
  }, [employeeId, cycleId]);

  useEffect(() => {
    fetchFeedbackDetails();
  }, [fetchFeedbackDetails]);

  const goToPreviousPage = () => {
    const { cycleId, fromReceiversList, fromReceiversListDirect } =
      location.state || {};

    if ((fromReceiversList || fromReceiversListDirect) && cycleId) {
      setTimeout(() => {
        navigate('/performance/assign-feedback-providers', {
          state: { openReceiversList: true, cycleId },
          replace: true,
        });
      }, 0);
    } else {
      navigate(-1);
    }
  };

  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span className="back-arrow" onClick={goToPreviousPage}>
            <ArrowDownSVG />
          </span>
          {t('Assign_Feedback_Receivers_Providers')}
          <span className="separator-form">
            <LeftArrowSVG />
          </span>

          {formName && (
            <>
              <span className="form-name">{formName}</span>
              <span className="separator-form">
                <LeftArrowSVG />
              </span>
            </>
          )}
          <span className="nav_AddClient">{t('View_More_Details')}</span>
        </span>
      </ExpenseHeadingSection>

      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitleProviders>
            {t('Feedback Providers')} {receiverName ? `of ${receiverName}` : ''}
            <p className="sub-text">
              The following are the feedback contributors
              {receiverName ? ` of ${receiverName}` : '.'}
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
                                <FeedbackStatusDropdown
                                  value={provider.status}
                                  options={FeedbackStatus}
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
