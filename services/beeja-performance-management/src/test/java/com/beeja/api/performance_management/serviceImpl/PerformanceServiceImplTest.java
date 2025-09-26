package com.beeja.api.performance_management.serviceImpl;

import com.beeja.api.performance_management.model.ReviewCycle;
import com.beeja.api.performance_management.repository.ReviewCycleRepository;
import com.beeja.api.performance_management.service.PerformanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceImplTest {

    @Mock
    private ReviewCycleRepository reviewCycleRepository;

    @InjectMocks
    private PerformanceServiceImpl performanceService;

    private ReviewCycle sampleCycle;

    @BeforeEach
    void setup() {
        sampleCycle = new ReviewCycle();
        sampleCycle.setId("1");
        sampleCycle.setCycleId("CYCLE-001");
        sampleCycle.setName("Q1 Review");
        sampleCycle.setDescription("Quarterly performance review");
        sampleCycle.setStartDate(LocalDate.now().atStartOfDay());
        sampleCycle.setEndDate(LocalDate.now().plusMonths(1).atStartOfDay());
        sampleCycle.setStatus("ACTIVE");
    }

    @Test
    void getAllReviewCycles_ShouldReturnList() {
        List<ReviewCycle> cycles = Arrays.asList(sampleCycle);
        when(reviewCycleRepository.findAll()).thenReturn(cycles);

        List<ReviewCycle> result = performanceService.getAllReviewCycles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCycleId()).isEqualTo("CYCLE-001");
        verify(reviewCycleRepository, times(1)).findAll();
    }

    @Test
    void getReviewCycleById_WhenExists_ShouldReturnOptional() {
        when(reviewCycleRepository.findByCycleId("CYCLE-001")).thenReturn(Optional.of(sampleCycle));

        Optional<ReviewCycle> result =performanceService.getReviewCycleById("CYCLE-001");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Q1 Review");
        verify(reviewCycleRepository, times(1)).findByCycleId("CYCLE-001");
    }

    @Test
    void getReviewCycleById_WhenNotExists_ShouldReturnEmpty() {
        when(reviewCycleRepository.findByCycleId("INVALID")).thenReturn(Optional.empty());

        Optional<ReviewCycle> result = performanceService.getReviewCycleById("INVALID");

        assertThat(result).isEmpty(); 
        verify(reviewCycleRepository, times(1)).findByCycleId("INVALID");
    }

    @Test
    void createReviewCycle_ShouldSaveAndReturnEntity() {
        when(reviewCycleRepository.save(any(ReviewCycle.class))).thenReturn(sampleCycle);

        ReviewCycle result = performanceService.createReviewCycle(sampleCycle);

        assertThat(result.getCycleId()).isEqualTo("CYCLE-001");
        verify(reviewCycleRepository, times(1)).save(sampleCycle);
    }

    @Test
    void updateReviewCycle_WhenExists_ShouldUpdateAndReturn() {
        ReviewCycle updated = new ReviewCycle();
        updated.setName("Updated Name");
        updated.setDescription("Updated Description");
        updated.setStartDate(LocalDate.now().atStartOfDay());
        updated.setEndDate(LocalDate.now().plusMonths(2).atStartOfDay());
        updated.setStatus("CLOSED");

        when(reviewCycleRepository.findByCycleId("CYCLE-001")).thenReturn(Optional.of(sampleCycle));
        when(reviewCycleRepository.save(any(ReviewCycle.class))).thenReturn(sampleCycle);

        ReviewCycle result = performanceService.updateReviewCycle("CYCLE-001", updated);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getStatus()).isEqualTo("CLOSED");
        verify(reviewCycleRepository, times(1)).findByCycleId("CYCLE-001");
        verify(reviewCycleRepository, times(1)).save(any(ReviewCycle.class));
    }

    @Test
    void updateReviewCycle_WhenNotExists_ShouldThrowException() {
        when(reviewCycleRepository.findByCycleId("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                performanceService.updateReviewCycle("INVALID", sampleCycle));

        verify(reviewCycleRepository, times(1)).findByCycleId("INVALID");
        verify(reviewCycleRepository, never()).save(any());
    }

    @Test
    void getReviewCyclesByManager_ShouldReturnList() {
        List<ReviewCycle> cycles = Arrays.asList(sampleCycle);
        when(reviewCycleRepository.findByManagerIdsContaining("manager-123"))
                .thenReturn(cycles);

        List<ReviewCycle> result = performanceService.getReviewCyclesByManager("manager-123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCycleId()).isEqualTo("CYCLE-001");
        verify(reviewCycleRepository, times(1))
                .findByManagerIdsContaining("manager-123");
    }

    @Test
    void getCurrentManagerId_ShouldReturnDefaultValue() {
        class TestService extends PerformanceService {
            String exposeCurrentManagerId() {
                return getCurrentManagerId();
            }
        }

        TestService testService = new TestService();
        String managerId = testService.exposeCurrentManagerId();

        assertThat(managerId).isEqualTo("current-manager-id");
    }

}
