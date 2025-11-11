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
          heading: 'Form Submitted',
          body: 'Self-evaluation form submitted successfully.',
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
    setIsEditable(false);
  };

  return (
    <>
      <StyledDiv>
        <Title>
          {t(
            'Please share your reflections on your key achievements, challenges, areas for improvement, and the support you need to grow further'
          )}

          <RequiredStar>*</RequiredStar>
        </Title>
        {loading ? (
          <SpinAnimation />
        ) : (
          <form onSubmit={handleSubmit}>
            {isSubmitted ? (
              <>
                <SubmittedContainer>
                  <h3>You’ve already submitted your self-evaluation!</h3>
                  <p>You can view it in the ‘My Feedback’ section.</p>
                </SubmittedContainer>
                <Note>
                  <span>
                    <StyledInfoICon />
                  </span>
                  Once you submit your self-evaluation, it cannot be changed, so
                  please review your response before submitting.
                </Note>
              </>
            ) : (
              <>
                <TextArea
                  value={reflection}
                  onChange={(e) => setReflection(e.target.value)}
                  onClick={handleTextAreaClick}
                  disabled={!isEditable}
                  placeholder="Example: Delivered project X ahead of deadline, collaborated with Y team to improve process Z, learned new skill A. Plan to focus on improving B next cycle."
                  required
                />
                <Note>
                  <span>
                    <StyledInfoICon />
                  </span>
                  Once you submit your self-evaluation, it cannot be changed, so
                  please review your response before submitting.
                </Note>

                <div>
                  <ButtonGroup>
                    <Button
                      onClick={handleCancel}
                      type="button"
                      className="cancel"
                    >
                      {t('Cancel')}
                    </Button>
                    <Button className="submit" type="submit">
                      {t('Submit')}
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
