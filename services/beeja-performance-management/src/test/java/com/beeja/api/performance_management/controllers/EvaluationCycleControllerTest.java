package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.enums.CycleStatus;
import com.beeja.api.performance_management.enums.CycleType;
import com.beeja.api.performance_management.model.EvaluationCycle;
import com.beeja.api.performance_management.model.Questionnaire;
import com.beeja.api.performance_management.model.dto.EvaluationCycleCreateDto;
import com.beeja.api.performance_management.model.dto.EvaluationCycleDetailsDto;
import com.beeja.api.performance_management.service.EvaluationCycleService;
import com.beeja.api.performance_management.service.QuestionnaireService;
import com.beeja.api.performance_management.exceptions.InvalidOperationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EvaluationCycleControllerTest {

    @Mock
    private EvaluationCycleService cycleService;

    @Mock
    private QuestionnaireService questionnaireService;

    @InjectMocks
    private EvaluationCycleController controller;

    private EvaluationCycle cycle;
    private Questionnaire questionnaire;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        cycle = new EvaluationCycle();
        cycle.setId("123");
        cycle.setName("Test Cycle");
        cycle.setType(CycleType.ANNUAL);
        cycle.setFormDescription("Test Description");
        cycle.setStartDate(LocalDate.now());
        cycle.setEndDate(LocalDate.now().plusDays(10));
        cycle.setFeedbackDeadline(LocalDate.now().plusDays(5));
        cycle.setSelfEvalDeadline(LocalDate.now().plusDays(7));
        cycle.setStatus(CycleStatus.IN_PROGRESS);
        cycle.setQuestionnaireId("Q1");

        questionnaire = new Questionnaire();
        questionnaire.setId("Q1");
        questionnaire.setQuestions(List.of());
    }

    @Test
    void testCreateEvaluationCycle() {
        when(cycleService.createCycle(any(EvaluationCycle.class))).thenReturn(cycle);

        ResponseEntity<EvaluationCycle> response = controller.createEvaluationCycle(cycle);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("123", response.getBody().getId());
    }

    @Test
    void testCreateCycleWithQuestions() {
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto();
        when(cycleService.createCycleWithQuestions(any(EvaluationCycleCreateDto.class))).thenReturn(dto);

        ResponseEntity<EvaluationCycleDetailsDto> response =
                controller.createCycleWithQuestions(new EvaluationCycleCreateDto());

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllEvaluationCycles() {
        when(cycleService.getAllCycles()).thenReturn(List.of(cycle));

        ResponseEntity<List<EvaluationCycle>> response = controller.getAllEvaluationCycles();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetCyclesWithQuestionnaires() {
        when(cycleService.getAllCycles()).thenReturn(List.of(cycle));
        when(questionnaireService.getQuestionnaireById("Q1")).thenReturn(questionnaire);

        ResponseEntity<List<EvaluationCycleDetailsDto>> response =
                controller.getCyclesWithQuestionnaires();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Q1", response.getBody().get(0).getQuestionnaireId());
    }

    @Test
    void testGetEvaluationCycleById() {
        when(cycleService.getCycleById("123")).thenReturn(cycle);

        ResponseEntity<EvaluationCycle> response = controller.getEvaluationCycleById("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("123", response.getBody().getId());
    }

    @Test
    void testGetEvaluationCycleDetails() {
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto(cycle, questionnaire);
        when(cycleService.getCycleWithQuestionnaire("123")).thenReturn(dto);

        ResponseEntity<EvaluationCycleDetailsDto> response =
                controller.getEvaluationCycleDetails("123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("123", response.getBody().getId());
    }

    @Test
    void testGetActiveEvaluationCycle_WithStatus() {
        when(cycleService.getCyclesByStatus(CycleStatus.IN_PROGRESS)).thenReturn(List.of(cycle));

        ResponseEntity<List<EvaluationCycle>> response =
                controller.getActiveEvaluationCycle("in_progress");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetActiveEvaluationCycle_NoStatus() {
        when(cycleService.getAllCycles()).thenReturn(List.of(cycle));

        ResponseEntity<List<EvaluationCycle>> response = controller.getActiveEvaluationCycle(null);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateEvaluationCycle() {
        when(cycleService.updateCycle(eq("123"), any(EvaluationCycle.class))).thenReturn(cycle);

        ResponseEntity<EvaluationCycle> response = controller.updateEvaluationCycle("123", cycle);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateEvaluationCycleStatus() {
        when(cycleService.updateCycleStatus("123", CycleStatus.COMPLETED)).thenReturn(cycle);

        ResponseEntity<EvaluationCycle> response =
                controller.updateEvaluationCycleStatus("123", Map.of("status", "COMPLETED"));

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateEvaluationCycleStatus_MissingField() {
        assertThrows(InvalidOperationException.class, () ->
                controller.updateEvaluationCycleStatus("123", Map.of()));
    }

    @Test
    void testUpdateEvaluationCycleStatus_InvalidValue() {
        assertThrows(InvalidOperationException.class, () ->
                controller.updateEvaluationCycleStatus("123", Map.of("status", "INVALID")));
    }

    @Test
    void testUpdateFullEvaluationCycle() {
        EvaluationCycleDetailsDto dto = new EvaluationCycleDetailsDto(cycle, questionnaire);
        when(cycleService.updateFullCycle(eq("123"), any(EvaluationCycleDetailsDto.class))).thenReturn(dto);

        ResponseEntity<EvaluationCycleDetailsDto> response =
                controller.updateFullEvaluationCycle("123", dto);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteEvaluationCycle() {
        doNothing().when(cycleService).deleteCycle("123");

        ResponseEntity<Void> response = controller.deleteEvaluationCycle("123");

        assertEquals(204, response.getStatusCodeValue());
        verify(cycleService, times(1)).deleteCycle("123");
    }
}
