package com.beeja.api.projectmanagement.service;

import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.dto.InvoiceIdentifiersResponse;
import com.beeja.api.projectmanagement.request.InvoiceRequest;
import java.util.List;

public interface InvoiceService {

    Invoice generateInvoiceForContract(String contractId, InvoiceRequest invoiceRequest);
    Invoice getInvoiceById(String invoiceId);
    List<Invoice> getInvoicesByContractId(String contractId);
    Invoice markInvoiceAsPaid(String invoiceId);
    InvoiceIdentifiersResponse generateInvoiceIdentifiers(String contractId);

}
