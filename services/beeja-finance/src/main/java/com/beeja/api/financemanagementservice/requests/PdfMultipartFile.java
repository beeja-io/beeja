package com.beeja.api.financemanagementservice.requests;

import com.beeja.api.financemanagementservice.Utils.Constants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class PdfMultipartFile implements MultipartFile {

  private final String name;
  private final String originalFileName;
  private final byte[] pdfData;

  public PdfMultipartFile(String name, String originalFileName, byte[] pdfData) {
    this.name = name;
    this.originalFileName = originalFileName;
    this.pdfData = pdfData;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFileName;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }

  @Override
  public boolean isEmpty() {
    return pdfData.length == 0;
  }

  @Override
  public long getSize() {
    return pdfData.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return pdfData;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(pdfData);
  }

  @Override
  public void transferTo(File file) throws IOException, IllegalStateException {
    throw new UnsupportedOperationException(Constants.THIS_METHOD_NOT_SUPPORTED);
  }
}
