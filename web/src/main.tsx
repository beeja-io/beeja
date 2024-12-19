import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.tsx';
import { BrowserRouter } from 'react-router-dom';
import { UserProvider } from './context/UserContext.tsx';
import { PreferencesProvider } from './context/PreferencesContext.tsx';
import { FeatureToggleContextProvider } from './context/FeatureToggleContext.tsx';
import { I18nextProvider } from 'react-i18next';
import i18n from '../i18n.ts';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <UserProvider>
        <FeatureToggleContextProvider>
          <PreferencesProvider>
            <I18nextProvider i18n={i18n}>
              <App />
            </I18nextProvider>
          </PreferencesProvider>
        </FeatureToggleContextProvider>
      </UserProvider>
    </BrowserRouter>
  </React.StrictMode>
);
