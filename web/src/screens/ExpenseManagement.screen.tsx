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
import { OrganizationValues } from '../entities/OrgValueEntity';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

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
  const [expenseCategories, setExpenseCategories] =
    useState<OrganizationValues>({} as OrganizationValues);
  const [expenseTypes, setExpenseTypes] = useState<OrganizationValues>(
    {} as OrganizationValues
  );
  const [expenseDepartments, setExpenseDepartments] =
    useState<OrganizationValues>({} as OrganizationValues);
  const [expensePaymentModes, setExpensePaymentModes] =
    useState<OrganizationValues>({} as OrganizationValues);
  const fetchOrganizationValues = async () => {
    setIsLoading(true);
    try {
    const expenseCategories =
      await getOrganizationValuesByKey('expenseCategories');
    const expenseTypes = await getOrganizationValuesByKey('expenseTypes');
    const expenseDepartments = await getOrganizationValuesByKey('departments');
    const expensePaymentModes = await getOrganizationValuesByKey(
      'paymentModes'
    );
    setExpenseCategories(expenseCategories.data);
    setExpenseTypes(expenseTypes.data);
    setExpenseDepartments(expenseDepartments.data);
    setExpensePaymentModes(expensePaymentModes.data);
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
  }
    catch (error) {
      setIsLoading(false);
      throw new Error('Error fetching expenses:' + error);
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
