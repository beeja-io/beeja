import { useUser } from '../context/UserContext';
import { t } from 'i18next';
import React, { useEffect, useState } from 'react';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';

import { ButtonGroup } from '../styles/ClientStyles.style';
import {
  Note,
  RequiredStar,
  StyledDiv,
  SubmittedContainer,
  TextArea,
  Title,
} from '../styles/FeedbackHubStyles.style';
import { Button } from '../styles/CommonStyles.style';
import {
  getSelfEvaluation,
  postSelfEvaluation,
} from '../service/axiosInstance';
import { StyledInfoICon } from '../svgs/PerformanceEvaluation.Svgs.scg';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

const SelfEvaluationForm: React.FC = () => {
  const [reflection, setReflection] = useState('');
  const [isEditable, setIsEditable] = useState(true);

  const [isSubmitted, setIsSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const { user } = useUser();
  const employeeId = user?.employeeId ? user?.employeeId : '';
  const [toastMessage, setToastMessage] = useState<{
    type: 'success' | 'error';
    heading: string;
    body: string;
  } | null>(null);
  const [isCreatedToastMessage, setIsCreatedToastMessage] = useState(false);

  const handleTextAreaClick = () => {
    setIsEditable(true);
  };

  const handleIsCreatedToastMessage = () => {
    setIsCreatedToastMessage(false);
    setToastMessage(null);
  };

  useEffect(() => {
    const fetchStatus = async () => {
      if (!employeeId) return;
      setLoading(true);
      try {
        const res = await getSelfEvaluation();
        if (res?.data[0]?.submitted) {
          setIsSubmitted(true);
        }
      } catch (error) {
        throw new Error(
          'Error checking evaluation status: ' +
            (error instanceof Error ? error.message : error)
        );
      } finally {
        setLoading(false);
      }
    };
    if (employeeId) fetchStatus();
  }, [employeeId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const fullName = [user?.firstName, user?.lastName]
        .filter(Boolean)
        .join(' ');
      const payload = {
        employeeId: employeeId,
        submittedBy: fullName,
        responses: [
          {
            questionId: 'self_reflection',
            answer: reflection,
          },
        ],
      };

      const response = await postSelfEvaluation(payload);

      if (response?.status === 200 || response?.status === 201) {
        setToastMessage({
          type: 'success',
          heading: 'Submitted Successfully',
          body: 'Your self evaluation has been submitted successfully.',
        });
        setIsCreatedToastMessage(true);

        setIsSubmitted(true);
      } else {
        alert('Something went wrong. Please try again.');
      }
    } catch (error) {
      throw new Error(
        'Error submitting self evaluation: ' +
          (error instanceof Error ? error.message : error)
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setReflection('');
    setIsEditable(true);
  };

  return (
    <>
      <StyledDiv>
        <Title>
          {t('SELF_EVALUATION_QUESTION')}
          <RequiredStar>*</RequiredStar>
        </Title>
        {loading ? (
          <SpinAnimation />
        ) : (
          <form onSubmit={handleSubmit}>
            {isSubmitted ? (
              <>
                <SubmittedContainer>
                  <h3>{t('SELF_EVALUATION_ALREADY_SUBMITTED')}</h3>
                  <p>{t('VIEW_SELF_EVALUATION_IN_MY_FEEDBACK')}</p>
                </SubmittedContainer>
                <Note>
                  <span>
                    <StyledInfoICon />
                  </span>
                  {t('SELF_EVALUATION_SUBMIT_WARNING')}
                </Note>
              </>
            ) : (
              <>
                <TextArea
                  value={reflection}
                  onChange={(e) => setReflection(e.target.value)}
                  onClick={handleTextAreaClick}
                  disabled={!isEditable}
                  placeholder={t('SELF_EVALUATION_PLACEHOLDER')}
                  required
                />
                <Note>
                  <span>
                    <StyledInfoICon />
                  </span>
                  {t('SELF_EVALUATION_NOTE')}
                </Note>

                <div>
                  <ButtonGroup>
                    <Button
                      onClick={handleCancel}
                      type="button"
                      className="cancel"
                      disabled={reflection.trim().length === 0}
                    >
                      {t('Cancel')}
                    </Button>
                    <Button
                      className="submit"
                      type="submit"
                      disabled={reflection.trim().length === 0}
                    >
                      {t('SUBMIT')}
                    </Button>
                  </ButtonGroup>
                </div>
              </>
            )}
          </form>
        )}
      </StyledDiv>
      {toastMessage && isCreatedToastMessage && (
        <ToastMessage
          messageType={toastMessage.type}
          messageHeading={toastMessage.heading}
          messageBody={toastMessage.body}
          handleClose={handleIsCreatedToastMessage}
        />
      )}
    </>
  );
};

export default SelfEvaluationForm;
