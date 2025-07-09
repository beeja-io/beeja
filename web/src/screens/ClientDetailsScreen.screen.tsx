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
  RightSectionDiv,
  AddressDiv,
  LogoPreview,
  LogoNameWrapper,
  ButtonGroup,
  ActionButton,
} from '../styles/ClientStyles.style';

import {
  DotSVG,
  IndustrySVG,
  EmailSVG,
  CallSVG,
  AddSVG,
} from '../svgs/ClientSvgs.svs';

import { ClientResponse } from '../entities/ClientEntity';
import { t } from 'i18next';
import { downloadClientLogo, getClient } from '../service/axiosInstance';
import { useParams } from 'react-router-dom';
import AddProjectForm from '../components/directComponents/AddProjectForm.component';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import ClientTabsSection from './ClientTabSection.screen';
import AddContractForm from '../components/directComponents/AddContractForm.component';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

const ClientDetailsScreen: React.FC = () => {
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const { id } = useParams();
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [isAddProjectModalOpen, setIsAddProjectModalOpen] = useState(false);
  const [isAddContractModalOpen, setIsAddContractModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleAddProjectModalToggle = () => {
    setIsAddProjectModalOpen((prev) => !prev);
  };

  const handleAddContractModalToggle = () => {
    setIsAddContractModalOpen((prev) => !prev);
  };

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (client?.logoId) {
        setLoading(true);
        try {
          const response = await downloadClientLogo(client.logoId);

          if (!response.data || response.data.size === 0) {
            throw new Error('Received empty or invalid blob data');
          }

          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;
            setLogoUrl(imageUrl);
            setLoading(false);
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
      if (!id) return;
      setLoading(true);
      try {
        const res = await getClient(id);
        setClient(res.data);
      } catch (error) {
        throw new Error('Failed to fetch client:' + error);
      } finally {
        setLoading(false);
      }
    };
    fetchClient();
  }, [id]);

  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const handleShowSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => {
      setShowSuccessMessage(false);
    }, 2000);
  };
  const handleSuccessMessage = () => {
    handleShowSuccessMessage();
    setIsAddProjectModalOpen(false);
  };

  if (loading) {
    return <SpinAnimation />;
  }

  return (
    <Container
      style={{
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        gap: '20px',
      }}
    >
      <LeftSection style={{ flex: 2, minWidth: '750px' }}>
        <ClientInfo>
          <LogoNameWrapper>
            {client?.logoId && (
              <LogoPreview>
                <img src={logoUrl || undefined} alt="Logo Preview" />
              </LogoPreview>
            )}
            <ClientTitle> {client?.clientName}</ClientTitle>
          </LogoNameWrapper>
          <div style={{ display: 'flex', marginBottom: '30px' }}>
            {client?.clientId && (
              <>
                <ClientInfoDiv style={{ width: '100px', paddingRight: '10px' }}>
                  ID: {client.clientId}
                </ClientInfoDiv>
              </>
            )}
            {client?.clientType && (
              <>
                <DotSVG />
                <ClientInfoDiv style={{ width: '120px' }}>
                  {client.clientType}
                </ClientInfoDiv>
              </>
            )}
            {client?.industry && (
              <>
                <DotSVG />
                <IndustrySVG />
                <ClientInfoDiv style={{ width: '130px', paddingRight: '10px' }}>
                  {client.industry}
                </ClientInfoDiv>
              </>
            )}
            {client?.email && (
              <>
                <DotSVG />
                <EmailSVG />
                <ClientInfoDiv
                  style={{ width: '160px', wordWrap: 'break-word' }}
                >
                  {client.email}
                </ClientInfoDiv>
              </>
            )}
            {client?.contact && (
              <>
                <DotSVG />
                <CallSVG />
                <ClientInfoDiv style={{ width: '100px' }}>
                  {t('91+')}
                  {client.contact}
                </ClientInfoDiv>
              </>
            )}
          </div>

          <ButtonGroup>
            <ActionButton onClick={handleAddProjectModalToggle}>
              <AddSVG />
              <ProjectInfo>{t('Add Project')}</ProjectInfo>
            </ActionButton>

            <ActionButton onClick={handleAddContractModalToggle}>
              <AddSVG />
              <ProjectInfo>{t('Add Contract')}</ProjectInfo>
            </ActionButton>
          </ButtonGroup>
        </ClientInfo>

        {client?.clientId && <ClientTabsSection clientId={client?.clientId} />}
      </LeftSection>

      <RightSection style={{ flex: 1, minWidth: '300px' }}>
        <RightSectionDiv>
          <div>Primary Address</div>
          <AddressDiv>
            {client?.primaryAddress?.street},{client?.primaryAddress?.city},
            {client?.primaryAddress?.state},{client?.primaryAddress?.country},
            {client?.primaryAddress?.postalCode}
          </AddressDiv>
        </RightSectionDiv>
        <RightSectionDiv>
          <div>Billing Address</div>
          <AddressDiv>
            {client?.primaryAddress?.street},{client?.primaryAddress?.city},
            {client?.primaryAddress?.state},{client?.primaryAddress?.country},
            {client?.primaryAddress?.postalCode}
          </AddressDiv>
        </RightSectionDiv>
        <RightSectionDiv>
          <div>{t('Tax Details')}</div>
          <div style={{ display: 'flex' }}>
            <AddressDiv style={{ width: '130px' }}>
              {t(' VAT/ GAT Number')}
            </AddressDiv>
            <AddressDiv style={{ paddingLeft: '20px' }}>
              {client?.taxDetails?.taxNumber ?? '-'}
            </AddressDiv>
          </div>
          <div style={{ display: 'flex' }}>
            <AddressDiv style={{ width: '130px' }}>
              {t(' Tax Category')}
            </AddressDiv>
            <AddressDiv style={{ paddingLeft: '20px' }}>
              {client?.taxDetails?.taxCategory ?? '-'}
            </AddressDiv>
          </div>
        </RightSectionDiv>
      </RightSection>
      {isAddProjectModalOpen && client?.clientId && (
        <CenterModalMain
          heading="ADD_NEW_PROJECT"
          modalClose={handleAddProjectModalToggle}
          actualContentContainer={
            <AddProjectForm
              handleClose={handleAddProjectModalToggle}
              onCancel={handleAddProjectModalToggle}
              handleSuccessMessage={handleSuccessMessage}
              onSubmit={() => {
                handleAddProjectModalToggle();
              }}
              client={client}
            />
          }
        />
      )}
      {isAddContractModalOpen && client?.clientId && (
        <CenterModalMain
          heading="ADD_NEW_CONTRACT"
          modalClose={handleAddContractModalToggle}
          actualContentContainer={
            <AddContractForm
              handleClose={handleAddContractModalToggle}
              handleSuccessMessage={handleSuccessMessage}
              onSubmit={() => {
                handleAddContractModalToggle();
              }}
              client={client}
            />
          }
        />
      )}
    </Container>
  );
};

export default ClientDetailsScreen;
