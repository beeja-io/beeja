import { useLocation } from 'react-router-dom';
import { MobileNavMainContainer } from '../../styles/MobileNavBar.style';
import { HamburgeOpen } from '../../svgs/MobileMenuSvgs.svgs';
import LeftSideModal from '../reusableComponents/LeftSideModal.component';
import { useEffect, useState } from 'react';
import {
  BeejaIconSvg,
  ChevronDownSVG,
  DashBoardSVG,
  EmployeesSVG,
  MyProfileSVG,
} from '../../svgs/NavBarSvgs.svg';
import {
  DashBoardButton,
  LeftNavList,
  NavHeader,
} from '../../styles/NavBarStyles.style';
import { StyledNavLink } from '../../styles/CommonStyles.style';
import { useTranslation } from 'react-i18next';
import { Text } from 'web-kit-components';

type MobileNavBarProps = {
  handleIsOpen: () => void;
};
const MobileNavbar = (props: MobileNavBarProps) => {
  const [sidebarOpen] = useState(true);
  const { t } = useTranslation();
  const [openDropdown, setOpenDropdown] = useState<string | null>(null);
  const location = useLocation();
  const currentPath = location.pathname;
  return (
    <MobileNavMainContainer>
      <LeftSideModal
        handleClose={props.handleIsOpen}
        isModalOpen={sidebarOpen}
        innerContainerContent={
          <div>
            <NavHeader
              className="logo_details"
              isOpen={sidebarOpen ? true : false}
            >
              <BeejaIconSvg />
              <div className="logo_name">
                {' '}
                &nbsp; BEE
                <span
                  style={{
                    color: '#005792',
                    fontFamily: 'Rubik',
                    fontWeight: 400,
                  }}
                >
                  JA
                </span>
              </div>

              <span className="hamburger" onClick={props.handleIsOpen}>
                {<HamburgeOpen />}
              </span>
            </NavHeader>

            <LeftNavList className="nav-list">
              <StyledNavLink to="/">
                <li>
                  <DashBoardButton>
                    {t('DASHBOARD')}
                    <DashBoardSVG />
                  </DashBoardButton>
                </li>
              </StyledNavLink>

              <ListItem
                isSideBarOpen={sidebarOpen}
                handleClose={props.handleIsOpen}
                linkTo="/profile/me"
                tooltipName=""
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
                handleClose={props.handleIsOpen}
                linkTo="/employees"
                tooltipName=""
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
            </LeftNavList>
          </div>
        }
      ></LeftSideModal>
    </MobileNavMainContainer>
  );
};

export default MobileNavbar;

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
  isSideBarOpen: boolean;
  handleClose: () => void;
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
  useEffect(() => {
    setDropdownOpen(props.isDropdownOpen || false);
  }, [props.isDropdownOpen]);

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
              <Text fontWeight="500" onClick={props.handleClose}>
                {t(props.linkName)}
              </Text>
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
    </li>
  );
};
