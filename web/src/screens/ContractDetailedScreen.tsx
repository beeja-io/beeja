import React, { useEffect, useState } from 'react';
import {
  LeftSection,
  Container,
  TableContainer,
  ClientInfo,
  LogoPreview,
  ClientTitle,
} from '../styles/ClientStyles.style';

import {
  DotSVG,
  EmailSVG,
  CallSVG,
  DateIcon,
  DollarIcon,
  CompanyIcon,
} from '../svgs/ClientSvgs.svs';

import { ClientResponse } from '../entities/ClientEntity';
import {
  downloadClientLogo,
  getClient,
  getContractDetails,
  getProject,
} from '../service/axiosInstance';
import { useParams } from 'react-router-dom';
import { ContractDetails } from '../entities/ContractEntiy';
import { InfoText } from '../styles/ProjectStyles.style';
import { ProjectEntity } from '../entities/ProjectEntity';
import {
  RightSectionDiv,
  RightSectionHeading,
  ClientInfoWrapper,
  IconWrapper,
  RightSection,
  ProjectSeactionHeading,
  RightSubSectionDiv,
} from '../styles/AddContractFormStyles.style';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import ContactTabSection from './ContractTabSection';

const ContractDetailsScreen: React.FC = () => {
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const { id } = useParams();
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [project, setProject] = useState<ProjectEntity | null>(null);
  const [contract, setContract] = useState<ContractDetails | null>(null);

  const [clientId, setClientId] = useState<string | null>(null);
  const [projectId, setProjectId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

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
    const fetchContract = async () => {
      if (!id) return;
      setIsLoading(true);
      try {
        const res = await getContractDetails(id);
        setContract(res.data);
        setClientId(res.data.clientId);
        setProjectId(res.data.projectId);
      } catch (error) {
        throw new Error('Failed to fetch contract: ' + error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchContract();
  }, [id]);

  useEffect(() => {
    const fetchProjectAndClient = async () => {
      if (!projectId || !clientId) return;
      setIsLoading(true);
      try {
        const projectRes = await getProject(projectId, clientId);
        const clientRes = await getClient(clientId);
        setProject(projectRes?.data[0]);
        setClient(clientRes.data);
      } catch (error) {
        throw new Error('Failed to fetch project/client: ' + error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProjectAndClient();
  }, [projectId, clientId]);

  if (isLoading) {
    return <SpinAnimation />;
  }

  return (
    <Container>
      <LeftSection>
        <ClientInfo>
  <ClientTitle>{contract?.contractTitle}</ClientTitle>

  <div
    style={{
      display: 'flex',
      flexWrap: 'wrap',
      alignItems: 'center',
      columnGap: '10px',
      rowGap: '6px',
      fontSize: '14px',
      fontWeight: 500,
      color: '#1f2937',
      marginBottom: '8px',
    }}
  >
    <span>ID: {contract?.contractId}</span>
    <DotSVG />
    <span>{contract?.contractType}</span>
    <DotSVG />
    <span>{contract?.billingType}</span>
    <DotSVG />
    <DollarIcon />
    <span>{contract?.contractValue}</span>
  </div>
    
  <div
    style={{
      display: 'flex',
      flexWrap: 'wrap',
      alignItems: 'center',
      borderBottom: '1px solid #E5E7EB',
      paddingBottom: '20px',
      marginBottom: '20px',
      columnGap: '10px',
      rowGap: '6px',
      fontSize: '14px',
      fontWeight: 500,
      color: '#1f2937',
    }}
  >
    <CompanyIcon />
    <span>{client?.clientName}</span>
    <DotSVG />
    <span>{project?.projectManagerNames}</span>
    <DateIcon />
    <span>
      {contract?.startDate &&
        new Date(contract.startDate).toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: '2-digit',
        })}
    </span>
    <span>TO</span>
    <DateIcon />
    <span>
      {contract?.endDate &&
        new Date(contract.endDate).toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: '2-digit',
        })}
    </span>
  </div>
</ClientInfo>

        {contract?.contractId && <ContactTabSection contractId={contract.contractId} />}
        <TableContainer />
      </LeftSection>

      <RightSection>
        <RightSectionDiv>
          <RightSectionHeading>Client Details</RightSectionHeading>
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

   
          <RightSubSectionDiv>
            <RightSectionHeading>Project Details</RightSectionHeading>
            <ProjectSeactionHeading>{client?.industry}</ProjectSeactionHeading>

            {project?.projectId && (
              <>
              <ClientInfoWrapper>
                <div className="name">{project.projectManagerNames}</div>
                <DotSVG />
                <div>{project.projectId}</div>
              </ClientInfoWrapper>
              <ClientInfoWrapper>
                   <DateIcon />
                <div className="industry">
                  {new Date(project.startDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: '2-digit',
                  })}
                </div>
              </ClientInfoWrapper>
              </>
            )}
          </RightSubSectionDiv>
        </RightSectionDiv>
      </RightSection>
    </Container>
  );
};

export default ContractDetailsScreen;
