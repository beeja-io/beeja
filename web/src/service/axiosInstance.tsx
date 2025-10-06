import axios, { AxiosResponse } from 'axios';
import { OriginURL, ProdOriginURL } from '../constants/UrlConstants';
import { IFeatureToggle } from '../entities/FeatureToggle';
import { IAssignedInterviewer } from '../entities/ApplicantEntity';
import { OrganizationValues } from '../entities/OrgValueEntity';
import { Project } from '../entities/Projects';
import { InvoiceIdentifiers } from '../entities/Requests/InvoiceIdentifiersRequest';
import {
  ProjectEntity,
  ProjectStatus,
  Employee,
} from '../entities/ProjectEntity';
import { ProjectFormData } from '../components/directComponents/AddProjectForm.component';
import { ContractDetails } from '../entities/ContractEntiy';
/* eslint-disable */
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      const { pathname, origin } = window.location;
      const loginPath = '/login';
      const loginURL =
        origin === OriginURL || origin === ProdOriginURL
          ? `${origin}${loginPath}`
          : `${import.meta.env.VITE_API_BASE_URL}${loginPath}`;

      if (
        pathname !== '/login' &&
        pathname !== '/service-unavailable' &&
        pathname !== '/' &&
        pathname !== ''
      ) {
        window.location.href = loginURL;
      }
    }
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 503) {
      if (window.location.pathname !== '/service-unavailable') {
        window.location.href = '/service-unavailable';
      }
    }
    return Promise.reject(error);
  }
);

export const fetchMe = (): Promise<AxiosResponse> => {
  return axiosInstance.get('/accounts/v1/users/me');
};

export const updateEmployeeStatusByEmployeeId = (
  employeeId: string
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/accounts/v1/users/${employeeId}/status`);
};

export const getEmployeesCount = (): Promise<AxiosResponse> => {
  return axiosInstance.get('/accounts/v1/users/count');
};

export const getAllEmployeesByPermission = (
  permission: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/accounts/v1/users/permissions/${permission}`);
};

export const updateEmployeeRole = (
  employeeId: string,
  roles: string[]
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/accounts/v1/users/roles/${employeeId}`, {
    roles,
  });
};

export const createEmployee = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post('/accounts/v1/users', data);
};

export const fetchEmployeeDetailsByEmployeeId = (
  employeeId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/employees/v1/users/${employeeId}`);
};

export const updateEmployeeDetailsByEmployeeId = (
  employeeId: string,
  data?: any
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/employees/v1/users/${employeeId}`, data);
};
export const getAllEmployees = (
  queryString: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/employees/v1/users${queryString}`);
};

export const uploadProfilePicture = (
  file: File,
  entityId: string
): Promise<AxiosResponse> => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('entityId', entityId);
  return axiosInstance.post('employees/v1/files/profile-pic', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const uploadEmployeeFiles = (
  formData: FormData
): Promise<AxiosResponse> => {
  return axiosInstance.post('/employees/v1/files', formData);
};

export const getAllFilesByEmployeeId = (
  url: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(url);
};

export const deleteEmployeeFile = (fileId: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`/employees/v1/files/${fileId}`);
};

export const downloadEmployeeFile = (
  fileId: string
): Promise<AxiosResponse<Blob>> => {
  return axiosInstance.get(`/employees/v1/files/download/${fileId}`, {
    responseType: 'blob',
  });
};

export const getAllSettingTypes = (key: String): Promise<AxiosResponse> => {
  return axiosInstance.get(`/accounts/v1/organizations/values/${key}`);
};

export const updateSettingType = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.put('/accounts/v1/organizations/update-values', data);
};

export const getAllExpenses = (url?: string): Promise<AxiosResponse> => {
  return axiosInstance.get(url || '/expenses/v1');
};

export const expenseReceiptDownload = (
  fileId: string
): Promise<AxiosResponse<Blob>> => {
  return axiosInstance.get(`/expenses/v1/receipts/${fileId}`, {
    responseType: 'blob',
  });
};

export const createExpense = (expense: FormData): Promise<AxiosResponse> => {
  return axiosInstance.post(`/expenses/v1`, expense, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const createIDPattern = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/accounts/v1/organization/patterns`, data);
};

export const getIDPatterns = (patternType: string): Promise<AxiosResponse> => {
  return axiosInstance.get(`/accounts/v1/organization/patterns`, {
    params: { patternType },
  });
};

export const updatePatternStatus = async (
  patternId: string,
  patternType: string
) => {
  try {
    const response = await axiosInstance.put(
      `/accounts/v1/organization/patterns/update-status`,
      null,
      {
        params: {
          patternId,
          patternType,
        },
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const deleteExpense = (expenseId: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`/expenses/v1/${expenseId}`);
};

export const updateExpense = (
  expenseId: string,
  data: any
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/expenses/v1/${expenseId}`, data);
};
export const postLoan = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/finance/v1/loans`, data);
};
export const postCompanyProfile = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/accounts/v1/organization`, data);
};

export const uploadBulkPayslip = (data: FormData): Promise<AxiosResponse> => {
  return axiosInstance.post(`/finance/v1/payslips`, data, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
export const getAllLoans = (queryString = ''): Promise<AxiosResponse> => {
  return axiosInstance.get(`/finance/v1/loans${queryString}`);
};

export const getLoans = (
  userId: string,
  hasViewAll: boolean = false,
  queryString = ''
): Promise<AxiosResponse> => {
  if (hasViewAll) {
    return axiosInstance.get(`/finance/v1/loans${queryString}`);
  } else {
    return axiosInstance.get(`/finance/v1/loans/${userId}`);
  }
};

export const statusChange = (
  Id: string,
  status: string,
  message?: string
): Promise<AxiosResponse> => {
  return axiosInstance.put(
    `/finance/v1/loans/${Id}/status?status=${status}&message=${message}`
  );
};

export const getHealthInsuranceDetails = (
  employeeId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/finance/v1/health-insurances/${employeeId}`);
};

export const updateHealthInsuranceDetails = (
  employeeId: string,
  data: any
): Promise<AxiosResponse> => {
  return axiosInstance.put(
    `/finance/v1/health-insurances/employee/${employeeId}`,
    data
  );
};

export const postHealthInsuranceDetails = (
  data: any
): Promise<AxiosResponse> => {
  return axiosInstance.post(`/finance/v1/health-insurances`, data);
};

export const postInventory = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/finance/v1/inventory`, data);
};

export const getInventory = (url?: string): Promise<AxiosResponse> => {
  return axiosInstance.get(url || `/finance/v1/inventory`);
};
export const putInventory = (id: string, data: any): Promise<AxiosResponse> => {
  return axiosInstance.put(`/finance/v1/inventory/${id}`, data);
};
export const deleteInventory = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`/finance/v1/inventory/${id}`);
};

export const downloadClientLogo = (
  fileId: string
): Promise<AxiosResponse<Blob>> => {
  return axiosInstance.get(`/projects/v1/files/download/${fileId}`, {
    responseType: 'blob',
  });
};
export const postClient = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/projects/v1/clients`, data);
};

export const getClient = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.get(`/projects/v1/clients/${id}`);
};

export const getAllClient = (
  pageNumber: number,
  pageSize: number
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/projects/v1/clients`, {
    params: {
      pageNumber,
      pageSize,
    },
  });
};

export const putClient = (
  id: string,
  data: FormData
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/projects/v1/clients/${id}`, data);
};

export const putProject = (
  projectId: string,
  data: ProjectFormData
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/projects/v1/projects/${projectId}`, data, {
    headers: { 'Content-Type': 'application/json' },
  });
};

export const postProjects = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/projects/v1/projects`, data, {
    headers: { 'Content-Type': 'application/json' },
  });
};

export const postContracts = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/projects/v1/contracts`, data);
};

export const getProjectDetails = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.get(`/projects/v1/projects/client/${id}`);
};

export const getContractDetails = (
  contractId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/projects/v1/contracts/${contractId}`);
};

export const getResourceManager = (): Promise<AxiosResponse<Employee[]>> => {
  return axiosInstance.get('/accounts/v1/users/names');
};

export const getAllProjects = (
  pageNumber: number,
  pageSize: number,
  projectId?: string,
  status?: string
): Promise<AxiosResponse<any>> => {
  return axiosInstance.get('/projects/v1/projects/all-projects', {
    params: {
      pageNumber,
      pageSize,
      projectId,
      status,
    },
  });
};

export const getProject = (
  projectId: string,
  clientId: string
): Promise<AxiosResponse<ProjectEntity[]>> => {
  return axiosInstance.get(`/projects/v1/projects/${projectId}/${clientId}`);
};

export const getAllContracts = (
  pageNumber: number,
  pageSize: number
): Promise<AxiosResponse<any>> => {
  return axiosInstance.get(`/projects/v1/contracts`, {
    params: {
      pageNumber,
      pageSize,
    },
  });
};

export const getProjectsByClientId = (
  clientId: string
): Promise<AxiosResponse<ProjectEntity[]>> => {
  return axiosInstance.get(`/projects/v1/projects/client/${clientId}`);
};

export const getProjectEmployees = (projectId: string) => {
  return axiosInstance.get(`/projects/v1/projects/${projectId}/employees`);
};

export const getContractsByClientId = (
  clientId: string
): Promise<AxiosResponse<ContractDetails[]>> => {
  return axiosInstance.get(`/projects/v1/contracts/client/${clientId}`);
};

export const updateProjectStatus = (
  projectId: string,
  newStatus: ProjectStatus
): Promise<AxiosResponse<ProjectEntity>> => {
  return axiosInstance.patch(
    `/projects/v1/projects/${projectId}/status`,
    JSON.stringify(newStatus),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );
};

export const getResourcesByClientId = (
  clientId: string
): Promise<AxiosResponse<ContractDetails[]>> => {
  return axiosInstance.get(`/projects/v1/contracts/${clientId}/resources`);
};

export const updateContractStatus = (
  contractId: string,
  newStatus: ProjectStatus
): Promise<AxiosResponse<ProjectEntity>> => {
  return axiosInstance.patch(
    `/projects/v1/contracts/${contractId}/status`,
    JSON.stringify(newStatus),
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );
};

export const updateContract = (
  contractId: string,
  data: any
): Promise<AxiosResponse<any>> => {
  return axiosInstance.put(`/projects/v1/contracts/${contractId}`, data);
};
export const getAllRolesInOrganization = (): Promise<AxiosResponse> => {
  return axiosInstance.get('/accounts/v1/roles');
};

export const getOrganizationById = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.get(`/accounts/v1/organizations/${id}`);
};

export const updateOrganizationById = (
  id: string,
  data: any
): Promise<AxiosResponse> => {
  return axiosInstance.patch(`/accounts/v1/organizations/${id}`, data);
};

export const downloadOrgFile = (): Promise<AxiosResponse<Blob>> => {
  return axiosInstance.get('accounts/v1/organizations/logo', {
    responseType: 'blob',
  });
};

export const getFeatureToggles = (): Promise<AxiosResponse> => {
  return axiosInstance.get('/accounts/v1/features');
};

export const updateFeatureTogglesByOrgId = (
  organizationId: string,
  data: IFeatureToggle
): Promise<AxiosResponse> => {
  return axiosInstance.put(`/accounts/v1/features/${organizationId}`, data);
};

export const postRole = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post(`/accounts/v1/roles`, data);
};

export const putRole = (id: string, data: any): Promise<AxiosResponse> => {
  return axiosInstance.put(`/accounts/v1/roles/${id}`, data);
};

export const deleteRole = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`/accounts/v1/roles/${id}`);
};

export const getAllApplicantList = (
  queryString = ''
): Promise<AxiosResponse> => {
  return axiosInstance.get(
    `/recruitments/v1/applicants/combinedApplicants${queryString}`
  );
};

export const postApplicant = (data: FormData): Promise<AxiosResponse> => {
  return axiosInstance.post('/recruitments/v1/applicants', data);
};

export const downloadApplicantResume = (
  fileId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/recruitments/v1/applicants/resume/${fileId}`, {
    responseType: 'blob',
  });
};

export const changeApplicationStatus = (
  applicantId: string,
  status: string
): Promise<AxiosResponse> => {
  return axiosInstance.put(
    `/recruitments/v1/applicants/${applicantId}/status/${status}`
  );
};
export const referApplicant = (data: FormData): Promise<AxiosResponse> => {
  return axiosInstance.post('/recruitments/v1/referrals', data);
};

export const getMyReferrals = (queryString = ''): Promise<AxiosResponse> => {
  return axiosInstance.get(`/recruitments/v1/referrals${queryString}`);
};

export const downloadReferralResume = (
  fileId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/recruitments/v1/referrals/${fileId}`, {
    responseType: 'blob',
  });
};

export const postComment = (data: any) => {
  return axiosInstance.post('/recruitments/v1/applicants/comments', data);
};

export const assignInterviewer = (
  applicantId: string,
  interviewer: IAssignedInterviewer
): Promise<AxiosResponse> => {
  return axiosInstance.put(
    `/recruitments/v1/applicants/${applicantId}/assign-interviewer`,
    interviewer
  );
};

export const getApplicantById = (
  applicantId: string
): Promise<AxiosResponse> => {
  return axiosInstance.get(`/recruitments/v1/applicants/${applicantId}`);
};

export const updateKycDetails = (
  employeeId: string,
  data?: any
): Promise<AxiosResponse> => {
  return axiosInstance.patch(`/employees/v1/users/${employeeId}/kyc`, data);
};

export const getOrganizationValuesByKey = (
  key: string
): Promise<AxiosResponse<OrganizationValues>> => {
  return axiosInstance.get(`/accounts/v1/organizations/values/${key}`);
};

export const updateOrganizationValues = (
  orgDefaults: OrganizationValues
): Promise<AxiosResponse<OrganizationValues>> => {
  return axiosInstance.put(
    '/accounts/v1/organizations/update-values',
    orgDefaults
  );
};
export const PostLogHours = (data: any): Promise<AxiosResponse> => {
  return axiosInstance.post('projects/api/timesheets', data, {
    headers: { 'Content-Type': 'application/json' },
  });
};

export const fetchMonthLogs = (date: any): Promise<AxiosResponse> => {
  return axiosInstance.get(`projects/api/timesheets?month=${date}`);
};

export const deleteLog = (id: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`projects/api/timesheets/${id}`);
};

export const updateLog = (id: string, data: any): Promise<AxiosResponse> => {
  return axiosInstance.put(`projects/api/timesheets/${id}`, data);
};

export const generateInvoiceIdentifiers = (
  contractId: string
): Promise<AxiosResponse<InvoiceIdentifiers>> => {
  return axiosInstance.post(
    `/projects/v1/invoices/generate-identifiers/${contractId}`
  );
};

export const fetchContractById = (contractId: string) => {
  return axiosInstance.get(`/projects/v1/contracts/${contractId}`);
};

export const fetchClientById = (clientId: string) => {
  return axiosInstance.get(`/projects/v1/clients/${clientId}`);
};

export const fetchProjectByIdAndClientId = async (
  projectId: string,
  clientId: string
): Promise<Project> => {
  const response = await axiosInstance.get(
    `/projects/v1/projects/${projectId}/${clientId}`
  );
  return response.data;
};

export const createInvoice = (invoiceRequest: {
  contractId: string;
  [key: string]: any;
}) => {
  return axiosInstance.post('/projects/v1/invoices', invoiceRequest);
};

export const downloadContractFile = (
  fileId: string
): Promise<AxiosResponse<Blob>> => {
  return axiosInstance.get(`/projects/v1/files/download/${fileId}`, {
    responseType: 'blob',
  });
};

export const deleteContractFile = (fileId: string): Promise<AxiosResponse> => {
  return axiosInstance.delete(`/projects/v1/files/${fileId}`);
};

export const getInvoicesBycontractId = (contractId: string) => {
  return axiosInstance.get(`/projects/v1/invoices/contract/${contractId}`);
};
