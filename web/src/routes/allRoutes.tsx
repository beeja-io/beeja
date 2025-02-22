import { Navigate, Route, Routes } from 'react-router-dom';
// import DashboardScreen from '../screens/DashboardScreen.screen';
import MyProfileScreen from '../screens/MyProfileScreen.screen';
import EmployeeList from '../screens/EmployeeList.screen';
import BulkPayslip from '../screens/BulkPayslipScreen.screen';
import Error404Screen from '../screens/Error404Screen.screen';
import ExpenseManagement from '../screens/ExpenseManagement.screen';
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
        path="/features"
        element={
          <CustomRoute
            permission={FEATURE_TOGGLES_MODULE.UPDATE_FEATURE}
            featureToggle={EFeatureToggles.EMPLOYEE_MANAGEMENT}
          >
            <FeatureToggleScreen />
          </CustomRoute>
        }
      />
       <Route
        path="/recruitment"
        element={<RecruitmentManagementScreen isReferral={false} />}
      />
      <Route
        path="/recruitment/new"
        element={<AddNewApplicant isReferScreen={false} />}
      />
      <Route path="/recruitment/:id" element={<EditApplicant />} />
      <Route path="/recruuitment/refer" element={<ReferEmployeeScreen />} />
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
