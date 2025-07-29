import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AddClientForm from '../components/directComponents/AddClientForm.component';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';

import { useTranslation } from 'react-i18next';
import { matchPath, Outlet, useLocation } from 'react-router-dom';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { Client, ClientDetails } from '../entities/ClientEntity';
import { getAllClient, getClient } from '../service/axiosInstance';

const ClientManagement = () => {
  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedClientData, setSelectedClientData] =
    useState<ClientDetails | null>(null);
  const [editLoading, setEditLoading] = useState(false);
  const handleOpenCreateModal = useCallback(() => {
    setIsEditMode(false);
    setSelectedClientData(null);
    setIsCreateModalOpen(true);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsCreateModalOpen(false);
    setIsEditMode(false);
    setSelectedClientData(null);
  }, []);

  const onEditClient = async (client: Client) => {
    try {
      setEditLoading(true);
      const res = await getClient(client.clientId);
      setSelectedClientData(res.data);
      setIsEditMode(true);
      setIsCreateModalOpen(true);
    } catch (error) {
      toast.error(t('Failed to fetch client details. Please try again.'));
    } finally {
      setEditLoading(false);
    }
  };

  const handleSuccessMessage = () => {
    handleShowSuccessMessage();
    setIsCreateModalOpen(false);
  };
  const [loading, setLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
  };
  const [allClients, setAllClients] = useState([]);
  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllClient();
      setAllClients(res.data);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      toast.error('Error fetching client data');
    }
  }, []);
  useEffect(() => {
    fetchData();
  }, [fetchData]);
  const updateClientList = async (): Promise<void> => {
    await fetchData();
  };

  const location = useLocation();

  const isClientDetailsRoute = matchPath(
    '/clients/client-management/:id',
    location.pathname
  );

  const { t } = useTranslation();
  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Client Management')}
            {isCreateModalOpen && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Add Client')}</span>
              </>
            )}
            {!isCreateModalOpen && isClientDetailsRoute && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Client Details')}</span>
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
              {t('Add New Client')}
            </Button>
          )}
        </ExpenseHeadingSection>
        {isCreateModalOpen ? (
          <AddClientForm
            handleClose={handleCloseModal}
            handleSuccessMessage={handleSuccessMessage}
            isEditMode={isEditMode}
            initialData={selectedClientData ?? undefined}
            updateClientList={updateClientList}
          />
        ) : (
          <Outlet
            context={{
              clientList: allClients,
              updateClientList,
              isLoading: loading,
              onEditClient,
            }}
          />
        )}
        {editLoading && <SpinAnimation />}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody={t('The Client has been Added Successfully')}
          messageHeading={t('Client Suceessfully Added.')}
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};
export default ClientManagement;
