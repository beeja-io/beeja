import React, { ReactNode, createContext, useState } from 'react';
import { IFeatureToggle } from '../entities/FeatureToggle';

interface FeatureToggleContextType {
  featureToggles: IFeatureToggle | null | undefined;
  updateFeatureToggles: (
    featureToggles: IFeatureToggle | null | undefined
  ) => void;
}

export const FeatureToggleContext = createContext<FeatureToggleContextType>({
  featureToggles: null,
  updateFeatureToggles: () => {},
});

export const FeatureToggleContextProvider: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [featureToggles, setFeatureToggles] = useState<
    IFeatureToggle | null | undefined
  >(null);

  const updateFeatureToggles = (
    featureToggles: IFeatureToggle | null | undefined
  ) => {
    setFeatureToggles(featureToggles);
  };

  return (
    <FeatureToggleContext.Provider
      value={{ featureToggles, updateFeatureToggles }}
    >
      {children}
    </FeatureToggleContext.Provider>
  );
};

export const useFeatureToggles = () => {
  const context = React.useContext(FeatureToggleContext);

  if (!context) {
    throw new Error(
      'useFeatureToggles must be used within a FeatureToggleContextProvider'
    );
  }

  return context;
};
