import { AxiosError } from 'axios';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'sonner';
import { TextInput } from 'web-kit-components';
import { IApplicant } from '../../entities/ApplicantEntity';
import {
  changeApplicationStatus,
  getApplicantById,
  postComment,
} from '../../service/axiosInstance';
import { BulkPayslipContainer } from '../../styles/BulkPayslipStyles.style';
import {
  InputLabelContainer,
  ValidationText,
} from '../../styles/DocumentTabStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../../styles/ExpenseManagementStyles.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import CommentsSection from '../reusableComponents/CommentsSection.component';

const EditApplicant = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { t } = useTranslation();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const [isLoading, setIsLoading] = useState(false);
  const [applicant, setApplicant] = useState<IApplicant>({} as IApplicant);
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setApplicant((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const fetchApplicantById = () => {
    setIsLoading(true);
    id &&
      getApplicantById(id)
        .then((res) => {
          setApplicant(res.data);
          setIsLoading(false);
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSubmitComment = async (message: string) => {
    const payload = {
      applicantId: applicant.id,
      comment: message,
    };

    try {
      setIsLoading(true);
      const response = await postComment(payload);
      setApplicant(response.data);
      setIsLoading(false);
      toast.success('Comment added successfully');
    } catch {
      setIsLoading(false);
      toast.error('Failed to post comment');
    }
  };

  const handleStatusChange = async (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const newStatus = e.target.value;
    e.preventDefault();
    toast.promise(
      async () => await changeApplicationStatus(applicant.id, newStatus),
      {
        loading: 'Updating applicant status',
        success: 'Applicant status updated successfully',
        error: 'Failed to update applicant status',
      }
    );
  };

  return (
    <>
      {isLoading ? (
        <SpinAnimation />
      ) : (
        <ExpenseManagementMainContainer>
          <ExpenseHeadingSection>
            <span className="heading">
              <span onClick={goToPreviousPage}>
                <ArrowDownSVG />
              </span>
              {t('UPDATING')}{' '}
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
                      {t('First_Name')}{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="text"
                      name="firstName"
                      id="firstName"
                      onChange={handleInputChange}
                      value={applicant.firstName}
                      disabled
                      required
                      onKeyPress={(
                        e: React.KeyboardEvent<HTMLInputElement>
                      ) => {
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
                      style={{ cursor: 'not-allowed' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>
                      {t('Last_Name')}{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="text"
                      name="lastName"
                      id="lastName"
                      value={applicant.lastName}
                      required
                      disabled
                      autoComplete="off"
                      onKeyPress={(
                        e: React.KeyboardEvent<HTMLInputElement>
                      ) => {
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
                      style={{ cursor: 'not-allowed' }}
                    />
                  </InputLabelContainer>
                </div>

                <div>
                  <InputLabelContainer>
                    <label>
                      {t('Phone_Number')}{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="text"
                      name="phoneNumber"
                      id="phoneNumber"
                      value={applicant.phoneNumber}
                      required
                      disabled
                      autoComplete="off"
                      onKeyPress={(
                        e: React.KeyboardEvent<HTMLInputElement>
                      ) => {
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
                      style={{ cursor: 'not-allowed' }}
                    />
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>
                      {t('EMAIL')}{' '}
                      <ValidationText className="star">*</ValidationText>
                    </label>
                    <TextInput
                      type="email"
                      name="email"
                      id="email"
                      value={applicant.email}
                      autoComplete="off"
                      required
                      disabled
                      placeholder={'Enter Email'}
                      style={{ cursor: 'not-allowed' }}
                    />
                  </InputLabelContainer>
                </div>
                <div>
                  <InputLabelContainer>
                    <label>{t('EXPERIENCE')}</label>
                    <select
                      className="selectoption"
                      name="experience"
                      id="experience"
                      disabled
                      onKeyPress={(event) => {
                        if (event.key === 'Enter') {
                          event.preventDefault();
                        }
                      }}
                      style={{ cursor: 'not-allowed' }}
                    >
                      <option value="">
                        {applicant.experience ? applicant.experience : '-'}
                      </option>
                    </select>
                  </InputLabelContainer>
                  <InputLabelContainer>
                    <label>Status</label>
                    <select
                      className="selectoption largeSelectOption"
                      name="department"
                      onChange={handleStatusChange}
                      onKeyPress={(event) => {
                        if (event.key === 'Enter') {
                          event.preventDefault();
                        }
                      }}
                      style={{ cursor: 'pointer' }}
                    >
                      {[
                        'APPLIED',
                        'SHORTLISTED',
                        'INTERVIEW_SCHEDULED',
                        'HIRED',
                        'REJECTED',
                      ].map((status) => (
                        <option
                          key={status}
                          value={status}
                          selected={applicant.status === status}
                        >
                          {status.replace('_', ' ')}
                        </option>
                      ))}
                    </select>
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
        </ExpenseManagementMainContainer>
      )}
    </>
  );
};

export default EditApplicant;
