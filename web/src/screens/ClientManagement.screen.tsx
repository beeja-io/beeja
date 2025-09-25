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
    if (isCreateModalOpen) {
      setIsCreateModalOpen(false);
    } else {
      navigate(-1);
    }
  };
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedClientData, setSelectedClientData] =
    useState<ClientDetails | null>(null);
  const [editLoading, setEditLoading] = useState(false);
  const [toastData, setToastData] = useState<{
    heading: string;
    body: string;
  } | null>(null);
  const handleOpenCreateModal = useCallback(() => {
    setIsEditMode(false);
    setSelectedClientData(null);
    setIsCreateModalOpen(true);
  }, []);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
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

  const handleSuccessMessage = (value: string, type: 'add' | 'edit') => {
    if (type === 'add') {
      setToastData({
        heading: 'Client_Added_Successfully.',
        body: `New_Client_has_been_added\nsuccessfully_with "Client ID: ${value}".`,
      });
    } else if (type === 'edit') {
      setToastData({
        heading: 'Client_Updated_Successfully.',
        body: value,
      });
    }

    setShowSuccessMessage(true);
    setIsCreateModalOpen(false);

    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 3000);

    updateClientList();
  };

  const [loading, setLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);

  const [allClients, setAllClients] = useState([]);
  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getAllClient(currentPage, itemsPerPage);
      setAllClients(res.data.data || []);
      const { totalRecords, pageNumber, pageSize } = res.data.metadata || {};
      setTotalItems(totalRecords ?? 0);
      setCurrentPage(pageNumber ?? 1);
      setItemsPerPage(pageSize ?? 10);
      setLoading(false);
    } catch (error) {
      setLoading(false);
      toast.error('Error fetching client data');
    }
  }, [currentPage,itemsPerPage]);
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
            {t('Client_Management')}
            {isCreateModalOpen && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">
                  {isEditMode ? t('Edit_Client') : t('Add_Client')}
                </span>
              </>
            )}
            {!isCreateModalOpen && isClientDetailsRoute && (
              <>
                <span className="separator"> {'>'} </span>
                <span className="nav_AddClient">{t('Client_Details')}</span>
              </>
            )}
          </span>
          {!isCreateModalOpen && !isClientDetailsRoute && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add_New_Client')}
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
              totalItems,
              currentPage,
              setCurrentPage,
              itemsPerPage,
              setItemsPerPage,
            }}
          />
        )}
        {editLoading && <SpinAnimation />}
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
export default ClientManagement;
