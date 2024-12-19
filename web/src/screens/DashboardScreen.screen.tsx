import { useUser } from '../context/UserContext';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import { useEffect, useState } from 'react';
import { fetchEmployeeDetailsByEmployeeId } from '../service/axiosInstance';

const DashboardScreen = () => {
  const { user, isLoading } = useUser();
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
      <h2>Dashboard</h2>
      Lorem ipsum, dolor sit amet consectetur adipisicing elit. Porro, ex
      facilis? Illo quasi nihil quia! Ipsum animi reprehenderit unde quas!
      Accusamus alias qui asperiores quaerat commodi ullam perferendis rerum
      praesentium!
      <br />
      <br />
      <h3>Sample from UseContext: {isLoading ? 'Loading' : user?.firstName}</h3>
      <b>Employee Id: {user?.employeeId}</b>
      <br />
      <small>{employeeData?.account.organizations.email}</small>
    </div>
  );
};

export default DashboardScreen;
