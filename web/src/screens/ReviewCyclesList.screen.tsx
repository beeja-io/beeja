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
import { useTranslation } from 'react-i18next';
import { DeleteIcon, EditIcon } from '../svgs/ExpenseListSvgs.svg';
import { getAllPerformance } from '../service/axiosInstance';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import EvaluationListAction from '../components/reusableComponents/EvaluationListAction';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { isBefore, startOfDay } from 'date-fns';

type PerformanceCycle = {
  id: string;
  name: string;
  type: string;
  startDate: string;
  feedbackDeadline: string;
  status?: string;
};

const ReviewCyclesList = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [cycles, setCycles] = useState<PerformanceCycle[]>([]);
  const [activeActionId, setActiveActionId] = useState<string | null>(null);
  const { t } = useTranslation();
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [successToastMessage, setSuccessToastMessage] = useState({
    heading: '',
    body: '',
  });
  const [errorToastMessage, setErrorToastMessage] = useState({
    heading: '',
    body: '',
  });

  const fetchPerformanceCycles = async () => {
    setIsLoading(true);
    try {
      const response = await getAllPerformance();
      setCycles(response.data);
    } catch (error) {
      throw new Error('Failed to load review cycle details');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPerformanceCycles();
  }, []);

  const Actions = [
    { title: 'Edit', svg: <EditIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];

  const isCycleExpired = (cycle: any) => {
    if (!cycle?.feedbackDeadline) return false;

    const today = startOfDay(new Date());
    const endDate = startOfDay(new Date(cycle.feedbackDeadline));

    return isBefore(endDate, today);
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('List of forms')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && cycles.length === 0 ? (
            <ZeroEntriesFound heading="No Evaluation Cycles Found" />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Name')}</th>
                  <th>{t('Evaluation Start Date')}</th>
                  <th>{t('Evaluation End date')}</th>
                  <th>{t('Evaluation Status')}</th>
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
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))}
                  </>
                ) : (
                  cycles
                    ?.slice()
                    .sort((a, b) => b.id.localeCompare(a.id))
                    .map((cycle) => {
                      const expired = isCycleExpired(cycle);
                      return (
                        <TableBodyRow
                          key={cycle.id}
                          className={expired ? 'disabled-row' : ''}
                        >
                          <td>{cycle?.name || '-'}</td>
                          <td>
                            {cycle?.startDate
                              ? new Date(cycle.startDate).toLocaleDateString(
                                  'en-GB',
                                  {
                                    day: '2-digit',
                                    month: '2-digit',
                                    year: 'numeric',
                                  }
                                )
                              : '-'}
                          </td>
                          <td>
                            {cycle?.feedbackDeadline
                              ? new Date(
                                  cycle.feedbackDeadline
                                ).toLocaleDateString('en-GB', {
                                  day: '2-digit',
                                  month: '2-digit',
                                  year: 'numeric',
                                })
                              : '-'}
                          </td>
                          <td>
                            {cycle?.status ? (
                              <StatusDropdown value={cycle.status} disabled />
                            ) : (
                              '-'
                            )}
                          </td>
                          <td>
                            <EvaluationListAction
                              options={Actions}
                              currentCycle={cycle}
                              setCycles={setCycles}
                              isOpen={activeActionId === cycle.id}
                              onToggle={() =>
                                setActiveActionId((prev) =>
                                  prev === cycle.id ? null : cycle.id
                                )
                              }
                              fetchCycles={fetchPerformanceCycles}
                              onSuccess={(msg) => {
                                setSuccessToastMessage({
                                  heading: 'Form Deleted Successfully',
                                  body: msg,
                                });
                                setShowSuccessMessage(true);
                              }}
                              onError={(msg) => {
                                setErrorToastMessage({
                                  heading: 'Error',
                                  body: msg,
                                });
                                setShowSuccessMessage(true);
                              }}
                              disabled={expired}
                            />
                          </td>
                        </TableBodyRow>
                      );
                    })
                )}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>
      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageHeading={successToastMessage.heading}
          messageBody={successToastMessage.body}
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}

      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageHeading={errorToastMessage.heading || 'Error'}
          messageBody={errorToastMessage.body || 'Something went wrong.'}
          handleClose={() => setShowErrorMessage(false)}
        />
      )}
    </>
  );
};

export default ReviewCyclesList;
