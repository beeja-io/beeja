import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ClientDetails } from '../../entities/ClientEntity';
import {
  downloadClientLogo,
  postClient,
  putClient,
} from '../../service/axiosInstance';
import {
  AddClientButtons,
  AddFormMainContainer,
  AddressBlock,
  AddressMainContainer,
  BasicOrganizationDetailsContainer,
  BrowseText,
  ButtonGroup,
  CheckBoxOuterContainer,
  ColumnWrapper,
  DotWrap,
  EditIconWrapper,
  ExpenseAddFormMainContainer,
  FileName,
  FormContainer,
  FormInputs,
  FormInputsContainer,
  HeadingContainer,
  HeadingDiv,
  InfoBlock,
  InfoGroup,
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
  PrimaryContainer,
  RemoveButton,
  SectionHeader,
  StepLabel,
  StepsContainer,
  StepWrapper,
  StyledCheckbox,
  SubHeadingDiv,
  SummaryAddressContainer,
  SummaryAddressSubContainer,
  SummarySubContainer,
  TextInput,
  UploadText,
} from '../../styles/ClientStyles.style';
import { Button } from '../../styles/CommonStyles.style';
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import {
  CallSVG,
  CheckIcon,
  DotSVG,
  EditSVG,
  EmailSVG,
  IndustrySVG,
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
import CenterModal from '../reusableComponents/CenterModal.component';

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
      logoId: '',
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
  const [isDiscardModalOpen, setIsDiscardModalOpen] = useState(false);
  const [errors, setErrors] = useState<{
    taxCategory?: string;
    taxNumber?: string;
  }>({});
  const [logoPreviewUrl, setLogoPreviewUrl] = useState<string | null>(null);

  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (formData?.logoId && !logoPreviewUrl && !file) {
        try {
          const response = await downloadClientLogo(formData.logoId);

          if (response?.data && response.data.size > 0) {
            const reader = new FileReader();
            reader.onloadend = () => {
              const imageUrl = reader.result as string;
              setLogoPreviewUrl(imageUrl);
            };
            reader.readAsDataURL(response.data);
          }
        } catch (error) {
          throw Error('Error fetching logo:' + error);
        }
      }
    };

    fetchLogoImage();
    return () => {
      if (logoPreviewUrl) {
        URL.revokeObjectURL(logoPreviewUrl);
      }
    };
  }, [formData?.logoId]);

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
      const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
      const maxSizeInBytes = 5 * 1024 * 1024;
      if (!validTypes.includes(selectedFile.type)) {
        alert(
          'Invalid file type. Please upload an image file (JPEG, PNG, GIF).'
        );
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

    let newValue = value ?? '';

    if (name === 'contact') {
      newValue = newValue.replace(/\D/g, '').slice(0, 10);
    }

    if (name === 'taxDetails.taxNumber') {
      newValue = newValue.replace(/[^a-zA-Z0-9-]/g, '').slice(0, 15);
    }

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
              [childKey]: newValue,
            },
          };
        }
      }

      return {
        ...prevState,
        [name]: newValue,
      };
    });

    setErrors((prevErrors) => {
      const newErrors = { ...prevErrors };

      if (name === 'taxDetails.taxCategory' && newValue) {
        delete newErrors.taxCategory;
      }

      if (name === 'taxDetails.taxNumber') {
        if (
          newValue &&
          newValue.length >= 10 &&
          newValue.length <= 15 &&
          /^[A-Za-z0-9-]+$/.test(newValue)
        ) {
          delete newErrors.taxNumber;
        }
      }

      return newErrors;
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
    if (!formData.industry) {
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
    let isValid = true;

    const newErrors: { taxNumber?: string; taxCategory?: string } = {};

    if (!formData.taxDetails.taxCategory) {
      isValid = false;
      newErrors.taxCategory = 'Please select a Tax Category.';
    }

    const taxNumber: string = formData.taxDetails.taxNumber?.trim() || '';
    if (
      taxNumber === '' ||
      taxNumber.length < 10 ||
      taxNumber.length > 15 ||
      !/^[A-Za-z0-9-]+$/.test(taxNumber)
    ) {
      isValid = false;
      newErrors.taxNumber =
        'Tax Number must be 10–15 characters. Example: GSTIN12345.';
    }
    setErrors(newErrors);
    return isValid;
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
          <AddFormMainContainer
            onSubmit={(e) => {
              e.preventDefault();
              handleNextStep();
            }}
          >
            <FormInputsContainer>
              <ColumnWrapper>
                {formData?.clientId && (
                  <InputLabelContainer>
                    <label>{t('Client ID')}</label>
                    <TextInput type="text" value={formData.clientId} disabled />
                  </InputLabelContainer>
                )}
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
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Email')}
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
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>{t('Contact')}</label>
                  <TextInput
                    type="tel"
                    name="contact"
                    placeholder={t('Enter Contact')}
                    className="largeInput"
                    inputMode="numeric"
                    value={formData?.contact}
                    onChange={handleChange}
                  />
                </InputLabelContainer>
              </ColumnWrapper>
              <ColumnWrapper>
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
                  >
                    <option value="">{t('Select type')}</option>
                    {[...(clientOptions?.clientType || [])]
                      .sort((a, b) => a.localeCompare(b))
                      .map((type) => (
                        <option key={type} value={type}>
                          {t(type)}
                        </option>
                      ))}
                  </select>
                </InputLabelContainer>

                <InputLabelContainer>
                  <label>
                    {t('Industry')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <select
                    className="selectoption largeSelectOption"
                    name="industry"
                    value={formData?.industry}
                    onChange={handleChange}
                    required
                  >
                    <option value="">{t('Select Industry')}</option>
                    {[...(clientOptions?.industry || [])]
                      .sort((a, b) => a.localeCompare(b))
                      .map((industry) => (
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
                  />
                </InputLabelContainer>
                {formData?.clientId && (
                  <InputLabelContainer className="logoEditContainer">
                    <label>{t('Logo')}</label>
                    <LogoUploadContainer
                      onClick={() =>
                        document.getElementById('fileInput')?.click()
                      }
                      className="edit-height"
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
                        {t('Upload your Logo')}
                        <BrowseText>{t('Browse')}</BrowseText>
                      </UploadText>
                    </LogoUploadContainer>
                    {file && (
                      <LogoLabel>
                        <FileName>{file.name}</FileName>
                        <RemoveButton
                          onClick={() => {
                            setFile(null);
                            setLogoPreviewUrl(null);
                            setFormData((prev) => ({
                              ...prev,
                              clientLogo: null,
                            }));
                          }}
                        >
                          x
                        </RemoveButton>
                      </LogoLabel>
                    )}
                    {!file && formData?.logoId && (
                      <LogoLabel className="editLabel">
                        <FileName>LogoId: {formData.logoId}</FileName>
                        <RemoveButton
                          onClick={() => {
                            setFormData((prev) => ({ ...prev, logoId: '' }));
                            setLogoPreviewUrl(null);
                          }}
                        >
                          x
                        </RemoveButton>
                      </LogoLabel>
                    )}
                  </InputLabelContainer>
                )}
              </ColumnWrapper>

              {!formData?.clientId && (
                <LogoContainer>
                  <LogoLabel>{t('Logo')}</LogoLabel>
                  <LogoUploadContainer
                    className="add_height"
                    onClick={() =>
                      document.getElementById('fileInput')?.click()
                    }
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
                      {t('Upload your Logo')}
                      <BrowseText>{t('Browse')}</BrowseText>
                    </UploadText>
                  </LogoUploadContainer>
                  {file && (
                    <LogoLabel>
                      <FileName>{file.name}</FileName>
                      <RemoveButton
                        onClick={() => {
                          setFile(null);
                          setLogoPreviewUrl(null);
                          setFormData((prev) => ({
                            ...prev,
                            clientLogo: null,
                          }));
                        }}
                      >
                        x
                      </RemoveButton>
                    </LogoLabel>
                  )}
                </LogoContainer>
              )}
            </FormInputsContainer>
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
          </AddFormMainContainer>
        )}
        {step === 2 && (
          <AddressMainContainer
            className="formBackground"
            onSubmit={(e) => {
              e.preventDefault();
              handleNextStep();
            }}
          >
            <FormInputs className="tax-container">
              <InputLabelContainer>
                <label>
                  {t('Tax Category')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <select
                  className="selectoption largeSelectOption"
                  name="taxDetails.taxCategory"
                  value={formData.taxDetails?.taxCategory ?? ''}
                  required
                  onChange={handleChange}
                >
                  <option value="">{t('Select category')}</option>
                  {[...(clientOptions?.taxCategory || [])]
                    .sort((a, b) => a.localeCompare(b))
                    .map((category) => (
                      <option key={category} value={category}>
                        {t(category)}
                      </option>
                    ))}
                </select>
                {errors.taxCategory && (
                  <ValidationText className="error">
                    {errors.taxCategory}
                  </ValidationText>
                )}
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('Tax Number')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  name="taxDetails.taxNumber"
                  placeholder={t('Enter Tax Number')}
                  className="largeInput"
                  required
                  minLength={10}
                  maxLength={15}
                  pattern="^[A-Za-z0-9-]{10,15}$"
                  value={formData.taxDetails?.taxNumber ?? ''}
                  onChange={handleChange}
                />
                {errors.taxNumber && (
                  <ValidationText className="error">
                    {errors.taxNumber}
                  </ValidationText>
                )}
              </InputLabelContainer>
            </FormInputs>
            <AddClientButtons>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <ButtonGroup>
                <Button onClick={props.handleClose} type="button">
                  {t('Skip')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={() => {
                    handleNextStep();
                  }}
                >
                  {t('Save & Continue')}
                </Button>
              </ButtonGroup>
            </AddClientButtons>
          </AddressMainContainer>
        )}
        {step === 3 && (
          <ExpenseAddFormMainContainer
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
                  />
                </InputLabelContainer>
              </div>
            </FormInputs>
            <HeadingDiv>{t('Billing Address')}</HeadingDiv>
            <CheckBoxOuterContainer>
              <div>
                <StyledCheckbox
                  type="checkbox"
                  name="usePrimaryAddress"
                  style={{ cursor: 'pointer' }}
                  checked={formData.usePrimaryAddress}
                  onChange={handleCheckboxChange}
                />
              </div>

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
                  />
                </InputLabelContainer>
              </div>
            </FormInputs>
            <AddClientButtons>
              <div onClick={handlePreviousStep} className="leftAlign">
                <span className="separator"> {'<'} </span> &nbsp;
                {t('Previous')}
              </div>
              <div className="centerAlign">
                <Button onClick={props.handleClose} type="button">
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
          </ExpenseAddFormMainContainer>
        )}
        {step === 4 && (
          <ExpenseAddFormMainContainer
            className="formBackground"
            onSubmit={handleSubmitData}
            id="summaryForm"
          >
            <SummarySubContainer>
              <HeadingContainer>
                <SectionHeader>
                  {t('Basic Organisation Details')}
                  <EditIconWrapper>
                    <EditSVG onClick={() => setStep(1)} />
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
                      <DotWrap>
                        <DotSVG />
                      </DotWrap>
                      <InfoGroup>
                        <IndustrySVG />
                        <InfoText>{formData.industry}</InfoText>
                      </InfoGroup>
                      <DotWrap>
                        <DotSVG />
                      </DotWrap>
                      <InfoGroup>
                        <EmailSVG />
                        <InfoText>{formData.email}</InfoText>
                      </InfoGroup>

                      <DotWrap>
                        <DotSVG />
                      </DotWrap>

                      <InfoGroup>
                        <CallSVG />
                        <InfoText>{formData.contact}</InfoText>
                      </InfoGroup>
                    </InfoRow>
                  </div>
                  {(logoPreviewUrl || formData?.logoId) && (
                    <LogoNameWrapper>
                      <LogoPreview>
                        <img
                          src={
                            logoPreviewUrl
                              ? logoPreviewUrl
                              : `/projects/v1/files/download/${formData.logoId}`
                          }
                          alt="Client Logo"
                        />
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
              <SummaryAddressContainer>
                <SummaryAddressSubContainer>
                  <InfoBlock className="address">
                    <SubHeadingDiv>{t('Tax Category')}</SubHeadingDiv>
                    <InfoText>{formData.taxDetails.taxCategory}</InfoText>
                  </InfoBlock>
                  <InfoBlock style={{ display: 'flex' }}>
                    <SubHeadingDiv>{t('GST Number')}</SubHeadingDiv>
                    <InfoText>{formData.taxDetails.taxNumber}</InfoText>
                  </InfoBlock>
                </SummaryAddressSubContainer>
              </SummaryAddressContainer>

              <HeadingContainer>
                <SectionHeader>
                  {t('Address')}
                  <EditIconWrapper>
                    <EditSVG onClick={() => setStep(3)} />
                  </EditIconWrapper>
                </SectionHeader>
              </HeadingContainer>
              <SummaryAddressContainer>
                <PrimaryContainer>
                  <h2>Primary Address</h2>
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
                </PrimaryContainer>
                <PrimaryContainer>
                  <h2>Billing Address</h2>
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
                </PrimaryContainer>
              </SummaryAddressContainer>
            </SummarySubContainer>
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
      {step === 4 && (
        <div className="formButtons">
          <ButtonGroup>
            <Button onClick={handleDiscardModalToggle} type="button">
              {t('Cancel')}
            </Button>
            <Button className="submit" type="submit" form="summaryForm">
              {t('Save & Continue')}
            </Button>
          </ButtonGroup>
        </div>
      )}
      {isDiscardModalOpen && (
        <CenterModal
          handleModalLeftButtonClick={handleDiscardModalToggle}
          handleModalClose={handleDiscardModalToggle}
          handleModalSubmit={props.handleClose}
          modalHeading={t('Discard Changes?')}
          modalContent={t('Are you sure you want to discard your changes?')}
          modalType="discardModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
          modalLeftButtonText={t('No')}
          modalRightButtonText={t('Discard')}
        />
      )}
    </FormContainer>
  );
};

export default AddClientForm;
