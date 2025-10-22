import styled from "styled-components";

export const OuterContainer = styled.div`
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  margin: 20px;
`;

export const Container = styled.div`
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  padding: 24px 32px;
  border: 1px solid #e6ebf2;
`;

export const HeaderRow = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 4px;
  margin-bottom: 16px;
`;

export const TitleBlock = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const Title = styled.h1`
  font-size: 20px;
  margin: 0;
  color: #005792;
  font-weight: 700;
`;

export const Subtitle = styled.span`
  font-size: 16px;
  color: #111827;
  margin-top: 4px;
  font-weight: 600;
`;

export const TabBar = styled.div`
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #eef1f5;
  margin-bottom: 18px;
  padding-bottom: 8px;
`;

export const Tab = styled.button<{ active?: boolean }>`
  background: none;
  border: none;
  outline: none;
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  color: ${(p) => (p.active ? "#0b66d1" : "#667085")};
  border-bottom: ${(p) => (p.active ? "2px solid #0b66d1" : "3px solid transparent")};
  cursor: pointer;
  transition: all 0.2s ease-in-out;

  &:hover {
    color: #0b66d1;
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
  color: #005792;
`;

export const QuestionDesc = styled.p`
  font-size: 13px;
  color: #5c6470;
  margin: 6px 0 12px 0;
  line-height: 1.45;
`;

export const Placeholder = styled.div`
  min-height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8f98a4;
  font-size: 15px;
  border: 1px dashed #e6ebf2;
  border-radius: 8px;
  padding: 20px;
`;

export const DescriptionBox = styled.div`
  background: #ffff;
  border: 1px solid #e3e6eb;
  border-radius: 10px;
  padding: 16px 20px;
  margin: 12px 0 20px 0;
  color: #111827;
  line-height: 1.5;
  font-weight: 400;
  font-size: 15px;
`;

export const NameBox = styled.div`
  background: #f9fafb;
  border: 1px solid #e5e8ec;
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 20px;
  width: fit-content;
  font-weight: 500;
  color: #687588;
  font-size: 14px;
`;

export const NavButton = styled.button<{ primary?: boolean }>`
  color: ${(p) => (p.primary ? "#fff" : "#222")};
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
    background: ${(p) => (p.primary ? "#005792" : "#e5e7eb")};
  }

  &:hover:not(:disabled) .arrow.left svg {
    transform: rotate(270deg) scale(1.08);
  }
  &:hover:not(:disabled) .arrow.right svg {
    transform: rotate(90deg) scale(1.08);
  }
`;

export const ResponsesContainer = styled.div`
  background: #f0f2f6ff;
  border: 1px solid #e2e5ea;
  border-radius: 10px;
  padding: 16px 18px;
  margin-top: 14px;
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
  background: #ffffff;
  border: 1px solid #e5e8ec;
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 14px;
  color: #687588;
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
  color: #444;

  .progressText {
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
    color: #111827;
    margin-bottom: 6px;
    text-align: left;
  }

  p {
    font-size: 12px;
    color: #818181;
    font-weight: 400;
    margin-bottom: 20px;
    text-align: left;
  }
`;

export const Title1 = styled.h2`
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #111827;
  margin: 20px;
`;

export const ReceiverRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
`;

export const ReceiverInfo = styled.div`
  display: flex;
  flex-direction: column;
`;

export const ReceiverLabel = styled.h6`
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 500;
  color: #111827;
`;

export const ProvideRatingButton = styled.button`
  background-color: #0056b3;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  height: fit-content;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: #004494;
  }
`;

export const HideNamesToggle = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  user-select: none;

  min-height: 32px;

  .toggle-switch {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 24px;
    width: 50px;
    transition: all 0.3s ease;
  }

  svg {
    width: 46px;
    height: 24px;
    display: block;
    cursor: pointer;
  }

  span {
    font-size: 14px;
    color: #333;
    margin-top: 2px;
    transition: color 0.2s ease;
  }

  &:hover span {
    color: #2a7ae4;
  }
`;
export const EvaluationHeadingSection = styled.section`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 20px 20px 0 20px;

  .heading {
    display: flex;
    align-items: center;
    font-size: 24px;
    font-style: normal;
    font-weight: 550;
    color: #1a202c;

    span {
      display: inline-block;
      transform: rotate(90deg);
      margin-right: 8px;
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

export const DateDisplayContainer = styled.div`
  font-weight: 400;
  color: #687588;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  margin-bottom: 7px;

  .date-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;

    svg {
      width: 16px;
      height: 16px;
    }
  }

  .separator {
    color: #9ca3af;
    font-size: 14px;
  }
`;

