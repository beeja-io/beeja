import { ChangeEvent, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { EMPLOYEE_MODULE } from '../../constants/PermissionConstants';
import { useUser } from '../../context/UserContext';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';
import {
  getOrganizationValuesByKey,
  updateEmployeeDetailsByEmployeeId,
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
import EmploymentHistory from './EmploymentHistory.component';

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
  const { user } = useUser();

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
      defaultFormData[label] = value === '-' ? '' : value;
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

  useEffect(() => {
    if (isEditModeOn) {
      const initialFormData: { [key: string]: string } = {};
      details.forEach(({ label, value }) => {
        initialFormData[label] = value === '-' ? '' : value;
      });

      setFormData(initialFormData);
      setOriginalFormData(initialFormData);

      if (initialFormData['Joining Date']) {
        setJoiningDate(initialFormData['Joining Date']);
      }
    }
  }, [isEditModeOn, details]);

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
      defaultFormData[label] = value === '-' ? '' : value;
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
      const originalEmployeeId = employee.account.employeeId;
      const updatedEmployeeId = formData['Employee Id'] || originalEmployeeId;

      await updateEmployeeDetailsByEmployeeId(
        originalEmployeeId,
        mapFormDataToBackendStructure(modifiedFields)
      );
      resetFormData();
      fetchEmployeeAgain(updatedEmployeeId);
      handleUpdateToastMessage();
      setIsUpdateResponseLoading(false);
      setIsFormSubmitted(true);

      const defaultFormData: { [key: string]: string } = {};
      details.forEach(({ label, value }) => {
        defaultFormData[label] = value;
      });
      setFormData(defaultFormData);
      setOriginalFormData(defaultFormData);
      setModifiedFields({});
      handleIsEditModeOn();
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

  const [showHistory, setShowHistory] = useState(false);

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
                        setShowHistory(false);
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
                {firstColumn
                  .sort((a, b) => {
                    if (heading === 'Employment Info') {
                      if (
                        a.label === 'Joining Date' &&
                        b.label === 'Employment Type'
                      )
                        return -1;
                      if (
                        a.label === 'Employment Type' &&
                        b.label === 'Joining Date'
                      )
                        return 1;
                    }
                    return 0;
                  })
                  .map(({ label, value }) => (
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
                                    ? departmentList?.values?.map(
                                        (d) => d.value
                                      )
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
                                          employee.employee.jobDetails
                                            .joiningDate
                                        ? formatDateDDMMYYYY(
                                            employee.employee.jobDetails
                                              .joiningDate
                                          )
                                        : ''
                                  }
                                  onChange={(e) =>
                                    setJoiningDate(e.target.value)
                                  }
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
                                (label === 'Employee Id' &&
                                  user &&
                                  user.employeeId !==
                                    employee.account.employeeId &&
                                  !user.roles.some((role) =>
                                    role.permissions.includes(
                                      EMPLOYEE_MODULE.UPDATE_EMPLOYEE
                                    )
                                  )) ??
                                false
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
                                  ? formData[label] === '-' ? ''
                                    : label === 'Date of Birth' ||
                                    label === 'Joining Date'
                                    ? formatDateYYYYMMDD(formData[label])
                                    : formData[label]
                                  : value === '-' ? '' : value
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
                            <TabContentTableTd>
                              {t(value)}{' '}
                              {heading === 'Employment Info' &&
                                label === 'Employment Type' && (
                                  <ViewHistoryLink
                                    onClick={() =>
                                      setShowHistory((prev) => !prev)
                                    }
                                    $open={showHistory}
                                  >
                                    {t('View_History')}{' '}
                                    <svg
                                      width="12"
                                      height="12"
                                      viewBox="0 0 24 24"
                                      fill="none"
                                      xmlns="http://www.w3.org/2000/svg"
                                    >
                                      <path
                                        d="M6 9L12 15L18 9"
                                        stroke="#007AFF"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                      />
                                    </svg>
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
                                  ? formData[label] === '-' ? '' : formData[label]
                                  : value === '-' ? '' : value
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
                        <TabContentTableTd> {value === '-' || value === '' ? '' : t(value)}</TabContentTableTd>
                      )}
                    </tr>
                  ))}
                </TabContentTable>
              </div>
            )}
          </TabContentInnerContainer>
          {showHistory && (
            <div style={{ marginTop: 18 }}>
              <div
                style={{
                  borderTop: '1px solid #ddd',
                  margin: '12px 0',
                }}
              />
              <EmploymentHistory
                employeeId={employee.account.employeeId}
                jobTitles={jobTitles}
                employmentTypes={employmentTypes}
              />
            </div>
          )}
        </TabContentMainContainer>
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
