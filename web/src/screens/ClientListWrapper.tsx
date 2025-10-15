import { useOutletContext } from 'react-router-dom';
import ClientList from './ClientList.Screen';
import { Client } from '../entities/ClientEntity';
type ClientOutletContext = {
  clientList: Client[];
  updateClientList: () => void;
  isLoading: boolean;
  onEditClient: (client: Client) => void;
  totalItems: number;
  currentPage: number;
  setCurrentPage: (page: number) => void;
  itemsPerPage: number;
  setItemsPerPage: (size: number) => void;
};

const ClientListWrapper = () => {
  const {
    clientList,
    updateClientList,
    isLoading,
    onEditClient,
    totalItems,
    currentPage,
    setCurrentPage,
    itemsPerPage,
    setItemsPerPage,
  } = useOutletContext<ClientOutletContext>();

  return (
    <ClientList
      clientList={clientList}
      updateClientList={updateClientList}
      isLoading={isLoading}
      onEditClient={onEditClient}
      totalItems={totalItems}
      currentPage={currentPage}
      setCurrentPage={setCurrentPage}
      itemsPerPage={itemsPerPage}
      setItemsPerPage={setItemsPerPage}
    />
  );
};

export default ClientListWrapper;
