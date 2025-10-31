import React, { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Container,
  ProjectsTable,
  Tab,
  TabContent,
  Tabs,
  AttachmentList,
  AttachmentItem,
  AttachmentInfo,
  FileDetails,
  FileName,
  FileMeta,
  NoAttachments,
  LoaderContainer,
  NoDataContainer,
} from '../styles/ProjectTabSectionStyles.style';
import {
  InvId,
  PdfCell,
  PdfWrapper,
} from '../styles/InvoiceManagementStyles.style';
import { toast } from 'sonner';
import {
  getAllAttachmentsByContractId,
  getInvoicesBycontractId,
} from '../service/axiosInstance.tsx';
import {
  PdfSVG,
  ExcelIconSVG,
  DownloadIcon,
  DeleteIcon,
} from '../svgs/DocumentTabSvgs.svg.tsx';
import { FileEntity } from '../entities/FileEntity.tsx';
import PulseLoader from '../components/loaders/PulseAnimation.loader';
import { DocumentAction } from '../components/reusableComponents/DocumentAction.tsx';
import { useUser } from '../context/UserContext';
import { hasPermission } from '../utils/permissionCheck';
import { CLIENT_MODULE } from '../constants/PermissionConstants';
import { hasFeature } from '../utils/featureCheck';
import { EFeatureToggles } from '../entities/FeatureToggle';
import { useFeatureToggles } from '../context/FeatureToggleContext';

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

interface Attachment {
  fileId: string;
  name: string;
  fileSize?: string;
  uploadedBy?: string;
  uploadedOn?: string;
  createdBy?: string;
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
  const { t } = useTranslation();
  const { user } = useUser();
  const { featureToggles } = useFeatureToggles();

  const [activeTab, setActiveTab] = useState<
    'Resources' | 'Invoices' | 'Attachments' | 'Description'
  >(() => {
    return (
      (sessionStorage.getItem(`contractTab_${contractId}`) as
        | 'Resources'
        | 'Invoices'
        | 'Attachments'
        | 'Description') || 'Resources'
    );
  });

  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loadingInvoices, setLoadingInvoices] = useState(false);

  useEffect(() => {
    sessionStorage.setItem(`contractTab_${contractId}`, activeTab);
    if (activeTab === 'Attachments') {
      fetchAttachments();
    } else if (activeTab === 'Invoices') {
      fetchInvoices();
    }
  }, [activeTab, contractId]);
  const [attachments, setAttachments] = useState<Attachment[]>([]);
  const [loadingAttachments, setLoadingAttachments] = useState(false);
  const triggerRefs = useRef<Map<string, HTMLDivElement | null>>(new Map());

  const commonActions: ActionType[] = [
    { title: 'Download', svg: <DownloadIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];
  useEffect(() => {
    if (activeTab === 'Invoices') fetchInvoices();
    if (activeTab === 'Attachments') fetchAttachments();
  }, [activeTab]);
  const fetchInvoices = async () => {
    setLoadingInvoices(true);
    try {
      const response = await getInvoicesBycontractId(contractId);
      setInvoices(response.data || []);
    } catch {
      toast.error(t('Failed to fetch the invoices'));
    } finally {
      setLoadingInvoices(false);
    }
  };

  const fetchAttachments = async () => {
    setLoadingAttachments(true);
    try {
      const response = await getAllAttachmentsByContractId(contractId);
      const files =
        response.data.files?.map((file: any) => ({
          fileId: file.id,
          name: file.name,
          fileSize: file.fileSize || '',
          uploadedBy: file.createdByName || '',
          uploadedOn: file.createdAt
            ? new Date(file.createdAt).toLocaleDateString('en-GB', {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
              })
            : '',
          createdBy: file.createdBy,
        })) || [];
      setAttachments(files);
    } catch {
      setAttachments([]);
    } finally {
      setLoadingAttachments(false);
    }
  };
  const renderDocumentAction = (
    item: any,
    entityType: 'invoice' | 'contract',
    fetchCallback: () => void
  ) => {
    const fileObj: FileEntity = {
      id: item.fileId || item.invoiceFileId,
      entityId: item.contractId || contractId,
      name: item.name || item.invoiceId,
      organizationId: item.organizationId || '',
      description: item.description || item.notes?.join(', ') || '',
      entityType: entityType,
      fileFormat: item.name?.split('.').pop() || 'pdf',
      fileSize: item.fileSize || '',
      fileType: entityType,
      createdAt: item.createdAt || new Date().toISOString(),
      createdBy: item.createdByName || item.uploadedBy || '',
      createdByName: item.createdByName || item.uploadedBy || '',
      modifiedAt: item.modifiedAt || new Date(),
      modifiedBy: item.modifiedBy || item.createdByName || item.uploadedBy,
    };

    return (
      <DocumentAction
        options={commonActions}
        fileId={fileObj.id}
        fetchFiles={fetchCallback}
        fileName={fileObj.name}
        fileExtension={fileObj.fileFormat}
        file={fileObj}
      />
    );
  };

  const getFileIcon = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase();
    if (ext === 'pdf') return <PdfSVG />;
    if (ext === 'xls' || ext === 'xlsx') return <ExcelIconSVG />;
    return <PdfSVG />;
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  return (
    <Container>
      <Tabs>
        {['Resources', 'Invoices', 'Attachments', 'Description']
          .filter(
            (tabKey) =>
              tabKey !== 'Invoices' ||
              (user &&
                hasPermission(user, CLIENT_MODULE.GENERATE_INVOICE) &&
                hasFeature(
                  featureToggles?.featureToggles ?? [],
                  EFeatureToggles.INVOICE_GENERATION
                ))
          )
          .map((tabKey) => (
            <Tab
              key={tabKey}
              active={activeTab === tabKey}
              onClick={() => setActiveTab(tabKey as any)}
            >
              {t(tabKey)}
            </Tab>
          ))}
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
              {rawProjectResources?.length ? (
                rawProjectResources.map((r, idx) => (
                  <tr key={idx}>
                    <td>{r.employeeId}</td>
                    <td>{r.name}</td>
                    <td>{r.allocationPercentage}%</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={3}>
                    <NoDataContainer>
                      {t('No Resources Available')}
                    </NoDataContainer>
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}
        {activeTab === 'Invoices' &&
          user &&
          hasPermission(user, CLIENT_MODULE.GENERATE_INVOICE) &&
          hasFeature(
            featureToggles?.featureToggles ?? [],
            EFeatureToggles.INVOICE_GENERATION
          ) && (
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
                    <td colSpan={5}>
                      <LoaderContainer>
                        <PulseLoader />
                      </LoaderContainer>
                    </td>
                  </tr>
                ) : invoices.length === 0 ? (
                  <tr>
                    <td colSpan={5}>
                      <NoDataContainer>
                        {t('No Invoice Available')}
                      </NoDataContainer>
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
                          fetchInvoices
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </ProjectsTable>
          )}

        {activeTab === 'Attachments' && (
          <AttachmentList className="attachment-scroll-container">
            {loadingAttachments ? (
              <LoaderContainer>
                <PulseLoader />
              </LoaderContainer>
            ) : attachments.length ? (
              attachments.map((attachment) => (
                <AttachmentItem
                  key={attachment.fileId}
                  ref={(el) =>
                    el
                      ? triggerRefs.current.set(attachment.fileId, el)
                      : triggerRefs.current.delete(attachment.fileId)
                  }
                >
                  <AttachmentInfo>
                    {getFileIcon(attachment.name)}
                    <FileDetails>
                      <FileName>{attachment.name}</FileName>
                      <FileMeta>
                        {attachment.fileSize}
                        {attachment.uploadedBy &&
                          ` Uploaded by ${attachment.uploadedBy}`}
                        {attachment.uploadedOn &&
                          ` on ${attachment.uploadedOn}`}
                      </FileMeta>
                    </FileDetails>
                  </AttachmentInfo>
                  {renderDocumentAction(
                    attachment,
                    'contract',
                    fetchAttachments
                  )}
                </AttachmentItem>
              ))
            ) : (
              <NoAttachments>{t('No Attachments Found')}</NoAttachments>
            )}
          </AttachmentList>
        )}
        {activeTab === 'Description' && (
          <div>
            {description?.trim() ? (
              description
            ) : (
              <NoDataContainer>{t('No Description Added')}</NoDataContainer>
            )}
          </div>
        )}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
