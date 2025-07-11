import React, { useEffect, useState } from 'react';
import {
  Tabs,
  Container,
  ProjectsTable,
  TabContent,
  AvatarGroup,
  Avatar,
  Status,
  Tab,
} from '../styles/ProjectTabSectionStyles.style';
import {
  getContractDetails,
  getProject,
  getProjectDetails,
} from '../service/axiosInstance';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

interface Project {
  projectId: string;
  name: string;
  status: string;
  projectManagers: { name: string; avatarUrl: string }[];
  startDate: string;
}
interface Contract {
  id: string;
  name: string;
  status: string;
  manager: { name: string; avatarUrl: string };
  startDate: string;
}
interface Resource {
  employeeId: string;
  name: string;
  avatarUrl: string;
  contractName: string;
  allocation: string;
}
interface StatusProps {
  status: string;
  children: React.ReactNode;
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
    | 'Project Managers'
    | 'Contracts'
    | 'Resources'
    | 'Attachments'
    | 'Description'
  >('Project Managers');
  const [projects, setProjects] = useState<Project[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [resources, setResources] = useState<Resource[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const Status: React.FC<StatusProps> = ({ status, children }) => {
    let color;
    switch (status.toLowerCase()) {
      case 'active':
        color = 'green';
        break;
      case 'pending':
        color = 'orange';
        break;
      case 'inactive':
        color = 'gray';
        break;
      default:
        color = 'black';
    }
    return <span style={{ color }}>{children}</span>;
  };

  useEffect(() => {
    const fetchProjects = async () => {
      if (!clientId || !projectId) return;
      setLoading(true);
      try {
        const ProjectResponse = await getProject(projectId, clientId);

        setProjects([ProjectResponse.data]);
        setError(null);
      } catch (err: any) {
        setError('Failed to load projects');
        throw new Error('Failed to fetch client projects:' + err);
      } finally {
        setLoading(false);
      }
    };
    fetchProjects();
  }, [clientId]);

  if (loading) {
    return <SpinAnimation />;
  }

  return (
    <Container
      style={{
        maxWidth: '1400px',
      }}
    >
      <Tabs>
        {[
          'Project Managers',
          'Contracts',
          'Resources',
          'Attachments',
          'Description',
        ].map((tab) => (
          <Tab
            key={tab}
            active={activeTab === tab}
            onClick={() => setActiveTab(tab as any)}
          >
            {tab}
          </Tab>
        ))}
      </Tabs>
      <TabContent>
        {activeTab === 'Project Managers' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>Employee ID</th>
                <th>Name</th>
                <th>Contract</th>
              </tr>
            </thead>
            <tbody>
              {projects?.length > 0 ? (
                projects.map((project) => (
                  <tr key={project.projectId}>
                    <td>{project?.projectId}</td>
                    <td>{project.name}</td>
                    <td>{project.status}</td>
                    {/* <td>{project.projectManagers?.join(', ')}</td> */}
                    {/* <td>{project.projectManagers[0]}</td> */}
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5}>No projects available</td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}
        {activeTab === 'Contracts' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>Contract ID</th>
                <th>Contract Name</th>
                <th>Status</th>
                <th>Project Manager</th>
                <th>Start date</th>
              </tr>
            </thead>
            <tbody>
              {contracts.map((contract) => (
                <tr key={contract.id}>
                  <td>{contract.id}</td>
                  <td>{contract.name}</td>
                  <td>
                    <Status status={contract.status}>{contract.status}</Status>
                  </td>
                  <td>
                    <AvatarGroup>
                      <Avatar
                        src={contract.manager.avatarUrl}
                        alt={contract.manager.name}
                      />
                      {contract.manager.name}
                    </AvatarGroup>
                  </td>
                  <td>{contract.startDate}</td>
                </tr>
              ))}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>Employee ID</th>
                <th>Name</th>
                <th>Allocation</th>
              </tr>
            </thead>
            <tbody>
              {resources.map((resource) => (
                <tr key={resource.employeeId}>
                  <td>{resource.employeeId}</td>
                  <td>
                    <AvatarGroup>
                      <Avatar src={resource.avatarUrl} alt={resource.name} />
                      <i>{resource.name}</i>
                    </AvatarGroup>
                  </td>
                  <td>{resource.contractName}</td>
                  <td>{resource.allocation}</td>
                </tr>
              ))}
            </tbody>
          </ProjectsTable>
        )}
      </TabContent>
    </Container>
  );
};

export default ClientTabsSection;
