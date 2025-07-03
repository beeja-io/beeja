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

interface AddProjectFormProps {
  // onCancel: () => void;
  // onSubmit: (formData: ProjectFormData) => void;
  // handleClose: () => void;
  // clientId: string;
  initialData?: Partial<ProjectFormData>;
}

interface ProjectFormData {
  projectName: string;
  clientName: string;
  projectManagers: string[];
  resources: string[];
  description: string;
  startDate: string;
}

const AddProjectForm: React.FC<AddProjectFormProps> = ({
  // onCancel,
  // onSubmit,
  initialData,
  // handleClose,
  // clientId,
}) => {
  const [formData, setFormData] = useState<ProjectFormData>({
    projectName: initialData?.projectName || '',
    clientName: initialData?.clientName || '',
    projectManagers: initialData?.projectManagers || [],
    resources: initialData?.resources || [],
    description: initialData?.description || '',
    startDate: initialData?.startDate || '',
  });

  const [startDate, setStartDate] = useState<Date | null>(null);
  const [isStartDateCalOpen, setIsStartDateCalOpen] = useState(false);

  const calendarRef = useRef<HTMLDivElement>(null);

  const handleCalendarToggle = (state: boolean) => {
    setIsStartDateCalOpen(state);
  };

  const handleDateSelect = (date: Date | null) => {
    if (date) {
      setStartDate(date);
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

  const handleSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, selectedOptions } = e.target;
    const values = Array.from(selectedOptions).map((option) => option.value);
    setFormData((prev) => ({ ...prev, [name]: values }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.projectName || !formData.clientName || !formData.startDate) {
      alert(t('Please fill in all required fields.'));
      return;
    }
    // onSubmit(formData);
  };

  return (
    <FormContainer>
      <SectionTitle>{t('Project Details')}</SectionTitle>
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
              multiple
              value={formData.projectManagers}
              onChange={handleSelectChange}
            >
              <option value="">{t('Select Managers')}</option>
              <option value="pm1">Manager 1</option>
              <option value="pm2">Manager 2</option>
              <option value="pm3">Manager 3</option>
            </Select>
          </FormField>

          <FormField>
            <Label htmlFor="resources">{t('Resources')}</Label>
            <Select
              id="resources"
              name="resources"
              multiple
              value={formData.resources}
              onChange={handleSelectChange}
            >
              <option value="">{t('Select Resources')}</option>
              <option value="res1">Resource 1</option>
              <option value="res2">Resource 2</option>
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
              rows={4}
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
          <CancelButton type="button" >
            {t('Cancel')}
          </CancelButton>
          <SubmitButton type="submit">{t('Add')}</SubmitButton>
        </ButtonContainer>
      </form>
    </FormContainer>
  );
};

export default AddProjectForm;
