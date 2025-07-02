import { months } from './monthsConstants';

const currentMonth = new Date().getMonth();

export function formatDate(inputDateString: string): string {
  const originalDate = new Date(inputDateString);
  const options: Intl.DateTimeFormatOptions = {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  };
  return new Intl.DateTimeFormat('en-US', options).format(originalDate);
}

export const getCurrentDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export const getCurrentTwoYears = () => {
  const currentYear = new Date().getFullYear();
  const previousYear = currentYear - 1;
  return [currentYear, previousYear];
};

export const getCurrentThreeMonths: string[] = [];
for (let i = 0; i < 3; i++) {
  const monthIndex = (currentMonth - i + 12) % 12;
  getCurrentThreeMonths.push(months[monthIndex]);
}

export function formatDateYYYYMMDD(dateString: string): string {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');

  return `${year}-${month}-${day}`;
}
export function formatDateDDMMYYYY(dateString: string): string {
  const date = new Date(dateString);
  if (!dateString) {
    return 'dd-mm-yyyy';
  }
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');

  return `${day}-${month}-${year}`;
}

export function compareTwoDates(date1: string, date2: any): boolean {
  const jsDate1 = new Date(date1);
  const jsDate2 = new Date(date2);

  return jsDate1 >= jsDate2;
}

export function monthsDiff(startDate: Date, endDate: Date): number {
  const startYear = startDate.getFullYear();
  const startMonth = startDate.getMonth();

  const endYear = endDate.getFullYear();
  const endMonth = endDate.getMonth();

  return (endYear - startYear) * 12 + (endMonth - startMonth);
}

export function formatDateDDMMYYYYHHMM(dateString: string): string {
  const date = new Date(dateString);
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${day}-${month}-${year} ${hours}:${minutes}`;
}

export const safeFormatDate = (
  dateString: string | null | undefined
): string => {
  if (!dateString) return '-';

  try {
    return formatDate(dateString);
  } catch (error) {
    return '-';
  }
};
