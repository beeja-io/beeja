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
  gap: 10px;
  height: 86px;
  align-items: flex-start;
  margin-bottom: 20px;
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
  width: 100%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.backgroundColors.primary};

  &.largeInput {
    width: 491px;
  }

  &.disabledBgWhite {
    background-color: white;
  }
  &.grayText {
    color: #8b8b8b;
  }
`;

export const StyledResourceWrapper = styled.div`
  width: 546px;
  height: 56px;
  .react-select__control {
    height: 56px;
    min-height: 56px;
    border-radius: 10px;
    border: 1px solid #ccc;
    box-shadow: none;
    &:hover {
      border-color: #999;
    }
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

export const AvailabilityInput = styled.input`
  width: 240px;
  height: 56px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 10px;
`;

export const SaveButton = styled.button`
  height: 56px;
  width: 120px;
  padding: 0 16px;
  background-color: #0056a6;
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
`;

export const ResourceBlock = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 16px;
  margin-left: 190px;
  margin-bottom: 30px;
  width: 900px;
`;

export const ResourceLabel = styled.label`
  font-size: 14px;
  font-weight: 500;
  color: #333;
`;

export const NameBubbleListContainer = styled.div`
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  background-color: white;
  width: 100%;
  flex-wrap: wrap;
`;

export const NameBubble = styled.div`
  padding: 6px 12px;
  border-radius: 20px;
  background-color: #f3f3f3;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
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
  border: 1px solid #f1f2f4;
  align-items: flex-start;
  justify-content: flex-start;
  word-wrap: break-word;
  background-color: #ffffff;
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
  color: rgba(17, 24, 39, 1);
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
