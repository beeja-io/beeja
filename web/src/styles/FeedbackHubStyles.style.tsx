import styled from 'styled-components';
export const TabHeading = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const Tabs = styled.div`
  display: flex;
  gap: 60px;
`;

export const StyledDiv = styled.div`
  padding: 24px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
`;

export const Tab = styled.div<{ active: boolean }>`
  flex: 1;
  text-align: center;
  padding: 10px 16px;
  cursor: pointer;
  white-space: nowrap;
  border-bottom: 2px solid
    ${(props) => (props.active ? '#005792' : 'transparent')};
  color: ${(props) =>
    props.active
      ? '#005792'
      : '${(props) => props.theme.colors.blackColors.white6}'};
  font-weight: ${(props) => (props.active ? '600' : '500')};
  &:hover {
    color: #005792;
  }
  .badge {
    background-color: #005792;
    color: #fff;
    border-radius: 50%;
    width: 22px;
    height: 22px;
    font-size: 12px;
    font-weight: 700;
    line-height: 22px;
    text-align: center;
    display: inline-block;
    margin-left: 7px;
  }
`;

export const TabContent = styled.div`
  overflow-x: auto;
  margin-top: 15px;
`;

export const ListContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 28px;
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
`;

export const ListRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 130px;
  padding-left: 20px;
`;

export const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const UserText = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Name = styled.div`
  font-size: 16px;
  font-weight: 600;
  color: ${(props) => props.theme.colors.blackColors.black7};
`;

export const Role = styled.div`
  font-size: 14px;
  color: #6b7280;
`;

export const ProvideButton = styled.button`
  background-color: #005691;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  width: 140px;

  &:hover {
    background-color: #00406d;
  }
  &:disabled {
    background: #ffffff;
    color: #aaaaaa;
    border: 2px solid #aaaaaa;
    cursor: not-allowed;
  }
`;

export const Title = styled.h2`
  font-size: 14px;
  color: ${(props) => props.theme.colors.blackColors.black7};
  font-weight: 600;
  margin-bottom: 18px;
  line-height: 1.6;
`;

export const RequiredStar = styled.span`
  color: #eb5757;
  margin-left: 2px;
  font-size: 14px;
`;

export const TextArea = styled.textarea`
  width: 100%;
  height: 333px;
  border-radius: 8px;
  border: 0.86px solid rgba(224, 224, 224, 0.16);
  padding: 14px 16px;
  font-size: 0.95rem;
  line-height: 1.5;
  resize: none;
  outline: none;
  color: ${(props) => props.theme.colors.blackColors.black7};
  transition: all 0.2s ease-in-out;
  background: ${(props) => props.theme.colors.grayColors.gray6};

  &:focus {
    border: 0.86px solid #005792;
  }

  &::placeholder {
    color: #a0aec0;
  }
`;

export const SubmittedContainer = styled.div`
  background: ${(props) => props.theme.colors.grayColors.gray6};
  border-radius: 8px;
  padding: 50px 20px;
  text-align: center;
  font-size: 16px;

  h3 {
    font-family: Nunito;
    font-weight: 600;
    font-style: SemiBold;
    font-size: 20px;
    leading-trim: NONE;
    line-height: 200%;
    letter-spacing: 0px;
    text-align: center;
    vertical-align: middle;
    color: ${(props) => props.theme.colors.blackColors.black7};
  }

  p {
    font-family: Nunito;
    font-weight: 400;
    font-style: Regular;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 200%;
    letter-spacing: 0px;
    text-align: center;
    vertical-align: middle;
    color: ${(props) => props.theme.colors.grayColors.gray7};
  }
`;

export const Note = styled.p`
  font-size: 12px;
  color: #818181;
  margin-top: 10px;
  gap: 10px;
  span {
    margin: 0px 6px 0 0;
    vertical-align: middle;
  }
`;

export const ReadOnlyInput = styled.input`
  width: 40%;
  height: 54px;
  padding: 10px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #f9f9f9;
  color: #555;
  font-size: 15px;
  font-family: 'Nunito', sans-serif;
  outline: none;
  cursor: not-allowed;
  margin-top: 8px;

  &:focus {
    border-color: #007bff;
  }
`;

export const AnswerField = styled.textarea<{ isEmpty: boolean }>`
  width: 100%;
  box-sizing: border-box;
  padding: 12px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
  font-size: 15px;
  font-family: 'Nunito', sans-serif;
  color: #687588;
  resize: none;
  margin-top: 8px;
  white-space: pre-wrap;
  overflow-y: auto;
  overflow-x: hidden;
  line-height: 20px;
  min-height: 66px;
  max-height: 132px;

  &:focus {
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.1);
    outline: none;
  }

  &::placeholder {
    color: #999;
  }
`;

export const RatingBox = styled.div`
  background: ${(props) => props.theme.colors.blackColors.white6};
  border: 1px solid #e2e8f0;
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
  color: ${(props) => props.theme.colors.blackColors.black7};
  margin: 0;
`;

export const Placeholder = styled.div`
  background: #fff;
  border: 1px dashed #e5e8ec;
  border-radius: 8px;
  padding: 18px 20px;
  font-size: 14px;
  color: #687588;
  text-align: center;
  margin-top: 16px;
`;
