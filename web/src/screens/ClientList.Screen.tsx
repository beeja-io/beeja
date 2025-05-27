import { useEffect, useRef, useState } from 'react';
import {
  ExpenseHeading,
  ExpenseTitle,
  StyledDiv,
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { capitalizeFirstLetter, removeUnderScore } from '../utils/stringUtils';
import { keyPressFind } from '../service/keyboardShortcuts/shortcutValidator';
import { useTranslation } from 'react-i18next';
import { EditSVG } from '../svgs/ClientSvgs.svs';
import { useNavigate } from 'react-router-dom';

interface Client {
  clientId: string;
  clientName: string;
  clientType: string;
  organizationId: string;
  id: string;
}
interface Props {
  clientList: Client[];
  updateClientList: () => void;
  isLoading: boolean;
  onEditClient: (client: Client) => void;
}

const ClientList = ({
  clientList,
  updateClientList,
  isLoading,
  onEditClient,
}: Props) => {
  const searchInputRef = useRef<HTMLInputElement>(null);

  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
    updateClientList();
  };
  useEffect(() => {
    keyPressFind(searchInputRef);
  }, []);
  const { t } = useTranslation();

  const navigate = useNavigate();
  const handleClientClick = (id: string) => {
    navigate(`/clients/client-management/${id}`);
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All Clients')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && clientList.length === 0 ? (
            <ZeroEntriesFound
              heading="No Clients Found"
              message="You Don't Have Any Clients"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th style={{ textAlign: 'left' }}>{t('Client ID')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Client Name')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Client Type')}</th>
                  <th style={{ textAlign: 'left' }}>{t('ACTION')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading ? (
                  <>
                    {[...Array(6).keys()].map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(4).keys()].map((cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))}
                  </>
                ) : (
                  clientList.map((client, index) => (
                    <TableBodyRow key={index}>
                      <td onClick={() => handleClientClick(client.clientId)}>
                        {client.clientId ?? '-'}
                      </td>
                      <td onClick={() => handleClientClick(client.clientId)}>
                        {client.clientName
                          ? capitalizeFirstLetter(client.clientName)
                          : '-'}
                      </td>
                      <td onClick={() => handleClientClick(client.clientId)}>
                        {client.clientType
                          ? removeUnderScore(client.clientType)
                          : '-'}
                      </td>

                      <td>
                        <EditSVG
                          onClick={() => {
                            onEditClient(client);
                          }}
                        />
                      </td>
                    </TableBodyRow>
                  ))
                )}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>
      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_CLIENT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_UPDATED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default ClientList;
