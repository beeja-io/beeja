import { useTranslation } from 'react-i18next';
import { ZeroEntriesFoundMainContainer } from '../../styles/ZeroEntriesFoundStyles.Style';
import { NoDocsIcon } from '../../svgs/DocumentTabSvgs.svg';

type ZeroEntriesFoundProps = {
  heading: string;
  message: string;
};
const ZeroEntriesFound = (props: ZeroEntriesFoundProps) => {
  const { t } = useTranslation();
  return (
    <ZeroEntriesFoundMainContainer>
      <span>
        <NoDocsIcon />
      </span>
      <span className="contentArea">
        <span className="heading">{t(props.heading)}</span>
        <span className="content">{t(props.message)}</span>
      </span>
    </ZeroEntriesFoundMainContainer>
  );
};

export default ZeroEntriesFound;
