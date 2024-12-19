import { useCallback, useEffect, useState } from 'react';
/* eslint-disable */
import { useTranslation } from 'react-i18next';
import {
  DocumentContainer,
  DocumentHeadSection,
  FileUploadField,
  FileUploadForm,
  FormFileSelected,
  InputLabelContainer,
  NoDocsContainer,
  TableBodyRow,
  TableHead,
  TableList,
  TextInput,
  UploadButton,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import SideModal from './SideModal.component';
import { Button } from '../../styles/CommonStyles.style';
import {
  FileUploadIcon,
  FileTextIcon,
  DownloadIcon,
  DeleteIcon,
  FormFileIcon,
  FormFileUploadIcon,
  FormFileCloseIcon,
  NoDocsIcon,
  CalenderIcon,
} from '../../svgs/DocumentTabSvgs.svg';
import { DocumentAction } from './DocumentAction';
import {
  getAllFilesByEmployeeId,
  uploadEmployeeFiles,
} from '../../service/axiosInstance';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { FileEntity } from '../../entities/FileEntity';
import { formatDate } from '../../utils/dateFormatter';
import PulseLoader from '../loaders/PulseAnimation.loader';
import { AlertISVG } from '../../svgs/CommonSvgs.svs';
import {
  DocumnentNameRequired,
  FileRequired,
  UploadFileError,
} from '../../constants/Constants';
import ToastMessage from './ToastMessage.component';
import { EditIcon } from '../../svgs/ExpenseListSvgs.svg';
import { useUser } from '../../context/UserContext';
import { DOCUMENT_MODULE } from '../../constants/PermissionConstants';
import axios from 'axios';
import { toast } from 'sonner';
import { hasPermission } from '../../utils/permissionCheck';
import useKeyCtrl from '../../service/keyboardShortcuts/onKeySave';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import Pagination from '../directComponents/Pagination.component';
type DocumentTabContentProps = {
  employee: EmployeeEntity;
};

export const DocumentTabContent = (props: DocumentTabContentProps) => {
  const { t } = useTranslation();
  const { user } = useUser();
  const [isCreateDocumentModelOpen, setIsCreateDocumentModelOpen] =
    useState(false);

  const handleIsCreateDocumentModal = () => {
    setIsCreateDocumentModelOpen(true);
  };

  const handleClose = () => {
    setIsCreateDocumentModelOpen(false);
    setDocumentName('');
    setDescription('');
    setCategory('');
    removeFile();
    setErrors((prevErrors) => ({ ...prevErrors, emptyFile: '' }));
    setErrors((prevErrors) => ({ ...prevErrors, emptyDocumentType: '' }));
  };

  const documentType = [
    'Identity',
    'Payslip',
    'ADDRESS_PROOF',
    'Education',
    'NDA',
    'Tax_Exit_Doc',
    'Equipment_Policy',
    'Appraisal Letter',
  ];
  const Actions = [
    ...(user &&
    (hasPermission(user, DOCUMENT_MODULE.READ_DOCUMENT) ||
      hasPermission(user, DOCUMENT_MODULE.READ_ENTIRE_DOCUMENTS))
      ? [{ title: 'Download', svg: <DownloadIcon /> }]
      : []),
    ...(user &&
    (hasPermission(user, DOCUMENT_MODULE.DELETE_DOCUMENT) ||
      hasPermission(user, DOCUMENT_MODULE.DELETE_ENTIRE_DOCUMENTS))
      ? [{ title: 'Delete', svg: <DeleteIcon /> }]
      : []),
    ...(user &&
    (hasPermission(user, DOCUMENT_MODULE.READ_DOCUMENT) ||
      hasPermission(user, DOCUMENT_MODULE.READ_ENTIRE_DOCUMENTS))
      ? [{ title: 'Preview', svg: <EditIcon /> }]
      : []),
  ];

  const [selectedFileName, setSelectedFileName] = useState<string | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const fileInput = event.target;
    if (fileInput.files && fileInput.files.length > 0) {
      setSelectedFileName(fileInput.files[0].name);
      setSelectedFile(fileInput.files[0] || null);
      setErrors((prevErrors) => ({ ...prevErrors, emptyFile: '' }));
    }
  };

  const removeFile = () => {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.value = '';
    setSelectedFileName(null);
    setSelectedFile(null);
  };

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();

    event.dataTransfer.dropEffect = 'copy';
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    event.stopPropagation();

    const file = event.dataTransfer.files[0];
    if (file) {
      setSelectedFileName(file.name);
      setSelectedFile(file);
    }
  };

  const [allFilesList, setAllFilesList] = useState<FileEntity[]>([]);
  const [documentName, setDocumentName] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [category, setCategory] = useState<string>('');
  const [isResponseLoading, setIsResponseLoading] = useState(false);
  const [responseMessage, setResponseMessage] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalEntries, setTotalEntries] = useState<number>(0);
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };
  const handleItemsPerPage = (itemsPerPage: number) => {
    setItemsPerPage(itemsPerPage);
    setCurrentPage(1);
    handleTotalPages(totalPages ?? 1);
  };
  const handleTotalPages = (totalPages: number) => {
    setTotalPages(totalPages);
  };

  const [errors, setErrors] = useState({
    emptyDocumentType: '',
    emptyFile: '',
  });

  useEffect(() => {
    fetchData();
  }, [currentPage, itemsPerPage]);
  const entityId = props.employee.account.employeeId;
  const fetchData = useCallback(async () => {
    setIsLoading(true);
    try {
      const queryParams = [];
      if (currentPage) {
        queryParams.push(`page=${currentPage}`);
      }
      if (itemsPerPage) {
        queryParams.push(`size=${itemsPerPage}`);
      }
      const filteredParams = queryParams.filter((param) => param.length > 0);
      const url =
        filteredParams.length > 0
          ? `/employees/v1/files/${entityId}?${filteredParams.join('&')}`
          : `/employees/v1/files/${entityId}`;
      const response = await getAllFilesByEmployeeId(url);
      const totalSize = response.data.metadata.totalSize;
      const totalPages = Math.ceil(totalSize / itemsPerPage);
      setTotalEntries(response.data.metadata.totalSize);
      setTotalPages(totalPages);
      handleTotalPages(totalPages ?? 1);
      setAllFilesList(response.data.files);
    } catch (error) {
      throw new Error('Error fetching data:' + error);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, itemsPerPage, entityId]);

  const [isUpdateToastMessage, setIsUpdateToastMessage] = useState(false);
  const handleUpdateToastMessage = () => {
    setIsUpdateToastMessage(!isUpdateToastMessage);
  };

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { value } = e.target;

    setCategory(value);
    setErrors((prevErrors) => ({ ...prevErrors, emptyDocumentType: '' }));
  };

  const handleFormSubmit = async (event: any) => {
    event.preventDefault();

    const newErrors = {
      emptyDocumentType: category === '' ? DocumnentNameRequired : '',
      emptyFile: selectedFile === null ? FileRequired : '',
    };

    setErrors(newErrors);

    if (selectedFile && !newErrors.emptyDocumentType && !newErrors.emptyFile) {
      try {
        const formData = new FormData();
        formData.append('file', selectedFile);
        if (documentName.length > 0) {
          formData.append('name', documentName);
        }
        formData.append('description', description);
        formData.append('fileType', category);
        formData.append('entityId', props.employee.account.employeeId);
        formData.append('entityType', 'employee');

        setIsResponseLoading(true);
        await uploadEmployeeFiles(formData);
        // setResponseMessage(ProfileCreationSuccessMessage);
        fetchData();

        handleClose();
        setSelectedFile(null);
        setDocumentName('');
        setDescription('');
        setCategory('');
        setIsUpdateToastMessage(true);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          const { response } = error;
          if (response) {
            if (response.status === 413) {
              toast('File size is too large');
              setResponseMessage('File size exceeds the 10 MB limit');
            }
            return;
          }
        }
        setResponseMessage(UploadFileError);
      } finally {
        setIsResponseLoading(false);
        setTimeout(() => {
          setResponseMessage('');
        }, 5000);
      }
    }
  };

  const handleDocumentTypeChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setCategory(e.target.value);
    handleChange(e);
  };
  useKeyCtrl('s', () =>
    handleFormSubmit(event as unknown as React.FormEvent<HTMLFormElement>)
  );
  useKeyPress(27, () => {
    handleClose();
  });
  return (
    <>
      <DocumentContainer>
        <DocumentHeadSection>
          <div className="document_heading">
            {user?.employeeId === props.employee.account.employeeId ? (
              <>
                <p>{t('My Documents')}</p>
              </>
            ) : (
              <>
                <p>{t('Documents')}</p>
              </>
            )}
            <span>{t('List of Documents')}</span>
          </div>
          <div>
            {/* TODO: Document Filter */}
            {/* <FilterButton
              options={documentType}
              name={'Filter by Document Type'}
              icon={<FilterIcon />}
            /> */}
            {((user &&
              user.employeeId === props.employee.account.employeeId &&
              hasPermission(user, DOCUMENT_MODULE.CREATE_DOCUMENT)) ||
              (user &&
                hasPermission(
                  user,
                  DOCUMENT_MODULE.CREATE_ENTIRE_DOCUMENTS
                ))) && (
              <UploadButton
                className="submit"
                onClick={handleIsCreateDocumentModal}
              >
                <FileUploadIcon />
                {t('Upload Document')}
              </UploadButton>
            )}
          </div>
        </DocumentHeadSection>

        {isLoading ? (
          <PulseLoader height="300px" />
        ) : allFilesList.length > 0 ? (
          <TableList>
            <TableHead>
              <tr className="documentsTableTow">
                <th>{t('Name')}</th>
                <th>{t('Document Type')}</th>
                <th>{t('Created Date')}</th>
                <th>{t('Created By')}</th>
                {user &&
                  (hasPermission(user, DOCUMENT_MODULE.READ_DOCUMENT) ||
                    hasPermission(user, DOCUMENT_MODULE.DELETE_DOCUMENT) ||
                    hasPermission(
                      user,
                      DOCUMENT_MODULE.DELETE_ENTIRE_DOCUMENTS
                    ) ||
                    hasPermission(user, DOCUMENT_MODULE.UPDATE_DOCUMENT) ||
                    hasPermission(
                      user,
                      DOCUMENT_MODULE.READ_ENTIRE_DOCUMENTS
                    )) && <th>{t('Actions')}</th>}
              </tr>
            </TableHead>

            {allFilesList &&
              allFilesList.map((file) => {
                return (
                  <TableBodyRow key={file.id}>
                    <td className="truncate_filename" title={file.name}>
                      <span
                        style={{
                          verticalAlign: 'middle',
                          marginRight: '12px',
                        }}
                      >
                        <FileTextIcon />
                      </span>
                      {file.name ? file.name : '-'}
                    </td>
                    <td>{file.fileType ? file.fileType : '-'}</td>
                    <td>
                      <span
                        style={{
                          verticalAlign: 'middle',
                          marginRight: '12px',
                        }}
                      >
                        <CalenderIcon />
                      </span>
                      {file.createdAt && formatDate(file.createdAt)}
                    </td>
                    <td>{file.createdByName ? file.createdByName : '-'}</td>
                    {user &&
                      (hasPermission(user, DOCUMENT_MODULE.READ_DOCUMENT) ||
                        hasPermission(user, DOCUMENT_MODULE.DELETE_DOCUMENT) ||
                        hasPermission(
                          user,
                          DOCUMENT_MODULE.DELETE_ENTIRE_DOCUMENTS
                        ) ||
                        hasPermission(user, DOCUMENT_MODULE.UPDATE_DOCUMENT) ||
                        hasPermission(
                          user,
                          DOCUMENT_MODULE.READ_ENTIRE_DOCUMENTS
                        )) && (
                        <td>
                          <DocumentAction
                            options={Actions}
                            fileId={file.id}
                            fetchFiles={fetchData}
                            fileName={file.name}
                            fileExtension={file.fileFormat}
                            file={file}
                          />
                        </td>
                      )}
                  </TableBodyRow>
                );
              })}
          </TableList>
        ) : (
          <NoDocsContainer>
            <NoDocsIcon />
            <p className="heading">There is no document here</p>
            <p className="description">
              Please add new Document by clicking "Upload Document" above
            </p>
          </NoDocsContainer>
        )}
        {totalPages && (
          <Pagination
            totalPages={totalPages}
            currentPage={currentPage}
            handlePageChange={handlePageChange}
            totalItems={totalEntries}
            handleItemsPerPage={handleItemsPerPage}
            itemsPerPage={itemsPerPage}
          />
        )}
      </DocumentContainer>

      {isCreateDocumentModelOpen && (
        <SideModal
          handleClose={handleClose}
          isModalOpen={isCreateDocumentModelOpen}
          innerContainerContent={
            <FileUploadForm
              style={{ cursor: isResponseLoading ? 'progress' : '' }}
              className="documentsUploadForm"
            >
              <form onSubmit={handleFormSubmit}>
                <div>
                  <p style={{ fontSize: '24px', fontWeight: 700 }}>
                    Add New Document
                  </p>
                  <InputLabelContainer>
                    <label>
                      Document Type{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <select
                      style={{
                        borderColor: errors.emptyDocumentType ? 'red' : '',
                      }}
                      value={category}
                      onChange={handleDocumentTypeChange}
                      className="selectoption"
                    >
                      {['Select a Type', ...documentType].map((option) => (
                        <option key={option} value={option}>
                          {option}
                        </option>
                      ))}
                    </select>
                    {errors.emptyDocumentType && (
                      <ValidationText>
                        <AlertISVG /> {errors.emptyDocumentType}
                      </ValidationText>
                    )}
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>Document Name</label>
                    <TextInput
                      type="text"
                      value={documentName}
                      placeholder="Ex: Pan Card /Aadhar Card /Voter Id/ Driving License"
                      onChange={(e) => setDocumentName(e.target.value)}
                    />
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>Description</label>
                    <TextInput
                      type="text"
                      value={description}
                      placeholder="Ex: Pan Card front Image"
                      onChange={(e) => setDescription(e.target.value)}
                    />
                  </InputLabelContainer>

                  <InputLabelContainer>
                    <label>
                      Choose/Drag File{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>

                    <FileUploadField
                      style={{ borderColor: errors.emptyFile ? 'red' : '' }}
                      onDragOver={handleDragOver}
                      onDrop={handleDrop}
                    >
                      <label htmlFor="fileInput">
                        <FormFileUploadIcon />
                        Choose/Drag File
                      </label>
                      <input
                        type="file"
                        accept="application/pdf,application/vnd.ms-excel,application/msword,image/png,image/jpeg"
                        id="fileInput"
                        style={{ display: 'none' }}
                        onChange={handleFileChange}
                      />
                    </FileUploadField>
                    {selectedFileName && (
                      <FormFileSelected
                        style={{
                          borderColor: responseMessage.length ? 'red' : '',
                        }}
                      >
                        <FormFileIcon />
                        <span className="fileName">{selectedFileName}</span>
                        <span className="closeMark" onClick={removeFile}>
                          <FormFileCloseIcon />
                        </span>
                      </FormFileSelected>
                    )}
                    {errors.emptyFile && (
                      <ValidationText>
                        <AlertISVG /> {errors.emptyFile}
                      </ValidationText>
                    )}
                  </InputLabelContainer>
                </div>
                <div>
                  {responseMessage.length != 0 && (
                    <ValidationText>
                      <AlertISVG /> {responseMessage}
                    </ValidationText>
                  )}
                  <div>
                    <div
                      style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                      }}
                    >
                      <Button
                        style={
                          isResponseLoading
                            ? { opacity: 0.3, cursor: 'not-allowed' }
                            : {}
                        }
                        onClick={(e) => {
                          if (isResponseLoading) {
                            e.preventDefault();
                          }
                          if (!isResponseLoading) {
                            handleClose();
                          }
                        }}
                        className="cancel"
                      >
                        Cancel
                      </Button>
                      <Button
                        className="submit"
                        style={{ cursor: isResponseLoading ? 'progress' : '' }}
                      >
                        Submit
                      </Button>
                    </div>
                  </div>
                </div>
                <br />
                <span className="infoText">
                  File format : .pdf, .png, .jpeg (Maximum Size: 10MB)
                </span>
              </form>
            </FileUploadForm>
          }
        />
      )}
      {isUpdateToastMessage && (
        <ToastMessage
          messageType="success"
          messageBody="File uploaded"
          messageHeading="Updated Successfully"
          handleClose={handleUpdateToastMessage}
        />
      )}
    </>
  );
};
