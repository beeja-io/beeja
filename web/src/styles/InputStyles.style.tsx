import styled from 'styled-components';

export const TextInput = styled.input<{ isError?: boolean }>`
  outline: none;
  border-radius: 10px;
  border: 1px solid
    ${(props) =>
      props.isError ? 'red' : props.theme.colors.grayColors.grayscale300};
  display: flex;
  padding: 16px 20px;
  align-items: flex-start;
  gap: 10px;
  align-self: stretch;
  width: 100%;
  color: ${(props) => props.theme.colors.blackColors.black1};
  background-color: ${(props) => props.theme.colors.blackColors.white6};

  &.disabledPreview {
    background-color: white;
    color: #8b8b8b;
  }
  &:disabled {
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
  }
  &.errorEnabledInput {
    border-color: red;
  }
`;

export const StyledForm = styled.form`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .buttonArea {
    display: flex;
    justify-content: space-between;
  }
`;

export const InputLabelContainer = styled.div`
  margin: 10px 0;

  label {
    font-size: 15px;
  }
`;

export const ValidationText = styled.span`
  color: #e03137;
  font-size: 12px;
  font-weight: 400;
  line-height: 160%;
  display: flex;
  align-items: center;
  margin-top: 4px;

  &.star {
    margin-top: 0px;
    font-size: 15px;
    font-weight: 400;
    display: inline-block;
  }

  &.info {
    color: ${(props) => props.theme.colors.grayColors.gray3};
  }
`;

// TOGGLE

export const SwitchLabel = styled.label`
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
`;

export const SwitchInput = styled.input`
  opacity: 0;
  width: 0;
  height: 0;
`;

export const Slider = styled.span`
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  border-radius: 34px;
  transition: 0.4s;

  &:before {
    content: '';
    position: absolute;
    height: 26px;
    width: 26px;
    left: 4px;
    bottom: 4px;
    background-color: white;
    border-radius: 50%;
    transition: 0.4s;
  }
`;

export const StyledSwitch = styled(SwitchInput)`
  &:checked + ${Slider} {
    background-color: #005792;

    &:before {
      transform: translateX(26px);
    }
  }
  &:disabled + ${Slider} {
    background-color: ${(props) => props.theme.colors.grayColors.gray3};
    cursor: not-allowed;
  }
`;

export const SideModalResponseError = styled.div`
  width: 100%;
  height: fit-content;
  padding: 6px;
  background-color: #00589227;
  margin-bottom: 30px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
`;
