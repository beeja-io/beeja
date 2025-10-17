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
} from '../styles/ProjectTabSectionStyles.style';
import { getAllAttachmentsByContractId } from '../service/axiosInstance.tsx';
import {
  PdfSVG,
  ExcelIconSVG,
  DownloadIcon,
  DeleteIcon,
} from '../svgs/DocumentTabSvgs.svg.tsx';
import { FileEntity } from '../entities/FileEntity.tsx';
import PulseLoader from '../components/loaders/PulseAnimation.loader';
import { DocumentAction } from '../components/reusableComponents/DocumentAction.tsx';

interface RawProjectResource {
  employeeId: string;
  name: string;
  allocationPercentage: number;
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
}

const ContactTabSection: React.FC<ContactTabSectionProps> = ({
  contractId,
  description,
  rawProjectResources,
}) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<
    'Resources' | 'Description' | 'Attachments'
  >(() => {
    return (
      (sessionStorage.getItem(`contractTab_${contractId}`) as
        | 'Resources'
        | 'Description'
        | 'Attachments') || 'Resources'
    );
  });

  useEffect(() => {
    sessionStorage.setItem(`contractTab_${contractId}`, activeTab);

    if (activeTab === 'Attachments') fetchAttachments();
  }, [activeTab, contractId]);
  const [attachments, setAttachments] = useState<Attachment[]>([]);
  const [loading, setLoading] = useState(false);
  const triggerRefs = useRef<Map<string, HTMLDivElement | null>>(new Map());

  useEffect(() => {
    if (activeTab === 'Attachments') fetchAttachments();
  }, [activeTab]);

  const fetchAttachments = async () => {
    setLoading(true);
    try {
      const response = await getAllAttachmentsByContractId(contractId);
      const files =
        response.data.files?.map((file: any) => {
          const uploadedOn = file.createdAt
            ? new Date(file.createdAt).toLocaleDateString('en-GB', {
                day: '2-digit',
                month: 'short',
                year: 'numeric',
              })
            : '';
          return {
            fileId: file.id,
            name: file.name,
            fileSize: file.fileSize || '',
            uploadedBy: file.createdByName || '',
            uploadedOn,
            createdBy: file.createdBy,
          } as Attachment;
        }) || [];
      setAttachments(files);
    } catch {
      setAttachments([]);
    } finally {
      setLoading(false);
    }
  };

  const getFileIcon = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase();
    if (ext === 'pdf') return <PdfSVG />;
    if (ext === 'xls' || ext === 'xlsx') return <ExcelIconSVG />;
    return <PdfSVG />;
  };

  const attachmentActions = [
    { title: 'Download', svg: <DownloadIcon /> },
    { title: 'Delete', svg: <DeleteIcon /> },
  ];

  return (
    <Container>
      <Tabs>
        {['Resources', 'Attachments', 'Description'].map((tabKey) => (
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
                  <td colSpan={3} style={{ textAlign: 'center' }}>
                    {t('No Resources Found')}
                  </td>
                </tr>
              )}
            </tbody>
          </ProjectsTable>
        )}

        {activeTab === 'Description' && (
          <div>
            {description && description.trim() !== ''
              ? description
              : 'No description available'}
          </div>
        )}

        {activeTab === 'Attachments' && (
          <AttachmentList className="attachment-scroll-container">
            {loading ? (
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
                  className="attachment-item"
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

                  <DocumentAction
                    fileId={attachment.fileId}
                    fileName={attachment.name}
                    fileExtension={attachment.name.split('.').pop() || 'pdf'}
                    file={
                      {
                        id: attachment.fileId,
                        name: attachment.name,
                        fileFormat: attachment.name.split('.').pop() || 'pdf',
                        fileSize: attachment.fileSize || '',
                        createdAt: new Date().toISOString(),
                        createdBy: attachment.uploadedBy || 'Unknown',
                        entityType: 'contract',
                      } as FileEntity
                    }
                    fetchFiles={fetchAttachments}
                    options={attachmentActions}
                  />
                </AttachmentItem>
              ))
            ) : (
              <NoAttachments>{t('No Attachments Found')}</NoAttachments>
            )}
          </AttachmentList>
        )}
      </TabContent>
    </Container>
  );
};

export default ContactTabSection;
