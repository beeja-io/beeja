import React, { ReactNode, createContext, useState } from 'react';
import { Expense } from '../entities/ExpenseEntity';

interface ExpenseListContextType {
  expenseList: Expense[] | null | undefined;
  updateExpenseList: (expenseList: Expense[] | null | undefined) => void;
}

export const ExpenseListContext = createContext<ExpenseListContextType>({
  expenseList: null,
  updateExpenseList: () => {},
});

export const ApplicationContextProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [expenseList, setExpenseList] = useState<Expense[] | null | undefined>(
    null
  );

  const updateExpenseList = (expenseList: Expense[] | null | undefined) => {
    setExpenseList(expenseList);
  };

  return (
    <ExpenseListContext.Provider value={{ expenseList, updateExpenseList }}>
      {children}
    </ExpenseListContext.Provider>
  );
};
