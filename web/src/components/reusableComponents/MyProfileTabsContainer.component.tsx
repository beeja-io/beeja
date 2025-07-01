import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { LOADING } from '../../constants/Constants';
import {
  DOCUMENT_MODULE,
  EMPLOYEE_MODULE,
  HEALTH_INSURANCE_MODULE,
  KYC_MODULE,
} from '../../constants/PermissionConstants';
import { useFeatureToggles } from '../../context/FeatureToggleContext';
import { useUser } from '../../context/UserContext';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { EFeatureToggles } from '../../entities/FeatureToggle';
import leafsAnimation from '../../images/birthdayAnimation.gif';
import {
  MyProfileTabsDiv,
  MyProfileTabsMainContainer,
} from '../../styles/MyProfile.style';
import { hasFeature } from '../../utils/featureCheck';
import { hasPermission } from '../../utils/permissionCheck';
import DeductionsTab from '../directComponents/DeductionsTab.component';
import HappyBirthday from '../directComponents/HappyBirthday';
import { DocumentTabContent } from './MyProfileDocumentTabContent.component';
import { GeneralDetailsTab } from './MyProfileGeneralTabContent.component';
import KycTabContent from './MyProfileKYCTabContent.component';
import { useLocation } from 'react-router-dom';

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
  }, [isActiveTab]);

  const location = useLocation();

  useEffect(() => {
    if (location.pathname === '/profile/me') {
      handleTabChange('general');
      handleIsActiveTab('general');
      chooseTab('General');
    } 
  }, [location.pathname]);

  const handleTabChange = (selectedTab: string) => {
    setSelectedTab(selectedTab);
    setIsActiveTab(selectedTab);
  };

  const personalDetails = [
    {
      label: 'Full Name',
      value: employee.account.firstName + ' ' + employee.account.lastName,
    },
    {
      label: 'Date of Birth',
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.dateOfBirth
          ? employee.employee.personalInformation.dateOfBirth
          : '-',
    },
    {
      label: 'Nationality',
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.nationality
          ? employee.employee.personalInformation.nationality
          : '-',
    },
    { label: 'Email Address', value: employee.account.email },
    {
      label: 'Alt Email Address',
      value:
        employee.employee.contact && employee.employee.contact.alternativeEmail
          ? employee.employee.contact.alternativeEmail
          : '-',
    },
    {
      label: 'Gender',
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.gender
          ? employee.employee.personalInformation.gender
          : '-',
    },
    {
      label: 'Marital Status',
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.maritalStatus
          ? employee.employee.personalInformation.maritalStatus
          : '-',
    },

    {
      label: 'Personal Tax ID',
      value:
        employee.employee.personalInformation &&
        employee.employee.personalInformation.personalTaxId
          ? employee.employee.personalInformation.personalTaxId
          : '-',
    },
    {
      label: 'Phone Number',
      value:
        employee.employee.contact && employee.employee.contact.phone
          ? employee.employee.contact.phone
          : '-',
    },
    {
      label: 'Alt Phone Number',
      value:
        employee.employee.contact && employee.employee.contact.alternativePhone
          ? employee.employee.contact.alternativePhone
          : '-',
    },
  ];

  const addressDetails = [
    {
      label: 'Primary Address',
      value:
        employee.employee.address && employee.employee.address.landMark
          ? employee.employee.address.landMark
          : '-',
    },
    {
      label: 'City',
      value:
        employee.employee.address && employee.employee.address.city
          ? employee.employee.address.city
          : '-',
    },
    {
      label: 'State',
      value:
        employee.employee.address && employee.employee.address.state
          ? employee.employee.address.state
          : '-',
    },
    {
      label: 'Country',
      value:
        employee.employee.address && employee.employee.address.country
          ? employee.employee.address.country
          : '-',
    },
    {
      label: 'Postal Code',
      value:
        employee.employee.address && employee.employee.address.pinCode
          ? employee.employee.address.pinCode
          : '-',
    },
  ];

  const jobDetails = [
    {
      label: 'Employee Id',
      value:
        employee && employee.account.employeeId
          ? employee.account.employeeId
          : '-',
    },
    {
      label: 'Designation',
      value:
        employee && employee.employee.jobDetails
          ? employee.employee.jobDetails.designation
          : '-',
    },
    {
      label: 'Department',
      value:
        employee &&
        employee.employee.jobDetails &&
        employee.employee.jobDetails.department
          ? employee.employee.jobDetails.department
          : '-',
    },
    {
      label: 'Employment Type',
      value:
        employee &&
        employee.employee.jobDetails &&
        employee.employee.jobDetails.employementType
          ? employee.employee.jobDetails.employementType
          : '-',
    },
    {
      label: 'Joining Date',
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
      label: 'Name',
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
      label: 'Email',
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
      label: 'Phone',
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
      label: 'Relation Type',
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
      label: 'Aadhaar Number',
      value: employee?.employee?.kycDetails?.aadhaarNumber ?? '-',
    },
    {
      label: 'PAN Number',
      value: employee?.employee?.kycDetails?.panNumber ?? '-',
    },
    {
      label: 'Passport Number',
      value: employee?.employee?.kycDetails?.passportNumber ?? '-',
    },
  ];

  const bankDetails = [
    {
      label: 'Account Number',
      value: employee?.employee?.bankDetails?.accountNo ?? '-',
    },
    {
      label: 'IFSC Code',
      value: employee?.employee?.bankDetails?.ifscCode ?? '-',
    },
    {
      label: 'Bank Name',
      value: employee?.employee?.bankDetails?.bankName ?? '-',
    },
    {
      label: 'Branch Name',
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
              heading="Personal Info"
              details={personalDetails}
              isEditModeOn={isPersonalDetailsEditModeOn}
              handleIsEditModeOn={handleIsPersonalDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
            />
            <GeneralDetailsTab
              heading="Address"
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
                  heading="Emergency Contact"
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
            heading="Employment Info"
            details={jobDetails}
            employee={employee}
            fetchEmployeeAgain={fetchEmployeeAgain}
            isEditModeOn={isJobAddressDetailsEditModeOn}
            handleIsEditModeOn={handleIsJobAddressDetailsEditModeOn}
          />
        )}
        {selectedTab === 'deductions' && (
          <DeductionsTab
            heading="Health Insurance"
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
              heading="KYC"
              details={kycDetails}
              isEditModeOn={isKycDetailsEditModeOn}
              employee={employee}
              fetchEmployeeAgain={fetchEmployeeAgain}
              handleIsEditModeOn={handleIsKycDetailsEditModeOn}
            />
            <KycTabContent
              heading="Bank Details"
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
