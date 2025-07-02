package com.beeja.api.projectmanagement.repository;

import com.beeja.api.projectmanagement.model.Invoice;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
  List<Invoice> findByContractId(String contractId);

  Invoice findByInvoiceIdAndOrganizationId(String invoiceId, String organizationId);
}
