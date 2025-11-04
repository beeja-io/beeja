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
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';
import { getAllPerformance } from '../service/axiosInstance';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import FeedbackReceiversList from './FeedbackReceiversList.screen'; // ✅ import your list screen

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
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const [selectedCycle, setSelectedCycle] = useState<PerformanceCycle | null>(
    null
  );

  useEffect(() => {
    const fetchPerformanceCycles = async () => {
      setIsLoading(true);
      try {
        const response = await getAllPerformance();
        setCycles(response.data);
      } catch (error) {
        toast.error('Failed to fetch performance cycles');
      } finally {
        setIsLoading(false);
      }
    };

    fetchPerformanceCycles();
  }, []);

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
                <ZeroEntriesFound heading="No Assign Feedback Providers" />
              ) : (
                <TableList>
                  <TableHead>
                    <tr>
                      <th>{t('Name')}</th>
                      <th>{t('Start_Date')}</th>
                      <th>{t('End_Date')}</th>
                      <th>{t('Status')}</th>
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
                      cycles?.map((cycle, index) => (
                        <TableBodyRow key={index}>
                          <td onClick={() => handleCycleClick(cycle)}>
                            {cycle?.name || '-'}
                          </td>
                          <td onClick={() => handleCycleClick(cycle)}>
                            {cycle?.startDate || '-'}
                          </td>
                          <td onClick={() => handleCycleClick(cycle)}>
                            {cycle?.endDate || '-'}
                          </td>
                          <td onClick={() => handleCycleClick(cycle)}>
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
          />
        )}
      </ExpenseManagementMainContainer>
    </>
  );
};

export default AssignFeedbackProviders;
