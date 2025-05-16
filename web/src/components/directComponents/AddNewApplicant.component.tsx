import { useNavigate } from 'react-router-dom';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../../styles/ExpenseManagementStyles.style';
import { useUser } from '../../context/UserContext';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import { hasPermission } from '../../utils/permissionCheck';
import { RECRUITMENT_MODULE } from '../../constants/PermissionConstants';
import { TextInput } from 'web-kit-components';
import { BulkPayslipContainer } from '../../styles/BulkPayslipStyles.style';
import {
  FileUploadField,
  FormFileSelected,
  InputLabelContainer,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { noOfYearsExperience } from '../../utils/selectOptions';
import { useEffect, useState } from 'react';
import {
  FileTextIcon,
  UploadReceiptIcon,
} from '../../svgs/ExpenseListSvgs.svg';
import { FormFileCloseIcon } from '../../svgs/DocumentTabSvgs.svg';
import {
  getOrganizationValuesByKey,
  postApplicant,
  referApplicant,
} from '../../service/axiosInstance';
import { toast } from 'sonner';
import { Button } from '../../styles/CommonStyles.style';
import { t } from 'i18next';
import { OrgDefaults } from '../../entities/OrgDefaultsEntity';
import SpinAnimation from '../loaders/SprinAnimation.loader';

type AddNewApplicantProps = {
  isReferScreen: boolean;
};
const AddNewApplicant = (props: AddNewApplicantProps) => {
  const navigate = useNavigate();
  const { user } = useUser();
  const goToPreviousPage = () => {
    navigate(-1);
  };

  const [seledtedResume, setSelectedResume] = useState<File>();
  const [isLoading, setIsLoading] = useState(false);

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();

    event.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();
    if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
      const droppedFiles = Array.from(event.dataTransfer.files);
      setSelectedResume(droppedFiles[0]);
    }
  };
  const removeFile = () => {
    setSelectedResume(undefined);
  };

  const [newApplicant, setNewApplicant] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    positionAppliedFor: '',
    experience: '',
  });

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    setNewApplicant((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSaveApplicant = async (e: { preventDefault: () => void }) => {
    e.preventDefault();

    if (
      newApplicant.email == '' ||
      newApplicant.firstName == '' ||
      newApplicant.lastName == '' ||
      newApplicant.phoneNumber == '' ||
      newApplicant.positionAppliedFor == '' ||
      seledtedResume == undefined ||
      seledtedResume == null
    ) {
      toast.error('Please fill mandatory (*) fields');
      return;
    }

    if (
      newApplicant.phoneNumber !== '' &&
      !newApplicant.phoneNumber.match(/^[0-9]{10}$/)
    ) {
      toast.error('Please enter a valid phone number');
      return;
    }
    if (
      newApplicant.email !== '' &&
      !newApplicant.email.match(/^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,63}$/)
    ) {
      toast.error('Please enter a valid email address');
      return;
    }
    try {
      setIsLoading(true);
      const newApplicantData = new FormData();
      newApplicantData.append('firstName', newApplicant.firstName);
      newApplicantData.append('lastName', newApplicant.lastName);
      newApplicantData.append('phoneNumber', newApplicant.phoneNumber);
      newApplicantData.append('email', newApplicant.email);
      newApplicantData.append(
        'positionAppliedFor',
        newApplicant.positionAppliedFor
      );
      newApplicantData.append('experience', newApplicant.experience);
      newApplicantData.append('resume', seledtedResume!);
      let response;
      if (props.isReferScreen) {
        response = await referApplicant(newApplicantData);
      } else {
        response = await postApplicant(newApplicantData);
      }
      if (response.status === 200) {
        toast.success('Applicant saved successfully');
        navigate(-1);
      } else {
        toast.error('Failed to save applicant');
      }
    } catch {
      toast.error('An error occurred while saving the applicant');
    } finally {
      setIsLoading(false);
    }
  };
  const [apiLoading, setApiLoading] = useState(false);
  const [jobTitles, setJobTitles] = useState<OrgDefaults>();
  useEffect(() => {
    const fetchJobTitles = async () => {
      setApiLoading(true);
      try {
        const response = await getOrganizationValuesByKey('jobTitles');
        setJobTitles(response.data);
      } catch {
        toast.error(t('ERROR_WHILE_FETCHING_JOB_TITLES'));
      } finally {
        setApiLoading(false);
      }
    };
    fetchJobTitles();
  }, []);

  return (
    <>
      {apiLoading ? (
        <SpinAnimation />
      ) : (
        <ExpenseManagementMainContainer>
          <ExpenseHeadingSection>
            <span className="heading">
              <span onClick={goToPreviousPage}>
                <ArrowDownSVG />
              </span>
              {props.isReferScreen ? 'Refer An Employee' : 'Add New Applicant'}
            </span>
          </ExpenseHeadingSection>
          <BulkPayslipContainer className="addNewApplicant">
            <form>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('First_Name')}{' '}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="text"
                    name="firstName"
                    id="firstName"
                    onChange={handleChange}
                    value={newApplicant.firstName}
                    required
                    onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                      } else {
                        const isAlphabet = /^[a-zA-Z\s]+$/.test(e.key);
                        if (!isAlphabet) {
                          e.preventDefault();
                        }
                      }
                    }}
                    placeholder={'Ex: John'}
                    autoComplete="off"
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Last_Name')}{' '}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="text"
                    name="lastName"
                    id="lastName"
                    onChange={handleChange}
                    required
                    autoComplete="off"
                    onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                      } else {
                        const isAlphabet = /^[a-zA-Z]+$/.test(e.key);
                        if (!isAlphabet) {
                          e.preventDefault();
                        }
                      }
                    }}
                    placeholder={'Ex: Doe'}
                  />
                </InputLabelContainer>
              </div>

              <div>
                <InputLabelContainer>
                  <label>
                    {t('Phone_Number')}{' '}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="text"
                    name="phoneNumber"
                    id="phoneNumber"
                    onChange={handleChange}
                    required
                    autoComplete="off"
                    maxLength={10}
                    onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                      } else {
                        const isNumeric = /^\d+$/.test(e.key);
                        if (!isNumeric) {
                          e.preventDefault();
                        }
                      }
                    }}
                    placeholder={'Enter Phone Number'}
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('EMAIL')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <TextInput
                    type="email"
                    name="email"
                    id="email"
                    onChange={handleChange}
                    pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,63}$"
                    autoComplete="off"
                    required
                    placeholder={'Enter Email'}
                  />
                </InputLabelContainer>
              </div>

              <div>
                <InputLabelContainer className="selectOption">
                  <label>
                    {t('POSITION_APPLIED_FOR')}{' '}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <select
                    className="selectoption"
                    name="positionAppliedFor"
                    id="positionAppliedFor"
                    onChange={handleChange}
                    required
                    onKeyPress={(event) => {
                      if (event.key === 'Enter') {
                        event.preventDefault();
                      }
                    }}
                  >
                    <option value="">Select Position</option>
                    {jobTitles?.values.map((position, index) => (
                      <option key={index} value={position.value}>
                        {position.value}
                      </option>
                    ))}
                  </select>
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>{t('EXPERIENCE')}</label>
                  <select
                    className="selectoption"
                    name="experience"
                    id="experience"
                    onChange={handleChange}
                    onKeyPress={(event) => {
                      if (event.key === 'Enter') {
                        event.preventDefault();
                      }
                    }}
                  >
                    <option value="">Select Experience</option>
                    {noOfYearsExperience.map((number) => (
                      <option key={number} value={number}>
                        {number}
                      </option>
                    ))}
                  </select>
                </InputLabelContainer>
              </div>

              <div>
                <InputLabelContainer style={{ marginBottom: 0 }}>
                  <label>
                    {t('UPLOAD_RESUME')}{' '}
                    <ValidationText className="star">*</ValidationText>
                  </label>{' '}
                </InputLabelContainer>
                <FileUploadField
                  className="expenseReceiptUpload"
                  onDragOver={handleDragOver}
                  onDrop={handleDrop}
                >
                  <label htmlFor="fileInput">
                    <div>
                      <UploadReceiptIcon />
                      <div>
                        {t('DRAG_AND_DROP_OR')}
                        <span> {t('BROWSE')}</span>
                      </div>
                    </div>
                  </label>
                  <input
                    type="file"
                    accept="application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/msword"
                    id="fileInput"
                    style={{ display: 'none' }}
                    required
                    onChange={(event) => {
                      setSelectedResume(event.target.files![0]);
                    }}
                    onKeyPress={(event) => {
                      if (event.key === 'Enter') {
                        event.preventDefault();
                      }
                    }}
                    maxLength={3}
                  />
                  <span className="textInInput file-info-text">
                    {t('FILE_FORMATS_REMUME')}
                  </span>
                </FileUploadField>
                {seledtedResume && (
                  <div className="selectedFilesMain">
                    <FormFileSelected>
                      <FileTextIcon />
                      <span>{seledtedResume.name}</span>
                      <span className="redPointer" onClick={() => removeFile()}>
                        <FormFileCloseIcon />
                      </span>
                    </FormFileSelected>
                  </div>
                )}
              </div>
              {user &&
                (hasPermission(user, RECRUITMENT_MODULE.CREATE_APPLICANT) ||
                  hasPermission(
                    user,
                    RECRUITMENT_MODULE.ACCESS_REFFERRAlS
                  )) && (
                  <Button
                    className={`submit ${isLoading ? 'loading' : ''}`}
                    width="216px"
                    onClick={handleSaveApplicant}
                    type="submit"
                    disabled={isLoading}
                  >
                    {isLoading ? '' : 'Submit'}
                  </Button>
                )}
            </form>
          </BulkPayslipContainer>
        </ExpenseManagementMainContainer>
      )}
    </>
  );
};

export default AddNewApplicant;
