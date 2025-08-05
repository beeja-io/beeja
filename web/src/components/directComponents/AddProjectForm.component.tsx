import { t } from 'i18next';
import React, { useEffect, useRef, useState } from 'react';
import Select from 'react-select';
import { Client } from '../../entities/ClientEntity.tsx';
import { Employee } from '../../entities/ProjectEntity.tsx';
import {
  getAllClient,
  getResourceManager,
  postProjects,
  putProject,
} from '../../service/axiosInstance.tsx';
import { Button } from '../../styles/CommonStyles.style';
import {
  ButtonContainer,
  DateInputWrapper,
  FormContainer,
  FormField,
  FormGrid,
  Input,
  Label,
  RequiredAsterisk,
  SectionTitle,
  SelectDropDown,
  SelectWrapper,
  TextInput,
} from '../../styles/ProjectStyles.style.tsx';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader.tsx';
import Calendar from '../reusableComponents/Calendar.component';
import { toast } from 'sonner';

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
    getAllClient()
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
  }, []);

  const calendarRef = useRef<HTMLDivElement>(null);

  const handleCalendarToggle = (state: boolean) => {
    setIsStartDateCalOpen(state);
  };

  const handleDateSelect = (date: Date | null) => {
    if (date) {
      setStartDate(date);
      setProjectFormData((prev) => ({
        ...prev,
        startDate: date.toISOString().split('T')[0],
      }));
      setIsStartDateCalOpen(false);
    }
  };

  const formatDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-CA');
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setProjectFormData((prev) => ({ ...prev, [name]: value }));
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
      <SectionTitle>{t('Project Details')}</SectionTitle>
      {isSuccess && <SpinAnimation />}
      <form onSubmit={handleSubmit}>
        <FormGrid>
          <FormField>
            <Label htmlFor="name">
              {t('Project Name')}
              <RequiredAsterisk>*</RequiredAsterisk>
            </Label>
            <Input
              type="text"
              id="name"
              name="name"
              value={projectFormData.name}
              onChange={handleChange}
              placeholder={t('Enter Project Name')}
              required
            />
          </FormField>
          <FormField>
            <Label htmlFor="clientName">
              {t('Client Name')}
              <RequiredAsterisk>*</RequiredAsterisk>
            </Label>
            <SelectDropDown
              name="clientName"
              value={projectFormData.clientId}
              onChange={(e) => {
                const selectedValue = e.target.value;
                const selectedOption = clientOptions.find(
                  (opt) => opt.value === selectedValue
                );
                if (selectedOption) {
                  setProjectFormData((prev) => ({
                    ...prev,
                    clientId: selectedOption.value,
                    clientName: selectedOption.label,
                  }));
                }
              }}
            >
              <option value="">Select Client</option>
              {clientOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </SelectDropDown>
          </FormField>

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
                  projectFormData.projectManagers.includes(option.value)
                )}
                options={managerOptions}
                onChange={(selected) => {
                  const values = selected.map((opt) => opt.value);
                  setProjectFormData((prev) => ({
                    ...prev,
                    projectManagers: values,
                  }));
                }}
                classNamePrefix="react-select"
                placeholder={t('Select Project Managers')}
              />
            </FormField>
          </SelectWrapper>

          <SelectWrapper>
            <FormField>
              <Label>
                {t('Resources')}
                <RequiredAsterisk>*</RequiredAsterisk>
              </Label>
              <Select
                isMulti
                name="resources"
                value={resourceOptions.filter((option) =>
                  projectFormData.projectResources.includes(option.value)
                )}
                options={resourceOptions}
                onChange={(selected) => {
                  const values = selected.map((opt) => opt.value);
                  setProjectFormData((prev) => ({
                    ...prev,
                    projectResources: values,
                  }));
                }}
                classNamePrefix="react-select"
                placeholder={t('Select Resources')}
              />
            </FormField>
          </SelectWrapper>

          <FormField style={{ gridColumn: '1 / span 1' }}>
            <Label htmlFor="description">{t('Description')}</Label>
            <Input
              id="description"
              name="description"
              value={projectFormData.description}
              onChange={handleChange}
              placeholder={t('Add Project Description')}
            />
          </FormField>

          <FormField>
            <Label htmlFor="startDate">
              {t('Start Date')}
              <RequiredAsterisk>*</RequiredAsterisk>
            </Label>
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
                    maxDate={new Date()}
                    selectedDate={startDate}
                    handleDateInput={handleDateSelect}
                    handleCalenderChange={() => {}}
                  />
                )}
              </div>
            </DateInputWrapper>
          </FormField>
        </FormGrid>

        {isSubmitting ? (
          <SpinAnimation />
        ) : (
          <ButtonContainer>
            <Button onClick={handleClose} type="button">
              {t('Cancel')}
            </Button>
            <Button className="submit" type="submit">
              {isEditMode ? t('Update') : t('Add')}
            </Button>
          </ButtonContainer>
        )}
      </form>
    </FormContainer>
  );
};

export default AddProjectForm;
