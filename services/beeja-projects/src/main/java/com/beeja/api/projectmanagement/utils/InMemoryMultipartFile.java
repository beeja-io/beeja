package com.beeja.api.projectmanagement.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;

public class InMemoryMultipartFile implements MultipartFile {

  private final byte[] content;
  private final String fileName;

  public InMemoryMultipartFile(String fileName, byte[] content) {
    this.fileName = fileName;
    this.content = content;
  }

  @Override
  public String getName() {
    return fileName;
  }

  @Override
  public String getOriginalFilename() {
    return fileName;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() {
    return content;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(File dest) throws IOException {
    try (OutputStream out = new FileOutputStream(dest)) {
      out.write(content);
    }
  }
}
