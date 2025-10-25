package com.beeja.api.projectmanagement.controllers;

import com.beeja.api.projectmanagement.annotations.HasPermission;
import com.beeja.api.projectmanagement.constants.PermissionConstants;
import com.beeja.api.projectmanagement.exceptions.ResourceNotFoundException;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import com.beeja.api.projectmanagement.service.InvoiceService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/invoices")
@Slf4j
public class InvoiceController {

  @Autowired private InvoiceService invoiceService;

  @PostMapping
  @HasPermission(PermissionConstants.CREATE_INVOICE)
  public ResponseEntity<Invoice> createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
    try {
      Invoice invoice =
          invoiceService.generateInvoiceForContract(invoiceRequest.getContractId(), invoiceRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    } catch (Exception e) {
      log.error("Failed to create invoice: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{invoiceId}")
  @HasPermission(PermissionConstants.GET_INVOICE)
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
  @HasPermission({PermissionConstants.GET_INVOICE, PermissionConstants.READ_DOCUMENT})
  public ResponseEntity<List<Invoice>> getInvoicesByContractId(@PathVariable String contractId) {
    List<Invoice> invoices = invoiceService.getInvoicesByContractId(contractId);
    return ResponseEntity.ok(invoices);
  }

  @PutMapping("/{invoiceId}/mark-paid")
  @HasPermission(PermissionConstants.UPDATE_STATUS_INVOICE)
  public ResponseEntity<Invoice> markInvoiceAsPaid(@PathVariable String invoiceId) {
    try {
      Invoice updatedInvoice = invoiceService.markInvoiceAsPaid(invoiceId);
      return ResponseEntity.ok(updatedInvoice);
    } catch (ResourceNotFoundException e) {
      log.warn("Invoice not found to mark as paid: {}", invoiceId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
    @PostMapping("/generate-identifiers/{contractId}")
    @HasPermission(PermissionConstants.CREATE_INVOICE)
    public ResponseEntity<InvoiceIdentifiersResponse> generateInvoiceIdentifiers(@PathVariable String contractId) {
        try {
            InvoiceIdentifiersResponse response = invoiceService.generateInvoiceIdentifiers(contractId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
            }
        }

  }


