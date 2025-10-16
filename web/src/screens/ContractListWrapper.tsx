import { useOutletContext } from 'react-router-dom';
import { ContractDetails } from '../entities/ContractEntiy';
import ContractList from './ContractListScreen.screen';

type ContractOutletContext = {
  contractList: ContractDetails[];
  updateContractList: () => void;
  isLoading: boolean;
  onEditContract: (contract: ContractDetails) => Promise<void>;
  totalItems: number;
  currentPage: number;
  setCurrentPage: (page: number) => void;
  itemsPerPage: number;
  setItemsPerPage: (size: number) => void;
};

const ContractListWrapper = () => {
  const {
    contractList,
    isLoading,
    updateContractList,
    onEditContract,
    totalItems,
    currentPage,
    setCurrentPage,
    itemsPerPage,
    setItemsPerPage,
  } = useOutletContext<ContractOutletContext>();

  return (
    <ContractList
      contractList={contractList}
      isLoading={isLoading}
      updateContractList={updateContractList}
      onEditContract={onEditContract}
      totalItems={totalItems}
      currentPage={currentPage}
      setCurrentPage={setCurrentPage}
      itemsPerPage={itemsPerPage}
      setItemsPerPage={setItemsPerPage}
    />
  );
};

export default ContractListWrapper;
