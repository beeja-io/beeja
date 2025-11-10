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
}

type ProvideFeedbackProps = {
  user?: any;
};

const ProvideFeedback: React.FC<ProvideFeedbackProps> = ({ user }) => {
  const [formData, setFormData] = useState<PerformanceFormData | null>(null);
  const [selectedEmployee, setSelectedEmployee] = useState<FeedbackItem | null>(
    null
  );
  const [answers, setAnswers] = useState<Answer[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
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

  const fetchFeedbackData = async () => {
    try {
      setIsLoading(true);
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
            submitted: allSubmitted,
            feedbackCycles: employee.feedbackCycles || [],
          };
        });
        setFeedbackData(formattedData);
      } else {
        setFeedbackData([]);
      }
    } catch (error) {
      setError(`Error fetching feedback data: ${(error as Error).message}`);
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
    setError('');
    setFeedbackReceiverName(employee.name);
    setSelectedEmployee(employee);
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

        const feedbackResponse = await getProvideFeedbackById(
          activeForm.cycleId
        );
        const { evaluationCycle, feedbackResponses } = feedbackResponse.data;

        if (!feedbackResponses?.length) {
          throw new Error('No feedback responses found!');
        }

        const latestFeedback = feedbackResponses.at(-1);

        const questions = latestFeedback.responses.map((res: any) => ({
          questionId: res.questionId,
          question: res.question || `Question for ${res.questionId}`,
          required: true,
          description: res.description || '',
        }));

        setFormData({ ...evaluationCycle, questions });

        const mappedAnswers = latestFeedback.responses.map((res: any) => ({
          questionId: res.questionId,
          description: res.description,
          answer: null,
        }));

        setAnswers(mappedAnswers);
      } else {
        setError('No forms available for this employee.');
      }
    } catch (err) {
      setError('Failed to load feedback forms');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!selectedFormId) return;

    const fetchFormDetails = async () => {
      try {
        setIsLoading(true);
        const response = await getProvideFeedbackById(selectedFormId);
        const { evaluationCycle, feedbackResponses } = response.data;

        if (!feedbackResponses?.length) {
          throw new Error('No feedback responses found!');
        }

        const latestFeedback = feedbackResponses.at(-1);

        const questions = latestFeedback.responses.map((res: any) => ({
          questionId: res.questionId,
          question: res.question || `Question for ${res.questionId}`,
          required: true,
          description: res.description || '',
        }));

        setFormData({ ...evaluationCycle, questions });

        const mappedAnswers = latestFeedback.responses.map((res: any) => ({
          questionId: res.questionId,
          description: res.description,
          answer: null,
        }));

        setAnswers(mappedAnswers);
      } catch (err) {
        setError('Failed to load form data');
      } finally {
        setIsLoading(false);
      }
    };

    fetchFormDetails();
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
      // const updatedForms = formListResponse.data || [];
      // setForms(updatedForms);

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
        setError('');
        setSuccessMessageBody(
          `Feedback for ${selectedEmployee.name} submitted successfully! Moving to next form (${nextForm.label}).`
        );
        setShowSuccessMessage(true);
      } else {
        setSelectedEmployee(null);
        setSuccessMessageBody(
          `All feedback forms for ${selectedEmployee.name} have been submitted!`
        );
        setShowSuccessMessage(true);
        fetchFeedbackData();
      }
      setPreviewMode(false);
    } catch (err) {
      setResponseErrorMessage('Failed to submit feedback. Please try again.');
      setShowErrorMessage(true);
    } finally {
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
  };

  const handleSelectForm = (formId: string) => {
    const form = forms.find((f) => f.id === formId);
    if (!form) return;

    if (form.status === 'COMPLETED') return;

    if (formId === selectedFormId) return;

    setSelectedFormId(formId);
  };

  if (error) return <div>{error}</div>;

  return (
    <FormContainer>
      {isLoading ? (
        <SpinAnimation />
      ) : !selectedEmployee ? (
        <ListContainer>
          {feedbackData.map((item) => (
            <ListRow key={item.employeeId}>
              <UserInfo>
                <UserText>
                  <Name>{item.name}</Name>
                  <Role>{item.role}</Role>
                </UserText>
              </UserInfo>
              <ProvideButton
                disabled={item.submitted}
                onClick={() => !item.submitted && handleProvideFeedback(item)}
                className={item.submitted ? 'submitted' : ''}
              >
                {item.submitted ? 'Submitted' : 'Provide Feedback'}
              </ProvideButton>
            </ListRow>
          ))}
        </ListContainer>
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
                        label: `${item.label}${item.status === 'COMPLETED' ? ' (Completed)' : ''}`,
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
          <FormSubContainer>
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
              <DateRangeContainer>
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
                {`${
                  formData.type
                    ? formData.type.charAt(0).toUpperCase() +
                      formData.type.slice(1).toLowerCase()
                    : 'Annual'
                } Review`}
              </Subtitle>
            </Header>

            <DescriptionBox>{formData.formDescription}</DescriptionBox>

            <div style={{ marginBottom: '20px' }}>
              <Label>
                Feedback Receiver Name<RequiredMark>*</RequiredMark>
              </Label>
              <ReadOnlyInput value={feedbackReceiverName} readOnly />
            </div>

            <Questions>
              {formData.questions.map((q, index) => (
                <QuestionBlock key={index}>
                  <QuestionText>
                    {index + 1}. {q.question}
                    {q.required && <RequiredMark>*</RequiredMark>}
                  </QuestionText>
                  {q.description && (
                    <QuestionDescription>{q.description}</QuestionDescription>
                  )}
                  <FormLabelContainer>
                    <InputContainer>
                      <StyledTextArea
                        className="answer-color"
                        ref={(el) => (answerRefs.current[index] = el)}
                        rows={1}
                        placeholder="Type your Answer"
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
                    <ErrorText>This field is required</ErrorText>
                  )}
                </QuestionBlock>
              ))}
            </Questions>

            <FooterContainer>
              <ButtonGroup>
                <Button type="button" onClick={() => setPreviewMode(true)}>
                  Preview
                </Button>
                <Button className="submit" type="button" onClick={handleSubmit}>
                  Submit
                </Button>
              </ButtonGroup>
            </FooterContainer>
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
    </FormContainer>
  );
};

export default ProvideFeedback;
