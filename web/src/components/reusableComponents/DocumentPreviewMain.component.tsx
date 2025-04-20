import {
  CenterModalContainer,
  CenterModalTypeTwoInnerContainer,
} from "../../styles/CenterModalStyles.style";
import { CenterModalCloseSVG } from "../../svgs/CommonSvgs.svs";
import DocumentPreview from "../directComponents/DocumentPreview.component";
import { Expense } from "../../entities/ExpenseEntity";

type CenterModalProps = {
  heading: string;
  subHeading?: string;
  modalClose: () => void;
  images: string;
  fileExtension: string | undefined;
  expense?: Expense;
  modalSVG: React.ReactNode;
};

const DocumentPreviewMain = (props: CenterModalProps) => {
  return (
    <>
      <CenterModalContainer className="scrollable">
        <CenterModalTypeTwoInnerContainer>
          <span className="headingSection">
            <span className="heading">
              {props.heading}
              <span className="subHeading">{props.subHeading}</span>
            </span>
            <span className="modalSVG">
              {props.modalSVG}
              <span className="closeIcon" onClick={props.modalClose}>
                <CenterModalCloseSVG />
              </span>
            </span>
          </span>
          {
            <DocumentPreview
              images={props.images}
              fileExtension={props.fileExtension}
            />
          }
          {/* {props.actualContentContainer} */}
        </CenterModalTypeTwoInnerContainer>
      </CenterModalContainer>
    </>
  );
};

export default DocumentPreviewMain;
