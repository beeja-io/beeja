import { useNavigate } from 'react-router-dom';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { PayrollMainContainer } from '../styles/LoanApplicationStyles.style';
import { FeatureToggleContainer } from '../styles/FeatureToggleStyles.style';
import { EFeatureToggles, IFeatureToggle } from '../entities/FeatureToggle';
import { SwitchLabel, StyledSwitch, Slider } from '../styles/InputStyles.style';
import { removeUnderScore } from '../utils/stringUtils';
import { useFeatureToggles } from '../context/FeatureToggleContext';
import { ChangeEvent, useEffect, useState } from 'react';
import { updateFeatureTogglesByOrgId } from '../service/axiosInstance';
import { useTranslation } from 'react-i18next';

const FeatureToggleScreen = () => {
  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const { featureToggles, updateFeatureToggles } = useFeatureToggles();

  const [featureTogglesToBeUpdated, setFeatureTogglesToBeUpdated] =
    useState<IFeatureToggle>({} as IFeatureToggle);

  const [features, setFeatures] = useState<string[]>(
    Array.from(featureToggles?.featureToggles ?? [])
  );

  const handleCheckboxChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = e.target;
    setFeatures((prevFeatures) => {
      if (checked) {
        return [...prevFeatures, name];
      } else {
        return prevFeatures.filter((feature) => feature !== name);
      }
    });

    const updatedFeatures = checked
      ? [...features, name]
      : features.filter((feature) => feature !== name);
    setFeatureTogglesToBeUpdated((prev) => ({
      ...prev,
      featureToggles: updatedFeatures,
    }));
  };

  useEffect(() => {
    const updateFeatureTogglesOfOrg = async () => {
      if (Object.keys(featureTogglesToBeUpdated).length !== 0) {
        const res = await updateFeatureTogglesByOrgId(
          featureToggles ? featureToggles.organizationId : '',
          featureTogglesToBeUpdated
        );
        updateFeatureToggles(res.data);
      }
    };
    updateFeatureTogglesOfOrg();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [featureTogglesToBeUpdated]);

  const keys = Object.keys(EFeatureToggles);
  const { t } = useTranslation();
  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('FEATURE_TOGGLES')}
          </span>
        </ExpenseHeadingSection>
        <PayrollMainContainer>
          <span>{t('CHOOESE_ALL_FEATURES_WHICH_SHOULD_ENABLE')}</span>

          <FeatureToggleContainer>
            <div>
              <div>
                {keys.map((key, index) => (
                  <span className="innerDiv" key={index}>
                    {removeUnderScore(key.toString())}{' '}
                    {featureToggles && (
                      <SwitchLabel>
                        <StyledSwitch
                          type="checkbox"
                          name={key.toString()}
                          value={key.toString()}
                          checked={features.includes(key.toString())}
                          onChange={handleCheckboxChange}
                          disabled={
                            key === EFeatureToggles.EMPLOYEES_MANAGEMENT ||
                            key === EFeatureToggles.EMPLOYEE_DOCUMENT_MANAGEMENT
                          }
                        />
                        <Slider />
                      </SwitchLabel>
                    )}
                  </span>
                ))}
              </div>
            </div>
          </FeatureToggleContainer>
        </PayrollMainContainer>
      </ExpenseManagementMainContainer>
    </>
  );
};

export default FeatureToggleScreen;
