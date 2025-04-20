import { useState } from "react";
import { expenseReceiptDownload } from "../../service/axiosInstance";
import { DownloadIcon } from "../../svgs/DocumentTabSvgs.svg";
import axios, { AxiosError } from "axios";
import { Expense } from "../../entities/ExpenseEntity";
import ToastMessage from "../reusableComponents/ToastMessage.component";
import { DownloadReceiptError } from "../../constants/Constants";
import { useTranslation } from "react-i18next";

type DocumentDownloadProps = {
  expense?: Expense;
  fileName: string | undefined;
  fileExtension: string | undefined;
  fileId: string | undefined;
};

const DocumentDownload = (props: DocumentDownloadProps) => {
  const { t } = useTranslation();
  const fileId: string = props.fileId ? props.fileId : "";
  const fileName: string = props.fileName ? props.fileName : "";
  const fileType: string = props.fileExtension ? props.fileExtension : "";

  const [responseErrorMessage, setResponseErrorMessage] = useState("");
  const [showErrorMessage, setShowErrorMessage] = useState(false);
  const handleShowErrorMessage = () => {
    setShowErrorMessage(!showErrorMessage);
  };

  const [showDownloadSuccessMessage, setShowDownloadSuccessMessage] =
    useState(false);
  const handleShowDownloadSuccessMessage = () => {
    setShowDownloadSuccessMessage(!showDownloadSuccessMessage);
  };

  const handleFileDownload = async (
    fileId: string,
    fileName: string,
    fileType: string | undefined,
  ) => {
    try {
      const response = await expenseReceiptDownload(fileId);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${fileName}.${fileType}`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      handleShowDownloadSuccessMessage();
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError;
        if (axiosError.response?.status === 403) {
          setResponseErrorMessage(DownloadReceiptError);
          handleShowErrorMessage();
        }
      } else {
        // console.error('Error downloading file:', error);
      }
    }
  };

  return (
    <>
      <div
        title={t("DOWNLOAD")}
        className="downloadButton"
        onClick={() => {
          handleFileDownload(fileId, fileName, fileType);
        }}
      >
        <span className="downloadSVG">
          <DownloadIcon stroke="#28303F" />
        </span>
      </div>
      {showErrorMessage && (
        <ToastMessage
          messageType="error"
          messageBody={responseErrorMessage}
          messageHeading="EXPENSE_IS_UNSUCCESFUL"
          handleClose={handleShowErrorMessage}
        />
      )}
      {showDownloadSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="RECEIPT_DOWNLOADED_SUCCESFULLY"
          messageHeading="SUCCESSFULLY_DOWNLOADED"
          handleClose={handleShowDownloadSuccessMessage}
        />
      )}
    </>
  );
};

export default DocumentDownload;
