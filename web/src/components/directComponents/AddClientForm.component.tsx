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
  Button,
} from '../../styles/ClientStyles.style';
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
import DropdownMenu from '../reusableComponents/DropDownMenu.component';

type AddClientFormProps = {
  handleClose: () => void;
  handleSuccessMessage: (value: string, type: 'add' | 'edit') => void;
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

      const same = isSameAddress(
        mergedData.primaryAddress,
        mergedData.billingAddress
      );
      setFormData({
        ...mergedData,
        usePrimaryAddress: same,
      });

      if (typeof props.initialData.logo === 'string') {
        setLogoPreviewUrl(props.initialData.logo);
      }
    } else {
      setFormData(defaultData);
      setLogoPreviewUrl(null);
      setFile(null);
    }
  }, [props.initialData, props.isEditMode]);

  useEffect(() => {
    if (formData.usePrimaryAddress) {
      setFormData((prev) => ({
        ...prev,
        billingAddress: { ...prev.primaryAddress },
      }));
    }
  }, [formData.primaryAddress, formData.usePrimaryAddress]);

  const normalize = (value: any) =>
    value?.toString().trim().toLowerCase() ?? '';

  const isSameAddress = (addr1: any, addr2: any) => {
    if (!addr1 || !addr2) return false;

    const fields = ['street', 'city', 'state', 'postalCode', 'country'];
    let hasValue = false;

    return (
      fields.every((field) => {
        const val1 = addr1[field];
        const val2 = addr2[field];

        if (!val1 || !val2) return true;

        hasValue = true;
        return normalize(val1) === normalize(val2);
      }) && hasValue
    );
  };

  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const isChecked = event.target.checked;
    setFormData((prevState) => ({
      ...prevState,
      usePrimaryAddress: isChecked,
      billingAddress: isChecked
        ? { ...prevState.primaryAddress }
        : { ...prevState.billingAddress },
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
        if (value === null || value === undefined) return;
        if (typeof value === 'object' && !(value instanceof File)) {
          Object.entries(value).forEach(([subKey, subValue]) => {
            if (subValue !== null && subValue !== undefined) {
              dataToSend.append(`${key}.${subKey}`, String(subValue));
            }
          });
        } else if (key !== 'logo') {
          dataToSend.append(key, String(value));
        }
      });
      if (file instanceof File) {
        dataToSend.append('logo', file);
      } else if (isEditMode && !formData.logoId) {
        dataToSend.append('removeLogo', 'true');
      }

      if (isEditMode && formData.clientId) {
        await putClient(formData.clientId, dataToSend);
        handleSuccessMessage('Client has been successfully updated.', 'edit');
      } else {
        const response = await postClient(dataToSend);
        const clientId = response?.data?.clientId;

        if (clientId) {
          handleSuccessMessage(clientId, 'add');
        } else {
          setErrorMessage(
            t('Client created, but ID is missing in the response.')
          );
        }
      }
      await updateClientList();
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
            'Basic_Organisation_Details',
            'Tax_Details',
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
                    <div className="labelHead">{t(label)}</div>
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
                    <label>{t('Client_ID')}</label>
                    <TextInput
                      type="text"
                      className="disabled"
                      value={formData.clientId}
                      disabled
                    />
                  </InputLabelContainer>
                )}
                {formData?.clientId && (
                  <InputLabelContainer>
                    <label>
                      {t('Client_Type')}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <DropdownMenu
                      label={t('Select type')}
                      name="clientType"
                      id="clientType"
                      value={formData?.clientType || ''}
                      className="largeContainerHei"
                      onChange={(e) => {
                        const event = {
                          target: {
                            name: 'clientType',
                            value: e,
                          },
                        } as React.ChangeEvent<HTMLSelectElement>;
                        handleChange(event);
                      }}
                      required
                      options={[
                        { label: t('Select type'), value: '' },
                        ...clientOptions.clientType.map((type) => ({
                          label: t(type),
                          value: type,
                        })),
                      ]}
                    />
                  </InputLabelContainer>
                )}
                {!formData?.clientId && (
                  <InputLabelContainer>
                    <label>
                      {t('Client_Name')}
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
                )}
                <InputLabelContainer>
                  <label>
                    {t('EMAIL')}
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
                {!formData?.clientId && (
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
                )}
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
                        {t('Upload_your_Logo')}
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
                    <span className="infoText">
                      File format : .pdf, .png, .jpeg
                    </span>
                  </InputLabelContainer>
                )}
              </ColumnWrapper>
              <ColumnWrapper>
                {formData?.clientId && (
                  <InputLabelContainer>
                    <label>
                      {t('Client_Name')}
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
                )}
                {!formData?.clientId && (
                  <InputLabelContainer>
                    <label>
                      {t('Client_Type')}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <DropdownMenu
                      label={t('Select type')}
                      name="clientType"
                      id="clientType"
                      value={formData?.clientType || ''}
                      className="largeContainerHei"
                      onChange={(e) => {
                        const event = {
                          target: {
                            name: 'clientType',
                            value: e,
                          },
                        } as React.ChangeEvent<HTMLSelectElement>;
                        handleChange(event);
                      }}
                      required
                      options={[
                        { label: t('Select type'), value: '' },
                        ...clientOptions.clientType.map((type) => ({
                          label: t(type),
                          value: type,
                        })),
                      ]}
                    />
                  </InputLabelContainer>
                )}

                <InputLabelContainer>
                  <label>
                    {t('Industry')}
                    <ValidationText className="star">*</ValidationText>
                  </label>
                  <DropdownMenu
                    label={t('Select Industry')}
                    name="industry"
                    id="industry"
                    className="largeContainerHei"
                    value={formData?.industry || ''}
                    onChange={(e) => {
                      const event = {
                        target: {
                          name: 'industry',
                          value: e,
                        },
                      } as React.ChangeEvent<HTMLSelectElement>;
                      handleChange(event);
                    }}
                    required
                    options={[
                      { label: t('Select Industry'), value: '' },
                      ...clientOptions.industry.map((industry) => ({
                        label: t(industry),
                        value: industry,
                      })),
                    ]}
                  />
                </InputLabelContainer>
                {formData?.clientId && (
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
                )}
                <InputLabelContainer>
                  <label>{t('Description')}</label>
                  <TextInput
                    type="text"
                    name="description"
                    placeholder={t('Enter_Client_Description')}
                    className="largeInput"
                    value={formData?.description}
                    onChange={handleChange}
                  />
                </InputLabelContainer>
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
                      {t('Upload_your_Logo')}
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
                  <br />
                  <span className="infoText">
                    File format : .pdf, .png, .jpeg
                  </span>
                </LogoContainer>
              )}
            </FormInputsContainer>
            <div className="formButtons">
              <Button
                onClick={props.handleClose}
                type="button"
                className="cancel"
              >
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
                  {t('Tax_Category')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('Select category')}
                  name="taxDetails.taxCategory"
                  id="taxDetails.taxCategory"
                  className="largeContainerHei"
                  value={formData.taxDetails?.taxCategory ?? ''}
                  required
                  onChange={(e) => {
                    const event = {
                      target: {
                        name: 'taxDetails.taxCategory',
                        value: e,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>;
                    handleChange(event);
                  }}
                  options={[
                    { label: 'Select', value: '' },
                    ...(clientOptions?.taxCategory ?? []).map((category) => ({
                      label: t(`TaxCategory.${category}`),
                      value: category,
                    })),
                  ]}
                />
                {errors.taxCategory && (
                  <ValidationText className="error">
                    {errors.taxCategory}
                  </ValidationText>
                )}
              </InputLabelContainer>

              <InputLabelContainer>
                <label>
                  {t('Tax_Number')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  name="taxDetails.taxNumber"
                  placeholder={t('Enter_Tax_Number')}
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
                <Button
                  onClick={props.handleClose}
                  type="button"
                  className="cancel"
                >
                  {t('Cancel')}
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
            <HeadingDiv>{t('Primary_Address')}</HeadingDiv>
            <FormInputs>
              <div style={{ fontFamily: 'Nunito' }}>
                <InputLabelContainer style={{ marginBottom: '1px' }}>
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
                      placeholder={t('Zip/Postal_Code')}
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
                    placeholder={t('United_States_of_America')}
                    className="largeInput"
                    value={formData.primaryAddress?.country}
                    onChange={handleChange}
                    style={{ width: '420px' }}
                  />
                </InputLabelContainer>
              </div>
            </FormInputs>
            <HeadingDiv>{t('Billing_Address')}</HeadingDiv>
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
                {t('Use_Primary_address_as_billing_address')}
              </LabelText>
            </CheckBoxOuterContainer>
            <FormInputs>
              <div>
                <InputLabelContainer style={{ marginBottom: '1px' }}>
                  <label>{t('Street')}</label>
                  <TextInput
                    type="text"
                    name="billingAddress.street"
                    disabled={formData.usePrimaryAddress}
                    placeholder={t('Street')}
                    className="largeInput"
                    value={formData?.billingAddress?.street ?? ''}
                    onChange={handleChange}
                    style={{ width: '420px' }}
                  />
                </InputLabelContainer>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <InputLabelContainer>
                    <TextInput
                      type="text"
                      name="billingAddress.city"
                      disabled={formData.usePrimaryAddress}
                      placeholder={t('City')}
                      className="largeInput"
                      value={formData?.billingAddress?.city ?? ''}
                      onChange={handleChange}
                      style={{ width: '133px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <TextInput
                      type="text"
                      name="billingAddress.state"
                      disabled={formData.usePrimaryAddress}
                      placeholder={t('State')}
                      className="largeInput"
                      value={formData?.billingAddress?.state ?? ''}
                      onChange={handleChange}
                      style={{ width: '133px' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <TextInput
                      type="text"
                      name="billingAddress.postalCode"
                      disabled={formData.usePrimaryAddress}
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
                    disabled={formData.usePrimaryAddress}
                    placeholder={t('United_States_of_America')}
                    className="largeInput"
                    value={formData?.billingAddress?.country ?? ''}
                    onChange={handleChange}
                    style={{ width: '420px' }}
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
                <Button
                  onClick={props.handleClose}
                  className="cancel"
                  type="button"
                >
                  {t('Skip')}
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
                  {t('Basic_Organisation_Details')}
                  <EditIconWrapper>
                    <EditSVG onClick={() => setStep(1)} />
                  </EditIconWrapper>
                </SectionHeader>
              </HeadingContainer>
              <InputLabelLogoContainer>
                <BasicOrganizationDetailsContainer>
                  <div>
                    <InfoRow>
                      <SubHeadingDiv className="spacing">
                        {t('Client_Name')}
                      </SubHeadingDiv>
                      <InfoText>{formData.clientName}</InfoText>
                    </InfoRow>
                    <InfoRow>
                      <SubHeadingDiv className="spacing">
                        {t('Client_Type')}
                      </SubHeadingDiv>
                      <InfoText>{formData.clientType}</InfoText>
                    </InfoRow>
                    <InfoRow>
                      <SubHeadingDiv className="spacing">
                        {t('Description')}
                      </SubHeadingDiv>
                      <InfoText className="description">
                        {formData.description?.trim()
                          ? formData.description
                          : '-'}
                      </InfoText>
                    </InfoRow>
                    <InfoRow>
                      <SubHeadingDiv>{t('Internal_Type')}</SubHeadingDiv>
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
                  {t('Tax Details')}
                  <EditIconWrapper>
                    <EditSVG onClick={() => setStep(2)} />
                  </EditIconWrapper>
                </SectionHeader>
              </HeadingContainer>
              <SummaryAddressContainer>
                <SummaryAddressSubContainer>
                  <InfoBlock className="address">
                    <SubHeadingDiv className="spacing tax-container">
                      {t('Tax_Category')}
                    </SubHeadingDiv>
                    <InfoText className="tax-details">
                      {formData.taxDetails?.taxCategory
                        ? t(
                            `TaxCategory.${formData.taxDetails.taxCategory.toUpperCase()}`,
                            {
                              defaultValue:
                                formData.taxDetails.taxCategory.replace(
                                  /_/g,
                                  ' '
                                ),
                            }
                          )
                        : '-'}
                    </InfoText>
                  </InfoBlock>
                  <InfoBlock className="address">
                    <SubHeadingDiv className="spacing tax-container">
                      {t('GST_Number')}
                    </SubHeadingDiv>
                    <InfoText className="tax-details">
                      {formData.taxDetails.taxNumber}
                    </InfoText>
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
                    <SubHeadingDiv className="spacing">
                      {t('Street')}
                    </SubHeadingDiv>
                    <InfoText>{formData.primaryAddress.street}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('City')}
                    </SubHeadingDiv>
                    <InfoText>{formData.primaryAddress.city}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('State')}
                    </SubHeadingDiv>
                    <InfoText>{formData.primaryAddress.state}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Country')}
                    </SubHeadingDiv>
                    <InfoText>{formData.primaryAddress.country}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Zip/Postal Code')}
                    </SubHeadingDiv>
                    <InfoText>{formData.primaryAddress.postalCode}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Contact')}
                    </SubHeadingDiv>
                    <InfoText>{formData.contact}</InfoText>
                  </AddressBlock>
                </PrimaryContainer>
                <PrimaryContainer>
                  <h2>{t('Billing_Address')}</h2>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Street')}
                    </SubHeadingDiv>
                    <InfoText>{formData.billingAddress.street}</InfoText>
                  </AddressBlock>

                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('City')}
                    </SubHeadingDiv>
                    <InfoText>{formData.billingAddress.city}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('State')}
                    </SubHeadingDiv>
                    <InfoText>{formData.billingAddress.state}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Country')}
                    </SubHeadingDiv>
                    <InfoText>{formData.billingAddress.country}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Zip/Postal Code')}
                    </SubHeadingDiv>
                    <InfoText>{formData.billingAddress.postalCode}</InfoText>
                  </AddressBlock>
                  <AddressBlock>
                    <SubHeadingDiv className="spacing">
                      {t('Contact')}
                    </SubHeadingDiv>
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
        <ButtonGroup>
          <Button
            onClick={handleDiscardModalToggle}
            type="button"
            className="cancel"
          >
            {t('Cancel')}
          </Button>
          <Button className="submit" type="submit" form="summaryForm">
            {props.isEditMode ? t('Update') : t('Add')}
          </Button>
        </ButtonGroup>
      )}
      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageBody="Email Already Exists"
          messageHeading="Error Occured"
          handleClose={handleShowErrorMessage}
        />
      )}
      {isDiscardModalOpen && (
        <CenterModal
          handleModalLeftButtonClick={handleDiscardModalToggle}
          handleModalClose={handleDiscardModalToggle}
          handleModalSubmit={props.handleClose}
          modalHeading={t('Discard_Changes?')}
          modalContent={t('Are_you_sure_you_want_to_discard_your_changes?')}
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
