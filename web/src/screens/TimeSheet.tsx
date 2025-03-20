import React, { useEffect, useState, useCallback, useRef } from "react";
import {
  TimesheetContainer,
  WeekContainer,
  Weekday,
  DailyLogContainer,
  EditButton,
  SaveButton,
  RotateArrow,
  FormContainer,
  AddButton,
  Filters,
  SearchBox,
  SearchInput,
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
} from "../styles/TimeSheetStyles.style";
import { getMonthLogs, getWeekDate, PostLogHours } from "../service/axiosInstance";
import { ArrowDownSVG, EditWhitePenSVG } from "../svgs/CommonSvgs.svs";
import { getISOWeek, format, parse, subMonths, addMonths } from "date-fns";
import { SearchSVG } from "../svgs/NavBarSvgs.svg";
import { useNavigate } from "react-router-dom";
import { useTranslation } from 'react-i18next';
import { useUser } from '../context/UserContext';

interface DailyLog {
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

const WEEKS_PER_PAGE = 5;

const Timesheet = () => {
  const [weeksData, setWeeksData] = useState<WeekLog[]>([]);
  const [expandedWeeks, setExpandedWeeks] = useState<ExpandedWeeksState>({});
  const [expandedDays, setExpandedDays] = useState<ExpandedDaysState>({});
  const [dailyLogs, setDailyLogs] = useState<DailyLogsState>({});
  const [currentPage, setCurrentPage] = useState<number>(0);

  const [selectedProject, setSelectedProject] = useState<string>("All");
  const [selectedContract, setSelectedContract] = useState<string>("All");
  const [isLastPage, setIsLastPage] = useState<boolean>(false);
  const [editingLog, setEditingLog] = useState<{ logDate: string; index: number } | null>(null);
  const [editedHours, setEditedHours] = useState<number>(0);
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [logEntries, setLogEntries] = useState<any[]>([
    {
      projectId: "",
      contractId: "",
      loghour: "",
      description: "",
    },
  ]);

  const getWeekNumber = (date: Date): number => {
    const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDays = Math.floor((date.getTime() - firstDayOfYear.getTime()) / (24 * 60 * 60 * 1000));
    return Math.ceil((pastDays + firstDayOfYear.getDay() + 1) / 7);
  };

  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());
  const [currentWeekNumber, setCurrentWeekNumber] = useState(getWeekNumber(new Date()));
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
  const isFetchingRef = useRef(false);
  const fetchWeeksData = useCallback(async () => {
    if (isFetchingRef.current) return;
    isFetchingRef.current = true;
    try {
      const employeeId = user?.employeeId ?? "";
      let currentWeekNumber = getISOWeek(currentMonth);
      let startWeek = currentWeekNumber;
      let endWeek = Math.max(1, startWeek - WEEKS_PER_PAGE + 1);

      const weekPromises = [];
      for (let i = startWeek; i >= endWeek; i--) {
        weekPromises.push(getWeekDate(employeeId, i));
      }

      const responses = await Promise.all(weekPromises);
      const weeks = responses.map((res) => res.data);
      if (JSON.stringify(weeks) !== JSON.stringify(weeksData)) {
        setWeeksData(weeks);
      }

      setIsLastPage(currentPage === 0);
    } catch (error) {
      console.error("Error fetching weeks data", error);
    } finally {
      isFetchingRef.current = false;
    }
  }, [currentMonth, currentPage]);

  const fetchMonthData = useCallback(async () => {
    try {
      const formattedMonth = format(currentMonth, "yyyy-MM-dd");
      const employeeId = user?.employeeId ?? "";
      const monthResponse = await getMonthLogs(employeeId, formattedMonth);

      const DateFormatConvert = format(parse(monthResponse.data.month, "yyyy-MM", new Date()), "MMMM yyyy")
      setMonthLogs(DateFormatConvert)
      setTotalMonthlyHours(monthResponse.data.totalMonthHours);
    } catch (error) {
      console.error("Error fetching weeks data", error);
    }
  }, [currentMonth])

  const handlePreviousMonth = () => {
    const newMonth = subMonths(currentMonth, 1);
    setCurrentMonth(newMonth);
    setCurrentWeekNumber(getWeekNumber(newMonth));
  };

  const handleNextMonth = () => {
    const newMonth = addMonths(currentMonth, 1);
    setCurrentMonth(newMonth);
    setCurrentWeekNumber(getWeekNumber(newMonth));
  };

  useEffect(() => {
    fetchWeeksData();
  }, [fetchWeeksData])

  useEffect(() => {
    fetchMonthData();
  }, [fetchMonthData]);


  const handleWeekClick = (weekNumber: number, startOfWeek: string, dailyLogsData: DailyLog[]) => {
    setExpandedWeeks((prev) => ({ ...prev, [weekNumber]: !prev[weekNumber] }));

    const generatedDays = generateWeekDays(startOfWeek);
    const tempDailyLogs: DailyLogsState = {};
    generatedDays.forEach((day) => (tempDailyLogs[day.dateISO] = []));
    dailyLogsData.forEach((log) => {
      tempDailyLogs[log.logDate]?.push(log);
    });
    setDailyLogs(tempDailyLogs);
  };

  const handleDayClick = (day: any) => {
    setExpandedDays((prev) => ({ ...prev, [day]: !prev[day] }));
    setSelectedDate(day);
    setLogEntries((prev) => ({
      ...prev,
      [day]: prev[day] || [],
    }));
  };

  const handleEditClick = (logDate: string, index: number, initialHours: number) => {
    setEditingLog({ logDate, index });
    setEditedHours(initialHours);
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
    console.log("clicked")
    return (
      logEntries.length > 0 &&
      <FormContainer>
        {Array.isArray(logEntries) && logEntries.map((entry, index) => (
          <div key={index} className="Form_Row">
            <select
              value={entry.projectId}
              onChange={(e) => handleInputChange(index, "projectId", e.target.value)}
            >
              <option value="">Select Project</option>
              <option value="Beeja">Beeja</option>
              <option value="Project 2">Project 2</option>
            </select>
            <select
              value={entry.contractId}
              onChange={(e) => handleInputChange(index, "contractId", e.target.value)}
            >
              <option value="">Select Contract</option>
              <option value="Contract ">Contract </option>
              <option value="Contract 2">Contract 2</option>
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
            <button onClick={handleSaveLogEntries}>Save</button>
          </div>
        ))}

      </FormContainer>
    );
  }

  const handleSaveLogEntries = async () => {
    const convertDecimalToTime = (decimal: number) => {
      if (isNaN(decimal) || decimal === null) {
        return "00:00";
      }
      const hours = Math.floor(decimal);
      const minutes = Math.round((decimal - hours) * 60);
      return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}`;
    };

    const newEntries = logEntries.map((entry) => {
      const decimalHours = Number(entry.loghour) || 0;

      return {
        projectId: entry.projectId,
        contractId: entry.contractId,
        description: entry.description,
        loghour: convertDecimalToTime(decimalHours),
        loggedHours: decimalHours,
        date: selectedDate,
      };
    });

    const formattedData = {
      employeeId: user?.employeeId ?? "",
      logHours: newEntries,
    };

    try {
      const response = await PostLogHours(formattedData);
      console.log("Response:", response);
      setAddButtonClicked(false);
      setLogEntries([]);

      setDailyLogs((prevLogs: any) => ({
        ...prevLogs,
        [String(selectedDate)]: [
          ...(prevLogs[String(selectedDate)] || []),
          ...newEntries.map((entry) => ({
            ...entry,
            loghour: entry.loghour,
            loggedHours: entry.loggedHours,
          })),
        ],
      }));

      return response;
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };
  const { t } = useTranslation();

  return (
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
            <SearchBox>
              <SearchInput type="text" placeholder="Search for any thing" />
              <span>
                <SearchSVG />
              </span>
            </SearchBox>
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
          <div className="Export">
            Export
          </div>
        </div>

        <MonthHoursContainer>
          <MonthBox>
            {t('Month')}: <span>{monthLogs}</span>
          </MonthBox>
          <HoursBox>
            {t('Total Hours')}: <span>{totalMonthlyHours} hrs</span>
          </HoursBox>
        </MonthHoursContainer>

        {weeksData.map((weekData) => {
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
                                  <th>Action</th>
                                </tr>
                              </thead>
                              <tbody>

                                {dailyLogs[day.dateISO] && dailyLogs[day.dateISO].length > 0 ? (
                                  dailyLogs[day.dateISO].map((log, logIndex) => (
                                    <tr key={logIndex}>
                                      <td>{log.projectId}</td>
                                      <td>{log.contractId}</td>
                                      <td>
                                        {editingLog?.logDate === day.dateISO && editingLog.index === logIndex ? (
                                          <input
                                            type="number"
                                            value={editedHours}
                                            onChange={(e) => setEditedHours(Number(e.target.value))}
                                          />
                                        ) : (
                                          `${log.loggedHours} hrs`
                                        )}
                                      </td>
                                      <td>{log.description}</td>
                                      <td className="Action">
                                        {editingLog?.logDate === day.dateISO && editingLog.index === logIndex ? (
                                          <SaveButton>Save</SaveButton>
                                        ) : (
                                          <EditButton onClick={() => handleEditClick(day.dateISO, logIndex, log.loggedHours)}>
                                            <EditWhitePenSVG />
                                          </EditButton>
                                        )}
                                      </td>
                                    </tr>
                                  ))
                                ) : (
                                  <tr>
                                    <td className="no-entries">
                                      No entries yet.
                                    </td>
                                  </tr>
                                )}
                                <tr>
                                  <td colSpan={5} style={{ textAlign: "right", paddingTop: "10px", paddingRight: "10px" }}>
                                    <AddButton
                                      onClick={() => {
                                        console.log("Clicked Date:", day.dateISO);
                                        if (selectedDate === day.dateISO && addButtonClicked) {
                                          setAddButtonClicked(false);
                                          setLogEntries([]);
                                        } else {
                                          setSelectedDate(day.dateISO);
                                          setLogEntries([{ projectId: "", contractId: "", loghour: "", description: "" }]);
                                          setAddButtonClicked(true);
                                        }
                                      }}
                                    >
                                      +
                                    </AddButton>
                                  </td>
                                </tr>
                                {selectedDate === day.dateISO && addButtonClicked && (
                                  <tr>
                                    <td colSpan={5}>
                                      {addButtonEntries()}
                                    </td>
                                  </tr>
                                )}
                              </tbody>
                            </StyledTable>
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
    </TimesheetContainer >
  );
};

export default Timesheet;