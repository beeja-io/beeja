import { useState } from 'react';
import { ChevronleftSVG, ChevronRightSVG } from '../../svgs/CommonSvgs.svs';
import {
  Weekday,
  CalendarContainer,
  CalendarHeader,
  HeaderLine,
  MonthContainer,
  MonthHeader,
  MonthName,
  WeekdaysContainer,
  CalendarGrid,
} from '../../styles/DatePickerStyles.style';
import {
  DayCell,
  ArrowButton,
} from '../../styles/CalenderComponentStyles.style';

interface CalendarProps {
  minDate?: Date | null;
  maxDate?: Date | null;
  title: string;
  handleDateInput: (date: Date | null) => void;
  handleCalenderChange: (date: Date) => void;
  selectedDate: Date | null;
}

const Calendar: React.FC<CalendarProps> = ({
  minDate,
  maxDate,
  title,
  handleDateInput,
  selectedDate,
  handleCalenderChange,
}) => {
  const initialDate = selectedDate || new Date();
  const [currentDate, setCurrentDate] = useState(initialDate);

  const renderWeekdays = () => {
    const weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return weekdays.map((day) => <Weekday key={day}>{day}</Weekday>);
  };

  const daysInMonth = (date: Date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  };

  const dayOfMonth = (date: Date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay();
  };

  const handlePrevMonth = () => {
    const prevMonth = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() - 1,
      1
    );

    const lastDateOfMonth = daysInMonth(prevMonth);

    const selectedDate = new Date(
      prevMonth.getFullYear(),
      prevMonth.getMonth(),
      Math.min(currentDate.getDate(), lastDateOfMonth)
    );

    if (
      minDate &&
      prevMonth.getMonth() === minDate.getMonth() &&
      prevMonth.getFullYear() === minDate.getFullYear()
    ) {
      setCurrentDate(minDate);
    } else if (
      !minDate ||
      prevMonth > new Date(minDate.getFullYear(), minDate.getMonth(), 1)
    ) {
      setCurrentDate(selectedDate);
    }
  };

  const handleNextMonth = () => {
    const nextMonth = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
      1
    );

    const lastDateOfMonth = daysInMonth(nextMonth);

    const selectedDate = new Date(
      nextMonth.getFullYear(),
      nextMonth.getMonth(),
      Math.min(currentDate.getDate(), lastDateOfMonth)
    );

    if (
      maxDate &&
      nextMonth.getMonth() === maxDate.getMonth() &&
      nextMonth.getFullYear() === maxDate.getFullYear()
    ) {
      setCurrentDate(maxDate);
    } else if (
      !maxDate ||
      nextMonth <= new Date(maxDate.getFullYear(), maxDate.getMonth(), 1)
    ) {
      setCurrentDate(selectedDate);
    }
  };

  const handleDayClick = (day: number) => {
    const selectedDay = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      day
    );

    setCurrentDate(selectedDay);
    if (handleDateInput) {
      handleDateInput(selectedDay);
    }

    // handleDateInput(selectedDay);
    if (handleCalenderChange) {
      handleCalenderChange(selectedDay);
    }
  };

  const renderCalendar = () => {
    const days = [];
    const daysCount = daysInMonth(currentDate);
    const firstDay = dayOfMonth(currentDate);
    const totalCells = 42;

    const prevMonthDays = firstDay === 0 ? 6 : firstDay - 1;
    const nextMonthDays = totalCells - (prevMonthDays + daysCount);

    const prevMonthEndDate = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      0
    ).getDate();

    for (
      let i = prevMonthEndDate - prevMonthDays + 1;
      i <= prevMonthEndDate;
      i++
    ) {
      days.push(
        <DayCell key={`prev-${i}`} isDisabled={true}>
          {i}
        </DayCell>
      );
    }

    for (let day = 1; day <= daysCount; day++) {
      const currentDay = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        day
      );
      const isBeforeMinDate = minDate && currentDay < minDate;
      const isAfterMaxDate = maxDate && currentDay > maxDate;
      const isDisabled = !!isBeforeMinDate || !!isAfterMaxDate;
      const isCurrentDaySelected = currentDate && currentDate.getDate() === day;
      const isSelected = isCurrentDaySelected && !isDisabled;

      days.push(
        <DayCell
          key={`current-${day}`}
          isDisabled={isDisabled}
          isSelected={
            !!isSelected ||
            !!(currentDate && currentDay.getTime() === currentDate.getTime())
          }
          onClick={isDisabled ? undefined : () => handleDayClick(day)}
          className={isSelected ? 'selectedDate' : ''}
        >
          {day}
        </DayCell>
      );
    }

    for (let i = 1; i <= nextMonthDays; i++) {
      days.push(
        <DayCell key={`next-${i}`} isDisabled={true}>
          {i}
        </DayCell>
      );
    }

    return days;
  };

  return (
    <CalendarContainer>
      <CalendarHeader>
        {title}
        <HeaderLine />
      </CalendarHeader>
      <MonthContainer>
        <MonthHeader>
          <ArrowButton
            isDisabled={
              (minDate &&
                currentDate.getFullYear() === minDate.getFullYear() &&
                currentDate.getMonth() <= minDate.getMonth()) ||
              false
            }
            onClick={handlePrevMonth}
          >
            <ChevronleftSVG />
          </ArrowButton>
          <MonthName>
            {`${new Intl.DateTimeFormat('en-US', {
              month: 'long',
              year: 'numeric',
            }).format(currentDate)}`}
          </MonthName>
          <ArrowButton
            isDisabled={
              (maxDate &&
                currentDate.getFullYear() === maxDate.getFullYear() &&
                currentDate.getMonth() >= maxDate.getMonth()) ||
              false
            }
            onClick={handleNextMonth}
          >
            <ChevronRightSVG />
          </ArrowButton>
        </MonthHeader>
        <WeekdaysContainer>{renderWeekdays()}</WeekdaysContainer>
        <CalendarGrid>{renderCalendar()}</CalendarGrid>
      </MonthContainer>
    </CalendarContainer>
  );
};

export default Calendar;
