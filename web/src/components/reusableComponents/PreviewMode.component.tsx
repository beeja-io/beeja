import React from 'react';
import {
  ButtonGroup,
  DateRow,
  DateText,
  DescriptionBox,
  ErrorText,
  FooterContainer,
  Header,
  Label,
  PreviewCard,
  PreviewWrapper,
  QuestionBlock,
  QuestionDescription,
  QuestionMainText,
  QuestionNumber,
  Questions,
  QuestionText,
  RequiredMark,
  Subtitle,
  Title,
} from '../../styles/CreateReviewCycleStyle.style';
import { Button } from '../../styles/CommonStyles.style';
import { useTranslation } from 'react-i18next';
import {
  AnswerField,
  ReadOnlyInput,
} from '../../styles/FeedbackHubStyles.style';
import { CalenderIconDark } from '../../svgs/ExpenseListSvgs.svg';
import { ReviewType, ReviewTypeLabels } from './PerformanceEnums.component';

type Question = {
  question: string;
  answer?: string;
  required?: boolean;
  questionDescription?: string;
};

type FormData = {
  reviewCycleName: string;
  reviewType?: string;
  startDate?: string;
  endDate?: string;
  formDescription?: string;
  questions: Question[];
};

type Props = {
  formData: FormData;
  onEdit?: () => void;
  onConfirm?: () => Promise<void> | void;
  showAnswers?: boolean;
  feedbackReceiverName?: string;
  validationErrors?: boolean[];
};

const formatDate = (dateStr: string | Date) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: '2-digit',
    year: 'numeric',
  });
};

const PreviewMode: React.FC<Props> = ({
  formData,
  onEdit,
  onConfirm,
  showAnswers = false,
  feedbackReceiverName,
  validationErrors,
}) => {
  const { t } = useTranslation();
  return (
    <PreviewWrapper>
      <PreviewCard>
        <Header>
          <Title>{formData.reviewCycleName}</Title>
          <DateRow>
            <DateText>
              <CalenderIconDark />
              {formData.startDate ? formatDate(formData.startDate) : ''}
              <span>To </span>
              <CalenderIconDark />
              {formData.endDate ? formatDate(formData.endDate) : ''}
            </DateText>
          </DateRow>
          <Subtitle>
            {formData.reviewType
              ? `${ReviewTypeLabels[formData.reviewType as ReviewType]} Review`
              : ''}
          </Subtitle>
        </Header>
        <Label>Form Description</Label>
        <DescriptionBox className="preview-mode">
          {formData.formDescription || 'No description provided.'}
        </DescriptionBox>
        {showAnswers && feedbackReceiverName && (
          <div style={{ marginBottom: '20px' }}>
            <Label>
              Feedback Receiver Name<RequiredMark>*</RequiredMark>
            </Label>

            <ReadOnlyInput value={feedbackReceiverName} readOnly />
          </div>
        )}
        <Questions>
          {formData.questions.map((q, index) => (
            <QuestionBlock key={index} className="preview-block">
              <QuestionText>
                <QuestionNumber>{index + 1}.</QuestionNumber>
                <QuestionMainText>
                  {q.question}
                  {q.required && <RequiredMark>*</RequiredMark>}
                </QuestionMainText>
              </QuestionText>
              {q.questionDescription && (
                <QuestionDescription>
                  {q.questionDescription}
                </QuestionDescription>
              )}
              {showAnswers && (
                <>
                  {q.answer ? (
                    <ReadOnlyInput value={q.answer} readOnly />
                  ) : (
                    <AnswerField placeholder="No answer provided" disabled />
                  )}
                  {validationErrors?.[index] && (
                    <ErrorText>This field is required</ErrorText>
                  )}
                </>
              )}
            </QuestionBlock>
          ))}
        </Questions>

        <FooterContainer>
          <ButtonGroup>
            <Button onClick={onEdit} type="button">
              {t('Edit')}
            </Button>
            <Button className="submit" type="button" onClick={onConfirm}>
              {t('Save')}
            </Button>
          </ButtonGroup>
        </FooterContainer>
      </PreviewCard>
    </PreviewWrapper>
  );
};

export default PreviewMode;
