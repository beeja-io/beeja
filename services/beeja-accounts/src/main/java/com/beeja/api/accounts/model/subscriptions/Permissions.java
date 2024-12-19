package com.beeja.api.accounts.model.subscriptions;

import com.beeja.api.accounts.enums.SubscriptionName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "permissions")
public class Permissions {
  @Id private String id;
  private SubscriptionName name;
  private Set<String> permissions;
}
