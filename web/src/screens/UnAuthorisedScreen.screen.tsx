import { Button } from 'web-kit-components';
import {
  MainErrorScreenContainer,
  ServiceUnavailableText,
} from '../styles/ErrorScreenStyles.style';
import { SadSVG } from '../svgs/CommonSvgs.svs';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const UnAuthorisedScreen = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  return (
    <MainErrorScreenContainer className="serviceUnavailable">
      <SadSVG />
      <ServiceUnavailableText>
        <span>{t('OOPS_NO_PERMISSIONS')}</span>
        <span className="description">
          {t('CONTACT_YOUR_HR_TEAM')} <br />
        </span>
        <Button className="submit" onClick={() => navigate('/')}>
          {t('GO_TO_HOME')}
        </Button>
      </ServiceUnavailableText>
    </MainErrorScreenContainer>
  );
};

export default UnAuthorisedScreen;
