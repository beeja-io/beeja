import React, { useRef, useState } from 'react';
import { t } from 'i18next';
import {
  FormContainer,
  SectionTitle,
  FormGrid,
  FormField,
  Label,
  Input,
  Select,
  TextArea,
  ButtonContainer,
  SubmitButton,
  CancelButton,
  RequiredAsterisk,
  DateInputWrapper,
} from '../../styles/ProjectStyles.style.tsx';
import Calendar from '../reusableComponents/Calendar.component';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import { TextInput } from '../../styles/InputStyles.style';
import { postProjects } from '../../service/axiosInstance.tsx';
import { ClientResponse } from '../../entities/ClientEntity.tsx';
import SpinAnimation from '../loaders/SprinAnimation.loader.tsx';
import { toast } from 'sonner';

interface AddProjectFormProps {
  onCancel: () => void;
  onSubmit: (formData: ProjectFormData) => void;
  handleClose: () => void;
  handleSuccessMessage: () => void;
  client: ClientResponse;
  initialData?: Partial<ProjectFormData>;
}

interface ProjectFormData {
  projectName: string;
  clientName: string;
  projectManagers: string;
  resources: string;
  description: string;
  startDate: string;
}

const AddProjectForm: React.FC<AddProjectFormProps> = ({
  onCancel,
  onSubmit,
  initialData,
  handleClose,
  handleSuccessMessage,
  client,
}) => {
  const [formData, setFormData] = useState<ProjectFormData>({
    projectName: initialData?.projectName || '',
    clientName: initialData?.clientName || '',
    projectManagers: initialData?.projectManagers || '',
    resources: initialData?.resources || '',
    description: initialData?.description || '',
    startDate: initialData?.startDate || '',
  });

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const calendarRef = useRef<HTMLDivElement>(null);

  const handleCalendarToggle = (state: boolean) => {
    setIsStartDateCalOpen(state);
  };

  const formatDate = (dateStr: string | Date) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-CA');
  };

  const handleDateSelect = (date: Date | null) => {
    if (date) {
      setStartDate(date);
      setFormData((prev) => ({
        ...prev,
        startDate: formatDate(date),
      }));
      setIsStartDateCalOpen(false);
    }
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

    if (!formData.projectName || !formData.clientName || !formData.startDate) {
      alert(t('Please fill in all required fields.'));
      return;
    }

    setIsLoading(true);

    try {
      const dataToSend = {
        ...formData,
        clientId: client.clientId,
        organizationId: client.organizationId,
      };
      await postProjects(dataToSend);
      toast.success(t('Project added successfully!'));

      setFormData({
        projectName: '',
        clientName: '',
        projectManagers: '',
        resources: '',
        description: '',
        startDate: '',
      });
      setStartDate(null);

      onCancel();
    } catch (error) {
      toast.error(t('Failed to add project. Please try again.'));
      throw new Error('Failed to add project:' + error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <FormContainer>
      <SectionTitle>{t('Project Details')}</SectionTitle>
      {isLoading ? (
        <SpinAnimation />
      ) : (
        <form onSubmit={handleSubmit}>
          <FormGrid>
            <FormField>
              <Label htmlFor="projectName">
                {t('Project Name')}
                <RequiredAsterisk>*</RequiredAsterisk>
              </Label>
              <Input
                type="text"
                id="projectName"
                name="projectName"
                value={formData.projectName}
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
              <Select
                id="clientName"
                name="clientName"
                value={formData.clientName}
                onChange={handleChange}
                required
              >
                <option value="">{t('Select Client')}</option>
                <option value="client1">Client A</option>
                <option value="client2">Client B</option>
              </Select>
            </FormField>

            <FormField>
              <Label htmlFor="projectManagers">{t('Project Managers')}</Label>
              <Select
                id="projectManagers"
                name="projectManagers"
                value={formData.projectManagers}
                onChange={handleChange}
                required
              >
                <option value="">{t('Select Manager')}</option>
                <option value="Manager 1">Manager 1</option>
                <option value="Manager 2">Manager 2</option>
                <option value="Manager 3">Manager 3</option>
              </Select>
            </FormField>

            <FormField>
              <Label htmlFor="resources">{t('Resources')}</Label>
              <Select
                id="resources"
                name="resources"
                value={formData.resources}
                onChange={handleChange}
                required
              >
                <option value="">{t('Select Resource')}</option>
                <option value="Resource 1">Resource 1</option>
                <option value="Resource 2">Resource 2</option>
              </Select>
            </FormField>

            <FormField style={{ gridColumn: '1 / span 1' }}>
              <Label htmlFor="description">{t('Description')}</Label>
              <TextArea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder={t('Add Project Description')}
                rows={2}
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
            <CancelButton type="button" onClick={handleClose}>
              {t('Cancel')}
            </CancelButton>
            <SubmitButton type="submit">{t('Add')}</SubmitButton>
          </ButtonContainer>
        </form>
      )}
    </FormContainer>
  );
};

export default AddProjectForm;
