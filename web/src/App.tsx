import { useState, useEffect } from 'react';
import CompleteNavBar from './components/reusableComponents/CompleteNavBar.component';
import { Container } from './styles/MainContainer.style';
import { useUser } from './context/UserContext';
import { fetchMe, getFeatureToggles } from './service/axiosInstance';
import { ApplicationContextProvider } from './context/ApplicationContext';
import { Toaster } from 'sonner';
import { OriginURL, ProdOriginURL } from './constants/UrlConstants';
import { ThemeProvider } from 'styled-components';
import { Theme } from './theme/theme.style';
import SpinAnimation from './components/loaders/SprinAnimation.loader';
import { usePreferences } from './context/PreferencesContext';
import { IPreferences } from './entities/OrganizationEntity';
import { useFeatureToggles } from './context/FeatureToggleContext';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import UnAuthorisedScreen from './screens/UnAuthorisedScreen.screen';
import ServiceUnavailable from './screens/ServiceUnavailable.screen';
import { ProfileImageProvider } from './context/ProfileImageContext';

function App() {
  const [isLoaded, setIsLoaded] = useState(false);
  const [errorCode, setIsErrorCode] = useState<number>();
  const { setUser } = useUser();
  const { preferences, setPreferences } = usePreferences();
  const { updateFeatureToggles } = useFeatureToggles();

  const navigate = useNavigate();

  useEffect(() => {
    const checkLogin = async () => {
      try {
        const response = await fetchMe();

        if (response.status === 200) {
          setUser(response.data);
          setPreferences(
            response.data.organizations.preferences as IPreferences
          );
        }
        setIsLoaded(true);
        setIsErrorCode(200);
        if (response.data.organizations.preferences.fontName) {
          document.documentElement.style.setProperty(
            '--font-family-primary',
            response.data.organizations.preferences.fontName
          );
        }
        const sizeValue = `${response.data.organizations.preferences.fontSize}px`;
        document.documentElement.style.setProperty(
          '--font-size-primary',
          sizeValue
        );
      } catch (error) {
        if (axios.isAxiosError(error)) {
          if (
            error.response?.status === 503 ||
            error.response?.status === 500
          ) {
            setIsErrorCode(503);
            setIsLoaded(true);
            navigate('/service-unavailable');
            return;
          }
          if (error.response?.status === 403) {
            setIsErrorCode(403);
            setIsLoaded(true);
            navigate('/unauthorised');
            return;
          }
        }
        window.location.href =
          window.location.origin === OriginURL ||
          window.location.origin === ProdOriginURL
            ? `${window.location.origin}/login`
            : `${import.meta.env.VITE_API_BASE_URL}/login`;
        setIsLoaded(true);
      }
    };
    checkLogin();

    const fetchFeatureToggles = async () => {
      const response = await getFeatureToggles();
      updateFeatureToggles(response.data);
    };
    fetchFeatureToggles();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [setPreferences, setUser]);

  const renderContent = () => {
    if (!isLoaded) {
      return <SpinAnimation />;
    }

    switch (errorCode) {
      case 200:
        return (
          <ProfileImageProvider>
            {' '}
            {/* Wrap the main content with ProfileImageProvider */}
            <CompleteNavBar />
          </ProfileImageProvider>
        );
      case 403:
        return <UnAuthorisedScreen />;
      case 503:
        return <ServiceUnavailable />;
      default:
        return <SpinAnimation />;
    }
  };

  return (
    <ThemeProvider
      theme={
        preferences
          ? preferences.theme === 'LIGHT'
            ? Theme.light
            : Theme.dark
          : Theme.light
      }
    >
      <ApplicationContextProvider>
        <Container>
          <Toaster position="top-right" closeButton />
          {renderContent()}
        </Container>
      </ApplicationContextProvider>
    </ThemeProvider>
  );
}

export default App;
