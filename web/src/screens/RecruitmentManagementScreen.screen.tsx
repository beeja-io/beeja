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

  const handleIsLoading = () => {
    setIsLoading(!isLoading);
  };
  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);
  };

  const fetchApplicants = useCallback(async () => {
    try {
      setIsLoading(true);

      const queryString = `?page=${currentPage}&pageSize=${itemsPerPage}`;

      const response = props.isReferral
        ? await getMyReferrals(queryString)
        : await getAllApplicantList(queryString);

      const applicantData = Array.isArray(response?.data?.applicants)
        ? response.data.applicants
        : (response?.data ?? []);

      setAllApplicants(applicantData);

      const totalCount = response?.data?.totalRecords ?? applicantData.length;
      setTotalItems(totalCount);
    } catch (error) {
      alert('Failed to fetch applicants');
      setAllApplicants([]);
      setTotalItems(0);
    } finally {
      setIsLoading(false);
    }
  }, [props.isReferral, currentPage, itemsPerPage]);

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
          setCurrentPage={setCurrentPage}
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
