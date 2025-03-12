import { useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG} from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useEffect, useState, useCallback } from 'react';
import AddClientForm from '../components/directComponents/AddClientForm.component';
import ClientList from './ClientList.Screen';
import {
  getAllClient,
} from '../service/axiosInstance';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { useTranslation } from 'react-i18next';
const ClientManagement = () => {
  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const handleIsCreateModalOpen = () => {
    setIsCreateModalOpen((prev) => { 
      return !prev;
    });
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
      const res = await getAllClient(); // Fetch client data
      setAllClients(res.data); // Store response in allClients
      setLoading(false);
    } catch (error) {
      console.error('Error fetching client data:', error);
      setLoading(false);
    }
  }, []);
  useEffect(() => {
    fetchData();
  }, [fetchData]);
  const updateClientList =() => {
    fetchData();
  };

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
          </span>
          {!isCreateModalOpen  && (
            <Button
              className="submit shadow"
              onClick={handleIsCreateModalOpen}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add New Client')}
            </Button>
          )}
        </ExpenseHeadingSection>
               {isCreateModalOpen ? (
          <AddClientForm
            handleClose={handleIsCreateModalOpen}
            handleSuccessMessage={handleSuccessMessage}
          />
        ) : (
          <ClientList
            clientList={allClients}
            updateClientList={updateClientList}
            isLoading={loading}
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
export default ClientManagement;
