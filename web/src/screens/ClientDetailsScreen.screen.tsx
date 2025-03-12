import React from 'react';
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
 AddressDiv
 } from '../styles/ClientStyles.style';

 import { DotSVG, IndustrySVG,EmailSVG, CallSVG,AddSVG} from '../svgs/ClientSvgs.svs';

import { ClientResponse } from '../entities/ClientEntity';
import { t } from 'i18next';

interface Props {
    client: ClientResponse| null;
  }

const ClientDetailsScreen: React.FC<Props> = ({ client}) => {
  return (
    <Container>
      
      <LeftSection>
        <ClientInfo >
          <ClientTitle>
          {client?.clientName}
          </ClientTitle>
          <div
          style={{display:'flex',marginBottom:'30px'}}
          >
           <ClientInfoDiv>
                {client?.clientId}
                </ClientInfoDiv> 
            <ClientInfoDiv style={{width:'150px'}}>
                {client?.clientType }
                </ClientInfoDiv>
                
                <DotSVG/>
                <IndustrySVG/>
                <ClientInfoDiv style={{width: "100px",paddingRight:"10px"}}>
                  {client?.industry}
                  </ClientInfoDiv>
                <DotSVG/>
                <EmailSVG/>

                <ClientInfoDiv style={{width: "200px",wordWrap: "break-word"}}>
                  {client?.email }
                  </ClientInfoDiv>
                <DotSVG/>
                <CallSVG/>
             <ClientInfoDiv style={{width:'100px'}}>
             {t('91+')}{client?.contact}
             </ClientInfoDiv>
          </div>
          <div
          style={{display:"flex"}}
          >
            <div>
          <AddSVG/>
                </div>
                <ProjectInfo>
                {t(' Add Project')}
                  </ProjectInfo>
                <div>
                        <AddSVG/>
                </div>
                <ProjectInfo>
                  {t('Add Contract')}
                  </ProjectInfo>
                          </div>
                        
                        </ClientInfo>
                        <TableContainer>
                        </TableContainer>
                      </LeftSection>

                      <RightSection>
                      <RightSectionDiv>
                  <div>Primary Address</div>
                  <AddressDiv>
                    {client?.primaryAddress.street},
                    {client?.primaryAddress.city},
                    {client?.primaryAddress.state},
                    {client?.primaryAddress.country},
                    {client?.primaryAddress.postalCode}
                    </AddressDiv>
                  </RightSectionDiv>
                <RightSectionDiv>
                  <div>Billing Address</div>
                  <AddressDiv>
                    {client?.primaryAddress.street},
                    {client?.primaryAddress.city},
                    {client?.primaryAddress.state},
                    {client?.primaryAddress.country},
                    {client?.primaryAddress.postalCode}
                    </AddressDiv>
                  
                    </RightSectionDiv>
                <RightSectionDiv>
                  <div>{t('Tax Details')}</div>
                  <div
                  style={{display:"flex"}}>
                <AddressDiv style={{width:'130px'}}>
                  {t(' VAT/ GAT Number')}
                    </AddressDiv>
                    <AddressDiv style={{paddingLeft:'20px'}}>
                    {client?.taxDetails.taxNumber}
                    </AddressDiv>
                    </div>
                    <div
                  style={{display:"flex"}}>
                  <AddressDiv style={{width:'130px'}}>
                  {t(' Tax Category')}
                    </AddressDiv>
                    <AddressDiv style={{paddingLeft:'20px'}}>
                    {client?.taxDetails.taxCategory}
                    </AddressDiv>
                    </div>
                  
                    </RightSectionDiv>

                      </RightSection>
                    </Container>
                  );
};

export default ClientDetailsScreen;
