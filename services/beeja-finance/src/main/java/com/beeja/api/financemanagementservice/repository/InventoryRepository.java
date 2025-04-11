package com.beeja.api.financemanagementservice.repository;

import com.beeja.api.financemanagementservice.modals.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
  Optional<Inventory> findByIdAndOrganizationId(String id, String organisationId);

  List<Inventory> findByOrganizationId(String organizationId);

  Optional<Inventory> findByProductId(String productId);
}
