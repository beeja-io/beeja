import { useEffect, useState } from 'react';
import {
  MyProfileTabsDiv,
  MyProfileTabsMainContainer,
} from '../../styles/MyProfile.style';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { useUser } from '../../context/UserContext';
import { GeneralDetailsTab } from './MyProfileGeneralTabContent.component';
import KycTabContent from './MyProfileKYCTabContent.component';
import { DocumentTabContent } from './MyProfileDocumentTabContent.component';
import DeductionsTab from '../directComponents/DeductionsTab.component';
import {
  DOCUMENT_MODULE,
  EMPLOYEE_MODULE,
  HEALTH_INSURANCE_MODULE,
  KYC_MODULE,
} from '../../constants/PermissionConstants';
import { hasPermission } from '../../utils/permissionCheck';
import { hasFeature } from '../../utils/featureCheck';
import { useFeatureToggles } from '../../context/FeatureToggleContext';
import { EFeatureToggles } from '../../entities/FeatureToggle';
import HappyBirthday from '../directComponents/HappyBirthday';
import leafsAnimation from '../../images/birthdayAnimation.gif';
import { LOADING } from '../../constants/Constants';
import { useTranslation } from 'react-i18next';

type MyProfileTabsContainerComponentProps = {
  employee: EmployeeEntity;
  fetchEmployeeAgain: () => void;
  chooseTab: (tab: string) => void;
};
const MyProfileTabsContainerComponent = ({
  employee,
  fetchEmployeeAgain,
  chooseTab,
}: MyProfileTabsContainerComponentProps) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const { featureToggles } = useFeatureToggles();
  const [selectedTab, setSelectedTab] = useState('general');
  const [isActiveTab, setIsActiveTab] = useState('general');
  useEffect(() => {
    setSelectedTab(isActiveTab);
  }, [employee, isActiveTab]);

  const handleTabChange = (selectedTab: string) => {
    setSelectedTab(selectedTab);
    setIsActiveTab(selectedTab);
  };

  const personalDetails = [
    {
      label: t('FULL_NAME'),
      value: employee.account.firstName + ' ' + employee.account.lastName,
    },
    {
      label: t('DATE_OF_BIRTH'),
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.dateOfBirth
          ? employee.employee.personalInformation.dateOfBirth
          : '-',
    },
    {
      label: t('NATIONALITY'),
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nationality
          ? employee.employee.personalInformation.nationality
          : '-',
    },
    { label: t('EMAIL_ADDRESS'),
       value: employee.account.email 
    },
    {
      label: t('ALT_EMAIL_ADDRESS'),
      value:
        employee.employee.contact && employee.employee.contact.alternativeEmail
          ? employee.employee.contact.alternativeEmail
          : '-',
    },
    {
      label: t('GENDER'),
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.gender
          ? employee.employee.personalInformation.gender
          : '-',
    },
    {
      label: t('MARITAL_STATUS'),
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.maritalStatus
          ? employee.employee.personalInformation.maritalStatus
          : '-',
    },
    // FIXME - Update personal Tax ID
    { label: t('PERSONAL_TAX_ID'), value: '-' },
    {
      label: t('PHONE_NUMBER'),
      value:
        employee.employee.contact && employee.employee.contact.phone
          ? employee.employee.contact.phone
          : '-',
    },
    {
      label: t('ALT_PHONE_NUMBER'),
      value:
        employee.employee.contact && employee.employee.contact.alternativePhone
          ? employee.employee.contact.alternativePhone
          : '-',
    },
  ];

  const addressDetails = [
    {
      label: t('PRIMARY_ADDRESS'),
      value:
        employee.employee.address && employee.employee.address.landMark
          ? employee.employee.address.landMark
          : '-',
    },
    {
      label: t('CITY'),
      value:
        employee.employee.address && employee.employee.address.city
          ? employee.employee.address.city
          : '-',
    },
    {
      label: t('STATE'),
      value:
        employee.employee.address && employee.employee.address.state
          ? employee.employee.address.state
          : '-',
    },
    {
      label: t('COUNTRY'),
      value:
        employee.employee.address && employee.employee.address.country
          ? employee.employee.address.country
          : '-',
    },
    {
      label: t('POSTAL_CODE'),
      value:
        employee.employee.address && employee.employee.address.pinCode
          ? employee.employee.address.pinCode
          : '-',
    },
  ];

  const jobDetails = [
    {
      label: t('EMPLOYEE_ID'),
      value:
        employee && employee.account.employeeId
          ? employee.account.employeeId
          : '-',
    },
    {
      label: t('DESIGNATION'),
      value:
        employee && employee.employee.jobDetails
          ? employee.employee.jobDetails.designation
          : '-',
    },
    {
      label: t('DEPARTMENT'),
      value:
        employee &&
        employee.employee.jobDetails &&
        employee.employee.jobDetails.department
          ? employee.employee.jobDetails.department
          : '-',
    },
    {
      label: t('EMPLOYMENT_TYPE'),
      value:
        employee &&
        employee.employee.jobDetails &&
        employee.employee.jobDetails.employementType
          ? employee.employee.jobDetails.employementType
          : '-',
    },
    {
      label: t('JOINING_DATE'),
      value:
        employee &&
        employee.employee.jobDetails &&
        employee.employee.jobDetails.joiningDate
          ? employee.employee.jobDetails.joiningDate
          : '-',
    },
  ];

  const nomineeDetails = [
    {
      label: t('NAME'),
      value:
        employee &&
        employee.employee &&
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nomineeDetails &&
        employee.employee.personalInformation.nomineeDetails.name
          ? employee.employee.personalInformation.nomineeDetails.name
          : '-',
    },
    {
      label: t('EMAIL'),
      value:
        employee &&
        employee.employee &&
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nomineeDetails &&
        employee.employee.personalInformation.nomineeDetails.email
          ? employee.employee.personalInformation.nomineeDetails.email
          : '-',
    },
    {
      label: t('PHONE'),
      value:
        employee &&
        employee.employee &&
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nomineeDetails &&
        employee.employee.personalInformation.nomineeDetails.phone
          ? employee.employee.personalInformation.nomineeDetails.phone
          : '-',
    },
    {
      label: t('RELATION_TYPE'),
      value:
        employee &&
        employee.employee &&
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nomineeDetails &&
        employee.employee.personalInformation.nomineeDetails.relationType
          ? employee.employee.personalInformation.nomineeDetails.relationType
          : '-',
    },
  ];

  const kycDetails = [
    {
      label: t('AADHAAR_NUMBER'),
      value: employee?.employee?.kycDetails?.aadhaarNumber ?? '-',
    },
    {
      label: t('PAN_NUMBER'),
      value: employee?.employee?.kycDetails?.panNumber ?? '-',
    },
    {
      label: t('PASSPORT_NUMBER'),
      value: employee?.employee?.kycDetails?.passportNumber ?? '-',
    },
  ];

  const bankDetails = [
    {
      label: t('ACCOUNT_NUMBER'),
      value: employee?.employee?.bankDetails?.accountNo ?? '-',
    },
    {
      label: t('IFSC_CODE'),
      value: employee?.employee?.bankDetails?.ifscCode ?? '-',
    },
    {
      label: t('BANK_NAME'),
      value: employee?.employee?.bankDetails?.bankName ?? '-',
    },
    {
      label: t('BRANCH_NAME'),
      value: employee?.employee?.bankDetails?.branchName ?? '-',
    },
  ];

  const [isPersonalDetailsEditModeOn, setIsPersonalDetailsEditModeOn] =
    useState(false);
  const handleIsPersonalDetailsEditModeOn = () => {
    setIsPersonalDetailsEditModeOn(!isPersonalDetailsEditModeOn);
    setIsAddressDetailsEditModeOn(false);
    setIsNomineeDetailsEditModeOn(false);
    setIsJobAddressDetailsEditModeOn(false);
  };

  const [isAddressDetailsEditModeOn, setIsAddressDetailsEditModeOn] =
    useState(false);
  const handleIsAddressDetailsEditModeOn = () => {
    setIsAddressDetailsEditModeOn(!isAddressDetailsEditModeOn);
    setIsNomineeDetailsEditModeOn(false);
    setIsPersonalDetailsEditModeOn(false);
    setIsJobAddressDetailsEditModeOn(false);
  };

  const [isNomineeDetailsEditModeOn, setIsNomineeDetailsEditModeOn] =
    useState(false);
  const handleIsNomineeDetailsEditModeOn = () => {
    setIsNomineeDetailsEditModeOn(!isNomineeDetailsEditModeOn);
    setIsPersonalDetailsEditModeOn(false);
    setIsAddressDetailsEditModeOn(false);
    setIsJobAddressDetailsEditModeOn(false);
  };

  const [isJobAddressDetailsEditModeOn, setIsJobAddressDetailsEditModeOn] =
    useState(false);
  const handleIsJobAddressDetailsEditModeOn = () => {
    setIsJobAddressDetailsEditModeOn(!isJobAddressDetailsEditModeOn);
    setIsAddressDetailsEditModeOn(false);
    setIsNomineeDetailsEditModeOn(false);
    setIsPersonalDetailsEditModeOn(false);
  };

  const [
    isHealthInsuranceDetailsEditModeOn,
    setIsHealthInsuranceDetailsEditModeOn,
  ] = useState(false);
  const handleIsHealthInsuranceDetailsEditModeOn = () => {
    setIsHealthInsuranceDetailsEditModeOn(!isHealthInsuranceDetailsEditModeOn);
    setIsAddressDetailsEditModeOn(false);
    setIsNomineeDetailsEditModeOn(false);
    setIsPersonalDetailsEditModeOn(false);
  };

  const handleIsActiveTab = (tab: string) => {
    setIsActiveTab(tab);
    setIsJobAddressDetailsEditModeOn(false);
    setIsAddressDetailsEditModeOn(false);
    setIsNomineeDetailsEditModeOn(false);
    setIsPersonalDetailsEditModeOn(false);
    setIsHealthInsuranceDetailsEditModeOn(false);
  };

  const [isKycDetailsEditModeOn, setIsKycDetailsEditModeOn] = useState(false);
  const [isBankDetailsEditModeOn, setIsBankDetailsEditModeOn] = useState(false);

  const handleIsKycDetailsEditModeOn = () => {
    setIsKycDetailsEditModeOn(!isKycDetailsEditModeOn);
    setIsBankDetailsEditModeOn(false);
  };

  const handleIsBankDetailsEditModeOn = () => {
    setIsBankDetailsEditModeOn(!isBankDetailsEditModeOn);
    setIsKycDetailsEditModeOn(false);
  };
  const [showBirthdayAnimation, setShowBirthdayAnimation] = useState(false);
  const birthDate = employee?.employee?.personalInformation?.dateOfBirth;
  const todayDate = new Date().toISOString().split('T')[0];
  const birthDateObj = birthDate ? new Date(birthDate) : null;

  const isValidBirthDate =
    birthDateObj instanceof Date && !isNaN(birthDateObj.getTime());

  const todayMonthDay = todayDate.slice(5);
  const birthMonthDay = isValidBirthDate
    ? birthDateObj.toISOString().slice(5, 10)
    : null;

  useEffect(() => {
    const birthdayFlag = localStorage.getItem('birthdayAnimationShown');
    if (
      user &&
      user.employeeId === employee.account.employeeId &&
      birthMonthDay &&
      todayMonthDay === birthMonthDay &&
      !birthdayFlag
    ) {
      setShowBirthdayAnimation(true);
      localStorage.setItem('birthdayAnimationShown', 'true');
    }
  }, [todayMonthDay, birthMonthDay, user, employee.account.employeeId]);
  return (
    <MyProfileTabsMainContainer>
      {employee.employee.personalInformation && showBirthdayAnimation && (
        <span className="animationGif">
          <div className="imageArea">
            <img src={leafsAnimation} alt={LOADING} />
            <img src={leafsAnimation} alt={LOADING} />
            <img src={leafsAnimation} alt={LOADING} />
          </div>
          <div className="firework">
            <HappyBirthday />
          </div>
        </span>
      )}
      <MyProfileTabsDiv>
        <ul>
          <li
            className={isActiveTab === 'general' ? 'active' : ''}
            onClick={() => {
              handleTabChange('general');
              handleIsActiveTab('general');
              chooseTab('General');
            }}
          >
            {t('GENERAL')}
          </li>
          {(employee.account.employeeId === user?.employeeId ||
            (user &&
              hasPermission(
                user,
                EMPLOYEE_MODULE.READ_COMPLETE_EMPLOYEE_DETAILS
              ))) && (
            <li
              className={isActiveTab === 'job' ? 'active' : ''}
              onClick={() => {
                handleTabChange('job');
                handleIsActiveTab('job');
                chooseTab('Job');
              }}
            >
              {t('JOB')}
            </li>
          )}

          {/* FIXME - Update below when available */}
          {/* <li
                className={isActiveTab === 'payroll' ? 'active' : ''}
                onClick={() => {
                  handleTabChange('payroll');
                  handleIsActiveTab('payroll');
                }}
              >
                Payroll
              </li> */}
          {((employee.account.employeeId === user?.employeeId &&
            employee.account.roles.some((role) =>
              role.permissions.some(
                (permission) => permission === DOCUMENT_MODULE.READ_DOCUMENT
              )
            )) ||
            (user &&
              hasPermission(user, EMPLOYEE_MODULE.READ_ENTIRE_DOCUMENTS))) && (
            <li
              className={isActiveTab === 'documents' ? 'active' : ''}
              onClick={() => {
                handleTabChange('documents');
                handleIsActiveTab('documents');
                chooseTab('Documents');
              }}
            >
              {t('DOCUMENTS')}
            </li>
          )}
          {(employee.account.employeeId === user?.employeeId ||
            (user &&
              hasPermission(
                user,
                HEALTH_INSURANCE_MODULE.CREATE_HEALTH_INSURANCE
              ))) && (
            <li
              className={isActiveTab === 'deductions' ? 'active' : ''}
              onClick={() => {
                handleTabChange('deductions');
                handleIsActiveTab('deductions');
                chooseTab('Deductions');
              }}
            >
              {t('DEDUCTIONS')}
            </li>
          )}
          {user &&
            featureToggles &&
            hasFeature(
              featureToggles.featureToggles,
              EFeatureToggles.KYC_MANAGEMENT
            ) &&
            ((hasPermission(user, KYC_MODULE.READ_KYC) &&
              employee.account.employeeId === user.employeeId) ||
              hasPermission(user, KYC_MODULE.READ_ALL_KYC)) && (
              <li
                className={isActiveTab === 'kyc' ? 'active' : ''}
                onClick={() => {
                  handleTabChange('kyc');
                  handleIsActiveTab('kyc');
                  chooseTab('KYC');
                }}
              >
                {t('KYC')}
              </li>
            )}
        </ul>
      </MyProfileTabsDiv>

      <div style={{ overflow: 'hidden' }}>
        {selectedTab === 'general' && (
          <div>
            <GeneralDetailsTab
              heading={t('PERSONAL_INFO')}
              details={personalDetails}
              isEditModeOn={isPersonalDetailsEditModeOn}
              handleIsEditModeOn={handleIsPersonalDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
            />
            <GeneralDetailsTab
              heading={t('ADDRESS')}
              details={addressDetails}
              isEditModeOn={isAddressDetailsEditModeOn}
              handleIsEditModeOn={handleIsAddressDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
            />
            {user &&
              (hasPermission(
                user,
                EMPLOYEE_MODULE.READ_COMPLETE_EMPLOYEE_DETAILS
              ) ||
                // FIXME - Update after discussion with Prasad/Jay
                user.employeeId === employee.account.employeeId) && (
                <GeneralDetailsTab
                  heading={t('EMERGENCY_CONTACT')}
                  details={nomineeDetails}
                  isEditModeOn={isNomineeDetailsEditModeOn}
                  handleIsEditModeOn={handleIsNomineeDetailsEditModeOn}
                  employee={employee}
                  fetchEmployeeAgain={fetchEmployeeAgain}
                />
              )}
          </div>
        )}
        {selectedTab === 'job' && (
          <GeneralDetailsTab
            heading={t('EMPLOYMENT_INFO')}
            details={jobDetails}
            employee={employee}
            fetchEmployeeAgain={fetchEmployeeAgain}
            isEditModeOn={isJobAddressDetailsEditModeOn}
            handleIsEditModeOn={handleIsJobAddressDetailsEditModeOn}
          />
        )}
        {selectedTab === 'deductions' && (
          <DeductionsTab
            heading={t('HEALTH_INSURANCE')}
            handleIsEditModeOn={handleIsHealthInsuranceDetailsEditModeOn}
            isEditModeOn={isHealthInsuranceDetailsEditModeOn}
            employee={employee}
          />
        )}
        {selectedTab === 'payroll' && <>PAYROLL</>}
        {selectedTab === 'documents' && (
          <DocumentTabContent employee={employee} />
        )}

        {selectedTab === 'kyc' && (
          <div>
            <KycTabContent
              heading={t('KYC')}
              details={kycDetails}
              isEditModeOn={isKycDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
              handleIsEditModeOn={handleIsKycDetailsEditModeOn}
            />
            <KycTabContent
              heading={t('BANK_DETAILS')}
              details={bankDetails}
              isEditModeOn={isBankDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
              handleIsEditModeOn={handleIsBankDetailsEditModeOn}
            />
          </div>
        )}
      </div>
    </MyProfileTabsMainContainer>
  );
};

export default MyProfileTabsContainerComponent;
