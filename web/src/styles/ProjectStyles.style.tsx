import styled from 'styled-components';
import StatusDropdown from './ProjectStatusStyle.style';

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

export const ClientInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const LogoPreview = styled.div`
  img {
    width: 48px;
    height: 48px;
    object-fit: cover;
    border-radius: 50%;
  }
`;

export const InfoText = styled.div`
  line-height: 1.4;
  display: flex;
  flex-direction: column;

  .id {
    font-size: 12px;
    color: #6b7280;

    span {
      color: #007bff;
      font-weight: 500;
    }
  }

  .name {
    font-size: 14px;
    font-weight: 600;
    color: #111827 !important;

    font-family: Nunito;
    font-weight: 600;
    font-style: SemiBold;
    font-size: 16px;
    leading-trim: NONE;
    line-height: 160%;
    letter-spacing: 0px;
    vertical-align: middle;
  }

  .industry {
    font-size: 12px;
    color: #65676d !important;
  }
`;

export const RightSectionDiv = styled.div`
  width: 315;
  height: 218;
  angle: 0 deg;
  opacity: 1;
  gap: 32px;
  border-width: 1px;
  padding-top: 28px;
  padding-right: 24px;
  padding-bottom: 28px;
  padding-left: 24px;
  border-top-left-radius: 16px;
  border-bottom-left-radius: 16px;
  background-color: #ffffff;
`;

export const IconWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  svg {
    width: 19px;
    height: 16px;
  }
`;

export const FormContainer = styled.div`
  background: #fff;
  padding: 24px 32px;
  border-radius: 10px;
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.08);
  width: 100%;
  margin: 10px auto;
`;

export const SectionTitle = styled.h2`
  font-family: Nunito;
  font-weight: 600;
  font-style: SemiBold;
  font-size: 18px;
  leading-trim: NONE;
  line-height: 150%;
  letter-spacing: 0px;
  color: #005792;
  font-size: 18px;
  font-weight: 600;
  margin: 40px 0 28px 190px;
`;

export const FormGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto auto;
  gap: 14px 22px;
  max-width: 900px;
  margin: 0 auto;
`;

export const FormField = styled.div`
  display: flex;
  flex-direction: column;
`;

export const Label = styled.label`
  font-size: 14px;
  font-weight: 500;
  color: #1e1e1e;
  margin-bottom: 8px;
`;

export const RequiredAsterisk = styled.span`
  color: red;
  margin-left: 4px;
`;

export const Input = styled.input`
  width: 433px;
  height: 53px;
  padding: 10px 14px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

export const SelectDropDown = styled.select`
  width: 433px;
  height: 53px;
  padding: 10px 14px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  background-color: white;
  max-width: 100%;
  appearance: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:focus {
    border-color: #007bff;
    outline: none;
  }

  &::-webkit-scrollbar {
    display: none;
  }
`;

export const SelectWrapper = styled.div`
  width: 433px;
  border-radius: 8px;

  .react-select__control {
    min-height: 53px;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    box-shadow: none;
  }
`;

export const TextArea = styled.textarea`
  width: 433px;
  height: 53px;
  padding: 10px 14px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

export const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 32px;
  gap: 16px;
`;
export const CancelButton = styled.button`
  padding: 10px 24px;
  font-size: 14px;
  border: 1px solid #c5c5c5;
  background-color: white;
  border-radius: 8px;
  color: #333;
  cursor: pointer;
  &:hover {
    background-color: #f4f4f4;
  }
`;

export const SubmitButton = styled.button`
  padding: 10px 24px;
  font-size: 14px;
  background-color: #004b8d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  &:hover {
    background-color: #003866;
  }
`;

export const TextInput = styled.input`
  width: 433px;
  height: 53px;
  padding: 10px 14px;
  padding-right: 40px; /* space for calendar icon */
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 14px;
  box-sizing: border-box;

  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

export const DateInputWrapper = styled.div`
  position: relative;
  width: 400px;
  height: 53px;

  .iconArea {
    position: absolute;
    top: 50%;
    right: 14px;
    transform: translateY(-50%);
    pointer-events: all;
    cursor: pointer;
    display: flex;
    align-items: center;
  }

  .calendarSpace {
    position: absolute;
    top: 60px;
    z-index: 10;
  }
`;

export const ClientTitleWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const InfoRow = styled.div`
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 10px;

  svg {
    vertical-align: middle;
  }
`;

export const StyledStatusDropdown = styled(StatusDropdown)`
  max-width: 150px;
`;
