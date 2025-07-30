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
    { title: 'Edit', svg: <EditIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
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
    if (action === 'Edit') {
      handleOpenModal();
    } else if (action === 'Delete') {
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
        heading: 'Failed To Fetch Type',
        body: 'Unsuccessfull Fetch Type',
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
      actionType === 'Delete'
        ? settingTypes?.filter((category) => category.index !== indexvalue)
        : actionType === 'Edit'
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

    if (actionType === 'Delete') {
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
          actionType === 'Delete'
            ? `${type} Deleted`
            : actionType === 'Edit'
              ? `${type} Updated`
              : `${type} Created`,
        body:
          actionType === 'Delete'
            ? `${type} has been deleted successfully.`
            : actionType === 'Edit'
              ? `${type} has been updated successfully.`
              : `${type} has been created successfully.`,
      });
      setActionType(null);
    } catch (error) {
      setIsCreatedToastMessage(true);
      setToastMessage({
        type: 'error',
        heading: 'Failed To Create Type',
        body: 'Unsuccessfull create Type',
      });
    }
  };
  useKeyPress(27, () => {
    setConfirmDeleteModal(false);
  });

  return (
    <TabContentMainContainer>
      <TabContentMainContainerHeading>
        <h4>{type}</h4>
        <Button className="submit shadow buttonstyle" onClick={handleOpenModal}>
          <AddNewPlusSVG />
          {t('ADD')} {type}
        </Button>
      </TabContentMainContainerHeading>
      <Hr />
      {/* Table Section */}

      {settingTypes?.length === 0 ? (
        <ZeroEntriesFound heading={`No ${type} found`} />
      ) : (
        <TableContainer>
          <Table>
            <TableHead>
              <tr className="table-row">
                <th className="th-type">{type}</th>
                <th className="th-description">Description</th>
                <th className="th-action">Action</th>
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
            actionType === 'Edit' ? `Edit ${type}` : `Create New ${type}`
          }
          modalLeftButtonText="Cancel"
          modalRightButtonText={actionType === 'Edit' ? 'Update' : 'Create'}
          onChange={handleInputChange}
        >
          <div>
            <InputContainer isValueInvalid={isValueInvalid}>
              <label>
                {type}:<ValidationText className="star">*</ValidationText>
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
            modalHeading="Delete"
            modalContent={`Are you sure want to Delete this ${type}`}
          />
        </span>
      )}
    </TabContentMainContainer>
  );
};
