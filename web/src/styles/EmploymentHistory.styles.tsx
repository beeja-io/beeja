import styled from 'styled-components';

export const Container = styled.div`
  background: ${(props) => props.theme.colors.backgroundColors.white};
  border-radius: 8px;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.06);
`;

export const CardContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  line-height: 1.6;
`;

export const Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
`;

export const Title = styled.h1`
  margin: 0;
  font-size: 18px;
`;

export const Timeline = styled.ul<{ scrollable?: boolean }>`
  list-style: none;
  padding: 0;
  margin: 0;
  position: relative;
  max-height: ${(props) => (props.scrollable ? '400px' : 'auto')};
  overflow-y: ${(props) => (props.scrollable ? 'auto' : 'visible')};
`;

export const TimelineItem = styled.li`
  --dot-size: 10px;
  --line-width: 2px;
  --timeline-left: 18px;
  --line-color: #005792;
  --dot-offset: 14px;

  position: relative;
  padding-left: calc(var(--timeline-left) + var(--dot-size) + 12px);
  margin-bottom: 32px;

  &::before {
    content: '';
    position: absolute;
    left: var(--timeline-left);
    top: var(--dot-offset);
    width: var(--dot-size);
    height: var(--dot-size);
    border-radius: 50%;
    background: var(--line-color);
    border: 2px solid #fff;
    box-shadow: 0 0 0 2px #e5e7eb;
    z-index: 1;
  }

  &::after {
    content: '';
    position: absolute;
    left: calc(
      var(--timeline-left) + (var(--dot-size) / 2) - (var(--line-width) / 2) +
        1.5px
    );
    top: calc(var(--dot-offset) + var(--dot-size));
    bottom: -32px;
    width: var(--line-width);
    background: var(--line-color);
    z-index: 0;
  }

  &:first-child::after {
    top: calc(var(--dot-offset) + var(--dot-size) / 2);
  }

  &:last-child::after {
    display: none;
  }
`;

export const Card = styled.div`
  background: transparent;
  border-radius: 8px;
  padding: 12px 14px;
  border: 1px solid rgba(0, 0, 0, 0.04);
`;

export const TopRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
`;

export const Small = styled.div`
  font-size: 12px;
  color: #6b7280;
`;

export const Badge = styled.span<{ type?: string }>`
  display: inline-block;
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 9999px;
  margin-right: 8px;

  color: ${({ type }) => {
    switch (type) {
      case 'Full Time':
        return '#065f46';
      case 'Part Time':
        return '#1e3a8a';
      case 'Intern':
        return '#92400e';
      case 'Paid Intern':
        return '#920e68ff';
      case 'Free Launcher':
        return '#6b21a8';
      default:
        return '#1e40af';
    }
  }};
  background: ${({ type }) => {
    switch (type) {
      case 'Full Time':
        return '#d1fae5';
      case 'Part Time':
        return '#dbeafe';
      case 'Intern':
        return '#fef3c7';
      case 'Paid Intern':
        return '#e8e2deff';
      case 'Free Launcher':
        return '#ede9fe';
      default:
        return '#e0e7ff';
    }
  }};
`;

export const ActionsMenu = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: absolute;
  right: 0;
  top: 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 4px 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

export const ModalOverlay = styled.div`
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
`;

export const Modal = styled.div`
  width: 1000px;
  max-width: calc(100% - 40px);
  background: #fff;
  border-radius: 10px;
  padding: 50px;
`;

export const Required = styled.span`
  color: red;
  margin-left: 2px;
`;

export const CancelButton = styled.button`
  padding: 10px 24px;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
`;

export const SaveButton = styled.button<{ disabled?: boolean }>`
  padding: 10px 24px;
  border-radius: 8px;
  background: #004080;
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 14px;
  opacity: ${({ disabled }) => (disabled ? 0.6 : 1)};
`;
