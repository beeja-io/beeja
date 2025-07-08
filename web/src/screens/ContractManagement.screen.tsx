import { matchPath, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState, useCallback } from 'react';
import { useTranslation } from 'react-i18next';

import { Button } from '../styles/CommonStyles.style';
import {
  ExpenseHeadingSection,
  ExpenseManagementMainContainer,
} from '../styles/ExpenseManagementStyles.style';

import { ArrowDownSVG, VectorSVG } from '../svgs/CommonSvgs.svs';
import { AddNewPlusSVG } from '../svgs/EmployeeListSvgs.svg';

import ToastMessage from '../components/reusableComponents/ToastMessage.component';
import { ContractDetails } from '../entities/ContractEntiy';
import { getAllContracts } from '../service/axiosInstance';
import { ExpenseFilterArea } from '../styles/ExpenseListStyles.style';
import AddContractForm from '../components/directComponents/AddContractForm.component';

const ContractManagement = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const goToPreviousPage = () => navigate(-1);

  const [contractList, setContractList] = useState<ContractDetails[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccessMessage, setShowSuccessMessage] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalSize, setTotalSize] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filters
  const [statusFilter, setStatusFilter] = useState('');
  const [titleFilter, setTitleFilter] = useState('');
  const [isShowFilters, setIsShowFilters] = useState(false);

  const location = useLocation();

  const isContractDetailsRoute = matchPath(
    '/clients/contract-management/:id',
    location.pathname
  );
  const fetchData = useCallback(async () => {
    try {
      setIsLoading(true);
      setIsShowFilters(!!(statusFilter || titleFilter));

      // const queryParams: string[] = [];

      // if (statusFilter)
      //   queryParams.push(`status=${encodeURIComponent(statusFilter)}`);
      // if (titleFilter)
      //   queryParams.push(`contractTitle=${encodeURIComponent(titleFilter)}`);
      // queryParams.push(`pageNumber=${currentPage}`);
      // queryParams.push(`pageSize=${itemsPerPage}`);
      // const url = `/projects/v1/contracts?${queryParams.join('&')}`;

      const res = await getAllContracts();
      const response = res?.data?.contracts;

      setContractList(response);

      setIsLoading(false);
    } catch (error) {
      setIsLoading(false);
    }
  }, [statusFilter, titleFilter, currentPage, itemsPerPage]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedContractData, setSelectedContractData] =
    useState<ContractDetails | null>(null);

  const handleOpenCreateModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(true);
    setSelectedContractData(null);
  }, []);

  const handleCloseModal = useCallback(() => {
    setIsEditMode(false);
    setIsCreateModalOpen(false);
    setSelectedContractData(null);
  }, []);

  const handleSuccessMessage = () => {
    setShowSuccessMessage(true);
    setTimeout(() => setShowSuccessMessage(false), 2000);
    fetchData();
  };

  const clearFilters = (key: string) => {
    if (key === 'status') setStatusFilter('');
    if (key === 'title') setTitleFilter('');
    if (key === 'clearAll') {
      setStatusFilter('');
      setTitleFilter('');
    }
    setCurrentPage(1);
  };

  const selectedFiltersText = () => {
    const filters = [
      { key: 'status', value: statusFilter },
      { key: 'title', value: titleFilter },
    ];

    return (
      <ExpenseFilterArea>
        {filters
          .filter((f) => f.value)
          .map((filter) => (
            <span className="filterValues" key={filter.key}>
              {filter.value}
              <span
                className="filterClearBtn"
                onClick={() => clearFilters(filter.key)}
              >
                <VectorSVG />
              </span>
            </span>
          ))}
        {(statusFilter || titleFilter) && (
          <span className="clearAll" onClick={() => clearFilters('clearAll')}>
            Clear All
          </span>
        )}
      </ExpenseFilterArea>
    );
  };

  return (
    <>
      <ExpenseManagementMainContainer>
        <ExpenseHeadingSection>
          <span className="heading">
            <span onClick={goToPreviousPage}>
              <ArrowDownSVG />
            </span>
            {t('Contract Management')}

            {isCreateModalOpen && (
              <>
                <span className="separator"> {`>`} </span>
                <span className="nav_AddClient">{t('Add Contract')}</span>
              </>
            )}

            {!isCreateModalOpen && isContractDetailsRoute && (
              <>
                <span className="separator"> {`>`} </span>
                <span className="nav_AddClient">{t('Contract Details')}</span>
              </>
            )}
          </span>

          {!isCreateModalOpen && (
            <Button
              className="submit shadow"
              onClick={handleOpenCreateModal}
              width="216px"
            >
              <AddNewPlusSVG />
              {t('Add New Contract')}
            </Button>
          )}
        </ExpenseHeadingSection>

        {isCreateModalOpen ? (
          <AddContractForm
            handleClose={handleCloseModal}
            handleSuccessMessage={handleSuccessMessage}
            initialData={selectedContractData ?? undefined}
          />
        ) : (
          <Outlet
            context={{
              contractList,
              isLoading,
              fetchData,
            }}
          />
        )}
      </ExpenseManagementMainContainer>

      {showSuccessMessage && (
        <ToastMessage
          messageType="success"
          messageBody="THE_CONTRACT_HAS_BEEN_ADDED"
          messageHeading="SUCCESSFULLY_ADDED"
          handleClose={() => setShowSuccessMessage(false)}
        />
      )}
    </>
  );
};

export default ContractManagement;
