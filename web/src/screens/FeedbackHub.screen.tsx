import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
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
import { hasPermission } from '../utils/permissionCheck';
import { PERFORMANCE_MODULE } from '../constants/PermissionConstants';

const FeedbackHub = () => {
  const [activeTab, setActiveTab] = useState<
    'Feedback Requests' | 'Self Evaluation' | 'My Feedbacks'
  >('Feedback Requests');
  const [pendingCount, setPendingCount] = useState(0);

  const { t } = useTranslation();

  const { user } = useUser();

  const availableTabs = [
    user && hasPermission(user, PERFORMANCE_MODULE.READ_RESPONSE) && 'Feedback Requests',
    user && hasPermission(user, PERFORMANCE_MODULE.SELF_EVALUATION) && 'Self Evaluation',
    user && hasPermission(user, PERFORMANCE_MODULE.READ_OWN_RESPONSES) && 'My Feedbacks',
  ].filter(Boolean) as ('Feedback Requests' | 'Self Evaluation' | 'My Feedbacks')[];

  const visibleActiveTab = availableTabs.includes(activeTab)
    ? activeTab
    : availableTabs[0];

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">{t('Feedback Hub')}</span>
        </ExpenseHeadingSection>

        <StyledDiv>
          <TabHeading>
            <Tabs>
              {availableTabs.map((tab) => (
                <Tab
                  key={tab}
                  active={visibleActiveTab === tab}
                  onClick={() => setActiveTab(tab)}
                >
                  {t(tab)}
                  {tab === 'Feedback Requests' && pendingCount > 0 && (
                    <span className="badge">{pendingCount}</span>
                  )}
                </Tab>
              ))}
            </Tabs>
          </TabHeading>
          <TabContent>
            {activeTab === 'Feedback Requests' && (
              <ProvideFeedback
                user={user}
                onPendingCountChange={setPendingCount}
              />
            )}

            {visibleActiveTab === 'Self Evaluation' && <SelfEvaluationForm />}

            {visibleActiveTab === 'My Feedbacks' && (
              <FeedbackReceived user={user} />
            )}
          </TabContent>
        </StyledDiv>
      </ExpenseManagementMainContainer>
    </>
  );
};

export default FeedbackHub;
