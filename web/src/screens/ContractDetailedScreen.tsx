import React, { useEffect, useState } from 'react';
import {
  ClientInfo,
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
  DollarIcon,
  DotSVG,
  EmailSVG,
} from '../svgs/ClientManagmentSvgs.svg';

import { t } from 'i18next';
import { useParams } from 'react-router-dom';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ClientResponse } from '../entities/ClientEntity';
import { ContractDetails } from '../entities/ContractEntiy';
import { ProjectEntity } from '../entities/ProjectEntity';
import {
  downloadClientLogo,
  getClient,
  getContractDetails,
  getProject,
} from '../service/axiosInstance';
import {
  ClientInfoWrapper,
  IconWrapper,
  ProjectSeactionHeading,
  RightSection,
  RightSectionDiv,
  RightSectionHeading,
  RightSubSectionDiv,
} from '../styles/AddContractFormStyles.style';
import {
  ColumnItem,
  ContractTitleHeader,
  HorizontalLine,
  IconItem,
  RowWrapper,
} from '../styles/ContractStyle.style';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { InfoText } from '../styles/ProjectStyles.style';
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
            toast.error('Received_empty_or_invalid_blob_data');
          }
          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;
            setLogoUrl(imageUrl);
          };
          reader.onerror = () => {
            toast.error('Error_converting_blob_to_base64');
          };
          reader.readAsDataURL(response.data);
        } catch (error) {
          toast.error(t('Error_Fetching_logo'));
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
        setContract(res?.data);
        setClientId(res?.data?.clientId);
        setProjectId(res?.data?.projectId);
      } catch (error) {
        toast.error(t('Failed_to_fetch_contract'));
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
        toast.error('Failed_to_fetch_project/client: ');
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
          <ContractTitleHeader>
            <ClientTitle>{contract?.contractTitle}</ClientTitle>
            {contract?.status && (
              <StatusDropdown
                value={contract.status}
                onChange={() => {}}
                disabled
              />
            )}
          </ContractTitleHeader>

          <RowWrapper>
            <ColumnItem>
              {t('ID')}: {contract?.contractId}
            </ColumnItem>
            <DotSVG />
            <ColumnItem>{contract?.contractType}</ColumnItem>
            <DotSVG />
            <ColumnItem>{contract?.billingType}</ColumnItem>
            <DotSVG />
            <DollarIcon />
            <ColumnItem>{contract?.contractValue}</ColumnItem>
          </RowWrapper>

          <HorizontalLine />

          <RowWrapper>
            <CompanyIcon />
            <ColumnItem>{client?.clientName}</ColumnItem>
            <DotSVG />
            <ColumnItem>
              {(
                project?.contracts?.flatMap(
                  (contract) =>
                    contract?.projectManagers?.map((item) => item?.name) ?? []
                ) ?? []
              ).join(', ')}
            </ColumnItem>
            <DateIcon />
            <ColumnItem>
              {contract?.startDate &&
                new Date(contract?.startDate).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'short',
                  day: '2-digit',
                })}
            </ColumnItem>
            <ColumnItem>{t('TO')}</ColumnItem>
            <DateIcon />
            <ColumnItem>
              {contract?.endDate &&
                new Date(contract.endDate).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'short',
                  day: '2-digit',
                })}
            </ColumnItem>
          </RowWrapper>
        </ClientInfo>

        {contract?.contractId && (
          <ContactTabSection
            contractId={contract?.contractId}
            rawProjectResources={contract?.rawProjectResources}
            description={contract?.description || ''}
          />
        )}
        <TableContainer />
      </LeftSection>

      <RightSection>
        <RightSectionDiv>
          <RightSectionHeading>{t('Client_Details')}</RightSectionHeading>
          {client?.clientId && (
            <ClientInfoWrapper>
              <LogoPreview>
                <img src={logoUrl || undefined} alt="Logo Preview" />
              </LogoPreview>
              <InfoText>
                <div className="id">
                  {t('ID')}: <span>{client?.clientId}</span>
                </div>
                <div className="name">{client?.clientName}</div>
                <div className="industry">{client?.industry}</div>
              </InfoText>
            </ClientInfoWrapper>
          )}
          <IconWrapper>
            <IconItem>
              <CallSVG />
              <span>{client?.contact}</span>
            </IconItem>
            <IconItem>
              <EmailSVG />
              <span>{client?.email}</span>
            </IconItem>
          </IconWrapper>
          <HorizontalLine />
          <RightSubSectionDiv>
            <RightSectionHeading>{t('Project_Details')}</RightSectionHeading>
            <ContractTitleHeader>
              <ProjectSeactionHeading>{project?.name}</ProjectSeactionHeading>
              {project?.status && (
                <StatusDropdown
                  value={project.status}
                  onChange={() => {}}
                  disabled
                />
              )}
            </ContractTitleHeader>
            {project?.projectId && (
              <>
                <ClientInfoWrapper>
                  <div className="name">{project?.projectManagerNames}</div>
                  <DotSVG />
                  <div>{project.projectId}</div>
                </ClientInfoWrapper>
                <ClientInfoWrapper>
                  <DateIcon />
                  <div className="industry">
                    {new Date(project?.startDate).toLocaleDateString('en-US', {
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
