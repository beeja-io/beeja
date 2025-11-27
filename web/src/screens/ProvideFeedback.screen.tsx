import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import {
  getFeedBackProviderReviewer,
  getMultiFormList,
  getProvideFeedbackById,
  postEmployeeResponse,
} from '../service/axiosInstance';
import {
  BackButton,
  ButtonGroup,
  ControlsRow,
  Count,
  DateRangeContainer,
  DateRow,
  DateText,
  DescriptionBox,
  ErrorText,
  FooterContainer,
  FormContainer,
  FormContentWrapper,
  FormInputsContainer,
  FormLabelContainer,
  FormsCount,
  FormSubContainer,
  Header,
  InputContainer,
  Label,
  QuestionBlock,
  QuestionDescription,
  Questions,
  QuestionText,
  RequiredMark,
  StyledTextArea,
  Subtitle,
  TitleHeading,
} from '../styles/CreateReviewCycleStyle.style';
import {
  ListContainer,
  ListRow,
  Name,
  ProvideButton,
  Role,
  UserInfo,
  UserText,
} from '../styles/FeedbackHubStyles.style';
import { Button } from '../styles/CommonStyles.style';
import { ReadOnlyInput } from '../styles/FeedbackHubStyles.style';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { CalenderIconDark } from '../svgs/ExpenseListSvgs.svg';
import PreviewMode from '../components/reusableComponents/PreviewMode.component';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';
import { hasPermission } from '../utils/permissionCheck';
import { PERFORMANCE_MODULE } from '../constants/PermissionConstants';
import {
  ReviewType,
  ReviewTypeLabels,
} from '../components/reusableComponents/PerformanceEnums.component';
import { useTranslation } from 'react-i18next';
import { format, isAfter, isBefore, startOfDay } from 'date-fns';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';

interface Question {
  question: string;
  description: string;
  target: 'SELF' | 'OTHER';
  required: boolean;
}

interface PerformanceFormData {
  endDate: string;
  feedbackDeadline: string;
  formDescription: string;
  id: string;
  name: string;
  organizationId: string | null;
  questionnaireId: string;
  questions: Question[];
  selfEvalDeadline: string;
  startDate: string;
  status: string;
  type: string;
}

interface Answer {
  questionId: string;
  description: string;
  answer: string;
}

interface FeedbackItem {
  employeeId: string;
  cycleId: string;
  name: string;
  role: string;
  submitted: boolean;
  department: string;
  designation: string;
}

type ProvideFeedbackProps = {
  user?: any;
  onPendingCountChange?: (count: number) => void;
};

const ProvideFeedback: React.FC<ProvideFeedbackProps> = ({
  user,
  onPendingCountChange,
}) => {
  const [formData, setFormData] = useState<PerformanceFormData | null>(null);
  const [selectedEmployee, setSelectedEmployee] = useState<FeedbackItem | null>(
    null
  );
  const [answers, setAnswers] = useState<Answer[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [previewMode, setPreviewMode] = useState(false);
  const [feedbackReceiverName, setFeedbackReceiverName] = useState('');
  const [feedbackData, setFeedbackData] = useState<FeedbackItem[]>([]);
  const [forms, setForms] = useState<
    { id: string; label: string; status: string }[]
  >([]);
  const [selectedFormId, setSelectedFormId] = useState<string | null>(null);
  const [formsAvailableCount, setFormsAvailableCount] = useState<number>(0);

  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [responseErrorMessage, setResponseErrorMessage] = useState('');
  const [successMessageBody, setSuccessMessageBody] = useState('');
  const [validationErrors, setValidationErrors] = useState<boolean[]>([]);
  const isPermissionDenied = !hasPermission(
    user!,
    PERFORMANCE_MODULE.PROVIDE_FEEDBACK
  );
  const [showWarning, setShowWarning] = useState(false);
  const [warningMessageBody, setWarningMessageBody] = useState('');
  const [blockWarnings, setBlockWarnings] = useState(false);

  const { t } = useTranslation();

  const fetchFeedbackData = async () => {
    try {
      setIsLoading(true);
      setShowErrorMessage(false);
      const response = await getFeedBackProviderReviewer();
      if (response?.data) {
        const employees = response.data.assignedEmployees || [];
        const formattedData = employees.map((employee: any) => {
          const allSubmitted = employee.feedbackCycles?.every(
            (cycle: any) => cycle.submitted === true
          );
          return {
            employeeId: employee.employeeId,
            name: employee.employeeName,
            role: employee.role,
            department: employee.department,
            designation: employee.designation,
            submitted: allSubmitted,
            feedbackCycles: employee.feedbackCycles || [],
          };
        });
        setFeedbackData(formattedData);
        const pending = formattedData.filter(
          (emp: any) => !emp.submitted
        ).length;
        if (onPendingCountChange) onPendingCountChange(pending);
      } else {
        setFeedbackData([]);
      }
    } catch (error) {
      setResponseErrorMessage(
        'Failed to load feedback requests. Please try again later.'
      );
      setShowErrorMessage(true);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchFeedbackData();
  }, []);

  const handleProvideFeedback = async (employee: FeedbackItem) => {
    setFormData(null);
    setForms([]);
    setSelectedFormId(null);
    setResponseErrorMessage('');
    setFeedbackReceiverName(employee.name);
    setSelectedEmployee(employee);
    setShowErrorMessage(false);
    try {
      setIsLoading(true);

      const response = await getMultiFormList(employee.employeeId);
      const formList = response.data || [];

      const mappedForms = formList.map((form: any) => ({
        id: form.cycleId,
        label: form.cycleName,
        status: form.status,
      }));

      setForms(mappedForms);
      setFormsAvailableCount(formList.length);

      if (formList.length > 0) {
        const activeForm =
          formList.find((form: any) => form.status === 'IN_PROGRESS') ||
          formList[0];
        setSelectedFormId(activeForm.cycleId);
        await fetchFormDetails(activeForm.cycleId);
      } else {
        setResponseErrorMessage('No forms available for this employee.');
      }
    } catch (err) {
      setResponseErrorMessage('Failed to load feedback forms');
      setShowErrorMessage(true);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchFormDetails = async (selectedFormId: string) => {
    try {
      setIsLoading(true);
      setShowErrorMessage(false);
      const response = await getProvideFeedbackById(selectedFormId);
      const { evaluationCycle } = response.data;

      if (!evaluationCycle?.questions?.length) {
        throw new Error('No questions found in the evaluation cycle');
      }

      const questions = evaluationCycle.questions.map((q: any) => ({
        questionId: q.questionId,
        question: q.question,
        required: q.required,
        description: q.questionDescription,
      }));
      setFormData({ ...evaluationCycle, questions });

      const mappedAnswers = questions.map((res: any) => ({
        questionId: res.question,
        description: res.description,
        answer: null,
      }));

      setAnswers(mappedAnswers);
    } catch (err) {
      setResponseErrorMessage('Failed to load form data');
      setShowErrorMessage(true);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (selectedFormId) fetchFormDetails(selectedFormId);
  }, [selectedFormId]);

  const formatDate = (dateString: string): string => {
    if (!dateString) return '';
    const options: Intl.DateTimeFormatOptions = {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    };
    return new Date(dateString).toLocaleDateString('en-US', options);
  };

  const handleAnswerChange = (index: number, value: string) => {
    const newAnswers = [...answers];
    newAnswers[index].answer = value;
    setAnswers(newAnswers);
  };

  const handleSubmit = async () => {
    setBlockWarnings(true);
    if (!formData || !selectedEmployee) return;

    const errors = formData.questions.map(
      (q, index) => q.required && !answers[index]?.answer?.trim()
    );
    const hasErrors = errors.some((err) => err === true);
    setValidationErrors(errors);

    if (hasErrors) return;

    const payload = {
      cycleId: selectedFormId,
      employeeId: selectedEmployee.employeeId,
      reviewerId: user?.employeeId,
      reviewerRole: 'PEER',
      responses: answers.map((a) => ({
        questionId: a.questionId,
        description: a.description,
        answer: a.answer,
      })),
    };

    try {
      setIsLoading(true);
      await postEmployeeResponse(payload);
      setFeedbackData((prevData) =>
        prevData.map((item) =>
          item.employeeId === selectedEmployee.employeeId
            ? { ...item, submitted: true }
            : item
        )
      );

      const formListResponse = await getMultiFormList(
        selectedEmployee.employeeId
      );

      const updatedForms = formListResponse.data || [];
      const mappedForms = updatedForms.map((form: any) => ({
        id: form.cycleId,
        label: form.cycleName,
        status: form.status,
      }));

      setForms(mappedForms);

      const nextForm = mappedForms.find((f: any) => f.status === 'IN_PROGRESS');

      if (nextForm) {
        setSelectedFormId(nextForm.id);
        setResponseErrorMessage('');
        setSuccessMessageBody(
          t('feedback.nextFormSuccess', {
            name: selectedEmployee.name,
            label: nextForm.label,
          })
        );
        setShowSuccessMessage(true);
      } else {
        setSelectedEmployee(null);

        if (mappedForms.length === 1) {
          setSuccessMessageBody(
            t('feedback.singleSuccess', { name: selectedEmployee.name })
          );
        } else {
          setSuccessMessageBody(
            t('feedback.allSuccess', { name: selectedEmployee.name })
          );
        }
        setShowSuccessMessage(true);
        setFeedbackData((prevData) => {
          const updatedData = prevData.map((item) =>
            item.employeeId === selectedEmployee.employeeId
              ? { ...item, submitted: true }
              : item
          );

          if (onPendingCountChange) {
            const pending = updatedData.filter((emp) => !emp.submitted).length;
            onPendingCountChange(pending);
          }

          return updatedData;
        });

        fetchFeedbackData();
      }
      setPreviewMode(false);
    } catch (err) {
      setResponseErrorMessage('Failed to submit feedback. Please try again.');
      setShowErrorMessage(true);
    } finally {
      setTimeout(() => setBlockWarnings(false), 300);
      setIsLoading(false);
    }
  };

  const answerRefs = useRef<(HTMLTextAreaElement | null)[]>([]);
  useLayoutEffect(() => {
    answers.forEach((_, index) => {
      const textArea = answerRefs.current[index];
      if (textArea) {
        textArea.style.height = '0px';
        textArea.style.height = `${textArea.scrollHeight}px`;
      }
    });
  }, [answers]);

  const goToPreviousPage = () => {
    setSelectedEmployee(null);
    setFormData(null);
    fetchFeedbackData();
    setShowWarning(false);
    setWarningMessageBody('');
  };

  const handleSelectForm = (formId: string) => {
    const form = forms.find((f) => f.id === formId);
    if (!form) return;

    if (form.status === 'COMPLETED') return;

    if (formId === selectedFormId) return;

    setSelectedFormId(formId);
    setShowWarning(false);
    setWarningMessageBody('');
  };

  useEffect(() => {
    if (previewMode) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [previewMode]);

  const isFormActive = (formData: any) => {
    if (!formData?.startDate || !formData?.endDate) return false;

    const today = startOfDay(new Date());
    const startDate = startOfDay(new Date(formData.startDate));
    const endDate = startOfDay(new Date(formData.endDate));

    const hasStarted = !isBefore(today, startDate);
    const notExpired = !isAfter(today, endDate);

    return hasStarted && notExpired;
  };

  const formActive = isFormActive(formData);

  useEffect(() => {
    if (blockWarnings) return;
    if (!formData?.startDate || !formData?.endDate) return;

    const today = startOfDay(new Date());
    const startDate = startOfDay(new Date(formData.startDate));
    const endDate = startOfDay(new Date(formData.endDate));

    if (isBefore(today, startDate)) {
      const formattedStart = format(startDate, 'dd MMM yyyy');
      setWarningMessageBody(
        `This form is not yet active. It will be available from ${formattedStart}.`
      );
      setTimeout(() => {
        setShowSuccessMessage(false);
        setShowWarning(true);
      }, 800);
    } else if (isAfter(today, endDate)) {
      setWarningMessageBody(
        'This form has expired. You can no longer submit responses.'
      );
      setTimeout(() => {
        setShowSuccessMessage(false);
        setShowWarning(true);
      }, 800);
    }
  }, [formData, blockWarnings]);

  return (
    <FormContainer
      className={`noBorder ${!selectedEmployee && feedbackData.length === 0 ? 'empty-state' : ''}`}
    >
      {isLoading ? (
        <SpinAnimation />
      ) : !selectedEmployee ? (
        <>
          {feedbackData.length === 0 ? (
            <ZeroEntriesFound heading="No Feedback Requests Found" />
          ) : (
            <ListContainer>
              {feedbackData.map((item) => (
                <ListRow key={item.employeeId}>
                  <UserInfo>
                    <UserText>
                      <Name>{item.name}</Name>
                      <Role>
                        {item.designation === 'Unknown'
                          ? '-'
                          : item.designation}
                      </Role>
                    </UserText>
                  </UserInfo>
                  <ProvideButton
                    disabled={item.submitted || isPermissionDenied}
                    onClick={() => {
                      if (!item.submitted && !isPermissionDenied) {
                        handleProvideFeedback(item);
                      }
                    }}
                    title={
                      isPermissionDenied
                        ? 'You don’t have permission to provide feedback'
                        : ''
                    }
                    className={item.submitted ? 'submitted' : ''}
                  >
                    {item.submitted ? 'Submitted' : 'Provide Feedback'}
                  </ProvideButton>
                </ListRow>
              ))}
            </ListContainer>
          )}
        </>
      ) : formData ? (
        <FormInputsContainer className="stepTwoContainer">
          <ControlsRow>
            <div>
              <label htmlFor="feedbackFormSelect">
                Select Feedback Form<RequiredMark>*</RequiredMark>
              </label>
              <DropdownMenu
                label={
                  forms.length === 0 ? 'No forms available' : 'Select form'
                }
                name="feedbackFormSelect"
                id="feedbackFormSelect"
                value={selectedFormId || ''}
                required
                className="largeContainerExp"
                disabled={isLoading || forms.length === 0}
                options={
                  forms.length > 0
                    ? forms.map((item) => ({
                        label: `${item.label}${item.status === 'COMPLETED' ? ' (Submitted)' : ''}`,
                        value: item.id,
                        disabled: item.status === 'COMPLETED',
                      }))
                    : []
                }
                onChange={(selectedValue: string | null) => {
                  if (selectedValue) {
                    handleSelectForm(selectedValue);
                  }
                }}
              />
            </div>
            <FormsCount>
              No of forms available: <Count>{formsAvailableCount}</Count>
            </FormsCount>
          </ControlsRow>
          <FormSubContainer className="stepTwoContainer">
            <Header>
              <TitleHeading>
                <BackButton onClick={goToPreviousPage}>
                  <span>
                    <ArrowDownSVG />
                  </span>
                  Back
                </BackButton>
                <h2>{formData.name || 'Performance Evaluation Form'}</h2>
                <div style={{ width: '60px' }}></div>
              </TitleHeading>
              <DateRangeContainer className="feedback-date-range">
                <DateRow>
                  <DateText>
                    <CalenderIconDark />
                    {formData.startDate ? formatDate(formData.startDate) : ''}
                    <span>To</span>
                    <CalenderIconDark />
                    {formData.endDate ? formatDate(formData.endDate) : ''}
                  </DateText>
                </DateRow>
              </DateRangeContainer>
              <Subtitle>
                {formData.type
                  ? `${ReviewTypeLabels[formData.type as ReviewType]} Review`
                  : ''}
              </Subtitle>
            </Header>
            <FormContentWrapper className={!formActive ? 'disabled-state' : ''}>
              {formData.formDescription && (
                <DescriptionBox>{formData.formDescription}</DescriptionBox>
              )}

              <div style={{ marginBottom: '20px' }}>
                <Label>
                  Feedback Receiver Name<RequiredMark>*</RequiredMark>
                </Label>
                <ReadOnlyInput value={feedbackReceiverName} readOnly />
              </div>

              <Questions>
                {formData.questions.map((q, index) => (
                  <QuestionBlock key={index}>
                    <QuestionText className="viewquestion">
                      <span>{index + 1}.</span>
                      <span>
                        {q.question}
                        {q.required && <RequiredMark>*</RequiredMark>}
                      </span>
                    </QuestionText>

                    {q.description && (
                      <QuestionDescription className="feedback-description">
                        {q.description}
                      </QuestionDescription>
                    )}
                    <FormLabelContainer>
                      <InputContainer>
                        <StyledTextArea
                          className="answer-color"
                          disabled={!formActive}
                          ref={(el) => (answerRefs.current[index] = el)}
                          rows={1}
                          placeholder="Enter your Answer"
                          value={answers[index]?.answer || ''}
                          onChange={(e) => {
                            handleAnswerChange(index, e.target.value);
                            if (validationErrors[index]) {
                              const updatedErrors = [...validationErrors];
                              updatedErrors[index] = false;
                              setValidationErrors(updatedErrors);
                            }
                          }}
                        />
                      </InputContainer>
                    </FormLabelContainer>
                    {validationErrors[index] && (
                      <ErrorText>
                        An answer is required for this question.
                      </ErrorText>
                    )}
                  </QuestionBlock>
                ))}
              </Questions>

              <FooterContainer>
                <ButtonGroup>
                  <Button
                    type="button"
                    onClick={() => setPreviewMode(true)}
                    disabled={!formActive}
                  >
                    Preview
                  </Button>
                  <Button
                    className="submit"
                    type="button"
                    onClick={handleSubmit}
                    disabled={!formActive}
                  >
                    Submit
                  </Button>
                </ButtonGroup>
              </FooterContainer>
            </FormContentWrapper>
          </FormSubContainer>
        </FormInputsContainer>
      ) : null}

      {previewMode && formData && (
        <PreviewMode
          formData={{
            reviewCycleName: formData.name,
            reviewType: formData.type,
            startDate: formData.startDate,
            endDate: formData.endDate,
            formDescription: formData.formDescription,
            questions: formData.questions.map((q, index) => ({
              question: q.question,
              questionDescription: q.description,
              required: q.required,
              answer: answers[index]?.answer || '',
            })),
          }}
          onEdit={() => setPreviewMode(false)}
          onConfirm={handleSubmit}
          showAnswers
          feedbackReceiverName={feedbackReceiverName}
          validationErrors={validationErrors}
          Submit="Submit"
        />
      )}

      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageBody={responseErrorMessage}
          messageHeading="Submission Failed"
          handleClose={() => setShowErrorMessage(false)}
        />
      )}
      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody={successMessageBody}
          messageHeading="Submitted Successfully"
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
      {showWarning && (
        <ToastMessage
          messageType="warn"
          messageHeading="Warning"
          messageBody={warningMessageBody}
          handleClose={() => setShowWarning(false)}
        />
      )}
    </FormContainer>
  );
};

export default ProvideFeedback;
