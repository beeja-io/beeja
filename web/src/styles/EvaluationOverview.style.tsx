import styled from 'styled-components';

export const OuterContainer = styled.div`
  background-color: ${(p) => p.theme.colors.backgroundColors.primary};
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px ${(p) => p.theme.colors.blackColors.black6};
  margin: 20px;
`;

export const Container = styled.div`
  background: ${(p) => p.theme.colors.backgroundColors.primary};
  border-radius: 12px;
  box-shadow: 0 4px 10px ${(p) => p.theme.colors.blackColors.black6};
  padding: 24px 32px;
  border: 1px solid ${(p) => p.theme.colors.grayColors.grayscale300};
`;

export const TabBar = styled.div`
  display: flex;
  gap: 8px;
  border-bottom: 2px solid ${(p) => p.theme.colors.grayColors.gray10};
  margin-bottom: 18px;
`;

export const Tab = styled.button<{ active?: boolean }>`
  background: none;
  border: none;
  outline: none;
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: ${(p) =>
    p.active
      ? "#005792"
      : p.theme.colors.grayColors.gray7};
  border-bottom: ${(p) =>
    p.active
      ? `2px solid ${"#005792"}`
      : "3px solid transparent"};

  cursor: pointer;
  transition: all 0.2s ease-in-out;

  &:hover {
    color: #005792;
  }
`;

export const Content = styled.div`
  margin-top: 6px;
`;

export const QuestionBlock = styled.div`
  margin-bottom: 22px;
`;

export const QuestionHeader = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
`;

export const QuestionText = styled.h3`
  font-size: 16px;
  margin: 0;
  font-weight: 500;
  color: ${(p) => p.theme.colors.brandColors.primary};
`;

export const QuestionDesc = styled.p`
  font-size: 13px;
  color: ${(p) => p.theme.colors.grayColors.gray7};
  margin: 6px 0 12px 0;
  line-height: 1.45;
`;

export const Placeholder = styled.div`
  min-height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${(p) => p.theme.colors.grayColors.gray7};
  font-size: 15px;
  border-radius: 8px;
  padding: 20px;
`;

export const NameBox = styled.div`
  background: ${(p) => p.theme.colors.grayColors.gray6};
  border: 1px solid ${(p) => p.theme.colors.grayColors.gray5};
  border-radius: 8px;
  padding: 10px 14px;
  width: fit-content;
  min-width: 350px;
  height: 55px;
  font-weight: 500;
  color: ${(p) => p.theme.colors.grayColors.gray7};
  font-size: 14px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
`;

export const NavButton = styled.button<{ primary?: boolean }>`
  color: ${(p) =>
    p.primary
      ? p.theme.colors.blackColors.white
      : p.theme.colors.blackColors.black1};
  border: none;
  border-radius: 6px;
  padding: 6px;
  font-size: 16px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: transparent;
  .arrow {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    line-height: 0;
  }
  svg {
    width: 18px;
    height: 18px;
    display: block;
    transition: transform 0.18s ease;
  }
  .arrow.left svg {
    transform: rotate(270deg);
  }
  .arrow.right svg {
    transform: rotate(90deg);
  }
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  &:hover:not(:disabled) {
    background: ${(p) =>
      p.primary
        ? p.theme.colors.brandColors.primary
        : p.theme.colors.grayColors.gray5};
  }
  &:hover:not(:disabled) .arrow.left svg {
    transform: rotate(270deg) scale(1.08);
  }
  &:hover:not(:disabled) .arrow.right svg {
    transform: rotate(90deg) scale(1.08);
  }
`;

export const ResponsesContainer = styled.div`
  background: ${(p) => p.theme.colors.brandColors.primary + '12'};
  border: none;
  border-radius: 10px;
  padding: 16px 18px;
  margin-top: 14px;
  max-height: 360px;
  overflow-y: auto;

  scrollbar-width: thin;
  scrollbar-color: ${(props) => props.theme.colors.grayColors.gray8} transparent;
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background-color: ${(props) => props.theme.colors.grayColors.gray8};
    border-radius: 4px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
`;

export const ResponseHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 400;
  font-size: 14px;
  color: #687588;
  margin-bottom: 8px;
  margin-top: 12px;
`;

export const ResponseInnerBox = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: none;
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 14px;
  color: ${(p) => p.theme.colors.grayColors.gray7};
  line-height: 1.5;
  margin-bottom: 8px;
`;

export const FeedbackHeaderRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
  label {
    font-size: 13px;
    color: #555;
  }
`;

export const QuestionProgress = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 500;
  color: ${(props) => props.theme.colors.blackColors.black6} .progressText {
    display: flex;
    align-items: center;
    gap: 6px;
  }
`;

export const OuterHeader = styled.div`
  width: 100%;
  max-width: 900px;
  margin-bottom: 20px;
  h6 {
    font-size: 16px;
    font-weight: 700;
    color: ${(p) => p.theme.colors.blackColors.black4};
    margin-bottom: 6px;
    text-align: left;
  }
  p {
    font-size: 12px;
    color: ${(props) => props.theme.colors.grayColors.gray11};
    font-weight: 400;
    margin-bottom: 20px;
    text-align: left;
  }
`;

export const ReceiverRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;
`;

export const ReceiverInfo = styled.div`
  display: flex;
  flex-direction: column;
`;

export const ReceiverLabel = styled.h6`
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: ${(p) => p.theme.colors.blackColors.black4};
`;

export const ProvideRatingButton = styled.button`
  display: inline-flex;     
  align-items: center;          
  gap: 8px;                        
  background-color: #005792;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  height: fit-content;
  transition: background-color 0.2s ease;

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    background-color: #004494;
  }

  &:disabled {
    background-color: #a0a0a0;
    cursor: not-allowed;
    opacity: 0.7;
  }
`;

export const HideNamesToggle = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  user-select: none;
  min-height: 32px;
  // .toggle-switch {
  //   display: flex;
  //   align-items: center;
  //   justify-content: center;
  //   height: 24px;
  //   width: 50px;
  //   transition: all 0.3s ease;
  // }
  svg {
    width: 46px;
    height: 24px;
    display: block;
    cursor: pointer;
  }
  span {
    font-size: 14px;
    color: ${(p) => p.theme.colors.blackColors.black1};
    transition: color 0.2s ease;
  }
`;
export const EvaluationHeadingSection = styled.section`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin: 10px;
  margin-top: 20px;
  .heading {
    display: flex;
    align-items: center;
    font-size: 24px;
    font-style: normal;
    font-weight: 700;
    color: ${(props) => props.theme.colors.blackColors.black1};
    span {
      display: inline-block;
      transform: rotate(90deg);
      margin-right: 3px;
      cursor: pointer;
      transition: transform 0.2s ease;
      svg {
        width: 22px;
        height: 22px;
      }
      &:hover {
        transform: rotate(90deg) scale(1.1);
      }
    }
  }
`;

export const AuthorInfo = styled.span`
  display: inline-flex;
  align-items: center;
  gap: 6px;
  svg {
    width: 16px;
    height: 16px;
  }
`;

export const CycleSelectContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const CycleLabel = styled.label`
  font-weight: 500;
  color: ${(p) => p.theme.colors.blackColors.black4};
  font-size: 14px;
  margin-bottom: 8px;
`;

export const ResponsesContainer2 = styled.div`
  background: ${(p) => p.theme.colors.brandColors.primary + '12'};
  border: none;
  border-radius: 10px;
  padding: 16px 18px;
  margin-top: 14px;
`;
export const RatingBox = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: none;
  border-radius: 8px;
  padding: 20px 24px;
  box-shadow: 0px 1px 3px rgba(16, 24, 40, 0.05);
`;
export const RatingHeader = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 8px;
`;
export const RatingIcon = styled.div`
  margin-right: 8px;
  display: flex;
  align-items: center;
`;

export const RatingValue = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0.2px;
  color: #005792;

  span {
    margin-left: 4px;
  }
`;

export const RatingText = styled.p`
  font-family: Nunito;
  font-weight: 500;
  font-style: Medium;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
  color: ${(p) => p.theme.colors.grayColors.gray7};
  margin: 0;
`;

export const OverallRatingStar = () => {
  return (
    <svg
      width="16"
      height="15"
      viewBox="0 0 16 15"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M7.9999 12.3934L3.74477 14.9234C3.61973 14.9842 3.50368 15.0088 3.39662 14.9972C3.29034 14.9849 3.18679 14.9488 3.08598 14.8887C2.98439 14.8272 2.9078 14.7402 2.85623 14.6279C2.80465 14.5156 2.79996 14.3928 2.84216 14.2597L3.97452 9.5161L0.229296 6.31906C0.123797 6.23442 0.0542456 6.13324 0.0206421 6.01552C-0.0129614 5.89779 -0.00553727 5.78507 0.0429142 5.67734C0.0913657 5.56962 0.155837 5.48114 0.236329 5.41189C0.317603 5.34494 0.42701 5.29955 0.56455 5.27569L5.5066 4.85096L7.43372 0.358946C7.48686 0.231988 7.56345 0.140424 7.66348 0.0842541C7.7635 0.0280847 7.87565 0 7.9999 0C8.12416 0 8.23669 0.0280847 8.3375 0.0842541C8.43831 0.140424 8.5145 0.231988 8.56608 0.358946L10.4932 4.85096L15.4341 5.27569C15.5724 5.29878 15.6822 5.34456 15.7635 5.41304C15.8447 5.48075 15.9096 5.56885 15.9581 5.67734C16.0057 5.78507 16.0128 5.89779 15.9792 6.01552C15.9456 6.13324 15.876 6.23442 15.7705 6.31906L12.0253 9.5161L13.1576 14.2597C13.2014 14.3913 13.1971 14.5136 13.1447 14.6267C13.0924 14.7399 13.0154 14.8268 12.9138 14.8876C12.8138 14.9491 12.7102 14.9857 12.6032 14.9972C12.4969 15.0088 12.3812 14.9842 12.2562 14.9234L7.9999 12.3934Z"
        fill="#005792"
      />
    </svg>
  );
};
export const WriteSVG = () => {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 20 20"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M6.43073 13.7167L7.89823 13.2934L16.7341 4.35171C16.8038 4.28029 16.8426 4.18432 16.8421 4.08453C16.8416 3.98474 16.8019 3.88913 16.7316 3.81837L16.2024 3.28337C16.1682 3.24824 16.1274 3.22026 16.0822 3.20109C16.0371 3.18191 15.9886 3.17191 15.9396 3.17168C15.8906 3.17145 15.842 3.18098 15.7967 3.19973C15.7514 3.21848 15.7103 3.24606 15.6757 3.28087L6.86323 12.1992L6.43073 13.7167ZM17.2499 2.22337L17.7791 2.7592C18.5091 3.49837 18.5157 4.69087 17.7924 5.42254L8.6874 14.6375L5.55073 15.5409C5.4559 15.5675 5.35676 15.5752 5.25895 15.5636C5.16114 15.5519 5.06659 15.5211 4.9807 15.4729C4.8948 15.4247 4.81924 15.36 4.75834 15.2826C4.69743 15.2052 4.65237 15.1165 4.62573 15.0217C4.58486 14.8842 4.58428 14.7379 4.62406 14.6L5.53656 11.4L14.6174 2.2092C14.7901 2.03534 14.9956 1.89759 15.2221 1.804C15.4486 1.71041 15.6914 1.66285 15.9364 1.66409C16.1815 1.66533 16.4238 1.71535 16.6493 1.81123C16.8748 1.90711 17.079 2.04776 17.2499 2.22337ZM7.65073 3.1842C8.06406 3.1842 8.39906 3.52337 8.39906 3.9417C8.39972 4.0406 8.38088 4.13865 8.34362 4.23025C8.30636 4.32185 8.25141 4.40521 8.18191 4.47557C8.11241 4.54592 8.02972 4.60188 7.93858 4.64025C7.84743 4.67863 7.74962 4.69866 7.65073 4.69921H4.6574C3.83073 4.69921 3.16073 5.37754 3.16073 6.21337V15.3017C3.16073 16.1384 3.83073 16.8167 4.6574 16.8167H13.6374C14.4641 16.8167 15.1349 16.1384 15.1349 15.3017V12.2725C15.1349 11.8542 15.4699 11.515 15.8832 11.515C16.2966 11.515 16.6316 11.8542 16.6316 12.2734V15.3017C16.6316 16.975 15.2907 18.3317 13.6374 18.3317H4.6574C3.00406 18.3317 1.66406 16.975 1.66406 15.3017V6.21337C1.66406 4.54087 3.00406 3.1842 4.6574 3.1842H7.65073Z"
        fill="white"
      />
    </svg>
  );
};
