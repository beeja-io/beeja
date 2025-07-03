import { useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useEffect, useState, useCallback } from 'react';
import AddClientForm from '../components/directComponents/AddClientForm.component';

import { getAllClient, getClient } from '../service/axiosInstance';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { useTranslation } from 'react-i18next';
import { ClientDetails } from '../entities/ClientEntity';
import { Outlet } from 'react-router-dom';
import { useLocation, matchPath } from 'react-router-dom';

interface Client {
  clientId: string;
  clientName: string;
  clientType: string;
  organizationId: string;
  id: string;
}

const ProjectManagement = () => {
  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedClientData, setSelectedClientData] =
    useState<ClientDetails | null>(null);

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
      const res = await getClient(client.clientId);
      setSelectedClientData(res.data);
      setIsEditMode(true);
      setIsCreateModalOpen(true);
    } catch (error) {
      throw new Error('Error fetching edit getClient:' + error);
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
      throw new Error('Error fetching client data:' + error);
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
            Project Management
            {isCreateModalOpen && (
              <>
                <span className="separator"> {`>`} </span>
                <span className="nav_AddClient">{t('Add Client')}</span>
              </>
            )}
            {!isCreateModalOpen && isClientDetailsRoute && (
              <>
                <span className="separator"> {`>`} </span>
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
              {t('Add New Project')}
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
      </ExpenseManagementMainContainer>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_CLIENT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_ADDED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};
export default ProjectManagement;
