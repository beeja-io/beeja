import { useCallback, useEffect, useState } from 'react';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import {
  TabContentMainContainer,
  TabContentMainContainerHeading,
  TabContentEditArea,
  BorderDivLine,
  TabContentInnerContainer,
  TabContentTable,
  TabContentTableTd,
  InlineInput,
} from '../../styles/MyProfile.style';
import {
  EditWhitePenSVG,
  CheckBoxOnSVG,
  CrossMarkSVG,
} from '../../svgs/CommonSvgs.svs';
import { KYC_MODULE } from '../../constants/PermissionConstants';
import { useUser } from '../../context/UserContext';
import { useTranslation } from 'react-i18next';
import ToastMessage from './ToastMessage.component';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import { updateKycDetails } from '../../service/axiosInstance';
import {
  isValidAccountNo,
  isValidIFSCCode,
  isValidPanCardNo,
  isValidAadhaarNumber,
  isValidPassportNumber,
} from '../../utils/formInputValidators';
import { hasPermission } from '../../utils/permissionCheck';

type KycTabContentProps = {
  heading: string;
  details: { label: string; value: string }[];
  isEditModeOn: boolean;
  handleIsEditModeOn: () => void;
  employee: EmployeeEntity;
  // fetchEmployeeAgain: () => void;
};

const KycTabContent = ({
  heading,
  details,
  isEditModeOn,
  handleIsEditModeOn,
  employee,
  // fetchEmployeeAgain,
}: KycTabContentProps) => {
  const { t } = useTranslation();
  const { user } = useUser();

  const [modifiedFields, setModifiedFields] = useState<{
    [key: string]: string;
  }>({});
  const [formData, setFormData] = useState<{ [key: string]: string }>({});
  const [originalFormData, setOriginalFormData] = useState<{
    [key: string]: string;
  }>({});
  const [validationErrors, setValidationErrors] = useState<{
    [key: string]: string;
  }>({});
  const [isUpdateToastMessage, setIsUpdateToastMessage] = useState(false);
  const [isUpdateErrorOccured, setIsUpdateErrorOccured] = useState(false);
  const [isUpdateResponseLoading, setIsUpdateResponseLoading] = useState(false);
  const [isFormSubmitted, setIsFormSubmitted] = useState(false);

  const allowFullEditingAccess =
    user &&
    ((hasPermission(user, KYC_MODULE.UPDATE_KYC) &&
      employee.account.employeeId == user.employeeId) ||
      hasPermission(user, KYC_MODULE.UPDATE_ALL_KYC));

  const initializeFormData = useCallback(() => {
    const defaultFormData: { [key: string]: string } = {};
    details.forEach(({ label, value }) => {
      defaultFormData[label] = value;
    });
    setFormData(defaultFormData);
    setOriginalFormData(defaultFormData);
    setModifiedFields({});
    setValidationErrors({});
  }, [details]);

  useEffect(() => {
    if (!isEditModeOn && isFormSubmitted) {
      initializeFormData();
      setIsFormSubmitted(false);
    } else if (isEditModeOn) {
      initializeFormData();
    }
  }, [isEditModeOn, isFormSubmitted, details, initializeFormData]);

  const validateField = (
    label: string,
    value: string,
    showError: boolean,
    setValidationErrors: React.Dispatch<
      React.SetStateAction<{ [key: string]: string }>
    >
  ): boolean => {
    let isValid = true;
    let errorMessage = '';

    switch (label) {
      case 'Account Number':
        if (value.trim() !== '') {
          isValid = isValidAccountNo(value);
          errorMessage = 'Please enter a valid Account Number';
        }
        break;
      case 'IFSC Code':
        if (value.trim() !== '') {
          isValid = isValidIFSCCode(value);
          errorMessage = 'Please enter a valid IFSC Code';
        }
        break;
      case 'PAN Number':
        if (value.trim() !== '') {
          isValid = isValidPanCardNo(value);
          errorMessage = 'PAN Number must be format of ABCDH9008L';
        }
        break;
      case 'Aadhaar Number':
        if (value.trim() !== '') {
          isValid = isValidAadhaarNumber(value);
          errorMessage =
            'The Aadhaar Number should consist of exactly 12 digits.';
        }
        break;
      case 'Passport Number':
        if (value.trim() !== '' && value !== originalFormData[label]) {
          isValid = isValidPassportNumber(value);
          errorMessage = 'Please enter a valid Passport Number';
        }
        break;
      default:
        isValid = true;
    }

    if (showError && !isValid) {
      setValidationErrors((prevErrors) => ({
        ...prevErrors,
        [label]: errorMessage,
      }));
      setTimeout(() => {
        setValidationErrors((prevErrors) => ({
          ...prevErrors,
          [label]: '',
        }));
      }, 4000);
    } else {
      setValidationErrors((prevErrors) => ({
        ...prevErrors,
        [label]: '',
      }));
    }

    return isValid;
  };

  const handleChange = (label: string, newValue: string) => {
    let processedValue = newValue.replace(/-/g, ''); // Remove hyphens for all fields

    // Automatically capitalize all alphabets
    processedValue = processedValue.toUpperCase();

    // Restrict input of alphabets for specific fields
    const numericFields = ['Account Number', 'Aadhaar Number'];
    const alphanumericFields = ['IFSC Code', 'PAN Number', 'Passport Number'];
    const bankInputFeilds = ['Bank Name'];
    const branchInputFeilds = ['Branch Name'];
    if (numericFields.includes(label) && /\D/.test(processedValue)) {
      // If field should be numeric and contains non-numeric characters, ignore the change
      return;
    }
    if (
      alphanumericFields.includes(label) &&
      /[^A-Z0-9]/.test(processedValue)
    ) {
      return;
    }
    if (bankInputFeilds.includes(label) && /[^A-Z\s]/.test(processedValue)) {
      return;
    }

    if (branchInputFeilds.includes(label) && /[^A-Z]/.test(processedValue)) {
      return;
    }

    setFormData((prevData) => ({
      ...prevData,
      [label]: processedValue,
    }));

    if (originalFormData[label] !== processedValue) {
      setModifiedFields((prevModifiedFields) => ({
        ...prevModifiedFields,
        [label]: processedValue,
      }));
    }
  };

  const handleBlur = (label: string) => {
    validateField(label, formData[label], true, setValidationErrors);
  };

  const resetFormData = () => {
    initializeFormData();
  };

  const cancelEdit = () => {
    resetFormData();
    handleIsEditModeOn();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsUpdateResponseLoading(true);

    let allFieldsValid = true;
    details.forEach(({ label }) => {
      if (
        modifiedFields[label] !== undefined &&
        !validateField(label, formData[label], true, setValidationErrors)
      ) {
        allFieldsValid = false;
      }
    });

    if (!allFieldsValid) {
      setIsUpdateResponseLoading(false);
      return;
    }

    if (Object.keys(modifiedFields).length === 0) {
      handleIsEditModeOn();
      setIsUpdateResponseLoading(false);
      return;
    }

    try {
      const updatedData = { ...originalFormData, ...modifiedFields };
      await updateKycDetails(
        employee.employee.employeeId,
        mapFormDataToBackendStructure(updatedData)
      );
      setIsUpdateToastMessage(true);
      setIsFormSubmitted(true);
      handleIsEditModeOn();
    } catch {
      setIsUpdateErrorOccured(true);
    } finally {
      setIsUpdateResponseLoading(false);
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

  const mapFormDataToBackendStructure = (data: {
    [key: string]: string;
  }): any => {
    const backendData: { [key: string]: string | undefined } = {};

    for (const label in data) {
      if (Object.prototype.hasOwnProperty.call(data, label)) {
        const backendKey = handleFinalDataToBeSentToBackend(label);
        const updatedValue = data[label];

        setBackendData(backendData, backendKey, updatedValue);
      }
    }

    return backendData;
  };

  const handleFinalDataToBeSentToBackend = (label: string): string => {
    switch (label) {
      case 'Aadhaar Number':
        return 'kycDetails.aadhaarNumber';
      case 'PAN Number':
        return 'kycDetails.panNumber';
      case 'Passport Number':
        return 'kycDetails.passportNumber';
      case 'Account Number':
        return 'bankDetails.accountNo';
      case 'IFSC Code':
        return 'bankDetails.ifscCode';
      case 'Bank Name':
        return 'bankDetails.bankName';
      case 'Branch Name':
        return 'bankDetails.branchName';
      default:
        return label.toLowerCase().replace(/\s/g, '');
    }
  };

  return (
    <>
      <TabContentMainContainer>
        <TabContentMainContainerHeading>
          <h4>{heading}</h4>
          {allowFullEditingAccess && (
            <TabContentEditArea>
              {(allowFullEditingAccess ||
                user.employeeId === employee.account.employeeId) &&
                (!isEditModeOn ? (
                  <span
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
                    <span title="Discard Changes" onClick={cancelEdit}>
                      <CrossMarkSVG />
                    </span>
                  </span>
                ))}
            </TabContentEditArea>
          )}
        </TabContentMainContainerHeading>
        <BorderDivLine width="100%" />
        <TabContentInnerContainer>
          <div>
            <TabContentTable>
              {details.map(({ label, value }) => (
                <tr key={label}>
                  <TabContentTableTd>{t(label)}</TabContentTableTd>
                  {isEditModeOn && allowFullEditingAccess ? (
                    <TabContentTableTd key={label}>
                      <div>
                        <InlineInput
                          type="text"
                          value={
                            Number(formData[label]) === 0
                              ? ''
                              : (formData[label] ?? '')
                          }
                          placeholder={
                            label === 'Account Number'
                              ? 'Enter Account Number'
                              : label === 'IFSC Code'
                                ? 'Enter IFSC Code'
                                : `Enter ${t(label)}`
                          }
                          onChange={(e) => handleChange(label, e.target.value)}
                          onFocus={(e) => {
                            if (e.target.value === '-') {
                              handleChange(label, '');
                            }
                          }}
                          onBlur={() => handleBlur(label)}
                          maxLength={
                            label === 'Aadhaar Number' ||
                            label === 'Passport Number'
                              ? 12
                              : label === 'PAN Number'
                                ? 10
                                : label === 'IFSC Code'
                                  ? 11
                                  : label === 'Bank Name' ||
                                      label === 'Branch Name'
                                    ? 40
                                    : 10
                          }
                        />

                        {validationErrors[label] && (
                          <span className="validation-error">
                            {validationErrors[label]}
                          </span>
                        )}
                      </div>
                    </TabContentTableTd>
                  ) : (
                    <TabContentTableTd>{value || '-'}</TabContentTableTd>
                  )}
                </tr>
              ))}
            </TabContentTable>
          </div>
        </TabContentInnerContainer>
      </TabContentMainContainer>
      {isUpdateToastMessage && (
        <ToastMessage
          messageType="success"
          messageBody="Employee details have been updated successfully"
          messageHeading="Successfully Updated"
          handleClose={() => setIsUpdateToastMessage(false)}
        />
      )}
      {isUpdateErrorOccured && (
        <ToastMessage
          messageType="error"
          messageBody="Error occurred while updating user"
          messageHeading="Update Unsuccessful"
          handleClose={() => setIsUpdateErrorOccured(false)}
        />
      )}
      {isUpdateResponseLoading && <SpinAnimation />}
    </>
  );
};

export default KycTabContent;
