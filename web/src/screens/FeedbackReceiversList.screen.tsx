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

import {
  AssignUserSVG,
  DocumentTextSVG,
} from '../svgs/PerformanceEvaluation.Svgs.scg';
import AddFeedbackReceivers from '../components/reusableComponents/AddFeedbackReceivers.component';
import { getReceivers } from '../service/axiosInstance';
import FeedbackStatusDropdown from '../styles/FeedbackStatusStyle.style';
import { hasPermission } from '../utils/permissionCheck';
import { PERFORMANCE_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';

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
  formName?: string;
  isExpired?: boolean;
}

const FeedbackReceiversList: React.FC<FeedbackReceiversListProps> = ({
  cycleId,
  questionnaireId,
  onBack,
  formName,
  isExpired,
}) => {
  const { t } = useTranslation();
  const { user } = useUser();

  const [feedbackReceivers, setFeedbackReceivers] = useState<
    FeedbackReceiver[]
  >([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [selectedReceiver, setSelectedReceiver] =
    useState<FeedbackReceiver | null>(null);
  const isCycleExpired = isExpired;

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

  const canAssign =
    user && hasPermission(user, PERFORMANCE_MODULE.ASSIGN_RECEIVER);
  const canUpdate =
    user && hasPermission(user, PERFORMANCE_MODULE.UPDATE_RECEIVER);
  const canShowButton =
    !isLoading &&
    ((feedbackReceivers.length === 0 && canAssign) ||
      (feedbackReceivers.length > 0 && canUpdate));

  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span onClick={onBack}>
            <ArrowDownSVG />
          </span>
          {t('Assign_Feedback_Receivers_Providers')}
        </span>

        {canShowButton && (
          <Button
            className={`submit shadow ${isExpired ? 'disabled-action' : ''}`}
            disabled={isExpired}
            onClick={() => {
              if (isExpired) {
                toast.error('Evaluation period is completed');
                return;
              }
              handleAddFeedbackReceiver();
            }}
            width="216px"
          >
            <AddNewPlusSVG />
            {t('Add_Feedback_Receiver')}
          </Button>
        )}
      </ExpenseHeadingSection>

      <StyledDiv>
        <ExpenseHeadingFeedback>
          <ExpenseTitle>
            {t('Feedback_Receivers_List')}
            {formName ? ` : ${formName}` : ''}
          </ExpenseTitle>
        </ExpenseHeadingFeedback>

        <TableListContainer>
          {isLoading ? (
            <p style={{ textAlign: 'center', marginTop: '1rem' }}>
              {t('Loading...')}
            </p>
          ) : feedbackReceivers.length === 0 ? (
            <ZeroEntriesFound heading={t('FEEDBACK_RECEIVERS_NOT_YET_ADDED')} />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Employee_Name')}</th>
                  <th>{t('DEPARTMENT')}</th>
                  <th>{t('Feedback_Status')}</th>
                  <th>{t('Action')}</th>
                </tr>
              </TableHead>
              <tbody>
                {feedbackReceivers.map((receiver) => {
                  const rawStatus =
                    receiver.providerStatus?.toUpperCase() || 'NOT_ASSIGNED';

                  const uiStatus =
                    !isCycleExpired && rawStatus === 'COMPLETED'
                      ? 'IN_PROGRESS'
                      : rawStatus;

                  let feedbackActions;

                  if (isCycleExpired) {
                    feedbackActions = [
                      {
                        title:
                          uiStatus === 'NOT_ASSIGNED'
                            ? t('ASSIGN_FEEDBACK_PROVIDERS')
                            : t('REASSIGN_FEEDBACK_PROVIDERS'),
                        svg: <AssignUserSVG />,
                        disabled: true,
                      },
                      {
                        title: t('View_More_Details'),
                        svg: <DocumentTextSVG />,
                        disabled: false,
                      },
                    ];
                  } else {
                    feedbackActions =
                      uiStatus === 'NOT_ASSIGNED'
                        ? [
                            {
                              title: t('ASSIGN_FEEDBACK_PROVIDERS'),
                              svg: <AssignUserSVG />,
                              disabled: false,
                            },
                            {
                              title: t('View_More_Details'),
                              svg: <DocumentTextSVG />,
                              disabled: false,
                            },
                          ]
                        : [
                            {
                              title: t('REASSIGN_FEEDBACK_PROVIDERS'),
                              svg: <AssignUserSVG />,
                              disabled: false,
                            },
                            {
                              title: t('View_More_Details'),
                              svg: <DocumentTextSVG />,
                              disabled: false,
                            },
                          ];
                  }

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

                      <td className={getStatusClass(rawStatus)}>
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
