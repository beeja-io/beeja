package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
import com.beeja.api.projectmanagement.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvoiceControllerTest {

    @InjectMocks
    private InvoiceController invoiceController;

    @Mock
    private InvoiceService invoiceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateInvoiceIdentifiers_ValidContractId_ReturnsIdentifiers() {
        // Arrange
        String contractId = "contract123";
        InvoiceIdentifiersResponse mockResponse = new InvoiceIdentifiersResponse("INV-202506-01", "TACINV-202506-01");
        when(invoiceService.generateInvoiceIdentifiers(contractId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<InvoiceIdentifiersResponse> response = invoiceController.generateInvoiceIdentifiers(contractId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INV-202506-01", response.getBody().getInvoiceId());
        assertEquals("TACINV-202506-01", response.getBody().getRemittanceReferenceNumber());
        verify(invoiceService, times(1)).generateInvoiceIdentifiers(contractId);
    }

    @Test
    void generateInvoiceIdentifiers_ServiceThrowsException_ReturnsInternalServerError() {
        // Arrange
        String contractId = "contract123";
        when(invoiceService.generateInvoiceIdentifiers(contractId)).thenThrow(new RuntimeException("Something went wrong"));

        // Act
        ResponseEntity<InvoiceIdentifiersResponse> response = invoiceController.generateInvoiceIdentifiers(contractId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(invoiceService, times(1)).generateInvoiceIdentifiers(contractId);
    }
}
