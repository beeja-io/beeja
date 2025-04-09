package com.beeja.api.financemanagementservice.serviceImpl;

import com.beeja.api.financemanagementservice.Utils.UserContext;
import com.beeja.api.financemanagementservice.enums.LoanStatus;
import com.beeja.api.financemanagementservice.repository.LoanRepository;
import com.beeja.api.financemanagementservice.response.LoanDTO;
import com.beeja.api.financemanagementservice.response.LoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLoansWithCountMethodTest {

    private LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private MongoOperations mongoOperations;

    @BeforeEach
    void setup() {
       loanService = new LoanServiceImpl(mongoOperations, loanRepository);
    }

    @Test
    void testGetLoansWithStatus() {
        int pageNumber = 1;
        int pageSize = 5;
        String sortBy = "requestedDate";
        String sortDirection = "DESC";
        LoanStatus status = LoanStatus.APPROVED;
        String orgId = "org123";

        List<LoanDTO> mockLoans = List.of(new LoanDTO());
        long mockCount = 10;

        try (MockedStatic<UserContext> userContextMockedStatic = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockOrgMap = new HashMap<>();
            mockOrgMap.put("id", orgId);
            userContextMockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrgMap);

            Pageable expectedPageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
            when(loanRepository.findAllByOrganizationIdAndStatus(orgId, status, expectedPageable)).thenReturn(mockLoans);
            when(loanRepository.countByOrganizationIdAndStatus(orgId, status)).thenReturn(mockCount);

            LoanResponse response = loanService.getLoansWithCount(pageNumber, pageSize, sortBy, sortDirection, status);

            assertEquals(mockLoans, response.getLoansList());
            assertEquals(mockCount, response.getTotalRecords());
            assertEquals(pageSize, response.getPageSize());
            assertEquals(pageNumber, response.getPageNumber());
        }
    }

    @Test
    void testGetLoansWithoutStatus() {
        int pageNumber = 2;
        int pageSize = 10;
        String sortBy = "employeeName";
        String sortDirection = "ASC";
        LoanStatus status = null;
        String orgId = "org123";

        List<LoanDTO> mockLoans = List.of(new LoanDTO(), new LoanDTO());
        long mockCount = 20;

        try (MockedStatic<UserContext> userContextMockedStatic = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockOrgMap = new HashMap<>();
            mockOrgMap.put("id", orgId);
            userContextMockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrgMap);

            Pageable expectedPageable = PageRequest.of(1, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
            when(loanRepository.findAllByOrganizationId(orgId, expectedPageable)).thenReturn(mockLoans);
            when(loanRepository.countByOrganizationId(orgId)).thenReturn(mockCount);

            LoanResponse response = loanService.getLoansWithCount(pageNumber, pageSize, sortBy, sortDirection, status);

            assertEquals(mockLoans, response.getLoansList());
            assertEquals(mockCount, response.getTotalRecords());
            assertEquals(pageSize, response.getPageSize());
            assertEquals(pageNumber, response.getPageNumber());
        }
    }

    @Test
    void testGetLoansWithInvalidPageNumber() {
        int pageNumber = -1;
        int pageSize = 5;
        String sortBy = "status";
        String sortDirection = "DESC";
        LoanStatus status = null;
        String orgId = "org123";

        List<LoanDTO> mockLoans = List.of();
        long mockCount = 0;

        try (MockedStatic<UserContext> userContextMockedStatic = Mockito.mockStatic(UserContext.class)) {
            Map<String, Object> mockOrgMap = new HashMap<>();
            mockOrgMap.put("id", orgId);
            userContextMockedStatic.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrgMap);

            Pageable expectedPageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, sortBy));
            when(loanRepository.findAllByOrganizationId(orgId, expectedPageable)).thenReturn(mockLoans);
            when(loanRepository.countByOrganizationId(orgId)).thenReturn(mockCount);

            LoanResponse response = loanService.getLoansWithCount(pageNumber, pageSize, sortBy, sortDirection, status);

            assertEquals(0, response.getLoansList().size());
            assertEquals(mockCount, response.getTotalRecords());
            assertEquals(pageSize, response.getPageSize());
            assertEquals(pageNumber, response.getPageNumber());
        }
    }

    @Test
    void testGetLoansThrowsException() {
        int pageNumber = 1;
        int pageSize = 5;
        String sortBy = "status";
        String sortDirection = "ASC";
        LoanStatus status = null;

        try (MockedStatic<UserContext> userContextMockedStatic = Mockito.mockStatic(UserContext.class)) {
            userContextMockedStatic.when(UserContext::getLoggedInUserOrganization)
                    .thenThrow(new RuntimeException("DB Down"));

            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    loanService.getLoansWithCount(pageNumber, pageSize, sortBy, sortDirection, status));

            assertEquals("Error fetching paginated loans.", ex.getMessage());
        }
    }
}
