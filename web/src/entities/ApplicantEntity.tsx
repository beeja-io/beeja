export interface IAssignedInterviewer {
  employeeId: string;
  email: string;
  fullName: string;
  interviewType: string;
  orderOfInterview: string;
  feedback: string;
}

export interface IApplicantComment {
  id: number;
  message: string;
  commentedByEmail: string;
  commentedByName: string;
  createdAt: string;
}

export interface IApplicant {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  positionAppliedFor: string;
  resumeId: string;
  status:
    | 'APPLIED'
    | 'SHORTLISTED'
    | 'INTERVIEW_SCHEDULED'
    | 'HIRED'
    | 'REJECTED';
  organizationId: string;
  assignedInterviewers: IAssignedInterviewer[];
  notes: string[];
  referredByEmployeeName: string;
  experience: string;
  applicantComments: IApplicantComment[];
  applicantId: string;
  createdAt: string;
  modifiedAt: string;
  modifiedBy: string;
}
