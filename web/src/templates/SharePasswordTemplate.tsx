export const SharePasswordThroughMailTemplate = (props: {
  firstName: string;
  employeeId: string;
  email: string;
  password: string;
  companyName: string;
}) => {
  return encodeURIComponent(
    `Hello ${props.firstName},\n\nYour account has been successfully created. Below are your credentials:\n\n` +
      `Employee ID: ${props.employeeId}\nEmail ID: ${props.email}\nPassword: ${props.password}\n\n` +
      `Please store your password securely and do not share it with others.\n\nBest regards,\n${props.companyName}`
  );
};
