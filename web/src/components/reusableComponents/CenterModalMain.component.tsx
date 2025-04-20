import React from 'react';
import {
  CenterModalContainer,
  CenterModalTypeTwoInnerContainer,
} from '../../styles/CenterModalStyles.style';
import { CenterModalCloseSVG } from '../../svgs/CommonSvgs.svs';
import { useTranslation } from 'react-i18next';

type CenterModalProps = {
  heading: string;
  subHeading?: string;
  modalClose: () => void;
  modalSVG?: React.ReactNode;
  actualContentContainer: React.ReactNode;
};
const CenterModalMain = (props: CenterModalProps) => {
  const { t } = useTranslation();
  return (
    <CenterModalContainer className="scrollable">
      <CenterModalTypeTwoInnerContainer>
        <span className="headingSection">
          <span className="heading">
            {t(props.heading)}
            <span className="subHeading">
              {props.subHeading && t(props.subHeading)}
            </span>
          </span>
          <span className="modalSVG">
            {props.modalSVG}
            <span className="closeIcon" onClick={props.modalClose}>
              {/* <ModalCloseSVG /> */}
              <CenterModalCloseSVG />
            </span>
          </span>
        </span>

        {props.actualContentContainer}
      </CenterModalTypeTwoInnerContainer>
    </CenterModalContainer>
  );
};

export default CenterModalMain;
