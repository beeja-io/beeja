import { styled } from 'styled-components';

export const FeatureToggleContainer = styled.div`
  display: flex;
  gap: 1rem;
  padding: 20px 0;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  div {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    justify-content: center;
    align-items: flex-start;

    .innerDiv {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 0 1rem;
    }
  }
`;
