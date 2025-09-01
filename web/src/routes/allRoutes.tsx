import { Navigate, Route, Routes } from 'react-router-dom';
import MyProfileScreen from '../screens/MyProfileScreen.screen';
import EmployeeList from '../screens/EmployeeList.screen';
import BulkPayslip from '../screens/BulkPayslipScreen.screen';
import Error404Screen from '../screens/Error404Screen.screen';
import ExpenseManagement from '../screens/ExpenseManagement.screen';
import ClientManagement from '../screens/ClientManagement.screen';
import { useUser } from '../context/UserContext';
import LoanManagementScreen from '../screens/LoanManagementScreen.screen';
import {
  BULK_PAYSLIP_MODULE,
  EXPENSE_MODULE,
  FEATURE_TOGGLES_MODULE,
  INVENTORY_MODULE,
  LOAN_MODULE,
  ORGANIZATION_MODULE,
} from '../constants/PermissionConstants';
import ServiceUnavailable from '../screens/ServiceUnavailable.screen';
import InventoryManagement from '../screens/InventoryManagement.screen';
import { hasPermission } from '../utils/permissionCheck';
import OrganizationSettings from '../screens/OrganizationSettings.screen';
import { useFeatureToggles } from '../context/FeatureToggleContext';
import { hasFeature } from '../utils/featureCheck';
import { EFeatureToggles } from '../entities/FeatureToggle';
import FeatureToggleScreen from '../screens/FeatureToggleScreen.screen';
import RecruitmentManagementScreen from '../screens/RecruitmentManagementScreen.screen';
import AddNewApplicant from '../components/directComponents/AddNewApplicant.component';
import EditApplicant from '../components/directComponents/EditApplicant.component';
import ReferEmployeeScreen from '../screens/ReferEmployeeScreen.screen';
import TimeSheet from '../screens/TimeSheet';
import ClientDetailsScreen from '../screens/ClientDetailsScreen.screen';
import ClientListWrapper from '../screens/ClientListWrapper';
import ProjectManagement from '../screens/ProjectManagement.screen';
import ContractManagement from '../screens/ContractManagement.screen';
import ProjectListWrapper from '../screens/ProjectListWrapper';
import ProjectDetailsSCreen from '../screens/ProjectDetailsScreen.screen';
import ContractDetailsScreen from '../screens/ContractDetailedScreen';
import ContractListWrapper from '../screens/ContractListWrapper';

const AllRoutes = () => {
  return (
    <Routes>
      {/* FIXME - Update "/" route when Dashboard is ready */}
      <Route path="/" element={<MyProfileScreen />} />
      <Route path="/profile" element={<MyProfileScreen />} />
      <Route index path="/profile/me" element={<MyProfileScreen />} />
      <Route path="/employees" element={<EmployeeList />} />
      <Route
        path="/settings"
        element={
          <CustomRoute
            permission={ORGANIZATION_MODULE.READ_ORGANIZATIONS}
            featureToggle={EFeatureToggles.ORGANIZATION_SETTINGS}
          >
            {<OrganizationSettings />}
          </CustomRoute>
        }
      />
      <Route
        path="/accounts/bulk-payslip"
        element={
          <CustomRoute
            permission={BULK_PAYSLIP_MODULE.CREATE_BULK_PAYSLIP}
            featureToggle={EFeatureToggles.BULK_PAY_SLIPS}
          >
            {<BulkPayslip />}
          </CustomRoute>
        }
      />
      <Route
        path="/accounts/expenses"
        element={
          <CustomRoute
            permission={EXPENSE_MODULE.READ_EXPENSE}
            featureToggle={EFeatureToggles.EXPENSE_MANAGEMENT}
          >
            {<ExpenseManagement />}
          </CustomRoute>
        }
      />
      <Route
        path="/accounts/inventory"
        element={
          <CustomRoute
            permission={INVENTORY_MODULE.READ_DEVICE}
            featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
          >
            {<InventoryManagement />}
          </CustomRoute>
        }
      />

      <Route path="/clients/client-management" element={<ClientManagement />}>
        <Route
          index
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ClientListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":id"
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ClientDetailsScreen />
            </CustomRoute>
          }
        />
      </Route>

      <Route
        path="/projects/project-management"
        element={<ProjectManagement />}
      >
        <Route
          index
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ProjectListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":projectId/:clientId"
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ProjectDetailsSCreen />
            </CustomRoute>
          }
        />
      </Route>

      <Route
        path="/contracts/contract-management"
        element={<ContractManagement />}
      >
        <Route
          index
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ContractListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":id"
          element={
            <CustomRoute
              permission={INVENTORY_MODULE.READ_DEVICE}
              featureToggle={EFeatureToggles.INVENTORY_MANAGEMENT}
            >
              <ContractDetailsScreen />
            </CustomRoute>
          }
        />
      </Route>

      <Route
        path="/payroll/deductions-loans"
        element={
          <CustomRoute
            permission={LOAN_MODULE.READ_LOAN}
            featureToggle={EFeatureToggles.LOAN_MANAGEMENT}
          >
            <LoanManagementScreen />
          </CustomRoute>
        }
      />
      <Route
        path="/timeoff/timesheet"
        element={
          <CustomRoute
            permission={LOAN_MODULE.READ_LOAN}
            featureToggle={EFeatureToggles.LOAN_MANAGEMENT}
          >
            <TimeSheet />
          </CustomRoute>
        }
      />

      <Route
        path="/features"
        element={
          <CustomRoute
            permission={FEATURE_TOGGLES_MODULE.UPDATE_FEATURE}
            featureToggle={EFeatureToggles.EMPLOYEES_MANAGEMENT}
          >
            <FeatureToggleScreen />
          </CustomRoute>
        }
      />
      <Route
        path="/recruitment/hiring-management"
        element={<RecruitmentManagementScreen isReferral={false} />}
      />
      <Route
        path="/recruitment/hiring-management/new"
        element={<AddNewApplicant isReferScreen={false} />}
      />
      <Route
        path="/recruitment/hiring-management/:id"
        element={<EditApplicant />}
      />
      <Route
        path="/recruitment/my-referrals/refer"
        element={<ReferEmployeeScreen />}
      />
      <Route
        path="/recruitment/my-referrals"
        element={<RecruitmentManagementScreen isReferral={true} />}
      />
      <Route path="/notfound" element={<Error404Screen />} />
      <Route path="/service-unavailable" element={<ServiceUnavailable />} />
      <Route path="*" element={<Navigate to="/notfound" />} />
    </Routes>
  );
};

export default AllRoutes;

function CustomRoute({
  children,
  permission,
  featureToggle,
}: {
  children: React.ReactNode;
  permission: string;
  featureToggle?: string;
}) {
  const { user } = useUser();
  const { featureToggles } = useFeatureToggles();

  const isFeatureEnabled =
    featureToggle && featureToggles
      ? hasFeature(featureToggles.featureToggles, featureToggle)
      : false;
  if (user && hasPermission(user, permission) && isFeatureEnabled) {
    return children;
  } else {
    return <Navigate to="/notfound" />;
  }
}
