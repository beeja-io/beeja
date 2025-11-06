import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import {
  getFeedBackProviderReviewer,
  getProvideFeedbackById,
  postEmployeeResponse,
} from '../service/axiosInstance';
import {
  ButtonGroup,
  DateRangeContainer,
  DateRow,
  DateText,
  DescriptionBox,
  ErrorText,
  FooterContainer,
  FormContainer,
  FormInputsContainer,
  FormLabelContainer,
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
  Title,
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

  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [responseErrorMessage, setResponseErrorMessage] = useState('');
  const [successMessageBody, setSuccessMessageBody] = useState('');
  const [validationErrors, setValidationErrors] = useState<boolean[]>([]);

  useEffect(() => {
    const fetchFeedbackData = async () => {
      try {
        setIsLoading(true);
        const response = await getFeedBackProviderReviewer();
        if (response?.data) {
          const employees = response.data.assignedEmployees || [];
          const formattedData = employees.map((employee: any) => ({
            employeeId: employee.employeeId,
            cycleId: employee.cycleId,
            name: employee.employeeName,
            role: employee.role,
            department: employee.department,
            submitted: employee.isSubmitted || employee.submitted,
          }));
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
    fetchFeedbackData();
  }, []);

  const handleProvideFeedback = async (employee: FeedbackItem) => {
    setSelectedEmployee(employee);
    setFeedbackReceiverName(employee.name);

    try {
      setIsLoading(true);
      const response = await getProvideFeedbackById(employee.cycleId);
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
    } catch (error) {
      setError('Failed to load form data');
      throw new Error('Failed to load form data');
    } finally {
      setIsLoading(false);
    }
  };

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

    if (hasErrors) {
      return;
    }

    const payload = {
      cycleId: selectedEmployee.cycleId,
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

      const updatedResponse = await getFeedBackProviderReviewer();
      if (updatedResponse?.data?.assignedEmployees) {
        const formattedData = updatedResponse.data.assignedEmployees.map(
          (employee: any) => ({
            employeeId: employee.employeeId,
            cycleId: employee.cycleId,
            name: employee.employeeName,
            department: employee.department,
            submitted: employee.isSubmitted || employee.submitted,
          })
        );
        setFeedbackData(formattedData);
      }

      setSuccessMessageBody(
        `Feedback for ${selectedEmployee.name} submitted successfully!`
      );
      setSelectedEmployee(null);
      setShowSuccessMessage(true);
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
                  <Role>{item.department}</Role>
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
          <Header>
            <Title>
              <span onClick={goToPreviousPage}>
                <ArrowDownSVG />
              </span>
              {formData.name || 'Performance Evaluation Form'}
            </Title>
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
            <Subtitle>{formData.type || 'Annual Review'}</Subtitle>
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
