import React, { useState } from 'react';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
} from '../styles/ProjectTabSectionStyles.style';
import { useTranslation } from 'react-i18next';

interface ContactTabSectionProps {
  contractId: string;
}

const ContactTabSection: React.FC<ContactTabSectionProps> = () => {
  const [activeTab, setActiveTab] = useState<'Resources' | 'Description'>(
    'Resources'
  );
  const { t } = useTranslation();

  return (
    <Container>
      <Tabs>
        {[t('resources'), t('description')].map((tabLabel, index) => {
          const tabKey = index === 0 ? 'Resources' : 'Description';
          return (
            <Tab
              key={tabKey}
              active={activeTab === tabKey}
              onClick={() => setActiveTab(tabKey as any)}
            >
              {tabLabel}
            </Tab>
          );
        })}
      </Tabs>

      <TabContent>
        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('employeeId')}</th>
                <th>{t('name')}</th>
                <th>{t('contractName')}</th>
                <th>{t('allocation')}</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{t('staticResourceTableNoData')}</td>
              </tr>
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Description' && (
          <div>
            <p>
              <strong>{t('description')}:</strong> {t('No discription Found')}
            </p>
          </div>
        )}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
