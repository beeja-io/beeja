package com.beeja.api.financemanagementservice.Utils.bulkpayslipsUtil;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.response.PDFResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class TextExtractor {
  public static List<PDFResponse> extractTextsFromPDFs(List<MultipartFile> pdfFiles) {
    List<PDFResponse> pdfResponses = new ArrayList<>();
    for (MultipartFile pdfFile : pdfFiles) {
      if (!pdfFile.isEmpty()) {
        PDDocument document = null;
        try {
          document = PDDocument.load(pdfFile.getInputStream());
          if (!document.isEncrypted()) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            String entityID = extractEmployeeId(text);
            pdfResponses.add(new PDFResponse(entityID, pdfFile));
          } else {
            throw new IOException(Constants.ENCRYPTED_PDF_FILES_NOT_SUPPORTED);
          }
        } catch (IOException e) {
          log.error(Constants.ERROR_OCCORRED_DURING_BULK_PAY_SLIPS_UPLOAD, e);
        } finally {
          if (document != null) {
            try {
              document.close();
            } catch (IOException e) {
              log.error(Constants.ERROR_CLOSING_PDF_DOCUMENT, e);
            }
          }
        }
      }
    }

    return pdfResponses;
  }

  private static String extractEmployeeId(String pdfText) {

    String employeeIdPattern = "Employee ID\\s*(?::\\s*)?([A-Z0-9]+)";
    Pattern employeeIdRegex = Pattern.compile(employeeIdPattern);
    Matcher employeeIdMatcher = employeeIdRegex.matcher(pdfText);

    String employeeId = "";
    if (employeeIdMatcher.find()) {
      employeeId = employeeIdMatcher.group(1);
    }
    return employeeId;
  }
}
