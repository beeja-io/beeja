import React, { ReactNode, createContext, useState ,useContext } from 'react';
import { OrganizationValues } from '../entities/OrgValueEntity';

interface OrganizationDefaultValuesContextType {
  deviceTypes: OrganizationValues ;
  updateDeviceTypes: (deviceTypes: OrganizationValues) => void;
  inventoryProviders: OrganizationValues ;
  updateInventoryProviders: (inventoryProviders: OrganizationValues) => void;
  expenseCategories: OrganizationValues ;
  updateExpenseCategories: (expenseCategories: OrganizationValues) => void;
  expenseTypes: OrganizationValues ;
  updateExpenseTypes: (inventoryProviders: OrganizationValues) => void;
  expenseDepartments: OrganizationValues ;
  updateExpenseDepartments: (inventoryProviders: OrganizationValues) => void;
  expensePaymentModes: OrganizationValues ;
  updateExpensePaymentModes: (inventoryProviders: OrganizationValues) => void;
}

export const OrganizationDefaultValuesContext = createContext<OrganizationDefaultValuesContextType>({
  deviceTypes: {} as OrganizationValues,
  updateDeviceTypes: () => {},
  inventoryProviders: {} as OrganizationValues,
  updateInventoryProviders: () => {},
  expenseCategories: {} as OrganizationValues,
  updateExpenseCategories: () => {},
  expenseTypes: {} as OrganizationValues,
  updateExpenseTypes: () => {},
  expenseDepartments: {} as OrganizationValues,
  updateExpenseDepartments: () => {},
  expensePaymentModes: {} as OrganizationValues,
  updateExpensePaymentModes: () => {}
});

export const OrganizationDefaultValuesProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
    const [deviceTypes, setDeviceTypes] = useState<OrganizationValues>({} as OrganizationValues);
  
    const updateDeviceTypes = (deviceTypes: OrganizationValues) => {
      setDeviceTypes(deviceTypes);
    };

    const [inventoryProviders, setInventoryProviders] = useState<OrganizationValues>({} as OrganizationValues);
  
    const updateInventoryProviders = (inventoryProviders: OrganizationValues) => {
      setInventoryProviders(inventoryProviders);
    };

    const [expenseCategories, setExpenseCategories] = useState<OrganizationValues>({} as OrganizationValues);

    const updateExpenseCategories = (expenseCategories: OrganizationValues) => {
      setExpenseCategories(expenseCategories);
    };
  
    const [expenseTypes, setExpenseTypes] = useState<OrganizationValues>({} as OrganizationValues);

    const updateExpenseTypes = (expenseTypes: OrganizationValues) => {
      setExpenseTypes(expenseTypes);
    };
  
    const [expenseDepartments, setExpenseDepartments] = useState<OrganizationValues>({} as OrganizationValues);

    const updateExpenseDepartments = (expenseDepartments: OrganizationValues) => {
      setExpenseDepartments(expenseDepartments);
    };

    const [expensePaymentModes, setExpensePaymentModes] = useState<OrganizationValues>({} as OrganizationValues);

    const updateExpensePaymentModes = (expensePaymentModes: OrganizationValues) => {
      setExpensePaymentModes(expensePaymentModes);
    };
  
    return (
      <OrganizationDefaultValuesContext.Provider value={{ deviceTypes, updateDeviceTypes, inventoryProviders, 
          updateInventoryProviders, expenseCategories , updateExpenseCategories, expenseTypes, updateExpenseTypes,
          expenseDepartments, updateExpenseDepartments, expensePaymentModes, updateExpensePaymentModes}}>
        {children}
      </OrganizationDefaultValuesContext.Provider>
    );
};

export const OrganizationDefaultValuesDetails = () => {
  const context = useContext(OrganizationDefaultValuesContext);
  if (!context) {
    throw new Error(
      'OrganizationDefaultValuesDetails must be used within a OrganizationDefaultValuesProvider'
    );
  }

  return context;
};