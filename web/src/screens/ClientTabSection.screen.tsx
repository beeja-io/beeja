import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import {
  getContractsByClientId,
  getProjectsByClientId,
} from '../service/axiosInstance';
import { CountBadge, DateIconWrapper } from '../styles/ClientStyles.style';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
} from '../styles/ProjectTabSectionStyles.style';
import { DateIcon } from '../svgs/ClientManagmentSvgs.svg';

interface Project {
  projectId: string;
  name: string;
  status: string;
  startDate: string;
  projectManagers: string[];
}

interface Contract {
  contractId: string;
  name: string;
  status: string;
  projectName: string;
  projectManagers: string[];
  startDate: string;
}

interface Resource {
  employeeId: string;
  name: string;
  contractCount: number;
  allocation: string;
}

interface ClientTabsSectionProps {
  clientId: string;
  projectId?: string;
}

const ClientTabsSection: React.FC<ClientTabsSectionProps> = ({ clientId }) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<
    'Projects' | 'Contracts' | 'Resources' | 'Attachments' | 'Description'
  >('Projects');
  const [projects, setProjects] = useState<Project[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(false);
  const [description, setDescription] = useState<string>('');

  useEffect(() => {
    const fetchClientData = async () => {
      if (!clientId) return;
      setLoading(true);
      try {
        const res = await getProjectsByClientId(clientId);
        const entity = res?.data?.[0];

        if (!entity) {
          setProjects([]);
          setContracts([]);
          setResources([]);
          toast.info(t('No project data available for this client'));
          setLoading(false);
          return;
        }
        setDescription(entity?.description ?? '');

        const mappedProjects: Project[] = res.data.map((entity) => ({
          projectId: entity.projectId ?? 'N/A',
          name: entity.name ?? 'N/A',
          status: entity.status ?? 'N/A',
          startDate: entity.startDate?.split('T')[0] ?? 'N/A',
          projectManagers: Array.isArray(entity.projectManagers)
            ? entity.projectManagers.map((pm: any) => pm?.name ?? 'N/A')
            : [],
        }));

        const contractsRes = await getContractsByClientId(clientId);
        const mappedContracts: Contract[] = contractsRes.data.map((c) => ({
          contractId: c.contractId ?? 'N/A',
          name: c.contractTitle ?? 'N/A',
          status: c.status ?? 'N/A',
          projectName: c.projectName ?? 'N/A',
          projectManagers: c.projectManagerNames?.length
            ? c.projectManagerNames
            : [],
          startDate: c.startDate?.split('T')[0] ?? 'N/A',
        }));

        const resourceMap: Record<string, Resource> = {};
        Array.isArray(entity.resources) &&
          entity.resources.forEach((r: any) => {
            const key = r?.employeeId ?? 'unknown';
            if (!resourceMap[key]) {
              resourceMap[key] = {
                employeeId: r?.employeeId ?? 'N/A',
                name: r?.name ?? 'N/A',
                contractCount: 1,
                allocation: `${r?.allocationPercentage ?? 0}%`,
              };
            } else {
              resourceMap[key].contractCount += 1;
            }
          });

        setProjects(mappedProjects);
        setContracts(mappedContracts);
        setResources(Object.values(resourceMap));
      } catch (error) {
        toast.error(t('Failed to load project data'));
      } finally {
        setLoading(false);
      }
    };

    fetchClientData();
  }, [clientId, t]);

  if (loading) return <SpinAnimation />;

  return (
    <Container>
      <Tabs>
        {['Projects', 'Contracts', 'Resources', 'Description'].map((tab) => (
          <Tab
            key={tab}
            active={activeTab === tab}
            onClick={() => setActiveTab(tab as any)}
          >
            {t(tab)}
            {tab === 'Projects' && <CountBadge>{projects.length}</CountBadge>}
            {tab === 'Contracts' && <CountBadge>{contracts.length}</CountBadge>}
          </Tab>
        ))}
      </Tabs>

      <TabContent>
        {activeTab === 'Projects' && projects && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Project ID')}</th>
                <th>{t('Project Name')}</th>
                <th>{t('Status')}</th>
                <th>{t('Project Manager(s)')}</th>
                <th>{t('Start Date')}</th>
              </tr>
            </thead>
            <tbody>
              {projects.length ? (
                projects.map((project) => (
                  <tr key={project.projectId}>
                    <td>{project.projectId}</td>
                    <td>{project.name}</td>
                    <td>
                      <StatusDropdown
                        value={project.status}
                        disabled
                        onChange={() => {}}
                      />
                    </td>
                    <td>
                      {project.projectManagers.length
                        ? project.projectManagers.join(', ')
                        : 'N/A'}
                    </td>
                    <td>
                      <DateIconWrapper>
                        <DateIcon />
                        {project.startDate}
                      </DateIconWrapper>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5}>{t('No Projects Available')}</td>
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
                <th>{t('Project')}</th>
                <th>{t('Status')}</th>
                <th>{t('Project Manager')}</th>
                <th>{t('Start Date')}</th>
              </tr>
            </thead>
            <tbody>
              {contracts.length ? (
                contracts.map((c) => (
                  <tr key={c.contractId}>
                    <td>{c.contractId}</td>
                    <td>{c.name}</td>
                    <td>{c.projectName}</td>
                    <td>
                      <StatusDropdown
                        value={c.status}
                        disabled
                        onChange={() => {}}
                      />
                    </td>
                    <td>
                      {c.projectManagers.length
                        ? c.projectManagers.join(', ')
                        : 'N/A'}
                    </td>
                    <td>{c.startDate}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={6}>{t('No Contracts Available')}</td>
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
                <th>{t('No. of Contracts')}</th>
                <th>{t('Allocation')}</th>
              </tr>
            </thead>
            <tbody>
              {resources.length ? (
                resources.map((r) => (
                  <tr key={r.employeeId}>
                    <td>{r.employeeId}</td>
                    <td>{r.name}</td>
                    <td>{r.contractCount}</td>
                    <td>{r.allocation}</td>
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

        {activeTab === 'Attachments' && (
          <div>{t('Attachments will be shown here.')}</div>
        )}

        {activeTab === 'Description' && (
          <div>
            {description ? (
              <p>{description}</p>
            ) : (
              <p>{t('No Description Available')}</p>
            )}
          </div>
        )}
      </TabContent>
    </Container>
  );
};

export default ClientTabsSection;
