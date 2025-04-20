import { SetStateAction, useState } from "react";
import styled from "styled-components";
import { useTranslation } from "react-i18next";
import { ChevronleftSVG, ChevronRightSVG } from "../../svgs/CommonSvgs.svs";
import {
  Weekday,
  CalendarContainer,
  CalendarHeader,
  HeaderLine,
  MonthsContainer,
  MonthContainer,
  MonthHeader,
  MonthName,
  WeekdaysContainer,
  CalendarGrid,
} from "../../styles/DatePickerStyles.style";

interface DayCellProps {
  isToday?: boolean;
  isSelected?: boolean;
  isOldDay?: boolean;
  isPrevMonthDay?: boolean; // Add this line
  onClick?: () => void;
}

const DayCell = styled.div<DayCellProps>`
  text-align: center;
  padding: 8px;
  cursor: pointer;
  color: ${(props) => (props.isSelected ? "#fff" : "#000")};
  background: ${(props) => (props.isSelected ? "red" : "transparent")};

  &:hover {
    border-radius: 10px;
    background: #005792;
    color: white;
  }

  &.selectedDate {
    border-radius: 10px;
    background: #005792;
    color: white;
  }
`;

const ArrowButton = styled.button`
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  margin-top: 15px;
  margin-right: 14px;
  margin-left: 14px;
`;

interface InlineCalendarProps {
  handleDayClick: (
    day: number,
    month: number,
    year: number,
    isSecondMonth: boolean,
    isFrom: boolean,
  ) => void;
  defaultStartDate?: number | null;
  defaultEndDate?: number | null;
}

export const InlineCalendar = ({
  handleDayClick,
  defaultStartDate,
  defaultEndDate,
}: InlineCalendarProps) => {
  const [firstMonthYear, setFirstMonthYear] = useState(
    new Date().getFullYear(),
  );
  const [firstMonth, setFirstMonth] = useState(new Date().getMonth());
  const [secondMonthYear, setSecondMonthYear] = useState(
    new Date().getFullYear(),
  );
  const [secondMonth, setSecondMonth] = useState(new Date().getMonth());
  const [currentDay] = useState(new Date().getDate());
  const [selectedFirstMonthFromDate] = useState<Date | null>(null);
  const [selectedFirstMonthToDate] = useState<Date | null>(null);
  const [selectedSecondMonthFromDate] = useState<Date | null>(null);
  const [selectedSecondMonthToDate] = useState<Date | null>(null);

  const [firstMonthSelectedDate, setFirstMonthSelectedDate] =
    useState<number>();
  const [secondMonthSelectedDate, setSecondMonthSelectedDate] =
    useState<number>();
  const renderWeekdays = () => {
    const weekdays = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]; // Start with Monday
    return weekdays.map((day) => <Weekday key={day}>{day}</Weekday>);
  };
  const isDateInRange = (
    day: number,
    month: number,
    year: number,
    selectedFromDate: Date | null,
    selectedToDate: Date | null,
  ): boolean => {
    if (!selectedFromDate || !selectedToDate) {
      return false;
    }

    const currentDate = new Date(year, month, day);
    return currentDate >= selectedFromDate && currentDate <= selectedToDate;
  };

  const renderDays = (year: number, month: number, isSecondMonth: boolean) => {
    const firstDayOfMonth = new Date(year, month, 1).getDay(); // 0-indexed
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const daysFromPrevMonth = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;

    // Old month Dates
    const createDayCell = (dayNumber: number, isOldDay: boolean) => (
      <DayCell
        style={{ color: "#A0AEC0", cursor: "not-allowed" }}
        key={`${isOldDay ? "prev" : "current"}-${dayNumber}`}
        isOldDay={isOldDay}
      >
        {dayNumber}
      </DayCell>
    );

    const blanks = Array.from({ length: daysFromPrevMonth }, (_, i) =>
      createDayCell(daysInMonth - daysFromPrevMonth + i + 1, true),
    );

    const days = Array.from({ length: daysInMonth }, (_, i) => {
      const dayNumber = i + 1;
      const isSelected =
        (isSecondMonth &&
          isDateInRange(
            dayNumber,
            month,
            year,
            selectedSecondMonthFromDate,
            selectedSecondMonthToDate,
          )) ||
        (!isSecondMonth &&
          isDateInRange(
            dayNumber,
            month,
            year,
            selectedFirstMonthFromDate,
            selectedFirstMonthToDate,
          ));
      const isCurrentMonthOldDay = i < firstDayOfMonth && !isSelected;

      return (
        <DayCell
          className={
            firstMonthSelectedDate === dayNumber ||
            defaultStartDate === dayNumber
              ? "selectedDate"
              : ""
          }
          key={dayNumber}
          isToday={dayNumber === currentDay}
          isSelected={isSelected}
          isOldDay={isCurrentMonthOldDay}
          isPrevMonthDay={i < firstDayOfMonth}
          onClick={() => {
            handleDayClick(
              dayNumber,
              month,
              year,
              isSecondMonth,
              isSecondMonth ? false : true,
            );
            setFirstMonthSelectedDate(dayNumber);
          }}
        >
          <span>{dayNumber}</span>
        </DayCell>
      );
    });

    return [...blanks, ...days];
  };

  const renderSecondMonthDays = (
    year: number,
    month: number,
    isSecondMonth: boolean,
  ) => {
    const firstDayOfMonth = new Date(year, month, 1).getDay(); // 0-indexed
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const daysFromPrevMonth = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;

    // Old month Dates
    const createDayCell = (dayNumber: number, isOldDay: boolean) => (
      <DayCell
        style={{ color: "#A0AEC0", cursor: "not-allowed" }}
        key={`${isOldDay ? "prev" : "current"}-${dayNumber}`}
        isOldDay={isOldDay}
      >
        {dayNumber}
      </DayCell>
    );

    const blanks = Array.from({ length: daysFromPrevMonth }, (_, i) =>
      createDayCell(daysInMonth - daysFromPrevMonth + i + 1, true),
    );

    const days = Array.from({ length: daysInMonth }, (_, i) => {
      const dayNumber = i + 1;
      const isSelected =
        (isSecondMonth &&
          isDateInRange(
            dayNumber,
            month,
            year,
            selectedSecondMonthFromDate,
            selectedSecondMonthToDate,
          )) ||
        (!isSecondMonth &&
          isDateInRange(
            dayNumber,
            month,
            year,
            selectedFirstMonthFromDate,
            selectedFirstMonthToDate,
          ));
      const isCurrentMonthOldDay = i < firstDayOfMonth && !isSelected;

      return (
        <DayCell
          className={
            secondMonthSelectedDate === dayNumber ||
            defaultEndDate === dayNumber
              ? "selectedDate"
              : ""
          }
          key={dayNumber}
          isToday={dayNumber === currentDay}
          isSelected={isSelected}
          isOldDay={isCurrentMonthOldDay}
          isPrevMonthDay={i < firstDayOfMonth}
          onClick={() => {
            handleDayClick(
              dayNumber,
              month,
              year,
              isSecondMonth,
              isSecondMonth ? false : true,
            );
            setSecondMonthSelectedDate(dayNumber);
          }}
        >
          <span>{dayNumber}</span>
        </DayCell>
      );
    });

    return [...blanks, ...days];
  };

  const handleMonthChange = (
    setMonth: {
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (arg0: (prevMonth: any) => number): void;
    },
    setYear: {
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      (arg0: (prevYear: any) => any): void;
    },
    currentMonth: number,
    isIncrement: boolean,
  ) => {
    setMonth((prevMonth) =>
      isIncrement ? (prevMonth + 1) % 12 : prevMonth === 0 ? 11 : prevMonth - 1,
    );
    setYear((prevYear) =>
      currentMonth === (isIncrement ? 11 : 0)
        ? isIncrement
          ? prevYear + 1
          : prevYear - 1
        : prevYear,
    );
  };

  const createMonthHandlers = (
    setMonth: {
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
    },
    setYear: {
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
      (value: SetStateAction<number>): void;
    },
    month: number,
    isIncrement: boolean,
  ) => {
    return () => handleMonthChange(setMonth, setYear, month, isIncrement);
  };
  const { t } = useTranslation();
  const handlePrevFirstMonth = createMonthHandlers(
    setFirstMonth,
    setFirstMonthYear,
    firstMonth,
    false,
  );
  const handleNextFirstMonth = createMonthHandlers(
    setFirstMonth,
    setFirstMonthYear,
    firstMonth,
    true,
  );
  const handlePrevSecondMonth = createMonthHandlers(
    setSecondMonth,
    setSecondMonthYear,
    secondMonth,
    false,
  );
  const handleNextSecondMonth = createMonthHandlers(
    setSecondMonth,
    setSecondMonthYear,
    secondMonth,
    true,
  );
  return (
    <CalendarContainer>
      <CalendarHeader>
        {t("SET_DATE")}
        <HeaderLine />
      </CalendarHeader>

      <MonthsContainer>
        <MonthContainer>
          <MonthHeader>
            <ArrowButton onClick={handlePrevFirstMonth}>
              <ChevronleftSVG />
            </ArrowButton>
            <MonthName>
              {`${new Date(firstMonthYear, firstMonth, 1).toLocaleDateString(
                "en-US",
                {
                  month: "long",
                },
              )} ${firstMonthYear}`}
            </MonthName>
            <ArrowButton onClick={handleNextFirstMonth}>
              <ChevronRightSVG />
            </ArrowButton>
          </MonthHeader>
          <WeekdaysContainer>{renderWeekdays()}</WeekdaysContainer>
          <CalendarGrid>
            {renderDays(firstMonthYear, firstMonth, true)}
          </CalendarGrid>
        </MonthContainer>

        <MonthContainer>
          <MonthHeader>
            <ArrowButton onClick={handlePrevSecondMonth}>
              <ChevronleftSVG />
            </ArrowButton>
            <MonthName>
              {`${new Date(secondMonthYear, secondMonth, 1).toLocaleDateString(
                "en-US",
                {
                  month: "long",
                },
              )} ${secondMonthYear}`}
            </MonthName>
            <ArrowButton onClick={handleNextSecondMonth}>
              <ChevronRightSVG />
            </ArrowButton>
          </MonthHeader>
          <WeekdaysContainer>{renderWeekdays()}</WeekdaysContainer>
          <CalendarGrid>
            {renderSecondMonthDays(secondMonthYear, secondMonth, false)}
          </CalendarGrid>
        </MonthContainer>
      </MonthsContainer>
    </CalendarContainer>
  );
};
