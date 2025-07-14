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
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

interface Props {
  client: ClientResponse | null;
}

const ClientDetailsScreen: React.FC = () => {
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const { id } = useParams();
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [isAddProjectModalOpen, setIsAddProjectModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleAddProjectModalToggle = () => {
    setIsAddProjectModalOpen((prev) => !prev);
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

  if (loading) {
    return <SpinAnimation />;
  }

  return (
    <Container>
      <LeftSection>
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
            <ClientInfoDiv style={{ width: '100px', paddingRight: '10px' }}>
              ID: {client?.clientId}
            </ClientInfoDiv>
            <DotSVG />
            <ClientInfoDiv style={{ width: '150px' }}>
              {client?.clientType}
            </ClientInfoDiv>

            <DotSVG />
            <IndustrySVG />
            <ClientInfoDiv style={{ width: '100px', paddingRight: '10px' }}>
              {client?.industry}
            </ClientInfoDiv>
            <DotSVG />
            <EmailSVG />

            <ClientInfoDiv style={{ width: '200px', wordWrap: 'break-word' }}>
              {client?.email}
            </ClientInfoDiv>
            <DotSVG />
            <CallSVG />
            <ClientInfoDiv style={{ width: '100px' }}>
              {t('91+')}
              {client?.contact}
            </ClientInfoDiv>
          </div>
        </ClientInfo>
        <TableContainer></TableContainer>
      </LeftSection>

      <RightSection>
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
      {/* {isAddProjectModalOpen && client?.clientId && (
        <CenterModalMain
          heading="ADD_NEW_PROJECT"
          modalClose={handleAddProjectModalToggle}
          actualContentContainer={
            <AddProjectForm
              handleClose={handleAddProjectModalToggle}
              onCancel={handleAddProjectModalToggle}
              onSubmit={() => {
                handleAddProjectModalToggle();
              }}
              clientId={client.clientId}
            />
          }
        />
      )} */}
    </Container>
  );
};

export default ClientDetailsScreen;
