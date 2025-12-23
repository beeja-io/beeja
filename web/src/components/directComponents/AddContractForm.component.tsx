import React, { useEffect, useRef, useState } from 'react';

import { useTranslation } from 'react-i18next';
import { toast } from 'sonner';
import { ContractDetails } from '../../entities/ContractEntiy';
import { ProjectEntity } from '../../entities/ProjectEntity';
import {
  getProjectDropdown,
  getProjectEmployees,
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
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import {
  DateInputWrapper,
  FormField,
  Label,
  RequiredAsterisk,
  Button,
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
  AttachmentWrapper,
  FileLists,
} from '../../styles/ContractStyle.style';
import CenterModal from '../reusableComponents/CenterModal.component';
import DropdownMenu, {
  MultiSelectDropdown,
} from '../reusableComponents/DropDownMenu.component';

type AddContractFormProps = {
  handleClose: () => void;
  handleSuccessMessage: (value: string, type: 'add' | 'edit') => void;
  initialData?: ContractDetails;
  isEditMode?: boolean;
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
  customContractType: string;
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
  attachments?: (File | string)[];
  attachmentIds?: string[];
  projectId?: string;
  clientId?: string;
};

const AddContractForm: React.FC<AddContractFormProps> = ({
  handleClose,
  handleSuccessMessage,
  initialData,
  isEditMode,
}) => {
  const { t } = useTranslation();
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState<ContractFormData>({
    contractTitle: '',
    startDate: '',
    endDate: '',
    contractType: '',
    customContractType: '',
    billingType: '',
    billingCurrency: '',
    contractValue: '',
    description: '',
    terms: '',
    projectManagers: [],
    rawProjectResources: [],
    projectName: '',
    projectId: '',
    clientId: '',
    attachments: [],
    attachmentIds: [],
  });
  const [files, setFiles] = useState<File[]>([]);
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
    customContractType?: string;
    billingType?: string;
    billingCurrency?: string;
  }>({});
  const [errorsSteptwo, setErrorsSteptwo] = useState<{
    projectName?: string;
    selectedResources?: string;
    projectManagers?: string;
    availability?: string;
  }>({});
  const formatDate = (date: Date) => {
    const day = String(date.getDate()).padStart(2, '0');
    const month = date.toLocaleString('en-US', { month: 'short' });
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  };
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formInitialized, setFormInitialized] = useState(false);
  const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);
  const isDisabled = formData.billingType === ContractBillingType.NON_BILLABLE;

  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  const [isProjectLoading, setIsProjectLoading] = useState(false);

  useEffect(() => {
    getProjectDropdown()
      .then((response) => {
        const data = response.data;
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
  }, [t]);

  useEffect(() => {
    if (
      initialData &&
      !formInitialized &&
      projectOptions.length > 0 &&
      managerOptions.length > 0
    ) {
      const matchedProject = projectOptions.find(
        (p) => p.projectId === initialData.projectId
      );

      const mappedManagers = (initialData.projectManagers || []).map((id) => {
        const manager = managerOptions.find(
          (m) => m.value === id || m.label === id
        );
        return manager || { value: id, label: id };
      });
      const mappedResources =
        (initialData.rawProjectResources || []).map((r) => ({
          value: r.employeeId,
          label: r.name,
          availability: r.allocationPercentage ?? 0,
        })) || [];

      setFormData({
        contractTitle: initialData.contractTitle || '',
        startDate: initialData.startDate || '',
        endDate: initialData.endDate || '',
        contractType: initialData.contractType || '',
        customContractType: initialData.customContractType || '',
        billingType: initialData.billingType || '',
        billingCurrency: initialData.billingCurrency || '',
        contractValue: initialData.contractValue?.toString() || '',
        description: initialData.description || '',
        terms: '',
        projectManagers: mappedManagers
          .filter((m): m is { value: string; label: string } => !!m)
          .map((m) => m.value),
        rawProjectResources: mappedResources,
        projectName: matchedProject?.name || '',
        attachments: initialData.attachmentIds || [],
        attachmentIds: initialData.attachmentIds ?? [],
        projectId: initialData.projectId || '',
        clientId: matchedProject?.clientId || '',
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

    setErrors((prev) => ({
      ...prev,
      [name]: undefined,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const newFiles = Array.from(e.target.files);
      setFiles((prevFiles) => [...prevFiles, ...newFiles]);
      e.target.value = '';
    }
  };

  const handleProjectChange = async (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const selectedName = e.target.value;
    const selectedProject = projectOptions.find((p) => p.name === selectedName);

    if (selectedProject) {
      setFormData((prev) => ({
        ...prev,
        projectName: selectedProject.name,
        projectId: selectedProject.projectId,
        clientId: selectedProject.clientId,
        projectManagers: [],
        rawProjectResources: [],
      }));
      setSelectedResources([]);
      setIsProjectLoading(true);
      try {
        const response = await getProjectEmployees(selectedProject.projectId);

        const managers = (response.data.managers || []).map((m: any) => ({
          value: m.employeeId,
          label: m.fullName,
        }));

        const resources = (response.data.resources || []).map((r: any) => ({
          value: r.employeeId,
          label: r.fullName,
        }));

        setManagerOptions(managers);
        setResourceOptions(resources);
      } catch (error) {
        toast.error(t('FAILED_TO_FETCH_PROJECT_EMPLOYEES'));
        setManagerOptions([]);
        setResourceOptions([]);
      } finally {
        setIsProjectLoading(false);
      }
    } else {
      setFormData((prev) => ({
        ...prev,
        projectName: '',
        projectManagers: [],
      }));
    }
  };

  const validateStepTwo = () => {
    const newErrors1: typeof errorsSteptwo = {};
    if (!formData.projectName || formData.projectName === null) {
      newErrors1.projectName = 'Please select a Project';
    }
    if (!formData.projectManagers || formData.projectManagers.length === 0) {
      newErrors1.projectManagers = 'Please select at least one Project Manager';
    }
    if (!selectedResources || selectedResources.length === 0) {
      newErrors1.selectedResources =
        'Please add at least one Resource Allocation';
    }

    setErrorsSteptwo(newErrors1);

    return Object.keys(newErrors1).length === 0;
  };

  const validateAvailabilityInput = (rawValue: string) => {
    if (rawValue.trim() === '') return { cleanedValue: '', errorMessage: '' };

    if (!/^\d+$/.test(rawValue))
      return {
        cleanedValue: rawValue.replace(/\D/g, ''),
        errorMessage: t('availability.invalid_number'),
      };

    const value = Number(rawValue.replace(/^0+/, '') || '0');

    if (value < 1)
      return {
        cleanedValue: String(value || ''),
        errorMessage: t('availability.below_one'),
      };
    if (value > 100)
      return {
        cleanedValue: String(value),
        errorMessage: t('availability.above_hundred'),
      };

    return { cleanedValue: String(value), errorMessage: '' };
  };

  const handleSubmitData = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validateStepTwo()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const payload: any = { ...formData };
      payload.attachmentIds = Array.isArray(payload.attachmentIds)
        ? payload.attachmentIds.filter((id: string) => id && id.trim() !== '')
        : [];

      const processCustomField = (
        payload: any,
        enumField: 'contractType',
        customField: 'customContractType'
      ) => {
        const enumValue = payload[enumField];
        const customValue = payload[customField];

        if (enumValue === ContractType.OTHER) {
          payload[enumField] = 'OTHER';
          payload[customField] = customValue ? customValue.trim() : '';
        } else {
          payload[customField] = '';
        }
      };

      processCustomField(payload, 'contractType', 'customContractType');
      const formDataToSend = new FormData();

      Object.entries(payload).forEach(([key, value]) => {
        if (key === 'startDate' || key === 'endDate' || key === 'attachments') {
          return;
        }

        if (key === 'resourceAllocations' && Array.isArray(value)) {
          value.forEach((resource: any, index: number) => {
            formDataToSend.append(
              `projectResources[${index}].employeeId`,
              resource.value
            );
            formDataToSend.append(
              `projectResources[${index}].allocationPercentage`,
              String(Number(resource.availability || 0))
            );
          });
        } else if (Array.isArray(value)) {
          if (key === 'attachmentIds') {
            if (value.length > 0) {
              value.forEach((val: any) => {
                formDataToSend.append('attachmentIds', val);
              });
            } else {
              formDataToSend.append('attachmentIds', '');
            }
          } else {
            value.forEach((val: any) => {
              formDataToSend.append(key, val);
            });
          }
        } else if (value !== undefined && value !== null) {
          formDataToSend.append(key, value as any);
        }
      });
      if (Array.isArray(files)) {
        const validFiles = files.filter((f) => f instanceof File);
        if (validFiles.length > 0) {
          validFiles.forEach((f) => {
            formDataToSend.append('attachments', f);
          });
        }
      }

      if (formData.startDate) {
        formDataToSend.append(
          'startDate',
          new Date(formData.startDate).toISOString().split('T')[0]
        );
      }
      if (formData.endDate) {
        formDataToSend.append(
          'endDate',
          new Date(formData.endDate).toISOString().split('T')[0]
        );
      }

      if (initialData?.contractId) {
        await updateContract(initialData.contractId, formDataToSend);
        handleSuccessMessage('Contract has been successfully updated.', 'edit');
      } else {
        const response = await postContracts(formDataToSend);
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
      newErrors.contractTitle = t('PLEASE_ENTER_CONTRACT_NAME');

    if (!formData.startDate)
      newErrors.startDate = t('PLEASE_SELECT_START_DATE');

    if (!formData.contractType)
      newErrors.contractType = t('PLEASE_SELECT_CONTRACT_TYPE');

    if (!formData.billingType)
      newErrors.billingType = t('PLEASE_SELECT_BILLING_TYPE');

    if (formData.billingType !== ContractBillingType.NON_BILLABLE) {
      if (!formData.billingCurrency) {
        newErrors.billingCurrency = t('PLEASE_SELECT_BILLING_CURRENCY');
      }
    }
    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  useEffect(() => {
    if (initialData?.projectId && !formInitialized) {
      (async () => {
        try {
          setIsProjectLoading(true);
          const response = await getProjectEmployees(initialData.projectId);
          const managers = (response.data.managers || []).map((m: any) => ({
            value: m.employeeId,
            label: m.fullName,
          }));

          const resources = (response.data.resources || []).map((r: any) => ({
            value: r.employeeId,
            label: r.fullName,
            availability: r.allocationPercentage ?? 0,
          }));

          setManagerOptions(managers);
          setResourceOptions(resources);
        } catch (error) {
          toast.error('Failed to fetch project employees');
          setManagerOptions([]);
          setResourceOptions([]);
        } finally {
          setIsProjectLoading(false);
        }
      })();
    }
  }, [initialData?.projectId, formInitialized]);

  const isFormReady =
    !initialData ||
    (initialData && formInitialized && projectOptions.length > 0);

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

  useEffect(() => {
    if (formData.billingType === ContractBillingType.NON_BILLABLE) {
      setFormData((prev) => ({
        ...prev,
        contractValue: '',
        billingCurrency: '',
      }));
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors.billingCurrency;
        return newErrors;
      });
    }
  }, [formData.billingType]);

  if (isSubmitting || !isFormReady) {
    return <SpinAnimation />;
  }
  return (
    <FormContainer>
      <>
        <StepsContainer>
          {[t('GENERAL_INFORMATION'), t('PROJECT_AND_RESOURCE_ALLOCATION')].map(
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
                      className="disabled"
                      disabled
                    />
                  </InputLabelContainer>
                )}
                {!initialData?.contractId && (
                  <InputLabelContainer>
                    <label>
                      {t('Contract Name')}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      name="contractTitle"
                      type="text"
                      placeholder={t('Enter Contract Name')}
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
                )}
                {initialData?.contractId && (
                  <InputLabelContainer>
                    <label>
                      {t('Contract Type')}
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <DropdownMenu
                      label={t('Select Contract')}
                      name="contractType"
                      id="contractType"
                      className="largeContainerExp largeContainerHei"
                      value={
                        formData.contractType === ContractType.OTHER &&
                        formData.customContractType
                          ? formData.customContractType
                          : formData.contractType || null
                      }
                      onChange={(selectedValue) => {
                        if (selectedValue === ContractType.OTHER) {
                          setFormData((prev) => ({
                            ...prev,
                            contractType: ContractType.OTHER,
                            customContractType: '',
                          }));
                        } else {
                          setFormData((prev) => ({
                            ...prev,
                            contractType: selectedValue as ContractType,
                            customContractType: '',
                          }));
                        }

                        if (errors.contractType) {
                          setErrors((prev) => ({
                            ...prev,
                            contractType: undefined,
                          }));
                        }
                      }}
                      onCustomValue={(customValue) => {
                        setFormData((prev) => ({
                          ...prev,
                          contractType: ContractType.OTHER,
                          customContractType: customValue,
                        }));
                        setErrors((prev) => {
                          const newErrors = { ...prev };
                          delete newErrors.contractType;
                          delete newErrors.customContractType;
                          return newErrors;
                        });
                      }}
                      required
                      options={[
                        ...Object.values(ContractType).map((type) => ({
                          label:
                            type === 'OTHER'
                              ? 'Other'
                              : ContractTypeLabels[type],
                          value: type,
                        })),
                      ]}
                    />
                    {errors.contractType && (
                      <ValidationText>{errors.contractType}</ValidationText>
                    )}
                  </InputLabelContainer>
                )}

                <InputLabelContainer>
                  <label>
                    {t('Start Date')}
                    <ValidationText className="star">*</ValidationText>
                  </label>

                  <DateInputWrapper ref={calendarRef}>
                    <TextInput
                      type="text"
                      placeholder={t('Select Date')}
                      name="startDate"
                      value={
                        formData.startDate
                          ? formatDate(new Date(formData.startDate))
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
                  <label>
                    {t('Billing Type')}
                    <ValidationText className="star">*</ValidationText>
                  </label>

                  <DropdownMenu
                    label={t('Select Billing Type')}
                    name="billingType"
                    id="billingType"
                    className="largeContainerExp largeContainerHei"
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
                  {errors.billingType && (
                    <ValidationText>{errors.billingType}</ValidationText>
                  )}
                </InputLabelContainer>

                {initialData?.contractId && (
                  <InputLabelContainer>
                    <label>{t('Description')}</label>
                    <TextInput
                      name="description"
                      placeholder="Enter Contract Description"
                      value={formData.description}
                      onChange={handleChange}
                      className="largeInput"
                    />
                  </InputLabelContainer>
                )}
                {!initialData?.contractId && (
                  <InputLabelContainer>
                    <label>{t('Budget')}</label>
                    <TextInput
                      type="text"
                      name="contractValue"
                      placeholder={t('Enter Budget')}
                      value={formData.contractValue}
                      onChange={(e) => {
                        if (
                          formData.billingType !==
                          ContractBillingType.NON_BILLABLE
                        )
                          handleChange(e);
                      }}
                      disabled={
                        formData.billingType ===
                        ContractBillingType.NON_BILLABLE
                      }
                      readOnly={
                        formData.billingType ===
                        ContractBillingType.NON_BILLABLE
                      }
                      className="largeInput"
                    />
                  </InputLabelContainer>
                )}

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
                        multiple
                        style={{ display: 'none' }}
                        onChange={handleFileChange}
                      />
                      <UploadSVG />
                      <UploadText>
                        {t('Drag and drop or')}{' '}
                        <BrowseText>{t('Browse')}</BrowseText>
                      </UploadText>
                    </LogoUploadContainer>
                    {files.length > 0 && (
                      <FileLists>
                        {files.map((f, idx) => (
                          <FileName key={idx}>
                            {f.name}
                            <RemoveButton
                              onClick={() =>
                                setFiles((prev) =>
                                  prev.filter((_, i) => i !== idx)
                                )
                              }
                            >
                              x
                            </RemoveButton>
                          </FileName>
                        ))}
                      </FileLists>
                    )}
                    <span className="infoText-contract">
                      {t('File format')} : .pdf, .doc, .docx
                    </span>
                  </div>
                )}
              </ColumnWrapper>

              <ColumnWrapper>
                {initialData?.contractId && (
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
                )}
                {initialData?.contractId && (
                  <InputLabelContainer>
                    <label>{t('Budget')}</label>
                    <TextInput
                      type="text"
                      name="contractValue"
                      placeholder="Enter Budget"
                      value={formData.contractValue}
                      onChange={(e) => {
                        if (formData.billingType !== 'NON-BILLABLE')
                          handleChange(e);
                      }}
                      disabled={
                        formData.billingType ===
                        ContractBillingType.NON_BILLABLE
                      }
                      readOnly={
                        formData.billingType ===
                        ContractBillingType.NON_BILLABLE
                      }
                      className="largeInput"
                    />
                  </InputLabelContainer>
                )}
                {!initialData?.contractId && (
                  <InputLabelContainer>
                    <label>
                      {t('Contract Type')}
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <DropdownMenu
                      label="Select Contract"
                      name="contractType"
                      id="contractType"
                      className="largeContainerExp largeContainerHei"
                      value={
                        formData.contractType === ContractType.OTHER &&
                        formData.customContractType
                          ? formData.customContractType
                          : formData.contractType || null
                      }
                      onChange={(selectedValue) => {
                        if (selectedValue === ContractType.OTHER) {
                          setFormData((prev) => ({
                            ...prev,
                            contractType: ContractType.OTHER,
                            customContractType: '',
                          }));
                        } else {
                          setFormData((prev) => ({
                            ...prev,
                            contractType: selectedValue as ContractType,
                            customContractType: '',
                          }));
                        }

                        if (errors.contractType) {
                          setErrors((prev) => ({
                            ...prev,
                            contractType: undefined,
                          }));
                        }
                      }}
                      onCustomValue={(customValue) => {
                        setFormData((prev) => ({
                          ...prev,
                          contractType: ContractType.OTHER,
                          customContractType: customValue,
                        }));
                        setErrors((prev) => {
                          const newErrors = { ...prev };
                          delete newErrors.contractType;
                          delete newErrors.customContractType;
                          return newErrors;
                        });
                      }}
                      required
                      options={[
                        ...Object.values(ContractType).map((type) => ({
                          label:
                            type === 'OTHER'
                              ? 'Other'
                              : ContractTypeLabels[type],
                          value: type,
                        })),
                      ]}
                    />

                    {errors.contractType && (
                      <ValidationText>{errors.contractType}</ValidationText>
                    )}
                  </InputLabelContainer>
                )}

                <InputLabelContainer>
                  <label>{t('End Date')}</label>

                  <DateInputWrapper ref={calendarEndRef}>
                    <TextInput
                      type="text"
                      placeholder={t('Select Date')}
                      name="endDate"
                      value={
                        formData.endDate
                          ? formatDate(new Date(formData.endDate))
                          : ''
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
                  <label>
                    {t('Billing Currency')}
                    {formData.billingType !==
                      ContractBillingType.NON_BILLABLE && (
                      <ValidationText className="star">*</ValidationText>
                    )}
                  </label>
                  <DropdownMenu
                    label={t('Select Currency')}
                    name="billingCurrency"
                    id="billingCurrency"
                    className="largeContainerExp largeContainerHei"
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
                    disabled={isDisabled}
                    options={[
                      { label: t('Select Currency'), value: '' },
                      ...Object.values(BillingCurrency).map((currency) => ({
                        label: BillingCurrencyLabels[currency],
                        value: currency,
                      })),
                    ]}
                  />
                  {errors.billingCurrency && (
                    <ValidationText>{errors.billingCurrency}</ValidationText>
                  )}
                </InputLabelContainer>

                {!initialData?.contractId && (
                  <InputLabelContainer>
                    <label>{t('Description')}</label>
                    <TextInput
                      name="description"
                      placeholder={t('Enter Contract Description')}
                      value={formData.description}
                      onChange={handleChange}
                      className="largeInput"
                    />
                  </InputLabelContainer>
                )}

                {initialData?.contractId && (
                  <AttachmentWrapper>
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
                        multiple
                        style={{ display: 'none' }}
                        onChange={handleFileChange}
                      />
                      <UploadSVG />
                      <UploadText>
                        {t('Drag and drop or')}{' '}
                        <BrowseText>{t('Browse')}</BrowseText>
                      </UploadText>
                    </LogoUploadContainer>
                    <span className="infoText-contract">
                      {t('File format')} : .pdf, .doc, .docx
                    </span>
                    {files.length > 0 && (
                      <FileLists>
                        {files.map((f, idx) => (
                          <FileName key={idx}>
                            {f.name}
                            <RemoveButton
                              onClick={() =>
                                setFiles((prev) =>
                                  prev.filter((_, i) => i !== idx)
                                )
                              }
                            >
                              x
                            </RemoveButton>
                          </FileName>
                        ))}
                      </FileLists>
                    )}
                    {(formData.attachments ?? []).length > 0 && (
                      <FileLists>
                        {(formData.attachments ?? []).map((id, index) => (
                          <FileName key={index}>
                            {typeof id === 'string' ? id : id.name}
                            <RemoveButton
                              type="button"
                              onClick={() =>
                                setFormData((prev) => {
                                  const newAttachments = (
                                    prev.attachments ?? []
                                  ).filter((_, i) => i !== index);
                                  const newAttachmentIds = (
                                    prev.attachmentIds ?? []
                                  ).filter((_, i) => i !== index);
                                  return {
                                    ...prev,
                                    attachments: newAttachments,
                                    attachmentIds: newAttachmentIds,
                                  };
                                })
                              }
                            >
                              X
                            </RemoveButton>
                          </FileName>
                        ))}
                      </FileLists>
                    )}
                  </AttachmentWrapper>
                )}
              </ColumnWrapper>
            </FormInputsContainer>

            <div className="formButtons">
              <Button onClick={handleClose} type="button" className="cancel">
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
                {t('Continue')}
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
                    className="largeContainerExp largeContainerHei"
                    value={formData.projectName || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'projectName',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleProjectChange(event);
                      if (errorsSteptwo?.projectName) {
                        setErrorsSteptwo((prev) => ({
                          ...prev,
                          projectName: undefined,
                        }));
                      }
                    }}
                    options={[
                      { label: t('Select Project'), value: '' },
                      ...(projectOptions?.map((project) => ({
                        label: project.name,
                        value: project.name,
                      })) || []),
                    ]}
                  />
                  {errorsSteptwo && errorsSteptwo?.projectName && (
                    <ValidationText>{errorsSteptwo.projectName}</ValidationText>
                  )}
                </InputLabelContainer>

                {isProjectLoading ? (
                  <div>
                    <SpinAnimation />
                  </div>
                ) : (
                  <div>
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
                        className="largeContainerExp"
                        onChange={(selected) => {
                          const values = [...selected]
                            .sort((a, b) => a.value.localeCompare(b.value))
                            .map((opt) => opt.value);
                          setFormData((prev) => ({
                            ...prev,
                            projectManagers: values,
                          }));
                          if (errorsSteptwo?.projectManagers) {
                            setErrorsSteptwo((prev) => ({
                              ...prev,
                              projectManagers: undefined,
                            }));
                          }
                        }}
                        placeholder={t('Select Project Managers')}
                        searchable={true}
                      />
                      {errorsSteptwo?.projectManagers && (
                        <ValidationText>
                          {errorsSteptwo.projectManagers}
                        </ValidationText>
                      )}
                    </InputLabelContainer>
                  </div>
                )}
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
                    <MultiSelectDropdown
                      options={resourceOptions.map((opt) => ({
                        label: opt.label,
                        value: opt.value,
                      }))}
                      value={
                        currentResource
                          ? resourceOptions
                              .filter((r) => r.value === currentResource)
                              .map((r) => ({ label: r.label, value: r.value }))
                          : []
                      }
                      onChange={(selectedItems) => {
                        const selectedValue = selectedItems[0]?.value || null;
                        setCurrentResource(selectedValue);

                        if (errorsSteptwo?.selectedResources) {
                          setErrorsSteptwo((prev) => ({
                            ...prev,
                            selectedResources: undefined,
                          }));
                        }
                      }}
                      placeholder={t('Select Resources')}
                      searchable={true}
                      className="largeContainerRes largeContainerHei"
                    />

                    <AvailabilityContainer>
                      <AvailabilityInput
                        type="text"
                        inputMode="numeric"
                        placeholder={t('Enter Percentage')}
                        value={currentAvailability}
                        onChange={(e) => {
                          const rawValue = e.target.value;
                          const { cleanedValue, errorMessage } =
                            validateAvailabilityInput(rawValue);
                          if (errorMessage) {
                            setErrorsSteptwo((prev) => ({
                              ...prev,
                              availability: errorMessage,
                            }));
                          } else {
                            setErrorsSteptwo((prev) => ({
                              ...prev,
                              availability: undefined,
                            }));
                          }
                          setCurrentAvailability(cleanedValue);
                        }}
                      />
                      <PercentageSign>%</PercentageSign>
                    </AvailabilityContainer>

                    <SaveButton
                      type="button"
                      onClick={() => {
                        if (!currentResource || !currentAvailability) return;

                        const { cleanedValue, errorMessage } =
                          validateAvailabilityInput(currentAvailability);

                        if (errorMessage) {
                          setErrorsSteptwo((prev) => ({
                            ...prev,
                            availability: errorMessage,
                          }));
                          return;
                        }

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
                              availability: Number(cleanedValue),
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
                  {errorsSteptwo?.selectedResources && (
                    <ValidationText>
                      {errorsSteptwo.selectedResources}
                    </ValidationText>
                  )}
                  {errorsSteptwo?.availability && (
                    <ValidationText>
                      {errorsSteptwo.availability}
                    </ValidationText>
                  )}
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
                          <span className="percentageAvailability">
                            <span className="availability">
                              Allocation: {option.availability}%
                            </span>
                            <button
                              className="remove-btn"
                              onClick={() => {
                                setSelectedResources((prev) => {
                                  const updated = prev.filter(
                                    (item) => item.value !== option.value
                                  );

                                  setFormData((form) => ({
                                    ...form,
                                    resourceAllocations: updated,
                                  }));

                                  return updated;
                                });
                              }}
                            >
                              ✕
                            </button>
                          </span>
                        </NameBubble>
                      ))}
                  </NameBubbleListContainer>
                </ListWrapper>
              </ResourceBlock>
            )}
            <AddContractButtons>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <ButtonGroup>
                <Button
                  onClick={handleDiscardModalToggle}
                  type="button"
                  className="cancel"
                >
                  {t('Cancel')}
                </Button>
                <Button className="submit" type="submit">
                  {isEditMode ? t('Update') : t('Add')}
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
