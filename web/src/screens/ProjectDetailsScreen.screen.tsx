import React, { useEffect, useState } from 'react';
import {
  ClientInfo,
  ClientInfoDiv,
  ClientTitle,
  Container,
  LeftSection,
  LogoPreview,
  RightSection,
  TableContainer
} from '../styles/ClientStyles.style';

import {
  CallSVG,
  CompanyIcon,
  DateIcon,
  DotSVG,
  EmailSVG
} from '../svgs/ClientSvgs.svs';

import { useParams } from 'react-router-dom';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ProjectEntity } from '../entities/ProjectEntity';
import {
  downloadClientLogo,
  getProject
} from '../service/axiosInstance';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import {
  ClientInfoWrapper,
  IconWrapper,
  InfoText,
  RightSectionDiv,
} from '../styles/ProjectStyles.style';
import ProjectTabSection from './ProjectTabSection';

const ProjectDetailsSCreen: React.FC = () => {
  const { projectId, clientId } = useParams<{
    projectId: string;
    clientId: string;
  }>();

  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const [project, setProject] = useState<ProjectEntity | null>(null);
  const [isAddProjectModalOpen, setIsAddProjectModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleAddProjectModalToggle = () => {
    setIsAddProjectModalOpen((prev) => !prev);
  };

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (project?.logoId) {
        try {
          const response = await downloadClientLogo(project.logoId);

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
  }, [project?.logoId]);

  useEffect(() => {
    const fetchClient = async () => {
      if (!projectId || !clientId) return;
      setLoading(true);
      try {
        const projectRes = await getProject(projectId, clientId);
        setProject(projectRes.data[0]);
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
          <ClientTitle style={{ position: 'relative' }}>{project?.name}
            {project?.status && (
              <div style={{ position: 'absolute', top: 0, left: '42rem',transform: 'scale(0.7)'}}>
        <StatusDropdown
          value={project.status}
          onChange={() => {}}
          disabled
        />
      </div>

    
            )}
          </ClientTitle>
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
              {project?.clientName}
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
        {project?.clientId && project?.projectId && (
          <ProjectTabSection
            clientId={project.clientId}
            projectId={project.projectId}
          />
        )}
        <TableContainer></TableContainer>
      </LeftSection>

      <RightSection>
        <RightSectionDiv>
          <div style={{marginBottom: '5px'}}>Client Details</div>

          {project?.clientId && (
            <ClientInfoWrapper>
              <LogoPreview>
                <img src={logoUrl || undefined} alt="Logo Preview" />
              </LogoPreview>
              <InfoText>
                <div className="id">
                  ID: <span>{project.clientId}</span>
                </div>
                <div className="name">{project.clientName}</div>
                <div className="industry">{project.clientIndustries}</div>
              </InfoText>
            </ClientInfoWrapper>
          )}

        <IconWrapper>
  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
    <CallSVG />
    <span>{project?.clientContact || 'N/A'}</span>
  </div>

  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
    <EmailSVG />
    <span>{project?.clientEmail || 'N/A'}</span>
  </div>
</IconWrapper>

        </RightSectionDiv>
      </RightSection>
    </Container>
  );
};

export default ProjectDetailsSCreen;
