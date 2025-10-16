import { useLocation, useNavigate } from 'react-router-dom';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useState } from 'react';
import { ExpenseHeading, StyledDiv } from '../styles/ExpenseListStyles.style';
import { useTranslation } from 'react-i18next';
import { Tab, TabContent, Tabs } from '../styles/ProjectTabSectionStyles.style';

const FeedbackHub = () => {
  const [activeTab, setActiveTab] = useState<
    'Feedback Requests' | 'Self Evaluation' | 'My Feedback'
  >('Feedback Requests');
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    if (
      location.pathname.endsWith('/new') ||
      location.pathname.match(/\/\d+$/)
    ) {
      navigate('/performance/create-evaluation-form');
    } else {
      navigate(-1);
    }
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Feedback Hub')}
          </span>
        </ExpenseHeadingSection>

        <>
          <StyledDiv>
            <ExpenseHeading>
              <Tabs>
                {['Feedback Requests', 'Self Evaluation', 'My Feedback'].map(
                  (tab) => (
                    <Tab
                      key={tab}
                      active={activeTab === tab}
                      onClick={() => setActiveTab(tab as any)}
                    >
                      {t(tab)}
                    </Tab>
                  )
                )}
              </Tabs>
            </ExpenseHeading>
          </StyledDiv>
        </>

        <TabContent>
          {activeTab === 'Feedback Requests' && <p>Feedback Requests</p>}

          {activeTab === 'Self Evaluation' && <p>Self Evaluation</p>}

          {activeTab === 'My Feedback' && <p>My Feedback</p>}
        </TabContent>
      </ExpenseManagementMainContainer>
    </>
  );
};

export default FeedbackHub;
