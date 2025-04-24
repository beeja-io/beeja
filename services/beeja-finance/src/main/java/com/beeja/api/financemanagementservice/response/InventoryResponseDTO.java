package com.beeja.api.financemanagementservice.response;

import com.beeja.api.financemanagementservice.modals.Inventory;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
  private HashMap<String, Object> metadata;
  private List<Inventory> inventory;
}
