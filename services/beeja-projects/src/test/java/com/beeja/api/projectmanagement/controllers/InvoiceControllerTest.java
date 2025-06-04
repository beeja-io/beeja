package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.InvoicePeriod;
import com.beeja.api.projectmanagement.model.Task;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import com.beeja.api.projectmanagement.service.InvoiceService;
import com.beeja.api.projectmanagement.utils.UserContext;
import com.beeja.api.projectmanagement.responses.ErrorResponse;
import com.beeja.api.projectmanagement.enums.ErrorCode;
import com.beeja.api.projectmanagement.enums.ErrorType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private InvoiceRequest invoiceRequest;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        UserContext.setLoggedInUser("test@example.com", "Test User", "EMP123",
                Map.of("id", "org123", "name", "Test Org"),
                Set.of(PermissionConstants.CREATE_INVOICE, PermissionConstants.GET_INVOICE, PermissionConstants.UPDATE_STATUS_INVOICE),
                "test-token");

        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setContractId("contract123");
        invoiceRequest.setBillingDate(new Date());
        invoiceRequest.setDueDate(new Date());
        invoiceRequest.setAmount(100.0);
        invoiceRequest.setCurrency("USD");
        invoiceRequest.setNotes(List.of("Test note"));
        invoiceRequest.setTasks(List.of(new Task()));
        invoiceRequest.setClientId("client123");
        invoiceRequest.setProjectId("project123");
        invoiceRequest.setRemittanceRef("REF123");
        invoiceRequest.setVat(20);
        invoiceRequest.setDaysLeftForPayment("30");
        invoiceRequest.setInvoicePeriod(new InvoicePeriod());

        invoice = Invoice.builder()
                .id("invoiceId123")
                .invoiceId("INV001")
                .contractId("contract123")
                .amount(100.0)
                .currency("USD")
                .build();
    }

    @Test
    void createInvoice_Success() {
        when(invoiceService.generateInvoiceForContract(anyString(), any(InvoiceRequest.class))).thenReturn(invoice);

        ResponseEntity<Invoice> response = invoiceController.createInvoice(invoiceRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("invoiceId123", response.getBody().getId());
        verify(invoiceService, times(1)).generateInvoiceForContract(invoiceRequest.getContractId(), invoiceRequest);
    }

    @Test
    void createInvoice_InternalServerError() {
        when(invoiceService.generateInvoiceForContract(anyString(), any(InvoiceRequest.class)))
                .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<Invoice> response = invoiceController.createInvoice(invoiceRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(invoiceService, times(1)).generateInvoiceForContract(invoiceRequest.getContractId(), invoiceRequest);
    }

    @Test
    void getInvoiceById_Success() {
        when(invoiceService.getInvoiceById(anyString())).thenReturn(invoice);

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById("invoiceId123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("invoiceId123", response.getBody().getId());
        verify(invoiceService, times(1)).getInvoiceById("invoiceId123");
    }

    @Test
    void getInvoiceById_NotFound() {
        when(invoiceService.getInvoiceById(anyString()))
                .thenThrow(new ResourceNotFoundException(
                        new ErrorResponse(ErrorType.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, "Invoice not found", "/v1/invoices/nonExistentInvoiceId")
                ));

        ResponseEntity<Invoice> response = invoiceController.getInvoiceById("nonExistentInvoiceId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(invoiceService, times(1)).getInvoiceById("nonExistentInvoiceId");
    }

    @Test
    void getInvoicesByContractId_Success_WithInvoices() {
        List<Invoice> invoices = List.of(invoice, Invoice.builder().id("invoiceId456").build());
        when(invoiceService.getInvoicesByContractId(anyString())).thenReturn(invoices);

        ResponseEntity<List<Invoice>> response = invoiceController.getInvoicesByContractId("contract123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(invoiceService, times(1)).getInvoicesByContractId("contract123");
    }

    @Test
    void getInvoicesByContractId_Success_NoInvoices() {
        when(invoiceService.getInvoicesByContractId(anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<List<Invoice>> response = invoiceController.getInvoicesByContractId("contract456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(invoiceService, times(1)).getInvoicesByContractId("contract456");
    }

    @Test
    void markInvoiceAsPaid_Success() {
        when(invoiceService.markInvoiceAsPaid(anyString())).thenReturn(invoice);

        ResponseEntity<Invoice> response = invoiceController.markInvoiceAsPaid("invoiceId123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("invoiceId123", response.getBody().getId());
        verify(invoiceService, times(1)).markInvoiceAsPaid("invoiceId123");
    }

    @Test
    void markInvoiceAsPaid_NotFound() {

        when(invoiceService.markInvoiceAsPaid(anyString()))
                .thenThrow(new ResourceNotFoundException(
                        new ErrorResponse(ErrorType.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, "Invoice not found to mark as paid", "/v1/invoices/nonExistentInvoiceId/mark-paid")
                ));

        ResponseEntity<Invoice> response = invoiceController.markInvoiceAsPaid("nonExistentInvoiceId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(invoiceService, times(1)).markInvoiceAsPaid("nonExistentInvoiceId");
    }
}