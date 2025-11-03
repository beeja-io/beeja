package com.beeja.api.projectmanagement.utils;

import com.beeja.api.projectmanagement.model.Address;
import com.beeja.api.projectmanagement.model.Client;
import com.beeja.api.projectmanagement.model.Contract;
import com.beeja.api.projectmanagement.model.Invoice;
import com.beeja.api.projectmanagement.model.Task;
import com.beeja.api.projectmanagement.model.dto.orgAddress;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoicePDFGen {

    private void addLine(Document document, DeviceRgb color) {
        document.add(
                new Paragraph(Constants.LINE)
                        .setFontColor(color)
                        .setTextAlignment(TextAlignment.CENTER)
        );
    }

    private String getCurrencySymbol(String currencyCode) {
        return switch (currencyCode.toUpperCase()) {
            case "INR" -> "₹";
            case "DOLLER" -> "$";
            case "EURO" -> "€";
            default -> currencyCode;
        };
    }
  public byte[] generatePDF(Contract contract, Invoice invoice, Client client, orgAddress primaryAddress,
                            Address billingAddress
  ) {


    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PdfWriter writer = new PdfWriter(byteArrayOutputStream);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc, PageSize.A4);
    document.setMargins(30, 30, 30, 30);
      String currencyCode = contract.getBillingCurrency().name();
      String currencySymbol = getCurrencySymbol(currencyCode);
      String currencyName = currencyCode.toUpperCase();


      // Define your colors
    DeviceRgb blueColor = new DeviceRgb(0, 102, 204);
    DeviceRgb grayColor = new DeviceRgb(120, 120, 120);
    DeviceRgb blackColor = new DeviceRgb(0, 0, 0);
    DeviceRgb lightGray = new DeviceRgb(230, 230, 230);

    // FOR WHOLE PDF DATE FORMAT
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");

    // --- HEDEAR PART(TITLE AND ADDRESS)---

    Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

      String billingFromLabel = "Billing From : ";
      String billingFromName = UserContext.getLoggedInUserOrganization().get("name").toString();

      String billingFromAddress =
              (primaryAddress != null)
                      ? String.join(
                      "\n",
                      primaryAddress.getAddressOne(),
                      primaryAddress.getCity() + ", " + primaryAddress.getState() + ", " + primaryAddress.getPinCode(),
                      primaryAddress.getCountry()
              )
                      : "";

      Paragraph leftAddressPara = new Paragraph()
              .add(new Text(billingFromLabel).setFontSize(8).setBold().setFontColor(blueColor))
              .add(new Text(billingFromName + "\n").setFontSize(10).setBold())
              .add(new Text(billingFromAddress).setFontSize(8).setFontColor(grayColor));
      headerTable.addCell(new Cell().add(leftAddressPara).setBorder(Border.NO_BORDER));

      String billingToLabel = "Billing To : ";
      String billingToName = client.getClientName();
      String billingToAddress =
              (billingAddress != null)
                      ? String.join(
                      "\n",
                      billingAddress.getStreet(),
                      billingAddress.getCity() + ", " + billingAddress.getState() + ", " + billingAddress.getPostalCode(),
                      billingAddress.getCountry()
              )
                      : "";

      Paragraph rightAddressPara = new Paragraph()
              .add(new Text(billingToLabel).setFontSize(8).setBold().setFontColor(blueColor))
              .add(new Text(billingToName + "\n").setFontSize(10).setBold())
              .add(new Text(billingToAddress).setFontSize(8).setFontColor(grayColor))
              .setTextAlignment(TextAlignment.RIGHT);
      headerTable.addCell(new Cell().add(rightAddressPara).setBorder(Border.NO_BORDER));

      document.add(headerTable);
      addLine(document, grayColor);

      // --- ID'S PART---

    Table idSection =
        new Table(UnitValue.createPercentArray(new float[] {1, 1, 1})).useAllAvailableWidth();

    String remittanceRef = invoice.getRemittanceRef();
    String invoiceNo = invoice.getInvoiceId();
    String taxId = invoice.getTaxId();

    idSection.addCell(
        new Cell()
            .add(
                new Paragraph()
                    .add(
                        new Text("Remittance Ref ")
                            .setFontSize(10)
                            .setBold()
                            .setFontColor(grayColor))
                    .add(new Text(" #" + remittanceRef).setFontSize(10).setFontColor(blackColor)))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.LEFT));

    idSection.addCell(
        new Cell()
            .add(
                new Paragraph()
                    .add(new Text("Invoice No ").setFontSize(10).setBold().setFontColor(grayColor))
                    .add(new Text("#" + invoiceNo).setFontSize(10).setFontColor(blackColor)))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.CENTER));

    idSection.addCell(
        new Cell()
            .add(
                new Paragraph()
                    .add(new Text("TAX ID ").setFontSize(10).setBold().setFontColor(grayColor))
                    .add(new Text(taxId).setFontSize(10).setFontColor(blackColor)))
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT));

    document.add(idSection);
      addLine(document, grayColor);

    // --- INVOICE PART---

    Table footerTable =
        new Table(UnitValue.createPercentArray(new float[] {70, 30})).useAllAvailableWidth();

    String invoiceId = invoice.getInvoiceId();
    String contractId = contract.getContractId();
    String contractTitle = contract.getContractTitle();

    Date stDate = invoice.getInvoicePeriod().getStartDate();
    String startDate = formatter.format(stDate);
    Date dueDate = invoice.getInvoicePeriod().getEndDate();
    String endDate = formatter.format(dueDate);

    Date current = new Date();
    String contractCreatedAt = formatter.format(current);

    Paragraph invoiceDesc =
        new Paragraph()
            .add(new Text("Invoice : ").setFontSize(12).setBold())
            .add(new Text(invoiceId).setFontSize(12).setBold().setFontColor(blueColor)).add("\n")
            .add(new Text(contractId + " - " + contractTitle).setFontSize(10)).add("\n")
            .add(new Text("Invoice Period : ").setFontSize(9).setBold())
            .add(new Text(startDate + " To " + endDate).setFontSize(9).setFontColor(grayColor)).add("\n")
            .setMarginTop(5);

    Paragraph clientDetails =
        new Paragraph()
                .add(new Text(primaryAddress != null ? primaryAddress.getCity() + ", " : "").setFontSize(10).setBold())
            .add(new Text(contractCreatedAt).setFontSize(10).setBold())
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(10);

    footerTable.addCell(
        new Cell()
            .add(invoiceDesc)
            .setBorder(Border.NO_BORDER)
            .setVerticalAlignment(VerticalAlignment.MIDDLE));

    footerTable.addCell(
        new Cell()
            .add(clientDetails)
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT)
            .setVerticalAlignment(VerticalAlignment.BOTTOM));
    document.add(footerTable);
      addLine(document, grayColor);

    // ------TASK'S PART ----------

    List<Task> tasks = invoice.getTasks();

    Table itemsTable =
        new Table(UnitValue.createPercentArray(new float[] {5, 35, 40, 20}))
            .useAllAvailableWidth();

    itemsTable.addHeaderCell(
        new Cell()
            .add(new Paragraph("S.No.").setFontSize(10).setBold())
            .setFontColor(grayColor)
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.LEFT)
            .setVerticalAlignment(VerticalAlignment.MIDDLE));
    itemsTable.addHeaderCell(
        new Cell()
            .add(new Paragraph("Task").setFontSize(10).setBold())
            .setFontColor(grayColor)
            .setBorder(Border.NO_BORDER));
    itemsTable.addHeaderCell(
        new Cell()
            .add(new Paragraph("Description").setFontSize(10).setBold())
            .setFontColor(grayColor)
            .setBorder(Border.NO_BORDER));
    itemsTable.addHeaderCell(
        new Cell()
            .add(new Paragraph("Amount (" + currencySymbol + ")").setFontSize(10).setBold())
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontColor(grayColor)
            .setBorder(Border.NO_BORDER));

      itemsTable.addCell(
              new Cell(1, 4)
                      .add(new Paragraph(Constants.LINE))
                      .setBorder(Border.NO_BORDER)
                      .setTextAlignment(TextAlignment.CENTER)
      );

    int serialNo = 1;
    double totalAmount = 0.0;
    if (tasks != null) {
      for (Task task : tasks) {
        itemsTable.addCell(
            new Cell()
                .add(new Paragraph(String.valueOf(serialNo++)).setFontSize(9))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER));
        itemsTable.addCell(
            new Cell()
                .add(new Paragraph(task.getTaskName()).setFontSize(9))
                .setBorder(Border.NO_BORDER));
        itemsTable.addCell(
            new Cell()
                .add(new Paragraph(task.getDescription()).setFontSize(9))
                .setBorder(Border.NO_BORDER));
        itemsTable.addCell(
            new Cell()
                .add(new Paragraph(String.valueOf(task.getAmount())).setFontSize(9))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER));
        totalAmount += task.getAmount();
      }
    }

    document.add(itemsTable);

      addLine(document, grayColor);

    // ------CALCULATION PART---------

    double subTotal = totalAmount; // totalAmount is already calculated
    int vatPercentage = invoice.getVat();
    double vatValue = (vatPercentage / 100.0) * subTotal;
    double finalTotal = subTotal + vatValue;

    Table calculationTable =
        new Table(UnitValue.createPercentArray(new float[] {50, 50}))
            .setWidth(UnitValue.createPercentValue(25))
            .setHorizontalAlignment(HorizontalAlignment.RIGHT)
            .setMarginTop(10);

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("Subtotal").setFontSize(10))
            .setBold()
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("(" + currencySymbol + ")" + totalAmount).setFontSize(10))
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("TAX(%)").setFontSize(10))
            .setBold()
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("(" + currencySymbol + ")" + vatPercentage).setFontSize(10))
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("Total").setFontSize(10))
            .setBold()
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    calculationTable.addCell(
        new Cell()
            .add(new Paragraph("(" + currencySymbol + ")" + finalTotal).setFontSize(10))
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));

    document.add(calculationTable);

    // --- AMOUNT IN WORDS PART ---

    String amountInWordsDollars = AmountToWordsUtil.convertToWords(finalTotal) + " " + currencyName.toLowerCase() + " only /-";

    Paragraph amountInWordsPara =
        new Paragraph()
            .add(new Text("Amount in words: ").setFontSize(9).setFontColor(grayColor))
            .add(new Text(amountInWordsDollars).setFontSize(9).setBold().setFontColor(blackColor))
            .setTextAlignment(TextAlignment.RIGHT);
    document.add(amountInWordsPara);

    document.add(new Paragraph("\n"));

    // -----NOTE PART ------

    Paragraph notePara =
        new Paragraph()
            .add(new Text("NOTE: ").setBold().setFontSize(9))
                .add(new Text("\"" + (invoice.getNotes() != null && invoice.getNotes().size() > 1
                        ? invoice.getNotes().get(1)
                        : "") + "\"")
                        .setFontSize(9)
                        .setFontColor(grayColor));

    document.add(notePara);
      addLine(document, grayColor);
    // ----- PAYMENT SECTION -----

    String orgName = invoice.getPaymentDetails().getAccountName().toString();
    String bankName = invoice.getPaymentDetails().getBankName().toString();
    String accountNo = invoice.getPaymentDetails().getAccountNumber().toString();
    String isfc = invoice.getPaymentDetails().getIfscNumber().toString();

    Paragraph paymentHeader =
        new Paragraph("Payment Details")
            .setBold()
            .setFontSize(10)
            .setFontColor(blueColor)
            .setMarginTop(20);

    document.add(paymentHeader);

    Table paymentTable =
        new Table(UnitValue.createPercentArray(new float[] {30, 70}))
            .setWidth(UnitValue.createPercentValue(60))
            .setFontSize(9)
            .setMarginTop(5);

    paymentTable.addCell(
        new Cell().add(new Paragraph("Name")).setBorder(Border.NO_BORDER).setFontColor(grayColor));
    paymentTable.addCell(
        new Cell()
            .add(new Paragraph(orgName))
            .setBorder(Border.NO_BORDER)
            .setBold()
            .setFontColor(blackColor));

    paymentTable.addCell(
        new Cell()
            .add(new Paragraph("Bank Name"))
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));
    paymentTable.addCell(
        new Cell()
            .add(new Paragraph(bankName))
            .setBorder(Border.NO_BORDER)
            .setBold()
            .setFontColor(blackColor));

    paymentTable.addCell(
        new Cell()
            .add(new Paragraph("Account Number"))
            .setBorder(Border.NO_BORDER)
            .setFontColor(grayColor));
    paymentTable.addCell(
        new Cell()
            .add(new Paragraph(accountNo))
            .setBorder(Border.NO_BORDER)
            .setBold()
            .setFontColor(blackColor));

    paymentTable.addCell(
        new Cell().add(new Paragraph("isfc")).setBorder(Border.NO_BORDER).setFontColor(grayColor));
    paymentTable.addCell(
        new Cell()
            .add(new Paragraph(isfc))
            .setBorder(Border.NO_BORDER)
            .setBold()
            .setFontColor(blackColor));

    document.add(paymentTable);

      addLine(document, grayColor);

    // ------REMARKS SECTION----------

    Paragraph remarksPara =
        new Paragraph()
            .add(new Text("Remarks ").setBold().setFontSize(9))
                .add(new Text("\"" + (invoice.getNotes() != null && invoice.getNotes().size() > 1
                        ? invoice.getNotes().get(0)
                        : "") + "\"")
                        .setFontSize(9)
                        .setFontColor(grayColor))
            .setTextAlignment(TextAlignment.LEFT)
            .setMarginBottom(15);

    document.add(remarksPara);

    // ----WISHING SECTION-----

    Paragraph wishingPara =
        new Paragraph()
            .add(new Text("Best Regards,\n\n").setFontColor(grayColor).setBold())
            .add(new Text(orgName))
            .setFontSize(9);

    document.add(wishingPara);

    document.close();
    return byteArrayOutputStream.toByteArray();
  }
}
