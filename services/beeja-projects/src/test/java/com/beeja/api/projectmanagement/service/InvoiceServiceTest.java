package com.beeja.api.projectmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.beeja.api.projectmanagement.enums.InvoiceStatus;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InvoiceServiceTest {


    @Mock
    private InvoiceService invoiceService; // Mock the interface

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateInvoiceForContract() {
        InvoiceRequest request = new InvoiceRequest();
        request.setAmount(1000.0);

        Invoice invoice = Invoice.builder()
                .invoiceId("INV-123")
                .contractId("c1")
                .status(InvoiceStatus.PENDING)
                .build();

        when(invoiceService.generateInvoiceForContract("c1", request)).thenReturn(invoice);

        Invoice result = invoiceService.generateInvoiceForContract("c1", request);

        assertNotNull(result);
        assertEquals("INV-123", result.getInvoiceId());
        assertEquals(InvoiceStatus.PENDING, result.getStatus());
    }

    @Test
    void testGetInvoiceById() {
        Invoice invoice = Invoice.builder()
                .invoiceId("INV-123")
                .status(InvoiceStatus.PENDING)
                .build();

        when(invoiceService.getInvoiceById("INV-123")).thenReturn(invoice);

        Invoice result = invoiceService.getInvoiceById("INV-123");

        assertNotNull(result);
        assertEquals("INV-123", result.getInvoiceId());
    }

    @Test
    void testGetInvoicesByContractId() {
        Invoice invoice = Invoice.builder()
                .invoiceId("INV-123")
                .contractId("c1")
                .status(InvoiceStatus.PENDING)
                .build();

        when(invoiceService.getInvoicesByContractId("c1")).thenReturn(List.of(invoice));

        List<Invoice> result = invoiceService.getInvoicesByContractId("c1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("INV-123", result.get(0).getInvoiceId());
    }

    @Test
    void testMarkInvoiceAsPaid() {
        Invoice invoice = Invoice.builder()
                .invoiceId("INV-123")
                .status(InvoiceStatus.PENDING)
                .build();

        Invoice paidInvoice = Invoice.builder()
                .invoiceId("INV-123")
                .status(InvoiceStatus.PAID)
                .build();

        when(invoiceService.markInvoiceAsPaid("INV-123")).thenReturn(paidInvoice);

        Invoice result = invoiceService.markInvoiceAsPaid("INV-123");

        assertNotNull(result);
        assertEquals(InvoiceStatus.PAID, result.getStatus());
    }
}
