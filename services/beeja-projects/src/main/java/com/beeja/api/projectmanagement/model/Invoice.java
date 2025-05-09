package com.beeja.api.projectmanagement.model;

import com.beeja.api.projectmanagement.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    private String id;
    private String invoiceId;
    private String contractId;
    private String projectId;
    private String clientId;
    private String organizationId;

    private Double amount;
    private String currency;
    private Date billingDate;
    private Date dueDate;
    private InvoiceStatus status;
    private List<String> notes;
    private String invoiceFileId;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
