import React from 'react';
import { format } from 'date-fns';
import {
  WeekContainer,
  WeekSubContainer,
  WeekTitle,
  TotalWeekHoursContainer,
  WeeklyLogs,
  RotateArrow,
  DaysContainer,
  WeekdayRow,
  SingleRowContainer,
  DayText,
  LoggedHours,
  WeekBodyContainer,
} from '../styles/TimeSheetStyles.style';
import { useTranslation } from 'react-i18next';
import {
  DailyLog,
  WeekLog,
  TimeSheetLogState,
  TimeSheetHandlers,
  TimeSheetReferenceData,
} from '../entities/TimeSheetEntity';
import DailyLogTable from './DailyLogTable.screen';
import { ArrowDownSVG } from '../svgs/CommonSvgs.svs';

interface WeekRowTimesheetProps {
  compositeKey: string;
  weekData: WeekLog;
  currentMonth: Date;
  expandedWeeks: Record<string, boolean>;
  expandedDays: Record<string, boolean>;
  onWeekToggle: (compositeKey: string) => void;
  onDayClick: (day: any) => void;

  logState: TimeSheetLogState;
  handlers: TimeSheetHandlers;
  referenceData: TimeSheetReferenceData;
}

const WeekRowTimesheet: React.FC<WeekRowTimesheetProps> = ({
  compositeKey,
  weekData,
  currentMonth,
  expandedWeeks,
  expandedDays,
  onWeekToggle,
  onDayClick,
  logState,
  handlers,
  referenceData,
}) => {
  const isActive = !!expandedWeeks[compositeKey];
  const { t } = useTranslation();

  const generateWeekDays = (startDate: string) => {
    const start = new Date(startDate + 'T00:00:00');
    if (isNaN(start.getTime())) return [];

    const days: any[] = [];
    for (let i = 0; i < 7; i++) {
      const day = new Date(start);
      day.setDate(start.getDate() + i);

      if (isNaN(day.getTime())) continue;

      const dateISO = format(day, 'yyyy-MM-dd');
      const isOtherMonth =
        day.getMonth() !== currentMonth.getMonth() ||
        day.getFullYear() !== currentMonth.getFullYear();

      const totalLoggedForDay =
        weekData.dailyLogs
          ?.filter((l: DailyLog) => l.logDate === dateISO)
          .reduce((sum: number, l: DailyLog) => sum + l.loggedHours, 0) || 0;
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const isFuture = day.getTime() > today.getTime();

      days.push({
        dateISO,
        dayName: format(day, 'EEE'),
        formattedDate: format(day, 'dd/MM/yy'),
        isWeekend: day.getDay() === 6 || day.getDay() === 0,
        isOtherMonth,
        totalLoggedForDay,
        isFuture,
      });
    }
    return days;
  };

  const days = generateWeekDays(weekData.startOfWeek);

  return (
    <WeekContainer>
      <WeekSubContainer
        isActive={isActive}
        onClick={() => {
          onWeekToggle(compositeKey);
        }}
      >
        <WeekTitle>
          {t('Week')}
          {weekData.weekNumber}
          <span>
            {' - '}[
            {format(new Date(weekData.startOfWeek + 'T00:00:00'), 'MMM dd')}
            {' - '}
            {format(new Date(weekData.endOfWeek + 'T00:00:00'), 'MMM dd, yyyy')}
            ]
          </span>
        </WeekTitle>

        <TotalWeekHoursContainer>
          <WeeklyLogs>
            {t('Weekly_Logs')} <span>{weekData.totalWeekHours} hrs</span>
          </WeeklyLogs>
          <RotateArrow isExpanded={isActive}>
            <ArrowDownSVG />
          </RotateArrow>
        </TotalWeekHoursContainer>
      </WeekSubContainer>

      {isActive && (
        <WeekBodyContainer>
          <DaysContainer>
            {days.map((day) => {
              const isSelected = !!expandedDays[day.dateISO];
              return (
                <WeekdayRow key={day.dateISO}>
                  <SingleRowContainer
                    disabled={day.isOtherMonth}
                    isWeekend={day.isWeekend}
                    isSelected={isSelected}
                    isFuture={day.isFuture}
                    onClick={() =>
                      !day.isOtherMonth && !day.isFuture && onDayClick(day)
                    }
                  >
                    <DayText
                      isWeekend={day.isWeekend && !day.isOtherMonth}
                      isFuture={day.isFuture}
                    >
                      {day.dayName}, {day.formattedDate}
                    </DayText>

                    <LoggedHours>
                      {t('Logged_Hours')}
                      <span>{day.totalLoggedForDay.toFixed(2)} hrs</span>
                    </LoggedHours>
                  </SingleRowContainer>

                  {expandedDays[day.dateISO] && !day.isOtherMonth && (
                    <DailyLogTable
                      dateISO={day.dateISO}
                      dailyLogs={weekData.dailyLogs.filter(
                        (log: DailyLog) => log.logDate === day.dateISO
                      )}
                      logState={logState}
                      handlers={handlers}
                      referenceData={referenceData}
                    />
                  )}
                </WeekdayRow>
              );
            })}
          </DaysContainer>
        </WeekBodyContainer>
      )}
    </WeekContainer>
  );
};
export default WeekRowTimesheet;
