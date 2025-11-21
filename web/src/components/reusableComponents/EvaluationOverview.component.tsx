import React, { useEffect, useState } from "react";
import {
  OuterContainer,
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
  OuterHeader,
  ReceiverInfo,
  ReceiverLabel,
  ReceiverRow,
  ProvideRatingButton,
  HideNamesToggle,
  EvaluationHeadingSection,
  AuthorInfo,
  CycleSelectContainer,
  CycleLabel,
  ResponsesContainer2,
  RatingBox,
  RatingHeader,
  RatingIcon,
  RatingValue,
  RatingText,
  OverallRatingStar,
  WriteSVG,
} from "../../styles/EvaluationOverview.style";

import Rating from "./Rating.component";
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs'
import { MyProfileSVG } from "../../svgs/NavBarSvgs.svg";
import { Slider, StyledSwitch, SwitchLabel } from "../../styles/InputStyles.style";
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getEmployeeCycleGroupedResponse, getEmployeeFeedbackCycles, getEmployeeOverallRating, getEmployeeSelfEvaluation, postEmployeeOverallRating } from "../../service/axiosInstance";
import DropdownMenu from "./DropDownMenu.component";
import SpinAnimation from "../loaders/SprinAnimation.loader";
import { disableBodyScroll, enableBodyScroll } from "../../constants/Utility";
import { useTranslation } from "react-i18next";
import ToastMessage from "./ToastMessage.component";

type FeedbackCycle = {
  employeeId: string;
  cycleId: string;
  cycleName: string;
};

type GroupedResponse = {
  questions: {
    questionId: string;
    description: string;
    responses: {
      reviewerId: string;
      answer: string;
    }[];
  }[];
};

type SelfEvaluationResponse = {
  id: string;
  organizationId: string;
  employeeId: string;
  submittedBy: string;
  submittedAt: string;
  submitted: boolean;
  responses: {
    questionId: string;
    description: string | null;
    answer: string;
  }[];
};


const EvaluationOverview: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { employeeId } = useParams();
  const { state } = useLocation();
  const [activeTab, setActiveTab] = useState<"all" | "self" | "rating">("all");
  const [hideNames, setHideNames] = useState<boolean>(false);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState<number>(0);
  const [showRatingCard, setShowRatingCard] = useState<boolean>(false);
  const [groupedResponse, setGroupedResponse] = useState<GroupedResponse | null>(null);
  const [forms, setForms] = useState<FeedbackCycle[]>([]);
  const [selectedCycleId, setSelectedCycleId] = useState<string>("");
  const [overallRating, setOverallRating] = useState<{ rating: number; comments: string } | null>(null);
  const [selfEvaluation, setSelfEvaluation] = useState<SelfEvaluationResponse[] | null>(null);
  const [ratingData, setRatingData] = useState({
    rating: "",
    comments: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingAllResponses, setLoadingAllResponses] = useState(false);
  const [isLoadingSelf, setLoadingSelf] = useState(false);
  const [isLoadingRating, setLoadingRating] = useState(false);
  const [rating, setRating] = useState<boolean>(false);
  const [toast, setToast] = useState<{
    type: "success" | "error";
    message: string;
    head: string;
  } | null>(null);


  const handleCycleSelect = async (cycleId: string) => {
    if (!employeeId || !cycleId) return;
    setCurrentQuestionIndex(0);
    if (!isLoading)
      setLoadingAllResponses(true);
    await getEmployeeCycleGroupedResponse(employeeId, cycleId)
      .then((res) => { setGroupedResponse(res.data) })
      .catch((err) => console.error("Error fetching grouped response:", err));
    if (!isLoading)
      setLoadingAllResponses(false);
  };

  const fetchEmployeeRating = async () => {
    if (!employeeId) return;
    try {
      setLoadingRating(true);
      const res = await getEmployeeOverallRating(employeeId);
      setOverallRating(res.data);
      if (res.data.rating > 0) {
        setRating(true);
      }
      setLoadingRating(false);
    } catch (err) {
      console.error("Error fetching employee rating:", err);
    }
  };
  const submitEmployeeRating = async (rating: number, comments: string) => {
    if (!employeeId) return;
    try {
      const res = await postEmployeeOverallRating(employeeId, { rating, comments });
      setOverallRating(res.data);
      setToast({
        type: "success",
        message: `The Rating for ${state.firstName} ${state.lastName} has been submitted successfully`,
        head: "Rating submitted successfully",
      });
      fetchEmployeeRating();
    } catch (err) {
      console.error("Error submitting rating:", err);
      setToast({
        type: "error",
        message: "Error while submitting rating",
        head: "Failed",
      });
    }
  };
  const fetchSelfEvaluation = async () => {
    if (!employeeId)
      return;
    setLoadingSelf(true);
    await getEmployeeSelfEvaluation(employeeId)
      .then((res) => {
        setSelfEvaluation(res.data);
      })
      .catch((err) => console.error("Error fetching self evaluation:", err));
    setLoadingSelf(false);
  }

  useEffect(() => {
    if (!employeeId) return;
    setIsLoading(true);
    getEmployeeFeedbackCycles(employeeId)
      .then((res) => {
        setForms(res.data);
        if (res.data.length > 0) {
          const firstCycle = res.data[0];
          setSelectedCycleId(firstCycle.cycleId);
          handleCycleSelect(firstCycle.cycleId);
        }
      })
      .catch((err) => {
        console.error("Error fetching feedback cycles:", err);
      });
    setIsLoading(false);
  }, [employeeId]);

  useEffect(() => {
    if (showRatingCard) {
      disableBodyScroll();
    } else {
      enableBodyScroll();
    }

    return () => enableBodyScroll();
  }, [showRatingCard]);

  return (
    <>
      {isLoading ? <SpinAnimation /> :
        <>
          {showRatingCard &&
            <Rating
              setShowRatingCard={setShowRatingCard}
              ratingData={ratingData}
              setRatingData={setRatingData}
              submitEmployeeRating={submitEmployeeRating}
            />
          }
          <EvaluationHeadingSection>
            <span className="heading">
              <span onClick={() => navigate(-1)}>
                <ArrowDownSVG />
              </span>
              {t("My_Team_Overview")}
            </span>
          </EvaluationHeadingSection>

          <OuterContainer>
            <OuterHeader>
              <h6>{t("Feedback_Received")}</h6>
              <p>The Following Feedbacks have been Received for {`${state.firstName} ${state.lastName}`} </p>
            </OuterHeader>
            <TabBar>
              <Tab active={activeTab === "all"} onClick={() => { setActiveTab("all") }}>
                {t("All_Responses")}
              </Tab>
              <Tab active={activeTab === "self"} onClick={() => { setActiveTab("self"), fetchSelfEvaluation() }}>
                {t("Self_Evaluation")}
              </Tab>
              <Tab
                active={activeTab === "rating"}
                onClick={() => { setActiveTab("rating"), fetchEmployeeRating() }}
              >
                {t("Overall_Rating")}
              </Tab>
            </TabBar>
            <Container>

              <ReceiverRow>
                <ReceiverInfo>
                  <ReceiverLabel>{t("Feedback_Receiver_Name")}</ReceiverLabel>
                  <NameBox>{`${state.firstName} ${state.lastName}`}</NameBox>
                </ReceiverInfo>
                {activeTab === "all" && <CycleSelectContainer>
                  <ReceiverInfo>
                    <CycleLabel htmlFor="feedbackFormSelect">{t("Select_Feedback_Form")}</CycleLabel>
                    <DropdownMenu
                      label={
                        forms.length === 0
                          ? "No forms available"
                          : "Select form"
                      }
                      name="feedbackFormSelect"
                      id="feedbackFormSelect"
                      value={selectedCycleId || ""}
                      required
                      className="largeContainerExp"
                      disabled={forms.length === 0}
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
                          setSelectedCycleId(selectedValue);
                          handleCycleSelect(selectedValue);
                        }
                      }}
                    />
                  </ReceiverInfo>
                </CycleSelectContainer>}

                {activeTab === "rating" && (
                  <ProvideRatingButton disabled={!!rating}
                    onClick={() => !rating && setShowRatingCard(true)}>
                    <WriteSVG />
                    {t("Provide_Rating")}
                  </ProvideRatingButton>
                )}

              </ReceiverRow>

              <Content>
                {activeTab === "all" && (
                  <>{isLoadingAllResponses ? <SpinAnimation /> : <>
                    {(groupedResponse?.questions?.length ?? 0) > 0 ? <FeedbackHeaderRow>
                      <HideNamesToggle>
                        <SwitchLabel>
                          <StyledSwitch
                            type="checkbox"
                            checked={hideNames}
                            onChange={(e) => setHideNames(e.target.checked)}
                          />
                          <Slider />
                        </SwitchLabel>
                        <span>{t("Hide_feedback_provider_names")}</span>
                      </HideNamesToggle>

                      <QuestionProgress>
                        <NavButton
                          disabled={
                            !groupedResponse ||
                            currentQuestionIndex === 0
                          }
                          onClick={() => setCurrentQuestionIndex((i) => i - 1)}
                        >
                          <span className="arrow right"><ArrowDownSVG /></span>
                        </NavButton>

                        {" Question - "}
                        {groupedResponse ? currentQuestionIndex + 1 : 0}
                        {" / "}
                        {groupedResponse ? groupedResponse.questions.length : 0}
                        {" "}

                        <NavButton
                          disabled={
                            !groupedResponse ||
                            currentQuestionIndex === groupedResponse.questions.length - 1
                          }
                          onClick={() => setCurrentQuestionIndex((i) => i + 1)}
                        >
                          <span className="arrow left"><ArrowDownSVG /></span>
                        </NavButton>
                      </QuestionProgress>

                    </FeedbackHeaderRow> : <></>}

                    {(() => {
                      if (!groupedResponse || !groupedResponse.questions?.length) {
                        return <Placeholder>{t("No_feedback_responses_available.")}</Placeholder>;
                      }

                      const q = groupedResponse.questions[currentQuestionIndex];
                      return (
                        <>
                          <QuestionBlock key={q.questionId}>
                            <QuestionHeader>
                              <QuestionText>{`${currentQuestionIndex + 1}. ${q.questionId}`}</QuestionText>
                            </QuestionHeader>
                            {q.description && <QuestionDesc>{q.description}</QuestionDesc>}
                            <ResponsesContainer>
                              {q.responses.map((r, index) => (
                                <React.Fragment key={index}>
                                  <ResponseHeader>
                                    <div>Response {index + 1}</div>
                                    <div>
                                      {!hideNames && (
                                        <AuthorInfo>
                                          <MyProfileSVG props={{ isActive: false }} />
                                          {r.reviewerId}
                                        </AuthorInfo>
                                      )}
                                    </div>
                                  </ResponseHeader>
                                  <ResponseInnerBox>{r.answer}</ResponseInnerBox>
                                </React.Fragment>
                              ))}
                            </ResponsesContainer>
                          </QuestionBlock>
                        </>
                      );
                    })()}
                  </>}
                  </>
                )}

                {activeTab === "self" && (
                  <>{isLoadingSelf ? <SpinAnimation /> : <>
                    {(() => {
                      return (
                        <>
                          <QuestionBlock >
                            <ResponsesContainer>
                              <ResponseHeader>
                                <div>Response</div>
                              </ResponseHeader>
                              <ResponseInnerBox>
                                {selfEvaluation && selfEvaluation[0]?.responses?.[0]?.answer
                                  ? selfEvaluation[0].responses[0].answer
                                  : "No response provided."}
                              </ResponseInnerBox>
                            </ResponsesContainer>

                          </QuestionBlock>

                        </>
                      );
                    })()}
                  </>}
                  </>
                )}
                {activeTab === "rating" && (
                  <>{isLoadingRating ? <SpinAnimation /> : <>
                    {overallRating ? (
                      <ResponsesContainer2>
                        <RatingBox>
                          <RatingHeader>
                            <RatingIcon>
                              <OverallRatingStar />
                            </RatingIcon>
                            <RatingValue>
                              {overallRating.rating} <span>(Overall Rating)</span>
                            </RatingValue>
                          </RatingHeader>

                          <RatingText>{overallRating.comments}</RatingText>
                        </RatingBox>
                      </ResponsesContainer2>
                    ) : (
                      <Placeholder>
                        Overall Rating has not been submitted yet!
                      </Placeholder>
                    )}
                  </>}
                  </>
                )}
              </Content>
            </Container>
          </OuterContainer>
        </>}
      {toast && (
        <ToastMessage
          messageType={toast.type}
          messageBody={toast.message}
          messageHeading={toast.head}
          handleClose={() => setToast(null)}
        />
      )}

    </>
  );
};

export default EvaluationOverview;