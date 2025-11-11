import { useNavigate } from 'react-router-dom';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  StyledDiv,
  Tab,
  TabContent,
  TabHeading,
  Tabs,
} from '../styles/FeedbackHubStyles.style';
import SelfEvaluationForm from './SelfEvaluation.screen';
import ProvideFeedback from './ProvideFeedback.screen';
import FeedbackReceived from '../components/reusableComponents/FeedbackReceived.component';
import { useUser } from '../context/UserContext';

const FeedbackHub = () => {
  const [activeTab, setActiveTab] = useState<
    'Feedback Requests' | 'Self Evaluation' | 'My Feedbacks'
  >('Feedback Requests');
  const [pendingCount, setPendingCount] = useState(0);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const { user } = useUser();

  const goToPreviousPage = () => {
    navigate(-1);
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

        <StyledDiv>
          <TabHeading>
            <Tabs>
              {['Feedback Requests', 'Self Evaluation', 'My Feedbacks'].map(
                (tab) => (
                  <Tab
                    key={tab}
                    active={activeTab === tab}
                    onClick={() => setActiveTab(tab as any)}
                  >
                    {t(tab)}
                    {tab === 'Feedback Requests' && pendingCount > 0 && (
                      <span className="badge">{pendingCount}</span>
                    )}
                  </Tab>
                )
              )}
            </Tabs>
          </TabHeading>
          <TabContent>
            {activeTab === 'Feedback Requests' && (
              <ProvideFeedback
                user={user}
                onPendingCountChange={setPendingCount}
              />
            )}

            {activeTab === 'Self Evaluation' && <SelfEvaluationForm />}

            {activeTab === 'My Feedbacks' && <FeedbackReceived user={user} />}
          </TabContent>
        </StyledDiv>
      </ExpenseManagementMainContainer>
    </>
  );
};

export default FeedbackHub;
