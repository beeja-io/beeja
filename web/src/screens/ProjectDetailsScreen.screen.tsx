import React, { useEffect, useState } from 'react';
import {
  LeftSection,
  RightSection,
  Container,
  TableContainer,
  ClientInfo,
  ClientInfoDiv,
  ClientTitle,
  ProjectInfo,
  LogoPreview,
} from '../styles/ClientStyles.style';

import {
  DotSVG,
  EmailSVG,
  CallSVG,
  AddSVG,
  CompanyIcon,
  DateIcon,
} from '../svgs/ClientSvgs.svs';

import { ClientResponse } from '../entities/ClientEntity';
import { t } from 'i18next';
import {
  downloadClientLogo,
  getClient,
  getProject,
} from '../service/axiosInstance';
import { useParams } from 'react-router-dom';
import { ProjectEntity } from '../entities/ProjectEntity';
import {
  RightSectionDiv,
  ClientInfoWrapper,
  InfoText,
  IconWrapper,
} from '../styles/ProjectStyles.style';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import ProjectTabSection from './ProjectTabSection';

const ProjectDetailsSCreen: React.FC = () => {
  const { projectId, clientId } = useParams<{
    projectId: string;
    clientId: string;
  }>();

  const [client, setClient] = useState<ClientResponse | null>(null);
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const [project, setProject] = useState<ProjectEntity | null>(null);
  const [isAddProjectModalOpen, setIsAddProjectModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleAddProjectModalToggle = () => {
    setIsAddProjectModalOpen((prev) => !prev);
  };

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (client?.logoId) {
        try {
          const response = await downloadClientLogo(client.logoId);

          if (!response.data || response.data.size === 0) {
            throw new Error('Received empty or invalid blob data');
          }

          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;
            setLogoUrl(imageUrl);
          };
          reader.onerror = () => {
            throw new Error('Error converting blob to base64');
          };

          reader.readAsDataURL(response.data);
        } catch (error) {
          throw new Error('Error fetching logo:' + error);
        }
      }
    };

    fetchLogoImage();

    return () => {
      setLogoUrl(null);
    };
  }, [client?.logoId]);

  useEffect(() => {
    const fetchClient = async () => {
      if (!projectId || !clientId) return;
      setLoading(true);
      try {
        const projectRes = await getProject(projectId, clientId);
        const clientRes = await getClient(clientId);
        setProject(projectRes.data);
        setClient(clientRes.data);
      } catch (error) {
        throw new Error('Failed to fetch client:' + error);
      } finally {
        setLoading(false);
      }
    };

    fetchClient();
  }, [projectId, clientId]);

  if (loading) {
    return <SpinAnimation />;
  }

  return (
    <Container>
      <LeftSection>
        <ClientInfo>
          <ClientTitle> {project?.name}</ClientTitle>

          <div style={{ display: 'flex', marginBottom: '30px' }}>
            <ClientInfoDiv style={{ width: '100px', paddingRight: '10px' }}>
              ID: {project?.projectId}
            </ClientInfoDiv>
            <DotSVG />
            <CompanyIcon />
            <ClientInfoDiv
              style={{
                width: '100px',
                paddingRight: '10px',
                paddingLeft: '10px',
              }}
            >
              {client?.clientName}
            </ClientInfoDiv>
            <div
              style={{ display: 'flex', alignItems: 'flex-start', gap: '4px' }}
            >
              <DotSVG />
              <ClientInfoDiv
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  width: '250px',
                  wordWrap: 'break-word',
                }}
              >
                <span>Start Date :</span> &nbsp;
                <DateIcon /> &nbsp;
                {project?.startDate &&
                  new Date(project.startDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'long',
                    day: '2-digit',
                  })}
              </ClientInfoDiv>
            </div>
          </div>
        </ClientInfo>
        {client?.clientId && (
          <ProjectTabSection
            clientId={client.clientId}
            projectId={project?.projectId}

            // client={client}
            // project={project}
          />
        )}
        <TableContainer></TableContainer>
      </LeftSection>

      <RightSection>
        <RightSectionDiv>
          <div>Client Details</div>

          {client?.clientId && (
            <ClientInfoWrapper>
              <LogoPreview>
                <img src={logoUrl || undefined} alt="Logo Preview" />
              </LogoPreview>
              <InfoText>
                <div className="id">
                  ID: <span>{client.clientId}</span>
                </div>
                <div className="name">{client.clientName}</div>
                <div className="industry">{client.industry}</div>
              </InfoText>
            </ClientInfoWrapper>
          )}

          <IconWrapper>
            <CallSVG />

            <EmailSVG />
          </IconWrapper>
        </RightSectionDiv>
      </RightSection>
    </Container>
  );
};

export default ProjectDetailsSCreen;
