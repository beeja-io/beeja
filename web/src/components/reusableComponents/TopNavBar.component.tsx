import { useTranslation } from 'react-i18next';
import { TopNavBar, TopNavRightIcons } from '../../styles/NavBarStyles.style';
import {
  NotificationSVG,
  LogoutSVG,
  LanguageIcon,
} from '../../svgs/NavBarSvgs.svg';
import {
  LogoutUrl,
  OriginURL,
  ProdOriginURL,
} from '../../constants/UrlConstants';
import CenterModal from './CenterModal.component';
import { useEffect, useState } from 'react';
import { useProfileImage } from '../../context/ProfileImageContext';
import { LogoutModalSVG } from '../../svgs/CommonSvgs.svs';
import { useNavigate } from 'react-router-dom';
import { Hamburger } from '../../svgs/MobileMenuSvgs.svgs';
import MobileNavbar from '../directComponents/MobileNavbar.component';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import { Monogram } from '../../styles/EmployeeListStyles.style';
import { useUser } from '../../context/UserContext';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import { hasPermission } from '../../utils/permissionCheck';
import { TECTACADEMY } from '../../constants/PermissionConstants';
type topNavBarProps = {
  employee?: EmployeeEntity | undefined;
};
const TopNavBarComponent = ({ employee }: topNavBarProps) => {
  const { t } = useTranslation();
  const { i18n } = useTranslation();

  const languages = [
    { code: 'en', label: 'English' },
    { code: 'de', label: 'Deutsch' },
  ];

  const handleChangeLanguage = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const selectedLanguage = event.target.value;
    i18n.changeLanguage(selectedLanguage);
    localStorage.setItem('i18nextLng', selectedLanguage);
  };
  const handleLogout = async () => {
    // Clear localStorage and sessionStorage
    localStorage.clear();
    sessionStorage.clear();

    /*If application is running
      in dev environment, then it will use dev base URL
      else, it will use localhost as origin
    */
    window.location.href =
      window.location.origin === OriginURL ||
        window.location.origin === ProdOriginURL
        ? `${window.location.origin}${LogoutUrl}`
        : `${import.meta.env.VITE_API_BASE_URL}${LogoutUrl}`;
  };

  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const handleIsLogoutModalOpen = () => {
    setIsLogoutModalOpen(!isLogoutModalOpen);
  };

  const navigate = useNavigate();
  const [isMenuExpanded, setIsMenuExpanded] = useState(false);
  const handleIsMenuExpanded = () => {
    setIsMenuExpanded(!isMenuExpanded);
  };
  const { profileImageUrl } = useProfileImage();
  const [currentProfileImage, setCurrentProfileImage] = useState<string | null>(
    null
  );

  const { user } = useUser();
  const isOwnProfile = user?.employeeId === employee?.employee.employeeId;
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    if (profileImageUrl && isOwnProfile) {
      setCurrentProfileImage(profileImageUrl);
    }
    const storedImageUrl = localStorage.getItem('profileImageUrl');
    if (storedImageUrl) setCurrentProfileImage(storedImageUrl);
  }, [profileImageUrl]);
  /* eslint-enable react-hooks/exhaustive-deps */

  useKeyPress(81, () => {
    setIsLogoutModalOpen(true);
  });
  useKeyPress(27, () => {
    setIsLogoutModalOpen(false);
  });

  return (
    <TopNavBar className="topNavHeader">
      <span className="menuArea">
        <span onClick={handleIsMenuExpanded}>
          <Hamburger />
        </span>
        {isMenuExpanded && <MobileNavbar handleIsOpen={handleIsMenuExpanded} />}
      </span>
      <span className="mainTopNav">
        {/* TODO: Uncomment once implemented */}
        {/* <SearchBox className="search">
          <span className="span">
            <SearchSVG />
            <SearchInput placeholder={t('Search anything...')} />
          </span>
        </SearchBox>
        <span className="topNavLinks">
          <span>{t('DOCUMENTS')}</span>
          <span>{t('PAYSLIPS')}</span>
        </span> */}
      </span>
      <TopNavRightIcons>
        <span className="language">
          <LanguageIcon />
          <select value={i18n.language} onChange={handleChangeLanguage}>
            {languages.map((language) => (
              <option key={language.code} value={language.code}>
                {language.label}
              </option>
            ))}
          </select>
        </span>
        {user && hasPermission(user, TECTACADEMY.ACCESS_TECT_ACADEMY_LINK) && (
          <span
            className="academy"
            onClick={() =>
              window.open('https://academy.techatcore.com/programs', '_blank')
            }
          >
            {t("TECH_ACADEMY")}
          </span>
        )}
        <span>
          <NotificationSVG />
        </span>
        <span onClick={() => navigate('/profile/me')}>
          {currentProfileImage ? (
            <img
              src={currentProfileImage}
              alt={t('PROFILE')}
              key={currentProfileImage}
              style={{ borderRadius: '50%', width: '40px', height: '40px' }}
            />
          ) : (
            <Monogram
              title={t('ABOUT_ME')}
              className={`${user && user.firstName.charAt(0).toUpperCase()}`}
            >
              {user && user.firstName.charAt(0).toUpperCase()}
              {user && user.lastName.charAt(0).toUpperCase()}
            </Monogram>
          )}
        </span>
        <span onClick={handleIsLogoutModalOpen} title={t('LOGOUT')}>
          <LogoutSVG />
        </span>
      </TopNavRightIcons>
      {isLogoutModalOpen && (
        <CenterModal
          handleModalLeftButtonClick={handleIsLogoutModalOpen}
          handleModalClose={handleIsLogoutModalOpen}
          handleModalSubmit={handleLogout}
          modalHeading="LOGOUT"
          modalContent="ARE_YOU_SURE_TO_LOGOUT"
          modalSVG={<LogoutModalSVG />}
          modalType="loginModal"
          modalLeftButtonClass="mobileBtn"
          modalRightButtonClass="mobileBtn"
          modalRightButtonBorderColor="black"
          modalRightButtonTextColor="black"
        />
      )}
    </TopNavBar>
  );
};

export default TopNavBarComponent;
