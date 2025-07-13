// src/screens/ContactTabSection.tsx
import React, { useState } from 'react';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs
} from '../styles/ProjectTabSectionStyles.style';

interface ContactTabSectionProps {
  contractId: string;
}

const ContactTabSection: React.FC<ContactTabSectionProps> = () => {
  const [activeTab, setActiveTab] = useState<'Resources' | 'Description'>('Resources');

  return (
    <Container style={{ maxWidth: '1400px', marginTop: '2rem' }}>
      <Tabs>
        {['Resources', 'Description'].map((tab) => (
          <Tab key={tab} active={activeTab === tab} onClick={() => setActiveTab(tab as any)}>
            {tab}
          </Tab>
        ))}
      </Tabs>

      <TabContent>
        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>Employee ID</th>
                <th>Name</th>
                <th>Contract Name</th>
                <th>Allocation</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan={4} style={{ textAlign: 'center' }}>
                  Static Resource Table (No Data)
                </td>
              </tr>
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Description' && (
          <div style={{ padding: '1rem', fontSize: '16px', lineHeight: '1.5' }}>
            <p><strong>Description:</strong> Placeholder description for contract details.</p>
            <p>You can describe the contract goals, responsibilities, and terms here.</p>
          </div>
        )}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
