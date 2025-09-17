import { useCallback, useState } from 'react';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import { useTranslation } from 'react-i18next';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import AddReviewCycle from '../components/directComponents/AddReviewCycle.component';
import ReviewCyclesList from './ReviewCyclesList.screen';

const CreateReviewCycle = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const navigate = useNavigate();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    if (isCreateModalOpen) {
      setIsCreateModalOpen(false);
    } else {
      navigate(-1);
    }
  };

  const handleOpenCreateModal = useCallback(() => {
    setIsCreateModalOpen(true);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsCreateModalOpen(false);
  }, []);

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Create_Review_Cycle')}
          </span>

          {!isCreateModalOpen && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add_New_Review_Cycle')}
            </Button>
          )}
        </ExpenseHeadingSection>

        {isCreateModalOpen ? (
          <AddReviewCycle handleClose={handleCloseModal} />
        ) : (
          <ReviewCyclesList />
        )}
      </ExpenseManagementMainContainer>
    </>
  );
};

export default CreateReviewCycle;
