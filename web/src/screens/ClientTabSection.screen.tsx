import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import {
  getProjectsByClientId,
  getResourcesByClientId,
  getContractsByClientId,
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
  projectStatus: string;
  startDate: string;
  projectManagerNames: string[];
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
  description?: string;
}

const ClientTabsSection: React.FC<ClientTabsSectionProps> = ({
  clientId,
  description = '',
}) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<
    'Projects' | 'Contracts' | 'Resources' | 'Attachments' | 'Description'
  >('Projects');
  const [projects, setProjects] = useState<Project[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState(false);
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
          setLoading(false);
          return;
        }
        const mappedProjects: Project[] = res.data.map((entity) => ({
          projectId: entity.projectId ?? '-',
          name: entity.name ?? '-',
          projectStatus: entity.projectStatus ?? '-',
          startDate: entity.startDate?.split('T')[0] ?? '-',
          projectManagerNames: entity.projectManagerNames ?? [],
        }));

        const contractsRes = await getContractsByClientId(clientId);
        const mappedContracts: Contract[] = contractsRes.data.map((c) => ({
          contractId: c.contractId ?? '-',
          name: c.contractTitle ?? '-',
          status: c.status ?? '-',
          projectName: c.projectName ?? '-',
          projectManagers: c.projectManagerNames?.length
            ? c.projectManagerNames
            : [],
          startDate: c.startDate?.split('T')[0] ?? '-',
        }));

        const resourcesRes = await getResourcesByClientId(clientId);

        const mappedResources: Resource[] = Array.isArray(resourcesRes.data)
          ? resourcesRes.data.map((r: any) => ({
              employeeId: r.employeeId ?? '-',
              name: r.employeeName ?? '-',
              contractCount: r.numberOfContracts ?? 0,
              allocation: `${r.totalAllocation ?? 0}%`,
            }))
          : [];

        setProjects(mappedProjects);
        setContracts(mappedContracts);
        setResources(mappedResources);
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
                <th>{t('Project_ID')}</th>
                <th>{t('Project_Name')}</th>
                <th>{t('Status')}</th>
                <th>{t('Project_Manager(s)')}</th>
                <th>{t('Start_Date')}</th>
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
                        value={project.projectStatus}
                        disabled
                        onChange={() => {}}
                      />
                    </td>
                    <td>
                      {project.projectManagerNames.length
                        ? project.projectManagerNames.join(', ')
                        : '-'}
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
                        : '-'}
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
