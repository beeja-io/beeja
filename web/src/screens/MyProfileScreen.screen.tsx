import { useEffect, useState } from 'react';
import {
  MyProfileHeadingSection,
  MyProfileInformationContainer,
  MyProfileInnerContainer,
  MyProfileMainContainer,
} from '../styles/MyProfile.style';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import MyProfileQuickDetailsComponent from '../components/reusableComponents/MyProfileQuickDetails.component';
import MyProfileTabsContainerComponent from '../components/reusableComponents/MyProfileTabsContainer.component';
import { useLocation, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { fetchEmployeeDetailsByEmployeeId } from '../service/axiosInstance';
import { useTranslation } from 'react-i18next';

const MyProfileScreen = () => {
  const [employee, setEmployee] = useState<EmployeeEntity>();

  const { user } = useUser();

  const location = useLocation();
  const [employeeId, setEmployeeId] = useState<string | undefined>(undefined);
  useEffect(() => {
    setEmployeeId(location.state?.employeeId || user?.employeeId);
  }, [location, user]);
  const fetchData = async () => {
    setIsUpdateResponseLoading(true);
    try {
      if (employeeId) {
        const response = await fetchEmployeeDetailsByEmployeeId(employeeId);
        setEmployee(response.data);
        setIsUpdateResponseLoading(false);
      }
    } catch (error) {
      setIsUpdateResponseLoading(false);
      throw new Error('Error fetching data:' + error);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [employeeId]);

  const [isUpdateResponseLoading, setIsUpdateResponseLoading] = useState(false);

  const navigate = useNavigate();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    navigate(-1);
  };

  const [tabName, setTabName] = useState(t('GENERAL'));
  const chooseTab = (tab: string) => {
    setTabName(tab);
  };

  return (
    <MyProfileMainContainer>
      <MyProfileInnerContainer>
        <MyProfileHeadingSection>
          {' '}
          <span onClick={goToPreviousPage}>
            <ArrowDownSVG />
          </span>
          {t(`${tabName.toUpperCase()}`)}
        </MyProfileHeadingSection>
        <MyProfileInformationContainer>
          <MyProfileQuickDetailsComponent
            employee={employee}
            fetchEmployeeAgain={fetchData}
            isLoadingResponse={isUpdateResponseLoading}
          />
          {employee ? (
            <MyProfileTabsContainerComponent
              employee={employee}
              fetchEmployeeAgain={fetchData}
              chooseTab={chooseTab}
            />
          ) : (
            <section className="actualMainInfo">
              <span className="skeleton-div quick-details-tabs-skeleton">
                {[...Array(4).keys()].map((index) => (
                  <span key={index} className="skeleton skeleton-text">
                    &nbsp;
                  </span>
                ))}
              </span>

              <span className="skeleton-div quick-details-tabs-skeleton">
                {[...Array(4).keys()].map((index) => (
                  <span key={index} className="skeleton skeleton-text">
                    &nbsp;
                  </span>
                ))}
              </span>
            </section>
          )}
        </MyProfileInformationContainer>
      </MyProfileInnerContainer>
    </MyProfileMainContainer>
  );
};

export default MyProfileScreen;
