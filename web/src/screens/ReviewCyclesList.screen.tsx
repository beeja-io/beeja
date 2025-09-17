import {
  StyledDiv,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';

const ReviewCyclesList = () => {
  return (
    <>
      <StyledDiv>
        <TableListContainer>
          <ZeroEntriesFound heading="No Review Cycles Found" />
        </TableListContainer>
      </StyledDiv>
    </>
  );
};

export default ReviewCyclesList;
