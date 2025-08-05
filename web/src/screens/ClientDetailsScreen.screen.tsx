import React, { useEffect, useState } from 'react';
import {
  AddressDiv,
  ClientInfo,
  ClientInfoDiv,
  ClientInfoRowItem,
  ClientInfoSection,
  ClientTitle,
  Container,
  DotWrapper,
  LeftSection,
  LogoNameWrapper,
  LogoPreview,
  RightSection,
  RightSectionDiv,
  TaxDetailsWrapper,
  TaxItem,
  TaxLabel,
  TaxValue,
} from '../styles/ClientStyles.style';

import {
  CallSVG,
  DotSVG,
  EmailSVG,
  IndustrySVG,
} from '../svgs/ClientManagmentSvgs.svg';

import { t } from 'i18next';
import { useParams } from 'react-router-dom';
import { toast } from 'sonner';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { ClientResponse } from '../entities/ClientEntity';
import { downloadClientLogo, getClient } from '../service/axiosInstance';
import ClientTabsSection from './ClientTabSection.screen';

const ClientDetailsScreen: React.FC = () => {
  const [logoUrl, setLogoUrl] = useState<string | null>(null);
  const { id } = useParams();
  const [client, setClient] = useState<ClientResponse | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchLogoImage = async () => {
      if (client?.logoId) {
        setLoading(true);
        try {
          const response = await downloadClientLogo(client.logoId);

          if (!response.data || response.data.size === 0) {
            toast.error('Received empty or invalid blob data');
          }

          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;
            setLogoUrl(imageUrl);
            setLoading(false);
          };
          reader.onerror = () => {
            toast.error('Error converting blob to base64');
          };

          reader.readAsDataURL(response.data);
        } catch (error) {
          toast.error('fetching logo');
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
        toast.error('Failed to fetch client');
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
          <ClientInfoSection>
            <ClientInfoRowItem>
              <ClientInfoDiv>
                {t('ID')}: {client?.clientId}
              </ClientInfoDiv>
            </ClientInfoRowItem>

            <DotWrapper>
              <DotSVG />
            </DotWrapper>

            <ClientInfoRowItem>
              <ClientInfoDiv>{client?.clientType}</ClientInfoDiv>
            </ClientInfoRowItem>

            <DotWrapper>
              <DotSVG />
            </DotWrapper>

            <ClientInfoRowItem>
              <IndustrySVG />
              <ClientInfoDiv>{client?.industry}</ClientInfoDiv>
            </ClientInfoRowItem>

            <DotWrapper>
              <DotSVG />
            </DotWrapper>

            <ClientInfoRowItem>
              <EmailSVG />
              <ClientInfoDiv>{client?.email}</ClientInfoDiv>
            </ClientInfoRowItem>

            <DotWrapper>
              <DotSVG />
            </DotWrapper>

            <ClientInfoRowItem>
              <CallSVG />
              <ClientInfoDiv>
                {t('91+')}
                {client?.contact}
              </ClientInfoDiv>
            </ClientInfoRowItem>
          </ClientInfoSection>
        </ClientInfo>
        {client?.clientId && <ClientTabsSection clientId={client.clientId} />}
      </LeftSection>

      <RightSection>
        <RightSectionDiv>
          <div>{t('Primary Address')}</div>
          <AddressDiv>
            {client?.primaryAddress?.street},{client?.primaryAddress?.city},
            {client?.primaryAddress?.state},{client?.primaryAddress?.country},
            {client?.primaryAddress?.postalCode}
          </AddressDiv>
        </RightSectionDiv>
        <RightSectionDiv>
          <div>{t('Billing Address')}</div>
          <AddressDiv>
            {client?.primaryAddress?.street},{client?.primaryAddress?.city},
            {client?.primaryAddress?.state},{client?.primaryAddress?.country},
            {client?.primaryAddress?.postalCode}
          </AddressDiv>
        </RightSectionDiv>
        <RightSectionDiv>
          <div>{t('Tax Details')}</div>
          <TaxDetailsWrapper>
            <TaxItem>
              <TaxLabel>{t('VAT/ GAT Number')}:</TaxLabel>
              <TaxValue>{client?.taxDetails?.taxNumber ?? '-'}</TaxValue>
            </TaxItem>
            <TaxItem>
              <TaxLabel>{t('Tax Category')}:</TaxLabel>
              <TaxValue>{client?.taxDetails?.taxCategory ?? '-'}</TaxValue>
            </TaxItem>
          </TaxDetailsWrapper>
        </RightSectionDiv>
      </RightSection>
    </Container>
  );
};

export default ClientDetailsScreen;
