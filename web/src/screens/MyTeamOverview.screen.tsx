import { useEffect, useState, useCallback } from 'react';
import { DynamicSpaceMainContainer } from '../styles/CommonStyles.style';

import { FilterSection } from '../styles/ExpenseListStyles.style';
import {
  EmployeeListContainer,
  Monogram,
  TableBodyRow,
} from '../styles/EmployeeListStyles.style';
import { useTranslation } from 'react-i18next';
import { Table, TableContainer, TableHead } from '../styles/TableStyles.style';
import { useLocation, useNavigate } from 'react-router-dom';
import React from 'react';
import { useUser } from '../context/UserContext';
import {
  downloadEmployeeFile,
  getOrganizationValuesByKey,
  fetchEmployeeDetailsByEmployeeId,
  getAllPerformanceEmployees,
} from '../service/axiosInstance';
import { EMPLOYEE_MODULE } from '../constants/PermissionConstants';
import { DynamicSpace } from '../styles/NavBarStyles.style';
import { hasPermission } from '../utils/permissionCheck';
import Pagination from '../components/directComponents/Pagination.component';
import { toast } from 'sonner';
import { OrgDefaults } from '../entities/OrgDefaultsEntity';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';
import {
  RatingCenter,
  StatusCell,
  TableMain,
  Title,
  TitleSection,
} from '../styles/MyTeamOverview.style';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

interface EmployeeEntity {
  employeeId: string;
  organizationId: string;

  firstName: string | null;
  lastName: string | null;
  email: string | null;

  jobDetails: {
    designation: string | null;
    employementType: string | null;
    department: string | null;
  } | null;

  profilePictureId: string | null;

  overallRating: number | null;
  numberOfReviewersAssigned: number | null;
  numberOfReviewerResponses: number | null;

  active: boolean;
}

const EmployeeList = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user } = useUser();
  const [isLoadingData, setLoadingData] = useState(false);
  const [isLoadingEmployees, setIsLoadingEmployees] = useState(false);

  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [toasting, setToast] = useState<{
    type: 'success' | 'error';
    message: string;
    head: string;
  } | null>(null);

  const [departmentFilter, setDepartmentFilter] = useState<string>('');
  const [JobTitleFilter, setJobTitleFilter] = useState<string>('');
  const [EmployeeStatusFilter, setEmployeeStatusFilter] = useState<string>('');
  const [employees, setEmployees] = useState<EmployeeEntity[]>([]);

  const [departmentOptions, setDepartmentOptions] = useState<OrgDefaults>();
  const [jobTitles, setJobTitles] = useState<OrgDefaults>();
  const location = useLocation();
  const initialPage =
    Number(localStorage.getItem('employeeListCurrentPage')) ||
    location.state?.page ||
    1;
  const [currentPage, setCurrentPage] = useState(initialPage);

  const handleDepartmentChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setDepartmentFilter(event.target.value);
  };

  const handleJobTitleChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setJobTitleFilter(event.target.value);
  };

  const handleEmployeeStatusChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setEmployeeStatusFilter(event.target.value);
  };

  const [employeeImages, setEmployeeImages] = useState<Map<string, string>>(
    new Map()
  );
  const [employeesWithProfilePictures, setEmployeesWithProfilePictures] =
    useState<Set<string>>(new Set());

  const fetchEmployeeImages = async () => {
    const imageUrls = new Map<string, string>();
    const hasProfilePicture = new Set<string>();

    if (employees) {
      const promises = employees.map(async (emp) => {
        const employeeId = emp.employeeId;
        const response = await fetchEmployeeDetailsByEmployeeId(employeeId);
        const profilePictureId = response.data.employee.profilePictureId;
        if (profilePictureId) {
          try {
            const fileResponse = await downloadEmployeeFile(profilePictureId);
            const imageUrl = URL.createObjectURL(fileResponse.data);
            imageUrls.set(employeeId, imageUrl);
            hasProfilePicture.add(employeeId);
          } catch (error) {
            imageUrls.set(employeeId, '');
            throw new Error(
              `Error fetching profile image for employee ${employeeId}:` + error
            );
          }
        } else {
          imageUrls.set(employeeId, '');
        }
      });
      await Promise.all(promises);
      setEmployeeImages(new Map(imageUrls));
      setEmployeesWithProfilePictures(hasProfilePicture);
    }
  };

  useEffect(() => {
    setLoadingData(true);
    fetchDepartmentOptions();
    fetchJobTitles();
    fetchEmployeeImages();
    setLoadingData(false);
  }, []);

  const fetchDepartmentOptions = async () => {
    try {
      const response = await getOrganizationValuesByKey('departments');
      setDepartmentOptions(response.data);
      if (!response?.data?.values || response.data.values.length === 0) {
        toast.error(
          'Department list is empty. Please add at least one Department in Organization.'
        );
        return;
      }
    } catch {
      toast.error(t('ERROR_WHILE_FETCHING_DEPARTMENT_OPTIONS'));
    }
  };

  const fetchJobTitles = async () => {
    try {
      const response = await getOrganizationValuesByKey('jobTitles');
      setJobTitles(response.data);
    } catch {
      toast.error(t('ERROR_WHILE_FETCHING_JOB_TITLES'));
    }
  };

  const fetchEmployees = useCallback(async () => {
    setLoadingData(true);
    setIsLoadingEmployees(false);
    try {
      const queryParams = [];
      if (departmentFilter != null && departmentFilter != '-')
        queryParams.push(`department=${encodeURIComponent(departmentFilter)}`);
      if (JobTitleFilter != null && JobTitleFilter != '-')
        queryParams.push(`designation=${encodeURIComponent(JobTitleFilter)}`);
      if (EmployeeStatusFilter != null && EmployeeStatusFilter != '-')
        queryParams.push(`status=${encodeURIComponent(EmployeeStatusFilter)}`);

      queryParams.push(`pageNumber=${currentPage}`);
      queryParams.push(`pageSize=${itemsPerPage}`);
      const queryString =
        queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

      const response = await getAllPerformanceEmployees(queryString);
      setTotalItems(response.data.totalRecords);
      setEmployees(response.data.data);
      if (
        !response.data.totalRecords ||
        response.data.totalRecords.length === 0
      ) {
        setIsLoadingEmployees(true);
      } else {
        setIsLoadingEmployees(false);
      }
    } catch {
      setToast({
        type: 'error',
        head: 'Request Failed',
        message: 'Something went wrong while processing your request.',
      });
    } finally {
      setLoadingData(false);
    }
  }, [
    currentPage,
    itemsPerPage,
    departmentFilter,
    JobTitleFilter,
    EmployeeStatusFilter,
    t,
  ]);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  const handleNavigateToDetailedView = (employeeId: string) => {
    localStorage.setItem('employeeListCurrentPage', currentPage.toString());
    navigate(`/performance/my-team-overview/${employeeId}`);
  };
  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    localStorage.setItem('employeeListCurrentPage', newPage.toString());
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);
  };

  useEffect(() => {
    return () => {
      localStorage.removeItem('employeeListCurrentPage');
    };
  }, []);

  return (
    <div>
      <TitleSection>
        <Title>{t('My_Team_Overview')}</Title>
      </TitleSection>
      <DynamicSpace>
        <EmployeeListContainer>
          <DynamicSpaceMainContainer>
            <FilterSection>
              <DropdownMenu
                className="largeContainerFil"
                name="EmployeeDepartment"
                label="Department"
                options={[
                  { label: t('DEPARTMENT'), value: '' },
                  ...(departmentOptions?.values?.map((department) => ({
                    label: department.value,
                    value: department.value,
                  })) || []),
                ]}
                value={departmentFilter}
                onChange={(e) => {
                  handleDepartmentChange({
                    target: { value: e },
                  } as React.ChangeEvent<HTMLSelectElement>);
                  setCurrentPage(1);
                }}
              />
              <DropdownMenu
                label="Job Title"
                className="largeContainerFil"
                name="JobTitle"
                options={[
                  { label: t('JOB_TITLE'), value: '' },
                  ...(jobTitles?.values?.map((jobTitle) => ({
                    label: jobTitle.value,
                    value: jobTitle.value,
                  })) || []),
                ]}
                value={JobTitleFilter}
                onChange={(e) => {
                  handleJobTitleChange({
                    target: { value: e },
                  } as React.ChangeEvent<HTMLSelectElement>);
                  setCurrentPage(1);
                }}
              />

              {user &&
                (hasPermission(user, EMPLOYEE_MODULE.CREATE_EMPLOYEE) ||
                  hasPermission(user, EMPLOYEE_MODULE.CHANGE_STATUS) ||
                  hasPermission(
                    user,
                    EMPLOYEE_MODULE.UPDATE_ROLES_AND_PERMISSIONS
                  ) ||
                  hasPermission(user, EMPLOYEE_MODULE.UPDATE_ALL_EMPLOYEES) ||
                  hasPermission(user, EMPLOYEE_MODULE.UPDATE_EMPLOYEE)) && (
                  <DropdownMenu
                    label="Status"
                    className="largeContainerFil"
                    name="employeeStatus"
                    options={[
                      { label: t('Feedback_Status'), value: '' },
                      { label: t('COMPLETED'), value: 'completed' },
                      { label: t('IN_PROGRESS'), value: 'incomplete' },
                      { label: t('NOT_ASSIGNED'), value: 'notAssigned' },
                    ]}
                    value={EmployeeStatusFilter}
                    onChange={(e) => {
                      handleEmployeeStatusChange({
                        target: { value: e },
                      } as React.ChangeEvent<HTMLSelectElement>);
                      setCurrentPage(1);
                    }}
                  />
                )}
            </FilterSection>
            <br />
            {!isLoadingEmployees && (
              <>
                <TableContainer>
                  <Table>
                    <TableHead>
                      <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                        <th style={{ width: '250px' }}>{t('Employee_Name')}</th>
                        <th style={{ width: '150px' }}>{t('JOB_TITLE')}</th>
                        <th style={{ width: '150px' }}>{t('DEPARTMENT')}</th>
                        <th style={{ width: '130px' }}>
                          {t('FEEDBACK_RECEIVED_STATUS')}
                        </th>
                        <th style={{ width: '100px' }}>
                          {t('RATING')}
                          {' (Out of 5)'}
                        </th>
                      </tr>
                    </TableHead>
                    <tbody style={{ fontSize: '14px' }}>
                      {isLoadingData ? (
                        <>
                          {[...Array(8).keys()].map((index) => (
                            <TableBodyRow key={index}>
                              <td className="profilePicArea">
                                {<Monogram className="skeleton"> </Monogram>}
                                <span className="skeleton skeleton-text"></span>
                              </td>
                              <td>
                                <div className="skeleton skeleton-text"></div>
                              </td>
                              <td>
                                <div className="skeleton skeleton-text"></div>
                              </td>
                              <td>
                                <div className="skeleton skeleton-text"></div>
                              </td>
                              <td>
                                <div className="skeleton skeleton-text"></div>
                              </td>
                            </TableBodyRow>
                          ))}
                        </>
                      ) : (
                        employees?.map((emp, index) => (
                          <React.Fragment key={index}>
                            <TableMain
                              disabled={emp.numberOfReviewersAssigned === 0}
                              onClick={() =>
                                emp.numberOfReviewersAssigned === 0
                                  ? null
                                  : handleNavigateToDetailedView(emp.employeeId)
                              }
                            >
                              <td className="profilePicArea">
                                {employeesWithProfilePictures.has(
                                  emp.employeeId
                                ) ? (
                                  <Monogram
                                    className="initials"
                                    style={{
                                      backgroundImage: `url(${employeeImages.get(emp.employeeId)})`,
                                      backgroundSize: 'cover',
                                      backgroundPosition: 'center',
                                    }}
                                  ></Monogram>
                                ) : (
                                  <Monogram
                                    className={(emp.firstName ?? 'T')
                                      .charAt(0)
                                      .toUpperCase()}
                                  >
                                    {(emp.firstName ?? 'T')
                                      .charAt(0)
                                      .toUpperCase() +
                                      (emp.lastName ?? 'A')
                                        .charAt(0)
                                        .toUpperCase()}
                                  </Monogram>
                                )}
                                <span className="nameAndMail">
                                  <span>
                                    {emp.firstName === null &&
                                    emp.lastName === null
                                      ? 't.a.cer'
                                      : emp.firstName + ' ' + emp.lastName}
                                  </span>
                                  <span className="employeeMail">
                                    {emp.email}
                                  </span>
                                </span>
                              </td>
                              <td>
                                {emp.jobDetails?.designation
                                  ? emp.jobDetails.designation
                                  : '-'}
                              </td>
                              <td>
                                {emp.jobDetails?.department
                                  ? emp.jobDetails.department
                                  : '-'}
                              </td>
                              <td>
                                <StatusCell
                                  completed={
                                    emp.numberOfReviewerResponses ===
                                      emp.numberOfReviewersAssigned &&
                                    emp.numberOfReviewersAssigned !== 0
                                  }
                                  noProviders={
                                    emp.numberOfReviewerResponses === 0 &&
                                    emp.numberOfReviewersAssigned === 0
                                  }
                                >
                                  {emp.numberOfReviewerResponses === 0 &&
                                  emp.numberOfReviewersAssigned === 0
                                    ? 'No Providers Assigned'
                                    : `${emp.numberOfReviewerResponses}/${emp.numberOfReviewersAssigned}`}
                                </StatusCell>
                              </td>
                              <td>
                                <RatingCenter>
                                  <span>
                                    {emp.overallRating
                                      ? emp.overallRating
                                      : '-'}
                                  </span>
                                </RatingCenter>
                              </td>
                            </TableMain>
                          </React.Fragment>
                        ))
                      )}
                    </tbody>
                  </Table>
                </TableContainer>
                <Pagination
                  currentPage={currentPage}
                  totalPages={Math.ceil(totalItems / itemsPerPage)}
                  handlePageChange={handlePageChange}
                  itemsPerPage={itemsPerPage}
                  handleItemsPerPage={handlePageSizeChange}
                  totalItems={totalItems}
                />
              </>
            )}
            {employees.length <= 0 && isLoadingEmployees && (
              <ZeroEntriesFound heading="No results found" />
            )}
          </DynamicSpaceMainContainer>
        </EmployeeListContainer>
      </DynamicSpace>
      {toasting && (
        <ToastMessage
          messageType={toasting.type}
          messageBody={toasting.message}
          messageHeading={toasting.head}
          handleClose={() => setToast(null)}
        />
      )}
    </div>
  );
};

export default EmployeeList;
