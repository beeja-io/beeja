import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  ExpenseTitle,
  StyledDiv,
  TableListContainer,
  TableList,
  TableHead,
  TableBodyRow,
} from '../styles/ExpenseListStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { Button } from '../styles/CommonStyles.style';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';

import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { toast } from 'sonner';
import {
  ExpenseHeadingFeedback,
  ModalOverlay,
  ModalContainer,
  ProfileCell,
} from '../styles/AssignFeedbackReceiversProvidersStyle.style';
import FeedbackProviderAction from '../components/reusableComponents/FeedbackProviderAction';
import { FormFileIcon } from '../svgs/DocumentTabSvgs.svg';
import { AssignUserSVG } from '../svgs/PerformanceEvaluation.Svgs.scg';
import AddFeedbackReceivers from '../components/reusableComponents/AddFeedbackReceivers.component';
import { getReceivers } from '../service/axiosInstance';
import FeedbackStatusDropdown from '../styles/FeedbackStatusStyle.style';

type FeedbackReceiver = {
  id: string | undefined;
  employeeId: string;
  fullName: string;
  email: string;
  department: string;
  providerStatus: string;
  profileImage?: string;
};

interface FeedbackReceiversListProps {
  cycleId?: string;
  questionnaireId?: string;
  refresh?: number;
  onBack?: () => void;
  receiverName?: string;
}

const FeedbackReceiversList: React.FC<FeedbackReceiversListProps> = ({
  cycleId,
  questionnaireId,
  onBack,
}) => {
  const { t } = useTranslation();

  const [feedbackReceivers, setFeedbackReceivers] = useState<
    FeedbackReceiver[]
  >([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [selectedReceiver, setSelectedReceiver] =
    useState<FeedbackReceiver | null>(null);

  const fetchFeedbackReceivers = async () => {
    if (!cycleId || !questionnaireId) {
      throw new Error(
        `Missing IDs for fetchReceivers: cycleId=${cycleId}, questionnaireId=${questionnaireId}`
      );
    }

    try {
      setIsLoading(true);
      const response = await getReceivers(cycleId, questionnaireId);
      const data = Array.isArray(response.data.receivers)
        ? response.data.receivers
        : [];
      setFeedbackReceivers(data);
    } catch (error) {
      toast.error('Failed to load feedback receivers');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (cycleId && questionnaireId) {
      fetchFeedbackReceivers();
    } else {
      throw new Error('Missing cycleId or questionnaireId');
    }
  }, [cycleId, questionnaireId]);

  const handleAddFeedbackReceiver = () => {
    if (!cycleId) {
      toast.error('Cycle ID missing — please open from a valid cycle');
      return;
    }
    setIsAssignModalOpen(true);
  };

  const openAssignModal = (receiver?: FeedbackReceiver) => {
    setSelectedReceiver(receiver || null);
    setIsAssignModalOpen(true);
  };

  const closeAssignModal = () => {
    setSelectedReceiver(null);
    setIsAssignModalOpen(false);
  };

  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span onClick={onBack}>
            <ArrowDownSVG />
          </span>
          {t('Assign_Feedback_Receivers_Providers')}
        </span>

        <Button
          className="submit shadow"
          onClick={handleAddFeedbackReceiver}
          width="216px"
        >
          <AddNewPlusSVG />
          {t('Add_Feedback_Receiver')}
        </Button>
      </ExpenseHeadingSection>

      <StyledDiv>
        <ExpenseHeadingFeedback>
          <ExpenseTitle>{t('Feedback_Receivers_List')}</ExpenseTitle>
        </ExpenseHeadingFeedback>

        <TableListContainer>
          {isLoading ? (
            <p style={{ textAlign: 'center', marginTop: '1rem' }}>
              {t('Loading...')}
            </p>
          ) : feedbackReceivers.length === 0 ? (
            <ZeroEntriesFound heading="Feedback receivers not yet added" />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Employee_Name')}</th>
                  <th>{t('Department')}</th>
                  <th>{t('Feedback_Status')}</th>
                  <th>{t('Action')}</th>
                </tr>
              </TableHead>
              <tbody>
                {feedbackReceivers.map((receiver) => {
                  const normalizedStatus =
                    receiver.providerStatus?.toUpperCase() || 'NOT_ASSIGNED';

                  const feedbackActions =
                    normalizedStatus === 'IN_PROGRESS'
                      ? [
                          {
                            title: 'Reassign Feedback Providers',
                            svg: <AssignUserSVG />,
                          },
                          { title: 'View More Details', svg: <FormFileIcon /> },
                        ]
                      : [
                          {
                            title: 'Assign Feedback Providers',
                            svg: <AssignUserSVG />,
                          },
                          { title: 'View More Details', svg: <FormFileIcon /> },
                        ];

                  const getStatusClass = (status: any) => {
                    switch (status) {
                      case 'IN_PROGRESS':
                        return 'in-progress';
                      case 'COMPLETED':
                        return 'completed';
                      case 'NOT_ASSIGNED':
                      default:
                        return 'not-assigned';
                    }
                  };

                  return (
                    <TableBodyRow key={receiver.employeeId}>
                      <td>
                        <ProfileCell>
                          {receiver.profileImage && (
                            <img
                              src={receiver.profileImage}
                              alt={receiver.fullName}
                              className="profile-img"
                            />
                          )}
                          <div>
                            <div className="name">{receiver.fullName}</div>
                            <div className="email">{receiver.email}</div>
                          </div>
                        </ProfileCell>
                      </td>

                      <td>{receiver.department}</td>

                      <td className={getStatusClass(normalizedStatus)}>
                        {receiver?.providerStatus ? (
                          <FeedbackStatusDropdown
                            value={receiver.providerStatus}
                            disabled
                          />
                        ) : (
                          '-'
                        )}
                      </td>
                      <td>
                        <FeedbackProviderAction
                          options={feedbackActions}
                          currentEmployee={{ ...receiver, cycleId }}
                          handleAssign={() => openAssignModal(receiver)}
                        />
                      </td>
                    </TableBodyRow>
                  );
                })}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>

      {isAssignModalOpen && (
        <ModalOverlay>
          <ModalContainer>
            <AddFeedbackReceivers
              onClose={closeAssignModal}
              onSuccess={fetchFeedbackReceivers}
              cycleId={cycleId}
              questionnaireId={questionnaireId}
              selectedReceiver={selectedReceiver}
              mode={selectedReceiver ? 'provider' : 'receiver'}
              providerRole="Manager"
            />
          </ModalContainer>
        </ModalOverlay>
      )}
    </ExpenseManagementMainContainer>
  );
};

export default FeedbackReceiversList;
