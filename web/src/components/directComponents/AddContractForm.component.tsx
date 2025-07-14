import React, { useEffect, useRef, useState } from 'react';

import {
  // TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import {
  FormContainer,
  StepsContainer,
  StepWrapper,
  StepLabel,
  Line,
  AddFormMainContainer,
  FormInputsContainer,
  InputLabelContainer,
  LogoContainer,
  LogoLabel,
  LogoUploadContainer,
  UploadText,
  BrowseText,
  FileName,
  RemoveButton,
  FormInputs,
  AddClientButtons,
  FormResourceContainer,
} from '../../styles/ClientStyles.style';
import { UploadSVG, CheckIcon, LineIcon } from '../../svgs/ClientSvgs.svs';
import { Button } from '../../styles/CommonStyles.style';
import { ClientResponse } from '../../entities/ClientEntity';
import { useTranslation } from 'react-i18next';
import {
  DateInputWrapper,
  FormField,
  Label,
  RequiredAsterisk,
  SelectWrapper,
  TextArea,
} from '../../styles/ProjectStyles.style';
import { SearchSVG } from '../../svgs/NavBarSvgs.svg';
import { ButtonContainer } from '../../styles/SettingsStyles.style';
import {
  getAllProjects,
  getResourceManager,
  postContracts,
} from '../../service/axiosInstance';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import Calendar from '../reusableComponents/Calendar.component';
import {
  BillingCurrency,
  BillingCurrencyLabels,
  ContractBillingType,
  ContractBillingTypeLabels,
  ContractType,
  ContractTypeLabels,
} from '../reusableComponents/ContractEnums.component';
import { Employee, ProjectEntity } from '../../entities/ProjectEntity';
import { ContractDetails } from '../../entities/ContractEntiy';
import Select, { MultiValue } from 'react-select';
import {
  AllocatedInfo,
  AllocatedRow,
  AllocatedValue,
  AvailabilityInput,
  InitialCircle,
  ManageAllocationContainer,
  ManageHeader,
  NameBubble,
  NameBubbleListContainer,
  ResourceAllocationRow,
  ResourceAvailability,
  ResourceBlock,
  ResourceCard,
  ResourceLabel,
  ResourceListContainer,
  ResourceName,
  SaveButton,
  StyledResourceWrapper,
  TextInput,
  // StyledSelectWrapper,
} from '../../styles/AddContractFormStyles.style';

type AddContractFormProps = {
  handleClose: () => void;
  handleSuccessMessage: (msg: string) => void;
  // onSubmit: (data: ContractFormData, file: File | null) => void;
  // client: ClientResponse;
  initialData?: ContractDetails;
};

type OptionType = {
  value: string;
  label: string;
  availability?: number;
};

export type ContractFormData = {
  contractTitle: string;
  startDate: string;
  endDate: string;
  contractType: string;
  billingType?: string;
  billingCurrency?: string;
  contractValue?: string;
  description?: string;
  terms?: string;
  projectManagers: string[];
  Resources: OptionType[];
  projectName: string;
};

const AddContractForm: React.FC<AddContractFormProps> = ({
  handleClose,
  handleSuccessMessage,
}) => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState<ContractFormData>({
    contractTitle: '',
    startDate: '',
    endDate: '',
    contractType: '',
    billingType: '',
    billingCurrency: '',
    contractValue: '',
    description: '',
    terms: '',
    projectManagers: [],
    Resources: [],
    projectName: '',
  });
  const [file, setFile] = useState<File | null>(null);
  const [projectOptions, setProjectOptions] = useState<ProjectEntity[]>([]);
  const [resourceOptions, setResourceOptions] = useState<OptionType[]>([]);
  const [managerOptions, setManagerOptions] = useState<OptionType[]>([]);
  const [selectedResources, setSelectedResources] = useState<
    { value: string; label: string; availability: string }[]
  >([]);

  const [currentResource, setCurrentResource] = useState<string | null>(null);
  const [currentAvailability, setCurrentAvailability] = useState('');

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [isEndDateCalOpen, setIsEndDateCalOpen] = useState(false);
  const calendarRef = useRef<HTMLDivElement>(null);
  const calendarEndRef = useRef<HTMLDivElement>(null);
  const [errors, setErrors] = useState<{
    contractTitle?: string;
    startDate?: string;
    endDate?: string;
    contractType?: string;
  }>({});
  const formatDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-CA');
  };

  useEffect(() => {
    getAllProjects()
      .then((response) => {
        const data = response.data.projects;

        if (Array.isArray(data)) {
          setProjectOptions(data);
        } else {
          throw new Error('Invalid project data received.');
        }
      })
      .catch((error) => {
        setProjectOptions([]);
        throw new Error('Failed to fetch project list.' + error);
      });
  }, []);

  useEffect(() => {
    getResourceManager()
      .then((response) => {
        const users = response.data as Employee[];

        const managerOpts = users.map((user) => {
          const fullName = `${user.firstName} ${user.lastName}`;
          return {
            value: user.employeeId,
            label: fullName,
          };
        });

        const resourceOpts = users.map((user) => {
          const fullName = `${user.firstName} ${user.lastName}`;
          return {
            value: fullName,
            label: fullName,
            availability: user.availabilityPercentage ?? 0,
          };
        });

        setManagerOptions(managerOpts);
        setResourceOptions(resourceOpts);
      })
      .catch((error) => {
        throw new Error('Error fetching resource managers:' + error);
      });
  }, []);

  const handleNextStep = () => setStep((prev) => prev + 1);
  const handlePreviousStep = () => setStep((prev) => prev - 1);
  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  };

  const handleProjectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedName = e.target.value;
    const selectedProject = projectOptions.find((p) => p.name === selectedName);

    if (selectedProject) {
      setFormData((prev) => ({
        ...prev,
        projectName: selectedProject.name,
        projectId: selectedProject.projectId,
        clientId: selectedProject.clientId,
      }));
    }
  };

  const handleSubmitData = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const payload: any = {};

      Object.entries(formData).forEach(([key, value]) => {
        if (key === 'resourceAllocations' && Array.isArray(value)) {
          payload.resourceAllocations = value.map((resource: any) => ({
            value: resource.value,
            availability: Number(resource.availability),
          }));
        } else {
          payload[key] = value;
        }
      });

      await postContracts(payload);

      handleSuccessMessage('Contract saved successfully');
      handleClose();
    } catch (error) {
      throw new Error('Error submitting contract: ' + error);
    }
  };

  const validateStepOne = () => {
    const newErrors: typeof errors = {};

    if (!formData.contractTitle)
      newErrors.contractTitle = 'Please enter contract Name';
    if (!formData.startDate) newErrors.startDate = 'Please select Start Date';
    if (!formData.endDate) newErrors.endDate = 'Please select End Date';
    if (!formData.contractType)
      newErrors.contractType = 'Please select Contract Type';

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const { t } = useTranslation();

  return (
    <FormContainer>
      <>
        <StepsContainer>
          {['General Information', 'Project & Resource Allocation'].map(
            (label, index, arr) => {
              const isActive = step === index + 1;
              const isCompleted = step > index + 1;
              const isLast = index === arr.length - 1;
              return (
                <React.Fragment key={index}>
                  <StepWrapper>
                    <StepLabel isActive={isActive} isCompleted={isCompleted}>
                      <div className="circle">
                        {isCompleted ? <CheckIcon /> : index + 1}
                      </div>
                      <div className="labelHead">{label}</div>
                    </StepLabel>
                  </StepWrapper>
                  {!isLast && (
                    <Line>
                      <LineIcon />
                    </Line>
                  )}
                </React.Fragment>
              );
            }
          )}
        </StepsContainer>

        {step === 1 && (
          <AddFormMainContainer className="formBackground">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleNextStep();
              }}
            >
              <FormInputsContainer>
                <div>
                  <InputLabelContainer>
                    <label>
                      Contract Name
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      name="contractTitle"
                      type="text"
                      placeholder="Enter Contract Name"
                      value={formData.contractTitle}
                      onChange={(e) => {
                        handleChange(e);
                        if (errors.contractTitle) {
                          setErrors((prev) => ({
                            ...prev,
                            contractTitle: undefined,
                          }));
                        }
                      }}
                      required
                      style={{ width: '400px' }}
                    />
                    {errors.contractTitle && (
                      <ValidationText>{errors.contractTitle}</ValidationText>
                    )}
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>
                      Start Date
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <DateInputWrapper ref={calendarRef}>
                      <TextInput
                        type="text"
                        placeholder="Select Date"
                        name="startDate"
                        value={
                          formData.startDate
                            ? formatDate(formData.startDate)
                            : ''
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
                            title="Start Date"
                            minDate={new Date('01-01-2000')}
                            // maxDate={new Date()}
                            selectedDate={
                              formData.startDate
                                ? new Date(formData.startDate)
                                : null
                            }
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
                    <label>Billing Type</label>
                    <select
                      name="billingType"
                      value={formData.billingType}
                      onChange={handleChange}
                      className="selectoption largeSelectOption"
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Billing Type</option>
                      {Object.values(ContractBillingType).map((type) => (
                        <option key={type} value={type}>
                          {ContractBillingTypeLabels[type]}
                        </option>
                      ))}
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Budget</label>
                    <TextInput
                      type="text"
                      name="contractValue"
                      placeholder="Enter Budget"
                      value={formData.contractValue}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>

                <div>
                  <InputLabelContainer>
                    <label>
                      Contract Type
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <select
                      name="contractType"
                      value={formData.contractType}
                      onChange={(e) => {
                        handleChange(e);
                        if (errors.contractType) {
                          setErrors((prev) => ({
                            ...prev,
                            contractType: undefined,
                          }));
                        }
                      }}
                      className="selectoption largeSelectOption"
                      required
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Contract</option>
                      {Object.values(ContractType).map((type) => (
                        <option key={type} value={type}>
                          {ContractTypeLabels[type]}
                        </option>
                      ))}
                    </select>
                    {errors.contractType && (
                      <ValidationText>{errors.contractType}</ValidationText>
                    )}
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>
                      End Date
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <DateInputWrapper ref={calendarEndRef}>
                      <TextInput
                        type="text"
                        placeholder="Select Date"
                        name="endDate"
                        value={
                          formData.endDate ? formatDate(formData.endDate) : ''
                        }
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
                            title="End Date"
                            minDate={
                              formData.startDate
                                ? new Date(formData.startDate)
                                : new Date('2000-01-01')
                            }
                            selectedDate={
                              formData.endDate
                                ? new Date(formData.endDate)
                                : null
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

                  <InputLabelContainer>
                    <label>Billing Currency</label>
                    <select
                      name="billingCurrency"
                      value={formData.billingCurrency}
                      onChange={handleChange}
                      className="selectoption largeSelectOption"
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Currency</option>
                      {Object.values(BillingCurrency).map((currency) => (
                        <option key={currency} value={currency}>
                          {BillingCurrencyLabels[currency]}
                        </option>
                      ))}
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Description</label>
                    <TextArea
                      name="description"
                      placeholder="Enter Contract Description"
                      value={formData.description}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputsContainer>

              <LogoContainer>
                <LogoLabel>Attachments</LogoLabel>
                <LogoUploadContainer
                  onClick={() =>
                    document.getElementById('contractFile')?.click()
                  }
                >
                  <input
                    id="contractFile"
                    type="file"
                    accept=".pdf,.doc,.docx"
                    style={{ display: 'none' }}
                    onChange={handleFileChange}
                  />
                  <UploadSVG />
                  <UploadText>
                    Drag and drop or <BrowseText>Browse</BrowseText>
                  </UploadText>
                </LogoUploadContainer>
                {file && (
                  <LogoLabel>
                    <FileName>{file.name}</FileName>
                    <RemoveButton onClick={() => setFile(null)}>x</RemoveButton>
                  </LogoLabel>
                )}
              </LogoContainer>

              <div className="formButtons">
                <Button onClick={handleClose} type="button">
                  Cancel
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
                  Save & Continue
                </Button>
              </div>
            </form>
          </AddFormMainContainer>
        )}

        {step === 2 && (
          <form onSubmit={handleSubmitData}>
            <FormInputsContainer>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('Project Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <select
                    name="projectName"
                    value={formData.projectName}
                    onChange={handleProjectChange}
                    className="selectoption largeSelectOption"
                    required
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select Project')}</option>
                    {projectOptions?.map((project) => (
                      <option key={project.projectId} value={project.name}>
                        {project.name}
                      </option>
                    ))}
                  </select>
                </InputLabelContainer>
              </div>
              <div>
                <InputLabelContainer>
                  <SelectWrapper>
                    <FormField>
                      <Label>
                        {t('Project Managers')}
                        <RequiredAsterisk>*</RequiredAsterisk>
                      </Label>
                      <Select
                        isMulti
                        name="projectManagers"
                        value={managerOptions.filter((option) =>
                          formData.projectManagers.includes(option.value)
                        )}
                        options={managerOptions}
                        onChange={(
                          selected: MultiValue<{ value: string; label: string }>
                        ) => {
                          const values = selected.map((opt) => opt.value);
                          setFormData((prev) => ({
                            ...prev,
                            projectManagers: values,
                          }));
                        }}
                        classNamePrefix="react-select"
                        placeholder={t('Select Project Managers')}
                      />
                    </FormField>
                  </SelectWrapper>
                </InputLabelContainer>
              </div>
            </FormInputsContainer>

            <FormResourceContainer>
              <FormField>
                <Label>
                  Resources Allocation <RequiredAsterisk>*</RequiredAsterisk>
                </Label>

                <ResourceAllocationRow>
                  <StyledResourceWrapper>
                    <Select
                      classNamePrefix="react-select"
                      options={resourceOptions}
                      value={
                        resourceOptions.find(
                          (opt) => opt.value === currentResource
                        ) || null
                      }
                      onChange={(selected) =>
                        setCurrentResource(selected?.value || null)
                      }
                      placeholder="Search People"
                    />
                  </StyledResourceWrapper>

                  <AvailabilityInput
                    type="number"
                    placeholder="Enter Percentage"
                    value={currentAvailability}
                    onChange={(e) => setCurrentAvailability(e.target.value)}
                  />

                  <SaveButton
                    type="button"
                    onClick={() => {
                      if (!currentResource || !currentAvailability) return;

                      const label = resourceOptions.find(
                        (r) => r.value === currentResource
                      )?.label;

                      if (
                        selectedResources.some(
                          (r) => r.value === currentResource
                        )
                      ) {
                        alert('This person is already added.');
                        return;
                      }

                      setSelectedResources((prev: any) => {
                        const updated = [
                          ...prev,
                          {
                            value: currentResource,
                            label: label || '',
                            availability: Number(currentAvailability),
                          },
                        ];

                        setFormData((form) => ({
                          ...form,
                          Resources: updated,
                        }));

                        return updated;
                      });

                      setCurrentResource(null);
                      setCurrentAvailability('');
                    }}
                  >
                    Save
                  </SaveButton>
                </ResourceAllocationRow>
              </FormField>
            </FormResourceContainer>

            {selectedResources.length > 0 && (
              <ResourceBlock>
                <ResourceLabel>All Resources</ResourceLabel>
                <NameBubbleListContainer>
                  {selectedResources.map((option) => (
                    <NameBubble key={option.value}>{option.label}</NameBubble>
                  ))}
                </NameBubbleListContainer>
              </ResourceBlock>
            )}

            <ButtonContainer>
              <Button onClick={handlePreviousStep} className="leftAlign">
                <span className="separator">{`<`}</span> &nbsp;
                {t('Previous')}
              </Button>
              <Button className="submit" type="submit">
                {t('Submit')}
              </Button>
            </ButtonContainer>
          </form>
        )}
        {/* {showErrorMessage && (
          <ToastMessage
            messageType="error"
            messageBody={errorMessage}
            messageHeading="UPLOAD_UNSUCCESSFUL"
            handleClose={handleShowErrorMessage}
          />
        )}
        {isResponseLoading && <SpinAnimation />} */}
      </>
    </FormContainer>
  );
};

export default AddContractForm;
