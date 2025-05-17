import { useNavigate } from 'react-router-dom';
import { hasPermission } from '../utils/permissionCheck';
import { RECRUITMENT_MODULE } from '../constants/PermissionConstants';
import { useUser } from '../context/UserContext';
import { Button } from '../styles/CommonStyles.style';
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
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalApplicants, setTotalApplicants] = useState(0);

  const handleIsLoading = () => {
    setIsLoading(!isLoading);
  };
  const fetchApplicants = useCallback(async () => {
    try {
      setIsLoading(true);

      const url = props.isReferral
        ? '/recruitments/v1/referrals'
        : '/recruitments/v1/applicants/combinedApplicants';

      const response = props.isReferral
        ? await getMyReferrals(url)
        : await getAllApplicantList(url);

      const applicantData = Array.isArray(response?.data)
        ? response.data
        : response?.data?.applicants || [];

      const totalCount = response?.data?.totalSize ?? applicantData.length ?? 0;

      setTotalApplicants(totalCount);
      setTotalItems(totalCount);

      const startIndex = (currentPage - 1) * itemsPerPage;
      const paginated = applicantData.slice(
        startIndex,
        startIndex + itemsPerPage
      );
      setAllApplicants(paginated);
    } catch (error) {
      alert('Failed to fetch applicants');
      setTotalApplicants(0);
      setTotalItems(0);
    } finally {
      setIsLoading(false);
    }
  }, [currentPage, itemsPerPage, props.isReferral]);

  useEffect(() => {
    fetchApplicants();
  }, [fetchApplicants]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setPageSize(newPageSize); // <-- Add this line
    setCurrentPage(1);
  };

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
          {user &&
            (hasPermission(user, RECRUITMENT_MODULE.CREATE_APPLICANT) ||
              hasPermission(user, RECRUITMENT_MODULE.ACCESS_REFFERRAlS)) && (
              <Button
                className="submit shadow"
                width="216px"
                onClick={() =>
                  navigate(
                    !props.isReferral
                      ? '/recruitment/hiring-management/new'
                      : '/recruitment/my-referrals/refer'
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
          totalPages={Math.max(1, Math.ceil(totalItems / itemsPerPage))}
          handlePageChange={handlePageChange}
          itemsPerPage={itemsPerPage}
          handleItemsPerPage={handlePageSizeChange}
          totalItems={totalItems}
        />
      </ExpenseManagementMainContainer>
    </>
  );
};

export default RecruitmentManagementScreen;
