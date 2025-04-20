import React, { ReactNode, createContext, useCallback, useState } from "react";
import { EmployeeEntity } from "../entities/EmployeeEntity";
import { Loan } from "../entities/LoanEntity";

interface AppContextType {
  employeeList: EmployeeEntity[] | null | undefined;
  updateEmployeeList: (
    employeeList: EmployeeEntity[] | null | undefined,
  ) => void;
  loanList: Loan[] | null | undefined;
  updateLoanList: (loanList: Loan[] | null | undefined) => void;
}

export const ApplicationContext = createContext<AppContextType>({
  employeeList: null,
  updateEmployeeList: () => {},
  loanList: null,
  updateLoanList: () => {},
});

export const ApplicationContextProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [employeeList, setEmployeeList] = useState<
    EmployeeEntity[] | null | undefined
  >(null);

  const updateEmployeeList = useCallback(
    (employeeList: EmployeeEntity[] | null | undefined) => {
      setEmployeeList(employeeList);
    },
    [],
  );
  const [loanList, setLoanList] = useState<Loan[] | null | undefined>(null);

  const updateLoanList = (loanList: Loan[] | null | undefined) => {
    setLoanList(loanList);
  };

  return (
    <ApplicationContext.Provider
      value={{ employeeList, updateEmployeeList, loanList, updateLoanList }}
    >
      {children}
    </ApplicationContext.Provider>
  );
};
