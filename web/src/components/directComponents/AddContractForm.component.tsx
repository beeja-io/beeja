import React, { useEffect, useRef, useState } from 'react';

import { useTranslation } from 'react-i18next';
import { toast } from 'sonner';
import { ContractDetails } from '../../entities/ContractEntiy';
import { Employee, ProjectEntity } from '../../entities/ProjectEntity';
import {
  getAllProjects,
  getResourceManager,
  postContracts,
  updateContract,
} from '../../service/axiosInstance';
import {
  AvailabilityContainer,
  AvailabilityInput,
  NameBubble,
  NameBubbleListContainer,
  PercentageSign,
  ResourceAllocationRow,
  ResourceBlock,
  ResourceLabel,
  SaveButton,
  StyledResourceWrapper,
  TextInput,
} from '../../styles/AddContractFormStyles.style';
import {
  BrowseText,
  ButtonGroup,
  FileName,
  FormContainer,
  Line,
  LogoLabel,
  RemoveButton,
  StepLabel,
  StepWrapper,
  UploadText,
} from '../../styles/ClientStyles.style';
import { Button } from '../../styles/CommonStyles.style';
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import {
  DateInputWrapper,
  FormField,
  Label,
  RequiredAsterisk,
  TextArea,
} from '../../styles/ProjectStyles.style';
import {
  CheckIcon,
  LineIcon,
  UploadSVG,
} from '../../svgs/ClientManagmentSvgs.svg';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import Calendar from '../reusableComponents/Calendar.component';
import {
  BillingCurrency,
  BillingCurrencyLabels,
  ContractBillingType,
  ContractBillingTypeLabels,
  ContractType,
  ContractTypeLabels,
} from '../reusableComponents/ContractEnums.component';
import {
  AddFormMainContainer,
  ColumnWrapper,
  FormInputsContainer,
  InputLabelContainer,
  LogoUploadContainer,
  StepsContainer,
  FormResourceContainer,
  RowWrapper,
  ListWrapper,
  AddContractButtons,
} from '../../styles/ContractStyle.style';
import CenterModal from '../reusableComponents/CenterModal.component';
import DropdownMenu, {
  MultiSelectDropdown,
} from '../reusableComponents/DropDownMenu.component';

type AddContractFormProps = {
  handleClose: () => void;
  handleSuccessMessage: (value: string, type: 'add' | 'edit') => void;
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
  rawProjectResources: {
    value: string;
    label: string;
    availability: number;
  }[];
  projectName: string;
};

const AddContractForm: React.FC<AddContractFormProps> = ({
  handleClose,
  handleSuccessMessage,
  initialData,
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
    rawProjectResources: [],
    projectName: '',
  });
  const [file, setFile] = useState<File | null>(null);
  const [projectOptions, setProjectOptions] = useState<ProjectEntity[]>([]);
  const [resourceOptions, setResourceOptions] = useState<OptionType[]>([]);
  const [managerOptions, setManagerOptions] = useState<OptionType[]>([]);
  const [selectedResources, setSelectedResources] = useState<
    { value: string; label: string; availability: number }[]
  >([]);

  const [currentResource, setCurrentResource] = useState<string | null>(null);
  const [currentAvailability, setCurrentAvailability] = useState('');
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
  const [isOpen, setIsOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formInitialized, setFormInitialized] = useState(false);
  const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);

  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  useEffect(() => {
    getAllProjects()
      .then((response) => {
        const data = response.data.projects;

        if (Array.isArray(data)) {
          setProjectOptions(data);
        } else {
          toast.error(t('Invalid project data received.'));
        }
      })
      .catch((error) => {
        setProjectOptions([]);
        toast.error(t('Failed to fetch project list.') + error);
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
            value: user.employeeId,
            label: fullName,
            availability: user.availabilityPercentage ?? 0,
          };
        });

        setManagerOptions(managerOpts);
        setResourceOptions(resourceOpts);
      })
      .catch((error) => {
        toast.error('Error fetching resource managers:' + error);
      });
  }, []);
  useEffect(() => {
    if (
      initialData &&
      !formInitialized &&
      managerOptions.length > 0 &&
      resourceOptions.length > 0 &&
      projectOptions.length > 0
    ) {
      const mappedManagers = managerOptions.filter((manager) =>
        initialData.projectManagers?.includes(manager.value)
      );

      const mappedResources =
        initialData.rawProjectResources?.map((r) => ({
          value: r.employeeId,
          label: r.name,
          availability: r.allocationPercentage ?? 0,
        })) || [];

      const matchedProject = projectOptions.find(
        (p) => p.projectId === initialData.projectId
      );

      setFormData({
        contractTitle: initialData.contractTitle || '',
        startDate: initialData.startDate || '',
        endDate: initialData.endDate || '',
        contractType: initialData.contractType || '',
        billingType: initialData.billingType || '',
        billingCurrency: initialData.billingCurrency || '',
        contractValue: initialData.contractValue?.toString() || '',
        description: initialData.description || '',
        terms: '',
        projectManagers: mappedManagers.map((m) => m.value),
        rawProjectResources: mappedResources,
        projectName: matchedProject?.name || '',
      });

      setSelectedResources(mappedResources);
      setFormInitialized(true);
    }
  }, [
    initialData,
    managerOptions,
    resourceOptions,
    projectOptions,
    formInitialized,
  ]);

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
    setIsSubmitting(true);
    try {
      const payload: any = {};

      Object.entries(formData).forEach(([key, value]) => {
        if (key === 'resourceAllocations' && Array.isArray(value)) {
          payload.projectResources = value.map((resource: any) => ({
            employeeId: resource.value,
            allocationPercentage: Number(resource.availability || 0),
          }));
        } else {
          payload[key] = value;
        }
      });

      if (initialData?.contractId) {
        await updateContract(initialData.contractId, payload);
        handleSuccessMessage('Contract has been successfully updated.', 'edit');
      } else {
        const response = await postContracts(payload);
        const contractId = response?.data?.contractId;

        if (contractId) {
          handleSuccessMessage(contractId, 'add');
        } else {
          toast.warning(
            t('Contract created, but ID is missing in the response.')
          );
        }
      }
    } catch (error) {
      toast.error(t('An error occurred while submitting the contract.'));
    } finally {
      setIsSubmitting(false);
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
  const isFormReady =
    !initialData ||
    (initialData &&
      managerOptions.length > 0 &&
      resourceOptions.length > 0 &&
      projectOptions.length > 0 &&
      formInitialized);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        calendarRef.current &&
        !calendarRef.current.contains(event.target as Node)
      ) {
        setIsStartDateCalOpen(false);
      }

      if (
        calendarEndRef.current &&
        !calendarEndRef.current.contains(event.target as Node)
      ) {
        setIsEndDateCalOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  if (isSubmitting || !isFormReady) {
    return <SpinAnimation />;
  }
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
          <AddFormMainContainer
            className="formBackground"
            onSubmit={(e) => {
              e.preventDefault();
              handleNextStep();
            }}
          >
            <FormInputsContainer className="step-one">
              <ColumnWrapper>
                {initialData?.contractId && (
                  <InputLabelContainer>
                    <label>{t('Contract ID')}</label>
                    <TextInput
                      type="text"
                      value={initialData.contractId}
                      disabled
                    />
                  </InputLabelContainer>
                )}
                <InputLabelContainer>
                  <label>
                    {t('Contract Name')}
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
                  />
                  {errors.contractTitle && (
                    <ValidationText>{errors.contractTitle}</ValidationText>
                  )}
                </InputLabelContainer>

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
                          title="Start Date"
                          minDate={new Date('01-01-2000')}
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
                  <label>{t('Billing Type')}</label>
                  <DropdownMenu
                    label={t('Select Billing Type')}
                    name="billingType"
                    id="billingType"
                    className="largeContainerExp"
                    value={formData.billingType || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'billingType',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleChange(event);
                    }}
                    options={[
                      { label: t('Select Billing Type'), value: '' },
                      ...Object.values(ContractBillingType).map((type) => ({
                        label: ContractBillingTypeLabels[type],
                        value: type,
                      })),
                    ]}
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>{t('Budget')}</label>
                  <TextInput
                    type="text"
                    name="contractValue"
                    placeholder="Enter Budget"
                    value={formData.contractValue}
                    onChange={handleChange}
                  />
                </InputLabelContainer>

                {!initialData?.contractId && (
                  <div>
                    <LogoLabel>{t('Attachments')}</LogoLabel>
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
                        {t('Drag and drop or')}{' '}
                        <BrowseText>{t('Browse')}</BrowseText>
                      </UploadText>
                    </LogoUploadContainer>
                    {file && (
                      <LogoLabel>
                        <FileName>{file.name}</FileName>
                        <RemoveButton onClick={() => setFile(null)}>
                          x
                        </RemoveButton>
                      </LogoLabel>
                    )}
                  </div>
                )}
              </ColumnWrapper>

              <ColumnWrapper>
                <InputLabelContainer>
                  <label>
                    {t('Contract Type')}
                    <ValidationText className="star">*</ValidationText>
                  </label>

                  <DropdownMenu
                    label="Select Contract"
                    name="contractType"
                    id="contractType"
                    className="largeContainerExp"
                    value={formData.contractType || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'contractType',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;

                      handleChange(event);

                      if (errors.contractType) {
                        setErrors((prev) => ({
                          ...prev,
                          contractType: undefined,
                        }));
                      }
                    }}
                    required={true}
                    options={[
                      { label: 'Select Contract', value: '' },
                      ...Object.values(ContractType).map((type) => ({
                        label: ContractTypeLabels[type],
                        value: type,
                      })),
                    ]}
                  />
                  {errors.contractType && (
                    <ValidationText>{errors.contractType}</ValidationText>
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

                <InputLabelContainer>
                  <label>{t('Billing Currency')}</label>
                  <DropdownMenu
                    label="Select Currency"
                    name="billingCurrency"
                    id="billingCurrency"
                    className="largeContainerExp"
                    value={formData.billingCurrency || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'billingCurrency',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleChange(event);
                    }}
                    options={[
                      { label: 'Select Currency', value: '' },
                      ...Object.values(BillingCurrency).map((currency) => ({
                        label: BillingCurrencyLabels[currency],
                        value: currency,
                      })),
                    ]}
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>{t('Description')}</label>
                  <TextArea
                    name="description"
                    placeholder="Enter Contract Description"
                    value={formData.description}
                    onChange={handleChange}
                  />
                </InputLabelContainer>
                {initialData?.contractId && (
                  <div
                    style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '10px',
                    }}
                  >
                    <LogoLabel>{t('Attachments')}</LogoLabel>
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
                        {t('Drag and drop or')}{' '}
                        <BrowseText>{t('Browse')}</BrowseText>
                      </UploadText>
                    </LogoUploadContainer>
                    {file && (
                      <LogoLabel>
                        <FileName>{file.name}</FileName>
                        <RemoveButton onClick={() => setFile(null)}>
                          x
                        </RemoveButton>
                      </LogoLabel>
                    )}
                  </div>
                )}
              </ColumnWrapper>
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
          <form onSubmit={handleSubmitData}>
            <FormInputsContainer>
              <RowWrapper>
                <InputLabelContainer>
                  <label>
                    {t('Project Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <DropdownMenu
                    label={t('Select Project')}
                    name="projectName"
                    id="projectName"
                    className="largeContainerExp"
                    value={formData.projectName || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'projectName',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleProjectChange(event);
                    }}
                    required={true}
                    options={[
                      { label: t('Select Project'), value: '' },
                      ...(projectOptions?.map((project) => ({
                        label: project.name,
                        value: project.name,
                      })) || []),
                    ]}
                  />
                </InputLabelContainer>

                <InputLabelContainer>
                  <Label>
                    {t('Project Managers')}
                    <RequiredAsterisk>*</RequiredAsterisk>
                  </Label>
                  <MultiSelectDropdown
                    options={managerOptions}
                    value={managerOptions.filter((option) =>
                      formData.projectManagers.includes(option.value)
                    )}
                    onChange={(selected) => {
                      const values = [...selected]
                        .sort((a, b) => a.value.localeCompare(b.value))
                        .map((opt) => opt.value);
                      setFormData((prev) => ({
                        ...prev,
                        projectManagers: values,
                      }));
                    }}
                    placeholder={t('Select Project Managers')}
                    searchable={true}
                  />
                </InputLabelContainer>
              </RowWrapper>
            </FormInputsContainer>

            <FormResourceContainer>
              <RowWrapper>
                <FormField>
                  <Label>
                    {t('Resources Allocation')}
                    <RequiredAsterisk>*</RequiredAsterisk>
                  </Label>

                  <ResourceAllocationRow>
                    <StyledResourceWrapper>
                      <DropdownMenu
                        label="Search People"
                        name="currentResource"
                        id="currentResource"
                        className="largeContainerRes"
                        value={currentResource || ''}
                        onChange={(e) => {
                          const event = {
                            target: {
                              name: 'currentResource',
                              value: e,
                            },
                          } as React.ChangeEvent<HTMLSelectElement>;
                          setCurrentResource(event.target.value || null);
                        }}
                        options={[
                          ...resourceOptions.map((opt) => ({
                            label: opt.label,
                            value: opt.value,
                          })),
                        ]}
                      />
                    </StyledResourceWrapper>

                    <AvailabilityContainer>
                      <AvailabilityInput
                        type="number"
                        min="1"
                        max="100"
                        placeholder="Enter Percentage"
                        value={currentAvailability}
                        onChange={(e) => setCurrentAvailability(e.target.value)}
                      />
                      <PercentageSign>%</PercentageSign>
                    </AvailabilityContainer>

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
                          toast.warning('This person is already added.');
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
                            resourceAllocations: updated,
                          }));

                          return updated;
                        });

                        setCurrentResource(null);
                        setCurrentAvailability('');
                      }}
                    >
                      {t('Save')}
                    </SaveButton>
                  </ResourceAllocationRow>
                </FormField>
              </RowWrapper>
            </FormResourceContainer>

            {selectedResources.length > 0 && (
              <ResourceBlock>
                <ListWrapper>
                  <ResourceLabel>{t('All Resources')}</ResourceLabel>
                  <NameBubbleListContainer>
                    {[...(selectedResources || [])]
                      .sort((a, b) => a.value.localeCompare(b.value))
                      .map((option) => (
                        <NameBubble key={option.value}>
                          <span className="name">{option.label}</span>
                        </NameBubble>
                      ))}
                  </NameBubbleListContainer>
                </ListWrapper>
                <ListWrapper className="container">
                  <ResourceLabel
                    className="ManageResource"
                    onClick={() => setIsOpen((prev) => !prev)}
                  >
                    <span>{t('Manage Resource Allocation')} </span>
                    <span className={`arrow ${isOpen ? 'open' : ''}`}>❮</span>
                  </ResourceLabel>
                  {isOpen && (
                    <NameBubbleListContainer className="manageResourceList">
                      {[...(selectedResources || [])]
                        .sort((a, b) => a.value.localeCompare(b.value))
                        .map((option) => (
                          <NameBubble key={option.value}>
                            <span className="name">{option.label}</span>
                            <span className="percentageAvailability">
                              <span className="availability">
                                Allocation: {option.availability}%
                              </span>
                              <button
                                className="remove-btn"
                                onClick={() =>
                                  setSelectedResources((prev) =>
                                    prev.filter(
                                      (item) => item.value !== option.value
                                    )
                                  )
                                }
                              >
                                ✕
                              </button>
                            </span>
                          </NameBubble>
                        ))}
                    </NameBubbleListContainer>
                  )}
                </ListWrapper>
              </ResourceBlock>
            )}

            <AddContractButtons>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <ButtonGroup>
                <Button onClick={handleDiscardModalToggle} type="button">
                  {t('Cancel')}
                </Button>
                <Button className="submit" type="submit">
                  {t('Submit')}
                </Button>
              </ButtonGroup>
            </AddContractButtons>
          </form>
        )}
      </>
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
    </FormContainer>
  );
};

export default AddContractForm;
