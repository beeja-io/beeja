import React, { useEffect, useMemo, useState, useRef } from 'react';
import { formatDateDDMMYYYY } from '../../utils/dateFormatter';
import ToastMessage from './ToastMessage.component';
import {
  fetchEmployeeHistory,
  addEmployeeHistory,
  updateEmployeeHistory,
  deleteEmployeeHistory,
} from '../../service/axiosInstance';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';
import { DropdownOrg } from './DropDownMenu.component';
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
import { EditIcon, DeleteIcon, ActionIcon } from '../../svgs/ExpenseListSvgs.svg';
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
  SaveButton
} from '../../styles/EmploymentHistory.styles';

interface JobHistoryItem {
  id?: string;
  designation: string;
  employementType?: string;
  department?: string;
  joiningDate?: string;
  resignationDate?: string;
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
  employmentTypes
}) => {
  const [historyList, setHistoryList] = useState<JobHistoryItem[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingJobId, setEditingJobId] = useState<string | null>(null);
  const [form, setForm] = useState<JobHistoryItem>({ designation: '' });
  const [toast, setToast] = useState<{ type: 'success' | 'error'; message: string } | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [showMenuIndex, setShowMenuIndex] = useState<String | null>(null);
  const calendarJoinRef = useRef<HTMLDivElement>(null);
  const calendarResignRef = useRef<HTMLDivElement>(null);
  const [isJoinDateOpen, setIsJoinDateOpen] = useState(false);
  const [isResignDateOpen, setIsResignDateOpen] = useState(false);
  const actionMenuRef = useRef<HTMLDivElement>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const orderedHistory = useMemo(() => historyList, [historyList]);
  const [errors, setErrors] = useState<Partial<Record<keyof JobHistoryItem, string>>>({});
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [deleteJobId, setDeleteJobId] = useState<string | null>(null);
  const { user } = useUser();

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

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);


  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (calendarJoinRef.current && !calendarJoinRef.current.contains(event.target as Node)) {
        setIsJoinDateOpen(false);
      }
      if (calendarResignRef.current && !calendarResignRef.current.contains(event.target as Node)) {
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
      setHistoryList((data || []).reverse());
    
    } catch {
      setToast({ type: 'error', message: 'Failed to fetch history' });
    }
    finally {
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
    const jobToEdit = historyList.find(job => job.id === jobId);
    if (!jobToEdit) return;

    setForm({
      designation: jobToEdit.designation,
      employementType: jobToEdit.employementType,
      department: jobToEdit.department,
      joiningDate: jobToEdit.joiningDate,
      resignationDate: jobToEdit.resignationDate,
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
    setErrors((prev) => ({ ...prev, [key]: "" }));
  };

  const handleAdd = async () => {
    if (!form.designation || !form.employementType || !form.joiningDate || !form.resignationDate) {
      setToast({ type: 'error', message: 'Please fill all required fields' });
      return;
    }

    setIsSaving(true);
    try {
      const newItem = await addEmployeeHistory(employeeId, form);
      setHistoryList([newItem, ...historyList]);
      setToast({
        type: 'success',
        message: 'Employment history added successfully',
      });
      fetchHistory();
      closeModal();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while adding employment history',
      });
    } finally {
      setIsSaving(false);
    }
  };

  const handleEdit = async () => {
    if (!form.designation || !form.employementType || !form.joiningDate || !form.resignationDate) {
      setToast({ type: 'error', message: 'Please fill all required fields' });
      return;
    }
    if (!editingJobId) return;

    setIsSaving(true);

    try {
      await updateEmployeeHistory(employeeId, editingJobId, form);
      const newList = historyList.map(job =>
        job.id === editingJobId
          ? { ...form, id: editingJobId, updatedAt: new Date().toISOString() }
          : job
      );
      setHistoryList(newList);
      setToast({
        type: 'success',
        message: 'Employment history updated successfully',
      });
      fetchHistory();
      closeModal();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while updating employment history',
      });
    }
    finally {
      setIsSaving(false);
    }
  };
  const handleSave = () => {
    const newErrors: Partial<Record<keyof JobHistoryItem, string>> = {};

    if (!form.designation) newErrors.designation = "Please Select designation";
    if (!form.employementType) newErrors.employementType = "Please Select employment type";
    if (!form.joiningDate) newErrors.joiningDate = "Please Select joining date";
    if (!form.resignationDate) newErrors.resignationDate = "Please Select resignation date";

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
      });
      fetchHistory();
    } catch {
      setToast({
        type: 'error',
        message: 'Error occurred while deleting employment history',
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

    if (months < 0) {
      years--;
      months += 12;
    }

    if (years < 0) return '';

    if (years === 0 && months === 0) return '• 0 months';
    if (years === 0) return `• ${months} month${months > 1 ? 's' : ''}`;
    if (months === 0) return `• ${years} year${years > 1 ? 's' : ''}`;

    return `• ${years} year${years > 1 ? 's' : ''}, ${months} month${months > 1 ? 's' : ''}`;
  };
  const isSuperAdmin = () => user?.roles.some(role => role.name === "Super Admin");

  return (
    <Container>
      <Header>
        <Title>Employment History</Title>
        {isSuperAdmin() && (
          <Button
            className="submit shadow"
            onClick={openAdd}
            width="100px"
            style={{ marginLeft: 'auto' }}
          >
            <AddNewPlusSVG />
            Add
          </Button>
        )}
      </Header>

      <Timeline scrollable={historyList.length > 3}>
        {isLoading && <SpinAnimation />}

        {orderedHistory.map((job, idx) => (
          <TimelineItem
            key={job.id || idx}
            style={{ marginBottom: '28px' }}
          >
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
                        {job.joiningDate ? formatDateDDMMYYYY(job.joiningDate) : '-'} –{' '}
                        {job.resignationDate
                          ? formatDateDDMMYYYY(job.resignationDate)
                          : 'Present'}{' '}
                        {calculateDiff(job.joiningDate, job.resignationDate)}
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
                          setShowMenuIndex(showMenuIndex === job.id ? null : job.id || "")
                        }
                      >
                        <ActionIcon />
                      </div>
                      {showMenuIndex === job.id && (
                        <ActionsMenu ref={actionMenuRef}>
                          <div
                            onClick={() => openEdit(job.id || "")}
                            style={{
                              cursor: 'pointer',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '8px',
                              padding: '4px 8px'
                            }}
                          >
                            <EditIcon /> Edit
                          </div>
                          <div
                            onClick={() => handleDelete(job.id || "")}
                            style={{
                              cursor: 'pointer',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '8px',
                              padding: '4px 8px'
                            }}
                          >
                            <DeleteIcon /> Delete
                          </div>
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
                    {job.updatedBy ? `Updated by ${job.updatedBy}` : ''}
                  </Small>
                  <Small>
                    {job.updatedAt
                      ? `Updated on: ${formatDateDDMMYYYY(job.updatedAt)}`
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
              {editingJobId !== null ? 'Edit Employment History' : 'Add Employment History'}
            </Title>
            <FormInputsContainer>
              <ColumnWrapper>
                <InputLabelContainer>
                  <label>
                    Designation<Required>*</Required>
                  </label>
                  <DropdownOrg
                    label="Designation"
                    selected={form.designation || ''}
                    options={
                      jobTitles?.values?.map((j) => ({
                        label: j.value,
                        value: j.value,
                      })) || []
                    }
                    onChange={(selectedValue) =>
                      handleFormChange('designation', selectedValue as string)
                    }
                    className="styledDropdown"
                  />
                  {errors.designation && <div style={{ color: "red", fontSize: 12 }}>{errors.designation}</div>}
                </InputLabelContainer>

                <InputLabelContainer>
                  <label>Joining Date<Required>*</Required></label>
                  <DateInputWrapper ref={calendarJoinRef}>
                    <TextInput
                      type="text"
                      placeholder="Select Date"
                      name="joiningDate"
                      value={form.joiningDate ? formatDate(new Date(form.joiningDate)) : ''}
                      onFocus={() => setIsJoinDateOpen(true)}
                      onClick={() => setIsJoinDateOpen(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span className="iconArea" onClick={() => setIsJoinDateOpen(true)}>
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isJoinDateOpen && (
                        <Calendar
                          title="Joining Date"
                          minDate={new Date('2000-01-01')}
                          selectedDate={form.joiningDate ? new Date(form.joiningDate) : null}
                          handleDateInput={(date: Date | null) => {
                            if (!date) return;
                            handleFormChange('joiningDate', date.toLocaleDateString('en-CA'));
                            setIsJoinDateOpen(false);
                          }}
                          handleCalenderChange={() => { }}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                  {errors.joiningDate && <div style={{ color: "red", fontSize: 12 }}>{errors.joiningDate}</div>}
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>Note</label>
                  <TextInput
                    name="note"
                    placeholder="Type your Note (Optional)"
                    value={form.description || ''}
                    onChange={(e) => handleFormChange('description', e.target.value)}
                    className="largeInput"
                  />
                </InputLabelContainer>
              </ColumnWrapper>
              <ColumnWrapper>
                <InputLabelContainer>
                  <label>
                    Employment Type<Required>*</Required>
                  </label>
                  <DropdownOrg
                    label="Employment Type"
                    selected={form.employementType || ''}
                    options={
                      employmentTypes?.values?.map((e) => ({
                        label: e.value,
                        value: e.value,
                      })) || []
                    }
                    onChange={(selectedValue) =>
                      handleFormChange('employementType', selectedValue as string)
                    }
                    className="styledDropdown"
                  />
                  {errors.employementType && <div style={{ color: "red", fontSize: 12 }}>{errors.employementType}</div>}
                </InputLabelContainer>

                <InputLabelContainer>
                  <label>Resignation Date<Required>*</Required></label>
                  <DateInputWrapper ref={calendarResignRef}>
                    <TextInput
                      type="text"
                      placeholder="Select Date"
                      name="resignationDate"
                      value={form.resignationDate ? formatDate(new Date(form.resignationDate)) : ''}
                      onFocus={() => setIsResignDateOpen(true)}
                      onClick={() => setIsResignDateOpen(true)}
                      readOnly
                      autoComplete="off"
                    />
                    <span className="iconArea" onClick={() => setIsResignDateOpen(true)}>
                      <CalenderIconDark />
                    </span>
                    <div className="calendarSpace">
                      {isResignDateOpen && (
                        <Calendar
                          title="Resignation Date"
                          minDate={form.joiningDate ? new Date(form.joiningDate) : new Date('2000-01-01')}
                          selectedDate={form.resignationDate ? new Date(form.resignationDate) : null}
                          handleDateInput={(date: Date | null) => {
                            if (!date) return;
                            handleFormChange('resignationDate', date.toLocaleDateString('en-CA'));
                            setIsResignDateOpen(false);
                          }}
                          handleCalenderChange={() => { }}
                        />
                      )}
                    </div>
                  </DateInputWrapper>
                  {errors.resignationDate && <div style={{ color: "red", fontSize: 12 }}>{errors.resignationDate}</div>}
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
              <CancelButton
                onClick={closeModal}
              >
                Cancel
              </CancelButton>

              <SaveButton
                onClick={handleSave}
                disabled={isSaving}

              >
                {isSaving ? 'Saving...' : 'Save'}
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
          messageHeading={toast.type === 'success' ? 'Done' : 'Update unsuccessful'}
          handleClose={() => setToast(null)}
        />
      )}
    </Container>
  );
};

export default EmploymentHistory;
