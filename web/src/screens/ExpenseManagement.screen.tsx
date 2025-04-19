import { useEffect, useState } from 'react';
import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';
import { ExpenseList } from './ExpenseList.screen';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import AddExpenseForm from '../components/directComponents/AddExpenseForm.component';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import { useNavigate } from 'react-router-dom';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';
import { useUser } from '../context/UserContext';
import { EXPENSE_MODULE } from '../constants/PermissionConstants';
import useKeyPress from '../service/keyboardShortcuts/onKeyPress';
import { hasPermission } from '../utils/permissionCheck';
import { toast } from 'sonner';
import { useTranslation } from 'react-i18next';
import { getOrganizationValuesByKey } from '../service/axiosInstance';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import { OrganizationDefaultValuesDetails } from '../context/OrganizationDefaultValuesContext';

const ExpenseManagement = () => {
  const navigate = useNavigate();
  const { user } = useUser();
  const { t } = useTranslation();

  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const handleIsCreateModalOpen = () => {
    setIsCreateModalOpen(!isCreateModalOpen);
  };

  useKeyPress(78, () => {
    user &&
      hasPermission(user, EXPENSE_MODULE.CREATE_EXPENSE) &&
      setIsCreateModalOpen(true);
  });
  const [key, setKey] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  const forceRerender = () => {
    toast.success('Expense added successfully');
    setKey((prevKey) => prevKey + 1);
  };
  const {expenseCategories, updateExpenseCategories} = OrganizationDefaultValuesDetails();
  const {expenseTypes, updateExpenseTypes} = OrganizationDefaultValuesDetails();
  const {expenseDepartments, updateExpenseDepartments} = OrganizationDefaultValuesDetails();
  const {expensePaymentModes, updateExpensePaymentModes} = OrganizationDefaultValuesDetails();

  const fetchOrganizationValues = async () => {
    setIsLoading(true);

    const expenseCategories = await getOrganizationValuesByKey('expenseCategories');
    const expenseTypes = await getOrganizationValuesByKey('expenseTypes');
    const expenseDepartments = await getOrganizationValuesByKey('departments');
    const expensePaymentModes = await getOrganizationValuesByKey('paymentModes');

    updateExpenseCategories(expenseCategories.data);
    updateExpenseTypes(expenseTypes.data);
    updateExpenseDepartments(expenseDepartments.data);
    updateExpensePaymentModes(expensePaymentModes.data);
    setIsLoading(false);
    if (expenseDepartments.status === 204) {
      toast.error('Please add departments in organization values (Settings) to add expenses');
    }
    if (expenseCategories.status === 204) {
      toast.error('Please add expense categories in organization values (Settings) to add expenses');
    }
    if (expenseTypes.status === 204) {
      toast.error('Please add expense types in organization values (Settings) to add expenses');
    }
    if (expensePaymentModes.status === 204) {
      toast.error('Please add expense payment modes in organization (Settings) values to add expenses');
    }
  };
  useEffect(() => {
    fetchOrganizationValues();
  }, []);
  return (
    <>
      {isLoading ? <SpinAnimation /> : <>
        <ExpenseManagementMainContainer>
          <ExpenseHeadingSection>
            <span className="heading">
              <span onClick={goToPreviousPage}>
                <ArrowDownSVG />
              </span>
              {t('EXPENSE_MANAGEMENT')}
            </span>
            {user && hasPermission(user, EXPENSE_MODULE.CREATE_EXPENSE) && (
              <Button
                className="submit shadow"
                onClick={() => expenseCategories && expenseDepartments && expensePaymentModes && expenseTypes && setIsCreateModalOpen(true)}
                title={expenseCategories && expenseDepartments && expensePaymentModes && expenseTypes ? '' : 'Please add organization values in settings to add expenses'}
                style={{ cursor: expenseCategories && expenseDepartments && expensePaymentModes && expenseTypes ? 'pointer' : 'not-allowed', backgroundColor: expenseCategories && expenseDepartments && expensePaymentModes && expenseTypes ? '' : '#d2d2d2' }}
                width="216px"
              >
                <AddNewPlusSVG />
                {t('ADD_NEW_EXPENSE')}
              </Button>
            )}
          </ExpenseHeadingSection>
          <ExpenseList
            key={key}
            expenseCategories={expenseCategories}
            expenseTypes={expenseTypes}
            expenseDepartments={expenseDepartments}
            expensePaymentModes={expensePaymentModes}
          />
        </ExpenseManagementMainContainer>

        {isCreateModalOpen && (
          <CenterModalMain
            heading="ADD_NEW_EXPENSE"
            modalClose={handleIsCreateModalOpen}
            actualContentContainer={
              <AddExpenseForm
                handleClose={handleIsCreateModalOpen}
                handleLoadExpenses={forceRerender}
                mode="create"
                expenseCategories={expenseCategories}
                expenseTypes={expenseTypes}
                expenseDepartments={expenseDepartments}
                expensePaymentModes={expensePaymentModes}
              />
            }
          />
        )}
      </>}
    </>
  );
};

export default ExpenseManagement;
