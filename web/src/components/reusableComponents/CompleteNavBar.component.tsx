import { useEffect, useState } from 'react';
import {
  BeejaIconSvg,
  CardSendSVG,
  ChevronDownSVG,
  DashBoardSVG,
  EmployeesSVG,
  FeatureToggleSVG,
  MoonSVG,
  MyProfileSVG,
  NavCloseArrow,
  SettingsSVG,
  SunSVG,
  TrendingUpSVG,
} from '../../svgs/NavBarSvgs.svg';
import {
  DarkTheme,
  DashBoardButton,
  LeftNavList,
  LightTheme,
  NavBarContainer,
  NavHeader,
  RightSection,
  ThemeToggler,
} from '../../styles/NavBarStyles.style';
import TopNavBarComponent from './TopNavBar.component';
import { useTranslation } from 'react-i18next';
import AllRoutes from '../../routes/allRoutes';
import { StyledNavLink } from '../../styles/CommonStyles.style';
import { useLocation } from 'react-router-dom';
import Error404Screen from '../../screens/Error404Screen.screen';
import { useUser } from '../../context/UserContext';
import {
  BULK_PAYSLIP_MODULE,
  EXPENSE_MODULE,
  FEATURE_TOGGLES_MODULE,
  INVENTORY_MODULE,
  LOAN_MODULE,
  ORGANIZATION_MODULE,
} from '../../constants/PermissionConstants';
import ServiceUnavailable from '../../screens/ServiceUnavailable.screen';
import { usePreferences } from '../../context/PreferencesContext';
import { hasPermission } from '../../utils/permissionCheck';
import { hasFeature } from '../../utils/featureCheck';
import { useFeatureToggles } from '../../context/FeatureToggleContext';
import { EFeatureToggles } from '../../entities/FeatureToggle';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';
import { t } from 'i18next';

const CompleteNavBar = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const { user } = useUser();
  const { featureToggles } = useFeatureToggles();
  const { preferences, setPreferences } = usePreferences();

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const [openDropdown, setOpenDropdown] = useState<string | null>(null);

  const handleMouseEnter = () => {
    setSidebarOpen(true);
  };

  const location = useLocation();
  const currentPath = location.pathname;

  const [activeTheme, setActiveTheme] = useState<'LIGHT' | 'DARK'>('LIGHT');

  const handleThemeClick = (theme: 'LIGHT' | 'DARK') => {
    setActiveTheme(theme);
    changeTheme(theme);
  };

  const changeTheme = (newTheme: 'LIGHT' | 'DARK') => {
    if (preferences !== null) {
      setPreferences({
        ...preferences,
        theme: newTheme,
      });
    }
  };
  useKeyPress(68, () => {
    const newTheme = activeTheme === 'LIGHT' ? 'DARK' : 'LIGHT';
    handleThemeClick(newTheme);
  });
  return (
    <>
      {user && featureToggles && (
        <>
          {currentPath === '/notfound' ? (
            <Error404Screen />
          ) : currentPath === '/service-unavailable' ? (
            <ServiceUnavailable />
          ) : (
            <>
              <NavBarContainer
                className={`sidebar ${sidebarOpen ? 'open' : ''}`}
                onMouseEnter={handleMouseEnter}
              >
                <NavHeader
                  className="logo_details"
                  isOpen={sidebarOpen ? true : false}
                >
                  <BeejaIconSvg />
                  <div className="logo_name" style={{ fontFamily: 'Rubik' }}>
                    {' '}
                    &nbsp; BEE
                    <span className="logo_name logo_name_blue">JA</span>
                  </div>
                  <span className="btn" onClick={toggleSidebar}>
                    <NavCloseArrow isOpen={sidebarOpen ? false : true} />
                  </span>
                </NavHeader>

                <LeftNavList className="nav-list">
                  <StyledNavLink to="/">
                    <li>
                      <DashBoardButton>
                        {sidebarOpen ? 'Dashboard' : ''}
                        <DashBoardSVG />
                      </DashBoardButton>
                    </li>
                  </StyledNavLink>
                  <ListItem
                    isSideBarOpen={sidebarOpen}
                    linkTo="/profile/me"
                    tooltipName="My Profile"
                    linkName="MY_PROFILE"
                    svgIcon={
                      <MyProfileSVG
                        props={{
                          isActive:
                            openDropdown === 'profile' ||
                            currentPath === '/profile/me',
                        }}
                      />
                    }
                    additionalSvgIcon={<ChevronDownSVG />}
                    isDropdownOpen={openDropdown === 'profile'}
                    setDropdownOpen={() => {
                      setOpenDropdown((prev) =>
                        prev === 'profile' ? null : 'profile'
                      );
                    }}
                  />

                  <ListItem
                    isSideBarOpen={sidebarOpen}
                    linkTo="/employees"
                    tooltipName="Employees"
                    linkName="EMPLOYEES"
                    svgIcon={
                      <EmployeesSVG
                        props={{
                          isActive:
                            openDropdown === 'employees' ||
                            currentPath === '/employees',
                        }}
                      />
                    }
                    additionalSvgIcon={<ChevronDownSVG />}
                    isDropdownOpen={openDropdown === 'employees'}
                    setDropdownOpen={() => {
                      setOpenDropdown((prev) =>
                        prev === 'employees' ? null : 'employees'
                      );
                    }}
                  />
                  {(hasPermission(user, EXPENSE_MODULE.READ_EXPENSE) ||
                    hasPermission(
                      user,
                      BULK_PAYSLIP_MODULE.CREATE_BULK_PAYSLIP
                    ) ||
                    hasPermission(user, INVENTORY_MODULE.READ_DEVICE)) &&
                    (hasFeature(
                      featureToggles.featureToggles,
                      EFeatureToggles.LOAN_MANAGEMENT
                    ) ||
                      hasFeature(
                        featureToggles.featureToggles,
                        EFeatureToggles.INVENTORY_MANAGEMENT
                      ) ||
                      hasFeature(
                        featureToggles.featureToggles,
                        EFeatureToggles.EXPENSE_MANAGEMENT
                      )) && (
                      <ListItem
                        isSideBarOpen={sidebarOpen}
                        linkTo="#"
                        tooltipName="Accounts"
                        linkName="ACCOUNTS"
                        svgIcon={
                          <TrendingUpSVG
                            props={{
                              isActive:
                                openDropdown === 'accounts' ||
                                currentPath.startsWith('/accounts'),
                            }}
                          />
                        }
                        additionalSvgIcon={<ChevronDownSVG />}
                        dropdownItems={[
                          ...(hasPermission(
                            user,
                            BULK_PAYSLIP_MODULE.CREATE_BULK_PAYSLIP
                          ) &&
                          hasFeature(
                            featureToggles.featureToggles,
                            EFeatureToggles.BULK_PAY_SLIPS
                          )
                            ? [
                                {
                                  name: 'BULK_PAYSLIP_UPLOAD',
                                  link: '/accounts/bulk-payslip',
                                },
                              ]
                            : []),
                          ...(hasPermission(
                            user,
                            EXPENSE_MODULE.READ_EXPENSE
                          ) &&
                          hasFeature(
                            featureToggles.featureToggles,
                            EFeatureToggles.EXPENSE_MANAGEMENT
                          )
                            ? [
                                {
                                  name: 'EXPENSE_MANAGEMENT',
                                  link: '/accounts/expenses',
                                },
                              ]
                            : []),
                          ...(hasPermission(
                            user,
                            INVENTORY_MODULE.READ_DEVICE
                          ) &&
                          hasFeature(
                            featureToggles.featureToggles,
                            EFeatureToggles.INVENTORY_MANAGEMENT
                          )
                            ? [
                                {
                                  name: 'INVENTORY_MANAGEMENT',
                                  link: '/accounts/inventory',
                                },
                              ]
                            : []),
                        ]}
                        isDropdownOpen={openDropdown === 'accounts'}
                        setDropdownOpen={() => {
                          setOpenDropdown((prev) =>
                            prev === 'accounts' ? null : 'accounts'
                          );
                        }}
                        hasAdditionalSvg
                      />
                    )}

                  {hasPermission(user, LOAN_MODULE.READ_LOAN) &&
                    hasFeature(
                      featureToggles.featureToggles,
                      EFeatureToggles.LOAN_MANAGEMENT
                    ) && (
                      <ListItem
                        isSideBarOpen={sidebarOpen}
                        linkTo="#"
                        tooltipName="Payroll"
                        linkName="PAYROLL"
                        svgIcon={
                          <CardSendSVG
                            props={{
                              isActive:
                                openDropdown === 'payroll' ||
                                currentPath.startsWith('/payroll'),
                            }}
                          />
                        }
                        additionalSvgIcon={<ChevronDownSVG />}
                        dropdownItems={[
                          ...(hasPermission(user, LOAN_MODULE.READ_LOAN) &&
                          hasFeature(
                            featureToggles.featureToggles,
                            EFeatureToggles.LOAN_MANAGEMENT
                          )
                            ? [
                                {
                                  name: 'LOANS',
                                  link: '/payroll/deductions-loans',
                                },
                              ]
                            : []),
                        ]}
                        isDropdownOpen={openDropdown === 'payroll'}
                        setDropdownOpen={() => {
                          setOpenDropdown((prev) =>
                            prev === 'payroll' ? null : 'payroll'
                          );
                        }}
                        hasAdditionalSvg
                      />
                    )}

                  <ListItem
                    isSideBarOpen={sidebarOpen}
                    linkTo="#"
                    tooltipName="Quick Links"
                    linkName="QUICK_LINKS"
                    svgIcon={
                      <TrendingUpSVG
                        props={{ isActive: openDropdown === 'general' }}
                      />
                    }
                    additionalSvgIcon={<ChevronDownSVG />}
                    dropdownItems={[
                      {
                        name: 'HOLIDAY_LIST',
                        link: 'https://docs.google.com/spreadsheets/d/1eqcs0PtE7R4sN69BLBur8JoKXSbmYnTpNQygnolQ37o/edit#gid=387637465',
                      },
                    ]}
                    isDropdownOpen={openDropdown === 'general'}
                    setDropdownOpen={() => {
                      setOpenDropdown((prev) =>
                        prev === 'general' ? null : 'general'
                      );
                    }}
                    hasAdditionalSvg
                  />
                  {hasPermission(
                    user,
                    FEATURE_TOGGLES_MODULE.UPDATE_FEATURE
                  ) && (
                    <ListItem
                      isSideBarOpen={sidebarOpen}
                      linkTo="/features"
                      tooltipName="Features"
                      linkName="FEATURES"
                      svgIcon={
                        <FeatureToggleSVG
                          props={{
                            isActive:
                              openDropdown === 'features' ||
                              currentPath === '/features',
                          }}
                        />
                      }
                    />
                  )}
                </LeftNavList>

                <LeftNavList className="bottomLinks">
                  {/* <ListItem
                    linkTo="#"
                    tooltipName="Help Center"
                    linkName="Help Center"
                    svgIcon={<HelpCenterSVG />}
                    isSideBarOpen={sidebarOpen}
                  /> */}
                  {hasPermission(
                    user,
                    ORGANIZATION_MODULE.READ_ORGANIZATIONS
                  ) &&
                    hasFeature(
                      featureToggles.featureToggles,
                      EFeatureToggles.ORGANIZATION_SETTINGS
                    ) && (
                      <ListItem
                        isSideBarOpen={sidebarOpen}
                        linkTo="/settings"
                        tooltipName="Settings"
                        linkName="SETTINGS"
                        svgIcon={
                          <SettingsSVG
                            props={{
                              isActive:
                                openDropdown === 'settings' ||
                                currentPath === '/settings',
                            }}
                          />
                        }
                        additionalSvgIcon={<ChevronDownSVG />}
                        isDropdownOpen={openDropdown === 'settings'}
                        setDropdownOpen={() => {
                          setOpenDropdown((prev) =>
                            prev === 'settings' ? null : 'settings'
                          );
                        }}
                      />
                    )}
                  {sidebarOpen && (
                    <ThemeToggler>
                      <>
                        <LightTheme onClick={() => handleThemeClick('LIGHT')}>
                          <SunSVG />
                          {t('LIGHT')}
                        </LightTheme>
                        <DarkTheme onClick={() => handleThemeClick('DARK')}>
                          <MoonSVG />
                          {t('DARK')}
                        </DarkTheme>
                      </>
                      <span className="beta">{t('BETA')}</span>
                    </ThemeToggler>
                  )}
                </LeftNavList>
              </NavBarContainer>
              <RightSection className="home-section">
                <TopNavBarComponent />
                <AllRoutes />
              </RightSection>
            </>
          )}
        </>
      )}
    </>
  );
};

export default CompleteNavBar;

type ListItemProps = {
  hasAdditionalSvg?: boolean;
  svgIcon: React.ReactNode;
  linkName: string;
  tooltipName: string;
  linkTo: string;
  additionalSvgIcon?: React.ReactNode;
  dropdownItems?: { name: string; link: string }[];
  isDropdownOpen?: boolean;
  setDropdownOpen?: () => void;
  isSideBarOpen?: boolean;
};

export const ListItem: React.FC<ListItemProps> = (props) => {
  const { t } = useTranslation();
  const [isDropdownOpen, setDropdownOpen] = useState(
    props.isDropdownOpen || false
  );
  const [selectedDropdownItem, setSelectedDropdownItem] = useState<
    string | null
  >(null);

  const handleSelect = () => {
    setSelectedDropdownItem(props.linkTo);
    setDropdownOpen(!isDropdownOpen);
    props.setDropdownOpen && props.setDropdownOpen();
  };
  const currentPath = location.pathname;

  const isSelectedAndActive = (item: { name: string; link: string }) => {
    const isActive =
      currentPath === item.link || currentPath.startsWith(item.link);
    const isItemSelected = isDropdownOpen
      ? selectedDropdownItem === item.link
      : false;

    return isActive || isItemSelected;
  };
  const handleDropdownItemClick = (item: { name: string; link: string }) => {
    if (isDropdownOpen) {
      setSelectedDropdownItem((prev) =>
        prev === item.link ? null : item.link
      );
    }
  };

  useEffect(() => {
    setDropdownOpen(props.isDropdownOpen || false);
  }, [props.isDropdownOpen]);

  const isLinkActive = () => {
    return (
      isSelectedAndActive({
        name: props.linkName,
        link: props.linkTo,
      }) || props.dropdownItems?.some((item) => isSelectedAndActive(item))
    );
  };

  return (
    <li key={props.linkTo} className={isDropdownOpen ? 'dropdown-open' : ''}>
      <StyledNavLink
        to={props.linkTo}
        className={`${isDropdownOpen ? 'active' : ''} ${
          isSelectedAndActive({
            name: props.linkName,
            link: props.linkTo,
          }) || props.dropdownItems?.some((item) => isSelectedAndActive(item))
            ? 'selected'
            : ''
        }`}
        onClick={handleSelect}
      >
        <a
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <i
              style={{
                color: isSelectedAndActive({
                  name: props.linkName,
                  link: props.linkTo,
                })
                  ? 'red'
                  : 'red',
              }}
            >
              {props.svgIcon}
            </i>
            <span
              className="link_name"
              style={{
                color:
                  isSelectedAndActive({
                    name: props.linkName,
                    link: props.linkTo,
                  }) ||
                  props.dropdownItems?.some((item) => isSelectedAndActive(item))
                    ? '#005792'
                    : 'black',
              }}
            >
              <span
                className={`nav_Link ${
                  isLinkActive() ? 'active_nav_link' : ''
                }`}
              >
                {t(props.linkName)}
              </span>
            </span>
          </div>
          {props.hasAdditionalSvg && (
            <div
              style={{
                position: 'relative',
                transform: isDropdownOpen ? 'rotate(180deg)' : 'rotate(0)',
                transition: 'transform 0.3s ease',
              }}
            >
              <i>{props.additionalSvgIcon}</i>
            </div>
          )}
        </a>
      </StyledNavLink>

      {props.dropdownItems && (
        <div
          className={`dropdown-container ${isDropdownOpen ? 'open' : ''}`}
          style={{
            marginLeft: '40px',
            display: `${props.isSideBarOpen ? '' : 'none'}`,
          }}
        >
          {isDropdownOpen && (
            <ul>
              {props.dropdownItems.map((item) => (
                <li key={item.link} style={{ display: 'inline' }}>
                  <StyledNavLink
                    target={
                      item.link.startsWith('https://') ? '_blank' : undefined
                    }
                    style={{
                      display: 'flex',
                      padding: '10px 0 20px 10px',
                      marginTop: '4px',
                    }}
                    to={item.link}
                    className={`dropdown-link ${
                      isSelectedAndActive(item) ? 'selected' : ''
                    }`}
                    onClick={() => handleDropdownItemClick(item)}
                  >
                    <div className="dropdown-item">
                      <span>{t(item.name)}</span>
                    </div>
                  </StyledNavLink>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}

      <span className="tooltip">{t(props.tooltipName)}</span>
    </li>
  );
};
