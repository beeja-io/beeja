import styled from "styled-components";

export const CommentContainer = styled.div`
  background-color: ${(props) => props.theme.colors.grayColors.gray12};
  padding: 20px;
  border-radius: 8px;
  width: 100%;
`;

export const CommentForm = styled.form`
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray13};
  border-radius: 8px;

  h5 {
    margin-bottom: 10px;
  }
`;

export const CommentTextArea = styled.textarea`
  width: 100%;
  padding: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray13};
  border-radius: 8px;
  resize: none;
  font-size: 1rem;
  background-color: ${(props) => props.theme.colors.grayColors.gray12};
  padding: 20px;
  margin-bottom: 10px;
`;

export const CommentCard = styled.div`
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray13};
  border-radius: 8px;
`;

export const CommentBody = styled.div`
  h5 {
    margin-bottom: 10px;
  }

  p {
    margin-bottom: 10px;
  }
  .commentedDate {
    color: ${(props) => props.theme.colors.grayColors.gray11};
    font-weight: 300;
  }
`;
