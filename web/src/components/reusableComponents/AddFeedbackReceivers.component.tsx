import { useState, useEffect, useCallback, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { matchPath, useNavigate, useLocation } from 'react-router-dom';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../../styles/ExpenseManagementStyles.style';
import { ExpenseTitle, StyledDiv } from '../../styles/ExpenseListStyles.style';
import {
  FeedbackCard,
  Section,
  SectionTitle,
  Divider,
  FooterContainer,
  ButtonGroup,
  ExpenseHeadingFeedback,
} from '../../styles/AssignFeedbackReceiversProvidersStyle.style';
import { Button } from '../../styles/CommonStyles.style';
import { AddNewPlusSVG } from '../../svgs/EmployeeListSvgs.svg';
import { SearchSVG } from '../../svgs/NavBarSvgs.svg';
import { PreviewWrapper } from '../../styles/CreateReviewCycleStyle.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import {
  searchUsers,
  createReceiverList,
  getReceivers,
  getProviders,
  assignProvider,
  updateFeedbackProviders,
} from '../../service/axiosInstance';
import { toast } from 'sonner';
import { TickmarkIcon } from '../../svgs/DocumentTabSvgs.svg';
import ToastMessage from './ToastMessage.component';
import { ValidationText } from '../../styles/DocumentTabStyles.style';

type FeedbackReceiver = {
  employeeId: string;
  fullName: string;
  department: string | null;
  email: string | null;
  providerStatus?: string;
};

type FeedbackUser = {
  employeeId: string | number;
  fullName: string;
  department: any;
  id: string | number;
  name: string;
  role: string;
  email: string;
};

interface AddFeedbackReceiversProps {
  mode?: 'receiver' | 'provider';
  cycleId?: string;
  questionnaireId?: string;
  onClose?: () => void;
  onSuccess?: () => void;
  selectedReceiver?: FeedbackReceiver | null;
  providerRole?: string;
}

const AddFeedbackReceivers: React.FC<AddFeedbackReceiversProps> = ({
  mode = 'receiver',
  onClose,
  onSuccess,
  cycleId,
  questionnaireId,
  selectedReceiver,
  providerRole,
}) => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();

  const [employees, setEmployees] = useState<FeedbackUser[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selected, setSelected] = useState<FeedbackUser[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [fromReceiversList, setList] = useState<any[]>([]);
  const isProvidersPage = mode === 'provider';

  const [showErrorToast, setShowErrorToast] = useState(false);
  const [errorToastMessage] = useState('');

  const [previousProviders, setPreviousProviders] = useState<any[]>([]);
  const [searchCompleted, setSearchCompleted] = useState(false);

  const [toastData, setToastData] = useState<{
    type: 'success' | 'error' | null;
    heading?: string;
    body?: string;
  }>({ type: null });

  useEffect(() => {
    if (isProvidersPage && selectedReceiver?.employeeId && cycleId) {
      const stored = sessionStorage.getItem(
        `previousProviders_${selectedReceiver.employeeId}_${cycleId}`
      );
      if (stored) {
        const parsed = JSON.parse(stored);
        setPreviousProviders(parsed);
      }
    }
  }, [isProvidersPage, selectedReceiver, cycleId]);

  const handleAssignProviders = async () => {
    try {
      if (!isProvidersPage) return;

      if (!selectedReceiver?.employeeId) {
        toast.error('Receiver details missing');
        return;
      }

      if (!cycleId || !questionnaireId) {
        toast.error('Cycle ID or Questionnaire ID missing');
        return;
      }

      if (selected.length === 0) {
        setToastData({
          type: 'error',
          heading: 'Error',
          body: t('Please select at least one provider.'),
        });
        return;
      }

      const duplicateProvider = selected.find((emp) =>
        fromReceiversList.some(
          (existing) =>
            existing.reviewerId === emp.id ||
            existing.employeeId === emp.id ||
            existing.id === emp.id
        )
      );

      if (duplicateProvider) {
        setToastData({
          type: 'error',
          heading: 'Duplicate Provider',
          body: t('This employee is already assigned as a feedback provider.'),
        });
        return;
      }

      const storedProvidersKey = `previousProviders_${selectedReceiver.employeeId}_${cycleId}`;
      const oldProviders =
        JSON.parse(sessionStorage.getItem(storedProvidersKey) || '[]') || [];

      const oldReviewerList = oldProviders.map((prov: any) => ({
        reviewerId: prov.id?.toString(),
        role: prov.role || prov.designation || 'Reviewer',
        status: prov.status || 'IN_PROGRESS',
      }));

      const newReviewerList = selected.map((emp) => ({
        reviewerId: emp.id?.toString(),
        role: providerRole || emp.role || 'Reviewer',
        status: 'IN_PROGRESS',
      }));

      const mergedReviewers = [
        ...oldReviewerList,
        ...newReviewerList.filter(
          (newProv) =>
            !oldReviewerList.some(
              (oldProv: any) => oldProv.reviewerId === newProv.reviewerId
            )
        ),
      ];

      const payload = {
        cycleId,
        questionnaireId,
        assignedReviewers: mergedReviewers,
      };
      if (selectedReceiver.providerStatus === 'NOT_ASSIGNED') {
        await assignProvider(selectedReceiver.employeeId, payload);
        setToastData({
          type: 'success',
          heading: t('Providers Assigned'),
          body: t('The feedback providers have been assigned successfully.'),
        });
      } else if (
        selectedReceiver.providerStatus === 'IN_PROGRESS' ||
        selectedReceiver.providerStatus === 'COMPLETED'
      ) {
        await updateFeedbackProviders(selectedReceiver.employeeId, payload);
        setToastData({
          type: 'success',
          heading: t('Providers Updated'),
          body: t('The feedback providers have been updated successfully.'),
        });
      }

      setTimeout(() => {
        onSuccess?.();
        onClose?.();
      }, 2000);
    } catch (err) {
      setToastData({
        type: 'error',
        heading: 'Error',
        body: t('Something went wrong while assigning providers.'),
      });
    }
  };

  const handleCreateReceivers = async () => {
    if (!cycleId || !questionnaireId) {
      toast.error('Cycle ID or Questionnaire ID missing');
      return;
    }
    if (selected.length === 0) {
      setToastData({
        type: 'error',
        heading: 'Error',
        body: t('Please select at least one receiver.'),
      });
      return;
    }

    const duplicateReceiver = selected.find((emp) =>
      fromReceiversList.some((existing) => existing.employeeId === emp.id)
    );

    if (duplicateReceiver) {
      setToastData({
        type: 'error',
        heading: 'Duplicate Receiver',
        body: t('This employee is already assigned as a feedback receiver.'),
      });
      return;
    }

    const payload = {
      cycleId,
      questionnaireId,
      receiverDetails: selected.map((emp) => ({
        employeeId: emp.id,
        fullName: emp.name,
        department: emp.department,
        designation: emp.role,
        email: emp.email,
      })),
    };

    try {
      await createReceiverList(payload);
      setToastData({
        type: 'success',
        heading: 'Receivers Added',
        body: t('The feedback receivers have been added successfully.'),
      });

      setTimeout(() => {
        onSuccess?.();
        onClose?.();
      }, 2000);
    } catch (error) {
      setToastData({
        type: 'error',
        heading: 'Error',
        body: t('Something went wrong while adding receivers.'),
      });
    }
  };

  const fetchUsers = useCallback(
    async (keyword: string = '') => {
      if (keyword.trim().length === 0) {
        setSearchCompleted(false);
        setEmployees([]);
        return;
      }

      setLoading(true);
      setSearchCompleted(false);

      try {
        const response = await searchUsers(keyword);
        const employeeList = response?.data || [];

        const formatted = employeeList
          .map((emp: any) => ({
            id: emp.employeeId,
            name: emp.fullName,
            role: emp.designation || '—',
            department: emp.department || '',
            email: emp.email || '',
          }))
          .filter((emp: { name: string }) =>
            emp.name?.toLowerCase().includes(keyword.toLowerCase())
          );

        if (keyword === searchTerm.trim()) {
          setEmployees(formatted);
          setSearchCompleted(true);
          setError(null);
        }
      } catch (err) {
        setError('Error while fetching employees');
        setEmployees([]);
        setSearchCompleted(true);
      } finally {
        setLoading(false);
      }
    },
    [searchTerm]
  );

  useEffect(() => {
    if (searchTerm.trim().length === 0) {
      setEmployees([]);
      return;
    }

    const delayDebounce = setTimeout(() => {
      fetchUsers(searchTerm);
    }, 300);
    return () => clearTimeout(delayDebounce);
  }, [searchTerm, fetchUsers]);

  // const handleSearchClick = () => fetchUsers(searchTerm);
  const handleSearchClick = () => {
    inputRef.current?.focus();
    fetchUsers(searchTerm);
  };
  const handleAdd = (emp: FeedbackUser) => {
    setSelected((prev) => {
      if (prev.some((e) => e.id === emp.id)) return prev;
      return [...prev, emp];
    });
    setSearchTerm('');
  };

  const handleRemove = (id: string | number) => {
    setSelected(selected.filter((emp) => emp.id !== id));
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

  const handleOpenModal = () => setIsCreateModalOpen(true);

  useEffect(() => {
    if (!cycleId || !questionnaireId) return;

    const fetchData = async () => {
      try {
        if (isProvidersPage && selectedReceiver?.employeeId) {
          const res = await getProviders(selectedReceiver.employeeId);
          const providerList = res.data?.providers || [];
          setList(providerList);
          if (providerList.length > 0) {
            setPreviousProviders(providerList);
            sessionStorage.setItem(
              `previousProviders_${selectedReceiver.employeeId}_${cycleId}`,
              JSON.stringify(providerList)
            );
          }
        } else {
          const res = await getReceivers(cycleId, questionnaireId);
          const receiverList = res.data?.receivers || [];
          setList(receiverList);
        }
      } catch (err) {
        throw new Error('Error loading existing list');
      }
    };

    fetchData();
  }, [isProvidersPage, selectedReceiver, cycleId, questionnaireId]);

  const isViewMoreDetailsRoute = matchPath(
    '/performance/view-more-details/:employeeId/:cycleId',
    location.pathname
  );

  const handleAssignFeedbackProvider = () => {
    navigate(
      `/performance/assign-feedback-providers/${cycleId}/add-feedback-receiver`
    );
  };

  useEffect(() => {
    document.body.style.overflow = 'hidden';

    return () => {
      document.body.style.overflow = '';
    };
  }, []);
  const inputRef = useRef<HTMLInputElement | null>(null);
  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span onClick={goToPreviousPage}>
            <ArrowDownSVG />
          </span>
          {isProvidersPage
            ? t('Assign_Feedback_Providers')
            : t('Assign_Feedback_Receivers')}
        </span>
        {isViewMoreDetailsRoute && (
          <>
            <span className="separator"> {'>'} </span>
            <span className="nav_ViewMoreDetails">
              {t('View_More_Details')}
            </span>
          </>
        )}
        {!isCreateModalOpen && (
          <Button
            className="submit shadow"
            onClick={
              isProvidersPage ? handleAssignFeedbackProvider : handleOpenModal
            }
            width="216px"
          >
            <AddNewPlusSVG />
            {isProvidersPage
              ? t('Assign_Feedback_Provider')
              : t('Add_Feedback_Receiver')}
          </Button>
        )}
      </ExpenseHeadingSection>

      <StyledDiv>
        <PreviewWrapper>
          <FeedbackCard>
            <ExpenseHeadingFeedback>
              <ExpenseTitle>
                {isProvidersPage
                  ? t('Select_Assign_Feedback_Providers')
                  : t('Select_Add_Feedback_Receivers')}
              </ExpenseTitle>
            </ExpenseHeadingFeedback>

            <Section>
              <SectionTitle>
                {isProvidersPage
                  ? t('Select_Feedback_Providers')
                  : t('Select_Feedback_Receivers')}
                <ValidationText className="star">*</ValidationText>
              </SectionTitle>
              <div className="search-container">
                <input
                  ref={inputRef}
                  type="text"
                  className="search-input"
                  placeholder="Search by Employee Name"
                  value={searchTerm}
                  onChange={(e) => {
                    const value = e.target.value;
                    const formattedValue = value
                      .toLowerCase()
                      .replace(/\b\w/g, (char) => char.toUpperCase());
                    setSearchTerm(formattedValue);
                  }}
                />
                <span className="search-icon" onClick={handleSearchClick}>
                  <SearchSVG />
                </span>
              </div>
            </Section>

            <Section>
              <div className="list-container">
                <div className="scrollable-list">
                  {loading ? (
                    <p>Loading...</p>
                  ) : error ? (
                    <p>{error}</p>
                  ) : employees.length > 0 ? (
                    employees.map((emp) => {
                      const isAlreadyReceiver =
                        !isProvidersPage &&
                        fromReceiversList?.some(
                          (rec) => rec.employeeId === emp.id
                        );

                      const isAlreadyProvider =
                        isProvidersPage &&
                        (fromReceiversList?.some((prov) => {
                          const providerId =
                            prov.reviewerId || prov.employeeId || prov.id;
                          return providerId?.toString() === emp.id?.toString();
                        }) ||
                          previousProviders.some(
                            (prov) => prov.id?.toString() === emp.id?.toString()
                          ));

                      const isSelected = selected.some(
                        (sel) => sel.id === emp.id
                      );
                      const isSelf =
                        isProvidersPage &&
                        emp.id === selectedReceiver?.employeeId;

                      const showTick =
                        isAlreadyReceiver || isAlreadyProvider || isSelected;
                      const isDisabled =
                        isAlreadyReceiver ||
                        isAlreadyProvider ||
                        isSelected ||
                        isSelf;

                      return (
                        <div
                          key={emp.id}
                          className={`employee-row ${showTick ? 'selected' : ''} ${
                            isDisabled ? 'disabled' : ''
                          }`}
                          onClick={() => {
                            if (isSelf) {
                              return;
                            }

                            if (isAlreadyReceiver || isAlreadyProvider) {
                              return;
                            }

                            if (isSelected) {
                              return;
                            }
                            handleAdd(emp);
                          }}
                        >
                          <div className="employee-details">
                            <p className="employee-name">{emp.name}</p>
                            <p className="employee-role">{emp.role}</p>
                          </div>

                          {showTick && (
                            <span className="selected-icon">
                              <TickmarkIcon />
                            </span>
                          )}
                        </div>
                      );
                    })
                  ) : !loading &&
                    searchCompleted &&
                    searchTerm.trim().length > 0 &&
                    employees.length === 0 ? (
                    <p>No employees found</p>
                  ) : null}
                </div>
              </div>
            </Section>

            {selected.length > 0 && (
              <>
                <Divider />
                <Section className="selected-section">
                  <SectionTitle>
                    {isProvidersPage
                      ? t('Selected_Feedback_Providers')
                      : t('Selected_Feedback_Receivers')}
                  </SectionTitle>
                  <div className="scrollable-list">
                    {selected.map((emp) => (
                      <div key={emp.id} className="employee-row">
                        <div className="employee-details">
                          <p className="employee-name">{emp.name}</p>
                          <p className="employee-role">{emp.role}</p>
                        </div>
                        <span
                          className="remove-icon"
                          onClick={() => handleRemove(emp.id)}
                        >
                          ×
                        </span>
                      </div>
                    ))}
                  </div>
                </Section>
              </>
            )}

            <FooterContainer>
              <ButtonGroup>
                <Button onClick={onClose} type="button">
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={
                    isProvidersPage
                      ? handleAssignProviders
                      : handleCreateReceivers
                  }
                >
                  {mode === 'provider' ? 'Assign' : 'Add'}
                </Button>
              </ButtonGroup>
            </FooterContainer>
          </FeedbackCard>
        </PreviewWrapper>
      </StyledDiv>
      {toastData.type && (
        <ToastMessage
          messageType={toastData.type}
          messageHeading={toastData.heading || ''}
          messageBody={toastData.body || ''}
          handleClose={() => setToastData({ type: null })}
        />
      )}
      {showErrorToast && (
        <ToastMessage
          messageType="error"
          messageHeading="Error"
          messageBody={errorToastMessage}
          handleClose={() => setShowErrorToast(false)}
        />
      )}
    </ExpenseManagementMainContainer>
  );
};

export default AddFeedbackReceivers;
