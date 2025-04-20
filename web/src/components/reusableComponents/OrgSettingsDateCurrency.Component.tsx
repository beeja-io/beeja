import { useState } from 'react';
import {
  BorderDivLine,
  TabContentEditArea,
  TabContentMainContainer,
  TabContentMainContainerHeading,
} from '../../styles/MyProfile.style';
import {
  EditWhitePenSVG,
  CheckBoxOnSVG,
  CrossMarkSVG,
} from '../../svgs/CommonSvgs.svs';
import {
  Container,
  ProfileHeading,
  Row,
} from '../../styles/OrganizationSettingsStyles.style';
import { Label, Select } from '../../styles/OrganizationSettingsStyles.style';
import { IOrganization } from '../../entities/OrganizationEntity';
import {
  CURRENCY_TYPES,
  DATE_FORMATS,
  TIME_ZONES,
} from '../../utils/themeUtils';
import { useUser } from '../../context/UserContext';
import { hasPermission } from '../../utils/permissionCheck';
import { ORGANIZATION_MODULE } from '../../constants/PermissionConstants';
import { useTranslation } from 'react-i18next';

type DateCurrencyType = {
  organization: IOrganization;
  handleUpdateOrganization: (org: IOrganization) => void;
  isErrorOccuredWhileUpdating: boolean;
  setCompanyProfile: (org: IOrganization) => void;
  handleCancelUpdate: () => void;
};
export const OrganizationSettingsDateCurrency = ({
  organization,
  handleUpdateOrganization,
  isErrorOccuredWhileUpdating,
  setCompanyProfile,
  handleCancelUpdate,
}: DateCurrencyType) => {
  const { user } = useUser();
  const { t } = useTranslation();
  const [isEditDateFormatModeOn, setEditDateFormatModeOn] = useState(false);
  const [isEditCurrencyModeOn, setEditCurrencyModeOn] = useState(false);

  const handleIsEditDateFormatModeOn = () => {
    setEditDateFormatModeOn(true);
    setEditCurrencyModeOn(false);
  };

  const handleIsEditDateFormatModeOff = () => {
    setCompanyProfile(organization);
    setEditDateFormatModeOn(false);
  };

  const handleIsEditCurrencyModeOn = () => {
    setEditCurrencyModeOn(true);
    setEditDateFormatModeOn(false);
  };

  const handleIsEditCurrencyModeOff = () => {
    setCompanyProfile(organization);
    setEditCurrencyModeOn(false);
  };
  const handleSaveChanges = () => {
    handleUpdateOrganization(updatedOrganization);
    if (!isErrorOccuredWhileUpdating) {
      setEditCurrencyModeOn(false);
      setEditDateFormatModeOn(false);
    }
  };
  const [updatedOrganization, setUpdatedOrganization] = useState<IOrganization>(
    {} as IOrganization
  );

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    if (name.startsWith('preferences')) {
      const preferences: string[] = name.split('.');
      setCompanyProfile({
        ...organization,
        preferences: {
          ...organization.preferences,
          [preferences[1]]: value,
        },
      });

      setUpdatedOrganization(
        (prevState) =>
          ({
            ...prevState,
            preferences: {
              ...prevState.preferences,
              [preferences[1]]: value,
            },
          }) as IOrganization
      );
      return;
    }
  };

  return (
    <>
      <ProfileHeading>{t('DATE_AND_CURRENCY')}</ProfileHeading>
      <BorderDivLine width="100%" />
      <TabContentMainContainer>
        <TabContentMainContainerHeading>
          <h4>{t('DATE_FORMAT')}</h4>
          {user &&
            hasPermission(user, ORGANIZATION_MODULE.UPDATE_ORGANIZATION) && (
              <TabContentEditArea>
                {!isEditDateFormatModeOn ? (
                  <span
                    onClick={() => {
                      handleIsEditDateFormatModeOn();
                      handleCancelUpdate();
                    }}
                  >
                    <EditWhitePenSVG />
                  </span>
                ) : (
                  <span>
                    <span title={t('SAVE_CHANGES')} onClick={handleSaveChanges}>
                      <CheckBoxOnSVG />
                    </span>
                    <span
                      title={t('DISCARD_CHANGES')}
                      onClick={() => {
                        handleIsEditDateFormatModeOff();
                        handleCancelUpdate();
                      }}
                    >
                      <CrossMarkSVG />
                    </span>
                  </span>
                )}
              </TabContentEditArea>
            )}
        </TabContentMainContainerHeading>
        <BorderDivLine width="100%" />
        <Container>
          <Row>
            <Label>{t('DATE_FORMAT')}</Label>
            <Select
              name="preferences.dateFormat"
              onChange={handleInputChange}
              disabled={!isEditDateFormatModeOn}
              value={
                organization.preferences && organization.preferences.dateFormat
                  ? organization.preferences.dateFormat
                  : ''
              }
            >
              {Object.keys(DATE_FORMATS).map((key) => (
                <option key={DATE_FORMATS[key]} value={key}>
                  {DATE_FORMATS[key]}
                </option>
              ))}
            </Select>
          </Row>
          <Row>
            <Label>Time Zone</Label>
            <Select
              name="preferences.timeZone"
              onChange={handleInputChange}
              disabled={!isEditDateFormatModeOn}
              value={
                organization.preferences && organization.preferences.timeZone
                  ? organization.preferences.timeZone
                  : ''
              }
            >
              {Object.keys(TIME_ZONES).map((key) => (
                <option key={TIME_ZONES[key]} value={key}>
                  {TIME_ZONES[key]}
                </option>
              ))}
            </Select>
          </Row>
        </Container>
      </TabContentMainContainer>
      <TabContentMainContainer>
        <TabContentMainContainerHeading>
          <h4>{t('CURRENCY')}</h4>

          {user &&
            hasPermission(user, ORGANIZATION_MODULE.UPDATE_ORGANIZATION) && (
              <TabContentEditArea>
                {!isEditCurrencyModeOn ? (
                  <span
                    onClick={() => {
                      handleCancelUpdate();
                      handleIsEditCurrencyModeOn();
                    }}
                  >
                    <EditWhitePenSVG />
                  </span>
                ) : (
                  <span>
                    <span title={t('SAVE_CHANGES')} onClick={handleSaveChanges}>
                      <CheckBoxOnSVG />
                    </span>
                    <span
                      title={t('DISCARD_CHANGES')}
                      onClick={() => {
                        handleIsEditCurrencyModeOff();
                        handleCancelUpdate();
                      }}
                    >
                      <CrossMarkSVG />
                    </span>
                  </span>
                )}
              </TabContentEditArea>
            )}
        </TabContentMainContainerHeading>
        <BorderDivLine width="100%" />
        <Container>
          <Row>
            <Label>{t('CURRENCY_TYPE')}</Label>
            <Select
              name="preferences.currencyType"
              disabled={!isEditCurrencyModeOn}
              onChange={handleInputChange}
              value={
                organization.preferences &&
                organization.preferences.currencyType
                  ? organization.preferences.currencyType
                  : ''
              }
            >
              {Object.keys(CURRENCY_TYPES).map((key) => (
                <option key={CURRENCY_TYPES[key]} value={key}>
                  {CURRENCY_TYPES[key]}
                </option>
              ))}
            </Select>
          </Row>
        </Container>
      </TabContentMainContainer>
    </>
  );
};
