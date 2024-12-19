package com.beeja.api.apigateway.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserEntity {
  @Id private String id;
  private String email;
  private boolean isActive;
  private String password;
}
