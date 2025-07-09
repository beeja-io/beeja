import { useOutletContext } from 'react-router-dom';
import ClientList from './ClientList.Screen';

interface Client {
  clientId: string;
  clientName: string;
  clientType: string;
  organizationId: string;
  id: string;
}

const ClientListWrapper = () => {
  const { clientList, updateClientList, isLoading, onEditClient } =
    useOutletContext<{
      clientList: Client[];
      updateClientList: () => void;
      isLoading: boolean;
      onEditClient: (client: Client) => void;
    }>();

  return (
    <ClientList
      clientList={clientList}
      updateClientList={updateClientList}
      isLoading={isLoading}
      onEditClient={onEditClient}
    />
  );
};

export default ClientListWrapper;
