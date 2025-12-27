import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Container,
  TabBar,
  Tab,
  Content,
  QuestionBlock,
  QuestionHeader,
  QuestionText,
  QuestionDesc,
  Placeholder,
  NameBox,
  NavButton,
  ResponsesContainer,
  ResponseHeader,
  ResponseInnerBox,
  FeedbackHeaderRow,
  QuestionProgress,
  ReceiverInfo,
  ReceiverLabel,
  ReceiverRow,
} from '../../styles/FeedbackReceivedStyles.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';

import {
  getAllResponsesById,
  getAllResponsesDropDown,
  getOverallRating,
  getSelfEvaluation,
} from '../../service/axiosInstance';
import {
  RatingBox,
  RatingHeader,
  RatingIcon,
  RatingText,
  RatingValue,
  RequiredStar,
} from '../../styles/FeedbackHubStyles.style';
import { OverallRatingStar } from '../../svgs/PerformanceEvaluation.Svgs.scg';
import SpinAnimation from '../loaders/SprinAnimation.loader';
import { SelectWrapper } from '../../styles/CreateReviewCycleStyle.style';
import DropdownMenu from './DropDownMenu.component';

type QuestionResponse = {
  questionId: string;
  responses: string[];
  title?: string;
  description?: string;
};

interface CycleItem {
  cycleId: string;
  cycleName: string;
  type: string;
  status: string;
  startDate: string;
  endDate: string;
}

interface OverallRating {
  id: string;
  organizationId: string;
  employeeId: string;
  cycleId: string;
  rating: number;
  comments: string | null;
  givenBy: string;
  published: boolean;
  publishedAt: string | null;
}

type FeedbackReceivedProps = {
  user: any;
};

const FeedbackReceived: React.FC<FeedbackReceivedProps> = ({ user }) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<'all' | 'self' | 'rating'>('all');
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState<number>(0);

  const [loading, setLoading] = useState(false);
  const [mainLoading, setMainLoading] = useState(true);
  const [selfEvaluationData, setSelfEvaluationData] = useState('');
  const [forms, setForms] = useState<CycleItem[]>([]);
  const [selectedCycleId, setSelectedCycleId] = useState('');

  const [questionData, setQuestionData] = useState<QuestionResponse[]>([]);

  const [ratingData, setRatingData] = useState<OverallRating | null>(null);

  const employeeId = user?.employeeId;

  useEffect(() => {
    const fetchForms = async () => {
      try {
        setMainLoading(true);
        const res = await getAllResponsesDropDown();
        const formsList = res.data || [];
        setForms(formsList);

        if (formsList.length > 0) {
          const firstCycleId = formsList[0].id || formsList[0].cycleId;
          setSelectedCycleId(firstCycleId);

          const response = await getAllResponsesById(firstCycleId);
          const { questions } = response.data;
          setQuestionData(questions || []);
        }
      } catch (err: any) {
        throw new Error(err?.message || 'Failed to fetch cycle details');
      } finally {
        setMainLoading(false);
      }
    };

    fetchForms();
  }, []);

  const handleSelectForm = async (cycleId: string) => {
    setSelectedCycleId(cycleId);
    setCurrentQuestionIndex(0);
    try {
      setMainLoading(true);
      const res = await getAllResponsesById(cycleId);

      const { questions } = res.data;
      setQuestionData(questions || []);
    } catch (err: any) {
      throw new Error(err?.message || 'Failed to fetch forms');
    } finally {
      setMainLoading(false);
    }
  };

  useEffect(() => {
    const fetchSelfEvaluation = async () => {
      try {
        setLoading(true);

        if (!employeeId) {
          throw new Error('Employee ID not found');
        }

        const res = await getSelfEvaluation();
        const storedAnswer = res.data?.[0]?.responses?.[0]?.answer || '';
        setSelfEvaluationData(storedAnswer);
      } catch (err: any) {
        throw new Error(err?.message || 'Failed to fetch self evaluation');
      } finally {
        setLoading(false);
      }
    };
    fetchSelfEvaluation();
  }, []);

  useEffect(() => {
    const fetchOverallRating = async () => {
      setLoading(true);
      try {
        const response = await getOverallRating(employeeId);
        setRatingData(response.data);
      } catch (error) {
        setRatingData(null);
      } finally {
        setLoading(false);
      }
    };
    fetchOverallRating();
  }, []);

  return (
    <>
      {mainLoading ? (
        <SpinAnimation />
      ) : (
        <>
          <Container>
            <ReceiverRow>
              <ReceiverInfo>
                <ReceiverLabel>{t('Feedback_Receiver_Name')}</ReceiverLabel>
                <NameBox>
                  {[user?.firstName, user?.lastName]
                    .filter(Boolean)
                    .map(
                      (name) =>
                        name.charAt(0).toUpperCase() +
                        name.slice(1).toLowerCase()
                    )
                    .join(' ')}
                </NameBox>
              </ReceiverInfo>
            </ReceiverRow>
            <TabBar>
              <Tab
                active={activeTab === 'all'}
                onClick={() => setActiveTab('all')}
              >
                {t('All_Responses')}
              </Tab>
              <Tab
                active={activeTab === 'self'}
                onClick={() => setActiveTab('self')}
              >
                {t('Self_Evaluation')}
              </Tab>
              <Tab
                active={activeTab === 'rating'}
                onClick={() => setActiveTab('rating')}
              >
                {t('Overall_Rating')}
              </Tab>
            </TabBar>

            <Content>
              {activeTab === 'all' && (
                <>
                  <FeedbackHeaderRow>
                    <SelectWrapper>
                      <label htmlFor="feedbackFormSelect">
                        {t('Feedback_Form')}
                      </label>
                      <DropdownMenu
                        label={
                          forms.length === 0
                            ? t('NO_FORMS_AVAILABLE')
                            : t('Select form')
                        }
                        name="feedbackFormSelect"
                        id="feedbackFormSelect"
                        value={selectedCycleId || ''}
                        required
                        className="largeContainerExp"
                        disabled={mainLoading || forms.length === 0}
                        options={
                          forms.length > 0
                            ? forms.map((item) => ({
                                label: item.cycleName,
                                value: item.cycleId,
                              }))
                            : []
                        }
                        onChange={(selectedValue) => {
                          if (selectedValue) {
                            handleSelectForm(selectedValue);
                          }
                        }}
                      />
                    </SelectWrapper>

                    {questionData.length > 0 && (
                      <QuestionProgress>
                        <NavButton
                          disabled={currentQuestionIndex === 0}
                          onClick={() => setCurrentQuestionIndex((i) => i - 1)}
                        >
                          <span className="arrow right">
                            <ArrowDownSVG />
                          </span>
                        </NavButton>
                        <span>
                          Question - {currentQuestionIndex + 1}/
                          {questionData.length}
                        </span>
                        <NavButton
                          disabled={
                            currentQuestionIndex === questionData.length - 1
                          }
                          onClick={() => setCurrentQuestionIndex((i) => i + 1)}
                        >
                          <span className="arrow left">
                            <ArrowDownSVG />
                          </span>
                        </NavButton>
                      </QuestionProgress>
                    )}
                  </FeedbackHeaderRow>

                  {!questionData.length ||
                  !questionData[currentQuestionIndex] ? (
                    <Placeholder>{t('NO_RESPONSES_AVAILABLE')}</Placeholder>
                  ) : (
                    (() => {
                      const q = questionData[currentQuestionIndex];

                      return (
                        <QuestionBlock key={q.questionId}>
                          <QuestionHeader>
                            <QuestionText className="questionHeading">
                              {`${currentQuestionIndex + 1}. ${q.title || q.questionId}`}
                            </QuestionText>
                          </QuestionHeader>

                          {q.description && (
                            <QuestionDesc>{q.description}</QuestionDesc>
                          )}

                          <ResponsesContainer>
                            {q.responses?.length &&
                            q.responses.some((r) => !!r) ? (
                              q.responses
                                .filter((res) => !!res)
                                .map((res: string, index: number) => (
                                  <React.Fragment key={index}>
                                    <ResponseHeader>
                                      <div>Response {index + 1}</div>
                                    </ResponseHeader>
                                    <ResponseInnerBox>{res}</ResponseInnerBox>
                                  </React.Fragment>
                                ))
                            ) : (
                              <ResponseInnerBox className="no-answer">
                                No responses yet
                              </ResponseInnerBox>
                            )}
                          </ResponsesContainer>
                        </QuestionBlock>
                      );
                    })()
                  )}
                </>
              )}

              {activeTab === 'self' && (
                <>
                  {loading ? (
                    <SpinAnimation />
                  ) : (
                    <QuestionBlock>
                      {selfEvaluationData && selfEvaluationData.length > 0 ? (
                        <>
                          <QuestionHeader>
                            <QuestionText>
                              {t('SELF_EVALUATION_QUESTION')}
                              <RequiredStar>*</RequiredStar>
                            </QuestionText>
                          </QuestionHeader>

                          <ResponsesContainer>
                            <ResponseHeader>
                              <div>{t('RESPONSE')}</div>
                            </ResponseHeader>
                            <ResponseInnerBox>
                              {selfEvaluationData}
                            </ResponseInnerBox>
                          </ResponsesContainer>
                        </>
                      ) : (
                        <Placeholder>
                          {t('SELF_EVALUATION_NOT_SUBMITTED')}
                        </Placeholder>
                      )}
                    </QuestionBlock>
                  )}
                </>
              )}

              {activeTab === 'rating' && (
                <>
                  {loading ? (
                    <SpinAnimation />
                  ) : ratingData ? (
                    <ResponsesContainer>
                      <RatingBox>
                        <RatingHeader>
                          <RatingIcon>
                            <OverallRatingStar />
                          </RatingIcon>
                          <RatingValue>
                            {ratingData.rating} <span>(Overall Rating)</span>
                          </RatingValue>
                        </RatingHeader>

                        <RatingText>{ratingData.comments}</RatingText>
                      </RatingBox>
                    </ResponsesContainer>
                  ) : (
                    <Placeholder className="rating">
                      {t('OVERALL_RATING_NOT_SUBMITTED')}
                    </Placeholder>
                  )}
                </>
              )}
            </Content>
          </Container>
        </>
      )}
    </>
  );
};

export default FeedbackReceived;
