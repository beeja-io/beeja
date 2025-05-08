package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import com.beeja.api.projectmanagement.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/invoices")
@Slf4j
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        try {
            Invoice invoice = invoiceService.generateInvoiceForContract(invoiceRequest.getContractId(), invoiceRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            return ResponseEntity.ok(invoice);
        } catch (ResourceNotFoundException e) {
            log.warn("Invoice not found: {}", invoiceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<Invoice>> getInvoicesByContractId(@PathVariable String contractId) {
        List<Invoice> invoices = invoiceService.getInvoicesByContractId(contractId);
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{invoiceId}/mark-paid")
    public ResponseEntity<Invoice> markInvoiceAsPaid(@PathVariable String invoiceId) {
        try {
            Invoice updatedInvoice = invoiceService.markInvoiceAsPaid(invoiceId);
            return ResponseEntity.ok(updatedInvoice);
        } catch (ResourceNotFoundException e) {
            log.warn("Invoice not found to mark as paid: {}", invoiceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
