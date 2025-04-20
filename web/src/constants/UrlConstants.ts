export const OriginURL = "http://localhost:8000";
export const ProdOriginURL = "http://localhost:8000";

// NOTE : Base URLS
const AccountsBaseURL = "/accounts";
// const EmployeeBaseURL = "/employees"

// NOTE: Authentication URLS
export const LoginUrl = "/auth/login/google";
export const LogoutUrl = "/auth/logout";

// NOTE: Accounts URLS
export const AccountUsers = AccountsBaseURL + "/v1/users";
export const MeUrl = AccountsBaseURL + "/v1/users/me";
