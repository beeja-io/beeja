import { t } from "i18next";
import {
  PaginationContainer,
  PaginationList,
  PaginationItem,
  PaginationButton,
  PaginationActionArea,
} from "../../styles/StyledPagination.style";

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
  const createPageNumbers = () => {
    const pageNumbers: (string | number)[] = [];
    const maxPageToShow = 5;
    const halfMaxPageToShow = Math.floor(maxPageToShow / 2);

    if (totalPages <= maxPageToShow) {
      for (let i = 1; i <= totalPages; i++) {
        pageNumbers.push(i);
      }
    } else {
      const leftOffset = Math.min(currentPage - 1, halfMaxPageToShow);
      const rightOffset = Math.min(totalPages - currentPage, halfMaxPageToShow);
      let startPage = currentPage - leftOffset;
      let endPage = currentPage + rightOffset;

      if (startPage === 1) {
        endPage = maxPageToShow;
      } else if (endPage === totalPages) {
        startPage = totalPages - maxPageToShow + 1;
      }

      for (let i = startPage; i <= endPage; i++) {
        pageNumbers.push(i);
      }

      if (startPage > 1) {
        pageNumbers.unshift("...");
        pageNumbers.unshift(1);
      }

      if (endPage < totalPages) {
        pageNumbers.push("...");
        pageNumbers.push(totalPages);
      }
    }

    return pageNumbers;
  };

  const pageNumbers = createPageNumbers();

  return (
    <PaginationContainer>
      <PaginationList>
        <PaginationItem className={currentPage === 1 ? "disabled" : ""}>
          <PaginationButton
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="arrowIcon"
          >
            &laquo;
          </PaginationButton>
        </PaginationItem>
        {pageNumbers.map((number, index) => (
          <PaginationItem
            key={index}
            className={currentPage === number ? "active" : ""}
          >
            <PaginationButton
              onClick={() => handlePageChange(number as number)}
            >
              {number}
            </PaginationButton>
          </PaginationItem>
        ))}
        <PaginationItem
          className={currentPage === totalPages ? "disabled" : ""}
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
          {t("SHOWING")}{" "}
          {Math.min((currentPage - 1) * itemsPerPage + 1, totalItems)} {t("TO")}{" "}
          {Math.min(currentPage * itemsPerPage, totalItems)} of {totalItems}{" "}
          {t("ENTRIES")}
        </span>

        <select
          onChange={(e) => handleItemsPerPage(parseInt(e.target.value))}
          defaultValue="10"
        >
          <option value="10">{t("SHOW 10")}</option>
          <option value="25">{t("SHOW 25")}</option>
          <option value="50">{t("SHOW 50")}</option>
          <option value="75">{t("SHOW 75")}</option>
          <option value="100">{t("SHOW 100")}</option>
        </select>
      </PaginationActionArea>
    </PaginationContainer>
  );
};

export default Pagination;
