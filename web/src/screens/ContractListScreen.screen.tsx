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
import { capitalizeFirstLetter } from '../utils/stringUtils';
import { keyPressFind } from '../service/keyboardShortcuts/shortcutValidator';
import { useTranslation } from 'react-i18next';
import { EditSVG } from '../svgs/ClientSvgs.svs';
import { useNavigate } from 'react-router-dom';
import { ContractDetails } from '../entities/ContractEntiy';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { updateContractStatus } from '../service/axiosInstance';
import { ProjectStatus } from '../entities/ProjectEntity';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

export interface ContractListProps {
  contractList: ContractDetails[];
  updateContractList: () => void;
  isLoading: boolean;
  onEditContract: (contract: ContractDetails) => Promise<void>;
}

const ContractList = ({
  contractList,
  updateContractList,
  isLoading,
  onEditContract,
}: ContractListProps) => {
  const searchInputRef = useRef<HTMLInputElement>(null);
  const [contractLists, setContractLists] = useState<ContractDetails[]>([]);
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [editLoadingContractId, setEditLoadingContractId] = useState<string | null>(null);

  useEffect(() => {
    setContractLists(contractList);
  }, [contractList]);

  useEffect(() => {
    keyPressFind(searchInputRef);
  }, []);

  const handleContractClick = (id: string) => {
    navigate(`/contracts/contract-management/${id}`);
  };

  const handleStatusChange = async (
    contractId: string,
    newStatus: ProjectStatus
  ) => {
    try {
      await updateContractStatus(contractId, newStatus);
      setContractLists((prev): ContractDetails[] =>
        prev.map((c) =>
          c.contractId === contractId
            ? { ...c, status: newStatus } as ContractDetails
            : c
        )
      );
      toast.success('Status updated successfully');
      updateContractList();
    } catch (error) {
      toast.error('Failed to update status');
    }
  };

  return (
    <>
      <StyledDiv>
        <ExpenseHeading>
          <ExpenseTitle>{t('All Contracts')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer style={{ marginTop: 0 }}>
          {!isLoading && contractLists.length === 0 ? (
            <ZeroEntriesFound
              heading="No Contracts Found"
              message="You Don't Have Any Contracts"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th style={{ width: '140px', textAlign: 'left' }}>{t('Contract ID')}</th>
                  <th style={{ width: '200px', textAlign: 'left' }}>{t('Contract Name')}</th>
                  <th style={{ width: '200px', textAlign: 'left' }}>{t('Project')}</th>
                  <th style={{ width: '200px', textAlign: 'left' }}>{t('Client')}</th>
                  <th style={{ width: '200px', textAlign: 'left' }}>{t('Project Manager')}</th>
                  <th style={{ width: '160px', textAlign: 'left' }}>{t('Status')}</th>
                  <th style={{ width: '100px', textAlign: 'left' }}>{t('ACTION')}</th>
                </tr>
              </TableHead>

              <tbody>
                {isLoading
                  ? [...Array(6).keys()].map((rowIndex) => (
                      <TableBodyRow key={rowIndex}>
                        {[...Array(7).keys()].map((cellIndex) => (
                          <td key={cellIndex}>
                            <div className="skeleton skeleton-text">&nbsp;</div>
                          </td>
                        ))}
                      </TableBodyRow>
                    ))
                  : contractLists.map((contract) => (
                      <TableBodyRow key={contract.contractId}>
                        <td onClick={() => handleContractClick(contract.contractId)}>
                          {contract.contractId ?? '-'}
                        </td>
                        <td onClick={() => handleContractClick(contract.contractId)}>
                          {capitalizeFirstLetter(contract.contractTitle) ?? '-'}
                        </td>
                        <td onClick={() => handleContractClick(contract.contractId)}>
                          {contract.projectName ?? '-'}
                        </td>
                        <td onClick={() => handleContractClick(contract.contractId)}>
                          {contract.clientName ?? '-'}
                        </td>
                        <td>{contract.projectManagerNames?.[0]}</td>
                        <td>
                          {contract.status ? (
                            <StatusDropdown
                              value={contract.status}
                              onChange={(newStatus) =>
                                handleStatusChange(
                                  contract.contractId,
                                  newStatus as ProjectStatus
                                )
                              }
                            />
                          ) : (
                            '-' 
                          )}
                        </td>

                        <td>
                          {editLoadingContractId === contract.contractId ? (
                            <SpinAnimation />
                          ) : (
                            <EditSVG
                              onClick={async () => {
                                setEditLoadingContractId(contract.contractId);
                                try {
                                  await onEditContract(contract);
                                } finally {
                                  setEditLoadingContractId(null);
                                }
                              }}
                            />
                          )}
                        </td>
                      </TableBodyRow>
                    ))}
              </tbody>
            </TableList>
          )}
        </TableListContainer>
      </StyledDiv>
    </>
  );
};

export default ContractList;
