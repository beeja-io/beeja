import { Expense } from "../ExpenseEntity";

export interface AllExpensesResponse {
  expenses: Expense[];
  metadata: {
    totalAmount: number;
    totalSize: number;
  };
}
