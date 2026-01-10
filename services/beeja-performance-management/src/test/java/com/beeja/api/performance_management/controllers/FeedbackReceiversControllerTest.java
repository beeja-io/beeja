package com.beeja.api.performance_management.controllers;

import com.beeja.api.performance_management.model.dto.ReceiverDetails;
import com.beeja.api.performance_management.request.ReceiverRequest;
import com.beeja.api.performance_management.response.ReceiverResponse;
import com.beeja.api.performance_management.service.FeedbackReceiversService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FeedbackReceiversControllerTest {

    @InjectMocks
    private FeedbackReceiversController controller;

    @Mock
    private FeedbackReceiversService service;

    private ReceiverRequest request;
    private ReceiverDetails receiverDetails;
    private ReceiverResponse receiverResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        receiverDetails = ReceiverDetails.builder()
                .employeeId("EMP1")
                .fullName("John Doe")
                .department("IT")
                .email("john.doe@example.com")
                .providerStatus(null)
                .build();

        request = new ReceiverRequest();
        request.setCycleId("CYCLE1");
        request.setQuestionnaireId("Q1");
        request.setReceiverDetails(List.of(receiverDetails));

        receiverResponse = ReceiverResponse.builder()
                .cycleId("CYCLE1")
                .questionnaireId("Q1")
                .receivers(List.of(receiverDetails))
                .build();
    }

    @Test
    void testAddFeedbackReceivers_Success() {
        when(service.addFeedbackReceivers(request)).thenReturn(List.of());

        ResponseEntity<?> response = controller.addFeedbackReceivers(request);

        assertEquals(201, response.getStatusCodeValue());
        verify(service, times(1)).addFeedbackReceivers(request);
    }

    @Test
    void testUpdateReceivers_Success() {
        when(service.updateFeedbackReceivers("CYCLE1", request)).thenReturn(List.of());

        ResponseEntity<?> response = controller.updateReceivers("CYCLE1", request);

        assertEquals(200, response.getStatusCodeValue());
        verify(service, times(1)).updateFeedbackReceivers("CYCLE1", request);
    }

    @Test
    void testGetFeedbackReceivers_Success() {
        when(service.getFeedbackReceiversList("CYCLE1", "Q1")).thenReturn(receiverResponse);

        ResponseEntity<?> response = controller.getFeedbackReceivers("CYCLE1", "Q1");

        assertEquals(200, ((ResponseEntity<?>) response).getStatusCodeValue());
        ReceiverResponse body = (ReceiverResponse) ((ResponseEntity<?>) response).getBody();
        assertEquals("CYCLE1", body.getCycleId());
        assertEquals(1, body.getReceivers().size());
        assertEquals("EMP1", body.getReceivers().get(0).getEmployeeId());

        verify(service, times(1)).getFeedbackReceiversList("CYCLE1", "Q1");
    }
}
