import React, { useRef, useState, useEffect } from 'react';
import { t } from 'i18next';
import { Button } from '../../styles/CommonStyles.style';
import Calendar from '../reusableComponents/Calendar.component';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import {
  FormContainer,
  SectionTitle,
  FormGrid,
  FormField,
  Label,
  Input,
  SelectDropDown,
  ButtonContainer,
  RequiredAsterisk,
  DateInputWrapper,
  SelectWrapper,
  TextInput,
} from '../../styles/ProjectStyles.style.tsx';
import {
  getAllClient,
  getResourceManager,
  postProjects,
} from '../../service/axiosInstance.tsx';
import Select from 'react-select';
import { Employee } from '../../entities/ProjectEntity.tsx';
import { Client } from '../../entities/ClientEntity.tsx';

interface AddProjectFormProps {
  handleClose: () => void;
  handleSuccessMessage: () => void;
  initialData?: Partial<ProjectFormData>;
}

type OptionType = {
  value: string;
  label: string;
};

interface ProjectFormData {
  clientId: string;
  name: string;
  clientName: string;
  projectManagers: string[];
  resources: string[];
  description: string;
  startDate: string;
}

const AddProjectForm: React.FC<AddProjectFormProps> = ({
  handleClose,
  handleSuccessMessage,
  initialData,
}) => {
  const [formData, setFormData] = useState<ProjectFormData>({
    clientId: initialData?.clientId || '',
    name: initialData?.name || '',
    clientName: initialData?.clientName || '',
    projectManagers: initialData?.projectManagers || [],
    resources: initialData?.resources || [],
    description: initialData?.description || '',
    startDate: initialData?.startDate || '',
  });

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [resourceOptions, setResourceOptions] = useState<OptionType[]>([]);
  const [managerOptions, setManagerOptions] = useState<OptionType[]>([]);
  const [clientOptions, setClientOptions] = useState<OptionType[]>([]);

  useEffect(() => {
    getResourceManager()
      .then((response) => {
        const users = response.data as Employee[];

        const userOptions = users.map((user) => {
          const fullName = `${user.firstName} ${user.lastName}`;
          return {
            value: user.employeeId,
            label: fullName,
          };
        });
        setManagerOptions(userOptions);
        setResourceOptions(userOptions);
      })
      .catch((error) => {
        throw new Error('Error fetching resource managers:' + error);
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
        throw new Error('Error fetching clients: ' + error);
      });
  }, []);

  const calendarRef = useRef<HTMLDivElement>(null);
  const handleCalendarToggle = (state: boolean) => {
    setIsStartDateCalOpen(state);
  };

  const handleDateSelect = (date: Date | null) => {
    if (date) {
      setStartDate(date);
      setFormData((prev) => ({
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
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await postProjects(formData);
      if (response?.status === 200 || response?.status === 201) {
        handleSuccessMessage();
        handleClose();
      } else {
        throw new Error(
          'Project submission failed with status: ' + response?.status
        );
      }
    } catch (error) {
      throw new Error('An error occurred during submission.' + error);
    }
  };

  return (
    <FormContainer>
      <SectionTitle>{t('Project Details')}</SectionTitle>
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
              value={formData.name}
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
              value={formData.clientId}
              onChange={(e) => {
                const selectedValue = e.target.value;
                const selectedOption = clientOptions.find(
                  (opt) => opt.value === selectedValue
                );
                if (selectedOption) {
                  setFormData((prev) => ({
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
                  formData.projectManagers.includes(option.value)
                )}
                options={managerOptions}
                onChange={(selected) => {
                  const values = selected.map((opt) => opt.value);
                  setFormData((prev) => ({ ...prev, projectManagers: values }));
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
                  formData.resources.includes(option.value)
                )}
                options={resourceOptions}
                onChange={(selected) => {
                  const values = selected.map((opt) => opt.value);
                  setFormData((prev) => ({ ...prev, resources: values }));
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
              value={formData.description}
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

        <ButtonContainer>
          <Button onClick={handleClose} type="button">
            {t('Cancel')}
          </Button>
          <Button className="submit" type="submit">
            {t('Add')}
          </Button>
        </ButtonContainer>
      </form>
    </FormContainer>
  );
};

export default AddProjectForm;
