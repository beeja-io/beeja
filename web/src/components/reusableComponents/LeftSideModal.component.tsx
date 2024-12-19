import React from 'react';
import {
  LeftSideModalContainer,
  LeftSideModalInnerContainer,
} from '../../styles/LeftModalStyles.style';

type SideModalProps = {
  handleClose: () => void;
  isModalOpen: boolean;
  innerContainerContent: React.ReactNode;
};
const LeftSideModal = (props: SideModalProps) => {
  return (
    <LeftSideModalContainer>
      <LeftSideModalInnerContainer isModalOpen={props.isModalOpen}>
        {props.innerContainerContent}
      </LeftSideModalInnerContainer>
    </LeftSideModalContainer>
  );
};

export default LeftSideModal;
