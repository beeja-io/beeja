package com.beeja.api.financemanagementservice.response;

import com.beeja.api.financemanagementservice.modals.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
  private HashMap<String, Object> metadata;
  private List<Inventory> inventory;
}
