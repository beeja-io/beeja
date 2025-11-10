import { useState, useEffect, useCallback } from 'react';
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
  const [, setList] = useState([]);
  const isProvidersPage = mode === 'provider';

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
        toast.error('Please select at least one provider');
        return;
      }

      const payload = {
        cycleId,
        questionnaireId,
        assignedReviewers: selected.map((emp) => ({
          reviewerId: emp.id?.toString(),
          role: providerRole || emp.role || 'Reviewer',
          status: 'IN_PROGRESS',
        })),
      };

      if (selectedReceiver.providerStatus === 'NOT_ASSIGNED') {
        await assignProvider(selectedReceiver.employeeId, payload);
        toast.success('Providers assigned successfully');
      } else if (selectedReceiver.providerStatus === 'IN_PROGRESS') {
        await updateFeedbackProviders(selectedReceiver.employeeId, payload);
        toast.success('Providers updated successfully');
      } else {
        toast.error('Unsupported provider status');
        return;
      }

      onSuccess?.();
      onClose?.();
    } catch (err) {
      toast.error('Failed to assign provider');
    }
  };

  const handleCreateReceivers = async () => {
    if (!cycleId || !questionnaireId) {
      toast.error('Cycle ID or Questionnaire ID missing');
      return;
    }

    const payload = {
      cycleId,
      questionnaireId,
      receiverDetails: selected.map((emp) => ({
        employeeId: emp.id,
        fullName: emp.name,
        department: emp.role,
        email: emp.email,
      })),
    };

    try {
      await createReceiverList(payload);

      toast.success('Successfully Added', {
        description: 'The Feedback Receiver has been added successfully.',
      });

      onSuccess?.();
      onClose?.();
    } catch (error) {
      toast.error('Failed to create receiver list');
    }
  };

  const fetchUsers = useCallback(async (keyword: string = '') => {
    if (keyword.trim().length === 0) return;
    setLoading(true);

    try {
      const response = await searchUsers(keyword);
      const employeeList = response?.data || [];

      const formatted = employeeList
        .map((emp: any) => ({
          id: emp.employeeId,
          name: emp.fullName,
          role: emp.department || '—',
          email: emp.email || '',
        }))
        .filter((emp: { name: string }) =>
          emp.name?.toLowerCase().includes(keyword.toLowerCase())
        );

      if (formatted.length === 0) {
        setError('No employees found');
        setEmployees([]);
      } else {
        setEmployees(formatted);
        setError(null);
      }
    } catch (err) {
      setError('Error while fetching employees');
      setEmployees([]);
    } finally {
      setLoading(false);
    }
  }, []);

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

  const handleSearchClick = () => fetchUsers(searchTerm);

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

    if (isProvidersPage && selectedReceiver?.employeeId) {
      getProviders(selectedReceiver.employeeId).then((res) => {
        setList(res.data?.providers || []);
      });
    } else {
      getReceivers(cycleId, questionnaireId).then((res) => {
        setList(res.data?.receivers || []);
      });
    }
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

            {/* 🔍 Search Section */}
            <Section>
              <SectionTitle>
                {isProvidersPage
                  ? t('Select_Feedback_Providers')
                  : t('Select_Feedback_Receivers')}
                *
              </SectionTitle>
              <div className="search-container">
                <input
                  type="text"
                  className="search-input"
                  placeholder="Search by Employee Name, Employee ID"
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
                      const isSelected = selected.some(
                        (sel) => sel.id === emp.id
                      );
                      return (
                        <div
                          key={emp.id}
                          className={`employee-row ${isSelected ? 'selected' : ''}`}
                          onClick={() => !isSelected && handleAdd(emp)}
                        >
                          <div className="employee-details">
                            <p className="employee-name">{emp.name}</p>
                            <p className="employee-role">{emp.role}</p>
                          </div>

                          {isSelected && (
                            <span className="selected-label">Selected</span>
                          )}
                        </div>
                      );
                    })
                  ) : (
                    <p>No employees found</p>
                  )}
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
    </ExpenseManagementMainContainer>
  );
};

export default AddFeedbackReceivers;
