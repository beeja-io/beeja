import React, { useState } from 'react';
import { Button } from '../styles/CommonStyles.style';
import {
  MyProfileInnerContainer,
  MyProfileTabsDiv,
  UnderLineEmp,
} from '../styles/MyProfile.style';
import { createIDPattern } from '../service/axiosInstance';
import {
  FormContainer,
  FormGroup,
  ToggleSwitchContainer,
  ButtonGroup,
  ToggleInfoContainer,
  ToggleInfoText,
} from '../styles/IDPatternCreateStyles.style';
import {
  CheckedSVG,
  UncheckedSVG,
  AlertIDPatternSVG,
} from '../svgs/CreateIDPatternSvgs.svg';
import { t } from 'i18next';

interface EmployeeIDCreateProps {
  onClose: () => void;
  refreshPatterns: () => void;
  patternType: 'LOAN_ID_PATTERN' | 'EMPLOYEE_ID_PATTERN' | 'DEVICE_ID_PATTERN';
}
const EmployeeIDCreate: React.FC<EmployeeIDCreateProps> = ({
  onClose,
  refreshPatterns,
  patternType,
}) => {
  const [formData, setFormData] = useState({
    idLength: null,
    prefix: '',
    initialSequence: null,
    active: false,
  });

  const [isChecked, setIsChecked] = useState(false);
  const handleToggleChange = () => {
    setIsChecked(!isChecked);
    setFormData((prevData) => ({
      ...prevData,
      active: !isChecked,
    }));
  };

  const handleReset = () => {
    setFormData({
      idLength: null,
      prefix: '',
      initialSequence: null,
      active: false,
    });
    setIsChecked(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      patternType,
      patternLength: formData.idLength,

      prefix: formData.prefix,
      initialSequence: formData.initialSequence,
      active: formData.active,
    };
    await createIDPattern(payload);
    onClose();
    refreshPatterns();
  };
  return (
    <MyProfileInnerContainer className="settings-tab" style={{ width: '100%' }}>
      <MyProfileTabsDiv className="under-line emp">
        <UnderLineEmp>
          {t('CREATE')}{' '}
          {patternType === 'LOAN_ID_PATTERN'
            ? 'Loan ID'
            : patternType === 'EMPLOYEE_ID_PATTERN'
              ? 'Employee ID'
              : 'Device ID'}{' '}
          {t('PATTERN')}
        </UnderLineEmp>
      </MyProfileTabsDiv>
      <FormContainer onSubmit={handleSubmit}>
        <FormGroup>
          <label>{t('ID_LENGTH')}</label>
          <input
            type="text"
            name="idLength"
            placeholder="Specify ID Length"
            onChange={handleInputChange}
          />
        </FormGroup>
        <FormGroup>
          <label>{t('ID_PREFIX')}</label>
          <input
            type="text"
            name="prefix"
            placeholder="Specify ID Prefix"
            onChange={handleInputChange}
          />
        </FormGroup>
        <FormGroup>
          <label>{t('INTIAL_SEQUENCE')}</label>

          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '8px',
              whiteSpace: 'nowrap',
            }}
          >
            <input
              type="text"
              name="initialSequence"
              placeholder="Specify Initial Sequence"
              onChange={handleInputChange}
            />
            <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
              <AlertIDPatternSVG></AlertIDPatternSVG>
              <span className="custom-text-style">
                {t(
                  'AUTO_SEQUENCE_ID_WILL_START_FROM_THE_SPECIFIED_INTIAL_SEQUENCE_NUMBER'
                )}
              </span>
            </div>
          </div>
        </FormGroup>

        <ToggleSwitchContainer isChecked={isChecked}>
          <label>{t('STATUS')}</label>
          <div className="toggle-switch-container">
            <div className="toggle-switch" onClick={handleToggleChange}>
              <input
                type="checkbox"
                checked={isChecked}
                onChange={handleToggleChange}
              />
              {isChecked ? <CheckedSVG /> : <UncheckedSVG />}
            </div>
          </div>
        </ToggleSwitchContainer>

        {/* Info container directly below the Toggle */}
        <ToggleInfoContainer>
          <AlertIDPatternSVG></AlertIDPatternSVG>
          <ToggleInfoText>
            {t('ENABLING_THIS_PATTERN_WILL_DISABLE_OTHERS')}
          </ToggleInfoText>
        </ToggleInfoContainer>
        <ButtonGroup>
          <Button className="reset-btn" type="button" onClick={handleReset}>
            {t('RESET')}
          </Button>
          <Button className="submit-btn" type="submit">
            {t('CREATE_PATTERN')}
          </Button>
        </ButtonGroup>
      </FormContainer>
    </MyProfileInnerContainer>
  );
};
export default EmployeeIDCreate;
