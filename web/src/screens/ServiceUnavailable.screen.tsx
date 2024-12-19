import { useNavigate } from 'react-router-dom';
import { Button } from '../styles/CommonStyles.style';
import {
  MainErrorScreenContainer,
  ServiceUnavailableText,
} from '../styles/ErrorScreenStyles.style';
import { SadSVG } from '../svgs/CommonSvgs.svs';
import { useTranslation } from 'react-i18next';

const ServiceUnavailable = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();
  return (
    <MainErrorScreenContainer className="serviceUnavailable">
      <SadSVG />
      <ServiceUnavailableText>
        <span>{t('OOPS_SOMETHING_WENT_WRONG')}</span>
        <span className="description">
          {t('WE_ARE_CURRENTLY_ADDRESSING_ISSUE')} <br />
          {t('IT_MAY_REQUIRE_TIME')}
        </span>
        <Button className="submit" onClick={() => navigate('/')}>
          {t('GO_TO_HOME')}
        </Button>
      </ServiceUnavailableText>
    </MainErrorScreenContainer>
  );
};

export default ServiceUnavailable;
