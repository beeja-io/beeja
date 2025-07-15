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

import AddContractForm from '../components/directComponents/AddContractForm.component';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { ContractDetails } from '../entities/ContractEntiy';
import { getAllContracts } from '../service/axiosInstance';

const ContractManagement = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const goToPreviousPage = () => navigate(-1);

  const [contractList, setContractList] = useState<ContractDetails[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

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

  const handleSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => setShowSuccessMessage(false), 2000);
    fetchData();
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Contract Management')}

            {isCreateModalOpen && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Add Contract')}</span>
              </>
            )}

            {!isCreateModalOpen && isContractDetailsRoute && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Contract Details')}</span>
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
              {t('Add New Contract')}
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
            }}
          />
        )}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_CONTRACT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_ADDED"
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default ContractManagement;
