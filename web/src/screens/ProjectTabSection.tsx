import { t } from 'i18next';
import React, { useEffect, useState } from 'react';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { getProject } from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import {
  Container,
  NoDataContainer,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
} from '../styles/ProjectTabSectionStyles.style';
import { toast } from 'sonner';

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
  contractName: string[];
  allocation: number;
}

interface ClientTabsSectionProps {
  clientId: string;
  projectId?: string;
  description?: string;
}

const ClientTabsSection: React.FC<ClientTabsSectionProps> = ({
  clientId,
  projectId,
  description,
}) => {
  const [activeTab, setActiveTab] = useState<
    'Contracts' | 'Resources' | 'Attachments' | 'Description'
  >(() => {
    return (
      (sessionStorage.getItem(`activeTab_${clientId}_${projectId}`) as
        | 'Contracts'
        | 'Resources'
        | 'Attachments'
        | 'Description') ?? 'Contracts'
    );
  });

  const handleTabChange = (
    tab: 'Contracts' | 'Resources' | 'Attachments' | 'Description'
  ) => {
    setActiveTab(tab);
    sessionStorage.setItem(`activeTab_${clientId}_${projectId}`, tab);
  };

  const [contracts, setContracts] = useState<Contract[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const fetchProjects = async () => {
      if (!clientId || !projectId) return;
      setLoading(true);
      try {
        const projectResponse = await getProject(projectId, clientId);
        const entity = projectResponse?.data[0];

        if (!entity) {
          setLoading(false);
          return;
        }

        const mappedContracts: Contract[] = (entity?.contracts || []).map(
          (c: any) => ({
            contractId: c.contractId ?? 'N/A',
            name: c.name ?? 'N/A',
            status: c.status ?? 'N/A',
            projectManagers: (c.projectManagers || []).map((pm: any) => ({
              employeeId: pm.employeeId ?? 'N/A',
              name: pm.name ?? 'N/A',
              contractName: pm.contractName ?? 'N/A',
            })),
            startDate: c.startDate?.split('T')[0] ?? '',
          })
        );

        const mappedResources: Resource[] = (entity.resources || []).map(
          (r: any) => ({
            employeeId: r.employeeId,
            name: r.name,
            contractName: r.contractName ?? [],
            allocation: r.allocationPercentage ?? 0,
          })
        );

        setContracts(mappedContracts);
        setResources(mappedResources);
      } catch (err: any) {
        toast.error('Fetching Error', err);
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
        {['Contracts', 'Resources', 'Description'].map((tab) => (
          <Tab
            key={tab}
            active={activeTab === tab}
            onClick={() => handleTabChange(tab as any)}
          >
            {t(tab)}
          </Tab>
        ))}
      </Tabs>

      <TabContent>
        {activeTab === 'Contracts' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Contract_ID')}</th>
                <th>{t('Contract_Name')}</th>
                <th>{t('Status')}</th>
                <th>{t('Project_Manager')}</th>
                <th>{t('Start_Date')}</th>
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
                        ? contract?.projectManagers
                            .map((pm) => pm.name)
                            .join(', ')
                        : 'N/A'}
                    </td>
                    <td>{contract?.startDate}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5}>
                    <NoDataContainer>
                      {t('No Contracts Available')}
                    </NoDataContainer>
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Employee_ID')}</th>
                <th>{t('Name')}</th>
                <th>{t('Contract_Name')}</th>
                <th>{t('Allocation')}</th>
              </tr>
            </thead>
            <tbody>
              {resources?.length ? (
                resources.map((r) => (
                  <tr key={r?.employeeId}>
                    <td>{r?.employeeId}</td>
                    <td>{r?.name}</td>
                    <td>
                      {Array.isArray(r?.contractName) &&
                      r.contractName.length > 0
                        ? r.contractName.map((c, index) => (
                            <span key={index}>
                              {c}
                              {index < r.contractName.length - 1 ? ', ' : ''}
                            </span>
                          ))
                        : t('No contracts assigned')}
                    </td>
                    <td>{r.allocation > 0 ? `${r.allocation}%` : '-'}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={4}>
                    <NoDataContainer>
                      {t('No Resources Available')}
                    </NoDataContainer>
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}
        {activeTab === 'Description' && (
          <div>
            {description ? (
              <p>{description}</p>
            ) : (
              <NoDataContainer>{t('No Description Added')}</NoDataContainer>
            )}
          </div>
        )}
      </TabContent>
    </Container>
  );
};

export default ClientTabsSection;
