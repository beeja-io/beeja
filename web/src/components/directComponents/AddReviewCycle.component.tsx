import React, { useEffect, useRef, useState } from 'react';
import { StepLabel, StepWrapper } from '../../styles/ClientStyles.style';
import { CheckIcon, DateIcon } from '../../svgs/ClientManagmentSvgs.svg';
import { useTranslation } from 'react-i18next';
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import Calendar from '../reusableComponents/Calendar.component';
import { CalenderIconDark, DeleteIcon } from '../../svgs/ExpenseListSvgs.svg';
import { Button } from '../../styles/CommonStyles.style';
import {
  RowWrapper,
  FormInputsContainer,
  AddFormMainContainer,
  QuestionBlock,
  FormHeader,
  FormLabelContainer,
  Label,
  InputContainer,
  DateInputWrapper,
  StyledInput,
  Container,
  FormContainer,
  StepsContainer,
  FooterContainer,
  ButtonGroup,
  InputLabelContainer,
  TextInput,
} from '../../styles/CreateReviewCycleStyle.style';
import DropdownMenu, {
  MultiSelectDropdown,
} from '../reusableComponents/DropDownMenu.component';
import {
  Department,
  DepartmentLabels,
  ReviewType,
  ReviewTypeLabels,
} from '../reusableComponents/PerformanceEnums.component';
import {
  Slider,
  StyledSwitch,
  SwitchLabel,
} from '../../styles/InputStyles.style';
import PreviewMode from '../reusableComponents/PreviewMode.component';
import CenterModal from '../reusableComponents/CenterModal.component';
import { getResourceManager } from '../../service/axiosInstance';
import { Employee } from '../../entities/ProjectEntity';
import { toast } from 'sonner';

type AddReviewCycleProps = {
  handleClose: () => void;
  // handleSuccessMessage: (type: 'add' | 'edit') => void;
  //   initialData?: ContractDetails;
};

type Question = {
  question: string;
  answer: string;
  required: boolean;
};
type OptionType = {
  value: string;
  label: string;
};
type FormData = {
  reviewCycleName: string;
  reviewType: string;
  startDate: string;
  endDate: string;
  formDescription: string;
  questions: Question[];
  department: string;
  managers: string[];
};

const AddReviewCycle: React.FC<AddReviewCycleProps> = ({ handleClose }) => {
  const [step, setStep] = useState(1);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [isEndDateCalOpen, setIsEndDateCalOpen] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string | undefined }>(
    {}
  );
  const calendarRef = useRef<HTMLDivElement>(null);
  const calendarEndRef = useRef<HTMLDivElement>(null);
  const { t } = useTranslation();
  const [managerOptions, setManagerOptions] = useState<OptionType[]>([]);

  useEffect(() => {
    getResourceManager()
      .then((response) => {
        const users = response.data as Employee[];
        const userOptions = users.map((user) => ({
          value: user.employeeId,
          label: `${user.firstName} ${user.lastName}`,
        }));
        setManagerOptions(userOptions);
      })
      .catch((error) => {
        toast.error('Error fetching managers', error);
      });
  }, []);

  const [formData, setFormData] = useState<FormData>({
    reviewCycleName: '',
    reviewType: '',
    startDate: '',
    endDate: '',
    formDescription: '',
    questions: [
      {
        question: '',
        answer: '',
        required: false,
      },
    ],
    department: '',
    managers: [],
  });
  const [previewMode, setPreviewMode] = useState(false);
  const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);
  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  const formatDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  const formatMonthDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: '2-digit',
      year: 'numeric',
    });
  };

  const validateStepOne = () => {
    const newErrors: typeof errors = {};

    if (!formData.reviewCycleName) {
      newErrors.reviewCycleName = 'Please enter Review Cycle Name';
    }

    if (!formData.reviewType) {
      newErrors.reviewType = 'Please select Review Type';
    }

    if (!formData.startDate) {
      newErrors.startDate = 'Please select Start Date';
    }

    if (!formData.endDate) {
      newErrors.endDate = 'Please select End Date';
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
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
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData((prev) => ({
      ...prev,
      formDescription: e.target.value,
    }));
  };

  const addQuestion = () => {
    setFormData((prev) => ({
      ...prev,
      questions: [
        ...prev.questions,
        { question: '', answer: '', required: false },
      ],
    }));
  };

  const deleteQuestion = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      questions: prev.questions.filter((_, i) => i !== index),
    }));
  };

  const handleQuestionChange = <FieldName extends keyof Question>(
    questionIndex: number,
    fieldName: FieldName,
    newValue: Question[FieldName]
  ) => {
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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateStepOne()) {
      return;
    }
  };

  const handleNextStep = () => setStep((prev) => prev + 1);
  const handlePreviousStep = () => setStep((prev) => prev - 1);

  return (
    <FormContainer>
      <>
        <StepsContainer>
          {['New_Review_Cycle', 'Setup_Review_Form', 'Assign_Manager'].map(
            (label, index) => {
              const isActive = step === index + 1;
              const isCompleted = step > index + 1;

              return (
                <React.Fragment key={index}>
                  <StepWrapper>
                    <StepLabel isActive={isActive} isCompleted={isCompleted}>
                      <div className="circle">
                        {isCompleted ? <CheckIcon /> : index + 1}
                      </div>
                      <div className="labelHead">{t(label)}</div>
                    </StepLabel>
                  </StepWrapper>
                </React.Fragment>
              );
            }
          )}
        </StepsContainer>
      </>
      {step === 1 && (
        <AddFormMainContainer
          className="formBackground"
          onSubmit={(e) => {
            e.preventDefault();
            handleNextStep();
          }}
        >
          <FormInputsContainer className="stepOne">
            <RowWrapper>
              <InputLabelContainer>
                <label>
                  {t('Review_Cycle_Name')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  placeholder="Enter Review Cycle Name"
                  value={formData.reviewCycleName}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      reviewCycleName: e.target.value,
                    }))
                  }
                  required
                />
                {errors.reviewCycleName && (
                  <ValidationText>{errors.reviewCycleName}</ValidationText>
                )}
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('Review Type')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('Select Review Type')}
                  name="reviewType"
                  id="reviewType"
                  // className="largeContainerExp"
                  value={formData.reviewType || ''}
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
                {errors.reviewType && (
                  <ValidationText>{errors.reviewType}</ValidationText>
                )}
              </InputLabelContainer>
            </RowWrapper>

            <RowWrapper>
              <InputLabelContainer>
                <label>
                  {t('Start Date')}
                  <ValidationText className="star">*</ValidationText>
                </label>

                <DateInputWrapper ref={calendarRef}>
                  <TextInput
                    type="text"
                    placeholder="Select Date"
                    name="startDate"
                    value={
                      formData.startDate ? formatDate(formData.startDate) : ''
                    }
                    onFocus={() => setIsStartDateCalOpen(true)}
                    onClick={() => setIsStartDateCalOpen(true)}
                    readOnly
                    autoComplete="off"
                  />
                  <span
                    className="iconArea"
                    onClick={() => setIsStartDateCalOpen(true)}
                  >
                    <CalenderIconDark />
                  </span>

                  <div className="calendarSpace">
                    {isStartDateCalOpen && (
                      <Calendar
                        title="From Date"
                        minDate={new Date('01-01-2000')}
                        selectedDate={
                          formData.startDate
                            ? new Date(formData.startDate)
                            : null
                        }
                        defaultMonth={new Date(new Date().getFullYear(), 0, 1)}
                        handleDateInput={(date: Date | null) => {
                          if (!date) return;
                          setFormData((prev) => ({
                            ...prev,
                            startDate: date.toLocaleDateString('en-CA'),
                          }));
                          setErrors((prev) => ({
                            ...prev,
                            startDate: undefined,
                          }));
                          setIsStartDateCalOpen(false);
                        }}
                        handleCalenderChange={() => {}}
                      />
                    )}
                  </div>
                </DateInputWrapper>
                {errors.startDate && (
                  <ValidationText>{errors.startDate}</ValidationText>
                )}
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('End Date')}
                  <ValidationText className="star">*</ValidationText>
                </label>

                <DateInputWrapper ref={calendarEndRef}>
                  <TextInput
                    type="text"
                    placeholder="Select Date"
                    name="endDate"
                    value={formData.endDate ? formatDate(formData.endDate) : ''}
                    onFocus={() => setIsEndDateCalOpen(true)}
                    onClick={() => setIsEndDateCalOpen(true)}
                    readOnly
                    autoComplete="off"
                  />
                  <span
                    className="iconArea"
                    onClick={() => setIsEndDateCalOpen(true)}
                  >
                    <CalenderIconDark />
                  </span>

                  <div className="calendarSpace">
                    {isEndDateCalOpen && (
                      <Calendar
                        title="To Date"
                        minDate={
                          formData.startDate
                            ? new Date(formData.startDate)
                            : new Date('2000-01-01')
                        }
                        selectedDate={
                          formData.endDate ? new Date(formData.endDate) : null
                        }
                        handleDateInput={(date: Date | null) => {
                          if (!date) return;

                          setFormData((prev) => ({
                            ...prev,
                            endDate: date.toLocaleDateString('en-CA'),
                          }));
                          setErrors((prev) => ({
                            ...prev,
                            endDate: undefined,
                          }));
                          setIsEndDateCalOpen(false);
                        }}
                        handleCalenderChange={() => {}}
                      />
                    )}
                  </div>
                </DateInputWrapper>
                {errors.endDate && (
                  <ValidationText>{errors.endDate}</ValidationText>
                )}
              </InputLabelContainer>
            </RowWrapper>
          </FormInputsContainer>

          <div className="formButtons">
            <Button onClick={handleClose} type="button">
              {t('Cancel')}
            </Button>
            <Button
              className="submit"
              type="button"
              onClick={() => {
                if (validateStepOne()) {
                  handleNextStep();
                }
              }}
            >
              {t('Save & Continue')}
            </Button>
          </div>
        </AddFormMainContainer>
      )}
      {step === 2 && (
        <FormInputsContainer className="stepTwoContainer">
          <FormHeader>
            <h2>
              {formData.reviewCycleName} -{' '}
              {new Date(formData.startDate).getFullYear()}
            </h2>
            <p>
              <DateIcon />
              <span>{formatMonthDate(formData.startDate)} </span> To{' '}
              <DateIcon />
              <span> {formatMonthDate(formData.endDate)}</span>
            </p>
            <h3>{formData.reviewType} Form</h3>
          </FormHeader>
          <Container>
            <Label>Form Description</Label>
            <FormLabelContainer>
              <InputContainer>
                <StyledInput
                  type="text"
                  placeholder="Description"
                  value={formData.formDescription}
                  onChange={handleFormDescriptionChange}
                />
              </InputContainer>
            </FormLabelContainer>

            {formData.questions.map((q: any, index: number) => (
              <QuestionBlock key={index}>
                <input
                  type="text"
                  className="question-input"
                  placeholder="Write your question here"
                  value={q.question}
                  onChange={(e) =>
                    handleQuestionChange(index, 'question', e.target.value)
                  }
                />
                <FormLabelContainer>
                  <InputContainer>
                    <StyledInput
                      type="text"
                      placeholder="Answer text"
                      value={q.answer}
                      onChange={(e) =>
                        handleQuestionChange(index, 'answer', e.target.value)
                      }
                    />
                  </InputContainer>
                </FormLabelContainer>

                <div className="question-footer">
                  <button
                    type="button"
                    className="add-btn"
                    onClick={addQuestion}
                  >
                    + Add
                  </button>

                  <div className="actions">
                    <button type="button" onClick={() => deleteQuestion(index)}>
                      <DeleteIcon />
                    </button>

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
                  </div>
                </div>
              </QuestionBlock>
            ))}

            <FooterContainer>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <ButtonGroup>
                <Button onClick={handleClose} type="button">
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={() => {
                    setPreviewMode(true);
                  }}
                >
                  {t('Save & Continue')}
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
          onConfirm={() => {
            setPreviewMode(false);
            handleNextStep();
          }}
        />
      )}

      {step === 3 && (
        <>
          <AddFormMainContainer onSubmit={handleSubmit}>
            <FormInputsContainer className="row-container">
              <InputLabelContainer>
                <label>
                  {t('Department')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('Select Department')}
                  name="department"
                  id="department"
                  value={formData.department || ''}
                  onChange={(e) => {
                    const event = {
                      target: {
                        name: 'department',
                        value: e,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>;
                    handleChange(event);
                  }}
                  options={[
                    { label: t('Select Department'), value: '' },
                    ...Object.values(Department).map((dept) => ({
                      label: DepartmentLabels[dept],
                      value: dept,
                    })),
                  ]}
                />
                {errors.department && (
                  <ValidationText>{errors.department}</ValidationText>
                )}
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('Managers')}
                  <ValidationText className="star">*</ValidationText>
                </label>

                <MultiSelectDropdown
                  options={managerOptions}
                  value={managerOptions.filter((option) =>
                    formData.managers.includes(option.value)
                  )}
                  onChange={(selected: OptionType[]) => {
                    const values = selected
                      .sort((a, b) => a.value.localeCompare(b.value))
                      .map((opt) => opt.value);

                    setFormData((prev) => ({
                      ...prev,
                      managers: values,
                    }));
                  }}
                  placeholder={t('Select Managers')}
                  searchable={true}
                />
              </InputLabelContainer>
            </FormInputsContainer>

            <FooterContainer>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <ButtonGroup>
                <Button onClick={handleDiscardModalToggle} type="button">
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={() => {
                    setPreviewMode(true);
                  }}
                >
                  {t('Save & Continue')}
                </Button>
              </ButtonGroup>
            </FooterContainer>
          </AddFormMainContainer>
          {isDiscardModalOpen && (
            <CenterModal
              handleModalLeftButtonClick={handleDiscardModalToggle}
              handleModalClose={handleDiscardModalToggle}
              handleModalSubmit={handleClose}
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
        </>
      )}
    </FormContainer>
  );
};

export default AddReviewCycle;
