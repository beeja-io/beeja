import { useState } from 'react';
import {
  CenterModalContainer,
  CenterModelMainContainer,
} from '../../styles/CenterModalStyles.style';
import { ValidationText } from '../../styles/DocumentTabStyles.style';
import { useTranslation } from 'react-i18next';
type CenterModalProps = {
  handleModalClose: () => void;
  handleModalSubmit: (reason: string) => void;
  modalFieldText: string;
  modalPlaceHolder?: string;
  modalLeftButtonText?: string;
  modalRightButtonText?: string;
  modalType?: string;
  modalLeftButtonClass?: string;
  modalRightButtonClass?: string;
  modalWidth?: string;
};

/**
 *  Below component is used while Rejecting Loan
 *  @returns {JSX.Element} A React element
 */
const CenterModalReject = (props: CenterModalProps) => {
  const [rejectReason, setRejectReason] = useState('');
  const { t } = useTranslation();
  const rightButtonStyles = props.modalRightButtonClass
    ? {
        backgroundColor: 'white',
        border: '2px solid red',
        color: 'red',
      }
    : {};

  const handleTextAreaChange = (
    event: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    const inputText = event.target.value;
    const words = inputText.split(/\s+/);
    if (words.length <= 10) {
      setRejectReason(inputText);
    }
  };

  const handleModalSubmit = () => {
    props.handleModalSubmit(rejectReason);
  };
  return (
    <CenterModalContainer className="loanRejectModal">
      <CenterModelMainContainer className="loanRejectModalInner">
        <div>
          <div className="modalFieldText">
            {t(props.modalFieldText)}{' '}
            <ValidationText className="star">*</ValidationText>
          </div>
          <textarea
            className="modalTextArea"
            rows={2}
            cols={40}
            value={rejectReason}
            onChange={handleTextAreaChange}
            placeholder={
              props.modalPlaceHolder ? t(props.modalPlaceHolder) : ''
            }
          />
        </div>
        <div className="controlButtonArea">
          <button
            className={
              props.modalLeftButtonClass
                ? props.modalLeftButtonClass
                : 'closeButton'
            }
            onClick={props.handleModalClose}
          >
            {props.modalLeftButtonText ? t(props.modalLeftButtonText) : t('NO')}
          </button>
          <button
            className={
              props.modalRightButtonClass ? props.modalRightButtonClass : ''
            }
            style={{
              ...rightButtonStyles,
              // pointerEvents: rejectReason ? 'auto' : 'none',
              backgroundColor: rejectReason ? '#005792' : '#D1D5DA',
              cursor: rejectReason ? 'pointer' : 'not-allowed',
            }}
            onClick={handleModalSubmit}
            disabled={!rejectReason}
          >
            {props.modalRightButtonText
              ? t(props.modalRightButtonText)
              : t('YES')}
          </button>
        </div>
      </CenterModelMainContainer>
    </CenterModalContainer>
  );
};
export default CenterModalReject;
