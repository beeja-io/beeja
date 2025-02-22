import React, { useEffect, useState } from 'react';
import { TextInput } from 'web-kit-components';
import { BulkPayslipContainer } from '../../styles/BulkPayslipStyles.style';
import {
  InputLabelContainer,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import {
  ExpenseManagementMainContainer,
  ExpenseHeadingSection,
} from '../../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import { useNavigate, useParams } from 'react-router-dom';
import {
  getAllEmployeesByPermission,
  getApplicantById,
  postComment,
} from '../../service/axiosInstance';
import { IApplicant } from '../../entities/ApplicantEntity';
import { AxiosError } from 'axios';
import { RECRUITMENT_MODULE } from '../../constants/PermissionConstants';
import { LoggedInUserEntity } from '../../entities/LoggedInUserEntity';
import { InterviewerArea } from '../../styles/RecruitmentStyles.style';
import CommentsSection from '../reusableComponents/CommentsSection.component';

const EditApplicant = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [applicant, setApplicant] = useState<IApplicant>({} as IApplicant);
  const [interviewers, setInterviewers] = useState<LoggedInUserEntity[]>([]);
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setApplicant((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };
  const getAllInterviewersWithRequiredPermission = () => {
    getAllEmployeesByPermission(RECRUITMENT_MODULE.TAKE_INTERVIEW)
      .then((res) => {
        setInterviewers(res.data);
      })
      .catch((error) => {
        if (error instanceof AxiosError) {
          if (error.response?.status === 404) {
            navigate('/recruitment');
            return;
          }
        }
        navigate('/recruitment');
        return;
      });
  };
  const fetchApplicantById = () => {
    id &&
      getApplicantById(id)
        .then((res) => {
          setApplicant(res.data);
        })
        .catch((error) => {
          if (error instanceof AxiosError) {
            if (error.response?.status === 404) {
              navigate('/recruitment');
              return;
            }
          }
          navigate('/recruitment');
          return;
        });
  };
  useEffect(() => {
    fetchApplicantById();
    getAllInterviewersWithRequiredPermission();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSubmitComment = async (message: string) => {
    const payload = {
      applicantId: applicant.id,
      comment: message,
    };

    try {
      const response = await postComment(payload);
      alert(response.data);
      setApplicant(response.data);
    } catch (error) {
      alert('An error occurred while submitting the comment.');
    }
  };

  return (
    <ExpenseManagementMainContainer>
      <ExpenseHeadingSection>
        <span className="heading">
          <span onClick={goToPreviousPage}>
            <ArrowDownSVG />
          </span>
          Updating{' '}
          <strong>
            {applicant?.firstName} {applicant?.lastName}
          </strong>
        </span>
      </ExpenseHeadingSection>
      <BulkPayslipContainer className="addNewApplicant">
        {applicant && (
          <form>
            <div>
              <InputLabelContainer>
                <label>
                  First Name <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  name="firstName"
                  id="firstName"
                  onChange={handleInputChange}
                  value={applicant.firstName}
                  required
                  onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    } else {
                      const isAlphabet = /^[a-zA-Z\s]+$/.test(e.key);
                      if (!isAlphabet) {
                        e.preventDefault();
                      }
                    }
                  }}
                  placeholder={'Ex: John'}
                  autoComplete="off"
                />
              </InputLabelContainer>
              <InputLabelContainer>
                <label>
                  Last Name <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  name="lastName"
                  id="lastName"
                  value={applicant.lastName}
                  required
                  autoComplete="off"
                  onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    } else {
                      const isAlphabet = /^[a-zA-Z]+$/.test(e.key);
                      if (!isAlphabet) {
                        e.preventDefault();
                      }
                    }
                  }}
                  placeholder={'Ex: Doe'}
                />
              </InputLabelContainer>
            </div>

            <div>
              <InputLabelContainer>
                <label>
                  Phone Number{' '}
                  <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="text"
                  name="phoneNumber"
                  id="phoneNumber"
                  value={applicant.phoneNumber}
                  required
                  autoComplete="off"
                  onKeyPress={(e: React.KeyboardEvent<HTMLInputElement>) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    } else {
                      const isNumeric = /^\d+$/.test(e.key);
                      if (!isNumeric) {
                        e.preventDefault();
                      }
                    }
                  }}
                  placeholder={'Enter Phone Number'}
                />
              </InputLabelContainer>
              <InputLabelContainer>
                <label>
                  Email <ValidationText className="star">*</ValidationText>
                </label>
                <TextInput
                  type="email"
                  name="email"
                  id="email"
                  value={applicant.email}
                  autoComplete="off"
                  required
                  placeholder={'Enter Email'}
                />
              </InputLabelContainer>
            </div>
          </form>
        )}
        <CommentsSection
          comments={
            applicant.applicantComments ? applicant.applicantComments : []
          }
          handleSubmitComment={handleSubmitComment}
        />
      </BulkPayslipContainer>
      <BulkPayslipContainer className="addNewApplicant secondItem">
        <InterviewerArea>
          <section className="assignedInterviewers">
            <table>
              <thead>
                <tr>
                  <th>Employee ID</th>
                  <th>Full Name</th>
                  <th>Email</th>
                  <th>Order of Interview</th>
                </tr>
              </thead>
              {applicant &&
                applicant.assignedInterviewers &&
                applicant.assignedInterviewers.map((interviewer) => (
                  <tr>
                    <td>{interviewer.employeeId}</td>
                    <td>{interviewer.fullName}</td>
                    <td>{interviewer.email}</td>
                    <td>{interviewer.orderOfInterview}</td>
                  </tr>
                ))}
            </table>
          </section>

          <section>
            <span>Assign Interviewer</span>
            {interviewers && (
              <select>
                {interviewers.map((interviewer) => (
                  <option value={interviewer.employeeId}>
                    {interviewer.firstName} {interviewer.employeeId}
                  </option>
                ))}
              </select>
            )}
          </section>
        </InterviewerArea>
      </BulkPayslipContainer>
    </ExpenseManagementMainContainer>
  );
};

export default EditApplicant;
