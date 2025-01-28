package com.beeja.api.accounts.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.accounts.enums.ErrorCode;
import com.beeja.api.accounts.enums.ErrorType;
import com.beeja.api.accounts.enums.PatternType;
import com.beeja.api.accounts.exceptions.BadRequestException;
import com.beeja.api.accounts.exceptions.ResourceNotFoundException;
import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.repository.OrganizationPatternsRepository;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;
import com.beeja.api.accounts.utils.BuildErrorMessage;
import com.beeja.api.accounts.utils.Constants;
import com.beeja.api.accounts.utils.UserContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrganizationPatternServiceImplTest {

    @Mock private OrganizationPatternsRepository organizationPatternsRepository;

    @InjectMocks private OrganizationPatternServiceImpl organizationPatternService;

    private Organization mockOrganization;

    @Mock
    private UserContext userContext;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockOrganization = new Organization();
        mockOrganization.setId("org123");
        UserContext.setLoggedInUserOrganization(mockOrganization);
    }

    @Test
    void testAddPatternSuccessfully() {
        OrganizationPatternRequest request = new OrganizationPatternRequest();
        request.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        request.setPatternLength(5);
        request.setPrefix("EMP");
        request.setInitialSequence(1);
        request.setActive(true);
        when(organizationPatternsRepository
                .existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                        eq("org123"), anyString(), eq(5), eq("EMP")))
                .thenReturn(false);

        when(organizationPatternsRepository.findByOrganizationIdAndPatternType(
                eq("org123"), anyString()))
                .thenReturn(Collections.emptyList());

        OrganizationPattern savedPattern = new OrganizationPattern();
        savedPattern.setPatternType(request.getPatternType());
        savedPattern.setPatternLength(request.getPatternLength());
        savedPattern.setPrefix(request.getPrefix());
        savedPattern.setInitialSequence(request.getInitialSequence());
        savedPattern.setActive(request.isActive());
        when(organizationPatternsRepository.save(any(OrganizationPattern.class)))
                .thenReturn(savedPattern);
        OrganizationPattern result =
                organizationPatternService.addPatternByPatternIdAndPatternType(request);
        assertNotNull(result);
        assertEquals(request.getPatternType(), result.getPatternType());
        assertEquals(request.getPrefix(), result.getPrefix());
    }

    @Test
    void testAddPatternThrowsBadRequestExceptionWhenPatternExists() {
        OrganizationPatternRequest request = new OrganizationPatternRequest();
        request.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        request.setPatternLength(5);
        request.setPrefix("EMP");
        request.setInitialSequence(1);
        request.setActive(true);

        when(organizationPatternsRepository
                .existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                        eq("org123"), eq("EMPLOYEE_ID_PATTERN"), eq(5), eq("EMP")))
                .thenReturn(true);
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Organization organization = new Organization();
            organization.setId("org123");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(organization);
            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> organizationPatternService.addPatternByPatternIdAndPatternType(request)
            );

            String expectedMessage = BuildErrorMessage.buildErrorMessage(
                    ErrorType.CONFLICT_ERROR,
                    ErrorCode.RESOURCE_IN_USE,
                    Constants.SAME_ID_PATTERN_ALREADY_REGISTERED);
            assertEquals(expectedMessage, exception.getMessage());

            verify(organizationPatternsRepository, times(1))
                    .existsByOrganizationIdAndPatternTypeAndPatternLengthAndPrefix(
                            "org123", "EMPLOYEE_ID_PATTERN", 5, "EMP");
            verify(organizationPatternsRepository, times(0)).findByOrganizationIdAndPatternType(any(), any());
            verify(organizationPatternsRepository, times(0)).save(any());
        }
    }


    @Test
    void testUpdatePatternStatusSuccessfully() {
        OrganizationPattern activePattern = new OrganizationPattern();
        activePattern.setId("pattern1");
        activePattern.setActive(false);

        OrganizationPattern inactivePattern = new OrganizationPattern();
        inactivePattern.setId("pattern2");
        inactivePattern.setActive(true);

        List<OrganizationPattern> patterns = Arrays.asList(activePattern, inactivePattern);

        when(organizationPatternsRepository.findByOrganizationIdAndPatternType(
                eq("org123"), anyString()))
                .thenReturn(patterns);

        when(organizationPatternsRepository.saveAll(anyList())).thenReturn(patterns);
        OrganizationPattern result =
                organizationPatternService.updatePatternStatusByPatternIdAndPatternType(
                        "pattern1", "EMPLOYEE_ID_PATTERN");
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(organizationPatternsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdatePatternStatus_NoMatchingPattern() {
        OrganizationPattern pattern = new OrganizationPattern();
        pattern.setId("pattern1");
        pattern.setActive(false);

        List<OrganizationPattern> patterns = Collections.singletonList(pattern);
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Organization organization = new Organization();
            organization.setId("org123");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(organization);

            when(organizationPatternsRepository.findByOrganizationIdAndPatternType(
                    eq("org123"), eq("EMPLOYEE_ID_PATTERN")))
                    .thenReturn(patterns);
            String expectedErrorMessage = BuildErrorMessage.buildErrorMessage(
                    ErrorType.RESOURCE_NOT_FOUND_ERROR,
                    ErrorCode.CANNOT_SAVE_CHANGES,
                    Constants.NO_PATTERN_FOUND_WITH_PROVIDED_ID);
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> organizationPatternService.updatePatternStatusByPatternIdAndPatternType(
                            "nonexistentPatternId", "EMPLOYEE_ID_PATTERN")
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verify(organizationPatternsRepository, times(1))
                    .findByOrganizationIdAndPatternType("org123", "EMPLOYEE_ID_PATTERN");
            verify(organizationPatternsRepository, times(0)).saveAll(anyList());
        }
    }

    @Test
    void testDeletePatternByPatternIdAndPatternType_Success() {
        String patternId = "pattern123";
        String patternType = "EMPLOYEE_ID_PATTERN";
        when(organizationPatternsRepository.deleteByOrganizationIdAndPatternTypeAndId(
                UserContext.getLoggedInUserOrganization().getId(), patternId, patternType))
                .thenReturn(null);
        organizationPatternService.deletePatternByPatternIdAndPatternType(patternId, patternType);
        verify(organizationPatternsRepository, times(1))
                .deleteByOrganizationIdAndPatternTypeAndId(
                        UserContext.getLoggedInUserOrganization().getId(), patternId, patternType);
    }

    @Test
    void testGetPatternsByPatternTypeSuccessfully() throws Exception {
        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Organization organization = new Organization();
            organization.setId("org123");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(organization);

            OrganizationPattern pattern = new OrganizationPattern();
            pattern.setId("pattern1");
            pattern.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);

            when(organizationPatternsRepository.findByOrganizationIdAndPatternType(
                    eq("org123"), eq("EMPLOYEE_ID_PATTERN")))
                    .thenReturn(Collections.singletonList(pattern));
            List<OrganizationPattern> result =
                    organizationPatternService.getPatternsByPatternType("EMPLOYEE_ID_PATTERN");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("pattern1", result.get(0).getId());
            assertEquals(PatternType.EMPLOYEE_ID_PATTERN, result.get(0).getPatternType());
            verify(organizationPatternsRepository, times(1))
                    .findByOrganizationIdAndPatternType("org123", "EMPLOYEE_ID_PATTERN");
        }
    }


    @Test
    void testGetPatternsByPatternType_ExceptionThrown() {

        try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
            Organization organization = new Organization();
            organization.setId("org123");
            mockedUserContext.when(UserContext::getLoggedInUserOrganization).thenReturn(organization);

            when(organizationPatternsRepository.findByOrganizationIdAndPatternType(
                    eq("org123"), eq("EMPLOYEE_ID_PATTERN")))
                    .thenThrow(new RuntimeException("Database connection error"));
            String expectedErrorMessage = BuildErrorMessage.buildErrorMessage(
                    ErrorType.DB_ERROR,
                    ErrorCode.UNABLE_TO_FETCH_DETAILS,
                    Constants.UNABLE_TO_FETCH_DETAILS_FROM_DATABASE);

            Exception exception = assertThrows(
                    Exception.class,
                    () -> organizationPatternService.getPatternsByPatternType("EMPLOYEE_ID_PATTERN")
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verify(organizationPatternsRepository, times(1))
                    .findByOrganizationIdAndPatternType("org123", "EMPLOYEE_ID_PATTERN");
        }
    }

    @Test
    void testCreateOrganizationPattern_Success() throws Exception {
        String organizationId = "org123";

        OrganizationPatternRequest organizationPatternRequest = new OrganizationPatternRequest();
        organizationPatternRequest.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        organizationPatternRequest.setPatternLength(10);
        organizationPatternRequest.setActive(true);
        organizationPatternRequest.setPrefix("EMP");
        organizationPatternRequest.setInitialSequence(1);

        OrganizationPattern expectedOrganizationPattern = new OrganizationPattern();
        expectedOrganizationPattern.setOrganizationId(organizationId);
        expectedOrganizationPattern.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        expectedOrganizationPattern.setPatternLength(10);
        expectedOrganizationPattern.setActive(true);
        expectedOrganizationPattern.setPrefix("EMP");
        expectedOrganizationPattern.setInitialSequence(1);
        String zeros =
                String.format(
                        "%0"
                                + (organizationPatternRequest.getPatternLength()
                                - organizationPatternRequest.getPrefix().length())
                                + "d",
                        organizationPatternRequest.getInitialSequence());
        expectedOrganizationPattern.setExamplePattern(organizationPatternRequest.getPrefix() + zeros);
        Method privateMethod =
                OrganizationPatternServiceImpl.class.getDeclaredMethod(
                        "createOrganizationPattern", OrganizationPatternRequest.class, String.class);
        privateMethod.setAccessible(true);
        OrganizationPattern actualOrganizationPattern =
                (OrganizationPattern)
                        privateMethod.invoke(
                                organizationPatternService, organizationPatternRequest, organizationId);
        assertEquals(expectedOrganizationPattern, actualOrganizationPattern);
    }
}