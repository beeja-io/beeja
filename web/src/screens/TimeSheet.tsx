import React, { useEffect, useState } from "react";
import {
  TimesheetContainer,
  WeekContainer,
  Weekday,
  DailyLogContainer,
  SaveButton,
  RotateArrow,
  FormContainer,
  AddButton,
  Filters,
  Dropdown,
  WeekSubContainer, WeekTitle,
  WeeklyLogs, TotalWeekHoursContainer,
  MonthHoursContainer,
  MonthBox,
  HoursBox,
  WeekdayRow,
  DayText,
  LoggedHours,
  DaysContainer,
  SingleRowContainer, PaginationContainer, PaginationButton,
  StyledTable,
  CloseButton,
  ButtonGroup,
  ButtonWrapper,
  SelectInput,
  Input
} from "../styles/TimeSheetStyles.style";
// import { getMonthLogs, getWeekDate, PostLogHours } from "../service/axiosInstance";
import { ArrowDownSVG, EditWhitePenSVG } from "../svgs/CommonSvgs.svs";
import { format, subMonths, addMonths } from "date-fns";
import { useNavigate } from "react-router-dom";
import { useTranslation } from 'react-i18next';
import { useUser } from '../context/UserContext';
import { deleteLog, fetchMonthLogs, PostLogHours, updateLog } from "../service/axiosInstance";
import LogAction from "../components/reusableComponents/LogAction";
import SpinAnimation from "../components/loaders/SprinAnimation.loader";
import { toast } from "sonner";
import CenterModal from "../components/reusableComponents/CenterModal.component";
interface DailyLog {
  Id: string
  logDate: string;
  projectId: string;
  description: string;
  loggedHours: number;
  contractId: string;
}

interface WeekLog {
  startOfWeek: string;
  endOfWeek: string;
  totalWeekHours: number;
  dailyLogs: DailyLog[];
  weekNumber: number;
  year: number;
}

interface LogEntry {
  id: string;
  projectId: string;
  contractId: string;
  loghour: string;
  description: string;
}

type ExpandedWeeksState = Record<number, boolean>;
type ExpandedDaysState = Record<string, boolean>;
type DailyLogsState = Record<string, DailyLog[]>;

// const WEEKS_PER_PAGE = 5;

const Timesheet = () => {
  const [weeksData, setWeeksData] = useState<WeekLog[]>([]);

  function mergeWeeksData(weeksDataFromApi: any[], selectedMonth: Date) {
    const emptyWeeks = getEmptyWeeksForMonth(selectedMonth); // <-- Use the selected month
    const apiWeekMap = new Map<number, any>();

    weeksDataFromApi.forEach((week: any) => {
      apiWeekMap.set(week.weekNumber, week);
    });

    return emptyWeeks.map((week) => {
      const filled = apiWeekMap.get(week.weekNumber);
      return filled ? filled : week;
    });
  }

  function getEmptyWeeksForMonth(month: Date) {
    const weeks: any[] = [];
    const year = month.getFullYear();
    const monthIndex = month.getMonth(); // 0-based

    const startOfMonth = new Date(year, monthIndex, 1);
    const endOfMonth = new Date(year, monthIndex + 1, 0);

    let current = new Date(startOfMonth);

    while (current <= endOfMonth) {
      const startOfWeek = new Date(current);
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);

      const weekNumber = getWeekNumber(startOfWeek);

      weeks.push({
        weekNumber,
        year: year,
        startOfWeek: startOfWeek.toISOString().slice(0, 10),
        endOfWeek: endOfWeek.toISOString().slice(0, 10),
        totalWeekHours: 0,
        dailyLogs: [],
      });

      current.setDate(current.getDate() + 7);
    }
    return weeks;
  }

  function adaptApiResponseToExpectedFormat(apiResponse: any): WeekLog[] {
    const weeks: WeekLog[] = [];

    Object.entries(apiResponse).forEach(([weekKey, weekData]: [string, any]) => {
      const weekNumber = parseInt(weekKey.replace('week-', ''), 10);
      const startOfWeek = weekData.weekStartDate;
      const endOfWeek = weekData.weekEndDate;
      const totalWeekHours = weekData.totalHours || 0;

      // Group logs by day (logDate) with sum of loggedHours per log
      const dailyLogsMap: Record<string, DailyLog[]> = {};

      weekData.timesheets.forEach((ts: any) => {
        const logDate = ts.startDate.slice(0, 10); // YYYY-MM-DD
        const loggedHours = ts.timeInMinutes / 60; // convert minutes to hours

        const dailyLog: DailyLog = {

          logDate,
          loggedHours,
          Id: ts.id,
          projectId: ts.projectId || "",
          contractId: ts.contractId || "",
          description: ts.description || "",
        };

        if (!dailyLogsMap[logDate]) {
          dailyLogsMap[logDate] = [];
        }
        dailyLogsMap[logDate].push(dailyLog);
      });

      // Flatten daily logs into one array (optional: you can keep grouped if needed)
      // Here we combine multiple logs on the same day as separate entries.
      const dailyLogs = Object.values(dailyLogsMap).flat();

      // Extract year from startOfWeek
      const year = new Date(startOfWeek).getFullYear();

      weeks.push({
        startOfWeek,
        endOfWeek,
        totalWeekHours,
        weekNumber,
        year,
        dailyLogs,
      });
    });

    // Sort weeks by weekNumber ascending
    weeks.sort((a, b) => a.weekNumber - b.weekNumber);

    return weeks;
  }



  const [reloadFlag, setReloadFlag] = useState(false);
  const [expandedWeeks, setExpandedWeeks] = useState<ExpandedWeeksState>({});
  const [expandedDays, setExpandedDays] = useState<ExpandedDaysState>({});
  const [dailyLogs, setDailyLogs] = useState<DailyLogsState>({});

  const [selectedProject, setSelectedProject] = useState<string>("All");
  const [selectedContract, setSelectedContract] = useState<string>("All");
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [showSaveConfirmation, setShowSaveConfirmation] = useState(false);

  const [logEntries, setLogEntries] = useState<any[]>([
    {
      projectId: "",
      contractId: "",
      loghour: "",
      description: "",
    },
  ]);

  function getWeekNumber(date: Date): number {
    const startOfYear = new Date(date.getFullYear(), 0, 1);
    const days = Math.floor((date.getTime() - startOfYear.getTime()) / (24 * 60 * 60 * 1000));
    return Math.ceil((days + startOfYear.getDay() + 1) / 7);
  }

  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());
  // const [currentWeekNumber, setCurrentWeekNumber] = useState(getWeekNumber(new Date()));
  const [monthLogs, setMonthLogs] = useState(" ");
  const [totalMonthlyHours, setTotalMonthlyHours] = useState<number>(0);

  const generateWeekDays = (startDate: string) => {
    let days = [];
    for (let i = 0; i < 7; i++) {
      let day = new Date(startDate);
      day.setDate(day.getDate() + i);
      days.push({
        dateISO: day.toISOString().split("T")[0],
        dayName: format(day, "EEE"),
        formattedDate: format(day, "dd/MM/yy"),
        isWeekend: day.getDay() === 6 || day.getDay() === 0,
        isToday: day.toISOString().split("T")[0] === new Date().toISOString().split("T")[0],
      });
    }
    return days;
  };

  const handleProjectFilter = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedProject(event.target.value);
  };

  const handleContractFilter = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedContract(event.target.value);
  };

  const { user } = useUser();
  const handlePreviousMonth = () => {
    const newMonth = subMonths(currentMonth, 1);
    setCurrentMonth(newMonth);
  };

  const handleNextMonth = () => {
    const newMonth = addMonths(currentMonth, 1);
    setCurrentMonth(newMonth);
  };
  async function fetchMonthData() {
    try {
      setLoading(true);
      const formattedMonth = format(currentMonth, 'yyyy-MM');
      const res = await fetchMonthLogs(formattedMonth);
      setTotalMonthlyHours(res.data.monthlyTotalHours);
      const readableMonth = format(currentMonth, 'MMMM yyyy');

      // Set it to state
      setMonthLogs(readableMonth);


      const rawWeekData = res?.data?.weekTimesheets;
      if (!rawWeekData) {
        setWeeksData(getEmptyWeeksForMonth(currentMonth));
        return;
      }

      const adaptedWeekData = adaptApiResponseToExpectedFormat(rawWeekData);
      const finalWeeksData = mergeWeeksData(adaptedWeekData, currentMonth);

      setWeeksData(finalWeeksData);
      setLoading(false);
    } catch (error) {
      setWeeksData(getEmptyWeeksForMonth(currentMonth));
    }
  }
  useEffect(() => {
    fetchMonthData();
  }, [currentMonth, reloadFlag]);


  const handleWeekClick = (
    weekNumber: number,
    startOfWeek: string,
    dailyLogsData: DailyLog[]
  ) => {
    setExpandedWeeks((prev) => {
      const isAlreadyOpen = prev[weekNumber];
      // If it's already open, close all weeks; otherwise, open only this one
      return isAlreadyOpen ? {} : { [weekNumber]: true };
    });

    const generatedDays = generateWeekDays(startOfWeek);
    const tempDailyLogs: DailyLogsState = {};
    generatedDays.forEach((day) => {
      tempDailyLogs[day.dateISO] = [];
    });

    dailyLogsData.forEach((log) => {
      tempDailyLogs[log.logDate]?.push(log);
    });

    setDailyLogs(tempDailyLogs);

    // Optional: collapse any open days when switching weeks
    setExpandedDays({});
  };

  const handleDayClick = (day: any) => {
    setExpandedDays((prev) => ({ ...prev, [day]: !prev[day] }));
    setSelectedDate(day);
    setLogEntries((prev) => ({
      ...prev,
      [day]: prev[day] || [],
    }));
  };

  const handleEditClick = (logDate: string, index: number, log: any) => {
    setEditingLog({
      id: log.Id,
      logDate,
      index,
      loghour: log.loggedHours,
      projectId: log.projectId,
      contractId: log.contractId,
      description: log.description || "",
    });
  };


  const handleDelete = async (id: string) => {
    try {
      setLoading(true);
      await deleteLog(id);
      toast.success("Log deleted successfully");
      setReloadFlag((prev) => !prev);
      // Remove deleted log locally
      setDailyLogs((prevLogs) => {
        const updatedLogs = { ...prevLogs };
        Object.keys(updatedLogs).forEach((date) => {
          updatedLogs[date] = updatedLogs[date].filter((log) => log.Id !== id);
        });
        return updatedLogs;
      });
    } catch (error) {
      toast.error("Failed to delete log");
    } finally {
      setLoading(false);
    }
  };


  const [addButtonClicked, setAddButtonClicked] = useState<any>(false)

  const handleInputChange = (index: number, field: keyof LogEntry, value: string) => {
    setLogEntries((prevEntries: any) => {
      const updatedEntries = [...prevEntries];
      updatedEntries[index] = { ...updatedEntries[index], [field]: value };
      return updatedEntries;
    });
  };

  const addButtonEntries = () => {
    return (
      logEntries.length > 0 &&
      <FormContainer>
        {Array.isArray(logEntries) &&
          logEntries.map((entry, index) => (
            <div key={index}>
              <div className="Form_Row">
                <select
                  value={entry.projectId}
                  onChange={(e) => handleInputChange(index, "projectId", e.target.value)}
                >
                  <option value="">Select Project</option>
                  <option value="Beeja">Beeja</option>
                  <option value="Project 2">Project 2</option>
                  <option value="PROJ002">PROJ002</option>
                </select>
                <select
                  value={entry.contractId}
                  onChange={(e) => handleInputChange(index, "contractId", e.target.value)}
                >
                  <option value="">Select Contract</option>
                  <option value="Contract ">Contract </option>
                  <option value="Contract 2">Contract 2</option>
                  <option value="CON002">CON002</option>
                </select>
                <select
                  value={entry.loghour}
                  onChange={(e) => handleInputChange(index, "loghour", e.target.value)}
                >
                  {[...Array(16)].map((_, i) => {
                    const value = (i + 1) * 0.5;
                    return (
                      <option key={value} value={`${value}`}>
                        {value} hrs
                      </option>
                    );
                  })}
                </select>
                <input
                  placeholder="Description"
                  value={entry.description || ""}
                  onChange={(e) => handleInputChange(index, "description", e.target.value)}
                />
                <div>
                  {!addButtonClicked && <EditWhitePenSVG />}
                </div>
              </div>
              <ButtonWrapper>
                <SaveButton onClick={handleSaveLogEntries}>Save</SaveButton>
              </ButtonWrapper>
            </div>
          ))}
      </FormContainer>

    );
  }

  const handleSaveLogEntries = async () => {
    const newEntries = logEntries.map((entry) => {
      const decimalHours = Number(entry.loghour) || 0;

      return {
        projectId: entry.projectId,
        contractId: entry.contractId,
        timeInMinutes: decimalHours * 60,
        description: entry.description,
        startDate: selectedDate,
      };
    });
    try {
      setLoading(true);
      const response = await PostLogHours(newEntries[logEntries.length - 1]);
      fetchMonthData();
      setAddButtonClicked(false);
      setLogEntries([]);
      setEditingLog(null);
      setReloadFlag((prev) => !prev);
      setDailyLogs((prevLogs: any) => ({
        ...prevLogs,
        [String(selectedDate)]: [
          ...(prevLogs[String(selectedDate)] || []),
          ...newEntries.map((entry) => ({
            ...entry,
            Id: response.data.id,
            loggedHours: entry.timeInMinutes / 60,
          })),
        ],
      }));
      setLoading(false);
      toast.success("Log Added Successfully")
      return response;
    } catch (error) {
      setLoading(false);

    }
  };

  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [editingLog, setEditingLog] = useState<{
    id: string;
    logDate: string;
    index: number;
    loghour: number;
    projectId: string
    contractId: string;
    description: string;
  } | null>(null);

  const handleSaveClick = async () => {
    if (!editingLog) return;

    const { logDate, projectId, index, loghour, contractId, description } = editingLog;
    const log = dailyLogs[logDate][index];

    const updatedData = {
      projectId: projectId,
      contractId,
      timeInMinutes: loghour * 60,
      description,
      startDate: logDate,
    };

    try {
      setLoading(true);
      await updateLog(log.Id, updatedData); // Make sure updateLog is defined and imported
      setLoading(false);
      toast.success("Log updated");
      setReloadFlag((prev) => !prev);

      // Update local state
      setDailyLogs((prev) => {
        const updated = { ...prev };
        updated[logDate][index] = {
          ...log,
          loggedHours: loghour,
          projectId,
          contractId,
          description,
        };
        return updated;
      });

      setEditingLog(null);
    } catch (err) {
      toast.error("Update failed");
      setLoading(false);
    }
  };


  return (

    <>
      {loading ? (
        <SpinAnimation /> // <-- Replace with your actual spinner or loader
      ) : (
        <TimesheetContainer>
          <div className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Time Sheet')}
          </div>
          <div className="TimesheetSubContainer">
            <div className="TimeSheet_Heading">
              <p className="TimeSheetTitle underline">{t('List of Time Sheets')}</p>
            </div>
            <div className="Filter_Container">
              <Filters>
                <Dropdown onChange={handleProjectFilter} value={selectedProject}>
                  <option value="All">All Projects</option>
                  <option value="Project A">Project A</option>
                  <option value="Project B">Project B</option>
                </Dropdown>
                <Dropdown onChange={handleContractFilter} value={selectedContract}>
                  <option value="All">All Contracts</option>
                  <option value="Contract1">Contract1</option>
                  <option value="Contract2">Contract2</option>
                </Dropdown>
              </Filters>
            </div>

            <MonthHoursContainer>
              <MonthBox>
                {t('Month')}: <span>{monthLogs}</span>
              </MonthBox>
              <HoursBox>
                {t('Total Hours')}: <span>{totalMonthlyHours} hrs</span>
              </HoursBox>
            </MonthHoursContainer>
            {mergeWeeksData(weeksData, currentMonth).map((weekData) => {
              const isActive = expandedWeeks[weekData.weekNumber] || false;
              return (
                <WeekContainer key={weekData.weekNumber}>
                  <WeekSubContainer isActive={isActive} onClick={() => handleWeekClick(weekData.weekNumber, weekData.startOfWeek, weekData.dailyLogs)}>
                    <WeekTitle>
                      {t('Week')} {weekData.weekNumber} ({weekData.startOfWeek} - {weekData.endOfWeek})
                    </WeekTitle>
                    <TotalWeekHoursContainer>
                      <WeeklyLogs>{t('Weekly Logs')}: {weekData.totalWeekHours} hrs</WeeklyLogs>
                      <RotateArrow isExpanded={expandedWeeks[weekData.weekNumber]}>
                        <ArrowDownSVG />
                      </RotateArrow>
                    </TotalWeekHoursContainer>
                  </WeekSubContainer>
                  {
                    expandedWeeks[weekData.weekNumber] && (
                      <DaysContainer>
                        {generateWeekDays(weekData.startOfWeek).map((day) => (
                          <WeekdayRow key={day.dateISO}
                          >
                            <SingleRowContainer onClick={() => handleDayClick(day.dateISO)}
                              style={{ background: day.isToday ? "rgba(52, 168, 83, 0.12)" : day.isWeekend ? "#FFF4F4" : "" }}>
                              <Weekday>
                                <DayText style={{ color: day.isWeekend ? "#E03137" : "" }}>{day.dayName}, {day.formattedDate}</DayText>
                              </Weekday>
                              <LoggedHours>{t('Logged hours')}: {dailyLogs[day.dateISO]?.reduce((sum, log) => sum + log.loggedHours, 0) || 0} hrs</LoggedHours>
                            </SingleRowContainer>
                            {expandedDays[day.dateISO] && (
                              <DailyLogContainer>
                                <StyledTable>
                                  <thead>
                                    <tr>
                                      <th>Project</th>
                                      <th>Contract</th>
                                      <th>Log Hours</th>
                                      <th>Description</th>
                                      {/* Show Action column only if no row is being edited */}
                                      <th>Action</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    {dailyLogs[day.dateISO] && dailyLogs[day.dateISO].length > 0 ? (
                                      dailyLogs[day.dateISO].map((log, logIndex) => {
                                        const isEditing = editingLog?.logDate === day.dateISO && editingLog.index === logIndex;
                                        return (
                                          <React.Fragment key={logIndex}>
                                            <tr>
                                              <td>
                                                {isEditing ? (
                                                  <SelectInput
                                                    value={editingLog.projectId}
                                                    onChange={(e) =>
                                                      setEditingLog((prev) =>
                                                        prev ? { ...prev, projectId: e.target.value } : null
                                                      )
                                                    }
                                                  >
                                                    <option value="">Select Project</option>
                                                    <option value="PROJ001">PROJ001</option>
                                                    <option value="PROJ002">PROJ002</option>
                                                    <option value="PROJ003">PROJ003</option>
                                                  </SelectInput>
                                                ) : (
                                                  log.projectId
                                                )}
                                              </td>

                                              <td>
                                                {isEditing ? (
                                                  <SelectInput
                                                    value={editingLog.contractId}
                                                    onChange={(e) =>
                                                      setEditingLog((prev) =>
                                                        prev ? { ...prev, contractId: e.target.value } : null
                                                      )
                                                    }
                                                  >
                                                    <option value="">Select Contract</option>
                                                    <option value="Contract 1">Contract 1</option>
                                                    <option value="Contract 2">Contract 2</option>
                                                    <option value="CON002">CON002</option>
                                                  </SelectInput>
                                                ) : (
                                                  log.contractId
                                                )}
                                              </td>

                                              <td>
                                                {isEditing ? (
                                                  <Input
                                                    type="number"
                                                    value={editingLog.loghour}
                                                    onChange={(e) =>
                                                      setEditingLog((prev) =>
                                                        prev ? { ...prev, loghour: Number(e.target.value) } : null
                                                      )
                                                    }
                                                  />
                                                ) : (
                                                  `${log.loggedHours} hrs`
                                                )}
                                              </td>

                                              <td>
                                                {isEditing ? (
                                                  <Input
                                                    value={editingLog.description}
                                                    onChange={(e) =>
                                                      setEditingLog((prev) =>
                                                        prev ? { ...prev, description: e.target.value } : null
                                                      )
                                                    }
                                                  />
                                                ) : (
                                                  log.description
                                                )}
                                              </td>
                                              <td className="Action">
                                                <LogAction
                                                  onDelete={() => handleDelete(log.Id)}
                                                  onEdit={() => handleEditClick(day.dateISO, logIndex, log)}
                                                />
                                              </td>
                                            </tr>
                                            {isEditing && (
                                              <tr>
                                                <td colSpan={5}>
                                                  <div
                                                    style={{
                                                      display: 'flex',
                                                      justifyContent: 'flex-end',
                                                      gap: '10px',
                                                      paddingTop: '8px',
                                                      paddingRight: '10px',
                                                    }}
                                                  ><ButtonGroup>
                                                      <CloseButton onClick={() => setEditingLog(null)}>Cancel</CloseButton>
                                                      <SaveButton onClick={() => setShowSaveConfirmation(true)}>Save</SaveButton>
                                                    </ButtonGroup>
                                                  </div>

                                                </td>
                                              </tr>
                                            )}
                                          </React.Fragment>
                                        );
                                      })
                                    ) : (
                                      <tr>
                                        <td className="no-entries" colSpan={5}>
                                          No entries yet.
                                        </td>
                                      </tr>
                                    )}

                                    {/* Add Button Row */}
                                    <tr>
                                      <td colSpan={5} style={{ textAlign: 'right', paddingTop: '10px', paddingRight: '10px' }}>
                                        <AddButton
                                          onClick={() => {
                                            if (selectedDate === day.dateISO && addButtonClicked) {
                                              setAddButtonClicked(false);
                                              setLogEntries([]);
                                            } else {
                                              setSelectedDate(day.dateISO);
                                              setLogEntries([
                                                { projectId: '', contractId: '', loghour: '', description: '' },
                                              ]);
                                              setAddButtonClicked(true);
                                            }
                                          }}
                                        >
                                          +
                                        </AddButton>
                                      </td>
                                    </tr>

                                    {/* Add Entry Form Row */}
                                    {selectedDate === day.dateISO && addButtonClicked && (
                                      <tr>
                                        <td colSpan={5}>{addButtonEntries()}</td>
                                      </tr>
                                    )}
                                  </tbody>
                                </StyledTable>
                                {showSaveConfirmation && (
                                  <CenterModal
                                    handleModalLeftButtonClick={() => setShowSaveConfirmation(false)} // Cancel
                                    handleModalClose={() => setShowSaveConfirmation(false)}
                                    handleModalSubmit={() => {
                                      handleSaveClick(); // Your original save logic
                                      setShowSaveConfirmation(false);
                                    }}
                                    modalHeading="Save Changes"
                                  />
                                )}
                              </DailyLogContainer>
                            )}

                          </WeekdayRow>
                        ))}
                      </DaysContainer>
                    )
                  }
                </WeekContainer>
              )
            }
            )
            }

            <PaginationContainer>
              <PaginationButton onClick={handlePreviousMonth}><span className="leftArrow"><ArrowDownSVG /></span>{t('Previous')}</PaginationButton>
              <PaginationButton
                onClick={handleNextMonth}
              >
                {t('Next')} <span className="rightArrow"><ArrowDownSVG /></span>
              </PaginationButton>
            </PaginationContainer>
          </div>
        </TimesheetContainer >)
      }</>

  );
};

export default Timesheet;