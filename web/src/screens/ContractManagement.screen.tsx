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

const ContractManagement = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    if (isCreateModalOpen) {
      setIsCreateModalOpen(false);
    } else {
      navigate(-1);
    }
  };

  const [contractList, setContractList] = useState<ContractDetails[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [toastData, setToastData] = useState<{
    heading: string;
    body: string;
  } | null>(null);

  const location = useLocation();

  const isContractDetailsRoute = matchPath(
    '/contracts/contract-management/:id',
    location.pathname
  );

  const fetchData = useCallback(async () => {
    try {
      setIsLoading(true);
      const res = await getAllContracts();
      const response = res?.data?.contracts;
      setContractList(response);
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const [selectedContractData, setSelectedContractData] =
    useState<ContractDetails | null>(null);

  const handleOpenCreateModal = useCallback(() => {
    setIsCreateModalOpen(true);
    setSelectedContractData(null);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsCreateModalOpen(false);
    setSelectedContractData(null);
  }, []);

  const handleSuccessMessage = (value: string, type: 'add' | 'edit') => {
    if (type === 'add') {
      setToastData({
        heading: 'Contract_Added_Successfully.',
        body: `New_Contract_has_been_added\nsuccessfully_with "Contract ID: ${value}".`,
      });
    } else if (type === 'edit') {
      setToastData({
        heading: 'Contract_Updated_Successfully.',
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

          {!isCreateModalOpen && (
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
          />
        ) : (
          <Outlet
            context={{
              contractList,
              isLoading,
              updateContractList: fetchData,
              onEditContract: handleEditContract,
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
