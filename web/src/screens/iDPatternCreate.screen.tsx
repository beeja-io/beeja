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
          Create{' '}
          {patternType === 'LOAN_ID_PATTERN'
            ? 'Loan ID'
            : patternType === 'EMPLOYEE_ID_PATTERN'
              ? 'Employee ID'
              : 'Device ID'}{' '}
          Pattern
        </UnderLineEmp>
      </MyProfileTabsDiv>
      <FormContainer onSubmit={handleSubmit}>
        <FormGroup>
          <label>ID Length</label>
          <input
            type="text"
            name="idLength"
            placeholder="Specify ID Length"
            onChange={handleInputChange}
          />
        </FormGroup>
        <FormGroup>
          <label>ID Prefix</label>
          <input
            type="text"
            name="prefix"
            placeholder="Specify ID Prefix"
            onChange={handleInputChange}
          />
        </FormGroup>
        <FormGroup>
          <label>Initial Sequence</label>

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
                Auto Sequence Id will start from the specified initial sequence
                number
              </span>
            </div>
          </div>
        </FormGroup>

        <ToggleSwitchContainer isChecked={isChecked}>
          <label>Status</label>
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
            Enabling this pattern will disable others.
          </ToggleInfoText>
        </ToggleInfoContainer>
        <ButtonGroup>
          <Button className="reset-btn" type="button" onClick={handleReset}>
            Reset
          </Button>
          <Button className="submit-btn" type="submit">
            Create Pattern
          </Button>
        </ButtonGroup>
      </FormContainer>
    </MyProfileInnerContainer>
  );
};
export default EmployeeIDCreate;
