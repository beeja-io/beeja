package com.beeja.api.financemanagementservice.modals;

import com.beeja.api.financemanagementservice.Utils.Constants;
import com.beeja.api.financemanagementservice.Utils.UserContext;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "files")
public class File {
  @Id private String id;
  private String name;
  private String fileType;
  private String fileFormat;
  private String fileSize;
  private String entityId;

  @Pattern(
      regexp = "^(employee|project|organization|client)$",
      message = Constants.INVALID_ENTITY_TYPE)
  private String entityType;

  private String description;

  private String organizationId = UserContext.getLoggedInUserOrganization().get("id").toString();

  private String createdBy = UserContext.getLoggedInUserEmail();
  private String createdByName = UserContext.getLoggedInUserName();
  private String modifiedBy;

  @Field("created_at")
  @CreatedDate
  private Date createdAt;

  @Field("modified_at")
  @LastModifiedDate
  private Date modifiedAt;
}
