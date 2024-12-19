export type FileEntity = {
  id: string;
  entityId: string;
  name: string;
  organizationId: string;
  description: string | null;
  entityType: string;
  fileFormat: string;
  fileSize: string;
  fileType: string;
  createdAt: string;
  createdBy: string;
  createdByName: string;
  modifiedAt: Date;
  modifiedBy: string | null;
};
