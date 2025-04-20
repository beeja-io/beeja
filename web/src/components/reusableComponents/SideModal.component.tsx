import React from 'react';
import {
  SideModalCloseButton,
  SideModalInnerContainer,
  SideModalMainContainer,
} from '../../styles/SideModalStyles.style';
import { GreaterThanSVG } from '../../svgs/CommonSvgs.svs';

type SideModalProps = {
  handleClose: () => void;
  isModalOpen: boolean;
  innerContainerContent: React.ReactNode;
};
const SideModal = (props: SideModalProps) => {
  return (
    <SideModalMainContainer>
      <SideModalInnerContainer isModalOpen={props.isModalOpen}>
        <SideModalCloseButton onClick={props.handleClose}>
          {' '}
          <GreaterThanSVG />{' '}
        </SideModalCloseButton>
        {props.innerContainerContent}
      </SideModalInnerContainer>
    </SideModalMainContainer>
  );
};

export default SideModal;
