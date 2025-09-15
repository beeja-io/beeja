import styled from 'styled-components';

export const ResourceListContainer = styled.div`
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

export const ResourceCard = styled.div`
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 300px;
`;

export const ResourceName = styled.strong`
  font-size: 14px;
  color: #333;
`;

export const ResourceAvailability = styled.small`
  font-size: 12px;
  color: #777;
`;

export const ResourceAllocationRow = styled.div`
  display: flex;
  align-items: center;
  height: 56px;
  margin-bottom: 20px;
  max-width: 994px;
  width: 100%;
  gap: 20px;
`;
export const TextInput = styled.input`
  outline: none;
  border-radius: 10px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
  height: 54px;
  width: 100%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};

  &.largeInput {
    width: 491px;
  }

  &.disabledBgWhite {
    background-color: white;
  }
  &:disabled,
  &.disabled {
    background-color: ${(props) => props.theme.colors.grayColors.gray6};
    cursor: not-allowed;
    color: ${(props) => props.theme.colors.grayColors.gray11};
  }
  &.grayText {
    color: #8b8b8b;
  }
`;

export const StyledResourceWrapper = styled.div`
  max-width: 630px;
  margin-right: 20px;
  width: 100%;
  height: 56px;
  min-width: 0;
  .react-select__control {
    height: 56px;
    border-radius: 10px;
    border: 1px solid #ccc;
    box-shadow: none;
    width: 100%;
    &:hover {
      border-color: #999;
    }
  }
  .react-select-container {
    width: 100%;
    height: 100%;
  }
  .react-select__value-container {
    height: 56px;
    width: 546px;
    padding: 0 12px;
  }
  .react-select__indicators {
    height: 56px;
  }
`;

export const AvailabilityContainer = styled.div`
  display: flex;
  align-items: center;
  width: 300px;
  height: 56px;
  border: 1px solid #ccc;
  border-radius: 10px;
  margin-right: 20px;
`;

export const AvailabilityInput = styled.input`
  flex-grow: 1;
  height: 100%;
  padding: 8px;
  color: ${(props) => props.theme.colors.blackColors.black7};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};
  border-radius: 10px 0 0 10px;
  border: none;
  outline: none;
`;

export const PercentageSign = styled.span`
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${(props) => props.theme.colors.blackColors.black4};
  font-size: 16px;
  font-weight: bold;
  background: rgba(242, 242, 242, 0.95);
  height: 100%;
  width: 40px;
`;

export const SaveButton = styled.button`
  height: 56px;
  max-width: 93px;
  width: 100%;
  padding: 0 16px;
  background-color: #0056a6;
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  margin-left: 10px;
`;

export const ResourceBlock = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
`;

export const ResourceLabel = styled.label`
  color: ${(props) => props.theme.colors.blackColors.black7};
  font-family: Nunito;
  font-weight: 600;
  font-style: SemiBold;
  font-size: 14px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;

  &.ManageResource {
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    border-radius: 10px;
    height: 54px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 60%;
    span {
      margin: 30px;
    }
  }
  .arrow {
    font-size: 14px;
    transition: transform 0.2s ease;
    display: inline-block;
    margin-left: 8px;
    display: inline-block;
    transition: transform 0.2s ease;
    transform: rotate(-90deg);
  }
  .arrow.open {
    transform: rotate(90deg);
  }
`;

export const NameBubbleListContainer = styled.div`
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 10px;
  width: 100%;
  flex-wrap: wrap;
  &.manageResourceList {
    border: none;
    width: 60%;
  }
`;

export const NameBubble = styled.div`
  padding: 6px 12px;
  color: ${(props) => props.theme.colors.blackColors.black7};
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 95%;
  height: 32px;
  border-bottom-width: 1px;
  border-bottom: 1px solid ${(props) => props.theme.colors.blackColors.white2};

  .percentageAvailability {
    display: flex;
    gap: 10px;
  }
  .availability {
    display: flex;
    align-items: center;
    font-family: Nunito;
    font-weight: 500px;
    font-style: Medium;
    font-size: 10px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    color: ${(props) => props.theme.colors.blackColors.black7};
    border: 1px solid ${(props) => props.theme.colors.grayColors.gray6};
    border-radius: 10px;
    border-width: 1px;
    padding: 5px 14px 5px 14px;
    gap: 10px;
  }
  .remove-btn {
    margin-left: 4px;
    background: transparent;
    border: none;
    font-size: 14px;
    cursor: pointer;
    color: #005792;
    transition: color 0.2s ease;

    &:hover {
      color: red;
    }
  }
`;

export const ManageAllocationContainer = styled.div`
  margin-top: 24px;
  padding: 16px;
  border-radius: 10px;
  background: #f9f9f9;
  border: 1px solid #eee;
`;

export const ManageHeader = styled.div`
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 12px;
`;

export const AllocatedRow = styled.div`
  display: flex;
  align-items: center;
  padding: 10px 0;
  gap: 12px;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
`;

export const InitialCircle = styled.div`
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f0f0;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
`;

export const AllocatedInfo = styled.div`
  display: flex;
  flex-direction: column;
  flex: 1;

  .name {
    font-weight: 500;
    font-size: 14px;
    color: #222;
  }

  .availability {
    font-size: 12px;
    color: #777;
  }
`;

export const AllocatedValue = styled.div`
  background-color: #f3f3f3;
  padding: 6px 10px;
  border-radius: 8px;
  font-size: 13px;
  color: #333;
`;

export const RemoveButton = styled.div`
  font-size: 20px;
  color: #888;
  cursor: pointer;

  &:hover {
    color: #ff4d4f;
  }
`;

export const RightSectionDiv = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-size: 16px;
  line-height: 160%;
  letter-spacing: 0.2px;
  width: auto;
  height: 424px;
  gap: 16px;
  padding: 16px;
  border: 1px solid ${(props) => props.theme.colors.grayColors.gray10};
  color: ${(props) => props.theme.colors.blackColors.black7};
  background: ${(props) => props.theme.colors.blackColors.white6};
  align-items: flex-start;
  justify-content: flex-start;
  word-wrap: break-word;
`;

export const ProjectSeactionHeading = styled.div`
  font-family: Nunito;
  font-weight: 700;
  font-style: Bold;
  font-size: 15px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  vertical-align: middle;
`;

export const RightSubSectionDiv = styled.div`
  margin-top: 40px;
`;

export const RightSectionHeading = styled.div`
  font-family: Nunito;
  font-weight: 500;
  font-style: Medium;
  font-size: 16px;
  leading-trim: NONE;
  line-height: 160%;
  letter-spacing: 0px;
  color: ${(props) => props.theme.colors.blackColors.black7};
  margin-bottom: 20px;
`;

export const ClientInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 15px;
`;

export const IconWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 16px;
  svg {
    width: 19px;
    height: 16px;
  }
`;

export const RightSection = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
`;
