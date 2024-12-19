package com.beeja.api.filemanagement.utils.helpers;

import java.text.DecimalFormat;

public class SizeConverter {
  public static String formatFileSize(long fileSize) {
    String[] units = {"B", "KB", "MB", "GB", "TB"};
    int unitIndex = 0;
    double size = fileSize;

    while (size > 1024 && unitIndex < units.length - 1) {
      size /= 1024.0;
      unitIndex++;
    }

    return new DecimalFormat("#,##0.#").format(size) + " " + units[unitIndex];
  }
}
