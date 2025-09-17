import React from 'react';
import {
  AnswerPlaceholder,
  ButtonGroup,
  DateRow,
  DateText,
  DescriptionBox,
  FooterContainer,
  Header,
  PreviewCard,
  PreviewWrapper,
  QuestionBlock,
  Questions,
  QuestionText,
  RequiredMark,
  Subtitle,
  Title,
} from '../../styles/CreateReviewCycleStyle.style';
import { Button } from '../../styles/CommonStyles.style';
import { useTranslation } from 'react-i18next';

type Question = {
  question: string;
  answer?: string;
  required?: boolean;
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
  onEdit: () => void;
  onConfirm: () => void;
};

const PreviewMode: React.FC<Props> = ({ formData, onEdit, onConfirm }) => {
  const { t } = useTranslation();
  return (
    <PreviewWrapper>
      <PreviewCard>
        <Header>
          <Title>
            {formData.reviewCycleName} -{' '}
            {new Date(formData.startDate || '').getFullYear()}
          </Title>
          <DateRow>
            <DateText>
              {formData.startDate} To {formData.endDate}
            </DateText>
          </DateRow>
          <Subtitle>{formData.reviewType} Form</Subtitle>
        </Header>

        <DescriptionBox>
          {formData.formDescription || 'No description provided.'}
        </DescriptionBox>

        <Questions>
          {formData.questions.map((q, index) => (
            <QuestionBlock key={index}>
              <QuestionText>
                {index + 1}. {q.question}
                {q.required && <RequiredMark>*</RequiredMark>}
              </QuestionText>
              <AnswerPlaceholder>Answer text</AnswerPlaceholder>
            </QuestionBlock>
          ))}
        </Questions>

        <FooterContainer>
          <ButtonGroup>
            <Button onClick={onEdit} type="button">
              {t('Edit')}
            </Button>
            <Button className="submit" type="button" onClick={onConfirm}>
              {t('Save & Continue')}
            </Button>
          </ButtonGroup>
        </FooterContainer>
      </PreviewCard>
    </PreviewWrapper>
  );
};

export default PreviewMode;
