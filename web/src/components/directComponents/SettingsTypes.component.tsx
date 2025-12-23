import { useState, useEffect } from 'react';
import {
  getAllSettingTypes,
  updateSettingType,
} from '../../service/axiosInstance';
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import { InputContainer } from '../../styles/SettingsStyles.style';
import { TableBodyRow } from '../../styles/DocumentTabStyles.style';
import ToastMessage from '../reusableComponents/ToastMessage.component';
import CenterModal from '../reusableComponents/CenterModal.component';
import {
  TabContentMainContainer,
  TabContentMainContainerHeading,
} from '../../styles/MyProfile.style';
import { Hr } from '../../styles/LoanApplicationStyles.style';
import { Button } from '../../styles/CommonStyles.style';
import { AddNewPlusSVG } from '../../svgs/EmployeeListSvgs.svg';
import { EditIcon, DeleteIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  Table,
  TableContainer,
  TableHead,
} from '../../styles/TableStyles.style';
import { ExpenseTypeAction } from '../reusableComponents/ExpenseTyeAction';
import CenterModalExpense from '../reusableComponents/CenterModalExpense.component';
import { OrgValues } from '../../entities/OrgDefaultsEntity';
import { useUser } from '../../context/UserContext';
import { useTranslation } from 'react-i18next';
import { AlertISVG } from '../../svgs/CommonSvgs.svs';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import ZeroEntriesFound from '../reusableComponents/ZeroEntriesFound.compoment';

export const SettingsTypes = ({
  keyvalue,
  type,
}: {
  keyvalue: string;
  type: string;
}) => {
  const { t } = useTranslation();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [actionType, setActionType] = useState<string | null>(null);
  const [indexvalue, setIndexvalue] = useState<number>();
  const [newSettingType, setNewSettingType] = useState<OrgValues>({
    value: '',
    description: '',
  });
  const [settingTypes, setSettingTypes] = useState<
    { orgValues: OrgValues; index: number }[] | null
  >(null);
  const [isValueInvalid, setIsValueInvalid] = useState(false);
  const [toastMessage, setToastMessage] = useState<{
    type: 'success' | 'error';
    heading: string;
    body: string;
  } | null>(null);
  const [isCreatedToastMessage, setIsCreatedToastMessage] = useState(false);
  const Actions = [
    { title: 'EDIT', label: t('EDIT'), svg: <EditIcon /> },
    { title: 'DELETE', label: t('DELETE'), svg: <DeleteIcon /> },
  ];
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const handleOpenModal = () => setIsCreateModalOpen(true);
  const { user } = useUser();
  const handleDeleteModal = () => {
    setConfirmDeleteModal(!confirmDeleteModal);
  };

  const handleIsCreatedToastMessage = () => {
    setIsCreatedToastMessage(!isCreatedToastMessage);
  };

  const handleCloseModal = () => {
    setIsCreateModalOpen(false);
    setIsValueInvalid(false);
    setNewSettingType({
      value: '',
      description: '',
    });
  };
  const handleAction = (
    action: string,
    expense: { orgValues: OrgValues; index: number }
  ) => {
    setActionType(action);
    setNewSettingType(expense.orgValues);
    setIndexvalue(expense.index);
    if (action === 'EDIT') {
      handleOpenModal();
    } else if (action === 'DELETE') {
      handleDeleteModal();
    }
  };

  const handleInputChange = (field: keyof OrgValues, value: string) => {
    setNewSettingType((prev) => ({
      ...prev, // Retain the existing fields
      [field]: value, // Update the specific field dynamically
    }));
  };

  useEffect(() => {
    fetchSettingsTypes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchSettingsTypes = async () => {
    try {
      const response = await getAllSettingTypes(keyvalue);
      if (response.data.values) {
        const updatedValues = response.data.values.map(
          (item: OrgValues, index: number) => ({
            orgValues: item,
            index,
          })
        );
        setSettingTypes(updatedValues);
      }
    } catch (error) {
      setIsCreatedToastMessage(true);
      setToastMessage({
        type: 'error',
        heading: t('FETCH_TYPE_FAILED_TITLE'),
        body: t('FETCH_TYPE_FAILED_BODY'),
      });
    }
  };

  let updatedSettingTypes;
  const handleSubmitButton = async (e?: React.FormEvent) => {
    e?.preventDefault();
    if (newSettingType.value.trim() === '') {
      setIsValueInvalid(true);
      return;
    }
    updatedSettingTypes =
      actionType === 'DELETE'
        ? settingTypes?.filter((category) => category.index !== indexvalue)
        : actionType === 'EDIT'
          ? settingTypes?.map((category) =>
              category.index === indexvalue
                ? { orgValues: newSettingType, index: category.index }
                : category
            )
          : [
              ...(settingTypes || []),
              {
                orgValues: newSettingType,
                index: settingTypes?.length || 0,
              },
            ];

    if (actionType === 'DELETE') {
      handleDeleteModal();
    }
    const data = {
      organizationId: user?.organizations.id,
      key: keyvalue,
      values: updatedSettingTypes?.map((category) => ({
        value: category.orgValues.value,
        description: category.orgValues.description,
      })),
    };
    try {
      await updateSettingType(data);
      handleCloseModal();
      fetchSettingsTypes();
      setIsCreatedToastMessage(true);
      setToastMessage({
        type: 'success',
        heading:
          actionType === 'DELETE'
            ? t('TYPE_DELETED_TITLE', { type: t(type) })
            : actionType === 'EDIT'
              ? t('TYPE_UPDATED_TITLE', { type: t(type) })
              : t('TYPE_CREATED_TITLE', { type: t(type) }),
        body:
          actionType === 'DELETE'
            ? t('TYPE_DELETED_SUCCESS', { type: t(type) })
            : actionType === 'EDIT'
              ? t('TYPE_UPDATED_SUCCESS', { type: t(type) })
              : t('TYPE_CREATED_SUCCESS', { type: t(type) }),
      });
      setActionType(null);
    } catch (error) {
      setIsCreatedToastMessage(true);
      setToastMessage({
        type: 'error',
        heading: t('FETCH_TYPE_FAILED_TITLE'),
        body: t('FETCH_TYPE_FAILED_BODY'),
      });
    }
  };
  useKeyPress(27, () => {
    setConfirmDeleteModal(false);
  });

  return (
    <TabContentMainContainer>
      <TabContentMainContainerHeading>
        <h4>{t(type)}</h4>
        <Button className="submit shadow buttonstyle" onClick={handleOpenModal}>
          <AddNewPlusSVG />
          {t('ADD')} {t(type)}
        </Button>
      </TabContentMainContainerHeading>
      <Hr />
      {/* Table Section */}

      {settingTypes?.length === 0 ? (
        <ZeroEntriesFound heading={t('NO_TYPE_FOUND', { type: t(type) })} />
      ) : (
        <TableContainer>
          <Table>
            <TableHead>
              <tr className="table-row">
                <th className="th-type">{t(type)}</th>
                <th className="th-description">{t('DESCRIPTION')}</th>
                <th className="th-action">{t('ACTION')}</th>
              </tr>
            </TableHead>
            <tbody>
              {settingTypes
                ?.filter((settingTypes) => settingTypes.orgValues.value)
                .sort((a, b) =>
                  a.orgValues.value.localeCompare(b.orgValues.value)
                )
                .map((settingTypes) => (
                  <TableBodyRow key={settingTypes.index}>
                    <td>{settingTypes.orgValues.value}</td>
                    <td>{settingTypes.orgValues.description}</td>
                    <td>
                      <ExpenseTypeAction
                        options={Actions}
                        fetchExpenses={fetchSettingsTypes}
                        currentExpense={settingTypes}
                        onActionClick={handleAction}
                      />
                    </td>
                  </TableBodyRow>
                ))}
            </tbody>
          </Table>
        </TableContainer>
      )}
      {toastMessage && isCreatedToastMessage && (
        <ToastMessage
          messageType={toastMessage.type}
          messageHeading={toastMessage.heading}
          messageBody={toastMessage.body}
          handleClose={handleIsCreatedToastMessage}
        />
      )}

      {/* Modal */}
      {isCreateModalOpen && (
        <CenterModalExpense
          handleModalClose={handleCloseModal}
          handleModalSubmit={handleSubmitButton}
          modalHeading={
            actionType === 'EDIT'
              ? `${t('EDIT')} ${t(type)}`
              : `${t('CREATE_NEW')} ${t(type)}`
          }
          modalLeftButtonText={t('CANCEL')}
          modalRightButtonText={
            actionType === 'EDIT' ? t('UPDATE') : t('CREATE')
          }
          onChange={handleInputChange}
        >
          <div>
            <InputContainer isValueInvalid={isValueInvalid}>
              <label>
                {t(type)}:<ValidationText className="star">*</ValidationText>
              </label>
              <input
                type="text"
                value={newSettingType.value}
                onChange={(e) => handleInputChange('value', e.target.value)}
              />
            </InputContainer>
            {isValueInvalid && (
              <div style={{ paddingLeft: '180px' }}>
                <ValidationText>
                  <AlertISVG />
                  {t('THE_FIELD_IS_MANDATORY')}{' '}
                </ValidationText>
              </div>
            )}
          </div>
          <div>
            <InputContainer>
              <label>{t('DESCRIPTION')}:</label>
              <input
                type="text"
                value={newSettingType.description}
                onChange={(e) =>
                  handleInputChange('description', e.target.value)
                }
              />
            </InputContainer>
          </div>
        </CenterModalExpense>
      )}

      {confirmDeleteModal && (
        <span style={{ cursor: 'default' }}>
          <CenterModal
            handleModalClose={handleDeleteModal}
            handleModalLeftButtonClick={handleDeleteModal}
            handleModalSubmit={handleSubmitButton}
            modalHeading={t('DELETE')}
            modalContent={t('DELETE_CONFIRMATION', { type: t(type) })}
          />
        </span>
      )}
    </TabContentMainContainer>
  );
};
