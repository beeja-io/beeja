import styled from 'styled-components';

export const AppContainer = styled.div`
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Inter', sans-serif;
`;

export const Overlay = styled.div`
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
`;

export const ModalCard = styled.div`
  background: #fff;
  border-radius: 14px;
  width: 480px;
  padding: 36px 40px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.25);
`;

export const ModalTitle = styled.h2`
  text-align: center;
  font-size: 32px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 36px;
`;

export const FormSection = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 28px;
`;

export const Label = styled.label`
  font-weight: 500;
  color: #111827;
  margin-bottom: 8px;
  font-size: 14px;
`;

export const Required = styled.span`
  color: red;
`;

export const RatingInput = styled.input`
  width: 100%;
  height: 40px;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0056b3;
  }

  &::-webkit-inner-spin-button,
  &::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  &[type='number'] {
    -moz-appearance: textfield;
  }
`;

export const CommentsBox = styled.textarea`
  height: 90px;
  resize: none;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  color: #111827;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0056b3;
  }
`;

export const HintStarsRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 6px;
`;

export const HintText = styled.span`
  font-size: 13px;
  color: #818181;
  font-weight: 400;
`;

export const Stars = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  user-select: none;
  line-height: 1;
`;

export const StarWrapper = styled.span<{ fill: number }>`
  position: relative;
  display: inline-block;
  font-size: 30px;
  margin-right: -3px;
  color: #d9d9d9;
  width: 32px;
  height: 32px;

  /* gray base star */
  &::before {
    content: '★';
    color: #d9d9d9;
    position: absolute;
    top: 0;
    left: 0;
  }

  &::after {
    content: '★';
    color: #005792;
    position: absolute;
    top: 0;
    left: 0;
    width: ${({ fill }) => fill}%;
    overflow: hidden;
    white-space: nowrap;
    transition: width 0.25s ease-in-out;
  }
`;

export const ButtonRow = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
`;

export const ResetButton = styled.button`
  border: none;
  padding: 10px 22px;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  background: #f0f0f0;
  color: #333;
  transition: 0.2s;

  &:hover {
    background: #e0e0e0;
  }
`;

export const SubmitButton = styled.button<{ disabled?: boolean }>`
  border: none;
  padding: 10px 22px;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  transition: 0.2s;
  background: ${({ disabled }) => (disabled ? '#a0a0a0' : '#005792')};
  color: white;

  &:hover {
    background: ${({ disabled }) => (disabled ? '#a0a0a0' : '#004494')};
  }
`;
