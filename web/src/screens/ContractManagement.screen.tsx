import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { matchPath, Outlet, useLocation, useNavigate } from 'react-router-dom';

import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';

import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';

import { toast } from 'sonner';
import AddContractForm from '../components/directComponents/AddContractForm.component';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { ContractDetails } from '../entities/ContractEntiy';
import { getAllContracts, getContractDetails } from '../service/axiosInstance';
import { hasPermission } from '../utils/permissionCheck';
import { CONTRACT_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';

const ContractManagement = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    if (isCreateModalOpen) {
      setIsCreateModalOpen(false);
    } else {
      navigate(-1);
    }
  };

  const [isEditMode, setIsEditMode] = useState(false);
  const [contractList, setContractList] = useState<ContractDetails[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [toastData, setToastData] = useState<{
    heading: string;
    body: string;
  } | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const location = useLocation();

  const isContractDetailsRoute = matchPath(
    '/contracts/contract-management/:id',
    location.pathname
  );

  const fetchData = useCallback(async () => {
    try {
      setIsLoading(true);
      const res = await getAllContracts(currentPage, itemsPerPage);
      setContractList(res.data.contracts || []);
      setTotalItems(res.data.metadata.totalRecords);
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
    }
  }, [currentPage, itemsPerPage]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const [selectedContractData, setSelectedContractData] =
    useState<ContractDetails | null>(null);

  const handleOpenCreateModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(true);
    setSelectedContractData(null);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(false);
    setSelectedContractData(null);
  }, []);

  const handleSuccessMessage = (value: string, type: 'add' | 'edit') => {
    if (type === 'add') {
      setToastData({
        heading: t('CONTRACT_ADDED_SUCCESS_HEADING'),
        body: t('CONTRACT_ADDED_SUCCESS_BODY', {
          id: value,
        }),
      });
    } else if (type === 'edit') {
      setToastData({
        heading: t('Contract Updated Successfully.'),
        body: value,
      });
    }

    setShowSuccessMessage(true);
    setIsCreateModalOpen(false);

    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 3000);

    fetchData();
  };

  const handleEditContract = async (contract: ContractDetails) => {
    try {
      const { data } = await getContractDetails(contract.contractId);
      setSelectedContractData(data);
      setIsEditMode(true);
      setIsCreateModalOpen(true);
    } catch (error) {
      toast.error('Failed_to_fetch_contract_details');
    }
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Contract_Management')}

            {isCreateModalOpen && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">
                  {selectedContractData
                    ? t('Edit_Contract')
                    : t('Add_Contract')}
                </span>
              </>
            )}

            {!isCreateModalOpen && isContractDetailsRoute && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Contract_Details')}</span>
              </>
            )}
          </span>

          {!isCreateModalOpen &&
            !isContractDetailsRoute &&
            user &&
            hasPermission(user, CONTRACT_MODULE.CREATE_CONTRACT) && (
              <Button
                className="submit shadow"
                onClick={handleOpenCreateModal}
                width="216px"
              >
                <AddNewPlusSVG />
                {t('Add_New_Contract')}
              </Button>
            )}
        </ExpenseHeadingSection>

        {isCreateModalOpen ? (
          <AddContractForm
            handleClose={handleCloseModal}
            handleSuccessMessage={handleSuccessMessage}
            initialData={selectedContractData ?? undefined}
            isEditMode={isEditMode}
          />
        ) : (
          <Outlet
            context={{
              contractList,
              isLoading,
              updateContractList: fetchData,
              onEditContract: handleEditContract,
              totalItems,
              currentPage,
              setCurrentPage,
              itemsPerPage,
              setItemsPerPage,
            }}
          />
        )}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && toastData && (
        <ToastMessage
          messageType="success"
          messageHeading={toastData.heading}
          messageBody={toastData.body}
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default ContractManagement;
