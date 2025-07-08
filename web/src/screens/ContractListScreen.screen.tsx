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
import { ContractDetails } from '../entities/ContractEntiy';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { updateContractStatus } from '../service/axiosInstance';
import { ProjectStatus } from '../entities/ProjectEntity';

export interface ContractListProps {
  contractList: ContractDetails[];
  updateContractList: () => void;
  isLoading: boolean;
  onEditContract?: (contract: ContractDetails) => void;
}
const ContractList = ({
  contractList,
  updateContractList,
  isLoading,
}: ContractListProps) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [editableContractId, setEditableContractId] = useState<string | null>(
    null
  );

  const [statusUpdateLoading, setStatusUpdateLoading] = useState(false);
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
    updateContractList();
  };

  const handleContractClick = (id: string) => {
    navigate(`/contracts/contract-management/${id}`);
  };

  useEffect(() => {
    keyPressFind(searchInputRef);
  }, []);

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All Contracts')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && contractList?.length === 0 ? (
            <ZeroEntriesFound
              heading="No Contracts Found"
              message="You Don't Have Any Contracts"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th style={{ textAlign: 'left' }}>{t('Contract ID')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Contract Name')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Project')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Client')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Project Manager')}</th>
                  <th style={{ textAlign: 'left' }}>{t('Status')}</th>
                  <th style={{ textAlign: 'left' }}>{t('ACTION')}</th>
                </tr>
              </TableHead>
              <tbody>
                {isLoading
                  ? [...Array(6)].map((_, rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(5)].map((_, cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))
                  : contractList?.map((contract, index) => (
                      <TableBodyRow key={index}>
                        <td
                          onClick={() =>
                            handleContractClick(contract.contractId)
                          }
                        >
                          {contract.contractId ?? '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleContractClick(contract.contractId)
                          }
                        >
                          {contract.contractTitle
                            ? capitalizeFirstLetter(contract.contractTitle)
                            : '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleContractClick(contract.contractId)
                          }
                        >
                          {contract.projectName ?? '-'}
                        </td>
                        <td
                          onClick={() =>
                            handleContractClick(contract.contractId)
                          }
                        >
                          {contract.clientName ?? '-'}
                        </td>
                        {/* <td>{contract?.projectManagerNames[0]}</td> */}
                        <td>{contract?.projectManagerNames[0]}</td>

                        <StatusDropdown
                          value={contract.status ?? 'NOT_STARTED'}
                          disabled={editableContractId !== contract.contractId}
                          onChange={async (newStatus) => {
                            if (editableContractId !== contract.contractId)
                              return;
                            try {
                              setStatusUpdateLoading(true);
                              await updateContractStatus(
                                contract.contractId,
                                newStatus as ProjectStatus
                              );
                              handleShowSuccessMessage();
                              setEditableContractId(null);
                            } catch (error) {
                              throw new Error(
                                'Failed to update status: ' + error
                              );
                            } finally {
                              setStatusUpdateLoading(false);
                            }
                          }}
                        />

                        <td>
                          <EditSVG
                            onClick={() =>
                              setEditableContractId(contract.contractId)
                            }
                          />
                        </td>
                      </TableBodyRow>
                    ))}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_CONTRACT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_UPDATED"
          handleClose={handleShowSuccessMessage}
        />
      )}
    </>
  );
};

export default ContractList;
