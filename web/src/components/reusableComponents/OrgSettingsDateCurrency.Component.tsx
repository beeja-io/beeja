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
import { Label } from '../../styles/OrganizationSettingsStyles.style';
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
import DropdownMenu from './DropDownMenu.component';

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
            <DropdownMenu
              label="Date Format"
              className={'drop'}
              value={organization.preferences?.dateFormat || ''}
              disabled={!isEditDateFormatModeOn}
              options={Object.keys(DATE_FORMATS).map((key) => ({
                label: DATE_FORMATS[key],
                value: key,
              }))}
              onChange={(selectedValue) =>
                handleInputChange({
                  target: {
                    name: 'preferences.dateFormat',
                    value: selectedValue || '',
                  },
                } as React.ChangeEvent<HTMLInputElement>)
              }
            />
          </Row>
          <Row>
            <Label>{t('TIME_ZONE')}</Label>
            <DropdownMenu
              label={t('Select Timezone')}
              value={organization.preferences?.timeZone ?? ''}
              className={'drop'}
              onChange={(value: string | null) => {
                handleInputChange({
                  target: {
                    name: 'preferences.timeZone',
                    value: value ?? '',
                  },
                } as React.ChangeEvent<HTMLInputElement>);
              }}
              options={Object.keys(TIME_ZONES).map((key) => ({
                label: TIME_ZONES[key],
                value: key,
              }))}
              disabled={!isEditDateFormatModeOn}
            />
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
            <DropdownMenu
              label="Currency Type"
              className={'drop'}
              value={organization.preferences?.currencyType ?? ''}
              disabled={!isEditCurrencyModeOn}
              options={Object.keys(CURRENCY_TYPES).map((key) => ({
                label: CURRENCY_TYPES[key],
                value: key,
              }))}
              onChange={(value) => {
                if (value !== null) {
                  const event = {
                    target: {
                      name: 'preferences.currencyType',
                      value,
                    },
                  } as React.ChangeEvent<HTMLSelectElement>;
                  handleInputChange(event);
                }
              }}
            />
          </Row>
        </Container>
      </TabContentMainContainer>
    </>
  );
};
