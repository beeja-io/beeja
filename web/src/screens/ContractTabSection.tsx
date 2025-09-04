import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
} from '../styles/ProjectTabSectionStyles.style';

interface RawProjectResource {
  employeeId: string;
  name: string;
  allocationPercentage: number;
}

interface ContactTabSectionProps {
  contractId: string;
  description: string;
  rawProjectResources: RawProjectResource[];
}

const ContactTabSection: React.FC<ContactTabSectionProps> = ({
  description,
  rawProjectResources,
}) => {
  const [activeTab, setActiveTab] = useState<'Resources' | 'Description'>(
    'Resources'
  );
  const { t } = useTranslation();

  return (
    <Container>
      <Tabs>
        {[t('Resources'), t('Description')].map((tabLabel, index) => {
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
                <th>{t('EMPLOYEE_ID')}</th>
                <th>{t('Name')}</th>
                <th>{t('Allocation')}</th>
              </tr>
            </thead>
            <tbody>
              {rawProjectResources && rawProjectResources.length > 0 ? (
                rawProjectResources.map((resource, index) => (
                  <tr key={index}>
                    <td>{resource.employeeId}</td>
                    <td>{resource.name}</td>
                    <td>{resource.allocationPercentage}%</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={3} style={{ textAlign: 'center' }}>
                    {t('No  Resources  Found')}
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Description' && <div>{description}</div>}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
