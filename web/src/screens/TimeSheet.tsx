import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  format,
  subMonths,
  addMonths,
  startOfWeek,
  addDays,
  getISOWeek,
  getISOWeekYear,
} from 'date-fns';
import {
  deleteLog,
  fetchMonthLogs,
  getContractDropdownBasedOnUserSelection,
  getProjectDropdownBasedOnUser,
  PostLogHours,
  updateLog,
} from '../service/axiosInstance';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';
import SpinAnimation from '../components/loaders/SprinAnimation.loader';
import CenterModal from '../components/reusableComponents/CenterModal.component';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

import {
  TimesheetContainer,
  MonthHoursContainer,
  MonthBox,
  HoursBox,
  PaginationContainer,
  PaginationButton,
} from '../styles/TimeSheetStyles.style';

import {
  Contract,
  DailyLog,
  LogEntry,
  WeekLog,
  APITimesheet,
  APIDay,
  APIWeek,
} from '../entities/TimeSheetEntity';
import { ProjectEntity } from '../entities/ProjectEntity';
import WeekRowTimesheet from './WeekRowTimeSheet.screen';

const contractsCache: Record<string, Contract[]> = {};

const fetchContractsForProjectCached = async (
  projectId: string
): Promise<Contract[]> => {
  if (contractsCache[projectId]) {
    return contractsCache[projectId];
  }

  const res = await getContractDropdownBasedOnUserSelection(projectId);
  const contracts = res.data as Contract[];
  contractsCache[projectId] = contracts;
  return contracts;
};

const Timesheet: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [weeksData, setWeeksData] = useState<WeekLog[]>([]);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [monthLabel, setMonthLabel] = useState('');
  const [monthlyTotalHours, setMonthlyTotalHours] = useState(0);
  const [projectOptions, setProjectOptions] = useState<ProjectEntity[]>([]);

  const [expandedWeeks, setExpandedWeeks] = useState<Record<string, boolean>>(
    {}
  );
  const [expandedDays, setExpandedDays] = useState<Record<string, boolean>>({});
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [logEntries, setLogEntries] = useState<LogEntry[]>([
    { projectId: '', contractId: '', loghour: '', description: '' },
  ]);
  const [addButtonClicked, setAddButtonClicked] = useState(false);
  const [isEditingMode, setIsEditingMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showSaveConfirmation, setShowSaveConfirmation] = useState(false);
  const [originalLogEntry, setOriginalLogEntry] = useState<LogEntry | null>(
    null
  );
  const [confirmType, setConfirmType] = useState<null | 'discard' | 'save'>(
    null
  );

  const [contractLookup, setContractLookup] = useState<Record<string, string>>(
    {}
  );

  const [successToast, setSuccessToast] = useState<{
    heading: string;
    body: string;
  } | null>(null);

  const [errorToast, setErrorToast] = useState<{
    heading: string;
    body: string;
  } | null>(null);

  const getEmptyWeeksForMonth = (month: Date) => {
    const weeks: any[] = [];
    const year = month.getFullYear();
    const monthIndex = month.getMonth();
    const startOfMonth = new Date(year, monthIndex, 1);
    const endOfMonth = new Date(year, monthIndex + 1, 0);

    let weekStart = startOfWeek(startOfMonth, { weekStartsOn: 1 });

    while (weekStart <= endOfMonth) {
      const weekEnd = addDays(weekStart, 6);

      const startStr = format(weekStart, 'yyyy-MM-dd');
      const endStr = format(weekEnd, 'yyyy-MM-dd');
      const weekNum = getISOWeek(weekStart);
      const weekYear = getISOWeekYear(weekStart);
      const compositeKey = `${weekYear}-${weekNum}`;

      weeks.push({
        compositeKey,
        weekNumber: weekNum,
        weekYear,
        startOfWeek: startStr,
        endOfWeek: endStr,
        totalWeekHours: 0,
        dailyLogs: [] as DailyLog[],
        dailyTotals: {},
        year: weekStart.getFullYear(),
      });

      weekStart = addDays(weekStart, 7);
    }

    return weeks;
  };

  const fetchMonthData = async () => {
    try {
      setLoading(true);

      const formattedMonth = format(currentMonth, 'yyyy-MM');
      const res = await fetchMonthLogs(formattedMonth);

      setMonthlyTotalHours(res.data.monthlyTotalHours || 0);
      setMonthLabel(format(currentMonth, 'MMMM yyyy'));

      const apiWeeksMap = new Map<string, WeekLog>();
      const tempContractLookup: Record<string, string> = {};
      const projectIdsToFetch = new Set<string>();

      const weekTimesheets = (res.data.weekTimesheets || {}) as Record<
        string,
        APIWeek
      >;

      Object.values(weekTimesheets).forEach((week) => {
        const flattenedDailyLogs: DailyLog[] = [];

        Object.values(week.dailyLogs || {}).forEach((day: APIDay) => {
          (day.timesheets || []).forEach((ts: APITimesheet) => {
            const logDate = (ts.startDate || '').split('T')[0];

            flattenedDailyLogs.push({
              Id: ts.id,
              logDate,
              projectId: ts.projectId || '',
              contractId: ts.contractId || '',
              description: ts.description || '',
              loggedHours: (ts.timeInMinutes || 0) / 60,
            });

            if (ts.projectId && ts.contractId) {
              projectIdsToFetch.add(ts.projectId);
            }
          });
        });

        const key = `${week.weekYear}-${week.weekNumber}`;

        apiWeeksMap.set(key, {
          compositeKey: key,
          weekNumber: week.weekNumber,
          weekYear: week.weekYear,
          startOfWeek: week.weekStartDate,
          endOfWeek: week.weekEndDate,
          totalWeekHours: week.weeklyTotalHours || 0,
          dailyLogs: flattenedDailyLogs,
          dailyTotals: {},
          year: week.weekYear,
        });
      });

      await Promise.all(
        Array.from(projectIdsToFetch).map(async (projectId) => {
          try {
            const contracts = await fetchContractsForProjectCached(projectId);
            contracts.forEach((c) => {
              tempContractLookup[c.contractId] = c.contractTitle;
            });
          } catch (err) {
            throw new Error(
              `Contract lookup failed for project ${projectId}: ${
                err instanceof Error ? err.message : String(err)
              }`
            );
          }
        })
      );

      const allWeeks = getEmptyWeeksForMonth(currentMonth);

      const mergedWeeks: WeekLog[] = allWeeks.map((w) => {
        const apiWeek = apiWeeksMap.get(w.compositeKey);
        return apiWeek
          ? {
              ...apiWeek,
              dailyLogs: apiWeek.dailyLogs || [],
              totalWeekHours: apiWeek.totalWeekHours || 0,
            }
          : ({ ...w } as WeekLog);
      });

      setContractLookup((prev) => ({ ...prev, ...tempContractLookup }));
      setWeeksData(mergedWeeks);
    } catch (err: any) {
      setWeeksData(getEmptyWeeksForMonth(currentMonth));
      throw new Error(`Fetch error:, ${err}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMonthData();
  }, [currentMonth]);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await getProjectDropdownBasedOnUser();
        const data = response.data;
        if (Array.isArray(data)) {
          setProjectOptions(data);
        } else {
          setErrorToast({
            heading: 'Error',
            body: t('Invalid_project_data_received'),
          });
        }
      } catch (error) {
        setProjectOptions([]);
        setErrorToast({
          heading: 'Error',
          body: t('Failed_to_fetch_project_list.') + String(error),
        });
      }
    };
    fetchProjects();
  }, [t]);

  const handlePreviousMonth = () => setCurrentMonth(subMonths(currentMonth, 1));
  const handleNextMonth = () => setCurrentMonth(addMonths(currentMonth, 1));

  const handleWeekToggle = (compositeKey: string) => {
    setExpandedWeeks((prev) => {
      const isAlreadyOpen = !!prev[compositeKey];

      if (isAlreadyOpen) {
        return {};
      }

      return { [compositeKey]: true };
    });

    setExpandedDays({});
    setSelectedDate(null);
    setAddButtonClicked(false);
  };

  const handleSaveLogEntries = async () => {
    if (
      !selectedDate ||
      !logEntries[0].projectId ||
      !logEntries[0].contractId
    ) {
      setErrorToast({
        heading: 'Error',
        body: t('Please_fill_project_contract_and_hours'),
      });
      return;
    }

    const entry = logEntries[0];
    const decimalHours = Number(entry.loghour) || 0;

    const payload = {
      projectId: entry.projectId,
      contractId: entry.contractId,
      timeInMinutes: decimalHours * 60,
      description: entry.description,
      startDate: `${selectedDate}T00:00:00Z`,
    };

    try {
      setLoading(true);
      await PostLogHours(payload);
      setSuccessToast({
        heading: 'Success',
        body: t('Log_Added_Successfully'),
      });

      await fetchMonthData();

      setAddButtonClicked(false);
      setLogEntries([
        { projectId: '', contractId: '', loghour: '', description: '' },
      ]);
      setSelectedDate(null);
      setOriginalLogEntry(null);
      setShowSaveConfirmation(false);
    } catch (error) {
      setErrorToast({
        heading: 'Error',
        body: t('Failed_to_save_logs'),
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateClick = () => {
    setConfirmType('save');
    setShowSaveConfirmation(true);
  };

  const checkIsDirty = () => {
    if (logEntries.length === 0) return false;
    const current = logEntries[0];

    if (isEditingMode && originalLogEntry) {
      return (
        current.projectId !== originalLogEntry.projectId ||
        current.contractId !== originalLogEntry.contractId ||
        current.loghour !== originalLogEntry.loghour ||
        current.description !== originalLogEntry.description
      );
    } else if (!isEditingMode) {
      return !!(
        current.projectId ||
        current.contractId ||
        current.loghour ||
        current.description
      );
    }
    return false;
  };

  const handleEdit = async (log: DailyLog) => {
    if (checkIsDirty()) {
      setConfirmType('discard');
      setShowSaveConfirmation(true);
      return;
    }

    setIsEditingMode(true);
    setAddButtonClicked(false);
    setSelectedDate(log.logDate);
    const entry = {
      projectId: log.projectId,
      contractId: log.contractId,
      loghour: log.loggedHours.toString(),
      description: log.description,
      id: log.Id,
    };
    setLogEntries([entry]);
    setOriginalLogEntry(entry);
    setAddButtonClicked(true);
  };

  const handleUpdateLog = async () => {
    if (!selectedDate || !logEntries[0].projectId) return;

    const entry = logEntries[0];
    const decimalHours = Number(entry.loghour) || 0;

    const payload = {
      projectId: entry.projectId,
      contractId: entry.contractId,
      timeInMinutes: decimalHours * 60,
      description: entry.description,
      startDate: `${selectedDate}T00:00:00Z`,
    };

    try {
      setLoading(true);
      await updateLog(logEntries[0].id || '', payload);
      setSuccessToast({
        heading: 'Success',
        body: t('Log_updated_successfully'),
      });

      await fetchMonthData();

      setAddButtonClicked(false);
      setLogEntries([
        { projectId: '', contractId: '', loghour: '', description: '' },
      ]);
      setSelectedDate(null);
      setIsEditingMode(false);
      setOriginalLogEntry(null);
      setShowSaveConfirmation(false);
    } catch (error) {
      setErrorToast({
        heading: 'Error',
        body: t('Update_failed'),
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (logId: string) => {
    try {
      setLoading(true);
      await deleteLog(logId);
      setSuccessToast({
        heading: 'Success',
        body: t('Log_deleted_successfully'),
      });
      await fetchMonthData();
    } catch (error) {
      setErrorToast({
        heading: 'Error',
        body: t('Delete_failed'),
      });
    } finally {
      setLoading(false);
    }
  };

  const getProjectName = (projectId: string) =>
    projectOptions.find((p) => p.projectId === projectId)?.name || projectId;

  const getContractTitle = (contractId: string) =>
    contractLookup[contractId] || contractId;

  const handleCancel = () => {
    if (checkIsDirty()) {
      setConfirmType('discard');
      setShowSaveConfirmation(true);
    } else {
      closeForm();
    }
  };

  const closeForm = () => {
    setAddButtonClicked(false);
    setIsEditingMode(false);
    setLogEntries([
      { projectId: '', contractId: '', loghour: '', description: '' },
    ]);
    setSelectedDate(null);
    setOriginalLogEntry(null);
    setShowSaveConfirmation(false);
    setConfirmType(null);
  };

  const handleSaveConfirmed = async () => {
    if (confirmType === 'discard') {
      closeForm();
    } else {
      if (isEditingMode) {
        await handleUpdateLog();
      } else {
        await handleSaveLogEntries();
      }
    }
  };

  const goToPreviousPage = () => navigate(-1);
  if (loading) return <SpinAnimation />;

  return (
    <TimesheetContainer>
      <div className="heading">
        <span onClick={goToPreviousPage}>
          <ArrowDownSVG />
        </span>
        {t('Time_Sheet')}
      </div>

      <div className="TimesheetSubContainer">
        <div className="TimeSheet_Heading">
          <p className="TimeSheetTitle underline">{t('List_of_Time_Sheets')}</p>
        </div>

        <MonthHoursContainer>
          <MonthBox>
            {t('Month')} : <span>{monthLabel}</span>
          </MonthBox>

          <HoursBox>
            {t('Total_Hours')} : <span>{monthlyTotalHours} hrs</span>
          </HoursBox>
        </MonthHoursContainer>

        {weeksData.map((weekData) => {
          return (
            <WeekRowTimesheet
              key={weekData.compositeKey}
              compositeKey={weekData.compositeKey}
              weekData={weekData}
              currentMonth={currentMonth}
              expandedWeeks={expandedWeeks}
              expandedDays={expandedDays}
              onWeekToggle={handleWeekToggle}
              onDayClick={(day: any) =>
                setExpandedDays((prev) => {
                  const isAlreadyOpen = !!prev[day.dateISO];
                  return isAlreadyOpen ? {} : { [day.dateISO]: true };
                })
              }
              logState={{
                logEntries,
                setLogEntries,
                addButtonClicked,
                setAddButtonClicked,
                selectedDate,
                setSelectedDate,
                isEditingMode,
                setIsEditingMode,
              }}
              handlers={{
                handleSaveLogEntries,
                onDelete: handleDelete,
                onEdit: handleEdit,
                onUpdate: handleUpdateClick,
                onCancel: handleCancel,
              }}
              referenceData={{
                projectOptions,
                getProjectName,
                getContractTitle,
                setContractLookup,
                fetchContractsForProject: fetchContractsForProjectCached,
              }}
            />
          );
        })}
      </div>

      <PaginationContainer>
        <PaginationButton onClick={handlePreviousMonth}>
          <span className="leftArrow">
            <ArrowDownSVG />
          </span>
          {t('Previous')}
        </PaginationButton>
        <PaginationButton onClick={handleNextMonth}>
          {t('Next')}
          <span className="rightArrow">
            <ArrowDownSVG />
          </span>
        </PaginationButton>
      </PaginationContainer>

      {showSaveConfirmation && (
        <CenterModal
          handleModalLeftButtonClick={() => setShowSaveConfirmation(false)}
          handleModalClose={() => setShowSaveConfirmation(false)}
          handleModalSubmit={handleSaveConfirmed}
          modalHeading={
            confirmType === 'discard' ? t('Discard_Changes') : t('Save_Changes')
          }
          modalLeftButtonText="No"
          modalRightButtonText={
            confirmType === 'discard' ? t('Discard') : t('Yes')
          }
          isResponseLoading={loading}
        />
      )}

      {successToast && (
        <ToastMessage
          messageType="success"
          messageHeading={successToast.heading}
          messageBody={successToast.body}
          handleClose={() => setSuccessToast(null)}
        />
      )}

      {errorToast && (
        <ToastMessage
          messageType="error"
          messageHeading={errorToast.heading}
          messageBody={errorToast.body}
          handleClose={() => setErrorToast(null)}
        />
      )}
    </TimesheetContainer>
  );
};

export default Timesheet;
