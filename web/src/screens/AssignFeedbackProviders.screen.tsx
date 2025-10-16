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

type PerformanceCycle = {
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
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Assign Feedback Providers')}
          </span>
        </ExpenseHeadingSection>

        <>
          <StyledDiv>
            <ExpenseHeading>
              <ExpenseTitle>{t('List of forms')}</ExpenseTitle>
            </ExpenseHeading>

            <TableListContainer style={{ marginTop: 0 }}>
              {!isLoading && cycles.length === 0 ? (
                <ZeroEntriesFound heading="No Assign Feedback Providers" />
              ) : (
                <TableList>
                  <TableHead>
                    <tr>
                      <th>{t('Name')}</th>
                      <th>{t('Start date')}</th>
                      <th>{t('End date')}</th>
                      <th>{t('Status')}</th>
                      <th>{t('ACTION')}</th>
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
                      cycles?.map((cycle) => (
                        <TableBodyRow key={cycle.id}>
                          <td>{cycle?.name || '-'}</td>
                          <td>{cycle?.startDate || '-'}</td>
                          <td>{cycle?.endDate || '-'}</td>
                          {cycle?.status ? (
                            <StatusDropdown value={cycle.status} disabled />
                          ) : (
                            '-'
                          )}
                          <td>Details</td>
                        </TableBodyRow>
                      ))
                    )}
                  </tbody>
                </TableList>
              )}
            </TableListContainer>
          </StyledDiv>
        </>
      </ExpenseManagementMainContainer>
    </>
  );
};

export default AssignFeedbackProviders;
