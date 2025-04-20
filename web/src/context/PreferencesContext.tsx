import React, { ReactNode, createContext, useState } from 'react';
import { IPreferences } from '../entities/OrganizationEntity';

type PreferencesContextType = {
  preferences: IPreferences | null;
  setPreferences: (preferences: IPreferences | null) => void;
};
const PreferencesContext = createContext<PreferencesContextType | undefined>(
  undefined
);

export const PreferencesProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [preferences, setPreferences] = useState<IPreferences | null>(null);

  return (
    <PreferencesContext.Provider value={{ preferences, setPreferences }}>
      {children}
    </PreferencesContext.Provider>
  );
};

export const usePreferences = () => {
  const context = React.useContext(PreferencesContext);

  if (!context) {
    return { preferences: null, setPreferences: () => {}, isLoading: true };
  }

  return { ...context, isLoading: false };
};
