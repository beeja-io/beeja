package com.beeja.api.projectmanagement.model;

import lombok.Data;

@Data
public class Task {
  String taskName;
  String description;
  Double price;
}
