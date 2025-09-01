import React, { useEffect, useState } from 'react';
import {
  ClientInfo,
  ClientInfoDiv,
  ClientTitle,
  Container,
  LeftSection,
  LogoPreview,
  TableContainer,
} from '../styles/ClientStyles.style';

import {
  CallSVG,
  CompanyIcon,
  DateIcon,
  DotSVG,
  EmailSVG,
} from '../svgs/ClientManagmentSvgs.svg';

import { useParams } from 'react-router-dom';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ProjectEntity } from '../entities/ProjectEntity';
import { downloadClientLogo, getProject } from '../service/axiosInstance';
import {
  ClientInfoWrapper,
  ClientTitleWrapper,
  IconWrapper,
  InfoRow,
  InfoText,
  RightSection,
  StyledStatusDropdown,
} from '../styles/ProjectStyles.style';
import ProjectTabSection from './ProjectTabSection';
import { t } from 'i18next';

const ProjectDetailsSCreen: React.FC = () => {
  const { projectId, clientId } = useParams<{
    projectId: string;
    clientId: string;
  }>();

  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const [project, setProject] = useState<ProjectEntity | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (project?.clientLogId) {
        try {
          const response = await downloadClientLogo(project.clientLogId);

          if (!response.data || response.data.size === 0) {
            throw new Error(t('Received_empty_or_invalid_blob_data'));
          }

          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;
            setLogoUrl(imageUrl);
          };
          reader.onerror = () => {
            throw new Error(t('Error_converting_blob_to_base64'));
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
  }, [project?.clientLogId]);

  useEffect(() => {
    const fetchClient = async () => {
      if (!projectId || !clientId) return;
      setLoading(true);
      try {
        const projectRes = await getProject(projectId, clientId);
        setProject(projectRes.data[0]);
      } catch (error) {
        throw new Error(t('Failed_to_fetch_client') + error);
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
          <ClientTitleWrapper>
            <ClientTitle>{project?.name}</ClientTitle>
            {project?.status && (
              <StyledStatusDropdown
                value={project.status}
                onChange={() => {}}
                disabled
              />
            )}
          </ClientTitleWrapper>

          <InfoRow>
            <ClientInfoDiv>ID: {project?.projectId}</ClientInfoDiv>
            <DotSVG />
            <CompanyIcon />
            <ClientInfoDiv>{project?.clientName}</ClientInfoDiv>
            <DotSVG />
            <ClientInfoDiv>
              <DateIcon />
              &nbsp;
              {project?.startDate &&
                new Date(project.startDate).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: '2-digit',
                })}
            </ClientInfoDiv>
          </InfoRow>
        </ClientInfo>

        {project?.clientId && project?.projectId && (
          <ProjectTabSection
            clientId={project.clientId}
            projectId={project.projectId}
            description={project.description}
          />
        )}

        <TableContainer />
      </LeftSection>

      <RightSection>
        <div>{t('Client_Details')}</div>

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
          <div>
            <CallSVG /> &nbsp;
            <span>{project?.clientContact || 'N/A'}</span>
          </div>
          &nbsp;
          <div>
            <EmailSVG /> &nbsp;
            <span>{project?.clientEmail || 'N/A'}</span>
          </div>
        </IconWrapper>
      </RightSection>
    </Container>
  );
};

export default ProjectDetailsSCreen;
