import { useNavigate } from 'react-router-dom';
import { hasPermission } from '../utils/permissionCheck';
import { RECRUITMENT_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';
import { Button } from 'web-kit-components';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useCallback, useEffect, useState } from 'react';
import { IApplicant } from '../entities/ApplicantEntity';
import { getAllApplicantList, getMyReferrals } from '../service/axiosInstance';
import ApplicantsList from '../components/directComponents/ApplicantsList.component';
import Pagination from '../components/directComponents/Pagination.component';

type RecruitmentManagementScreenProps = {
  isReferral: boolean;
};
const RecruitmentManagementScreen = (
  props: RecruitmentManagementScreenProps
) => {
  const navigate = useNavigate();
  const { user } = useUser();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [allApplicants, setAllApplicants] = useState<IApplicant[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalApplicants, setTotalApplicants] = useState(0);


  const handleIsLoading = () => {
    setIsLoading(!isLoading)
  }
  const fetchApplicants = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = props.isReferral
        ? await getMyReferrals()
        : await getAllApplicantList();
        setTotalApplicants(response.data.length);
      const startIndex = (currentPage - 1) * pageSize;
      setAllApplicants(response.data.slice(startIndex, startIndex + pageSize)); 
    } catch (error) {
      // FIXME
      alert(error);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, pageSize, props.isReferral]);

  useEffect(() => {
    fetchApplicants();
  }, [fetchApplicants]);

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {props.isReferral ? 'Referrals' : 'Hiring'}
          </span>
          {user && (hasPermission(user, RECRUITMENT_MODULE.CREATE_APPLICANT)
            || hasPermission(user, RECRUITMENT_MODULE.ACCESS_REFFERRAlS)) && (
              <Button
                className="submit shadow"
                width="216px"
                onClick={() =>
                  navigate(
                    !props.isReferral ? '/recruitment/hiring-management/new' : '/recruitment/my-referrals/refer'
                  )
                }
              >
                <AddNewPlusSVG /> &nbsp;
                {props.isReferral ? 'Add New Referral' : 'Add Applicant'}
              </Button>
            )}
        </ExpenseHeadingSection>
        <ApplicantsList
          allApplicants={allApplicants}
          isLoading={isLoading}
          handleIsLoading={handleIsLoading}
          isReferral={props.isReferral}
          currentPage={currentPage}
          totalApplicants={totalApplicants}
          pageSize={pageSize}
          setCurrentPage={setCurrentPage}
          setPageSize={setPageSize}
        />
        <Pagination
            currentPage={currentPage}
            totalPages={Math.ceil(totalApplicants / pageSize)} 
            handlePageChange={setCurrentPage}
            totalItems={totalApplicants}
            handleItemsPerPage={setPageSize} 
            itemsPerPage={pageSize} 
         />

      </ExpenseManagementMainContainer>
    </>
  );
};

export default RecruitmentManagementScreen;
