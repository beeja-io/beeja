package com.beeja.api.financemanagementservice.serviceImpl;

import static org.mockito.Mockito.*;

import com.beeja.api.financemanagementservice.client.FileClient;
import com.beeja.api.financemanagementservice.requests.BulkPayslipRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class BulkPayslipUploadTest {

  @InjectMocks private LoanServiceImpl loanService;

  @Mock private FileClient fileClient;

  @Mock private MultipartFile zipFile;

  private final String authorizationHeader = "Bearer test-token";

  @Test
  void testValidPdf_uploadSuccess() throws Exception {
    // 1. Create dummy PDF with employee ID
    byte[] pdfBytes = createDummyPdf("Employee ID: EMP12345");

    // 2. Zip the dummy PDF
    byte[] zipBytes = createZipBytes(Map.of("payslip1.pdf", pdfBytes));
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

    // 3. Prepare request
    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    // 4. Mock uploadFile response
    when(fileClient.uploadFile(any(), eq(authorizationHeader)))
        .thenReturn(ResponseEntity.ok().build());

    // 5. Call the method
    loanService.uploadBulkPaySlips(request, authorizationHeader);

    // 6. Verify fileClient was used once
    verify(fileClient, times(1)).uploadFile(any(), eq(authorizationHeader));
  }

  // Helper to create a simple PDF with a line of text
  private byte[] createDummyPdf(String text) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PDDocument document = new PDDocument()) {

      PDPage page = new PDPage();
      document.addPage(page);

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(text);
        contentStream.endText();
      }

      document.save(outputStream);
      return outputStream.toByteArray();
    }
  }

  @Test
  void testMissingFileName_skipped() throws Exception {
    byte[] zipBytes = createZipBytes(Map.of("", createDummyPdf("Employee ID: EMP12345")));
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);
    // No exception is thrown, log should warn about missing filename
  }

  @Test
  void testUnsupportedFileType_skipped() throws Exception {
    byte[] fakeTxt = "Just text".getBytes();
    byte[] zipBytes = createZipBytes(Map.of("notes.txt", fakeTxt));
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);
    // Should skip .txt file as unsupported
  }

  @Test
  void testMissingEmployeeId_skipped() throws Exception {
    byte[] pdfBytes = createDummyPdf("This PDF has no employee ID.");
    byte[] zipBytes = createZipBytes(Map.of("payslip.pdf", pdfBytes));
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);
    // Should log "Employee ID not found"
  }

  @Test
  void testFileUploadFailure_logged() throws Exception {
    byte[] pdfBytes = createDummyPdf("Employee ID: EMP99999");
    byte[] zipBytes = createZipBytes(Map.of("failme.pdf", pdfBytes));
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

    doThrow(new RuntimeException("Simulated upload failure"))
        .when(fileClient)
        .uploadFile(any(), eq(authorizationHeader));

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);
    // Logs should show the failure
  }

  @Test
  void testInvalidZipFile_logsFailure() throws Exception {
    // Send corrupted zip content
    byte[] invalidZip = "not a real zip".getBytes();
    when(zipFile.getInputStream()).thenReturn(new ByteArrayInputStream(invalidZip));

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(zipFile);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);
    // Should log "ZIP file could not be processed"
  }

  @Test
  void testCorruptedZipFile_triggersIOException() throws Exception {
    byte[] invalidZip = "not-a-valid-zip".getBytes();

    // Use a real MockMultipartFile here (no unnecessary stub)
    MockMultipartFile brokenZip =
        new MockMultipartFile("zipFile", "broken.zip", "application/zip", invalidZip);

    BulkPayslipRequest request = new BulkPayslipRequest();
    request.setZipFile(brokenZip);
    request.setMonth("April");
    request.setYear("2025");

    loanService.uploadBulkPaySlips(request, authorizationHeader);

    // No upload should happen
    verify(fileClient, never()).uploadFile(any(), any());
  }

  // Helper to zip a single or multiple files
  private byte[] createZipBytes(Map<String, byte[]> files) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
      for (Map.Entry<String, byte[]> entry : files.entrySet()) {
        ZipEntry zipEntry = new ZipEntry(entry.getKey());
        zos.putNextEntry(zipEntry);
        zos.write(entry.getValue());
        zos.closeEntry();
      }
    }
    return baos.toByteArray();
  }
}
