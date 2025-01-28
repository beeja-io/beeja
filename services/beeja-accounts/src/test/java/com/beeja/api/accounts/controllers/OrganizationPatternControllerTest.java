package com.beeja.api.accounts.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.beeja.api.accounts.enums.PatternType;
import com.beeja.api.accounts.model.Organization.OrganizationPattern;
import com.beeja.api.accounts.requests.OrganizationPatternRequest;
import com.beeja.api.accounts.service.OrganizationPatternService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

class OrganizationPatternControllerTest {

    @InjectMocks private OrganizationPatternController organizationPatternController;

    @Mock private OrganizationPatternService organizationPatternService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private BindingResult bindingResult;

    public OrganizationPatternControllerTest() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(organizationPatternController).build();
    }

    @Test
    void testUpdatePatternStatus_Success() {
        String patternId = "pattern123";
        String patternType = "EMPLOYEE_ID_PATTERN"; // Use a valid enum value
        OrganizationPattern mockPattern = new OrganizationPattern();
        mockPattern.setId(patternId);
        mockPattern.setPatternType(PatternType.valueOf(patternType)); // Convert to enum
        mockPattern.setActive(true);

        when(organizationPatternService.updatePatternStatusByPatternIdAndPatternType(
                patternId, patternType))
                .thenReturn(mockPattern);
        ResponseEntity<OrganizationPattern> response =
                organizationPatternController.updatePatternStatus(patternId, patternType);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPattern, response.getBody());
        verify(organizationPatternService, times(1))
                .updatePatternStatusByPatternIdAndPatternType(patternId, patternType);
    }

    @Test
    void testAddPattern_Success() throws Exception {
        OrganizationPatternRequest request = new OrganizationPatternRequest();
        request.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        request.setPatternLength(5);
        request.setPrefix("EMP");
        request.setInitialSequence(0);
        request.setActive(true);

        OrganizationPattern createdPattern = new OrganizationPattern();
        createdPattern.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        createdPattern.setPatternLength(5);
        createdPattern.setPrefix("EMP");
        createdPattern.setInitialSequence(0);
        createdPattern.setActive(true);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(organizationPatternService.addPatternByPatternIdAndPatternType(request))
                .thenReturn(createdPattern);
        ResponseEntity<OrganizationPattern> response =
                organizationPatternController.addPattern(request, bindingResult);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(createdPattern, response.getBody());
        verify(organizationPatternService, times(1)).addPatternByPatternIdAndPatternType(request);
    }

    @Test
    void testDeletePattern() throws Exception {
        String patternId = "123";
        String patternType = "TYPE_A";

        doNothing()
                .when(organizationPatternService)
                .deletePatternByPatternIdAndPatternType(patternId, patternType);

        mockMvc
                .perform(
                        delete("/v1/organization/patterns")
                                .param("patternId", patternId)
                                .param("patternType", patternType))
                .andExpect(status().isNoContent());

        verify(organizationPatternService, times(1))
                .deletePatternByPatternIdAndPatternType(patternId, patternType);
    }

    @Test
    void testGetPatternsByType_Success() throws Exception {
        String patternType = "EMPLOYEE_ID_PATTERN";

        OrganizationPattern pattern1 = new OrganizationPattern();
        pattern1.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        pattern1.setPatternLength(5);
        pattern1.setPrefix("EMP");
        pattern1.setInitialSequence(0);
        pattern1.setActive(true);

        OrganizationPattern pattern2 = new OrganizationPattern();
        pattern2.setPatternType(PatternType.EMPLOYEE_ID_PATTERN);
        pattern2.setPatternLength(6);
        pattern2.setPrefix("EMPL");
        pattern2.setInitialSequence(100);
        pattern2.setActive(false);

        List<OrganizationPattern> patterns = List.of(pattern1, pattern2);

        when(organizationPatternService.getPatternsByPatternType(patternType)).thenReturn(patterns);
        ResponseEntity<List<OrganizationPattern>> response =
                organizationPatternController.getPatternsByType(patternType);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(patterns, response.getBody());
        verify(organizationPatternService, times(1)).getPatternsByPatternType(patternType);
    }

    @Test
    void testGetPatternsByType_NoPatternsFound() throws Exception {
        String patternType = "EMPLOYEE_ID_PATTERN";
        when(organizationPatternService.getPatternsByPatternType(patternType))
                .thenReturn(Collections.emptyList());
        ResponseEntity<List<OrganizationPattern>> response =
                organizationPatternController.getPatternsByType(patternType);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(organizationPatternService, times(1)).getPatternsByPatternType(patternType);
    }
}