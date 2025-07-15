import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ClientDetails } from '../../entities/ClientEntity';
import { postClient, putClient } from '../../service/axiosInstance';
import {
    AddClientButtons,
    AddFormMainContainer,
    AddressBlock,
    BasicOrganizationDetailsContainer,
    BrowseText,
    CheckBoxOuterContainer,
    EditIconWrapper,
    FileName,
    FormContainer,
    FormInputs,
    FormInputsContainer,
    HeadingContainer,
    HeadingDiv,
    InfoBlock,
    InfoRow,
    InfoText,
    InputLabelContainer,
    InputLabelLogoContainer,
    LabelText,
    Line,
    LogoContainer,
    LogoLabel,
    LogoNameWrapper,
    LogoPreview,
    LogoUploadContainer,
    RemoveButton,
    SectionHeader,
    StepLabel,
    StepsContainer,
    StepWrapper,
    StyledCheckbox,
    SubHeadingDiv,
    UploadText,
} from '../../styles/ClientStyles.style';
import { Button } from '../../styles/CommonStyles.style';
import {
    TextInput,
    ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import {
    CallSVG,
    CheckIcon,
    DotSVG,
    EditSVG,
    EmailSVG,
    LineIcon,
    UploadSVG,
} from '../../svgs/ClientManagmentSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import {
    clientOptions,
    ClientType,
    Industry,
    TaxCategory,
} from '../reusableComponents/ClientEnums.component';
import ToastMessage from '../reusableComponents/ToastMessage.component';

type AddClientFormProps = {
  handleClose: () => void;
  handleSuccessMessage: () => void;
  isEditMode?: boolean;
  initialData?: ClientDetails;
  updateClientList: () => Promise<void>;
};

const AddClientForm = (props: AddClientFormProps) => {
  const [formData, setFormData] = useState<ClientDetails>({
    clientId: '',
    clientName: '',
    clientType: '' as ClientType,
    email: '',
    industry: '' as Industry,
    contact: '',
    description: '',
    logo: '',

    taxDetails: {
      taxCategory: '' as TaxCategory,
      taxNumber: '',
    },
    primaryAddress: {
      street: '',
      country: '',
      state: '',
      city: '',
      postalCode: '',
    },
    billingAddress: {
      street: '',
      country: '',
      state: '',
      city: '',
      postalCode: '',
    },
    usePrimaryAddress: false,
  });

  useEffect(() => {
    const defaultData: ClientDetails = {
      clientId: '',
      clientName: '',
      clientType: '' as ClientType,
      email: '',
      industry: '' as Industry,
      contact: '',
      description: '',
      logo: '',
      taxDetails: {
        taxCategory: '' as TaxCategory,
        taxNumber: '',
      },
      primaryAddress: {
        street: '',
        country: '',
        state: '',
        city: '',
        postalCode: '',
      },
      billingAddress: {
        street: '',
        country: '',
        state: '',
        city: '',
        postalCode: '',
      },
      usePrimaryAddress: false,
    };

    if (props.isEditMode && props.initialData) {
      const mergedData: ClientDetails = {
        ...defaultData,
        ...props.initialData,
        taxDetails: {
          ...defaultData.taxDetails,
          ...(props.initialData.taxDetails ?? {}),
        },
        primaryAddress: {
          ...defaultData.primaryAddress,
          ...(props.initialData.primaryAddress ?? {}),
        },
        billingAddress: {
          ...defaultData.billingAddress,
          ...(props.initialData.billingAddress ?? {}),
        },
      };

      setFormData(mergedData);

      if (typeof props.initialData.logo === 'string') {
        setLogoPreviewUrl(props.initialData.logo);
      }
    } else {
      setFormData(defaultData);
      setLogoPreviewUrl(null);
      setFile(null);
    }
  }, [props.initialData, props.isEditMode]);

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const isChecked = event.target.checked;
    setFormData((prevState) => ({
      ...prevState,
      usePrimaryAddress: isChecked,
      billingAddress: isChecked
        ? { ...prevState.primaryAddress }
        : prevState.billingAddress,
    }));
  };
  const [step, setStep] = useState(1);
  const handleNextStep = () => {
    let isValid = false;
    if (step === 1) {
      isValid = validateStep1();
    } else if (step === 2) {
      isValid = validateStep2();
    } else if (step === 3) {
      isValid = validateStep3();
    }
    if (isValid) {
      setStep((prevStep) => prevStep + 1);
    }
  };

  const handlePreviousStep = () => {
    setStep((prevStep) => prevStep - 1);
  };
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);

  const handleShowErrorMessage = () => {
    setShowErrorMessage(!showErrorMessage);
  };

  const [errorMessage, setErrorMessage] = useState<string>('');
  const [file, setFile] = useState<File | null>(null);
  const [logoPreviewUrl, setLogoPreviewUrl] = useState<string | null>(null);
  useEffect(() => {
  return () => {
    if (logoPreviewUrl) {
      URL.revokeObjectURL(logoPreviewUrl);
    }
  };
}, [logoPreviewUrl]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];
    if (selectedFile) {
      // Validate file type and size
      const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
      const maxSizeInBytes = 5 * 1024 * 1024; // 5 MB
      if (!validTypes.includes(selectedFile.type)) {
        alert('Invalid file type. Please upload an image file (JPEG, PNG, GIF).');
        return;
      }
      if (selectedFile.size > maxSizeInBytes) {
        alert('File is too large. Please upload a file smaller than 5 MB.');
        return;
      }
      
      // Revoke previous blob URL if exists
      if (logoPreviewUrl) {
        URL.revokeObjectURL(logoPreviewUrl);
      }
      
      // Set new file and generate blob URL
      const objectUrl = URL.createObjectURL(selectedFile);
    setLogoPreviewUrl(objectUrl);
    setFile(selectedFile);
    setFormData((prev) => ({
      ...prev,
      clientLogo: selectedFile,
    }));
    }
  };

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    const keys = name.split('.');

    setFormData((prevState) => {
      if (keys.length === 2) {
        const [parentKey, childKey] = keys;
        if (
          parentKey === 'primaryAddress' ||
          parentKey === 'billingAddress' ||
          parentKey === 'taxDetails'
        ) {
          return {
            ...prevState,
            [parentKey]: {
              ...(prevState[parentKey] ?? {}),
              [childKey]: value ?? '',
            },
          };
        }
      }

      return {
        ...prevState,
        [name]: value ?? '',
      };
    });
  };

  const validateStep1 = () => {
    let isValid = true;

    if (!formData.clientName.trim()) {
      isValid = false;
    }
    if (!formData.clientType) {
      isValid = false;
    }
    if (!formData.email.trim()) {
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      isValid = false;
    }
    return isValid;
  };
  const validateStep2 = () => {
    return true;
  };

  const validateStep3 = () => {
    return true;
  };

  const handleSubmitData = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsResponseLoading(true);

    const { handleSuccessMessage, handleClose, isEditMode, updateClientList } =
      props;
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
        dataToSend.append('logo', file);
      }

      if (isEditMode && formData.clientId) {
        await putClient(formData.clientId, dataToSend);
      } else {
        await postClient(dataToSend);
      }

      await updateClientList();
      handleSuccessMessage();
      handleClose;
    } catch (error) {
      setErrorMessage('Failed to submit data.');
      setShowErrorMessage(true);
      handleClose;
      throw new Error('Error fetching data:' + error);
    } finally {
      setIsResponseLoading(false);
    }
  };

  const { t } = useTranslation();
  return (
    <FormContainer>
      <>
        <StepsContainer>
          {[
            'Basic Organisation Details',
            'Tax Details',
            'Address',
            'Summary',
          ].map((label, index, arr) => {
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
          })}
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
                      {t('Client Name')}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="text"
                      name="clientName"
                      placeholder={t('Enter Client Name')}
                      className="largeInput"
                      value={formData?.clientName}
                      onChange={handleChange}
                      required
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>
                      {t('Email')}{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="email"
                      name="email"
                      placeholder={t('Enter Email')}
                      className="largeInput"
                      value={formData?.email}
                      onChange={handleChange}
                      required
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>{t('Contact')}</label>
                    <TextInput
                      type="text"
                      name="contact"
                      placeholder={t('Enter Contact')}
                      className="largeInput"
                      value={formData?.contact}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
                <div>
                  <InputLabelContainer>
                    <label>
                      {t('Client Type')}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <select
                      className="selectoption largeSelectOption"
                      name="clientType"
                      value={formData?.clientType}
                      onChange={handleChange}
                      required
                      style={{ width: '400px' }}
                    >
                      <option value="">{t('Select type')}</option>
                      {clientOptions.clientType.map((type) => (
                        <option key={type} value={type}>
                          {t(type)}
                        </option>
                      ))}
                    </select>
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>{t('Industry')}</label>
                    <select
                      className="selectoption largeSelectOption"
                      name="industry"
                      value={formData?.industry}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    >
                      <option value="">{t('Select Industry')}</option>
                      {clientOptions.industry.map((industry) => (
                        <option key={industry} value={industry}>
                          {t(industry)}
                        </option>
                      ))}
                    </select>
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>{t('Description')}</label>
                    <TextInput
                      type="text"
                      name="description"
                      placeholder={t('Enter Client Description')}
                      className="largeInput"
                      value={formData?.description}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputsContainer>
              <LogoContainer>
                <LogoLabel>{t('Logo')}</LogoLabel>
                <LogoUploadContainer
                  onClick={() => document.getElementById('fileInput')?.click()}
                >
                  <input
                    id="fileInput"
                    type="file"
                    accept=".png, .jpg, .jpeg"
                    style={{ display: 'none' }}
                    onChange={handleFileChange}
                    name="logo"
                  />
                  <UploadSVG />
                  <UploadText>
                    {t('Upload your Logo')}{' '}
                    <BrowseText>{t('Browse')}</BrowseText>
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
                <Button onClick={props.handleClose} type="button">
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="submit"
                  onClick={() => {
                    handleNextStep();
                  }}
                >
                  {t('Save & Continue')}
                </Button>
              </div>
            </form>
          </AddFormMainContainer>
        )}
        {step === 2 && (
          <ExpenseAddFormMainContainer className="formBackground">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleNextStep();
              }}
            >
              <FormInputs>
                <div>
                  <InputLabelContainer>
                    <label>{t('Tax Category')}</label>
                    <select
                      className="selectoption largeSelectOption"
                      name="taxDetails.taxCategory"
                      value={formData.taxDetails?.taxCategory ?? ''}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    >
                      <option value="">{t('Select category')}</option>
                      {clientOptions.taxCategory.map((category) => (
                        <option key={category} value={category}>
                          {t(category)}
                        </option>
                      ))}
                    </select>
                  </InputLabelContainer>
                </div>
                <div>
                  <InputLabelContainer>
                    <label>{t('Tax Number')}</label>
                    <TextInput
                      type="text"
                      name="taxDetails.taxNumber"
                      placeholder={t('Enter Tax Number')}
                      className="largeInput"
                      value={formData.taxDetails?.taxNumber ?? ''}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputs>
              <AddClientButtons>
                <div onClick={handlePreviousStep} className='leftAlign'>
                  <span className="separator"> {`<`} </span> &nbsp;
                  {t('Previous')}
                </div>
                <div className='centerAlign'>
                  <Button onClick={props.handleClose} type='button'>
                    {t('Cancel')}
                  </Button>
                  <Button
                    className="submit"
                    type="button"
                    onClick={handleNextStep}
                  >
                    {t('Save & Continue')}
                  </Button>
                </div>
              </AddClientButtons>
            </form>
          </ExpenseAddFormMainContainer>
        )}
        {step === 3 && (
          <ExpenseAddFormMainContainer className="formBackground">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleNextStep();
              }}
            >
              <HeadingDiv>{t('Primary Address')}</HeadingDiv>
              <FormInputs>
                <div style={{ fontFamily: 'Nunito' }}>
                  <InputLabelContainer>
                    <label>{t('Street')}</label>
                    <TextInput
                      type="text"
                      name="primaryAddress.street"
                      placeholder={t('Street')}
                      className="largeInput"
                      value={formData.primaryAddress?.street ?? ''}
                      onChange={handleChange}
                      style={{ width: '420px' }}
                    />
                  </InputLabelContainer>
                  <div style={{ display: 'flex', gap: '10px' }}>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="primaryAddress.city"
                        placeholder={t('City')}
                        className="largeInput"
                        value={formData.primaryAddress?.city ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="primaryAddress.state"
                        placeholder={t('State')}
                        className="largeInput"
                        value={formData.primaryAddress?.state ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="primaryAddress.postalCode"
                        placeholder={t('Zip/Postal Code')}
                        className="largeInput"
                        value={formData.primaryAddress?.postalCode ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                  </div>
                </div>
                <div>
                  <InputLabelContainer>
                    <label>{t('Country')}</label>
                    <TextInput
                      type="text"
                      name="primaryAddress.country"
                      placeholder={t('United States of America')}
                      className="largeInput"
                      value={formData.primaryAddress?.country}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputs>
              <HeadingDiv>{t('Billing Address')}</HeadingDiv>
              <CheckBoxOuterContainer>
                <InputLabelContainer style={{ marginTop: '15px' }}>
                  <StyledCheckbox
                    type="checkbox"
                    name="usePrimaryAddress"
                    checked={formData.usePrimaryAddress}
                    onChange={handleCheckboxChange}
                  />
                </InputLabelContainer>

                <LabelText>
                  {t('Use Primary address as billing address')}
                </LabelText>
              </CheckBoxOuterContainer>
              <FormInputs>
                <div>
                  <InputLabelContainer style={{ marginBottom: '1px' }}>
                    <label>{t('Street')}</label>
                    <TextInput
                      type="text"
                      name="billingAddress.street"
                      placeholder={t('Street')}
                      className="largeInput"
                      value={formData?.billingAddress?.street ?? ''}
                      onChange={handleChange}
                      style={{ width: '420px' }}
                    />
                  </InputLabelContainer>
                  <div style={{ display: 'flex', gap: '10px' }}>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="billingAddress.city"
                        placeholder={t('City')}
                        className="largeInput"
                        value={formData?.billingAddress?.city ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="billingAddress.state"
                        placeholder={t('State')}
                        className="largeInput"
                        value={formData?.billingAddress?.state ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                    <InputLabelContainer style={{ marginTop: '12px' }}>
                      <TextInput
                        type="text"
                        name="billingAddress.postalCode"
                        placeholder={t('Zip/Postal Code')}
                        className="largeInput"
                        value={formData?.billingAddress?.postalCode ?? ''}
                        onChange={handleChange}
                        style={{ width: '133px' }}
                      />
                    </InputLabelContainer>
                  </div>
                </div>
                <div>
                  <InputLabelContainer>
                    <label>{t('Country')}</label>
                    <TextInput
                      type="text"
                      name="billingAddress.country"
                      placeholder={t('United States of America')}
                      className="largeInput"
                      value={formData?.billingAddress?.country ?? ''}
                      onChange={handleChange}
                      style={{ width: '400px' }}
                    />
                  </InputLabelContainer>
                </div>
              </FormInputs>
              <AddClientButtons>
                <div onClick={handlePreviousStep} className="leftAlign">
                  <span className="separator"> {`<`} </span> &nbsp;
                  {t('Previous')}
                </div>
                <div className='centerAlign'>
                  <Button onClick={props.handleClose} type='button'>
                    {t('Cancel')}
                  </Button>
                  <Button
                    className="submit"
                    type="button"
                    onClick={handleNextStep}
                  >
                    {t('Save & Continue')}
                  </Button>
                </div>
              </AddClientButtons>
            </form>
          </ExpenseAddFormMainContainer>
        )}
        {step === 4 && (
          <ExpenseAddFormMainContainer
            className="formBackground"
            onSubmit={handleSubmitData}
          >
            <HeadingContainer>
              <SectionHeader>
                {t('Basic Organisation Details')}
                <EditIconWrapper>
                  <EditSVG onClick={() => setStep(2)} />
                </EditIconWrapper>
              </SectionHeader>
            </HeadingContainer>
            <InputLabelLogoContainer>
              <BasicOrganizationDetailsContainer>
                <div>
                  <InfoRow>
                    <SubHeadingDiv>Client Name</SubHeadingDiv>
                    <InfoText>{formData.clientName}</InfoText>
                  </InfoRow>
                  <InfoRow>
                    <SubHeadingDiv>Client Type</SubHeadingDiv>
                    <InfoText>{formData.clientType}</InfoText>
                  </InfoRow>
                  <InfoRow>
                    <SubHeadingDiv>Description</SubHeadingDiv>
                    <InfoText>{formData.description}</InfoText>
                  </InfoRow>
                  <InfoRow>
                    <SubHeadingDiv>Internal Type</SubHeadingDiv>
                    <InfoText>{formData.industry}</InfoText>
                    <DotSVG />
                    <EmailSVG />
                    <InfoText>{formData.email}</InfoText>
                    <DotSVG />
                    <CallSVG />
                    <InfoText>{formData.contact}</InfoText>
                  </InfoRow>
                </div>
                {logoPreviewUrl && (
                  <LogoNameWrapper>
                    <LogoPreview>
                      <img src={logoPreviewUrl} alt="Client Logo" />
                    </LogoPreview>
                  </LogoNameWrapper>
                )}
              </BasicOrganizationDetailsContainer>
            </InputLabelLogoContainer>

            <HeadingContainer>
              <SectionHeader>
                {t('Details')}
                <EditIconWrapper>
                  <EditSVG onClick={() => setStep(2)} />
                </EditIconWrapper>
              </SectionHeader>
            </HeadingContainer>
            <FormInputsContainer>
              <InfoBlock style={{ display: 'flex' }}>
                <SubHeadingDiv style={{ width: '100px' }}>
                  {t('Tax Category')}
                </SubHeadingDiv>
                <InfoText>{formData.taxDetails.taxCategory}</InfoText>
              </InfoBlock>
              <InfoBlock style={{ display: 'flex' }}>
                <SubHeadingDiv>
                  {t('GST Number')}
                </SubHeadingDiv>
                <InfoText>{formData.taxDetails.taxNumber}</InfoText>
              </InfoBlock>
            </FormInputsContainer>
            <HeadingContainer>
              <SectionHeader>
                {t('Address')}
                <EditIconWrapper>
                  <EditSVG onClick={() => setStep(2)} />
                </EditIconWrapper>
              </SectionHeader>
            </HeadingContainer>
            <FormInputsContainer>
              <div>
                <AddressBlock>
                  <SubHeadingDiv>{t('Street')}</SubHeadingDiv>
                  <InfoText>{formData.primaryAddress.street}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('city')}</SubHeadingDiv>
                  <InfoText>{formData.primaryAddress.city}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('State')}</SubHeadingDiv>
                  <InfoText>{formData.primaryAddress.state}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Country')}</SubHeadingDiv>
                  <InfoText>{formData.primaryAddress.country}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Zip/postal Code')}</SubHeadingDiv>
                  <InfoText>{formData.primaryAddress.postalCode}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Contact')}</SubHeadingDiv>
                  <InfoText>{formData.contact}</InfoText>
                </AddressBlock>
              </div>
              <div>
                <AddressBlock>
                  <SubHeadingDiv>{t('Street')}</SubHeadingDiv>
                  <InfoText>{formData.billingAddress.street}</InfoText>
                </AddressBlock>

                <AddressBlock>
                  <SubHeadingDiv>{t('city')}</SubHeadingDiv>
                  <InfoText>{formData.billingAddress.city}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('State')}</SubHeadingDiv>
                  <InfoText>{formData.billingAddress.state}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Country')}</SubHeadingDiv>
                  <InfoText>{formData.billingAddress.country}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Zip/postal Code')}</SubHeadingDiv>
                  <InfoText>{formData.billingAddress.postalCode}</InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>{t('Contact')}</SubHeadingDiv>
                  <InfoText>{formData.contact}</InfoText>
                </AddressBlock>
              </div>
            </FormInputsContainer>

            <div className="formButtons">
              <Button onClick={props.handleClose} type="button">
                {t('Skip')}
              </Button>
              <Button className="submit" type="submit">
                {t('Save & Continue')}
              </Button>
            </div>
          </ExpenseAddFormMainContainer>
        )}
        {showErrorMessage && (
          <ToastMessage
            messageType="error"
            messageBody={errorMessage}
            messageHeading="UPLOAD_UNSUCCESSFUL"
            handleClose={handleShowErrorMessage}
          />
        )}
        {isResponseLoading && <SpinAnimation />}
      </>
    </FormContainer>
  );
};

export default AddClientForm;
