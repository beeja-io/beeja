import React, { useRef, useState, useEffect } from 'react';
import {
  ActionIcon,
  ChevronLeftSVG,
  ChevronRightSVG,
  DownloadIcon,
} from '../../svgs/DocumentTabSvgs.svg';
/* eslint-disable */
import axios, { AxiosError } from 'axios';
import { t } from 'i18next';
import { Document, Page, pdfjs } from 'react-pdf';
import 'react-pdf/dist/Page/AnnotationLayer.css';
import 'react-pdf/dist/Page/TextLayer.css';
import { DOCUMENT_MODULE } from '../../constants/PermissionConstants';
import { useUser } from '../../context/UserContext';
import { FileEntity } from '../../entities/FileEntity';
import {
  deleteEmployeeFile,
  downloadEmployeeFile,
  downloadContractFile,
  deleteContractFile,
} from '../../service/axiosInstance';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import {
  ActionContainer,
  ActionMenu,
  ActionMenuContent,
  ActionMenuOption,
} from '../../styles/DocumentTabStyles.style';
import { hasPermission } from '../../utils/permissionCheck';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import CenterModal from './CenterModal.component';
import CenterModalMain from './CenterModalMain.component';
import { disableBodyScroll, enableBodyScroll } from '../../constants/Utility';

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.js',
  import.meta.url
).toString();

interface ActionProps {
  options: {
    title: string;
    svg: React.ReactNode;
  }[];
  fileId: string;
  fetchFiles: () => void;
  fileName: string;
  fileExtension: string;
  file: FileEntity;
  // onOptionSelect: (selectedOption: string) => void;
}

export const DocumentAction: React.FC<ActionProps> = ({
  options,
  fileId,
  fetchFiles,
  fileName,
  fileExtension,
  file,
}) => {
  const { user } = useUser();
  const [isOpen, setIsOpen] = useState(false);
  const [images, setImages] = useState(null);
  const [numPages, setNumPages] = useState<number>(0);
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [confirmDeleteModal, setConfirmDeleteModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const openDropdown = () => {
    setIsOpen(!isOpen);
  };

  const [isDocumentPreviewModalOpen, setIsDocumentPreviewModalOpen] =
    useState(false);
  const handleIsDocumentPreviewModalOpen = () => {
    setIsDocumentPreviewModalOpen(!isDocumentPreviewModalOpen);
  };
  useEffect(() => {
    if (isDocumentPreviewModalOpen) {
      disableBodyScroll();
    } else {
      enableBodyScroll();
    }

    return () => {
      enableBodyScroll();
    };
  }, [isDocumentPreviewModalOpen]);

  const handleOptionClick = (option: string) => {
    setSelectedOption(option);
    //FIXME ...
    if (option === 'Delete') {
      if (
        user &&
        ((hasPermission(user, DOCUMENT_MODULE.DELETE_DOCUMENT) &&
          user.employeeId === file.createdBy) ||
          hasPermission(user, DOCUMENT_MODULE.DELETE_ENTIRE_DOCUMENTS))
      ) {
        setConfirmDeleteModal(true);
      }
    }

    if (option == 'Download') {
      handleFileDownload(fileId, fileName, fileExtension);
    }
    if (option == 'Preview') {
      handleIsDocumentPreviewModalOpen();
      documentPreview(fileName, fileId);
    }
    setIsOpen(false);
    setSelectedOption(null);
    // onOptionSelect(option);
  };

  const deleteModalClose = () => {
    setConfirmDeleteModal(false);
  };

  const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown-container')) {
      setIsOpen(false);
    }
  };

  document.addEventListener('click', handleClickOutside);

  const handleDocumentClick = (e: any) => {
    if (isOpen && !dropdownRef.current?.contains(e.target as Node)) {
      setIsOpen(false);
    }
  };

  window.addEventListener('click', handleDocumentClick);

  const handleConfirmDelete = () => {
    deleteFile(fileId);
  };
  const deleteFile = async (fileId: string) => {
    setIsLoading(true);
    try {
      if (file.entityType === 'invoice' || file.entityType === 'contract') {
        await deleteContractFile(fileId);
      } else {
        await deleteEmployeeFile(fileId);
      }
      fetchFiles();
      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
      throw new Error('Error deleting file: ' + error);
    }
  };

  const documentPreview = async (fileName: any, fileId: string) => {
    try {
      const response = await downloadEmployeeFile(fileId);
      const imageUrl: any = window.URL.createObjectURL(
        new Blob([response.data])
      );
      setImages(imageUrl);
      const link = document.createElement('img');
      link.setAttribute('preview', `${fileName}.${fileExtension}`);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(imageUrl);
      fetchFiles();
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response?.status === 403) {
          alert('No permissions');
        } else {
          alert("Server Down! We'll come back soon");
        }
      }
    }
  };

  const onDocumentSuccess = ({ numPages }: { numPages: number }): void => {
    setNumPages(numPages);
  };

  const prevPage = () => {
    setPageNumber(pageNumber <= 1 ? 1 : pageNumber - 1);
  };

  const nextPage = () => {
    setPageNumber(pageNumber >= numPages ? pageNumber : pageNumber + 1);
  };

  const handleFileDownload = async (
    fileId: string,
    fileName: string,
    fileExtension: string
  ) => {
    try {
      let response;
      if (file.entityType === 'invoice' || file.entityType === 'contract') {
        response = await downloadContractFile(fileId);
      } else {
        response = await downloadEmployeeFile(fileId);
      }

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${fileName}.${fileExtension}`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response?.status === 403) {
          alert('No permissions');
        } else {
          alert("Server Down! We'll come back soon");
        }
      }
    }
  };
  useKeyPress(27, () => {
    setConfirmDeleteModal(false);
  });

  return (
    <>
      <ActionContainer className="dropdown-container" ref={dropdownRef}>
        <ActionMenu className="action-align" onClick={openDropdown}>
          <ActionIcon />
        </ActionMenu>
        {isOpen && (
          <ActionMenuContent>
            {options.map(
              (option, index) =>
                //FIXME ...
                (option.title !== 'Delete' ||
                  (user &&
                    (hasPermission(user, DOCUMENT_MODULE.DELETE_DOCUMENT) ||
                      hasPermission(
                        user,
                        DOCUMENT_MODULE.DELETE_ENTIRE_DOCUMENTS
                      )))) && (
                  <ActionMenuOption
                    key={index}
                    className={
                      selectedOption === option.title ? 'selected' : ''
                    }
                    onClick={() => handleOptionClick(option.title)}
                  >
                    {option.svg}
                    {option.title}
                  </ActionMenuOption>
                )
            )}
          </ActionMenuContent>
        )}
      </ActionContainer>
      {confirmDeleteModal && (
        <CenterModal
          handleModalLeftButtonClick={deleteModalClose}
          handleModalClose={deleteModalClose}
          handleModalSubmit={handleConfirmDelete}
          modalHeading="Delete"
          modalContent="Are you sure want to Delete the Document?"
        />
      )}
      {isDocumentPreviewModalOpen && (
        <span style={{ cursor: 'default' }}>
          <CenterModalMain
            heading="Document Preview"
            subHeading={fileName}
            modalSVG={
              <button
                style={{
                  width: '145px',
                  height: '40px',
                  display: 'inline-flex',
                  padding: '10px 10px 10px 10px',
                  justifyContent: 'flex-end',
                  alignItems: 'center',
                  borderRadius: '10px',
                  border: '1px solid #005792',
                  backgroundColor: '#FFF',
                  cursor: 'pointer',
                }}
                onClick={() =>
                  handleFileDownload(fileId, fileName, fileExtension)
                }
              >
                <span
                  style={{
                    display: 'flex',
                    color: '#005792',
                    textAlign: 'center',
                    fontFamily: 'Nunito',
                    fontSize: '16px',
                    fontStyle: 'normal',
                    fontWeight: '700',
                    lineHeight: '150%' /* 24px */,
                    letterSpacing: '0.3px',
                    gap: '5px',
                    justifyContent: 'center',
                    alignItems: 'center',
                  }}
                >
                  <span
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    <DownloadIcon stroke="#28303F" />
                  </span>
                  {t('DOWNLOAD')}
                </span>
              </button>
            }
            modalClose={handleIsDocumentPreviewModalOpen}
            actualContentContainer={
              images ? (
                <div
                  style={{
                    width: '790px',
                    height: 'fit-content',
                    border: '1px solid #F8F4F4',
                    backgroundColor: '#F8F4F4 no-repeat',
                  }}
                >
                  {images && fileExtension === 'pdf' ? (
                    <Document file={images} onLoadSuccess={onDocumentSuccess}>
                      <div
                        style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                        }}
                      >
                        <p className="pages">
                          {t('PAGE')} {pageNumber} {t('OF')} {numPages}
                        </p>
                        <div
                          style={{
                            display: 'flex',
                            justifyContent: 'flex-start',
                            alignItems: 'center',
                            gap: '5px',
                          }}
                        >
                          <button className="pageButton" onClick={prevPage}>
                            <span>
                              <ChevronLeftSVG />
                            </span>
                          </button>
                          <button className="pageButton" onClick={nextPage}>
                            <span>
                              <ChevronRightSVG />
                            </span>
                          </button>
                        </div>
                      </div>
                      <Page
                        pageNumber={pageNumber}
                        className="react-pdf__Page__canvas"
                      />
                    </Document>
                  ) : images ? (
                    <img
                      src={images}
                      id="myImg"
                      style={{
                        width: '790px',
                        height: '556px',
                        border: '1px solid #F8F4F4',
                        backgroundColor: '#F8F4F4 no-repeat',
                      }}
                    />
                  ) : (
                    ' '
                  )}
                </div>
              ) : (
                <p>loading...</p>
              )
            }
          />
        </span>
      )}
      {isLoading && <SpinAnimation />}
    </>
  );
};
