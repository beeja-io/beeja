import { useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import {
  ErrorScreenContentArea,
  MainErrorScreenContainer,
} from '../styles/ErrorScreenStyles.style';
import { useTranslation } from 'react-i18next';

const Error404Screen = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  return (
    <MainErrorScreenContainer>
      <ErrorScreenContentArea>
        <span className="errorCode">
          4<span>0</span>4
        </span>
        <span className="errorHeading">{t('SORRY_PAGE_NOT_FOUND')}</span>
        <span className="errorDescription">
          {t('THE_PAGE_REQUESTED_NOT_EXIST')}
        </span>
        <Button className="submit" onClick={() => navigate('/')}>
          {t('GO_TO_HOME')}
        </Button>
      </ErrorScreenContentArea>
    </MainErrorScreenContainer>
  );
};

export default Error404Screen;
