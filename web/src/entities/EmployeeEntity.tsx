import { IOrganization } from './OrganizationEntity';

export interface EmployeeEntity {
  employee: {
    id: string;
    beejaAccountId: string;
    employeeId: string;
    organizationId: string;
    profilePictureId?: string;
    address: {
      houseNumber: string;
      landMark: string;
      village: string;
      city: string;
      state: string;
      country: string;
      pinCode: string;
    };
    personalInformation: {
      nationality: string;
      dateOfBirth: string;
      gender: string;
      maritalStatus: string;
      personalTaxId: string;
      nomineeDetails: {
        name: string;
        email: string;
        phone: string;
        relationType: string;
        aadharNumber: string;
      };
    };
    kycDetails: {
      aadhaarNumber: string;
      panNumber: string;
      passportNumber: string;
    };
    contact: {
      alternativeEmail: string;
      phone: string;
      alternativePhone: string;
    };
    jobDetails: {
      designation: string;
      employementType: string;
      department: string;
      joiningDate: string;
      resignationDate: string;
    };
    position: string;
    pfDetails: {
      joiningData: string;
      accountNumber: string;
      state: string;
      location: string;
      pfnumber: string | null;
      uan: string;
    };
    bankDetails: {
      accountNo: string;
      ifscCode: string;
      bankName: string;
      branchName: string;
    };
  };
  account: {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    roles: Array<{
      id: string;
      name: string;
      permissions: string[];
    }>;
    employeeId: string;
    organizations: IOrganization;
    userPreferences: null;
    permissions: string[];
    createdBy: string;
    modifiedBy: string;
    createdAt: string;
    modifiedAt: string;
    active: boolean;
  };
}
