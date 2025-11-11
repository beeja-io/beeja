export interface FeedbackStatusOption {
  value: string;
  label: string;
  color: string;
  bgColor: string;
}

export const FeedbackStatus: FeedbackStatusOption[] = [
  {
    value: 'NOT_ASSIGNED',
    label: 'Not Assigned',
    color: '#AAAAAA',
    bgColor: '#EFEFEF',
  },
  {
    value: 'IN_PROGRESS',
    label: 'In Progress',
    color: '#FF9900',
    bgColor: '#FF99001A',
  },
  {
    value: 'COMPLETED',
    label: 'Completed',
    color: '#34A853',
    bgColor: '#34A8531A',
  },
];
