import { t } from 'i18next';
import {
  PaginationContainer,
  PaginationList,
  PaginationItem,
  PaginationButton,
  PaginationActionArea,
} from '../../styles/StyledPagination.style';
import { DropdownOrg } from '../reusableComponents/DropDownMenu.component';

/**
 * @example https://www.freecodecamp.org/news/build-a-custom-pagination-component-in-react/
 */

const Pagination = ({
  totalPages,
  currentPage,
  handlePageChange,
  totalItems,
  handleItemsPerPage,
  itemsPerPage,
}: {
  totalPages: number;
  currentPage: number;
  handlePageChange: (page: number) => void;
  totalItems: number;
  handleItemsPerPage: (page: number) => void;
  itemsPerPage: number;
}) => {
  const createPageNumbers = (): (number | string)[] => {
    const pageNumbers: (number | string)[] = [];
    const maxPageToShow = 6;

    if (totalPages <= maxPageToShow) {
      for (let i = 1; i <= totalPages; i++) {
        pageNumbers.push(i);
      }
      return pageNumbers;
    }

    pageNumbers.push(1);

    if (currentPage <= 3) {
      pageNumbers.push(2, 3, 4, '...', totalPages);
    } else if (currentPage >= totalPages - 2) {
      pageNumbers.push(
        '...',
        totalPages - 3,
        totalPages - 2,
        totalPages - 1,
        totalPages
      );
    } else {
      pageNumbers.push(
        '...',
        currentPage - 1,
        currentPage,
        currentPage + 1,
        '...',
        totalPages
      );
    }

    return pageNumbers;
  };

  const pageNumbers = createPageNumbers();

  return (
    <PaginationContainer>
      <PaginationList>
        <PaginationItem className={currentPage === 1 ? 'disabled' : ''}>
          <PaginationButton
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="arrowIcon"
          >
            &laquo;
          </PaginationButton>
        </PaginationItem>

        {pageNumbers.map((page, index) => {
          if (page === '...') {
            const prev = pageNumbers[index - 1];
            const next = pageNumbers[index + 1];
            let jumpToPage = 1;

            if (typeof prev === 'number' && typeof next === 'number') {
              jumpToPage = Math.floor((prev + next) / 2);
            }

            return (
              <PaginationItem key={`ellipsis-${index}`}>
                <PaginationButton onClick={() => handlePageChange(jumpToPage)}>
                  ...
                </PaginationButton>
              </PaginationItem>
            );
          }

          return (
            <PaginationItem
              key={index}
              className={currentPage === page ? 'active' : ''}
            >
              <PaginationButton
                onClick={() => handlePageChange(page as number)}
              >
                {page}
              </PaginationButton>
            </PaginationItem>
          );
        })}

        <PaginationItem
          className={currentPage === totalPages ? 'disabled' : ''}
        >
          <PaginationButton
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="arrowIcon"
          >
            &raquo;
          </PaginationButton>
        </PaginationItem>
      </PaginationList>

      <PaginationActionArea>
        <span>
          {t('SHOWING')}{' '}
          {Math.min((currentPage - 1) * itemsPerPage + 1, totalItems)} {t('TO')}{' '}
          {Math.min(currentPage * itemsPerPage, totalItems)} {t('OF')}{' '}
          {totalItems} {t('ENTRIES')}
        </span>
        <DropdownOrg
          value={itemsPerPage.toString()}
          onChange={(val) => handleItemsPerPage(parseInt(val ?? '10'))}
          options={[
            { label: t('SHOW 10'), value: '10' },
            { label: t('SHOW 25'), value: '25' },
            { label: t('SHOW 50'), value: '50' },
            { label: t('SHOW 75'), value: '75' },
            { label: t('SHOW 100'), value: '100' },
          ]}
        />
      </PaginationActionArea>
    </PaginationContainer>
  );
};

export default Pagination;
