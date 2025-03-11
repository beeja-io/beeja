import React, { useEffect, useRef, useState } from 'react';
import { Button } from '../../styles/CommonStyles.style';
import {
  InputLabelContainer,
  TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import Calendar from '../reusableComponents/Calendar.component';
import { putInventory } from '../../service/axiosInstance';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import {
  DeviceDetails,
  IUpdateDeviceDetails,
} from '../../entities/InventoryEntity';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import axios from 'axios';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import { useTranslation } from 'react-i18next';

type EditInventoryFormProps = {
  initialFormData: DeviceDetails; // Initial data to populate the form
  handleClose: () => void;
  handleSuccessMessage: () => void;
  updateInventoryList: () => void;
  deviceTypes: OrganizationValues;
};

const EditInventoryForm: React.FC<EditInventoryFormProps> = ({
  initialFormData,
  handleClose,
  handleSuccessMessage,
  updateInventoryList,
  deviceTypes,
}) => {
  const calendarFromRef = useRef<HTMLDivElement>(null);
  const [formData, setFormData] = useState<DeviceDetails>(initialFormData);
  const { t } = useTranslation();
  const [isCalenderOpen, setIsCalenderOpen] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string>('');

  const [deviceToUpdate, setDeviceToUpdate] = useState<IUpdateDeviceDetails>(
    {} as IUpdateDeviceDetails
  );

  const handleShowErrorMessage = () => {
    setShowErrorMessage(!showErrorMessage);
    setTimeout(() => {
      setShowErrorMessage(false);
    }, 2000);
  };

  const handleCalenderOpen = (isOpen: boolean) => {
    setIsCalenderOpen(isOpen);
  };

  const formatDate = (date: Date): string => {
    const options: Intl.DateTimeFormatOptions = {
      month: 'short',
      day: '2-digit',
      year: 'numeric',
    };
    return new Intl.DateTimeFormat('en-US', options).format(date);
  };

  const [dateOfPurchase, setDateOfPurchase] = useState<Date | null>(
    initialFormData.dateOfPurchase
      ? new Date(initialFormData.dateOfPurchase)
      : null
  );

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = event.target;
    if (
      (name == 'device' && value == 'MUSIC_SYSTEM') ||
      (name == 'device' && value == 'ACCESSORIES')
    ) {
      setFormData((prevState) => ({
        ...prevState,
        ram: '',
        os: '',
      }));
    }
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
    if (
      (name == 'device' && value == 'MUSIC_SYSTEM') ||
      (name == 'device' && value == 'ACCESSORIES')
    ) {
      setDeviceToUpdate((prevState) => ({
        ...prevState,
        ram: '',
        os: '',
      }));
    }
    setDeviceToUpdate((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };
  useEffect(() => {
    if (initialFormData.productId == deviceToUpdate.productId) {
      setDeviceToUpdate((prevState) => ({
        ...prevState,
        productId: null,
      }));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formData.productId]);

  const handleSubmitData = async (event: { preventDefault: () => void }) => {
    event.preventDefault();
    setIsResponseLoading(true);
    try {
      await putInventory(initialFormData.id, deviceToUpdate);
      handleSuccessMessage();
      handleClose();
      updateInventoryList();
    } catch (error) {
      if (
        axios.isAxiosError(error) &&
        error.response?.data.startsWith('Product ID already exists')
      ) {
        setErrorMessage('PRODUCT_ID_ALREADY_EXIST');
      } else {
        setErrorMessage('INVENTORY_NOT_UPLOADED');
      }
      setShowErrorMessage(true);
    } finally {
      setIsResponseLoading(false);
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

  useEffect(() => {
    setFormData(initialFormData);
    setDateOfPurchase(
      initialFormData.dateOfPurchase
        ? new Date(initialFormData.dateOfPurchase)
        : null
    );
  }, [initialFormData]);

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
                {deviceTypes?.values?.map((device) => (
                  <option key={device.value} value={device.value}>
                    {device.description || device.value}
                  </option>
                ))}
              </select>
            </InputLabelContainer>

            {formData.device === "Accessories" && (
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
                  <option value="">{t('SELECT_ACCESSORY_TYPE')}</option>
                  <option value="KEYBOARD">Keyboard</option>
                  <option value="CABLE">Cable</option>
                  <option value="HEADSET">Headset</option>
                  <option value="MOUSE">Mouse</option>
                  <option value="USB_STICKS">USB sticks</option>
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
                <option value="NEW">{t('NEW')}</option>
                <option value="OLD">{t('OLD')}</option>
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
                <option value="YES">{t('YES')}</option>
                <option value="NO">{t('NO')}</option>
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
                          setDeviceToUpdate((prevState) => ({
                            ...prevState,
                            dateOfPurchase: selectedDate,
                          }));
                        }
                        handleCalenderOpen(false);
                      }}
                      selectedDate={dateOfPurchase}
                      maxDate={new Date()}
                      handleCalenderChange={() => {}}
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
                placeholder={t('ENTER_PRICE')}
                required
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
              />
            </InputLabelContainer>
          </div>
          <div>
            <InputLabelContainer>
              <label>
                {t('PROVIDER')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <TextInput
                type="text"
                name="provider"
                placeholder={t('EXAMPLE_APPLE')}
                className="largeInput"
                value={formData.provider}
                onChange={handleChange}
                required
              />
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
                {t('OS')}
                {['DESKTOP', 'MUSIC_SYSTEM', 'PRINTER', 'ACCESSORIES'].includes(
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
                  formData.device !== 'Desktops' ||
                  formData.device !== 'Laptops' ||
                  formData.device !== 'Mobiles' ||
                  formData.device !== 'Tablets' ||
                  formData.device !== 'Desktop' ||
                  formData.device !== 'Laptop' ||
                  formData.device !== 'Mobile' ||
                  formData.device !== 'Tablet'
                }
                required={
                  ![
                    'PRINTER',
                    'MUSIC_SYSTEM',
                    'DESKTOP',
                    'Accessories',
                  ].includes(formData.device)
                }
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('RAM')}
                {['DESKTOP', 'MUSIC_SYSTEM', 'PRINTER', 'ACCESSORIES'].includes(
                  formData.device
                ) ? null : (
                  <ValidationText className="star">*</ValidationText>
                )}
              </label>
              <TextInput
                type="text"
                name="ram"
                placeholder={t('ENTER_IN_GB')}
                value={formData.ram ? formData.ram : ''}
                className="largeInput"
                onChange={handleChange}
                disabled={formData.device === 'Accessories'}
                required={
                  ![
                    'PRINTER',
                    'DESKTOP',
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
          <Button onClick={handleClose} fontSize="16px" width="145px">
            {t('CANCEL')}
          </Button>
          <Button className="submit" fontSize="16px" width="145px">
            {t('UPDATE')}
          </Button>
        </div>
      </ExpenseAddFormMainContainer>
      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageBody={errorMessage}
          messageHeading="UPDATE_UNSUCCESSFUL"
          handleClose={handleShowErrorMessage}
        />
      )}
      {isResponseLoading && <SpinAnimation />}
    </>
  );
};

export default EditInventoryForm;
