import React, { useEffect, useMemo, useState, useRef } from 'react';
import ToastMessage from './ToastMessage.component';
import { useTranslation } from 'react-i18next';
import {
  fetchEmployeeHistory,
  addEmployeeHistory,
  updateEmployeeHistory,
  deleteEmployeeHistory,
} from '../../service/axiosInstance';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';
import DropdownMenu from './DropDownMenu.component';
import { Button } from '../../styles/CommonStyles.style';
import { AddNewPlusSVG } from '../../svgs/EmployeeListSvgs.svg';
import Calendar from '../reusableComponents/Calendar.component';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import { DateInputWrapper, TextInput } from '../../styles/ProjectStyles.style';
import { InputLabelContainer } from '../../styles/ProjectStyles.style';
import {
  ColumnWrapper,
  FormInputsContainer,
} from '../../styles/ContractStyle.style';
import {
  EditIcon,
  DeleteIcon,
  ActionIcon,
} from '../../svgs/ExpenseListSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import CenterModal from '../reusableComponents/CenterModal.component';
import { useUser } from '../../context/UserContext';
import {
  Container,
  CardContent,
  Header,
  Title,
  Timeline,
  TimelineItem,
  Card,
  TopRow,
  Small,
  Badge,
  ActionsMenu,
  ModalOverlay,
  Modal,
  Required,
  CancelButton,
  SaveButton,
  ActionItem,
} from '../../styles/EmploymentHistory.styles';

interface JobHistoryItem {
  id?: string;
  designation: string;
  employementType?: string;
  department?: string;
  joiningDate?: string;
  resignationDate?: string;
  startDate?: string;
  endDate?: string;
  description?: string;
  updatedBy?: string;
  updatedAt?: string;
}

interface Props {
  employeeId: string;
  jobTitles?: OrgDefaults;
  employmentTypes?: OrgDefaults;
}

const EmploymentHistory: React.FC<Props> = ({
  employeeId,
  jobTitles,
  employmentTypes,
}) => {
  const [historyList, setHistoryList] = useState<JobHistoryItem[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingJobId, setEditingJobId] = useState<string | null>(null);
  const [form, setForm] = useState<JobHistoryItem>({ designation: '' });
  const [toast, setToast] = useState<{
    type: 'success' | 'error';
    message: string;
    head: string;
  } | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [showMenuIndex, setShowMenuIndex] = useState<String | null>(null);
  const calendarJoinRef = useRef<HTMLDivElement>(null);
  const calendarResignRef = useRef<HTMLDivElement>(null);
  const [isJoinDateOpen, setIsJoinDateOpen] = useState(false);
  const [isResignDateOpen, setIsResignDateOpen] = useState(false);
  const actionMenuRef = useRef<HTMLDivElement>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const orderedHistory = useMemo(() => historyList, [historyList]);
  const [errors, setErrors] = useState<
    Partial<Record<keyof JobHistoryItem, string>>
  >({});
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [deleteJobId, setDeleteJobId] = useState<string | null>(null);
  const { user } = useUser();
  const { t } = useTranslation();

  const formatDate = (date: Date) => {
    const day = String(date.getDate()).padStart(2, '0');
    const month = date.toLocaleString('en-US', { month: 'short' });
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  };
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        actionMenuRef.current &&
        !actionMenuRef.current.contains(event.target as Node)
      ) {
        setShowMenuIndex(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        calendarJoinRef.current &&
        !calendarJoinRef.current.contains(event.target as Node)
      ) {
        setIsJoinDateOpen(false);
      }
      if (
        calendarResignRef.current &&
        !calendarResignRef.current.contains(event.target as Node)
      ) {
        setIsResignDateOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchHistory = async () => {
    setIsLoading(true);
    try {
      const data = await fetchEmployeeHistory(employeeId);
      const sortedData = (data || [])
        .reverse()
        .sort((a: JobHistoryItem, b: JobHistoryItem) => {
          const dateA = new Date(a.startDate || 0).getTime();
          const dateB = new Date(b.startDate || 0).getTime();
          return dateB - dateA;
        });
      setHistoryList(sortedData);
    } catch {
      setToast({ type: 'error', message: 'Failed to fetch history', head: '' });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, [employeeId]);

  const openAdd = () => {
    setEditingJobId(null);
    setForm({ designation: '' });
    setIsModalOpen(true);
  };

  const openEdit = (jobId: string) => {
    const jobToEdit = historyList.find((job) => job.id === jobId);
    if (!jobToEdit) return;

    setForm({
      designation: jobToEdit.designation,
      employementType: jobToEdit.employementType,
      department: jobToEdit.department,
      startDate: jobToEdit.startDate,
      endDate: jobToEdit.endDate,
      description: jobToEdit.description,
    });

    setEditingJobId(jobId);
    setIsModalOpen(true);
    setShowMenuIndex(null);
  };

  const closeModal = () => {
    setEditingJobId(null);
    setErrors({});
    setIsModalOpen(false);
  };

  const handleFormChange = (key: keyof JobHistoryItem, value: string) => {
    setForm((f) => ({ ...f, [key]: value }));
    setErrors((prev) => ({ ...prev, [key]: '' }));
  };

  const handleAdd = async () => {
    if (
      !form.designation ||
      !form.employementType ||
      !form.startDate ||
      !form.endDate
    ) {
      setToast({
        type: 'error',
        message: 'Please fill all required fields',
        head: '',
      });
      return;
    }

    setIsSaving(true);
    try {
      const newItem = await addEmployeeHistory(employeeId, form);
      setHistoryList([newItem, ...historyList]);
      setToast({
        type: 'success',
        message: 'Employment history added successfully',
        head: 'Added Successfully',
      });
      fetchHistory();
      closeModal();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while adding employment history',
        head: 'Add Unsuccessful',
      });
    } finally {
      setIsSaving(false);
    }
  };

  const handleEdit = async () => {
    if (
      !form.designation ||
      !form.employementType ||
      !form.startDate ||
      !form.endDate
    ) {
      setToast({
        type: 'error',
        message: 'Please fill all required fields',
        head: '',
      });
      return;
    }
    if (!editingJobId) return;

    setIsSaving(true);

    try {
      await updateEmployeeHistory(employeeId, editingJobId, form);
      const newList = historyList.map((job) =>
        job.id === editingJobId
          ? { ...form, id: editingJobId, updatedAt: new Date().toISOString() }
          : job
      );
      setHistoryList(newList);
      setToast({
        type: 'success',
        message: 'Employment history updated successfully',
        head: 'Updated Successfully',
      });
      fetchHistory();
      closeModal();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while updating employment history',
        head: 'Update Unsuccessful',
      });
    } finally {
      setIsSaving(false);
    }
  };
  const handleSave = () => {
    const newErrors: Partial<Record<keyof JobHistoryItem, string>> = {};

    if (!form.designation) newErrors.designation = 'Please Select designation';
    if (!form.employementType)
      newErrors.employementType = 'Please Select employment type';
    if (!form.startDate) newErrors.startDate = 'Please Select start date';
    if (!form.endDate) newErrors.endDate = 'Please Select End date';

    setErrors(newErrors);

    if (Object.keys(newErrors).length > 0) return;
    if (editingJobId) {
      handleEdit();
    } else {
      handleAdd();
    }
  };

  const handleDelete = (jobId: string) => {
    setShowMenuIndex(null);
    setDeleteJobId(jobId);
    setIsDeleteModalOpen(true);
  };
  const confirmDelete = async () => {
    if (!deleteJobId) return;
    try {
      await deleteEmployeeHistory(employeeId, deleteJobId);
      setToast({
        type: 'success',
        message: 'Employment history deleted successfully',
        head: 'Deleted Successfully',
      });
      fetchHistory();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while deleting employment history',
        head: 'Delete Unsuccessful',
      });
    } finally {
      setIsDeleteModalOpen(false);
      setDeleteJobId(null);
    }
  };
  const calculateDiff = (start?: string, end?: string) => {
    if (!start) return '';

    const startDate = new Date(start);
    const endDate = end ? new Date(end) : new Date();

    let years = endDate.getFullYear() - startDate.getFullYear();
    let months = endDate.getMonth() - startDate.getMonth();
    let days = endDate.getDate() - startDate.getDate();

    if (days < 0) {
      months--;
      const prevMonth = new Date(endDate.getFullYear(), endDate.getMonth(), 0);
      days += prevMonth.getDate();
    }

    if (months < 0) {
      years--;
      months += 12;
    }

    if (years < 0) return '';

    if (years === 0 && months === 0 && days < 29) return '• 0 months';

    if (years === 0 && months === 0) return '• 1 month';
    if (years === 0) return `• ${months} month${months > 1 ? 's' : ''}`;
    if (months === 0) return `• ${years} year${years > 1 ? 's' : ''}`;

    return `• ${years} year${years > 1 ? 's' : ''}, ${months} month${months > 1 ? 's' : ''}`;
  };

  const isSuperAdmin = () =>
    user?.roles.some((role) => role.name === 'Super Admin');
  const formatDateReadable = (dateStr?: string): string => {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '-';

    return date.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };
  useEffect(() => {
    if (isModalOpen) window.scrollTo({ top: 0 });
  }, [isModalOpen]);

  useEffect(() => {
    if (isModalOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }

    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [isModalOpen]);

  return (
    <Container>
      <Header>
        <Title>{t('Employment_History')}</Title>
        {isSuperAdmin() && (
          <Button
            className="submit shadow"
            onClick={openAdd}
            width="100px"
            style={{ marginLeft: 'auto' }}
          >
            <AddNewPlusSVG />
            {t('ADD')}
          </Button>
        )}
      </Header>

      <Timeline scrollable={historyList.length > 3}>
        {isLoading && <SpinAnimation />}

        {orderedHistory.map((job, idx) => (
          <TimelineItem key={job.id || idx} style={{ marginBottom: '28px' }}>
            <Card>
              <CardContent>
                <TopRow>
                  <div>
                    <div style={{ fontWeight: 600 }}>{job.designation}</div>
                    <div style={{ fontSize: 13, marginTop: 4 }}>
                      <Small>
                        <Badge type={job.employementType}>
                          {job.employementType || 'Unknown'}
                        </Badge>
                        {job.startDate
                          ? formatDateReadable(job.startDate)
                          : formatDateReadable(job.joiningDate)}{' '}
                        –{' '}
                        {job.endDate
                          ? formatDateReadable(job.endDate)
                          : 'Present'}{' '}
                        {calculateDiff(
                          job.startDate ? job.startDate : job.joiningDate,
                          job.endDate
                        )}
                      </Small>
                    </div>

                    <Small>
                      {job.description && (
                        <div style={{ marginTop: 6 }}>{job.description}</div>
                      )}
                    </Small>
                  </div>

                  {isSuperAdmin() && idx !== 0 && (
                    <div style={{ position: 'relative' }}>
                      <div
                        onClick={() =>
                          setShowMenuIndex(
                            showMenuIndex === job.id ? null : job.id || ''
                          )
                        }
                      >
                        <ActionIcon />
                      </div>
                      {showMenuIndex === job.id && (
                        <ActionsMenu ref={actionMenuRef}>
                          <ActionItem onClick={() => openEdit(job.id || '')}>
                            <EditIcon /> {t('Edit')}
                          </ActionItem>
                          <ActionItem
                            className="delete"
                            onClick={() => handleDelete(job.id || '')}
                          >
                            <DeleteIcon /> {t('Delete')}
                          </ActionItem>
                        </ActionsMenu>
                      )}
                    </div>
                  )}
                </TopRow>
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    marginTop: 8,
                  }}
                >
                  <Small>
                    {job.updatedBy ? `${t('Updated_by')} ${job.updatedBy}` : ''}
                  </Small>
                  <Small>
                    {job.updatedAt
                      ? `${t('Updated_On')} ${formatDateReadable(job.updatedAt)}`
                      : ''}
                  </Small>
                </div>
              </CardContent>
            </Card>
          </TimelineItem>
        ))}
      </Timeline>

      {isModalOpen && (
        <ModalOverlay>
          <Modal>
            <Title style={{ marginTop: 0, marginBottom: 16 }}>
              {editingJobId !== null
                ? `${t('Edit_Employment_History')}`
                : `${t('Add_Employment_History')}`}
            </Title>
            <FormInputsContainer>
              <ColumnWrapper>
                <InputLabelContainer>
                  <label>
                    {t('Designation')}
                    <Required>*</Required>
                  </label>
                  <DropdownMenu
                    label={t('Select type')}
                    name="designation"
                    id="designation"
                    value={form.designation || ''}
                    className="largeContainerHei"
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'designation',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleFormChange('designation', event.target.value);
                    }}
                    required
                    options={[
                      { label: t('Select type'), value: '' },
                      ...(jobTitles?.values?.map((j) => ({
                        label: j.value,
                        value: j.value,
                      })) || []),
                    ]}
                  />
                  {errors.designation && (
                    <div style={{ color: 'red', fontSize: 12 }}>
                      {errors.designation}
                    </div>
                  )}
                </InputLabelContainer>

                <InputLabelContainer>
                  <label>
                    {t('Start_Date')}
                    <Required>*</Required>
                  </label>
                  <DateInputWrapper ref={calendarJoinRef}>
                    <TextInput
                      type="text"
                      placeholder="Select Date"
                      name="startDate"
                      value={
                        form.startDate
                          ? formatDate(new Date(form.startDate))
                          : ''
                      }
                      onFocus={() => setIsJoinDateOpen(true)}
                      onClick={() => setIsJoinDateOpen(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span
                      className="iconArea"
                      onClick={() => setIsJoinDateOpen(true)}
                    >
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isJoinDateOpen && (
                        <Calendar
                          title="Start Date"
                          minDate={new Date('2000-01-01')}
                          maxDate={new Date()}
                          selectedDate={
                            form.startDate ? new Date(form.startDate) : null
                          }
                          handleDateInput={(date: Date | null) => {
                            if (!date) return;
                            handleFormChange(
                              'startDate',
                              date.toLocaleDateString('en-CA')
                            );
                            setIsJoinDateOpen(false);
                          }}
                          handleCalenderChange={() => {}}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                  {errors.startDate && (
                    <div style={{ color: 'red', fontSize: 12 }}>
                      {errors.startDate}
                    </div>
                  )}
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>{t('Note')}</label>
                  <TextInput
                    name="note"
                    placeholder="Type your Note (Optional)"
                    value={form.description || ''}
                    onChange={(e) =>
                      handleFormChange('description', e.target.value)
                    }
                    className="largeInput"
                  />
                </InputLabelContainer>
              </ColumnWrapper>
              <ColumnWrapper>
                <InputLabelContainer>
                  <label>
                    {t('Employment_Type')}
                    <Required>*</Required>
                  </label>
                  <DropdownMenu
                    label={t('Select type')}
                    name="employementType"
                    id="employementType"
                    value={form.employementType || ''}
                    className="largeContainerHei"
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'employementType',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleFormChange('employementType', event.target.value);
                    }}
                    required
                    options={[
                      { label: t('Select type'), value: '' },
                      ...(employmentTypes?.values?.map((e) => ({
                        label: e.value,
                        value: e.value,
                      })) || []),
                    ]}
                  />
                  {errors.employementType && (
                    <div style={{ color: 'red', fontSize: 12 }}>
                      {errors.employementType}
                    </div>
                  )}
                </InputLabelContainer>

                <InputLabelContainer>
                  <label>
                    {t('End_Date')}
                    <Required>*</Required>
                  </label>
                  <DateInputWrapper ref={calendarResignRef}>
                    <TextInput
                      type="text"
                      placeholder="Select Date"
                      name="endDate"
                      value={
                        form.endDate ? formatDate(new Date(form.endDate)) : ''
                      }
                      onFocus={() => setIsResignDateOpen(true)}
                      onClick={() => setIsResignDateOpen(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span
                      className="iconArea"
                      onClick={() => setIsResignDateOpen(true)}
                    >
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isResignDateOpen && (
                        <Calendar
                          title="End Date"
                          minDate={
                            form.startDate
                              ? new Date(form.startDate)
                              : new Date('2000-01-01')
                          }
                          maxDate={new Date()}
                          selectedDate={
                            form.endDate ? new Date(form.endDate) : null
                          }
                          handleDateInput={(date: Date | null) => {
                            if (!date) return;
                            handleFormChange(
                              'endDate',
                              date.toLocaleDateString('en-CA')
                            );
                            setIsResignDateOpen(false);
                          }}
                          handleCalenderChange={() => {}}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                  {errors.endDate && (
                    <div style={{ color: 'red', fontSize: 12 }}>
                      {errors.endDate}
                    </div>
                  )}
                </InputLabelContainer>
              </ColumnWrapper>
            </FormInputsContainer>
            <div
              style={{
                display: 'flex',
                justifyContent: 'center',
                gap: 16,
                marginTop: 24,
              }}
            >
              <CancelButton onClick={closeModal}>{t('Cancel')}</CancelButton>

              <SaveButton onClick={handleSave} disabled={isSaving}>
                {isSaving
                  ? editingJobId
                    ? 'Updating...'
                    : 'Saving...'
                  : editingJobId
                    ? 'Update'
                    : 'Save'}
              </SaveButton>
            </div>
          </Modal>
        </ModalOverlay>
      )}

      {isDeleteModalOpen && (
        <CenterModal
          handleModalLeftButtonClick={() => setIsDeleteModalOpen(false)}
          handleModalClose={() => setIsDeleteModalOpen(false)}
          handleModalSubmit={confirmDelete}
          modalHeading=""
          modalContent="Do you want to Delete Employment History?"
          modalType="discardModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
          modalLeftButtonText="No"
          modalRightButtonText="Yes"
        />
      )}
      {toast && (
        <ToastMessage
          messageType={toast.type}
          messageBody={toast.message}
          messageHeading={toast.head}
          handleClose={() => setToast(null)}
        />
      )}
    </Container>
  );
};

export default EmploymentHistory;
