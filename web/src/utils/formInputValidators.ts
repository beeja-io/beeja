export function isValidPanCardNo(panCardNo: string) {
  const regex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/;
  return regex.test(panCardNo);
}

export function isValidPFNumber(pfNumber: string) {
  const regex =
    /^[A-Z]{2}[ /]?[A-Z]{3}[ /]?[0-9]{7}[ /]?[0-9]{3}[ /]?[0-9]{7}$/;
  return regex.test(pfNumber);
}

export function isValidTANNumber(tanNumber: string) {
  const regex = /^[A-Z]{4}[0-9]{5}[A-Z]{1}$/;
  return regex.test(tanNumber);
}

export function isValidESINumber(esiNumber: string) {
  // const regex = /^[0-9]{2}-[0-9]{2}-[0-9]{6}-[0-9]{3}-[0-9]{4}$/;
  const regex = /^[0-9]{17}$/;
  return regex.test(esiNumber);
}

export function isValidGSTNumber(gstNumber: string) {
  const regex =
    /^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$/;
  return regex.test(gstNumber);
}

export function isValidLINNumber(linNumber: string) {
  const regex = /^[0-9]{10}$/;
  return regex.test(linNumber);
}

export function isValidEmail(email: string) {
  const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return regex.test(email);
}

export function isValidURL(url: string) {
  const regex =
    /^(http(s)?:\/\/)?(www\.)?([a-zA-Z0-9-_]+)(\.[a-zA-Z]{2,})+(\.[a-zA-Z]{2,})?$/;
  return regex.test(url);
}

export function isValidPINCode(pinCode: string) {
  const regex = /^[0-9]{6}$/;
  return regex.test(pinCode);
}

export const isValidAccountNo = (accountNo: string): boolean => {
  return /^\d{1,17}$/.test(accountNo);
};

export const isValidAadhaarNumber = (aadhaarNumber: string): boolean => {
  return /^\d{12}$/.test(aadhaarNumber);
};

export const isValidPassportNumber = (passportNumber: string): boolean => {
  return /^[A-Z0-9]{12}$/.test(passportNumber);
};

export const isValidBankName = (bankName: string): boolean => {
  const regex = /^[A-Za-z ]+$/;
  return regex.test(bankName.trim());
};

export const isValidAccountName = (accountName: string): boolean => {
  const regex = /^[A-Za-z ]+$/;
  return regex.test(accountName.trim());
};

export const isValidAccountNumber = (accountNumber: string): boolean => {
  const regex = /^[0-9]{9,18}$/;
  return regex.test(accountNumber.trim());
};

export const isValidIFSCCode = (ifscCode: string): boolean => {
  const regex = /^[A-Z]{4}0[0-9A-Z]{6}$/;
  return regex.test(ifscCode.trim());
};

export const isValidTaxNo = (taxNumber: string): boolean => {
  const regex = /^[A-Z]{3,}[ -]?[0-9]{5,}$/;
  return regex.test(taxNumber.trim());
};
