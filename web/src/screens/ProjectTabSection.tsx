import { t } from 'i18next';
import React, { useEffect, useState } from 'react';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { getProject } from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs
} from '../styles/ProjectTabSectionStyles.style';

interface Project {
  projectId: string;
  name: string;
  status: string;
  startDate: string;
  projectManagers: {
    employeeId: string;
    name: string;
    contract: string;
  }[];
}

interface Contract {
  contractId: string;
  name: string;
  status: string;
  projectManagers: {
    employeeId: string;
    name: string;
    contractName: string;
  }[];
  startDate: string;
}

interface Resource {
  employeeId: string;
  name: string;
  contractName: string;
  allocation: string;
}

interface ClientTabsSectionProps {
  clientId: string;
  projectId?: string;
}

const ClientTabsSection: React.FC<ClientTabsSectionProps> = ({
  clientId,
  projectId,
}) => {
  const [activeTab, setActiveTab] = useState<
    'Project Managers' | 'Contracts' | 'Resources' | 'Attachments' | 'Description'
  >('Project Managers');

  const [project, setProject] = useState<Project | null>(null);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProjects = async () => {
      if (!clientId || !projectId) return;
      setLoading(true);
      try {
        const projectResponse = await getProject(projectId, clientId);
        const entity = projectResponse?.data[0];

        if (!entity) {
          setError('Project not found');
          setLoading(false);
          return;
        }

        const mappedProject: Project = {
          projectId: entity?.projectId,
          name: entity?.name,
          status: entity?.status ?? 'N/A',
          startDate: entity?.startDate?.split('T')[0] ?? '',
          projectManagers: (entity?.projectManagers || []).map((pm: any) => ({
            employeeId: pm?.employeeId ?? 'N/A',
            name: pm?.name ?? 'N/A',
            contract: pm?.contractName ?? 'N/A',
          })),
        };

        const mappedContracts: Contract[] = (entity?.contracts || []).map((c: any) => ({
          contractId: c.contractId ?? 'N/A',
          name: c.name ?? 'N/A',
          status: c.status ?? 'N/A',
          projectManagers: (c.projectManagers || []).map((pm: any) => ({
            employeeId: pm.employeeId ?? 'N/A',
            name: pm.name ?? 'N/A',
            contractName: pm.contractName ?? 'N/A',
          })),
          startDate: c.startDate?.split('T')[0] ?? '',
        }));

        const mappedResources: Resource[] = (entity.resources || []).map((r: any) => ({
          employeeId: r.employeeId,
          name: r.name,
          contractName: r.contractName ?? 'N/A',
          allocation: `${r.allocationPercentage ?? 0}%`,
        }));

        setProject(mappedProject);
        setContracts(mappedContracts);
        setResources(mappedResources);
        setError(null);
      } catch (err: any) {
        setError('Failed to load project');
        console.error('Fetch Error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProjects();
  }, [clientId, projectId]);

  if (loading) return <SpinAnimation />;

  return (
    <Container>
      <Tabs>
        {['Project Managers', 'Contracts', 'Resources', 'Description'].map((tab) => (
          <Tab key={tab} active={activeTab === tab} onClick={() => setActiveTab(tab as any)}>
            {tab}
          </Tab>
        ))}
      </Tabs>

      <TabContent>
        {activeTab === 'Project Managers' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Employee ID')}</th>
                <th>{t('Name')}</th>
                <th>{t('Contract')}</th>
              </tr>
            </thead>
            <tbody>
              {project?.projectManagers?.length ? (
                project.projectManagers.map((pm, idx) => (
                  <tr key={idx}>
                    <td>{pm?.employeeId}</td>
                    <td>{pm?.name}</td>
                    <td>{pm?.contract}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={3}>{t('No Project Managers Available')}</td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Contracts' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Contract ID')}</th>
                <th>{t('Contract Name')}</th>
                <th>{t('Status')}</th>
                <th>{t('Project Manager')}</th>
                <th>{t('Start Date')}</th>
              </tr>
            </thead>
            <tbody>
              {contracts?.length ? (
                contracts?.map((contract) => (
                  <tr key={contract?.contractId}>
                    <td>{contract?.contractId}</td>
                    <td>{contract?.name}</td>
                    <td>
                      <StatusDropdown
                        value={contract?.status}
                        onChange={() => {}}
                        disabled
                      />
                    </td>
                    <td>
                      {contract.projectManagers.length
                        ? contract?.projectManagers.map((pm) => pm.name).join(', ')
                        : 'N/A'}
                    </td>
                    <td>{contract?.startDate}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5}>{t('No Contracts Available')}</td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Employee ID')}</th>
                <th>{t('Name')}</th>
                <th>{t('Contract Name')}</th>
                <th>{t('Allocation')}</th>
              </tr>
            </thead>
            <tbody>
              {resources?.length ? (
                resources.map((r) => (
                  <tr key={r?.employeeId}>
                    <td>{r?.employeeId}</td>
                    <td>{r?.name}</td>
                    <td>{r?.contractName}</td>
                    <td>{r?.allocation}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={4}>{t('No Resources Available')}</td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}
      </TabContent>
    </Container>
  );
};

export default ClientTabsSection;
