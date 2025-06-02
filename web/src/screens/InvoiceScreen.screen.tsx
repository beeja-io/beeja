import { useEffect, useState } from 'react';

import { AddInvoiceForm } from '../components/directComponents/AddInvoiceForm.component';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import {
  InvoiceInnerBigContainer,
  InvoiceInnersmallContainer,
  InvoiceManagementHeading,
  InvoiceManagementMainContainer,
} from '../styles/InvoiceManagementStyles.style';

import {
  BlueDotDividerSVG,
  ChevronRightSVG,
  MessageIconSVG,
  PhoneIconSVG,
} from '../svgs/CommonSvgs.svs';
import {
  CompanyIconSVG,
  CompanyLogoSVG,
  NotepadSVG,
} from '../svgs/InvoiceSvgs.svg';

import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { CLIENT_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';
import { InvoiceIdentifiers } from '../entities/Requests/InvoiceIdentifiersRequest';
import {
  fetchClientById,
  fetchContractById,
  fetchProjectByIdAndClientId,
  generateInvoiceIdentifiers,
} from '../service/axiosInstance';
import { hasPermission } from '../utils/permissionCheck';

export const InvoiceScreen = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [generatedInvoiceData, setGeneratedInvoiceData] =
    useState<InvoiceIdentifiers | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [contractId, _setContractId] = useState('7A73868');
  const [projectId, _setProjectId] = useState('C36FB9');
  const [contractDetails, setContractDetails] = useState<any>(null);
  const [clientDetails, setClientDetails] = useState<any>(null);
  const [projects, setProjects] = useState<any>([]);

  const { user } = useUser();
  useEffect(() => {
    const loadContractDetails = async () => {
      try {
        const response = await fetchContractById(contractId);
        setContractDetails(response.data);
        if (response.data?.clientId) {
          const clientResponse = await fetchClientById(response.data.clientId);
          setClientDetails(clientResponse.data);

          const projectData = await fetchProjectByIdAndClientId(
            projectId,
            response.data.clientId
          );
          setProjects(projectData);
        }
      } catch (error) {
        // eslint-disable-next-line no-console
        console.log('error');
      }
    };

    if (contractId) {
      loadContractDetails();
    }
  }, [contractId]);

  const handleIsCreateModalOpen = async () => {
    if (!isCreateModalOpen) {
      setIsLoading(true);
      try {
        const response = await generateInvoiceIdentifiers({ contractId });
        setGeneratedInvoiceData(response.data);
        setIsCreateModalOpen(true);
      } catch (error) {
        // eslint-disable-next-line no-console
        console.log('errr');
        setGeneratedInvoiceData(null);
      } finally {
        setIsLoading(false);
      }
    } else {
      setGeneratedInvoiceData(null);
    }
    setIsCreateModalOpen(!isCreateModalOpen);
  };

  return (
    <>
      <InvoiceManagementHeading>
        <span>Contract Management</span>&nbsp;
        <ChevronRightSVG />
        &nbsp;
        <span className="highlight">Contract Details</span>
      </InvoiceManagementHeading>
      <InvoiceManagementMainContainer>
        <InvoiceInnerBigContainer>
          <div className="Project_Heading">
            <span className="projectName">
              {contractDetails?.contractTitle || ' '}
            </span>
            <span>{[projects.status]}</span>
          </div>
          <div className="projectDetails">
            &nbsp; <BlueDotDividerSVG /> &nbsp;
            <span>
              {contractDetails?.contractId}
              <span>
                &nbsp; <BlueDotDividerSVG /> &nbsp;
              </span>{' '}
              Fixed Price Contract{' '}
              <span>
                &nbsp; <BlueDotDividerSVG /> &nbsp;
              </span>{' '}
              Billable{' '}
              <span>
                &nbsp; <BlueDotDividerSVG /> &nbsp;
              </span>{' '}
              <span> $30000</span>
            </span>
          </div>
          <div className="projectDetails">
            <div>
              <span>
                <NotepadSVG />
              </span>
              <span> {projects?.name}</span>
            </div>
            <div>
              <span>
                <CompanyIconSVG />
              </span>
              <span>{clientDetails?.clientName}</span>
            </div>
          </div>

          {user && hasPermission(user, CLIENT_MODULE.GENERATE_INVOICE) && (
            <button
              className="button_element"
              onClick={handleIsCreateModalOpen}
            >
              {isLoading ? <SpinAnimation /> : 'Generate Invoice'}
            </button>
          )}
        </InvoiceInnerBigContainer>
        <InvoiceInnersmallContainer>
          <span style={{ fontSize: '13px' }}>Client Details</span>
          <div className="clientDetails">
            <CompanyLogoSVG />
            <div className="align">
              <span>
                ID:<span className="text">{contractDetails?.clientId}</span>
              </span>
              <span style={{ fontWeight: 'bold', margin: '5px 0px' }}>
                {clientDetails?.billingAddress.state}
              </span>
              <span style={{ fontSize: '12px' }}>
                {clientDetails?.clientName}
              </span>
            </div>
          </div>
          <div>
            <span>
              <PhoneIconSVG />
              {clientDetails?.contact}
            </span>
            &nbsp;
            <span>
              <MessageIconSVG />
              {clientDetails?.email}
            </span>
          </div>
        </InvoiceInnersmallContainer>
      </InvoiceManagementMainContainer>

      {isCreateModalOpen && (
        <CenterModalMain
          heading="Generate Invoice"
          modalClose={handleIsCreateModalOpen}
          actualContentContainer={
            <AddInvoiceForm
              handleClose={handleIsCreateModalOpen}
              invoiceId={generatedInvoiceData?.invoiceId}
              remittanceReferenceNumber={
                generatedInvoiceData?.remittanceReferenceNumber
              }
              contractId={contractId}
              contractTitle={contractDetails?.contractTitle}
              startDate={contractDetails?.startDate}
              endDate={contractDetails?.endDate}
              billingAddress={clientDetails?.billingAddress}
              clientName={clientDetails?.clientName}
            />
          }
        />
      )}
    </>
  );
};
