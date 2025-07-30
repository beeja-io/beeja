import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import MyProfileQuickDetailsComponent from '../components/reusableComponents/MyProfileQuickDetails.component';
import MyProfileTabsContainerComponent from '../components/reusableComponents/MyProfileTabsContainer.component';
import { useUser } from '../context/UserContext';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import { fetchEmployeeDetailsByEmployeeId } from '../service/axiosInstance';
import {
  MyProfileHeadingSection,
  MyProfileInformationContainer,
  MyProfileInnerContainer,
  MyProfileMainContainer,
} from '../styles/MyProfile.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';

const MyProfileScreen = () => {
  const [employee, setEmployee] = useState<EmployeeEntity>();

  const { user } = useUser();

  const location = useLocation();
  const [employeeId, setEmployeeId] = useState<string | undefined>(undefined);
  useEffect(() => {
    setEmployeeId(location.state?.employeeId || user?.employeeId);
  }, [location, user]);
  const fetchData = async (newEmployeeId?: string) => {
    setIsUpdateResponseLoading(true);
    try {
      const idToUse = newEmployeeId || employeeId;
      if (idToUse) {
        const response = await fetchEmployeeDetailsByEmployeeId(idToUse);
        setEmployee(response.data);
        setEmployeeId(idToUse);
      }
      setIsUpdateResponseLoading(false);
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

  const goToPreviousPage = () => {
    navigate(-1);
  };

  const [tabName, setTabName] = useState('General');
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
          {tabName}
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
