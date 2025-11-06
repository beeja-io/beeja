import { useState } from "react";
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
};

const Rating = ({
    setShowRatingCard,
}:RatingProps) => {
    const [rating, setRating] = useState<number | "">("");
    const [comments, setComments] = useState("");

    const handleCancel = () => {
        setShowRatingCard(false);
    };

    const handleSubmit = () => {
        alert(`Rating: ${rating}\nComments: ${comments}`);
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

    const starFills = getStarFills(Number(rating) || 0);

    return (
        <AppContainer>
            <Overlay>
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
                            value={rating}
                            onChange={(e) => {
                                const val = parseFloat(e.target.value);
                                if (val >= 0 && val <= 5) setRating(val);
                                else if (e.target.value === "") setRating("");
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
                            value={comments}
                            onChange={(e) => setComments(e.target.value)}
                        />
                    </FormSection>

                    <ButtonRow>
                        <ResetButton onClick={handleCancel}>Cancel</ResetButton>
                        <SubmitButton
                            disabled={rating === ""}
                            onClick={handleSubmit}
                        >
                            Submit
                        </SubmitButton>
                    </ButtonRow>
                </ModalCard>
            </Overlay>
        </AppContainer>
    );
}

export default Rating;