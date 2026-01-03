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
  Submit?: string;
  isLoading?: boolean;
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
  Submit,
  isLoading,
}) => {
  const { t } = useTranslation();
  const answerRefs = React.useRef<(HTMLTextAreaElement | null)[]>([]);
  React.useEffect(() => {
    formData.questions.forEach((_, index) => {
      const ref = answerRefs.current[index];
      if (ref) {
        ref.style.height = '0px';
        const minHeight = 66;
        const maxHeight = 106;
        const scrollHeight = Math.max(ref.scrollHeight, minHeight);
        ref.style.height =
          scrollHeight > maxHeight ? `${maxHeight}px` : `${scrollHeight}px`;
      }
    });
  }, [formData.questions]);
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
        {formData?.formDescription && (
          <>
            <Label>{t('FORM_DESCRIPTION')}</Label>
            <DescriptionBox className="preview-mode">
              {formData.formDescription}
            </DescriptionBox>
          </>
        )}
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
                    <AnswerField
                      ref={(el) => (answerRefs.current[index] = el)}
                      value={q.answer}
                      isEmpty={false}
                      readOnly
                    />
                  ) : (
                    <AnswerField isEmpty={true} disabled />
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
              {t('EDIT')}
            </Button>
            <Button
              className="submit"
              type="button"
              onClick={onConfirm}
              disabled={isLoading}
            >
              {Submit || t('SAVE')}
            </Button>
          </ButtonGroup>
        </FooterContainer>
      </PreviewCard>
    </PreviewWrapper>
  );
};

export default PreviewMode;
