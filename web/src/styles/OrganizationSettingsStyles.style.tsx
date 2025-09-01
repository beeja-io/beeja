import styled from 'styled-components';
export const ProfileHeading = styled.div`
  height: 28px;
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 0;
`;
export const Container = styled.div`
  margin-top: 10px;
  form {
    display: flex;
    flex-direction: column;
    align-items: center;

    button {
      margin-top: 20px;
    }
  }

  .roleInfo {
    font-size: 12px;
    display: flex;
    gap: 0 5px;
  }
  .drop {
    span {
      color: ${(props) => props.theme.colors.blackColors.black1};
      font-size: 14px;
    }
  }
`;
export const Row = styled.div`
  display: flex;
  flex-wrap: nowrap;
  margin-bottom: 20px;
  align-items: center;
  & > * {
    flex: 0 0 auto;
  }

  & > :first-child {
    flex-basis: 25%;
  }

  & > :last-child {
    flex-basis: 70%;
  }

  span {
    /* display: none; */
    color: #ff0606;
    font-size: 10px;
    padding-left: 5px;
  }

  textarea {
    width: 100%;
    background-color: ${(props) => props.theme.colors.blackColors.white6};
    color: ${(props) => props.theme.colors.blackColors.black1};
    resize: none;
    border-radius: 10px;
    border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
    outline: none;
    padding: 10px 10px;
    font-size: 14px;
    max-height: 330px;
    overflow: auto;

    &:disabled {
      color: ${(props) => props.theme.colors.blackColors.black1};
      background-color: ${(props) => props.theme.colors.grayColors.gray10};
      cursor: not-allowed;
    }
  }
  textarea::placeholder {
    color: #b3b3b3;
  }
  textarea:is(:focus, :valid) {
    padding: 10px 10px;
  }
  textarea::-webkit-scrollbar {
    width: 0px;
  }

  input:disabled {
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
  }
`;

export const Label = styled.label`
  font-size: 14px;
  font-weight: 500;
  color: ${(props) => props.theme.colors.blackColors.black1};
  margin-bottom: 5px;
`;

export const Input = styled.input`
  width: 100%;
  height: 43px;
  border-radius: 10px;

  background-color: ${(props) => props.theme.colors.blackColors.white6};

  color: ${(props) => props.theme.colors.blackColors.black1};
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  outline: none;
  padding: 0 15px;
  font-size: 14px;

  -moz-appearance: textfield;
  -webkit-appearance: none;
  appearance: none;

  &::-webkit-outer-spin-button,
  &::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  &:disabled {
    color: ${(props) => props.theme.colors.blackColors.black1};
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
    cursor: not-allowed;
  }
`;

export const Select = styled.select`
  width: 100%;
  height: 43px;
  border-radius: 10px;
  background-color: ${(props) => props.theme.colors.blackColors.white6};
  color: ${(props) => props.theme.colors.blackColors.black1};
  border: 1px solid ${(props) => props.theme.colors.grayColors.grayscale300};
  outline: none;
  padding: 0 15px;
  font-size: 14px;
  appearance: none;
  background-image: url('data:image/svg+xml;charset=UTF-8,%3csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"%3e%3cpolyline points="6 9 12 15 18 9"%3e%3c/polyline%3e%3c/svg%3e');
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;

  &:disabled {
    color: ${(props) => props.theme.colors.blackColors.black1};
    background-color: ${(props) => props.theme.colors.grayColors.gray10};
    cursor: not-allowed;
  }
`;

export const LogoText = styled.div`
  font-size: 10px;
  font-weight: 400;
  color: #818181;
  margin-top: 5px;
  padding-left: 10px;
  width: fit-content;
`;
export const Logo = styled.div<{ isEditModeOn: boolean }>`
  width: 100%;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  & > * {
    flex: 0 0 auto;
  }

  & > :first-child {
    flex-basis: 70%;
  }

  & > :last-child {
    flex-basis: auto;
  }

  .uploadIcon {
    display: ${(props) => (props.isEditModeOn ? 'block' : 'none')};
    cursor: ${(props) => (props.isEditModeOn ? 'pointer' : 'default')};
    margin-left: 40px;
  }
`;

export const FileUploadSection = styled.div<{ isEditModeOn: boolean }>`
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  border: 1.5px dashed ${(props) => props.theme.colors.grayColors.grayscale300};
  border-radius: 10px;
  cursor: ${(props) => (props.isEditModeOn ? 'pointer' : 'default')};
  span {
    padding-left: 10px;
    padding-right: 5px;
    color: #a0aec0;
    font-size: 14px;
    font-weight: 400;
  }

  input[type='file'] {
    position: absolute;
    left: 10px;
    top: 10px;
    width: 100%;
    height: 100%;
    opacity: 0;
  }

  .imageArea {
    display: flex;
    justify-content: center;
    width: 100%;

    img {
      max-width: 250px;
      height: fit-content;
      max-height: 70px;
      width: fit-content;
    }
  }

  .imageClearIcon {
    position: absolute;
    top: 4px;
    right: 4px;
    cursor: pointer;
  }
`;
export const Address = styled.div`
  display: flex;
  flex-wrap: wrap;
  width: 30%;
  gap: 10px;
`;

export const LoanFields = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 10px 100px;
  width: 100%;
  padding: 20px 0;

  .label {
    display: flex;
    flex-direction: column;
    font-size: 16px;
    font-weight: normal;
    padding: 0;
    gap: 5px;
    width: 45%;

    span.label-info {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      gap: 4px;
      font-size: 13px;
      color: ${(props) => props.theme.colors.grayColors.gray11};
    }
  }
  .input {
    width: 155px;
    height: 40px;
    max-width: 30%;
  }
`;

export const HeaderWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;
