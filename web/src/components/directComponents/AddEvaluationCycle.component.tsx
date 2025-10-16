import React, { useEffect, useRef, useState, useLayoutEffect } from 'react';
import { useTranslation } from 'react-i18next';
import Calendar from '../reusableComponents/Calendar.component';
import { CalenderIconDark, DeleteIcon } from '../../svgs/ExpenseListSvgs.svg';
import { Button } from '../../styles/CommonStyles.style';
import {
  FormInputsContainer,
  QuestionBlock,
  FormHeader,
  FormLabelContainer,
  Label,
  InputContainer,
  StyledInput,
  Container,
  FormContainer,
  FooterContainer,
  ButtonGroup,
  DropdownRow,
  TitleInput,
  DateRangeContainer,
  DateField,
  HeaderRow,
  DescriptionButton,
  StyledTextArea,
} from '../../styles/CreateReviewCycleStyle.style';
import DropdownMenu from '../reusableComponents/DropDownMenu.component';
import {
  ReviewType,
  ReviewTypeLabels,
} from '../reusableComponents/PerformanceEnums.component';
import {
  Slider,
  StyledSwitch,
  SwitchLabel,
} from '../../styles/InputStyles.style';
import PreviewMode from '../reusableComponents/PreviewMode.component';
import {
  getPerformanceById,
  postPerformanceCycle,
  updatePerformanceCycle,
} from '../../service/axiosInstance';
import { toast } from 'sonner';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import { useNavigate, useParams } from 'react-router-dom';
import { useOutletContext } from 'react-router-dom';
import SpinAnimation from '../loaders/SprinAnimation.loader';

interface OutletContextType {
  handleShowSuccessMessage: (heading: string, body: string) => void;
}

type Question = {
  question: string;
  required: boolean;
  questionDescription?: string;
};

type ReviewFormData = {
  reviewCycleName: string;
  reviewType: string;
  startDate: string;
  endDate: string;
  formDescription: string;
  questions: Question[];
};

const AddEvaluationCycle: React.FC = () => {
  const { handleShowSuccessMessage } = useOutletContext<OutletContextType>();
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [isEndDateCalOpen, setIsEndDateCalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const { t } = useTranslation();
  const navigate = useNavigate();

  const { id } = useParams<{ id: string }>();
  const isEditMode = Boolean(id);

  const handleClose = () => {
    navigate(-1);
  };

  useEffect(() => {
    if (isEditMode) {
      fetchReviewCycleById();
    }
  }, [isEditMode]);

  const fetchReviewCycleById = async () => {
    if (!id) return;
    try {
      setIsLoading(true);
      const response = await getPerformanceById(id);
      const data = response.data;

      setFormData({
        reviewCycleName: data.name,
        reviewType: data.type,
        startDate: data.startDate,
        endDate: data.feedbackDeadline,
        formDescription: data.formDescription || '',
        questions:
          data?.questions?.map(
            (q: Question): Question => ({
              question: q.question || '',
              questionDescription: q.questionDescription || '',
              required: q.required ?? false,
            })
          ) || [],
      });
    } catch (error) {
      toast.error('Failed to load review cycle details');
    } finally {
      setIsLoading(false);
    }
  };

  const [formData, setFormData] = useState<ReviewFormData>({
    reviewCycleName: '',
    reviewType: '',
    startDate: '',
    endDate: '',
    formDescription: '',
    questions: [
      {
        question: '',
        questionDescription: undefined,
        required: false,
      },
    ],
  });
  const [showErrorMessage, setShowErrorMessage] = useState(false);

  const [errorMessage, setErrorMessage] = useState('');
  const [formErrors, setFormErrors] = useState({
    reviewCycleName: '',
    reviewType: '',
    startDate: '',
    endDate: '',
    questions: '',
  });

  const handleCloseErrorMessage = () => setShowErrorMessage(false);

  const [previewMode, setPreviewMode] = useState(false);

  const formatDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFormDescriptionChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({
      ...prev,
      formDescription: e.target.value,
    }));
    const target = e.target;
    target.style.height = '0px';
    target.style.height = `${target.scrollHeight}px`;
  };

  const addQuestion = () => {
    setFormData((prev) => ({
      ...prev,
      questions: [...prev.questions, { question: '', required: false }],
    }));
  };

  const deleteQuestion = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      questions: prev.questions.filter((_, i) => i !== index),
    }));
    questionRefs.current.splice(index, 1);
    answerRefs.current.splice(index, 1);
  };

  const handleQuestionChange = <FieldName extends keyof Question>(
    questionIndex: number,
    fieldName: FieldName,
    newValue: string | boolean
  ) => {
    if (fieldName === 'question' && questionRefs.current[questionIndex]) {
      const textarea = questionRefs.current[questionIndex];
      if (textarea) {
        textarea.style.height = '0px';
        textarea.style.height = `${textarea.scrollHeight}px`;
      }
    }

    setFormData((previousFormData) => {
      const updatedQuestions = [...previousFormData.questions];
      updatedQuestions[questionIndex] = {
        ...updatedQuestions[questionIndex],
        [fieldName]: newValue,
      };
      return {
        ...previousFormData,
        questions: updatedQuestions,
      };
    });
  };

  const handlePreview = () => {
    const errors: any = {};

    if (!formData.reviewCycleName?.trim()) {
      errors.reviewCycleName = 'required Review Cycle';
    }
    if (!formData.reviewType) {
      errors.reviewType = 'required Review Type';
    }
    if (!formData.startDate) {
      errors.startDate = 'required Start Date';
    }
    if (!formData.endDate) {
      errors.endDate = 'required End Date';
    }

    if (
      !formData.questions?.length ||
      !formData.questions[0].question?.trim()
    ) {
      errors.questions = 'At least one question is required';
    }
    if (
      formData.startDate &&
      formData.endDate &&
      new Date(formData.endDate) < new Date(formData.startDate)
    ) {
      errors.endDate = 'End Date cannot be before Start Date';
    }

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    setPreviewMode(true);
  };

  const handleSubmit = async () => {
    try {
      setIsLoading(true);
      const cycleData = {
        name: formData.reviewCycleName,
        type: formData.reviewType,
        startDate: formData.startDate,
        endDate: formData.endDate,
        selfEvalDeadline: formData.endDate,
        feedbackDeadline: formData.endDate,
        status: 'IN_PROGRESS',
        formDescription: formData.formDescription,
        questions: formData.questions.map((q) => ({
          question: q.question,
          questionDescription: q.questionDescription ?? undefined,
          required: q.required,
        })),
      };

      if (isEditMode) {
        if (!id) {
          throw new Error('No ID found for edit');
        }
        await updatePerformanceCycle(id, cycleData);
        setIsLoading(false);
        handleShowSuccessMessage(
          'Form Updated Successfully',
          'The Evaluation Form had been updated successfully!'
        );
        navigate('/performance/create-evaluation-form');
      } else {
        await postPerformanceCycle(cycleData);
        setIsLoading(false);
        handleShowSuccessMessage(
          'Form Created Successfully',
          'The Evaluation Form has been created successfully.'
        );
        navigate('/performance/create-evaluation-form');
      }
    } catch (error: any) {
      setErrorMessage(
        error.response?.data?.message || 'Something went wrong while saving.'
      );
      setShowErrorMessage(true);
    } finally {
      setIsLoading(false);
    }
  };

  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const questionRefs = useRef<(HTMLTextAreaElement | null)[]>([]);
  const answerRefs = useRef<(HTMLTextAreaElement | null)[]>([]);
  const useAutosizeTextArea = (
    textAreaRef: HTMLTextAreaElement | null,
    value: string
  ) => {
    useLayoutEffect(() => {
      if (textAreaRef) {
        textAreaRef.style.height = '0px';
        textAreaRef.style.height = `${textAreaRef.scrollHeight}px`;
      }
    }, [textAreaRef, value]);
  };
  useAutosizeTextArea(textareaRef.current, formData.formDescription);

  return (
    <FormContainer>
      {isLoading ? (
        <SpinAnimation />
      ) : (
        <FormInputsContainer className="stepTwoContainer">
          <FormHeader>
            <TitleInput
              type="text"
              placeholder="Enter Title"
              value={formData.reviewCycleName}
              onChange={(e: any) =>
                setFormData((prev) => ({
                  ...prev,
                  reviewCycleName: e.target.value,
                }))
              }
            />

            <DateRangeContainer>
              <div className="date-wrapper">
                <DateField onClick={() => setIsStartDateCalOpen(true)}>
                  <CalenderIconDark />
                  <span>
                    {formData.startDate
                      ? formatDate(formData.startDate)
                      : 'From Date'}
                  </span>
                  {isStartDateCalOpen && (
                    <div
                      className="calendarSpace"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <Calendar
                        title="From Date"
                        minDate={new Date('2000-01-01')}
                        selectedDate={
                          formData.startDate
                            ? new Date(formData.startDate)
                            : null
                        }
                        defaultMonth={new Date()}
                        handleDateInput={(date: Date | null) => {
                          if (!date) return;
                          setFormData((prev) => ({
                            ...prev,
                            startDate: date.toLocaleDateString('en-CA'),
                          }));
                          setIsStartDateCalOpen(false);
                        }}
                        handleCalenderChange={() => {}}
                      />
                    </div>
                  )}
                </DateField>
                {formErrors && formErrors?.startDate && (
                  <span className="error-text">{formErrors.startDate}</span>
                )}
              </div>

              <span className="to-label">To</span>
              <div className="date-wrapper">
                <DateField onClick={() => setIsEndDateCalOpen(true)}>
                  <CalenderIconDark />
                  <span>
                    {formData.endDate
                      ? formatDate(formData.endDate)
                      : 'To Date'}
                  </span>
                  {isEndDateCalOpen && (
                    <div
                      className="calendarSpace"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <Calendar
                        title="To Date"
                        minDate={
                          formData.startDate
                            ? new Date(formData.startDate)
                            : new Date()
                        }
                        selectedDate={
                          formData.endDate ? new Date(formData.endDate) : null
                        }
                        defaultMonth={
                          formData.startDate
                            ? new Date(formData.startDate)
                            : new Date()
                        }
                        handleDateInput={(date: Date | null) => {
                          if (!date) return;
                          setFormData((prev) => ({
                            ...prev,
                            endDate: date.toLocaleDateString('en-CA'),
                          }));
                          setIsEndDateCalOpen(false);
                        }}
                        handleCalenderChange={() => {}}
                      />
                    </div>
                  )}
                </DateField>
                {formErrors && formErrors?.endDate && (
                  <span className="error-text">{formErrors.endDate}</span>
                )}
              </div>
            </DateRangeContainer>

            <DropdownRow>
              <div className="dropdown-wrapper">
                <DropdownMenu
                  label={t('Select Review Type')}
                  name="reviewType"
                  id="reviewType"
                  style={{ border: 'none' }}
                  value={formData.reviewType || ''}
                  className="largeContainerFil"
                  onChange={(e) => {
                    const event = {
                      target: {
                        name: 'reviewType',
                        value: e,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>;
                    handleChange(event);
                  }}
                  options={[
                    { label: t('Select Review Type'), value: '' },
                    ...Object.values(ReviewType).map((type) => ({
                      label: ReviewTypeLabels[type],
                      value: type,
                    })),
                  ]}
                />
                {formErrors && formErrors?.reviewType && (
                  <span className="error-text">{formErrors.reviewType}</span>
                )}
              </div>
            </DropdownRow>
          </FormHeader>
          <Container>
            <HeaderRow>
              <Label>Form Description</Label>
            </HeaderRow>
            <FormLabelContainer className="description-container">
              <InputContainer>
                <StyledTextArea
                  ref={textareaRef}
                  rows={1}
                  placeholder="Description"
                  value={formData.formDescription}
                  onChange={handleFormDescriptionChange}
                />
              </InputContainer>
            </FormLabelContainer>

            {formData.questions.map((q, index) => (
              <QuestionBlock key={index}>
                <StyledTextArea
                  className="question-input"
                  rows={1}
                  ref={(el) => (questionRefs.current[index] = el)}
                  placeholder="Write your question here"
                  value={q.question}
                  onChange={(e) =>
                    handleQuestionChange(index, 'question', e.target.value)
                  }
                />

                {!q.questionDescription && (
                  <DescriptionButton
                    type="button"
                    onClick={() =>
                      handleQuestionChange(index, 'questionDescription', '')
                    }
                  >
                    <span className="plus-box">+</span> Add Description
                  </DescriptionButton>
                )}

                {q.questionDescription !== undefined && (
                  <FormLabelContainer className="answer-container">
                    <StyledInput
                      type="text"
                      placeholder="Add a description (optional)"
                      value={q.questionDescription}
                      onChange={(e) =>
                        handleQuestionChange(
                          index,
                          'questionDescription',
                          e.target.value
                        )
                      }
                    />
                  </FormLabelContainer>
                )}

                <div className="question-footer">
                  <button
                    type="button"
                    className="add-btn"
                    onClick={addQuestion}
                  >
                    + Add New Question
                  </button>

                  <div className="actions">
                    <div className="required-toggle">
                      <span>Required</span>
                      <SwitchLabel>
                        <StyledSwitch
                          type="checkbox"
                          checked={q.required}
                          onChange={(e) =>
                            handleQuestionChange(
                              index,
                              'required',
                              e.target.checked
                            )
                          }
                        />
                        <Slider />
                      </SwitchLabel>
                    </div>
                    <button type="button" onClick={() => deleteQuestion(index)}>
                      <DeleteIcon />
                    </button>
                  </div>
                </div>
              </QuestionBlock>
            ))}

            <FooterContainer className="centerAlign">
              <ButtonGroup>
                <Button onClick={handleClose} type="button">
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={handlePreview}
                >
                  {isEditMode ? 'Update & Preview' : 'Save & Preview'}
                </Button>
              </ButtonGroup>
            </FooterContainer>
          </Container>
        </FormInputsContainer>
      )}

      {previewMode && (
        <PreviewMode
          formData={formData}
          onEdit={() => setPreviewMode(false)}
          onConfirm={handleSubmit}
        />
      )}
      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageBody={errorMessage}
          messageHeading="ERROR"
          handleClose={handleCloseErrorMessage}
        />
      )}
    </FormContainer>
  );
};

export default AddEvaluationCycle;
