import axios from 'axios';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { DeviceDetails } from '../../entities/InventoryEntity';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import { postInventory } from '../../service/axiosInstance';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import useKeyCtrl from '../../service/keyboardShortcuts/onKeySave';
import { Button } from '../../styles/CommonStyles.style';
import {
  InputLabelContainer,
  TextInput,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import { ExpenseAddFormMainContainer } from '../../styles/ExpenseManagementStyles.style';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import Calendar from '../reusableComponents/Calendar.component';
import { Availability } from '../reusableComponents/InventoryEnums.component';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import DropdownMenu from '../reusableComponents/DropDownMenu.component';

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
  const isOsRamEnabled = (device: string | null | undefined) => {
    const osRamDevices = [
      'DESKTOP',
      'LAPTOP',
      'MOBILE',
      'TABLET',
      'DESKTOPS',
      'LAPTOPS',
      'MOBILES',
      'TABLETS',
    ];
    return device ? osRamDevices.includes(device.toUpperCase()) : false;
  };

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
      setFormData((prevState) => {
        let updatedData = { ...prevState, [name]: value };

        if (name === 'device' && !isOsRamEnabled(value)) {
          updatedData = { ...updatedData, os: '', ram: '' };
        }

        return updatedData;
      });
    }
  };

  const handleSubmitData = async (event: { preventDefault: () => void }) => {
    event.preventDefault();
    if (formData) {
      const errorMessages = [];

      if (
        formData.device === undefined ||
        formData.device === null ||
        formData.device === ''
      ) {
        errorMessages.push(t('DEVICE'));
      }
      if (
        formData.type === undefined ||
        formData.type === null ||
        formData.type === ''
      ) {
        errorMessages.push(t('TYPE'));
      }
      if (
        formData.specifications === undefined ||
        formData.specifications === null ||
        formData.specifications === ''
      ) {
        errorMessages.push(t('SPECIFICATIONS'));
      }
      if (
        formData.availability === undefined ||
        formData.availability === null
      ) {
        errorMessages.push(t('AVAILABILITY'));
      }
      if (
        formData.dateOfPurchase === undefined ||
        formData.dateOfPurchase === null
      ) {
        errorMessages.push(t('DATE_OF_PURCHASE'));
      }
      if (formData.price === undefined || formData.price === null) {
        errorMessages.push(t('PRICE'));
      }
      if (
        formData.provider === undefined ||
        formData.provider === null ||
        formData.provider === ''
      ) {
        errorMessages.push(t('PROVIDER'));
      }
      if (
        formData.model === undefined ||
        formData.model === null ||
        formData.model === ''
      ) {
        errorMessages.push(t('MODEL'));
      }
      if (isOsRamEnabled(formData.device)) {
        if (!formData.os) {
          errorMessages.push(t('OS'));
        }
        if (!formData.ram) {
          errorMessages.push(t('RAM'));
        }
      }

      if (
        formData.productId === undefined ||
        formData.productId === null ||
        formData.productId === ''
      ) {
        errorMessages.push(t('PRODUCT_ID_SERIAL_NUMBER'));
      }

      if (errorMessages.length > 0) {
        setErrorMessage('Please fill ' + errorMessages);
        handleShowErrorMessage();
        return;
      }
      const productIdPattern = /^[A-Za-z0-9-]{8,20}$/;

      if (!productIdPattern.test(formData.productId || '')) {
        setErrorMessage(t('PRODUCT_ID_VALIDATION_ERROR'));
        handleShowErrorMessage();
        return;
      }
      const form = new FormData();
      form.append('device', formData.device);
      form.append('type', formData.type);
      form.append('specifications', formData.specifications ?? '');
      form.append('availability', formData.availability);
      form.append(
        'price',
        formData.price != null ? formData.price.toString() : ''
      );
      form.append('provider', formData.provider);
      form.append('model', formData.model);
      form.append('os', formData.os ?? '');
      form.append('ram', formData.ram ?? '');
      if (formData.dateOfPurchase != null) {
        form.append('dateOfPurchase', formData.dateOfPurchase.toString());
      }
      form.append('productId', formData.productId);
      if (formData.comments !== undefined) {
        form.append('comments', formData.comments);
      } else {
        form.append('comments', 'null');
      }

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
          error.response?.data.message === '[value must be greater than 0]'
        ) {
          setErrorMessage(
            'INVENTORY_PRICE_CANNOT_BE_ZERO_PLEASE_ENTER_A_VALID_PRICE'
          );
        } else if (
          axios.isAxiosError(error) &&
          error.response?.data.message.startsWith('Product ID already exists')
        ) {
          setErrorMessage('PRODUCT_ID_ALREADY_EXIST');
        } else {
          setErrorMessage('INVENTORY_NOT_UPLOADED');
        }
        setIsResponseLoading(false);
        handleShowErrorMessage();
      }
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
              <DropdownMenu
                label={t('SELECT_DEVICE')}
                name="device"
                value={formData.device}
                required={true}
                onChange={(e) => {
                  handleChange({
                    target: {
                      name: 'device',
                      value: e,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>);
                }}
                options={[
                  { label: t('SELECT_DEVICE'), value: '' },
                  ...(props.deviceTypes.values || []).map((deviceType) => ({
                    label: deviceType.value,
                    value: deviceType.value,
                  })),
                ]}
              />
            </InputLabelContainer>
            {formData.device === 'Accessories' && (
              <InputLabelContainer>
                <label>
                  {t('ACCESSORY_TYPE')}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <DropdownMenu
                  label={t('SELECT_ACCESSORY_TYPE')}
                  name="accessoryType"
                  value={formData.accessoryType}
                  onChange={(val) => {
                    handleChange({
                      target: {
                        name: 'accessoryType',
                        value: val,
                      },
                    } as React.ChangeEvent<HTMLSelectElement>);
                  }}
                  options={[
                    { label: t('SELECT_ACCESSORY_TYPE'), value: '' },
                    { label: t('KEYBOARD'), value: 'Keyboard' },
                    { label: t('CABLE'), value: 'Cable' },
                    { label: t('HEADSET'), value: 'Headset' },
                    { label: t('MOUSE'), value: 'Mouse' },
                    { label: t('USB_STICKS'), value: 'USB_sticks' },
                  ]}
                />
              </InputLabelContainer>
            )}
            <InputLabelContainer>
              <label>
                {t('TYPE')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <DropdownMenu
                label={t('SELECT_TYPE')}
                name="type"
                value={formData.type}
                required={true}
                onChange={(val) => {
                  handleChange({
                    target: {
                      name: 'type',
                      value: val,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>);
                }}
                options={[
                  { label: t('SELECT_TYPE'), value: '' },
                  { label: t('NEW'), value: 'New' },
                  { label: t('OLD'), value: 'Old' },
                ]}
              />
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
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('AVAILABILITY')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <DropdownMenu
                label={t('SELECT_AVAILABILITY')}
                name="availability"
                value={formData.availability}
                required={true}
                onChange={(val) => {
                  handleChange({
                    target: {
                      name: 'availability',
                      value: val,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>);
                }}
                options={[
                  { label: t('SELECT_AVAILABILITY'), value: '' },
                  { label: t('YES'), value: 'Yes' },
                  { label: t('NO'), value: 'No' },
                ]}
              />
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
              />
            </InputLabelContainer>
          </div>
          <div>
            <InputLabelContainer>
              <label>
                {t('PROVIDER')}
                <ValidationText className="star">*</ValidationText>
              </label>
              <DropdownMenu
                label={t('SELECT_INVENTORY_PROVIDER')}
                name="provider"
                value={formData.provider}
                required={true}
                onChange={(val) => {
                  handleChange({
                    target: {
                      name: 'provider',
                      value: val,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>);
                }}
                options={[
                  { label: t('SELECT_INVENTORY_PROVIDER'), value: '' },
                  ...(props.inventoryProviders.values?.map(
                    (inventoryProvider) => ({
                      label: inventoryProvider.value,
                      value: inventoryProvider.value,
                    })
                  ) || []),
                ]}
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
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                OS
                {isOsRamEnabled(formData.device) && (
                  <ValidationText className="star">*</ValidationText>
                )}
              </label>
              <TextInput
                type="text"
                name="os"
                placeholder={t('EXAMPLE_MAC_OS')}
                value={formData.os || ''}
                onChange={handleChange}
                disabled={Boolean(
                  formData.device &&
                    formData.device !== 'Select Device' &&
                    !isOsRamEnabled(formData.device)
                )}
                required={isOsRamEnabled(formData.device)}
                className="largeInput"
              />
            </InputLabelContainer>
            <InputLabelContainer>
              <label>
                {t('RAM')}
                {isOsRamEnabled(formData.device) && (
                  <ValidationText className="star">*</ValidationText>
                )}
              </label>
              <TextInput
                type="text"
                placeholder={t('ENTER_IN_GB')}
                name="ram"
                value={formData.ram || ''}
                className="largeInput"
                onChange={handleChange}
                autoComplete="off"
                disabled={Boolean(
                  formData.device &&
                    formData.device !== 'Select Device' &&
                    !isOsRamEnabled(formData.device)
                )}
                required={isOsRamEnabled(formData.device)}
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
