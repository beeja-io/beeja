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
// import { downloadClientLogo } from '../service/axiosInstance';

interface Props {
  client: ClientResponse | null;
}

const ClientDetailsScreen: React.FC<Props> = ({ client }) => {
  // const [logoUrl, setLogoUrl] = useState<string | null>(null);

  // useEffect(() => {
  //   const fetchLogoImage = async () => {
  //     if (client?.logoId) {
  //       try {
  //         const response = await downloadClientLogo(client.logoId);

  //         if (!response.data || response.data.size === 0) {
  //           throw new Error('Received empty or invalid blob data');
  //         }

  //         const reader = new FileReader();
  //         reader.onloadend = () => {
  //           const imageUrl = reader.result as string;
  //           setLogoUrl(imageUrl);
  //         };
  //         reader.onerror = () => {
  //           throw new Error('Error converting blob to base64');
  //         };

  //         reader.readAsDataURL(response.data);
  //       } catch (error) {
  //         console.error('Error fetching logo:', error);
  //       }
  //     }
  //   };

  //   fetchLogoImage();

  //   return () => {
  //     setLogoUrl(null);
  //   };
  // }, [client?.logoId]);
  return (
    <Container>
      <LeftSection>
        <ClientInfo>
          {client?.logoId && (
            <LogoPreview>
              <img
                src={client?.logoId}
                // src={logoUrl}
                alt="Logo Preview"
                style={{
                  marginTop: '15px',
                  maxHeight: '64px',
                  objectFit: 'contain',
                }}
              />
            </LogoPreview>
          )}
          <ClientTitle> {client?.clientName}</ClientTitle>
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
          <div style={{ display: 'flex' }}>
            <div>
              <AddSVG />
            </div>
            <ProjectInfo>{t(' Add Project')}</ProjectInfo>
            <div>
              <AddSVG />
            </div>
            <ProjectInfo>{t('Add Contract')}</ProjectInfo>
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
    </Container>
  );
};

export default ClientDetailsScreen;
