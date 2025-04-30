const roleData = [
  // {
  //   heading: 'Employees',
  //   labels: [
  //     'View',
  //     'Create',
  //     'Edit',
  //   ],
  //   subsections: [
  //     {
  //       moduleName: 'Manage Employees',
  //       permissions: [
  //         { value: 'REMP', label: 'View', default: 'true' },
  //         { value: 'CEMP', label: 'Create' },
  //         { value: 'UEMP', label: 'Edit' },
  //       ],
  //     },
  //     // Add more subsections as needed
  //   ],
  // },
  {
    heading: 'EMPLOYEE_PROFILE',
    labels: [
      'FULL_ACCESS',
      'CREATE',
      'EDIT_ALL_EMPLOYEES',
      'INACTIVE',
      'ROLE_CHANGE',
      'READ_ALL_EMPLOYEES',
      'READ_FULL_INFO',
    ],
    subsections: [
      {
        moduleName: 'BASIC_PERSONAL_INFO',
        permissions: [
          { value: 'CEMP', label: 'CREATE' },
          { value: 'UALEMP', label: 'EDIT_ALL_EMPLOYEES' },
          { value: 'IEM', label: 'INACTIVE' },
          { value: 'URAP', label: 'UPDATE_ROLES' },
          { value: 'GALEMP', label: 'READ_ALL_EMPLOYEES' },
          { value: 'RCEMP', label: 'READ_FULL_INFO' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'KYC',
    labels: [
      'FULL_ACCESS',
      'SELF_READ',
      'READ',
      'SELF_EDIT',
      'EDIT',
    ],
    subsections: [
      {
        moduleName: 'MANAGE_KYC',
        permissions: [
          { value: 'RKYC', label: 'SELF_READ' },
          { value: 'RALKYC', label: 'READ' },
          { value: 'UKYC', label: 'SELF_EDIT' },
          { value: 'UALKYC', label: 'EDIT' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'DOCUMENTS',
    labels: [
      'FULL_ACCESS',
      'VIEW',
      'UPLOAD',
      'DELETE',
      'UPDATE',
    ],
    subsections: [
      {
        moduleName: 'MANAGE_OWN_DOCUMENTS',
        permissions: [
          { value: 'RDM', label: 'VIEW', default: 'true' },
          { value: 'CDM', label: 'UPLOAD' },
          { value: 'DDM', label: 'DELETE' },
          { value: 'UDM', label: 'UPDATE' },
        ],
      },
      {
        moduleName: 'MANAGE_EMPLOYEE_DOCUMENTS',
        permissions: [
          { value: 'RALDOC', label: 'VIEW' },
          { value: 'CALDOC', label: 'UPLOAD' },
          { value: 'DALDOC', label: 'DELETE' },
          { value: 'UALDOC', label: 'UPDATE' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'ROLES_PERMISSIONS',
    labels: [
      'FULL_ACCESS',
      'CREATE',
      'EDIT',
      'DELETE',
    ],
    subsections: [
      {
        moduleName: 'MANAGE_ROLES',
        permissions: [
          { value: 'CRORG', label: 'CREATE' },
          { value: 'URORG', label: 'EDIT' },
          { value: 'DRORG', label: 'DELETE' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'ORGANIZATION_PREFERENCE',
    labels: ['FULL_ACCESS', 'READ', 'UPDATE'],
    subsections: [
      {
        moduleName: 'MANAGE_ORGANIZATION',
        permissions: [
          { value: 'RORG', label: 'READ' },
          { value: 'UORG', label: 'UPDATE' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'ACCOUNTS',
    labels: [
      'FULL_ACCESS',
      'VIEW',
      'CREATE',
      'EXPORT',
      'EDIT',
      'DELETE',
    ],
    subsections: [
      {
        moduleName: 'BULK_PAYSLIPS',
        permissions: [
          { value: '', label: 'VIEW' },
          { value: 'CBPS', label: 'CREATE' },
          { value: '', label: 'EXPORT' },
          { value: '', label: 'EDIT' },
          { value: '', label: 'DELETE' },
        ],
      },
      {
        moduleName: 'EXPENSE_MANAGEMENT',
        permissions: [
          { value: 'REX', label: 'VIEW' },
          { value: 'CEX', label: 'CREATE' },
          { value: '', label: 'EXPORT' },
          { value: 'UEX', label: 'EDIT' },
          { value: 'DEX', label: 'DELETE' },
        ],
      },
      {
        moduleName: 'INVENTORY_MANAGEMENT',
        permissions: [
          { value: 'RDEV', label: 'VIEW' },
          { value: 'CDEV', label: 'CREATE' },
          { value: '', label: 'EXPORT' },
          { value: 'UDEV', label: 'EDIT' },
          { value: 'DDEV', label: 'DELETE' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'LOANS',
    labels: [
      'FULL_ACCESS',
      'VIEW',
      'VIEW_ALL_LOANS',
      'CREATE',
      'EDIT',
      'APPROVE',
    ],
    subsections: [
      {
        moduleName: 'LOANS',
        permissions: [
          { value: 'RLON', label: 'VIEW' },
          { value: 'GALON', label: 'VIEW_ALL_LOANS' },
          { value: 'CLON', label: 'CREATE' },
          { value: 'ULON', label: 'EDIT' },
          { value: 'SCLON', label: 'APPROVE' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'HEALTH_INSURANCE',
    labels: [
      'FULL_ACCESS',
      'VIEW',
      'CREATE',
      'EDIT',
      'DELETE'
    ],
    subsections: [
      {
        moduleName: 'MANAGE_HEALTH_INSURANCE',
        permissions: [
          { value: 'RHIN', label: 'VIEW', default: 'true' },
          { value: 'CHI', label: 'CREATE' },
          { value: 'UHI', label: 'EDIT' },
          { value: 'DHI', label: 'DELETE' }
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'RECRUITMENT_MANAGEMENT',
    labels: [
      'FULL_ACCESS',
      'CREATE_APPLICANT',
      'READ_APPLICANTS',
      'READ_ALL_APPLICANTS',
      'DOWNLOAD_RESUME',
      'UPDATE_ENTIRE_APPLICANT',
      'UPDATE_APPLICANT',
      'TAKE_INTERVIEW',
      'DELETE_INTERVIEW',
      'ACCESS_REFERRALS'
    ],
    subsections: [
      {
        moduleName: 'RECRUITMENT_MANAGEMENT',
        permissions: [
          { value: 'CRA', label: 'CREATE_APPLICANT' },
        { value: 'RRA', label: 'READ_APPLICANTS' },
        { value: 'RALRA', label: 'READ_ALL_APPLICANTS' },
        { value: 'DRA', label: 'DOWNLOAD_RESUME' },
        { value: 'URRA', label: 'UPDATE_ENTIRE_APPLICANT' },
        { value: 'URA', label: 'UPDATE_APPLICANT' },
        { value: 'TIA', label: 'TAKE_INTERVIEW' },
        { value: 'DIA', label: 'DELETE_INTERVIEW' },
        { value: 'AR', label: 'ACCESS_REFERRALS' }
        ],
      },
    ],
  },
  {
    heading: 'PROFILE_PICTURE',
    labels: [
      'FULL_ACCESS',
      'EDIT_PROFILE_PICTURE',
      'EDIT_OWN',
      'EDIT_OTHERS'
    ],
    subsections: [
      {
        moduleName: 'PROFILE_PICTURE',
        permissions: [
          { value: 'UPPS', label: 'EDIT_OWN' },
          { value: 'UPPA', label: 'EDIT_OTHERS' },
        ],
      },
      // Add more subsections as needed
    ],
  },
];

export default roleData;
