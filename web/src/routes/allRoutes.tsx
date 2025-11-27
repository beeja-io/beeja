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
  CLIENT_MODULE,
  CONTRACT_MODULE,
  EXPENSE_MODULE,
  FEATURE_TOGGLES_MODULE,
  INVENTORY_MODULE,
  LOAN_MODULE,
  ORGANIZATION_MODULE,
  PERFORMANCE_MODULE,
  PROJECT_MODULE,
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
import CreateReviewCycleScreen from '../screens/CreateReviewCycle.screen';
import ReviewCyclesList from '../screens/ReviewCyclesList.screen';
import AssignFeedbackProviders from '../screens/AssignFeedbackReceiversProviders.screen';
import FeedbackHub from '../screens/FeedbackHub.screen';
import AddEvaluationCycle from '../components/directComponents/AddEvaluationCycle.component';
import EvaluationOverview from '../components/reusableComponents/EvaluationOverview.component';
import MyTeamOverview from '../screens/MyTeamOverview.screen';
import FeedbackReceiversList from '../screens/FeedbackReceiversList.screen';
import AddFeedbackReceivers from '../components/reusableComponents/AddFeedbackReceivers.component';
import ViewMoreDetails from '../screens/ViewMoreDetailsListScreen.screen';
import ProvideFeedback from '../screens/ProvideFeedback.screen';

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
              permission={CLIENT_MODULE.READ_CLIENT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
            >
              <ClientListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":id"
          element={
            <CustomRoute
              permission={CLIENT_MODULE.READ_CLIENT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
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
              permission={PROJECT_MODULE.READ_PROJECT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
            >
              <ProjectListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":projectId/:clientId"
          element={
            <CustomRoute
              permission={PROJECT_MODULE.READ_PROJECT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
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
              permission={CONTRACT_MODULE.READ_CONTRACT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
            >
              <ContractListWrapper />
            </CustomRoute>
          }
        />
        <Route
          path=":id"
          element={
            <CustomRoute
              permission={CONTRACT_MODULE.READ_CONTRACT}
              featureToggle={EFeatureToggles.PROJECT_CONTRACT_MANAGEMENT}
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
        path="/performance/create-evaluation-form"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_REVIEW_CYCLE}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <CreateReviewCycleScreen />
          </CustomRoute>
        }
      >
        <Route
          index
          element={
            <CustomRoute
              permission={PERFORMANCE_MODULE.READ_REVIEW_CYCLE}
              featureToggle={
                EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
              }
            >
              <ReviewCyclesList />
            </CustomRoute>
          }
        />
        <Route
          path="new"
          element={
            <CustomRoute
              permission={PERFORMANCE_MODULE.CREATE_REVIEW_CYCLE}
              featureToggle={
                EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
              }
            >
              <AddEvaluationCycle />
            </CustomRoute>
          }
        />
        <Route
          path=":id"
          element={
            <CustomRoute
              permission={PERFORMANCE_MODULE.UPDATE_REVIEW_CYCLE}
              featureToggle={
                EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
              }
            >
              <AddEvaluationCycle />
            </CustomRoute>
          }
        />
      </Route>

      <Route
        path="/performance/assign-feedback-providers"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_RECEIVER}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <AssignFeedbackProviders />
          </CustomRoute>
        }
      />

      <Route
        path="/performance/view-more-details"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_RESPONSE}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <ViewMoreDetails />
          </CustomRoute>
        }
      />

      <Route
        path="/performance/assign-feedback-providers/:cycleId/:questionnaireId/add-feedback-receiver"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.ASSIGN_RECEIVER}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <AddFeedbackReceivers />
          </CustomRoute>
        }
      />

      <Route
        path="/performance/assign-feedback-providers/:cycleId/:questionnaireId"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_RECEIVER}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <FeedbackReceiversList />
          </CustomRoute>
        }
      />

      <Route
        path="/performance/feedback-hub"
        element={
          <CustomRoute
            permission={`${PERFORMANCE_MODULE.READ_RESPONSE}||${PERFORMANCE_MODULE.SELF_EVALUATION}||${PERFORMANCE_MODULE.READ_OWN_RESPONSES}`}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <FeedbackHub />
          </CustomRoute>
        }
      />
      <Route
        path="/performance/feedback-hub/provide-feedback/:id"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.PROVIDE_FEEDBACK}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <ProvideFeedback />
          </CustomRoute>
        }
      />
      <Route
        path="/performance/my-team-overview"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_ALL_RESPONSES}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <MyTeamOverview />
          </CustomRoute>
        }
      />
      <Route
        path="/performance/my-team-overview/:employeeId"
        element={
          <CustomRoute
            permission={PERFORMANCE_MODULE.READ_ALL_RESPONSES}
            featureToggle={
              EFeatureToggles.PERFORMANCE_AND_EVALUATION_MANAGEMENT
            }
          >
            <EvaluationOverview />
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
  // if (user && hasPermission(user, permission) && isFeatureEnabled) {
  if (
    user &&
    permission.split('||').some((p) => hasPermission(user, p)) &&
    isFeatureEnabled
  ) {
    return children;
  } else {
    return <Navigate to="/notfound" />;
  }
}
