import { ExpenseAction } from '../components/reusableComponents/ExpenseListAction';
import {
  DatePicker,
  DisplayFilters,
  ExpenseFilterArea,
  ExpenseListSection,
  FilterSection,
  TableBodyRow,
  TableHead,
  TableList,
  TableListContainer,
} from '../styles/ExpenseListStyles.style';
import {
  CalenderIcon,
  CalenderIconDark,
  DeleteIcon,
  EditIcon,
} from '../svgs/ExpenseListSvgs.svg';
import { useTranslation } from 'react-i18next';
import { formatDate } from '../utils/dateFormatter';
import ZeroEntriesFound from '../components/reusableComponents/ZeroEntriesFound.compoment';
import { getAllExpenses } from '../service/axiosInstance';
import { useCallback, useEffect, useRef, useState, useMemo } from 'react';
import { VectorSVG } from '../svgs/CommonSvgs.svs';
// import InlineCalendar from '../components/reusableComponents/FromToDatePicker';
import { AllExpensesResponse } from '../entities/respponses/AllExpensesResponse';
import { Expense } from '../entities/ExpenseEntity';
import AddExpenseForm from '../components/directComponents/AddExpenseForm.component';
import CenterModalMain from '../components/reusableComponents/CenterModalMain.component';
import Calendar from '../components/reusableComponents/Calendar.component';
import { minDateOfFromCalendar } from '../constants/Constants';
import { useUser } from '../context/UserContext';
import { EXPENSE_MODULE } from '../constants/PermissionConstants';
import Pagination from '../components/directComponents/Pagination.component';
import { InfoCircleSVG } from '../svgs/NavBarSvgs.svg';
import { formatToINR } from '../utils/currencyFormattors';
import { hasPermission } from '../utils/permissionCheck';
import { OrganizationValues } from '../entities/OrgValueEntity';

type ExpenseListProps = {
  expenseCategories: OrganizationValues;
  expenseDepartments: OrganizationValues;
  expenseTypes: OrganizationValues;
  expensePaymentModes: OrganizationValues;
};
export const ExpenseList = (props: ExpenseListProps) => {
  const { user } = useUser();
  const [showFromCalendar, setShowFromCalendar] = useState(false);
  const [showToCalendar, setShowToCalendar] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalSize, setTotalSize] = useState<number | undefined>(0);
  const [totalPages, setTotalPages] = useState<number | undefined>(
    Math.ceil(totalSize ? totalSize / 10 : 0)
  );

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };
  const handleItemsPerPage = (itemsPerPage: number) => {
    setItemsPerPage(itemsPerPage);
    const totalPages =
      expenses && Math.ceil(expenses?.metadata.totalSize / itemsPerPage);
    handleTotalPages(totalPages ?? 1);
  };
  const handleTotalPages = (totalPages: number) => {
    setTotalPages(totalPages);
  };

  const calendarFromRef = useRef<HTMLDivElement>(null);
  const calendarToRef = useRef<HTMLDivElement>(null);

  const handleClickOutside = (event: MouseEvent) => {
    if (
      calendarFromRef.current &&
      !calendarFromRef.current.contains(event.target as Node)
    ) {
      setShowFromCalendar(false);
    }
    if (
      calendarToRef.current &&
      !calendarToRef.current.contains(event.target as Node)
    ) {
      setShowToCalendar(false);
    }
  };

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);

    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const handleFromCalendarClick = () => {
    setShowFromCalendar(!showFromCalendar);
  };

  const handleToCalendarClick = () => {
    setShowToCalendar(!showToCalendar);
  };

  const [fromDate, setFromDate] = useState<Date | null>();

  const currentDate = useMemo(() => new Date(), []);

  const [toDate, setToDate] = useState<Date | null>();
  const [departmentFilter, setDepartmentFilter] = useState<string>('');
  const [categoryFilter, setCategoryFilter] = useState<string>('');
  const [typeFilter, setTypeFilter] = useState<string>('');
  const [paymentModeFilter, setPaymentModeFilter] = useState<string>('');
  const [sortBy, setSortBy] = useState<string>('expenseDate');
  const [filterBasedOn, setFilterBasedOn] = useState<string>('expenseDate');
  const [sortOrder, setSortOrder] = useState<string>('false');
  const [expenses, setExpenses] = useState<AllExpensesResponse>();

  const handleDepartmentChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedDepartment = e.target.value;
    setDepartmentFilter(selectedDepartment !== '' ? selectedDepartment : '');
    setCategoryFilter('');
    setTypeFilter('');
  };
  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedCategory = e.target.value;
    setCategoryFilter(selectedCategory !== '' ? selectedCategory : '');
    setTypeFilter('');
  };

  function dateFormat(date: Date) {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const formattedDate = `${year}/${month}/${day}`;

    return formattedDate;
  }

  const [isShowFilters, setIsShowFilters] = useState(false);

  const selectedFiltersText = () => {
    const filters = [
      {
        key: 'date',
        value:
          fromDate && toDate
            ? `${formatDate(fromDate.toString())} - ${formatDate(
              toDate.toString()
            )}`
            : fromDate
              ? `From ${formatDate(fromDate.toString())}`
              : toDate
                ? `Upto ${formatDate(toDate.toString())}`
                : '',
      },
      { key: 'department', value: departmentFilter },
      { key: 'category', value: categoryFilter },
      { key: 'type', value: typeFilter },
      { key: 'paymentMode', value: paymentModeFilter },
    ];
    return (
      <ExpenseFilterArea>
        {filters.map(
          (filter) =>
            filter.value && (
              <span className="filterValues" key={filter.key}>
                {filter.value}
                <span
                  className="filterClearBtn"
                  onClick={() => clearFilters(filter.key)}
                >
                  <VectorSVG />
                </span>
              </span>
            )
        )}
      </ExpenseFilterArea>
    );
  };
  const [filteredResponseLoading, setFilteredResponseLoading] = useState(false);

  const fetchExpenses = useCallback(async () => {
    try {
      setFilteredResponseLoading(true);
      const queryParams = [];
      if (departmentFilter) {
        const encodedDepartment = encodeURIComponent(departmentFilter);
        queryParams.push(`department=${encodedDepartment}`);
      }
      if (categoryFilter) {
        const encodedCategory = encodeURIComponent(categoryFilter);
        queryParams.push(`expenseCategory=${encodedCategory}`);
      }
      if (typeFilter) queryParams.push(`expenseType=${typeFilter}`);
      if (paymentModeFilter)
        queryParams.push(`modeOfPayment=${paymentModeFilter}`);
      if (fromDate) queryParams.push(`startDate=${dateFormat(fromDate)}`);
      if (toDate) {
        const toDateCopy = new Date(toDate);
        toDateCopy.setDate(toDateCopy.getDate());
        queryParams.push(`endDate=${dateFormat(toDateCopy)}`);
      }
      if (itemsPerPage) {
        queryParams.push(`pageSize=${itemsPerPage}`);
      }
      if (currentPage) {
        queryParams.push(`pageNumber=${currentPage}`);
      }
      queryParams.push(`sortBy=${sortBy}`);
      queryParams.push(`filterBasedOn=${filterBasedOn}`);
      queryParams.length > 4 ? setIsShowFilters(true) : setIsShowFilters(false);

      queryParams.push(`ascending=${sortOrder}`);
      const filteredParams = queryParams.filter((param) => param.length > 0);

      const url =
        filteredParams.length > 0
          ? `/expenses/v1?${filteredParams.join('&')}`
          : '/expenses/v1';

      const response = await getAllExpenses(url);
      const metadata = response.data.metadata;
      const finalResponse: AllExpensesResponse = {
        expenses: response.data.expenses,
        metadata: metadata,
      };
      const totalPages = Math.ceil(
        response.data.metadata.totalSize / itemsPerPage
      );
      setTotalSize(metadata.totalSize);
      handleTotalPages(totalPages ?? 1);
      setExpenses(finalResponse);
      setFilteredResponseLoading(false);
    } catch (error) {
      setFilteredResponseLoading(false);
      throw new Error('Error fetching expenses:' + error);
    }
  }, [
    categoryFilter,
    typeFilter,
    paymentModeFilter,
    fromDate,
    toDate,
    itemsPerPage,
    currentPage,
    sortBy,
    filterBasedOn,
    sortOrder,
    departmentFilter,
  ]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  const [maxToDate, setMaxToDate] = useState<Date | null>(new Date());

  // Use currentDate instead of the original currentDate state variable
  useEffect(() => {
    if (fromDate && fromDate < currentDate) {
      setMaxToDate(currentDate);
    }
  }, [fromDate, currentDate]);

  const handleDateInput = (selectedDate: Date | null, isFrom: boolean) => {
    if (isFrom) {
      setFromDate(selectedDate);
      setShowFromCalendar(false);
    } else {
      setToDate(selectedDate);
      setShowToCalendar(false);
    }
  };

  const clearFilters = (filterName: string) => {
    setCurrentPage(1);
    if (filterName === 'clearAll') {
      setDepartmentFilter('');
      setCategoryFilter('');
      setTypeFilter('');
      setPaymentModeFilter('');
      setFromDate(null);
      setToDate(null);
    }
    if (filterName === 'department') {
      setDepartmentFilter('');
    }
    if (filterName === 'category') {
      setCategoryFilter('');
    }
    if (filterName === 'type') {
      setTypeFilter('');
    }
    if (filterName === 'paymentMode') {
      setPaymentModeFilter('');
    }
    if (filterName === 'date') {
      setFromDate(null);
      setToDate(null);
    }
  };

  const Actions = [
    ...(user && hasPermission(user, EXPENSE_MODULE.UPDATE_EXPENSE)
      ? [{ title: 'Edit', svg: <EditIcon /> }]
      : []),
    ...(user && hasPermission(user, EXPENSE_MODULE.DELETE_EXPENSE)
      ? [{ title: 'Delete', svg: <DeleteIcon /> }]
      : []),
  ];

  const { t } = useTranslation();

  const [expenseToBePreviewed, setExpenseToBePreviewed] = useState<Expense>();
  const handleExpenseToBePreviewed = (expense: Expense) => {
    setExpenseToBePreviewed(expense);
    handleIsExpensePreviewModalOpen();
  };
  const [isExpensePreviewModalOpen, setIsExpensePreviewModalOpen] =
    useState(false);
  const handleIsExpensePreviewModalOpen = () => {
    setIsExpensePreviewModalOpen(!isExpensePreviewModalOpen);
  };

  const [modeOfModal, setModeOfModal] = useState('Expense Preview');
  const handleSetModeOfModal = (modalTitle: string) => {
    setModeOfModal(modalTitle);
  };

  return (
    <ExpenseListSection>
      <div className="mainDiv">
        <div className="Expense_Heading">
          <p className="expenseListTitle underline">{t('LIST_OF_EXPENSES')}</p>
        </div>
        <FilterSection>
          <div ref={calendarFromRef} style={{ position: 'relative' }}>
            <DatePicker onClick={handleFromCalendarClick}>
              <span className="dateName">
                <span className="dateChild">
                  {fromDate
                    ? `${t('FROM')} ${formatDate(fromDate.toString())}`
                    : `${t('FROM_DATE')}`}
                </span>
                <span className="calenderIcon">
                  <CalenderIconDark />
                </span>
              </span>
            </DatePicker>

            {showFromCalendar && (
              <div className="filterCalender">
                <Calendar
                  title={t('FROM_DATE')}
                  minDate={minDateOfFromCalendar}
                  maxDate={currentDate}
                  handleDateInput={(selectedDate) => {
                    handleDateInput(selectedDate, true);
                    setCurrentPage(1);
                  }}
                  selectedDate={fromDate ? fromDate : new Date()}
                  handleCalenderChange={function (): void {
                    throw new Error('Function not implemented.');
                  }}
                />
              </div>
            )}
          </div>
          <div ref={calendarToRef} style={{ position: 'relative' }}>
            <DatePicker onClick={handleToCalendarClick}>
              <span className="dateName">
                <span className="dateChild">
                  {toDate
                    ? `${t('TO')} ${formatDate(toDate.toString())}`
                    : t('TO_DATE')}
                </span>
                <span className="calenderIcon">
                  <CalenderIconDark />
                </span>
              </span>
            </DatePicker>

            {showToCalendar && (
              <div className="filterCalender">
                <Calendar
                  title={t('TO_DATE')}
                  minDate={fromDate}
                  maxDate={maxToDate}
                  handleDateInput={(selectedDate) => {
                    handleDateInput(selectedDate, false);
                    setCurrentPage(1);
                  }}
                  selectedDate={toDate ? toDate : new Date()}
                  handleCalenderChange={() => { }}
                />
              </div>
            )}
          </div>
          <select
            className="selectoption"
            name="Department"
            value={departmentFilter}
            onChange={(e) => {
              handleDepartmentChange(e);
              setCurrentPage(1);
            }}
          >
            <option value="">{t('DEPARTMENT')}</option>
            {props.expenseDepartments.values?.map((department) => (
              <option key={department.value} value={department.value}>
                {department.value}
              </option>
            ))}
          </select>

          <select
            className="selectoption"
            name="Expensetype"
            value={categoryFilter}
            onChange={(e) => {
              handleCategoryChange(e);
              setCurrentPage(1);
            }}
          >
            <option value="">{t('EXPENSE_CATEGORY')}</option>
            {props.expenseCategories.values?.map((category) => (
              <option key={category.value} value={category.value}>
                {category.value}
              </option>
            ))}
          </select>
          <select
            className="selectoption largeSelectOption"
            name="type"
            value={typeFilter}
            onChange={(e) => {
              setTypeFilter(e.target.value != '' ? e.currentTarget.value : '');
              setCurrentPage(1);
            }}
          >
            <option value="">{t('EXPENSE_TYPE')}</option>
            {props.expenseTypes.values?.map((type) => (
              <option key={type.value} value={type.value}>
                {type.value}
              </option>
            ))}
          </select>
          <select
            className="selectoption largeSelectOption"
            name="modeOfPayment"
            value={paymentModeFilter}
            onChange={(e) => {
              setPaymentModeFilter(
                e.target.value != '' ? e.currentTarget.value : ''
              );
              setCurrentPage(1);
            }}
          >
            <option value="">{t('MODE_OF_PAYMENT')}</option>
            {/* FIXME - fetch from API Based on Country */}
            {props.expensePaymentModes.values?.map((type) => (
              <option key={type.value} value={type.value}>
                {type.value}
              </option>
            ))}
          </select>
        </FilterSection>
        <div className="right">
          <DisplayFilters>
            <label>{t('FILTERS')} </label>
            {isShowFilters ? (
              <>
                {selectedFiltersText() && (
                  <span className="filterText">{selectedFiltersText()}</span>
                )}
                <span
                  onClick={() => clearFilters('clearAll')}
                  className="clearFilters"
                >
                  {t('CLEAR_FILTERS')}
                </span>
              </>
            ) : (
              <span className="noFilters">{t('NO_FILTERS_APPLIED')}</span>
            )}
          </DisplayFilters>
          <span className="expenseListTitle amount">
            {t('TOTAL_AMOUNT')}{' '}
            <span className="blackText">
              ₹{' '}
              {expenses?.metadata.totalAmount !== undefined
                ? formatToINR(
                  expenses.metadata.totalAmount.toFixed(
                    2
                  ) as unknown as number
                )
                : '0.00'}
            </span>
          </span>
        </div>
        <DisplayFilters>
          <label>{t('SORT_BY')} </label>
        </DisplayFilters>
        <FilterSection>
          <select
            className="selectoption largeSelectOption"
            name="sortBy"
            value={sortBy}
            onChange={(e) => {
              setSortBy(e.target.value != '' ? e.currentTarget.value : '');
              setFilterBasedOn(
                e.target.value != '' ? e.currentTarget.value : ''
              );
              setCurrentPage(1);
            }}
          >
            <option value="expenseDate" selected>
              {t('EXPENSE_DATE')}
            </option>
            <option value="requestedDate">{t('REQUESTED_DATE')}</option>
            <option value="paymentDate">{t('PAYMENT_DATE')}</option>
            <option value="created_at">{t('CREATED_DATE')}</option>
          </select>

          <select
            className="selectoption largeSelectOption"
            name="sortBy"
            value={sortOrder}
            onChange={(e) => {
              setSortOrder(e.target.value != '' ? e.currentTarget.value : '');
              setCurrentPage(1);
            }}
          >
            <option value="false">{t('NEWEST_TO_OLDEST')}</option>
            <option value="true" selected>
              {t('OLDEST_TO_NEWEST')}
            </option>
          </select>
        </FilterSection>
        <br />
        {fromDate == null && toDate == null && (
          <span className="noFilters noMargin">
            <InfoCircleSVG />
            {`Showing current month expenses (sorted based on ${sortBy === 'expenseDate'
                ? t('EXPENSE_DATE')
                : sortBy === 'requestedDate'
                  ? t('REQUESTED_DATE')
                  : sortBy === 'paymentDate'
                    ? t('PAYMENT_DATE')
                    : t('CREATED_DATE')
              })`}
          </span>
        )}
        <TableListContainer style={{ marginTop: 0 }}>
          {expenses && expenses.expenses.length === 0 ? (
            <ZeroEntriesFound
              heading="THERE_IS_NO_EXPENSE_HISTORY_FOUND"
              message="YOU_HAVE_NOT_ADDED_ANY_EXPENSES"
            />
          ) : (
            <TableList>
              <TableHead>
                <tr style={{ textAlign: 'left', borderRadius: '10px' }}>
                  <th>{t('DEPARTMENT')}</th>
                  <th>{t('EXPENSE_CATEGORY')}</th>
                  <th>{t('EXPENSE_TYPE')}</th>
                  <th>{t('AMOUNT')}</th>
                  <th>{t('BY_WHOM')}</th>
                  <th>{t('MODE_OF_PAYMENT')}</th>
                  <th>{t('EXPENSE_DATE')}</th>
                  <th>{t('PAYMENT_SETTLED')}</th>
                  {user &&
                    (hasPermission(user, EXPENSE_MODULE.UPDATE_EXPENSE) ||
                      hasPermission(user, EXPENSE_MODULE.DELETE_EXPENSE)) && (
                      <th>{t('ACTION')}</th>
                    )}
                </tr>
              </TableHead>
              {filteredResponseLoading ? (
                <>
                  {[...Array(6).keys()].map((rowIndex) => (
                    <TableBodyRow key={rowIndex}>
                      {[...Array(9).keys()].map((cellIndex) => (
                        <td key={cellIndex}>
                          <div className="skeleton skeleton-text">&nbsp;</div>
                        </td>
                      ))}
                    </TableBodyRow>
                  ))}
                </>
              ) : (
                <>
                  {expenses && expenses.expenses.length > 0 ? (
                    expenses.expenses.map((exp) => (
                      <TableBodyRow key={exp.id}>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {exp.department ? exp.department : '-'}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {exp.category}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {exp.type}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {/* FIXME - Currency */}
                          {exp.amount === 0
                            ? '-'
                            : formatToINR(exp.amount) + ' INR'}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {exp.paymentMadeBy}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          {exp.modeOfPayment}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          <span
                            style={{
                              verticalAlign: 'middle',
                              marginRight: '6px',
                            }}
                          >
                            <CalenderIcon />
                          </span>
                          {exp.expenseDate
                            ? formatDate(exp.expenseDate as unknown as string)
                            : '-'}
                        </td>
                        <td onClick={() => handleExpenseToBePreviewed(exp)}>
                          <span
                            style={{
                              verticalAlign: 'middle',
                              marginRight: '6px',
                            }}
                          >
                            <CalenderIcon />
                          </span>
                          {exp.paymentDate
                            ? formatDate(exp.paymentDate as unknown as string)
                            : '-'}
                        </td>
                        {user?.roles.some((role) =>
                          role.permissions.some(
                            (permission) =>
                              permission === EXPENSE_MODULE.UPDATE_EXPENSE ||
                              permission === EXPENSE_MODULE.DELETE_EXPENSE
                          )
                        ) && (
                            <td>
                              <ExpenseAction
                                options={Actions}
                                fetchExpenses={fetchExpenses}
                                currentExpense={exp}
                                expenseCategories={props.expenseCategories}
                                expenseTypes={props.expenseTypes}
                                expenseDepartments={props.expenseDepartments}
                                expensePaymentModes={props.expensePaymentModes}
                              />
                            </td>
                          )}
                      </TableBodyRow>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={7}>
                        <ZeroEntriesFound
                          heading="THERE_IS_NO_EXPENSE_HISTORY_FOUND"
                          message="YOU_HAVE_NOT_ADDED_ANY_EXPENSES"
                        />
                      </td>
                    </tr>
                  )}
                </>
              )}
            </TableList>
          )}
          {totalPages && (
            <Pagination
              totalPages={totalPages}
              currentPage={currentPage}
              handlePageChange={handlePageChange}
              totalItems={expenses?.metadata.totalSize || 0}
              handleItemsPerPage={handleItemsPerPage}
              itemsPerPage={itemsPerPage}
            />
          )}
        </TableListContainer>
      </div>
      {isExpensePreviewModalOpen && (
        <span style={{ cursor: 'default' }}>
          <CenterModalMain
            heading={modeOfModal}
            modalClose={() => {
              handleIsExpensePreviewModalOpen();
              handleSetModeOfModal('Expense Preview');
            }}
            actualContentContainer={
              <AddExpenseForm
                handleClose={handleIsExpensePreviewModalOpen}
                handleLoadExpenses={fetchExpenses}
                mode="preview"
                expense={expenseToBePreviewed}
                handleModeOfModal={handleSetModeOfModal}
                expenseCategories={props.expenseCategories}
                expenseTypes={props.expenseTypes}
                expenseDepartments={props.expenseDepartments}
                expensePaymentModes={props.expensePaymentModes}
              />
            }
          />
        </span>
      )}
    </ExpenseListSection>
  );
};
