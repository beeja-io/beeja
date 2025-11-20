import { useLocation, useNavigate } from 'react-router-dom';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useEffect, useState } from 'react';
import {
  ExpenseHeading,
  ExpenseTitle,
  StyledDiv,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import {
  TableHead,
  TableBodyRow,
} from '../styles/AssignFeedbackReceiversProvidersStyle.style';
import { useTranslation } from 'react-i18next';
import { getAllPerformance } from '../service/axiosInstance';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import FeedbackReceiversList from './FeedbackReceiversList.screen';
import { endOfDay, isBefore, parseISO } from 'date-fns';

type PerformanceCycle = {
  questionnaireId: string;
  id: string;
  name: string;
  type: string;
  startDate: string;
  endDate: string;
  status?: string;
};

const AssignFeedbackProviders = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [cycles, setCycles] = useState<PerformanceCycle[]>([]);
  const [selectedCycle, setSelectedCycle] = useState<PerformanceCycle | null>(
    null
  );

  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const isCycleExpired = (endDate: string) => {
    if (!endDate) return false;

    const today = new Date();
    const end = endOfDay(parseISO(endDate));

    return isBefore(end, today);
  };

  useEffect(() => {
    const fetchPerformanceCycles = async () => {
      setIsLoading(true);
      try {
        const response = await getAllPerformance();
        setCycles(response.data);

        if (location.state?.openReceiversList && location.state?.cycleId) {
          const matchedCycle = response.data.find(
            (c: any) => c.id === location.state.cycleId
          );
          if (matchedCycle) setSelectedCycle(matchedCycle);
        }
      } catch (error) {
        throw new Error('Failed to load review cycle details');
      } finally {
        setIsLoading(false);
      }
    };

    fetchPerformanceCycles();
  }, [location.state]);

  const handleCycleClick = (cycle: PerformanceCycle) => {
    setSelectedCycle(cycle);
  };

  const goToPreviousPage = () => {
    if (
      location.pathname.endsWith('/new') ||
      location.pathname.match(/\/\d+$/)
    ) {
      navigate('/performance/create-evaluation-form');
    } else {
      navigate(-1);
    }
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        {!selectedCycle && (
          <ExpenseHeadingSection>
            <span className="heading">
              <span onClick={goToPreviousPage}>
                <ArrowDownSVG />
              </span>
              {t('Assign_Feedback_Receivers_Providers')}
            </span>
          </ExpenseHeadingSection>
        )}

        {!selectedCycle ? (
          <StyledDiv>
            <ExpenseHeading>
              <ExpenseTitle>{t('List_of_forms')}</ExpenseTitle>
            </ExpenseHeading>

            <TableListContainer>
              {!isLoading && cycles.length === 0 ? (
                <ZeroEntriesFound heading="No Review Cycle forms available to Assign Feedback Receivers" />
              ) : (
                <TableList>
                  <TableHead>
                    <tr className="table-header">
                      <th>{t('Name')}</th>
                      <th>{t('Start_Date')}</th>
                      <th>{t('End_Date')}</th>
                      <th className="status-container">{t('Status')}</th>
                    </tr>
                  </TableHead>
                  <tbody>
                    {isLoading ? (
                      <>
                        {[...Array(6).keys()]?.map((rowIndex) => (
                          <TableBodyRow key={rowIndex}>
                            {[...Array(5).keys()]?.map((cellIndex) => (
                              <td key={cellIndex}>
                                <div className="skeleton skeleton-text">
                                  &nbsp;
                                </div>
                              </td>
                            ))}
                          </TableBodyRow>
                        ))}
                      </>
                    ) : (
                      cycles
                        ?.slice()
                        .sort((a, b) => b.id.localeCompare(a.id))
                        .map((cycle, index) => (
                          <TableBodyRow key={index}>
                            <td onClick={() => handleCycleClick(cycle)}>
                              {cycle?.name || '-'}
                            </td>
                            <td onClick={() => handleCycleClick(cycle)}>
                              <td>
                                {cycle?.startDate
                                  ? new Date(
                                      cycle.startDate
                                    ).toLocaleDateString('en-GB', {
                                      month: '2-digit',
                                      day: '2-digit',
                                      year: 'numeric',
                                    })
                                  : '-'}
                              </td>
                            </td>
                            <td onClick={() => handleCycleClick(cycle)}>
                              {/* {cycle?.endDate || '-'} */}
                              {cycle?.endDate
                                ? new Date(cycle.endDate).toLocaleDateString(
                                    'en-GB',
                                    {
                                      month: '2-digit',
                                      day: '2-digit',
                                      year: 'numeric',
                                    }
                                  )
                                : '-'}
                            </td>
                            <td
                              className="status-row"
                              onClick={() => handleCycleClick(cycle)}
                            >
                              {cycle?.status ? (
                                <StatusDropdown value={cycle.status} disabled />
                              ) : (
                                '-'
                              )}
                            </td>
                          </TableBodyRow>
                        ))
                    )}
                  </tbody>
                </TableList>
              )}
            </TableListContainer>
          </StyledDiv>
        ) : (
          <FeedbackReceiversList
            cycleId={selectedCycle.id}
            questionnaireId={selectedCycle.questionnaireId}
            formName={selectedCycle.name}
            onBack={() => setSelectedCycle(null)}
            isExpired={isCycleExpired(selectedCycle.endDate)}
          />
        )}
      </ExpenseManagementMainContainer>
    </>
  );
};

export default AssignFeedbackProviders;
