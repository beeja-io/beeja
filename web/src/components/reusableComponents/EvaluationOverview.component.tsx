import React, { useState } from "react";
import {
  OuterContainer,
  Container,
  // HeaderRow,
  // TitleBlock,
  // Title,
  // Subtitle,
  TabBar,
  Tab,
  Content,
  QuestionBlock,
  QuestionHeader,
  QuestionText,
  QuestionDesc,
  Placeholder,
  // DescriptionBox,
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
  // DateDisplayContainer,

} from "../../styles/EvaluationOverview.style";

import Rating from "./Rating.component";
import { ArrowDownSVG } from '../../svgs/CommonSvgs.svs'
import { MyProfileSVG } from "../../svgs/NavBarSvgs.svg";
import { Slider, StyledSwitch, SwitchLabel } from "../../styles/InputStyles.style";
import { WriteSVG } from "../../svgs/PerformanceEvaluation.Svgs.scg";
// import { CalenderIcon} from "../../svgs/ExpenseListSvgs.svg";

type Response = {
  id: number;
  author: string;
  date: string;
  text: string;
};

type Question = {
  id: number;
  title: string;
  description?: string;
  responses: Response[];
};

const mockQuestions: Question[] = [
  {
    id: 1,
    title:
      "1. How effectively does the employee perform their job responsibilities?",
    description:
      "Consider meeting deadlines, quality of work, solving problems, collaborating with colleagues and adapting to new challenges.",
    responses: [
      {
        id: 11,
        author: "Shravya Boga",
        date: "2025-03-14",
        text:
          "Consistently meets deadlines and maintains high-quality output. Demonstrates strong problem-solving for daily blockers.",
      },
      {
        id: 12,
        author: "Teja",
        date: "2025-03-18",
        text:
          "Delivers on promises and coordinates well across teams. Could improve on documenting design decisions.",
      },
      {
        id: 13,
        author: "Swathi",
        date: "2025-03-22",
        text:
          "Very reliable and helps teammates when required. Shows initiative during sprint planning.",
      },
    ],
  },
  {
    id: 2,
    title: "2. How well does the employee communicate within the team?",
    description:
      "Look at clarity, timeliness, feedback, and ability to align stakeholders during projects.",
    responses: [
      {
        id: 21,
        author: "Anil",
        date: "2025-04-02",
        text:
          "Communicates clearly and raises issues early. Proactive in standups and cross-team syncs.",
      },
      {
        id: 22,
        author: "Maya",
        date: "2025-04-07",
        text:
          "Good at explaining technical tradeoffs. Could add more status updates for non-technical stakeholders.",
      },
    ],
  },
  {
    id: 3,
    title: "3. How does the employee demonstrate ownership and initiative?",
    description:
      "Consider taking on extra responsibilities, helping others, and driving improvements beyond assigned tasks.",
    responses: [
      {
        id: 31,
        author: "Ravi",
        date: "2025-05-01",
        text:
          "Often volunteers to own tough tasks and mentors juniors during onboarding.",
      },
      {
        id: 32,
        author: "Priya",
        date: "2025-05-09",
        text:
          "Took the lead on the CI improvement project — reduced build flakiness significantly.",
      },
      {
        id: 33,
        author: "Karthik",
        date: "2025-05-12",
        text:
          "Brings ideas to retrospective and helps implement improvements; strong ownership mindset.",
      },
    ],
  },
];

const selfResponses = {
  questions: "How effectively does the employee perform their job responsibilities?",
  response: "Brings ideas to retrospective and helps implement improvements; strong ownership mindset."
}

const EvaluationOverview: React.FC = () => {
  const [activeTab, setActiveTab] = useState<"all" | "self" | "rating">("all");
  const [hideNames, setHideNames] = useState<boolean>(false);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState<number>(0);
  const [showRatingCard, setShowRatingCard] = useState<boolean>(false);


  const feedbackReceiver = {
    name: "Bhagath",
    role: "Software Engineer II",
    period: "Jan 2025 - Jun 2025",
    description:
      "This evaluation gathers peer feedback, self-assessment and summarizes overall ratings for Bhagath's performance in the first half of 2025.",
  };
  


  return (
    <div>
      {showRatingCard && <Rating setShowRatingCard={setShowRatingCard} />}
      <EvaluationHeadingSection>
        <span className="heading">
          <span>
            <ArrowDownSVG />
          </span>
          Evaluation Overview
        </span>
      </EvaluationHeadingSection>

      <OuterContainer>
        <OuterHeader>
          <h6>Feedback Received</h6>
          <p>The Following Feedbacks have been Received for {"Bhagath"} </p>
        </OuterHeader>
        <TabBar>
          <Tab active={activeTab === "all"} onClick={() => setActiveTab("all")}>
            All Responses
          </Tab>
          <Tab active={activeTab === "self"} onClick={() => setActiveTab("self")}>
            Self Evaluation
          </Tab>
          <Tab
            active={activeTab === "rating"}
            onClick={() => setActiveTab("rating")}
          >
            Overall Rating
          </Tab>
        </TabBar>
        <Container>

          <ReceiverRow>
            <ReceiverInfo>
              <ReceiverLabel>Feedback Receiver Name</ReceiverLabel>
              <NameBox>{feedbackReceiver.name}</NameBox>
            </ReceiverInfo>

            {activeTab === "rating" && (
              <ProvideRatingButton onClick={() => setShowRatingCard(true)}>
                 <WriteSVG />
                Provide Rating
              </ProvideRatingButton>
            )}

          </ReceiverRow>



          <Content>
            {activeTab === "all" && (
              <>
                <FeedbackHeaderRow>
                  <HideNamesToggle>
                    <SwitchLabel>
                      <StyledSwitch
                        type="checkbox"
                        checked={hideNames}
                        onChange={(e) => setHideNames(e.target.checked)}
                      />
                      <Slider />
                    </SwitchLabel>
                    {/* <ToggleSwitchContainer isChecked={hideNames}>
                      <div
                        className="toggle-switch"
                        onClick={() => setHideNames((prev) => !prev)}
                      >
                        {hideNames ? <ActiveToggleSVG /> : <InactiveToggleSVG />}
                      </div>
                    </ToggleSwitchContainer> */}

                    <span>Hide feedback provider names</span>
                  </HideNamesToggle>

                  <QuestionProgress>
                    <NavButton
                      disabled={currentQuestionIndex === 0}
                      onClick={() => setCurrentQuestionIndex((i) => i - 1)}
                    >
                      <span className="arrow right"><ArrowDownSVG /></span>
                    </NavButton>
                    {" Question - "}{currentQuestionIndex + 1}{" / "}{mockQuestions.length}{" "}
                    <NavButton
                      disabled={currentQuestionIndex === mockQuestions.length - 1}
                      onClick={() => setCurrentQuestionIndex((i) => i + 1)}
                    >
                      <span className="arrow left"><ArrowDownSVG /></span>
                    </NavButton>
                  </QuestionProgress>
                </FeedbackHeaderRow>

                {(() => {
                  const q = mockQuestions[currentQuestionIndex];
                  return (
                    <>
                      <QuestionBlock key={q.id}>
                        <QuestionHeader>
                          <QuestionText>{q.title}</QuestionText>
                        </QuestionHeader>

                        {q.description && <QuestionDesc>{q.description}</QuestionDesc>}

                        <ResponsesContainer>
                          {q.responses.map((r, index) => (
                            <React.Fragment key={r.id}>
                              <ResponseHeader>
                                <div>Response {index + 1}</div>
                                <div>
                                  {!hideNames && (
                                    <>
                                      <AuthorInfo>
                                        <MyProfileSVG
                                          props={{
                                            isActive: false
                                          }}
                                        />
                                        {r.author}
                                      </AuthorInfo>
                                    </>
                                  )}
                                </div>
                              </ResponseHeader>
                              <ResponseInnerBox>{r.text}</ResponseInnerBox>
                            </React.Fragment>
                          ))}
                        </ResponsesContainer>


                      </QuestionBlock>

                    </>
                  );
                })()}

              </>
            )}

            {activeTab === "self" && (
              <>
                {(() => {
                  return (
                    <>
                      <QuestionBlock >
                        <QuestionHeader>
                          <QuestionText>{selfResponses.questions}</QuestionText>
                        </QuestionHeader>
                        {/* {q.description && <QuestionDesc>{q.description}</QuestionDesc>} */}
                        <ResponsesContainer>
                          <ResponseHeader>
                            <div>Response</div>
                          </ResponseHeader>
                          <ResponseInnerBox>{selfResponses.response ? selfResponses.response : "No response provided."}</ResponseInnerBox>
                        </ResponsesContainer>

                      </QuestionBlock>

                    </>
                  );
                })()}

              </>
            )}

            {activeTab === "rating" && (
              <Placeholder>Overall Rating has been not submitted yet!</Placeholder>
            )}
          </Content>
        </Container>
      </OuterContainer>
    </div>
  );
};

export default EvaluationOverview;
