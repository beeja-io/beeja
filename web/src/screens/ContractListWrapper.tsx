import { useOutletContext } from 'react-router-dom';
import { ContractDetails } from '../entities/ContractEntiy';
import ContractList from './ContractListScreen.screen';

const ContractListWrapper = () => {
  const { contractList, isLoading, updateContractList, onEditContract } = useOutletContext<{
    contractList: ContractDetails[];
    isLoading: boolean;
    onEditContract: (contract: ContractDetails) =>  Promise<void>;
    updateContractList: () => void;
  }>();

  return (
    <ContractList
      contractList={contractList}
      isLoading={isLoading}
      updateContractList={updateContractList}
      onEditContract={onEditContract}
    />
  );
};

export default ContractListWrapper;