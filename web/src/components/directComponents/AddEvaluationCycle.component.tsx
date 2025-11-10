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
import CenterModal from '../reusableComponents/CenterModal.component';

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
  const context = useOutletContext<OutletContextType>();
  const handleShowSuccessMessage =
    context?.handleShowSuccessMessage || (() => {});
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [isEndDateCalOpen, setIsEndDateCalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [activeAddIndex, setActiveAddIndex] = useState<number>(0);

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

  const startCalendarRef = useRef<HTMLDivElement>(null);
  const endCalendarRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;

      if (
        startCalendarRef.current &&
        !startCalendarRef.current.contains(target)
      ) {
        setIsStartDateCalOpen(false);
      }

      if (endCalendarRef.current && !endCalendarRef.current.contains(target)) {
        setIsEndDateCalOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const fetchReviewCycleById = async () => {
    if (!id) return;
    try {
      setIsLoading(true);
      const response = await getPerformanceById(id);
      const data = response.data;

      const mappedQuestions =
        data?.questions?.map(
          (q: Question): Question => ({
            question: q.question || '',
            questionDescription:
              q.questionDescription && q.questionDescription.trim() !== ''
                ? q.questionDescription
                : undefined,
            required: q.required ?? false,
          })
        ) || [];

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
              questionDescription:
                q.questionDescription && q.questionDescription.trim() !== ''
                  ? q.questionDescription
                  : undefined,
              required: q.required ?? false,
            })
          ) || [],
      });
      setActiveAddIndex(mappedQuestions.length - 1);
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

  useEffect(() => {
    if (formData.questions.length === 0) {
      setFormData((prev) => ({
        ...prev,
        questions: [
          { question: '', questionDescription: undefined, required: false },
        ],
      }));
      setActiveAddIndex(0);
    }
  }, [formData.questions]);

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
  const [showCancelModal, setShowCancelModal] = useState(false);
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

  const addQuestion = (currentIndex?: number) => {
    const currentQuestion =
      typeof currentIndex === 'number'
        ? formData.questions[currentIndex]
        : null;

    if (
      currentQuestion &&
      (!currentQuestion.question ||
        currentQuestion.question.trim().length === 0)
    ) {
      return;
    }

    setFormData((prev) => ({
      ...prev,
      questions: [
        ...prev.questions,
        { question: '', questionDescription: undefined, required: false },
      ],
    }));

    if (typeof currentIndex === 'number') {
      setActiveAddIndex(currentIndex + 1);
    } else {
      setActiveAddIndex(formData.questions.length);
    }
  };

  const deleteQuestion = (index: number) => {
    setFormData((prev) => {
      const updatedQuestions = prev.questions.filter((_, i) => i !== index);
      return {
        ...prev,
        questions: updatedQuestions,
      };
    });

    questionRefs.current.splice(index, 1);
    answerRefs.current.splice(index, 1);

    setActiveAddIndex((prev) => {
      if (index === prev && prev > 0) return prev - 1;
      if (index < prev) return prev - 1;
      if (index > prev) return prev;
      if (
        index === formData.questions.length - 1 &&
        prev === formData.questions.length - 1
      )
        return formData.questions.length - 2;
      return prev;
    });
  };

  const handleQuestionChange = <FieldName extends keyof Question>(
    questionIndex: number,
    fieldName: FieldName,
    newValue: string | boolean | undefined
  ) => {
    if (
      (fieldName === 'question' && questionRefs.current[questionIndex]) ||
      (fieldName === 'questionDescription' && answerRefs.current[questionIndex])
    ) {
      const textarea =
        fieldName === 'question'
          ? questionRefs.current[questionIndex]
          : answerRefs.current[questionIndex];

      if (textarea) {
        textarea.style.height = '0px';
        textarea.style.height = `${textarea.scrollHeight}px`;
      }
    }

    setFormData((previousFormData) => {
      const updatedQuestions = [...previousFormData.questions];
      const updatedQuestion = { ...updatedQuestions[questionIndex] };

      if (newValue === undefined && fieldName === 'questionDescription') {
        delete updatedQuestion[fieldName];
      } else {
        updatedQuestion[fieldName] = newValue as any;
      }

      updatedQuestions[questionIndex] = updatedQuestion;

      return {
        ...previousFormData,
        questions: updatedQuestions,
      };
    });
  };

  useEffect(() => {
    if (!formData?.questions?.length) return;
    formData.questions.forEach((_, index) => {
      const questionTextArea = questionRefs.current[index];
      const descriptionTextArea = answerRefs.current[index];

      if (questionTextArea) {
        questionTextArea.style.height = '0px';
        questionTextArea.style.height = `${questionTextArea.scrollHeight}px`;
      }

      if (descriptionTextArea) {
        descriptionTextArea.style.height = '0px';
        descriptionTextArea.style.height = `${descriptionTextArea.scrollHeight}px`;
      }
    });
  }, [formData.questions]);

  const handlePreview = () => {
    const errors: any = {};

    if (!formData.reviewCycleName?.trim()) {
      errors.reviewCycleName = 'Required Review Cycle';
    }
    if (!formData.reviewType) {
      errors.reviewType = 'Required Review Type';
    }
    if (!formData.startDate) {
      errors.startDate = 'Required Start Date';
    }
    if (!formData.endDate) {
      errors.endDate = 'Required End Date';
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
    const filteredQuestions = formData.questions.filter(
      (q) =>
        q.question.trim() !== '' || (q.questionDescription?.trim() || '') !== ''
    );

    setFormData((prev) => ({
      ...prev,
      questions: filteredQuestions,
    }));

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    setPreviewMode(true);
  };

  const hasValidQuestion = formData.questions.some(
    (q) => q.question && q.question.trim() !== ''
  );

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

  return (
    <FormContainer>
      {isLoading ? (
        <SpinAnimation />
      ) : (
        <FormInputsContainer className="stepTwoContainer">
          <FormHeader>
            <div className="date-wrapper">
              <TitleInput
                type="text"
                placeholder="Enter Title"
                value={formData.reviewCycleName}
                onChange={(e: any) => {
                  const value = e.target.value;
                  const filteredValue = value.replace(/[^a-zA-Z0-9 _-]/g, '');
                  setFormData((prev) => ({
                    ...prev,
                    reviewCycleName: filteredValue,
                  }));

                  if (formErrors?.reviewCycleName && value.trim()) {
                    setFormErrors((prev: any) => ({
                      ...prev,
                      reviewCycleName: '',
                    }));
                  }
                }}
              />
              {formErrors && formErrors?.reviewCycleName && (
                <span className="error-text">{formErrors.reviewCycleName}</span>
              )}
            </div>
            <DateRangeContainer>
              <div className="date-wrapper" ref={startCalendarRef}>
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
                          setFormErrors((prev: any) => ({
                            ...prev,
                            startDate: '',
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
              <div className="date-wrapper" ref={endCalendarRef}>
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
                          setFormErrors((prev: any) => ({
                            ...prev,
                            endDate: '',
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
                  justify="center"
                  className="largeContainerFil dropdownCenterFit"
                  onChange={(e) => {
                    const event = {
                      target: {
                        name: 'reviewType',
                        value: e,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>;
                    handleChange(event);
                    setFormErrors((prev: any) => ({ ...prev, reviewType: '' }));
                  }}
                  options={[
                    { label: t('Select Review Type'), value: '' },
                    ...Object.values(ReviewType).map((type) => ({
                      label: `${ReviewTypeLabels[type]} Review`,
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
                  className="description"
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

                {q.questionDescription === undefined ? (
                  <DescriptionButton
                    type="button"
                    onClick={() => {
                      handleQuestionChange(index, 'questionDescription', '');
                      setTimeout(() => {
                        answerRefs.current[index]?.focus();
                      }, 0);
                    }}
                  >
                    <span className="plus-box">+</span> Add Description
                  </DescriptionButton>
                ) : (
                  <StyledTextArea
                    placeholder="Add a description (optional)"
                    className="question-input description"
                    value={q.questionDescription}
                    rows={1}
                    ref={(el) => (answerRefs.current[index] = el)}
                    onChange={(e) => {
                      const value = e.target.value;

                      if (value.trim() === '') {
                        handleQuestionChange(
                          index,
                          'questionDescription',
                          undefined
                        );
                      } else {
                        handleQuestionChange(
                          index,
                          'questionDescription',
                          value
                        );
                      }
                    }}
                  />
                )}

                <div className="question-footer">
                  {activeAddIndex === index && (
                    <button
                      type="button"
                      className="add-btn"
                      onClick={() => addQuestion(index)}
                      disabled={!q.question.trim()}
                    >
                      + Add New Question
                    </button>
                  )}

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
                <Button
                  type="button"
                  className="cancel"
                  onClick={() => setShowCancelModal(true)}
                  disabled={!hasValidQuestion}
                >
                  {t('Cancel')}
                </Button>

                <Button
                  className="submit"
                  type="button"
                  onClick={handlePreview}
                  disabled={!hasValidQuestion}
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
      {showCancelModal && (
        <CenterModal
          handleModalLeftButtonClick={() => setShowCancelModal(false)}
          handleModalClose={() => setShowCancelModal(false)}
          handleModalSubmit={() => {
            setShowCancelModal(false);
            handleClose();
          }}
          modalHeading={t('Discard Changes?')}
          modalContent={t('Are you sure you want to discard your changes?')}
          modalType="discardModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
          modalLeftButtonText={t('No')}
          modalRightButtonText={t('Discard')}
        />
      )}
    </FormContainer>
  );
};

export default AddEvaluationCycle;
