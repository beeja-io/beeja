import React, { useEffect, useState } from 'react';
import {
  ClientTitle,
  Container,
  LeftSection,
  LogoPreview,
} from '../styles/ClientStyles.style';

import {
  CallSVG,
  CompanyIcon,
  DateIcon,
  DollarIcon,
  RupeeIcon,
  EuroIcon,
  DotSVG,
  EmailSVG,
} from '../svgs/ClientManagmentSvgs.svg';

import { t } from 'i18next';
import { useParams } from 'react-router-dom';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ClientResponse } from '../entities/ClientEntity';
import { ContractDetails, ContractType } from '../entities/ContractEntiy';
import { ProjectEntity } from '../entities/ProjectEntity';
import { AddInvoiceForm } from '../components/directComponents/AddInvoiceForm.component';
import {
  downloadClientLogo,
  generateInvoiceIdentifiers,
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
  ContractInfo,
} from '../styles/ContractStyle.style';
import StatusDropdown from '../styles/ProjectStatusStyle.style';
import { InfoText } from '../styles/ProjectStyles.style';
import ContactTabSection from './ContractTabSection';
import { CLIENT_MODULE } from '../constants/PermissionConstants';
import { hasPermission } from '../utils/permissionCheck';
import { useUser } from '../context/UserContext';
import { InvoiceIdentifiers } from '../entities/Requests/InvoiceIdentifiersRequest';
import { InvoiceInnerBigContainer } from '../styles/InvoiceManagementStyles.style';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import { BillingCurrency } from '../components/reusableComponents/ContractEnums.component';
import { useFeatureToggles } from '../context/FeatureToggleContext';
import { hasFeature } from '../utils/featureCheck';
import { EFeatureToggles } from '../entities/FeatureToggle';

const ContractDetailsScreen: React.FC = () => {
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const { id } = useParams();
  const { user } = useUser();
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [project, setProject] = useState<ProjectEntity | null>(null);
  const [contract, setContract] = useState<ContractDetails | null>(null);

  const [clientId, setClientId] = useState<string | null>(null);
  const [projectId, setProjectId] = useState<string | null>(null);
  const [generatedInvoiceData, setGeneratedInvoiceData] =
    useState<InvoiceIdentifiers | null>(null);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [contractId, setContractId] = useState<string | null>(null);
  const [contractDetails, setContractDetails] = useState<any>(null);
  const [clientDetails, setClientDetails] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(false);

  const formatEnum = (value?: string) => {
    if (!value) return '';
    return value
      .toLowerCase()
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };
  const { featureToggles } = useFeatureToggles();

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
        setContractId(res?.data?.contractId);
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
        setClientDetails(clientRes.data);
        setContractDetails(clientRes.data);
      } catch (error) {
        toast.error('Failed_to_fetch_project/client: ');
      } finally {
        setIsLoading(false);
      }
    };
    fetchProjectAndClient();
  }, [projectId, clientId]);

  const handleIsCreateModalOpen = async (contractId: string) => {
    if (!isCreateModalOpen) {
      setIsLoading(true);
      try {
        const response = await generateInvoiceIdentifiers(contractId);
        setGeneratedInvoiceData(response.data);
        setIsCreateModalOpen(true);
      } catch (error) {
        setGeneratedInvoiceData(null);
      } finally {
        setIsLoading(false);
      }
    } else {
      setGeneratedInvoiceData(null);
    }
    setIsCreateModalOpen(!isCreateModalOpen);
  };

  useEffect(() => {
    if (isCreateModalOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [isCreateModalOpen]);

  if (isLoading) {
    return <SpinAnimation />;
  }
  return (
    <Container>
      <LeftSection>
        <ContractInfo>
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
            <ColumnItem>
              {contract?.contractType === ContractType.OTHER
                ? contract?.customContractType
                : formatEnum(contract?.contractType)}
            </ColumnItem>
            <DotSVG />
            <ColumnItem>{formatEnum(contract?.billingType)}</ColumnItem>
            {contract?.contractValue && (
              <>
                <DotSVG />
                {contract?.billingCurrency === BillingCurrency.DOLLER && (
                  <DollarIcon />
                )}
                {contract?.billingCurrency === BillingCurrency.EURO && (
                  <EuroIcon />
                )}
                {contract?.billingCurrency === BillingCurrency.INR && (
                  <RupeeIcon />
                )}
                <ColumnItem>{contract?.contractValue}</ColumnItem>
              </>
            )}
          </RowWrapper>

          <HorizontalLine />

          <RowWrapper>
            <CompanyIcon />
            <ColumnItem>{client?.clientName}</ColumnItem>
            <DotSVG />
            <ColumnItem>
              {[...new Set(contract?.projectManagers ?? [])].join(', ')}
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
            {contract?.endDate && (
              <>
                <ColumnItem>{t('TO')}</ColumnItem>
                <DateIcon />
                <ColumnItem>
                  {new Date(contract.endDate).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: '2-digit',
                  })}
                </ColumnItem>
              </>
            )}
          </RowWrapper>
          <HorizontalLine />
          <InvoiceInnerBigContainer>
            {user &&
              hasPermission(user, CLIENT_MODULE.GENERATE_INVOICE) &&
              hasFeature(
                featureToggles?.featureToggles ?? [],
                EFeatureToggles.INVOICE_GENERATION
              ) && (
                <button
                  className="button_element"
                  disabled={contract?.billingType === 'NON_BILLABLE'}
                  title={
                    contract?.billingType === 'NON_BILLABLE'
                      ? 'It is a non-billable contract'
                      : ''
                  }
                  onClick={() => {
                    if (contract?.contractId) {
                      handleIsCreateModalOpen(contract.contractId);
                    }
                  }}
                >
                  {isLoading ? <SpinAnimation /> : 'Generate Invoice'}
                </button>
              )}
          </InvoiceInnerBigContainer>
        </ContractInfo>

        {contract?.contractId && (
          <ContactTabSection
            contractId={contract?.contractId}
            rawProjectResources={contract?.rawProjectResources}
            description={contract?.description || ''}
            contractName={contract?.contractTitle}
          />
        )}
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
                  <div className="name">
                    {project?.projectManagerNames?.join(', ')}
                  </div>
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
      {isCreateModalOpen && (
        <CenterModalMain
          heading="Generate Invoice"
          modalClose={() => contractId && handleIsCreateModalOpen(contractId)}
          actualContentContainer={
            <AddInvoiceForm
              handleClose={() =>
                contractId && handleIsCreateModalOpen(contractId)
              }
              invoiceId={generatedInvoiceData?.invoiceId}
              remittanceReferenceNumber={
                generatedInvoiceData?.remittanceReferenceNumber
              }
              contractId={contractId ?? ''}
              contractTitle={contract?.contractTitle}
              startDate={contract?.startDate}
              endDate={contract?.endDate}
              billingAddress={clientDetails?.billingAddress}
              clientName={clientDetails?.clientName}
              organizationId={contractDetails?.organizationId}
              projectId={projectId ?? undefined}
              status={project?.status}
              clientId={clientDetails?.clientId}
              billingCurrency={contractDetails?.billingCurrency}
            />
          }
        />
      )}
    </Container>
  );
};

export default ContractDetailsScreen;
