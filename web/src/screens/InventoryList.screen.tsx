import { useEffect, useRef, useState } from 'react';
import {
  DisplayFilters,
  ExpenseHeading,
  ExpenseTitle,
  FilterSection,
  StyledDiv,
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { CalenderIcon, DeleteIcon } from '../svgs/DocumentTabSvgs.svg';
import { InventoryListAction } from '../components/reusableComponents/InventoryListAction.component';
import { DeviceDetails } from '../entities/InventoryEntity';
import PreviewInventoryForm from '../components/directComponents/PreviewInventory.component';
import EditInventoryForm from '../components/directComponents/EditInventory.component';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { EditIcon } from '../svgs/ExpenseListSvgs.svg';
import { useUser } from '../context/UserContext';
import { INVENTORY_MODULE } from '../constants/PermissionConstants';
import { capitalizeFirstLetter, removeUnderScore } from '../utils/stringUtils';
import { formatDate } from '../utils/dateFormatter';
import { hasPermission } from '../utils/permissionCheck';
import { keyPressFind } from '../service/keyboardShortcuts/shortcutValidator';
import Pagination from '../components/directComponents/Pagination.component';
import { inventoryOptions } from '../components/reusableComponents/InventoryEnums.component';
import { useTranslation } from 'react-i18next';
import { OrganizationValues } from '../entities/OrgValueEntity';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';

interface Inventory extends DeviceDetails {
  deviceNumber: string;
}

interface Props {
  inventoryList: Inventory[];
  updateInventoryList: () => void;
  isLoading: boolean;
  currentPage: number;
  itemsPerPage: number;
  totalSize: number;
  totalPages: number;
  deviceFilter: string | undefined;
  availabilityFilter: string | undefined;
  providerFilter: string | undefined;
  isShowFilters: boolean;
  handlePageChange: (page: number) => void;
  handleItemsPerPage: (page: number) => void;
  onDeviceChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  onAvailabilityChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  onProviderChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
  clearFilters: (filterName: string) => void;
  selectedFiltersText: () => React.ReactNode;
  deviceTypes: OrganizationValues;
  inventoryProviders: OrganizationValues;
}

const InventoryList = ({
  inventoryList,
  updateInventoryList,
  isLoading,
  currentPage,
  itemsPerPage,
  totalPages,
  totalSize,
  deviceFilter,
  availabilityFilter,
  providerFilter,
  onDeviceChange,
  onAvailabilityChange,
  onProviderChange,
  handleItemsPerPage,
  handlePageChange,
  clearFilters,
  isShowFilters,
  selectedFiltersText,
  deviceTypes,
  inventoryProviders,
}: Props) => {
  const { user } = useUser();
  const Actions = [
    { title: 'Edit', svg: <EditIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [selectedInventory, setSelectedInventory] = useState<Inventory | null>(
    null
  );

  const [isEditInventoryOpen, setIsEditInventoryOpen] = useState(false);
  const handleEditInventoryOpen = () => {
    setIsEditInventoryOpen(true);
  };

  const hasPreviewPermission =
    user && hasPermission(user, INVENTORY_MODULE.READ_DEVICE);
  const handlePreviewModal = (inventory: Inventory) => {
    hasPreviewPermission && setSelectedInventory(inventory);
  };

  const handleCloseModal = () => {
    setSelectedInventory(null);
    setIsEditInventoryOpen(false);
  };

  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
    updateInventoryList();
  };
  useEffect(() => {
    keyPressFind(searchInputRef);
  }, []);
  const { t } = useTranslation();
  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('INVENTORY_LIST')}</ExpenseTitle>
        </ExpenseHeading>
        <FilterSection>
          <DropdownMenu
            className="largeContainerFil"
            name="Device"
            label={t('DEVICE')}
            options={[
              { label: t('DEVICE'), value: null },
              ...(deviceTypes.values?.map((device) => ({
                label: capitalizeFirstLetter(device.value),
                value: device.value,
              })) || []),
            ]}
            value={deviceFilter}
            onChange={(e) => {
              onDeviceChange({
                target: { value: e },
              } as React.ChangeEvent<HTMLSelectElement>);
              currentPage;
            }}
          />
          <DropdownMenu
            className="largeContainerFil"
            name="Availability"
            label="Availability"
            options={[
              { label: t('Availability'), value: null },
              ...(inventoryOptions.availability?.map((availability) => ({
                label: capitalizeFirstLetter(availability),
                value: availability,
              })) || []),
            ]}
            value={availabilityFilter}
            onChange={(e) => {
              onAvailabilityChange({
                target: { value: e },
              } as React.ChangeEvent<HTMLSelectElement>);
              currentPage;
            }}
          />
          {inventoryProviders && inventoryProviders.values && (
            <DropdownMenu
              label="Provider"
              name="provider"
              id="provider"
              className={'largeContainerFil'}
              value={providerFilter || ''}
              onChange={(selectedValue) => {
                onProviderChange({
                  target: { name: 'provider', value: selectedValue },
                } as React.ChangeEvent<HTMLSelectElement>);
                currentPage;
              }}
              options={[
                { label: t('PROVIDER'), value: '' },
                ...(inventoryProviders?.values || []).map((provider) => ({
                  label: provider.value,
                  value: provider.value,
                })),
              ]}
            />
          )}
        </FilterSection>
        <div className="right">
          <DisplayFilters>
            <label>{t('FILTERS')}: </label>
            {isShowFilters ? (
              <>
                <span className="filterText">{selectedFiltersText()}</span>
                <span
                  onClick={() => clearFilters('clearAll')}
                  className="clearFilters"
                >
                  {t('CLEAR_FILTERS')}
                </span>
              </>
            ) : (
              <span className="noFilters">&nbsp;{t('NO_FILTERS_APPLIED')}</span>
            )}
          </DisplayFilters>
        </div>
        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && inventoryList && inventoryList.length === 0 ? (
            <ZeroEntriesFound
              heading={'THERE_IS_NO_INVENTORY_FOUND'}
              message="YOU_DONT_HAVE_ANY_INVENTORY"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th>{t('DEVICE_NUMBER')}</th>
                  <th>{t('DEVICE')}</th>
                  <th>{t('PROVIDER')}</th>
                  <th>{t('OS')}</th>
                  <th>{t('RAM')}</th>
                  <th>{t('DATE_OF_PURCHASE')}</th>
                  <th>{t('AVAILABILITY')}</th>
                  <th>{t('ACTIONS')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading ? (
                  <>
                    {[...Array(6).keys()].map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(8).keys()].map((cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))}
                  </>
                ) : (
                  <>
                    {inventoryList
                      .slice()
                      .reverse()
                      .map((inventory, index) => (
                        <TableBodyRow key={index}>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {inventory.deviceNumber}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {removeUnderScore(inventory.device)}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {inventory.provider ? inventory.provider : '-'}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {inventory.os ? inventory.os : '-'}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {inventory.ram ? inventory.ram : '-'}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            <span
                              style={{
                                verticalAlign: 'middle',
                                marginRight: '6px',
                              }}
                            >
                              <CalenderIcon />
                            </span>
                            {formatDate(inventory.dateOfPurchase.toString())
                              ? formatDate(inventory.dateOfPurchase.toString())
                              : '-'}
                          </td>
                          <td onClick={() => handlePreviewModal(inventory)}>
                            {inventory.availability
                              ? inventory.availability
                              : '-'}
                          </td>
                          <td>
                            <InventoryListAction
                              options={Actions}
                              currentDevice={inventory}
                              handleSuccessMessage={handleShowSuccessMessage}
                              handleDeleteInventory={updateInventoryList}
                              updateInventoryList={updateInventoryList}
                              deviceTypes={deviceTypes}
                              inventoryProviders={inventoryProviders}
                            />
                          </td>
                        </TableBodyRow>
                      ))}
                  </>
                )}
              </tbody>
            </TableList>
          )}
          {inventoryList.length > 0 && totalPages && (
            <Pagination
              totalPages={totalPages}
              currentPage={currentPage}
              handlePageChange={handlePageChange}
              totalItems={totalSize}
              handleItemsPerPage={handleItemsPerPage}
              itemsPerPage={itemsPerPage}
            />
          )}
        </TableListContainer>
      </StyledDiv>
      {selectedInventory && (
        <CenterModalMain
          heading="INVENTORY_PREVIEW"
          modalClose={handleCloseModal}
          actualContentContainer={
            <PreviewInventoryForm
              formData={selectedInventory}
              handleClose={handleCloseModal}
              handleEdit={handleEditInventoryOpen}
            />
          }
        />
      )}
      {isEditInventoryOpen && selectedInventory && (
        <CenterModalMain
          heading="EDIT_INVENTORY"
          modalClose={handleCloseModal}
          actualContentContainer={
            <EditInventoryForm
              initialFormData={selectedInventory}
              handleClose={handleCloseModal}
              handleSuccessMessage={handleShowSuccessMessage}
              updateInventoryList={updateInventoryList}
              deviceTypes={deviceTypes}
              inventoryProviders={inventoryProviders}
            />
          }
        />
      )}
      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_INVENTORY_HAS_BEEN_UPDATED"
          messageHeading="SUCCESSFULLY_UPDATED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default InventoryList;
