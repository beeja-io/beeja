import { ChangeEvent, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { EMPLOYEE_MODULE } from '../../constants/PermissionConstants';
import { useUser } from '../../context/UserContext';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';
import {
  getOrganizationValuesByKey,
  updateEmployeeDetailsByEmployeeId,
  fetchMe, 
  getEmployeeHistory, 
} from '../../service/axiosInstance';
import { Select } from '../../styles/CommonStyles.style';
import {
  BorderDivLine,
  CalendarContainer,
  CalendarInputContainer,
  InlineInput,
  StyledCalendarIconDark,
  TabContentEditArea,
  TabContentInnerContainer,
  TabContentMainContainer,
  TabContentMainContainerHeading,
  TabContentTable,
  TabContentTableTd,
  EmploymentHistoryContainer, 
  HistoryItem, 
  HistoryContent, 
  Dot , 
  ViewHistoryLink, 
} from '../../styles/MyProfile.style';
import {
  CheckBoxOnSVG,
  CrossMarkSVG,
  EditWhitePenSVG,
} from '../../svgs/CommonSvgs.svs';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import {
  formatDate,
  formatDateDDMMYYYY,
  formatDateYYYYMMDD,
} from '../../utils/dateFormatter';
import { isValidEmail, isValidPINCode } from '../../utils/formInputValidators';
import { hasPermission } from '../../utils/permissionCheck';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import Calendar from './Calendar.component';
import ToastMessage from './ToastMessage.component';
import { DropdownOrg } from './DropDownMenu.component';

type GeneralDetailsTabProps = {
  heading: string;
  details: { label: string; value: string }[];
  isEditModeOn: boolean;
  handleIsEditModeOn: () => void;
  employee: EmployeeEntity;
  fetchEmployeeAgain: (employeeId: string) => void;
};

export const GeneralDetailsTab = ({
  heading,
  details,
  isEditModeOn,
  handleIsEditModeOn,
  employee,
  fetchEmployeeAgain,
}: GeneralDetailsTabProps) => {
  const { t } = useTranslation();
  const [currentUser, setCurrentUser] = useState<any>(null);

useEffect(() => {
  fetchMe().then((res) => setCurrentUser(res.data));
}, []);

  const { user } = useUser();
  const [showEmploymentHistory, setShowEmploymentHistory] = useState(false);
  const [modifiedFields, setModifiedFields] = useState<{
    [key: string]: string;
  }>({});
  const [isFormSubmitted, setIsFormSubmitted] = useState(false);

  let numToDevide = 1;
  if (details.length > 6) {
    numToDevide = 2;
  }
  const splitIndex = Math.ceil(details.length / numToDevide);
  const firstColumn = details.slice(0, splitIndex);
  const secondColumn = details.slice(splitIndex);

  const cancelEdit = () => {
    const defaultFormData: { [key: string]: string } = {};
    details.forEach(({ label, value }) => {
      defaultFormData[label] = value;
    });
    setFormData(defaultFormData);
    handleIsEditModeOn();
  };

  const [originalFormData, setOriginalFormData] = useState<{
    [key: string]: string;
  }>({});

  // useEffect(() => {
  //   const initialFormData: { [key: string]: string } = {};
  //   details.forEach(({ label, value }) => {
  //     initialFormData[label] = value;
  //   });
  //   setOriginalFormData(initialFormData);
  // }, [details]);
  useEffect(() => {
    if (!isEditModeOn && isFormSubmitted) {
      const defaultFormData: { [key: string]: string } = {};
      details.forEach(({ label, value }) => {
        defaultFormData[label] = value;
      });
      setFormData(defaultFormData);
      setOriginalFormData(defaultFormData);
      setModifiedFields({});
      setIsFormSubmitted(false);
      setShowCalendar(false);
    }
  }, [isEditModeOn, isFormSubmitted, details]);
  const [formData, setFormData] = useState<{ [key: string]: string }>({});

  const handleChange = (label: string, newValue: string) => {
    setFormData((prevData) => ({ ...prevData, [label]: newValue }));
    if (originalFormData[label] !== newValue) {
      setModifiedFields((prevModifiedFields) => ({
        ...prevModifiedFields,
        [label]: newValue,
      }));
    }
  };

  const resetFormData = () => {
    const defaultFormData: { [key: string]: string } = {};
    details.forEach(({ label, value }) => {
      defaultFormData[label] = value;
    });
    setFormData(defaultFormData);
    setOriginalFormData(defaultFormData);
    setModifiedFields({});
    setIsFormSubmitted(false);
  };
  const [showCalendar, setShowCalendar] = useState(false);
  const [joiningDate, setJoiningDate] = useState<string>('');
  const toggleCalendar = () => {
    setShowCalendar((prev) => !prev);
  };

  const handleCalendarChange = (date: Date) => {
    if (!date) {
      return;
    }
    setJoiningDate(formatDateYYYYMMDD(date.toString()));
    setFormData((prevData) => ({
      ...prevData,
      ['Joining Date']: formatDateYYYYMMDD(date.toString()),
    }));
    if (
      originalFormData['Joining Date'] !== formatDateYYYYMMDD(date.toString())
    ) {
      setModifiedFields((prevModifiedFields) => ({
        ...prevModifiedFields,
        ['Joining Date']: formatDateYYYYMMDD(date.toString()),
      }));
    }
    setShowCalendar(false);
  };

  const [isFormFieldsValid, setIsFormFieldsValid] = useState(false);
  const handleIsFormEmailValid = () => {
    setIsFormFieldsValid(!isFormFieldsValid);
  };

  const [formErrorToastMessage, setFormErrorToastMessage] = useState('');
  const [formErrorToastHead, setFormErrorToastHead] = useState('');
    const [previousJobDetails, setPreviousJobDetails] = useState<EmployeeEntity['employee']['jobDetails'] | null>(null);// save
   const [employmentHistory, setEmploymentHistory] = useState<
  EmployeeEntity['employee']['jobDetails'][]
>([]);
// ... inside GeneralDetailsTab component

// Inside the GeneralDetailsTab component function
const [isAddingNew, setIsAddingNew] = useState(false);
const [editingIndex, setEditingIndex] = useState<number | null>(null);
const [historyFormData, setHistoryFormData] = useState({
  designation: '',
  employmentType: '',
  startDate: '',
  endDate: '',
  specialization: '',
});

// A single handler for the form inputs
const handleHistoryFormChange = (name: string, value: string) => {
  setHistoryFormData(prev => ({ ...prev, [name]: value }));
};

const handleAddHistoryItem = () => {
  setIsAddingNew(true);
  setEditingIndex(null);
  // Reset form to empty fields for a new entry
  console.log("add"); 
  setHistoryFormData({
    designation: '',
    employmentType: '',
    startDate: '',
    endDate: '',
    specialization: '',
  });
};

const handleEditHistoryItem = (index: number) => {
  setEditingIndex(index);
  setIsAddingNew(false);
  // Populate form with data of the selected item
  setHistoryFormData(history[index]);
};

const handleSubmitHistory = async () => {
  try {
    // You'll need to create a payload from historyFormData
    const newHistoryEntry = {
      ...historyFormData,
      // You may need to add dates and other fields here
      // For example: startDate: formatDateYYYYMMDD(new Date(historyFormData.startDate)),
    };

    // Construct the payload to send to the backend
    const payload = {
      jobDetails: {
        ...employee.employee.jobDetails, // Keep existing job details
        // Add the new or updated history entry
        employmentHistory: isAddingNew
          ? [...history, newHistoryEntry]
          : history.map((item, index) =>
              index === editingIndex ? newHistoryEntry : item
            ),
      },
    };

    // Call your update API
    await updateEmployeeDetailsByEmployeeId(employee.account.employeeId, payload);
    
    // After a successful update, refresh the employee data
    fetchEmployeeAgain(employee.account.employeeId);

    // Reset state to hide the form
    setIsAddingNew(false);
    setEditingIndex(null);
    setHistoryFormData({
      designation: '',
      employmentType: '',
      startDate: '',
      endDate: '',
      specialization: '',
    });
    // show success message
    handleUpdateToastMessage();
  } catch (error) {
    // Handle errors
    handleUpdateErrorOccured();
  }
};

const handleCancelAddOrEdit = () => {
  setIsAddingNew(false);
  setEditingIndex(null);
};


const [history, setHistory] = useState<any[]>([]);
const originalEmployeeId = employee?.account?.employeeId;
const [historyLoading, setHistoryLoading] = useState(false);

useEffect(() => {
  // ensure employee.jobDetails is available before fetching
  const jdArray = Array.isArray(employee?.employee?.jobDetails)
    ? employee.employee.jobDetails
    : [employee?.employee?.jobDetails].filter(Boolean);

  if (showEmploymentHistory && originalEmployeeId && jdArray.length > 0) {
    fetchAndBuildHistory(originalEmployeeId);
  }
}, [showEmploymentHistory, originalEmployeeId, employee,historyLoading]);

const fetchAndBuildHistory = async (employeeId: string) => {
  try {
    setHistoryLoading(true);
    const historyResponse = await getEmployeeHistory(employeeId);
    const prev = Array.isArray(historyResponse?.data) ? historyResponse.data : [];

    // map previous items with default values
    const prevItems = prev.map((item: any) => ({
      designation: item.designation ?? '—',
      employmentType: item.employementType ?? item.employmentType ?? '—',
      department: item.department ?? '—',
      startDate: item.joiningDate ? formatDateDDMMYYYY(item.joiningDate) : '—',
      endDate: item.resignationDate ? formatDateDDMMYYYY(item.resignationDate) : 'Present',
      updatedBy: item.updatedBy ?? '—',
      updatedAt: item.updatedAt ? formatDateDDMMYYYY(item.updatedAt) : null,
      _sortKey: item.updatedAt
        ? new Date(item.updatedAt).getTime()
        : item.resignationDate
        ? new Date(item.resignationDate).getTime()
        : item.joiningDate
        ? new Date(item.joiningDate).getTime()
        : 0,
    }));

    // current job from employee.jobDetails
    const jdArray = Array.isArray(employee?.employee?.jobDetails)
      ? employee.employee.jobDetails
      : [employee?.employee?.jobDetails].filter(Boolean);

    const currentJob = jdArray.find((job: any) => job.resignationDate === null);

    const currentItem = currentJob
      ? {
          designation: currentJob.designation ?? '—',
          employmentType: currentJob.employementType ?? currentJob.employmentType ?? '—',
          department: currentJob.department ?? '—',
          startDate: currentJob.joiningDate ? formatDateDDMMYYYY(currentJob.joiningDate) : '—',
          endDate: 'Present',
          updatedBy: currentJob.updatedBy ?? '—',
          updatedAt: currentJob.updatedAt ? formatDateDDMMYYYY(currentJob.updatedAt) : null,
          _sortKey: currentJob.updatedAt
            ? new Date(currentJob.updatedAt).getTime()
            : currentJob.joiningDate
            ? new Date(currentJob.joiningDate).getTime()
            : 0,
        }
      : null;

    // only add current job if it doesn’t exist
    if (
      currentItem &&
      !prevItems.some(
        (item) =>
          item.designation === currentItem.designation &&
          item.startDate === currentItem.startDate &&
          item.endDate === currentItem.endDate
      )
    ) {
      prevItems.push(currentItem);
    }

    // sort newest first
    prevItems.sort((a, b) => (b._sortKey ?? 0) - (a._sortKey ?? 0));

    setHistory(prevItems);
  } catch (err) {
    console.error('Error fetching employment history:', err);
    setHistory([]);
  }
  finally {
    // Set loading to false whether the call succeeded or failed
    setHistoryLoading(false);
  }
};

    const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setJoiningDate(joiningDate);
    setModifiedFields((prevState) => ({
      ...prevState,
      'Joining Date': joiningDate,
    }));
    const mobileNumbersLabels = ['Phone Number', 'Alt Phone Number', 'Phone'];
    for (let i = 0; i < mobileNumbersLabels.length; i++) {
      const label = mobileNumbersLabels[i];
      const value = formData[label];

      if (label in modifiedFields) {
        if (value.trim() !== '' && value.length < 10) {
          setFormErrorToastHead('Error in Updating Profile');
          setFormErrorToastMessage(`${label} must be 10 digits`);
          handleIsFormEmailValid();
          return;
        }
      }
    }

    const postalCode = 'Postal Code';
    if (
      postalCode in modifiedFields &&
      (formData[postalCode]?.trim().length < 6 ||
        !isValidPINCode(formData[postalCode]))
    ) {
      if (formData[postalCode]?.trim() == '') {
        setFormData((prev: typeof formData) => ({ ...prev, [postalCode]: '' }));
      } else {
        setFormErrorToastHead('Error in updating profile');
        setFormErrorToastMessage('Postal Code must be 6 digits!');
        handleIsFormEmailValid();
        return;
      }
    }

    const fullName = 'Full Name';
    if (fullName in modifiedFields && formData[fullName]?.trim().length === 0) {
      setFormErrorToastHead('Error in updating profile');
      setFormErrorToastMessage('Full Name is mandatory, kindly fill it!');
      handleIsFormEmailValid();
      return;
    }
    const email = 'Email Address';
    if (email in modifiedFields && formData[email]?.trim().length === 0) {
      setFormErrorToastHead('Error in updating profile');
      setFormErrorToastMessage('Email is mandatory, kindly fill it!');
      handleIsFormEmailValid();
      return;
    }
    const employeeId = 'Employee Id';
    if (employeeId in modifiedFields) {
      const empIdValue = formData[employeeId]?.trim();
      const hasAlphabet = /[a-zA-Z]/.test(empIdValue);
      const hasNumber = /\d/.test(empIdValue);

      if (!empIdValue) {
        setFormErrorToastHead('Error in updating profile');
        setFormErrorToastMessage('Employee ID cannot be empty');
        handleIsFormEmailValid();
        return;
      }

      if (!hasAlphabet || !hasNumber) {
        setFormErrorToastHead('Error in updating profile');
        setFormErrorToastMessage(
          'Employee ID must contain at least one letter and one number'
        );
        handleIsFormEmailValid();
        return;
      }
    }
    const emailLabels = ['Email Address', 'Alt Email Address', 'Email'];
    for (const emailLabel of emailLabels) {
      const emailValue = formData[emailLabel] || originalFormData[emailLabel];

      if (emailLabel in modifiedFields && !isValidEmail(emailValue)) {
        setFormErrorToastHead('Invalid Email');
        setFormErrorToastMessage(
          `${
            emailLabel === 'Email'
              ? 'Emergency email'
              : emailLabel === 'Email Address'
                ? 'Email Address'
                : 'Alt. Email Address'
          } must be in format of example@exam.com`
        );
        handleIsFormEmailValid();
        return;
      }
    }

    handleIsUpdateResponseLoading();

    Object.keys(modifiedFields).forEach((fieldLabel) => {
      if (!isFieldEditable(fieldLabel)) {
        delete modifiedFields[fieldLabel];
      }
    });
    if (Object.keys(modifiedFields).length === 0) {
      // No fields are modified, So I'm not sending the request
      handleIsEditModeOn();
      setIsUpdateResponseLoading(false);
      return;
    }
    try {
       const previousJobDetails = { ...employee.employee.jobDetails }
       setPreviousJobDetails({ ...employee.employee.jobDetails });

      const updatedEmployeeId = formData['Employee Id'] || originalEmployeeId;
     
    console.log('Current user:', currentUser)
const mapped = mapFormDataToBackendStructure(modifiedFields);

const payload = {
  jobDetails: {
    ...mapped.jobDetails,   // ✅ flatten here
    updatedBy: `${currentUser.firstName} ${currentUser.lastName}`,
  }
};

await updateEmployeeDetailsByEmployeeId(originalEmployeeId, payload);

      resetFormData();
      fetchEmployeeAgain(updatedEmployeeId);
      handleUpdateToastMessage();
      setIsUpdateResponseLoading(false);
      setIsFormSubmitted(true);
      /*await fetchAndBuildHistory(originalEmployeeId);*/

    /* try {
    const historyResponse = await getEmployeeHistory(originalEmployeeId);
    console.log('Employment history 1:', historyResponse.data);
    
    const formattedHistory = historyResponse.data.map((item: any) => ({
  designation: item.designation,                 // e.g. "Front End Developer"
  employmentType: item.employementType,          // e.g. "Intern"
  department: item.department,                   // e.g. "HR"
  startDate: formatDateDDMMYYYY(item.joiningDate),
  endDate: item.resignationDate
    ? formatDateDDMMYYYY(item.resignationDate)
    : 'Present',
  updatedBy: item.updatedBy,
  updatedAt: formatDateDDMMYYYY(item.updatedAt),
}));

   console.log("Formatted history:", formattedHistory);
    setHistory(formattedHistory); // ✅ valid to call here
  } catch (err) {
    console.error('Error fetching employment history:', err);
  }
*/
      const defaultFormData: { [key: string]: string } = {};
      details.forEach(({ label, value }) => {
        defaultFormData[label] = value;
      });
      setFormData(defaultFormData);
      setOriginalFormData(defaultFormData);
      setModifiedFields({});
      handleIsEditModeOn();
     setEmploymentHistory((prev) => [
  ...prev,
  {
    ...previousJobDetails,           // old snapshot
    updatedBy: currentUser?.firstName || 'Unknown',
    updatedAt: new Date().toISOString(),
  },
  {
    ...employee.employee.jobDetails, // updated snapshot
    updatedBy: currentUser?.firstName || 'Unknown',
    updatedAt: new Date().toISOString(),
  },
]);


    } catch (error: any) {
      const errorMessage = error?.response?.data?.message;

      if (errorMessage === 'Email is already registered') {
        handleMailRegesteredError();
      } else if (errorMessage === 'Employee ID already exists') {
        setFormErrorToastHead('Update Failed');
        setFormErrorToastMessage('Employee ID already exists');
        handleIsFormEmailValid();
      } else {
        handleUpdateErrorOccured();
      }
      setIsUpdateResponseLoading(false);
    } finally {
      resetFormData();
    }
  };

  const mapFormDataToBackendStructure = (data: {
    [key: string]: string;
  }): any => {
    const backendData: { [key: string]: string | undefined } = {};

    for (const label in data) {
      if (Object.prototype.hasOwnProperty.call(data, label)) {
        const backendKey = handleFinalDataToBeSentToBackend(label);
        let updatedValue = data[label];

        if (label === 'Full Name') {
          const fullNameWords = updatedValue.split(' ');
          // If more than one word, consider the last word as the last name
          if (fullNameWords.length > 1) {
            const lastName = fullNameWords.pop();
            backendData['lastName'] = lastName;
            updatedValue = fullNameWords.join(' ');
          } else backendData['lastName'] = '';
        }
        // Only include modified fields
        if (originalFormData[label] !== updatedValue) {
          setBackendData(backendData, backendKey, updatedValue);
        }
      }
    }

    return backendData;
  };

  const handleFinalDataToBeSentToBackend = (label: string): string => {
    switch (label) {
      case 'Employee Id':
        return 'employeeId';
      case 'Full Name':
        return 'firstName';
      case 'Date of Birth':
        return 'personalInformation.dateOfBirth';
      case 'Nationality':
        return 'personalInformation.nationality';
      case 'Email Address':
        return 'email';
      case 'Alt Email Address':
        return 'contact.alternativeEmail';
      case 'Gender':
        return 'personalInformation.gender';
      case 'Marital Status':
        return 'personalInformation.maritalStatus';
      case 'Phone Number':
        return 'contact.phone';
      case 'Alt Phone Number':
        return 'contact.alternativePhone';
      case 'Primary Address':
        return 'address.landMark';
      case 'City':
        return 'address.city';
      case 'State':
        return 'address.state';
      case 'Country':
        return 'address.country';
      case 'Postal Code':
        return 'address.pinCode';
      case 'Designation':
        return 'jobDetails.designation';
      case 'Employment Type':
        return 'jobDetails.employementType';
      case 'Department':
        return 'jobDetails.department';
      case 'Joining Date':
        return 'jobDetails.joiningDate';
      case 'Name':
        return 'personalInformation.nomineeDetails.name';
      case 'Email':
        return 'personalInformation.nomineeDetails.email';
      case 'Phone':
        return 'personalInformation.nomineeDetails.phone';
      case 'Relation Type':
        return 'personalInformation.nomineeDetails.relationType';
      case 'Aadhar Number':
        return 'personalInformation.nomineeDetails.aadharNumber';
      case 'Personal Tax ID':
        return 'personalInformation.personalTaxId';
      default:
        return label.toLowerCase().replace(/\s/g, '');
    }
  };

  const setBackendData = (obj: any, path: string, value: string): void => {
    const keys = path.split('.');
    keys.reduce((acc, key, index) => {
      if (index === keys.length - 1) {
        acc[key] = value;
      } else {
        acc[key] = acc[key] || {};
      }
      return acc[key];
    }, obj);
  };

  const [isUpdateToastMessage, setIsUpdateToastMessage] = useState(false);
  const handleUpdateToastMessage = () => {
    setIsUpdateToastMessage(!isUpdateToastMessage);
  };

  const [isMailRegesteredError, setMailRegesteredError] = useState(false);
  const handleMailRegesteredError = () => {
    setMailRegesteredError(!isMailRegesteredError);
  };

  const [isUpdateErrorOccured, setISUpdateErrorOccured] = useState(false);
  const handleUpdateErrorOccured = () => {
    setISUpdateErrorOccured(!isUpdateErrorOccured);
  };

  const [isUpdateResponseLoading, setIsUpdateResponseLoading] = useState(false);
  const handleIsUpdateResponseLoading = () => {
    setIsUpdateResponseLoading(!isUpdateResponseLoading);
  };
  const isFieldEditable = (label: string): boolean => {
    if (allowFullEditingAccess) return true;
    const editableFields = ['Alt Email Address', 'Alt Phone Number'];

    return (
      editableFields.includes(label) &&
      user?.employeeId === employee?.account?.employeeId
    );
  };

  const allowFullEditingAccess =
    user && hasPermission(user, EMPLOYEE_MODULE.UPDATE_ALL_EMPLOYEES);

  const [departmentList, setDepartmentList] = useState<OrgDefaults>(
    {} as OrgDefaults
  );
  const [jobTitles, setJobTitles] = useState<OrgDefaults>({} as OrgDefaults);
  const [employmentTypes, setEmploymentTypes] = useState<OrgDefaults>(
    {} as OrgDefaults
  );
  
  const [isDefaultResponseLoading, setIsDefaultResponseLoading] =
    useState(false);

  useEffect(() => {
    if (heading === 'Employment Info') {
      const fetchData = async () => {
        try {
          setIsDefaultResponseLoading(true);
          const response = await getOrganizationValuesByKey('departments');
          const jobDetailsResponse =
            await getOrganizationValuesByKey('jobTitles');
          const employmentTypesResponse =
            await getOrganizationValuesByKey('employeeTypes');
          setEmploymentTypes(employmentTypesResponse.data);
          setJobTitles(jobDetailsResponse.data);
          setDepartmentList(response.data);
          setIsDefaultResponseLoading(false);
        } catch (error) {
          setIsDefaultResponseLoading(false);
        }
      };

      fetchData();
    }
  }, [heading]);
  /*const handleToggleHistory = async () => {
  if (!showEmploymentHistory) {
    try {
      const historyResponse = await getEmployeeHistory(employee.account.employeeId);
      const formattedHistory = historyResponse.data.map((item: any) => ({
        designation: item.designation,
        employmentType: item.employementType,
        department: item.department,
        startDate: formatDateDDMMYYYY(item.joiningDate),
        endDate: item.resignationDate
          ? formatDateDDMMYYYY(item.resignationDate)
          : 'Present',
        updatedBy: item.updatedBy,
        updatedAt: formatDateDDMMYYYY(item.updatedAt),
      }));
      console.log("Iam in")
      setHistory(formattedHistory);
    } catch (err) {
      console.error("Error fetching employment history:", err);
    }
  }
  setShowEmploymentHistory((prev) => !prev);
};*/
const calculateDuration = (startDate:any, endDate:any) => {
  if (!startDate) return "—";
  const start = new Date(startDate);
  const end = endDate === "Present" ? new Date() : new Date(endDate);
  let years = end.getFullYear() - start.getFullYear();
  let months = end.getMonth() - start.getMonth();
  if (months < 0) {
    years -= 1;
    months += 12;
  }
  const yearString = years > 0 ? `${years} yr` : "";
  const monthString = months > 0 ? `${months} mo` : "";
  let durationString = [yearString, monthString].filter(Boolean).join(" ");
  return durationString || "Less than a month";
};
  return (
    <>
      {isDefaultResponseLoading ? (
        <SpinAnimation />
      ) : (
        <TabContentMainContainer>
          <TabContentMainContainerHeading>
            <h4>{heading}</h4>
            {allowFullEditingAccess ? (
              <TabContentEditArea>
                {user &&
                  (allowFullEditingAccess ||
                    user.employeeId === employee.account.employeeId) &&
                  (!isEditModeOn ? (
                    <span
                      title={`Edit ${employee.account.firstName}'s Profile`}
                      onClick={() => {
                        handleIsEditModeOn();
                        resetFormData();
                      }}
                    >
                      <EditWhitePenSVG />
                    </span>
                  ) : (
                    <span>
                      <span title="Save Changes" onClick={handleSubmit}>
                        <CheckBoxOnSVG />
                      </span>
                      <span
                        title="Discard Changes"
                        onClick={() => {
                          cancelEdit();
                          resetFormData();
                        }}
                      >
                        <CrossMarkSVG />
                      </span>
                    </span>
                  ))}
              </TabContentEditArea>
            ) : heading === 'Personal Info' &&
              user?.employeeId === employee.account.employeeId ? (
              <TabContentEditArea>
                {user &&
                  (allowFullEditingAccess ||
                    user.employeeId === employee.account.employeeId) &&
                  (!isEditModeOn ? (
                    <span
                      title={`Edit ${employee.account.firstName}'s Profile`}
                      onClick={() => {
                        handleIsEditModeOn();
                        resetFormData();
                      }}
                    >
                      <EditWhitePenSVG />
                    </span>
                  ) : (
                    <span>
                      <span title="Save Changes" onClick={handleSubmit}>
                        <CheckBoxOnSVG />
                      </span>
                      <span
                        title="Discard Changes"
                        onClick={() => {
                          cancelEdit();
                          resetFormData();
                        }}
                      >
                        <CrossMarkSVG />
                      </span>
                    </span>
                  ))}
              </TabContentEditArea>
            ) : (
              ''
            )}
          </TabContentMainContainerHeading>
          <BorderDivLine width="100%" />
          <TabContentInnerContainer>
            <div>
              <TabContentTable>
                {firstColumn.map(({ label, value }) => (
                  <tr key={label}>
                    <TabContentTableTd>{t(label)}</TabContentTableTd>
                    {isEditModeOn && isFieldEditable(label) ? (
                      <TabContentTableTd>
                        {label === 'Country' ||
                        label === 'Nationality' ||
                        label === 'Department' ||
                        label === 'Employment Type' ||
                        label === 'Designation' ? (
                          <DropdownOrg
                            label={label}
                            selected={formData[label] ?? ''}
                            options={(label === 'Country'
                              ? ['India', 'Germany', 'United States']
                              : label === 'Nationality'
                                ? ['Indian', 'German', 'American']
                                : label === 'Department'
                                  ? departmentList?.values?.map((d) => d.value)
                                  : label === 'Employment Type'
                                    ? employmentTypes?.values?.map(
                                        (e) => e.value
                                      )
                                    : label === 'Designation'
                                      ? jobTitles?.values?.map((j) => j.value)
                                      : []
                            )?.map((value) => ({
                              label: value,
                              value: value,
                            }))}
                            onChange={(selectedValue) =>
                              handleChange(label, selectedValue as string)
                            }
                          />
                        ) : label === 'Joining Date' ? (
                          <>
                            <CalendarInputContainer>
                              <InlineInput
                                placeholder={t('Enter Joining Date')}
                                value={
                                  joiningDate
                                    ? formatDateDDMMYYYY(joiningDate)
                                    : employee.employee.jobDetails &&
                                        employee.employee.jobDetails.joiningDate
                                      ? formatDateDDMMYYYY(
                                          employee.employee.jobDetails
                                            .joiningDate
                                        )
                                      : ''
                                }
                                onChange={(e) => setJoiningDate(e.target.value)}
                                disabled
                              />
                              <StyledCalendarIconDark>
                                <span onClick={toggleCalendar}>
                                  <CalenderIconDark />
                                </span>
                              </StyledCalendarIconDark>
                            </CalendarInputContainer>
                          </>
                        ) : (
                          <InlineInput
                            disabled={
                              label === 'Employee Id' &&
                              user &&
                              user.employeeId !== employee.account.employeeId &&
                              !user.roles.some((role) =>
                                role.permissions.includes(
                                  EMPLOYEE_MODULE.UPDATE_EMPLOYEE
                                )
                              )
                            }
                            type={
                              label === 'Date of Birth' ||
                              label === 'Joining Date'
                                ? 'date'
                                : 'text'
                            }
                            max={new Date().toISOString().split('T')[0]}
                            value={
                              formData[label] !== undefined
                                ? label === 'Date of Birth' ||
                                  label === 'Joining Date'
                                  ? formatDateYYYYMMDD(formData[label])
                                  : formData[label]
                                : ''
                            }
                            placeholder={`Enter ${t(label)}`}
                            onFocus={(e) => {
                              if (e.target.value === '-') {
                                e.target.value = '';
                              }
                            }}
                            onChange={(e) => {
                              const inputValue = e.target.value;
                              const labelsToExcludeFromStrictAlphabets = [
                                'Email Address',
                                'Alt Email Address',
                                'Primary Address',
                                'Email',
                                'Phone',
                                'Date of Birth',
                                'Joining Date',
                                'Employee Id',
                              ];
                              if (label === 'Postal Code') {
                                const numericValue = inputValue.replace(
                                  /\D/g,
                                  ''
                                );
                                const validValue = numericValue.slice(0, 6);
                                handleChange(label, validValue);
                              } else if (label === 'Aadhar Number') {
                                const numericValue = inputValue.replace(
                                  /\D/g,
                                  ''
                                );
                                const validValue = numericValue.slice(0, 12);
                                handleChange(label, validValue);
                              } else if (label === 'Phone') {
                                const numericValue = inputValue.replace(
                                  /\D/g,
                                  ''
                                );
                                const validValue = numericValue.slice(0, 10);
                                handleChange(label, validValue);
                              } else if (label === 'Employee Id') {
                                handleChange(label, inputValue.toUpperCase());
                              } else if (
                                !labelsToExcludeFromStrictAlphabets.includes(
                                  label
                                ) &&
                                (/^[a-zA-Z\s]*$/.test(inputValue) ||
                                  inputValue === '')
                              ) {
                                handleChange(label, inputValue);
                              }

                              // For other cases in the exclusion list, allow any input
                              else if (
                                labelsToExcludeFromStrictAlphabets.includes(
                                  label
                                )
                              ) {
                                handleChange(label, inputValue);
                              }
                            }}
                          />
                        )}
                      </TabContentTableTd>
                    ) : (
                      <>
                        {value != '-' &&
                        (label === 'Joining Date' ||
                          label === 'Date of Birth') ? (
                          <TabContentTableTd>
                            {formatDate(value)}
                          </TabContentTableTd>
                        ) : (
                          <TabContentTableTd>{t(value)}
                            {label === 'Employment Type' && (
   <ViewHistoryLink
    href="#"
    onClick={(e) => {
      e.preventDefault();
      setShowEmploymentHistory(!showEmploymentHistory);
    }}
  >
    {showEmploymentHistory ? "Hide History" : "View History"}
  </ViewHistoryLink>
)}

</TabContentTableTd>
                          









         )}














                        
                </>



















                    )}
                  </tr>
                ))}
              </TabContentTable>
            </div>
            {secondColumn.length > 0 && (
              <div>
                <TabContentTable>
                  {secondColumn.map(({ label, value }) => (
                    <tr key={label}>
                      <TabContentTableTd>{t(label)}</TabContentTableTd>
                      {isEditModeOn && isFieldEditable(label) ? (        
                        <TabContentTableTd>
                          {label === 'Gender' || label === 'Marital Status' ? (
                            <DropdownOrg
                              label={label}
                              selected={
                                formData[label] !== undefined
                                  ? formData[label]
                                  : ''
                              }
                              options={(label === 'Gender'
                                ? ['Male', 'Female']
                                : ['Married', 'Single']
                              ).map((option) => ({
                                label: option,
                                value: option,
                              }))}
                              onChange={(selectedValue) => {
                                handleChange(label, selectedValue ?? '');
                              }}
                            />
                          ) : (
                            <InlineInput
                              type={label === 'Date of Birth' ? 'date' : 'text'}
                              placeholder={`Enter ${label}`}
                              value={
                                formData[label] !== undefined
                                  ? formData[label]
                                  : value
                              }
                              onFocus={(e) => {
                                if (e.target.value === '-') {
                                  e.target.value = '';
                                }
                              }}
                              onChange={(e) => {
                                const inputValue = e.target.value;
                                const labelsWhichAllowOnlyNumbers = [
                                  'Phone Number',
                                  'Alt Phone Number',
                                ];
                                if (
                                  labelsWhichAllowOnlyNumbers.includes(label)
                                ) {
                                  const numericInputValue = inputValue
                                    .replace(/[^0-9]/g, '')
                                    .slice(0, 10);
                                  handleChange(label, numericInputValue);
                                } else {
                                  handleChange(label, inputValue);
                                }
                              }}
                            />
                          )}
                        </TabContentTableTd>
                      ) : (
                        <TabContentTableTd>{t(value)}</TabContentTableTd>
                      )}
                    </tr>
                  ))}
                </TabContentTable>
              </div>
            )}
          </TabContentInnerContainer>
        </TabContentMainContainer>
      )}
{showEmploymentHistory && (
  <EmploymentHistoryContainer>
    <div className="history-header"> {/* Use a div for alignment */}
      <h3>Employment history</h3>
      {isEditModeOn && (
        <button className="add-history-button" onClick={handleAddHistoryItem}>
          Add
        </button>
      )}
    </div>
    {history.length > 0 &&
      [...history].map((job, index) => (
        <HistoryItem key={index}>
          <Dot />
          <HistoryContent>
            <p className="designation">
              {job.designation || "—"}
            </p>
            {isEditModeOn && (
              <span className="edit-history-icon" onClick={() => handleEditHistoryItem(index)}>
                <EditWhitePenSVG />
              </span>
            )}
            <div className="details-line">
              {job.employmentType && (
                <span className={`badge ${job.employmentType.toLowerCase().replace(/\s/g, '')}`}>
                  {job.employmentType}
                </span>
              )}
              <span className="duration">
                {job.startDate || "—"} - {job.endDate || "Present"}
                {job.startDate && (
                  <>
                    <span className="dot-divider">•</span>
                    <span className="calculated-duration">
                      {calculateDuration(job.startDate, job.endDate)}
                    </span>
                  </>
                )}
              </span>
            </div>
            {job.updatedBy && (
              <p className="updated">
                Updated by {job.updatedBy}{job.updatedRole ? ` (${job.updatedRole})` : ""}
              </p>
            )}
            {job.specialization && (
              <p className="specialization">{job.specialization}</p>
            )}
          </HistoryContent>
        </HistoryItem>
      ))}
  </EmploymentHistoryContainer>
)}

// Corrected JSX for the form
{(isAddingNew || editingIndex !== null) && (
  <TabContentEditArea>
    <TabContentInnerContainer>
      <h3>{editingIndex !== null ? "Edit Job" : "Add New Job"}</h3>
      <InlineInput
        type="text"
        name="designation"
        placeholder="Designation"
        value={historyFormData.designation} // Use historyFormData
        onChange={(e) => handleHistoryFormChange('designation', e.target.value)}
      />
      <Select
        name="employmentType"
        value={historyFormData.employmentType} // Use historyFormData
        onChange={(e) => handleHistoryFormChange('employmentType', e.target.value)}
      >
        <option value="">Select Type</option>
        <option value="Full-time">Full-time</option>
        <option value="Part-time">Part-time</option>
        <option value="Contract">Contract</option>
        <option value="Internship">Internship</option>
      </Select>
      {/* ... other inputs ... */}
      <button onClick={handleSubmitHistory}>Save</button>
      <button onClick={handleCancelAddOrEdit}>Cancel</button>
    </TabContentInnerContainer>
  </TabContentEditArea>
)}










      {isEditModeOn && showCalendar ? (
        <CalendarContainer>
          <Calendar
            title="Select a Date"
            handleCalenderChange={handleCalendarChange}
            handleDateInput={() => {}}
            selectedDate={null}
          />
        </CalendarContainer>
      ) : null}
      {isUpdateToastMessage && (
        <ToastMessage
          messageType="success"
          messageBody="Employee details has been updated successfully"
          messageHeading="Successfully Updated"
          handleClose={handleUpdateToastMessage}
        />
      )}
      {isMailRegesteredError && (
        <ToastMessage
          messageType="error"
          messageBody="Email Already Registered"
          messageHeading="Error Occured"
          handleClose={handleMailRegesteredError}
        />
      )}

      {isUpdateErrorOccured && (
        <ToastMessage
          messageType="error"
          messageBody="Error occured while updating user"
          messageHeading="Update unsuccessful"
          handleClose={handleUpdateErrorOccured}
        />
      )}

      {isFormFieldsValid && (
        <ToastMessage
          messageHeading={formErrorToastHead}
          messageBody={formErrorToastMessage}
          messageType="error"
          handleClose={handleIsFormEmailValid}
        />
      )}
      {isUpdateResponseLoading && <SpinAnimation />}
    </>
  );
};

interface SelectInputProps {
  label: string;
  value: string;
  options: string[];
  onChange: (label: string, value: string) => void;
  selected?: string;
}

export const SelectInput: React.FC<SelectInputProps> = ({
  label,
  value,
  options,
  onChange,
  selected,
}) => {
  return (
    // FIXME - Update below select options as per FIGMA
    <Select
      style={{
        width: '100%',
        borderRadius: '5px',
        border: 0,
        padding: '4px 3px',
        outline: '0',
      }}
      value={value}
      onChange={(e: ChangeEvent<HTMLSelectElement>) =>
        onChange(label, e.target.value)
      }
    >
      <option value="">Select</option>
      {[...(options || [])]
        .sort((a, b) => a.localeCompare(b))
        .map((option) => (
          <option key={option} value={option} selected={selected === option}>
            {option}
          </option>
        ))}
    </Select>
  );
};
