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
import { DeleteIcon, EditIcon } from '../svgs/ExpenseListSvgs.svg';
import { getAllPerformance } from '../service/axiosInstance';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import EvaluationListAction from '../components/reusableComponents/EvaluationListAction';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

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
  const [successToastMessage, setSuccessToastMessage] = useState({
    heading: '',
    body: '',
  });

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

  useEffect(() => {
    fetchPerformanceCycles();
  }, []);

  const Actions = [
    { title: 'Edit', svg: <EditIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];

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
                    .map((cycle) => (
                      <TableBodyRow key={cycle.id}>
                        <td>{cycle?.name || '-'}</td>
                        <td>
                          {cycle?.startDate
                            ? new Date(cycle.startDate).toLocaleDateString(
                                'en-US',
                                {
                                  month: '2-digit',
                                  day: '2-digit',
                                  year: 'numeric',
                                }
                              )
                            : '-'}
                        </td>
                        <td>
                          {cycle?.feedbackDeadline
                            ? new Date(
                                cycle.feedbackDeadline
                              ).toLocaleDateString('en-US', {
                                month: '2-digit',
                                day: '2-digit',
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
                            isOpen={activeActionId === cycle.id}
                            onToggle={() =>
                              setActiveActionId((prev) =>
                                prev === cycle.id ? null : cycle.id
                              )
                            }
                            fetchCycles={fetchPerformanceCycles}
                            onSuccess={(msg) => {
                              setSuccessToastMessage({
                                heading: 'Success',
                                body: msg,
                              });
                              setShowSuccessMessage(true);
                            }}
                            onError={(msg) => {
                              setSuccessToastMessage({
                                heading: 'Error',
                                body: msg,
                              });
                              setShowSuccessMessage(true);
                            }}
                          />
                        </td>
                      </TableBodyRow>
                    ))
                )}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>
      {showSuccessMessage && (
        <ToastMessage
          messageType={
            successToastMessage.heading === 'Success' ? 'success' : 'error'
          }
          messageHeading={successToastMessage.heading}
          messageBody={successToastMessage.body}
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default ReviewCyclesList;
