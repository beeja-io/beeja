import ReactDOM from "react-dom";
import {
  AppContainer,
  Overlay,
  ModalCard,
  ModalTitle,
  FormSection,
  Label,
  Required,
  RatingInput,
  CommentsBox,
  HintStarsRow,
  HintText,
  Stars,
  SubmitButton,
  ButtonRow,
  ResetButton,
  StarWrapper,
} from "../../styles/Rating.style";

type RatingProps = {
  setShowRatingCard: React.Dispatch<React.SetStateAction<boolean>>;
  ratingData: { rating: string; comments: string };
  setRatingData: React.Dispatch<React.SetStateAction<{ rating: string; comments: string }>>;
  submitEmployeeRating: (rating: number, comments: string) => Promise<void>;
};

const Rating = ({
  setShowRatingCard,
  ratingData,
  setRatingData,
  submitEmployeeRating,
}: RatingProps) => {


  const handleCancel = () => setShowRatingCard(false);

  const handleSubmit = () => {
    submitEmployeeRating(Number(ratingData.rating), ratingData.comments);
    setShowRatingCard(false);
  };

  const getStarFills = (value: number) => {
    const stars: number[] = [];
    for (let i = 1; i <= 5; i++) {
      if (value >= i) stars.push(100);
      else if (value > i - 1) stars.push((value - (i - 1)) * 80);
      else stars.push(0);
    }
    return stars;
  };

  const starFills = getStarFills(Number(ratingData.rating) || 0);

  return ReactDOM.createPortal(
    <Overlay onClick={handleCancel}>
      <AppContainer onClick={(e) => e.stopPropagation()}>
        <ModalCard>
          <ModalTitle>Overall Rating Form</ModalTitle>

          <FormSection>
            <Label>
              Rating <Required>*</Required>
            </Label>

            <RatingInput
              type="number"
              placeholder="Enter Rating"
              step={0.1}
              min={0}
              max={5}
              value={ratingData.rating}
              onChange={(e) => {
                const val = parseFloat(e.target.value);
                if (val >= 0 && val <= 5) {
                  setRatingData((prev) => ({ ...prev, rating: String(val) }));
                } else if (e.target.value === "") {
                  setRatingData((prev) => ({ ...prev, rating: "" }));
                }
              }}
            />

            <HintStarsRow>
              <HintText>Enter rating between 0–5</HintText>
              <Stars>
                {starFills.map((fillPercent, i) => (
                  <StarWrapper key={i} fill={fillPercent} />
                ))}
              </Stars>
            </HintStarsRow>
          </FormSection>

          <FormSection>
            <Label>Comments</Label>
            <CommentsBox
              placeholder="Write Comments if any"
              value={ratingData.comments}
              onChange={(e) => setRatingData((prev) => ({ ...prev, comments: e.target.value }))}
            />
          </FormSection>

          <ButtonRow>
            <ResetButton onClick={handleCancel}>Cancel</ResetButton>
            <SubmitButton
              disabled={ratingData.rating === ""}
              onClick={handleSubmit}
            >
              Submit
            </SubmitButton>
          </ButtonRow>
        </ModalCard>
      </AppContainer>
    </Overlay>,
    document.body
  );
};

export default Rating;
