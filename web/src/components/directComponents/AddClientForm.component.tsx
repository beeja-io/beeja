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
  const { t } = useTranslation();
  const [formData, setFormData] = useState<ClientDetails>({
    clientId: '',
    clientName: '',
    clientType: '' as ClientType,
    customClientType: '',
    email: '',
    industry: '' as Industry,
    customIndustry: '',
    contact: '',
    description: '',
    logo: '',

    taxDetails: {
      taxCategory: '' as TaxCategory,
      customTaxCategory: '',
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
      customClientType: '',
      email: '',
      industry: '' as Industry,
      customIndustry: '',
      contact: '',
      description: '',
      logo: '',
      logoId: '',
      taxDetails: {
        taxCategory: '' as TaxCategory,
        customTaxCategory: '',
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
    clientName?: string;
    clientType?: string;
    customClientType?: string;
    industry?: string;
    customIndustry?: string;
    email?: string;
    taxCategory?: string;
    customTaxCategory?: string;
    taxNumber?: string;
    primaryAddressPostalCode?: string;
    billingAddressPostalCode?: string;
  }>({});
  const [logoPreviewUrl, setLogoPreviewUrl] = useState<string | null>(null);

  const handleDiscardModalToggle = () => {
    setIsDiscardModalOpen((prev) => !prev);
  };

  const [touched, setTouched] = useState<{
    primaryAddressPostalCode?: boolean;
    billingAddressPostalCode?: boolean;
  }>({});

  const fiveDigitCountries = ['US', 'Germany', 'Australia'];

  const validatePostalCode = (country: string, code: string) => {
    if (!code) return true;

    if (country === 'India') {
      return /^\d{6}$/.test(code);
    }
    if (fiveDigitCountries.includes(country)) {
      return /^\d{5}$/.test(code);
    }
    return true;
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
      if (logoPreviewUrl) {
        URL.revokeObjectURL(logoPreviewUrl);
      }
      const objectUrl = URL.createObjectURL(selectedFile);
      setLogoPreviewUrl(objectUrl);
      setFile(selectedFile);
      setFormData((prev) => ({
        ...prev,
        clientLogo: selectedFile,
      }));
    }
  };

  const taxCategoryConfig: Record<
    string,
    { regex?: RegExp; errorMsg: string; placeholder: string }
  > = {
    GST: {
      regex: /^[A-Za-z0-9]{15}$/,
      errorMsg: t(
        'Invalid GST Number, it must be 15 alphanumeric characters (e.g., 27ABCD1234F1Z5)'
      ),
      placeholder: t(
        'GST must be 15 alphanumeric characters (e.g., 27ABCD1234F1Z5)'
      ),
    },
    ABN: {
      regex: /^\d{11}$/,
      errorMsg: t('Invalid ABN, it must be 11 digits (e.g., 51679993001)'),
      placeholder: t('ABN should be 11 digits (e.g., 51679993001)'),
    },
    VAT: {
      regex: /^[A-Z]{2}\d{9}$/,
      errorMsg: t(
        'Invalid VAT number. Format: Country code + digits (e.g., DE123456789)'
      ),
      placeholder: t('VAT Format: Country code + digits (e.g., DE123456789)'),
    },
    OTHER: {
      errorMsg: '',
      placeholder: t('Enter tax number'),
    },
  };

  const validateTaxNumber = (category: string, taxNumber: string) => {
    const config = taxCategoryConfig[category];
    if (!config) return '';
    if (config.regex && !config.regex.test(taxNumber)) {
      return config.errorMsg;
    }
    return '';
  };

  const getTaxNumberPlaceholder = (category: string) => {
    const config = taxCategoryConfig[category];
    return config ? config.placeholder : t('Enter Tax Number');
  };

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    const keys = name.split('.');

    let newValue = value ?? '';

    if (name === 'contact') {
      newValue = newValue.replace(/\D/g, '');
      if (newValue.startsWith('0')) {
        newValue = newValue.slice(1);
      }
      newValue = newValue.slice(0, 10);
    }

    if (
      name === 'billingAddress.postalCode' ||
      name === 'primaryAddress.postalCode'
    ) {
      const addressType = name.startsWith('primaryAddress')
        ? 'primaryAddress'
        : 'billingAddress';
      const country = formData[addressType]?.country ?? '';
      const maxLength =
        country === 'India' ? 6 : fiveDigitCountries.includes(country) ? 5 : 6;
      newValue = newValue.replace(/\D/g, '').slice(0, maxLength);
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

      if (
        [
          'clientName',
          'clientType',
          'industry',
          'customClientType',
          'customIndustry',
        ].includes(name)
      ) {
        if (newValue?.toString().trim()) {
          delete newErrors[name as keyof typeof newErrors];

          if (name === 'customClientType') {
            delete newErrors.clientType;
          }
          if (name === 'customIndustry') {
            delete newErrors.industry;
          }
        }
      }

      if (name === 'email') {
        if (!newValue.trim() || /\S+@\S+\.\S+/.test(newValue)) {
          delete newErrors.email;
        }
      }

      if (
        name === 'primaryAddress.postalCode' ||
        name === 'billingAddress.postalCode'
      ) {
        const addressType = name.startsWith('primaryAddress')
          ? 'primaryAddress'
          : 'billingAddress';
        const country = formData[addressType]?.country ?? '';
        const code = newValue;
        if (validatePostalCode(country, code)) {
          if (!touched[`${addressType}PostalCode`]) {
            return newErrors;
          }
          delete newErrors[`${addressType}PostalCode`];
        }
      }

      return newErrors;
    });
  };

  const handlePostalCodeBlur = (
    field: 'primaryAddressPostalCode' | 'billingAddressPostalCode'
  ) => {
    setTouched((prev) => ({ ...prev, [field]: true }));

    const addressType =
      field === 'primaryAddressPostalCode'
        ? 'primaryAddress'
        : 'billingAddress';
    const country = formData[addressType]?.country ?? '';
    const code = formData[addressType]?.postalCode ?? '';

    if (!validatePostalCode(country, code)) {
      setErrors((prev) => ({
        ...prev,
        [field]:
          country === 'India'
            ? t('validation.postalCode.india')
            : t('validation.postalCode.default', { country }),
      }));
    } else {
      setErrors((prev) => {
        const newErrors = { ...prev };
        delete newErrors[field];
        return newErrors;
      });
    }
  };

  const isOtherValue = (val: any) =>
    val === ClientType.OTHER ||
    val === Industry.OTHER ||
    String(val).toUpperCase() === 'OTHER';
  const validateStep1 = () => {
    const newErrors: {
      clientName?: string;
      clientType?: string;
      customClientType?: string;
      industry?: string;
      customIndustry?: string;
      email?: string;
    } = {};

    if (!formData.clientName.trim()) {
      newErrors.clientName = t('CLIENT_NAME_REQUIRED');
    }

    if (!formData.clientType) {
      newErrors.clientType = t('CLIENT_TYPE_REQUIRED');
    } else if (
      isOtherValue(formData.clientType) &&
      !formData.customClientType?.trim()
    ) {
      newErrors.customClientType = t('CUSTOM_CLIENT_TYPE_REQUIRED');
    }

    if (!formData.industry) {
      newErrors.industry = t('INDUSTRY_REQUIRED');
    } else if (
      isOtherValue(formData.industry) &&
      !formData.customIndustry?.trim()
    ) {
      newErrors.customIndustry = t('CUSTOM_INDUSTRY_REQUIRED');
    }

    if (formData.email?.trim() && !/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = t('INVALID_EMAIL');
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateStep2 = () => {
    let isValid = true;

    const newErrors: { taxNumber?: string; taxCategory?: string } = {};

    const { taxCategory, customTaxCategory, taxNumber } = formData.taxDetails;
    const category =
      taxCategory === TaxCategory.OTHER ? customTaxCategory : taxCategory;

    if (!taxCategory) {
      isValid = false;
      newErrors.taxCategory = 'Please select a Tax Category.';
    }

    const trimmedTaxNumber = taxNumber?.trim() || '';
    if (!trimmedTaxNumber) {
      isValid = false;
      newErrors.taxNumber = 'Please enter a Tax Number.';
    } else {
      const errorMessage = validateTaxNumber(category, trimmedTaxNumber);
      if (errorMessage) {
        isValid = false;
        newErrors.taxNumber = errorMessage;
      }
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

    const processCustomField = (
      payload: any,
      enumField: 'clientType' | 'industry',
      customField: 'customClientType' | 'customIndustry'
    ) => {
      const enumValue = payload[enumField];
      const customValue = payload[customField];

      if (enumValue === ClientType.OTHER || enumValue === Industry.OTHER) {
        payload[enumField] = 'OTHER';
        payload[customField] = customValue ? customValue.trim() : '';
      } else {
        payload[customField] = '';
      }
    };
    try {
      const payload: any = { ...formData };

      processCustomField(payload, 'clientType', 'customClientType');
      processCustomField(payload, 'industry', 'customIndustry');
      if (payload.taxDetails.taxCategory === TaxCategory.OTHER) {
        payload.taxDetails.taxCategory = 'OTHER';
        payload.taxDetails.customTaxCategory =
          formData.taxDetails.customTaxCategory.trim();
      } else {
        payload.taxDetails.customTaxCategory = '';
      }
      const dataToSend = new FormData();
      Object.entries(payload).forEach(([key, value]) => {
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
      handleClose();
    } catch (error: any) {
      const backendMessage =
        error?.response?.data?.message || error?.message || '';

      if (backendMessage.includes('Client Found with provided email')) {
        setErrorMessage('Email Already Exists');
        setShowErrorMessage(true);
      } else {
        setErrorMessage('Failed to submit data.');
        setShowErrorMessage(true);
      }
    } finally {
      setIsResponseLoading(false);
    }
  };

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
                      options={clientOptions.clientType.map((type) => ({
                        label: type === 'OTHER' ? 'Other' : type,
                        value: type,
                      }))}
                      value={
                        formData.clientType === ClientType.OTHER &&
                        formData.customClientType
                          ? formData.customClientType
                          : formData.clientType || null
                      }
                      onChange={(selectedValue) => {
                        if (selectedValue === ClientType.OTHER) {
                          setFormData((prev) => {
                            const newState = {
                              ...prev,
                              clientType: ClientType.OTHER,
                            };
                            return newState;
                          });
                        } else {
                          setFormData((prev) => ({
                            ...prev,
                            clientType: selectedValue as ClientType,
                            customClientType: '',
                          }));
                        }
                      }}
                      onCustomValue={(customValue) => {
                        setFormData((prev) => {
                          const newState = {
                            ...prev,
                            clientType: ClientType.OTHER,
                            customClientType: customValue,
                          };
                          return newState;
                        });
                      }}
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
                    />
                    {errors.clientName && (
                      <ValidationText className="error">
                        {errors.clientName}
                      </ValidationText>
                    )}
                  </InputLabelContainer>
                )}
                <InputLabelContainer>
                  <label>{t('EMAIL')}</label>
                  <TextInput
                    type="email"
                    name="email"
                    placeholder={t('Enter Email')}
                    className="largeInput"
                    value={formData?.email}
                    onChange={handleChange}
                  />
                  {errors.email && (
                    <ValidationText className="error">
                      {errors.email}
                    </ValidationText>
                  )}
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
                    <span className="grayText">
                      {t('File format')} : .png, .jpeg
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
                    />
                    {errors.clientName && (
                      <ValidationText className="error">
                        {errors.clientName}
                      </ValidationText>
                    )}
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
                      value={
                        formData.clientType === ClientType.OTHER &&
                        formData.customClientType
                          ? formData.customClientType
                          : formData.clientType || null
                      }
                      className="largeContainerHei"
                      required
                      options={[
                        ...clientOptions.clientType.map((type) => ({
                          label: type === 'OTHER' ? t('Other') : t(type),
                          value: type,
                        })),
                      ]}
                      onChange={(selectedValue) => {
                        if (selectedValue === ClientType.OTHER) {
                          setFormData((prev) => ({
                            ...prev,
                            clientType: ClientType.OTHER,
                            customClientType: '',
                          }));
                        } else {
                          setFormData((prev) => ({
                            ...prev,
                            clientType: selectedValue as ClientType,
                            customClientType: '',
                          }));
                        }
                        setErrors((prev) => {
                          const newErrors = { ...prev };
                          delete newErrors.clientType;
                          delete newErrors.customClientType;
                          return newErrors;
                        });
                      }}
                      onCustomValue={(customValue) => {
                        setFormData((prev) => ({
                          ...prev,
                          clientType: ClientType.OTHER,
                          customClientType: customValue,
                        }));
                        setErrors((prev) => {
                          const newErrors = { ...prev };
                          delete newErrors.clientType;
                          delete newErrors.customClientType;
                          return newErrors;
                        });
                      }}
                    />
                    {errors.clientType && (
                      <ValidationText className="error">
                        {errors.clientType}
                      </ValidationText>
                    )}
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
                    value={
                      formData.industry === Industry.OTHER &&
                      formData.customIndustry
                        ? formData.customIndustry
                        : formData.industry || null
                    }
                    onChange={(selectedValue) => {
                      if (selectedValue === Industry.OTHER) {
                        setFormData((prev) => ({
                          ...prev,
                          industry: Industry.OTHER,
                          customIndustry: '',
                        }));
                      } else {
                        setFormData((prev) => ({
                          ...prev,
                          industry: selectedValue as Industry,
                          customIndustry: '',
                        }));
                      }
                      setErrors((prev) => {
                        const newErrors = { ...prev };
                        delete newErrors.industry;
                        delete newErrors.customIndustry;
                        return newErrors;
                      });
                    }}
                    onCustomValue={(customValue) => {
                      setFormData((prev) => ({
                        ...prev,
                        industry: Industry.OTHER,
                        customIndustry: customValue,
                      }));
                      setErrors((prev) => {
                        const newErrors = { ...prev };
                        delete newErrors.industry;
                        delete newErrors.customIndustry;
                        return newErrors;
                      });
                    }}
                    required
                    options={[
                      ...clientOptions.industry.map((industry) => ({
                        label: industry === 'OTHER' ? t('Other') : t(industry),
                        value: industry,
                      })),
                    ]}
                  />
                  {errors.industry && (
                    <ValidationText className="error">
                      {errors.industry}
                    </ValidationText>
                  )}
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
                    {t('File format')} : .png, .jpeg
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
                {t('Continue')}
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
                  value={
                    formData.taxDetails?.taxCategory === TaxCategory.OTHER &&
                    formData.taxDetails?.customTaxCategory
                      ? formData.taxDetails.customTaxCategory
                      : (formData.taxDetails?.taxCategory ?? null)
                  }
                  required
                  onChange={(selectedValue) => {
                    if (selectedValue === TaxCategory.OTHER) {
                      setFormData((prev) => ({
                        ...prev,
                        taxDetails: {
                          ...prev.taxDetails,
                          taxCategory: TaxCategory.OTHER,
                          customTaxCategory: '',
                        },
                      }));
                    } else {
                      setFormData((prev) => ({
                        ...prev,
                        taxDetails: {
                          ...prev.taxDetails,
                          taxCategory: selectedValue as TaxCategory,
                          customTaxCategory: '',
                        },
                      }));
                    }
                    setErrors((prev) => {
                      const newErrors = { ...prev };
                      delete newErrors.taxCategory;
                      return newErrors;
                    });
                  }}
                  onCustomValue={(customValue) => {
                    setFormData((prev) => ({
                      ...prev,
                      taxDetails: {
                        ...prev.taxDetails,
                        taxCategory: TaxCategory.OTHER,
                        customTaxCategory: customValue,
                      },
                    }));
                    setErrors((prev) => {
                      const newErrors = { ...prev };
                      delete newErrors.taxCategory;
                      delete newErrors.customTaxCategory;
                      return newErrors;
                    });
                  }}
                  options={[
                    ...(clientOptions?.taxCategory ?? []).map((category) => ({
                      label: category === 'OTHER' ? 'Other' : category,
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
                  placeholder={getTaxNumberPlaceholder(
                    formData.taxDetails.taxCategory
                  )}
                  className="largeInput"
                  required
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
                  {t('Continue')}
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
                      onBlur={() =>
                        handlePostalCodeBlur('primaryAddressPostalCode')
                      }
                      style={{ width: '133px' }}
                    />
                  </InputLabelContainer>
                </div>
                {touched.primaryAddressPostalCode &&
                  errors.primaryAddressPostalCode && (
                    <ValidationText>
                      {errors.primaryAddressPostalCode}
                    </ValidationText>
                  )}
              </div>
              <div>
                <InputLabelContainer>
                  <label>{t('Country')}</label>
                  <DropdownMenu
                    label={t('SELECT_COUNTRY')}
                    value={formData.primaryAddress?.country ?? ''}
                    className="largeInput"
                    onChange={(value: string | null) => {
                      handleChange({
                        target: {
                          name: 'primaryAddress.country',
                          value: value ?? '',
                        },
                      } as React.ChangeEvent<HTMLInputElement>);
                    }}
                    options={[
                      { label: t('Select Country'), value: '' },
                      { label: t('INDIA'), value: 'India' },
                      { label: t('GERMANY'), value: 'Germany' },
                      { label: t('US'), value: 'US' },
                      { label: t('AUSTRALIA'), value: 'Australia' },
                    ]}
                    style={{ width: '280px', minWidth: '280px' }}
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
                      onBlur={() =>
                        handlePostalCodeBlur('billingAddressPostalCode')
                      }
                      style={{ width: '133px' }}
                    />
                  </InputLabelContainer>
                </div>
                {touched.billingAddressPostalCode &&
                  errors.billingAddressPostalCode && (
                    <ValidationText>
                      {errors.billingAddressPostalCode}
                    </ValidationText>
                  )}
              </div>
              <div>
                <InputLabelContainer>
                  <label>{t('Country')}</label>
                  <DropdownMenu
                    label={t('SELECT_COUNTRY')}
                    value={formData.billingAddress?.country ?? ''}
                    className="largeInput"
                    disabled={formData.usePrimaryAddress}
                    onChange={(value: string | null) => {
                      handleChange({
                        target: {
                          name: 'billingAddress.country',
                          value: value ?? '',
                        },
                      } as React.ChangeEvent<HTMLInputElement>);
                    }}
                    options={[
                      { label: t('Select Country'), value: '' },
                      { label: t('INDIA'), value: 'India' },
                      { label: t('GERMANY'), value: 'Germany' },
                      { label: t('US'), value: 'US' },
                      { label: t('AUSTRALIA'), value: 'Australia' },
                    ]}
                    style={{ width: '280px', minWidth: '280px' }}
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
                  {t('Cancel')}
                </Button>
                <Button
                  className="submit"
                  type="button"
                  onClick={handleNextStep}
                >
                  {formData.primaryAddress.street ||
                  formData.primaryAddress.city ||
                  formData.primaryAddress.state ||
                  formData.primaryAddress.postalCode ||
                  formData.primaryAddress.country ||
                  formData.billingAddress.street ||
                  formData.billingAddress.city ||
                  formData.billingAddress.state ||
                  formData.billingAddress.postalCode ||
                  formData.billingAddress.country
                    ? t('Continue')
                    : t('Skip & Continue')}
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
                      <InfoText>
                        {formData.clientType === ClientType.OTHER &&
                        formData.customClientType
                          ? formData.customClientType
                          : formData.clientType || '-'}
                      </InfoText>
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
                      <DotWrap>
                        <DotSVG />
                      </DotWrap>
                      <InfoGroup>
                        <IndustrySVG />
                        <InfoText>
                          {formData.industry === Industry.OTHER &&
                          formData.customIndustry
                            ? formData.customIndustry
                            : formData.industry || 'N/A'}
                        </InfoText>
                      </InfoGroup>
                      <DotWrap>
                        <DotSVG />
                      </DotWrap>
                      <InfoGroup>
                        <EmailSVG />
                        <InfoText>
                          {formData.email ? formData.email : '-'}
                        </InfoText>
                      </InfoGroup>

                      <DotWrap>
                        <DotSVG />
                      </DotWrap>
                      <InfoGroup>
                        <CallSVG />
                        <InfoText>
                          {formData.contact ? formData.contact : 'N/A'}
                        </InfoText>
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
                      {formData.taxDetails?.taxCategory === TaxCategory.OTHER &&
                      formData.taxDetails?.customTaxCategory
                        ? formData.taxDetails.customTaxCategory
                        : formData.taxDetails?.taxCategory
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
                  <h2>{t('Primary Address')}</h2>
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
