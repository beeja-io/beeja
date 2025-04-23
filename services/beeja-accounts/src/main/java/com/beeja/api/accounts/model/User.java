package com.beeja.api.accounts.model;

import com.beeja.api.accounts.model.Organization.Organization;
import com.beeja.api.accounts.model.Organization.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
  @Id private String id;

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @NotBlank(message = "Email is mandatory")
  @Email(message = "Invalid email format")
  @Indexed(unique = true)
  private String email;

  @DBRef private Set<Role> roles = new HashSet<>();

  @Indexed(unique = true)
  @NotBlank(message = "EmployeeID is mandatory")
  private String employeeId;

  private String employmentType;

  @DBRef private Organization organizations;

  private UserPreferences userPreferences;

  @JsonIgnore private String password;

  private boolean isActive = true;

  private String createdBy;
  private String modifiedBy;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;
}
