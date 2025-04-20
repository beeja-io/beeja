import { useState } from "react";
import {
  ChevronLeftSVG,
  ChevronRightSVG,
} from "../../svgs/DocumentTabSvgs.svg";
import { Document, Page } from "react-pdf";
import { pdfjs } from "react-pdf";
import {
  ChevronButton,
  DocumentPreviewModal,
  PaginationModal,
} from "../../styles/DocumentPreviewStyles.style";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import SpinAnimation from "../loaders/SprinAnimation.loader";
import { useTranslation } from "react-i18next";

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  "pdfjs-dist/build/pdf.worker.min.js",
  import.meta.url,
).toString();

type DocumentPreviewProps = {
  images: string;
  fileExtension: string | undefined;
};

const DocumentPreview = (props: DocumentPreviewProps) => {
  const { t } = useTranslation();
  const [numPages, setNumPages] = useState<number>(0);
  const [pageNumber, setPageNumber] = useState<number>(1);

  const onDocumentSuccess = ({ numPages }: { numPages: number }): void => {
    setNumPages(numPages);
  };

  const prevPage = () => {
    setPageNumber(pageNumber <= 1 ? 1 : pageNumber - 1);
  };

  const nextPage = () => {
    setPageNumber(pageNumber >= numPages ? pageNumber : pageNumber + 1);
  };

  return (
    <DocumentPreviewModal>
      {props.images && props.fileExtension === "pdf" ? (
        <Document file={props.images} onLoadSuccess={onDocumentSuccess}>
          <PaginationModal>
            <p className="pages">
              {t("PAGE")} {pageNumber} {t("OF")} {numPages}
            </p>
            <ChevronButton>
              <div className="pageButton">
                <span onClick={prevPage}>
                  <ChevronLeftSVG />
                </span>
              </div>
              <div className="pageButton">
                <span onClick={nextPage}>
                  <ChevronRightSVG />
                </span>
              </div>
            </ChevronButton>
          </PaginationModal>
          <span className="pdfPreview">
            <Page pageNumber={pageNumber} />
          </span>
        </Document>
      ) : props.images ? (
        <img
          src={props.images}
          id="myImg"
          style={{
            width: "fit-content",
            maxWidth: "790px",
            height: "fit-content",
            maxHeight: "90dvh",
            overflowY: "auto",
            border: "1px solid #F8F4F4",
            backgroundColor: "#F8F4F4 no-repeat",
          }}
        />
      ) : (
        <SpinAnimation />
      )}
    </DocumentPreviewModal>
  );
};

export default DocumentPreview;
