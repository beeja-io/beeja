import { useContext, useEffect, useState, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Button,
  DynamicSpaceMainContainer,
  HighlightedBoxes,
} from '../styles/CommonStyles.style';

import { FilterSection } from '../styles/ExpenseListStyles.style';
import {
  EmployeeListContainer,
  EmployeeListHeadSection,
  Monogram,
  TableBodyRow,
} from '../styles/EmployeeListStyles.style';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useTranslation } from 'react-i18next';
import SideModal from '../components/reusableComponents/SideModal.component';
import {
  InputLabelContainer,
  SideModalResponseError,
  StyledForm,
  TextInput,
  ValidationText,
} from '../styles/InputStyles.style';
import { AlertISVG, ErrorRedMark } from '../svgs/CommonSvgs.svs';
import axios, { AxiosError } from 'axios';
import { Table, TableContainer, TableHead } from '../styles/TableStyles.style';
import {
  EmailRequired,
  EMPLOYMENT_TYPRE_REQUIRED,
  FirstNameRequired,
  LastNameRequired,
  ProfileCreationError,
} from '../constants/Constants';
import { useNavigate } from 'react-router-dom';
import React from 'react';
import { useUser } from '../context/UserContext';
import {
  createEmployee,
  getAllEmployees,
  getEmployeesCount,
  downloadEmployeeFile,
  getOrganizationValuesByKey,
  fetchEmployeeDetailsByEmployeeId,
} from '../service/axiosInstance';
import { ApplicationContext } from '../context/ApplicationContext';
import { EMPLOYEE_MODULE } from '../constants/PermissionConstants';
import { IEmployeeCount } from '../entities/respponses/EmployeeCount';
import { DynamicSpace } from '../styles/NavBarStyles.style';
import useKeyPress from '../service/keyboardShortcuts/onKeyPress';
import { hasPermission } from '../utils/permissionCheck';
import useKeyCtrl from '../service/keyboardShortcuts/onKeySave';
import Pagination from '../components/directComponents/Pagination.component';
import { OrganizationValues } from '../entities/OrgValueEntity';
import { InputLabelContainer as SelectOption } from '../styles/DocumentTabStyles.style';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { toast } from 'sonner';
import CopyPasswordPopup from '../components/directComponents/CopyPasswordPopup.component';
import { CreatedUserEntity } from '../entities/CreatedUserEntity';
import { OrgDefaults } from '../entities/OrgDefaultsEntity';
import DropdownMenu from '../components/reusableComponents/DropDownMenu.component';
import { disableBodyScroll, enableBodyScroll } from '../constants/Utility';

const EmployeeList = () => {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useUser();
  const { employeeList: finalEmpList, updateEmployeeList } =
    useContext(ApplicationContext);
  const [employeeCount, setEmployeeCount] = useState<IEmployeeCount>();
  const [isLoadingData, setLoadingData] = useState(false);

  const [itemsPerPage, setItemsPerPage] = useState(() => {
    return Number(searchParams.get('pageSize')) || 10;
  });
  const [totalItems, setTotalItems] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const [departmentFilter, setDepartmentFilter] = useState(() => {
    return searchParams.get('department') || '';
  }); //m
  const [EmployeementTypeFilter, setEmployeementTypeFilter] = useState(() => {
    return searchParams.get('employmentType') || '';
  });
  const [JobTitleFilter, setJobTitleFilter] = useState(() => {
    return searchParams.get('jobTitle') || '';
  });
  const [EmployeeStatusFilter, setEmployeeStatusFilter] = useState(() => {
    return searchParams.get('status') || '';
  });
  const [employeeTypes, setEmployeeTypes] = useState<OrgDefaults>();
  const [departmentOptions, setDepartmentOptions] = useState<OrgDefaults>();
  const [jobTitles, setJobTitles] = useState<OrgDefaults>();
  const initialPage = Number(searchParams.get('page')) || 1;
  const [currentPage, setCurrentPage] = useState(initialPage);

  useEffect(() => {
    setDepartmentFilter(searchParams.get('department') || '');
    setJobTitleFilter(searchParams.get('jobTitle') || '');
    setEmployeementTypeFilter(searchParams.get('employmentType') || '');
    setEmployeeStatusFilter(searchParams.get('status') || '');
    setCurrentPage(Number(searchParams.get('page')) || 1);
    setItemsPerPage(Number(searchParams.get('pageSize')) || 10);
  }, [searchParams]);

  const updateQueryParams = (
    updates: Record<string, string | number | null>
  ) => {
    const params = new URLSearchParams(searchParams);

    Object.entries(updates).forEach(([key, value]) => {
      if (value === '' || value === null) {
        params.delete(key);
      } else {
        params.set(key, String(value));
      }
    });

    setSearchParams(params);
  };
  const [isCreateEmployeeModelOpen, setIsCreateEmployeeModelOpen] =
    useState(false);

  const handleIsCreateEmployeeModal = () => {
    setIsCreateEmployeeModelOpen(!isCreateEmployeeModelOpen);
  };
  const [employeeImages, setEmployeeImages] = useState<Map<string, string>>(
    new Map()
  );
  const [employeesWithProfilePictures, setEmployeesWithProfilePictures] =
    useState<Set<string>>(new Set());
  const fetchEmployeeImages = async () => {
    const imageUrls = new Map<string, string>();
    const hasProfilePicture = new Set<string>();

    if (finalEmpList) {
      const promises = finalEmpList.map(async (emp) => {
        const employeeId = emp.employee.employeeId;
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

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    if (finalEmpList) {
      setLoadingData(true);
      fetchEmployeeTypes();
      fetchDepartmentOptions();
      fetchJobTitles();
      fetchEmployeeImages();
      setLoadingData(false);
    }
  }, [finalEmpList]);
  /* eslint-enable react-hooks/exhaustive-deps */

  const fetchEmployeeTypes = async () => {
    try {
      const response = await getOrganizationValuesByKey('employeeTypes');
      setEmployeeTypes(response.data);
    } catch {
      setError(t('ERROR_WHILE_FETCHING_EMPLOYEE_TYPES'));
    }
  };

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
      setError(t('ERROR_WHILE_FETCHING_DEPARTMENT_OPTIONS'));
    }
  };

  const fetchJobTitles = async () => {
    try {
      const response = await getOrganizationValuesByKey('jobTitles');
      setJobTitles(response.data);
    } catch {
      setError(t('ERROR_WHILE_FETCHING_JOB_TITLES'));
    }
  };

  const fetchEmployees = useCallback(async () => {
    setLoadingData(true);
    try {
      const queryParams = [];
      if (departmentFilter) {
        queryParams.push(`department=${encodeURIComponent(departmentFilter)}`);
      }
      if (EmployeementTypeFilter != null && EmployeementTypeFilter != '-')
        queryParams.push(
          `employmentType=${encodeURIComponent(EmployeementTypeFilter)}`
        );
      if (JobTitleFilter != null && JobTitleFilter != '-')
        queryParams.push(`designation=${encodeURIComponent(JobTitleFilter)}`);
      if (EmployeeStatusFilter != null && EmployeeStatusFilter != '-')
        queryParams.push(`status=${encodeURIComponent(EmployeeStatusFilter)}`);

      queryParams.push(`pageNumber=${currentPage}`);
      queryParams.push(`pageSize=${itemsPerPage}`);
      const queryString =
        queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

      const response = await getAllEmployees(queryString);
      const allEmployees = response.data.employeeList;
      setTotalItems(response.data.totalSize);
      updateEmployeeList(allEmployees);
      if (!allEmployees || allEmployees.length === 0) {
        setError(t('NO_EMPLYEES_FOUND'));
      } else {
        setError(null);
      }
    } catch {
      setError(t('ERROR_WHILE_FETCHING_EMPLOYEES'));
    } finally {
      setLoadingData(false);
    }
  }, [
    currentPage,
    itemsPerPage,
    departmentFilter,
    EmployeementTypeFilter,
    JobTitleFilter,
    EmployeeStatusFilter,
    updateEmployeeList,
    t,
  ]);

  const fetchEmployeeCount = async () => {
    try {
      const response = await getEmployeesCount();
      setEmployeeCount(response.data);
    } catch (error) {
      setError(t('ERROR_WHILE_FETCHING_EMPLOYEE_COUNT'));
    }
  };

  useEffect(() => {
    fetchEmployeeCount();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  const handleNavigateToDetailedView = (employeeId: string) => {
    navigate(`/profile?${searchParams.toString()}`, {
      state: {
        employeeId: employeeId,
        from: 'employeeList',
        page: currentPage,
      },
    });
  };

  /*Below state used to set response loading when we click on Creat Profile Button
    Used to prevent close function while response from BE is loading
  */
  const [isResponseLoading, setIsResponseLoading] = useState(false);

  useKeyPress(78, () => {
    user &&
      hasPermission(user, EMPLOYEE_MODULE.CREATE_EMPLOYEE) &&
      setIsCreateEmployeeModelOpen(true);
  });

  const currentEmployees = finalEmpList;
  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    updateQueryParams({
      page: newPage,
    });
  };
  const handlePageSizeChange = (newPageSize: number) => {
    setItemsPerPage(newPageSize);
    setCurrentPage(1);
    updateQueryParams({
      page: 1,
      pageSize: newPageSize,
    });
  };
  useEffect(() => {
    if (isCreateEmployeeModelOpen) {
      disableBodyScroll();
    } else {
      enableBodyScroll();
    }

    return () => {
      enableBodyScroll();
    };
  }, [isCreateEmployeeModelOpen]);
  return (
    <DynamicSpace>
      <EmployeeListContainer>
        <DynamicSpaceMainContainer>
          <EmployeeListHeadSection>
            <div>
              <h3>{t('EMPLOYEES')}</h3>
              <span>{t('MANAGE_YOUR_EMPLOYEES')}</span>
            </div>
            {user && hasPermission(user, EMPLOYEE_MODULE.CREATE_EMPLOYEE) && (
              <div>
                <Button
                  className="submit"
                  onClick={handleIsCreateEmployeeModal}
                >
                  <AddNewPlusSVG />
                  {t('ADD_NEW')}
                </Button>
              </div>
            )}
          </EmployeeListHeadSection>
          <HighlightedBoxes>
            {user && hasPermission(user, EMPLOYEE_MODULE.GET_ALL_EMPLOYEES) && (
              <div>
                {isLoadingData ? (
                  <>
                    <h4 className="skeleton skeleton-text"></h4>
                    <span className="skeleton skeleton-text"></span>
                  </>
                ) : (
                  <>
                    <h4>{t('TOTAL_EMPLOYEES')}</h4>
                    <span>{employeeCount?.totalCount}</span>
                  </>
                )}
              </div>
            )}
            <div>
              {isLoadingData ? (
                <>
                  <h4 className="skeleton skeleton-text"></h4>
                  <span className="skeleton skeleton-text"></span>
                </>
              ) : (
                <>
                  <h4>{t('ACTIVE_EMPLOYEES')}</h4>
                  <span>{employeeCount?.activeCount}</span>
                </>
              )}
            </div>
            {user && hasPermission(user, EMPLOYEE_MODULE.GET_ALL_EMPLOYEES) && (
              <div>
                {isLoadingData ? (
                  <>
                    <h4 className="skeleton skeleton-text"></h4>
                    <span className="skeleton skeleton-text"></span>
                  </>
                ) : (
                  <>
                    <h4>{t('INACTIVE_EMPLOYEES')}</h4>
                    <span>{employeeCount?.inactiveCount}</span>
                  </>
                )}
              </div>
            )}
          </HighlightedBoxes>

          {/* FIXME Uncomment below while adding search & filter functionalities */}

          {/* <EmployeeListFilterSection>
            <SearchEmployee placeholder={t('Search employee')} />
            <Button>{t('All Jobs')}</Button>
            <Button>{t('Status')}</Button>
          </EmployeeListFilterSection> */}

          <FilterSection>
            <DropdownMenu
              className="largeContainerFil"
              name="EmployeeDepartment"
              label="Department"
              options={[
                { label: t('Department'), value: '' },
                ...(departmentOptions?.values?.map((department) => ({
                  label: department.value,
                  value: department.value,
                })) || []),
              ]}
              value={departmentFilter}
              onChange={(e) => {
                const value = e ?? '';

                setDepartmentFilter(value);
                setCurrentPage(1);

                updateQueryParams({
                  department: value,
                  page: 1,
                });
              }}
            />
            <DropdownMenu
              label="Job Title"
              className="largeContainerFil"
              name="JobTitle"
              options={[
                { label: t('Job Title'), value: '' },
                ...(jobTitles?.values?.map((jobTitle) => ({
                  label: jobTitle.value,
                  value: jobTitle.value,
                })) || []),
              ]}
              value={JobTitleFilter}
              onChange={(e) => {
                const value = e ?? '';

                setJobTitleFilter(value);
                setCurrentPage(1);

                updateQueryParams({
                  jobTitle: value,
                  page: 1,
                });
              }}
            />
            <DropdownMenu
              label="Employement Type"
              className="largeContainerFil"
              name="EmployementType"
              options={[
                { label: t('Employement Type'), value: '' },
                ...(employeeTypes?.values?.map((employeeType) => ({
                  label: employeeType.value,
                  value: employeeType.value,
                })) || []),
              ]}
              value={EmployeementTypeFilter}
              onChange={(e) => {
                const value = e ?? '';

                setEmployeementTypeFilter(value);
                setCurrentPage(1);

                updateQueryParams({
                  employmentType: value,
                  page: 1,
                });
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
                    { label: t('Status'), value: '' },
                    { label: t('ACTIVE'), value: 'Active' },
                    { label: t('INACTIVE'), value: 'Inactive' },
                  ]}
                  value={EmployeeStatusFilter}
                  onChange={(e) => {
                    const value = e ?? '';

                    setEmployeeStatusFilter(value);
                    setCurrentPage(1);

                    updateQueryParams({
                      status: value,
                      page: 1,
                    });
                  }}
                />
              )}
          </FilterSection>
          <br />
          <TableContainer>
            <Table>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th style={{ width: '250px' }}>{t('NAME')}</th>
                  <th style={{ width: '120px' }}>{t('EMPLOYEE_ID')}</th>
                  <th style={{ width: '120px' }}>{t('JOB_TITLE')}</th>
                  <th style={{ width: '140px' }}>{t('TYPE')}</th>
                  <th style={{ width: '140px' }}>{t('DEPARTMENT')}</th>
                  <th style={{ width: '120px' }}>{t('STATUS')}</th>
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
                  currentEmployees?.map((emp, index) => (
                    <React.Fragment key={index}>
                      <TableBodyRow
                        onClick={() =>
                          handleNavigateToDetailedView(emp.account.employeeId)
                        }
                      >
                        <td className="profilePicArea">
                          {employeesWithProfilePictures.has(
                            emp.employee.employeeId
                          ) ? (
                            <Monogram
                              className="initials"
                              style={{
                                backgroundImage: `url(${employeeImages.get(emp.employee.employeeId)})`,
                                backgroundSize: 'cover',
                                backgroundPosition: 'center',
                              }}
                            >
                              {/* Empty because the background image handles the visual */}
                            </Monogram>
                          ) : (
                            <Monogram
                              className={emp.account.firstName
                                .charAt(0)
                                .toUpperCase()}
                            >
                              {emp.account.firstName.charAt(0).toUpperCase() +
                                emp.account.lastName.charAt(0).toUpperCase()}
                            </Monogram>
                          )}
                          <span className="nameAndMail">
                            <span>
                              {emp.account.firstName === null &&
                              emp.account.lastName === null
                                ? // FIXME
                                  't.a.cer'
                                : emp.account.firstName +
                                  ' ' +
                                  emp.account.lastName}
                            </span>
                            <span className="employeeMail">
                              {emp.account.email}
                            </span>
                          </span>
                        </td>
                        <td>
                          {emp.employee.employeeId
                            ? emp.employee.employeeId
                            : '-'}
                        </td>
                        <td>
                          {emp.employee.jobDetails
                            ? emp.employee.jobDetails.designation
                            : '-'}
                        </td>
                        <td>
                          {emp.employee.jobDetails &&
                          emp.employee.jobDetails.employementType
                            ? emp.employee.jobDetails.employementType
                            : '-'}
                        </td>
                        <td>
                          {emp.employee.jobDetails
                            ? emp.employee.jobDetails.department
                            : '-'}
                        </td>
                        <td>
                          {emp.account.active ? t('ACTIVE') : t('INACTIVE')}
                        </td>
                      </TableBodyRow>
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
          {error && <div className="error-message">{error}</div>}
        </DynamicSpaceMainContainer>

        {isCreateEmployeeModelOpen && departmentOptions && (
          <SideModal
            handleClose={
              isResponseLoading ? () => {} : handleIsCreateEmployeeModal
            }
            isModalOpen={isCreateEmployeeModelOpen}
            innerContainerContent={
              <CreateAccount
                setIsResponseLoading={setIsResponseLoading}
                isResponseLoading={isResponseLoading}
                handleClose={handleIsCreateEmployeeModal}
                reloadEmployeeList={fetchEmployees}
                refetchEmployeeCount={fetchEmployeeCount}
                departmentOptions={departmentOptions}
              />
            }
          />
        )}
      </EmployeeListContainer>
    </DynamicSpace>
  );
};

export default EmployeeList;

type CreateAccountProps = {
  handleClose: () => void;
  reloadEmployeeList: () => void;
  refetchEmployeeCount: () => void;
  setIsResponseLoading: (param: boolean) => void;
  isResponseLoading: boolean;
  departmentOptions: OrgDefaults;
};

const CreateAccount: React.FC<CreateAccountProps> = (props) => {
  const [responseMessage, setResponseMessage] = useState('');
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    employmentType: '',
    department: '',
    employeeId: '',
  });
  const [organizationValues, setOrganizationValues] =
    useState<OrganizationValues | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [showPassword, setShowPassword] = useState(false);
  const [user, setUser] = useState<CreatedUserEntity>();
  const handleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const [errors, setErrors] = useState({
    firstName: '',
    lastName: '',
    email: '',
    employmentType: '',
    department: '',
    employeeId: '',
  });
  const [emailMessage, setEmailMessage] = useState('');
  const [employeeIdMessage, setEmployeeIdMessage] = useState('');

  useEffect(() => {
    const fetchOrganizationValues = async () => {
      try {
        setLoading(true);
        const key = 'employeeTypes';
        const response = await getOrganizationValuesByKey(key);

        if (!response?.data?.values || response.data.values.length === 0) {
          toast.error(
            'Employee type is empty. Please add at least one Employee Type in Organization.'
          );
          return null;
        }
        setOrganizationValues(response.data);
      } catch (err) {
        toast.error(t('ERROR_OCCURRED_PLEASE_RELOAD'));
      } finally {
        setLoading(false);
      }
    };

    fetchOrganizationValues();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [name]: value }));
    setErrors((prevErrors) => ({ ...prevErrors, [name]: '' }));
  };

  const handleCreateEmployee = async (e: any) => {
    e.preventDefault();
    const newErrors = {
      firstName:
        formData.firstName === ''
          ? FirstNameRequired
          : /\d/.test(formData.firstName)
            ? 'FIRST_NAME_CANNOT_CONTAIN_NuMBERS'
            : '',
      lastName:
        formData.lastName === ''
          ? LastNameRequired
          : /\d/.test(formData.lastName)
            ? 'LAST_NAME_CANNOT_CONTAIN_NuMBERS'
            : '',
      email:
        formData.email === ''
          ? EmailRequired
          : !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(formData.email)
            ? 'INVALID_EMAIL_FORMAT'
            : '',
      employmentType:
        formData.employmentType === '' || formData.employmentType === null
          ? EMPLOYMENT_TYPRE_REQUIRED
          : '',
      department:
        formData.department === '' || formData.department === null
          ? 'Department Required'
          : '',
      employeeId:
        formData.employeeId === ''
          ? 'Employee ID Required'
          : formData.employeeId.length < 4
            ? 'Employee ID Length'
            : '',
    };

    setErrors(newErrors);

    if (Object.values(newErrors).every((error) => error === '')) {
      try {
        props.setIsResponseLoading(true);
        const resp = await createEmployee(formData);
        setUser(resp.data);
        if (resp.data.password) {
          handleShowPassword();
        }
        props.reloadEmployeeList();
        props.refetchEmployeeCount();
        toast.success(t('PROFILE_HAS_BEEN_SUCCESSFULLY_ADDED'));
        props.handleClose();
      } catch (e) {
        if (axios.isAxiosError(e)) {
          const axiosError: AxiosError = e;
          const errorData = axiosError.response?.data as {
            message?: string;
            code?: string;
          };

          if (errorData?.code === 'EMPLOYEE_ID_ALREADY_FOUND') {
            setResponseMessage(
              'A profile with this Employee ID already exists'
            );
            setEmployeeIdMessage(
              'A profile with this Employee ID already exists'
            );
          } else if (errorData?.code === 'EMPLOYEE_ALREADY_FOUND') {
            setResponseMessage('A profile with this email ID already exists');
            setEmailMessage('A profile with this email ID already exists');
          } else if (
            errorData?.code === 'EMAIL_ALREADY_EXISTS_IN_ANOTHER_ORG'
          ) {
            setResponseMessage(
              'Email is already existed in another organization in Beeja'
            );
            setEmailMessage(
              'Email is already existed in another organization in Beeja'
            );
          } else {
            setResponseMessage(ProfileCreationError);
          }
        }
      } finally {
        props.setIsResponseLoading(false);
        setTimeout(() => {
          setResponseMessage('');
          setEmailMessage('');
          setEmployeeIdMessage('');
        }, 5000);
      }
    }
  };
  useKeyCtrl('s', () =>
    handleCreateEmployee(event as unknown as React.FormEvent<HTMLFormElement>)
  );

  useKeyPress(27, () => {
    props.handleClose();
  });

  const { t } = useTranslation();
  return (
    <StyledForm style={{ cursor: props.isResponseLoading ? 'progress' : '' }}>
      <div>
        <h3 className="add-new-employee-heading">{t('ADD_NEW_PROFILE')}</h3>
        <InputLabelContainer>
          <label>
            {t('FIRST_NAME')}{' '}
            <ValidationText className="star">*</ValidationText>
          </label>
          <TextInput
            type="text"
            name="firstName"
            placeholder="Ex: John"
            value={formData.firstName}
            onChange={handleChange}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
              }
            }}
            className={`${errors.firstName}` ? 'errorEnabledInput' : ''}
          />
          {errors.firstName && (
            <ValidationText>
              <AlertISVG /> {errors.firstName}
            </ValidationText>
          )}
        </InputLabelContainer>

        <InputLabelContainer>
          <label>
            {t('LAST_NAME')} <ValidationText className="star">*</ValidationText>
          </label>
          <TextInput
            type="text"
            name="lastName"
            placeholder="Ex: Mark"
            value={formData.lastName}
            onChange={handleChange}
            className={`${errors.lastName}` ? 'errorEnabledInput' : ''}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
              }
            }}
          />
          {errors.lastName && (
            <ValidationText>
              <AlertISVG /> {errors.lastName}
            </ValidationText>
          )}
        </InputLabelContainer>

        <InputLabelContainer>
          <label>
            {t('EMPLOYEE_ID')}{' '}
            <ValidationText className="star">*</ValidationText>
          </label>
          <TextInput
            type="text"
            name="employeeId"
            placeholder="Ex: ORG1234"
            value={formData.employeeId}
            onChange={handleChange}
            className={`${errors.employeeId}` ? 'errorEnabledInput' : ''}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
              }
            }}
          />
          {errors.employeeId && (
            <ValidationText>
              <AlertISVG /> {errors.employeeId}
            </ValidationText>
          )}
          {employeeIdMessage && (
            <ValidationText>
              <AlertISVG /> {employeeIdMessage}
            </ValidationText>
          )}
        </InputLabelContainer>

        <InputLabelContainer>
          <label>
            {t('EMAIL_ADDRESS')}{' '}
            <ValidationText className="star">*</ValidationText>
          </label>
          <TextInput
            type="email"
            name="email"
            placeholder="Ex: john@techatcore.com"
            value={formData.email}
            onChange={handleChange}
            className={`${errors.email}` ? 'errorEnabledInput' : ''}
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
              }
            }}
          />
          {errors.email && (
            <ValidationText>
              <AlertISVG /> {errors.email}
            </ValidationText>
          )}
          {emailMessage && (
            <ValidationText>
              <AlertISVG /> {emailMessage}
            </ValidationText>
          )}
        </InputLabelContainer>

        <SelectOption>
          <label>
            {t('EMPLOYMENT_TYPE')}{' '}
            <ValidationText className="star">*</ValidationText>
          </label>
          <DropdownMenu
            label={t('SELECT_EMPLOYMENT_TYPE')}
            name="employmentType"
            id="employmentType"
            className="selectcontainer"
            onChange={(e) =>
              handleChange({
                target: { name: 'employmentType', value: e },
              } as React.ChangeEvent<HTMLSelectElement>)
            }
            disabled={
              !organizationValues?.values ||
              organizationValues.values.length === 0
            }
            options={[
              { label: t('SELECT_EMPLOYMENT_TYPE'), value: null },
              ...(organizationValues?.values || []).map((type) => ({
                label: t(type.value),
                value: type.value,
              })),
            ]}
          />
          {errors.employmentType && (
            <ValidationText>
              <AlertISVG /> {errors.employmentType}
            </ValidationText>
          )}
        </SelectOption>

        <SelectOption>
          <label>
            {t('SELECT_DEPARTMENT')}{' '}
            <ValidationText className="star">*</ValidationText>
          </label>
          <DropdownMenu
            label={t('SELECT_DEPARTMENT')}
            className="selectcontainer"
            name="department"
            id="department"
            onChange={(e) =>
              handleChange({
                target: { name: 'department', value: e },
              } as React.ChangeEvent<HTMLSelectElement>)
            }
            disabled={
              !props.departmentOptions?.values ||
              props.departmentOptions.values.length === 0
            }
            options={[
              { label: t('SELECT_DEPARTMENT'), value: null },
              ...(props.departmentOptions?.values || []).map((department) => ({
                label: t(department.value),
                value: department.value,
              })),
            ]}
          />
          {errors.department && (
            <ValidationText>
              <AlertISVG /> {errors.department}
            </ValidationText>
          )}
        </SelectOption>

        {/* TODO: Revert this back when using automatic employee id generation */}
        {/* <span>
          <ValidationText className="info">
            {t('EMPLOYEE_ID_WILL_BE_GENERATED_BASED_ON_PATTERN')}
          </ValidationText>
        </span> */}
      </div>

      <div>
        {responseMessage.length != 0 && (
          <SideModalResponseError>
            <span>
              <ErrorRedMark />
            </span>
            <span>{responseMessage}</span>
          </SideModalResponseError>
        )}
        <div className="buttonArea">
          <Button
            style={
              props.isResponseLoading
                ? { opacity: 0.3, cursor: 'not-allowed' }
                : {}
            }
            onClick={(e) => {
              if (props.isResponseLoading) {
                e.preventDefault();
              }
              if (!props.isResponseLoading) {
                props.handleClose();
              }
            }}
          >
            {t('CANCEL')}
          </Button>
          <Button
            className="submit"
            onClick={handleCreateEmployee}
            style={{ cursor: props.isResponseLoading ? 'progress' : '' }}
            disabled={props.isResponseLoading}
          >
            {t('CREATE')}
          </Button>
        </div>
      </div>
      {loading && <SpinAnimation />}
      {showPassword && (
        <CopyPasswordPopup
          password={user?.password || ''}
          handleClose={() => {
            props.handleClose();
            handleShowPassword();
          }}
          employeeId={user?.user?.employeeId || ''}
          firstName={user?.user?.firstName || ''}
          email={user?.user?.email || ''}
          companyName={user?.user?.organizations?.name?.toUpperCase() || ''}
        />
      )}
    </StyledForm>
  );
};
