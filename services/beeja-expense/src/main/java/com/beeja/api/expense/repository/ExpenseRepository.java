package com.beeja.api.expense.repository;

import com.beeja.api.expense.modal.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

  Optional<Expense> findByOrganizationIdAndId(String organizationId, String expenseId);

  Long countByOrganizationId(String organizationId);
}
