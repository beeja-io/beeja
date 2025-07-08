import { useOutletContext } from 'react-router-dom';
import ContractList from './ContractListScreen.screen';
import { ContractDetails } from '../entities/ContractEntiy';

const ContractListWrapper = () => {
  const { contractList, isLoading, updateContractList } = useOutletContext<{
    contractList: ContractDetails[];
    isLoading: boolean;
    updateContractList: () => void;
  }>();

  return (
    <ContractList
      contractList={contractList}
      isLoading={isLoading}
      updateContractList={updateContractList}
    />
  );
};

export default ContractListWrapper;
