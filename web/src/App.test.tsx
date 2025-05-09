import { render, waitFor } from '@testing-library/react';
import App from './App';

jest.mock('react-router-dom', () => ({
  useNavigate: () => jest.fn(),
}));

jest.mock('./context/UserContext', () => ({
  useUser: () => ({ setUser: jest.fn() }),
}));

jest.mock('./context/PreferencesContext', () => ({
  usePreferences: () => ({
    preferences: { theme: 'LIGHT', fontName: 'Arial' },
    setPreferences: jest.fn(),
  }),
}));

jest.mock('./context/FeatureToggleContext', () => ({
  useFeatureToggles: () => ({ updateFeatureToggles: jest.fn() }),
}));

jest.mock('./service/axiosInstance', () => ({
  fetchMe: jest.fn(() =>
    Promise.resolve({
      status: 200,
      data: {
        organizations: {
          preferences: {
            theme: 'LIGHT',
            fontName: 'Arial',
          },
        },
      },
    })
  ),
  getFeatureToggles: jest.fn(() => Promise.resolve({ data: {} })),
}));

jest.mock('./components/loaders/SprinAnimation.loader', () => () => (
  <div>Loading...</div>
));
jest.mock(
  './components/reusableComponents/CompleteNavBar.component',
  () => () => <div>Navbar</div>
);
jest.mock('./screens/UnAuthorisedScreen.screen', () => () => <div>403</div>);
jest.mock('./screens/ServiceUnavailable.screen', () => () => <div>503</div>);

describe('App Component', () => {
  it('renders main content when fetch is successful', async () => {
    const { getByText } = render(<App />);
    await waitFor(() => {
      expect(getByText('Navbar')).toBeInTheDocument();
    });
  });
});
