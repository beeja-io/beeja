import styled from 'styled-components';

export const FormContainer = styled.div`
  width: 95vw;
  max-width: 1050px;
  padding: 2.5rem 3rem;
  background-color: #fff;
  border-radius: 12px;
  margin: 0 auto;
  @media screen and (max-width: 1150px) {
    width: 90vw;
    padding: 2rem;
  }

  @media screen and (max-width: 768px) {
    width: 95vw;
    padding: 1.5rem;
  }

  max-width: 750px;
  padding: 2rem;
  background-color: #fff;
  border-radius: 12px;
  margin: 0 auto;
`;

export const SectionTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
`;

export const FormGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
`;

export const FormField = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Label = styled.label`
  font-size: 0.95rem;
  margin-bottom: 0.5rem;
  font-weight: 500;
`;

export const Input = styled.input`
  padding: 0.6rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;

  &:focus {
    border-color: #3182ce;
    outline: none;
  }
`;

export const Select = styled.select`
  padding: 0.6rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
  background-color: #fff;

  &:focus {
    border-color: #3182ce;
    outline: none;
  }
`;

export const TextArea = styled.textarea`
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 0.95rem;
  resize: vertical;

  &:focus {
    border-color: #3182ce;
    outline: none;
  }
`;

export const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 2rem;
`;

export const SubmitButton = styled.button`
  padding: 0.6rem 1.5rem;
  background-color: rgba(0, 87, 146, 1);

  color: white;
  font-weight: 500;
  font-size: 0.95rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  width: 100px;

  &:hover {
    background-color: rgb(3, 74, 121);
  }
`;

export const CancelButton = styled.button`
  padding: 0.6rem 1.2rem;
  background-color: #f0f0f0;
  color: #333;
  font-size: 0.95rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  width: 100px;

  &:hover {
    background-color: #e0e0e0;
  }
`;

export const RequiredAsterisk = styled.span`
  color: red;
  margin-left: 0.2rem;
`;

export const DateInputWrapper = styled.div`
  position: relative;
  display: flex;
  align-items: center;

  .iconArea {
    position: absolute;
    right: 1rem;
    cursor: pointer;
  }

  .calendarSpace {
    position: absolute;
    top: 110%;
    left: 0;
    z-index: 10;
    background: #fff;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.15);
    border-radius: 8px;
    overflow: hidden;
  }
`;
