db = db.getSiblingDB("accounts_sbox");
db.createCollection("roles");

db.createCollection("organizations");
db.organizations.insertMany([
  {
   
    "name": "Test Org",
    "email": "admin@testorg.domain",
    "subscriptionId": "1234567890",
    "emailDomain": "tac3@example.com",
    "contactMail": "contacttac@example.com",
    "website": "https://duckduckgo.com",
    "preferences": {
      "dateFormat": "DD_DOT_MM_DOT_YYYY",
      "timeZone": "EASTERN_STANDARD_TIME",
      "fontName": "NUNITO",
      "fontSize": 14,
      "theme": "LIGHT",
      "currencyType": "EURO"
    },
    "address": {
      "addressOne": "",
      "country": "India",
      "state": "",
      "pinCode": "333333"
    },
    "filingAddress": "Test",
    "accounts": {
      "pfNumber": "",
      "tanNumber": "",
      "panNumber": "FBFPQ0110p",
      "esiNumber": "11111111111111111",
      "linNumber": "",
      "gstNumber": "22AAAAA0000A1Z5"
    },
    "logoFileId": "6638a00fd305585559235d87",
    "loanLimit": {
      "monitorLoan": 31,
      "isMonitorLoanEnabled": true,
      "personalLoan": 1222,
      "isPersonalLoanEnabled": true,
      "salaryMultiplier": 3,
      "isSalaryMultiplierEnabled": false
    }
  }
]);

const query = {name: "Test Org"};
const org = db.organizations.findOne(query);
db.roles.insertMany([{

                      "name": "Super Admin",
                      "description": "Admin",
                      "permissions": [
                        "REX",
                        "UEX",
                        "DEX",
                        "RDEV",
                        "DLON",
                        "DALDOC",
                        "DHIN",
                        "DDM",
                        "CDM",
                        "CDEV",
                        "ULON",
                        "UEMP",
                        "RORG",
                        "CHIN",
                        "GALON",
                        "CLON",
                        "RDM",
                        "CEMP",
                        "RALDOC",
                        "UDM",
                        "UHIN",
                        "CDMPS",
                        "CALDOC",
                        "UDEV",
                        "UORG",
                        "RLOG",
                        "GALEMP",
                        "CEM",
                        "SCLON",
                        "URAP",
                        "UALEMP",
                        "RDMH",
                        "DDEV",
                        "IEMM",
                        "RCEMP",
                        "CEX",
                        "REMP",
                        "UEM",
                        "RHIN",
                        "CBPS",
                        "RLON",
                        "URORG",
                        "DRORG",
                        "CRORG",
                        "CAPT",
                        "GAAPT",
                        "GENAPT",
                        "RRSM",
                        "UENTAP",
                        "UAPL",
                        "TINT",
                        "DINT",
                        "CORG",
                        "RALORG",
                        "DORG",
                        "UALORG\"",
                        "GACLT",
                        "RALFTG",
                        "UALFTG",
                        "URAP",
                        "UKYC",
                        "UALKYC",
                        "RKYC",
                        "RALKYC",
                        "GAPT",
                        "RPT",
                        "CCRT",
                        "CCLT",
                        "UCLT",
                        "ACLT",
                        "CPT",
                        "UPT",
                        "APT",
                        "GPRSC",
                        "GPRSRC",
                        "UCRT",
                        "ACRT",
                        "RCRT",
                        "GACRT",
                        "GCPRJT",
                        "ARCT",
                        "GCART",
                        "GRC",
                        "RC",
                        "RR",
                        "RU",
                        "RUA",
                        "UPPS"
                      ],
                      "_class": "com.beeja.api.accounts.model.Organization.Role",
                      "organizationId": ""+org._id
                    },
                    {

                      "name": "Employee",
                      "description": "Employee",
                      "permissions": [
                        "REX",
                        "RKYC",
                        "CPT",
                        "UKYC",
                        "UORG",
                        "CALDOC",
                        "DALDOC",
                        "UEX",
                        "REMP",
                        "IEM",
                        "RORG",
                        "UALKYC",
                        "CCLT",
                        "CEX",
                        "TINT",
                        "RALKYC",
                        "RALDOC",
                        "GPRSRC",
                        "GCPRJT",
                        "GCART",
                        "LH",
                        "GMLH",
                        "GWLH",
                        "GDLH"
                      ],
                      "organizationId": ""+org._id,
                      "_class": "com.beeja.api.accounts.model.Organization.Role"
                    },
                    {

                      "name": "Accounts Manager",
                      "description": "Manages accounts operations",
                      "permissions": [
                        "RPAY",
                        "REMP",
                        "UF",
                        "UEMP",
                        "UPAY",
                        "CHIN",
                        "DHIN",
                        "CPAY",
                        "URAP",
                        "RHIN",
                        "UHIN"
                      ],
                      "organizationId": ""+org._id,
                      "_class": "com.beeja.api.accounts.model.Organization.Role"
                    }]);

const _super_admin_role = db.roles.findOne({name: "Super Admin"});
const _employee_role = db.roles.findOne({name: "Employee"});

db.createCollection("users");
db.users.insertMany([
  {

    "firstName": "Beeja",
    "lastName": "Admin",
    "email": "beeja.admin@domain.com",
    "roles": [
            {
              "$ref": "roles", "$id": _super_admin_role._id
            },
            {
              "$ref": "roles", "$id": _employee_role._id
            }
    ],
    "employeeId": "TACVHR",
    "organizations": { $ref: 'organizations', $id: org._id},
    "password": "$2a$10$f29MeC42B.syEbcbp8YYIuwn4CYviaL9sDjgrJGN7OwWVT2Lt5WiK",
    "isActive": true,
    "createdBy": "kranthi3460@gmail.com",
    "_class": "com.beeja.api.accounts.model.User"
  }]);

db.createCollection("featureToggle");
db.featureToggle.insertMany([{
  "organizationId": ""+org._id,
  "featureToggles": [
    "DOCUMENT_MANAGEMENT",
    "BULK_PAY_SLIPS",
    "ORGANIZATION_SETTINGS",
    "EXPENSE_MANAGEMENT",
    "EMPLOYEE_MANAGEMENT",
    "ORGANIZATION_SETTINGS_ROLES_AND_PERMISSIONS",
    "LOAN_MANAGEMENT",
    "ORGANIZATION_SETTINGS_PROFILE",
    "ORGANIZATION_SETTINGS_FONT_NAME",
    "INVENTORY_MANAGEMENT",
    "ORGANIZATION_SETTINGS_FONT_SIZE",
    "ORGANIZATION_SETTINGS_DATE_CURRENCY",
    "KYC_MANAGEMENT",
    "ORGANIZATION_SETTINGS_THEMES"
  ]
}]);

db.createCollection("organization-values");

db["organization-values"].insertMany([
  {
    organizationId: ""+org._id,
    key: "employeeTypes",
    values: [
      { name: "Full-Time Employees", description: "Employees who work full-time hours" },
      { name: "Permanent Employees", description: "Employees with permanent contracts" },
      { name: "Part-Time Employees", description: "Employees who work part-time hours" },
      { name: "Fixed-term Employees", description: "Employees with a fixed-term contract" },
      { name: "Temporary Employees", description: "Employees hired for temporary roles" },
      { name: "Contract Employees", description: "Employees hired on a contract basis" },
      { name: "Interns", description: "Students gaining work experience" },
      { name: "Trainees", description: "New hires in training programs" },
      { name: "Apprentices", description: "Workers learning a trade under supervision" },
      { name: "Freelancers", description: "Independent workers for specific projects" },
      { name: "Independent Contractors", description: "Non-employee contractors for services" },
      { name: "Consultants", description: "Specialists providing expertise" },
      { name: "Seasonal Employees", description: "Employees hired for seasonal work" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "expenseTypes",
    values: [
      { name: "Airfare", description: "Expenses for flight tickets" },
      { name: "Local transportation", description: "Taxi, metro, or bus fares" },
      { name: "Fuel and vehicle maintenance", description: "Costs for company vehicle maintenance" },
      { name: "Hotel stays", description: "Accommodation for business travel" },
      { name: "Guest houses", description: "Expenses for company-provided accommodations" },
      { name: "Business meals", description: "Meals during business meetings" },
      { name: "Client entertainment", description: "Entertainment expenses for clients" },
      { name: "Stationery", description: "Office stationery items" },
      { name: "IT accessories", description: "Accessories like cables, keyboards, and mice" },
      { name: "Internet charges", description: "Reimbursement for internet usage" },
      { name: "Mobile phone bills", description: "Company-related mobile usage charges" },
      { name: "Medical reimbursements", description: "Reimbursement for medical bills" },
      { name: "Training and development programs", description: "Costs for employee training programs" },
      { name: "Corporate gifts", description: "Gifts for business purposes" },
      { name: "Event or celebration expenses", description: "Costs related to company events" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "expenseCategories",
    values: [
      { name: "Sustainability", description: "Expenses related to environmental initiatives" },
      { name: "Health Care", description: "Medical and wellness-related expenses" },
      { name: "Miscellaneous", description: "General uncategorized expenses" },
      { name: "Professional Development", description: "Training and skill enhancement programs" },
      { name: "Employee Benefits", description: "Employee-related benefits and allowances" },
      { name: "Utilities & Communication", description: "Costs for utilities and communication tools" },
      { name: "Office Supplies", description: "General office-related supplies" },
      { name: "Meals & Entertainment", description: "Expenses for food and entertainment" },
      { name: "Accommodation", description: "Expenses for housing during business travel" },
      { name: "Travel", description: "General travel-related costs" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "expensePaymentTypes",
    values: [
      { name: "Bank Transfers", description: "Payments made through bank transfers" },
      { name: "Cash Payments", description: "Payments made in cash" },
      { name: "Cheques", description: "Payments made via cheques" },
      { name: "Direct Deposits", description: "Payments directly deposited into accounts" },
      { name: "Credit Card", description: "Payments using corporate credit cards" },
      { name: "SEPA Transfers", description: "Standardized European bank payments" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "departments",
    values: [
      { name: "IT Department", description: "Handles IT infrastructure and support" },
      { name: "Information Systems Department", description: "Manages corporate information systems" },
      { name: "Technology Solutions Team", description: "Develops and implements tech solutions" },
      { name: "Software Development Team", description: "Focuses on software creation and updates" },
      { name: "Digital Solutions Department", description: "Works on digital transformation projects" },
      { name: "HR Technology Department", description: "Manages HR-related tech systems" },
      { name: "Enterprise Systems Team", description: "Oversees enterprise-level applications" },
      { name: "IT Services Division", description: "Provides IT support and services" },
      { name: "Digital Innovation Team", description: "Drives innovation in digital technologies" },
      { name: "Information Management Department", description: "Handles corporate information assets" },
      { name: "IT Operations Department", description: "Oversees daily IT operations" },
      { name: "HR Systems Team", description: "Manages HR software and infrastructure" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "jobTypes",
    values: [
      { name: "Software Engineer", description: "Develops and maintains software solutions" },
      { name: "Senior Software Engineer", description: "Leads development projects and mentors juniors" },
      { name: "Software Developer", description: "Focuses on coding and software implementation" },
      { name: "Technical Lead", description: "Guides technical teams on projects" },
      { name: "Project Manager", description: "Manages project timelines and deliverables" },
      { name: "UI/UX Designer", description: "Designs user interfaces and improves user experiences" },
      { name: "Data Scientist", description: "Analyzes data to extract insights" },
      { name: "DevOps Engineer", description: "Automates and optimizes development workflows" },
      { name: "Business Analyst", description: "Analyzes and documents business requirements" },
      { name: "Product Manager", description: "Oversees product development and strategy" },
      { name: "Database Administrator (DBA)", description: "Manages and secures databases" },
      { name: "IT Consultant", description: "Provides expert IT advice and solutions" },
      { name: "Network Engineer", description: "Maintains and optimizes network infrastructure" }
    ]
  },
  {
    organizationId: ""+org._id,
    key: "deviceTypes",
    values: [
      { name: "Laptop", description: "Portable computing devices used for work" },
      { name: "Desktop", description: "Stationary computing systems for office environments" },
      { name: "Tablet", description: "Touchscreen computing devices for mobility" },
      { name: "Server", description: "Centralized machines for managing networks or hosting data" },
      { name: "Smartphone", description: "Mobile phones for business and communication purposes" },
      { name: "Router", description: "Devices for directing data across networks" },
      { name: "Printer", description: "Devices for producing hard copies of documents" },
      { name: "Keyboard", description: "Input devices for typing" },
      { name: "CCTV", description: "Security cameras for monitoring premises" }
    ]
  }
]);

db.createCollection("organization-patterns");
db["organization-patterns"].insertMany([{
  "patternType": "EMPLOYEE_ID_PATTERN",
  "organizationId": ""+org._id,
  "patternLength": 4,
  "prefix": "",
  "initialSequence": 1,
  "examplePattern": "0001",
  "active": true
},
{
  "patternType": "DEVICE_ID_PATTERN",
  "organizationId": ""+org._id,
  "patternLength": 4,
  "prefix": "",
  "initialSequence": 1,
  "examplePattern": "0001",
  "active": true,
},
{
  "patternType": "LOAN_ID_PATTERN",
  "organizationId": ""+org._id,
  "patternLength": 4,
  "prefix": "",
  "initialSequence": 1,
  "examplePattern": "0001",
  "active": true,
}]);



db = db.getSiblingDB("employees_sbox");
db.createCollection("employees");
db.employees.insertMany([{ "employeeId": "TAC1000", "organizationId": ""+org._id }, { "employeeId": "TACVHR", "organizationId": ""+org._id }]);
db = db.getSiblingDB("expenses_sbox");
db.createCollection("expenses");
db = db.getSiblingDB("files_sbox");
db.createCollection("files");
db = db.getSiblingDB("finance_sbox");
db.createCollection("invoice");
db.createCollection("projects");
db.createCollection("clients");
