import React, { useEffect, useState } from 'react';
import {
  Container,
  HeaderRow,
  TitleBlock,
  Title,
  Subtitle,
  TabBar,
  Tab,
  Content,
  QuestionBlock,
  QuestionHeader,
  QuestionText,
  QuestionDesc,
  Placeholder,
  DescriptionBox,
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
  HideNamesToggle,
  AuthorInfo,
  DateDisplayContainer,
} from '../../styles/EvaluationOverview.style';
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs';
import { CalenderIcon } from '../../svgs/ExpenseListSvgs.svg';
import {
  Slider,
  StyledSwitch,
  SwitchLabel,
} from '../../styles/InputStyles.style';
import {
  getAllResponses,
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

type QuestionResponse = {
  questionId: string;
  responses: string[];
  title?: string;
  description?: string;
};

type EvaluationCycle = {
  id: string;
  organizationId: string;
  name: string;
  type: string;
  formDescription?: string;
  startDate: string;
  endDate: string;
  feedbackDeadline: string;
  selfEvalDeadline: string;
  status: string;
  questionnaireId: string;
};

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
  showHideNames?: boolean;
};

const FeedbackReceived: React.FC<FeedbackReceivedProps> = ({
  user,
  showHideNames,
}) => {
  const [activeTab, setActiveTab] = useState<'all' | 'self' | 'rating'>('all');
  const [hideNames, setHideNames] = useState<boolean>(false);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState<number>(0);

  const [loading, setLoading] = useState(false);
  const [mainLoading, setMainLoading] = useState(true);
  const [selfEvaluationData, setSelfEvaluationData] = useState('');

  const [questionData, setQuestionData] = useState<QuestionResponse[]>([]);
  const [evaluationCycle, setEvaluationCycle] =
    useState<EvaluationCycle | null>(null);

  const [ratingData, setRatingData] = useState<OverallRating | null>(null);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    });
  };

  useEffect(() => {
    const fetchResponses = async () => {
      setMainLoading(true);
      try {
        const result = await getAllResponses();
        setEvaluationCycle(result.data.evaluationCycle);
        setQuestionData(result.data.questions);
      } catch (err: any) {
        throw new Error(err?.message || 'Failed to fetch all responses');
      } finally {
        setMainLoading(false);
      }
    };

    fetchResponses();
  }, []);

  useEffect(() => {
    const fetchSelfEvaluation = async () => {
      try {
        setLoading(true);

        const employeeId = user?.employeeId;
        if (!employeeId) {
          throw new Error('Employee ID not found');
        }

        const res = await getSelfEvaluation(employeeId);
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
        const response = await getOverallRating();
        if (response?.data?.length > 0) {
          setRatingData(response.data[0]);
        } else {
          setRatingData(null);
        }
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
            <HeaderRow>
              <TitleBlock>
                <Title>{evaluationCycle?.name}</Title>

                {evaluationCycle && (
                  <DateDisplayContainer>
                    <span className="date-item">
                      <CalenderIcon />
                      {formatDate(evaluationCycle.startDate)}
                    </span>

                    <span className="separator">To</span>

                    <span className="date-item">
                      <CalenderIcon />
                      {formatDate(evaluationCycle.endDate)}
                    </span>
                  </DateDisplayContainer>
                )}
                <Subtitle>
                  {evaluationCycle?.type?.toLowerCase() === 'annual'
                    ? 'Annual Review'
                    : evaluationCycle?.type}
                </Subtitle>
              </TitleBlock>
            </HeaderRow>
            {evaluationCycle?.formDescription && (
              <DescriptionBox>{evaluationCycle.formDescription}</DescriptionBox>
            )}

            <ReceiverRow>
              <ReceiverInfo>
                <ReceiverLabel>Feedback Receiver Name</ReceiverLabel>

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
                All Responses
              </Tab>
              <Tab
                active={activeTab === 'self'}
                onClick={() => setActiveTab('self')}
              >
                Self Evaluation
              </Tab>
              <Tab
                active={activeTab === 'rating'}
                onClick={() => setActiveTab('rating')}
              >
                Overall Rating
              </Tab>
            </TabBar>

            <Content>
              {activeTab === 'all' && (
                <>
                  <FeedbackHeaderRow>
                    {showHideNames && (
                      <HideNamesToggle>
                        <SwitchLabel>
                          <StyledSwitch
                            type="checkbox"
                            checked={hideNames}
                            onChange={(e) => setHideNames(e.target.checked)}
                          />
                          <Slider />
                        </SwitchLabel>
                        <span>Hide feedback provider names</span>
                      </HideNamesToggle>
                    )}
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
                  </FeedbackHeaderRow>

                  {!questionData.length ||
                  !questionData[currentQuestionIndex] ? (
                    <Placeholder>No responses available</Placeholder>
                  ) : (
                    (() => {
                      const q = questionData[currentQuestionIndex];

                      return (
                        <QuestionBlock key={q.questionId}>
                          <QuestionHeader>
                            <QuestionText className="questionHeading">
                              {`${currentQuestionIndex + 1}. ${q.title || ` ${q.questionId}`}`}
                            </QuestionText>
                          </QuestionHeader>

                          {q.description && (
                            <QuestionDesc>{q.description}</QuestionDesc>
                          )}

                          <ResponsesContainer>
                            {q.responses?.length ? (
                              q.responses.map((res: string, index: number) => (
                                <React.Fragment key={index}>
                                  <ResponseHeader>
                                    <div>Response {index + 1}</div>
                                    {showHideNames && !hideNames && (
                                      <AuthorInfo>Anonymous</AuthorInfo>
                                    )}
                                  </ResponseHeader>

                                  <ResponseInnerBox>{res}</ResponseInnerBox>
                                </React.Fragment>
                              ))
                            ) : (
                              <ResponseInnerBox>
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
                      <QuestionHeader>
                        <QuestionText>
                          Please share your reflections on your key
                          achievements, challenges, areas for improvement, and
                          the support you need to grow further
                          <RequiredStar>*</RequiredStar>
                        </QuestionText>
                      </QuestionHeader>
                      <ResponsesContainer>
                        <ResponseHeader>
                          <div>Response</div>
                        </ResponseHeader>
                        <ResponseInnerBox>
                          {selfEvaluationData}
                        </ResponseInnerBox>
                      </ResponsesContainer>
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
                    <Placeholder>
                      Overall Rating has not been submitted yet!
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
