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
    heading: 'Employee Profile',
    labels: [
      'Full Access',
      'Create',
      'Edit All Employees',
      'Inactive',
      'Role Change',
      'Read All Employees',
      'Read Full Info',
    ],
    subsections: [
      {
        moduleName: 'Basic & Personal Info',
        permissions: [
          { value: 'CEMP', label: 'Create' },
          { value: 'UALEMP', label: 'Edit All Employees' },
          { value: 'IEM', label: 'Inactive' },
          { value: 'URAP', label: 'Update Roles' },
          { value: 'GALEMP', label: 'Read All Employees' },
          { value: 'RCEMP', label: 'Read Full Info' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'KYC',
    labels: ['Full Access', 'Self Read', 'Read', 'Self Edit', 'Edit'],
    subsections: [
      {
        moduleName: 'Manage KYC',
        permissions: [
          { value: 'RKYC', label: 'Self Read' },
          { value: 'RALKYC', label: 'Read' },
          { value: 'UKYC', label: 'Self Edit' },
          { value: 'UALKYC', label: 'Edit' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Documents',
    labels: ['Full Access', 'View', 'Upload', 'Delete', 'Update'],
    subsections: [
      {
        moduleName: 'Manage Own Documents',
        permissions: [
          { value: 'RDM', label: 'View', default: 'true' },
          { value: 'CDM', label: 'Upload' },
          { value: 'DDM', label: 'Delete' },
          { value: 'UDM', label: 'Update' },
        ],
      },
      {
        moduleName: 'Manage Employee Documents',
        permissions: [
          { value: 'RALDOC', label: 'View' },
          { value: 'CALDOC', label: 'Upload' },
          { value: 'DALDOC', label: 'Delete' },
          { value: 'UALDOC', label: 'Update' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Roles & Permissions',
    labels: ['Full Access', 'Create', 'Edit', 'Delete'],
    subsections: [
      {
        moduleName: 'Manage Roles',
        permissions: [
          { value: 'CRORG', label: 'Create' },
          { value: 'URORG', label: 'Edit' },
          { value: 'DRORG', label: 'Delete' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Organization Preferences',
    labels: ['Full Access', 'Read', 'Update'],
    subsections: [
      {
        moduleName: 'Manage Organization',
        permissions: [
          { value: 'RORG', label: 'Read' },
          { value: 'UORG', label: 'Update' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Accounts',
    labels: ['Full Access', 'View', 'Create', 'Export', 'Edit', 'Delete'],
    subsections: [
      {
        moduleName: 'Bulk Payslips',
        permissions: [
          { value: '', label: 'View' },
          { value: 'CBPS', label: 'Create' },
          { value: '', label: 'Export' },
          { value: '', label: 'Edit' },
          { value: '', label: 'Delete' },
        ],
      },
      {
        moduleName: 'Expense Management',
        permissions: [
          { value: 'REX', label: 'View' },
          { value: 'CEX', label: 'Create' },
          { value: '', label: 'Export' },
          { value: 'UEX', label: 'Edit' },
          { value: 'DEX', label: 'Delete' },
        ],
      },
      {
        moduleName: 'Inventory Management',
        permissions: [
          { value: 'RDEV', label: 'View' },
          { value: 'CDEV', label: 'Create' },
          { value: '', label: 'Export' },
          { value: 'UDEV', label: 'Edit' },
          { value: 'DDEV', label: 'Delete' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Loans',
    labels: [
      'Full Access',
      'View',
      'View All Loans',
      'Create',
      'Edit',
      'Approve',
    ],
    subsections: [
      {
        moduleName: 'Loans',
        permissions: [
          { value: 'RLON', label: 'View' },
          { value: 'GALON', label: 'View All Loans' },
          { value: 'CLON', label: 'Create' },
          { value: 'ULON', label: 'Edit' },
          { value: 'SCLON', label: 'Approve' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Health Insurance',
    labels: ['Full Access', 'View', 'Create', 'Edit', 'Delete'],
    subsections: [
      {
        moduleName: 'Manage Health Insurance',
        permissions: [
          { value: 'RHIN', label: 'View', default: 'true' },
          { value: 'CHIN', label: 'Create' },
          { value: 'UHIN', label: 'Edit' },
          { value: 'DHIN', label: 'Delete' },
        ],
      },
      // Add more subsections as needed
    ],
  },
  {
    heading: 'Recruitment Management',
    labels: ['Full Access', 'Create Applicant', 'Read Applicants', 'Read All Applicants', 'Download Resume', 'Update Entire Applicant', 'Update Applicant', 'Take Interview', 'Delete Interview'],
    subsections: [
      {
        moduleName: 'Recruitment Management',
        permissions: [
          { value: 'CAPT', label: 'Create Applicant' },
          { value: 'GAAPT', label: 'Read Applicants' },
          { value: 'GENAPT', label: 'Read All Applicants' },
          { value: 'RRSM', label: 'Download Resume' },
          { value: 'UENTAP', label: 'Update Entire Applicant' },
          { value: 'UAPL', label: 'Update Applicant' },
          { value: 'TINT', label: 'Take Interview' },
          { value: 'DINT', label: 'Delete Interview' },
        ],
      },
    ],
  },
  {
    heading: 'Profile Picture',
    labels: ['Full Access', 'Edit Own', 'Edit Others'],
    subsections: [
      {
        moduleName: 'Edit Profile Picture',
        permissions: [
          { value: 'UPPS', label: 'Edit Own' },
          { value: 'UPPA', label: 'Edit Others' },
        ],
      },
      // Add more subsections as needed
    ],
  },
];

export default roleData;
