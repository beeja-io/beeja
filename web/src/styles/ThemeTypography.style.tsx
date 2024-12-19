import { styled } from 'styled-components';

interface FontNameProps {
  fontFamily?: string;
  fontSize?: string;
}

export const FontName = styled.div<{ fontNameProps: FontNameProps }>`
  display: flex;
  width: 106px;
  align-items: flex-start;
  gap: 2px;
  color: ${(props) => props.theme.colors.blackColors.black1};
  font-family: ${({ fontNameProps }) =>
    fontNameProps.fontFamily || 'var(--font-family-primary)'};
  // font-size: ${(props) =>
    props.fontNameProps.fontSize || 'font-size: var(--font-size-primary);'};
  font-size: ${(props) => props.fontNameProps.fontSize || '14px'};
  font-style: normal;
  font-weight: 500;
  line-height: 160%;
`;
export const OptionElement = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  cursor: pointer;
  margin-bottom: 31px;
`;
export const ThemeTypographyHead = styled.div`
  padding: 5px;
`;

export const WrapperInnerContainer = styled.div`
  display: flex;
  width: 491px;
  flex-direction: column;
  flex-wrap: wrap;
`;
export const WrapperFontInnerContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 430px;
`;
export const Wrapper = styled.div`
  display: flex;
  align-items: flex-start;
  margin: 24px 0px 0px 0px;
`;
export const FontStyleWrapper = styled.div`
  display: flex;
  align-items: flex-start;
`;
export const WrapperContainer = styled.div`
  margin: 0px 97px 0px 24px;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
`;

export const ThemeSection = styled.section`
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  display: flex;
  margin: 24px 0px;
  flex-direction: column;
  padding: 24px;

  @media (max-width: 991px) {
    max-width: 100%;
    padding: 20px;
  }
`;

export const SectionTitle = styled.h3`
  color: ${(props) => props.theme.colors.blackColors.black1};
  margin-bottom: 10px;
  @media (max-width: 991px) {
    max-width: 100%;
  }
`;

export const SectionDivider = styled.div`
  background-color: ${(props) => props.theme.colors.grayColors.grayscale300};
  margin-top: 1px;
  height: 1px;

  @media (max-width: 991px) {
    max-width: 100%;
  }
`;

export const ThemeOptions = styled.div`
  margin-top: 20px;
  width: 448px;
  max-width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
`;

export const ThemeOption = styled.div`
  display: flex;
  flex-direction: column;
  line-height: normal;
  margin-left: 0px;
  cursor: pointer;

  @media (max-width: 991px) {
    width: 100%;
  }
`;

export const ThemePreview = styled.div`
  border-radius: 10px;
  background-color: ${(props) => props.theme.colors.grayColors.gray8};
  display: flex;
  width: 100%;
  flex-grow: 1;
  flex-direction: column;
  margin: 0 auto;
  padding: 12px 9px;
`;

export const ThemeImage = styled.div`
  aspect-ratio: 1.37;
  object-fit: auto;
  object-position: center;
  width: 186px;
  align-self: center;
`;

export const ThemeDetails = styled.div`
  display: flex;
  gap: 20px;
  margin: 15px 12px 0;

  @media (max-width: 991px) {
    margin: 0 10px;
  }
`;

export const ThemeLabel = styled.span`
  color: ${(props) => props.theme.colors.blackColors.black4};
  letter-spacing: 0.2px;
  flex-grow: 1;
  flex-basis: auto;
  margin: auto 0;
  font:
    600 12px/140% Nunito,
    sans-serif;
`;

export const ActiveIndicator = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 100px;
  background: #005792;
  box-shadow: 0px 20px 40px 0px rgba(0, 0, 0, 0.15);
  width: 18px;
  height: 18px;
  padding: 0 4px;
  gap: 10px;
`;
//#ededed  #005792  #fff
export const InActiveIndicator = styled.div`
  justify-content: center;
  align-items: center;
  border-radius: 100px;
  border: 1px solid #fff;
  background: #fff;
  box-shadow: 0px 20px 40px 0px rgba(0, 0, 0, 0.15);
  display: flex;
  width: 18px;
  height: 18px;
  padding: 0 7px;
  gap: 10px;
`;
export const TypographyContainer = styled.div`
  align-self: stretch;
  border-radius: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  display: flex;
  width: 100%;
  max-width: 740px;
  flex-direction: column;
  padding: 24px 23px;
  @media (max-width: 991px) {
    padding: 0 20px;
  }
`;
