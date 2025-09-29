import { t } from 'i18next';
import React, { useEffect, useRef, useState } from 'react';
import { Client } from '../../entities/ClientEntity.tsx';
import { Employee } from '../../entities/ProjectEntity.tsx';
import {
  getAllClient,
  getResourceManager,
  postProjects,
  putProject,
} from '../../service/axiosInstance.tsx';
import {
  AddFormMainContainer,
  ColumnWrapper,
  DateInputWrapper,
  FormContainer,
  FormInputsContainer,
  InputLabelContainer,
  SectionContainer,
  TextInput,
  Button,
} from '../../styles/ProjectStyles.style.tsx';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader.tsx';
import Calendar from '../reusableComponents/Calendar.component';
import { toast } from 'sonner';
import { ValidationText } from '../../styles/DocumentTabStyles.style.tsx';
import CenterModal from '../reusableComponents/CenterModal.component.tsx';
import DropdownMenu, {
  MultiSelectDropdown,
} from '../reusableComponents/DropDownMenu.component.tsx';

interface AddProjectFormProps {
  handleClose: () => void;
  handleSuccessMessage: (newProjectId: string) => void;
  initialData?: Partial<ProjectFormData>;
  refreshProjectList: () => Promise<void>;
  isEditMode?: boolean;
}

type OptionType = {
  value: string;
  label: string;
};

export interface ProjectFormData {
  clientId: string;
  name: string;
  clientName: string;
  projectManagers: string[];
  projectResources: string[];
  description: string;
  startDate: string;
  projectId: string;
}

const AddProjectForm: React.FC<AddProjectFormProps> = ({
  handleClose,
  handleSuccessMessage,
  initialData,
  refreshProjectList,
  isEditMode,
}) => {
  const [projectFormData, setProjectFormData] = useState<ProjectFormData>({
    clientId: initialData?.clientId || '',
    name: initialData?.name || '',
    clientName: initialData?.clientName || '',
    projectManagers: initialData?.projectManagers || [],
    projectResources: initialData?.projectResources || [],
    description: initialData?.description || '',
    startDate: '',
    projectId: initialData?.projectId || '',
  });

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [resourceOptions, setResourceOptions] = useState<OptionType[]>([]);
  const [managerOptions, setManagerOptions] = useState<OptionType[]>([]);
  const [clientOptions, setClientOptions] = useState<OptionType[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);

  const [errors, setErrors] = useState({
    name: '',
    clientName: '',
    startDate: '',
  });

  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  useEffect(() => {
    getResourceManager()
      .then((response) => {
        const users = response.data as Employee[];
        const userOptions = users.map((user) => ({
          value: user.employeeId,
          label: `${user.firstName} ${user.lastName}`,
        }));
        setManagerOptions(userOptions);
        setResourceOptions(userOptions);
      })
      .catch((error) => {
        toast.error('Error fetching resource managers', error);
      });
  }, []);

  useEffect(() => {
    getAllClient(0, 10)
      .then((response) => {
        const clients: Client[] = response.data;
        const clientOpts: OptionType[] = clients.map((client) => ({
          value: client.clientId,
          label: client.clientName,
        }));
        setClientOptions(clientOpts);
      })
      .catch((error) => {
        toast.error('Error Fetching Clients', error);
      });
  }, []);

  useEffect(() => {
    if (initialData?.startDate) {
      const parsedDate = new Date(initialData.startDate);
      if (!isNaN(parsedDate.getTime())) {
        setStartDate(parsedDate);
        setProjectFormData((prev) => ({
          ...prev,
          startDate: parsedDate.toISOString().split('T')[0],
        }));
      }
    }
  }, [initialData]);

  const calendarRef = useRef<HTMLDivElement>(null);
  const handleCalendarToggle = (state: boolean) => {
    setIsStartDateCalOpen(state);
  };

  const handleDateSelect = (date: Date | null) => {
    if (date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const formattedDate = `${year}-${month}-${day}`; // YYYY-MM-DD

      setStartDate(date);
      setProjectFormData((prev) => ({
        ...prev,
        startDate: formattedDate,
      }));
      setIsStartDateCalOpen(false);
    }
  };
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        calendarRef.current &&
        !calendarRef.current.contains(event.target as Node)
      ) {
        setIsStartDateCalOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const formatDate = (date: Date) => {
    const day = String(date.getDate()).padStart(2, '0');
    const month = date.toLocaleString('en-US', { month: 'short' });
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setProjectFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prevErrors) => ({ ...prevErrors, [name]: '' }));
  };
  const handleAddProject = async (e: any) => {
    e.preventDefault();
    const newErrors = {
      name:
        projectFormData.name === '' || projectFormData.name === null
          ? 'Project Name Required'
          : '',
      clientName:
        projectFormData.clientId === '' || projectFormData.clientId === null
          ? 'Client Name Required'
          : '',
      startDate: startDate === null ? 'Start Date Required' : '',
    };
    setErrors(newErrors);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const response =
        isEditMode && initialData?.projectId
          ? await putProject(initialData.projectId, projectFormData)
          : await postProjects(projectFormData);

      if (response?.status === 200 || response?.status === 201) {
        handleSuccessMessage(response.data.projectId);
        setIsSuccess(true);
        await refreshProjectList();
        setTimeout(() => {
          handleClose();
        }, 1500);
      } else {
        toast.error('Project Submition Failed');
      }
    } catch (error) {
      toast.error('An error occurred during submission: ' + error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <FormContainer>
      <>
        {isSuccess && <SpinAnimation />}
        <AddFormMainContainer onSubmit={handleSubmit}>
          <SectionContainer>
            <h2>{t('Project_Details')}</h2>
          </SectionContainer>
          <FormInputsContainer>
            <ColumnWrapper>
              {projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>{t('Project ID')}</label>
                  <TextInput
                    className="disabled"
                    type="text"
                    value={projectFormData.projectId}
                    disabled
                  />
                </InputLabelContainer>
              )}
              {!projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>
                    {t('Project_Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="text"
                    name="name"
                    placeholder={t('Enter_Project_Name')}
                    className={`largeInput ${errors.name ? 'errorEnabledInput' : ''}`}
                    value={projectFormData.name}
                    onChange={handleChange}
                    required
                  />
                  {errors.name && (
                    <ValidationText>{errors.name}</ValidationText>
                  )}
                </InputLabelContainer>
              )}

              {projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>
                    {t('Client_Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>

                  <DropdownMenu
                    label="Select Client"
                    name="clientName"
                    id="clientName"
                    className={`largeContainerHei ${isEditMode ? 'cursor-disabled' : ''}`}
                    disabled={isEditMode}
                    value={projectFormData.clientId ?? ''}
                    onChange={(selectedValue) => {
                      const selectedOption = clientOptions.find(
                        (opt) => opt.value === selectedValue
                      );
                      if (selectedOption) {
                        setProjectFormData((prev) => ({
                          ...prev,
                          clientId: selectedOption.value,
                          clientName: selectedOption.label,
                        }));
                      } else {
                        setProjectFormData((prev) => ({
                          ...prev,
                          clientId: '',
                          clientName: '',
                        }));
                      }
                    }}
                    required={true}
                    options={[
                      { label: 'Select Client', value: '' },
                      ...clientOptions.map((opt) => ({
                        label: opt.label,
                        value: opt.value,
                      })),
                    ]}
                  />
                </InputLabelContainer>
              )}
              <InputLabelContainer>
                <label>
                  {t('Project_Managers')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <MultiSelectDropdown
                  options={managerOptions}
                  value={managerOptions.filter((option) =>
                    projectFormData.projectManagers.includes(option.value)
                  )}
                  onChange={(selected) => {
                    const values = [...selected]
                      .sort((a, b) => a.value.localeCompare(b.value))
                      .map((opt) => opt.value);

                    setProjectFormData((prev) => ({
                      ...prev,
                      projectManagers: values,
                    }));
                  }}
                  required
                  placeholder={t('Select Project Managers')}
                  searchable={true}
                />
              </InputLabelContainer>
              {!projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>
                    {t('Start_Date')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <DateInputWrapper ref={calendarRef}>
                    <TextInput
                      type="text"
                      placeholder="Select Date"
                      name="startDate"
                      value={startDate ? formatDate(startDate) : ''}
                      onFocus={() => handleCalendarToggle(true)}
                      onClick={() => handleCalendarToggle(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span
                      className="iconArea"
                      onClick={() => handleCalendarToggle(true)}
                    >
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isStartDateCalOpen && (
                        <Calendar
                          title="Start Date"
                          minDate={new Date('01-01-2000')}
                          selectedDate={startDate}
                          handleDateInput={handleDateSelect}
                          handleCalenderChange={() => {}}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                  {errors.startDate && (
                    <ValidationText>{errors.startDate}</ValidationText>
                  )}
                </InputLabelContainer>
              )}
            </ColumnWrapper>

            <ColumnWrapper>
              {projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>
                    {t('Project_Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="text"
                    name="name"
                    placeholder={t('Enter_Project_Name')}
                    className="largeInput"
                    value={projectFormData.name}
                    onChange={handleChange}
                    required
                  />
                </InputLabelContainer>
              )}
              {projectFormData?.projectId && (
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
                      value={startDate ? formatDate(startDate) : ''}
                      onFocus={() => handleCalendarToggle(true)}
                      onClick={() => handleCalendarToggle(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span
                      className="iconArea"
                      onClick={() => handleCalendarToggle(true)}
                    >
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isStartDateCalOpen && (
                        <Calendar
                          title="Start Date"
                          minDate={new Date('01-01-2000')}
                          selectedDate={startDate}
                          handleDateInput={handleDateSelect}
                          handleCalenderChange={() => {}}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                </InputLabelContainer>
              )}
              {!projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>
                    {t('Client_Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <DropdownMenu
                    label="Select Client"
                    name="clientName"
                    id="clientName"
                    className={`largeContainerHei ${isEditMode ? 'cursor-disabled' : ''}`}
                    disabled={isEditMode}
                    value={projectFormData.clientId ?? ''}
                    onChange={(selectedValue) => {
                      const selectedOption = clientOptions.find(
                        (opt) => opt.value === selectedValue
                      );
                      if (selectedOption) {
                        setProjectFormData((prev) => ({
                          ...prev,
                          clientId: selectedOption.value,
                          clientName: selectedOption.label,
                        }));
                      } else {
                        setProjectFormData((prev) => ({
                          ...prev,
                          clientId: '',
                          clientName: '',
                        }));
                      }
                    }}
                    required={true}
                    options={[
                      { label: 'Select Client', value: '' },
                      ...clientOptions.map((opt) => ({
                        label: opt.label,
                        value: opt.value,
                      })),
                    ]}
                  />
                </InputLabelContainer>
              )}

              <InputLabelContainer>
                <label>
                  {t('Resources')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <MultiSelectDropdown
                  options={resourceOptions}
                  value={resourceOptions.filter((option) =>
                    projectFormData.projectResources.includes(option.value)
                  )}
                  onChange={(selected) => {
                    const values = [...selected]
                      .sort((a, b) => a.value.localeCompare(b.value))
                      .map((opt) => opt.value);
                    setProjectFormData((prev) => ({
                      ...prev,
                      projectResources: values,
                    }));
                  }}
                  required
                  placeholder={t('Select Resources')}
                  searchable={true}
                />
              </InputLabelContainer>

              {!projectFormData?.projectId && (
                <InputLabelContainer>
                  <label>{t('Description')}</label>
                  <TextInput
                    type="text"
                    name="description"
                    placeholder={t('Add_Project_Description')}
                    className="largeInput"
                    value={projectFormData.description}
                    onChange={handleChange}
                  />
                </InputLabelContainer>
              )}
            </ColumnWrapper>
          </FormInputsContainer>
          {projectFormData?.projectId && (
            <FormInputsContainer>
              <InputLabelContainer className="editContainer">
                <label>{t('Description')}</label>
                <TextInput
                  type="text"
                  name="description"
                  placeholder={t('Add_Project_Description')}
                  className="editText"
                  value={projectFormData.description}
                  onChange={handleChange}
                />
              </InputLabelContainer>
            </FormInputsContainer>
          )}

          {isSubmitting ? (
            <SpinAnimation />
          ) : (
            <div className="formButtons">
              <Button
                onClick={isEditMode ? handleDiscardModalToggle : handleClose}
                type="button"
                className="cancel"
              >
                {t('Cancel')}
              </Button>
              <Button
                className="submit"
                type="submit"
                onClick={handleAddProject}
              >
                {isEditMode ? t('Update') : t('Add')}
              </Button>
            </div>
          )}
        </AddFormMainContainer>
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

export default AddProjectForm;
