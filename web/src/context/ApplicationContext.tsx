import React, { ReactNode, createContext, useCallback, useState } from 'react';
import { EmployeeEntity } from '../entities/EmployeeEntity';
import { Loan } from '../entities/LoanEntity';
import { IOrganization } from '../entities/OrganizationEntity';

interface ContextAddress {
  addressOne: string;
  addressTwo?: string;
  city: string;
  state: string;
  pinCode: number;
  country: string;
}

interface IMinimalOrganizationDetails {
  taxId?: string;
  name?: string;
  address?: ContextAddress;
  bankDetails?: {
    accountName?: string;
    bankName?: string;
    accountNumber?: string;
    ifscNumber?: string;
  };
  currencyType?: string;
}
interface AppContextType {
  employeeList: EmployeeEntity[] | null | undefined;
  updateEmployeeList: (
    employeeList: EmployeeEntity[] | null | undefined
  ) => void;
  loansList: Loan[] | null | undefined;
  updateLoansList: (loansList: Loan[] | null | undefined) => void;

  organizationDetails: IMinimalOrganizationDetails | null | undefined;
  setOrganizationData: (orgData: IOrganization | null | undefined) => void;
}

export const ApplicationContext = createContext<AppContextType>({
  employeeList: null,
  updateEmployeeList: () => {},
  loansList: null,
  updateLoansList: () => {},
  organizationDetails: null,
  setOrganizationData: () => {},
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
    []
  );
  const [loansList, setLoanList] = useState<Loan[] | null | undefined>(null);

  const updateLoansList = (loanList: Loan[] | null | undefined) => {
    setLoanList(loanList);
  };
  const [organizationDetails, setOrganizationDetails] = useState<
    IMinimalOrganizationDetails | null | undefined
  >(null);

  const setOrganizationData = useCallback(
    (orgData: IOrganization | null | undefined) => {
      if (orgData) {
        const contextAddress: ContextAddress | undefined = orgData.address
          ? {
              addressOne: orgData.address.addressOne || '',
              addressTwo: orgData.address.addressTwo,
              city: orgData.address.city || '',
              state: orgData.address.state || '',
              pinCode: orgData.address.pinCode || 0,
              country: orgData.address.country || '',
            }
          : undefined;

        setOrganizationDetails({
          taxId: orgData.accounts?.taxId,
          currencyType: orgData.preferences?.currencyType,
          name: orgData.name,
          address: contextAddress,
          bankDetails: orgData.bankDetails
            ? {
                accountName: orgData.bankDetails.accountName || '',
                bankName: orgData.bankDetails.bankName || '',
                accountNumber: orgData.bankDetails.accountNumber || '',
                ifscNumber: orgData.bankDetails.ifscNumber || '',
              }
            : undefined,
        });
      } else {
        setOrganizationDetails(null);
      }
    },
    []
  );

  return (
    <ApplicationContext.Provider
      value={{
        employeeList,
        updateEmployeeList,
        loansList,
        updateLoansList,
        organizationDetails,
        setOrganizationData,
      }}
    >
      {children}
    </ApplicationContext.Provider>
  );
};
