import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { keyPressFind } from '../service/keyboardShortcuts/shortcutValidator';
import {
  ExpenseHeading,
  ExpenseTitle,
  StyledDiv,
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import { EditSVG } from '../svgs/ClientManagmentSvgs.svg';
import { capitalizeFirstLetter, removeUnderScore } from '../utils/stringUtils';
import Pagination from '../components/directComponents/Pagination.component';
import { Client } from '../entities/ClientEntity';
import { hasPermission } from '../utils/permissionCheck';
import { CLIENT_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';

export type ClientListProps = {
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

const ClientList = ({
  clientList,
  updateClientList,
  isLoading,
  onEditClient,
  totalItems,
  currentPage,
  setCurrentPage,
  itemsPerPage,
  setItemsPerPage,
}: ClientListProps) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const { user } = useUser();
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
  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    const searchParams = new URLSearchParams(location.search);
    searchParams.set('page', newPage.toString());
    searchParams.set('size', itemsPerPage.toString());
    navigate({ search: searchParams.toString() });
  };

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const page = parseInt(searchParams.get('page') || '1', 10);
    const size = parseInt(
      searchParams.get('size') || itemsPerPage.toString(),
      10
    );

    setCurrentPage(page);
    setItemsPerPage(size);
  }, []);

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);

    const searchParams = new URLSearchParams(location.search);
    searchParams.set('page', '1');
    searchParams.set('size', newPageSize.toString());
    navigate({ search: searchParams.toString() });
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All_Clients')}</ExpenseTitle>
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
                  <th>{t('Client_ID')}</th>
                  <th>{t('Client_Name')}</th>
                  <th>{t('Client_Type')}</th>
                  <th>{t('ACTION')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading ? (
                  <>
                    {[...Array(6).keys()]?.map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(4).keys()]?.map((cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))}
                  </>
                ) : (
                  clientList?.map((client, index) => (
                    <TableBodyRow key={index}>
                      <td onClick={() => handleClientClick(client?.clientId)}>
                        {client?.clientId ?? '-'}
                      </td>
                      <td onClick={() => handleClientClick(client?.clientId)}>
                        {client?.clientName
                          ? capitalizeFirstLetter(client?.clientName)
                          : '-'}
                      </td>
                      <td onClick={() => handleClientClick(client?.clientId)}>
                        {client?.clientType === 'OTHER' &&
                        client?.customClientType
                          ? capitalizeFirstLetter(client.customClientType)
                          : client?.clientType
                            ? removeUnderScore(client.clientType)
                            : '-'}
                      </td>
                      <td>
                        <EditSVG
                          disabled={
                            !(
                              user &&
                              hasPermission(user, CLIENT_MODULE.UPDATE_CLIENT)
                            )
                          }
                          onClick={() => {
                            if (
                              user &&
                              hasPermission(user, CLIENT_MODULE.UPDATE_CLIENT)
                            ) {
                              onEditClient(client);
                            }
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
        <Pagination
          currentPage={currentPage}
          totalPages={Math.ceil(totalItems / itemsPerPage)}
          handlePageChange={handlePageChange}
          itemsPerPage={itemsPerPage}
          handleItemsPerPage={handlePageSizeChange}
          totalItems={totalItems}
        />
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
