package com.beeja.api.employeemanagement.response;

import com.beeja.api.employeemanagement.model.File;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
  private Map<String, Object> metadata;
  private List<File> files;
}
