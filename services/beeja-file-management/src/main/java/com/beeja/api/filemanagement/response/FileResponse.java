package com.beeja.api.filemanagement.response;

import com.beeja.api.filemanagement.model.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
  private Map<String, Object> metadata;
  private List<File> files;
}
