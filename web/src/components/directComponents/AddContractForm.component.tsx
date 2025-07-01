import React, { useState } from 'react';

import {
  TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import {
  FormContainer,
  StepsContainer,
  StepWrapper,
  StepLabel,
  Line,
  AddFormMainContainer,
  FormInputsContainer,
  InputLabelContainer,
  LogoContainer,
  LogoLabel,
  LogoUploadContainer,
  UploadText,
  BrowseText,
  FileName,
  RemoveButton,
} from '../../styles/ClientStyles.style';
import { UploadSVG, CheckIcon, LineIcon } from '../../svgs/ClientSvgs.svs';
import { Button } from '../../styles/CommonStyles.style';
import { ClientResponse } from '../../entities/ClientEntity';
import { useTranslation } from 'react-i18next';
import { TextArea } from '../../styles/ProjectStyles.style';
import { SearchSVG } from '../../svgs/NavBarSvgs.svg';
import { ButtonContainer } from '../../styles/SettingsStyles.style';
import { postContracts } from '../../service/axiosInstance';

type AddContractFormProps = {
  handleClose: () => void;
  handleSuccessMessage: (msg: string) => void;
  onSubmit: (data: ContractFormData, file: File | null) => void;
  client: ClientResponse;
};

export type ContractFormData = {
  contractTitle: string;
  startDate: string;
  endDate: string;
  type: string;
  billingType?: string;
  billingCurrency?: string;
  contractValue?: string;
  description?: string;
  terms?: string;
  projectManagers: string;
  projectName: string;
};

const AddContractForm: React.FC<AddContractFormProps> = ({
  handleClose,
  handleSuccessMessage,
}) => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState<ContractFormData>({
    contractTitle: '',
    startDate: '',
    endDate: '',
    type: '',
    billingType: '',
    billingCurrency: '',
    contractValue: '',
    description: '',
    terms: '',
    projectManagers: '',
    projectName: '',
  });
  const [file, setFile] = useState<File | null>(null);

  const handleNextStep = () => setStep((prev) => prev + 1);
  const handlePreviousStep = () => setStep((prev) => prev - 1);

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  };

  const handleSubmitData = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const dataToSend = new FormData();

      Object.entries(formData).forEach(([key, value]) => {
        if (typeof value === 'object' && value !== null) {
          Object.entries(value).forEach(([subKey, subValue]) => {
            if (subValue !== null && subValue !== '') {
              dataToSend.append(`${key}.${subKey}`, String(subValue));
            }
          });
        } else if (value !== null && value !== '') {
          dataToSend.append(key, String(value));
        }
      });

      if (file) {
        dataToSend.append('file', file);
      }
      await postContracts(dataToSend);

      handleSuccessMessage('Contract saved successfully');
    } catch (error) {
      throw new Error('Error submitting contract:' + error);
    }
  };      
  const { t } = useTranslation();

  return (
    <FormContainer>
      <>
        <StepsContainer>
          {['General Information', 'Project & project Manager'].map(
            (label, index, arr) => {
              const isActive = step === index + 1;
              const isCompleted = step > index + 1;
              const isLast = index === arr.length - 1;
              return (
                <React.Fragment key={index}>
                  <StepWrapper>
                    <StepLabel isActive={isActive} isCompleted={isCompleted}>
                      <div className="circle">
                        {isCompleted ? <CheckIcon /> : index + 1}
                      </div>
                      <div className="labelHead">{label}</div>
                    </StepLabel>
                  </StepWrapper>
                  {!isLast && (
                    <Line>
                      <LineIcon />
                    </Line>
                  )}
                </React.Fragment>
              );
            }
          )}
        </StepsContainer>

        {step === 1 && (
          <AddFormMainContainer className="formBackground">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleNextStep();
              }}
            >
              <FormInputsContainer>
                <div>
                  <InputLabelContainer>
                    <label>
                      Contract Name
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      name="contractTitle"
                      type="text"
                      placeholder="Enter Contract Name"
                      value={formData.contractTitle}
                      onChange={handleChange}
                      required
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>
                      Start Date
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="date"
                      name="startDate"
                      value={formData.startDate}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Billing Type</label>
                    <select
                      name="billingType"
                      value={formData.billingType}
                      onChange={handleChange}
                      className="selectoption largeSelectOption"
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Billing Type</option>
                      <option value="Hourly">Hourly</option>
                      <option value="Fixed">Fixed</option>
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Budget</label>
                    <TextInput
                      type="text"
                      name="contractValue"
                      placeholder="Enter Budget"
                      value={formData.contractValue}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>

                <div>
                  <InputLabelContainer>
                    <label>
                      Contract Type
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <select
                      name="type"
                      value={formData.type}
                      onChange={handleChange}
                      className="selectoption largeSelectOption"
                      required
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Contract</option>
                      <option value="Abc">Abc</option>
                      <option value="Ctr code">Ctr code</option>
                      <option value="Contract 3">Contract 3</option>
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>End Date</label>
                    <TextInput
                      type="date"
                      name="endDate"
                      value={formData.endDate}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Billing Currency</label>
                    <select
                      name="billingCurrency"
                      value={formData.billingCurrency}
                      onChange={handleChange}
                      className="selectoption largeSelectOption"
                      style={{ width: '400px' }}
                    >
                      <option value="">Select Currency</option>
                      <option value="INR">INR</option>
                      <option value="USD">USD</option>
                      <option value="EUR">EUR</option>
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Description</label>
                    <TextArea
                      name="description"
                      placeholder="Enter Contract Description"
                      value={formData.description}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputsContainer>

              <LogoContainer>
                <LogoLabel>Attachments</LogoLabel>
                <LogoUploadContainer
                  onClick={() =>
                    document.getElementById('contractFile')?.click()
                  }
                >
                  <input
                    id="contractFile"
                    type="file"
                    accept=".pdf,.doc,.docx"
                    style={{ display: 'none' }}
                    onChange={handleFileChange}
                  />
                  <UploadSVG />
                  <UploadText>
                    Drag and drop or <BrowseText>Browse</BrowseText>
                  </UploadText>
                </LogoUploadContainer>
                {file && (
                  <LogoLabel>
                    <FileName>{file.name}</FileName>
                    <RemoveButton onClick={() => setFile(null)}>x</RemoveButton>
                  </LogoLabel>
                )}
              </LogoContainer>

              <div className="formButtons">
                <Button onClick={handleClose} type="button">
                  Cancel
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={() => {
                    handleNextStep();
                  }}
                >
                  Save & Continue
                </Button>
              </div>
            </form>
          </AddFormMainContainer>
        )}

        {step === 2 && (
          <form onSubmit={handleSubmitData}>
            <FormInputsContainer>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('Project Name')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <select
                    name="projectName"
                    value={formData.projectName}
                    onChange={handleChange}
                    className="selectoption largeSelectOption"
                    required
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select Project')}</option>

                    <option value="Project A">Project A</option>
                    <option value="Proj Project B">Project B</option>
                  </select>
                </InputLabelContainer>
              </div>
              <div>
                <InputLabelContainer>
                  <label>{t('Project Managers')}</label>
                  <select
                    name="projectManagers"
                    value={formData.projectManagers}
                    onChange={handleChange}
                    className="selectoption largeSelectOption"
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select Managers')}</option>
                    <option value="pm1">SD</option>
                    <option value="pm2">Kiran</option>
                    <option value="pm3">John cena</option>
                  </select>

                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      marginTop: '10px',
                    }}
                  ></div>
                </InputLabelContainer>
              </div>
            </FormInputsContainer>

            <InputLabelContainer
              style={{
                gridColumn: '1 / -1',
                width: '900px',
                marginLeft: '190px',
              }}
            >
              <label>
                {t('Resources Allocation')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <div
                style={{
                  display: 'flex',
                  gap: '10px',
                  alignItems: 'center',
                }}
              >
                <TextInput
                  type="text"
                  placeholder={t('Search People')}

                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      searchTerm: e.target.value,
                    }))
                  }
                  style={{ flex: 1 }}
                />
                <SearchSVG />
              </div>
            </InputLabelContainer>
            <InputLabelContainer
              style={{
                marginLeft: '190px',
              }}
            >
              <label>{t('All Resources')}</label>
              <div
                style={{ display: 'flex', flexWrap: 'wrap', gap: '5px' }}
              ></div>
            </InputLabelContainer>
            <InputLabelContainer
              style={{
                marginLeft: '190px',
              }}
            >
              
              <div
                onClick={() => alert('Open Manage Resource Allocation screen')}
              >
                {t('Manage Resource Allocation')}
                        </div>
            </InputLabelContainer>
            <ButtonContainer>
              <Button onClick={handlePreviousStep} className="leftAlign">
                <span className="separator">{`<`}</span> &nbsp;
                {t('Previous')}
              </Button>
              <Button className="submit" type="submit">
                {t('Submit')}
              </Button>
            </ButtonContainer>
          </form>
          
        )}
        {/* {showErrorMessage && (
          <ToastMessage
            messageType="error"
            messageBody={errorMessage}
            messageHeading="UPLOAD_UNSUCCESSFUL"
            handleClose={handleShowErrorMessage}
          />
        )}
        {isResponseLoading && <SpinAnimation />} */}
      </>
    </FormContainer>
  );
};

export default AddContractForm;
