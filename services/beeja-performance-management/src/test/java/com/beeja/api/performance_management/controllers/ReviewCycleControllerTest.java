package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.responses.ReviewCycleDto;
import com.beeja.api.performance_management.service.EnhancedPerformanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewCycleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnhancedPerformanceService performanceService;

    @InjectMocks
    private ReviewCycleController reviewCycleController;

    private ReviewCycle sampleCycle;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(reviewCycleController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        sampleCycle = ReviewCycle.builder()
                .id("1")
                .cycleId("CYCLE-001")
                .name("Test Cycle")
                .description("Sample description")
                .reviewFormId("FORM-001")
                .managerIds(List.of("MGR-001"))
                .employeeIds(List.of("EMP-001"))
                .status("DRAFT")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .organizationId("ORG-001")
                .createdBy("USER-001")
                .build();
    }

    // ========== LIST ALL ==========
    @Test
    void list_ShouldReturnAllReviewCycles() throws Exception {
        List<ReviewCycle> cycles = Arrays.asList(sampleCycle);
        when(performanceService.getAllReviewCycles()).thenReturn(cycles);

        mockMvc.perform(get("/performance/v1/review-cycles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cycleId").value("CYCLE-001"))
                .andExpect(jsonPath("$[0].managerIds[0]").value("MGR-001"));
    }

    // ========== GET BY ID ==========
    @Test
    void getReviewCycleById_WhenExists_ShouldReturnCycle() throws Exception {
        when(performanceService.getReviewCycleById("CYCLE-001")).thenReturn(Optional.of(sampleCycle));

        mockMvc.perform(get("/performance/v1/review-cycles/CYCLE-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cycleId").value("CYCLE-001"))
                .andExpect(jsonPath("$.managerIds[0]").value("MGR-001"));
    }

    @Test
    void createReviewCycle_WhenInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/performance/v1/review-cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalidField\":\"oops\"}"))
                .andExpect(status().isBadRequest());
    }
//
//    @Test
//    void updateReviewCycleById_WhenNotFound_ShouldReturn404() throws Exception {
//        when(performanceService.updateReviewCycle(eq("INVALID"), any()))
//                .thenReturn(null);
//
//        mockMvc.perform(put("/performance/v1/review-cycles/INVALID")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sampleCycle)))
//                .andExpect(status().isNotFound());
//    }

    @Test
    void getAllReviewCyclesbyManager_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(performanceService.getReviewCyclesByManager("MGR-999")).thenReturn(List.of());

        mockMvc.perform(get("/performance/v1/review-cycles/manager/MGR-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getReviewCycleById_WhenNotExists_ShouldReturn404() throws Exception {
        when(performanceService.getReviewCycleById("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/performance/v1/review-cycles/INVALID"))
                .andExpect(status().isNotFound());
    }

    // ========== CREATE ==========
    @Test
    void createReviewCycle_ShouldReturnCreatedCycle() throws Exception {
        when(performanceService.createReviewCycle(any(ReviewCycle.class))).thenReturn(sampleCycle);

        mockMvc.perform(post("/performance/v1/review-cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCycle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cycleId").value("CYCLE-001"));
               // .andExpect(jsonPath("$.managerIds[0]").value("MGR-001"));
    }

    // ========== UPDATE ==========
    @Test
    void updateReviewCycleById_ShouldReturnUpdatedCycle() throws Exception {
        ReviewCycle updatedCycle = sampleCycle;
        updatedCycle.setName("Updated Name");

        Mockito.when(performanceService.updateReviewCycle(Mockito.eq("CYCLE-001"), Mockito.any()))
                .thenReturn(updatedCycle);

        mockMvc.perform(put("/performance/v1/review-cycles/CYCLE-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCycle)))
                .andExpect(status().isOk());
               // .andExpect(jsonPath("$.name").value("Updated Name"));
    }
    // ========== GET BY MANAGER ==========
    @Test
    void getAllReviewCyclesbyManager_ShouldReturnCycles() throws Exception {
        List<ReviewCycle> cycles = Arrays.asList(sampleCycle);
        when(performanceService.getReviewCyclesByManager("MGR-001")).thenReturn(cycles);

        mockMvc.perform(get("/performance/v1/review-cycles/manager/MGR-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cycleId").value("CYCLE-001"))
                .andExpect(jsonPath("$[0].managerIds[0]").value("MGR-001"));
    }
}
