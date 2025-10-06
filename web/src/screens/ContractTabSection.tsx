import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
} from '../styles/ProjectTabSectionStyles.style';
import { PdfSVG } from '../svgs/InvoiceSvgs.svg';
import { getInvoicesBycontractId } from '../service/axiosInstance';
import { DeleteIcon, DownloadIcon } from '../svgs/DocumentTabSvgs.svg';
import { DocumentAction } from '../components/reusableComponents/DocumentAction';
import PulseLoader from '../components/loaders/PulseAnimation.loader';
import {
  InvId,
  PdfCell,
  PdfWrapper,
} from '../styles/InvoiceManagementStyles.style';
import { toast } from 'sonner';

interface RawProjectResource {
  employeeId: string;
  name: string;
  allocationPercentage: number;
}
interface Invoice {
  contractId: string;
  organizationId: string;
  notes: any;
  invoicePeriod: any;
  invoiceId: string;
  contractName: string;
  createdAt: string;
  createdByName: string;
  invoiceFileId: string;
}
interface ContactTabSectionProps {
  contractId: string;
  description: string;
  rawProjectResources: RawProjectResource[];
  contractName: string;
}
interface ActionType {
  title: string;
  svg: React.ReactNode;
}

const ContactTabSection: React.FC<ContactTabSectionProps> = ({
  contractId,
  description,
  rawProjectResources,
  contractName,
}) => {
  const [activeTab, setActiveTab] = useState<
    'Resources' | 'Invoices' | 'Description'
  >('Resources');
  const { t } = useTranslation();
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loadingInvoices, setLoadingInvoices] = useState(false);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  const Actions: ActionType[] = [
    { title: 'Download', svg: <DownloadIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];

  useEffect(() => {
    if (activeTab === 'Invoices') {
      fetchInvoices();
    }
  }, [activeTab]);

  const fetchInvoices = async () => {
    setLoadingInvoices(true);
    try {
      const response = await getInvoicesBycontractId(contractId);
      setInvoices(response.data || []);
    } catch (error) {
      toast.error(t('Failed to fetch the invoices'));
    } finally {
      setLoadingInvoices(false);
    }
  };

  const renderDocumentAction = (
    item: any,
    entityType: 'invoice' | 'contract',
    fetchCallback: () => void,
    options: ActionType[]
  ) => {
    const fileObj = {
      id: item.fileId || item.invoiceFileId || item.attachmentId,
      entityId: item.entityId || item.contractId || '',
      name: item.name || item.fileName || item.invoiceId || '',
      organizationId: item.organizationId || '',
      description: item.description || item.notes?.join(', ') || '',
      entityType: entityType,
      fileFormat: item.fileFormat || 'pdf',
      fileSize: item.fileSize || '',
      fileType: item.fileType || entityType,
      createdAt: item.createdAt || item.uploadedAt || '',
      createdBy: item.createdByName || item.uploadedBy || '',
      createdByName: item.createdByName || item.uploadedBy || '',
      modifiedAt: item.modifiedAt || new Date(),
      modifiedBy: item.modifiedBy || item.createdByName || item.uploadedBy,
    };

    return (
      <DocumentAction
        options={options}
        fileId={fileObj.id}
        fetchFiles={fetchCallback}
        fileName={fileObj.name}
        fileExtension={fileObj.fileFormat}
        file={fileObj}
      />
    );
  };

  return (
    <Container>
      <Tabs>
        {[t('Resources'), t('Invoices'), t('Description')].map(
          (tabLabel, index) => {
            const tabKey =
              index === 0
                ? 'Resources'
                : index === 1
                  ? 'Invoices'
                  : 'Description';
            return (
              <Tab
                key={tabKey}
                active={activeTab === tabKey}
                onClick={() => setActiveTab(tabKey as any)}
              >
                {tabLabel}
              </Tab>
            );
          }
        )}
      </Tabs>

      <TabContent>
        {activeTab === 'Resources' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('EMPLOYEE_ID')}</th>
                <th>{t('Name')}</th>
                <th>{t('Allocation')}</th>
              </tr>
            </thead>
            <tbody>
              {rawProjectResources && rawProjectResources.length > 0 ? (
                rawProjectResources.map((resource, index) => (
                  <tr key={index}>
                    <td>{resource.employeeId}</td>
                    <td>{resource.name}</td>
                    <td>{resource.allocationPercentage}%</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={3} style={{ textAlign: 'center' }}>
                    {t('No  Resources  Found')}
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Invoices' && (
          <ProjectsTable>
            <thead>
              <tr>
                <th>{t('Invoice ID')}</th>
                <th>{t('Contract')}</th>
                <th>{t('Generated By')}</th>
                <th>{t('Generated On')}</th>
                <th>{t('Action')}</th>
              </tr>
            </thead>
            <tbody>
              {loadingInvoices ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center' }}>
                    <PulseLoader height="300px" />
                  </td>
                </tr>
              ) : invoices.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center' }}>
                    {t('No invoices found')}
                  </td>
                </tr>
              ) : (
                invoices.map((invoice) => (
                  <tr key={invoice.invoiceId}>
                    <PdfCell>
                      <PdfWrapper>
                        <PdfSVG />
                        <InvId>{invoice.invoiceId}</InvId>
                      </PdfWrapper>
                    </PdfCell>
                    <td>{contractName}</td>
                    <td>{invoice.createdByName || '-'}</td>
                    <td>{formatDate(invoice.createdAt)}</td>
                    <td>
                      {renderDocumentAction(
                        invoice,
                        'invoice',
                        fetchInvoices,
                        Actions
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Description' && <div>{description}</div>}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
