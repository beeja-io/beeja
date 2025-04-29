import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useUser } from '../../context/UserContext';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import {
  TabContentMainContainer,
  TabContentMainContainerHeading,
  TabContentEditArea,
  BorderDivLine,
  TabContentInnerContainer,
  TabContentTable,
  TabContentTableTd,
  InlineInput
} from '../../styles/MyProfile.style';
import {
  EditWhitePenSVG,
  CheckBoxOnSVG,
  CrossMarkSVG,
} from '../../svgs/CommonSvgs.svs';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import ToastMessage from './ToastMessage.component';
import {
  getOrganizationValuesByKey,
  updateEmployeeDetailsByEmployeeId,
} from '../../service/axiosInstance';
import {
  formatDate,
  formatDateYYYYMMDD
} from '../../utils/dateFormatter';
import { EMPLOYEE_MODULE } from '../../constants/PermissionConstants';
import { isValidEmail, isValidPINCode } from '../../utils/formInputValidators';
import { hasPermission } from '../../utils/permissionCheck';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';

type GeneralDetailsTabProps = {
  heading: string;
  details: { label: string; value: string }[];
  isEditModeOn: boolean;
  handleIsEditModeOn: () => void;
  employee: EmployeeEntity;
  fetchEmployeeAgain: () => void;
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

  const [modifiedFields, setModifiedFields] = useState<{ [key: string]: string }>( {} );
  const [isFormSubmitted, setIsFormSubmitted] = useState(false);

  let numToDivide = 1;
  if (details.length > 6) {
    numToDivide = 2;
  }
  const splitIndex = Math.ceil(details.length / numToDivide);
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

  const [originalFormData, setOriginalFormData] = useState<{ [key: string]: string }>({});

  // When form with new values is submitted , the new Values need to be updated in Form Data to display.
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

  const [joiningDate, setJoiningDate] = useState<string>('');

  const [isFormFieldsValid, setIsFormFieldsValid] = useState(false);
  const handleIsFormEmailValid = () => {
    setIsFormFieldsValid(!isFormFieldsValid);
  };

  const [formErrorToastMessage, setFormErrorToastMessage] = useState('');
  const [formErrorToastHead, setFormErrorToastHead] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setModifiedFields((prevState) => ({
      ...prevState,
      'Joining Date': joiningDate,
    }));
    const mobileNumbersLabels = ['Phone Number', 'Alt Phone Number', 'Phone'];
    for (let i = 0; i <= mobileNumbersLabels.length - 1; i++) {
      if (
        mobileNumbersLabels[i] in modifiedFields &&
        formData[mobileNumbersLabels[i]].length < 10
      ) {
        setFormErrorToastHead('Error in updating profile');
        setFormErrorToastMessage(`${mobileNumbersLabels[i]} must be 10 digits`);
        handleIsFormEmailValid();
        return;
      }
    }

    const postalCode = 'Postal Code';
    if (
      postalCode in modifiedFields &&
      (formData[postalCode]?.trim().length < 6 ||
        !isValidPINCode(formData[postalCode]))
    ) {
      setFormErrorToastHead('Error in updating profile');
      setFormErrorToastMessage('Postal Code must be 6 digits!');
      handleIsFormEmailValid();
      return;
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

    // No fields are modified, So I'm not sending the request
    if (Object.keys(modifiedFields).length === 0) {
      handleIsEditModeOn();
      setIsUpdateResponseLoading(false);
      return;
    }
    try {
      await updateEmployeeDetailsByEmployeeId(
        employee.account.employeeId,
        mapFormDataToBackendStructure(modifiedFields)
      );
      resetFormData();
      fetchEmployeeAgain();
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
      if (
        error.response &&
        error.response.data &&
        error.response.data === 'Email is already registered'
      ) {
        handleMailRegesteredError();
      } else {
        handleUpdateErrorOccured();
      }
      setIsUpdateResponseLoading(false);
    } finally {
      resetFormData();
    }
  };

  const mapFormDataToBackendStructure = (data: { [key: string]: string }): any => {
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
  const editableFieldsForLoggedInEmployee = [
    'Alt Email Address',
    'Alt Phone Number',
  ];
  const allowFullEditingAccess =
    user && hasPermission(user, EMPLOYEE_MODULE.UPDATE_ALL_EMPLOYEES);

  const [departmentList, setDepartmentList] = useState<OrgDefaults>( {} as OrgDefaults );
  const [jobTitles, setJobTitles] = useState<OrgDefaults>( {} as OrgDefaults );
  const [employmentTypes, setEmploymentTypes] = useState<OrgDefaults>( {} as OrgDefaults );
  const [isDefaultResponseLoading, setIsDefaultResponseLoading] = useState(false);
  
  // Object.values is used to convert Object to array
  const optionsByLabel:{ [key: string]: string[]} = {
    Nationality: ['India','German','American'],
    Country: ['India','Germany','UnitedStates'],
    Department: Object.values(departmentList),
    Designation: Object.values(jobTitles),
    'Employment Type': Object.values(employmentTypes),
    Gender : ['Male', 'Female'],
    'Marital Status' : ['Single', 'Married']
  };

  const labelsToExcludeFromStrictAlphabets = ['Email Address','Alt Email Address','Primary Address',
    'Email','Phone','Date of Birth','Joining Date', 'Phone Number' ,'Alt Phone Number'
  ];

const handleInputChange = (label: string, value: string) => {
    const inputValue = value;
    if (label === 'Postal Code') {
        const validValue = inputValue.replace( /\D/g,'').slice(0, 6);  // maximum 6 digits it accepts for Postal Code
        handleChange(label, validValue);
    }
    else if (label === 'Aadhar Number') {
        const validValue = inputValue.replace(/\D/g,'').slice(0, 12);   // maximum 6 digits it accepts for Aadhar Number
        handleChange(label, validValue);
    } 
    else if (label === 'Phone' || label === 'Phone Number' || label ==='Alt Phone Number') {
        const validValue = inputValue.replace(/\D/g,'').slice(0, 10);    // maximum 10 digits it accepts for Phone Number
        handleChange(label, validValue);
    } 
    // Only Alphabets and Spaces allowed for fields not in labelsToExcludeFromStrictAlphabets List.
    else if ( !labelsToExcludeFromStrictAlphabets.includes(label) ) {
        if((/^[a-zA-Z\s]*$/.test(inputValue) ||inputValue === '')){
            handleChange(label, inputValue);
        }
    }
    else if (labelsToExcludeFromStrictAlphabets.includes(label)) {
        handleChange(label, inputValue);
    }
}

  // Effect : This useEffect runs only when the Job tab is active.
  useEffect(() => {
    if (heading === 'Employment Info') {
      const fetchData = async () => {
        try {
          setIsDefaultResponseLoading(true);
          const response = await getOrganizationValuesByKey('departments');
          const jobDetailsResponse =
            await getOrganizationValuesByKey('jobTitles');
          const employmentTypesResponse =
            await getOrganizationValuesByKey('employmentTypes');
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

  // Check if the edit area should be shown:
  // 1) User has full editing access and editing other profiles
  // 2) OR User is editing their Personal Info section only.
  const showEditArea = (allowFullEditingAccess &&
    user.employeeId != employee.account.employeeId) || (heading === 'Personal Info' &&
        user?.employeeId === employee.account.employeeId)

  return (
    <>
      {isDefaultResponseLoading ? (
        <SpinAnimation />
      ) : (
        <TabContentMainContainer>
          <TabContentMainContainerHeading>
            <h4>{heading}</h4>
            {showEditArea && (
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
            ) 
        }
          </TabContentMainContainerHeading>
          <BorderDivLine width="100%" />
          <TabContentInnerContainer>
            <div>
              <TabContentTable>
                {firstColumn.map(({ label, value }) => (
                  <tr key={label}>
                    <TabContentTableTd>{t(label)}</TabContentTableTd>
                    {isEditModeOn &&
                    ((user?.employeeId === employee.account.employeeId &&
                      editableFieldsForLoggedInEmployee.includes(label)) ||
                      (allowFullEditingAccess &&
                        user.employeeId !== employee.account.employeeId)) ? (
                      <TabContentTableTd>
                        {['Country','Nationality','Department','Employment Type','Designation'].includes(label)?
                          (
                            (<select 
                                className="selectOptionContent" 
                                onChange={(e) => handleChange(label,e.target.value)}
                                value={formData[label] !== undefined ? formData[label] : '' }
                              >
                                <option value="">Select</option>
                                {optionsByLabel[label].map((optionValue) => (
                                  <option key={optionValue} value={optionValue}>{optionValue}</option>
                                ))}
                              </select>)
                          ):(
                            <InlineInput
                            type={ label === 'Date of Birth' || label === 'Joining Date' ? 'date' : 'text'}
                            max={label === 'Date of Birth' || label === 'Joining Date' ? new Date().toISOString().split('T')[0] : undefined }
                            value={formData[label] !== undefined ?
                                    (label === 'Date of Birth' || label === 'Joining Date' ? 
                                        formatDateYYYYMMDD(formData[label]) : formData[label] ) : ""
                                }
                            placeholder={`Enter ${t(label)}`}
                            onChange={(e) => {handleInputChange(label,e.target.value);
                                             if(label === 'Joining Date'){
                                                setJoiningDate(e.target.value)
                                             }
                            }}
                            />
                        )}
                      </TabContentTableTd>
                      ) : (
                        <>
                          {value != '-' && (label === 'Joining Date' || label === 'Date of Birth') ? (
                            <TabContentTableTd>
                              {formatDate(value)}
                            </TabContentTableTd>
                          ) : (
                            <TabContentTableTd>{t(value)}</TabContentTableTd>
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
                      {isEditModeOn &&
                      ((user?.employeeId === employee.account.employeeId &&
                        editableFieldsForLoggedInEmployee.includes(label)) ||
                        (allowFullEditingAccess &&
                          user.employeeId !== employee.account.employeeId)) ? (
                        <TabContentTableTd>
                          {label === 'Gender' || label === 'Marital Status' ? (
                              <select className="selectOptionContent" 
                                onChange={(e) => handleChange(label,e.target.value)}
                                value={formData[label] !== undefined ? formData[label] : ''}
                              >
                                <option value="">Select</option>
                                {optionsByLabel[label].map((optionValue) => (
                                    <option key={optionValue} value={optionValue}>{optionValue}</option>
                                ))}
                              </select>
                          ) : (
                            <InlineInput
                                type={'text'}
                                placeholder={`Enter ${label}`}
                                value={ formData[label] !== undefined ? formData[label] : value }
                                onChange={(e) => handleInputChange(label, e.target.value)}
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


