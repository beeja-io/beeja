package com.beeja.api.accounts.service;



import com.beeja.api.accounts.enums.PatternType;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.repository.OrganizationPatternsRepository;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;
import com.beeja.api.accounts.serviceImpl.OrganizationPatternServiceImpl;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationPatternServiceImplTest {

    private OrganizationPatternServiceImpl service;
    private OrganizationPatternsRepository repository;

    private final String ORG_ID = "org123";
    private final String PATTERN_TYPE = "EMPLOYEE_ID_PATTERN";

    @BeforeEach
    void setUp() {
        repository = mock(OrganizationPatternsRepository.class);
        service = new OrganizationPatternServiceImpl();
        service.organizationPatternsRepository = repository;
    }

    @Test
    void testUpdatePatternStatus_Success() {
        OrganizationPattern pattern1 = new OrganizationPattern();
        pattern1.setId("p1");
        pattern1.setActive(false);

        OrganizationPattern pattern2 = new OrganizationPattern();
        pattern2.setId("p2");
        pattern2.setActive(true);

        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.findByOrganizationIdAndPatternType(ORG_ID, PATTERN_TYPE))
                    .thenReturn(List.of(pattern1, pattern2));

            OrganizationPattern updated = service.updatePatternStatusByPatternIdAndPatternType("p1", PATTERN_TYPE);

            assertTrue(updated.isActive());
            assertFalse(pattern2.isActive());

            verify(repository).saveAll(anyList());
        }
    }


    @Test
    void testUpdatePatternStatus_NotFound() {
        // Mock Organization and its behavior
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        // Mock the static UserContext to return the mocked Organization
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            // Simulate repository returning no patterns
            when(repository.findByOrganizationIdAndPatternType(ORG_ID, PATTERN_TYPE))
                    .thenReturn(new ArrayList<>());

            // Expect a ResourceNotFoundException
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                service.updatePatternStatusByPatternIdAndPatternType("invalid", PATTERN_TYPE);
            });

            // Verify error message
            assertTrue(ex.getMessage().contains(Constants.NO_PATTERN_FOUND_WITH_PROVIDED_ID));
        }
    }


    @Test
    void testAddPattern_Success_NewActivePattern() {
        OrganizationPatternRequest request = new OrganizationPatternRequest();
        request.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        request.setPatternLength(6);
        request.setPrefix("EMP");
        request.setInitialSequence(0);
        request.setActive(true);

        // Create a mock Organization and return ORG_ID on getId()
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                    anyString(), anyString(), anyInt(), anyString()))
                    .thenReturn(false);

            when(repository.findByOrganizationIdAndPatternType(ORG_ID, PATTERN_TYPE))
                    .thenReturn(List.of());

            when(repository.save(any(OrganizationPattern.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            OrganizationPattern result = service.addPatternByPatternIdAndPatternType(request);

            assertNotNull(result);
            assertTrue(result.isActive());
            assertEquals("EMP", result.getPrefix());
            assertEquals("EMP000", result.getExamplePattern());
        }
    }


    @Test
    void testAddPattern_ThrowsWhenDuplicateExists() {
        OrganizationPatternRequest request = new OrganizationPatternRequest();
        request.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        request.setPatternLength(5);
        request.setPrefix("EMP");
        request.setInitialSequence(0);
        request.setActive(false);

        // Mock Organization and stub getId()
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        // Mock static UserContext
        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                    any(), any(), anyInt(), any()))
                    .thenReturn(true);

            assertThrows(BadRequestException.class, () -> {
                service.addPatternByPatternIdAndPatternType(request);
            });
        }
    }


    @Test
    void testDeletePatternByPatternIdAndPatternType() {
        // Mock Organization and stub getId()
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            service.deletePatternByPatternIdAndPatternType("pattern123", PATTERN_TYPE);

            verify(repository).deleteByOrganizationIdAndPatternTypeAndId(ORG_ID, "pattern123", PATTERN_TYPE);
        }
    }


    @Test
    void testGetPatternsByPatternType_Success() throws Exception {
        // Mock the Organization object
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        OrganizationPattern p = new OrganizationPattern();
        p.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.findByOrganizationIdAndPatternType(ORG_ID, PATTERN_TYPE))
                    .thenReturn(List.of(p));

            List<OrganizationPattern> result = service.getPatternsByPatternType(PATTERN_TYPE);

            assertEquals(1, result.size());
            assertEquals(PatternType.EMPLOYEE_ID_PATTERN, result.get(0).getPatternType());
        }
    }



    @Test
    void testGetPatternsByPatternType_ThrowsException() {
        // Mock the Organization object
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.findByOrganizationIdAndPatternType(ORG_ID, PATTERN_TYPE))
                    .thenThrow(new RuntimeException("DB issue"));

            Exception ex = assertThrows(Exception.class, () -> {
                service.getPatternsByPatternType(PATTERN_TYPE);
            });

            assertTrue(ex.getMessage().contains(Constants.UNABLE_TO_FETCH_DETAILS_FROM_DATABASE));
        }
    }



    @Test
    void testGetActivePatternByPatternType_Success() throws Exception {
        OrganizationPattern p = new OrganizationPattern();
        p.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        p.setActive(true);

        // Create a mock Organization and stub the getId() call
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.findByOrganizationIdAndPatternTypeAndActive(ORG_ID, PATTERN_TYPE, true))
                    .thenReturn(p);

            OrganizationPattern result = service.getActivePatternByPatternType(PATTERN_TYPE);
            assertNotNull(result);
            assertTrue(result.isActive());
        }
    }


    @Test
    void testGetActivePatternByPatternType_ThrowsException() {
        // Mock the Organization and set up the ID
        Organization mockOrganization = mock(Organization.class);
        when(mockOrganization.getId()).thenReturn(ORG_ID);

        try (MockedStatic<UserContext> mocked = mockStatic(UserContext.class)) {
            mocked.when(UserContext::getLoggedInUserOrganization).thenReturn(mockOrganization);

            when(repository.findByOrganizationIdAndPatternTypeAndActive(ORG_ID, PATTERN_TYPE, true))
                    .thenThrow(new RuntimeException("DB crash"));

            Exception ex = assertThrows(Exception.class, () -> {
                service.getActivePatternByPatternType(PATTERN_TYPE);
            });

            assertTrue(ex.getMessage().contains(Constants.UNABLE_TO_FETCH_DETAILS_FROM_DATABASE));
        }
    }


}

