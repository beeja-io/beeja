interface DepartmentOptions {
  departments: string[];
  categories: string[];
  types: string[];
  jobTitles: string[];
  status: string[];
  employmentTypes: string[];
  employeeDepartments: string[];
}

export const departmentOptions: DepartmentOptions = {
  departments: [
    'Admin',
    'Learning & Development',
    'Accounts & HR',
    'R & D',
    'Beeja',
    'F & M',
    'CST',
    'SCM',
  ],
  categories: [
    'Stationary & Supplies',
    'Utilities',
    'Food & Refreshments',
    'Events',
    'Fitness',
    'L & D',
    'Subscriptions',
    'Rent',
    'Transportation',
    'Office Setup',
    'Office Equipment',
    'F & M',
  ],
  types: [
    'Stationary Items',
    'Electricity',
    'Internet',
    'AC',
    'Lunch',
    'Snacks',
    'Events',
    'Gym',
    'Sports',
    'Paid Internship Allowances',
    'Unpaid Internship Allowances',
    'Apprenticeship Allowances',
    'Purchase Course',
    'Google Cloud',
    'Google Work Space',
    'Miro',
    'GCP',
    'Linkedin',
    'Office 1 Rent',
    'Office 2 Rent',
    'Rent Allowances',
    'Transportation',
    'Purchase Office Furniture',
    'Purchase Equipment',
    'Kitchen & Dining',
    'Cleaning',
    'Repair & Maintenance',
  ],
  jobTitles: [
    'Software Engineer',
    'Product Manager',
    'Data Scientist',
    'Designer',
    'HR Specialist',
    'Finance Analyst',
  ],
  status: ['Active', 'Inactive'],
  employmentTypes: ['Full time', 'Part-Time', 'Contractor', 'Intern'],
  employeeDepartments: [
    'IT',
    'Admin',
    'Learning & Development',
    'Accounts & HR',
    'R & D',
  ],
};
