import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import enTranslations from './src/locales/en/translation.json';
import deTranslations from './src/locales/de/translation.json';

i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: enTranslations,
    },
    de: {
      translation: deTranslations,
    },
  },
  lng: localStorage.getItem('i18nextLng') || 'en',
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
  react: {
    useSuspense: false,
  },
});

export default i18n;
