import React from 'react';
import {
  CenterModalContainer,
  CenterModalTypeExpenseInnerContainer,
} from '../../styles/CenterModalStyles.style';
import { ButtonContainer } from '../../styles/SettingsStyles.style';
import { useTranslation } from 'react-i18next';
import { OrgValues } from '../../entities/OrgDefaultsEntity';

type CenterModalExpenseProps = {
  handleModalClose: () => void;
  handleModalSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
  modalHeading: string;
  modalLeftButtonText: string;
  modalRightButtonText: string;
  children: React.ReactNode;
  onChange: (field: keyof OrgValues, value: string) => void; // Updated to match ExpenseType
};

const CenterModalExpense: React.FC<CenterModalExpenseProps> = ({
  handleModalClose,
  handleModalSubmit,
  modalHeading,
  modalLeftButtonText,
  modalRightButtonText,
  children,
}) => {
  const { t } = useTranslation();

  return (
    <CenterModalContainer>
      <CenterModalTypeExpenseInnerContainer>
        <div className="headingSection">
          <span className="heading">{t(modalHeading)}</span>
        </div>
        <form onSubmit={handleModalSubmit}>
          {children}
          <div className="buttonSection">
            <ButtonContainer>
              <button
                type="button"
                className="close-button"
                onClick={handleModalClose}
              >
                {t(modalLeftButtonText)}
              </button>
              <button type="submit" className="create">
                {t(modalRightButtonText)}
              </button>
            </ButtonContainer>
          </div>
        </form>
      </CenterModalTypeExpenseInnerContainer>
    </CenterModalContainer>
  );
};

export default CenterModalExpense;
