import { useEffect, useState } from 'react';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import { useTranslation } from 'react-i18next';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

const CreateReviewCycle = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [successToastMessage, setSuccessToastMessage] = useState({
    heading: '',
    body: '',
  });

  const handleShowSuccessMessage = (heading: string, body: string) => {
    setSuccessToastMessage({ heading, body });
    setShowSuccessMessage(true);
  };

  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    navigate(-1);
  };

  useEffect(() => {
    if (location.pathname === '/performance/create-evaluation-form') {
      setIsCreateModalOpen(false);
    }
  }, [location.pathname]);

  const isEditMode =
    location.pathname.startsWith('/performance/create-evaluation-form/') &&
    !location.pathname.endsWith('/new');

  const handleOpenCreateModal = () => {
    setIsCreateModalOpen(true);
    navigate('/performance/create-evaluation-form/new');
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Review_Cycles')}
          </span>

          {!isCreateModalOpen && !isEditMode && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Create_New_Form')}
            </Button>
          )}
        </ExpenseHeadingSection>

        <Outlet context={{ handleShowSuccessMessage }} />
      </ExpenseManagementMainContainer>
      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageHeading={successToastMessage.heading}
          messageBody={successToastMessage.body}
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default CreateReviewCycle;
