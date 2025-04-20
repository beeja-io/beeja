import { Button } from "web-kit-components";
import {
  CommentContainer,
  CommentForm,
  CommentTextArea,
  CommentCard,
  CommentBody,
} from "../../styles/CommentsStyles.style";
import { IApplicantComment } from "../../entities/ApplicantEntity";
import { formatDateDDMMYYYYHHMM } from "../../utils/dateFormatter";
import { useTranslation } from "react-i18next";
import { useState } from "react";

type CommentSectionProps = {
  comments: IApplicantComment[];
  handleSubmitComment: (comment: string) => void;
};
const CommentsSection = (props: CommentSectionProps) => {
  const { t } = useTranslation();
  const [message, setMessage] = useState<string>("");
  const handleSetMessage = (message: string) => {
    setMessage(message);
  };
  return (
    <CommentContainer>
      <h3>{t("COMMENTS")}</h3>
      <CommentForm>
        <h5>{t("LEAVE_A_COMMENT")}</h5>
        <CommentTextArea
          name="comment"
          value={message}
          onChange={(e) => handleSetMessage(e.target.value)}
          placeholder={t("ENTER_YOUR_COMMENTS_HERE")}
        />
        <Button
          className="normal"
          type="submit"
          onClick={(e) => {
            e.preventDefault();
            props.handleSubmitComment(message);
            setMessage("");
          }}
        >
          {t("COMMENT")}
        </Button>
      </CommentForm>

      {[...props.comments].reverse().map((comment: IApplicantComment) => (
        <CommentCard key={comment.id}>
          <CommentBody>
            <h5>
              {comment.commentedByName} &nbsp; &nbsp;
              <span className="commentedDate">
                [{formatDateDDMMYYYYHHMM(comment.createdAt)}]
              </span>
            </h5>
            <p>{comment.message}</p>
          </CommentBody>
        </CommentCard>
      ))}
    </CommentContainer>
  );
};

export default CommentsSection;
