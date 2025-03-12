import { useState } from 'react';
import { Button } from '../../styles/CommonStyles.style';
import {
  InputLabelContainer,
  TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import {
  FormContainer,
  StepsContainer,
  StepLabel,
  FormInputsContainer,
  LogoLabel,
  LogoUploadContainer,
  UploadText,
  BrowseText,
  FormInputs,
  HeadingDiv,
  CheckBoxOuterContainer,
  StyledCheckbox,
  LabelText,
  SubHeadingDiv,
  HeadingContainer,
  InfoRow,
  InfoText,
  InfoBlock,
  AddressBlock,
  BasicOrganizationDetailsContainer
} from "../../styles/ClientStyles.style";
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import { postClient } from '../../service/axiosInstance';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import { ClientType, Industry, TaxCategory } from '../reusableComponents/ClientEnums.component';
import { ClientDetails } from '../../entities/ClientEntity';
import { UploadSVG, 
  EditSVG,
  DotSVG,
  IndustrySVG,
  EmailSVG,
  CallSVG
} from '../../svgs/ClientSvgs.svs';
import { useTranslation } from 'react-i18next';

type AddClientFormProps = {
  handleClose: () => void;
  handleSuccessMessage: () => void;
};

const AddClientForm = (props: AddClientFormProps) => {
  const [formData, setFormData] = useState<ClientDetails>({
    clientName: "",
    clientType: "" as ClientType,
    email: "",
    industry: "" as Industry,
    contact: "",
    description: "",
    logo:  "",

    taxDetails: {
      taxCategory: "" as TaxCategory,
      taxNumber: ""
    },
     primaryAddress: {
      street: "",
      country: "",
      state: "",
      city: "",
      postalCode: ""
    },
    billingAddress: {
      street: "",
      country: "",
      state: "",
      city: "",
      postalCode: ""
    },
    usePrimaryAddress: false,
  });
  const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const isChecked = event.target.checked;
    setFormData((prevState) => ({
      ...prevState,
      usePrimaryAddress: isChecked,
      billingAddress: isChecked ? { ...prevState.primaryAddress } : prevState.billingAddress
    }));
  };
  const [step, setStep] = useState(1);
  const handleNextStep = () => {
    setStep((prevStep) => prevStep + 1);
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

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
  const selectedFile = event.target.files?.[0];
  if (selectedFile) {
    setFile(selectedFile); 
    setFormData((prevFormData) => ({
      ...prevFormData,
      logo: selectedFile, 
    }));
  }
};

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    const keys = name.split(".");

    setFormData((prevState) => {
      if (keys.length === 2) {
        const [parentKey, childKey] = keys as ["primaryAddress" | "billingAddress", string];

        return {
          ...prevState,
          [parentKey]: {
            ...prevState[parentKey],
            [childKey]: value,
          },
        };
      }

      return {
        ...prevState,
        [name]: value,
      };
    });
  };
  const handleSubmitData = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsResponseLoading(true);
  
    try {
      const dataToSend = new FormData();
      Object.entries(formData).forEach(([key, value]) => {
        if (typeof value === "object" && value !== null) {
          Object.entries(value).forEach(([subKey, subValue]) => {
            dataToSend.append(`${key}.${subKey}`, subValue as string);
          });
        } else {
          dataToSend.append(key, String(value));
        }
      });
      if (file) {
        dataToSend.append("logo", file);
      }
      await postClient(dataToSend);
      props.handleSuccessMessage();
    } catch (error) {
      setErrorMessage("Failed to submit data.");
      setShowErrorMessage(true);
    } finally {
      setIsResponseLoading(false);
    }
  };

  const { t } = useTranslation();
  return (
    <FormContainer>
      <>
      <StepsContainer>
        {["Basic Organisation Details", "Tax Details", "Address", "Summary"].map(
          (label, index) => (
            <StepLabel key={index} isActive={step === index + 1}>
              {label}
            </StepLabel>
          )
        )}
      </StepsContainer>

        {step === 1 && (
          <ExpenseAddFormMainContainer className="formBackground"
          >
            <FormInputsContainer>
              <div >
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
                    value={formData.clientName}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Email')}
                  </label>
                  <TextInput
                    type="email"
                    name="email" 
                    placeholder={t('Enter Email')}
                    className="largeInput"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Contact')}
                  </label>
                  <TextInput
                    type="text"
                    name="contact" 
                    placeholder={t('Enter Contact')}
                    className="largeInput"
                    value={formData.contact} 
                    onChange={handleChange}
                    required
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
                    value={formData.clientType}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select type')}</option>
                    <option value="INTERNAL">{t('INTERNAL')}</option>
                    <option value="INDIVIDUAL">{t('INDIVIDUAL')}</option>
                    <option value="COMPANY">{t('COMPANY')}</option>
                    <option value="ORGANIZATION">{t('ORGANIZATION')}</option>
                  </select>
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Industry')}
                  </label>
                  <select
                    className="selectoption largeSelectOption"
                    name="industry"
                    value={formData.industry}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select Industry')}</option>
                    <option value="HRMS">{t('HRMS')}</option>
                    <option value="SOCIALMEDIA">{t('SOCIALMEDIA')}</option>
                    <option value=" ECOMMERCE">{t(' ECOMMERCE')}</option>
                    <option value="ITSERVICES">{t('ITSERVICES')}</option>
                  </select>
                </InputLabelContainer>
                <InputLabelContainer>
                  <label>
                    {t('Description')}
                  </label>
                  <TextInput
                    type="text"
                    name="description"
                    placeholder={t('Enter Client Description')}
                    className="largeInput"
                    value={formData.description}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
              </div>

              </FormInputsContainer>
              <LogoLabel>{t('Logo')}</LogoLabel>
            
              <LogoUploadContainer onClick={() => document.getElementById("fileInput")?.click()}>
            <input
              id="fileInput"
              type="file"
              accept=".png, .jpg, .jpeg"
              style={{ display: "none" }}
              onChange={handleFileChange}
              name="logo"
            />
            <UploadSVG /> 
            <UploadText>
              {t('Upload your Logo')} <BrowseText>{t('Browse')}</BrowseText>
            </UploadText>
          </LogoUploadContainer>

            <div className="formButtons" >
              <Button onClick={props.handleClose}>{t('Skip')}</Button>
              <Button className="submit"
                onClick={() => {handleNextStep(); }}>
                {t('Save & Continue')}</Button>
            </div>
          </ExpenseAddFormMainContainer>
        )}
        {step === 2 && (
          <ExpenseAddFormMainContainer className="formBackground" >
            <FormInputs>
              <div  >
                <InputLabelContainer>
                  <label>
                    {t('Tax Category')}
                  </label>
                  <select
                    className="selectoption largeSelectOption"
                    name="taxDetails.taxCategory"
                    value={formData.taxDetails.taxCategory}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  >
                    <option value="">{t('Select category')}</option>
                    <option value="VAT">{t('VAT')}</option>
                    <option value="GST">{t('GST')}</option>
                    <option value="SALES_TAX">{t('SALES_TAX')}</option>
                    <option value="EXCISE_TAX">{t('EXCISE_TAX')}</option>
                  </select>
                </InputLabelContainer>
              </div>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('Tax Number')}
                  </label>
                  <TextInput
                    type="text"
                    name="taxDetails.taxNumber"
                    placeholder={t('Enter Tax Number')}
                    className="largeInput"
                    value={formData.taxDetails.taxNumber}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
              </div>
            </FormInputs>
            <div className="formButtons">
              <Button onClick={handlePreviousStep}>{t('Previous')}</Button>
              <Button onClick={props.handleClose}>{t('Skip')}</Button>
              <Button className="submit"
                onClick={handleNextStep}>
                {t('Save & Continue')}</Button>
            </div>
          </ExpenseAddFormMainContainer>
        )}
        {step === 3 && (
          <ExpenseAddFormMainContainer className="formBackground" >
            <HeadingDiv>
            {t('Primary Address')}
            </HeadingDiv>
           <FormInputs>
              <div style={{ fontFamily: 'Nunito', }}>
               <InputLabelContainer style={{ marginBottom: "1px" }}>
                  <label>
                    {t('Street')}
                  </label>
                  <TextInput
                    type="text"
                    name="primaryAddress.street" 
                    placeholder={t('Street')}
                    className="largeInput"
                    value={formData.primaryAddress.street} 
                    onChange={handleChange}
                    required
                    style={{ width: '420px' }}
                  />
                </InputLabelContainer>
                <div style={{ display: "flex", gap: '10px'}}>
                  <InputLabelContainer style={{ marginTop: "12px" }}>
                    <TextInput
                      type="text"
                      name="primaryAddress.city" 
                      placeholder={t('City')}
                      className="largeInput"
                      value={formData.primaryAddress.city} 
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer style={{ marginTop: "12px" }}>
                    <TextInput
                      type="text"
                      name="primaryAddress.state"
                      placeholder={t('State')}
                      className="largeInput"
                      value={formData.primaryAddress.state} 
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer style={{ marginTop: "12px" }}>
                    <TextInput
                      type="text"
                      name="primaryAddress.postalCode" 
                      placeholder={t('Zip/Postal Code')}
                      className="largeInput"
                      value={formData.primaryAddress.postalCode} 
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                </div>
              </div>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('Country')}
                  </label>
                  <TextInput
                    type="text"
                    name="primaryAddress.country"
                    placeholder={t('United States of America')}
                    className="largeInput"
                    value={formData.primaryAddress.country}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
              </div>
              </FormInputs>
            <HeadingDiv>
              {t('Billing Address')}
              </HeadingDiv>
            <CheckBoxOuterContainer>
              <InputLabelContainer style={{ marginTop: "15px" }}>

              <StyledCheckbox
                type="checkbox"
                name="usePrimaryAddress"
                checked={formData.usePrimaryAddress}
                onChange={handleCheckboxChange}
                required
              />
              </InputLabelContainer>

              <LabelText>{t('Use Primary address as billing address')}</LabelText>
              </CheckBoxOuterContainer>
            <FormInputs>
              <div >
                <InputLabelContainer style={{ marginBottom: "1px" }}>
                  <label>
                    {t('Street')}
                  </label>
                  <TextInput
                    type="text"
                    name="billingAddress.street"
                    placeholder={t('Street')}
                    className="largeInput"
                    value={formData.billingAddress.street}
                    onChange={handleChange}
                    required
                    style={{ width: '420px' }}
                  />
                </InputLabelContainer>
                <div style={{ display: "flex", gap: '10px'}}>
                  <InputLabelContainer style={{ marginTop: "12px" }}>
                    <TextInput
                      type="text"
                      name="billingAddress.city" 
                      placeholder={t('City')}
                      className="largeInput"
                      value={formData.billingAddress.city} 
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer style={{ marginTop: "12px" }} >
                    <TextInput
                      type="text"
                      name="billingAddress.state" 
                      placeholder={t('State')}
                      className="largeInput"
                      value={formData.billingAddress.state}
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer style={{ marginTop: "12px" }}>
                    <TextInput
                      type="text"
                      name="billingAddress.postalCode" 
                      placeholder={t('Zip/Postal Code')}
                      className="largeInput"
                      value={formData.billingAddress.postalCode} 
                      onChange={handleChange}
                      required
                      style={{ width: "133px" }}
                    />
                  </InputLabelContainer>
                </div>
              </div>
              <div>
                <InputLabelContainer>
                  <label>
                    {t('Country')}
                  </label>
                  <TextInput
                    type="text"
                    name="billingAddress.country"
                    placeholder={t('United States of America')}
                    className="largeInput"
                    value={formData.billingAddress.country}
                    onChange={handleChange}
                    required
                    style={{ width: '400px' }}
                  />
                </InputLabelContainer>
              </div>
            </FormInputs>
            <div className="formButtons">
              <Button onClick={handlePreviousStep}>{t('Previous')}</Button>
              <Button onClick={props.handleClose}>{t('Skip')}</Button>
              <Button className="submit"
                onClick={handleNextStep}>
                {t('Save & Continue')}</Button>
            </div>
          </ExpenseAddFormMainContainer>
        )}
        {step === 4 && (
          <ExpenseAddFormMainContainer className="formBackground" onSubmit={handleSubmitData}>
            <HeadingContainer>
              {t('Basic Organisation Details')}
              <EditSVG onClick={() => setStep(1)} />
              </HeadingContainer>
            <InputLabelContainer >
            <BasicOrganizationDetailsContainer>
            <InfoRow>
              <SubHeadingDiv>Client Name</SubHeadingDiv>
              <InfoText>{formData.clientName}</InfoText>
            </InfoRow>
              <InfoRow>
                <SubHeadingDiv>
                  {t('Client Type')}
                  </SubHeadingDiv>
                <InfoText>
                {formData.clientType}
                </InfoText>
              </InfoRow>
              <InfoRow>
                <SubHeadingDiv>
                  {t('Description')}
                  </SubHeadingDiv>
                <InfoText>
                {formData.description}
                </InfoText>
              </InfoRow>
              <InfoRow>
                <SubHeadingDiv style={{width:'100px'}}>
                {t('Internal Type')}
                  </SubHeadingDiv>
                <DotSVG/>
                <IndustrySVG/>
                 <SubHeadingDiv style={{width:'150px'}}>
                   {formData.industry}
                   </SubHeadingDiv>
                   <DotSVG/>
                <EmailSVG/>

                <SubHeadingDiv style={{width:'200px'}}>
                  {formData.email}
                  </SubHeadingDiv>
                  <DotSVG/>
                <CallSVG/>
                <SubHeadingDiv style={{width:'200px'}}>
                 {t('91+ ')}{formData.contact}
                 </SubHeadingDiv>
                 </InfoRow>
                 </BasicOrganizationDetailsContainer>
            </InputLabelContainer>
            <HeadingContainer>
            {t('Details')}
              <EditSVG onClick={() => setStep(2)} />
              </HeadingContainer>
            <FormInputsContainer style={{ backgroundColor: 'rgba(248, 249, 251, 1)'}}>

              <InfoBlock style={{display:'flex'}}>
                <SubHeadingDiv style={{width:'100px'}}>
                  {t('Task Category')}
                  </SubHeadingDiv>
                <InfoText>
                {formData.taxDetails.taxCategory}
                </InfoText>
              </InfoBlock>
              <InfoBlock style={{display:'flex'}}>
                <SubHeadingDiv style={{width:'100px'}}>
                  {t('GST Number')}
                  </SubHeadingDiv>
                <InfoText>
                {formData.taxDetails.taxNumber}
                </InfoText>
              </InfoBlock>

              </FormInputsContainer>
            <HeadingContainer>
               {t('Address')}
              <EditSVG onClick={() => setStep(3)} />
              </HeadingContainer>
              <FormInputsContainer style={{ backgroundColor: 'rgba(248, 249, 251, 1)'}}>

              <div>
                <AddressBlock>
                     <SubHeadingDiv>
                   {t('Street')}
                    </SubHeadingDiv>
                        <InfoText>
                        {formData.primaryAddress.street}
                        </InfoText>
                     </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                    {t('city')}
                    </SubHeadingDiv>
                  <InfoText>
                  {formData.primaryAddress.city}
                  </InfoText>
                </AddressBlock>
               <AddressBlock>
                  <SubHeadingDiv>
                  {t('State')}
                    </SubHeadingDiv>
                 <InfoText>
                 {formData.primaryAddress.state}
                 </InfoText>
                </AddressBlock>
               <AddressBlock>
                  <SubHeadingDiv>
                    {t('Country')}
                    </SubHeadingDiv>
                 <InfoText>
                 {formData.primaryAddress.country}
                 </InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                    {t('Zip/postal Code')}
                    </SubHeadingDiv>
                 <InfoText>
                 {formData.primaryAddress.postalCode}
                 </InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                    {t('Contact')}
                    </SubHeadingDiv>
                  <InfoText>
                  {formData.contact}
                  </InfoText>
                </AddressBlock>
              </div>
              <div>
              <AddressBlock>
                     <SubHeadingDiv>
                     {t('Street')}
                    </SubHeadingDiv>
                        <InfoText>
                        {formData.billingAddress.street}
                        </InfoText>
                     </AddressBlock>
                
                     <AddressBlock>
                  <SubHeadingDiv>
                  {t('city')}
                    </SubHeadingDiv>
                  <InfoText>
                  {formData.billingAddress.city}
                  </InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                  {t('State')}

                    </SubHeadingDiv>
                 <InfoText>
                 {formData.billingAddress.state}
                 </InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                    {t('Country')}
                    </SubHeadingDiv>
                 <InfoText>
                 {formData.billingAddress.country}
                 </InfoText>
                </AddressBlock>
                <AddressBlock>
                  <SubHeadingDiv>
                  {t('Zip/postal Code')}
                    </SubHeadingDiv>
                 <InfoText>
                 {formData.billingAddress.postalCode}
                 </InfoText>
                </AddressBlock>
               <AddressBlock>
                  <SubHeadingDiv>
                  {t('Contact')}
                    </SubHeadingDiv>
                  <InfoText>
                  {formData.contact}
                  </InfoText>
                </AddressBlock>



              </div>
            
            </FormInputsContainer>


            <div className="formButtons">
              <Button onClick={props.handleClose}>{t('Skip')}</Button>
              <Button className="submit">
                {t('Save & Continue')}</Button>
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
