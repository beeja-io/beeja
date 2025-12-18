import { ProjectEntity } from './ProjectEntity';
export interface DailyLog {
  Id: string;
  logDate: string;
  projectId: string;
  contractId: string;
  description: string;
  loggedHours: number;
}

export interface WeekLog {
  compositeKey: string;
  startOfWeek: string;
  endOfWeek: string;
  totalWeekHours: number;
  dailyLogs: DailyLog[];
  dailyTotals: Record<string, number>;
  weekNumber: number;
  year?: number;
  weekYear: number;
}

export interface LogEntry {
  id?: string;
  projectId: string;
  contractId: string;
  loghour: string;
  description: string;
}

export interface Contract {
  contractId: string;
  contractTitle: string;
}

export interface TimeSheetLogState {
  logEntries: LogEntry[];
  setLogEntries: React.Dispatch<React.SetStateAction<LogEntry[]>>;
  addButtonClicked: boolean;
  setAddButtonClicked: React.Dispatch<React.SetStateAction<boolean>>;
  selectedDate: string | null;
  setSelectedDate: (date: string | null) => void;
  isEditingMode: boolean;
  setIsEditingMode: React.Dispatch<React.SetStateAction<boolean>>;
}

export interface TimeSheetHandlers {
  handleSaveLogEntries: () => Promise<void>;
  onDelete: (logId: string) => void;
  onEdit: (log: DailyLog) => void;
  onUpdate: () => void;
  onCancel: () => void;
}

export interface TimeSheetReferenceData {
  projectOptions: ProjectEntity[];
  getProjectName: (id: string) => string;
  getContractTitle: (id: string, projectId?: string) => string;
  setContractLookup: React.Dispatch<
    React.SetStateAction<Record<string, string>>
  >;
  fetchContractsForProject: (projectId: string) => Promise<Contract[]>;
}

export interface APITimesheet {
  id: string;
  projectId: string;
  contractId: string;
  contractTitle?: string;
  description: string;
  timeInMinutes: number;
  startDate: string;
}

export interface APIDay {
  timesheets: APITimesheet[];
}

export interface APIWeek {
  weekNumber: number;
  weekYear: number;
  weekStartDate: string;
  weekEndDate: string;
  weeklyTotalHours: number;
  dailyLogs: Record<string, APIDay>;
}
