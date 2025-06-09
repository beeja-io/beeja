package com.beeja.api.projectmanagement.utils;

import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Invoice;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

@Component
public class PdfGenerationUtil {

  public byte[] generateInvoicePdf(Invoice invoice, Contract contract) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    PdfWriter writer = new PdfWriter(byteArrayOutputStream);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    document.add(new Paragraph("Invoice PDF"));
    document.add(new Paragraph("================================"));
    document.add(new Paragraph("Invoice ID: " + invoice.getInvoiceId()));
    document.add(new Paragraph("Contract ID: " + contract.getContractId()));
    document.add(new Paragraph("Client: " + contract.getClientId()));
    document.add(new Paragraph("Project: " + contract.getProjectId()));
    document.add(new Paragraph("Amount: ₹" + invoice.getAmount()));
    document.add(new Paragraph("Due Date: " + invoice.getDueDate()));
    document.add(new Paragraph("Status: " + invoice.getStatus()));
    document.add(new Paragraph("Created At: " + invoice.getCreatedAt()));
    document.add(new Paragraph("================================"));

    document.close();

    return byteArrayOutputStream.toByteArray();
  }
}
