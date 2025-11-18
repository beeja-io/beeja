import styled from "styled-components";

export const Title = styled.h2`
  font-size: 24px;
  font-weight: 700;
  color: ${(props) => props.theme.colors.blackColors.black1};
`;

export const TitleSection = styled.div`
  display: flex;
  align-items: center;
  gap: 3px;
  margin: 10px;
  margin-top: 20px;
  color: ${(props) => props.theme.colors.blackColors.black1};

  .arrow {
    display: inline-block;
    transform: rotate(90deg);
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
`;