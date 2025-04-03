import { useUser } from '../context/UserContext';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import { useEffect, useState } from 'react';
import { fetchEmployeeDetailsByEmployeeId } from '../service/axiosInstance';
import { useTranslation } from 'react-i18next';

const DashboardScreen = () => {
  const { user, isLoading } = useUser();
  const { t } = useTranslation();
  const [employeeData, setEmployeeData] = useState<EmployeeEntity | null>(null);

  const employeeId = user?.employeeId ? user?.employeeId : '';

  const getEmployeeData = async (employeeId: string): Promise<void> => {
    try {
      const response = await fetchEmployeeDetailsByEmployeeId(employeeId);
      setEmployeeData(response.data);
    } catch (error) {
      throw new Error('Error fetching employee data: ' + error);
    }
  };

  useEffect(() => {
    getEmployeeData(employeeId);
  }, [employeeId]);

  return (
    <div>
      <h2>{t("DASHBOARD")}</h2>
      {t("LOREM")}
      <br />
      <br />
      <h3>{t("SAMPLE_FROM_USERCONTEXT:")} {isLoading ? 'Loading' : user?.firstName}</h3>
      <b>{t("EMPLOYEE_ID.")} {user?.employeeId}</b>
      <br />
      <small>{employeeData?.account.organizations.email}</small>
    </div>
  );
};

export default DashboardScreen;
