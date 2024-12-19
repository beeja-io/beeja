package com.beeja.api.financemanagementservice.repository;

import com.beeja.api.financemanagementservice.modals.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
  Optional<Inventory> findByIdAndOrganizationId(String id, String organisationId);

  List<Inventory> findByOrganizationId(String organizationId);

  Optional<Inventory> findByProductId(String productId);
}
