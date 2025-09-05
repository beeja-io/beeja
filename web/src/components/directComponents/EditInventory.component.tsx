import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  DeviceDetails,
  IUpdateDeviceDetails,
} from '../../entities/InventoryEntity';
import { OrganizationValues } from '../../entities/OrgValueEntity';
import { putInventory } from '../../service/axiosInstance';
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
import ToastMessage from '../reusableComponents/ToastMessage.component';
import DropdownMenu from '../reusableComponents/DropDownMenu.component';

type EditInventoryFormProps = {
  initialFormData: DeviceDetails; // Initial data to populate the form
  handleClose: () => void;
  handleSuccessMessage: () => void;
  updateInventoryList: () => void;
  deviceTypes: OrganizationValues;
  inventoryProviders: OrganizationValues;
};

const EditInventoryForm: React.FC<EditInventoryFormProps> = ({
  initialFormData,
  handleClose,
  handleSuccessMessage,
  updateInventoryList,
  deviceTypes,
  inventoryProviders,
}) => {
  const calendarFromRef = useRef<HTMLDivElement>(null);
  const [formData, setFormData] = useState<DeviceDetails>(initialFormData);
  const { t } = useTranslation();
  const [isCalenderOpen, setIsCalenderOpen] = useState(false);
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string>('');

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

    setFormData((prevState) => {
      let updatedData = { ...prevState, [name]: value };
      if (name === 'device' && !isOsRamEnabled(value)) {
        updatedData = { ...updatedData, os: '', ram: '' };
      }

      return updatedData;
    });

    setDeviceToUpdate((prevState) => {
      let updatedUpdate = { ...prevState, [name]: value };
      if (name === 'device' && !isOsRamEnabled(value)) {
        updatedUpdate = { ...updatedUpdate, os: '', ram: '' };
      }
      return updatedUpdate;
    });
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

    if (initialFormData) {
      const errorMessages = [];

      if (
        initialFormData.device === undefined ||
        initialFormData.device === null ||
        deviceToUpdate.device === ''
      ) {
        errorMessages.push('Device');
      }

      if (
        initialFormData.type === undefined ||
        initialFormData.type === null ||
        deviceToUpdate.type === ''
      ) {
        errorMessages.push('Type');
      }

      if (
        initialFormData.specifications === undefined ||
        initialFormData.specifications === null ||
        deviceToUpdate.specifications === ''
      ) {
        errorMessages.push('Specifications');
      }

      if (
        initialFormData.availability === undefined ||
        initialFormData.availability === null ||
        deviceToUpdate.availability === null ||
        deviceToUpdate.availability === ''
      ) {
        errorMessages.push('Availability');
      }

      if (
        initialFormData.dateOfPurchase === undefined ||
        initialFormData.dateOfPurchase === null
      ) {
        errorMessages.push('Date of Purchase');
      }

      if (
        initialFormData.price === undefined ||
        initialFormData.price === null ||
        deviceToUpdate.price === null ||
        deviceToUpdate.price === ''
      ) {
        errorMessages.push('Price');
      }

      if (
        initialFormData.provider === undefined ||
        initialFormData.provider === null ||
        deviceToUpdate.provider === ''
      ) {
        errorMessages.push('Provider');
      }

      if (
        initialFormData.model === undefined ||
        initialFormData.model === null ||
        deviceToUpdate.model === ''
      ) {
        errorMessages.push('Model');
      }

      if (isOsRamEnabled(deviceToUpdate.device)) {
        if (!deviceToUpdate.os) {
          errorMessages.push('OS');
        }
        if (!deviceToUpdate.ram) {
          errorMessages.push('RAM');
        }
      }

      if (
        initialFormData.productId === undefined ||
        initialFormData.productId === null ||
        deviceToUpdate.productId === ''
      ) {
        errorMessages.push('Product ID/Serial No.');
      }

      if (errorMessages.length > 0) {
        handleShowErrorMessage();
        setErrorMessage('Please fill ' + errorMessages);
        return;
      }

      const formData = new FormData();

      formData.append('device', initialFormData.device);
      formData.append('type', initialFormData.type);
      formData.append('specifications', initialFormData.specifications ?? '');
      formData.append('availability', initialFormData.availability);
      formData.append(
        'price',
        initialFormData.price != null ? initialFormData.price.toString() : ''
      );
      formData.append('provider', initialFormData.provider);
      formData.append('model', initialFormData.model);
      formData.append('os', initialFormData.os ?? '');
      formData.append('ram', initialFormData.ram ?? '');
      if (initialFormData.dateOfPurchase != null) {
        formData.append(
          'dateOfPurchase',
          initialFormData.dateOfPurchase.toString()
        );
      }
      formData.append('productId', initialFormData.productId);
      formData.append('comments', initialFormData.comments ?? '');

      setIsResponseLoading(true);

      const price = Number(deviceToUpdate.price);

      if (!initialFormData.price || price <= 0) {
        setErrorMessage('UPDATED_PRICE_MUST_BE_GREATER_THAN_ZERO');
        handleShowErrorMessage();
        setIsResponseLoading(false);
        return;
      }

      try {
        await putInventory(initialFormData.id, deviceToUpdate);
        handleSuccessMessage();
        handleClose();
        updateInventoryList();
      } catch (error) {
        if (
          axios.isAxiosError(error) &&
          error.response?.data.message === '[value must be greater than 0]'
        ) {
          setErrorMessage('UPDATED_PRICE_MUST_BE_GREATER_THAN_ZERO');
        } else if (
          axios.isAxiosError(error) &&
          error.response?.data.message.startsWith('Product ID already exists')
        ) {
          setErrorMessage('PRODUCT_ID_ALREADY_EXIST');
        } else {
          setErrorMessage('INVENTORY_NOT_UPLOADED');
        }
        setShowErrorMessage(true);
      } finally {
        setIsResponseLoading(false);
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

  useEffect(() => {
    const fixedDevice = initialFormData.device?.toUpperCase?.() || '';
    setFormData({ ...initialFormData, device: fixedDevice });
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

              <DropdownMenu
                name="device"
                className=""
                value={formData.device}
                required={true}
                onChange={(val) =>
                  handleChange({
                    target: {
                      name: 'device',
                      value: val,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>)
                }
                options={[
                  { label: t('SELECT_DEVICE'), value: '' },
                  ...(deviceTypes?.values?.map((device) => ({
                    label: device.value,
                    value: device.value.toUpperCase(),
                  })) ?? []),
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
                  name="accessoryType"
                  className="largeContainerExp"
                  value={formData.accessoryType}
                  required
                  onChange={(val) =>
                    handleChange({
                      target: { value: val },
                    } as React.ChangeEvent<HTMLSelectElement>)
                  }
                  options={[
                    { label: t('SELECT_ACCESSORY_TYPE'), value: '' },
                    { label: 'Keyboard', value: 'KEYBOARD' },
                    { label: 'Cable', value: 'CABLE' },
                    { label: 'Headset', value: 'HEADSET' },
                    { label: 'Mouse', value: 'MOUSE' },
                    { label: 'USB sticks', value: 'USB_STICKS' },
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
                name="type"
                className=""
                value={formData.type}
                required={true}
                onChange={(val) =>
                  handleChange({
                    target: { name: 'type', value: val },
                  } as React.ChangeEvent<HTMLSelectElement>)
                }
                options={[
                  { label: t('SELECT_TYPE'), value: '' },
                  { label: t('NEW'), value: 'NEW' },
                  { label: t('OLD'), value: 'OLD' },
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
                name="availability"
                className=""
                required={true}
                value={formData.availability}
                onChange={(val) =>
                  handleChange({
                    target: { name: 'availability', value: val },
                  } as React.ChangeEvent<HTMLSelectElement>)
                }
                options={[
                  { label: t('SELECT_AVAILABILITY'), value: '' },
                  { label: t('YES'), value: 'YES' },
                  { label: t('NO'), value: 'NO' },
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
              <DropdownMenu
                label="Select Provider"
                name="provider"
                id="provider"
                className=""
                value={formData.provider ?? ''}
                required
                options={[
                  { label: 'Select Provider', value: '' },
                  ...(inventoryProviders?.values || []).map((provider) => ({
                    label: provider.value,
                    value: provider.value,
                  })),
                ]}
                onChange={(val) => {
                  handleChange({
                    target: {
                      name: 'provider',
                      value: val ?? '',
                    },
                  } as React.ChangeEvent<HTMLSelectElement>);
                }}
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
                {t('OS')}
                {isOsRamEnabled(formData.device) && (
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
                disabled={!isOsRamEnabled(formData.device)}
                required={isOsRamEnabled(formData.device)}
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
                name="ram"
                placeholder={t('ENTER_IN_GB')}
                value={formData.ram ? formData.ram : ''}
                className="largeInput"
                onChange={handleChange}
                disabled={!isOsRamEnabled(formData.device)}
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
