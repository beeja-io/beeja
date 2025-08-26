import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { ContractDetails } from '../entities/ContractEntiy';
import { ProjectStatus } from '../entities/ProjectEntity';
import { updateContractStatus } from '../service/axiosInstance';
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
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { EditSVG } from '../svgs/ClientManagmentSvgs.svg';
import { capitalizeFirstLetter } from '../utils/stringUtils';

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
  const [editLoadingContractId, setEditLoadingContractId] = useState<
    string | null
  >(null);

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
            ? ({ ...c, status: newStatus } as ContractDetails)
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
          <ExpenseTitle>{t('All_Contracts')}</ExpenseTitle>
        </ExpenseHeading>

        <TableListContainer>
          {!isLoading && contractLists.length === 0 ? (
            <ZeroEntriesFound
              heading="No_Contracts_Found"
              message="You_Don't_Have_Any_Contracts"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr>
                  <th>{t('Contract_ID')}</th>
                  <th>{t('Contract_Name')}</th>
                  <th>{t('Project')}</th>
                  <th>{t('Client')}</th>
                  <th>{t('Project_Manager')}</th>
                  <th>{t('Status')}</th>
                  <th>{t('ACTION')}</th>
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
                          {capitalizeFirstLetter(contract.contractTitle) ?? '-'}
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
                        <td
                          onClick={() =>
                            handleContractClick(contract.contractId)
                          }
                        >
                          {contract.projectManagerNames?.[0]}
                        </td>
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
