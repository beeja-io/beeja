import { toast } from 'sonner';
import {
  CenterModalContainer,
  CenterModelMainContainer,
} from '../../styles/CenterModalStyles.style';
import { ValidationText } from '../../styles/InputStyles.style';
import { CopySVG, EmailSVG } from '../../svgs/CommonSvgs.svs';
import { Button } from '../../styles/CommonStyles.style';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { SharePasswordThroughMailTemplate } from '../../templates/SharePasswordTemplate';

type CopyPasswordPopupProps = {
  employeeId: string;
  password: string;
  firstName: string;
  email: string;
  companyName: string;
  handleClose: () => void;
};
const CopyPasswordPopup = (props: CopyPasswordPopupProps) => {
  const [passwordStored, setIsPaasswordStored] = useState(false);
  const handlePasswordStored = () => {
    setIsPaasswordStored(true);
  };
  const { t } = useTranslation();
  const navigateToEmail = () => {
    const subject = encodeURIComponent('Beeja Account Password');
    const emailProps = {
      firstName: props.firstName,
      employeeId: props.employeeId,
      email: props.email,
      password: props.password,
      companyName: props.companyName,
    };

    const body = SharePasswordThroughMailTemplate(emailProps);

    window.location.href = `mailto:${props.email}?subject=${subject}&body=${body}`;
  };

  const copyToClipboard = () => {
    if (props.password) {
      navigator.clipboard
        .writeText(props.password)
        .then(() => {
          toast.success(t('PASSWORD_COPIED_TO_CLIP_BOARD'));
        })
        .catch(() => {
          toast.error(t('FAILED_TO_COPY'));
        });
    }
  };
  return (
    <CenterModalContainer>
      <CenterModelMainContainer className="mediumPopUp">
        <span>
          {t('USER_CREATED_WITH_EMP_ID')} {props.employeeId}
        </span>
        <div className="passwordArea">
          <span className="passwordBox">
            {props.password ? props.password : 'asd'}
          </span>
          <span
            className="passwordBox icon"
            title={t('COPY_PASSWORD_TO_CLIP_BOARD')}
            onClick={() => {
              copyToClipboard();
              handlePasswordStored();
            }}
          >
            <CopySVG />
          </span>
          <span
            className="passwordBox icon"
            title={t('PASSWORD_ICON_TITLE_TO_MAIL')}
            onClick={() => {
              handlePasswordStored();
              navigateToEmail();
            }}
          >
            <EmailSVG />
          </span>
        </div>
        <div>
          <ValidationText className="info">
            <strong>{t('NOTE')}&nbsp; </strong> {t('PASSWORD_POPUP_NOTE')}
          </ValidationText>
        </div>
        {passwordStored && (
          <Button className="submit" onClick={props.handleClose}>
            {t('CLOSE')}
          </Button>
        )}
      </CenterModelMainContainer>
    </CenterModalContainer>
  );
};

export default CopyPasswordPopup;
