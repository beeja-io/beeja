import { useEffect, useRef, useState } from 'react';
import { Button } from '../../styles/CommonStyles.style';
import {
  InputLabelContainer,
  TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import Calendar from '../reusableComponents/Calendar.component';
import { postInventory } from '../../service/axiosInstance';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import { DeviceDetails } from '../../entities/InventoryEntity';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import axios from 'axios';
import useKeyCtrl from '../../service/keyboardShortcuts/onKeySave';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import {
  Availability
} from '../reusableComponents/InventoryEnums.component';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import { useTranslation } from 'react-i18next';

type AddInventoryFormProps = {
  handleClose: () => void;
  handleSuccessMessage: () => void;
  deviceTypes: OrganizationValues;
  inventoryProviders: OrganizationValues;
};

const AddInventoryForm = (props: AddInventoryFormProps) => {
  const calendarFromRef = useRef<HTMLDivElement>(null);
  const [formData, setFormData] = useState<DeviceDetails>({} as DeviceDetails);
  const [isCalenderOpen, setIsCalenderOpen] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);

  const handleShowErrorMessage = () => {
    setShowErrorMessage(!showErrorMessage);
  };

  const handleCalenderOpen = (isOpen: boolean) => {
    setIsCalenderOpen(isOpen);
  };

  const [errorMessage, setErrorMessage] = useState<string>('');

  const formatDate = (date: Date): string => {
    const options: Intl.DateTimeFormatOptions = {
      month: 'short',
      day: '2-digit',
      year: 'numeric',
    };
    return new Intl.DateTimeFormat('en-US', options).format(date);
  };

  const [dateOfPurchase, setDateOfPurchase] = useState<Date | null>(null);
  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    if (name === 'purchaseDate') {
      setDateOfPurchase(value ? new Date(value) : null);
    } else {
      setFormData((prevState) => ({
        ...prevState,
        [name]: value,
      }));
    }
  };

  const handleSubmitData = async (event: { preventDefault: () => void }) => {
    event.preventDefault();
    setIsResponseLoading(true);
    try {
      const data: DeviceDetails = {
        ...formData,
        device: formData.device.toUpperCase(),
        type: formData.type.toUpperCase(),
        availability: formData.availability.toUpperCase() as Availability,
        accessoryType: formData.accessoryType?.toUpperCase(),
        dateOfPurchase: formData.dateOfPurchase,
      };
      await postInventory(data);
      setIsResponseLoading(false);
      props.handleSuccessMessage();
    } catch (error) {
      if (
        axios.isAxiosError(error) &&
        error.response?.data.startsWith('Product ID already exists')
      ) {
        setErrorMessage('PRODUCT_ID_ALREADY_EXIST');
      } else {
        setErrorMessage('INVENTORY_NOT_UPLOADED');
      }
      setIsResponseLoading(false);
      handleShowErrorMessage();
    }
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        calendarFromRef.current &&
        !calendarFromRef.current.contains(event.target as Node)
      ) {
        setIsCalenderOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);
  useKeyCtrl('s', () =>
    handleSubmitData(event as unknown as React.FormEvent<HTMLFormElement>)
  );
  useKeyPress(27, () => {
    props.handleClose();
  });

  const { t } = useTranslation();
  return (
    <>
      <ExpenseAddFormMainContainer onSubmit={handleSubmitData}>
        <div className="formInputs">
          <div>
            <InputLabelContainer>
              <label>
                {t('DEVICE')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <select
                className="selectoption largeSelectOption"
                name="device"
                value={formData.device}
                onChange={handleChange}
                required
              >
                <option value="">{t('SELECT_DEVICE')}</option>
                {props.deviceTypes.values?.map((deviceType) => (
                  <option key={deviceType.value} value={deviceType.value}>
                    {deviceType.value}
                  </option>
                ))}
              </select>
            </InputLabelContainer>
            {formData.device === 'Accessories' && (
              <InputLabelContainer>
                <label>
                  {t('ACCESSORY_TYPE')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <select
                  className="selectoption largeSelectOption"
                  name="accessoryType"
                  value={formData.accessoryType}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select Accessory Type</option>
                  <option value="Keyboard">Keyboard</option>
                  <option value="Cable">Cable</option>
                  <option value="Headset">Headset</option>
                  <option value="Mouse">Mouse</option>
                  <option value="USB_sticks">USB sticks</option>
                </select>
              </InputLabelContainer>
            )}
            <InputLabelContainer>
              <label>
                {t('TYPE')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <select
                className="selectoption largeSelectOption"
                name="type"
                value={formData.type}
                onChange={handleChange}
                required
              >
                <option value="">{t('SELECT_TYPE')}</option>
                <option value="New">{t('NEW')}</option>
                <option value="Old">{t('OLD')}</option>
              </select>
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('SPECIFICATIONS')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <TextInput
                type="text"
                name="specifications"
                placeholder={t('EXAMPLE_16_INCH_DISPLAY')}
                className="largeInput"
                value={formData.specifications}
                onChange={handleChange}
                required
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('AVAILABILITY')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <select
                className="selectoption largeSelectOption"
                name="availability"
                value={formData.availability}
                onChange={handleChange}
                required
              >
                <option value="">{t('SELECT_AVAILABILITY')}</option>
                <option value="Yes">{t('YES')}</option>
                <option value="No">{t('NO')}</option>
              </select>
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('DATE_OF_PURCHASE')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <span ref={calendarFromRef} className="calendarField">
                <TextInput
                  type="text"
                  placeholder={t('ENTER_DATE')}
                  name="dateOfPurchase"
                  value={dateOfPurchase ? formatDate(dateOfPurchase) : ''}
                  onFocus={() => handleCalenderOpen(true)}
                  required
                  autoComplete="off"
                />
                <span
                  className="iconArea"
                  onClick={() => handleCalenderOpen(true)}
                >
                  <CalenderIconDark />
                </span>
                <div className="calendarSpace" ref={calendarFromRef}>
                  {isCalenderOpen && (
                    <Calendar
                      title={t('PURCHASE_DATE')}
                      handleDateInput={(selectedDate) => {
                        if (selectedDate instanceof Date) {
                          setDateOfPurchase(selectedDate);
                          setFormData((prevState) => ({
                            ...prevState,
                            dateOfPurchase: selectedDate,
                          }));
                        }
                        handleCalenderOpen(false);
                      }}
                      selectedDate={dateOfPurchase}
                      maxDate={new Date()}
                      handleCalenderChange={() => { }}
                    />
                  )}
                </div>
              </span>
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('PRICE')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <TextInput
                type="text"
                name="price"
                value={formData.price}
                onChange={handleChange}
                autoComplete="off"
                onKeyDown={(event) => {
                  const allowedCharacters = /^[0-9.]+$/;
                  if (
                    !allowedCharacters.test(event.key) &&
                    event.key !== 'ArrowLeft' &&
                    event.key !== 'ArrowRight' &&
                    event.key !== 'Backspace'
                  ) {
                    event.preventDefault();
                  }
                  if (event.key === 'e') {
                    event.preventDefault();
                  }
                }}
                placeholder={t('ENTER_PRICE')}
                required
              />
            </InputLabelContainer>
          </div>
          <div>
            <InputLabelContainer>
              <label>
                {t('PROVIDER')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <select
                className="selectoption largeSelectOption"
                name="provider"
                value={formData.provider}
                onChange={handleChange}
                required
              >
                <option value="">{t('SELECT_INVENTORY_PROVIDER')}</option>
                {props.inventoryProviders.values?.map((inventoryProvider) => (
                  <option key={inventoryProvider.value} value={inventoryProvider.value}>
                    {inventoryProvider.value}
                  </option>
                ))}
              </select>
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('MODEL')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <TextInput
                type="text"
                name="model"
                placeholder={t('EXAMPLE_M3_PRO')}
                className="largeInput"
                value={formData.model}
                onChange={handleChange}
                required
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                OS
                {['Desktop', 'MUSIC_SYSTEM', 'Printer', 'Accessories'].includes(
                  formData.device
                ) ? null : (
                  <ValidationText className="star">*</ValidationText>
                )}
              </label>
              <TextInput
                type="text"
                name="os"
                placeholder={t('EXAMPLE_MAC_OS')}
                value={formData.os ? formData.os : ''}
                className="largeInput"
                onChange={handleChange}
                disabled={
                  !(
                    formData.device === 'Desktops' ||
                    formData.device === 'Laptops' ||
                    formData.device === 'Mobiles' ||
                    formData.device === 'Tablets' ||
                    formData.device === 'Desktop' ||
                    formData.device === 'Laptop' ||
                    formData.device === 'Mobile' ||
                    formData.device === 'Tablet'
                  )
                }

                required={
                  ![
                    'Printer',
                    'MUSIC_SYSTEM',
                    'Desktop',
                    'Accessories',
                  ].includes(formData.device)
                }
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('RAM')}
                {['Desktop', 'MUSIC_SYSTEM', 'Printer', 'Accessories'].includes(
                  formData.device
                ) ? null : (
                  <ValidationText className="star">*</ValidationText>
                )}
              </label>
              <TextInput
                type="text"
                placeholder={t('ENTER_IN_GB')}
                name="ram"
                value={formData.ram ? formData.ram : ''}
                className="largeInput"
                onChange={handleChange}
                disabled={formData.device === 'Accessories'}
                autoComplete="off"
                required={
                  ![
                    'Printer',
                    'Desktop',
                    'MUSIC_SYSTEM',
                    'Accessories',
                  ].includes(formData.device)
                }
                onKeyDown={(event) => {
                  const allowedCharacters = /^[0-9.-]+$/;
                  if (
                    !allowedCharacters.test(event.key) &&
                    event.key !== 'Backspace'
                  ) {
                    event.preventDefault();
                  }
                  if (event.key === 'e') {
                    event.preventDefault();
                  }
                }}
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('PRODUCT_ID_SERIAL_NUMBER')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <TextInput
                type="text"
                name="productId"
                placeholder={t('ENTER_PRODUCT_ID_OF_DEVICE')}
                className="largeInput"
                value={formData.productId}
                onChange={handleChange}
                required
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>{t('COMMENTS')}</label>
              <TextInput
                type="text"
                name="comments"
                value={formData.comments}
                className="largeInput"
                placeholder={t('TYPE_YOUR_COMMENTS_HERE_OPTIONAL')}
                onChange={handleChange}
              />
            </InputLabelContainer>
          </div>
        </div>
        <div className="formButtons">
          <Button onClick={props.handleClose}>{t('CANCEL')}</Button>
          <Button className="submit">{t('SUBMIT')}</Button>
        </div>
      </ExpenseAddFormMainContainer>
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
  );
};

export default AddInventoryForm;
